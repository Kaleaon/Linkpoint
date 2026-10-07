import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

Uint8List hexToBytes(String hex) {
  final result = Uint8List(hex.length ~/ 2);
  for (int i = 0; i < hex.length; i += 2) {
    result[i ~/ 2] = int.parse(hex.substring(i, i + 2), radix: 16);
  }
  return result;
}

String resolveVectorPath(String relativeSubpath) {
  final candidateRelative = [
    '../../test-vectors/$relativeSubpath',
    '../test-vectors/$relativeSubpath',
    'test-vectors/$relativeSubpath',
    '/app/Linkpoint/test-vectors/$relativeSubpath',
    'C:\\app\\Linkpoint\\test-vectors\\$relativeSubpath',
  ];
  for (final path in candidateRelative) {
    if (File(path).existsSync()) return path;
  }
  Directory current = Directory.current;
  while (current.parent.path != current.path) {
    final candidate = File('${current.path}/test-vectors/$relativeSubpath');
    if (candidate.existsSync()) return candidate.path;
    current = current.parent;
  }
  throw Exception('Vector file not found: $relativeSubpath');
}

void main() {
  group('Pure Dart J2K & LLMesh Pipeline Tests', () {
    test('J2K texture decoder decodes test vectors natively without C-FFI', () {
      final path = resolveVectorPath('textures/j2k_texture_decoder_vectors.json');
      final jsonContent = File(path).readAsStringSync();
      final data = json.decode(jsonContent);

      for (final caseItem in data['j2k_vectors']) {
        final bytes = hexToBytes(caseItem['hex_bytes']);
        final expected = caseItem['expected'];

        if (expected['status'] == 'success') {
          final header = J2KDecoder.parseHeader(bytes);
          expect(header, isNotNull);
          expect(header!.status, equals('success'));
          expect(header.width, equals(expected['width']));
          expect(header.height, equals(expected['height']));

          final rgba = J2KDecoder.decodeRgba(bytes);
          expect(rgba, isNotNull);
          expect(rgba!.length, equals(expected['width'] * expected['height'] * 4));
        } else {
          final header = J2KDecoder.parseHeader(bytes);
          expect(header, isNotNull);
          expect(header!.status, equals('fallback'));
        }
      }
    });

    test('LLMesh converter parses binary mesh and generates GLTF JSON in pure Dart', () {
      final path = resolveVectorPath('mesh/llmesh_decompress_vectors.json');
      final jsonContent = File(path).readAsStringSync();
      final data = json.decode(jsonContent);

      for (final caseItem in data['llmesh_vectors']) {
        final bytes = hexToBytes(caseItem['hex_bytes']);
        final expected = caseItem['expected'];

        final parsed = LLMeshConverter.parseBinary(bytes);
        expect(parsed, isNotNull);
        expect(parsed!.vertexCount, equals(expected['vertex_count']));
        expect(parsed.indexCount, equals(expected['index_count']));

        final gltfJson = LLMeshConverter.toGltfJson(parsed);
        expect(gltfJson, contains('"asset"'));
        expect(gltfJson, contains('"POSITION"'));
        expect(gltfJson, contains('Linkpoint Dart Asset Pipeline'));
      }
    });
  });
}
