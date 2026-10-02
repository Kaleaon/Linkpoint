import { createRequire } from 'node:module';
import { describe, expect, it } from 'vitest';

const { primAppearance } = createRequire(import.meta.url)('../../../core/serializers.cjs');
const ZERO = '00000000-0000-0000-0000-000000000000';
const ID = '11111111-2222-3333-4444-555555555555';
const uuid = (value: string) => ({ toString: () => value });

describe('which asset a prim draws from', () => {
  it('treats a sculpt parameter of type 5 as a mesh (how the simulator sends meshes)', () => {
    const a = primAppearance({ extraParams: { sculptData: { texture: uuid(ID), type: 5 } } });
    expect(a).toMatchObject({ assetKind: 'mesh', assetId: ID, sculptType: null });
  });

  it('keeps types 1-4 as sculpts and passes the flags through to the decoder', () => {
    expect(primAppearance({ extraParams: { sculptData: { texture: uuid(ID), type: 1 | 0x40 | 0x80 } } }))
      .toMatchObject({ assetKind: 'sculpt', assetId: ID, sculptType: 1 | 0x40 | 0x80 });
    expect(primAppearance({ extraParams: { sculptData: { texture: uuid(ID), type: 4 } } }).assetKind).toBe('sculpt');
  });

  it('accepts the dedicated mesh parameter', () => {
    expect(primAppearance({ extraParams: { meshData: { meshData: uuid(ID), type: 5 } } })).toMatchObject({ assetKind: 'mesh', assetId: ID });
  });

  it('draws an ordinary prim when the sculpt is type none or has no asset', () => {
    expect(primAppearance({ extraParams: { sculptData: { texture: uuid(ID), type: 0 } } })).toMatchObject({ assetKind: null, assetId: null });
    expect(primAppearance({ extraParams: { sculptData: { texture: uuid(ZERO), type: 1 } } })).toMatchObject({ assetKind: null, assetId: null });
    expect(primAppearance({ extraParams: { meshData: { meshData: uuid(ZERO), type: 5 } } })).toMatchObject({ assetKind: null });
  });

  it('flags animated meshes only for meshes', () => {
    const mesh = { sculptData: { texture: uuid(ID), type: 5 }, extendedMeshData: { flags: 1 } };
    expect(primAppearance({ extraParams: mesh }).animatedMesh).toBe(true);
    expect(primAppearance({ extraParams: { ...mesh, sculptData: { texture: uuid(ID), type: 2 } } }).animatedMesh).toBe(false);
  });
});
