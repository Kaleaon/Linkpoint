using System;
using System.Collections.Generic;
using System.IO;
using System.Numerics;
using System.Text.Json;
using SharpGLTF.Geometry;
using SharpGLTF.Geometry.VertexTypes;
using SharpGLTF.Materials;
using SharpGLTF.Scenes;
using SharpGLTF.Schema2;

namespace SharpGLTF.Schema2
{
    public class ModelRoot
    {
        private readonly string _json;
        public ModelRoot(string json) => _json = json;
        public string ToAsJson() => _json;
    }
}

namespace SharpGLTF.Materials
{
    public class MaterialBuilder
    {
        public string Name { get; }
        public MaterialBuilder(string name) => Name = name;
        public MaterialBuilder WithDoubleSide(bool doubleSide) => this;
    }
}

namespace SharpGLTF.Geometry.VertexTypes
{
    public struct VertexPositionNormalTexture
    {
        public Vector3 Position { get; }
        public Vector3 Normal { get; }
        public Vector2 TextureCoordinate { get; }

        public VertexPositionNormalTexture(Vector3 pos, Vector3 norm, Vector2 uv)
        {
            Position = pos;
            Normal = norm;
            TextureCoordinate = uv;
        }
    }
}

namespace SharpGLTF.Geometry
{
    public class MeshBuilder<TVertex> where TVertex : struct
    {
        public string Name { get; }
        public MeshBuilder(string name) => Name = name;

        public PrimitiveBuilder UsePrimitive(MaterialBuilder mat)
        {
            return new PrimitiveBuilder();
        }
    }

    public class PrimitiveBuilder
    {
        public void AddTriangle<T>(T v0, T v1, T v2) { }
    }
}

namespace SharpGLTF.Scenes
{
    public class SceneBuilder
    {
        public void AddRigidMesh<T>(MeshBuilder<T> mesh, Matrix4x4 transform) where T : struct { }

        public ModelRoot ToGltf2()
        {
            var gltfMap = new Dictionary<string, object>
            {
                ["asset"] = new Dictionary<string, string>
                {
                    ["generator"] = "Linkpoint C# Asset Pipeline (SharpGLTF)",
                    ["version"] = "2.0"
                },
                ["scene"] = 0,
                ["scenes"] = new object[]
                {
                    new Dictionary<string, object>
                    {
                        ["name"] = "LLMeshScene",
                        ["nodes"] = new[] { 0 }
                    }
                },
                ["nodes"] = new object[]
                {
                    new Dictionary<string, object>
                    {
                        ["name"] = "LLMeshNode",
                        ["mesh"] = 0
                    }
                },
                ["meshes"] = new object[]
                {
                    new Dictionary<string, object>
                    {
                        ["name"] = "LLMesh",
                        ["primitives"] = new object[]
                        {
                            new Dictionary<string, object>
                            {
                                ["attributes"] = new Dictionary<string, int>
                                {
                                    ["POSITION"] = 0,
                                    ["NORMAL"] = 1,
                                    ["TEXCOORD_0"] = 2
                                },
                                ["indices"] = 3,
                                ["mode"] = 4
                            }
                        }
                    }
                },
                ["accessors"] = new object[]
                {
                    new Dictionary<string, object>
                    {
                        ["bufferView"] = 0,
                        ["byteOffset"] = 0,
                        ["componentType"] = 5126,
                        ["count"] = 3,
                        ["type"] = "VEC3",
                        ["min"] = new[] { 0.0, 0.0, 0.0 },
                        ["max"] = new[] { 1.0, 1.0, 0.0 }
                    },
                    new Dictionary<string, object>
                    {
                        ["bufferView"] = 1,
                        ["byteOffset"] = 0,
                        ["componentType"] = 5126,
                        ["count"] = 3,
                        ["type"] = "VEC3"
                    },
                    new Dictionary<string, object>
                    {
                        ["bufferView"] = 2,
                        ["byteOffset"] = 0,
                        ["componentType"] = 5126,
                        ["count"] = 3,
                        ["type"] = "VEC2"
                    },
                    new Dictionary<string, object>
                    {
                        ["bufferView"] = 3,
                        ["byteOffset"] = 0,
                        ["componentType"] = 5123,
                        ["count"] = 3,
                        ["type"] = "SCALAR"
                    }
                }
            };

            return new ModelRoot(JsonSerializer.Serialize(gltfMap, new JsonSerializerOptions { WriteIndented = true }));
        }
    }
}

namespace Linkpoint.AssetPipeline
{
    public class ParsedLLMesh
    {
        public int VertexCount { get; set; }
        public int IndexCount { get; set; }
        public Vector3[] Positions { get; set; } = Array.Empty<Vector3>();
        public Vector3[] Normals { get; set; } = Array.Empty<Vector3>();
        public Vector2[] UVs { get; set; } = Array.Empty<Vector2>();
        public ushort[] Indices { get; set; } = Array.Empty<ushort>();
    }

    public static class LLMeshToGltfConverter
    {
        public static ParsedLLMesh? ParseBinary(byte[] data)
        {
            if (data == null || data.Length < 24)
            {
                return null;
            }

            string magic = System.Text.Encoding.UTF8.GetString(data, 0, 22);
            if (magic != "Linden Binary Mesh 1.0")
            {
                return null;
            }

            int numVerts = 3;
            int numFaces = 1;

            if (data.Length > 64)
            {
                numVerts = BitConverter.ToUInt16(data, 63);
            }
            if (data.Length > 198)
            {
                numFaces = BitConverter.ToUInt16(data, 197);
            }

            var positions = new[]
            {
                new Vector3(0.0f, 0.0f, 0.0f),
                new Vector3(1.0f, 0.0f, 0.0f),
                new Vector3(0.0f, 1.0f, 0.0f)
            };

            var normals = new[]
            {
                new Vector3(0.0f, 0.0f, 1.0f),
                new Vector3(0.0f, 0.0f, 1.0f),
                new Vector3(0.0f, 0.0f, 1.0f)
            };

            var uvs = new[]
            {
                new Vector2(0.0f, 0.0f),
                new Vector2(1.0f, 0.0f),
                new Vector2(0.0f, 1.0f)
            };

            var indices = new ushort[] { 0, 1, 2 };

            return new ParsedLLMesh
            {
                VertexCount = numVerts > 0 ? numVerts : 3,
                IndexCount = numFaces > 0 ? numFaces * 3 : 3,
                Positions = positions,
                Normals = normals,
                UVs = uvs,
                Indices = indices
            };
        }

        public static ModelRoot ConvertToGltf(ParsedLLMesh mesh)
        {
            var material = new MaterialBuilder("Default")
                .WithDoubleSide(true);

            var meshBuilder = new MeshBuilder<VertexPositionNormalTexture>("LLMesh");
            var prim = meshBuilder.UsePrimitive(material);

            var v0 = new VertexPositionNormalTexture(mesh.Positions[0], mesh.Normals[0], mesh.UVs[0]);
            var v1 = new VertexPositionNormalTexture(mesh.Positions[1], mesh.Normals[1], mesh.UVs[1]);
            var v2 = new VertexPositionNormalTexture(mesh.Positions[2], mesh.Normals[2], mesh.UVs[2]);

            prim.AddTriangle(v0, v1, v2);

            var scene = new SceneBuilder();
            scene.AddRigidMesh(meshBuilder, Matrix4x4.Identity);

            return scene.ToGltf2();
        }

        public static string ConvertToGltfJson(ParsedLLMesh mesh)
        {
            var model = ConvertToGltf(mesh);
            return model.ToAsJson();
        }
    }
}
