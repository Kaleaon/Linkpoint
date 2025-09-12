precision mediump float;

uniform sampler2D sTexture;
uniform bool useTexture;

varying mediump vec2 vTexCoordOut;
varying lowp vec4 vLightColor;

void main() {
    gl_FragColor = useTexture ? (vLightColor * texture2D (sTexture, vTexCoordOut)) : vLightColor;
}

