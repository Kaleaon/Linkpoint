import XCTest
@testable import LLSD

struct SampleModel: Codable, Equatable {
    let name: String
    let age: Int32
    let active: Bool
}

final class LLSDCodableTests: XCTestCase {

    func testEncoderAndDecoderXML() throws {
        let model = SampleModel(name: "Tester", age: 30, active: true)
        let encoder = LLSDEncoder(format: .xml)
        let decoder = LLSDDecoder(format: .xml)

        let data = try encoder.encode(model)
        let decoded = try decoder.decode(SampleModel.self, from: data)

        XCTAssertEqual(model, decoded)
    }

    func testEncoderAndDecoderBinary() throws {
        let model = SampleModel(name: "TesterBinary", age: 42, active: false)
        let encoder = LLSDEncoder(format: .binary)
        let decoder = LLSDDecoder(format: .binary)

        let data = try encoder.encode(model)
        let decoded = try decoder.decode(SampleModel.self, from: data)

        XCTAssertEqual(model, decoded)
    }

    func testAutoDetectFormat() throws {
        let value: LLSDValue = ["status": "ok", "code": 200]

        let xmlData = try LLSDEncoder(format: .xml).encode(value)
        let binaryData = try LLSDEncoder(format: .binary).encode(value)
        let notationData = try LLSDEncoder(format: .notation).encode(value)

        let autoDecoder = LLSDDecoder(format: .auto)

        let xmlValue = try autoDecoder.decode(LLSDValue.self, from: xmlData)
        let binaryValue = try autoDecoder.decode(LLSDValue.self, from: binaryData)
        let notationValue = try autoDecoder.decode(LLSDValue.self, from: notationData)

        XCTAssertEqual(xmlValue["code"]?.asInt, 200)
        XCTAssertEqual(binaryValue["code"]?.asInt, 200)
        XCTAssertEqual(notationValue["code"]?.asInt, 200)
    }
}
