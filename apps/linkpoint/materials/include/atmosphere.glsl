// Shared atmospheric scattering, ported line for line from ATMOSPHERE_GLSL in
// src/linkpoint/atmosphere.ts. The uniforms keep their names as material parameters
// (setParameter('uBlueHorizon', ...)), so atmosphereUniforms() feeds them unchanged.
// Included into sky.mat and water.mat by scripts/build-materials.mjs. World space is Z up.

vec3 atmosphereColor(vec3 dir) {
    // Position on the (notional) dome, lifted 50 m so the horizon is not degenerate, then
    // clamped to the maximum atmosphere height as the viewer does.
    vec3 rel = dir * 15000.0 + vec3(0.0, 0.0, 50.0);
    if (rel.z > 0.0) rel *= materialParams.uMaxY / rel.z;
    if (rel.z < 0.0) rel *= -32000.0 / rel.z;
    vec3 relNorm = normalize(rel);
    float relLen = length(rel);
    float lightDot = dot(relNorm, materialParams.uLightNorm);

    vec3 sunlight = materialParams.uSunUp > 0.5 ? materialParams.uSunlight : materialParams.uSunlight * 0.7;

    // sunlight attenuation (hue and brightness) through the atmosphere
    vec3 lightAtten = (materialParams.uBlueDensity + vec3(materialParams.uHazeDensity * 0.25))
        * (materialParams.uDensityMultiplier * materialParams.uMaxY);
    vec3 combinedHaze = max(abs(materialParams.uBlueDensity) + vec3(abs(materialParams.uHazeDensity)), vec3(1e-6));
    vec3 blueWeight = materialParams.uBlueDensity / combinedHaze;
    vec3 hazeWeight = vec3(materialParams.uHazeDensity) / combinedHaze;

    float offAxis = 1.0 / max(1e-6, max(0.0, relNorm.z) + materialParams.uLightNorm.z);
    sunlight *= exp(-lightAtten * offAxis);

    float densityDist = relLen * materialParams.uDensityMultiplier;
    combinedHaze = exp(-combinedHaze * densityDist);

    // haze glow around the light, brightest toward it
    float hazeGlow = 1.0 - lightDot;
    hazeGlow = max(hazeGlow, 0.001);
    hazeGlow *= materialParams.uGlow.x;
    hazeGlow = pow(hazeGlow, materialParams.uGlow.z);
    hazeGlow = (materialParams.uSunMoonGlow < 1.0) ? 0.0 : (materialParams.uSunMoonGlow * (hazeGlow + 0.25));

    vec3 color = (materialParams.uBlueHorizon * blueWeight * (sunlight + materialParams.uAmbient)
                 + (materialParams.uHazeHorizon * hazeWeight) * (sunlight * hazeGlow + materialParams.uAmbient));
    color *= (1.0 - combinedHaze);

    // more ambient under cloud, less sun
    vec3 ambient = materialParams.uAmbient + max(vec3(0.0), (1.0 - materialParams.uAmbient)) * materialParams.uCloudShadow * 0.5;
    sunlight *= max(0.0, 1.0 - materialParams.uCloudShadow);
    vec3 belowCloud = (materialParams.uBlueHorizon * blueWeight * (sunlight + ambient)
                      + (materialParams.uHazeHorizon * hazeWeight) * (sunlight * hazeGlow + ambient));
    combinedHaze = sqrt(combinedHaze);
    color += (belowCloud - color) * (1.0 - sqrt(combinedHaze));

    color *= 2.0;
    return clamp(color, vec3(0.0), vec3(5.0));
}

// The sky shader outputs linear light; the viewer tone maps and converts to sRGB in a later
// pass. This soft clip plus the sRGB curve reproduces that for a bright sky in display range.
vec3 toneMapSky(vec3 c) {
    return pow(vec3(1.0) - exp(-c * 1.2), vec3(1.0 / 2.2));
}
