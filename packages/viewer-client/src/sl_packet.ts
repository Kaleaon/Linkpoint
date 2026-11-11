export const SLPacketFlags = {
  ZEROCODED: 0x80,
  RELIABLE: 0x40,
  RESENT: 0x20,
  APPENDED_ACKS: 0x10,
} as const;

export enum SLPacketFrequency {
  High = 0,
  Medium = 1,
  Low = 2,
  Fixed = 3,
}

export interface PacketFlags {
  zerocoded: boolean;
  reliable: boolean;
  resent: boolean;
  appendedAcks: boolean;
}

export class SLPacketHeader {
  flags: PacketFlags;
  sequence: number;
  extra: Uint8Array;
  frequency: SLPacketFrequency;
  messageId: number;

  constructor(
    flags: PacketFlags,
    sequence: number,
    extra: Uint8Array,
    frequency: SLPacketFrequency,
    messageId: number
  ) {
    this.flags = flags;
    this.sequence = sequence;
    this.extra = extra;
    this.frequency = frequency;
    this.messageId = messageId;
  }

  static decode(bytes: Uint8Array): { header: SLPacketHeader; offset: number } {
    if (bytes.length < 7) {
      throw new Error("Truncated packet header");
    }

    const rawFlags = bytes[0];
    const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
    const sequence = view.getUint32(1, false); // Big-endian
    const extraLen = bytes[5];

    let offset = 6 + extraLen;
    if (offset >= bytes.length) {
      throw new Error("Invalid extra header length");
    }

    const extra = bytes.slice(6, offset);

    const first = bytes[offset++];
    let frequency: SLPacketFrequency;
    let messageId: number;

    if (first !== 0xff) {
      frequency = SLPacketFrequency.High;
      messageId = first;
    } else {
      if (offset >= bytes.length) throw new Error("Truncated packet header");
      const second = bytes[offset++];
      if (second !== 0xff) {
        frequency = SLPacketFrequency.Medium;
        messageId = second;
      } else {
        if (offset >= bytes.length) throw new Error("Truncated packet header");
        const high = bytes[offset++];
        if (high === 0xff) {
          if (offset >= bytes.length) throw new Error("Truncated packet header");
          frequency = SLPacketFrequency.Fixed;
          messageId = bytes[offset++];
        } else {
          if (offset >= bytes.length) throw new Error("Truncated packet header");
          const low = bytes[offset++];
          frequency = SLPacketFrequency.Low;
          messageId = (high << 8) | low;
        }
      }
    }

    const flags: PacketFlags = {
      zerocoded: (rawFlags & SLPacketFlags.ZEROCODED) !== 0,
      reliable: (rawFlags & SLPacketFlags.RELIABLE) !== 0,
      resent: (rawFlags & SLPacketFlags.RESENT) !== 0,
      appendedAcks: (rawFlags & SLPacketFlags.APPENDED_ACKS) !== 0,
    };

    return {
      header: new SLPacketHeader(flags, sequence, extra, frequency, messageId),
      offset,
    };
  }

  encode(): Uint8Array {
    let rawFlags = 0;
    if (this.flags.zerocoded) rawFlags |= SLPacketFlags.ZEROCODED;
    if (this.flags.reliable) rawFlags |= SLPacketFlags.RELIABLE;
    if (this.flags.resent) rawFlags |= SLPacketFlags.RESENT;
    if (this.flags.appendedAcks) rawFlags |= SLPacketFlags.APPENDED_ACKS;

    const extraLen = this.extra.length;
    let msgIdLen = 1;
    if (this.frequency === SLPacketFrequency.Medium) msgIdLen = 2;
    else if (this.frequency === SLPacketFrequency.Low) msgIdLen = 4;
    else if (this.frequency === SLPacketFrequency.Fixed) msgIdLen = 4;

    const totalLen = 6 + extraLen + msgIdLen;
    const out = new Uint8Array(totalLen);
    const view = new DataView(out.buffer);

    out[0] = rawFlags;
    view.setUint32(1, this.sequence, false);
    out[5] = extraLen;

    if (extraLen > 0) {
      out.set(this.extra, 6);
    }

    let offset = 6 + extraLen;
    switch (this.frequency) {
      case SLPacketFrequency.High:
        out[offset] = this.messageId & 0xff;
        break;
      case SLPacketFrequency.Medium:
        out[offset] = 0xff;
        out[offset + 1] = this.messageId & 0xff;
        break;
      case SLPacketFrequency.Low:
        out[offset] = 0xff;
        out[offset + 1] = 0xff;
        out[offset + 2] = (this.messageId >> 8) & 0xff;
        out[offset + 3] = this.messageId & 0xff;
        break;
      case SLPacketFrequency.Fixed:
        out[offset] = 0xff;
        out[offset + 1] = 0xff;
        out[offset + 2] = 0xff;
        out[offset + 3] = this.messageId & 0xff;
        break;
    }

    return out;
  }
}

export function zeroDecode(bytes: Uint8Array, limit: number): Uint8Array {
  const output: number[] = [];
  let offset = 0;

  while (offset < bytes.length) {
    const byte = bytes[offset++];
    if (byte === 0) {
      if (offset >= bytes.length) {
        throw new Error("Invalid zero code encoding");
      }
      const count = bytes[offset++];
      if (count === 0 || output.length + count > limit) {
        throw new Error("Invalid zero code length or expansion limit exceeded");
      }
      for (let i = 0; i < count; i++) {
        output.push(0);
      }
    } else {
      if (output.length >= limit) {
        throw new Error("Expansion limit exceeded");
      }
      output.push(byte);
    }
  }

  return new Uint8Array(output);
}

export function zeroEncode(bytes: Uint8Array): Uint8Array {
  const output: number[] = [];
  let offset = 0;

  while (offset < bytes.length) {
    if (bytes[offset] !== 0) {
      output.push(bytes[offset++]);
      continue;
    }

    const start = offset;
    while (offset < bytes.length && bytes[offset] === 0 && offset - start < 255) {
      offset++;
    }
    const count = offset - start;
    output.push(0, count);
  }

  return new Uint8Array(output);
}

export function splitAppendedAcks(bytes: Uint8Array): { payload: Uint8Array; acks: number[] } {
  if (bytes.length < 1) {
    throw new Error("Invalid appended ACKs: empty payload");
  }

  const count = bytes[bytes.length - 1];
  const trailerLen = count * 4 + 1;

  if (bytes.length < trailerLen) {
    throw new Error("Invalid appended ACKs trailer length");
  }

  const payloadLen = bytes.length - trailerLen;
  const payload = bytes.slice(0, payloadLen);
  const acks: number[] = [];

  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  for (let i = 0; i < count; i++) {
    const ackOffset = payloadLen + i * 4;
    acks.push(view.getUint32(ackOffset, false)); // Big-endian
  }

  return { payload, acks };
}

export function appendAcks(packet: Uint8Array, acks: number[]): Uint8Array {
  if (acks.length > 255) {
    throw new Error("Cannot append more than 255 ACKs");
  }

  const out = new Uint8Array(packet.length + acks.length * 4 + 1);
  out.set(packet, 0);

  const view = new DataView(out.buffer);
  let offset = packet.length;

  for (const ack of acks) {
    view.setUint32(offset, ack, false); // Big-endian
    offset += 4;
  }

  out[offset] = acks.length;
  return out;
}
