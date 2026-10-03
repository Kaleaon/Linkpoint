#version 300 es

uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform mat4 uTexMatrix;
uniform vec4 uObjCoordScale;

uniform vec4 vColor;
uniform vec3 LightDiffuseDir;
uniform lowp vec3 LightAmbientColor;
uniform lowp vec3 LightDiffuseColor;

uniform mat4 uBindShapeMatrix;

layout(std140) uniform RiggingData {
    int jointMap[__MAX_RIGGED_MESH_JOINTS__];
    mat4 jointMatrices[__MAX_RIGGED_MESH_JOINTS__];
};

layout(std140) uniform AnimationData {
    mat4 uJointMatrix [__MAX_RIGGED_MESH_BONES__];
};

in vec4 vPosition;
in vec4 vWeight;
in ivec4 vJoint;
in vec2 vTexCoord;
in vec3 vNormal;

out mediump vec2 vTexCoordOut;
out lowp vec4 vLightColor;

void main() {

#ifdef __ADRENO__

    mat4 matSum = uJointMatrix[jointMap[vJoint[0]]] * jointMatrices[vJoint[0]] * vWeight[0];
    matSum += uJointMatrix[jointMap[vJoint[1]]] * jointMatrices[vJoint[1]] * vWeight[1];
    matSum += uJointMatrix[jointMap[vJoint[2]]] * jointMatrices[vJoint[2]] * vWeight[2];
    matSum += uJointMatrix[jointMap[vJoint[3]]] * jointMatrices[vJoint[3]] * vWeight[3];

#else

    mat4 matSum = mat4 (0.0);
    int i;

    for (i = 0; i < 4 && vWeight[i] != 0.0; i++) {
        matSum += uJointMatrix[jointMap[vJoint[i]]] * jointMatrices[vJoint[i]] * vWeight[i];
    }


#endif

    mat4 finalMatrix = uObjWorldMatrix * matSum * uBindShapeMatrix;

    gl_Position = uMVPMatrix * finalMatrix * vPosition;

    mat4 normalMatrix = transpose (inverse (finalMatrix));
	vec3 worldNormal = normalize (vec3 (normalMatrix * vec4 (vNormal, 0.0)));

    vTexCoordOut = vec2 (uTexMatrix * vec4 (vTexCoord, 0.0, 1.0));

  	lowp vec3 lightColor = LightDiffuseColor * max (dot (worldNormal, normalize (-LightDiffuseDir)), 0.0) + LightAmbientColor;
	vLightColor = vColor * vec4 (min (lightColor, 1.0), 1.0);

}

