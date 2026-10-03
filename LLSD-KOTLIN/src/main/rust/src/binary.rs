/*!
 * LLSD Binary Parser and Serializer - Rust Implementation
 * 
 * Based on Java implementation and Second Life viewer binary format
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::{LLSDValue, LLSDDocument};
use crate::error::{LLSDError, LLSDResult};
use std::collections::HashMap;
use std::io::{Cursor, Read, Write};
use uuid::Uuid;
use chrono::{DateTime, Utc, TimeZone};
use bytes::{Buf, BufMut, BytesMut};

/// LLSD Binary format magic number
const LLSD_BINARY_MAGIC: u32 = 0x6C6C7364; // 'llsd' in big-endian

/// LLSD binary type identifiers
#[repr(u8)]
#[derive(Debug, Clone, Copy, PartialEq)]
enum BinaryType {
    Undefined = 0,
    Boolean = 1,
    Integer = 2,
    Real = 3,
    String = 4,
    UUID = 5,
    Date = 6,
    URI = 7,
    Binary = 8,
    Array = 9,
    Map = 10,
}

impl TryFrom<u8> for BinaryType {
    type Error = LLSDError;

    fn try_from(value: u8) -> Result<Self, Self::Error> {
        match value {
            0 => Ok(BinaryType::Undefined),
            1 => Ok(BinaryType::Boolean),
            2 => Ok(BinaryType::Integer),
            3 => Ok(BinaryType::Real),
            4 => Ok(BinaryType::String),
            5 => Ok(BinaryType::UUID),
            6 => Ok(BinaryType::Date),
            7 => Ok(BinaryType::URI),
            8 => Ok(BinaryType::Binary),
            9 => Ok(BinaryType::Array),
            10 => Ok(BinaryType::Map),
            _ => Err(LLSDError::InvalidType { type_id: value }),
        }
    }
}

/// LLSD binary parser
#[derive(Debug, Default)]
pub struct LLSDBinaryParser {
    validate_magic: bool,
    max_depth: usize,
    max_elements: usize,
}

impl LLSDBinaryParser {
    /// Create a new binary parser
    pub fn new() -> Self {
        Self {
            validate_magic: true,
            max_depth: 1000,
            max_elements: 1000000,
        }
    }

    /// Disable magic number validation (for parsing partial data)
    pub fn without_magic_validation(mut self) -> Self {
        self.validate_magic = false;
        self
    }

    /// Set maximum parsing depth to prevent stack overflow
    pub fn with_max_depth(mut self, depth: usize) -> Self {
        self.max_depth = depth;
        self
    }

    /// Set maximum number of elements to prevent memory exhaustion
    pub fn with_max_elements(mut self, elements: usize) -> Self {
        self.max_elements = elements;
        self
    }

    /// Parse LLSD from binary data
    pub fn parse(&self, data: &[u8]) -> LLSDResult<LLSDDocument> {
        let mut cursor = Cursor::new(data);
        
        if self.validate_magic {
            let magic = self.read_u32(&mut cursor)?;
            if magic != LLSD_BINARY_MAGIC {
                return Err(LLSDError::InvalidMagic);
            }
        }

        let value = self.parse_value(&mut cursor, 0)?;
        Ok(LLSDDocument::new(value))
    }

    /// Parse a single value from binary data
    fn parse_value(&self, cursor: &mut Cursor<&[u8]>, depth: usize) -> LLSDResult<LLSDValue> {
        if depth > self.max_depth {
            return Err(LLSDError::binary_error("Maximum parsing depth exceeded"));
        }

        let type_byte = self.read_u8(cursor)?;
        let binary_type = BinaryType::try_from(type_byte)?;

        match binary_type {
            BinaryType::Undefined => Ok(LLSDValue::Undefined),
            BinaryType::Boolean => {
                let value = self.read_u8(cursor)? != 0;
                Ok(LLSDValue::Boolean(value))
            }
            BinaryType::Integer => {
                let value = self.read_i32(cursor)?;
                Ok(LLSDValue::Integer(value))
            }
            BinaryType::Real => {
                let value = self.read_f64(cursor)?;
                Ok(LLSDValue::Real(value))
            }
            BinaryType::String => {
                let string = self.read_string(cursor)?;
                Ok(LLSDValue::String(string))
            }
            BinaryType::UUID => {
                let uuid = self.read_uuid(cursor)?;
                Ok(LLSDValue::UUID(uuid))
            }
            BinaryType::Date => {
                let timestamp = self.read_f64(cursor)?;
                let date = Utc.timestamp_opt(timestamp as i64, ((timestamp.fract() * 1e9) as u32))
                    .single()
                    .ok_or_else(|| LLSDError::binary_error("Invalid timestamp"))?;
                Ok(LLSDValue::Date(date))
            }
            BinaryType::URI => {
                let uri = self.read_string(cursor)?;
                Ok(LLSDValue::URI(uri))
            }
            BinaryType::Binary => {
                let binary = self.read_binary(cursor)?;
                Ok(LLSDValue::Binary(binary))
            }
            BinaryType::Array => self.parse_array(cursor, depth),
            BinaryType::Map => self.parse_map(cursor, depth),
        }
    }

    /// Parse an array from binary data
    fn parse_array(&self, cursor: &mut Cursor<&[u8]>, depth: usize) -> LLSDResult<LLSDValue> {
        let length = self.read_u32(cursor)? as usize;
        
        if length > self.max_elements {
            return Err(LLSDError::binary_error("Array too large"));
        }

        let mut array = Vec::with_capacity(length);
        for _ in 0..length {
            let value = self.parse_value(cursor, depth + 1)?;
            array.push(value);
        }

        Ok(LLSDValue::Array(array))
    }

    /// Parse a map from binary data
    fn parse_map(&self, cursor: &mut Cursor<&[u8]>, depth: usize) -> LLSDResult<LLSDValue> {
        let length = self.read_u32(cursor)? as usize;
        
        if length > self.max_elements {
            return Err(LLSDError::binary_error("Map too large"));
        }

        let mut map = HashMap::with_capacity(length);
        for _ in 0..length {
            let key = self.read_string(cursor)?;
            let value = self.parse_value(cursor, depth + 1)?;
            map.insert(key, value);
        }

        Ok(LLSDValue::Map(map))
    }

    /// Read a single byte
    fn read_u8(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<u8> {
        let mut buf = [0u8; 1];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(buf[0])
    }

    /// Read a 32-bit unsigned integer (big-endian)
    fn read_u32(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<u32> {
        let mut buf = [0u8; 4];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(u32::from_be_bytes(buf))
    }

    /// Read a 32-bit signed integer (big-endian)
    fn read_i32(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<i32> {
        let mut buf = [0u8; 4];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(i32::from_be_bytes(buf))
    }

    /// Read a 64-bit floating point number (big-endian)
    fn read_f64(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<f64> {
        let mut buf = [0u8; 8];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(f64::from_be_bytes(buf))
    }

    /// Read a UTF-8 string
    fn read_string(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<String> {
        let length = self.read_u32(cursor)? as usize;
        let mut buf = vec![0u8; length];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        String::from_utf8(buf).map_err(LLSDError::from)
    }

    /// Read a UUID (16 bytes)
    fn read_uuid(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<Uuid> {
        let mut buf = [0u8; 16];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(Uuid::from_bytes(buf))
    }

    /// Read binary data
    fn read_binary(&self, cursor: &mut Cursor<&[u8]>) -> LLSDResult<Vec<u8>> {
        let length = self.read_u32(cursor)? as usize;
        let mut buf = vec![0u8; length];
        cursor.read_exact(&mut buf).map_err(|_| LLSDError::UnexpectedEndOfData)?;
        Ok(buf)
    }
}

/// LLSD binary serializer
#[derive(Debug, Default)]
pub struct LLSDBinarySerializer {
    include_magic: bool,
}

impl LLSDBinarySerializer {
    /// Create a new binary serializer
    pub fn new() -> Self {
        Self {
            include_magic: true,
        }
    }

    /// Don't include magic number in output (for partial serialization)
    pub fn without_magic(mut self) -> Self {
        self.include_magic = false;
        self
    }

    /// Serialize LLSD to binary data
    pub fn serialize(&self, document: &LLSDDocument) -> LLSDResult<Vec<u8>> {
        let mut buffer = BytesMut::new();

        if self.include_magic {
            buffer.put_u32(LLSD_BINARY_MAGIC);
        }

        self.write_value(&mut buffer, document.content())?;
        Ok(buffer.to_vec())
    }

    /// Write a single value to binary data
    fn write_value(&self, buffer: &mut BytesMut, value: &LLSDValue) -> LLSDResult<()> {
        match value {
            LLSDValue::Undefined => {
                buffer.put_u8(BinaryType::Undefined as u8);
            }
            LLSDValue::Boolean(b) => {
                buffer.put_u8(BinaryType::Boolean as u8);
                buffer.put_u8(if *b { 1 } else { 0 });
            }
            LLSDValue::Integer(i) => {
                buffer.put_u8(BinaryType::Integer as u8);
                buffer.put_i32(*i);
            }
            LLSDValue::Real(r) => {
                buffer.put_u8(BinaryType::Real as u8);
                buffer.put_f64(*r);
            }
            LLSDValue::String(s) => {
                buffer.put_u8(BinaryType::String as u8);
                self.write_string(buffer, s);
            }
            LLSDValue::UUID(u) => {
                buffer.put_u8(BinaryType::UUID as u8);
                buffer.put_slice(u.as_bytes());
            }
            LLSDValue::Date(d) => {
                buffer.put_u8(BinaryType::Date as u8);
                let timestamp = d.timestamp() as f64 + (d.timestamp_subsec_nanos() as f64 / 1e9);
                buffer.put_f64(timestamp);
            }
            LLSDValue::URI(u) => {
                buffer.put_u8(BinaryType::URI as u8);
                self.write_string(buffer, u);
            }
            LLSDValue::Binary(b) => {
                buffer.put_u8(BinaryType::Binary as u8);
                buffer.put_u32(b.len() as u32);
                buffer.put_slice(b);
            }
            LLSDValue::Array(arr) => {
                buffer.put_u8(BinaryType::Array as u8);
                buffer.put_u32(arr.len() as u32);
                for item in arr {
                    self.write_value(buffer, item)?;
                }
            }
            LLSDValue::Map(map) => {
                buffer.put_u8(BinaryType::Map as u8);
                buffer.put_u32(map.len() as u32);
                for (key, val) in map {
                    self.write_string(buffer, key);
                    self.write_value(buffer, val)?;
                }
            }
        }

        Ok(())
    }

    /// Write a string to binary data
    fn write_string(&self, buffer: &mut BytesMut, s: &str) {
        let bytes = s.as_bytes();
        buffer.put_u32(bytes.len() as u32);
        buffer.put_slice(bytes);
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use uuid::uuid;

    #[test]
    fn test_binary_round_trip() {
        let original = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("string".to_string(), LLSDValue::String("Hello World".to_string()));
            map.insert("integer".to_string(), LLSDValue::Integer(42));
            map.insert("real".to_string(), LLSDValue::Real(3.14159));
            map.insert("boolean".to_string(), LLSDValue::Boolean(true));
            map.insert("uuid".to_string(), LLSDValue::UUID(uuid!("550e8400-e29b-41d4-a716-446655440000")));
            map.insert("array".to_string(), LLSDValue::Array(vec![
                LLSDValue::Integer(1),
                LLSDValue::Integer(2),
                LLSDValue::Integer(3),
            ]));
            map
        });

        let document = LLSDDocument::new(original.clone());
        let serializer = LLSDBinarySerializer::new();
        let binary_data = serializer.serialize(&document).unwrap();

        let parser = LLSDBinaryParser::new();
        let parsed_document = parser.parse(&binary_data).unwrap();

        assert_eq!(*parsed_document.content(), original);
    }

    #[test]
    fn test_magic_number_validation() {
        let invalid_data = vec![0xFF, 0xFF, 0xFF, 0xFF, 0x00]; // Invalid magic + undefined
        let parser = LLSDBinaryParser::new();
        assert!(parser.parse(&invalid_data).is_err());

        let parser_no_magic = LLSDBinaryParser::new().without_magic_validation();
        // Should still fail because 0xFFFFFFFF is not a valid type
        assert!(parser_no_magic.parse(&invalid_data).is_err());
    }

    #[test]
    fn test_basic_types() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        // Test undefined
        let doc = LLSDDocument::new(LLSDValue::Undefined);
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Undefined);

        // Test boolean true
        let doc = LLSDDocument::new(LLSDValue::Boolean(true));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Boolean(true));

        // Test integer
        let doc = LLSDDocument::new(LLSDValue::Integer(-12345));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Integer(-12345));

        // Test real
        let doc = LLSDDocument::new(LLSDValue::Real(3.14159265));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        if let LLSDValue::Real(r) = parsed.content() {
            assert!((r - 3.14159265).abs() < 1e-10);
        } else {
            panic!("Expected real value");
        }
    }

    #[test]
    fn test_string_handling() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        let test_strings = vec![
            "",
            "Hello World",
            "Unicode: 你好世界",
            "Special chars: \n\t\r\0",
        ];

        for s in test_strings {
            let doc = LLSDDocument::new(LLSDValue::String(s.to_string()));
            let data = serializer.serialize(&doc).unwrap();
            let parsed = parser.parse(&data).unwrap();
            assert_eq!(*parsed.content(), LLSDValue::String(s.to_string()));
        }
    }

    #[test]
    fn test_binary_data() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        let test_data = vec![0x00, 0xFF, 0x42, 0xAB, 0xCD, 0xEF];
        let doc = LLSDDocument::new(LLSDValue::Binary(test_data.clone()));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Binary(test_data));
    }

    #[test]
    fn test_uuid_handling() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        let test_uuid = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let doc = LLSDDocument::new(LLSDValue::UUID(test_uuid));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::UUID(test_uuid));
    }

    #[test]
    fn test_date_handling() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        let test_date = Utc.timestamp_opt(1609459200, 500_000_000).single().unwrap();
        let doc = LLSDDocument::new(LLSDValue::Date(test_date));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        
        if let LLSDValue::Date(parsed_date) = parsed.content() {
            // Allow for some precision loss in the conversion
            let diff = (*parsed_date - test_date).num_milliseconds().abs();
            assert!(diff < 10, "Date difference too large: {} ms", diff);
        } else {
            panic!("Expected date value");
        }
    }

    #[test]
    fn test_nested_structures() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        let nested = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("level1".to_string(), LLSDValue::Map({
                let mut inner_map = HashMap::new();
                inner_map.insert("level2".to_string(), LLSDValue::Array(vec![
                    LLSDValue::String("deep".to_string()),
                    LLSDValue::Integer(123),
                ]));
                inner_map
            }));
            map
        });

        let doc = LLSDDocument::new(nested.clone());
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), nested);
    }

    #[test]
    fn test_empty_containers() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new();

        // Empty array
        let doc = LLSDDocument::new(LLSDValue::Array(Vec::new()));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Array(Vec::new()));

        // Empty map
        let doc = LLSDDocument::new(LLSDValue::Map(HashMap::new()));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Map(HashMap::new()));
    }

    #[test]
    fn test_large_structures() {
        let serializer = LLSDBinarySerializer::new();
        let parser = LLSDBinaryParser::new().with_max_elements(10000);

        // Large array
        let large_array: Vec<LLSDValue> = (0..1000)
            .map(|i| LLSDValue::Integer(i))
            .collect();

        let doc = LLSDDocument::new(LLSDValue::Array(large_array.clone()));
        let data = serializer.serialize(&doc).unwrap();
        let parsed = parser.parse(&data).unwrap();
        assert_eq!(*parsed.content(), LLSDValue::Array(large_array));
    }

    #[test]
    fn test_max_depth_protection() {
        let parser = LLSDBinaryParser::new().with_max_depth(3);
        
        // Create deeply nested structure (beyond max depth)
        let deeply_nested = LLSDValue::Map({
            let mut map1 = HashMap::new();
            map1.insert("level1".to_string(), LLSDValue::Map({
                let mut map2 = HashMap::new();
                map2.insert("level2".to_string(), LLSDValue::Map({
                    let mut map3 = HashMap::new();
                    map3.insert("level3".to_string(), LLSDValue::Map({
                        let mut map4 = HashMap::new();
                        map4.insert("level4".to_string(), LLSDValue::String("too deep".to_string()));
                        map4
                    }));
                    map3
                }));
                map2
            }));
            map1
        });

        let serializer = LLSDBinarySerializer::new();
        let data = serializer.serialize(&LLSDDocument::new(deeply_nested)).unwrap();
        
        // Should fail due to depth limit
        assert!(parser.parse(&data).is_err());
    }
}