/*!
 * LLSD Rust Implementation
 * 
 * Complete implementation of LLSD (Linden Lab Structured Data) format
 * Based on Java implementation and Second Life/Firestorm viewer code
 * 
 * Copyright (C) 2024 Linden Lab
 */

pub mod types;
pub mod xml;
pub mod binary;
pub mod json;
pub mod utils;
pub mod error;

#[cfg(feature = "secondlife")]
pub mod secondlife;

#[cfg(feature = "firestorm")]
pub mod firestorm;

// Re-export core types
pub use types::{LLSDValue, LLSDType, LLSDFormat, LLSDDocument};
pub use error::{LLSDError, LLSDResult};
pub use utils::LLSDUtils;

// Re-export parsers and serializers
pub use xml::{LLSDXmlParser, LLSDXmlSerializer};
pub use binary::{LLSDBinaryParser, LLSDBinarySerializer};
pub use json::{LLSDJsonParser, LLSDJsonSerializer};

#[cfg(feature = "secondlife")]
pub use secondlife::SecondLifeLLSDUtils;

#[cfg(feature = "firestorm")]
pub use firestorm::FirestormLLSDUtils;

/// Factory for creating LLSD parsers and serializers
pub struct LLSDFactory;

impl LLSDFactory {
    /// Parse LLSD from XML string
    pub fn parse_xml(xml: &str) -> LLSDResult<LLSDDocument> {
        let parser = LLSDXmlParser::new();
        parser.parse(xml)
    }

    /// Parse LLSD from binary data
    pub fn parse_binary(data: &[u8]) -> LLSDResult<LLSDDocument> {
        let parser = LLSDBinaryParser::new();
        parser.parse(data)
    }

    /// Parse LLSD from JSON string
    pub fn parse_json(json: &str) -> LLSDResult<LLSDDocument> {
        let parser = LLSDJsonParser::new();
        parser.parse(json)
    }

    /// Serialize LLSD to XML string
    pub fn serialize_xml(document: &LLSDDocument, pretty: bool) -> LLSDResult<String> {
        let serializer = LLSDXmlSerializer::new().with_pretty_print(pretty);
        serializer.serialize(document)
    }

    /// Serialize LLSD to binary data
    pub fn serialize_binary(document: &LLSDDocument) -> LLSDResult<Vec<u8>> {
        let serializer = LLSDBinarySerializer::new();
        serializer.serialize(document)
    }

    /// Serialize LLSD to JSON string
    pub fn serialize_json(document: &LLSDDocument, pretty: bool) -> LLSDResult<String> {
        let serializer = LLSDJsonSerializer::new().with_pretty_print(pretty);
        serializer.serialize(document)
    }

    /// Create an LLSD document with the given content
    pub fn create(content: LLSDValue) -> LLSDDocument {
        LLSDDocument::new(content)
    }

    /// Create an LLSD document with a map
    pub fn create_map() -> LLSDDocument {
        LLSDDocument::new(LLSDValue::Map(std::collections::HashMap::new()))
    }

    /// Create an LLSD document with an array
    pub fn create_array() -> LLSDDocument {
        LLSDDocument::new(LLSDValue::Array(Vec::new()))
    }
}