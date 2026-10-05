use crate::{ser::xml::xml_escape, LLSDValue};
use anyhow::Error;
use base64::Engine;
use std::io::Write;

pub fn to_string(val: &LLSDValue, do_indent: bool, method_name: &str) -> Result<String, Error> {
    let mut s: Vec<u8> = Vec::new();
    to_writer_xmlrpc(&mut s, val, do_indent, method_name)?;
    Ok(std::str::from_utf8(&s)?.to_string())
}

pub fn to_writer_xmlrpc<W: Write>(
    writer: &mut W,
    value: &LLSDValue,
    do_indent: bool,
    method_name: &str,
) -> Result<(), Error> {
    write!(writer, "<?xml version=\"1.0\"?>")?;

    if do_indent {
        writeln!(writer, "\n<methodCall>")?;
        writeln!(
            writer,
            "    <methodName>{}</methodName>",
            xml_escape(method_name)
        )?;
        writeln!(writer, "    <params>")?;
        writeln!(writer, "        <param>")?;
        writeln!(writer, "            <value>")?;
    } else {
        write!(
            writer,
            "<methodCall><methodName>{}</methodName><params><param><value>",
            xml_escape(method_name)
        )?;
    }

    // write actual value (e.g., struct/map)
    generate_value_xmlrpc(writer, value, if do_indent { 4 } else { 0 }, 12, do_indent);

    if do_indent {
        writeln!(writer, "            </value>")?;
        writeln!(writer, "        </param>")?;
        writeln!(writer, "    </params>")?;
        writeln!(writer, "</methodCall>")?;
    } else {
        write!(writer, "</value></param></params></methodCall>")?;
    }

    writer.flush()?;
    Ok(())
}

fn generate_value_xmlrpc<W: Write>(
    writer: &mut W,
    val: &LLSDValue,
    spaces: usize,
    indent: usize,
    do_indent: bool,
) {
    fn tag<W: Write>(writer: &mut W, tag: &str, close: bool, indent: usize, do_indent: bool) {
        if do_indent && indent > 0 {
            let _ = write!(writer, "{:1$}", " ", indent);
        }
        if do_indent {
            let _ = writeln!(writer, "<{}{}>", if close { "/" } else { "" }, tag);
        } else {
            let _ = write!(writer, "<{}{}>", if close { "/" } else { "" }, tag);
        }
    }

    fn tag_value<W: Write>(writer: &mut W, text: &str, indent: usize, do_indent: bool) {
        if do_indent && indent > 0 {
            let _ = write!(writer, "{:1$}", " ", indent);
        }
        if text.is_empty() {
            if do_indent {
                let _ = writeln!(writer, "<string />");
            } else {
                let _ = write!(writer, "<string />");
            }
        } else if do_indent {
            let _ = writeln!(writer, "<string>{}</string>", xml_escape(text));
        } else {
            let _ = write!(writer, "<string>{}</string>", xml_escape(text));
        }
    }

    match val {
        LLSDValue::Map(v) => {
            tag(writer, "struct", false, indent, do_indent);
            for (key, value) in v {
                tag(writer, "member", false, indent + spaces, do_indent);
                if do_indent {
                    let _ = writeln!(
                        writer,
                        "{:indent$}<name>{}</name>",
                        "",
                        xml_escape(key),
                        indent = indent + spaces * 2
                    );
                } else {
                    let _ = write!(writer, "<name>{}</name>", xml_escape(key));
                }
                tag(writer, "value", false, indent + spaces * 2, do_indent);
                generate_value_xmlrpc(writer, value, spaces, indent + spaces * 3, do_indent);
                tag(writer, "value", true, indent + spaces * 2, do_indent);
                tag(writer, "member", true, indent + spaces, do_indent);
            }
            tag(writer, "struct", true, indent, do_indent);
        }

        LLSDValue::Array(v) => {
            tag(writer, "array", false, indent, do_indent);
            tag(writer, "data", false, indent + spaces, do_indent);
            for value in v {
                tag(writer, "value", false, indent + spaces * 2, do_indent);
                generate_value_xmlrpc(writer, value, spaces, indent + spaces * 3, do_indent);
                tag(writer, "value", true, indent + spaces * 2, do_indent);
            }
            tag(writer, "data", true, indent + spaces, do_indent);
            tag(writer, "array", true, indent, do_indent);
        }
        _ => {
            // all leaf types become <string>
            let s = match val {
                LLSDValue::Undefined => "",
                LLSDValue::Boolean(v) => {
                    if *v {
                        "true"
                    } else {
                        "false"
                    }
                }
                LLSDValue::String(v) => v.as_str(),
                LLSDValue::Integer(v) => &v.to_string(),
                LLSDValue::Real(v) => &v.to_string(),
                LLSDValue::UUID(v) => &v.to_string(),
                LLSDValue::Binary(v) => &base64::engine::general_purpose::STANDARD.encode(v),
                LLSDValue::Date(v) => &v.to_string(),
                _ => "",
            };
            tag_value(writer, s, indent, do_indent);
        }
    }
}
