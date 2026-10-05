import XCTest
@testable import LLSDNetwork

final class PacketHeaderTests: XCTestCase {

    func testHeaderSerializationAndParsing() throws {
        let header = PacketHeader(
            flags: [.reliable, .zerocoded],
            sequenceNumber: 12345,
            extraBytesCount: 0
        )

        let serialized = header.serialize()
        let (parsedHeader, payload) = try PacketHeader.parse(from: serialized)

        XCTAssertTrue(parsedHeader.flags.contains(.reliable))
        XCTAssertTrue(parsedHeader.flags.contains(.zerocoded))
        XCTAssertEqual(parsedHeader.sequenceNumber, 12345)
        XCTAssertEqual(payload.count, 0)
    }

    func testZerocodeCompressionAndDecompression() {
        let original = Data([0x01, 0x00, 0x00, 0x00, 0x00, 0x02, 0x03, 0x00, 0x04])
        let compressed = Zerocode.compress(original)
        let decompressed = Zerocode.decompress(compressed)

        XCTAssertEqual(decompressed, original)
    }
}
