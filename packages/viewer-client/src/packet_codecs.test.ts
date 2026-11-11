import { describe, expect, test } from "vitest";
import {
  SLPacketFlags,
  SLPacketFrequency,
  SLPacketHeader,
  zeroDecode,
  zeroEncode,
  splitAppendedAcks,
  appendAcks,
} from "./sl_packet";
import { Vector3U16, Vector3U8, PackedQuaternion } from "./spatial_codecs";
import { RegionFlags, ObjectFlags, RegionHandshakeDecoder, ObjectUpdateDecoder } from "./protocol_decoders";

describe("SLPacketHeader & Packet Framing", () => {
  test("header encoding & decoding for all frequencies", () => {
    const header = new SLPacketHeader(
      { zerocoded: true, reliable: true, resent: false, appendedAcks: false },
      100,
      new Uint8Array([]),
      SLPacketFrequency.High,
      4
    );

    const bytes = header.encode();
    const decoded = SLPacketHeader.decode(bytes);

    expect(decoded.header.flags.zerocoded).toBe(true);
    expect(decoded.header.sequence).toBe(100);
    expect(decoded.header.frequency).toBe(SLPacketFrequency.High);
    expect(decoded.header.messageId).toBe(4);
  });

  test("zero coding roundtrip and expansion limits", () => {
    const raw = new Uint8Array([1, 0, 0, 0, 2]);
    const encoded = zeroEncode(raw);
    const decoded = zeroDecode(encoded, 100);
    expect(Array.from(decoded)).toEqual([1, 0, 0, 0, 2]);

    expect(() => zeroDecode(encoded, 3)).toThrow();
  });

  test("appended ACKs trailer roundtrip", () => {
    const packet = new Uint8Array([1, 2, 3]);
    const withAcks = appendAcks(packet, [7, 9]);
    const { payload, acks } = splitAppendedAcks(withAcks);

    expect(Array.from(payload)).toEqual([1, 2, 3]);
    expect(acks).toEqual([7, 9]);
  });
});

describe("Spatial Codecs", () => {
  test("Vector3U16 dequantization", () => {
    const zero = Vector3U16.dequantize([0, 0, 0]);
    expect(zero).toEqual([-128, -128, -128]);

    const mid = Vector3U16.dequantize([32767, 32767, 32767]);
    expect(mid).toEqual([0, 0, 0]);

    const max = Vector3U16.dequantize([65535, 65535, 65535]);
    expect(max).toEqual([128, 128, 128]);
  });

  test("Vector3U8 dequantization", () => {
    const zero = Vector3U8.dequantize([0, 0, 0]);
    expect(zero).toEqual([0, 0, 0]);

    const max = Vector3U8.dequantize([255, 255, 255]);
    expect(max).toEqual([255, 255, 255]);
  });

  test("16-bit PackedQuaternion", () => {
    const identity = PackedQuaternion.unpack16([0, 0, 0]);
    expect(identity).toEqual([0, 0, 0, 1]);

    const rot = PackedQuaternion.unpack16([0, 0, 23170]);
    expect(rot).toEqual([0, 0, 0.7071, 0.7071]);
  });
});

describe("Protocol Message Decoders", () => {
  test("RegionHandshakeDecoder", () => {
    const bytes = new Uint8Array([
      5, 0, 0, 0, // RegionFlags (5)
      21, // SimAccess (21)
      65, 104, 97, 114, 111, 110, 0, // SimName ("Aharon\0")
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // Owner UUID
    ]);

    const decoded = RegionHandshakeDecoder.decode(bytes);
    expect(decoded).not.toBeNull();
    expect(decoded!.regionFlags).toBe(5);
    expect(decoded!.simAccess).toBe(21);
    expect(decoded!.simName).toBe("Aharon");
    expect(RegionFlags.hasFlag(decoded!.regionFlags, RegionFlags.ALLOW_YOURSELF)).toBe(true);
    expect(RegionFlags.hasFlag(decoded!.regionFlags, RegionFlags.ALLOW_PHYSICS)).toBe(true);
  });
});
