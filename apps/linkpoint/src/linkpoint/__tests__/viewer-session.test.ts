import { describe, expect, it } from 'vitest';
import { serializeSceneObject } from '../scene-object';

describe('desktop simulator object bridge', () => {
  it('converts node-metaverse objects into structured-clone-safe scene data', () => {
    const result = serializeSceneObject({
      localID: 42,
      object: {
        FullID: { toString: () => '00000000-0000-0000-0000-000000000042' },
        ParentID: 7,
        PCode: 9,
        Position: { x: 1, y: 2, z: 3 },
        Scale: { x: 4, y: 5, z: 6 },
        Rotation: { x: 0, y: 0, z: 0, w: 1 },
        name: 'Decoded prim',
      },
    });

    expect(result).toEqual({
      id: '00000000-0000-0000-0000-000000000042',
      localId: 42,
      parentId: 7,
      pcode: 9,
      avatar: false,
      position: [1, 2, 3],
      scale: [4, 5, 6],
      rotation: [0, 0, 0, 1],
      name: 'Decoded prim',
    });
    expect(() => structuredClone(result)).not.toThrow();
  });
});
