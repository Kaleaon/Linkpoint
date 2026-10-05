//! Linden Lab Structured Data codecs used by capabilities and the event queue.

pub use serde_llsd_benthic::LLSDValue as Value;

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
        serde_llsd_benthic::binary_from_bytes(payload)
            .map_err(|error| Error::Invalid(error.to_string()))
    } else {
        // Sanitize python-llsd date format '+00:00Z' in XML payloads if present
        if bytes.windows(7).any(|w| w == b"+00:00Z") {
            let s = String::from_utf8_lossy(bytes).replace("+00:00Z", "+00:00");
            serde_llsd_benthic::auto_from_bytes(s.as_bytes())
                .map_err(|error| Error::Invalid(error.to_string()))
        } else {
            serde_llsd_benthic::auto_from_bytes(bytes)
                .map_err(|error| Error::Invalid(error.to_string()))
        }
    }
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

    #[test]
    fn passes_all_31_canonical_test_vectors() {
        let manifest_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
        let vectors_dir =
            manifest_dir.join("../../Linkpoint/src/test/resources/llsd-conformance/vectors");

        let entries = std::fs::read_dir(&vectors_dir)
            .unwrap_or_else(|e| panic!("Failed to read vectors dir at {:?}: {}", vectors_dir, e));

        let mut fixtures: Vec<_> = entries
            .filter_map(|e| e.ok())
            .filter(|e| e.path().is_dir())
            .collect();
        fixtures.sort_by_key(|e| e.file_name());

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
}
