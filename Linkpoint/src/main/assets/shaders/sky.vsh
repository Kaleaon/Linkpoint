attribute vec4 vPosition;
uniform mat4 uMVPMatrix;

varying vec3 planarCoord;

void main() {

	planarCoord = vPosition.xyz;
	vec4 projected = uMVPMatrix * vec4 (vPosition.xyz, 0.0);
	gl_Position = vec4 (projected.xyz, projected.z);


}

