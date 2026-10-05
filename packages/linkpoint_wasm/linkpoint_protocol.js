/* @ts-self-types="./linkpoint_protocol.d.ts" */
import * as wasm from "./linkpoint_protocol_bg.wasm";
import { __wbg_set_wasm } from "./linkpoint_protocol_bg.js";

__wbg_set_wasm(wasm);

export {
    wasm_parse_binary, wasm_parse_notation, wasm_parse_xml, wasm_serialize_binary, wasm_serialize_notation, wasm_serialize_xml
} from "./linkpoint_protocol_bg.js";
