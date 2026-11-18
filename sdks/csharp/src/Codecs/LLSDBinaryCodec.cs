using System;
using System.Buffers.Binary;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace Linkpoint.LLSD.Codecs
{
    public static class LLSDBinaryCodec
    {
        public const string Header = "<? llsd/binary ?>\n";

        public static byte[] Encode(LLSDValue value, bool includeHeader = false)
        {
            using var ms = new MemoryStream();
            if (includeHeader)
            {
                byte[] headerBytes = Encoding.UTF8.GetBytes(Header);
                ms.Write(headerBytes, 0, headerBytes.Length);
            }

            WriteValue(ms, value);
            return ms.ToArray();
        }

        private static void WriteValue(Stream stream, LLSDValue value)
        {
            Span<byte> buffer = stackalloc byte[8];

            switch (value.Type)
            {
                case LLSDType.Undefined:
                    stream.WriteByte((byte)'!');
                    break;

                case LLSDType.Boolean:
                    stream.WriteByte(value.AsBoolean() ? (byte)'1' : (byte)'0');
                    break;

                case LLSDType.Integer:
                    stream.WriteByte((byte)'i');
                    BinaryPrimitives.WriteInt32BigEndian(buffer, value.AsInteger());
                    stream.Write(buffer.Slice(0, 4));
                    break;

                case LLSDType.Real:
                    stream.WriteByte((byte)'r');
                    BinaryPrimitives.WriteInt64BigEndian(buffer, BitConverter.DoubleToInt64Bits(value.AsReal()));
                    stream.Write(buffer);
                    break;

                case LLSDType.String:
                    stream.WriteByte((byte)'s');
                    byte[] strBytes = Encoding.UTF8.GetBytes(value.AsString());
                    BinaryPrimitives.WriteInt32BigEndian(buffer, strBytes.Length);
                    stream.Write(buffer.Slice(0, 4));
                    stream.Write(strBytes, 0, strBytes.Length);
                    break;

                case LLSDType.UUID:
                    stream.WriteByte((byte)'u');
                    byte[] uuidBytes = value.AsUUID().ToByteArray();
                    stream.Write(uuidBytes, 0, 16);
                    break;

                case LLSDType.Date:
                    stream.WriteByte((byte)'d');
                    BinaryPrimitives.WriteInt64LittleEndian(buffer, BitConverter.DoubleToInt64Bits(value.AsDate().SecondsSinceEpoch));
                    stream.Write(buffer);
                    break;

                case LLSDType.URI:
                    stream.WriteByte((byte)'l');
                    byte[] uriBytes = Encoding.UTF8.GetBytes(value.AsURI().AsString());
                    BinaryPrimitives.WriteInt32BigEndian(buffer, uriBytes.Length);
                    stream.Write(buffer.Slice(0, 4));
                    stream.Write(uriBytes, 0, uriBytes.Length);
                    break;

                case LLSDType.Binary:
                    stream.WriteByte((byte)'b');
                    byte[] binBytes = value.AsBinary();
                    BinaryPrimitives.WriteInt32BigEndian(buffer, binBytes.Length);
                    stream.Write(buffer.Slice(0, 4));
                    stream.Write(binBytes, 0, binBytes.Length);
                    break;

                case LLSDType.Map:
                    stream.WriteByte((byte)'{');
                    var map = value.AsMap();
                    BinaryPrimitives.WriteInt32BigEndian(buffer, map.Count);
                    stream.Write(buffer.Slice(0, 4));

                    foreach (var kvp in map)
                    {
                        stream.WriteByte((byte)'k');
                        byte[] keyBytes = Encoding.UTF8.GetBytes(kvp.Key);
                        BinaryPrimitives.WriteInt32BigEndian(buffer, keyBytes.Length);
                        stream.Write(buffer.Slice(0, 4));
                        stream.Write(keyBytes, 0, keyBytes.Length);
                        WriteValue(stream, kvp.Value);
                    }

                    stream.WriteByte((byte)'}');
                    break;

                case LLSDType.Array:
                    stream.WriteByte((byte)'[');
                    var list = value.AsArray();
                    BinaryPrimitives.WriteInt32BigEndian(buffer, list.Count);
                    stream.Write(buffer.Slice(0, 4));

                    foreach (var item in list)
                    {
                        WriteValue(stream, item);
                    }

                    stream.WriteByte((byte)']');
                    break;

                default:
                    stream.WriteByte((byte)'!');
                    break;
            }
        }

        public static LLSDValue Decode(ReadOnlySpan<byte> span)
        {
            // Check for optional header
            if (span.Length >= 2 && span[0] == '<' && span[1] == '?')
            {
                int nlPos = span.IndexOf((byte)'\n');
                if (nlPos >= 0)
                {
                    span = span.Slice(nlPos + 1);
                }
            }

            int offset = 0;
            return ReadValue(span, ref offset);
        }

        private static LLSDValue ReadValue(ReadOnlySpan<byte> span, ref int offset)
        {
            if (offset >= span.Length) return LLSDValue.Undefined;

            byte tag = span[offset++];
            switch ((char)tag)
            {
                case '!':
                    return LLSDValue.Undefined;

                case '1':
                    return LLSDValue.FromBoolean(true);

                case '0':
                    return LLSDValue.FromBoolean(false);

                case 'i':
                    if (offset + 4 > span.Length) return LLSDValue.Undefined;
                    int iVal = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                    offset += 4;
                    return LLSDValue.FromInteger(iVal);

                case 'r':
                    if (offset + 8 > span.Length) return LLSDValue.Undefined;
                    double rVal = BitConverter.Int64BitsToDouble(BinaryPrimitives.ReadInt64BigEndian(span.Slice(offset, 8)));
                    offset += 8;
                    return LLSDValue.FromReal(rVal);

                case 's':
                    {
                        if (offset + 4 > span.Length) return LLSDValue.Undefined;
                        int sLen = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                        offset += 4;
                        if (sLen <= 0) return LLSDValue.FromString("");
                        if (offset + sLen > span.Length) return LLSDValue.Undefined;
                        string sVal = Encoding.UTF8.GetString(span.Slice(offset, sLen));
                        offset += sLen;
                        return LLSDValue.FromString(sVal);
                    }

                case 'u':
                    {
                        if (offset + 16 > span.Length) return LLSDValue.Undefined;
                        LLUUID uuid = new LLUUID(span.Slice(offset, 16));
                        offset += 16;
                        return LLSDValue.FromUUID(uuid);
                    }

                case 'd':
                    {
                        if (offset + 8 > span.Length) return LLSDValue.Undefined;
                        double seconds = BitConverter.Int64BitsToDouble(BinaryPrimitives.ReadInt64LittleEndian(span.Slice(offset, 8)));
                        offset += 8;
                        return LLSDValue.FromDate(new LLDate(seconds));
                    }

                case 'l':
                    {
                        if (offset + 4 > span.Length) return LLSDValue.Undefined;
                        int lLen = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                        offset += 4;
                        if (lLen <= 0) return LLSDValue.FromURI(LLURI.Empty);
                        if (offset + lLen > span.Length) return LLSDValue.Undefined;
                        string uriStr = Encoding.UTF8.GetString(span.Slice(offset, lLen));
                        offset += lLen;
                        return LLSDValue.FromURI(LLURI.FromString(uriStr));
                    }

                case 'b':
                    {
                        if (offset + 4 > span.Length) return LLSDValue.Undefined;
                        int bLen = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                        offset += 4;
                        if (bLen <= 0) return LLSDValue.FromBinary(Array.Empty<byte>());
                        if (offset + bLen > span.Length) return LLSDValue.Undefined;
                        byte[] bin = span.Slice(offset, bLen).ToArray();
                        offset += bLen;
                        return LLSDValue.FromBinary(bin);
                    }

                case '{':
                    {
                        if (offset + 4 > span.Length) return LLSDValue.Undefined;
                        int count = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                        offset += 4;

                        var map = new Dictionary<string, LLSDValue>(count, StringComparer.Ordinal);
                        for (int k = 0; k < count; k++)
                        {
                            if (offset >= span.Length || span[offset] != (byte)'k') break;
                            offset++; // skip 'k'

                            if (offset + 4 > span.Length) break;
                            int kLen = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                            offset += 4;

                            string key = Encoding.UTF8.GetString(span.Slice(offset, kLen));
                            offset += kLen;

                            LLSDValue val = ReadValue(span, ref offset);
                            map[key] = val;
                        }

                        if (offset < span.Length && span[offset] == (byte)'}')
                        {
                            offset++;
                        }

                        return LLSDValue.FromMap(map);
                    }

                case '[':
                    {
                        if (offset + 4 > span.Length) return LLSDValue.Undefined;
                        int count = BinaryPrimitives.ReadInt32BigEndian(span.Slice(offset, 4));
                        offset += 4;

                        var list = new List<LLSDValue>(count);
                        for (int i = 0; i < count; i++)
                        {
                            list.Add(ReadValue(span, ref offset));
                        }

                        if (offset < span.Length && span[offset] == (byte)']')
                        {
                            offset++;
                        }

                        return LLSDValue.FromArray(list);
                    }

                default:
                    return LLSDValue.Undefined;
            }
        }
    }
}
