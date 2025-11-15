attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform mat4 uTexMatrix;
uniform vec4 uObjCoordScale;

uniform mat4 uBindShapeMatrix;
uniform vec4 uJointVectors[__NUM_BASE_BONE_VECTORS__];

attribute vec4 vWeight;
attribute vec4 vJoint;

attribute vec2 vTexCoord;
varying mediump vec2 vTexCoordOut;

attribute vec3 vNormal;
uniform vec4 vColor;

uniform vec3 LightDiffuseDir;
uniform lowp vec3 LightAmbientColor;
uniform lowp vec3 LightDiffuseColor;

varying lowp vec4 vLightColor;

void main() {

	vec4 scaledPos = uBindShapeMatrix * vPosition;
	mat4 matSum = mat4 (0.0);
	
	int joint0 = int (vJoint[0]) * 3;
	int joint1 = int (vJoint[1]) * 3;
	int joint2 = int (vJoint[2]) * 3;
	int joint3 = int (vJoint[3]) * 3;
	
	mat4 jointMatrix0 = mat4 (uJointVectors[joint0], uJointVectors[joint0 + 1], uJointVectors[joint0 + 2], vec4 (0.0, 0.0, 0.0, 1.0));
	mat4 jointMatrix1 = mat4 (uJointVectors[joint1], uJointVectors[joint1 + 1], uJointVectors[joint1 + 2], vec4 (0.0, 0.0, 0.0, 1.0));
	mat4 jointMatrix2 = mat4 (uJointVectors[joint2], uJointVectors[joint2 + 1], uJointVectors[joint2 + 2], vec4 (0.0, 0.0, 0.0, 1.0));
	mat4 jointMatrix3 = mat4 (uJointVectors[joint3], uJointVectors[joint3 + 1], uJointVectors[joint3 + 2], vec4 (0.0, 0.0, 0.0, 1.0));
	
	matSum += jointMatrix0 * vWeight[0];
	matSum += jointMatrix1 * vWeight[1];
	matSum += jointMatrix2 * vWeight[2];
	matSum += jointMatrix3 * vWeight[3];

	gl_Position = uMVPMatrix * uObjWorldMatrix * (scaledPos * matSum);

    vTexCoordOut = vec2 (uTexMatrix * vec4 (vTexCoord, 0.0, 1.0));

	/* vec3 worldNormal = normalize (vec3 (uObjWorldMatrix * (uBindShapeMatrix * vec4 (vNormal, 0.0)) * matSum)); <-- original */

    vec3 worldNormal = normalize (vec3 ((uObjWorldMatrix * vec4 (vNormal, 0.0))));

  	lowp vec3 lightColor = LightDiffuseColor * max (dot (worldNormal, normalize (-LightDiffuseDir)), 0.0)
							+ LightAmbientColor;
	
	vLightColor = vColor * vec4 (min (lightColor, 1.0), 1.0);
}
