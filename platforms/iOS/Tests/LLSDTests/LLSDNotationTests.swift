import XCTest
@testable import LLSD

final class LLSDNotationTests: XCTestCase {

    func testNotationScalars() throws {
        XCTAssertEqual(try LLSDNotation.parse("!"), .undefined)
        XCTAssertEqual(try LLSDNotation.parse("1"), .boolean(true))
        XCTAssertEqual(try LLSDNotation.parse("t"), .boolean(true))
        XCTAssertEqual(try LLSDNotation.parse("true"), .boolean(true))
        XCTAssertEqual(try LLSDNotation.parse("0"), .boolean(false))
        XCTAssertEqual(try LLSDNotation.parse("f"), .boolean(false))
        XCTAssertEqual(try LLSDNotation.parse("i100"), .integer(100))
        XCTAssertEqual(try LLSDNotation.parse("r3.14"), .real(3.14))
        XCTAssertEqual(try LLSDNotation.parse("'hello'"), .string("hello"))
    }

    func testNotationUUIDAndMap() throws {
        let notationStr = "{ 'agent_id': u6b29fc40-ca47-1067-b31d-00dd010662da, 'name': 'Avatar' }"
        let parsed = try LLSDNotation.parse(notationStr)

        XCTAssertEqual(parsed["agent_id"]?.asUUID, UUID(uuidString: "6b29fc40-ca47-1067-b31d-00dd010662da"))
        XCTAssertEqual(parsed["name"]?.asString, "Avatar")
    }

    func testNotationSerializationRoundtrip() throws {
        let value: LLSDValue = [
            "id": .uuid(UUID(uuidString: "00000000-0000-0000-0000-000000000000")!),
            "count": 5,
            "tags": ["a", "b"]
        ]

        let notation = LLSDNotation.serialize(value)
        let parsed = try LLSDNotation.parse(notation)

        XCTAssertEqual(parsed["count"]?.asInt, 5)
        XCTAssertEqual(parsed["tags"]?.asArray?.count, 2)
    }

    func testNonASCIIUTF8StringsAndEscapes() throws {
        let nonASCII = try LLSDNotation.parse("'Café ☕ ユーザー'")
        XCTAssertEqual(nonASCII, .string("Café ☕ ユーザー"))

        let escaped = try LLSDNotation.parse("'line1\\nline2\\ttab\\\\slash\\'quote\\\"dquote'")
        XCTAssertEqual(escaped, .string("line1\nline2\ttab\\slash'quote\"dquote"))
    }

    func testSizedStringsAndBinary() throws {
        let sizedStr = try LLSDNotation.parse("s(5)'hello'")
        XCTAssertEqual(sizedStr, .string("hello"))

        let sizedNonASCII = try LLSDNotation.parse("s(4)'Café'")
        XCTAssertEqual(sizedNonASCII, .string("Café"))

        let sizedBin = try LLSDNotation.parse("b(5)'hello'")
        XCTAssertEqual(sizedBin, .binary("hello".data(using: .utf8)!))

        let base64Bin = try LLSDNotation.parse("b64'aGVsbG8='")
        XCTAssertEqual(base64Bin, .binary("hello".data(using: .utf8)!))

        let hexBin = try LLSDNotation.parse("b16'68656c6c6f'")
        XCTAssertEqual(hexBin, .binary("hello".data(using: .utf8)!))
    }

    func testDatesAndURIs() throws {
        let uriVal = try LLSDNotation.parse("l'https://secondlife.com'")
        XCTAssertEqual(uriVal, .uri(URL(string: "https://secondlife.com")!))

        let dateVal = try LLSDNotation.parse("d'2023-01-01T00:00:00Z'")
        if case .date(let date) = dateVal {
            XCTAssertEqual(date.timeIntervalSince1970, 1672531200.0, accuracy: 1.0)
        } else {
            XCTFail("Expected date value")
        }
    }

    func testErrorCharacterOffsetsWithNonASCII() throws {
        // String has non-ASCII characters before unexpected symbol
        let badNotation = "{ 'Café ☕': @ }"
        XCTAssertThrowsError(try LLSDNotation.parse(badNotation)) { error in
            if case LLSDNotationError.unexpectedCharacter(let char, let pos) = error {
                XCTAssertEqual(char, "@")
                // '{ 'Café ☕': @ }':
                // 0:'{', 1:' ', 2:'\'', 3:'C', 4:'a', 5:'f', 6:'é', 7:' ', 8:'☕', 9:'\'', 10:':', 11:' ', 12:'@'
                XCTAssertEqual(pos, 12)
            } else {
                XCTFail("Expected unexpectedCharacter error, got \(error)")
            }
        }
    }
}
