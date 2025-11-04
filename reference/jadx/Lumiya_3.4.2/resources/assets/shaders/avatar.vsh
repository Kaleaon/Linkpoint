attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform vec4 uObjCoordScale;

uniform mat4 uJointMatrix[__NUM_BASE_JOINTS__];
uniform int uJointMap[__NUM_BASE_JOINTS__];
uniform int uJointMapLength;
attribute float vWeight;
uniform bool uUseWeight;

attribute vec2 vTexCoord;
varying mediump vec2 vTexCoordOut;

attribute vec3 vNormal;
uniform vec4 vColor;

uniform vec3 LightDiffuseDir;
uniform lowp vec3 LightAmbientColor;
uniform lowp vec3 LightDiffuseColor;

varying lowp vec4 vLightColor;

void main() {

    float fIndex = floor (vWeight);
    float vertexWeight = vWeight - fIndex;
    int vIndex = int (fIndex) - 1;

    int bone1_id = uJointMap [vIndex];
    int bone2_id = uJointMap [vIndex + 1];

    mat4 blendMatrix = uUseWeight ? (uJointMatrix[bone1_id] * (1.0 - vertexWeight) + uJointMatrix[bone2_id] * vertexWeight) : mat4 (1.0);

	gl_Position = uMVPMatrix * uObjWorldMatrix * blendMatrix * vPosition;
    vTexCoordOut = vTexCoord;

    vec3 worldNormal = normalize (vec3 (uObjWorldMatrix * blendMatrix * vec4 (vNormal, 0.0)));
    lowp vec3 lightColor = LightDiffuseColor * max (dot (worldNormal, normalize (-LightDiffuseDir)), 0.0)
							+ LightAmbientColor;

    vLightColor = vColor * vec4 (min (lightColor, 1.0), 1.0);
}

