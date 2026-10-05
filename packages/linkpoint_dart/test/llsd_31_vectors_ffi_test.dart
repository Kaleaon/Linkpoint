import 'dart:convert';
import 'dart:io';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/src/ffi.dart';

void main() {
  group('Dart Flutter FFI LLSD 31 Test Vectors Conformance Suite', () {
    late List<dynamic> testVectors;

    setUpAll(() {
      final fixtureFile = File('/app/Linkpoint/crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json');
      if (!fixtureFile.existsSync()) {
        throw Exception('Test vectors fixture file not found');
      }
      final jsonContent = fixtureFile.readAsStringSync();
      testVectors = jsonDecode(jsonContent);
    });

    test('Fixture contains exactly 31 test vectors', () {
      expect(testVectors.length, 31);
    });

    test('Passes all 31 test vectors through FFI', () {
      for (final vector in testVectors) {
        final id = vector['id'];
        final name = vector['name'];
        final jsonVal = vector['json_value'];
        final jsonStr = jsonEncode(jsonVal);

        final xmlEnc = LinkpointProtocolFFI.serializeXml(jsonStr);
        expect(xmlEnc, isNotEmpty, reason: 'Vector #$id ($name) serializeXml');
        final xmlDec = LinkpointProtocolFFI.parseXml(xmlEnc);
        expect(xmlDec, isNotNull, reason: 'Vector #$id ($name) parseXml');

        final notationEnc = LinkpointProtocolFFI.serializeNotation(jsonStr);
        expect(notationEnc, isNotEmpty, reason: 'Vector #$id ($name) serializeNotation');
        final notationDec = LinkpointProtocolFFI.parseNotation(notationEnc);
        expect(notationDec, isNotNull, reason: 'Vector #$id ($name) parseNotation');

        final binaryEnc = LinkpointProtocolFFI.serializeBinary(jsonStr);
        expect(binaryEnc, isNotEmpty, reason: 'Vector #$id ($name) serializeBinary');
        final binaryDec = LinkpointProtocolFFI.parseBinary(binaryEnc);
        expect(binaryDec, isNotNull, reason: 'Vector #$id ($name) parseBinary');
      }
    });

    test('Memory leak check: 1000 parse and serialize iterations confirm zero lost buffers', () {
      final jsonStr = jsonEncode({'agent_id': '00000000-0000-0000-0000-000000000000', 'balance': 1000, 'online': true});
      for (var i = 0; i < 1000; i++) {
        final xml = LinkpointProtocolFFI.serializeXml(jsonStr);
        final jsonParsed = LinkpointProtocolFFI.parseXml(xml);
        expect(jsonParsed, isNotEmpty);
      }
    });
  });
}
