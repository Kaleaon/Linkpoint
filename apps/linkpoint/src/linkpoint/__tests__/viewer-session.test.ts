import { createRequire } from 'node:module';
import { describe, expect, it, vi } from 'vitest';

const require = createRequire(import.meta.url);
const { serializeEnvironment, serializeObject, serializeTerrain, serializeFriend } = require('../../../core/viewer-session.cjs');

describe('desktop simulator object bridge', () => {
  it('serializes native friends for the renderer process', () => {
    expect(serializeFriend({
      getKey: () => ({ toString: () => 'friend-id' }),
      getName: () => 'Friend Resident', online: true, myRights: 1, theirRights: 0,
    })).toEqual({
      id: 'friend-id', name: 'Friend Resident', onlineStatus: 'online', rightsGiven: true, rightsHas: false,
    });
  });
  it('serializes region WindLight and terrain without leaking class instances', () => {
    const sky = { type: 'sky', blueHorizon: { toArray: () => [0.2, 0.4, 0.8] }, sunlightColor: [1, 0.9, 0.7] };
    const environment = serializeEnvironment({
      regionID: { toString: () => 'region-id' }, dayLength: 14400,
      dayCycle: { frames: new Map([['sky', sky]]) },
    });
    const terrain = Array.from({ length: 256 }, (_, x) => Array.from({ length: 256 }, (_, y) => x + y));

    expect(environment).toMatchObject({ regionId: 'region-id', dayLength: 14400, currentSky: { blueHorizon: [0.2, 0.4, 0.8] } });
    expect(serializeTerrain({ terrain })).toMatchObject({ size: 256, heights: expect.arrayContaining([0, 1, 255, 510]) });
    expect(() => structuredClone(environment)).not.toThrow();
  });

  it('converts node-metaverse objects into structured-clone-safe scene data', () => {
    const result = serializeObject({
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
      attachmentPoint: 0,
      attachmentName: null,
      isHud: false,
      position: [1, 2, 3],
      scale: [4, 5, 6],
      rotation: [0, 0, 0, 1],
      name: 'Decoded prim',
      shape: 'cube',
      assetKind: null,
      animatedMesh: false,
      sculptType: null,
      assetId: null,
      textureId: null,
      faceTextures: [{
        textureId: null,
        color: [1, 1, 1, 1],
        repeat: [1, 1],
        offset: [0, 0],
        rotation: 0,
        fullBright: false,
        materialId: null,
        materialOverride: null,
      }],
      reflectionProbe: null,
      particles: null,
      color: [1, 1, 1, 1],
      shapeParams: {
        pathCurve: undefined, profileCurve: undefined,
        pathBegin: undefined, pathEnd: undefined,
        pathScaleX: undefined, pathScaleY: undefined,
        pathShearX: undefined, pathShearY: undefined,
        pathTwist: undefined, pathTwistBegin: undefined,
        pathRadiusOffset: undefined,
        pathTaperX: undefined, pathTaperY: undefined,
        pathRevolutions: undefined, pathSkew: undefined,
        profileBegin: undefined, profileEnd: undefined,
        profileHollow: undefined,
      },
    });
    expect(() => structuredClone(result)).not.toThrow();
  });

  it('preserves UDP prim appearance and selects standard SL geometry', () => {
    const result = serializeObject({
      localID: 9,
      object: {
        FullID: { toString: () => 'sphere-id' }, PCode: 9,
        PathCurve: 0x20, ProfileCurve: 0x05,
        TextureEntry: { defaultTexture: { rgba: {
          getRed: () => 0.1, getGreen: () => 0.2,
          getBlue: () => 0.3, getAlpha: () => 0.4,
        } } },
      },
    });

    expect(result.shape).toBe('sphere');
    expect(result.color).toEqual([0.1, 0.2, 0.3, 0.4]);
    expect(result.shapeParams).toMatchObject({ pathCurve: 0x20, profileCurve: 0x05 });
  });

  it('passes every shape parameter the renderer needs to build SL prim geometry', () => {
    const result = serializeObject({
      localID: 11,
      object: {
        FullID: { toString: () => 'twisted' }, PCode: 9, PathCurve: 0x10, ProfileCurve: 0x01,
        PathShearX: 0.1, PathShearY: -0.2, PathTwist: 0.5, PathTwistBegin: -0.25, PathRadiusOffset: 0.3,
        PathTaperX: 0.4, PathTaperY: -0.4, PathRevolutions: 2.5, PathSkew: 0.2,
      },
    });
    expect(result.shapeParams).toMatchObject({
      pathShearX: 0.1, pathShearY: -0.2, pathTwist: 0.5, pathTwistBegin: -0.25, pathRadiusOffset: 0.3,
      pathTaperX: 0.4, pathTaperY: -0.4, pathRevolutions: 2.5, pathSkew: 0.2,
    });
  });

  it('keeps uploaded mesh identity and gives it a visible renderer proxy', () => {
    const result = serializeObject({
      localID: 10,
      object: {
        FullID: { toString: () => 'object-id' }, PCode: 9,
        extraParams: { meshData: { meshData: { toString: () => 'mesh-asset-id' }, type: 5 } },
      },
    });

    expect(result).toMatchObject({
      shape: 'asset-proxy', assetKind: 'mesh', assetId: 'mesh-asset-id',
    });
    expect(() => structuredClone(result)).not.toThrow();
  });

  it('preserves per-material texture entries received with UDP object updates', () => {
    const texture = (id: string, red: number) => ({
      textureID: { toString: () => id }, rgba: { getRed: () => red, getGreen: () => .2, getBlue: () => .3, getAlpha: () => 1 },
      repeatU: 2, repeatV: 3, offsetU: .1, offsetV: .2, rotation: .5, fullBright: true,
    });
    const faces = [texture('face-zero', .4), texture('face-one', .8)];
    const result = serializeObject({
      localID: 11,
      object: {
        FullID: { toString: () => 'multi-material-mesh' }, PCode: 9,
        TextureEntry: { faces, defaultTexture: faces[0], getEffectiveEntryForFace: (index: number) => faces[index] },
      },
    });

    expect(result.faceTextures).toEqual([
      expect.objectContaining({ textureId: 'face-zero', color: [.4, .2, .3, 1], repeat: [2, 3] }),
      expect.objectContaining({ textureId: 'face-one', color: [.8, .2, .3, 1], offset: [.1, .2], rotation: .5, fullBright: true }),
    ]);
  });

  it('identifies modern PBR materials and mirror reflection probes from UDP extra params', () => {
    const result = serializeObject({
      localID: 12,
      object: {
        FullID: { toString: () => 'mirror-id' }, PCode: 9,
        extraParams: {
          renderMaterialData: { params: [{ textureIndex: 0, textureUUID: { toString: () => 'material-id' } }] },
          reflectionProbeData: { ambiance: .75, clipDistance: .1, flags: 0x07 },
        },
        TextureEntry: { faces: [{}], getEffectiveEntryForFace: () => ({}) },
      },
    });

    expect(result.faceTextures[0].materialId).toBe('material-id');
    expect(result.reflectionProbe).toMatchObject({ ambiance: .75, clipDistance: .1, box: true, dynamic: true, mirror: true });
  });

  it('serializes scripted particle sources for the renderer', () => {
    const result = serializeObject({ localID: 13, object: {
      FullID: { toString: () => 'emitter' }, PCode: 9,
      Particles: {
        pattern: 2, maxAge: 10, burstRate: .2, burstRadius: 1, burstSpeedMin: 2, burstSpeedMax: 4, burstPartCount: 3,
        acceleration: { x: 0, y: 0, z: -1 }, target: { toString: () => 'target' }, texture: { toString: () => 'particle-texture' },
        dataFlags: 259, partMaxAge: 2, startColor: { getRed: () => 1, getGreen: () => .5, getBlue: () => 0, getAlpha: () => 1 },
        endColor: { red: 0, green: 0, blue: 1, alpha: 0 }, startScaleX: .5, startScaleY: 1, endScaleX: 2, endScaleY: 3,
      },
    } });
    expect(result.particles).toMatchObject({ pattern: 2, burstPartCount: 3, acceleration: [0, 0, -1], targetId: 'target', textureId: 'particle-texture', startColor: [1, .5, 0, 1], endScale: [2, 3] });
  });
});

describe('desktop session script dialogs and lures', () => {
  const { Bot } = require('@caspertech/node-metaverse');
  const { ViewerSession } = require('../../../core/viewer-session.cjs');
  const uuid = (value: string) => ({ toString: () => value });

  async function sessionAfterSubscribing() {
    const sent: Array<[string, any]> = [];
    const session = new ViewerSession((type: string, data: any) => sent.push([type, data]));
    const login = vi.spyOn(Bot.prototype, 'login').mockRejectedValue(new Error('stop after subscribing'));
    await expect(session.connect({ loginUrl: 'https://login.example/cgi-bin/login.cgi', username: 'Test Resident', password: 'secret' })).rejects.toThrow();
    login.mockRestore();
    return { session, sent };
  }

  it('forwards simulator script dialogs and lures to the renderer with answerable ids', async () => {
    const { session, sent } = await sessionAfterSubscribing();
    const events = session.bot.clientEvents;
    events.onScriptDialog.next({ ObjectID: uuid('obj'), FirstName: 'Pat', LastName: 'R', ObjectName: 'Door', Message: 'Open?', ChatChannel: 7, ImageID: uuid('img'), Buttons: ['Yes', 'No'], Owners: [] });
    events.onLure.next({ from: uuid('f'), fromName: 'Sam', lureMessage: 'Come', regionID: uuid('r'), position: { x: 1, y: 2, z: 3 }, gridX: 5, gridY: 6, lureID: uuid('lid') });

    expect(sent.map(([type]) => type)).toEqual(['script-dialog', 'lure']);
    expect(sent[0][1]).toMatchObject({ objectName: 'Door', ownerName: 'Pat R', buttons: ['Yes', 'No'], channel: 7, textBox: false });
    expect(sent[1][1]).toMatchObject({ fromName: 'Sam', position: [1, 2, 3], gridX: 5 });
    expect(() => structuredClone(sent[0][1])).not.toThrow();
    expect(session.pending.size).toBe(2);
  });

  it('answers a dialog and accepts a lure through the library, then forgets them', async () => {
    const { session, sent } = await sessionAfterSubscribing();
    const respondToScriptDialog = vi.fn().mockResolvedValue(undefined);
    const acceptTeleport = vi.fn().mockResolvedValue({ message: 'ok' });
    vi.spyOn(session.bot, 'clientCommands', 'get').mockReturnValue({ comms: { respondToScriptDialog }, teleport: { acceptTeleport } });
    const events = session.bot.clientEvents;
    events.onScriptDialog.next({ ObjectID: uuid('obj'), FirstName: '', LastName: '', ObjectName: 'Door', Message: '', ChatChannel: 7, ImageID: uuid('img'), Buttons: ['Yes', 'No'], Owners: [] });
    events.onLure.next({ from: uuid('f'), fromName: 'Sam', lureMessage: '', regionID: uuid('r'), position: { x: 1, y: 2, z: 3 }, gridX: 5, gridY: 6, lureID: uuid('lid') });

    await expect(session.respondScriptDialog({ id: sent[0][1].id, buttonIndex: 1 })).resolves.toEqual({ answered: true });
    expect(respondToScriptDialog.mock.calls[0][1]).toBe(1);
    await expect(session.acceptLure({ id: sent[1][1].id })).resolves.toMatchObject({ accepted: true });
    expect(acceptTeleport).toHaveBeenCalledTimes(1);
    expect(session.pending.size).toBe(0);
    await expect(session.respondScriptDialog({ id: sent[0][1].id, buttonIndex: 0 })).rejects.toThrow(/no longer pending/);
  });

  it('drops pending requests when the session closes', async () => {
    const { session, sent } = await sessionAfterSubscribing();
    session.bot.clientEvents.onLure.next({ from: uuid('f'), fromName: 'Sam', lureMessage: '', regionID: uuid('r'), position: { x: 1, y: 2, z: 3 }, gridX: 5, gridY: 6, lureID: uuid('lid') });
    expect(session.pending.size).toBe(1);
    await session.close();
    expect(session.pending.size).toBe(0);
    expect(sent).toHaveLength(1);
  });
});

describe('desktop session texture downloads', () => {
  const { AssetType } = require('@caspertech/node-metaverse');
  const { ViewerSession } = require('../../../core/viewer-session.cjs');

  it('uses the Second Life GetTexture capability instead of ViewerAsset', async () => {
    const session = new ViewerSession(() => undefined);
    const requestGet = vi.fn().mockResolvedValue({ body: Buffer.from('texture') });
    const downloadAsset = vi.fn();
    session.bot = {
      currentRegion: { caps: { getCapability: vi.fn().mockResolvedValue('https://asset.example/get?token=one'), requestGet } },
      clientCommands: { asset: { downloadAsset } },
    };

    await expect(session.downloadTexture('texture id')).resolves.toEqual(Buffer.from('texture'));
    expect(requestGet).toHaveBeenCalledWith('https://asset.example/get?token=one&texture_id=texture%20id');
    expect(downloadAsset).not.toHaveBeenCalled();
  });

  it('falls back to the generic asset service when GetTexture is unavailable', async () => {
    const session = new ViewerSession(() => undefined);
    const downloaded = Buffer.from('fallback');
    const downloadAsset = vi.fn().mockResolvedValue(downloaded);
    session.bot = {
      currentRegion: { caps: { getCapability: vi.fn().mockResolvedValue(undefined), requestGet: vi.fn() } },
      clientCommands: { asset: { downloadAsset } },
    };

    await expect(session.downloadTexture('texture-id')).resolves.toBe(downloaded);
    expect(downloadAsset).toHaveBeenCalledWith(AssetType.Texture, 'texture-id');
  });

  it('allows transiently failed assets to retry instead of leaving their proxy forever', async () => {
    vi.useFakeTimers();
    vi.setSystemTime(10_000);
    const sent: Array<[string, any]> = [];
    const session = new ViewerSession((type: string, data: any) => sent.push([type, data]));
    session.bot = { clientCommands: { asset: { downloadAsset: vi.fn() } } };
    const download = vi.fn().mockRejectedValueOnce(new Error('capability still starting')).mockResolvedValue(Buffer.from('ok'));
    const ready = vi.fn();
    session.streamAsset('mesh:test', AssetType.Mesh, 'test', download, ready);
    await session.assetRequests.get('mesh:test');
    expect(session.assetRequests.has('mesh:test')).toBe(false);
    expect(sent.at(-1)?.[0]).toBe('asset-error');
    vi.advanceTimersByTime(5001);
    session.streamAsset('mesh:test', AssetType.Mesh, 'test', download, ready);
    await session.assetRequests.get('mesh:test');
    expect(download).toHaveBeenCalledTimes(2);
    expect(ready).toHaveBeenCalledWith(Buffer.from('ok'));
    vi.useRealTimers();
  });
});

describe('desktop session avatar movement', () => {
  const { ControlFlags } = require('@caspertech/node-metaverse');
  const { ViewerSession } = require('../../../core/viewer-session.cjs');

  it('sends SL agent controls and clears them when movement stops', () => {
    const session = new ViewerSession(() => undefined);
    const agent = { setControlFlag: vi.fn(), clearControlFlag: vi.fn(), sendAgentUpdate: vi.fn() };
    session.bot = { agent };
    expect(session.setMovement({ forward: 1, right: -1, run: true })).toEqual({ moving: true });
    expect(agent.setControlFlag).toHaveBeenCalledWith(ControlFlags.AGENT_CONTROL_AT_POS);
    expect(agent.setControlFlag).toHaveBeenCalledWith(ControlFlags.AGENT_CONTROL_LEFT_POS);
    expect(agent.setControlFlag).toHaveBeenCalledWith(ControlFlags.AGENT_CONTROL_FAST_AT);
    expect(agent.sendAgentUpdate).toHaveBeenCalledTimes(1);
    agent.setControlFlag.mockClear();
    expect(session.setMovement({})).toEqual({ moving: false });
    expect(agent.setControlFlag).not.toHaveBeenCalled();
    expect(agent.clearControlFlag).toHaveBeenCalledWith(ControlFlags.AGENT_CONTROL_AT_POS);
    expect(agent.sendAgentUpdate).toHaveBeenCalledTimes(2);
  });

  it('tolerates node-metaverse throwing while no current region exists', () => {
    const session = new ViewerSession(() => undefined);
    session.bot = Object.defineProperty({}, 'currentRegion', { get: () => { throw new Error('currentRegion is undefined'); } });
    expect(session.currentRegion()).toBeNull();
    expect(session.getSceneObjects()).toEqual([]);
    expect(session.getDiagnostics()).toMatchObject({ connected: true, regionName: '' });
  });
});
