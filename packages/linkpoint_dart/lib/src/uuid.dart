import 'dart:typed_data';

/// Represents a Second Life 128-bit LLUUID.
class LLUUID {
  final Uint8List _bytes;

  static final LLUUID zero = LLUUID._(Uint8List(16));

  LLUUID._(Uint8List bytes) : _bytes = bytes {
    if (_bytes.length != 16) {
      throw ArgumentError('LLUUID must be exactly 16 bytes');
    }
  }

  /// Parses a canonical 36-character UUID string (e.g. "00000000-0000-0000-0000-000000000000").
  factory LLUUID.parse(String uuidString) {
    final clean = uuidString.replaceAll('-', '').trim();
    if (clean.length != 32) {
      throw FormatException('Invalid UUID string format: $uuidString');
    }
    final bytes = Uint8List(16);
    for (var i = 0; i < 16; i++) {
      final hexByte = clean.substring(i * 2, i * 2 + 2);
      final byteVal = int.tryParse(hexByte, radix: 16);
      if (byteVal == null) {
        throw FormatException('Invalid hex sequence in UUID: $uuidString');
      }
      bytes[i] = byteVal;
    }
    return LLUUID._(bytes);
  }

  factory LLUUID.fromBytes(Uint8List bytes) {
    if (bytes.length != 16) {
      throw ArgumentError('Byte array for LLUUID must be 16 bytes');
    }
    return LLUUID._(Uint8List.fromList(bytes));
  }

  Uint8List toBytes() => Uint8List.fromList(_bytes);

  bool get isZero {
    for (final b in _bytes) {
      if (b != 0) return false;
    }
    return true;
  }

  @override
  String toString() {
    final hexStr =
        _bytes.map((b) => b.toRadixString(16).padLeft(2, '0')).join();
    return '${hexStr.substring(0, 8)}-${hexStr.substring(8, 12)}-${hexStr.substring(12, 16)}-${hexStr.substring(16, 20)}-${hexStr.substring(20, 32)}';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    if (other is! LLUUID) return false;
    for (var i = 0; i < 16; i++) {
      if (_bytes[i] != other._bytes[i]) return false;
    }
    return true;
  }

  @override
  int get hashCode {
    var hash = 0;
    for (final b in _bytes) {
      hash = (hash * 31 + b) & 0x7FFFFFFF;
    }
    return hash;
  }
}
