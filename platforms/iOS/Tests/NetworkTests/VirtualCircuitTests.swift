import XCTest
@testable import LLSDNetwork

final class VirtualCircuitTests: XCTestCase {

    func testPacketProcessingAndReliability() async throws {
        let circuit = VirtualCircuitManager()

        // Create a mock incoming packet bytes: reliable, sequence #100, message ID 0x00000001, body [0xAA, 0xBB]
        let header = PacketHeader(flags: [.reliable], sequenceNumber: 100)
        var rawData = header.serialize()
        var msgIDBig: UInt32 = UInt32(1).bigEndian
        withUnsafeBytes(of: &msgIDBig) { rawData.append(contentsOf: $0) }
        rawData.append(contentsOf: [0xAA, 0xBB])

        // Process incoming packet
        circuit.processIncomingData(rawData)

        XCTAssertEqual(circuit.pendingAckCount(), 1)
        let acks = circuit.popPendingAcks()
        XCTAssertEqual(acks, [100])
        XCTAssertEqual(circuit.pendingAckCount(), 0)
    }

    func testPacketStreamAsyncSequence() async throws {
        let circuit = VirtualCircuitManager()

        let header = PacketHeader(flags: [], sequenceNumber: 50)
        var rawData = header.serialize()
        var msgIDBig: UInt32 = UInt32(2).bigEndian
        withUnsafeBytes(of: &msgIDBig) { rawData.append(contentsOf: $0) }
        rawData.append(contentsOf: [0x11, 0x22])

        let dataToSend = rawData
        Task {
            try? await Task.sleep(nanoseconds: 10_000_000)
            circuit.processIncomingData(dataToSend)
        }

        for await packet in circuit.packetStream {
            XCTAssertEqual(packet.header.sequenceNumber, 50)
            XCTAssertEqual(packet.messageID, 2)
            XCTAssertEqual(packet.payload, Data([0x11, 0x22]))
            break
        }
    }
}
