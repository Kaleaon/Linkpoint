attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform float time;

varying mediump vec2 vWaterXY;
varying mediump vec2 vBumpUV0;
varying mediump vec2 vBumpUV1;

void main() {

    vWaterXY = vPosition.xy;
    vec2 uv = vPosition.xy * 0.05;
    vBumpUV0 = uv + vec2(time * 0.02, time * 0.01);
    vBumpUV1 = uv * 1.5 - vec2(time * 0.01, time * 0.03);
    gl_Position = uMVPMatrix * uObjWorldMatrix * vPosition;

}
