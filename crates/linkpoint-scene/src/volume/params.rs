use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct VolumeParams {
    pub path_curve: u8,
    pub profile_curve: u8,
    pub path_begin: f32,
    pub path_end: f32,
    pub path_scale_x: f32,
    pub path_scale_y: f32,
    pub path_shear_x: f32,
    pub path_shear_y: f32,
    pub path_twist: f32,
    pub path_twist_begin: f32,
    pub path_radius_offset: f32,
    pub path_taper_x: f32,
    pub path_taper_y: f32,
    pub path_revolutions: f32,
    pub path_skew: f32,
    pub profile_begin: f32,
    pub profile_end: f32,
    pub profile_hollow: f32,
}

impl Default for VolumeParams {
    fn default() -> Self {
        Self {
            path_curve: 0x10,    // PATH_LINE
            profile_curve: 0x01, // PROFILE_SQUARE
            path_begin: 0.0,
            path_end: 1.0,
            path_scale_x: 1.0,
            path_scale_y: 1.0,
            path_shear_x: 0.0,
            path_shear_y: 0.0,
            path_twist: 0.0,
            path_twist_begin: 0.0,
            path_radius_offset: 0.0,
            path_taper_x: 0.0,
            path_taper_y: 0.0,
            path_revolutions: 1.0,
            path_skew: 0.0,
            profile_begin: 0.0,
            profile_end: 1.0,
            profile_hollow: 0.0,
        }
    }
}

impl VolumeParams {
    pub fn cube() -> Self {
        Self::default()
    }

    pub fn sphere() -> Self {
        Self {
            profile_curve: 0x00, // PROFILE_CIRCLE
            path_curve: 0x20,    // PATH_CIRCLE
            ..Self::default()
        }
    }

    pub fn cylinder() -> Self {
        Self {
            profile_curve: 0x00, // PROFILE_CIRCLE
            path_curve: 0x10,    // PATH_LINE
            ..Self::default()
        }
    }
}
