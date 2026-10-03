attribute vec3 vPosition;
attribute vec2 vTexCoord;

uniform vec3 uPreTranslate;
uniform vec3 uScale;
uniform vec3 uPostTranslate;

varying mediump vec2 vTexCoordOut;

void main() {
    gl_Position = vec4 ((vPosition + uPostTranslate) * uScale + uPreTranslate, 1.0);
    vTexCoordOut = vTexCoord;
}

