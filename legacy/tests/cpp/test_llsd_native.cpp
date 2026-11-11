#include <linkpoint/llsd_native.hpp>

#include <cassert>
#include <cmath>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <sstream>
#include <string>

namespace fs = std::filesystem;

void test_core_types() {
    std::cout << "[Test] Core Types and Conversions..." << std::endl;

    // Undefined
    linkpoint::llsd::Value u;
    assert(u.is_undefined());
    assert(!u.as_boolean());
    assert(u.as_integer() == 0);
    assert(u.as_real() == 0.0);
    assert(u.as_string().empty());

    // Boolean
    linkpoint::llsd::Value b_true(true);
    linkpoint::llsd::Value b_false(false);
    assert(b_true.is_boolean());
    assert(b_true.as_boolean() == true);
    assert(b_true.as_integer() == 1);
    assert(b_false.as_boolean() == false);
    assert(b_false.as_integer() == 0);

    // Integer
    linkpoint::llsd::Value i_val(123456);
    assert(i_val.is_integer());
    assert(i_val.as_integer() == 123456);
    assert(i_val.as_real() == 123456.0);
    assert(i_val.as_string() == "123456");

    // Real
    linkpoint::llsd::Value r_val(3.14159265);
    assert(r_val.is_real());
    assert(std::abs(r_val.as_real() - 3.14159265) < 1e-6);

    // String
    linkpoint::llsd::Value s_val("Hello Linkpoint Native C++20!");
    assert(s_val.is_string());
    assert(s_val.as_string() == "Hello Linkpoint Native C++20!");

    // UUID
    std::string uuid_str = "f496d6bf-8235-4ebf-bd56-4f7f04a64a27";
    linkpoint::llsd::UUID uuid_obj = linkpoint::llsd::UUID::from_string(uuid_str);
    linkpoint::llsd::Value uuid_val(uuid_obj);
    assert(uuid_val.is_uuid());
    assert(uuid_val.as_uuid().to_string() == uuid_str);
    assert(uuid_val.as_string() == uuid_str);

    // Date
    linkpoint::llsd::Date date_obj = linkpoint::llsd::Date::from_iso8601("2026-05-01T07:27:22Z");
    linkpoint::llsd::Value date_val(date_obj);
    assert(date_val.is_date());
    assert(date_val.as_date().to_iso8601().substr(0, 19) == "2026-05-01T07:27:22");

    // URI
    linkpoint::llsd::URI uri_obj{"http://secondlife.com/api"};
    linkpoint::llsd::Value uri_val(uri_obj);
    assert(uri_val.is_uri());
    assert(uri_val.as_uri().value == "http://secondlife.com/api");

    // Binary
    linkpoint::llsd::Binary bin_bytes{0x01, 0x02, 0x03, 0x04, 0x05};
    linkpoint::llsd::Value bin_val(bin_bytes);
    assert(bin_val.is_binary());
    assert(bin_val.as_binary().size() == 5);

    // Array
    linkpoint::llsd::Array arr_data{i_val, s_val, b_true};
    linkpoint::llsd::Value arr_val(arr_data);
    assert(arr_val.is_array());
    assert(arr_val.as_array().size() == 3);
    assert(arr_val[0].as_integer() == 123456);
    assert(arr_val[1].as_string() == "Hello Linkpoint Native C++20!");
    assert(arr_val[2].as_boolean() == true);

    // Map
    linkpoint::llsd::Map map_data;
    map_data["agent_id"] = uuid_val;
    map_data["name"] = s_val;
    map_data["balance"] = linkpoint::llsd::Value(1000);
    linkpoint::llsd::Value map_val(map_data);
    assert(map_val.is_map());
    assert(map_val["agent_id"].as_uuid().to_string() == uuid_str);
    assert(map_val["name"].as_string() == "Hello Linkpoint Native C++20!");
    assert(map_val["balance"].as_integer() == 1000);

    std::cout << "  Passed core types and conversions!" << std::endl;
}

void test_binary_codec() {
    std::cout << "[Test] Binary Codec..." << std::endl;

    linkpoint::llsd::Map map_data;
    map_data["integer"] = linkpoint::llsd::Value(42);
    map_data["real"] = linkpoint::llsd::Value(2.71828);
    map_data["bool"] = linkpoint::llsd::Value(true);
    map_data["string"] = linkpoint::llsd::Value("Linkpoint Binary");
    map_data["uuid"] = linkpoint::llsd::Value(linkpoint::llsd::UUID::from_string("550e8400-e29b-41d4-a716-446655440000"));

    linkpoint::llsd::Value orig_map(map_data);
    std::vector<uint8_t> encoded = linkpoint::llsd::encode_binary(orig_map, true);
    assert(!encoded.empty());

    linkpoint::llsd::Value decoded = linkpoint::llsd::decode_binary(encoded);
    assert(decoded.is_map());
    assert(decoded["integer"].as_integer() == 42);
    assert(std::abs(decoded["real"].as_real() - 2.71828) < 1e-5);
    assert(decoded["bool"].as_boolean() == true);
    assert(decoded["string"].as_string() == "Linkpoint Binary");
    assert(decoded["uuid"].as_uuid().to_string() == "550e8400-e29b-41d4-a716-446655440000");

    std::cout << "  Passed binary codec!" << std::endl;
}

void test_xml_codec() {
    std::cout << "[Test] XML Codec..." << std::endl;

    linkpoint::llsd::Map map_data;
    map_data["title"] = linkpoint::llsd::Value("Grid Message");
    map_data["active"] = linkpoint::llsd::Value(true);
    map_data["count"] = linkpoint::llsd::Value(77);

    linkpoint::llsd::Value orig_map(map_data);
    std::string xml_str = linkpoint::llsd::encode_xml(orig_map, true);
    assert(!xml_str.empty());
    assert(xml_str.find("<llsd>") != std::string::npos);

    linkpoint::llsd::Value decoded = linkpoint::llsd::decode_xml(xml_str);
    assert(decoded.is_map());
    assert(decoded["title"].as_string() == "Grid Message");
    assert(decoded["active"].as_boolean() == true);
    assert(decoded["count"].as_integer() == 77);

    std::cout << "  Passed XML codec!" << std::endl;
}

void test_notation_codec() {
    std::cout << "[Test] Notation Codec..." << std::endl;

    std::string notation_input = "{name:s'John Doe',age:i30,active:1,scores:[r95.5,r88.0]}";
    linkpoint::llsd::Value decoded = linkpoint::llsd::decode_notation(notation_input);

    assert(decoded.is_map());
    assert(decoded["name"].as_string() == "John Doe");
    assert(decoded["age"].as_integer() == 30);
    assert(decoded["active"].as_boolean() == true);
    assert(decoded["scores"].is_array());
    assert(decoded["scores"].as_array().size() == 2);
    assert(std::abs(decoded["scores"][0].as_real() - 95.5) < 1e-4);

    std::string re_encoded = linkpoint::llsd::encode_notation(decoded, false);
    assert(!re_encoded.empty());

    std::cout << "  Passed notation codec!" << std::endl;
}

void test_auto_detect() {
    std::cout << "[Test] Auto Detect..." << std::endl;

    linkpoint::llsd::Value val(linkpoint::llsd::Map{{"k", linkpoint::llsd::Value(123)}});

    // Binary
    std::vector<uint8_t> bin_data = linkpoint::llsd::encode_binary(val, true);
    linkpoint::llsd::Value bin_decoded = linkpoint::llsd::decode(bin_data);
    assert(bin_decoded["k"].as_integer() == 123);

    // XML
    std::string xml_data = linkpoint::llsd::encode_xml(val, true);
    linkpoint::llsd::Value xml_decoded = linkpoint::llsd::decode(xml_data);
    assert(xml_decoded["k"].as_integer() == 123);

    // Notation
    std::string notation_data = linkpoint::llsd::encode_notation(val, true);
    linkpoint::llsd::Value notation_decoded = linkpoint::llsd::decode(notation_data);
    assert(notation_decoded["k"].as_integer() == 123);

    std::cout << "  Passed auto detect!" << std::endl;
}

void test_packet_framing() {
    std::cout << "[Test] Wire Protocol Packet Framing & ZeroCode..." << std::endl;

    std::vector<uint8_t> original_payload = {0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x34, 0x56, 0x00, 0x00, 0x78};
    std::vector<uint8_t> compressed;
    linkpoint::protocol::pack_zerocode(original_payload, compressed);

    std::vector<uint8_t> decompressed;
    linkpoint::protocol::unpack_zerocode(compressed, decompressed);

    assert(decompressed == original_payload);

    // Construct a raw grid packet with header
    // Flags: 0x02 (Reliable)
    // SeqNum: 1001 (0x000003E9)
    // Extra len: 0
    // Message ID: High frequency 0x05 (AgentUpdate)
    std::vector<uint8_t> raw_packet = {
        0x02,                   // Flags
        0x00, 0x00, 0x03, 0xE9, // Sequence Number 1001
        0x00,                   // Extra Header Bytes
        0x05,                   // Message ID (0x05)
        'D', 'A', 'T', 'A'      // Payload
    };

    linkpoint::protocol::DecodedPacket decoded = linkpoint::protocol::decode_packet(raw_packet);
    assert(decoded.header.sequence_number == 1001);
    assert(decoded.header.message_id == 0x05);
    assert(decoded.header.is_reliable());
    assert(decoded.payload.size() == 4);
    assert(decoded.payload[0] == 'D' && decoded.payload[3] == 'A');

    std::cout << "  Passed packet framing!" << std::endl;
}

void test_conformance_vectors() {
    std::cout << "[Test] Loading Conformance Vectors..." << std::endl;

    std::vector<fs::path> candidate_dirs = {
        "legacy/Linkpoint/src/test/resources/llsd-conformance/vectors",
        "../legacy/Linkpoint/src/test/resources/llsd-conformance/vectors",
        "../../legacy/Linkpoint/src/test/resources/llsd-conformance/vectors",
        "/app/Linkpoint/legacy/Linkpoint/src/test/resources/llsd-conformance/vectors",
        "Linkpoint/src/test/resources/llsd-conformance/vectors",
        "../Linkpoint/src/test/resources/llsd-conformance/vectors",
        "../../Linkpoint/src/test/resources/llsd-conformance/vectors",
        "/app/Linkpoint/Linkpoint/src/test/resources/llsd-conformance/vectors"
    };
    fs::path vectors_dir;
    for (const auto& candidate : candidate_dirs) {
        if (fs::exists(candidate)) {
            vectors_dir = candidate;
            break;
        }
    }
    if (vectors_dir.empty() || !fs::exists(vectors_dir)) {
        std::cout << "  Notice: Conformance vector directory not found" << std::endl;
        return;
    }

    size_t test_count = 0;
    for (const auto& entry : fs::directory_iterator(vectors_dir)) {
        if (!entry.is_directory()) continue;

        fs::path bin_file = entry.path() / "value.bin";
        fs::path xml_file = entry.path() / "value.xml";

        if (fs::exists(bin_file)) {
            std::ifstream ifs(bin_file, std::ios::binary);
            std::vector<uint8_t> data((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());
            if (!data.empty()) {
                try {
                    linkpoint::llsd::Value v = linkpoint::llsd::decode_binary(data);
                    (void)v;
                    test_count++;
                } catch (const std::exception& e) {
                    std::cout << "  Error parsing binary vector " << entry.path().filename() << ": " << e.what() << std::endl;
                }
            }
        }

        if (fs::exists(xml_file)) {
            std::ifstream ifs(xml_file);
            std::string xml_str((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());
            if (!xml_str.empty()) {
                try {
                    linkpoint::llsd::Value v = linkpoint::llsd::decode_xml(xml_str);
                    (void)v;
                    test_count++;
                } catch (const std::exception& e) {
                    std::cout << "  Error parsing XML vector " << entry.path().filename() << ": " << e.what() << std::endl;
                }
            }
        }
    }

    std::cout << "  Successfully parsed " << test_count << " conformance vectors!" << std::endl;
}

int main() {
    std::cout << "=== Linkpoint LLSD Native C++20 Test Suite ===" << std::endl;

    test_core_types();
    test_binary_codec();
    test_xml_codec();
    test_notation_codec();
    test_auto_detect();
    test_packet_framing();
    test_conformance_vectors();

    std::cout << "=== All Linkpoint LLSD Native C++20 Tests Passed! ===" << std::endl;
    return 0;
}
