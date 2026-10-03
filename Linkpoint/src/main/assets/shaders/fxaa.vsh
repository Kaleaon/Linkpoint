attribute vec4 vPosition;
uniform mat4 uMVPMatrix;

attribute vec2 vTexCoord;
varying mediump vec2 vTexCoordOut;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    vTexCoordOut = vec2 (vTexCoord.x, 1.0 - vTexCoord.y);
}

