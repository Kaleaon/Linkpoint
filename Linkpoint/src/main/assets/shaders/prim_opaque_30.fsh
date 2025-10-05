#version 300 es

precision mediump float;

uniform sampler2D sTexture;
uniform bool useTexture;

in mediump vec2 vTexCoordOut;
in lowp vec4 vLightColor;
out lowp vec4 fragmentColor;

void main() {
    fragmentColor = useTexture ? (vLightColor * texture (sTexture, vTexCoordOut)) : vLightColor;
}

