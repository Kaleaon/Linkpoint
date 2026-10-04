use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct J2KHeaderInfo {
    pub width: u32,
    pub height: u32,
    pub components: u32,
}

pub fn parse_j2k_header(data: &[u8]) -> Option<J2KHeaderInfo> {
    if data.len() < 12 {
        return None;
    }

    // Check for JP2 file format signature
    if data[0] == 0x00
        && data[1] == 0x00
        && data[2] == 0x00
        && data[3] == 0x0C
        && data[4] == 0x6A
        && data[5] == 0x50
        && data[6] == 0x20
        && data[7] == 0x20
    {
        let mut pos = 0;
        while pos + 8 <= data.len() {
            let box_len = u32::from_be_bytes([data[pos], data[pos + 1], data[pos + 2], data[pos + 3]]) as usize;
            let box_type = u32::from_be_bytes([data[pos + 4], data[pos + 5], data[pos + 6], data[pos + 7]]);

            if box_type == 0x69686472 && pos + 16 <= data.len() {
                // 'ihdr'
                let height = u32::from_be_bytes([data[pos + 8], data[pos + 9], data[pos + 10], data[pos + 11]]);
                let width = u32::from_be_bytes([data[pos + 12], data[pos + 13], data[pos + 14], data[pos + 15]]);
                let components = if pos + 18 <= data.len() {
                    u16::from_be_bytes([data[pos + 16], data[pos + 17]]) as u32
                } else {
                    4
                };
                if width > 0 && height > 0 {
                    return Some(J2KHeaderInfo { width, height, components });
                }
            }

            if box_len < 8 {
                break;
            }
            pos += box_len;
        }
    }

    // Check for J2K raw codestream SOC marker (0xFF4F)
    if data[0] == 0xFF && data[1] == 0x4F {
        let mut pos = 2;
        while pos + 22 <= data.len() {
            if data[pos] == 0xFF && data[pos + 1] == 0x51 {
                // SIZ marker
                let xsiz = u32::from_be_bytes([data[pos + 6], data[pos + 7], data[pos + 8], data[pos + 9]]);
                let ysiz = u32::from_be_bytes([data[pos + 10], data[pos + 11], data[pos + 12], data[pos + 13]]);
                let xosiz = u32::from_be_bytes([data[pos + 14], data[pos + 15], data[pos + 16], data[pos + 17]]);
                let yosiz = u32::from_be_bytes([data[pos + 18], data[pos + 19], data[pos + 20], data[pos + 21]]);
                let width = xsiz.saturating_sub(xosiz);
                let height = ysiz.saturating_sub(yosiz);
                let components = if pos + 38 <= data.len() {
                    u16::from_be_bytes([data[pos + 36], data[pos + 37]]) as u32
                } else {
                    4
                };
                if width > 0 && height > 0 {
                    return Some(J2KHeaderInfo { width, height, components });
                }
            }
            pos += 1;
        }
    }

    None
}

pub fn calculate_discard_level(width: u32, height: u32, target_max_dim: u32) -> u32 {
    let max_dim = width.max(height);
    if max_dim <= target_max_dim {
        return 0;
    }
    let mut discard = 0;
    let mut current = max_dim;
    while current > target_max_dim && discard < 5 {
        current >>= 1;
        discard += 1;
    }
    discard
}

pub fn generate_placeholder_rgba(width: u32, height: u32) -> Vec<u8> {
    let w = width.clamp(1, 2048) as usize;
    let h = height.clamp(1, 2048) as usize;
    let mut pixels = vec![0u8; w * h * 4];
    let grid_step = 16;

    for y in 0..h {
        let is_grid_row = y % grid_step == 0;
        let row_offset = y * w * 4;
        for x in 0..w {
            let offset = row_offset + x * 4;
            if is_grid_row || (x % grid_step == 0) {
                pixels[offset] = 0x66;
                pixels[offset + 1] = 0x66;
                pixels[offset + 2] = 0x66;
                pixels[offset + 3] = 0xFF;
            } else {
                pixels[offset] = 0x80;
                pixels[offset + 1] = 0x80;
                pixels[offset + 2] = 0x80;
                pixels[offset + 3] = 0xFF;
            }
        }
    }
    pixels
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_calculate_discard_level() {
        assert_eq!(calculate_discard_level(1024, 1024, 64), 4);
        assert_eq!(calculate_discard_level(64, 64, 64), 0);
        assert_eq!(calculate_discard_level(512, 256, 128), 2);
    }

    #[test]
    fn test_generate_placeholder_rgba() {
        let pixels = generate_placeholder_rgba(16, 16);
        assert_eq!(pixels.len(), 16 * 16 * 4);
    }

    #[test]
    fn test_parse_j2k_header_invalid() {
        assert!(parse_j2k_header(&[]).is_none());
        assert!(parse_j2k_header(&[0; 10]).is_none());
    }
}
