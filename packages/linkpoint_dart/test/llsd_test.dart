import 'dart:io';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('LLSD Serialization & Conformance Tests', () {
    test('Serializes LLSD Map to XML', () {
      final map = LLSDValue.map({
        'agent_id': LLSDValue.uuid(LLUUID.zero),
        'region_name': LLSDValue.string('Welcome Island'),
        'online': LLSDValue.boolean(true),
      });

      final xml = LLSD.serializeXml(map);
      expect(
        xml,
        contains(
          '<key>agent_id</key><uuid>00000000-0000-0000-0000-000000000000</uuid>',
        ),
      );
      expect(
        xml,
        contains('<key>region_name</key><string>Welcome Island</string>'),
      );
      expect(xml, contains('<key>online</key><boolean>true</boolean>'));
    });

    test('Parses LLSD XML Map', () {
      const xml =
          '<?xml version="1.0"?><llsd><map><key>online</key><boolean>true</boolean></map></llsd>';
      final parsed = LLSD.parseXml(xml);
      expect(parsed, isA<LLSDMap>());
      final mapVal = (parsed as LLSDMap).value;
      expect((mapVal['online'] as LLSDBoolean).value, true);
    });

    test(
      'Passes all 31 canonical LLSD test vectors in XML and Binary formats',
      () {
        var vectorsDir = Directory(
          '../../legacy/Linkpoint/src/test/resources/llsd-conformance/vectors',
        );
        if (!vectorsDir.existsSync()) {
          vectorsDir = Directory(
            '../../Linkpoint/src/test/resources/llsd-conformance/vectors',
          );
        }
        if (!vectorsDir.existsSync()) {
          vectorsDir = Directory(
            'legacy/Linkpoint/src/test/resources/llsd-conformance/vectors',
          );
        }
        if (!vectorsDir.existsSync()) {
          vectorsDir = Directory(
            'Linkpoint/src/test/resources/llsd-conformance/vectors',
          );
        }
        expect(
          vectorsDir.existsSync(),
          isTrue,
          reason: 'Vectors directory must exist',
        );

        final subdirs = vectorsDir.listSync().whereType<Directory>().toList()
          ..sort((a, b) => a.path.compareTo(b.path));

        expect(
          subdirs.length,
          equals(31),
          reason: 'Expected 31 test vector directories',
        );

        for (final fixtureDir in subdirs) {
          final fixtureName =
              fixtureDir.path.split(Platform.pathSeparator).last;
          final xmlFile = File('${fixtureDir.path}/value.xml');
          final binFile = File('${fixtureDir.path}/value.bin');

          expect(
            xmlFile.existsSync(),
            isTrue,
            reason: 'value.xml must exist for $fixtureName',
          );
          expect(
            binFile.existsSync(),
            isTrue,
            reason: 'value.bin must exist for $fixtureName',
          );

          final xmlString = xmlFile.readAsStringSync();
          final binBytes = binFile.readAsBytesSync();

          final fromXml = LLSD.parseXml(xmlString);
          final fromBin = LLSD.parseBinary(binBytes);

          expect(
            fromXml,
            equals(fromBin),
            reason: 'XML <-> BIN tree mismatch for $fixtureName',
          );

          // Round-trip check
          final reXml = LLSD.serializeXml(fromXml);
          final reBin = LLSD.serializeBinary(fromXml);

          final reFromXml = LLSD.parseXml(reXml);
          final reFromBin = LLSD.parseBinary(reBin);

          expect(
            reFromXml,
            equals(fromXml),
            reason: 'XML round-trip mismatch for $fixtureName',
          );
          expect(
            reFromBin,
            equals(fromXml),
            reason: 'BIN round-trip mismatch for $fixtureName',
          );
        }
      },
    );
  });
}
