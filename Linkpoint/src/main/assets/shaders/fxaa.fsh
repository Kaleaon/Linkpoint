precision mediump float;
precision mediump int;

/* const float FXAA_SPAN_MAX = 8.0;
const float FXAA_REDUCE_MUL = 1.0/8.0;
const float FXAA_REDUCE_MIN = (1.0/128.0); */

const float FXAA_SPAN_MAX = 8.0;
const float FXAA_REDUCE_MUL = 1.0/8.0;
const float FXAA_REDUCE_MIN = (1.0/128.0);

uniform sampler2D textureSampler;
uniform sampler2D noAAtextureSampler;

// The inverse of the texture dimensions along X and Y
uniform vec2 texcoordOffset;

varying mediump vec2 vTexCoordOut;

void main() {

  vec4 noAAcolor = texture2D (noAAtextureSampler, vTexCoordOut.xy);

  vec3 rgbNW = texture2D(textureSampler, vTexCoordOut.xy + (vec2(-1.0, -1.0) * texcoordOffset)).xyz;
  vec3 rgbNE = texture2D(textureSampler, vTexCoordOut.xy + (vec2(+1.0, -1.0) * texcoordOffset)).xyz;
  vec3 rgbSW = texture2D(textureSampler, vTexCoordOut.xy + (vec2(-1.0, +1.0) * texcoordOffset)).xyz;
  vec3 rgbSE = texture2D(textureSampler, vTexCoordOut.xy + (vec2(+1.0, +1.0) * texcoordOffset)).xyz;
  vec3 rgbM  = texture2D(textureSampler, vTexCoordOut.xy).xyz;
	
  vec3 luma = vec3(0.299, 0.587, 0.114);
  float lumaNW = dot(rgbNW, luma);
  float lumaNE = dot(rgbNE, luma);
  float lumaSW = dot(rgbSW, luma);
  float lumaSE = dot(rgbSE, luma);
  float lumaM  = dot( rgbM, luma);
	
  float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
  float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	
  vec2 dir;
  dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
  dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
	
  float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * FXAA_REDUCE_MUL), FXAA_REDUCE_MIN);
	  
  float rcpDirMin = 1.0/(min(abs(dir.x), abs(dir.y)) + dirReduce);
	
  dir = min(vec2(FXAA_SPAN_MAX,  FXAA_SPAN_MAX), 
        max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX), dir * rcpDirMin)) * texcoordOffset;
		
  vec3 rgbA = (1.0/2.0) * (
              texture2D(textureSampler, vTexCoordOut.xy + dir * (1.0/3.0 - 0.5)).xyz +
              texture2D(textureSampler, vTexCoordOut.xy + dir * (2.0/3.0 - 0.5)).xyz);
  vec3 rgbB = rgbA * (1.0/2.0) + (1.0/4.0) * (
              texture2D(textureSampler, vTexCoordOut.xy + dir * (0.0/3.0 - 0.5)).xyz +
              texture2D(textureSampler, vTexCoordOut.xy + dir * (3.0/3.0 - 0.5)).xyz);
  float lumaB = dot(rgbB, luma);

  vec3 aaColor;

  if((lumaB < lumaMin) || (lumaB > lumaMax)){
    aaColor=rgbA;
  } else {
	aaColor=rgbB;
  }
  
  gl_FragColor = vec4 (noAAcolor.rgb * noAAcolor.a + aaColor * (1.0 - noAAcolor.a), 1.0);

}
