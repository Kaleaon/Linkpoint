#include <iostream>
#include <cassert>
#include <vector>
#include <cstring>

#define GODOT_CPP_STUB 1

#include "../src/linkpoint_protocol.hpp"
#include "../src/generated/schema/GodotAssetSchemaAdapter.hpp"
#include "../src/generated/protocol/GodotProtocol.hpp"

void test_zerocode_decompression() {
    godot::LinkpointProtocol protocol;

    // Test vector 1: Simple non-zero bytes
    godot::PackedByteArray input1 = { 0x01, 0x02, 0x03, 0x04 };
    godot::PackedByteArray result1 = protocol.decompress_zerocoded(input1);
    assert(result1.size() == 4);
    assert(result1[0] == 0x01 && result1[3] == 0x04);

    // Test vector 2: Zero sequence expansion (0x00 0x03 -> three zero bytes)
    godot::PackedByteArray input2 = { 0xAA, 0x00, 0x03, 0xBB };
    godot::PackedByteArray result2 = protocol.decompress_zerocoded(input2);
    assert(result2.size() == 5);
    assert(result2[0] == 0xAA);
    assert(result2[1] == 0x00 && result2[2] == 0x00 && result2[3] == 0x00);
    assert(result2[4] == 0xBB);

    // Test vector 3: Literal zero byte encoding (0x00 0x00 -> single 0x00)
    godot::PackedByteArray input3 = { 0xCC, 0x00, 0x00, 0xDD };
    godot::PackedByteArray result3 = protocol.decompress_zerocoded(input3);
    assert(result3.size() == 3);
    assert(result3[0] == 0xCC);
    assert(result3[1] == 0x00);
    assert(result3[2] == 0xDD);

    std::cout << "[PASS] Zerocode Decompression Tests Passed Successfully!" << std::endl;
}

void test_schema_adapter() {
    using namespace UnifiedLLSD;

    // Test LOD Key lookup
    assert(std::string(GetLodKey(DetailLevel::Highest)) == "high_lod");
    assert(std::string(GetLodKey(DetailLevel::High)) == "high_lod");
    assert(std::string(GetLodKey(DetailLevel::Medium)) == "medium_lod");
    assert(std::string(GetLodKey(DetailLevel::Low)) == "low_lod");
    assert(std::string(GetLodKey(DetailLevel::Lowest)) == "lowest_lod");

    // Test Texture Transform Adapter
    GodotTextureTransformAdapter xform;
    xform.scaleX = 2.0f;
    xform.scaleY = 3.0f;
    xform.rotation = 0.5f;
    xform.offsetX = 0.1f;
    xform.offsetY = 0.2f;

    godot::PackedFloat32Array packed = xform.GetPacked();
    assert(packed.size() == 8);
    assert(packed[0] == 2.0f);
    assert(packed[1] == 3.0f);
    assert(packed[2] == 0.5f);
    assert(packed[4] == 0.1f);
    assert(packed[5] == 0.2f);

    godot::PackedFloat32Array tight = xform.GetPackedTight();
    assert(tight.size() == 5);
    assert(tight[0] == 2.0f);
    assert(tight[1] == 3.0f);
    assert(tight[2] == 0.5f);
    assert(tight[3] == 0.1f);
    assert(tight[4] == 0.2f);

    std::cout << "[PASS] Asset Schema Adapter Tests Passed Successfully!" << std::endl;
}

void test_mesh_marshalling() {
    godot::LinkpointProtocol protocol;

    godot::PackedFloat32Array positions = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f };
    godot::PackedFloat32Array normals = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };
    godot::PackedFloat32Array uvs = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };
    godot::PackedInt32Array indices = { 0, 1, 2 };

    godot::Ref<godot::ArrayMesh> mesh = protocol.create_array_mesh(positions, normals, uvs, indices);
    assert(mesh.is_valid());

    std::cout << "[PASS] Mesh Buffer Marshalling Tests Passed Successfully!" << std::endl;
}

int main() {
    std::cout << "Running GDExtension C++ Native Extension Verification..." << std::endl;
    test_zerocode_decompression();
    test_schema_adapter();
    test_mesh_marshalling();
    std::cout << "All GDExtension C++ Tests Completed Successfully!" << std::endl;
    return 0;
}
