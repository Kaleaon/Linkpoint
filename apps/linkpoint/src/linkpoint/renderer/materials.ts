// Loads the precompiled Filament materials (materials/*.mat compiled by scripts/build-materials.mjs).
//
// The .filamat packages are imported with Vite's `?url`, so the bundler fingerprints them and
// they resolve against the page's own origin: http(s) in the browser, the linkpoint:// scheme in
// Electron and the bundled assets in Capacitor. They are fetched as raw bytes (no reliance on
// the server's content type) and handed to engine.createMaterial.
import skyUrl from './materials/sky.filamat.base64?url';
import terrainUrl from './materials/terrain.filamat.base64?url';
import waterUrl from './materials/water.filamat.base64?url';

/** The slice of a Filament MaterialInstance the viewer drives. */
export interface MaterialInstanceLike {
  setFloatParameter(name: string, value: number): void;
  setFloat3Parameter(name: string, value: ArrayLike<number>): void;
  setFloat4Parameter(name: string, value: ArrayLike<number>): void;
  setTextureParameter(name: string, texture: unknown, sampler: unknown): void;
}

export interface MaterialLike<I extends MaterialInstanceLike = MaterialInstanceLike> {
  createInstance(): I;
}

/** The slice of a Filament Engine used to load materials. */
export interface MaterialEngineLike<M extends MaterialLike = MaterialLike> {
  createMaterial(buffer: ArrayBufferView): M;
  destroyMaterial?(material: M): void;
}

export type MaterialName = 'sky' | 'water' | 'terrain';

/** Make fresh instances of one compiled material; each instance carries its own parameters. */
export interface MaterialFactory<I extends MaterialInstanceLike = MaterialInstanceLike> {
  readonly name: MaterialName;
  createInstance(): I;
}

export interface LoadedMaterials<I extends MaterialInstanceLike = MaterialInstanceLike> {
  sky: MaterialFactory<I>;
  water: MaterialFactory<I>;
  terrain: MaterialFactory<I>;
  /** Destroys the materials (instances must be destroyed first). */
  dispose(): void;
}

export const MATERIAL_URLS: Record<MaterialName, string> = { sky: skyUrl, water: waterUrl, terrain: terrainUrl };

/** The atmosphere parameters shared by sky and water, named as AtmosphereUniforms. */
export const ATMOSPHERE_PARAMETERS = [
  'uBlueHorizon', 'uBlueDensity', 'uAmbient', 'uSunlight', 'uGlow', 'uLightNorm',
  'uHazeHorizon', 'uHazeDensity', 'uDensityMultiplier', 'uMaxY', 'uCloudShadow', 'uSunUp', 'uSunMoonGlow',
] as const;

export const SKY_PARAMETERS = [
  ...ATMOSPHERE_PARAMETERS, 'uSunDir', 'uMoonDir', 'uSunRadius', 'uMoonRadius', 'uMoonBrightness', 'uMoonUp',
] as const;

export const WATER_PARAMETERS = [
  ...ATMOSPHERE_PARAMETERS, 'uCameraPos', 'uFogColor', 'uFogDensity', 'uFresnelScale', 'uFresnelOffset',
  'uLightDir', 'uLightColor', 'uSurfaceAmbient', 'uTime', 'uPixelAngle', 'uNormalScale', 'uWaterHeight',
  'uFrequency', 'uPhase', 'uAmplitude', 'uDirection01', 'uDirection23', 'uNormalMap',
] as const;

export const TERRAIN_PARAMETERS = [
  'uComposition', 'uDetail0', 'uDetail1', 'uDetail2', 'uDetail3', 'uDetailUse',
  'uFallback0', 'uFallback1', 'uFallback2', 'uFallback3', 'uLightPos', 'uLightColor', 'uAmbientColor', 'uTileScale',
] as const;

async function fetchPackage(name: MaterialName, url: string): Promise<Uint8Array> {
  const response = await fetch(url);
  if (!response.ok) throw new Error(`Could not load the ${name} material (${response.status} ${response.statusText})`);
  const encoded = (await response.text()).replace(/\s/g, '');
  return Uint8Array.from(atob(encoded), (character) => character.charCodeAt(0));
}

/**
 * Fetches and builds every material. Rejects if a package cannot be fetched or the engine
 * refuses it (typically a .filamat built by a matc that does not match the runtime version).
 */
export async function loadMaterials<I extends MaterialInstanceLike, M extends MaterialLike<I>>(
  engine: MaterialEngineLike<M>,
): Promise<LoadedMaterials<I>> {
  const names = Object.keys(MATERIAL_URLS) as MaterialName[];
  const packages = await Promise.all(names.map((name) => fetchPackage(name, MATERIAL_URLS[name])));
  const built: M[] = [];
  try {
    const materials = {} as Record<MaterialName, M>;
    names.forEach((name, i) => {
      const material = engine.createMaterial(packages[i]);
      if (!material) throw new Error(`Filament rejected the ${name} material; rebuild it with npm run build:materials`);
      built.push(material);
      materials[name] = material;
    });
    const factory = (name: MaterialName): MaterialFactory<I> => ({ name, createInstance: () => materials[name].createInstance() });
    return {
      sky: factory('sky'),
      water: factory('water'),
      terrain: factory('terrain'),
      dispose() {
        for (const material of built.splice(0)) engine.destroyMaterial?.(material);
      },
    };
  } catch (error) {
    for (const material of built) engine.destroyMaterial?.(material);
    throw error;
  }
}
