import os
import shutil
import subprocess
import tempfile
import unittest

from tools.protocol_gen.emitters.c_emitter import CEmitter
from tools.protocol_gen.emitters.dart_emitter import DartEmitter
from tools.protocol_gen.emitters.java_emitter import JavaEmitter
from tools.protocol_gen.emitters.python_emitter import PythonEmitter
from tools.protocol_gen.emitters.rust_emitter import RustEmitter
from tools.protocol_gen.emitters.typescript_emitter import TypeScriptEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST

# Standard conformance test vectors (name, src_bytes, expected_uncompressed_bytes)
TEST_VECTORS = [
    ("zero_count_run", bytes([0x00, 0x00]), bytes([])),
    ("zero_count_run_in_middle", bytes([0x01, 0x00, 0x00, 0x02]), bytes([0x01, 0x02])),
    ("standard_zero_run_3", bytes([0x00, 0x03]), bytes([0x00, 0x00, 0x00])),
    ("standard_zero_run_1", bytes([0x00, 0x01]), bytes([0x00])),
    ("mixed_runs", bytes([0x05, 0x00, 0x02, 0x06]), bytes([0x05, 0x00, 0x00, 0x06])),
    ("trailing_single_zero", bytes([0x00]), bytes([0x00])),
    ("all_non_zero", bytes([0x01, 0x02, 0x03, 0x04]), bytes([0x01, 0x02, 0x03, 0x04])),
    ("empty_input", bytes([]), bytes([])),
    ("multiple_zero_runs", bytes([0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00]), bytes([0x01, 0x00, 0x00])),
]


def is_strict_conformance() -> bool:
    return os.environ.get("STRICT_CONFORMANCE") == "1" or os.environ.get("CI") == "true"


class TestDecompressionConformance(unittest.TestCase):
    def setUp(self):
        self.test_dir = tempfile.mkdtemp()
        self.ast = ProtocolAST()

    def tearDown(self):
        shutil.rmtree(self.test_dir, ignore_errors=True)

    def _handle_missing_toolchain(self, name: str):
        if is_strict_conformance():
            self.fail(f"Required toolchain '{name}' for conformance testing is missing in strict mode.")
        else:
            self.skipTest(f"Toolchain '{name}' not found.")

    def test_python_conformance(self):
        out_dir = os.path.join(self.test_dir, "python")
        PythonEmitter().emit(self.ast, out_dir)

        gen_file = os.path.join(out_dir, "generated.py")
        exec_scope = {}
        with open(gen_file, "r", encoding="utf-8") as f:
            code = f.read()
        exec(code, exec_scope)
        decompress_fn = exec_scope["decompress_zerocoded"]

        for name, src, expected in TEST_VECTORS:
            with self.subTest(vector=name):
                res = decompress_fn(src)
                self.assertEqual(res, expected, f"Python failure on vector '{name}'")

    def test_node_typescript_conformance(self):
        if not shutil.which("node"):
            self._handle_missing_toolchain("node")

        out_dir = os.path.join(self.test_dir, "ts")
        TypeScriptEmitter().emit(self.ast, out_dir)

        with open(os.path.join(out_dir, "package.json"), "w", encoding="utf-8") as f:
            f.write('{"type": "module"}\n')

        vector_entries = []
        for name, src, expected in TEST_VECTORS:
            src_str = ",".join(map(str, list(src)))
            exp_str = ",".join(map(str, list(expected)))
            vector_entries.append(
                f"  {{ name: '{name}', src: new Uint8Array([{src_str}]), expected: new Uint8Array([{exp_str}]) }}"
            )

        runner_code = f"""import {{ decompressZerocoded }} from './generated.ts';
import process from 'node:process';

const vectors = [
{",\n".join(vector_entries)}
];

function arraysEqual(a, b) {{
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i++) {{
    if (a[i] !== b[i]) return false;
  }}
  return true;
}}

for (const v of vectors) {{
  const actual = decompressZerocoded(v.src);
  if (!arraysEqual(actual, v.expected)) {{
    console.error(`Mismatch for ${{v.name}}: expected len ${{v.expected.length}}, got len ${{actual.length}}`);
    process.exit(1);
  }}
}}
"""
        runner_file = os.path.join(out_dir, "runner.ts")
        with open(runner_file, "w", encoding="utf-8") as f:
            f.write(runner_code)

        res = subprocess.run(
            ["node", "--experimental-strip-types", runner_file],
            capture_output=True,
            text=True,
            cwd=out_dir,
        )
        self.assertEqual(
            res.returncode,
            0,
            f"Node.js conformance test failed:\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}",
        )

    def test_rust_conformance(self):
        compiler = shutil.which("rustc")
        if not compiler:
            self._handle_missing_toolchain("rustc")

        out_dir = os.path.join(self.test_dir, "rust")
        RustEmitter().emit(self.ast, out_dir)

        shutil.copy(os.path.join(out_dir, "mod.rs"), os.path.join(out_dir, "generated.rs"))

        vector_entries = []
        for name, src, expected in TEST_VECTORS:
            src_str = ",".join(map(str, list(src)))
            exp_str = ",".join(map(str, list(expected)))
            vector_entries.append(
                f"        Vector {{ name: \"{name}\", src: &[{src_str}], expected: &[{exp_str}] }},"
            )

        runner_code = f"""mod generated;
use generated::decompress_zerocoded;

struct Vector {{
    name: &'static str,
    src: &'static [u8],
    expected: &'static [u8],
}}

fn main() {{
    let vectors = vec![
{chr(10).join(vector_entries)}
    ];

    for v in vectors {{
        let actual = decompress_zerocoded(v.src);
        if actual.as_slice() != v.expected {{
            eprintln!("Mismatch for {{}}: expected {{:?}}, got {{:?}}", v.name, v.expected, actual);
            std::process::exit(1);
        }}
    }}
}}
"""
        runner_file = os.path.join(out_dir, "main.rs")
        with open(runner_file, "w", encoding="utf-8") as f:
            f.write(runner_code)

        binary_file = os.path.join(out_dir, "runner")
        comp = subprocess.run(
            [compiler, runner_file, "-o", binary_file],
            capture_output=True,
            text=True,
            cwd=out_dir,
        )
        self.assertEqual(comp.returncode, 0, f"rustc compilation failed:\nSTDERR: {comp.stderr}")

        res = subprocess.run([binary_file], capture_output=True, text=True)
        self.assertEqual(
            res.returncode,
            0,
            f"Rust conformance test failed:\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}",
        )

    def test_c_conformance(self):
        compiler = shutil.which("gcc") or shutil.which("clang")
        if not compiler:
            self._handle_missing_toolchain("c_compiler")

        out_dir = os.path.join(self.test_dir, "c")
        CEmitter().emit(self.ast, out_dir)

        vector_decls = []
        vector_items = []
        for i, (name, src, expected) in enumerate(TEST_VECTORS):
            src_str = ", ".join(map(str, list(src))) if src else "0"
            exp_str = ", ".join(map(str, list(expected))) if expected else "0"
            vector_decls.append(f"static const uint8_t src_{i}[] = {{{src_str}}};")
            vector_decls.append(f"static const uint8_t exp_{i}[] = {{{exp_str}}};")
            vector_items.append(
                f"    {{\"{name}\", src_{i}, {len(src)}, exp_{i}, {len(expected)}}}"
            )

        runner_code = f"""#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "generated_protocol.h"

typedef struct {{
    const char* name;
    const uint8_t* src;
    size_t src_len;
    const uint8_t* expected;
    size_t expected_len;
}} Vector;

{chr(10).join(vector_decls)}

int main() {{
    Vector vectors[] = {{
{",\n".join(vector_items)}
    }};
    size_t num_vectors = sizeof(vectors) / sizeof(vectors[0]);

    for (size_t k = 0; k < num_vectors; k++) {{
        Vector v = vectors[k];
        uint8_t dest[512];
        size_t out_len = decompress_zerocoded(v.src, v.src_len, dest, sizeof(dest));
        if (out_len != v.expected_len) {{
            fprintf(stderr, "Length mismatch for %s: expected %zu, got %zu\\n", v.name, v.expected_len, out_len);
            return 1;
        }}
        if (v.expected_len > 0 && memcmp(dest, v.expected, v.expected_len) != 0) {{
            fprintf(stderr, "Content mismatch for %s\\n", v.name);
            return 1;
        }}
    }}
    return 0;
}}
"""
        runner_file = os.path.join(out_dir, "main.c")
        with open(runner_file, "w", encoding="utf-8") as f:
            f.write(runner_code)

        c_src = os.path.join(out_dir, "generated_protocol.c")
        binary_file = os.path.join(out_dir, "runner")
        comp = subprocess.run(
            [compiler, "-I" + out_dir, runner_file, c_src, "-o", binary_file],
            capture_output=True,
            text=True,
        )
        self.assertEqual(comp.returncode, 0, f"C compilation failed:\nSTDERR: {comp.stderr}")

        res = subprocess.run([binary_file], capture_output=True, text=True)
        self.assertEqual(
            res.returncode,
            0,
            f"C conformance test failed:\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}",
        )

    def test_dart_conformance(self):
        dart_cmd = shutil.which("dart")
        if not dart_cmd:
            self._handle_missing_toolchain("dart")

        out_dir = os.path.join(self.test_dir, "dart")
        DartEmitter().emit(self.ast, out_dir)

        vector_entries = []
        for name, src, expected in TEST_VECTORS:
            src_str = ", ".join(map(str, list(src)))
            exp_str = ", ".join(map(str, list(expected)))
            vector_entries.append(
                f"    Vector('{name}', [{src_str}], [{exp_str}]),"
            )

        runner_code = f"""import 'dart:typed_data';
import 'generated.dart';

class Vector {{
  final String name;
  final Uint8List src;
  final Uint8List expected;
  Vector(this.name, List<int> s, List<int> e)
      : src = Uint8List.fromList(s),
        expected = Uint8List.fromList(e);
}}

void main() {{
  final vectors = [
{chr(10).join(vector_entries)}
  ];

  for (final v in vectors) {{
    final actual = GeneratedProtocolCatalog.decompressZerocoded(v.src);
    if (actual.length != v.expected.length) {{
      print('Length mismatch for ' + v.name);
      throw Exception('Length mismatch');
    }}
    for (int i = 0; i < actual.length; i++) {{
      if (actual[i] != v.expected[i]) {{
        print('Byte mismatch for ' + v.name);
        throw Exception('Byte mismatch');
      }}
    }}
  }}
}}
"""
        runner_file = os.path.join(out_dir, "runner.dart")
        with open(runner_file, "w", encoding="utf-8") as f:
            f.write(runner_code)

        res = subprocess.run(
            [dart_cmd, runner_file],
            capture_output=True,
            text=True,
            cwd=out_dir,
        )
        self.assertEqual(
            res.returncode,
            0,
            f"Dart conformance test failed:\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}",
        )

    def test_java_conformance(self):
        javac = shutil.which("javac")
        java = shutil.which("java")
        if not javac or not java:
            self._handle_missing_toolchain("java")

        out_dir = os.path.join(self.test_dir, "java")
        JavaEmitter().emit(self.ast, out_dir)

        vector_entries = []
        for name, src, expected in TEST_VECTORS:
            # Java bytes are signed (-128 to 127)
            src_bytes = ", ".join(str(b if b < 128 else b - 256) for b in src)
            exp_bytes = ", ".join(str(b if b < 128 else b - 256) for b in expected)
            vector_entries.append(
                f"            new Vector(\"{name}\", new byte[]{{{src_bytes}}}, new byte[]{{{exp_bytes}}}),"
            )

        runner_code = f"""import com.linkpoint.protocol.java.generated.GeneratedProtocol;

public class TestRunner {{
    static class Vector {{
        String name;
        byte[] src;
        byte[] expected;
        Vector(String name, byte[] src, byte[] expected) {{
            this.name = name;
            this.src = src;
            this.expected = expected;
        }}
    }}

    public static void main(String[] args) {{
        Vector[] vectors = new Vector[] {{
{chr(10).join(vector_entries)}
        }};

        for (Vector v : vectors) {{
            byte[] actual = GeneratedProtocol.decompressZerocoded(v.src);
            if (actual.length != v.expected.length) {{
                System.err.println("Length mismatch for " + v.name + ": expected " + v.expected.length + ", got " + actual.length);
                System.exit(1);
            }}
            for (int i = 0; i < actual.length; i++) {{
                if (actual[i] != v.expected[i]) {{
                    System.err.println("Byte mismatch for " + v.name);
                    System.exit(1);
                }}
            }}
        }}
    }}
}}
"""
        runner_file = os.path.join(out_dir, "TestRunner.java")
        with open(runner_file, "w", encoding="utf-8") as f:
            f.write(runner_code)

        gen_java = os.path.join(out_dir, "GeneratedProtocol.java")
        gen_llsd = os.path.join(out_dir, "GeneratedLLSDCapabilities.java")

        comp = subprocess.run(
            [javac, "-d", out_dir, runner_file, gen_java, gen_llsd],
            capture_output=True,
            text=True,
            cwd=out_dir,
        )
        self.assertEqual(comp.returncode, 0, f"javac compilation failed:\nSTDERR: {comp.stderr}")

        res = subprocess.run(
            [java, "-cp", out_dir, "TestRunner"],
            capture_output=True,
            text=True,
            cwd=out_dir,
        )
        self.assertEqual(
            res.returncode,
            0,
            f"Java conformance test failed:\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}",
        )


if __name__ == "__main__":
    unittest.main()
