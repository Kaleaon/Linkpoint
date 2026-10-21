import os

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class GodotEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Godot", ".cpp")

    def emit(self, ast: ProtocolAST, out_dir: str) -> dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        header_code = self._generate_godot_header(ast)
        source_code = self._generate_godot_source(ast)

        header_file = os.path.join(out_dir, "GodotProtocol.hpp")
        source_file = os.path.join(out_dir, "GodotProtocol.cpp")

        results = {header_file: header_code, source_file: source_code}

        for path, content in results.items():
            with open(path, "w", encoding="utf-8", newline="\n") as f:
                f.write(content)

        return results

    def _generate_godot_header(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("#ifndef LINKPOINT_GODOT_PROTOCOL_HPP")
        out.append("#define LINKPOINT_GODOT_PROTOCOL_HPP\n")
        out.append("#include <cstdint>")
        out.append("#include <cstddef>\n")

        out.append(
            "#if __has_include(<godot_cpp/classes/ref_counted.hpp>) && !defined(GODOT_CPP_STUB)"
        )
        out.append("#include <godot_cpp/classes/ref_counted.hpp>")
        out.append("#include <godot_cpp/core/class_db.hpp>")
        out.append("#include <godot_cpp/variant/packed_byte_array.hpp>")
        out.append("#include <godot_cpp/variant/string.hpp>")
        out.append("#else")
        out.append("#ifndef GODOT_CPP_STUB_DEFINED")
        out.append("#define GODOT_CPP_STUB_DEFINED")
        out.append("#include <vector>")
        out.append("#include <string>")
        out.append("#include <cstdint>")
        out.append("#ifndef GDCLASS")
        out.append("#define GDCLASS(m_class, m_inherits)")
        out.append("#endif")
        out.append("namespace godot {")
        out.append("    class PackedByteArray : public std::vector<uint8_t> {")
        out.append("    public:")
        out.append("        using std::vector<uint8_t>::vector;")
        out.append("        const uint8_t* ptr() const { return data(); }")
        out.append("        uint8_t* ptrw() { return data(); }")
        out.append("        void append(uint8_t v) { push_back(v); }")
        out.append("    };")
        out.append("    class PackedFloat32Array : public std::vector<float> {")
        out.append("    public:")
        out.append("        using std::vector<float>::vector;")
        out.append("        const float* ptr() const { return data(); }")
        out.append("        float* ptrw() { return data(); }")
        out.append("        void append(float v) { push_back(v); }")
        out.append("    };")
        out.append("    class PackedInt32Array : public std::vector<int32_t> {")
        out.append("    public:")
        out.append("        using std::vector<int32_t>::vector;")
        out.append("        const int32_t* ptr() const { return data(); }")
        out.append("        int32_t* ptrw() { return data(); }")
        out.append("        void append(int32_t v) { push_back(v); }")
        out.append("    };")
        out.append("    class RefCounted {};")
        out.append("    class ArrayMesh {")
        out.append("    public:")
        out.append("        int get_surface_count() const { return 1; }")
        out.append("    };")
        out.append("    template <typename T>")
        out.append("    class Ref {")
        out.append("        T* obj;")
        out.append("    public:")
        out.append("        Ref() : obj(nullptr) {}")
        out.append("        Ref(T* o) : obj(o) {}")
        out.append("        T* operator->() { return obj; }")
        out.append("        T* ptr() const { return obj; }")
        out.append("        bool is_valid() const { return obj != nullptr; }")
        out.append("    };")
        out.append("}")
        out.append("#endif // GODOT_CPP_STUB_DEFINED")
        out.append("#endif\n")

        out.append(f'static const char* PROTOCOL_TEMPLATE_VERSION = "{ast.version}";\n')

        out.append("namespace godot {\n")

        out.append("class GodotProtocolDecompressor : public RefCounted {")
        out.append("    GDCLASS(GodotProtocolDecompressor, RefCounted)\n")
        out.append("protected:")
        out.append("    static void _bind_methods();\n")
        out.append("public:")
        out.append("    GodotProtocolDecompressor();")
        out.append("    ~GodotProtocolDecompressor();\n")
        out.append(
            "    PackedByteArray decompress_zerocoded(const PackedByteArray &src, int dest_capacity = 65536);"
        )
        out.append("};\n")

        # Message struct / class constants
        for msg in ast.messages:
            out.append(f"// Message: {msg.name}")
            out.append(
                f"constexpr uint32_t MSG_ID_{msg.name.upper()} = {msg.message_number};"
            )
            out.append(f"class Godot{msg.name}Packet : public RefCounted {{")
            out.append(f"    GDCLASS(Godot{msg.name}Packet, RefCounted)")
            out.append("private:")
            out.append(f"    uint32_t message_id = MSG_ID_{msg.name.upper()};")
            out.append("    bool is_zerocoded = false;")
            out.append("protected:")
            out.append("    static void _bind_methods();")
            out.append("public:")
            out.append(f"    Godot{msg.name}Packet() = default;")
            out.append(f"    ~Godot{msg.name}Packet() = default;")
            out.append("    uint32_t get_message_id() const { return message_id; }")
            out.append("    void set_message_id(uint32_t p_id) { message_id = p_id; }")
            out.append("    bool get_is_zerocoded() const { return is_zerocoded; }")
            out.append(
                "    void set_is_zerocoded(bool p_zerocoded) { is_zerocoded = p_zerocoded; }"
            )
            out.append("};\n")

        out.append("} // namespace godot\n")
        out.append("#endif // LINKPOINT_GODOT_PROTOCOL_HPP")
        return "\n".join(out)

    def _generate_godot_source(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append('#include "GodotProtocol.hpp"\n')
        out.append("namespace godot {\n")

        out.append("void GodotProtocolDecompressor::_bind_methods() {")
        out.append(
            "#if __has_include(<godot_cpp/core/class_db.hpp>) && !defined(GODOT_CPP_STUB)"
        )
        out.append(
            '    ClassDB::bind_method(D_METHOD("decompress_zerocoded", "src", "dest_capacity"), &GodotProtocolDecompressor::decompress_zerocoded, DEFVAL(65536));'
        )
        out.append("#endif")
        out.append("}\n")

        out.append("GodotProtocolDecompressor::GodotProtocolDecompressor() {}\n")
        out.append("GodotProtocolDecompressor::~GodotProtocolDecompressor() {}\n")

        out.append("""PackedByteArray GodotProtocolDecompressor::decompress_zerocoded(const PackedByteArray &src, int dest_capacity) {
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
""")

        for msg in ast.messages:
            out.append(f"void Godot{msg.name}Packet::_bind_methods() {{")
            out.append(
                "#if __has_include(<godot_cpp/core/class_db.hpp>) && !defined(GODOT_CPP_STUB)"
            )
            out.append(
                f'    ClassDB::bind_method(D_METHOD("get_message_id"), &Godot{msg.name}Packet::get_message_id);'
            )
            out.append(
                f'    ClassDB::bind_method(D_METHOD("set_message_id", "p_id"), &Godot{msg.name}Packet::set_message_id);'
            )
            out.append(
                f'    ClassDB::bind_method(D_METHOD("get_is_zerocoded"), &Godot{msg.name}Packet::get_is_zerocoded);'
            )
            out.append(
                f'    ClassDB::bind_method(D_METHOD("set_is_zerocoded", "p_zerocoded"), &Godot{msg.name}Packet::set_is_zerocoded);'
            )
            out.append(
                '    ADD_PROPERTY(PropertyInfo(Variant::INT, "message_id"), "set_message_id", "get_message_id");'
            )
            out.append(
                '    ADD_PROPERTY(PropertyInfo(Variant::BOOL, "is_zerocoded"), "set_is_zerocoded", "get_is_zerocoded");'
            )
            out.append("#endif")
            out.append("}\n")

        out.append("} // namespace godot\n")
        return "\n".join(out)
