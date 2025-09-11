precision mediump float;

uniform samplerCube textureSampler;

uniform vec3 cloudColor;
uniform vec3 skyColor;
uniform float hazeHorizon;
uniform vec3 hazeColor;
uniform float cloudGamma;
uniform float cloudAdd;

varying vec3 planarCoord;

void main() {

	float hazeIntensity = clamp ((hazeHorizon - planarCoord.z) * 2.0, 0.0, 1.0); 

	float cloudNoise = pow (textureCube (textureSampler, planarCoord).a + cloudAdd, 1.0 / cloudGamma);

	gl_FragColor.rgb = cloudColor *  clamp (cloudNoise, 0.0, 1.0)
						+ skyColor
						+ hazeColor * hazeIntensity;

	gl_FragColor.a = 1.0;						
 
}

