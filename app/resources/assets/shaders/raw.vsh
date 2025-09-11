attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 vTextureTransformMatrix;

attribute vec2 vTexCoord;
varying mediump vec2 vTexCoordOut;

void main() {
    vTexCoordOut = (vTextureTransformMatrix * vec4 (vTexCoord.x, vTexCoord.y, 0.0, 0.0)).xy;
    gl_Position = uMVPMatrix * vPosition;
}

