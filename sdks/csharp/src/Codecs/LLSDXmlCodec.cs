using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;
using System.Xml;

namespace Linkpoint.LLSD.Codecs
{
    public static class LLSDXmlCodec
    {
        public static string Encode(LLSDValue value, bool withDeclaration = false, bool wrappedInRoot = true)
        {
            var settings = new XmlWriterSettings
            {
                OmitXmlDeclaration = !withDeclaration,
                Indent = false,
                Encoding = Encoding.UTF8
            };

            using var sw = new StringWriter();
            using (var writer = XmlWriter.Create(sw, settings))
            {
                if (wrappedInRoot)
                {
                    writer.WriteStartElement("llsd");
                    WriteNode(writer, value);
                    writer.WriteEndElement();
                }
                else
                {
                    WriteNode(writer, value);
                }
            }

            return sw.ToString();
        }

        private static void WriteNode(XmlWriter writer, LLSDValue value)
        {
            switch (value.Type)
            {
                case LLSDType.Undefined:
                    writer.WriteStartElement("undef");
                    writer.WriteEndElement();
                    break;

                case LLSDType.Boolean:
                    writer.WriteStartElement("boolean");
                    writer.WriteString(value.AsBoolean() ? "true" : "false");
                    writer.WriteEndElement();
                    break;

                case LLSDType.Integer:
                    writer.WriteStartElement("integer");
                    writer.WriteString(value.AsInteger().ToString(CultureInfo.InvariantCulture));
                    writer.WriteEndElement();
                    break;

                case LLSDType.Real:
                    writer.WriteStartElement("real");
                    double r = value.AsReal();
                    if (double.IsNaN(r)) writer.WriteString("nan");
                    else if (double.IsPositiveInfinity(r)) writer.WriteString("inf");
                    else if (double.IsNegativeInfinity(r)) writer.WriteString("-inf");
                    else writer.WriteString(r.ToString(CultureInfo.InvariantCulture));
                    writer.WriteEndElement();
                    break;

                case LLSDType.String:
                    writer.WriteStartElement("string");
                    string s = value.AsString();
                    if (!string.IsNullOrEmpty(s)) writer.WriteString(s);
                    writer.WriteEndElement();
                    break;

                case LLSDType.UUID:
                    writer.WriteStartElement("uuid");
                    LLUUID u = value.AsUUID();
                    if (u.NotNull) writer.WriteString(u.ToString());
                    writer.WriteEndElement();
                    break;

                case LLSDType.Date:
                    writer.WriteStartElement("date");
                    writer.WriteString(value.AsDate().ToISOString());
                    writer.WriteEndElement();
                    break;

                case LLSDType.URI:
                    writer.WriteStartElement("uri");
                    writer.WriteString(value.AsURI().AsString());
                    writer.WriteEndElement();
                    break;

                case LLSDType.Binary:
                    writer.WriteStartElement("binary");
                    writer.WriteAttributeString("encoding", "base64");
                    byte[] bin = value.AsBinary();
                    if (bin.Length > 0) writer.WriteString(Convert.ToBase64String(bin));
                    writer.WriteEndElement();
                    break;

                case LLSDType.Map:
                    writer.WriteStartElement("map");
                    foreach (var kvp in value.AsMap())
                    {
                        writer.WriteStartElement("key");
                        writer.WriteString(kvp.Key);
                        writer.WriteEndElement();
                        WriteNode(writer, kvp.Value);
                    }
                    writer.WriteEndElement();
                    break;

                case LLSDType.Array:
                    writer.WriteStartElement("array");
                    foreach (var item in value.AsArray())
                    {
                        WriteNode(writer, item);
                    }
                    writer.WriteEndElement();
                    break;

                default:
                    writer.WriteStartElement("undef");
                    writer.WriteEndElement();
                    break;
            }
        }

        public static LLSDValue Decode(string xml)
        {
            if (string.IsNullOrWhiteSpace(xml)) return LLSDValue.Undefined;

            var settings = new XmlReaderSettings
            {
                DtdProcessing = DtdProcessing.Prohibit,
                XmlResolver = null,
                IgnoreWhitespace = true,
                IgnoreComments = true
            };

            using var sr = new StringReader(xml);
            using var reader = XmlReader.Create(sr, settings);

            while (reader.Read())
            {
                if (reader.NodeType == XmlNodeType.Element)
                {
                    if (reader.Name == "llsd")
                    {
                        // Advance to first inner element
                        while (reader.Read())
                        {
                            if (reader.NodeType == XmlNodeType.Element)
                            {
                                return ReadElement(reader);
                            }
                        }
                    }
                    else
                    {
                        return ReadElement(reader);
                    }
                }
            }

            return LLSDValue.Undefined;
        }

        private static LLSDValue ReadElement(XmlReader reader)
        {
            string name = reader.Name.ToLowerInvariant();
            bool isEmpty = reader.IsEmptyElement;

            switch (name)
            {
                case "undef":
                    reader.Skip();
                    return LLSDValue.Undefined;

                case "boolean":
                    string boolText = isEmpty ? "" : reader.ReadElementContentAsString().Trim().ToLowerInvariant();
                    if (isEmpty) reader.Skip();
                    bool bVal = boolText == "true" || boolText == "1" || boolText == "t";
                    return LLSDValue.FromBoolean(bVal);

                case "integer":
                    string intText = isEmpty ? "" : reader.ReadElementContentAsString().Trim();
                    if (isEmpty) reader.Skip();
                    int.TryParse(intText, NumberStyles.Any, CultureInfo.InvariantCulture, out int iVal);
                    return LLSDValue.FromInteger(iVal);

                case "real":
                    string realText = isEmpty ? "" : reader.ReadElementContentAsString().Trim().ToLowerInvariant();
                    if (isEmpty) reader.Skip();
                    double rVal = parseReal(realText);
                    return LLSDValue.FromReal(rVal);

                case "string":
                    string strText = isEmpty ? "" : reader.ReadElementContentAsString();
                    if (isEmpty) reader.Skip();
                    return LLSDValue.FromString(strText);

                case "uuid":
                    string uuidText = isEmpty ? "" : reader.ReadElementContentAsString().Trim();
                    if (isEmpty) reader.Skip();
                    return LLSDValue.FromUUID(LLUUID.FromString(uuidText));

                case "date":
                    string dateText = isEmpty ? "" : reader.ReadElementContentAsString().Trim();
                    if (isEmpty) reader.Skip();
                    return LLSDValue.FromDate(LLDate.FromISOString(dateText));

                case "uri":
                    string uriText = isEmpty ? "" : reader.ReadElementContentAsString().Trim();
                    if (isEmpty) reader.Skip();
                    return LLSDValue.FromURI(LLURI.FromString(uriText));

                case "binary":
                    string encoding = reader.GetAttribute("encoding") ?? "base64";
                    string binText = isEmpty ? "" : reader.ReadElementContentAsString().Trim();
                    if (isEmpty) reader.Skip();

                    if (string.IsNullOrEmpty(binText)) return LLSDValue.FromBinary(Array.Empty<byte>());

                    byte[] bytes = encoding.ToLowerInvariant() switch
                    {
                        "base16" => parseHex(binText),
                        _ => Convert.FromBase64String(binText)
                    };
                    return LLSDValue.FromBinary(bytes);

                case "map":
                    var map = new Dictionary<string, LLSDValue>(StringComparer.Ordinal);
                    if (isEmpty)
                    {
                        reader.Skip();
                        return LLSDValue.FromMap(map);
                    }

                    reader.Read(); // Enter <map>
                    string? currentKey = null;

                    while (reader.NodeType != XmlNodeType.EndElement && reader.NodeType != XmlNodeType.None)
                    {
                        if (reader.NodeType == XmlNodeType.Element)
                        {
                            if (reader.Name == "key")
                            {
                                currentKey = reader.ReadElementContentAsString();
                            }
                            else if (currentKey != null)
                            {
                                map[currentKey] = ReadElement(reader);
                                currentKey = null;
                            }
                            else
                            {
                                reader.Skip();
                            }
                        }
                        else
                        {
                            reader.Read();
                        }
                    }

                    if (reader.NodeType == XmlNodeType.EndElement) reader.Read();
                    return LLSDValue.FromMap(map);

                case "array":
                    var list = new List<LLSDValue>();
                    if (isEmpty)
                    {
                        reader.Skip();
                        return LLSDValue.FromArray(list);
                    }

                    reader.Read(); // Enter <array>
                    while (reader.NodeType != XmlNodeType.EndElement && reader.NodeType != XmlNodeType.None)
                    {
                        if (reader.NodeType == XmlNodeType.Element)
                        {
                            list.Add(ReadElement(reader));
                        }
                        else
                        {
                            reader.Read();
                        }
                    }

                    if (reader.NodeType == XmlNodeType.EndElement) reader.Read();
                    return LLSDValue.FromArray(list);

                default:
                    reader.Skip();
                    return LLSDValue.Undefined;
            }
        }

        private static double parseReal(string text)
        {
            return text switch
            {
                "nan" => double.NaN,
                "inf" or "+inf" or "infinity" or "+infinity" => double.PositiveInfinity,
                "-inf" or "-infinity" => double.NegativeInfinity,
                _ => double.TryParse(text, NumberStyles.Any, CultureInfo.InvariantCulture, out double v) ? v : 0.0
            };
        }

        private static byte[] parseHex(string hex)
        {
            if (hex.Length % 2 != 0) return Array.Empty<byte>();
            byte[] bytes = new byte[hex.Length / 2];
            for (int i = 0; i < bytes.Length; i++)
            {
                bytes[i] = byte.Parse(hex.Substring(i * 2, 2), NumberStyles.HexNumber, CultureInfo.InvariantCulture);
            }
            return bytes;
        }
    }
}
