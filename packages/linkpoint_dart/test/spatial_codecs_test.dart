import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('Spatial Vector Codecs & Protocol Decoders Tests', () {
    test('Vector3U16 dequantization and quantization', () {
      final dequantized = Vector3U16.dequantize(0, 32767, 65535, minVec: [-128.0, -128.0, -128.0], maxVec: [128.0, 128.0, 128.0]);
      expect(dequantized[0], closeTo(-128.0, 0.01));
      expect(dequantized[1], closeTo(0.0, 0.01));
      expect(dequantized[2], closeTo(128.0, 0.01));

      final quantized = Vector3U16.quantize(-128.0, 0.0, 128.0, minVec: [-128.0, -128.0, -128.0], maxVec: [128.0, 128.0, 128.0]);
      expect(quantized[0], 0);
      expect(quantized[1], closeTo(32767, 1));
      expect(quantized[2], 65535);
    });

    test('Vector3U8 dequantization and quantization', () {
      final dequantized = Vector3U8.dequantize(0, 128, 255, minVec: [0.0, 0.0, 0.0], maxVec: [255.0, 255.0, 255.0]);
      expect(dequantized[0], closeTo(0.0, 0.01));
      expect(dequantized[1], closeTo(128.0, 0.5));
      expect(dequantized[2], closeTo(255.0, 0.01));
    });

    test('16-bit Packed Quaternion reconstruction', () {
      final q1 = PackedQuaternion.unpack16(0, 0, 0);
      expect(q1[0], closeTo(0.0, 0.001));
      expect(q1[1], closeTo(0.0, 0.001));
      expect(q1[2], closeTo(0.0, 0.001));
      expect(q1[3], closeTo(1.0, 0.001));

      final q2 = PackedQuaternion.unpack16(0, 0, 23170);
      expect(q2[0], closeTo(0.0, 0.01));
      expect(q2[1], closeTo(0.0, 0.01));
      expect(q2[2], closeTo(0.7071, 0.01));
      expect(q2[3], closeTo(0.7071, 0.01));
    });

    test('Bitfield utility classes for RegionFlags and ObjectFlags', () {
      final rFlags = RegionFlags.allowDamage | RegionFlags.allowVoice;
      expect(RegionFlags.hasFlag(rFlags, RegionFlags.allowDamage), true);
      expect(RegionFlags.hasFlag(rFlags, RegionFlags.isSandbox), false);

      final oFlags = ObjectFlags.physics | ObjectFlags.phantom;
      expect(ObjectFlags.hasFlag(oFlags, ObjectFlags.physics), true);
      expect(ObjectFlags.hasFlag(oFlags, ObjectFlags.castShadows), false);
    });
  });
}
