from tools.protocol_gen.emitters.c_emitter import CEmitter
from tools.protocol_gen.emitters.csharp_emitter import CSharpEmitter
from tools.protocol_gen.emitters.dart_emitter import DartEmitter
from tools.protocol_gen.emitters.java_emitter import JavaEmitter
from tools.protocol_gen.emitters.kotlin_emitter import KotlinEmitter
from tools.protocol_gen.emitters.python_emitter import PythonEmitter
from tools.protocol_gen.emitters.rust_emitter import RustEmitter
from tools.protocol_gen.emitters.swift_emitter import SwiftEmitter
from tools.protocol_gen.emitters.typescript_emitter import TypeScriptEmitter

EMITTERS = {
    "kotlin": KotlinEmitter(),
    "rust": RustEmitter(),
    "dart": DartEmitter(),
    "typescript": TypeScriptEmitter(),
    "python": PythonEmitter(),
    "c": CEmitter(),
    "java": JavaEmitter(),
    "swift": SwiftEmitter(),
    "csharp": CSharpEmitter(),
}

__all__ = [
    "EMITTERS",
    "CEmitter",
    "CSharpEmitter",
    "DartEmitter",
    "JavaEmitter",
    "KotlinEmitter",
    "PythonEmitter",
    "RustEmitter",
    "SwiftEmitter",
    "TypeScriptEmitter",
]

