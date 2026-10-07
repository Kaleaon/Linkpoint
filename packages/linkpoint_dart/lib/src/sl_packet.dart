import 'dart:typed_data';

/// Second Life Packet Flags
class SLPacketFlags {
  static const int reliable = 0x40;
  static const int resent = 0x80;
  static const int zerocoded = 0x20;
  static const int appendedAcks = 0x10;
}

/// Second Life UDP Packet Header
class SLPacketHeader {
  final int flags;
  final int sequenceNumber;
  final int messageId;
  final List<int> acks;

  const SLPacketHeader({
    required this.flags,
    required this.sequenceNumber,
    required this.messageId,
    required this.acks,
  });

  bool get isReliable => (flags & SLPacketFlags.reliable) != 0;
  bool get isResent => (flags & SLPacketFlags.resent) != 0;
  bool get isZerocoded => (flags & SLPacketFlags.zerocoded) != 0;
  bool get hasAppendedAcks => (flags & SLPacketFlags.appendedAcks) != 0;
}

/// Second Life Packet Codec and Zero-Coding Decompressor
class SLPacketCodec {
  /// Decompresses zero-coded SL packet bytes.
  static Uint8List zeroDecompress(Uint8List src, {int headerSize = 4}) {
    if (src.length <= headerSize) return Uint8List.fromList(src);

    final out = <int>[];
    out.addAll(src.sublist(0, headerSize));

    var i = headerSize;
    while (i < src.length) {
      final b = src[i];
      if (b == 0) {
        if (i + 1 < src.length) {
          final zeroCount = src[i + 1];
          out.addAll(List.filled(zeroCount, 0));
          i += 2;
        } else {
          out.add(0);
          i++;
        }
      } else {
        out.add(b);
        i++;
      }
    }
    return Uint8List.fromList(out);
  }

  /// Parses SL UDP packet header from bytes.
  static SLPacketHeader parseHeader(Uint8List packetBytes) {
    if (packetBytes.length < 4) {
      throw FormatException('Packet length too short for SL header');
    }
    final flags = packetBytes[0];
    final seq = (packetBytes[1] << 24) |
        (packetBytes[2] << 16) |
        (packetBytes[3] << 8) |
        (packetBytes.length > 4 ? packetBytes[4] : 0);
    final msgId = packetBytes.length > 5 ? packetBytes[5] : 0;

    final acks = <int>[];
    if ((flags & SLPacketFlags.appendedAcks) != 0) {
      final ackCount = packetBytes.last;
      for (var i = 0; i < ackCount; i++) {
        acks.add(i + 1);
      }
    }

    return SLPacketHeader(
      flags: flags,
      sequenceNumber: seq,
      messageId: msgId,
      acks: acks,
    );
  }
}
