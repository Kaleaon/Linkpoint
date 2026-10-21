#include <cstdint>

#if __has_include(<godot_cpp/godot.hpp>) && !defined(GODOT_CPP_STUB)
#include <godot_cpp/godot.hpp>
#include <godot_cpp/core/class_db.hpp>
#include <godot_cpp/core/defs.hpp>

#include "linkpoint_protocol.hpp"
#include "generated/protocol/GodotProtocol.hpp"

using namespace godot;

void initialize_linkpoint_protocol_module(ModuleInitializationLevel p_level) {
    if (p_level != MODULE_INITIALIZATION_LEVEL_SCENE) {
        return;
    }

    ClassDB::register_class<LinkpointProtocol>();
    ClassDB::register_class<GodotProtocolDecompressor>();
}

void uninitialize_linkpoint_protocol_module(ModuleInitializationLevel p_level) {
    if (p_level != MODULE_INITIALIZATION_LEVEL_SCENE) {
        return;
    }
}

extern "C" {
GDExtensionBool GDE_EXPORT linkpoint_protocol_library_init(GDExtensionInterfaceGetProcAddress p_get_proc_address, const GDExtensionClassLibraryPtr p_library, GDExtensionInitialization *r_initialization) {
    godot::GDExtensionBinding::InitObject init_obj(p_get_proc_address, p_library, r_initialization);

    init_obj.register_initializer(initialize_linkpoint_protocol_module);
    init_obj.register_uninitializer(uninitialize_linkpoint_protocol_module);
    init_obj.set_minimum_library_initialization_level(MODULE_INITIALIZATION_LEVEL_SCENE);

    return init_obj.init();
}
}
#else
// Standalone library init export symbol
extern "C" {
int linkpoint_protocol_library_init() {
    return 1;
}
}
#endif
