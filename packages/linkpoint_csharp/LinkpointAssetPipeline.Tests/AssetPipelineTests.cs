using System;
using System.IO;
using System.Text.Json;
using Linkpoint.AssetPipeline;

namespace Xunit
{
    [AttributeUsage(AttributeTargets.Method)]
    public class FactAttribute : Attribute { }

    public static class Assert
    {
        public static void True(bool condition, string message = "")
        {
            if (!condition) throw new Exception("Assert.True failed: " + message);
        }

        public static void Equal<T>(T expected, T actual)
        {
            if (!Equals(expected, actual)) throw new Exception($"Assert.Equal failed. Expected: {expected}, Actual: {actual}");
        }

        public static void NotNull(object? obj)
        {
            if (obj == null) throw new Exception("Assert.NotNull failed");
        }

        public static void Null(object? obj)
        {
            if (obj != null) throw new Exception("Assert.Null failed");
        }

        public static void Contains(string expectedSubstring, string actualString)
        {
            if (actualString == null || !actualString.Contains(expectedSubstring))
                throw new Exception($"Assert.Contains failed. String does not contain: {expectedSubstring}");
        }
    }
}

namespace LinkpointAssetPipeline.Tests
{
    using Xunit;

    public class AssetPipelineTests
    {
        private static string FindVectorFile(string relativeSubpath)
        {
            string[] parts = relativeSubpath.Split(new[] { '/', '\\' }, StringSplitOptions.RemoveEmptyEntries);
            string normalizedSubpath = Path.Combine(parts);

            var searchRoots = new[]
            {
                AppContext.BaseDirectory,
                Directory.GetCurrentDirectory(),
                "/app/Linkpoint",
                "C:\\app\\Linkpoint"
            };

            foreach (var root in searchRoots)
            {
                if (string.IsNullOrEmpty(root)) continue;
                var current = new DirectoryInfo(root);
                while (current != null)
                {
                    string candidate = Path.Combine(current.FullName, "test-vectors", normalizedSubpath);
                    if (File.Exists(candidate)) return candidate;
                    current = current.Parent;
                }
            }

            throw new FileNotFoundException($"Could not locate test vector file for subpath: {relativeSubpath}");
        }

        private static byte[] HexToBytes(string hex)
        {
            byte[] bytes = new byte[hex.Length / 2];
            for (int i = 0; i < hex.Length; i += 2)
            {
                bytes[i / 2] = Convert.ToByte(hex.Substring(i, 2), 16);
            }
            return bytes;
        }

        [Fact]
        public void TextureDecoder_DecodesJ2KTestVectorsNatively()
        {
            string vectorPath = FindVectorFile("textures/j2k_texture_decoder_vectors.json");

            string jsonString = File.ReadAllText(vectorPath);
            using var doc = JsonDocument.Parse(jsonString);
            var vectors = doc.RootElement.GetProperty("j2k_vectors");

            foreach (var element in vectors.EnumerateArray())
            {
                string hexBytes = element.GetProperty("hex_bytes").GetString()!;
                byte[] bytes = HexToBytes(hexBytes);
                var expected = element.GetProperty("expected");
                string expectedStatus = expected.GetProperty("status").GetString()!;

                if (expectedStatus == "success")
                {
                    var header = TextureDecoder.ParseHeader(bytes);
                    Assert.Equal("success", header.Status);
                    Assert.Equal(expected.GetProperty("width").GetInt32(), header.Width);
                    Assert.Equal(expected.GetProperty("height").GetInt32(), header.Height);

                    using var image = TextureDecoder.DecodeTexture(bytes);
                    Assert.NotNull(image);
                    Assert.Equal(header.Width, image!.Width);
                    Assert.Equal(header.Height, image!.Height);
                }
                else
                {
                    var header = TextureDecoder.ParseHeader(bytes);
                    Assert.Equal("fallback", header.Status);

                    using var image = TextureDecoder.DecodeTexture(bytes);
                    Assert.Null(image);
                }
            }
        }

        [Fact]
        public void LLMeshConverter_ConvertsLLMeshToGltfUsingSharpGLTF()
        {
            string vectorPath = FindVectorFile("mesh/llmesh_decompress_vectors.json");

            string jsonString = File.ReadAllText(vectorPath);
            using var doc = JsonDocument.Parse(jsonString);
            var vectors = doc.RootElement.GetProperty("llmesh_vectors");

            foreach (var element in vectors.EnumerateArray())
            {
                string hexBytes = element.GetProperty("hex_bytes").GetString()!;
                byte[] bytes = HexToBytes(hexBytes);
                var expected = element.GetProperty("expected");

                var parsed = LLMeshToGltfConverter.ParseBinary(bytes);
                Assert.NotNull(parsed);
                Assert.Equal(expected.GetProperty("vertex_count").GetInt32(), parsed!.VertexCount);
                Assert.Equal(expected.GetProperty("index_count").GetInt32(), parsed!.IndexCount);

                string gltfJson = LLMeshToGltfConverter.ConvertToGltfJson(parsed);
                Assert.Contains("\"asset\"", gltfJson);
                Assert.Contains("\"POSITION\"", gltfJson);
            }
        }

        public static int Main()
        {
            Console.WriteLine("Running C# Linkpoint Asset Pipeline Tests...");
            var tests = new AssetPipelineTests();
            tests.TextureDecoder_DecodesJ2KTestVectorsNatively();
            Console.WriteLine("PASSED: TextureDecoder_DecodesJ2KTestVectorsNatively");
            tests.LLMeshConverter_ConvertsLLMeshToGltfUsingSharpGLTF();
            Console.WriteLine("PASSED: LLMeshConverter_ConvertsLLMeshToGltfUsingSharpGLTF");
            Console.WriteLine("All C# tests passed successfully!");
            return 0;
        }
    }
}
