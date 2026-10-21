//! Asset scheduling and decoding boundary.

pub mod generated;

use flatbuffers::FlatBufferBuilder;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum TextureChannel {
    Albedo,
    Normal,
    MetallicRoughness,
    Emissive,
    Occlusion,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum AssetKind {
    Texture,
    MaterialTexture(TextureChannel),
    Material,
    Mesh,
    Animation,
    Terrain,
    Sound,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct AssetRequest {
    pub id: String,
    pub kind: AssetKind,
    pub priority: u8,
    pub grid_uri: Option<String>,
}

#[derive(Debug, Default)]
pub struct AssetScheduler {
    pending: Vec<AssetRequest>,
}

impl AssetScheduler {
    pub fn enqueue(&mut self, request: AssetRequest) {
        if !self
            .pending
            .iter()
            .any(|item| item.id == request.id && item.grid_uri == request.grid_uri)
        {
            self.pending.push(request);
            self.pending
                .sort_by_key(|item| std::cmp::Reverse(item.priority));
        }
    }
    pub fn pop_next(&mut self) -> Option<AssetRequest> {
        (!self.pending.is_empty()).then(|| self.pending.remove(0))
    }
    pub fn len(&self) -> usize {
        self.pending.len()
    }
    pub fn is_empty(&self) -> bool {
        self.pending.is_empty()
    }
}

/// Type-safe FlatBuffers decoder for binary mesh assets.
pub struct MeshAssetDecoder;

impl MeshAssetDecoder {
    pub fn decode(
        bytes: &[u8],
    ) -> Result<generated::MeshBlock<'_>, flatbuffers::InvalidFlatbuffer> {
        flatbuffers::root::<generated::MeshBlock>(bytes)
    }

    pub fn encode_simple(lod: generated::DetailLevel, lod_key: Option<&str>) -> Vec<u8> {
        let mut builder = FlatBufferBuilder::new();
        let lod_key_offset = lod_key.map(|k| builder.create_string(k));
        let args = generated::MeshBlockArgs {
            lod,
            lod_key: lod_key_offset,
            ..Default::default()
        };
        let block = generated::MeshBlock::create(&mut builder, &args);
        builder.finish(block, None);
        builder.finished_data().to_vec()
    }
}

/// Type-safe FlatBuffers decoder for binary animation assets.
pub struct AnimationAssetDecoder;

impl AnimationAssetDecoder {
    pub fn decode(
        bytes: &[u8],
    ) -> Result<generated::AssetMetadata<'_>, flatbuffers::InvalidFlatbuffer> {
        flatbuffers::root::<generated::AssetMetadata>(bytes)
    }

    pub fn encode(max_joints: u32, joint_sentinel: u8) -> Vec<u8> {
        let mut builder = FlatBufferBuilder::new();
        let args = generated::AssetMetadataArgs {
            max_rigged_mesh_joints: max_joints,
            joint_sentinel,
        };
        let meta = generated::AssetMetadata::create(&mut builder, &args);
        builder.finish(meta, None);
        builder.finished_data().to_vec()
    }
}

/// Type-safe FlatBuffers decoder for binary terrain assets.
pub struct TerrainAssetDecoder;

impl TerrainAssetDecoder {
    pub fn decode(
        bytes: &[u8],
    ) -> Result<generated::GLTFMaterial<'_>, flatbuffers::InvalidFlatbuffer> {
        flatbuffers::root::<generated::GLTFMaterial>(bytes)
    }

    pub fn encode(
        alpha_mode: generated::AlphaMode,
        alpha_cutoff: f32,
        double_sided: bool,
    ) -> Vec<u8> {
        let mut builder = FlatBufferBuilder::new();
        let args = generated::GLTFMaterialArgs {
            alpha_mode,
            alpha_cutoff,
            double_sided,
            ..Default::default()
        };
        let mat = generated::GLTFMaterial::create(&mut builder, &args);
        builder.finish(mat, None);
        builder.finished_data().to_vec()
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn deduplicates_and_prioritizes_assets() {
        let mut scheduler = AssetScheduler::default();
        scheduler.enqueue(AssetRequest {
            id: "far".into(),
            kind: AssetKind::Texture,
            priority: 1,
            grid_uri: None,
        });
        scheduler.enqueue(AssetRequest {
            id: "near".into(),
            kind: AssetKind::Mesh,
            priority: 9,
            grid_uri: None,
        });
        scheduler.enqueue(AssetRequest {
            id: "near".into(),
            kind: AssetKind::Mesh,
            priority: 9,
            grid_uri: None,
        });
        assert_eq!(scheduler.len(), 2);
        assert_eq!(scheduler.pop_next().unwrap().id, "near");
    }

    #[test]
    fn schedules_multichannel_pbr_material_textures() {
        let mut scheduler = AssetScheduler::default();
        scheduler.enqueue(AssetRequest {
            id: "mat_1".into(),
            kind: AssetKind::Material,
            priority: 10,
            grid_uri: None,
        });
        scheduler.enqueue(AssetRequest {
            id: "normal_map".into(),
            kind: AssetKind::MaterialTexture(TextureChannel::Normal),
            priority: 8,
            grid_uri: None,
        });
        assert_eq!(scheduler.len(), 2);
        let req1 = scheduler.pop_next().unwrap();
        assert_eq!(req1.id, "mat_1");
        assert_eq!(req1.kind, AssetKind::Material);
        let req2 = scheduler.pop_next().unwrap();
        assert_eq!(req2.id, "normal_map");
        assert_eq!(
            req2.kind,
            AssetKind::MaterialTexture(TextureChannel::Normal)
        );
    }

    #[test]
    fn decodes_mesh_animation_and_terrain_flatbuffers_payloads() {
        // 1. Mesh decoder test
        let mesh_bytes =
            MeshAssetDecoder::encode_simple(generated::DetailLevel::HIGH, Some("high_lod"));
        let decoded_mesh = MeshAssetDecoder::decode(&mesh_bytes).expect("Valid mesh payload");
        assert_eq!(decoded_mesh.lod(), generated::DetailLevel::HIGH);
        assert_eq!(decoded_mesh.lod_key(), Some("high_lod"));

        // 2. Animation metadata decoder test
        let anim_bytes = AnimationAssetDecoder::encode(256, 0xFF);
        let decoded_anim = AnimationAssetDecoder::decode(&anim_bytes).expect("Valid anim payload");
        assert_eq!(decoded_anim.max_rigged_mesh_joints(), 256);
        assert_eq!(decoded_anim.joint_sentinel(), 0xFF);

        // 3. Terrain GLTF material decoder test
        let terrain_bytes = TerrainAssetDecoder::encode(generated::AlphaMode::BLEND, 0.75, true);
        let decoded_terrain =
            TerrainAssetDecoder::decode(&terrain_bytes).expect("Valid terrain payload");
        assert_eq!(decoded_terrain.alpha_mode(), generated::AlphaMode::BLEND);
        assert_eq!(decoded_terrain.alpha_cutoff(), 0.75);
        assert!(decoded_terrain.double_sided());
    }
}
