//! Bitfield flags and binary decoders for RegionHandshake and ObjectUpdate.

pub struct RegionFlags;

impl RegionFlags {
    pub const ALLOW_YOURSELF: u32 = 0x00000001;
    pub const ALLOW_OTHER_SCRIPTS: u32 = 0x00000002;
    pub const ALLOW_PHYSICS: u32 = 0x00000004;
    pub const ALLOW_DAMAGE: u32 = 0x00000008;
    pub const BLOCK_FLY: u32 = 0x00000010;
    pub const ALLOW_DIRECT_TELEPORT: u32 = 0x00000020;
    pub const RESTRICT_PUSHOBJECT: u32 = 0x00000040;
    pub const RESTRICTED_PARCEL: u32 = 0x00000080;
    pub const VOICE_ENABLED: u32 = 0x00000100;
    pub const BLOCK_DIRT_ENCODING: u32 = 0x00000200;
    pub const ALLOW_TERRAIN_EDIT: u32 = 0x00000400;

    pub fn has_flag(flags: u32, flag: u32) -> bool {
        (flags & flag) != 0
    }
}

pub struct ObjectFlags;

impl ObjectFlags {
    pub const NONE: u32 = 0x00000000;
    pub const USE_PHYSICS: u32 = 0x00000001;
    pub const CREATE_SELECTED: u32 = 0x00000002;
    pub const SCRIPTED: u32 = 0x00000004;
    pub const TOUCH: u32 = 0x00000008;
    pub const TEMPORARY: u32 = 0x00000010;
    pub const PHANTOM: u32 = 0x00000020;
    pub const INVENTORY_EMPTY: u32 = 0x00000040;
    pub const CHARACTER: u32 = 0x00000080;
    pub const JOINT_HINGE: u32 = 0x00000100;
    pub const JOINT_POINT: u32 = 0x00000200;
    pub const JOINT_BALL: u32 = 0x00000400;
    pub const TEMPORARY_ON_REZ: u32 = 0x00000800;
    pub const ALLOW_INVENTORY_DROP: u32 = 0x00001000;
    pub const ANIMO: u32 = 0x00002000;
    pub const CAST_SHADOWS: u32 = 0x00004000;

    pub fn has_flag(flags: u32, flag: u32) -> bool {
        (flags & flag) != 0
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct RegionHandshakeData {
    pub region_flags: u32,
    pub sim_access: u8,
    pub sim_name: String,
    pub sim_owner_id: [u8; 16],
}

pub struct RegionHandshakeDecoder;

impl RegionHandshakeDecoder {
    pub fn decode(bytes: &[u8]) -> Option<RegionHandshakeData> {
        if bytes.len() < 22 {
            return None;
        }

        let region_flags = u32::from_le_bytes(bytes[0..4].try_into().ok()?);
        let sim_access = bytes[4];

        let mut offset = 5;
        let mut sim_name_bytes = Vec::new();
        while offset < bytes.len() && bytes[offset] != 0 {
            sim_name_bytes.push(bytes[offset]);
            offset += 1;
        }
        if offset < bytes.len() && bytes[offset] == 0 {
            offset += 1; // skip null byte
        }

        if bytes.len() < offset + 16 {
            return None;
        }

        let sim_name = String::from_utf8_lossy(&sim_name_bytes).to_string();
        let sim_owner_id: [u8; 16] = bytes[offset..offset + 16].try_into().ok()?;

        Some(RegionHandshakeData {
            region_flags,
            sim_access,
            sim_name,
            sim_owner_id,
        })
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct ObjectUpdateData {
    pub local_id: u32,
    pub state: u8,
    pub full_id: [u8; 16],
    pub p_code: u8,
    pub material: u8,
    pub click_action: u8,
    pub scale: [f32; 3],
    pub position: [f32; 3],
    pub rotation: [f32; 4],
    pub flags: u32,
}

pub struct ObjectUpdateDecoder;

impl ObjectUpdateDecoder {
    pub fn decode(bytes: &[u8]) -> Option<ObjectUpdateData> {
        if bytes.len() < 76 {
            return None;
        }

        let local_id = u32::from_le_bytes(bytes[0..4].try_into().ok()?);
        let state = bytes[4];
        let full_id: [u8; 16] = bytes[5..21].try_into().ok()?;
        let p_code = bytes[21];
        let material = bytes[22];
        let click_action = bytes[23];

        let scale = [
            f32::from_le_bytes(bytes[24..28].try_into().ok()?),
            f32::from_le_bytes(bytes[28..32].try_into().ok()?),
            f32::from_le_bytes(bytes[32..36].try_into().ok()?),
        ];

        let position = [
            f32::from_le_bytes(bytes[36..40].try_into().ok()?),
            f32::from_le_bytes(bytes[40..44].try_into().ok()?),
            f32::from_le_bytes(bytes[44..48].try_into().ok()?),
        ];

        let rotation = [
            f32::from_le_bytes(bytes[48..52].try_into().ok()?),
            f32::from_le_bytes(bytes[52..56].try_into().ok()?),
            f32::from_le_bytes(bytes[56..60].try_into().ok()?),
            f32::from_le_bytes(bytes[60..64].try_into().ok()?),
        ];

        let flags = u32::from_le_bytes(bytes[64..68].try_into().ok()?);

        Some(ObjectUpdateData {
            local_id,
            state,
            full_id,
            p_code,
            material,
            click_action,
            scale,
            position,
            rotation,
            flags,
        })
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_region_handshake_decoder() {
        let mut bytes = Vec::new();
        bytes.extend(0x00000005u32.to_le_bytes()); // RegionFlags (ALLOW_YOURSELF | ALLOW_PHYSICS)
        bytes.push(21); // SimAccess
        bytes.extend(b"Aharon\0"); // SimName
        bytes.extend([1u8; 16]); // SimOwnerID

        let decoded = RegionHandshakeDecoder::decode(&bytes).unwrap();
        assert_eq!(decoded.region_flags, 5);
        assert_eq!(decoded.sim_access, 21);
        assert_eq!(decoded.sim_name, "Aharon");
        assert_eq!(decoded.sim_owner_id, [1u8; 16]);
        assert!(RegionFlags::has_flag(decoded.region_flags, RegionFlags::ALLOW_YOURSELF));
        assert!(RegionFlags::has_flag(decoded.region_flags, RegionFlags::ALLOW_PHYSICS));
    }
}
