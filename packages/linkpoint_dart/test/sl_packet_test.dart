import 'dart:typed_data';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('SL Packet Codec Tests', () {
    test('Bitmasks match canonical specification', () {
      expect(SLPacketFlags.zerocoded, 0x80);
      expect(SLPacketFlags.reliable, 0x40);
      expect(SLPacketFlags.resent, 0x20);
      expect(SLPacketFlags.appendedAcks, 0x10);
    });

    test('Zero decompress expands zero runs', () {
      // Header (6 bytes) + 0x00 + 0x03 (3 zeros) + 0x01
      final compressed = Uint8List.fromList(
          [0x80, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0x01]);
      final decompressed =
          SLPacketCodec.zeroDecompress(compressed, headerSize: 6);
      expect(
          decompressed,
          Uint8List.fromList(
              [0x80, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01]));
    });

    test('Header parsing parses flags, sequence, messageId, extraBytes', () {
      final raw =
          Uint8List.fromList([0x40, 0x00, 0x00, 0x00, 0x05, 0x00, 0x10]);
      final header = SLPacketCodec.parseHeader(raw);
      expect(header.isReliable, true);
      expect(header.isZerocoded, false);
      expect(header.sequenceNumber, 5);
      expect(header.messageId, 16);
    });
  });
}
