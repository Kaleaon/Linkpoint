#include "linkpoint_protocol.hpp"
#include "generated/schema/GodotAssetSchemaAdapter.hpp"

namespace godot {

void LinkpointProtocol::_bind_methods() {
#if __has_include(<godot_cpp/core/class_db.hpp>) && !defined(GODOT_CPP_STUB)
    ClassDB::bind_method(D_METHOD("decompress_zerocoded", "src", "dest_capacity"), &LinkpointProtocol::decompress_zerocoded, DEFVAL(65536));
    ClassDB::bind_method(D_METHOD("create_array_mesh", "positions", "normals", "uvs", "indices"), &LinkpointProtocol::create_array_mesh);
    ClassDB::bind_method(D_METHOD("pack_texture_transform", "scale_x", "scale_y", "rotation", "offset_x", "offset_y"), &LinkpointProtocol::pack_texture_transform);
#endif
}

LinkpointProtocol::LinkpointProtocol() {}
LinkpointProtocol::~LinkpointProtocol() {}

PackedByteArray LinkpointProtocol::decompress_zerocoded(const PackedByteArray &src, int dest_capacity) {
    PackedByteArray dest;
    int src_len = (int)src.size();
    if (src_len == 0) return dest;

    dest.resize(dest_capacity);
    const uint8_t *src_ptr = src.ptr();
    uint8_t *dest_ptr = dest.ptrw();

    size_t i = 0;
    size_t out_len = 0;
    while (i < (size_t)src_len && out_len < (size_t)dest_capacity) {
        uint8_t b = src_ptr[i];
        if (b == 0) {
            if (i + 1 < (size_t)src_len) {
                uint8_t count = src_ptr[i + 1];
                if (count == 0) {
                    dest_ptr[out_len++] = 0;
                    i += 2;
                } else {
                    for (uint8_t k = 0; k < count && out_len < (size_t)dest_capacity; k++) {
                        dest_ptr[out_len++] = 0;
                    }
                    i += 2;
                }
            } else {
                dest_ptr[out_len++] = 0;
                i += 1;
            }
        } else {
            dest_ptr[out_len++] = b;
            i += 1;
        }
    }
    dest.resize(out_len);
    return dest;
}

Ref<ArrayMesh> LinkpointProtocol::create_array_mesh(const PackedFloat32Array &positions,
                                                   const PackedFloat32Array &normals,
                                                   const PackedFloat32Array &uvs,
                                                   const PackedInt32Array &indices) {
    Ref<ArrayMesh> mesh;
#if __has_include(<godot_cpp/classes/array_mesh.hpp>) && !defined(GODOT_CPP_STUB)
    mesh.instantiate();
    Array surface_arrays;
    surface_arrays.resize(ArrayMesh::ARRAY_MAX);

    PackedVector3Array varr_pos;
    int pos_count = (int)positions.size() / 3;
    varr_pos.resize(pos_count);
    if (pos_count > 0) {
        const float *src_p = positions.ptr();
        Vector3 *dst_p = varr_pos.ptrw();
        for (int i = 0; i < pos_count; ++i) {
            dst_p[i] = Vector3(src_p[i * 3], src_p[i * 3 + 1], src_p[i * 3 + 2]);
        }
    }
    surface_arrays[ArrayMesh::ARRAY_VERTEX] = varr_pos;

    if (normals.size() > 0) {
        PackedVector3Array varr_norm;
        int norm_count = (int)normals.size() / 3;
        varr_norm.resize(norm_count);
        const float *src_n = normals.ptr();
        Vector3 *dst_n = varr_norm.ptrw();
        for (int i = 0; i < norm_count; ++i) {
            dst_n[i] = Vector3(src_n[i * 3], src_n[i * 3 + 1], src_n[i * 3 + 2]);
        }
        surface_arrays[ArrayMesh::ARRAY_NORMAL] = varr_norm;
    }

    if (uvs.size() > 0) {
        PackedVector2Array varr_uv;
        int uv_count = (int)uvs.size() / 2;
        varr_uv.resize(uv_count);
        const float *src_uv = uvs.ptr();
        Vector2 *dst_uv = varr_uv.ptrw();
        for (int i = 0; i < uv_count; ++i) {
            dst_uv[i] = Vector2(src_uv[i * 2], src_uv[i * 2 + 1]);
        }
        surface_arrays[ArrayMesh::ARRAY_TEX_UV] = varr_uv;
    }

    if (indices.size() > 0) {
        surface_arrays[ArrayMesh::ARRAY_INDEX] = indices;
    }

    mesh->add_surface_from_arrays(Mesh::PRIMITIVE_TRIANGLES, surface_arrays);
#else
    mesh = Ref<ArrayMesh>(new ArrayMesh());
#endif
    return mesh;
}

PackedFloat32Array LinkpointProtocol::pack_texture_transform(float scale_x, float scale_y, float rotation, float offset_x, float offset_y) {
    UnifiedLLSD::GodotTextureTransformAdapter adapter;
    adapter.scaleX = scale_x;
    adapter.scaleY = scale_y;
    adapter.rotation = rotation;
    adapter.offsetX = offset_x;
    adapter.offsetY = offset_y;
    return adapter.GetPacked();
}

} // namespace godot
