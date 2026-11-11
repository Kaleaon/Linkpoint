export class RegionFlags {
  static readonly ALLOW_YOURSELF = 0x00000001;
  static readonly ALLOW_OTHER_SCRIPTS = 0x00000002;
  static readonly ALLOW_PHYSICS = 0x00000004;
  static readonly ALLOW_DAMAGE = 0x00000008;
  static readonly BLOCK_FLY = 0x00000010;
  static readonly ALLOW_DIRECT_TELEPORT = 0x00000020;
  static readonly RESTRICT_PUSHOBJECT = 0x00000040;
  static readonly RESTRICTED_PARCEL = 0x00000080;
  static readonly VOICE_ENABLED = 0x00000100;
  static readonly BLOCK_DIRT_ENCODING = 0x00000200;
  static readonly ALLOW_TERRAIN_EDIT = 0x00000400;

  static hasFlag(flags: number, flag: number): boolean {
    return (flags & flag) !== 0;
  }
}

export class ObjectFlags {
  static readonly NONE = 0x00000000;
  static readonly USE_PHYSICS = 0x00000001;
  static readonly CREATE_SELECTED = 0x00000002;
  static readonly SCRIPTED = 0x00000004;
  static readonly TOUCH = 0x00000008;
  static readonly TEMPORARY = 0x00000010;
  static readonly PHANTOM = 0x00000020;
  static readonly INVENTORY_EMPTY = 0x00000040;
  static readonly CHARACTER = 0x00000080;
  static readonly JOINT_HINGE = 0x00000100;
  static readonly JOINT_POINT = 0x00000200;
  static readonly JOINT_BALL = 0x00000400;
  static readonly TEMPORARY_ON_REZ = 0x00000800;
  static readonly ALLOW_INVENTORY_DROP = 0x00001000;
  static readonly ANIMO = 0x00002000;
  static readonly CAST_SHADOWS = 0x00004000;

  static hasFlag(flags: number, flag: number): boolean {
    return (flags & flag) !== 0;
  }
}

export interface RegionHandshakeData {
  regionFlags: number;
  simAccess: number;
  simName: string;
  simOwnerId: Uint8Array;
}

export class RegionHandshakeDecoder {
  static decode(bytes: Uint8Array): RegionHandshakeData | null {
    if (bytes.length < 22) {
      return null;
    }

    const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
    const regionFlags = view.getUint32(0, true); // Little-endian
    const simAccess = bytes[4];

    let offset = 5;
    const simNameBytes: number[] = [];
    while (offset < bytes.length && bytes[offset] !== 0) {
      simNameBytes.push(bytes[offset++]);
    }

    if (offset < bytes.length && bytes[offset] === 0) {
      offset++; // skip null terminator
    }

    if (bytes.length < offset + 16) {
      return null;
    }

    const simName = String.fromCharCode(...simNameBytes);
    const simOwnerId = bytes.slice(offset, offset + 16);

    return {
      regionFlags,
      simAccess,
      simName,
      simOwnerId,
    };
  }
}

export interface ObjectUpdateData {
  localId: number;
  state: number;
  fullId: Uint8Array;
  pCode: number;
  material: number;
  clickAction: number;
  scale: [number, number, number];
  position: [number, number, number];
  rotation: [number, number, number, number];
  flags: number;
}

export class ObjectUpdateDecoder {
  static decode(bytes: Uint8Array): ObjectUpdateData | null {
    if (bytes.length < 68) {
      return null;
    }

    const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
    const localId = view.getUint32(0, true);
    const state = bytes[4];
    const fullId = bytes.slice(5, 21);
    const pCode = bytes[21];
    const material = bytes[22];
    const clickAction = bytes[23];

    const scale: [number, number, number] = [
      view.getFloat32(24, true),
      view.getFloat32(28, true),
      view.getFloat32(32, true),
    ];

    const position: [number, number, number] = [
      view.getFloat32(36, true),
      view.getFloat32(40, true),
      view.getFloat32(44, true),
    ];

    const rotation: [number, number, number, number] = [
      view.getFloat32(48, true),
      view.getFloat32(52, true),
      view.getFloat32(56, true),
      view.getFloat32(60, true),
    ];

    const flags = view.getUint32(64, true);

    return {
      localId,
      state,
      fullId,
      pCode,
      material,
      clickAction,
      scale,
      position,
      rotation,
      flags,
    };
  }
}
