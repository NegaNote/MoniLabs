#version 150

#line 0 1
/*#version 150*/

mat2 mat2_rotate_z(float radians) {
    return mat2(
    cos(radians), -sin(radians),
    sin(radians), cos(radians)
    );
}
#line 3 0

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;

in vec4 texProj0;
in vec3 vColor;

const vec3[] COLORS = vec3[](
vec3(0.022087, 0.098399, 0.110818),
vec3(0.011892, 0.095924, 0.089485),
vec3(0.027636, 0.101689, 0.100326),
vec3(0.046564, 0.109883, 0.114838),
vec3(0.064901, 0.117696, 0.097189),
vec3(0.063761, 0.086895, 0.123646),
vec3(0.084817, 0.111994, 0.166380),
vec3(0.097489, 0.154120, 0.091064),
vec3(0.106152, 0.131144, 0.195191),
vec3(0.097721, 0.110188, 0.187229),
vec3(0.133516, 0.138278, 0.148582),
vec3(0.070006, 0.243332, 0.235792),
vec3(0.196766, 0.142899, 0.214696),
vec3(0.047281, 0.315338, 0.321970),
vec3(0.204675, 0.390010, 0.302066),
vec3(0.080955, 0.314821, 0.661491)
);

const mat4 SCALE_TRANSLATE = mat4(
0.5, 0.0, 0.0, 0.25,
0.0, 0.5, 0.0, 0.25,
0.0, 0.0, 1.0, 0.0,
0.0, 0.0, 0.0, 1.0
);

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
    1.0, 0.0, 0.0, 17.0 / layer,
    0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2((4.5 - layer / 4.0) * 2.0);

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * COLORS[0];
    for (int i = 0; i < EndPortalLayers; i++) {
        color += textureProj(Sampler1, texProj0 * end_portal_layer(float(i + 1))).rgb * COLORS[i];
    }
    vec3 origHsv = rgb2hsv(color);
    vec3 destHsv = rgb2hsv(vColor);
    //copy hue and saturation
    origHsv.x = destHsv.x;
    origHsv.y = destHsv.y;
    color.rgb = hsv2rgb(origHsv);
    fragColor = vec4(color, 1.0);
}
