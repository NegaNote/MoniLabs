#version 330 core

in vec3 rayOrigin;
in vec3 rayDirection;

uniform vec3 SpherePos;
uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform sampler2D WorldColor;
uniform int uWriteOnlyDepth;
out vec4 fragColor;

bool hitSphere(vec3 ro, vec3 rd, float radius, out float t, out bool inside) {
    t = -1.0;
    inside = false;
    vec3 oc = ro - SpherePos;
    float b = dot(oc, rd);
    float c = dot(oc, oc) - radius * radius;
    float h = b * b - c;

    if (c < 0.0)
    {
        inside = true;
    }
    if (h < 0.0) return false;

    float sqrtH = sqrt(h);
    float t0 = -b - sqrtH;
    float t1 = -b + sqrtH;

    t = (t0 > 0.0) ? t0 : t1;

    return t > 0.0;
}

float writeDepth(float t, vec3 ro, vec3 rd) {
    vec3 hitPoint = ro + rd * t;
    vec4 clipPos = ProjMat * vec4(hitPoint, 1.0);
    float ndcDepth = clipPos.z / clipPos.w;
    float writtenDepth = (ndcDepth + 1.0) * 0.5;
    if (writtenDepth <= 0.01)
    {
        gl_FragDepth = gl_FragCoord.z;
        return writtenDepth;
    }
    gl_FragDepth = writtenDepth;
    return writtenDepth;
}

vec2 getViewSpaceUV(vec3 posView, mat4 projectionMatrix) {
    vec4 clipSpace = projectionMatrix * vec4(posView, 1.0);

    if (clipSpace.w <= 0.0) {
        return normalize(posView.xy) * 100.0;
    }

    vec3 ndc = clipSpace.xyz / clipSpace.w;
    return ndc.xy * 0.5 + 0.5;
}

uniform float uMass;
uniform float uDistModifier;
uniform float uSchwarzschildRadius;
uniform float uSphereRadius;


vec2 getDistortedRayUV(out bool absorbed) {
    absorbed = false;
    vec2 resolution = vec2(textureSize(WorldColor, 0));

    vec2 fragUV = gl_FragCoord.xy / resolution;
    vec2 bhUV = getViewSpaceUV(SpherePos, ProjMat);
    bhUV = clamp(bhUV, 0.0, 1.0);

    vec2 delta = fragUV - bhUV;

    float t;
    bool inside;
    vec3 rd = normalize(rayDirection);
    vec3 ro = rayOrigin;
    absorbed = hitSphere(ro, rd, uSchwarzschildRadius, t, inside);

    vec3 L = SpherePos - ro;
    float t_closest = dot(L, rd);
    if (t_closest < 0.0)
    {
        t_closest = 0;
    }
    vec3 closestPoint = ro + rd * t_closest;
    float d3D = length(SpherePos - closestPoint);

    float surfaceDist = max(d3D - uSchwarzschildRadius, 0.001);

    float base = (uMass * 0.1) / (surfaceDist + 0.05);
    float decay = exp(-pow(surfaceDist * uDistModifier * 5.0, 2.0));
    float gravityStrength = base * decay;

    return bhUV + delta * (1.0 - gravityStrength);
}

vec2 getDistortedTextureUV(out bool isBlack)
{
    isBlack = false;
    return fract(getDistortedRayUV(isBlack));
}

void main() {
    vec3 rayDir = normalize(rayDirection);
    float t;
    bool inside;
    hitSphere(rayOrigin, rayDir, uSphereRadius, t, inside);
    if (uWriteOnlyDepth == 1)
    {
        float writtenDepth = writeDepth(t, rayOrigin, rayDir);
        if (inside || writtenDepth <= 0.01)
        {
            gl_FragDepth = 0.0;
        }
        return;
    }

    bool isBlack;
    vec2 uvCoord = getDistortedTextureUV(isBlack);
    vec4 backgroundColor = texture(WorldColor, uvCoord);

    fragColor = backgroundColor;
    fragColor.a = 1.0;
    if (isBlack)
        fragColor = vec4(0, 0, 0, 1);
    float writtenDepth = writeDepth(t, rayOrigin, rayDir);
    if (inside || writtenDepth <= 0.01)
    {
        gl_FragDepth = 0.0;
    }
}