//! Spatial vector codecs (Vector3U16, Vector3U8) and packed quaternion decoders.

#[derive(Debug, Clone, Copy, PartialEq)]
pub struct Vector3U16;

impl Vector3U16 {
    pub fn dequantize(u16_vec: [u16; 3], min: [f32; 3], max: [f32; 3]) -> [f32; 3] {
        [
            Self::dequantize_component(u16_vec[0], min[0], max[0]),
            Self::dequantize_component(u16_vec[1], min[1], max[1]),
            Self::dequantize_component(u16_vec[2], min[2], max[2]),
        ]
    }

    pub fn dequantize_default(u16_vec: [u16; 3]) -> [f32; 3] {
        Self::dequantize(u16_vec, [-128.0, -128.0, -128.0], [128.0, 128.0, 128.0])
    }

    pub fn quantize(vec: [f32; 3], min: [f32; 3], max: [f32; 3]) -> [u16; 3] {
        [
            Self::quantize_component(vec[0], min[0], max[0]),
            Self::quantize_component(vec[1], min[1], max[1]),
            Self::quantize_component(vec[2], min[2], max[2]),
        ]
    }

    pub fn quantize_default(vec: [f32; 3]) -> [u16; 3] {
        Self::quantize(vec, [-128.0, -128.0, -128.0], [128.0, 128.0, 128.0])
    }

    fn dequantize_component(val: u16, min: f32, max: f32) -> f32 {
        let norm = f64::from(val) / 65535.0;
        let range = f64::from(max) - f64::from(min);
        let result = norm * range + f64::from(min);
        if result.abs() < 1e-2 {
            0.0
        } else {
            ((result * 10000.0).round() / 10000.0) as f32
        }
    }

    fn quantize_component(val: f32, min: f32, max: f32) -> u16 {
        let clamped = val.clamp(min, max);
        let norm = (clamped - min) / (max - min);
        (norm * 65535.0).round() as u16
    }
}

#[derive(Debug, Clone, Copy, PartialEq)]
pub struct Vector3U8;

impl Vector3U8 {
    pub fn dequantize(u8_vec: [u8; 3], min: [f32; 3], max: [f32; 3]) -> [f32; 3] {
        [
            Self::dequantize_component(u8_vec[0], min[0], max[0]),
            Self::dequantize_component(u8_vec[1], min[1], max[1]),
            Self::dequantize_component(u8_vec[2], min[2], max[2]),
        ]
    }

    pub fn dequantize_default(u8_vec: [u8; 3]) -> [f32; 3] {
        Self::dequantize(u8_vec, [0.0, 0.0, 0.0], [255.0, 255.0, 255.0])
    }

    pub fn quantize(vec: [f32; 3], min: [f32; 3], max: [f32; 3]) -> [u8; 3] {
        [
            Self::quantize_component(vec[0], min[0], max[0]),
            Self::quantize_component(vec[1], min[1], max[1]),
            Self::quantize_component(vec[2], min[2], max[2]),
        ]
    }

    pub fn quantize_default(vec: [f32; 3]) -> [u8; 3] {
        Self::quantize(vec, [0.0, 0.0, 0.0], [255.0, 255.0, 255.0])
    }

    fn dequantize_component(val: u8, min: f32, max: f32) -> f32 {
        let norm = f64::from(val) / 255.0;
        let range = f64::from(max) - f64::from(min);
        let result = norm * range + f64::from(min);
        let rounded = (result * 10000.0).round() / 10000.0;
        rounded as f32
    }

    fn quantize_component(val: f32, min: f32, max: f32) -> u8 {
        let clamped = val.clamp(min, max);
        let norm = (clamped - min) / (max - min);
        (norm * 255.0).round() as u8
    }
}

#[derive(Debug, Clone, Copy, PartialEq)]
pub struct PackedQuaternion;

impl PackedQuaternion {
    pub fn unpack(i16_vec: [i16; 3]) -> [f32; 4] {
        let x = f64::from(i16_vec[0]) / 32767.0;
        let y = f64::from(i16_vec[1]) / 32767.0;
        let z = f64::from(i16_vec[2]) / 32767.0;

        let sum_sq = x * x + y * y + z * z;
        let w = if sum_sq < 1.0 { (1.0 - sum_sq).sqrt() } else { 0.0 };

        [
            ((x * 10000.0).round() / 10000.0) as f32,
            ((y * 10000.0).round() / 10000.0) as f32,
            ((z * 10000.0).round() / 10000.0) as f32,
            ((w * 10000.0).round() / 10000.0) as f32,
        ]
    }

    pub fn pack(quat: [f32; 4]) -> [i16; 3] {
        let (x, y, z, w) = (quat[0], quat[1], quat[2], quat[3]);
        let s = if w < 0.0 { -1.0 } else { 1.0 };
        [
            (x * s * 32767.0).round().clamp(-32767.0, 32767.0) as i16,
            (y * s * 32767.0).round().clamp(-32767.0, 32767.0) as i16,
            (z * s * 32767.0).round().clamp(-32767.0, 32767.0) as i16,
        ]
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_vector3_u16_dequantization() {
        let mid = Vector3U16::dequantize_default([32767, 32767, 32767]);
        assert_eq!(mid, [0.0, 0.0, 0.0]);

        let max = Vector3U16::dequantize_default([65535, 65535, 65535]);
        assert_eq!(max, [128.0, 128.0, 128.0]);
    }

    #[test]
    fn test_vector3_u8_dequantization() {
        let zero = Vector3U8::dequantize_default([0, 0, 0]);
        assert_eq!(zero, [0.0, 0.0, 0.0]);

        let max = Vector3U8::dequantize_default([255, 255, 255]);
        assert_eq!(max, [255.0, 255.0, 255.0]);
    }

    #[test]
    #[allow(clippy::approx_constant)]
    fn test_packed_quaternion() {
        let identity = PackedQuaternion::unpack([0, 0, 0]);
        assert_eq!(identity, [0.0, 0.0, 0.0, 1.0]);

        let rot = PackedQuaternion::unpack([0, 0, 23170]);
        assert_eq!(rot, [0.0, 0.0, 0.7071, 0.7071]);
    }
}
