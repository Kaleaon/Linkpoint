import XCTest
@testable import LLSD

final class LLSDBinaryTests: XCTestCase {

    func testBinaryUUIDParsingNilUUID() throws {
        // Reference byte vector for nil UUID: 'u' (0x75) followed by 16 zero bytes
        let bytes: [UInt8] = [0x75, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        let data = Data(bytes)

        let parsed = try LLSDBinary.parse(data)
        XCTAssertEqual(parsed, .uuid(UUID(uuidString: "00000000-0000-0000-0000-000000000000")!))
    }

    func testBinaryUUIDParsingReferenceVector() throws {
        // Reference byte vector for UUID "6b29fc40-ca47-1067-b31d-00dd010662da"
        let bytes: [UInt8] = [
            0x75, // 'u' tag
            0x6b, 0x29, 0xfc, 0x40,
            0xca, 0x47,
            0x10, 0x67,
            0xb3, 0x1d,
            0x00, 0xdd, 0x01, 0x06, 0x62, 0xda
        ]
        let data = Data(bytes)

        let parsed = try LLSDBinary.parse(data)
        let expectedUUID = UUID(uuidString: "6b29fc40-ca47-1067-b31d-00dd010662da")!
        XCTAssertEqual(parsed, .uuid(expectedUUID))

        // Serialize back to binary
        let reencoded = LLSDBinary.serialize(parsed, includeHeader: false)
        XCTAssertEqual(reencoded, data)
    }

    func testBinaryIntegerAndRealEncoding() throws {
        let valueMap: LLSDValue = [
            "int": 42,
            "negative": -1024,
            "real": 3.1415926535
        ]

        let serialized = LLSDBinary.serialize(valueMap, includeHeader: true)
        let decoded = try LLSDBinary.parse(serialized)

        XCTAssertEqual(decoded["int"]?.asInt, 42)
        XCTAssertEqual(decoded["negative"]?.asInt, -1024)
        XCTAssertEqual(decoded["real"]?.asReal ?? 0.0, 3.1415926535, accuracy: 0.0000001)
    }

    func testBinaryDateEncoding() throws {
        let now = Date(timeIntervalSince1970: 1700000000.0) // Fixed timestamp
        let value: LLSDValue = .date(now)

        let serialized = LLSDBinary.serialize(value, includeHeader: false)
        let decoded = try LLSDBinary.parse(serialized)

        XCTAssertEqual(decoded.asDate?.timeIntervalSince1970, now.timeIntervalSince1970)
    }

    func testBinaryArrayAndMapContainers() throws {
        let original: LLSDValue = [
            "name": "Linkpoint iOS",
            "active": true,
            "items": [1, 2, 3]
        ]

        let serialized = LLSDBinary.serialize(original, includeHeader: true)
        let parsed = try LLSDBinary.parse(serialized)

        XCTAssertEqual(parsed["name"]?.asString, "Linkpoint iOS")
        XCTAssertEqual(parsed["active"]?.asBool, true)
        XCTAssertEqual(parsed["items"]?.asArray?.count, 3)
    }
}
