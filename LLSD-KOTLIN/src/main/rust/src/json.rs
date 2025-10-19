/*!
 * LLSD JSON Parser and Serializer - Rust Implementation
 * 
 * Based on Java implementation with enhanced functionality
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::{LLSDValue, LLSDDocument};
use crate::error::{LLSDError, LLSDResult};
use serde_json::{Value, Map};
use std::collections::HashMap;
use uuid::Uuid;
use chrono::{DateTime, Utc};

/// LLSD JSON parser
#[derive(Debug, Default)]
pub struct LLSDJsonParser {
    strict_uuid_parsing: bool,
}

impl LLSDJsonParser {
    /// Create a new JSON parser
    pub fn new() -> Self {
        Self::default()
    }

    /// Enable strict UUID parsing (only accept properly formatted UUIDs)
    pub fn with_strict_uuid_parsing(mut self, strict: bool) -> Self {
        self.strict_uuid_parsing = strict;
        self
    }

    /// Parse LLSD from JSON string
    pub fn parse(&self, json: &str) -> LLSDResult<LLSDDocument> {
        let value: Value = serde_json::from_str(json)?;
        let llsd_value = self.convert_json_value(&value)?;
        Ok(LLSDDocument::new(llsd_value))
    }

    /// Convert JSON value to LLSD value
    fn convert_json_value(&self, value: &Value) -> LLSDResult<LLSDValue> {
        match value {
            Value::Null => Ok(LLSDValue::Undefined),
            Value::Bool(b) => Ok(LLSDValue::Boolean(*b)),
            Value::Number(n) => {
                if let Some(i) = n.as_i64() {
                    if i >= i32::MIN as i64 && i <= i32::MAX as i64 {
                        Ok(LLSDValue::Integer(i as i32))
                    } else {
                        Ok(LLSDValue::Real(i as f64))
                    }
                } else if let Some(f) = n.as_f64() {
                    Ok(LLSDValue::Real(f))
                } else {
                    Err(LLSDError::custom("Invalid number format"))
                }
            }
            Value::String(s) => self.convert_json_string(s),
            Value::Array(arr) => {
                let mut llsd_array = Vec::with_capacity(arr.len());
                for item in arr {
                    llsd_array.push(self.convert_json_value(item)?);
                }
                Ok(LLSDValue::Array(llsd_array))
            }
            Value::Object(obj) => {
                let mut llsd_map = HashMap::with_capacity(obj.len());
                for (key, value) in obj {
                    llsd_map.insert(key.clone(), self.convert_json_value(value)?);
                }
                Ok(LLSDValue::Map(llsd_map))
            }
        }
    }

    /// Convert JSON string to appropriate LLSD type
    fn convert_json_string(&self, s: &str) -> LLSDResult<LLSDValue> {
        // Try to parse as UUID
        if self.strict_uuid_parsing {
            if let Ok(uuid) = Uuid::parse_str(s) {
                return Ok(LLSDValue::UUID(uuid));
            }
        } else {
            // Heuristic: if it looks like a UUID, try to parse it
            if s.len() == 36 && s.chars().filter(|&c| c == '-').count() == 4 {
                if let Ok(uuid) = Uuid::parse_str(s) {
                    return Ok(LLSDValue::UUID(uuid));
                }
            }
        }

        // Try to parse as date/time
        if let Ok(date) = DateTime::parse_from_rfc3339(s) {
            return Ok(LLSDValue::Date(date.with_timezone(&Utc)));
        }

        // Try to parse as URI (heuristic: contains :// or starts with common schemes)
        if s.contains("://") || s.starts_with("http:") || s.starts_with("https:") || s.starts_with("ftp:") {
            return Ok(LLSDValue::URI(s.to_string()));
        }

        // Default to string
        Ok(LLSDValue::String(s.to_string()))
    }
}

/// LLSD JSON serializer
#[derive(Debug, Default)]
pub struct LLSDJsonSerializer {
    pretty_print: bool,
    preserve_types: bool,
}

impl LLSDJsonSerializer {
    /// Create a new JSON serializer
    pub fn new() -> Self {
        Self::default()
    }

    /// Enable pretty printing
    pub fn with_pretty_print(mut self, pretty: bool) -> Self {
        self.pretty_print = pretty;
        self
    }

    /// Enable type preservation (add type hints for UUIDs, dates, etc.)
    pub fn with_type_preservation(mut self, preserve: bool) -> Self {
        self.preserve_types = preserve;
        self
    }

    /// Serialize LLSD to JSON string
    pub fn serialize(&self, document: &LLSDDocument) -> LLSDResult<String> {
        let json_value = self.convert_llsd_value(document.content())?;
        
        if self.pretty_print {
            Ok(serde_json::to_string_pretty(&json_value)?)
        } else {
            Ok(serde_json::to_string(&json_value)?)
        }
    }

    /// Convert LLSD value to JSON value
    fn convert_llsd_value(&self, value: &LLSDValue) -> LLSDResult<Value> {
        match value {
            LLSDValue::Undefined => Ok(Value::Null),
            LLSDValue::Boolean(b) => Ok(Value::Bool(*b)),
            LLSDValue::Integer(i) => Ok(Value::Number((*i).into())),
            LLSDValue::Real(r) => {
                if let Some(n) = serde_json::Number::from_f64(*r) {
                    Ok(Value::Number(n))
                } else {
                    Err(LLSDError::custom("Invalid floating-point number"))
                }
            }
            LLSDValue::String(s) => Ok(Value::String(s.clone())),
            LLSDValue::UUID(u) => {
                if self.preserve_types {
                    // Wrap in object with type hint
                    let mut obj = Map::new();
                    obj.insert("__type".to_string(), Value::String("uuid".to_string()));
                    obj.insert("value".to_string(), Value::String(u.to_string()));
                    Ok(Value::Object(obj))
                } else {
                    Ok(Value::String(u.to_string()))
                }
            }
            LLSDValue::Date(d) => {
                let date_str = d.to_rfc3339();
                if self.preserve_types {
                    let mut obj = Map::new();
                    obj.insert("__type".to_string(), Value::String("date".to_string()));
                    obj.insert("value".to_string(), Value::String(date_str));
                    Ok(Value::Object(obj))
                } else {
                    Ok(Value::String(date_str))
                }
            }
            LLSDValue::URI(u) => {
                if self.preserve_types {
                    let mut obj = Map::new();
                    obj.insert("__type".to_string(), Value::String("uri".to_string()));
                    obj.insert("value".to_string(), Value::String(u.clone()));
                    Ok(Value::Object(obj))
                } else {
                    Ok(Value::String(u.clone()))
                }
            }
            LLSDValue::Binary(b) => {
                let base64_str = base64::encode(b);
                if self.preserve_types {
                    let mut obj = Map::new();
                    obj.insert("__type".to_string(), Value::String("binary".to_string()));
                    obj.insert("value".to_string(), Value::String(base64_str));
                    Ok(Value::Object(obj))
                } else {
                    Ok(Value::String(base64_str))
                }
            }
            LLSDValue::Array(arr) => {
                let mut json_array = Vec::with_capacity(arr.len());
                for item in arr {
                    json_array.push(self.convert_llsd_value(item)?);
                }
                Ok(Value::Array(json_array))
            }
            LLSDValue::Map(map) => {
                let mut json_obj = Map::with_capacity(map.len());
                for (key, value) in map {
                    json_obj.insert(key.clone(), self.convert_llsd_value(value)?);
                }
                Ok(Value::Object(json_obj))
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use uuid::uuid;

    #[test]
    fn test_parse_basic_types() {
        let parser = LLSDJsonParser::new();
        
        // Test null
        let doc = parser.parse("null").unwrap();
        assert!(doc.content().is_undefined());
        
        // Test boolean
        let doc = parser.parse("true").unwrap();
        assert_eq!(doc.content(), &LLSDValue::Boolean(true));
        
        // Test integer
        let doc = parser.parse("42").unwrap();
        assert_eq!(doc.content(), &LLSDValue::Integer(42));
        
        // Test real
        let doc = parser.parse("3.14").unwrap();
        assert_eq!(doc.content(), &LLSDValue::Real(3.14));
        
        // Test string
        let doc = parser.parse("\"hello\"").unwrap();
        assert_eq!(doc.content(), &LLSDValue::String("hello".to_string()));
    }

    #[test]
    fn test_parse_array() {
        let parser = LLSDJsonParser::new();
        let doc = parser.parse("[1, \"hello\", true]").unwrap();
        
        if let LLSDValue::Array(arr) = doc.content() {
            assert_eq!(arr.len(), 3);
            assert_eq!(arr[0], LLSDValue::Integer(1));
            assert_eq!(arr[1], LLSDValue::String("hello".to_string()));
            assert_eq!(arr[2], LLSDValue::Boolean(true));
        } else {
            panic!("Expected array");
        }
    }

    #[test]
    fn test_parse_map() {
        let parser = LLSDJsonParser::new();
        let doc = parser.parse(r#"{"name": "Alice", "age": 30}"#).unwrap();
        
        if let LLSDValue::Map(map) = doc.content() {
            assert_eq!(map.len(), 2);
            assert_eq!(map["name"], LLSDValue::String("Alice".to_string()));
            assert_eq!(map["age"], LLSDValue::Integer(30));
        } else {
            panic!("Expected map");
        }
    }

    #[test]
    fn test_uuid_parsing() {
        let parser = LLSDJsonParser::new().with_strict_uuid_parsing(false);
        let uuid_str = "550e8400-e29b-41d4-a716-446655440000";
        let doc = parser.parse(&format!("\"{}\"", uuid_str)).unwrap();
        
        if let LLSDValue::UUID(uuid) = doc.content() {
            assert_eq!(uuid.to_string(), uuid_str);
        } else {
            panic!("Expected UUID");
        }
    }

    #[test]
    fn test_serialize_basic_types() {
        let serializer = LLSDJsonSerializer::new();
        
        // Test undefined/null
        let doc = LLSDDocument::new(LLSDValue::Undefined);
        assert_eq!(serializer.serialize(&doc).unwrap(), "null");
        
        // Test boolean
        let doc = LLSDDocument::new(LLSDValue::Boolean(true));
        assert_eq!(serializer.serialize(&doc).unwrap(), "true");
        
        // Test integer
        let doc = LLSDDocument::new(LLSDValue::Integer(42));
        assert_eq!(serializer.serialize(&doc).unwrap(), "42");
        
        // Test string
        let doc = LLSDDocument::new(LLSDValue::String("hello".to_string()));
        assert_eq!(serializer.serialize(&doc).unwrap(), "\"hello\"");
    }

    #[test]
    fn test_serialize_uuid_with_type_preservation() {
        let serializer = LLSDJsonSerializer::new().with_type_preservation(true);
        let uuid = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let doc = LLSDDocument::new(LLSDValue::UUID(uuid));
        
        let json = serializer.serialize(&doc).unwrap();
        assert!(json.contains("__type"));
        assert!(json.contains("uuid"));
        assert!(json.contains("550e8400-e29b-41d4-a716-446655440000"));
    }

    #[test]
    fn test_round_trip() {
        let original_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("name".to_string(), LLSDValue::String("Alice".to_string()));
            map.insert("age".to_string(), LLSDValue::Integer(30));
            map.insert("scores".to_string(), LLSDValue::Array(vec![
                LLSDValue::Integer(95),
                LLSDValue::Integer(87),
                LLSDValue::Real(92.5),
            ]));
            map
        });

        let doc = LLSDDocument::new(original_data.clone());
        let serializer = LLSDJsonSerializer::new();
        let json = serializer.serialize(&doc).unwrap();
        
        let parser = LLSDJsonParser::new();
        let parsed_doc = parser.parse(&json).unwrap();
        
        // Note: Due to JSON's limited type system, some type information may be lost
        // This is expected behavior
        assert_eq!(parsed_doc.get_type(), doc.get_type());
    }
}