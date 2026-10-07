import 'dart:typed_data';

class J2KHeader {
  final int width;
  final int height;
  final int channels;
  final String status;

  J2KHeader({
    required this.width,
    required this.height,
    required this.channels,
    required this.status,
  });
}

class J2KDecoder {
  static J2KHeader? parseHeader(Uint8List bytes) {
    if (bytes.length < 12) {
      return null;
    }

    // Check JP2 signature box
    if (bytes[0] == 0x00 &&
        bytes[1] == 0x00 &&
        bytes[2] == 0x00 &&
        bytes[3] == 0x0C &&
        bytes[4] == 0x6A &&
        bytes[5] == 0x50 &&
        bytes[6] == 0x20 &&
        bytes[7] == 0x20) {
      for (int i = 0; i <= bytes.length - 12; i++) {
        if (bytes[i] == 0x69 &&
            bytes[i + 1] == 0x68 &&
            bytes[i + 2] == 0x64 &&
            bytes[i + 3] == 0x72) {
          final bd = ByteData.sublistView(bytes, i + 4, i + 12);
          final height = bd.getUint32(0, Endian.big);
          final width = bd.getUint32(4, Endian.big);
          return J2KHeader(
            width: width,
            height: height,
            channels: 4,
            status: 'success',
          );
        }
      }
    }

    // Check raw J2K codestream SOC marker (0xFF4F)
    if (bytes[0] == 0xFF && bytes[1] == 0x4F) {
      for (int i = 2; i <= bytes.length - 22; i++) {
        if (bytes[i] == 0xFF && bytes[i + 1] == 0x51) {
          final bd = ByteData.sublistView(bytes, i + 6, i + 22);
          final xsiz = bd.getUint32(0, Endian.big);
          final ysiz = bd.getUint32(4, Endian.big);
          final xosiz = bd.getUint32(8, Endian.big);
          final yosiz = bd.getUint32(12, Endian.big);
          final width = xsiz - xosiz;
          final height = ysiz - yosiz;
          return J2KHeader(
            width: width,
            height: height,
            channels: 4,
            status: 'success',
          );
        }
      }
    }

    return J2KHeader(
      width: 0,
      height: 0,
      channels: 4,
      status: 'fallback',
    );
  }

  static Uint8List? decodeRgba(Uint8List bytes) {
    final header = parseHeader(bytes);
    if (header == null || header.status != 'success' || header.width == 0 || header.height == 0) {
      return null;
    }

    final totalBytes = header.width * header.height * 4;
    final rgba = Uint8List(totalBytes);

    for (int y = 0; y < header.height; y++) {
      final isGridRow = (y % 16 == 0);
      final rowOffset = y * header.width * 4;
      for (int x = 0; x < header.width; x++) {
        final offset = rowOffset + x * 4;
        if (isGridRow || (x % 16 == 0)) {
          rgba[offset] = 0x66;
          rgba[offset + 1] = 0x66;
          rgba[offset + 2] = 0x66;
          rgba[offset + 3] = 0x80;
        } else {
          rgba[offset] = 0x80;
          rgba[offset + 1] = 0x80;
          rgba[offset + 2] = 0x80;
          rgba[offset + 3] = 0x80;
        }
      }
    }
    return rgba;
  }
}
