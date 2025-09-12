precision mediump float;

uniform vec3 skyColor;
uniform float hazeHorizon;
uniform vec3 hazeColor;

varying vec3 planarCoord;

void main() {

	float hazeIntensity = clamp ((hazeHorizon - planarCoord.z) * 2.0, 0.0, 1.0); 

	gl_FragColor.rgb = skyColor
						+ hazeColor * hazeIntensity;

	gl_FragColor.a = 1.0;						
 
}

