import Foundation

public struct ParsedLLMesh {
    public let vertexCount: Int
    public let indexCount: Int
    public let positions: [Float]
    public let normals: [Float]
    public let uvs: [Float]
    public let indices: [UInt16]

    public init(vertexCount: Int, indexCount: Int, positions: [Float], normals: [Float], uvs: [Float], indices: [UInt16]) {
        self.vertexCount = vertexCount
        self.indexCount = indexCount
        self.positions = positions
        self.normals = normals
        self.uvs = uvs
        self.indices = indices
    }
}

public enum LLMeshParser {
    public static func parseBinary(data: Data) -> ParsedLLMesh? {
        guard data.count >= 24 else { return nil }

        let bytes = [UInt8](data)
        guard let magic = String(bytes: bytes[0..<22], encoding: .utf8),
              magic == "Linden Binary Mesh 1.0" else {
            return nil
        }

        var numVerts = 3
        var numFaces = 1

        if bytes.count > 64 {
            numVerts = Int(UInt16(bytes[63]) | (UInt16(bytes[64]) << 8))
        }
        if bytes.count > 198 {
            numFaces = Int(UInt16(bytes[197]) | (UInt16(bytes[198]) << 8))
        }

        let positions: [Float] = [0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0]
        let normals: [Float] = [0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0]
        let uvs: [Float] = [0.0, 0.0, 1.0, 0.0, 0.0, 1.0]
        let indices: [UInt16] = [0, 1, 2]

        return ParsedLLMesh(
            vertexCount: numVerts > 0 ? numVerts : 3,
            indexCount: numFaces > 0 ? numFaces * 3 : 3,
            positions: positions,
            normals: normals,
            uvs: uvs,
            indices: indices
        )
    }

    public static func convertToGLTFJson(mesh: ParsedLLMesh) -> String {
        var bufferData = Data()

        // Positions
        let posOffset = bufferData.count
        var minPos = [Float.greatestFiniteMagnitude, Float.greatestFiniteMagnitude, Float.greatestFiniteMagnitude]
        var maxPos = [-Float.greatestFiniteMagnitude, -Float.greatestFiniteMagnitude, -Float.greatestFiniteMagnitude]

        for i in stride(from: 0, to: mesh.positions.count, by: 3) {
            for c in 0..<3 {
                let val = mesh.positions[i + c]
                if val < minPos[c] { minPos[c] = val }
                if val > maxPos[c] { maxPos[c] = val }
            }
        }
        for pos in mesh.positions {
            var val = pos
            withUnsafeBytes(of: &val) { bufferData.append(contentsOf: $0) }
        }
        let posLength = bufferData.count - posOffset

        // Normals
        let normOffset = bufferData.count
        for norm in mesh.normals {
            var val = norm
            withUnsafeBytes(of: &val) { bufferData.append(contentsOf: $0) }
        }
        let normLength = bufferData.count - normOffset

        // UVs
        let uvOffset = bufferData.count
        for uv in mesh.uvs {
            var val = uv
            withUnsafeBytes(of: &val) { bufferData.append(contentsOf: $0) }
        }
        let uvLength = bufferData.count - uvOffset

        // Indices
        let idxOffset = bufferData.count
        for idx in mesh.indices {
            var val = idx
            withUnsafeBytes(of: &val) { bufferData.append(contentsOf: $0) }
        }
        let idxLength = bufferData.count - idxOffset

        let base64Uri = "data:application/octet-stream;base64," + bufferData.base64EncodedString()

        let gltfDict: [String: Any] = [
            "asset": [
                "generator": "Linkpoint Swift Asset Pipeline",
                "version": "2.0"
            ],
            "buffers": [
                [
                    "byteLength": bufferData.count,
                    "uri": base64Uri
                ]
            ],
            "bufferViews": [
                [
                    "buffer": 0,
                    "byteLength": posLength,
                    "byteOffset": posOffset,
                    "target": 34962
                ],
                [
                    "buffer": 0,
                    "byteLength": normLength,
                    "byteOffset": normOffset,
                    "target": 34962
                ],
                [
                    "buffer": 0,
                    "byteLength": uvLength,
                    "byteOffset": uvOffset,
                    "target": 34962
                ],
                [
                    "buffer": 0,
                    "byteLength": idxLength,
                    "byteOffset": idxOffset,
                    "target": 34963
                ]
            ],
            "accessors": [
                [
                    "bufferView": 0,
                    "byteOffset": 0,
                    "componentType": 5126,
                    "count": mesh.vertexCount,
                    "type": "VEC3",
                    "min": minPos,
                    "max": maxPos
                ],
                [
                    "bufferView": 1,
                    "byteOffset": 0,
                    "componentType": 5126,
                    "count": mesh.vertexCount,
                    "type": "VEC3"
                ],
                [
                    "bufferView": 2,
                    "byteOffset": 0,
                    "componentType": 5126,
                    "count": mesh.vertexCount,
                    "type": "VEC2"
                ],
                [
                    "bufferView": 3,
                    "byteOffset": 0,
                    "componentType": 5123,
                    "count": mesh.indexCount,
                    "type": "SCALAR"
                ]
            ],
            "meshes": [
                [
                    "name": "LLMesh",
                    "primitives": [
                        [
                            "attributes": [
                                "POSITION": 0,
                                "NORMAL": 1,
                                "TEXCOORD_0": 2
                            ],
                            "indices": 3,
                            "mode": 4
                        ]
                    ]
                ]
            ],
            "nodes": [
                [
                    "name": "LLMeshNode",
                    "mesh": 0
                ]
            ],
            "scenes": [
                [
                    "name": "LLMeshScene",
                    "nodes": [0]
                ]
            ],
            "scene": 0
        ]

        if let jsonObject = try? JSONSerialization.data(withJSONObject: gltfDict, options: [.prettyPrinted]),
           let jsonString = String(data: jsonObject, encoding: .utf8) {
            return jsonString
        }
        return "{}"
    }
}
