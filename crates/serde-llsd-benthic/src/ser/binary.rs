//! # ser/binary -- serialize LLSD, binary form.
//!
//!  Library for serializing and de-serializing data in
//!  Linden Lab Structured Data format.
//!
//!  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//!
//!  Binary format, serialization.
//
//  Animats
//  March, 2021.
//  License: LGPL.
//
use crate::LLSDValue;
use anyhow::Error;
use std::io::Write;
//
//  Constants
//
// Binary LLSD prefix
pub const LLSDBINARYPREFIX: &[u8] = b"<? LLSD/Binary ?>\n"; // binary LLSD prefix
pub const LLSDBINARYSENTINEL: &[u8] = LLSDBINARYPREFIX; // prefix must match exactly

/// Outputs an LLSDValue as a string of bytes, in LLSD "binary" format.
pub fn to_bytes(val: &LLSDValue) -> Result<Vec<u8>, Error> {
    let mut writer: Vec<u8> = Vec::new(); // just make a stream and use the stream form
    to_writer(&mut writer, val)?;
    Ok(writer)
}

/// Outputs an LLSD value to an output stream
pub fn to_writer<W: Write>(writer: &mut W, val: &LLSDValue) -> Result<(), Error> {
    writer.write_all(LLSDBINARYPREFIX)?; // prefix
    generate_value(writer, val)?;
    writer.flush()?;
    Ok(())
}

/// Generate one <TYPE> VALUE </TYPE> output. VALUE is recursive.
fn generate_value<W: Write>(writer: &mut W, val: &LLSDValue) -> Result<(), Error> {
    //  Emit binary for all possible types.
    match val {
        LLSDValue::Undefined => writer.write_all(b"!")?,
        LLSDValue::Boolean(v) => writer.write_all(if *v { b"1" } else { b"0" })?,
        LLSDValue::String(v) => {
            writer.write_all(b"s")?;
            writer.write_all(&(v.len() as u32).to_be_bytes())?;
            writer.write_all(v.as_bytes())?
        }
        LLSDValue::URI(v) => {
            writer.write_all(b"l")?;
            writer.write_all(&(v.len() as u32).to_be_bytes())?;
            writer.write_all(v.as_bytes())?
        }
        LLSDValue::Integer(v) => {
            writer.write_all(b"i")?;
            writer.write_all(&v.to_be_bytes())?
        }
        LLSDValue::Real(v) => {
            writer.write_all(b"r")?;
            writer.write_all(&v.to_be_bytes())?
        }
        LLSDValue::UUID(v) => {
            writer.write_all(b"u")?;
            writer.write_all(v.as_bytes())?
        }
        LLSDValue::Binary(v) => {
            writer.write_all(b"b")?;
            writer.write_all(&(v.len() as u32).to_be_bytes())?;
            writer.write_all(v)?
        }
        LLSDValue::Date(v) => {
            writer.write_all(b"d")?;
            writer.write_all(&(*v as f64).to_le_bytes())?
        }

        //  Map is { childcnt key value key value ... }
        LLSDValue::Map(v) => {
            //  Output count of key/value pairs
            writer.write_all(b"{")?;
            writer.write_all(&(v.len() as u32).to_be_bytes())?;
            //  Output key/value pairs
            for (key, value) in v {
                writer.write_all(b"k")?; // k prefix to key. UNDOCUMENTED
                writer.write_all(&(key.len() as u32).to_be_bytes())?;
                writer.write_all(key.as_bytes())?;
                generate_value(writer, value)?;
            }
            writer.write_all(b"}")?
        }
        //  Array is [ childcnt child child ... ]
        LLSDValue::Array(v) => {
            //  Output count of array entries
            writer.write_all(b"[")?;
            writer.write_all(&(v.len() as u32).to_be_bytes())?;
            //  Output array entries
            for value in v {
                generate_value(writer, value)?;
            }
            writer.write_all(b"]")?
        }
    };
    Ok(())
}
