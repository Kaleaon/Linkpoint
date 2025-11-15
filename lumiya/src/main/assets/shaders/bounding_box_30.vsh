#version 300 es

uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform vec4 uObjCoordScale;

in vec4 vPosition;

void main() {

    gl_Position = uMVPMatrix * uObjWorldMatrix * (vPosition * uObjCoordScale);

}

