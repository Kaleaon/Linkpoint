attribute vec4 vPosition;
uniform mat4 uMVPMatrix;

void main() {
	gl_Position = uMVPMatrix * vec4 (vPosition.xyz, 0.0);
	gl_PointSize = 1.5;
}

