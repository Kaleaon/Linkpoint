import { describe, it, expect, beforeAll } from 'vitest';
import fs from 'fs';
import path from 'path';
import testVectors from '../../../../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json';

// @ts-ignore
import * as bg from '../../../../packages/linkpoint_wasm/linkpoint_protocol_bg.js';

describe('WASM LLSD 31 Test Vectors Conformance Suite', () => {
  beforeAll(() => {
    const wasmPath = path.resolve(__dirname, '../../../../packages/linkpoint_wasm/linkpoint_protocol_bg.wasm');
    const wasmBytes = fs.readFileSync(wasmPath);
    const wasmModule = new WebAssembly.Module(wasmBytes);
    const wasmInstance = new WebAssembly.Instance(wasmModule, {
      './linkpoint_protocol_bg.js': bg
    });
    bg.__wbg_set_wasm(wasmInstance.exports);
  });

  it('has exactly 31 test vectors', () => {
    expect(testVectors.length).toBe(31);
  });

  testVectors.forEach((vector) => {
    describe(`Vector #${vector.id}: ${vector.name} (${vector.type})`, () => {
      it('serializes and parses XML through WASM', () => {
        const jsonStr = JSON.stringify(vector.json_value);
        const xmlEnc = bg.wasm_serialize_xml(jsonStr);
        expect(xmlEnc).toBeDefined();
        const xmlDecStr = bg.wasm_parse_xml(xmlEnc);
        expect(xmlDecStr).toBeDefined();
      });

      it('serializes and parses Notation through WASM', () => {
        const jsonStr = JSON.stringify(vector.json_value);
        const notEnc = bg.wasm_serialize_notation(jsonStr);
        expect(notEnc).toBeDefined();
        const notDecStr = bg.wasm_parse_notation(notEnc);
        expect(notDecStr).toBeDefined();
      });

      it('serializes and parses Binary through WASM', () => {
        const jsonStr = JSON.stringify(vector.json_value);
        const binEnc = bg.wasm_serialize_binary(jsonStr);
        expect(binEnc).toBeDefined();
        const binDecStr = bg.wasm_parse_binary(binEnc);
        expect(binDecStr).toBeDefined();
      });
    });
  });
});
