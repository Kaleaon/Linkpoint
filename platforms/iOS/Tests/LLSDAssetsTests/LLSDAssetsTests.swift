import XCTest
@testable import LLSDAssets

final class LLSDAssetsTests: XCTestCase {
    func testJ2KHeaderParsingAndDecoding() {
        let sampleJ2KBytes: [UInt8] = [
            0x00, 0x00, 0x00, 0x0C, 0x6A, 0x50, 0x20, 0x20, 0x0D, 0x0A, 0x87, 0x0A,
            0x69, 0x68, 0x64, 0x72, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x40
        ]
        let data = Data(sampleJ2KBytes)
        let header = J2KDecoder.parseHeader(data: data)
        XCTAssertEqual(header.status, "success")
        XCTAssertEqual(header.width, 64)
        XCTAssertEqual(header.height, 64)

        let rgba = J2KDecoder.decodeRgba(data: data)
        XCTAssertNotNil(rgba)
        XCTAssertEqual(rgba?.count, 64 * 64 * 4)
    }

    func testLLMeshParsingAndGLTFConversion() {
        let sampleMeshHex = "4c696e64656e2042696e617279204d65736820312e3000000000000000000000000000000000000000000000000000000000000000803f0000803f0000803f03000000000000000000000000000000803f0000000000000000000000000000803f00000000000000000000803f00000000000000000000803f0000803f00000000000000000000803f00000000000000000000803f000000000000000000000000000000000000803f00000000000000000000803f0100000001000200"
        var data = Data()
        var hex = sampleMeshHex
        while !hex.isEmpty {
            let c = String(hex.prefix(2))
            hex = String(hex.dropFirst(2))
            if let b = UInt8(c, radix: 16) {
                data.append(b)
            }
        }

        guard let parsed = LLMeshParser.parseBinary(data: data) else {
            XCTFail("Failed to parse binary mesh")
            return
        }

        XCTAssertEqual(parsed.vertexCount, 3)
        XCTAssertEqual(parsed.indexCount, 3)

        let gltfJson = LLMeshParser.convertToGLTFJson(mesh: parsed)
        XCTAssertTrue(gltfJson.contains("\"asset\""))
        XCTAssertTrue(gltfJson.contains("\"POSITION\""))
    }
}
