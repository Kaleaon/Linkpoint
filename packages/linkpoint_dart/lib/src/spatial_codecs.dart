import 'dart:math';

/// Quantized 3D Vector Codec using 16-bit unsigned integers per component
class Vector3U16 {
  static List<double> dequantize(
    int u16X,
    int u16Y,
    int u16Z, {
    List<double> minVec = const [-128.0, -128.0, -128.0],
    List<double> maxVec = const [128.0, 128.0, 128.0],
  }) {
    final x = minVec[0] + (u16X / 65535.0) * (maxVec[0] - minVec[0]);
    final y = minVec[1] + (u16Y / 65535.0) * (maxVec[1] - minVec[1]);
    final z = minVec[2] + (u16Z / 65535.0) * (maxVec[2] - minVec[2]);
    return [x, y, z];
  }

  static List<int> quantize(
    double x,
    double y,
    double z, {
    List<double> minVec = const [-128.0, -128.0, -128.0],
    List<double> maxVec = const [128.0, 128.0, 128.0],
  }) {
    final qX = (((x - minVec[0]) / (maxVec[0] - minVec[0])) * 65535.0)
        .round()
        .clamp(0, 65535);
    final qY = (((y - minVec[1]) / (maxVec[1] - minVec[1])) * 65535.0)
        .round()
        .clamp(0, 65535);
    final qZ = (((z - minVec[2]) / (maxVec[2] - minVec[2])) * 65535.0)
        .round()
        .clamp(0, 65535);
    return [qX, qY, qZ];
  }
}

/// Quantized 3D Vector Codec using 8-bit unsigned integers per component
class Vector3U8 {
  static List<double> dequantize(
    int u8X,
    int u8Y,
    int u8Z, {
    List<double> minVec = const [0.0, 0.0, 0.0],
    List<double> maxVec = const [255.0, 255.0, 255.0],
  }) {
    final x = minVec[0] + (u8X / 255.0) * (maxVec[0] - minVec[0]);
    final y = minVec[1] + (u8Y / 255.0) * (maxVec[1] - minVec[1]);
    final z = minVec[2] + (u8Z / 255.0) * (maxVec[2] - minVec[2]);
    return [x, y, z];
  }

  static List<int> quantize(
    double x,
    double y,
    double z, {
    List<double> minVec = const [0.0, 0.0, 0.0],
    List<double> maxVec = const [255.0, 255.0, 255.0],
  }) {
    final qX = (((x - minVec[0]) / (maxVec[0] - minVec[0])) * 255.0)
        .round()
        .clamp(0, 255);
    final qY = (((y - minVec[1]) / (maxVec[1] - minVec[1])) * 255.0)
        .round()
        .clamp(0, 255);
    final qZ = (((z - minVec[2]) / (maxVec[2] - minVec[2])) * 255.0)
        .round()
        .clamp(0, 255);
    return [qX, qY, qZ];
  }
}

/// 16-bit Packed Quaternion Decoder reconstructing scalar component w
class PackedQuaternion {
  static List<double> unpack16(int xI16, int yI16, int zI16) {
    final x = xI16 / 32767.0;
    final y = yI16 / 32767.0;
    final z = zI16 / 32767.0;

    final wSq = 1.0 - (x * x + y * y + z * z);
    final w = wSq > 0.0 ? sqrt(wSq) : 0.0;

    final mag = sqrt(x * x + y * y + z * z + w * w);
    if (mag > 0) {
      return [x / mag, y / mag, z / mag, w / mag];
    }
    return [0.0, 0.0, 0.0, 1.0];
  }
}
