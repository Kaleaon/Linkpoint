/*!
 * LLSD Error Types - Rust Implementation
 * 
 * Copyright (C) 2024 Linden Lab
 */

use thiserror::Error;

/// LLSD error types
#[derive(Error, Debug)]
pub enum LLSDError {
    /// XML parsing error
    #[error("XML parsing error: {0}")]
    XmlError(#[from] quick_xml::Error),

    /// JSON parsing error
    #[error("JSON parsing error: {0}")]
    JsonError(#[from] serde_json::Error),

    /// Binary parsing error
    #[error("Binary parsing error: {message}")]
    BinaryError { message: String },

    /// Invalid magic number in binary format
    #[error("Invalid LLSD binary magic number")]
    InvalidMagic,

    /// Invalid data type
    #[error("Invalid LLSD data type: {type_id}")]
    InvalidType { type_id: u8 },

    /// Unexpected end of data
    #[error("Unexpected end of data while parsing")]
    UnexpectedEndOfData,

    /// Invalid UUID format
    #[error("Invalid UUID format: {uuid}")]
    InvalidUuid { uuid: String },

    /// Invalid URI format
    #[error("Invalid URI format: {uri}")]
    InvalidUri { uri: String },

    /// Invalid date format
    #[error("Invalid date format: {date}")]
    InvalidDate { date: String },

    /// Base64 decoding error
    #[error("Base64 decoding error: {0}")]
    Base64Error(#[from] base64::DecodeError),

    /// UTF-8 encoding error
    #[error("UTF-8 encoding error: {0}")]
    Utf8Error(#[from] std::string::FromUtf8Error),

    /// I/O error
    #[error("I/O error: {0}")]
    IoError(#[from] std::io::Error),

    /// Validation error
    #[error("Validation error: {message}")]
    ValidationError { message: String },

    /// Missing required field
    #[error("Missing required field: {field}")]
    MissingField { field: String },

    /// Type mismatch error
    #[error("Type mismatch: expected {expected}, got {actual}")]
    TypeMismatch { expected: String, actual: String },

    /// Path not found error
    #[error("Path not found: {path}")]
    PathNotFound { path: String },

    /// Index out of bounds error
    #[error("Index out of bounds: {index}")]
    IndexOutOfBounds { index: usize },

    /// Generic error with custom message
    #[error("{message}")]
    CustomError { message: String },
}

impl LLSDError {
    /// Create a binary error with a custom message
    pub fn binary_error<S: Into<String>>(message: S) -> Self {
        LLSDError::BinaryError {
            message: message.into(),
        }
    }

    /// Create a validation error with a custom message
    pub fn validation_error<S: Into<String>>(message: S) -> Self {
        LLSDError::ValidationError {
            message: message.into(),
        }
    }

    /// Create a custom error with a message
    pub fn custom<S: Into<String>>(message: S) -> Self {
        LLSDError::CustomError {
            message: message.into(),
        }
    }

    /// Create a missing field error
    pub fn missing_field<S: Into<String>>(field: S) -> Self {
        LLSDError::MissingField {
            field: field.into(),
        }
    }

    /// Create a type mismatch error
    pub fn type_mismatch<S: Into<String>>(expected: S, actual: S) -> Self {
        LLSDError::TypeMismatch {
            expected: expected.into(),
            actual: actual.into(),
        }
    }

    /// Create a path not found error
    pub fn path_not_found<S: Into<String>>(path: S) -> Self {
        LLSDError::PathNotFound {
            path: path.into(),
        }
    }
}

/// Result type for LLSD operations
pub type LLSDResult<T> = Result<T, LLSDError>;