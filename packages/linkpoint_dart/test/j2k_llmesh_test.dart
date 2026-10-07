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

void main() {
  group('Pure Dart J2K & LLMesh Pipeline Tests', () {
    test('J2K texture decoder decodes test vectors natively without C-FFI', () {
      final vecFile = File('../../test-vectors/textures/j2k_texture_decoder_vectors.json');
      final path = vecFile.existsSync()
          ? vecFile.path
          : '/app/Linkpoint/test-vectors/textures/j2k_texture_decoder_vectors.json';
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
      final vecFile = File('../../test-vectors/mesh/llmesh_decompress_vectors.json');
      final path = vecFile.existsSync()
          ? vecFile.path
          : '/app/Linkpoint/test-vectors/mesh/llmesh_decompress_vectors.json';
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
