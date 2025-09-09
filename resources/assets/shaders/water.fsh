precision mediump float;

uniform vec4 vColor;
varying mediump vec2 vWaterXY;

const float pi = 3.14159;

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

    float waveArg;
    float amp = 0.0;
    vec2 waveNormal = vec2 (0.0, 0.0);

    waveArg = dot(direction[0], vWaterXY) * frequency[0] + time * phase[0];
    waveNormal += direction[0] * amplitude[0] * frequency[0] * cos (waveArg);
    amp += amplitude[0] * sin (waveArg);
    
    waveArg = dot(direction[1], vWaterXY) * frequency[1] + time * phase[1];
    waveNormal += direction[1] * amplitude[1] * frequency[1] * cos (waveArg);
    amp += amplitude[1] * sin (waveArg);
    
    waveArg = dot(direction[2], vWaterXY) * frequency[2] + time * phase[2];
    waveNormal += direction[2] * amplitude[2] * frequency[2] * cos (waveArg);
    amp += amplitude[2] * sin (waveArg);

    waveArg = dot(direction[3], vWaterXY) * frequency[3] + time * phase[3];
    waveNormal += direction[3] * amplitude[3] * frequency[3] * cos (waveArg);
    amp += amplitude[3] * sin (waveArg);

    vec3 normal = normalize (vec3 (-waveNormal, 1.0));
    vec3 light = normalize (DiffuseLightDirection);

    vec3 r = normalize(2.0 * dot(light, normal) * normal - light);
    float dotProduct = dot (r, ViewVector);

    gl_FragColor = DiffuseIntensity * vColor * (1.0 + amp * 0.3) + SpecularIntensity * SpecularColor * max (dotProduct, 0.0) + vec4 (0.0, 0.0, 0.0, 1.0);


}

