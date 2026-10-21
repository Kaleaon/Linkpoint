using System;
using System.Text;
using Linkpoint.LLSD.Codecs;

namespace Linkpoint.LLSD
{
    public enum LLSDFormat
    {
        Xml,
        Binary,
        Notation,
        Json
    }

    /// <summary>
    /// Facade providing unified LLSD serialization, parsing, and format auto-detection.
    /// </summary>
    public static class LLSDSerialize
    {
        public static LLSDFormat DetectFormat(ReadOnlySpan<byte> bytes)
        {
            if (bytes.Length == 0) return LLSDFormat.Xml;

            // Trim leading whitespace or BOM
            int start = 0;
            while (start < bytes.Length && (bytes[start] == ' ' || bytes[start] == '\t' || bytes[start] == '\r' || bytes[start] == '\n' || bytes[start] == 0xEF || bytes[start] == 0xBB || bytes[start] == 0xBF))
            {
                start++;
            }

            if (start >= bytes.Length) return LLSDFormat.Xml;

            ReadOnlySpan<byte> sub = bytes.Slice(start);
            string prefixStr = Encoding.ASCII.GetString(sub.Slice(0, Math.Min(64, sub.Length)));

            if (prefixStr.StartsWith("<?llsd/binary?>") || prefixStr.StartsWith("<? llsd/binary ?>"))
            {
                return LLSDFormat.Binary;
            }

            if (prefixStr.StartsWith("<?llsd/notation?>") || prefixStr.StartsWith("<? llsd/notation ?>"))
            {
                return LLSDFormat.Notation;
            }

            if (prefixStr.StartsWith("<?xml") || prefixStr.StartsWith("<llsd>") || prefixStr.StartsWith("<"))
            {
                return LLSDFormat.Xml;
            }

            if (prefixStr.StartsWith("{") || prefixStr.StartsWith("["))
            {
                return LLSDFormat.Json;
            }

            return LLSDFormat.Xml;
        }

        public static LLSDValue Parse(ReadOnlySpan<byte> bytes)
        {
            LLSDFormat format = DetectFormat(bytes);
            return Parse(bytes, format);
        }

        public static LLSDValue Parse(ReadOnlySpan<byte> bytes, LLSDFormat format)
        {
            return format switch
            {
                LLSDFormat.Binary => LLSDBinaryCodec.Decode(bytes),
                LLSDFormat.Notation => LLSDNotationCodec.Decode(Encoding.UTF8.GetString(bytes)),
                LLSDFormat.Xml => LLSDXmlCodec.Decode(Encoding.UTF8.GetString(bytes)),
                LLSDFormat.Json => LLSDJsonCodec.Decode(Encoding.UTF8.GetString(bytes)),
                _ => LLSDValue.Undefined
            };
        }

        public static LLSDValue FromBinary(ReadOnlySpan<byte> bytes) => LLSDBinaryCodec.Decode(bytes);
        public static LLSDValue FromXML(string xml) => LLSDXmlCodec.Decode(xml);
        public static LLSDValue FromNotation(string text) => LLSDNotationCodec.Decode(text);
        public static LLSDValue FromJSON(string json) => LLSDJsonCodec.Decode(json);

        public static byte[] ToBinary(LLSDValue value, bool includeHeader = false) => LLSDBinaryCodec.Encode(value, includeHeader);
        public static string ToXML(LLSDValue value, bool withDeclaration = false) => LLSDXmlCodec.Encode(value, withDeclaration);
        public static string ToNotation(LLSDValue value, bool includeHeader = false) => LLSDNotationCodec.Encode(value, includeHeader);
        public static string ToJSON(LLSDValue value) => LLSDJsonCodec.Encode(value);
    }
}
