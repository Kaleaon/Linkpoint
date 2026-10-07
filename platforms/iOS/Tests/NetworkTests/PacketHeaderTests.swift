import XCTest
@testable import LLSDNetwork

final class PacketHeaderTests: XCTestCase {

    func testHeaderFlagConstantsMatchProtocolStandards() {
        XCTAssertEqual(PacketHeaderFlags.zerocoded.rawValue, 0x80)
        XCTAssertEqual(PacketHeaderFlags.reliable.rawValue, 0x40)
        XCTAssertEqual(PacketHeaderFlags.resent.rawValue, 0x20)
        XCTAssertEqual(PacketHeaderFlags.appendedAcks.rawValue, 0x10)
    }

    func testHeaderSerializationAndParsing() throws {
        let header = PacketHeader(
            flags: [.reliable, .zerocoded],
            sequenceNumber: 12345,
            extraBytesCount: 0,
            messageId: 4,
            frequency: .high
        )

        let serialized = header.serialize()
        let (parsedHeader, payload) = try PacketHeader.parse(from: serialized)

        XCTAssertTrue(parsedHeader.flags.contains(.reliable))
        XCTAssertTrue(parsedHeader.flags.contains(.zerocoded))
        XCTAssertEqual(parsedHeader.sequenceNumber, 12345)
        XCTAssertEqual(parsedHeader.messageId, 4)
        XCTAssertEqual(payload.count, 0)
    }

    func testHeaderParsingLowFrequencyAndAppendedAcks() throws {
        var data = Data([
            0x10, // Appended ACKs flag
            0x00, 0x00, 0x00, 0x66, // Sequence = 102
            0x00, // Extra len = 0
            0xFF, 0xFF, 0x12, 0x34, // Low freq MsgID = 0x1234
            0x11, 0x22, 0x33, 0x44, // Payload
            0x00, 0x00, 0x00, 0x07, // ACK 7
            0x00, 0x00, 0x00, 0x09, // ACK 9
            0x02 // Ack count = 2
        ])

        let (header, payload) = try PacketHeader.parse(from: data)

        XCTAssertTrue(header.flags.contains(.appendedAcks))
        XCTAssertEqual(header.sequenceNumber, 102)
        XCTAssertEqual(header.frequency, .low)
        XCTAssertEqual(header.messageId, 0x1234)
        XCTAssertEqual(header.acks, [7, 9])
        XCTAssertEqual(Array(payload), [0x11, 0x22, 0x33, 0x44])
    }
}
