import XCTest
import Foundation
@testable import LLSD

final class LLSD31TestVectorsTests: XCTestCase {

    private func find31VectorsFile() -> URL? {
        let fileManager = FileManager.default
        let currentDir = URL(fileURLWithPath: fileManager.currentDirectoryPath)
        let candidates = [
            currentDir.appendingPathComponent("crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json"),
            currentDir.appendingPathComponent("../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json"),
            currentDir.appendingPathComponent("../../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json"),
            currentDir.appendingPathComponent("../../../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json"),
            URL(fileURLWithPath: "/app/Linkpoint/crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json")
        ]
        return candidates.first { fileManager.fileExists(atPath: $0.path) }
    }

    func test31CanonicalLLSDVectors() throws {
        guard let url = find31VectorsFile() else {
            XCTFail("Central vector fixture file llsd_31_test_vectors.json not found!")
            return
        }

        let data = try Data(contentsOf: url)
        guard let jsonArray = try JSONSerialization.jsonObject(with: data) as? [[String: Any]] else {
            XCTFail("Failed to parse llsd_31_test_vectors.json as JSON array")
            return
        }

        XCTAssertEqual(jsonArray.count, 31, "Fixture must contain exactly 31 test vectors")

        for (index, dict) in jsonArray.enumerated() {
            XCTAssertNotNil(dict["id"], "Vector #\(index) missing id")
            XCTAssertNotNil(dict["name"], "Vector #\(index) missing name")
            XCTAssertNotNil(dict["type"], "Vector #\(index) missing type")
            XCTAssertNotNil(dict["xml_snippet"], "Vector #\(index) missing xml_snippet")
            XCTAssertNotNil(dict["notation_snippet"], "Vector #\(index) missing notation_snippet")
        }
    }
}
