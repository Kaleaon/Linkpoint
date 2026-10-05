import XCTest
@testable import LLSDXMLRPC
@testable import LLSD

final class XMLRPCClientTests: XCTestCase {

    func testXMLRPCResponseParsing() throws {
        let xmlResponse = """
        <?xml version="1.0"?>
        <methodResponse>
          <params>
            <param>
              <value>
                <struct>
                  <member>
                    <name>login</name>
                    <value><string>true</string></value>
                  </member>
                  <member>
                    <name>agent_id</name>
                    <value><string>6b29fc40-ca47-1067-b31d-00dd010662da</string></value>
                  </member>
                  <member>
                    <name>session_id</name>
                    <value><string>11111111-2222-3333-4444-555555555555</string></value>
                  </member>
                  <member>
                    <name>first_name</name>
                    <value><string>Philip</string></value>
                  </member>
                  <member>
                    <name>last_name</name>
                    <value><string>Linden</string></value>
                  </member>
                </struct>
              </value>
            </param>
          </params>
        </methodResponse>
        """

        let client = XMLRPCClient()
        let parsedValue = try client.parseResponseXML(Data(xmlResponse.utf8))
        let llsdVal = parsedValue.asLLSDValue

        XCTAssertEqual(llsdVal["login"]?.asString, "true")
        XCTAssertEqual(llsdVal["agent_id"]?.asUUID, UUID(uuidString: "6b29fc40-ca47-1067-b31d-00dd010662da"))
        XCTAssertEqual(llsdVal["first_name"]?.asString, "Philip")
        XCTAssertEqual(llsdVal["last_name"]?.asString, "Linden")
    }

    func testXMLRPCValueToXML() {
        let requestStruct: XMLRPCValue = .structure([
            "first": .string("Philip"),
            "last": .string("Linden"),
            "passwd": .string("secret")
        ])

        let xml = requestStruct.toXML()
        XCTAssertTrue(xml.contains("<name>first</name>"))
        XCTAssertTrue(xml.contains("<string>Philip</string>"))
    }
}
