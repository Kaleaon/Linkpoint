import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('Vector3 & Quaternion Tests', () {
    test('Vector3 operations', () {
      const v1 = Vector3(1.0, 2.0, 3.0);
      const v2 = Vector3(4.0, 5.0, 6.0);
      final sum = v1 + v2;
      expect(sum.x, 5.0);
      expect(sum.y, 7.0);
      expect(sum.z, 9.0);
      expect(v1.dot(v2), 32.0);
    });

    test('Quaternion identity and normalization', () {
      const q = Quaternion.identity;
      expect(q.w, 1.0);
      expect(q.normalized().length, closeTo(1.0, 0.0001));
    });
  });
}
