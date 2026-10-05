//! #  ser/xml.rs -- XML serializer for LLSD
//!
//!
//!  Library for serializing and de-serializing data in
//!  Linden Lab Structured Data format.
//!
//!  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//!
//!  XML format.
//
//  Animats
//  February, 2021.
//  License: LGPL.
//
//
//  Much like Serde-JSON, this will serialize and de-serialize only trees of LLSDValue items.

use crate::LLSDValue;
use anyhow::Error;
use base64;
use base64::Engine;
use chrono;
use chrono::TimeZone;
use std::io::Write;
//
//  Constants
//
pub const LLSDXMLPREFIX: &str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<llsd>\n";
pub const LLSDXMLSENTINEL: &str = "<?xml"; // Must begin with this.
const INDENT: usize = 4; // indent 4 spaces if asked

// By convention, the public API of a Serde serializer is one or more `to_abc`
// functions such as `to_string`, `to_bytes`, or `to_writer` depending on what
// Rust types the serializer is able to produce as output.
//

/// LLSDValue to Writer
pub fn to_writer<W: Write>(
    writer: &mut W,
    value: &LLSDValue,
    do_indent: bool,
) -> Result<(), Error> {
    write!(writer, "{}", LLSDXMLPREFIX)?; // Standard XML prefix
    generate_value(writer, value, if do_indent { INDENT } else { 0 }, 0);
    write!(writer, "</llsd>")?;
    writer.flush()?;
    Ok(())
}

/// LLSDValue to String.
/// Pretty prints out the value as XML. Indents by 4 spaces if requested.
pub fn to_string(val: &LLSDValue, do_indent: bool) -> Result<String, Error> {
    let mut s: Vec<u8> = Vec::new();
    to_writer(&mut s, val, do_indent)?;
    Ok(std::str::from_utf8(&s)?.to_string())
}

/// Generate one <TYPE> VALUE </TYPE> output. VALUE is recursive.
pub fn generate_value<W: Write>(writer: &mut W, val: &LLSDValue, spaces: usize, indent: usize) {
    //  Output a single tag
    fn tag<W: Write>(writer: &mut W, tag: &str, close: bool, indent: usize) {
        if indent > 0 {
            let _ = write!(writer, "{:1$}", " ", indent);
        };
        let _ = writeln!(writer, "<{}{}>", if close { "/" } else { "" }, tag);
    }

    //  Internal fn - write out one tag with a value.
    fn tag_value<W: Write>(writer: &mut W, tag: &str, text: &str, indent: usize) {
        if indent > 0 {
            let _ = write!(writer, "{:1$}", " ", indent);
        };
        if text.is_empty() {
            // if empty, write as null tag
            let _ = writeln!(writer, "<{} />", tag);
        } else {
            let _ = writeln!(writer, "<{}>{}</{}>", tag, xml_escape(text), tag);
        }
    }

    //  Use SL "nan", not Rust "NaN"
    fn f64_to_xml(v: f64) -> String {
        let ss = v.to_string();
        if ss == "NaN" {
            "nan".to_string()
        } else {
            ss
        }
    }
    //  Emit XML for all possible types.
    match val {
        LLSDValue::Undefined => tag_value(writer, "undef", "", indent),
        LLSDValue::Boolean(v) => {
            tag_value(writer, "boolean", if *v { "true" } else { "false" }, indent)
        }
        LLSDValue::String(v) => tag_value(writer, "string", v.as_str(), indent),
        LLSDValue::URI(v) => tag_value(writer, "uri", v.as_str(), indent),
        LLSDValue::Integer(v) => tag_value(writer, "integer", v.to_string().as_str(), indent),
        LLSDValue::Real(v) => tag_value(writer, "real", f64_to_xml(*v).as_str(), indent),
        LLSDValue::UUID(v) => tag_value(writer, "uuid", v.to_string().as_str(), indent),
        LLSDValue::Binary(v) => tag_value(
            writer,
            "binary",
            base64::engine::general_purpose::STANDARD.encode(v).as_str(),
            indent,
        ),
        LLSDValue::Date(v) => tag_value(
            writer,
            "date",
            &chrono::Utc
                .timestamp_opt(*v, 0)
                .earliest()
                .unwrap() // may panic for times prior to January 1, 1970.
                .to_rfc3339_opts(chrono::SecondsFormat::Secs, true),
            indent,
        ),
        LLSDValue::Map(v) => {
            tag(writer, "map", false, indent);
            for (key, value) in v {
                tag_value(writer, "key", key, indent + spaces);
                generate_value(writer, value, spaces, indent + spaces);
            }
            tag(writer, "map", true, indent);
        }
        LLSDValue::Array(v) => {
            tag(writer, "array", false, indent);
            for value in v {
                generate_value(writer, value, spaces, indent + spaces);
            }
            tag(writer, "array", true, indent);
        }
    };
}

/// XML standard character escapes.
pub fn xml_escape(unescaped: &str) -> String {
    let mut s = String::new();
    for ch in unescaped.chars() {
        match ch {
            '<' => s += "&lt;",
            '>' => s += "&gt;",
            '\'' => s += "&apos;",
            '&' => s += "&amp;",
            '"' => s += "&quot;",
            _ => s.push(ch),
        }
    }
    s
}
/*
// Unit tests

#[test]
fn xmlparsetest1() {
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
        //  Must not contain NaN, because NaN != Nan and the equal test will
        let parsed1 = parse(teststr).unwrap();
        println!("Parse of {}: \n{:#?}", teststr, parsed1);
        //  Generate XML back from parsed version.
        let generated = to_xml_string(&parsed1, true).unwrap();
        //  Parse that.
        let parsed2 = parse(&generated).unwrap();
        //  Check that parses match.
        assert_eq!(parsed1, parsed2);
    }
    trytestcase(TESTXML1);
    //  Test NAN case
    {
        let parsed1 = parse(TESTXMLNAN).unwrap();
        println!("Parse of {}: \n{:#?}", TESTXMLNAN, parsed1);
        //  Generate XML back from parsed version.
        let generated = to_xml_string(&parsed1, true).unwrap();
        //  Remove all white space for comparison
        let s1 = TESTXMLNAN.replace(" ", "").replace("\n", "");
        let s2 = generated.replace(" ", "").replace("\n", "");
        assert_eq!(s1, s2);
    }
}
*/
