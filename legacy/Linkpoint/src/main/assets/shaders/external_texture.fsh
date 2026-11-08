#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES vTexture;

varying mediump vec2 vTexCoordOut;

void main() {

  gl_FragColor = texture2D (vTexture, vTexCoordOut);

}
