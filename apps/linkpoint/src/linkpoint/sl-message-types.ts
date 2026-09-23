/**
 * Linkpoint PWA - Second Life Message Types
 */

export const MessageIDs = {
  AGENT_UPDATE: 0x04,
  AGENT_ANIMATION: 0x14,
  COMPLETE_AGENT_MOVEMENT: 0x0F9,
  USE_CIRCUIT_CODE: 0xFFFF0003,
  CHAT_FROM_SIMULATOR: 0x8B,
  CHAT_FROM_VIEWER: 0x50,
  IMPROVED_IM: 0x12,
  OBJECT_UPDATE: 0x0C,
  OBJECT_UPDATE_COMPRESSED: 0x0D,
  OBJECT_UPDATE_CACHED: 0x0E,
  KILL_OBJECT: 0x78,
  REGION_HANDSHAKE: 0x94,
  REGION_HANDSHAKE_REPLY: 0x95,
  PACKET_ACK: 0xFB,
  START_PING_CHECK: 0x01,
  COMPLETE_PING_CHECK: 0x02,
  TELEPORT_REQUEST: 0x55,
  TELEPORT_PROGRESS: 0x56,
  TELEPORT_FINISH: 0x57,
  TELEPORT_FAILED: 0x58,
};

export class LLVector3 {
  constructor(public x: number = 0, public y: number = 0, public z: number = 0) {}

  static unpack(buffer: ArrayBuffer): LLVector3 {
    const view = new DataView(buffer);
    return new LLVector3(
      view.getFloat32(0, false),
      view.getFloat32(4, false),
      view.getFloat32(8, false)
    );
  }

  toArray(): number[] {
    return [this.x, this.y, this.z];
  }
}

export class LLQuaternion {
  constructor(public x: number = 0, public y: number = 0, public z: number = 0, public w: number = 1) {}

  static unpack(buffer: ArrayBuffer): LLQuaternion {
    const view = new DataView(buffer);
    return new LLQuaternion(
      view.getFloat32(0, false),
      view.getFloat32(4, false),
      view.getFloat32(8, false),
      view.getFloat32(12, false)
    );
  }
}

export abstract class SLMessage {
  public isReliable: boolean = false;
  public seqNum: number = 0;
  abstract getMessageID(): number;
  abstract getMessageName(): string;
}

export class ChatFromSimulatorMessage extends SLMessage {
  public fromName: string = '';
  public sourceId: string = '';
  public ownerId: string = '';
  public sourceType: number = 0;
  public chatType: number = 0;
  public audible: number = 0;
  public position: LLVector3 = new LLVector3();
  public message: string = '';

  getMessageID() { return MessageIDs.CHAT_FROM_SIMULATOR; }
  getMessageName() { return 'ChatFromSimulator'; }

  unpackPayload(buffer: ArrayBuffer) {
    const view = new DataView(buffer);
    let offset = 0;

    const nameLength = view.getUint8(offset++);
    const nameBytes = new Uint8Array(buffer, offset, nameLength);
    this.fromName = new TextDecoder().decode(nameBytes);
    offset += nameLength;

    // UUIDs (16 bytes)
    offset += 16; // sourceId
    offset += 16; // ownerId

    this.sourceType = view.getUint8(offset++);
    this.chatType = view.getUint8(offset++);
    this.audible = view.getUint8(offset++);

    this.position = LLVector3.unpack(buffer.slice(offset));
    offset += 12;

    const messageLength = view.getUint16(offset, false); offset += 2;
    const messageBytes = new Uint8Array(buffer, offset, messageLength);
    this.message = new TextDecoder().decode(messageBytes);
  }
}

export class ChatFromViewerMessage extends SLMessage {
  constructor(public agentId: string, public sessionId: string, public message: string, public type: number = 1, public channel: number = 0) {
    super();
    this.isReliable = true;
  }

  getMessageID() { return MessageIDs.CHAT_FROM_VIEWER; }
  getMessageName() { return 'ChatFromViewer'; }
}
