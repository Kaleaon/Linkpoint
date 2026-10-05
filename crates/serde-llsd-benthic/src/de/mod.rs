//! #De-serialization. Converts an LLSD stream to tree of LLSDValue structs.
pub mod binary;
pub mod newline;
pub mod notation;
pub mod xml;
pub mod xml_rpc;

use anyhow::{anyhow, Error};

use crate::de::newline::parse_llwearable_to_llsd;

/// Parse LLSD, detecting format.
/// Recognizes Notation, and XML LLSD with sentinels.
/// Will accept leading whitespace.
pub fn auto_from_str(msg_string: &str) -> Result<crate::LLSDValue, Error> {
    let msg_string = msg_string.trim_start(); // remove leading whitespace
                                              //  Try Notation sentinel. Tolerate missing newline at end of sentinel.
    if let Some(stripped) = msg_string.strip_prefix(notation::LLSDNOTATIONSENTINEL.trim_end()) {
        return notation::from_str(stripped);
    }
    //  Try XML sentinel.
    if msg_string.starts_with(xml::LLSDXMLSENTINEL) {
        // try XML if it has an LLSDXML prefix
        if msg_string.starts_with(xml::LLSDXMLPREFIX) {
            return xml::from_str(msg_string);
        }

        // try XMLRPC if it has a XMLRPC prefix
        if msg_string.starts_with(xml_rpc::XMLRPCPREFIX)
            || msg_string.starts_with(xml_rpc::XMLRPCPREFIX2)
            || msg_string.starts_with(xml_rpc::XMLRPCPREFIX3)
        {
            return xml_rpc::from_str(msg_string);
        }
    }

    if msg_string.starts_with(newline::LLSDNEWLINEPREFIX) {
        return parse_llwearable_to_llsd(msg_string);
    }

    //  Trim string to N chars for error msg.
    let snippet = msg_string
        .chars()
        .zip(0..60)
        .map(|(c, _)| c)
        .collect::<String>();
    Err(anyhow!("XML format not recognized: {:?}", snippet))
}

/// Parse LLSD, detecting format.
/// Recognizes binary, Notation, and XML LLSD, with or without sentinel.
/// Will accept leading whitespace for text forms, but not binary. That's strict.
pub fn auto_from_bytes(msg: &[u8]) -> Result<crate::LLSDValue, Error> {
    //  Try sentinels first.
    //  Binary sentinel
    if msg.len() >= binary::LLSDBINARYSENTINEL.len()
        && &msg[0..binary::LLSDBINARYSENTINEL.len()] == binary::LLSDBINARYSENTINEL
    {
        return binary::from_bytes(&msg[binary::LLSDBINARYSENTINEL.len()..]);
    }
    //  For text forms, tolerate leading whitespace.
    {
        let msg = trim_ascii_start(msg); // remove leading whitespace if any
                                         //  Try Notation sentinel. Tolerate trailing newline.
        let sentinel = notation::LLSDNOTATIONSENTINEL.trim_end().as_bytes(); // sentinel without the trailing newline
        if msg.len() >= sentinel.len() && &msg[0..sentinel.len()] == sentinel {
            return notation::from_bytes(&msg[sentinel.len()..]);
        }
        //  Try XML sentinel.
        let msgstring = std::str::from_utf8(msg)?; // convert to UTF-8 string
        if msgstring.trim_start().starts_with(xml::LLSDXMLSENTINEL) {
            // try XML
            return xml::from_str(msgstring);
        }
    }
    //  Check for binary without header. If array or map marker, parse.
    if msg.len() > 1 {
        match msg[0] {
            // check first char
            b'{' | b'[' => return binary::from_bytes(msg),
            _ => {}
        }
    }

    //  Trim string to N chars for error msg.
    let snippet = String::from_utf8_lossy(msg)
        .chars()
        .zip(0..60)
        .map(|(c, _)| c)
        .collect::<String>();
    Err(anyhow!("LLSD format not recognized: {:?}", snippet))
}

/// Trim ASCII whitespace from string.
/// From an unstable Rust feature soon to become standard.
fn trim_ascii_start(b: &[u8]) -> &[u8] {
    let mut bytes = b;
    while let [first, rest @ ..] = bytes {
        if first.is_ascii_whitespace() {
            bytes = rest;
        } else {
            break;
        }
    }
    bytes
}

#[test]
fn testpbrmaterialdecode() {
    use base64::Engine;
    // A sample PBR material item, in base64.
    const TESTPBRMATLLLSD: &str =
        "PD8gTExTRC9CaW5hcnkgPz4KewAAAANrAAAABGRhdGFzAAABc3siYXNzZXQiOnsidmVyc2lvbiI6
        IjIuMCJ9LCJpbWFnZXMiOlt7InVyaSI6ImQxZjkxYmI3LWY3ZDYtZDI2Zi1lMGQ3LTU2OGYwZmY3
        NDI3OSJ9LHsidXJpIjoiZDFmOTFiYjctZjdkNi1kMjZmLWUwZDctNTY4ZjBmZjc0Mjc5In0seyJ1
        cmkiOiI4YTQ1Yzk5YS1jZjg0LTc3YzUtOWQ5ZC01Yzk4NzUyMTNmZTkifV0sIm1hdGVyaWFscyI6
        W3sibm9ybWFsVGV4dHVyZSI6eyJpbmRleCI6Mn0sInBick1ldGFsbGljUm91Z2huZXNzIjp7ImJh
        c2VDb2xvclRleHR1cmUiOnsiaW5kZXgiOjB9LCJtZXRhbGxpY1JvdWdobmVzc1RleHR1cmUiOnsi
        aW5kZXgiOjF9fX1dLCJ0ZXh0dXJlcyI6W3sic291cmNlIjowfSx7InNvdXJjZSI6MX0seyJzb3Vy
        Y2UiOjJ9XX0KawAAAAR0eXBlcwAAAAhHTFRGIDIuMGsAAAAHdmVyc2lvbnMAAAADMS4wfQA=";

    let mut clean_base64 = TESTPBRMATLLLSD.to_string();
    clean_base64.retain(|c| !char::is_whitespace(c)); // without whitespace
    println!("Base 64: {}", clean_base64);
    let bytes = base64::engine::general_purpose::STANDARD
        .decode(clean_base64)
        .expect("PBR example failed base64 decode"); // as bytes
    let llsd = auto_from_bytes(&bytes).expect("LLSD decode failed");
    println!("PBR asset: {:?}", llsd);
    //  Display as XML
    println!(
        "As XML: \n{}",
        crate::ser::xml::to_string(&llsd, true).expect("Conversion to XML failed")
    );
}

#[test]
fn testnotationdetect1() {
    //  Test recognzier with trailing newline, no excess whitespace. This is the canonical form.
    const TESTNOTATION1A: &str = r#"<? llsd/notation ?>
[{'destination':"http://example.com"}]"#;
    //  No trailing newline, plus leading whitespace.
    //  This is not compliant with the spec but is tolerated for compatibiilty.
    const TESTNOTATION1B: &str = r#"
    <? llsd/notation ?>[
  {'destination':"http://example.com"}, 
]
"#;
    let parsed_sa = auto_from_str(TESTNOTATION1A).unwrap();
    let parsed_sb = auto_from_str(TESTNOTATION1B).unwrap();
    assert_eq!(parsed_sa, parsed_sb); // must match, with and without trailing whitespace.
    let s = crate::notation_to_string(&parsed_sa).unwrap();
    assert_eq!(TESTNOTATION1A, s); // check round-trip
    let parsed_ba = auto_from_bytes(TESTNOTATION1A.as_bytes()).unwrap();
    let parsed_bb = auto_from_bytes(TESTNOTATION1B.as_bytes()).unwrap();
    assert_eq!(parsed_ba, parsed_bb); // must match, with and without trailing whitespace.
                                      ////let b = crate::notation_to_bytes(&parsed_ba).unwrap();
                                      ////assert_eq!(TESTNOTATION1A.as_bytes(), b);         // must match correct form
}
