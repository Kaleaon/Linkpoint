import { describe, expect, it } from 'vitest';
import { createRequire } from 'node:module';
import { dayFraction, directionFrom, skyAt, skyState, waterAt } from '../eep';

const require = createRequire(import.meta.url);
const { LLSD } = require('@caspertech/node-metaverse/dist/lib/classes/llsd/LLSD');
const { RegionEnvironment } = require('@caspertech/node-metaverse/dist/lib/classes/public/RegionEnvironment');
const { serializeEnvironment } = require('../../../core/viewer-session.cjs');

const sky = (name: string, sunlight: string, sunRotation: string) =>
  `{'type':'sky','name':'${name}','sunlight_color':[${sunlight}],'sun_rotation':[${sunRotation}],'moon_rotation':[r0,r0.7071,r0,r0.7071],` +
  `'max_y':r1605,'glow':[r5,r0.001,r-0.48],'cloud_shadow':r0.27,'star_brightness':r0,'moon_brightness':r0.5,` +
  `'legacy_haze':{'ambient':[r0.25,r0.25,r0.25],'blue_density':[r0.2447,r0.4487,r0.7599],'blue_horizon':[r0.4954,r0.4954,r0.6399],'haze_density':r0.7,'haze_horizon':r0.19,'density_multiplier':r0.0001,'distance_multiplier':r0.8}}`;
const water = "{'type':'water','water_fog_color':[r0.0156,r0.149,r0.2509],'water_fog_density':r2,'fresnel_scale':r0.4,'fresnel_offset':r0.5,'normal_scale':[r2,r2,r2],'wave1_direction':[r1.05,r-0.42],'wave2_direction':[r1.11,r-1.16],'normal_map':u822ded49-9a6c-f61c-cb89-6df54f42cdf4}";
const notation = `{'success':true,'environment':{'day_cycle':{'type':'daycycle','name':'t','frames':{` +
  `'noon':${sky('noon', 'r1,r1,r1,r0', 'r0,r-0.7071,r0,r0.7071')},'dusk':${sky('dusk', 'r1,r0.4,r0.1,r0', 'r0,r0,r0,r1')},'water1':${water}},` +
  `'tracks':[[{'key_keyframe':r0,'key_name':'water1'}],[{'key_keyframe':r0,'key_name':'noon'},{'key_keyframe':r0.5,'key_name':'dusk'}]]},` +
  `'day_length':i14400,'day_offset':i3600,'env_version':i1,'track_altitudes':[i1000,i2000,i3000]}}`;

describe('a region environment from node-metaverse, through the session serializer and the sampler', () => {
  const environment = serializeEnvironment(new RegionEnvironment(LLSD.parseNotation(notation)));

  it('keeps the day length, offset and cycle', () => {
    expect(environment.dayLength).toBe(14400);
    expect(environment.dayOffset).toBe(3600);
    expect(Object.keys(environment.dayCycle.frames)).toEqual(expect.arrayContaining(['noon', 'dusk', 'water1']));
  });

  it('samples sky keyframes by the time of day, with the offset applied', () => {
    // offset 3600 s = 0.25 of the day: a quarter of the way in, half way between noon (0) and dusk (0.5)
    const fraction = dayFraction(0, environment.dayLength, environment.dayOffset);
    expect(fraction).toBeCloseTo(0.25, 6);
    const sky = skyAt(environment.dayCycle, fraction);
    expect(sky.sunlightColor[1]).toBeCloseTo(0.7, 4);   // between 1.0 and 0.4
    expect(sky.sunlightColor[2]).toBeCloseTo(0.55, 4);  // between 1.0 and 0.1
    expect(sky.blueDensity).toEqual([0.2447, 0.4487, 0.7599]);
    expect(sky.hazeHorizon).toBeCloseTo(0.19, 6);
    const state = skyState(sky);
    expect(state.sunUp).toBe(true);
    expect(directionFrom(skyAt(environment.dayCycle, 0).sunRotation)[2]).toBeCloseTo(1, 4); // noon: overhead
    expect(directionFrom(skyAt(environment.dayCycle, 0.5).sunRotation)[2]).toBeCloseTo(0, 4); // dusk: on the horizon
  });

  it('reads the water track, including the normal map id', () => {
    const water = waterAt(environment.dayCycle, 0.3);
    expect(water.fogColor).toEqual([0.0156, 0.149, 0.2509]);
    expect(water.fogDensity).toBe(2);
    expect(water.fresnelScale).toBeCloseTo(0.4, 6);
    expect(water.wave1Direction).toEqual([1.05, -0.42]);
    expect(water.normalMapId).toBe('822ded49-9a6c-f61c-cb89-6df54f42cdf4');
  });
});
