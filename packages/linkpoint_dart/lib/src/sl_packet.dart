import 'dart:typed_data';

/// Second Life Packet Flags matching protocol standards
class SLPacketFlags {
  static const int zerocoded = 0x80;
  static const int reliable = 0x40;
  static const int resent = 0x20;
  static const int appendedAcks = 0x10;
}

enum SLPacketFrequency { high, medium, low, fixed }

/// Second Life UDP Packet Header
class SLPacketHeader {
  final int flags;
  final int sequenceNumber;
  final int messageId;
  final SLPacketFrequency frequency;
  final Uint8List extra;
  final List<int> acks;
  final int bodyOffset;

  const SLPacketHeader({
    required this.flags,
    required this.sequenceNumber,
    required this.messageId,
    required this.frequency,
    required this.extra,
    required this.acks,
    required this.bodyOffset,
  });

  bool get isReliable => (flags & SLPacketFlags.reliable) != 0;
  bool get isResent => (flags & SLPacketFlags.resent) != 0;
  bool get isZerocoded => (flags & SLPacketFlags.zerocoded) != 0;
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
    if (packetBytes.length < 7) {
      throw FormatException('Packet length too short for SL header');
    }
    final flags = packetBytes[0];
    final seq = ((packetBytes[1] & 0xFF) << 24) |
        ((packetBytes[2] & 0xFF) << 16) |
        ((packetBytes[3] & 0xFF) << 8) |
        (packetBytes[4] & 0xFF);

    final extraLen = packetBytes[5] & 0xFF;
    var offset = 6 + extraLen;
    if (offset >= packetBytes.length) {
      throw FormatException('Extra header offset exceeds packet length');
    }

    final extra = packetBytes.sublist(6, offset);

    final first = packetBytes[offset++];
    SLPacketFrequency freq;
    int msgId;

    if (first != 0xFF) {
      freq = SLPacketFrequency.high;
      msgId = first;
    } else {
      if (offset >= packetBytes.length) {
        throw FormatException(
            'Truncated packet header while reading message ID');
      }
      final second = packetBytes[offset++];
      if (second != 0xFF) {
        freq = SLPacketFrequency.medium;
        msgId = second;
      } else {
        if (offset >= packetBytes.length) {
          throw FormatException(
              'Truncated packet header while reading low/fixed frequency message ID');
        }
        final high = packetBytes[offset++];
        if (high == 0xFF) {
          if (offset >= packetBytes.length) {
            throw FormatException(
                'Truncated packet header while reading fixed frequency message ID');
          }
          freq = SLPacketFrequency.fixed;
          msgId = packetBytes[offset++];
        } else {
          if (offset >= packetBytes.length) {
            throw FormatException(
                'Truncated packet header while reading low frequency message ID');
          }
          final low = packetBytes[offset++];
          freq = SLPacketFrequency.low;
          msgId = ((high & 0xFF) << 8) | (low & 0xFF);
        }
      }
    }

    final acks = <int>[];
    if ((flags & SLPacketFlags.appendedAcks) != 0 && packetBytes.isNotEmpty) {
      final ackCount = packetBytes.last & 0xFF;
      final acksStart = packetBytes.length - 1 - (ackCount * 4);
      if (acksStart >= offset) {
        for (var i = 0; i < ackCount; i++) {
          final ackOffset = acksStart + (i * 4);
          final ackSeq = ((packetBytes[ackOffset] & 0xFF) << 24) |
              ((packetBytes[ackOffset + 1] & 0xFF) << 16) |
              ((packetBytes[ackOffset + 2] & 0xFF) << 8) |
              (packetBytes[ackOffset + 3] & 0xFF);
          acks.add(ackSeq);
        }
      }
    }

    return SLPacketHeader(
      flags: flags,
      sequenceNumber: seq,
      messageId: msgId,
      frequency: freq,
      extra: Uint8List.fromList(extra),
      acks: acks,
      bodyOffset: offset,
    );
  }
}
