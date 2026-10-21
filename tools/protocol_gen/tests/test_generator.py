import os
import shutil
import tempfile
import unittest

from tools.protocol_gen.emitters import EMITTERS
from tools.protocol_gen.emitters.python_emitter import PythonEmitter
from tools.protocol_gen.parser.template_parser import TemplateParser
from tools.protocol_gen.proto_ast.models import (
    ProtocolAST,
)


class TestProtocolGenerator(unittest.TestCase):
    def setUp(self):
        self.test_dir = tempfile.mkdtemp()
        self.sample_template = """// Test template
version 2.0

{
    TestPacket Low 10 NotTrusted Zerocoded
    {
        TestBlock Single
        { TestField U32 }
    }
}
"""

    def tearDown(self):
        shutil.rmtree(self.test_dir)

    def test_template_parser(self):
        ast = TemplateParser().parse(self.sample_template)
        self.assertEqual(ast.version, "2.0")
        self.assertEqual(len(ast.messages), 1)

        msg = ast.messages[0]
        self.assertEqual(msg.name, "TestPacket")
        self.assertEqual(msg.frequency, "Low")
        self.assertEqual(msg.message_number, 10)
        self.assertEqual(msg.encoding, "Zerocoded")
        self.assertEqual(len(msg.blocks), 1)

        blk = msg.blocks[0]
        self.assertEqual(blk.name, "TestBlock")
        self.assertEqual(blk.block_type, "Single")
        self.assertEqual(len(blk.fields), 1)
        self.assertEqual(blk.fields[0].name, "TestField")
        self.assertEqual(blk.fields[0].type_name, "U32")

    def test_emitters_all_targets(self):
        ast = TemplateParser().parse(self.sample_template)
        for name, emitter in EMITTERS.items():
            out_dir = os.path.join(self.test_dir, name)
            results = emitter.emit(ast, out_dir)
            self.assertTrue(len(results) > 0)
            for file_path, content in results.items():
                self.assertTrue(os.path.exists(file_path))
                self.assertIn("AUTO-GENERATED FILE", content)

    def test_python_zerocoded_decompress(self):
        ast = ProtocolAST()
        emitter = PythonEmitter()
        code = emitter._generate_python_code(ast)
        exec_scope = {}
        exec(code, exec_scope)
        decompress_fn = exec_scope["decompress_zerocoded"]

        # Test zero coding: b'\x00\x03' -> 3 zeros, b'\x05' -> b'\x05'
        compressed = bytes([0x01, 0x00, 0x03, 0x02])
        uncompressed = decompress_fn(compressed)
        self.assertEqual(uncompressed, bytes([0x01, 0x00, 0x00, 0x00, 0x02]))

    def test_swift_emitter(self):
        ast = TemplateParser().parse(self.sample_template)
        emitter = EMITTERS["swift"]
        out_dir = os.path.join(self.test_dir, "swift")
        results = emitter.emit(ast, out_dir)
        
        proto_file = os.path.join(out_dir, "GeneratedProtocol.swift")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.swift")
        
        self.assertIn(proto_file, results)
        self.assertIn(llsd_file, results)
        
        proto_content = results[proto_file]
        self.assertIn("public struct GeneratedProtocol", proto_content)
        self.assertIn("func decompressZerocoded", proto_content)
        self.assertIn("public struct TestPacketPacket", proto_content)
        self.assertIn("func serialize() -> Data", proto_content)

    def test_csharp_emitter(self):
        ast = TemplateParser().parse(self.sample_template)
        emitter = EMITTERS["csharp"]
        out_dir = os.path.join(self.test_dir, "csharp")
        results = emitter.emit(ast, out_dir)
        
        proto_file = os.path.join(out_dir, "GeneratedProtocol.cs")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.cs")
        
        self.assertIn(proto_file, results)
        self.assertIn(llsd_file, results)
        
        proto_content = results[proto_file]
        self.assertIn("public static class GeneratedProtocol", proto_content)
        self.assertIn("DecompressZerocoded", proto_content)
        self.assertIn("public class TestPacketPacket", proto_content)
        self.assertIn("public byte[] Serialize()", proto_content)


if __name__ == "__main__":
    unittest.main()
