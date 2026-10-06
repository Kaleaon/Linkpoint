//! WebAssembly exports for TypeScript and web clients.

#[cfg(feature = "wasm")]
use wasm_bindgen::prelude::*;

use crate::llsd::{Format, decode, encode, json_to_llsd, llsd_to_json};

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_parse_xml(input: &str) -> Result<String, String> {
    let llsd_val = decode(input.as_bytes(), 16 * 1024 * 1024)
        .map_err(|e| format!("XML parse error: {}", e))?;
    let json_val = llsd_to_json(&llsd_val);
    serde_json::to_string(&json_val).map_err(|e| format!("JSON error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_parse_binary(bytes: &[u8]) -> Result<String, String> {
    let llsd_val =
        decode(bytes, 16 * 1024 * 1024).map_err(|e| format!("Binary parse error: {}", e))?;
    let json_val = llsd_to_json(&llsd_val);
    serde_json::to_string(&json_val).map_err(|e| format!("JSON error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_parse_notation(input: &str) -> Result<String, String> {
    let llsd_val = decode(input.as_bytes(), 16 * 1024 * 1024)
        .map_err(|e| format!("Notation parse error: {}", e))?;
    let json_val = llsd_to_json(&llsd_val);
    serde_json::to_string(&json_val).map_err(|e| format!("JSON error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_serialize_xml(json_str: &str) -> Result<String, String> {
    let json_val: serde_json::Value =
        serde_json::from_str(json_str).map_err(|e| format!("Invalid JSON: {}", e))?;
    let llsd_val = json_to_llsd(&json_val);
    let bytes = encode(&llsd_val, Format::Xml).map_err(|e| format!("XML encode error: {}", e))?;
    String::from_utf8(bytes).map_err(|e| format!("UTF-8 error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_serialize_binary(json_str: &str) -> Result<Vec<u8>, String> {
    let json_val: serde_json::Value =
        serde_json::from_str(json_str).map_err(|e| format!("Invalid JSON: {}", e))?;
    let llsd_val = json_to_llsd(&json_val);
    encode(&llsd_val, Format::Binary).map_err(|e| format!("Binary encode error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_serialize_notation(json_str: &str) -> Result<String, String> {
    let json_val: serde_json::Value =
        serde_json::from_str(json_str).map_err(|e| format!("Invalid JSON: {}", e))?;
    let llsd_val = json_to_llsd(&json_val);
    let bytes =
        encode(&llsd_val, Format::Notation).map_err(|e| format!("Notation encode error: {}", e))?;
    String::from_utf8(bytes).map_err(|e| format!("UTF-8 error: {}", e))
}

#[cfg_attr(feature = "wasm", wasm_bindgen)]
pub fn wasm_generate_volume(params_json: &str, detail: f32) -> String {
    linkpoint_scene::wasm::wasm_generate_volume(params_json, detail)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_wasm_exports_roundtrip() {
        let xml = "<llsd><integer>42</integer></llsd>";
        let json = wasm_parse_xml(xml).unwrap();
        assert_eq!(json, "42");

        let xml_back = wasm_serialize_xml("42").unwrap();
        assert!(xml_back.contains("<integer>42</integer>"));
    }
}
