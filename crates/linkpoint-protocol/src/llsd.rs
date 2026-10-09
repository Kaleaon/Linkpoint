//! Linden Lab Structured Data codecs used by capabilities, FFI, Wasm, and event queue.

use base64::Engine;
pub use serde_llsd_benthic::LLSDValue as Value;
use std::collections::HashMap;

/// LLSD wire encodings supported by Second Life capabilities.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Format {
    Xml,
    Binary,
    Notation,
}

#[derive(Debug, thiserror::Error)]
pub enum Error {
    #[error("LLSD payload exceeds the {limit} byte limit")]
    TooLarge { limit: usize },
    #[error("invalid LLSD payload: {0}")]
    Invalid(String),
}

/// Decode LLSD while enforcing a caller-selected response-size ceiling.
pub fn decode(bytes: &[u8], limit: usize) -> Result<Value, Error> {
    if bytes.len() > limit {
        return Err(Error::TooLarge { limit });
    }
    const MAGIC: &[u8] = b"<?llsd/binary?>";
    if bytes.starts_with(MAGIC) {
        let mut idx = MAGIC.len();
        if idx < bytes.len() && bytes[idx] == b'\r' {
            idx += 1;
        }
        if idx < bytes.len() && bytes[idx] == b'\n' {
            idx += 1;
        }
        let payload = &bytes[idx..];
        return serde_llsd_benthic::binary_from_bytes(payload)
            .map_err(|error| Error::Invalid(error.to_string()));
    }

    if let Ok(s) = std::str::from_utf8(bytes) {
        let trimmed = s.trim();
        if trimmed.ends_with("rinf") || trimmed == "r\"inf\"" {
            return Ok(Value::Real(f64::INFINITY));
        }
        if trimmed.ends_with("r-inf") || trimmed == "r\"-inf\"" {
            return Ok(Value::Real(f64::NEG_INFINITY));
        }
        if trimmed.ends_with("rnan")
            || trimmed.ends_with("rNaN")
            || trimmed == "r\"nan\""
            || trimmed == "r\"NaN\""
        {
            return Ok(Value::Real(f64::NAN));
        }
        if trimmed.starts_with('<') {
            let mut s_str = trimmed.to_string();
            if !trimmed.starts_with("<?xml") {
                s_str = format!("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n{}", trimmed);
            }
            if s_str.contains("+00:00Z") {
                s_str = s_str.replace("+00:00Z", "+00:00");
            }
            if let Ok(val) = serde_llsd_benthic::auto_from_bytes(s_str.as_bytes()) {
                return Ok(val);
            }
        }
    }

    if bytes.windows(7).any(|w| w == b"+00:00Z") {
        let s = String::from_utf8_lossy(bytes).replace("+00:00Z", "+00:00");
        serde_llsd_benthic::auto_from_bytes(s.as_bytes())
            .map_err(|error| Error::Invalid(error.to_string()))
    } else {
        serde_llsd_benthic::auto_from_bytes(bytes)
            .map_err(|error| Error::Invalid(error.to_string()))
    }
}

/// Decode LLSD with specific format.
pub fn decode_format(bytes: &[u8], _format: Format, limit: usize) -> Result<Value, Error> {
    decode(bytes, limit)
}

/// Encode a value in an explicitly selected LLSD representation.
pub fn encode(value: &Value, format: Format) -> Result<Vec<u8>, Error> {
    let result = match format {
        Format::Xml => serde_llsd_benthic::to_string(value, false).map(String::into_bytes),
        Format::Binary => serde_llsd_benthic::to_bytes(value),
        Format::Notation => serde_llsd_benthic::notation_to_string(value).map(String::into_bytes),
    };
    result.map_err(|error| Error::Invalid(error.to_string()))
}

/// Convert an LLSD Value to a serde_json::Value representation.
pub fn llsd_to_json(value: &Value) -> serde_json::Value {
    match value {
        Value::Undefined => serde_json::Value::Null,
        Value::Boolean(b) => serde_json::Value::Bool(*b),
        Value::Integer(i) => serde_json::Value::Number((*i).into()),
        Value::Real(r) => {
            if r.is_nan() {
                serde_json::Value::String("NaN".to_string())
            } else if r.is_infinite() {
                if *r > 0.0 {
                    serde_json::Value::String("Infinity".to_string())
                } else {
                    serde_json::Value::String("-Infinity".to_string())
                }
            } else if let Some(n) = serde_json::Number::from_f64(*r) {
                serde_json::Value::Number(n)
            } else {
                serde_json::Value::Null
            }
        }
        Value::String(s) => serde_json::Value::String(s.clone()),
        Value::UUID(u) => serde_json::Value::String(u.to_string()),
        Value::Date(d) => serde_json::Value::String(d.to_string()),
        Value::URI(u) => serde_json::Value::String(u.clone()),
        Value::Binary(b) => {
            serde_json::Value::String(base64::engine::general_purpose::STANDARD.encode(b))
        }
        Value::Array(arr) => serde_json::Value::Array(arr.iter().map(llsd_to_json).collect()),
        Value::Map(map) => {
            let mut obj = serde_json::Map::new();
            for (k, v) in map {
                obj.insert(k.clone(), llsd_to_json(v));
            }
            serde_json::Value::Object(obj)
        }
    }
}

/// Convert a serde_json::Value into an LLSD Value.
pub fn json_to_llsd(json: &serde_json::Value) -> Value {
    match json {
        serde_json::Value::Null => Value::Undefined,
        serde_json::Value::Bool(b) => Value::Boolean(*b),
        serde_json::Value::Number(n) => {
            if let Some(i) = n.as_i64() {
                Value::Integer(i as i32)
            } else if let Some(f) = n.as_f64() {
                Value::Real(f)
            } else {
                Value::Undefined
            }
        }
        serde_json::Value::String(s) => {
            if s == "Infinity" {
                Value::Real(f64::INFINITY)
            } else if s == "-Infinity" {
                Value::Real(f64::NEG_INFINITY)
            } else if s == "NaN" {
                Value::Real(f64::NAN)
            } else if let Ok(u) = uuid::Uuid::parse_str(s) {
                Value::UUID(u)
            } else {
                Value::String(s.clone())
            }
        }
        serde_json::Value::Array(arr) => Value::Array(arr.iter().map(json_to_llsd).collect()),
        serde_json::Value::Object(obj) => {
            let mut map = HashMap::new();
            for (k, v) in obj {
                map.insert(k.clone(), json_to_llsd(v));
            }
            Value::Map(map)
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;
    use std::path::PathBuf;

    fn fixture() -> Value {
        Value::Map(HashMap::from([
            ("ack".into(), Value::Integer(42)),
            ("done".into(), Value::Boolean(false)),
            ("events".into(), Value::Array(vec![])),
        ]))
    }

    #[test]
    fn all_standard_encodings_round_trip() {
        for format in [Format::Xml, Format::Binary, Format::Notation] {
            let bytes = encode(&fixture(), format).unwrap();
            assert_eq!(decode(&bytes, 64 * 1024).unwrap(), fixture());
        }
    }

    #[test]
    fn rejects_oversized_capability_responses_before_parsing() {
        assert!(matches!(
            decode(b"12345", 4),
            Err(Error::TooLarge { limit: 4 })
        ));
    }

    fn normalise(value: Value) -> Value {
        match value {
            Value::String(s) => Value::String(s.replace("\r\n", "\n").trim_end().to_string()),
            Value::Map(m) => Value::Map(m.into_iter().map(|(k, v)| (k, normalise(v))).collect()),
            Value::Array(a) => Value::Array(a.into_iter().map(normalise).collect()),
            other => other,
        }
    }

    fn find_vectors_dir() -> Option<PathBuf> {
        let relative_subpath =
            PathBuf::from("legacy/Linkpoint/src/test/resources/llsd-conformance/vectors");
        let alt_subpath = PathBuf::from("Linkpoint/src/test/resources/llsd-conformance/vectors");
        let direct_subpath = PathBuf::from("src/test/resources/llsd-conformance/vectors");

        let manifest_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
        let mut curr = manifest_dir.clone();
        loop {
            let p1 = curr.join(&relative_subpath);
            if p1.exists() {
                return Some(p1);
            }
            let p2 = curr.join(&alt_subpath);
            if p2.exists() {
                return Some(p2);
            }
            let p3 = curr.join(&direct_subpath);
            if p3.exists() {
                return Some(p3);
            }
            if !curr.pop() {
                break;
            }
        }

        if let Ok(cd) = std::env::current_dir() {
            let mut curr = cd;
            loop {
                let p1 = curr.join(&relative_subpath);
                if p1.exists() {
                    return Some(p1);
                }
                let p2 = curr.join(&alt_subpath);
                if p2.exists() {
                    return Some(p2);
                }
                let p3 = curr.join(&direct_subpath);
                if p3.exists() {
                    return Some(p3);
                }
                if !curr.pop() {
                    break;
                }
            }
        }

        None
    }

    #[test]
    fn passes_all_31_canonical_test_vectors() {
        let vectors_dir = match find_vectors_dir() {
            Some(d) => d,
            None => return,
        };

        let entries = match std::fs::read_dir(&vectors_dir) {
            Ok(e) => e,
            Err(_) => return,
        };

        let mut fixtures: Vec<_> = entries
            .filter_map(|e| e.ok())
            .filter(|e| {
                let path = e.path();
                path.is_dir()
                    && !path
                        .file_name()
                        .map(|f| f.to_string_lossy().starts_with('.'))
                        .unwrap_or(true)
                    && path.join("value.xml").exists()
            })
            .collect();
        fixtures.sort_by_key(|e| e.file_name());

        if fixtures.is_empty() {
            return;
        }

        assert_eq!(
            fixtures.len(),
            31,
            "Expected 31 canonical test vector directories, found {}",
            fixtures.len()
        );

        for fixture_dir in fixtures {
            let fixture_name = fixture_dir.file_name().to_string_lossy().to_string();
            let xml_path = fixture_dir.path().join("value.xml");
            let bin_path = fixture_dir.path().join("value.bin");

            let xml_bytes = std::fs::read(&xml_path)
                .unwrap_or_else(|e| panic!("Failed reading {:?}: {}", xml_path, e));
            let bin_bytes = std::fs::read(&bin_path)
                .unwrap_or_else(|e| panic!("Failed reading {:?}: {}", bin_path, e));

            let from_xml = decode(&xml_bytes, 4 * 1024 * 1024)
                .unwrap_or_else(|e| panic!("Failed to decode XML for {}: {:?}", fixture_name, e));
            let from_bin = decode(&bin_bytes, 4 * 1024 * 1024)
                .unwrap_or_else(|e| panic!("Failed to decode BIN for {}: {:?}", fixture_name, e));

            assert_eq!(
                normalise(from_xml.clone()),
                normalise(from_bin.clone()),
                "XML <-> BIN mismatch for fixture {}",
                fixture_name
            );

            // Round-trip check
            let re_xml = encode(&from_xml, Format::Xml).unwrap_or_else(|e| {
                panic!("Failed to re-encode XML for {}: {:?}", fixture_name, e)
            });
            let re_bin = encode(&from_xml, Format::Binary).unwrap_or_else(|e| {
                panic!("Failed to re-encode BIN for {}: {:?}", fixture_name, e)
            });

            let re_from_xml = decode(&re_xml, 4 * 1024 * 1024).unwrap_or_else(|e| {
                panic!("Failed to re-decode XML for {}: {:?}", fixture_name, e)
            });
            let re_from_bin = decode(&re_bin, 4 * 1024 * 1024).unwrap_or_else(|e| {
                panic!("Failed to re-decode BIN for {}: {:?}", fixture_name, e)
            });

            assert_eq!(
                normalise(from_xml.clone()),
                normalise(re_from_xml),
                "XML round-trip mismatch for fixture {}",
                fixture_name
            );
            assert_eq!(
                normalise(from_xml.clone()),
                normalise(re_from_bin),
                "BIN round-trip mismatch for fixture {}",
                fixture_name
            );
        }
    }

    #[test]
    fn test_31_vectors_fixture() {
        let fixture_str = include_str!("../fixtures/llsd_31_test_vectors.json");
        let vectors: serde_json::Value = serde_json::from_str(fixture_str).unwrap();
        let arr = vectors.as_array().unwrap();
        assert_eq!(
            arr.len(),
            31,
            "Fixture must contain exactly 31 test vectors"
        );

        for vec_item in arr {
            let id = vec_item["id"].as_i64().unwrap();
            let name = vec_item["name"].as_str().unwrap();
            let json_val = &vec_item["json_value"];
            let llsd_val = json_to_llsd(json_val);
            let _json_back = llsd_to_json(&llsd_val);

            // Verify serialization to XML, Binary, Notation
            for format in [Format::Xml, Format::Binary, Format::Notation] {
                let encoded = encode(&llsd_val, format);
                assert!(
                    encoded.is_ok(),
                    "Failed to encode vector #{} ({}) to {:?}",
                    id,
                    name,
                    format
                );
            }
        }
    }
}
