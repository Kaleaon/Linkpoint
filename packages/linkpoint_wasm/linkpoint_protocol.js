/* @ts-self-types="./linkpoint_protocol.d.ts" */
import initWasm from "./linkpoint_protocol_bg.wasm?init";
import { __wbg_set_wasm } from "./linkpoint_protocol_bg.js";

if (typeof initWasm === 'function') {
  initWasm().then((exports) => {
    __wbg_set_wasm(exports);
  }).catch(() => {});
}

export {
    WasmMatrix4, WasmQuaternion, wasm_generate_volume, wasm_parse_binary, wasm_parse_j2k_header, wasm_parse_notation, wasm_parse_xml, wasm_serialize_binary, wasm_serialize_notation, wasm_serialize_xml
} from "./linkpoint_protocol_bg.js";
