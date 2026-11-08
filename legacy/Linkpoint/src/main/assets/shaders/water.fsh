precision mediump float;

uniform vec4 vColor;
uniform sampler2D uNormalMap;

varying mediump vec2 vWaterXY;
varying mediump vec2 vBumpUV0;
varying mediump vec2 vBumpUV1;

uniform float time;

uniform float frequency[4];
uniform float phase[4];
uniform float amplitude[4];
uniform vec2 direction[4];

const vec3 DiffuseLightDirection = vec3 (1.0, 1.0, 0.5);
const vec3 ViewVector = vec3 (0.0, 0.0, 1.0);

const float DiffuseIntensity = 0.5;
const float SpecularIntensity = 1.0;
const vec4 SpecularColor = vec4 (0.8, 1.0, 1.0, 1.0);

void main() {

    vec3 normal1 = texture2D(uNormalMap, vBumpUV0).rgb * 2.0 - 1.0;
    vec3 normal2 = texture2D(uNormalMap, vBumpUV1).rgb * 2.0 - 1.0;
    vec2 waveNormal = normal1.xy + normal2.xy;

    vec3 normal = normalize (vec3 (-waveNormal, 1.0));
    vec3 light = normalize (DiffuseLightDirection);

    vec3 r = normalize(2.0 * dot(light, normal) * normal - light);
    float dotProduct = dot (r, ViewVector);

    gl_FragColor = DiffuseIntensity * vColor + SpecularIntensity * SpecularColor * max (dotProduct, 0.0) + vec4 (0.0, 0.0, 0.0, 1.0);

}
