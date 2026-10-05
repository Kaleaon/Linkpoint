//
//  de/xml.rs -- XML deserializer for LLSD
//
//  Library for serializing and de-serializing data in
//  Linden Lab Structured Data format.
//
//  Format documentation is at http://wiki.secondlife.com/wiki/LLSD
//
//  XML format.
//
//  Animats
//  February, 2021.
//  License: LGPL.
//
use crate::LLSDValue;
use anyhow::{anyhow, Error};
use ascii85;
use base64;
use base64::Engine;
use quick_xml::events::attributes::Attributes;
use quick_xml::events::Event;
use quick_xml::Reader;
use std::collections::HashMap;
use std::io::{BufRead, BufReader};
///use uuid;
//
//  Constants
//
pub const LLSDXMLPREFIX: &str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<llsd>\n";
pub const LLSDXMLSENTINEL: &str = "<?xml"; // Must begin with this.
///    Parse LLSD expressed in XML into an LLSD tree.
pub fn from_str(xmlstr: &str) -> Result<LLSDValue, Error> {
    let result = from_reader(&mut BufReader::new(xmlstr.as_bytes()));
    if let Err(e) = &result {
        return Err(anyhow::anyhow!("{}: {}", e, xmlstr));
    }
    result
}

/// Read XML from buffered source and parse into LLSDValue.
fn from_reader<R: BufRead>(rdr: &mut R) -> Result<LLSDValue, Error> {
    let mut reader = Reader::from_reader(rdr); // create an XML reader from a sequential reader
    reader.trim_text(true); // do not want trailing blanks
    reader.expand_empty_elements(true); // want end tag events always
    let mut buf = Vec::new(); // reader work area
    let mut output: Option<LLSDValue> = None;
    //  Outer parse. Find <llsd> and parse its interior.
    loop {
        match reader.read_event(&mut buf) {
            Ok(Event::Start(ref e)) => {
                match e.name() {
                    b"llsd" => {
                        if output.is_some() {
                            return Err(anyhow!("More than one <llsd> block in data"));
                        }
                        let mut buf2 = Vec::new();
                        match reader.read_event(&mut buf2) {
                            Ok(Event::Start(ref e)) => {
                                let tagname = std::str::from_utf8(e.name())?; // tag name as string to start parse
                                                                              //  This does all the real work.
                                output = Some(parse_value(&mut reader, tagname, &e.attributes())?);
                            }
                            _ => {
                                return Err(anyhow!(
                                    "Expected LLSD data, found {:?} error at position {}",
                                    e.name(),
                                    reader.buffer_position()
                                ))
                            }
                        };
                    }
                    _ => {
                        return Err(anyhow!(
                            "Expected <llsd>, found {:?} error at position {}",
                            e.name(),
                            reader.buffer_position()
                        ))
                    }
                }
            }
            Ok(Event::Text(_e)) => (), // Don't actually need random text
            Ok(Event::End(ref _e)) => (), // Tag matching check is automatic.
            Ok(Event::Eof) => break,   // exits the loop when reaching end of file
            Err(e) => {
                return Err(anyhow!(
                    "Error at position {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => (), // There are several other `Event`s we do not consider here
        }

        // if we don't keep a borrow elsewhere, we can clear the buffer to keep memory usage low
        buf.clear()
    }
    //  Final result, if stored
    match output {
        Some(out) => Ok(out),
        None => Err(anyhow!("Unexpected end of data, no <llsd> block.")),
    }
}

/// Parse one value - real, integer, map, etc. Recursive.
///fn parse_value<R: Read+BufRead>(rdr: &mut R) -> Result<LLSDValue, Error> {
fn parse_value<R: BufRead>(
    reader: &mut Reader<&mut R>,
    starttag: &str,
    attrs: &Attributes,
) -> Result<LLSDValue, Error> {
    //  Entered with a start tag alread parsed and in starttag
    match starttag {
        "undef" | "real" | "integer" | "boolean" | "string" | "uri" | "binary" | "uuid"
        | "date" => parse_primitive_value(reader, starttag, attrs),
        "map" => parse_map(reader),
        "array" => parse_array(reader, parse_value),
        _ => Err(anyhow!(
            "Unknown data type <{}> at position {}",
            starttag,
            reader.buffer_position()
        )),
    }
}

/// Parse one value - real, integer, map, etc. Recursive.
fn parse_primitive_value<R: BufRead>(
    reader: &mut Reader<&mut R>,
    starttag: &str,
    attrs: &Attributes,
) -> Result<LLSDValue, Error> {
    //  Entered with a start tag already parsed and in starttag
    let mut texts = Vec::new(); // accumulate text here
    let mut buf = Vec::new();
    loop {
        let event = reader.read_event(&mut buf);
        match event {
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if starttag != tagname {
                    return Err(anyhow!(
                        "Unmatched XML tags: <{}> .. <{}>",
                        starttag,
                        tagname
                    ));
                };
                //  End of an XML tag. Value is in text.
                let text = texts.join(" ").trim().to_string(); // combine into one big string
                texts.clear();
                //  Parse the primitive types.
                return match starttag {
                    "undef" => Ok(LLSDValue::Undefined),
                    "real" => Ok(LLSDValue::Real(
                        if text.to_lowercase() == "nan" {
                            "NaN".to_string()
                        } else {
                            text
                        }
                        .parse::<f64>()?,
                    )),
                    "integer" => Ok(LLSDValue::Integer(parse_integer(&text)?)),
                    "boolean" => Ok(LLSDValue::Boolean(parse_boolean(&text)?)),
                    "string" => Ok(LLSDValue::String(text)),
                    "uri" => Ok(LLSDValue::URI(text)),
                    "uuid" => Ok(LLSDValue::UUID(if text.is_empty() {
                        uuid::Uuid::nil()
                    } else {
                        uuid::Uuid::parse_str(&text)?
                    })),
                    "date" => Ok(LLSDValue::Date(parse_date(&text)?)),
                    "binary" => Ok(LLSDValue::Binary(parse_binary(&text, attrs)?)),
                    _ => Err(anyhow!(
                        "Unexpected primitive data type <{}> at position {}",
                        starttag,
                        reader.buffer_position()
                    )),
                };
                // unreachable
            }
            Ok(Event::Eof) => {
                return Err(anyhow!(
                    "Unexpected end of data in primitive value at position {}",
                    reader.buffer_position()
                ))
            }
            Ok(Event::Comment(_)) => {} // ignore comment
            Err(e) => {
                return Err(anyhow!(
                    "Parse Error at position {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => {
                return Err(anyhow!(
                    "Unexpected parse event {:?} at position {} while parsing: {:?}",
                    event,
                    reader.buffer_position(),
                    starttag
                ))
            }
        }
    }
}

//  Parse one map.
pub fn parse_map<R: BufRead>(reader: &mut Reader<&mut R>) -> Result<LLSDValue, Error> {
    //  Entered with a "map" start tag just parsed.
    let mut map: HashMap<String, LLSDValue> = HashMap::new(); // accumulating map
    let mut texts = Vec::new(); // accumulate text here
    let mut buf = Vec::new();
    loop {
        let event = reader.read_event(&mut buf);
        match event {
            Ok(Event::Start(ref e)) => {
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                match tagname {
                    "key" => {
                        let (k, v) = parse_map_entry(reader)?; // read one key/value pair
                        let _dup = map.insert(k, v); // insert into map
                                                     //  Duplicates are not errors, per LLSD spec.
                    }
                    _ => {
                        return Err(anyhow!("Expected 'key' in map, found '{}'", tagname));
                    }
                }
            }
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                //  End of an XML tag. No text expected.
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if "map" != tagname {
                    return Err(anyhow!("Unmatched XML tags: <{}> .. <{}>", "map", tagname));
                };
                return Ok(LLSDValue::Map(map)); // done, valid result
            }
            Ok(Event::Eof) => {
                return Err(anyhow!(
                    "Unexpected end of data in map at position {}",
                    reader.buffer_position()
                ))
            }
            Ok(Event::Comment(_)) => {} // ignore comment
            Err(e) => {
                return Err(anyhow!(
                    "Parse Error at position {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => {
                return Err(anyhow!(
                    "Unexpected parse event {:?} at position {} while parsing map",
                    event,
                    reader.buffer_position(),
                ))
            }
        }
    }
}

//  Parse one map entry.
//  Format <key> STRING </key> LLSDVALUE
pub fn parse_map_entry<R: BufRead>(
    reader: &mut Reader<&mut R>,
) -> Result<(String, LLSDValue), Error> {
    //  Entered with a "key" start tag just parsed.  Expecting text.
    let mut texts = Vec::new(); // accumulate text here
    let mut buf = Vec::new();
    loop {
        let event = reader.read_event(&mut buf);
        match event {
            Ok(Event::Start(ref e)) => {
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                return Err(anyhow!("Expected 'key' in map, found '{}'", tagname));
            }
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                //  End of an XML tag. Should be </key>
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if "key" != tagname {
                    return Err(anyhow!("Unmatched XML tags: <{}> .. <{}>", "key", tagname));
                };
                let mut buf = Vec::new();
                let k = texts.join(" ").trim().to_string(); // the key
                texts.clear();
                match reader.read_event(&mut buf) {
                    Ok(Event::Start(ref e)) => {
                        let tagname = std::str::from_utf8(e.name())?; // tag name as string
                        let v = parse_value(reader, tagname, &e.attributes())?; // parse next value
                        return Ok((k, v)); // return key value pair
                    }
                    _ => {
                        return Err(anyhow!(
                            "Unexpected parse error at position {} while parsing map entry",
                            reader.buffer_position()
                        ))
                    }
                };
            }
            Ok(Event::Eof) => {
                return Err(anyhow!(
                    "Unexpected end of data at position {}",
                    reader.buffer_position()
                ))
            }
            Ok(Event::Comment(_)) => {} // ignore comment
            Err(e) => {
                return Err(anyhow!(
                    "Parse Error at position {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => {
                return Err(anyhow!(
                    "Unexpected parse event {:?} at position {} while parsing map entry",
                    event,
                    reader.buffer_position(),
                ))
            }
        }
    }
}

/// Parse one LLSD object. Recursive.
pub fn parse_array<R, F>(reader: &mut Reader<&mut R>, parse_value: F) -> Result<LLSDValue, Error>
where
    R: BufRead,
    F: Fn(&mut Reader<&mut R>, &str, &Attributes) -> Result<LLSDValue, Error>,
{
    //  Entered with an <array> tag just parsed.
    let mut texts = Vec::new(); // accumulate text here
    let mut buf = Vec::new();
    let mut items: Vec<LLSDValue> = Vec::new(); // accumulate items.
    loop {
        let event = reader.read_event(&mut buf);
        match event {
            Ok(Event::Start(ref e)) => {
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                                                              //  Parse one data item.
                items.push(parse_value(reader, tagname, &e.attributes())?);
            }
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                //  End of an XML tag. Should be </array>
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if "array" != tagname {
                    return Err(anyhow!(
                        "Unmatched XML tags: <{}> .. <{}>",
                        "array",
                        tagname
                    ));
                };
                break; // end of array
            }
            Ok(Event::Eof) => {
                return Err(anyhow!(
                    "Unexpected end of data at position {}",
                    reader.buffer_position()
                ))
            }
            Ok(Event::Comment(_)) => {} // ignore comment
            Err(e) => {
                return Err(anyhow!(
                    "Parse Error at position {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => {
                return Err(anyhow!(
                    "Unexpected parse event {:?} at position {} while parsing array",
                    event,
                    reader.buffer_position(),
                ))
            }
        }
    }
    Ok(LLSDValue::Array(items)) // result is array of items
}

/// Parse binary object.
/// Input in base64, base16, or base85.
pub fn parse_binary(s: &str, attrs: &Attributes) -> Result<Vec<u8>, Error> {
    // "Parsers must support base64 encoding. Parsers may support base16 and base85."
    let encoding = match get_attr(attrs, b"encoding")? {
        Some(enc) => enc,
        None => "base64".to_string(), // default
    };
    //  Decode appropriately.
    Ok(match encoding.as_str() {
        "base64" => base64::engine::general_purpose::STANDARD.decode(s)?,
        "base16" => hex::decode(s)?,
        "base85" => match ascii85::decode(s) {
            Ok(v) => v,
            Err(e) => return Err(anyhow!("Base 85 decode error: {:?}", e)),
        },
        _ => {
            return Err(anyhow!(
                "Unknown encoding: <binary encoding=\"{}\">",
                encoding
            ))
        }
    })
}

/// Parse ISO 9660 date, simple form.
pub fn parse_date(s: &str) -> Result<i64, Error> {
    Ok(chrono::DateTime::parse_from_rfc3339(s)?.timestamp())
}

/// Parse integer. LSL allows the empty string as 0.
pub fn parse_integer(s: &str) -> Result<i32, Error> {
    let s = s.trim();
    if s.is_empty() {
        Ok(0) // empty string
    } else {
        Ok(s.parse::<i32>()?) // nonempty string
    }
}

///  Parse boolean. LSL allows 0. 0.0, false, 1. 1.0, true.
pub fn parse_boolean(s: &str) -> Result<bool, Error> {
    Ok(match s {
        "0" | "0.0" => false,
        "1" | "1.0" => true,
        _ => s.parse::<bool>()?,
    })
}

/// Search for attribute in attribute list
fn get_attr(attrs: &Attributes, key: &[u8]) -> Result<Option<String>, Error> {
    //  Each step has a possible error, so it's hard to do this more cleanly.
    for attr in attrs.clone() {
        let a = attr?;
        if a.key != key {
            continue;
        } // not this one
        let v = a.unescaped_value()?;
        let sv = std::str::from_utf8(&v)?;
        return Ok(Some(sv.to_string()));
    }
    Ok(None)
}

// Unit tests

#[test]
fn xmlparsetest1() {
    const TESTXMLZERO: &str = r#"
<?xml version="1.0" encoding="UTF-8"?>
<llsd>
<array>
<integer>0</integer>
<integer>100</integer>
<integer />
</array>
</llsd>
"#;

    const TESTXMLZEROARRAY: [i32; 3] = [0, 100, 0]; // expected values

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
        let parsed1 = from_str(teststr).unwrap();
        println!("Parse of {}: \n{:#?}", teststr, parsed1);
        //  Generate XML back from parsed version.
        let generated = crate::ser::xml::to_string(&parsed1, true).unwrap();
        //  Parse that.
        let parsed2 = from_str(&generated).unwrap();
        //  Check that parses match.
        assert_eq!(parsed1, parsed2);
    }
    trytestcase(TESTXML1);
    //  Special test cases.
    //  Test zero case, where an empty <integer /> is 0, per spec.
    {
        let parsed0 = from_str(TESTXMLZERO).unwrap();
        println!("Parse of {}: \n{:#?}", TESTXMLZERO, parsed0);
        let arr = parsed0.as_array().unwrap(); // yields array of LLSD values
        assert_eq!(arr.len(), TESTXMLZEROARRAY.len()); // lengths must match
        for (item, n) in arr.iter().zip(TESTXMLZEROARRAY) {
            assert_eq!(n, *(item.as_integer().unwrap())); // must match
        }
    }
    //  Test NAN case
    {
        let parsed1 = from_str(TESTXMLNAN).unwrap();
        println!("Parse of {}: \n{:#?}", TESTXMLNAN, parsed1);
        //  Generate XML back from parsed version.
        let generated = crate::ser::xml::to_string(&parsed1, true).unwrap();
        //  Remove all white space for comparison
        let s1 = TESTXMLNAN.replace(" ", "").replace("\n", "");
        let s2 = generated.replace(" ", "").replace("\n", "");
        assert_eq!(s1, s2);
    }
}
