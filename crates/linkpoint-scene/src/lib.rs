//! Renderer-neutral scene representation and normalization.

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
    }
}
