import 'dart:typed_data';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('SL Packet Codec Tests', () {
    test('Flag constants match protocol standards', () {
      expect(SLPacketFlags.zerocoded, 0x80);
      expect(SLPacketFlags.reliable, 0x40);
      expect(SLPacketFlags.resent, 0x20);
      expect(SLPacketFlags.appendedAcks, 0x10);
    });

    test('Zero decompress expands zero runs after header', () {
      final compressed = Uint8List.fromList(
          [0x80, 0x00, 0x00, 0x00, 0x01, 0x00, 0x04, 0x00, 0x02, 0x01]);
      final decompressed =
          SLPacketCodec.zeroDecompress(compressed, headerSize: 6);
      expect(
          decompressed,
          Uint8List.fromList(
              [0x80, 0x00, 0x00, 0x00, 0x01, 0x00, 0x04, 0x00, 0x00, 0x01]));
    });

    test('Header parsing parses extra bytes, message IDs, and appended ACKs',
        () {
      // High frequency message ID
      final highFreqRaw = Uint8List.fromList([
        0x80, // Zerocoded flag
        0x00, 0x00, 0x00, 0x64, // Sequence = 100
        0x00, // Extra len = 0
        0x04, // High freq MsgID = 4
        0x01, 0x02
      ]);
      final header1 = SLPacketCodec.parseHeader(highFreqRaw);
      expect(header1.isZerocoded, true);
      expect(header1.sequenceNumber, 100);
      expect(header1.frequency, SLPacketFrequency.high);
      expect(header1.messageId, 4);

      // Low frequency message ID with appended ACKs
      final lowFreqRaw = Uint8List.fromList([
        0x10, // Appended ACKs flag
        0x00, 0x00, 0x00, 0x66, // Sequence = 102
        0x00, // Extra len = 0
        0xFF, 0xFF, 0x12, 0x34, // Low freq MsgID = 0x1234
        0x11, 0x22, 0x33, 0x44, // Payload
        0x00, 0x00, 0x00, 0x07, // ACK 7
        0x00, 0x00, 0x00, 0x09, // ACK 9
        0x02 // Ack count = 2
      ]);
      final header2 = SLPacketCodec.parseHeader(lowFreqRaw);
      expect(header2.hasAppendedAcks, true);
      expect(header2.sequenceNumber, 102);
      expect(header2.frequency, SLPacketFrequency.low);
      expect(header2.messageId, 0x1234);
      expect(header2.acks, [7, 9]);
    });
  });
}
