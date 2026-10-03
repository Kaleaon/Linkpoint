precision mediump float;

uniform sampler2D sTexture;
uniform bool useTexture;

varying mediump vec2 vTexCoordOut;
varying lowp vec4 vLightColor;

void main() {
    lowp vec4 color = useTexture ? (vLightColor * texture2D (sTexture, vTexCoordOut)) : vLightColor;

    if (color.a < 0.1)
        discard;
    else
        gl_FragColor = color;
}

