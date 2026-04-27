/**
 * Linkpoint PWA - Second Life Message Types
 * Based on Linkpoint Android message implementation
 */

// Message IDs (from SLMessageFactory)
const MessageIDs = {
  // Agent Messages
  AGENT_UPDATE: 0x04,
  AGENT_ANIMATION: 0x14,
  COMPLETE_AGENT_MOVEMENT: 0x0F9,
  USE_CIRCUIT_CODE: 0xFFFF0003,
  
  // Chat Messages
  CHAT_FROM_SIMULATOR: 0x8B,
  CHAT_FROM_VIEWER: 0x50,
  IMPROVED_IM: 0x12,
  
  // Object Messages
  OBJECT_UPDATE: 0x0C,
  OBJECT_UPDATE_COMPRESSED: 0x0D,
  OBJECT_UPDATE_CACHED: 0x0E,
  KILL_OBJECT: 0x78,
  
  // Region Messages
  REGION_HANDSHAKE: 0x94,
  REGION_HANDSHAKE_REPLY: 0x95,
  
  // System Messages
  PACKET_ACK: 0xFB,
  START_PING_CHECK: 0x01,
  COMPLETE_PING_CHECK: 0x02,
  
  // Teleport Messages
  TELEPORT_REQUEST: 0x55,
  TELEPORT_PROGRESS: 0x56,
  TELEPORT_FINISH: 0x57,
  TELEPORT_FAILED: 0x58,
};

/**
 * LLVector3 - 3D Vector
 */
class LLVector3 {
  constructor(x = 0, y = 0, z = 0) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  pack(buffer) {
    const view = new DataView(buffer);
    let offset = 0;
    view.setFloat32(offset, this.x, false); offset += 4;
    view.setFloat32(offset, this.y, false); offset += 4;
    view.setFloat32(offset, this.z, false);
  }

  static unpack(buffer) {
    const view = new DataView(buffer);
    let offset = 0;
    const x = view.getFloat32(offset, false); offset += 4;
    const y = view.getFloat32(offset, false); offset += 4;
    const z = view.getFloat32(offset, false);
    return new LLVector3(x, y, z);
  }

  toArray() {
    return [this.x, this.y, this.z];
  }
}

/**
 * LLQuaternion - Rotation
 */
class LLQuaternion {
  constructor(x = 0, y = 0, z = 0, w = 1) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  pack(buffer) {
    const view = new DataView(buffer);
    let offset = 0;
    view.setFloat32(offset, this.x, false); offset += 4;
    view.setFloat32(offset, this.y, false); offset += 4;
    view.setFloat32(offset, this.z, false); offset += 4;
    view.setFloat32(offset, this.w, false);
  }

  static unpack(buffer) {
    const view = new DataView(buffer);
    let offset = 0;
    const x = view.getFloat32(offset, false); offset += 4;
    const y = view.getFloat32(offset, false); offset += 4;
    const z = view.getFloat32(offset, false); offset += 4;
    const w = view.getFloat32(offset, false);
    return new LLQuaternion(x, y, z, w);
  }

  toEuler() {
    // Convert quaternion to Euler angles (pitch, yaw, roll)
    const sinr_cosp = 2 * (this.w * this.x + this.y * this.z);
    const cosr_cosp = 1 - 2 * (this.x * this.x + this.y * this.y);
    const roll = Math.atan2(sinr_cosp, cosr_cosp);

    const sinp = 2 * (this.w * this.y - this.z * this.x);
    const pitch = Math.abs(sinp) >= 1 ? Math.sign(sinp) * Math.PI / 2 : Math.asin(sinp);

    const siny_cosp = 2 * (this.w * this.z + this.x * this.y);
    const cosy_cosp = 1 - 2 * (this.y * this.y + this.z * this.z);
    const yaw = Math.atan2(siny_cosp, cosy_cosp);

    return { pitch, yaw, roll };
  }
}

/**
 * Base SL Message class
 */
class SLMessage {
  constructor() {
    this.isReliable = false;
    this.isResent = false;
    this.zeroCoded = false;
    this.seqNum = 0;
    this.retries = 0;
    this.sentTimeMillis = 0;
  }

  getMessageID() {
    throw new Error('getMessageID() must be implemented');
  }

  getMessageName() {
    throw new Error('getMessageName() must be implemented');
  }

  packPayload(buffer) {
    throw new Error('packPayload() must be implemented');
  }

  unpackPayload(buffer) {
    throw new Error('unpackPayload() must be implemented');
  }
}

/**
 * ChatFromSimulator message
 */
class ChatFromSimulatorMessage extends SLMessage {
  constructor() {
    super();
    this.fromName = '';
    this.sourceId = '00000000-0000-0000-0000-000000000000';
    this.ownerId = '00000000-0000-0000-0000-000000000000';
    this.sourceType = 0;
    this.chatType = 0;
    this.audible = 0;
    this.position = new LLVector3();
    this.message = '';
  }

  getMessageID() {
    return MessageIDs.CHAT_FROM_SIMULATOR;
  }

  getMessageName() {
    return 'ChatFromSimulator';
  }

  unpackPayload(buffer) {
    const view = new DataView(buffer);
    let offset = 0;

    // From name (variable length)
    const nameLength = view.getUint8(offset++);
    const nameBytes = new Uint8Array(buffer, offset, nameLength);
    this.fromName = new TextDecoder().decode(nameBytes);
    offset += nameLength;

    // Source ID (UUID - 16 bytes)
    const sourceMsb = view.getBigUint64(offset, false); offset += 8;
    const sourceLsb = view.getBigUint64(offset, false); offset += 8;
    this.sourceId = this.uuidFromBigInts(sourceMsb, sourceLsb);

    // Owner ID
    const ownerMsb = view.getBigUint64(offset, false); offset += 8;
    const ownerLsb = view.getBigUint64(offset, false); offset += 8;
    this.ownerId = this.uuidFromBigInts(ownerMsb, ownerLsb);

    // Chat data
    this.sourceType = view.getUint8(offset++);
    this.chatType = view.getUint8(offset++);
    this.audible = view.getUint8(offset++);

    // Position
    this.position = LLVector3.unpack(buffer.slice(offset));
    offset += 12; // 3 floats

    // Message (variable length)
    const messageLength = view.getUint16(offset, false); offset += 2;
    const messageBytes = new Uint8Array(buffer, offset, messageLength);
    this.message = new TextDecoder().decode(messageBytes);
  }

  uuidFromBigInts(msb, lsb) {
    const hex = (n) => n.toString(16).padStart(16, '0');
    const msbHex = hex(msb);
    const lsbHex = hex(lsb);
    return `${msbHex.slice(0,8)}-${msbHex.slice(8,12)}-${msbHex.slice(12,16)}-${lsbHex.slice(0,4)}-${lsbHex.slice(4)}`;
  }
}

/**
 * ChatFromViewer message
 */
class ChatFromViewerMessage extends SLMessage {
  constructor(agentId, sessionId, message, type = 1, channel = 0) {
    super();
    this.agentId = agentId;
    this.sessionId = sessionId;
    this.message = message;
    this.type = type;
    this.channel = channel;
    this.isReliable = true;
  }

  getMessageID() {
    return MessageIDs.CHAT_FROM_VIEWER;
  }

  getMessageName() {
    return 'ChatFromViewer';
  }

  packPayload(buffer) {
    // TODO: Implement message packing
    // This would be used to send chat to simulator
  }
}

/**
 * ObjectUpdate message
 */
class ObjectUpdateMessage extends SLMessage {
  constructor() {
    super();
    this.regionHandle = 0n;
    this.timeDilation = 0;
    this.objectList = [];
  }

  getMessageID() {
    return MessageIDs.OBJECT_UPDATE;
  }

  getMessageName() {
    return 'ObjectUpdate';
  }

  unpackPayload(buffer) {
    const view = new DataView(buffer);
    let offset = 0;

    this.regionHandle = view.getBigUint64(offset, false); offset += 8;
    this.timeDilation = view.getUint16(offset, false); offset += 2;

    const objectCount = view.getUint8(offset++);
    this.objectList = [];

    for (let i = 0; i < objectCount; i++) {
      const obj = {};
      
      obj.id = view.getUint32(offset, false); offset += 4;
      obj.state = view.getUint8(offset++);
      
      // Full ID (UUID)
      const idMsb = view.getBigUint64(offset, false); offset += 8;
      const idLsb = view.getBigUint64(offset, false); offset += 8;
      obj.fullId = this.uuidFromBigInts(idMsb, idLsb);
      
      obj.crc = view.getUint32(offset, false); offset += 4;
      obj.pCode = view.getUint8(offset++);
      obj.material = view.getUint8(offset++);
      obj.clickAction = view.getUint8(offset++);
      
      // Scale
      obj.scale = LLVector3.unpack(buffer.slice(offset));
      offset += 12;
      
      // Object data (variable)
      const objectDataLength = view.getUint8(offset++);
      obj.objectData = new Uint8Array(buffer, offset, objectDataLength);
      offset += objectDataLength;
      
      // Continue unpacking other fields...
      obj.parentId = view.getUint32(offset, false); offset += 4;
      obj.updateFlags = view.getUint32(offset, false); offset += 4;
      
      this.objectList.push(obj);
    }
  }

  uuidFromBigInts(msb, lsb) {
    const hex = (n) => n.toString(16).padStart(16, '0');
    const msbHex = hex(msb);
    const lsbHex = hex(lsb);
    return `${msbHex.slice(0,8)}-${msbHex.slice(8,12)}-${msbHex.slice(12,16)}-${lsbHex.slice(0,4)}-${lsbHex.slice(4)}`;
  }
}

/**
 * AgentUpdate message
 */
class AgentUpdateMessage extends SLMessage {
  constructor(agentId, sessionId) {
    super();
    this.agentId = agentId;
    this.sessionId = sessionId;
    this.bodyRotation = new LLQuaternion();
    this.headRotation = new LLQuaternion();
    this.state = 0;
    this.cameraCenter = new LLVector3();
    this.cameraAtAxis = new LLVector3();
    this.cameraLeftAxis = new LLVector3();
    this.cameraUpAxis = new LLVector3();
    this.far = 128.0;
    this.controlFlags = 0;
    this.flags = 0;
    this.isReliable = false; // High frequency message
  }

  getMessageID() {
    return MessageIDs.AGENT_UPDATE;
  }

  getMessageName() {
    return 'AgentUpdate';
  }

  packPayload(buffer) {
    // TODO: Implement for sending position updates
  }
}

// Export all message types
window.SLMessageTypes = {
  MessageIDs,
  LLVector3,
  LLQuaternion,
  SLMessage,
  ChatFromSimulatorMessage,
  ChatFromViewerMessage,
  ObjectUpdateMessage,
  AgentUpdateMessage
};
