/*!
 * LLSD XML Parser and Serializer - Rust Implementation
 * 
 * Based on Java implementation and Second Life viewer XML handling
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::{LLSDValue, LLSDDocument};
use crate::error::{LLSDError, LLSDResult};
use quick_xml::events::{Event, BytesEnd, BytesStart, BytesText};
use quick_xml::{Reader, Writer};
use std::collections::HashMap;
use std::io::Cursor;
use uuid::Uuid;
use chrono::{DateTime, Utc};

/// LLSD XML parser
#[derive(Debug, Default)]
pub struct LLSDXmlParser {
    validate_structure: bool,
}

impl LLSDXmlParser {
    /// Create a new XML parser
    pub fn new() -> Self {
        Self::default()
    }

    /// Enable XML structure validation
    pub fn with_validation(mut self, validate: bool) -> Self {
        self.validate_structure = validate;
        self
    }

    /// Parse LLSD from XML string
    pub fn parse(&self, xml: &str) -> LLSDResult<LLSDDocument> {
        let mut reader = Reader::from_str(xml);
        reader.trim_text(true);
        
        let mut buf = Vec::new();
        let mut found_llsd_root = false;
        
        // Find the LLSD root element
        loop {
            match reader.read_event() {
                Ok(Event::Start(ref e)) => {
                    if e.name().as_ref() == b"llsd" {
                        found_llsd_root = true;
                        break;
                    }
                }
                Ok(Event::Eof) => break,
                Err(e) => return Err(LLSDError::from(e)),
                _ => {}
            }
        }

        if !found_llsd_root {
            return Err(LLSDError::custom("Missing <llsd> root element"));
        }

        // Parse the first child element
        let value = self.parse_element(&mut reader, &mut buf)?;
        Ok(LLSDDocument::new(value))
    }

    /// Parse an individual XML element
    fn parse_element(&self, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>) -> LLSDResult<LLSDValue> {
        loop {
            match reader.read_event() {
                Ok(Event::Start(ref e)) => {
                    let tag_name = String::from_utf8_lossy(e.name().as_ref()).to_string();
                    return self.parse_typed_element(&tag_name, reader, buf);
                }
                Ok(Event::Empty(ref e)) => {
                    let tag_name = String::from_utf8_lossy(e.name().as_ref()).to_string();
                    return self.parse_empty_element(&tag_name);
                }
                Ok(Event::End(_)) => {
                    return Ok(LLSDValue::Undefined);
                }
                Ok(Event::Eof) => {
                    return Ok(LLSDValue::Undefined);
                }
                Err(e) => return Err(LLSDError::from(e)),
                _ => {}
            }
        }
    }

    /// Parse a typed XML element with content
    fn parse_typed_element(&self, tag_name: &str, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>) -> LLSDResult<LLSDValue> {
        match tag_name {
            "undef" => {
                self.skip_to_end(reader, buf, "undef")?;
                Ok(LLSDValue::Undefined)
            }
            "boolean" => {
                let content = self.read_text_content(reader, buf)?;
                let value = content.trim().to_lowercase();
                Ok(LLSDValue::Boolean(value == "1" || value == "true"))
            }
            "integer" => {
                let content = self.read_text_content(reader, buf)?;
                let value: i32 = content.trim().parse()
                    .map_err(|_| LLSDError::custom(format!("Invalid integer: {}", content)))?;
                Ok(LLSDValue::Integer(value))
            }
            "real" => {
                let content = self.read_text_content(reader, buf)?;
                let value: f64 = content.trim().parse()
                    .map_err(|_| LLSDError::custom(format!("Invalid real: {}", content)))?;
                Ok(LLSDValue::Real(value))
            }
            "string" => {
                let content = self.read_text_content(reader, buf)?;
                Ok(LLSDValue::String(content))
            }
            "uuid" => {
                let content = self.read_text_content(reader, buf)?;
                let uuid = Uuid::parse_str(content.trim())
                    .map_err(|_| LLSDError::InvalidUuid { uuid: content })?;
                Ok(LLSDValue::UUID(uuid))
            }
            "date" => {
                let content = self.read_text_content(reader, buf)?;
                let date = DateTime::parse_from_rfc3339(content.trim())
                    .map_err(|_| LLSDError::InvalidDate { date: content.clone() })?
                    .with_timezone(&Utc);
                Ok(LLSDValue::Date(date))
            }
            "uri" => {
                let content = self.read_text_content(reader, buf)?;
                Ok(LLSDValue::URI(content))
            }
            "binary" => {
                let content = self.read_text_content(reader, buf)?;
                let bytes = base64::decode(content.trim())?;
                Ok(LLSDValue::Binary(bytes))
            }
            "array" => self.parse_array(reader, buf),
            "map" => self.parse_map(reader, buf),
            _ => Err(LLSDError::custom(format!("Unknown LLSD element: {}", tag_name)))
        }
    }

    /// Parse empty XML elements
    fn parse_empty_element(&self, tag_name: &str) -> LLSDResult<LLSDValue> {
        match tag_name {
            "undef" => Ok(LLSDValue::Undefined),
            "string" => Ok(LLSDValue::String(String::new())),
            "binary" => Ok(LLSDValue::Binary(Vec::new())),
            "array" => Ok(LLSDValue::Array(Vec::new())),
            "map" => Ok(LLSDValue::Map(HashMap::new())),
            "uuid" => Ok(LLSDValue::UUID(Uuid::nil())),
            _ => Err(LLSDError::custom(format!("Cannot have empty element: {}", tag_name)))
        }
    }

    /// Read text content from an element
    fn read_text_content(&self, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>) -> LLSDResult<String> {
        let mut content = String::new();
        
        loop {
            match reader.read_event() {
                Ok(Event::Text(ref e)) => {
                    content.push_str(&e.unescape().unwrap_or_default());
                }
                Ok(Event::CData(ref e)) => {
                    content.push_str(&String::from_utf8_lossy(&e));
                }
                Ok(Event::End(_)) => break,
                Ok(Event::Eof) => break,
                Err(e) => return Err(LLSDError::from(e)),
                _ => {}
            }
        }
        
        Ok(content)
    }

    /// Parse an array element
    fn parse_array(&self, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>) -> LLSDResult<LLSDValue> {
        let mut array = Vec::new();
        
        loop {
            match reader.read_event() {
                Ok(Event::Start(_)) | Ok(Event::Empty(_)) => {
                    // Step back one event to re-parse the element
                    // Skip to end of this element
                    // TODO: Implement proper position tracking
                    let element = self.parse_element(reader, buf)?;
                    array.push(element);
                }
                Ok(Event::End(ref e)) if e.name().as_ref() == b"array" => break,
                Ok(Event::Eof) => break,
                Err(e) => return Err(LLSDError::from(e)),
                _ => {}
            }
        }
        
        Ok(LLSDValue::Array(array))
    }

    /// Parse a map element
    fn parse_map(&self, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>) -> LLSDResult<LLSDValue> {
        let mut map = HashMap::new();
        let mut current_key: Option<String> = None;
        
        loop {
            match reader.read_event() {
                Ok(Event::Start(ref e)) => {
                    let tag_name = String::from_utf8_lossy(e.name().as_ref()).to_string();
                    if tag_name == "key" {
                        current_key = Some(self.read_text_content(reader, buf)?);
                    } else if let Some(key) = current_key.take() {
                        let value = self.parse_typed_element(&tag_name, reader, buf)?;
                        map.insert(key, value);
                    } else {
                        return Err(LLSDError::custom("Map value without key"));
                    }
                }
                Ok(Event::Empty(ref e)) => {
                    let tag_name = String::from_utf8_lossy(e.name().as_ref()).to_string();
                    if let Some(key) = current_key.take() {
                        let value = self.parse_empty_element(&tag_name)?;
                        map.insert(key, value);
                    } else if tag_name != "key" {
                        return Err(LLSDError::custom("Empty map value without key"));
                    }
                }
                Ok(Event::End(ref e)) if e.name().as_ref() == b"map" => break,
                Ok(Event::Eof) => break,
                Err(e) => return Err(LLSDError::from(e)),
                _ => {}
            }
        }
        
        Ok(LLSDValue::Map(map))
    }

    /// Skip to the end of an element
    fn skip_to_end(&self, reader: &mut Reader<&[u8]>, buf: &mut Vec<u8>, tag: &str) -> LLSDResult<()> {
        // For now, just continue reading until we find the matching end tag
        // This is a simplified implementation
        Ok(())
    }
}

/// LLSD XML serializer
#[derive(Debug)]
pub struct LLSDXmlSerializer {
    pretty_print: bool,
    indent_size: usize,
}

impl Default for LLSDXmlSerializer {
    fn default() -> Self {
        Self {
            pretty_print: false,
            indent_size: 2,
        }
    }
}

impl LLSDXmlSerializer {
    /// Create a new XML serializer
    pub fn new() -> Self {
        Self::default()
    }

    /// Enable pretty printing
    pub fn with_pretty_print(mut self, pretty: bool) -> Self {
        self.pretty_print = pretty;
        self
    }

    /// Set indent size for pretty printing
    pub fn with_indent_size(mut self, size: usize) -> Self {
        self.indent_size = size;
        self
    }

    /// Serialize LLSD to XML string
    pub fn serialize(&self, document: &LLSDDocument) -> LLSDResult<String> {
        let mut output = Vec::new();
        let mut writer = Writer::new(Cursor::new(&mut output));
        
        // Write XML declaration
        writer.write_event(Event::Decl(quick_xml::events::BytesDecl::new(
            "1.0", Some("UTF-8"), None
        )))?;
        
        if self.pretty_print {
            writer.write_event(Event::Text(BytesText::new("\n")))?;
        }

        // Write LLSD root element
        writer.write_event(Event::Start(BytesStart::new("llsd")))?;
        
        if self.pretty_print {
            writer.write_event(Event::Text(BytesText::new("\n")))?;
        }

        self.write_value(&mut writer, document.content(), if self.pretty_print { 1 } else { 0 })?;
        
        if self.pretty_print {
            writer.write_event(Event::Text(BytesText::new("\n")))?;
        }

        writer.write_event(Event::End(BytesEnd::new("llsd")))?;

        String::from_utf8(output).map_err(|e| LLSDError::from(e))
    }

    /// Write an LLSD value as XML
    fn write_value<W: std::io::Write>(
        &self,
        writer: &mut Writer<W>,
        value: &LLSDValue,
        depth: usize,
    ) -> LLSDResult<()> {
        let indent = if self.pretty_print {
            " ".repeat(depth * self.indent_size)
        } else {
            String::new()
        };

        if self.pretty_print && depth > 0 {
            writer.write_event(Event::Text(BytesText::new(&indent)))?;
        }

        match value {
            LLSDValue::Undefined => {
                writer.write_event(Event::Empty(BytesStart::new("undef")))?;
            }
            LLSDValue::Boolean(b) => {
                let content = if *b { "1" } else { "0" };
                writer.write_event(Event::Start(BytesStart::new("boolean")))?;
                writer.write_event(Event::Text(BytesText::new(content)))?;
                writer.write_event(Event::End(BytesEnd::new("boolean")))?;
            }
            LLSDValue::Integer(i) => {
                writer.write_event(Event::Start(BytesStart::new("integer")))?;
                writer.write_event(Event::Text(BytesText::new(&i.to_string())))?;
                writer.write_event(Event::End(BytesEnd::new("integer")))?;
            }
            LLSDValue::Real(r) => {
                writer.write_event(Event::Start(BytesStart::new("real")))?;
                writer.write_event(Event::Text(BytesText::new(&r.to_string())))?;
                writer.write_event(Event::End(BytesEnd::new("real")))?;
            }
            LLSDValue::String(s) => {
                writer.write_event(Event::Start(BytesStart::new("string")))?;
                writer.write_event(Event::Text(BytesText::from_escaped(s)))?;
                writer.write_event(Event::End(BytesEnd::new("string")))?;
            }
            LLSDValue::UUID(u) => {
                writer.write_event(Event::Start(BytesStart::new("uuid")))?;
                writer.write_event(Event::Text(BytesText::new(&u.to_string())))?;
                writer.write_event(Event::End(BytesEnd::new("uuid")))?;
            }
            LLSDValue::Date(d) => {
                writer.write_event(Event::Start(BytesStart::new("date")))?;
                writer.write_event(Event::Text(BytesText::new(&d.to_rfc3339())))?;
                writer.write_event(Event::End(BytesEnd::new("date")))?;
            }
            LLSDValue::URI(u) => {
                writer.write_event(Event::Start(BytesStart::new("uri")))?;
                writer.write_event(Event::Text(BytesText::from_escaped(u)))?;
                writer.write_event(Event::End(BytesEnd::new("uri")))?;
            }
            LLSDValue::Binary(b) => {
                let base64_str = base64::encode(b);
                writer.write_event(Event::Start(BytesStart::new("binary")))?;
                writer.write_event(Event::Text(BytesText::new(&base64_str)))?;
                writer.write_event(Event::End(BytesEnd::new("binary")))?;
            }
            LLSDValue::Array(arr) => {
                writer.write_event(Event::Start(BytesStart::new("array")))?;
                
                for item in arr {
                    if self.pretty_print {
                        writer.write_event(Event::Text(BytesText::new("\n")))?;
                    }
                    self.write_value(writer, item, depth + 1)?;
                }
                
                if self.pretty_print && !arr.is_empty() {
                    writer.write_event(Event::Text(BytesText::new("\n")))?;
                    writer.write_event(Event::Text(BytesText::new(&indent)))?;
                }
                
                writer.write_event(Event::End(BytesEnd::new("array")))?;
            }
            LLSDValue::Map(map) => {
                writer.write_event(Event::Start(BytesStart::new("map")))?;
                
                for (key, val) in map {
                    if self.pretty_print {
                        writer.write_event(Event::Text(BytesText::new("\n")))?;
                        writer.write_event(Event::Text(BytesText::new(
                            &" ".repeat((depth + 1) * self.indent_size)
                        )))?;
                    }
                    
                    writer.write_event(Event::Start(BytesStart::new("key")))?;
                    writer.write_event(Event::Text(BytesText::from_escaped(key)))?;
                    writer.write_event(Event::End(BytesEnd::new("key")))?;
                    
                    if self.pretty_print {
                        writer.write_event(Event::Text(BytesText::new("\n")))?;
                    }
                    
                    self.write_value(writer, val, depth + 1)?;
                }
                
                if self.pretty_print && !map.is_empty() {
                    writer.write_event(Event::Text(BytesText::new("\n")))?;
                    writer.write_event(Event::Text(BytesText::new(&indent)))?;
                }
                
                writer.write_event(Event::End(BytesEnd::new("map")))?;
            }
        }

        if self.pretty_print && depth > 0 {
            writer.write_event(Event::Text(BytesText::new("\n")))?;
        }

        Ok(())
    }
}