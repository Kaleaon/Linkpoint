use crate::de::xml;
//
//  de/xml.rs -- XML-rpc deserializer for OpenSimulator's login
//
//  Library for serializing and de-serializing data in
//  Linden Lab Structured Data format.
//
//  XML-rpc format.
//
//  Benthicllsd
//  October, 2025.
//  License: LGPL.
//
use crate::LLSDValue;
use anyhow::{anyhow, Error};
use quick_xml::events::attributes::Attributes;
use quick_xml::events::Event;
use quick_xml::Reader;
use std::collections::HashMap;
use std::io::{BufRead, BufReader};
///use uuid;
//
//  Constants
//
//
pub const XMLRPCPREFIX: &str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse>";
pub const XMLRPCPREFIX2: &str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<methodResponse>";
pub const XMLRPCPREFIX3: &str =
    "<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?><methodResponse>";
///    Parse LLSD expressed in XML into an LLSD tree.
pub fn from_str(xmlstr: &str) -> Result<LLSDValue, Error> {
    // Unwrap the Result, returning early if it was an error
    let parsed = from_reader(&mut BufReader::new(xmlstr.as_bytes()))?;
    let flattened = flatten_login_response(parsed);
    Ok(flattened)
}
fn flatten_login_response(value: LLSDValue) -> LLSDValue {
    match value {
        LLSDValue::Array(arr) => {
            let mut merged = HashMap::new();
            for v in arr {
                if let LLSDValue::Map(m) = v {
                    for (k, val) in m {
                        merged.insert(k, val);
                    }
                }
            }
            LLSDValue::Map(merged)
        }
        _ => value,
    }
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
                    b"methodResponse" => {
                        if output.is_some() {
                            return Err(anyhow!("More than one <methodResponse> block in data"));
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
                                    "Expected XMLRPC data, found {:?} error at position {}",
                                    e.name(),
                                    reader.buffer_position()
                                ))
                            }
                        };
                    }
                    _ => {
                        return Err(anyhow!(
                            "Expected <methodResponse>, found {:?} error at position {}",
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
        "undef" | "real" | "integer" | "boolean" | "string" | "uri" | "binary" | "uuid" | "i4"
        | "date" => parse_primitive_value(reader, starttag, attrs),
        "map" => xml::parse_map(reader),
        "array" => xml::parse_array(reader, parse_value),
        "params" | "param" | "value" | "struct" | "data" => parse_container(reader, starttag),
        "member" => parse_member(reader),
        _ => Err(anyhow!(
            "Unknown data asdf type <{}> at position {}",
            starttag,
            reader.buffer_position()
        )),
    }
}

fn parse_container<R: BufRead>(
    reader: &mut Reader<&mut R>,
    end_tag: &str,
) -> Result<LLSDValue, Error> {
    let mut items = Vec::new();
    let mut buf = Vec::new();

    loop {
        match reader.read_event(&mut buf) {
            Ok(Event::Start(e)) => {
                let tagname = std::str::from_utf8(e.name())?;
                let val = parse_value(reader, tagname, &e.attributes())?;

                if !matches!(val, LLSDValue::Array(ref arr) if arr.is_empty()) {
                    items.push(val);
                }
            }
            Ok(Event::End(e)) if e.name() == end_tag.as_bytes() => break,
            Ok(Event::Text(_)) | Ok(Event::Comment(_)) => {}
            Ok(Event::Eof) => return Err(anyhow!("Unexpected EOF while parsing <{}>", end_tag)),
            Err(e) => {
                return Err(anyhow!(
                    "Parse error at {}: {:?}",
                    reader.buffer_position(),
                    e
                ))
            }
            _ => {}
        }
        buf.clear();
    }

    if items.is_empty() {
        Ok(LLSDValue::Array(vec![]))
    } else if items.len() == 1 {
        match &items[0] {
            LLSDValue::Map(_) => Ok(items[0].clone()), // unwrap single map
            other => Ok(other.clone()),
        }
    } else {
        // multiple items
        let mut merged_items = Vec::new();

        for item in items {
            match item {
                LLSDValue::Array(arr) => {
                    // if array contains only maps, merge into a single map
                    if arr.iter().all(|v| matches!(v, LLSDValue::Map(_))) {
                        let mut merged_map = HashMap::new();
                        for v in arr {
                            if let LLSDValue::Map(m) = v {
                                for (k, val) in m {
                                    merged_map.insert(k, val);
                                }
                            }
                        }
                        merged_items.push(LLSDValue::Map(merged_map));
                    } else {
                        merged_items.push(LLSDValue::Array(arr));
                    }
                }
                LLSDValue::Map(_) => merged_items.push(item),
                other => merged_items.push(other),
            }
        }

        Ok(LLSDValue::Array(merged_items))
    }
}
//  Parse one map.
fn parse_member<R: BufRead>(reader: &mut Reader<&mut R>) -> Result<LLSDValue, Error> {
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
                    "name" => {
                        let (k, v) = parse_member_entry(reader)?; // read one key/value pair
                        let _dup = map.insert(k, v); // insert into map
                                                     //  Duplicates are not errors, per LLSD spec.
                    }
                    _ => {
                        return Err(anyhow!("Expected 'name' in map, found '{}'", tagname));
                    }
                }
            }
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                //  End of an XML tag. No text expected.
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if "member" != tagname {
                    return Err(anyhow!(
                        "Unmatched XML tags: <{}> .. <{}>",
                        "member",
                        tagname
                    ));
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
fn parse_member_entry<R: BufRead>(
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
                return Err(anyhow!("Expected 'name' in map, found '{}'", tagname));
            }
            Ok(Event::Text(e)) => texts.push(e.unescape_and_decode(reader)?),
            Ok(Event::End(ref e)) => {
                //  End of an XML tag. Should be </key>
                let tagname = std::str::from_utf8(e.name())?; // tag name as string
                if "name" != tagname {
                    return Err(anyhow!("Unmatched XML tags: <{}> .. <{}>", "name", tagname));
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
                    "integer" => Ok(LLSDValue::Integer(xml::parse_integer(&text)?)),
                    "boolean" => Ok(LLSDValue::Boolean(xml::parse_boolean(&text)?)),
                    "string" => match text.to_lowercase().as_str() {
                        "true" => Ok(LLSDValue::Boolean(true)),
                        "false" => Ok(LLSDValue::Boolean(false)),
                        _ => {
                            if let Ok(uuid) = uuid::Uuid::parse_str(&text) {
                                Ok(LLSDValue::UUID(uuid))
                            } else {
                                Ok(LLSDValue::String(text))
                            }
                        }
                    },
                    "uri" => Ok(LLSDValue::String(text)),
                    "uuid" => Ok(LLSDValue::UUID(if text.is_empty() {
                        uuid::Uuid::nil()
                    } else {
                        uuid::Uuid::parse_str(&text)?
                    })),
                    "i4" => Ok(LLSDValue::Integer(xml::parse_integer(&text)?)),
                    "date" => Ok(LLSDValue::Date(xml::parse_date(&text)?)),
                    "binary" => Ok(LLSDValue::Binary(xml::parse_binary(&text, attrs)?)),
                    _ => Err(anyhow!(
                        "Unexpected primitive data type <{}> at position {}",
                        starttag,
                        reader.buffer_position()
                    )),
                };
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
