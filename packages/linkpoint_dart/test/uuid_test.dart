import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('LLUUID Tests', () {
    test('LLUUID zero constant', () {
      expect(LLUUID.zero.isZero, true);
      expect(LLUUID.zero.toString(), '00000000-0000-0000-0000-000000000000');
    });

    test('Parses canonical 36-char string', () {
      const hex = '12345678-1234-1234-1234-123456789abc';
      final uuid = LLUUID.parse(hex);
      expect(uuid.toString(), hex);
      expect(uuid.isZero, false);
    });

    test('Equality and HashCode', () {
      const hex = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
      final u1 = LLUUID.parse(hex);
      final u2 = LLUUID.parse(hex);
      expect(u1, equals(u2));
      expect(u1.hashCode, equals(u2.hashCode));
    });
  });
}
