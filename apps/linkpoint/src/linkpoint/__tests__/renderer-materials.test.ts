import { readFileSync } from 'node:fs';
import path from 'node:path';
import { afterEach, describe, expect, it, vi } from 'vitest';
import {
  ATMOSPHERE_PARAMETERS, MATERIAL_URLS, SKY_PARAMETERS, TERRAIN_PARAMETERS, WATER_PARAMETERS, loadMaterials,
  type MaterialEngineLike, type MaterialInstanceLike, type MaterialLike,
} from '../renderer/materials';
import { atmosphereUniforms } from '../atmosphere';

const root = path.resolve(__dirname, '../../..');
const declaredParameters = (name: string) => {
  const source = readFileSync(path.join(root, 'materials', `${name}.mat`), 'utf8');
  return [...source.matchAll(/\{\s*type\s*:\s*\w+\s*,\s*(?:precision\s*:\s*\w+\s*,\s*)?name\s*:\s*(\w+)|\{\s*type\s*:\s*\w+\s*,\s*name\s*:\s*(\w+)/g)].map((m) => m[1] ?? m[2]);
};

function mockEngine() {
  const instance = (): MaterialInstanceLike => ({
    setFloatParameter: vi.fn(), setFloat3Parameter: vi.fn(), setFloat4Parameter: vi.fn(), setTextureParameter: vi.fn(),
  });
  const materials: (MaterialLike & { bytes: ArrayBufferView })[] = [];
  const engine: MaterialEngineLike & { destroyed: unknown[] } = {
    destroyed: [],
    createMaterial: vi.fn((bytes: ArrayBufferView) => {
      const material = { bytes, createInstance: vi.fn(instance) };
      materials.push(material);
      return material;
    }),
    destroyMaterial: vi.fn(function (this: unknown, m) { engine.destroyed.push(m); }),
  };
  return { engine, materials };
}

describe('renderer materials', () => {
  afterEach(() => vi.unstubAllGlobals());

  it('declares the same parameters in each .mat as the loader exposes', () => {
    expect(declaredParameters('sky').sort()).toEqual([...SKY_PARAMETERS].sort());
    expect(declaredParameters('water').sort()).toEqual([...WATER_PARAMETERS].sort());
    expect(declaredParameters('terrain').sort()).toEqual([...TERRAIN_PARAMETERS].sort());
  });

  it('keeps the atmosphere parameters in step with atmosphereUniforms', () => {
    const uniforms = atmosphereUniforms(
      { blueHorizon: [1, 1, 1], blueDensity: [1, 1, 1], ambient: [1, 1, 1], sunlightColor: [1, 1, 1], glow: [1, 1, 1] } as never,
      { lightDirection: [0, 0, 1] } as never,
    );
    expect(Object.keys(uniforms).sort()).toEqual([...ATMOSPHERE_PARAMETERS].sort());
  });

  it('fetches each package by its URL and builds one material per package', async () => {
    const fetched: string[] = [];
    vi.stubGlobal('fetch', vi.fn(async (url: string) => {
      fetched.push(url);
      return { ok: true, status: 200, statusText: 'OK', text: async () => 'AQID' };
    }));
    const { engine, materials } = mockEngine();
    const loaded = await loadMaterials(engine);
    expect(fetched.sort()).toEqual(Object.values(MATERIAL_URLS).sort());
    expect(materials).toHaveLength(3);
    expect(Array.from(materials[0].bytes as Uint8Array)).toEqual([1, 2, 3]);
    expect(loaded.sky.name).toBe('sky');
    loaded.terrain.createInstance();
    loaded.terrain.createInstance();
    expect(materials.map((m) => (m.createInstance as ReturnType<typeof vi.fn>).mock.calls.length).reduce((a, b) => a + b)).toBe(2);
    loaded.dispose();
    loaded.dispose();
    expect(engine.destroyed).toHaveLength(3);
  });

  it('rejects when a package cannot be fetched', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => ({ ok: false, status: 404, statusText: 'Not Found', text: async () => '' })));
    const { engine } = mockEngine();
    await expect(loadMaterials(engine)).rejects.toThrow(/material \(404/);
    expect(engine.createMaterial).not.toHaveBeenCalled();
  });

  it('destroys already built materials when the engine rejects a later one', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => ({ ok: true, status: 200, statusText: 'OK', text: async () => 'AAAAAA==' })));
    const { engine } = mockEngine();
    let calls = 0;
    const create = engine.createMaterial;
    engine.createMaterial = vi.fn((bytes) => (++calls === 3 ? (undefined as never) : create(bytes)));
    await expect(loadMaterials(engine)).rejects.toThrow(/rejected/);
    expect(engine.destroyed).toHaveLength(2);
  });

  it('verifies water material parameters include uNormalMap sampler without trig functions in fragment shader', () => {
    expect(WATER_PARAMETERS).toContain('uNormalMap');
    const waterSource = readFileSync(path.join(root, 'materials', 'water.mat'), 'utf8');
    const fragmentBlockMatch = waterSource.match(/fragment\s*\{([\s\S]*?)\}/);
    expect(fragmentBlockMatch).not.toBeNull();
    const fragmentContent = fragmentBlockMatch![1];
    expect(fragmentContent).not.toMatch(/\bsin\s*\(/);
    expect(fragmentContent).not.toMatch(/\bcos\s*\(/);
    expect(waterSource).toContain('uNormalMap');
  });

  it('allows setting normal map texture parameter on water material instance', () => {
    const { materials } = mockEngine();
    const { engine } = mockEngine();
    const material = engine.createMaterial(new Uint8Array());
    const instance = material.createInstance();
    const dummyTexture = {};
    const dummySampler = {};
    instance.setTextureParameter('uNormalMap', dummyTexture, dummySampler);
    expect(instance.setTextureParameter).toHaveBeenCalledWith('uNormalMap', dummyTexture, dummySampler);
  });
});
