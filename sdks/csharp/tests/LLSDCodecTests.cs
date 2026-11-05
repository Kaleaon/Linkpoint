using System;
using System.Collections.Generic;
using System.Text;
using Linkpoint.LLSD;

namespace Linkpoint.LLSD.Tests
{
    public class LLSDCodecTests
    {
        [Fact]
        public void TestPrimitivesRoundTripBinary()
        {
            var map = new Dictionary<string, LLSDValue>
            {
                ["undef"] = LLSDValue.Undefined,
                ["bool_true"] = LLSDValue.FromBoolean(true),
                ["bool_false"] = LLSDValue.FromBoolean(false),
                ["integer"] = LLSDValue.FromInteger(42),
                ["real"] = LLSDValue.FromReal(3.1415926535),
                ["string"] = LLSDValue.FromString("Hello Second Life!"),
                ["uuid"] = LLSDValue.FromUUID(LLUUID.FromString("00112233-4455-6677-8899-aabbccddeeff")),
                ["date"] = LLSDValue.FromDate(LLDate.FromISOString("2026-10-05T09:37:56Z")),
                ["uri"] = LLSDValue.FromURI(LLURI.FromString("http://secondlife.com")),
                ["binary"] = LLSDValue.FromBinary(new byte[] { 0x01, 0x02, 0x03, 0x04, 0xFF })
            };

            var original = LLSDValue.FromMap(map);
            byte[] encoded = LLSDSerialize.ToBinary(original, includeHeader: true);

            Assert.Equal(LLSDFormat.Binary, LLSDSerialize.DetectFormat(encoded));

            var decoded = LLSDSerialize.Parse(encoded);

            Assert.Equal(original, decoded);
            Assert.True(decoded["bool_true"].AsBoolean());
            Assert.False(decoded["bool_false"].AsBoolean());
            Assert.Equal(42, decoded["integer"].AsInteger());
            Assert.Equal(3.1415926535, decoded["real"].AsReal(), 6);
            Assert.Equal("Hello Second Life!", decoded["string"].AsString());
            Assert.Equal("00112233-4455-6677-8899-aabbccddeeff", decoded["uuid"].AsUUID().ToString());
            Assert.Equal("2026-10-05T09:37:56Z", decoded["date"].AsDate().ToISOString());
            Assert.Equal("http://secondlife.com", decoded["uri"].AsURI().AsString());
            Assert.Equal(new byte[] { 0x01, 0x02, 0x03, 0x04, 0xFF }, decoded["binary"].AsBinary());
        }

        [Fact]
        public void TestPrimitivesRoundTripXml()
        {
            var map = new Dictionary<string, LLSDValue>
            {
                ["undef"] = LLSDValue.Undefined,
                ["bool_true"] = LLSDValue.FromBoolean(true),
                ["integer"] = LLSDValue.FromInteger(100),
                ["real"] = LLSDValue.FromReal(2.71828),
                ["string"] = LLSDValue.FromString("Metaverse XML"),
                ["uuid"] = LLSDValue.FromUUID(LLUUID.FromString("11223344-5566-7788-9900-aabbccddeeff")),
                ["date"] = LLSDValue.FromDate(LLDate.FromISOString("2025-12-31T23:59:59Z")),
                ["uri"] = LLSDValue.FromURI(LLURI.FromString("https://wiki.secondlife.com")),
                ["binary"] = LLSDValue.FromBinary(new byte[] { 0xAA, 0xBB, 0xCC })
            };

            var original = LLSDValue.FromMap(map);
            string xml = LLSDSerialize.ToXML(original, withDeclaration: true);

            Assert.Equal(LLSDFormat.Xml, LLSDSerialize.DetectFormat(Encoding.UTF8.GetBytes(xml)));

            var decoded = LLSDSerialize.FromXML(xml);

            Assert.Equal(original["bool_true"], decoded["bool_true"]);
            Assert.Equal(100, decoded["integer"].AsInteger());
            Assert.Equal(2.71828, decoded["real"].AsReal(), 5);
            Assert.Equal("Metaverse XML", decoded["string"].AsString());
            Assert.Equal("11223344-5566-7788-9900-aabbccddeeff", decoded["uuid"].AsUUID().ToString());
            Assert.Equal("2025-12-31T23:59:59Z", decoded["date"].AsDate().ToISOString());
            Assert.Equal("https://wiki.secondlife.com", decoded["uri"].AsURI().AsString());
            Assert.Equal(new byte[] { 0xAA, 0xBB, 0xCC }, decoded["binary"].AsBinary());
        }

        [Fact]
        public void TestPrimitivesRoundTripNotation()
        {
            var original = LLSDValue.FromArray(new LLSDValue[]
            {
                LLSDValue.Undefined,
                LLSDValue.FromBoolean(true),
                LLSDValue.FromInteger(-12345),
                LLSDValue.FromReal(-0.00123),
                LLSDValue.FromString("Notation ' quoted \\ test"),
                LLSDValue.FromUUID(LLUUID.FromString("99999999-8888-7777-6666-555555555555")),
                LLSDValue.FromDate(LLDate.FromISOString("2024-01-01T00:00:00Z")),
                LLSDValue.FromURI(LLURI.FromString("http://example.org")),
                LLSDValue.FromBinary(new byte[] { 0x10, 0x20, 0x30 })
            });

            string notation = LLSDSerialize.ToNotation(original, includeHeader: true);

            Assert.Equal(LLSDFormat.Notation, LLSDSerialize.DetectFormat(Encoding.UTF8.GetBytes(notation)));

            var decoded = LLSDSerialize.FromNotation(notation);

            Assert.True(decoded[0].IsUndefined);
            Assert.True(decoded[1].AsBoolean());
            Assert.Equal(-12345, decoded[2].AsInteger());
            Assert.Equal(-0.00123, decoded[3].AsReal(), 5);
            Assert.Equal("Notation ' quoted \\ test", decoded[4].AsString());
            Assert.Equal("99999999-8888-7777-6666-555555555555", decoded[5].AsUUID().ToString());
            Assert.Equal("http://example.org", decoded[7].AsURI().AsString());
            Assert.Equal(new byte[] { 0x10, 0x20, 0x30 }, decoded[8].AsBinary());
        }

        [Fact]
        public void TestPrimitivesRoundTripJson()
        {
            var map = new Dictionary<string, LLSDValue>
            {
                ["null_key"] = LLSDValue.Undefined,
                ["active"] = LLSDValue.FromBoolean(true),
                ["count"] = LLSDValue.FromInteger(50),
                ["price"] = LLSDValue.FromReal(19.99),
                ["name"] = LLSDValue.FromString("Avatar \"Linkpoint\""),
                ["list"] = LLSDValue.FromArray(new LLSDValue[] { LLSDValue.FromInteger(1), LLSDValue.FromInteger(2) })
            };

            var original = LLSDValue.FromMap(map);
            string json = LLSDSerialize.ToJSON(original);

            Assert.Equal(LLSDFormat.Json, LLSDSerialize.DetectFormat(Encoding.UTF8.GetBytes(json)));

            var decoded = LLSDSerialize.FromJSON(json);

            Assert.True(decoded["null_key"].IsUndefined);
            Assert.True(decoded["active"].AsBoolean());
            Assert.Equal(50, decoded["count"].AsInteger());
            Assert.Equal(19.99, decoded["price"].AsReal(), 2);
            Assert.Equal("Avatar \"Linkpoint\"", decoded["name"].AsString());
            Assert.Equal(2, decoded["list"].Count);
        }
    }
}
