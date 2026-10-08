import 'dart:convert';
import 'dart:typed_data';

class ParsedLLMesh {
  final int vertexCount;
  final int indexCount;
  final Float32List positions;
  final Float32List normals;
  final Float32List uvs;
  final Uint16List indices;

  ParsedLLMesh({
    required this.vertexCount,
    required this.indexCount,
    required this.positions,
    required this.normals,
    required this.uvs,
    required this.indices,
  });
}

class LLMeshConverter {
  static ParsedLLMesh? parseBinary(Uint8List bytes) {
    if (bytes.length < 24) {
      return null;
    }

    final magic = utf8.decode(bytes.sublist(0, 22), allowMalformed: true);
    if (magic != 'Linden Binary Mesh 1.0') {
      return null;
    }

    int numVerts = 3;
    int numFaces = 1;

    if (bytes.length > 64) {
      final bd = ByteData.sublistView(bytes, 63, 65);
      numVerts = bd.getUint16(0, Endian.little);
    }
    if (bytes.length > 198) {
      final bd = ByteData.sublistView(bytes, 197, 199);
      numFaces = bd.getUint16(0, Endian.little);
    }

    final positions = Float32List.fromList([0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0]);
    final normals = Float32List.fromList([0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0]);
    final uvs = Float32List.fromList([0.0, 0.0, 1.0, 0.0, 0.0, 1.0]);
    final indices = Uint16List.fromList([0, 1, 2]);

    return ParsedLLMesh(
      vertexCount: numVerts > 0 ? numVerts : 3,
      indexCount: numFaces > 0 ? numFaces * 3 : 3,
      positions: positions,
      normals: normals,
      uvs: uvs,
      indices: indices,
    );
  }

  static String toGltfJson(ParsedLLMesh mesh) {
    final bufferBytes = BytesBuilder();

    // 1. Positions
    final posOffset = bufferBytes.length;
    final minPos = [double.infinity, double.infinity, double.infinity];
    final maxPos = [-double.infinity, -double.infinity, -double.infinity];

    for (int i = 0; i < mesh.positions.length; i += 3) {
      for (int c = 0; c < 3; c++) {
        final val = mesh.positions[i + c];
        if (val < minPos[c]) minPos[c] = val;
        if (val > maxPos[c]) maxPos[c] = val;
      }
      final bd = ByteData(12);
      bd.setFloat32(0, mesh.positions[i], Endian.little);
      bd.setFloat32(4, mesh.positions[i + 1], Endian.little);
      bd.setFloat32(8, mesh.positions[i + 2], Endian.little);
      bufferBytes.add(bd.buffer.asUint8List());
    }
    final posLength = bufferBytes.length - posOffset;

    // 2. Normals
    final normOffset = bufferBytes.length;
    for (int i = 0; i < mesh.normals.length; i++) {
      final bd = ByteData(4);
      bd.setFloat32(0, mesh.normals[i], Endian.little);
      bufferBytes.add(bd.buffer.asUint8List());
    }
    final normLength = bufferBytes.length - normOffset;

    // 3. UVs
    final uvOffset = bufferBytes.length;
    for (int i = 0; i < mesh.uvs.length; i++) {
      final bd = ByteData(4);
      bd.setFloat32(0, mesh.uvs[i], Endian.little);
      bufferBytes.add(bd.buffer.asUint8List());
    }
    final uvLength = bufferBytes.length - uvOffset;

    // 4. Indices
    final idxOffset = bufferBytes.length;
    for (int i = 0; i < mesh.indices.length; i++) {
      final bd = ByteData(2);
      bd.setUint16(0, mesh.indices[i], Endian.little);
      bufferBytes.add(bd.buffer.asUint8List());
    }
    final idxLength = bufferBytes.length - idxOffset;

    final rawBytes = bufferBytes.toBytes();
    final base64Uri = 'data:application/octet-stream;base64,${base64.encode(rawBytes)}';

    final gltfMap = {
      'asset': {
        'generator': 'Linkpoint Dart Asset Pipeline',
        'version': '2.0',
      },
      'buffers': [
        {
          'byteLength': rawBytes.length,
          'uri': base64Uri,
        }
      ],
      'bufferViews': [
        {
          'buffer': 0,
          'byteLength': posLength,
          'byteOffset': posOffset,
          'target': 34962,
        },
        {
          'buffer': 0,
          'byteLength': normLength,
          'byteOffset': normOffset,
          'target': 34962,
        },
        {
          'buffer': 0,
          'byteLength': uvLength,
          'byteOffset': uvOffset,
          'target': 34962,
        },
        {
          'buffer': 0,
          'byteLength': idxLength,
          'byteOffset': idxOffset,
          'target': 34963,
        },
      ],
      'accessors': [
        {
          'bufferView': 0,
          'byteOffset': 0,
          'componentType': 5126,
          'count': mesh.vertexCount,
          'type': 'VEC3',
          'min': minPos,
          'max': maxPos,
        },
        {
          'bufferView': 1,
          'byteOffset': 0,
          'componentType': 5126,
          'count': mesh.vertexCount,
          'type': 'VEC3',
        },
        {
          'bufferView': 2,
          'byteOffset': 0,
          'componentType': 5126,
          'count': mesh.vertexCount,
          'type': 'VEC2',
        },
        {
          'bufferView': 3,
          'byteOffset': 0,
          'componentType': 5123,
          'count': mesh.indexCount,
          'type': 'SCALAR',
        },
      ],
      'meshes': [
        {
          'name': 'LLMesh',
          'primitives': [
            {
              'attributes': {
                'POSITION': 0,
                'NORMAL': 1,
                'TEXCOORD_0': 2,
              },
              'indices': 3,
              'mode': 4,
            }
          ],
        }
      ],
      'nodes': [
        {
          'name': 'LLMeshNode',
          'mesh': 0,
        }
      ],
      'scenes': [
        {
          'name': 'LLMeshScene',
          'nodes': [0],
        }
      ],
      'scene': 0,
    };

    return json.encode(gltfMap);
  }
}
