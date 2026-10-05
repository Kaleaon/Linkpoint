//! # ser/notation -- serialize LLSD, notation form.
//!
//!  Library for serializing and de-serializing data in
//!  Linden Lab Structured Data format.
//!
//!  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//!
//!  Notation format, serialization.
//
//  Animats
//  July, 2023.
//  License: LGPL.
//
use crate::LLSDValue;
use anyhow::Error;
use base64::Engine;
use chrono::TimeZone;
//
//  Constants
//
/// Notation LLSD prefix
pub const LLSDNOTATIONPREFIX: &str = "<? llsd/notation ?>\n";
/// Sentinel, must match exactly.
pub const LLSDNOTATIONSENTINEL: &str = LLSDNOTATIONPREFIX;

/// Outputs an LLSDValue as a string of bytes, in LLSD "notation" format.
pub fn to_string(val: &LLSDValue) -> Result<String, Error> {
    let mut writer = String::new();
    writer.push_str(LLSDNOTATIONPREFIX); // prefix
    generate_value(&mut writer, val)?;
    Ok(writer)
}

//  There could be a corresponding function to generate LLSD notation as bytes,
//  but that creates transparency problems best avoided.

/*
/// Outputs an LLSD value to an output stream
pub fn to_writer<W: Write>(writer: &mut W, val: &LLSDValue) -> Result<(), Error> {
    writer.write_all(LLSDNOTATIONPREFIX)?; // prefix
    generate_value(writer, val)?;
    writer.flush()?;
    Ok(())
}
*/
/// Generate one <TYPE> VALUE </TYPE> output. VALUE is recursive.
fn generate_value(writer: &mut String, val: &LLSDValue) -> Result<(), Error> {
    //  Emit notation form for all possible types.
    match val {
        LLSDValue::Undefined => writer.push('!'),
        LLSDValue::Boolean(v) => writer.push(if *v { 'T' } else { 'F' }),
        LLSDValue::String(v) => {
            writer.push('"');
            writer.push_str(&escape_quotes(v));
            writer.push('"');
        }
        LLSDValue::URI(v) => {
            writer.push('l');
            writer.push('"');
            writer.push_str(&escape_url(v));
            writer.push('"');
        }
        LLSDValue::Integer(v) => {
            writer.push('i');
            writer.push_str(&format!("{}", v));
        }
        LLSDValue::Real(v) => {
            writer.push('r');
            writer.push_str(&format!("{}", v));
        }
        LLSDValue::UUID(v) => {
            writer.push('u');
            writer.push_str(&v.to_string());
        }
        LLSDValue::Binary(v) => {
            writer.push('b');
            writer.push('6');
            writer.push('4');
            writer.push('"');
            writer.push_str(&base64::engine::general_purpose::STANDARD.encode(v));
            writer.push('"');
        }
        LLSDValue::Date(v) => {
            writer.push('d');
            writer.push_str(
                &chrono::Utc
                    .timestamp_opt(*v, 0)
                    .earliest()
                    .unwrap() // may panic for times prior to January 1, 1970.
                    .to_rfc3339_opts(chrono::SecondsFormat::Secs, true),
            )
        }

        //  Map is {  key : value, key : value ... }
        LLSDValue::Map(v) => {
            //  Curly bracketed list
            writer.push('{');
            //  Output key/value pairs
            let mut first: bool = true;
            for (key, value) in v {
                if !first {
                    writer.push(',');
                    writer.push('\n');
                }
                first = false;
                writer.push('\'');
                writer.push_str(key);
                writer.push('\'');
                writer.push(':');
                generate_value(writer, value)?;
            }
            writer.push('}');
        }
        //  Array is [ child, child ... ]
        LLSDValue::Array(v) => {
            //  Square bracketed list
            writer.push('[');
            //  Output array entries
            let mut first: bool = true;
            for value in v {
                if !first {
                    writer.push(',');
                    writer.push('\n');
                }
                first = false;
                generate_value(writer, value)?;
            }
            writer.push(']');
        }
    };
    Ok(())
}

/// Escape double quote as \", and of course \ as \\.
fn escape_quotes(s: &str) -> String {
    let mut writer = String::new();
    for ch in s.chars() {
        match ch {
            '"' | '\\' => {
                writer.push('\\');
                writer.push(ch)
            }
            _ => writer.push(ch),
        }
    }
    writer
}

/// Escape URL per RFC1738
fn escape_url(s: &str) -> String {
    urlencoding::encode(s).to_string()
}

//  Limited test case. Better tests on decode side.
#[test]
fn notationgentest1() {
    const TESTXMLNAN: &str = r#"
<?xml version="1.0" encoding="UTF-8"?>
<llsd>
<array>
<real>nan</real>
<real>0</real>
<undef />
</array>
</llsd>
"#;

    const TESTXML1: &str = r#"
<?xml version="1.0" encoding="UTF-8"?>
<llsd>
<map>
  <key>region_id</key>
    <uuid>67153d5b-3659-afb4-8510-adda2c034649</uuid>
  <key>scale</key>
    <string>one minute</string>
  <key>simulator statistics</key>
  <map>
    <key>time dilation</key><real>0.9878624</real>
    <key>sim fps</key><real>44.38898</real>
    <key>pysics fps</key><real>44.38906</real>
    <key>lsl instructions per second</key><real>0</real>
    <key>total task count</key><real>4</real>
    <key>active task count</key><real>0</real>
    <key>active script count</key><real>4</real>
    <key>main agent count</key><real>0</real>
    <key>child agent count</key><real>0</real>
    <key>inbound packets per second</key><real>1.228283</real>
    <key>outbound packets per second</key><real>1.277508</real>
    <key>pending downloads</key><real>0</real>
    <key>pending uploads</key><real>0.0001096525</real>
    <key>frame ms</key><real>0.7757886</real>
    <key>net ms</key><real>0.3152919</real>
    <key>sim other ms</key><real>0.1826937</real>
    <key>sim physics ms</key><real>0.04323055</real>
    <key>agent ms</key><real>0.01599029</real>
    <key>image ms</key><real>0.01865955</real>
    <key>script ms</key><real>0.1338836</real>
    <!-- Comment - some additional test values -->
    <key>hex number</key><binary encoding="base16">0fa1</binary>
    <key>base64 number</key><binary>SGVsbG8gd29ybGQ=</binary>
    <key>date</key><date>2006-02-01T14:29:53Z</date>
    <key>array</key>
        <array>
            <boolean>false</boolean>
            <integer>42</integer>
            <undef/>
            <uuid/>
            <boolean>1</boolean>
        </array>
  </map>
</map>
</llsd>
"#;

    fn trytestcase(teststr: &str) {
        //  Internal utility function.
        //  Parse canned XML test case into internal format.
        //  Must not contain NaN, because NaN != Nan and the equal test will fail
        let parsed1 = crate::de::xml::from_str(teststr).unwrap();
        println!("Parse of {}: \n{:#?}", teststr, parsed1);
        //  Generate Notation back from parsed version.
        let generated = crate::ser::notation::to_string(&parsed1).unwrap();
        println!("Generated Notation format:\n{}", generated);
        /*
        let generated = crate::ser::xml::to_string(&parsed1, true).unwrap();
        //  Parse that.
        let parsed2 = from_str(&generated).unwrap();
        //  Check that parses match.
        assert_eq!(parsed1, parsed2);
        */
    }
    trytestcase(TESTXML1);
    //  Test NAN case
    {
        let parsed1 = crate::de::xml::from_str(TESTXMLNAN).unwrap();
        println!("Parse of {}: \n{:#?}", TESTXMLNAN, parsed1);
        //  Generate XML back from parsed version.
        let generated = crate::ser::notation::to_string(&parsed1).unwrap();
        println!("Generated Notation format:\n{}", generated);
    }
}
