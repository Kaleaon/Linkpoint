attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
uniform mat4 uObjWorldMatrix;
uniform mat4 uTexMatrix;
uniform vec4 uObjCoordScale;

attribute vec2 vTexCoord;
varying mediump vec2 vTexCoordOut;

attribute vec3 vNormal;
uniform vec4 vColor;

uniform vec3 LightDiffuseDir;
uniform lowp vec3 LightAmbientColor;
uniform lowp vec3 LightDiffuseColor;

varying lowp vec4 vLightColor;

void main() {
    gl_Position = uMVPMatrix * uObjWorldMatrix * (vPosition * uObjCoordScale);
    vTexCoordOut = vec2 (uTexMatrix * vec4 (vTexCoord, 0.0, 1.0));

    vec3 worldNormal = normalize (vec3 (uObjWorldMatrix * vec4 (vNormal, 0.0)));
    lowp vec3 lightColor = LightDiffuseColor * max (dot (worldNormal, normalize (-LightDiffuseDir)), 0.0)  
							+ LightAmbientColor;

    vLightColor = vColor * vec4 (min (lightColor, 1.0), 1.0);
}

