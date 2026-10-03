#version 300 es

precision mediump float;

uniform sampler2D sTexture;
uniform bool useTexture;

in mediump vec2 vTexCoordOut;
in lowp vec4 vLightColor;
out lowp vec4 fragmentColor;

void main() {
    lowp vec4 color = useTexture ? (vLightColor * texture (sTexture, vTexCoordOut)) : vLightColor;

    if (color.a < 0.1)
        discard;
    else
        fragmentColor = color;
}

