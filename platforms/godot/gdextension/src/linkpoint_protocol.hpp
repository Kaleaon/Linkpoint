#ifndef LINKPOINT_PROTOCOL_HPP
#define LINKPOINT_PROTOCOL_HPP

#include <cstdint>
#include <cstddef>

#if __has_include(<godot_cpp/classes/ref_counted.hpp>) && !defined(GODOT_CPP_STUB)
#include <godot_cpp/classes/ref_counted.hpp>
#include <godot_cpp/classes/array_mesh.hpp>
#include <godot_cpp/classes/ref.hpp>
#include <godot_cpp/core/class_db.hpp>
#include <godot_cpp/variant/packed_byte_array.hpp>
#include <godot_cpp/variant/packed_float32_array.hpp>
#include <godot_cpp/variant/packed_int32_array.hpp>
#include <godot_cpp/variant/packed_vector3_array.hpp>
#include <godot_cpp/variant/packed_vector2_array.hpp>
#include <godot_cpp/variant/array.hpp>
#include <godot_cpp/variant/dictionary.hpp>
#include <godot_cpp/variant/string.hpp>
#else
#ifndef GODOT_CPP_STUB_DEFINED
#define GODOT_CPP_STUB_DEFINED
#include <vector>
#include <string>
#ifndef GDCLASS
#define GDCLASS(m_class, m_inherits)
#endif
namespace godot {
    class PackedByteArray : public std::vector<uint8_t> {
    public:
        using std::vector<uint8_t>::vector;
        const uint8_t* ptr() const { return data(); }
        uint8_t* ptrw() { return data(); }
        void append(uint8_t v) { push_back(v); }
    };
    class PackedFloat32Array : public std::vector<float> {
    public:
        using std::vector<float>::vector;
        const float* ptr() const { return data(); }
        float* ptrw() { return data(); }
        void append(float v) { push_back(v); }
    };
    class PackedInt32Array : public std::vector<int32_t> {
    public:
        using std::vector<int32_t>::vector;
        const int32_t* ptr() const { return data(); }
        int32_t* ptrw() { return data(); }
        void append(int32_t v) { push_back(v); }
    };
    class RefCounted {};
    class ArrayMesh {
    public:
        int get_surface_count() const { return 1; }
    };
    template <typename T>
    class Ref {
        T* obj;
    public:
        Ref() : obj(nullptr) {}
        Ref(T* o) : obj(o) {}
        T* operator->() { return obj; }
        T* ptr() const { return obj; }
        bool is_valid() const { return obj != nullptr; }
    };
}
#endif // GODOT_CPP_STUB_DEFINED
#endif

namespace godot {

class LinkpointProtocol : public RefCounted {
    GDCLASS(LinkpointProtocol, RefCounted)

protected:
    static void _bind_methods();

public:
    LinkpointProtocol();
    ~LinkpointProtocol();

    PackedByteArray decompress_zerocoded(const PackedByteArray &src, int dest_capacity = 65536);

    Ref<ArrayMesh> create_array_mesh(const PackedFloat32Array &positions,
                                    const PackedFloat32Array &normals,
                                    const PackedFloat32Array &uvs,
                                    const PackedInt32Array &indices);

    PackedFloat32Array pack_texture_transform(float scale_x, float scale_y, float rotation, float offset_x, float offset_y);
};

} // namespace godot

#endif // LINKPOINT_PROTOCOL_HPP
