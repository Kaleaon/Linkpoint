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
}
