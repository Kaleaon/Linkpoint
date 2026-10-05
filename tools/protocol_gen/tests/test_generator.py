import unittest
import os
import tempfile
import shutil
from tools.protocol_gen.proto_ast.models import ProtocolAST, MessageSpec, BlockSpec, FieldSpec
from tools.protocol_gen.parser.template_parser import TemplateParser
from tools.protocol_gen.parser.llsd_schema_parser import LLSDSchemaParser
from tools.protocol_gen.emitters import EMITTERS
from tools.protocol_gen.emitters.kotlin_emitter import KotlinEmitter
from tools.protocol_gen.emitters.python_emitter import PythonEmitter

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
        from tools.protocol_gen.emitters.python_emitter import PythonEmitter
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

if __name__ == "__main__":
    unittest.main()
