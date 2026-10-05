use crate::decoder::j2k::parse_j2k_header;
use crate::math::{Matrix4, Quaternion};
use crate::volume::{VolumeParams, generate_volume};
use wasm_bindgen::prelude::*;

#[wasm_bindgen]
pub struct WasmQuaternion {
    inner: Quaternion,
}

#[wasm_bindgen]
impl WasmQuaternion {
    #[wasm_bindgen(constructor)]
    pub fn new(x: f32, y: f32, z: f32, w: f32) -> WasmQuaternion {
        Self {
            inner: Quaternion::new(x, y, z, w),
        }
    }

    #[wasm_bindgen]
    pub fn identity() -> WasmQuaternion {
        Self {
            inner: Quaternion::identity(),
        }
    }

    #[wasm_bindgen]
    pub fn normalize(&mut self) -> f32 {
        self.inner.normalize()
    }

    #[wasm_bindgen]
    pub fn rotate_vector3(&self, x: f32, y: f32, z: f32) -> Vec<f32> {
        let r = self.inner.rotate_vector3([x, y, z]);
        vec![r[0], r[1], r[2]]
    }

    #[wasm_bindgen]
    pub fn slerp(&self, other: &WasmQuaternion, t: f32) -> WasmQuaternion {
        WasmQuaternion {
            inner: Quaternion::slerp(t, &self.inner, &other.inner),
        }
    }
}

#[wasm_bindgen]
pub struct WasmMatrix4 {
    inner: Matrix4,
}

#[wasm_bindgen]
impl WasmMatrix4 {
    #[wasm_bindgen(constructor)]
    pub fn identity() -> WasmMatrix4 {
        Self {
            inner: Matrix4::identity(),
        }
    }

    #[wasm_bindgen]
    pub fn from_quaternion(q: &WasmQuaternion) -> WasmMatrix4 {
        Self {
            inner: Matrix4::from_quaternion(&q.inner),
        }
    }

    #[wasm_bindgen]
    pub fn transform_point(&self, x: f32, y: f32, z: f32) -> Vec<f32> {
        let r = self.inner.transform_vector3([x, y, z]);
        vec![r[0], r[1], r[2]]
    }
}

#[wasm_bindgen]
pub fn wasm_generate_volume(params_json: &str, detail: f32) -> String {
    let params: VolumeParams = serde_json::from_str(params_json).unwrap_or_default();
    let faces = generate_volume(&params, detail);
    serde_json::to_string(&faces).unwrap_or_else(|_| "[]".to_string())
}

#[wasm_bindgen]
pub fn wasm_parse_j2k_header(data: &[u8]) -> Option<String> {
    parse_j2k_header(data).and_then(|info| serde_json::to_string(&info).ok())
}
