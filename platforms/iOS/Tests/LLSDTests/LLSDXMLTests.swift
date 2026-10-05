import XCTest
@testable import LLSD

final class LLSDXMLTests: XCTestCase {

    func testXMLParsing() throws {
        let xmlStr = """
        <?xml version="1.0" encoding="UTF-8"?>
        <llsd>
          <map>
            <key>agent_id</key>
            <uuid>6b29fc40-ca47-1067-b31d-00dd010662da</uuid>
            <key>balance</key>
            <integer>500</integer>
            <key>online</key>
            <boolean>1</boolean>
          </map>
        </llsd>
        """

        let parsed = try LLSDXML.parse(xmlStr)
        XCTAssertEqual(parsed["agent_id"]?.asUUID, UUID(uuidString: "6b29fc40-ca47-1067-b31d-00dd010662da"))
        XCTAssertEqual(parsed["balance"]?.asInt, 500)
        XCTAssertEqual(parsed["online"]?.asBool, true)
    }

    func testXMLSerializationRoundtrip() throws {
        let value: LLSDValue = [
            "region": "Ahern",
            "coords": [128, 128, 25]
        ]

        let xml = LLSDXML.serialize(value)
        let parsed = try LLSDXML.parse(xml)

        XCTAssertEqual(parsed["region"]?.asString, "Ahern")
        XCTAssertEqual(parsed["coords"]?.asArray?.count, 3)
    }
}
