import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('LLSD Serialization Tests', () {
    test('Serializes LLSD Map to XML', () {
      final map = LLSDValue.map({
        'agent_id': LLSDValue.uuid(LLUUID.zero),
        'region_name': LLSDValue.string('Welcome Island'),
        'online': LLSDValue.boolean(true),
      });

      final xml = LLSD.serializeXml(map);
      expect(xml, contains('<key>agent_id</key><uuid>00000000-0000-0000-0000-000000000000</uuid>'));
      expect(xml, contains('<key>region_name</key><string>Welcome Island</string>'));
      expect(xml, contains('<key>online</key><boolean>true</boolean>'));
    });

    test('Parses LLSD XML Map', () {
      const xml = '<?xml version="1.0"?><llsd><map><key>online</key><boolean>true</boolean></map></llsd>';
      final parsed = LLSD.parseXml(xml);
      expect(parsed, isA<LLSDMap>());
      final mapVal = (parsed as LLSDMap).value;
      expect((mapVal['online'] as LLSDBoolean).value, true);
    });
  });
}
