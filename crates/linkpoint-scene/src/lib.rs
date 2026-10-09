//! Renderer-neutral scene representation and normalization.

pub mod decoder;
pub mod math;
pub mod spatial;
pub mod volume;
pub mod wasm;

pub use decoder::{
    J2KHeaderInfo, ParsedMeshHeader, calculate_discard_level, calculate_projected_pixel_coverage,
    generate_placeholder_rgba, parse_binary_mesh_header, parse_j2k_header, select_lod,
};
pub use math::{EulerAngles, Matrix4, Quaternion};
pub use spatial::{
    AABB, ChunkGrid, ChunkId, Octree, SpatialChunk, SpatialEntity, SpatialManager,
    SpatialWorkerPool,
};
pub use volume::{VolumeFace, VolumeParams, generate_volume};

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Transform {
    pub position: [f32; 3],
    pub rotation: [f32; 4],
    pub scale: [f32; 3],
}
impl Default for Transform {
    fn default() -> Self {
        Self {
            position: [0.0; 3],
            rotation: [0.0, 0.0, 0.0, 1.0],
            scale: [1.0; 3],
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Material {
    pub base_color_texture: Option<String>,
    pub normal_texture: Option<String>,
    pub alpha_mode: AlphaMode,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize, Default)]
#[serde(rename_all = "kebab-case")]
pub enum AlphaMode {
    #[default]
    Opaque,
    Mask,
    Blend,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SceneEntity {
    pub id: String,
    pub parent_id: Option<String>,
    pub transform: Transform,
    pub kind: EntityKind,
    pub materials: Vec<Material>,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "kebab-case")]
pub enum EntityKind {
    Primitive,
    Mesh,
    Avatar,
    Attachment,
    Terrain,
    Water,
    Light,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum SceneDelta {
    #[serde(rename = "scene.snapshot")]
    Snapshot(Vec<SceneEntity>),
    #[serde(rename = "scene.upsert")]
    Upsert(SceneEntity),
    #[serde(rename = "scene.remove")]
    Remove { id: String },
}

#[derive(Debug, Clone, PartialEq)]
pub struct SimulatorObject {
    pub id: String,
    pub parent_id: Option<String>,
    pub position: [f32; 3],
    pub rotation: Option<[f32; 4]>,
    pub scale: Option<[f32; 3]>,
    pub mesh_asset: Option<String>,
}

impl SceneEntity {
    pub fn from_binary_mesh_header(id: impl Into<String>, header_data: &[u8], transform: Transform) -> Self {
        let (kind, materials) = if let Ok(header) = parse_binary_mesh_header(header_data) {
            (EntityKind::Mesh, header.materials)
        } else {
            (EntityKind::Mesh, vec![])
        };
        Self {
            id: id.into(),
            parent_id: None,
            transform,
            kind,
            materials,
        }
    }
}

impl From<SimulatorObject> for SceneEntity {
    fn from(object: SimulatorObject) -> Self {
        Self {
            id: object.id,
            parent_id: object.parent_id,
            transform: Transform {
                position: object.position,
                rotation: object.rotation.unwrap_or([0.0, 0.0, 0.0, 1.0]),
                scale: object.scale.unwrap_or([1.0; 3]),
            },
            kind: if object.mesh_asset.is_some() {
                EntityKind::Mesh
            } else {
                EntityKind::Primitive
            },
            materials: vec![],
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn normalizes_missing_simulator_transform_fields() {
        let default_tf = Transform::default();
        assert_eq!(default_tf.position, [0.0; 3]);
        assert_eq!(default_tf.rotation, [0.0, 0.0, 0.0, 1.0]);
        assert_eq!(default_tf.scale, [1.0; 3]);

        assert_eq!(AlphaMode::default(), AlphaMode::Opaque);

        let scene = SceneEntity::from(SimulatorObject {
            id: "1".into(),
            parent_id: None,
            position: [1.0, 2.0, 3.0],
            rotation: None,
            scale: None,
            mesh_asset: None,
        });
        assert_eq!(scene.transform.rotation, [0.0, 0.0, 0.0, 1.0]);
        assert_eq!(scene.transform.scale, [1.0; 3]);
        assert_eq!(scene.kind, EntityKind::Primitive);

        let mesh_scene = SceneEntity::from(SimulatorObject {
            id: "2".into(),
            parent_id: Some("1".into()),
            position: [1.0, 2.0, 3.0],
            rotation: Some([0.0, 0.0, 0.0, 1.0]),
            scale: Some([2.0, 2.0, 2.0]),
            mesh_asset: Some("asset_uuid".into()),
        });
        assert_eq!(mesh_scene.kind, EntityKind::Mesh);
    }
}
