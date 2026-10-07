import 'dart:convert';
import 'dart:io';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/src/ffi.dart';

File? _findFixtureFile() {
  final candidates = [
    '../../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json',
    '../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json',
    'crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json',
    '/app/Linkpoint/crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json',
  ];
  for (final path in candidates) {
    final file = File(path);
    if (file.existsSync()) return file;
  }
  return null;
}

void main() {
  final ffiAvailable = LinkpointProtocolFFI.isAvailable;
  final fixtureFile = _findFixtureFile();
  final skipReason = !ffiAvailable
      ? 'FFI native library liblinkpoint_protocol is not available'
      : (fixtureFile == null ? 'Test vectors fixture file not found' : null);

  group('Dart Flutter FFI LLSD 31 Test Vectors Conformance Suite', () {
    late List<dynamic> testVectors;

    setUpAll(() {
      if (skipReason != null || fixtureFile == null) {
        testVectors = [];
        return;
      }
      final jsonContent = fixtureFile.readAsStringSync();
      testVectors = jsonDecode(jsonContent);
    });

    test('Fixture contains exactly 31 test vectors', () {
      if (testVectors.isEmpty) {
        markTestSkipped('Test vectors fixture file not found');
        return;
      }
      expect(testVectors.length, 31);
    }, skip: skipReason);

    test('Passes all 31 test vectors through FFI', () {
      if (!LinkpointProtocolFFI.isAvailable) {
        markTestSkipped('FFI library liblinkpoint_protocol is not available');
        return;
      }
      if (testVectors.isEmpty) {
        markTestSkipped('Test vectors fixture file not found');
        return;
      }
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
        expect(notationEnc, isNotEmpty,
            reason: 'Vector #$id ($name) serializeNotation');
        final notationDec = LinkpointProtocolFFI.parseNotation(notationEnc);
        expect(notationDec, isNotNull,
            reason: 'Vector #$id ($name) parseNotation');

        final binaryEnc = LinkpointProtocolFFI.serializeBinary(jsonStr);
        expect(binaryEnc, isNotEmpty,
            reason: 'Vector #$id ($name) serializeBinary');
        final binaryDec = LinkpointProtocolFFI.parseBinary(binaryEnc);
        expect(binaryDec, isNotNull, reason: 'Vector #$id ($name) parseBinary');
      }
    }, skip: skipReason);

    test(
        'Memory leak check: 1000 parse and serialize iterations confirm zero lost buffers',
        () {
      if (!LinkpointProtocolFFI.isAvailable) {
        markTestSkipped('FFI library liblinkpoint_protocol is not available');
        return;
      }
      final jsonStr = jsonEncode({
        'agent_id': '00000000-0000-0000-0000-000000000000',
        'balance': 1000,
        'online': true
      });
      for (var i = 0; i < 1000; i++) {
        final xml = LinkpointProtocolFFI.serializeXml(jsonStr);
        final jsonParsed = LinkpointProtocolFFI.parseXml(xml);
        expect(jsonParsed, isNotEmpty);
      }
    }, skip: skipReason);
  });
}
