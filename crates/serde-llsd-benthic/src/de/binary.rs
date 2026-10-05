//! #  de/binary -- de-serialize LLSD, binary form.
//!
//!  Library for serializing and de-serializing data in
//!  Linden Lab Structured Data format.
//!
//!  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//!
//!  Binary format.
//
//  Animats
//  March, 2021.
//  License: LGPL.
//
use crate::LLSDValue;
use anyhow::{anyhow, Error};
use std::collections::HashMap;
use std::io::{Cursor, Read};
use uuid;
//
//  Constants
//
pub const LLSDBINARYPREFIX: &[u8] = b"<? LLSD/Binary ?>\n"; // binary LLSD prefix
pub const LLSDBINARYSENTINEL: &[u8] = LLSDBINARYPREFIX; // prefix must match exactly

///    Parse LLSD array expressed in binary into an LLSDObject tree. No header.
pub fn from_bytes(b: &[u8]) -> Result<LLSDValue, Error> {
    let mut cursor: Cursor<&[u8]> = Cursor::new(b);
    parse_value(&mut cursor)
}

///    Parse LLSD reader expressed in binary into an LLSDObject tree. No header.
pub fn from_reader(cursor: &mut dyn Read) -> Result<LLSDValue, Error> {
    parse_value(cursor)
}

/// Parse one value - real, integer, map, etc. Recursive.
fn parse_value(cursor: &mut dyn Read) -> Result<LLSDValue, Error> {
    //  These could be generic if generics with numeric parameters were in stable Rust.
    fn read_u8(cursor: &mut dyn Read) -> Result<u8, Error> {
        let mut b: [u8; 1] = [0; 1];
        cursor.read_exact(&mut b)?; // read one byte
        Ok(b[0])
    }
    fn read_u32(cursor: &mut dyn Read) -> Result<u32, Error> {
        let mut b: [u8; 4] = [0; 4];
        cursor.read_exact(&mut b)?; // read one byte
        Ok(u32::from_be_bytes(b))
    }
    fn read_i32(cursor: &mut dyn Read) -> Result<i32, Error> {
        let mut b: [u8; 4] = [0; 4];
        cursor.read_exact(&mut b)?; // read one byte
        Ok(i32::from_be_bytes(b))
    }
    #[allow(dead_code)]
    fn read_i64(cursor: &mut dyn Read) -> Result<i64, Error> {
        let mut b: [u8; 8] = [0; 8];
        cursor.read_exact(&mut b)?; // read one byte
        Ok(i64::from_be_bytes(b))
    }
    fn read_f64(cursor: &mut dyn Read) -> Result<f64, Error> {
        let mut b: [u8; 8] = [0; 8];
        cursor.read_exact(&mut b)?; // read one byte
        Ok(f64::from_be_bytes(b))
    }
    fn read_f64_le(cursor: &mut dyn Read) -> Result<f64, Error> {
        let mut b: [u8; 8] = [0; 8];
        cursor.read_exact(&mut b)?;
        Ok(f64::from_le_bytes(b))
    }
    fn read_variable(cursor: &mut dyn Read) -> Result<Vec<u8>, Error> {
        let length = read_u32(cursor)?; // read length in bytes
        let mut buf = vec![0u8; length as usize];
        cursor.read_exact(&mut buf)?;
        Ok(buf) // read bytes of string
    }

    let typecode = read_u8(cursor)?;
    match typecode {
        //  Undefined - the empty value
        b'!' => Ok(LLSDValue::Undefined),
        //  Boolean - 1 or 0
        b'0' => Ok(LLSDValue::Boolean(false)),
        b'1' => Ok(LLSDValue::Boolean(true)),
        //  String - length followed by data
        b's' => Ok(LLSDValue::String(
            std::str::from_utf8(&read_variable(cursor)?)?.to_string(),
        )),
        //  URI - length followed by data
        b'l' => Ok(LLSDValue::URI(
            std::str::from_utf8(&read_variable(cursor)?)?.to_string(),
        )),
        //  Integer - 4 bytes
        b'i' => Ok(LLSDValue::Integer(read_i32(cursor)?)),
        //  Real - 4 bytes
        b'r' => Ok(LLSDValue::Real(read_f64(cursor)?)),
        //  UUID - 16 bytes
        b'u' => {
            let mut buf: [u8; 16] = [0u8; 16];
            cursor.read_exact(&mut buf)?; // read bytes of string
            Ok(LLSDValue::UUID(uuid::Uuid::from_bytes(buf)))
        }
        //  Binary - length followed by data
        b'b' => Ok(LLSDValue::Binary(read_variable(cursor)?)),
        //  Date - 64 bits little-endian float seconds since epoch
        b'd' => Ok(LLSDValue::Date(read_f64_le(cursor)? as i64)),
        //  Map -- keyed collection of items
        b'{' => {
            let mut dict: HashMap<String, LLSDValue> = HashMap::new(); // accumulate hash here
            let count = read_u32(cursor)?; // number of items
            for _ in 0..count {
                let keyprefix = &read_u8(cursor)?; // key should begin with b'k';
                match keyprefix {
                    b'k' => {
                        let key = std::str::from_utf8(&read_variable(cursor)?)?.to_string();
                        let _ = dict.insert(key, parse_value(cursor)?); // recurse and add, allowing dups
                    }
                    _ => {
                        return Err(anyhow!(
                            "Binary LLSD map key had {:?} instead of expected 'k'",
                            keyprefix
                        ))
                    }
                }
            }
            if read_u8(cursor)? != b'}' {
                return Err(anyhow!("Binary LLSD map did not end properly with }}"));
            }
            Ok(LLSDValue::Map(dict))
        }
        //  Array -- array of items
        b'[' => {
            let mut array: Vec<LLSDValue> = Vec::new(); // accumulate hash here
            let count = read_u32(cursor)?; // number of items
            for _ in 0..count {
                array.push(parse_value(cursor)?); // recurse and add, allowing dups
            }
            if read_u8(cursor)? != b']' {
                return Err(anyhow!("Binary LLSD array did not end properly with ] "));
            }
            Ok(LLSDValue::Array(array))
        }

        _ => Err(anyhow!("Binary LLSD, unexpected type code {:?}", typecode)),
    }
}

// Unit test

#[test]
fn binaryparsetest1() {
    //  Construct a test value.
    let test1map: HashMap<String, LLSDValue> = [
        ("val1".to_string(), LLSDValue::Real(456.0)),
        ("val2".to_string(), LLSDValue::Integer(999)),
    ]
    .iter()
    .cloned()
    .collect();
    let test1: LLSDValue = LLSDValue::Array(vec![
        LLSDValue::Real(123.5),
        LLSDValue::Map(test1map),
        LLSDValue::Integer(42),
        LLSDValue::String("Hello world".to_string()),
    ]);
    //  Convert to binary form.
    let test1bin = crate::to_bytes(&test1).unwrap();
    println!("Binary form: {:?}", test1bin);
    //  Convert back to value form.
    let test1value = from_bytes(&test1bin[LLSDBINARYSENTINEL.len()..]).unwrap();
    println!("Value after round-trip conversion: {:?}", test1value);
    //  Check that results match after round trip.
    assert_eq!(test1, test1value);
}
