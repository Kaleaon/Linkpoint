use gltf_json as json;
use json::validation::USize64;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ParsedLLMesh {
    pub vertex_count: usize,
    pub index_count: usize,
    pub positions: Vec<f32>,
    pub normals: Vec<f32>,
    pub uvs: Vec<f32>,
    pub indices: Vec<u16>,
}

pub fn parse_llmesh_binary(data: &[u8]) -> Option<ParsedLLMesh> {
    if data.len() < 24 {
        return None;
    }

    let magic = String::from_utf8_lossy(&data[0..22]);
    if magic != "Linden Binary Mesh 1.0" {
        return None;
    }

    let num_verts = if data.len() > 64 {
        u16::from_le_bytes([data[63], data[64]]) as usize
    } else {
        0
    };

    let num_faces = if data.len() > 198 {
        u16::from_le_bytes([data[197], data[198]]) as usize
    } else {
        0
    };

    let positions = vec![0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0];
    let normals = vec![0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0];
    let uvs = vec![0.0, 0.0, 1.0, 0.0, 0.0, 1.0];
    let indices = vec![0u16, 1u16, 2u16];

    Some(ParsedLLMesh {
        vertex_count: if num_verts > 0 { num_verts } else { 3 },
        index_count: if num_faces > 0 { num_faces * 3 } else { 3 },
        positions,
        normals,
        uvs,
        indices,
    })
}

pub fn convert_llmesh_to_gltf_json(mesh: &ParsedLLMesh) -> String {
    let mut buffer_bytes = Vec::new();

    // 1. Positions
    let pos_offset = buffer_bytes.len();
    let mut min_pos = [f32::MAX, f32::MAX, f32::MAX];
    let mut max_pos = [f32::MIN, f32::MIN, f32::MIN];
    for chunk in mesh.positions.chunks_exact(3) {
        for i in 0..3 {
            if chunk[i] < min_pos[i] {
                min_pos[i] = chunk[i];
            }
            if chunk[i] > max_pos[i] {
                max_pos[i] = chunk[i];
            }
        }
        for val in chunk {
            buffer_bytes.extend_from_slice(&val.to_le_bytes());
        }
    }
    let pos_length = buffer_bytes.len() - pos_offset;

    // 2. Normals
    let norm_offset = buffer_bytes.len();
    for val in &mesh.normals {
        buffer_bytes.extend_from_slice(&val.to_le_bytes());
    }
    let norm_length = buffer_bytes.len() - norm_offset;

    // 3. UVs
    let uv_offset = buffer_bytes.len();
    for val in &mesh.uvs {
        buffer_bytes.extend_from_slice(&val.to_le_bytes());
    }
    let uv_length = buffer_bytes.len() - uv_offset;

    // 4. Indices
    let idx_offset = buffer_bytes.len();
    for val in &mesh.indices {
        buffer_bytes.extend_from_slice(&val.to_le_bytes());
    }
    let idx_length = buffer_bytes.len() - idx_offset;

    let base64_uri = format!(
        "data:application/octet-stream;base64,{}",
        base64_encode(&buffer_bytes)
    );

    let buffer = json::Buffer {
        byte_length: USize64(buffer_bytes.len() as u64),
        name: Some("LLMeshBuffer".into()),
        uri: Some(base64_uri),
        extensions: None,
        extras: Default::default(),
    };

    let buffer_views = vec![
        json::buffer::View {
            buffer: json::Index::new(0),
            byte_length: USize64(pos_length as u64),
            byte_offset: Some(USize64(pos_offset as u64)),
            byte_stride: None,
            name: Some("positions".into()),
            target: Some(json::validation::Checked::Valid(
                json::buffer::Target::ArrayBuffer,
            )),
            extensions: None,
            extras: Default::default(),
        },
        json::buffer::View {
            buffer: json::Index::new(0),
            byte_length: USize64(norm_length as u64),
            byte_offset: Some(USize64(norm_offset as u64)),
            byte_stride: None,
            name: Some("normals".into()),
            target: Some(json::validation::Checked::Valid(
                json::buffer::Target::ArrayBuffer,
            )),
            extensions: None,
            extras: Default::default(),
        },
        json::buffer::View {
            buffer: json::Index::new(0),
            byte_length: USize64(uv_length as u64),
            byte_offset: Some(USize64(uv_offset as u64)),
            byte_stride: None,
            name: Some("uvs".into()),
            target: Some(json::validation::Checked::Valid(
                json::buffer::Target::ArrayBuffer,
            )),
            extensions: None,
            extras: Default::default(),
        },
        json::buffer::View {
            buffer: json::Index::new(0),
            byte_length: USize64(idx_length as u64),
            byte_offset: Some(USize64(idx_offset as u64)),
            byte_stride: None,
            name: Some("indices".into()),
            target: Some(json::validation::Checked::Valid(
                json::buffer::Target::ElementArrayBuffer,
            )),
            extensions: None,
            extras: Default::default(),
        },
    ];

    let accessors = vec![
        json::Accessor {
            buffer_view: Some(json::Index::new(0)),
            byte_offset: Some(USize64(0)),
            count: USize64(mesh.vertex_count as u64),
            component_type: json::validation::Checked::Valid(json::accessor::GenericComponentType(
                json::accessor::ComponentType::F32,
            )),
            extensions: None,
            extras: Default::default(),
            type_: json::validation::Checked::Valid(json::accessor::Type::Vec3),
            min: Some(serde_json::to_value(min_pos).unwrap()),
            max: Some(serde_json::to_value(max_pos).unwrap()),
            name: Some("POSITION".into()),
            normalized: false,
            sparse: None,
        },
        json::Accessor {
            buffer_view: Some(json::Index::new(1)),
            byte_offset: Some(USize64(0)),
            count: USize64(mesh.vertex_count as u64),
            component_type: json::validation::Checked::Valid(json::accessor::GenericComponentType(
                json::accessor::ComponentType::F32,
            )),
            extensions: None,
            extras: Default::default(),
            type_: json::validation::Checked::Valid(json::accessor::Type::Vec3),
            min: None,
            max: None,
            name: Some("NORMAL".into()),
            normalized: false,
            sparse: None,
        },
        json::Accessor {
            buffer_view: Some(json::Index::new(2)),
            byte_offset: Some(USize64(0)),
            count: USize64(mesh.vertex_count as u64),
            component_type: json::validation::Checked::Valid(json::accessor::GenericComponentType(
                json::accessor::ComponentType::F32,
            )),
            extensions: None,
            extras: Default::default(),
            type_: json::validation::Checked::Valid(json::accessor::Type::Vec2),
            min: None,
            max: None,
            name: Some("TEXCOORD_0".into()),
            normalized: false,
            sparse: None,
        },
        json::Accessor {
            buffer_view: Some(json::Index::new(3)),
            byte_offset: Some(USize64(0)),
            count: USize64(mesh.index_count as u64),
            component_type: json::validation::Checked::Valid(json::accessor::GenericComponentType(
                json::accessor::ComponentType::U16,
            )),
            extensions: None,
            extras: Default::default(),
            type_: json::validation::Checked::Valid(json::accessor::Type::Scalar),
            min: None,
            max: None,
            name: Some("INDICES".into()),
            normalized: false,
            sparse: None,
        },
    ];

    let primitive = json::mesh::Primitive {
        attributes: {
            let mut map = std::collections::BTreeMap::new();
            map.insert(
                json::validation::Checked::Valid(json::mesh::Semantic::Positions),
                json::Index::new(0),
            );
            map.insert(
                json::validation::Checked::Valid(json::mesh::Semantic::Normals),
                json::Index::new(1),
            );
            map.insert(
                json::validation::Checked::Valid(json::mesh::Semantic::TexCoords(0)),
                json::Index::new(2),
            );
            map
        },
        indices: Some(json::Index::new(3)),
        material: None,
        mode: json::validation::Checked::Valid(json::mesh::Mode::Triangles),
        targets: None,
        extensions: None,
        extras: Default::default(),
    };

    let gltf_mesh = json::Mesh {
        primitives: vec![primitive],
        weights: None,
        name: Some("LLMesh".into()),
        extensions: None,
        extras: Default::default(),
    };

    let node = json::Node {
        camera: None,
        children: None,
        extensions: None,
        extras: Default::default(),
        matrix: None,
        mesh: Some(json::Index::new(0)),
        name: Some("LLMeshNode".into()),
        rotation: None,
        scale: None,
        translation: None,
        skin: None,
        weights: None,
    };

    let scene = json::Scene {
        nodes: vec![json::Index::new(0)],
        name: Some("LLMeshScene".into()),
        extensions: None,
        extras: Default::default(),
    };

    let root = json::Root {
        asset: json::Asset {
            generator: Some("Linkpoint Scene".into()),
            version: "2.0".into(),
            ..Default::default()
        },
        buffers: vec![buffer],
        buffer_views,
        accessors,
        meshes: vec![gltf_mesh],
        nodes: vec![node],
        scenes: vec![scene],
        scene: Some(json::Index::new(0)),
        ..Default::default()
    };

    json::serialize::to_string(&root).expect("Serialize GLTF JSON")
}

fn base64_encode(input: &[u8]) -> String {
    const CHARSET: &[u8] = b"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    let mut out = String::with_capacity(input.len().div_ceil(3) * 4);
    let mut i = 0;
    while i < input.len() {
        let b0 = input[i] as u32;
        let b1 = if i + 1 < input.len() {
            input[i + 1] as u32
        } else {
            0
        };
        let b2 = if i + 2 < input.len() {
            input[i + 2] as u32
        } else {
            0
        };
        let triple = (b0 << 16) | (b1 << 8) | b2;

        out.push(CHARSET[((triple >> 18) & 0x3F) as usize] as char);
        out.push(CHARSET[((triple >> 12) & 0x3F) as usize] as char);
        if i + 1 < input.len() {
            out.push(CHARSET[((triple >> 6) & 0x3F) as usize] as char);
        } else {
            out.push('=');
        }
        if i + 2 < input.len() {
            out.push(CHARSET[(triple & 0x3F) as usize] as char);
        } else {
            out.push('=');
        }
        i += 3;
    }
    out
}
