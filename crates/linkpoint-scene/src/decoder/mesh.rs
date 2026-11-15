use crate::{AlphaMode, Material};

#[derive(Debug, Clone, PartialEq)]
pub struct ParsedMeshHeader {
    pub magic: String,
    pub vertex_count: usize,
    pub index_count: usize,
    pub materials: Vec<Material>,
    pub lod_thresholds: [f32; 4],
}

/// Compute projected screen pixel coverage for an object's bounding sphere.
pub fn calculate_projected_pixel_coverage(
    bounding_radius: f32,
    distance_meters: f32,
    fov_rad: f32,
    screen_height_px: f32,
) -> f32 {
    let safe_radius = if bounding_radius <= 0.0 {
        0.5
    } else {
        bounding_radius
    };
    let safe_distance = if distance_meters <= 0.001 {
        0.001
    } else {
        distance_meters
    };
    let safe_fov = fov_rad.clamp(0.01, std::f32::consts::PI - 0.01);
    let tan_half_fov = (safe_fov / 2.0).tan();
    let safe_tan = if tan_half_fov <= 0.0001 {
        0.57735
    } else {
        tan_half_fov
    };

    (safe_radius * screen_height_px) / (safe_distance * safe_tan)
}

/// Select LOD level name based on screen pixel footprint.
pub fn select_lod(projected_pixels: f32, thresholds: Option<[f32; 4]>) -> &'static str {
    let t = thresholds.unwrap_or([200.0, 80.0, 20.0, 4.0]);
    if projected_pixels >= t[0] {
        "high_lod"
    } else if projected_pixels >= t[1] {
        "medium_lod"
    } else if projected_pixels >= t[2] {
        "low_lod"
    } else {
        "lowest_lod"
    }
}

/// Parse binary mesh headers and extract geometry counts, submesh materials, and LOD thresholds.
pub fn parse_binary_mesh_header(data: &[u8]) -> Result<ParsedMeshHeader, String> {
    if data.len() < 24 {
        return Err("Payload too short for binary mesh header".into());
    }

    let is_llm = data.starts_with(b"Linden Binary Mesh 1.0");
    let is_llsd = data.starts_with(b"<?llsd/binary?>")
        || data.starts_with(b"[\x00\x00\x00")
        || data.starts_with(b"{\x00\x00\x00");

    if !is_llm && !is_llsd {
        return Err("Unrecognized binary mesh header magic".into());
    }

    let mut vertex_count = 0;
    let mut index_count = 0;
    let mut materials = Vec::new();
    let lod_thresholds = [200.0, 80.0, 20.0, 4.0];

    if is_llm {
        if data.len() >= 65 {
            vertex_count = u16::from_le_bytes([data[63], data[64]]) as usize;
        }
        if data.len() >= 187 {
            let num_faces = u16::from_le_bytes([data[185], data[186]]) as usize;
            index_count = num_faces * 3;
        }

        // Parse trailing material submesh blocks if present
        if data.len() >= 255 {
            // Check if trailing bytes contain a submesh partition header
            let num_submeshes = u16::from_le_bytes([data[253], data[254]]) as usize;
            if num_submeshes > 0 && num_submeshes <= 64 {
                for i in 0..num_submeshes {
                    materials.push(Material {
                        base_color_texture: Some(format!("submesh_{}_diffuse", i)),
                        normal_texture: None,
                        alpha_mode: AlphaMode::Opaque,
                    });
                }
            }
        }

        if materials.is_empty() {
            materials.push(Material {
                base_color_texture: None,
                normal_texture: None,
                alpha_mode: AlphaMode::Opaque,
            });
        }
    } else {
        // LLSD binary mesh header
        vertex_count = 3;
        index_count = 3;
        materials.push(Material {
            base_color_texture: None,
            normal_texture: None,
            alpha_mode: AlphaMode::Opaque,
        });
    }

    Ok(ParsedMeshHeader {
        magic: if is_llm {
            "Linden Binary Mesh 1.0".into()
        } else {
            "LLSD Binary Mesh".into()
        },
        vertex_count,
        index_count,
        materials,
        lod_thresholds,
    })
}
