#version 330 core

in vec3 Position;

uniform mat4 ProjMat;

out vec3 rayOrigin;
out vec3 rayDirection;

void main() {
    vec4 viewPos4 = vec4(Position, 1.0);

    rayOrigin = vec3(0.0);
    gl_Position = ProjMat * viewPos4;

    rayDirection = viewPos4.xyz;
}
