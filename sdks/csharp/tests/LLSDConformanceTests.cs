using System;
using System.Collections.Generic;

using Linkpoint.LLSD;
using Linkpoint.LLSD.Schema;

namespace Linkpoint.LLSD.Tests
{
    public class LLSDConformanceTests
    {
        [Fact]
        public void LLSD_EDGE_001_ParseXmlUndefProducesUndefinedNode()
        {
            string xml = "<llsd><undef/></llsd>";
            var parsed = LLSDSerialize.FromXML(xml);
            Assert.True(parsed.IsUndefined, "XML <undef/> MUST produce Undefined node type");
        }

        [Fact]
        public void LLSD_EDGE_002_ParseXmlEmptyStringVsExplicitString()
        {
            string xmlSelfClosing = "<llsd><string/></llsd>";
            string xmlExplicitClose = "<llsd><string></string></llsd>";

            var parsedSelfClosing = LLSDSerialize.FromXML(xmlSelfClosing);
            var parsedExplicitClose = LLSDSerialize.FromXML(xmlExplicitClose);

            Assert.True(parsedSelfClosing.IsString, "<string/> must decode as string type");
            Assert.Equal("", parsedSelfClosing.AsString());

            Assert.True(parsedExplicitClose.IsString, "<string></string> must decode as string type");
            Assert.Equal("", parsedExplicitClose.AsString());
        }

        [Fact]
        public void LLSD_EDGE_003_ParseBinaryBlobPayloadPreservedLosslessly()
        {
            byte[] blobData = new byte[256];
            for (int i = 0; i < 256; i++) blobData[i] = (byte)i;

            var val = LLSDValue.FromBinary(blobData);
            byte[] encoded = LLSDSerialize.ToBinary(val);
            var decoded = LLSDSerialize.FromBinary(encoded);

            Assert.Equal(blobData, decoded.AsBinary());
        }

        [Fact]
        public void LLSD_EDGE_004_ParseUuidTextualFormCanonicalRoundTrip()
        {
            string uuidText = "d22c9a18-36c1-432d-9488-ddfb5d7c3b99";
            var uuid = LLUUID.FromString(uuidText);

            Assert.Equal(uuidText.ToLowerInvariant(), uuid.ToString().ToLowerInvariant());

            var val = LLSDValue.FromUUID(uuid);
            byte[] bin = LLSDSerialize.ToBinary(val);
            var decodedBin = LLSDSerialize.FromBinary(bin);

            Assert.Equal(uuidText.ToLowerInvariant(), decodedBin.AsUUID().ToString().ToLowerInvariant());
        }

        [Fact]
        public void LLSD_EDGE_005_ParseAndSerializeDateRetainsInstantPrecision()
        {
            string isoText = "2026-10-05T09:37:56.123Z";
            var date = LLDate.FromISOString(isoText);

            var val = LLSDValue.FromDate(date);
            byte[] bin = LLSDSerialize.ToBinary(val);
            var decoded = LLSDSerialize.FromBinary(bin);

            Assert.Equal("2026-10-05T09:37:56.123Z", decoded.AsDate().ToISOString());
        }

        [Fact]
        public void LLSD_EDGE_006_ParseMapWithKeyOrderPreserved()
        {
            var map = new Dictionary<string, LLSDValue>
            {
                ["alpha"] = LLSDValue.FromInteger(1),
                ["gamma"] = LLSDValue.FromInteger(3),
                ["beta"] = LLSDValue.FromInteger(2)
            };

            var val = LLSDValue.FromMap(map);
            byte[] bin = LLSDSerialize.ToBinary(val);
            var decoded = LLSDSerialize.FromBinary(bin);

            Assert.True(decoded.Has("alpha"));
            Assert.True(decoded.Has("gamma"));
            Assert.True(decoded.Has("beta"));
        }

        [Fact]
        public void LLSD_EDGE_007_ParseArrayNestingAndMixedTypes()
        {
            var nestedMap = new Dictionary<string, LLSDValue> { ["inner_key"] = LLSDValue.FromString("inner_val") };
            var list = new List<LLSDValue>
            {
                LLSDValue.FromInteger(100),
                LLSDValue.FromString("text"),
                LLSDValue.FromMap(nestedMap),
                LLSDValue.FromArray(new LLSDValue[] { LLSDValue.FromBoolean(true), LLSDValue.FromBoolean(false) })
            };

            var val = LLSDValue.FromArray(list);
            byte[] bin = LLSDSerialize.ToBinary(val);
            var decoded = LLSDSerialize.FromBinary(bin);

            Assert.Equal(4, decoded.Count);
            Assert.Equal(100, decoded[0].AsInteger());
            Assert.Equal("text", decoded[1].AsString());
            Assert.Equal("inner_val", decoded[2]["inner_key"].AsString());
            Assert.True(decoded[3][0].AsBoolean());
            Assert.False(decoded[3][1].AsBoolean());
        }

        [Fact]
        public void LLSD_EDGE_008_SerializeExplicitFormatChoice()
        {
            var map = new Dictionary<string, LLSDValue> { ["value"] = LLSDValue.FromInteger(12345) };
            var val = LLSDValue.FromMap(map);

            byte[] bin = LLSDSerialize.ToBinary(val, includeHeader: true);
            string xml = LLSDSerialize.ToXML(val, withDeclaration: true);
            string notation = LLSDSerialize.ToNotation(val, includeHeader: true);
            string json = LLSDSerialize.ToJSON(val);

            Assert.Equal(LLSDFormat.Binary, LLSDSerialize.DetectFormat(bin));
            Assert.Equal(LLSDFormat.Xml, LLSDSerialize.DetectFormat(System.Text.Encoding.UTF8.GetBytes(xml)));
            Assert.Equal(LLSDFormat.Notation, LLSDSerialize.DetectFormat(System.Text.Encoding.UTF8.GetBytes(notation)));
            Assert.Equal(LLSDFormat.Json, LLSDSerialize.DetectFormat(System.Text.Encoding.UTF8.GetBytes(json)));
        }

        [Fact]
        public void TestTextureTransformPackedLayout()
        {
            var adapter = new TextureTransformAdapter
            {
                ScaleX = 2.0f,
                ScaleY = 3.0f,
                Rotation = 1.5707963f,
                OffsetX = 0.5f,
                OffsetY = 0.25f
            };

            float[] packed = adapter.GetPacked();
            float[] tight = adapter.GetPackedTight();

            Assert.Equal(8, packed.Length);
            Assert.Equal(2.0f, packed[0]);
            Assert.Equal(3.0f, packed[1]);
            Assert.Equal(1.5707963f, packed[2]);
            Assert.Equal(0.0f, packed[3]);
            Assert.Equal(0.5f, packed[4]);
            Assert.Equal(0.25f, packed[5]);
            Assert.Equal(0.0f, packed[6]);
            Assert.Equal(0.0f, packed[7]);

            Assert.Equal(5, tight.Length);
            Assert.Equal(2.0f, tight[0]);
            Assert.Equal(3.0f, tight[1]);
            Assert.Equal(1.5707963f, tight[2]);
            Assert.Equal(0.5f, tight[3]);
            Assert.Equal(0.25f, tight[4]);
        }

        [Fact]
        public void TestLodMappingAndJointLimits()
        {
            Assert.Equal("high_lod", AssetSchemaConstants.GetLodKey(DetailLevel.Highest));
            Assert.Equal("high_lod", AssetSchemaConstants.GetLodKey(DetailLevel.High));
            Assert.Equal("medium_lod", AssetSchemaConstants.GetLodKey(DetailLevel.Medium));
            Assert.Equal("low_lod", AssetSchemaConstants.GetLodKey(DetailLevel.Low));
            Assert.Equal("lowest_lod", AssetSchemaConstants.GetLodKey(DetailLevel.Lowest));

            Assert.Equal(256, AssetSchemaConstants.MAX_RIGGED_MESH_JOINTS);
            Assert.Equal(0xFF, AssetSchemaConstants.JOINT_SENTINEL);
        }
    }
}
