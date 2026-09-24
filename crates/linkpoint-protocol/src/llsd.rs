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
    serde_llsd_benthic::auto_from_bytes(bytes).map_err(|error| Error::Invalid(error.to_string()))
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
}
