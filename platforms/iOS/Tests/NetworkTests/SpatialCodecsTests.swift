import XCTest
@testable import LLSDNetwork

final class SpatialCodecsTests: XCTestCase {

    func testVector3U16DequantizeAndQuantize() {
        let (x, y, z) = Vector3U16.dequantize(u16X: 0, u16Y: 32767, u16Z: 65535)
        XCTAssertEqual(x, -128.0, accuracy: 0.01)
        XCTAssertEqual(y, 0.0, accuracy: 0.01)
        XCTAssertEqual(z, 128.0, accuracy: 0.01)

        let (qx, qy, qz) = Vector3U16.quantize(x: -128.0, y: 0.0, z: 128.0)
        XCTAssertEqual(qx, 0)
        XCTAssertEqual(qy, 32767, accuracy: 1)
        XCTAssertEqual(qz, 65535)
    }

    func testVector3U8DequantizeAndQuantize() {
        let (x, y, z) = Vector3U8.dequantize(u8X: 0, u8Y: 128, u8Z: 255)
        XCTAssertEqual(x, 0.0, accuracy: 0.01)
        XCTAssertEqual(y, 128.0, accuracy: 0.5)
        XCTAssertEqual(z, 255.0, accuracy: 0.01)
    }

    func testPackedQuaternionUnpack16() {
        let (x1, y1, z1, w1) = PackedQuaternion.unpack16(xI16: 0, yI16: 0, zI16: 0)
        XCTAssertEqual(x1, 0.0, accuracy: 0.001)
        XCTAssertEqual(y1, 0.0, accuracy: 0.001)
        XCTAssertEqual(z1, 0.0, accuracy: 0.001)
        XCTAssertEqual(w1, 1.0, accuracy: 0.001)

        let (x2, y2, z2, w2) = PackedQuaternion.unpack16(xI16: 0, yI16: 0, zI16: 23170)
        XCTAssertEqual(x2, 0.0, accuracy: 0.01)
        XCTAssertEqual(y2, 0.0, accuracy: 0.01)
        XCTAssertEqual(z2, 0.7071, accuracy: 0.01)
        XCTAssertEqual(w2, 0.7071, accuracy: 0.01)
    }

    func testRegionAndObjectFlags() {
        let rFlags = RegionFlags([.allowDamage, .allowVoice])
        XCTAssertTrue(rFlags.contains(.allowDamage))
        XCTAssertFalse(rFlags.contains(.isSandbox))

        let oFlags = ObjectFlags([.physics, .phantom])
        XCTAssertTrue(oFlags.contains(.physics))
        XCTAssertFalse(oFlags.contains(.castShadows))
    }
}
