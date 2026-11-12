import XCTest
@testable import LLSD

final class GeneratedProtocolTests: XCTestCase {
    func testCatalogMetadata() {
        XCTAssertEqual(GeneratedProtocolCatalog.templateVersion, "2.0")
        XCTAssertGreaterThan(GeneratedProtocolCatalog.registeredMessages.count, 0)
        XCTAssertNotNil(GeneratedProtocolCatalog.registeredMessages["StartPingCheck"])
    }

    func testZerocodedDecompression() {
        let compressed = Data([0x01, 0x00, 0x03, 0x02])
        let decompressed = GeneratedProtocolCatalog.decompressZerocoded(compressed)
        XCTAssertEqual(decompressed, Data([0x01, 0x00, 0x00, 0x00, 0x02]))
    }

    func testPacketSerialization() {
        let packet = StartPingCheckPacket()
        XCTAssertEqual(packet.name, "StartPingCheck")
        XCTAssertEqual(packet.messageNumber, 1)
        let data = packet.serialize()
        XCTAssertEqual(data.count, 4)
    }
}
