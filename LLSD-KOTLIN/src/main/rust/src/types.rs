/*!
 * LLSD Core Types - Rust Implementation
 * 
 * Based on Java implementation and Second Life viewer types
 * Copyright (C) 2024 Linden Lab
 */

use std::collections::HashMap;
use std::borrow::Borrow;
use serde::{Deserialize, Serialize};
use uuid::Uuid;
use chrono::{DateTime, Utc};

/// LLSD data types enumeration
#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, Serialize, Deserialize)]
pub enum LLSDType {
    Unknown,
    Boolean,
    Integer,
    Real,
    String,
    UUID,
    Date,
    URI,
    Binary,
    Map,
    Array,
}

/// LLSD serialization formats
#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum LLSDFormat {
    XML,
    JSON,
    Binary,
    Notation,
}

/// LLSD Value enumeration representing all possible LLSD data types
#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(untagged)]
pub enum LLSDValue {
    /// Undefined/null value
    Undefined,
    /// Boolean value
    Boolean(bool),
    /// Integer value (32-bit signed)
    Integer(i32),
    /// Real/float value (64-bit)
    Real(f64),
    /// String value
    String(String),
    /// UUID value
    UUID(Uuid),
    /// Date/timestamp value
    Date(DateTime<Utc>),
    /// URI value
    URI(String),
    /// Binary data
    Binary(Vec<u8>),
    /// Map/object with string keys
    Map(HashMap<String, LLSDValue>),
    /// Array of values
    Array(Vec<LLSDValue>),
}

impl LLSDValue {
    /// Get the type of this LLSD value
    pub fn get_type(&self) -> LLSDType {
        match self {
            LLSDValue::Undefined => LLSDType::Unknown,
            LLSDValue::Boolean(_) => LLSDType::Boolean,
            LLSDValue::Integer(_) => LLSDType::Integer,
            LLSDValue::Real(_) => LLSDType::Real,
            LLSDValue::String(_) => LLSDType::String,
            LLSDValue::UUID(_) => LLSDType::UUID,
            LLSDValue::Date(_) => LLSDType::Date,
            LLSDValue::URI(_) => LLSDType::URI,
            LLSDValue::Binary(_) => LLSDType::Binary,
            LLSDValue::Map(_) => LLSDType::Map,
            LLSDValue::Array(_) => LLSDType::Array,
        }
    }

    /// Check if this value is undefined
    pub fn is_undefined(&self) -> bool {
        matches!(self, LLSDValue::Undefined)
    }

    /// Try to get this value as a boolean
    pub fn as_boolean(&self) -> Option<bool> {
        match self {
            LLSDValue::Boolean(b) => Some(*b),
            _ => None,
        }
    }

    /// Try to get this value as an integer
    pub fn as_integer(&self) -> Option<i32> {
        match self {
            LLSDValue::Integer(i) => Some(*i),
            _ => None,
        }
    }

    /// Try to get this value as a real number
    pub fn as_real(&self) -> Option<f64> {
        match self {
            LLSDValue::Real(r) => Some(*r),
            LLSDValue::Integer(i) => Some(*i as f64),
            _ => None,
        }
    }

    /// Try to get this value as a string
    pub fn as_string(&self) -> Option<&str> {
        match self {
            LLSDValue::String(s) => Some(s),
            LLSDValue::URI(s) => Some(s),
            _ => None,
        }
    }

    /// Try to get this value as a UUID
    pub fn as_uuid(&self) -> Option<Uuid> {
        match self {
            LLSDValue::UUID(u) => Some(*u),
            _ => None,
        }
    }

    /// Try to get this value as a date
    pub fn as_date(&self) -> Option<DateTime<Utc>> {
        match self {
            LLSDValue::Date(d) => Some(*d),
            _ => None,
        }
    }

    /// Try to get this value as a URI string
    pub fn as_uri(&self) -> Option<&str> {
        match self {
            LLSDValue::URI(u) => Some(u),
            _ => None,
        }
    }

    /// Try to get this value as binary data
    pub fn as_binary(&self) -> Option<&[u8]> {
        match self {
            LLSDValue::Binary(b) => Some(b),
            _ => None,
        }
    }

    /// Try to get this value as a map
    pub fn as_map(&self) -> Option<&HashMap<String, LLSDValue>> {
        match self {
            LLSDValue::Map(m) => Some(m),
            _ => None,
        }
    }

    /// Try to get this value as a mutable map
    pub fn as_map_mut(&mut self) -> Option<&mut HashMap<String, LLSDValue>> {
        match self {
            LLSDValue::Map(m) => Some(m),
            _ => None,
        }
    }

    /// Try to get this value as an array
    pub fn as_array(&self) -> Option<&[LLSDValue]> {
        match self {
            LLSDValue::Array(a) => Some(a),
            _ => None,
        }
    }

    /// Try to get this value as a mutable array
    pub fn as_array_mut(&mut self) -> Option<&mut Vec<LLSDValue>> {
        match self {
            LLSDValue::Array(a) => Some(a),
            _ => None,
        }
    }

    /// Get a nested value using dot notation path
    pub fn get_path(&self, path: &str) -> Option<&LLSDValue> {
        let parts: Vec<&str> = path.split('.').collect();
        let mut current = self;

        for part in parts {
            match current {
                LLSDValue::Map(map) => {
                    current = map.get(part)?;
                }
                LLSDValue::Array(arr) => {
                    let index: usize = part.parse().ok()?;
                    current = arr.get(index)?;
                }
                _ => return None,
            }
        }

        Some(current)
    }

    /// Set a nested value using dot notation path
    pub fn set_path(&mut self, path: &str, value: LLSDValue) -> bool {
        let parts: Vec<&str> = path.split('.').collect();
        if parts.is_empty() {
            return false;
        }

        let mut current = self;
        let last_part = parts[parts.len() - 1];

        // Navigate to the parent of the target
        for part in &parts[..parts.len() - 1] {
            match current {
                LLSDValue::Map(map) => {
                    let key_str = part.to_string();
                    match map.get_mut(&key_str) {
                        Some(next) => current = next,
                        None => return false,
                    }
                }
                LLSDValue::Array(arr) => {
                    if let Ok(index) = part.parse::<usize>() {
                        if let Some(next) = arr.get_mut(index) {
                            current = next;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                _ => return false,
            }
        }

        // Set the final value
        match current {
            LLSDValue::Map(map) => {
                map.insert(last_part.to_string(), value);
                true
            }
            LLSDValue::Array(arr) => {
                if let Ok(index) = last_part.parse::<usize>() {
                    if index < arr.len() {
                        arr[index] = value;
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
            _ => false,
        }
    }
}

impl Default for LLSDValue {
    fn default() -> Self {
        LLSDValue::Undefined
    }
}

impl From<bool> for LLSDValue {
    fn from(value: bool) -> Self {
        LLSDValue::Boolean(value)
    }
}

impl From<i32> for LLSDValue {
    fn from(value: i32) -> Self {
        LLSDValue::Integer(value)
    }
}

impl From<f64> for LLSDValue {
    fn from(value: f64) -> Self {
        LLSDValue::Real(value)
    }
}

impl From<String> for LLSDValue {
    fn from(value: String) -> Self {
        LLSDValue::String(value)
    }
}

impl From<&str> for LLSDValue {
    fn from(value: &str) -> Self {
        LLSDValue::String(value.to_string())
    }
}

impl From<Uuid> for LLSDValue {
    fn from(value: Uuid) -> Self {
        LLSDValue::UUID(value)
    }
}

impl From<DateTime<Utc>> for LLSDValue {
    fn from(value: DateTime<Utc>) -> Self {
        LLSDValue::Date(value)
    }
}

impl From<Vec<u8>> for LLSDValue {
    fn from(value: Vec<u8>) -> Self {
        LLSDValue::Binary(value)
    }
}

impl From<HashMap<String, LLSDValue>> for LLSDValue {
    fn from(value: HashMap<String, LLSDValue>) -> Self {
        LLSDValue::Map(value)
    }
}

impl From<Vec<LLSDValue>> for LLSDValue {
    fn from(value: Vec<LLSDValue>) -> Self {
        LLSDValue::Array(value)
    }
}

/// LLSD Document container
#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct LLSDDocument {
    content: LLSDValue,
}

impl LLSDDocument {
    /// Create a new LLSD document with the given content
    pub fn new(content: LLSDValue) -> Self {
        Self { content }
    }

    /// Create a new empty LLSD document
    pub fn empty() -> Self {
        Self {
            content: LLSDValue::Undefined,
        }
    }

    /// Get the content of the document
    pub fn content(&self) -> &LLSDValue {
        &self.content
    }

    /// Get mutable access to the content of the document
    pub fn content_mut(&mut self) -> &mut LLSDValue {
        &mut self.content
    }

    /// Set the content of the document
    pub fn set_content(&mut self, content: LLSDValue) {
        self.content = content;
    }

    /// Get the type of the root content
    pub fn get_type(&self) -> LLSDType {
        self.content.get_type()
    }

    /// Check if the document is empty (undefined)
    pub fn is_empty(&self) -> bool {
        self.content.is_undefined()
    }
}

impl Default for LLSDDocument {
    fn default() -> Self {
        Self::empty()
    }
}

impl From<LLSDValue> for LLSDDocument {
    fn from(value: LLSDValue) -> Self {
        Self::new(value)
    }
}