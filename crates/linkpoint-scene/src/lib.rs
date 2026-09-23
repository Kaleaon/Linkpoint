//! Renderer-neutral scene representation.

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SceneEntity {
    pub id: String,
    pub parent_id: Option<String>,
    pub position: [f32; 3],
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum SceneDelta {
    #[serde(rename = "scene.upsert")]
    Upsert(SceneEntity),
    #[serde(rename = "scene.remove")]
    Remove { id: String },
}
