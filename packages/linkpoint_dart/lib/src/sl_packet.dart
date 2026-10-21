import 'dart:typed_data';

/// Second Life Packet Flags
class SLPacketFlags {
  static const int zerocoded = 0x80;
  static const int reliable = 0x40;
  static const int resent = 0x20;
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

  bool get isZerocoded => (flags & SLPacketFlags.zerocoded) != 0;
  bool get isReliable => (flags & SLPacketFlags.reliable) != 0;
  bool get isResent => (flags & SLPacketFlags.resent) != 0;
  bool get hasAppendedAcks => (flags & SLPacketFlags.appendedAcks) != 0;
}

/// Second Life Packet Codec and Zero-Coding Decompressor
class SLPacketCodec {
  /// Decompresses zero-coded SL packet bytes.
  static Uint8List zeroDecompress(Uint8List src, {int headerSize = 6}) {
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
    if (packetBytes.length < 6) {
      throw FormatException('Packet length too short for SL header');
    }
    final bd = ByteData.sublistView(packetBytes);
    final flags = packetBytes[0];
    final seq = bd.getUint32(1, Endian.big);
    final extra = packetBytes[5];

    var offset = 6 + extra;
    var msgId = 0;

    if (offset < packetBytes.length) {
      final first = packetBytes[offset];
      if (first != 0xFF) {
        msgId = first;
        offset += 1;
      } else if (offset + 1 < packetBytes.length && packetBytes[offset + 1] != 0xFF) {
        final second = packetBytes[offset + 1];
        msgId = (0xFF00) | second;
        offset += 2;
      } else if (offset + 3 < packetBytes.length) {
        msgId = bd.getUint32(offset, Endian.big);
        offset += 4;
      }
    }

    final acks = <int>[];
    if ((flags & SLPacketFlags.appendedAcks) != 0 && packetBytes.isNotEmpty) {
      final ackCount = packetBytes.last;
      final ackStart = packetBytes.length - 1 - (ackCount * 4);
      if (ackStart >= offset) {
        for (var i = 0; i < ackCount; i++) {
          final pos = ackStart + (i * 4);
          acks.add(bd.getUint32(pos, Endian.big));
        }
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
