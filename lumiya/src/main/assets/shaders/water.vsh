attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;

varying mediump vec2 vWaterXY;

void main() {

    vWaterXY = vPosition.xy;
    gl_Position = uMVPMatrix * uObjWorldMatrix * vPosition;

}

