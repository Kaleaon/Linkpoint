//! # lib.rs
//!
//!  Rust library for serializing and de-serializing data in
//!  Linden Lab Structured Data format.
//!
//!  Serde version.
//!
//!  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//
//  Animats
//  October, 2021.
//  License: LGPL.
//
//
//  Modules
//
pub mod converter;
pub mod de;
pub mod ser;

pub use crate::{
    de::{
        auto_from_bytes,
        auto_from_str,
        binary::from_bytes as binary_from_bytes,
        binary::from_reader as binary_from_reader, // Name clash
        notation::from_bytes as notation_from_bytes,
        notation::from_str as notation_from_str,
        xml::from_str,
    },
    ser::{
        binary::to_bytes,
        binary::to_writer as binary_to_writer,     // Name clash
        notation::to_string as notation_to_string, // Name clash
        xml::to_string,
        xml::to_writer,
    },
};

use enum_as_inner::EnumAsInner;
use std::collections::HashMap;
use uuid::Uuid;

/// The primitive LLSD data item.
/// Serialization takes a tree of these.
/// Deserialization returns a tree of these.
#[derive(Debug, Clone, PartialEq, EnumAsInner)]
pub enum LLSDValue {
    /// Not convertable.
    Undefined,
    /// Boolean
    Boolean(bool),
    /// Real, always 64-bit.
    Real(f64),
    /// Integer, always 32 bit, for historical reasons.
    Integer(i32),
    /// UUID, as a binary 128 bit value.
    UUID(Uuid),
    /// String, UTF-8.
    String(String),
    /// Date, as seconds relative to the UNIX epoch, January 1, 1970.
    Date(i64),
    /// Universal Resource Identifier
    URI(String),
    /// Array of bytes.
    Binary(Vec<u8>),
    /// Key/value set of more LLSDValue items.
    Map(HashMap<String, LLSDValue>),
    /// Array of more LLSDValue items.
    Array(Vec<LLSDValue>),
}
