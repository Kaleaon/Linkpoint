import { describe, expect, it, vi } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter { connected = true; authReply: Record<string, any> | null = null; }

describe('terrain materials in the world', () => {
  it('passes the region\'s terrain textures, blend ranges and water height to the scene', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const scene: any = { setTerrain: vi.fn(), setTerrainMaterials: vi.fn().mockReturnValue(true), setWaterHeight: vi.fn(), objects: new Map() };
    (world as any).scene3d = scene;
    protocol.emit('scene:world-data', {
      terrainMaterials: {
        textureIds: ['aaa', null, 'ccc', 'ddd'], startHeights: [1, 2, 3, 4], heightRanges: [10, 20, 30, 40], origin: [256000, 256256], waterHeight: 18.5,
      },
    });
    expect(scene.setWaterHeight).toHaveBeenCalledWith(18.5);
    expect(scene.setTerrainMaterials).toHaveBeenCalledWith({
      textureNames: ['texture:aaa', '', 'texture:ccc', 'texture:ddd'], startHeights: [1, 2, 3, 4], heightRanges: [10, 20, 30, 40], origin: [256000, 256256],
    });
  });

  it('applies materials that arrive before the heights, and keeps them across a scene rebuild', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    protocol.emit('scene:world-data', { terrainMaterials: { textureIds: [], startHeights: [0, 0, 0, 0], heightRanges: [1, 1, 1, 1], origin: [0, 0], waterHeight: 20 } });
    const scene: any = { setTerrain: vi.fn(), setTerrainMaterials: vi.fn(), setWaterHeight: vi.fn(), objects: new Map() };
    (world as any).scene3d = scene;
    (world as any).applyTerrainMaterials();
    expect(scene.setTerrainMaterials).toHaveBeenCalledTimes(1);
    protocol.emit('disconnected');
    scene.setTerrainMaterials.mockClear();
    (world as any).applyTerrainMaterials();
    expect(scene.setTerrainMaterials).not.toHaveBeenCalled(); // cleared on disconnect
  });

  it('ignores terrain data without blend parameters', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const scene: any = { setTerrain: vi.fn(), setTerrainMaterials: vi.fn(), setWaterHeight: vi.fn(), objects: new Map() };
    (world as any).scene3d = scene;
    protocol.emit('scene:world-data', { terrainMaterials: { textureIds: ['a'], waterHeight: null } });
    expect(scene.setTerrainMaterials).not.toHaveBeenCalled();
    expect(scene.setWaterHeight).not.toHaveBeenCalled();
  });
});
