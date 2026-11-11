precision mediump float;

uniform sampler2D vTexture;

varying mediump vec2 vTexCoordOut;

void main() {

  gl_FragColor = texture2D (vTexture, vTexCoordOut);

}
