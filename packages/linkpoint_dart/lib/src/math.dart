import 'dart:math' as math;

/// Second Life Vector3 3D coordinate (x, y, z).
class Vector3 {
  final double x;
  final double y;
  final double z;

  const Vector3(this.x, this.y, this.z);

  static const Vector3 zero = Vector3(0.0, 0.0, 0.0);
  static const Vector3 one = Vector3(1.0, 1.0, 1.0);

  Vector3 operator +(Vector3 other) =>
      Vector3(x + other.x, y + other.y, z + other.z);
  Vector3 operator -(Vector3 other) =>
      Vector3(x - other.x, y - other.y, z - other.z);
  Vector3 operator *(double scalar) =>
      Vector3(x * scalar, y * scalar, z * scalar);

  double get length => math.sqrt(x * x + y * y + z * z);

  Vector3 normalized() {
    final len = length;
    if (len == 0) return Vector3.zero;
    return Vector3(x / len, y / len, z / len);
  }

  double dot(Vector3 other) => x * other.x + y * other.y + z * other.z;

  Vector3 cross(Vector3 other) => Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x,
      );

  List<double> toList() => [x, y, z];

  @override
  String toString() => 'Vector3($x, $y, $z)';
}

/// Second Life Quaternion (x, y, z, w) for 3D rotation.
class Quaternion {
  final double x;
  final double y;
  final double z;
  final double w;

  const Quaternion(this.x, this.y, this.z, this.w);

  static const Quaternion identity = Quaternion(0.0, 0.0, 0.0, 1.0);

  double get length => math.sqrt(x * x + y * y + z * z + w * w);

  Quaternion normalized() {
    final len = length;
    if (len == 0) return Quaternion.identity;
    return Quaternion(x / len, y / len, z / len, w / len);
  }

  List<double> toList() => [x, y, z, w];

  @override
  String toString() => 'Quaternion($x, $y, $z, $w)';
}

/// Second Life Color4 (r, g, b, a) normalized [0.0..1.0].
class Color4 {
  final double r;
  final double g;
  final double b;
  final double a;

  const Color4(this.r, this.g, this.b, [this.a = 1.0]);

  static const Color4 white = Color4(1.0, 1.0, 1.0, 1.0);
  static const Color4 black = Color4(0.0, 0.0, 0.0, 1.0);

  List<double> toList() => [r, g, b, a];

  @override
  String toString() => 'Color4($r, $g, $b, $a)';
}
