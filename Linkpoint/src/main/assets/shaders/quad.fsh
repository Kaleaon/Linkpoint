precision mediump float;

uniform sampler2D sTexture;
uniform bool uColorize;
uniform vec4 uColor;

varying mediump vec2 vTexCoordOut;

void main() {

	vec4 textureColor = texture2D (sTexture, vTexCoordOut);
    lowp vec4 color = uColorize ? (uColor * textureColor.aaaa) : textureColor;

    if (color.a < 0.1)
        discard;
    else
        gl_FragColor = color;

}

