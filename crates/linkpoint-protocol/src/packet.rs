//! Common Second Life UDP packet framing and zero coding.

#[derive(Debug, Clone, Copy, Default, PartialEq, Eq)]
pub struct PacketFlags {
    pub zerocoded: bool,
    pub reliable: bool,
    pub resent: bool,
    pub appended_acks: bool,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum PacketFrequency {
    High,
    Medium,
    Low,
    Fixed,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct PacketHeader {
    pub flags: PacketFlags,
    pub sequence: u32,
    pub extra: Vec<u8>,
    pub frequency: PacketFrequency,
    pub message_id: u16,
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum PacketParseError {
    #[error("truncated packet header")]
    Truncated,
    #[error("invalid zero-coded payload")]
    InvalidZeroCode,
    #[error("packet extra-header length exceeds payload")]
    InvalidExtraHeader,
}

impl PacketHeader {
    pub fn decode(bytes: &[u8]) -> Result<(Self, usize), PacketParseError> {
        if bytes.len() < 7 {
            return Err(PacketParseError::Truncated);
        }
        let raw_flags = bytes[0];
        let extra_len = usize::from(bytes[5]);
        let mut offset = 6usize
            .checked_add(extra_len)
            .ok_or(PacketParseError::InvalidExtraHeader)?;
        if offset >= bytes.len() {
            return Err(PacketParseError::InvalidExtraHeader);
        }
        let extra = bytes[6..offset].to_vec();
        let first = take(bytes, &mut offset)?;
        let (frequency, message_id) = if first != 0xff {
            (PacketFrequency::High, u16::from(first))
        } else {
            let second = take(bytes, &mut offset)?;
            if second != 0xff {
                (PacketFrequency::Medium, u16::from(second))
            } else {
                let high = take(bytes, &mut offset)?;
                if high == 0xff {
                    (PacketFrequency::Fixed, u16::from(take(bytes, &mut offset)?))
                } else {
                    let low = take(bytes, &mut offset)?;
                    (PacketFrequency::Low, u16::from_be_bytes([high, low]))
                }
            }
        };
        Ok((
            Self {
                flags: PacketFlags {
                    zerocoded: raw_flags & 0x80 != 0,
                    reliable: raw_flags & 0x40 != 0,
                    resent: raw_flags & 0x20 != 0,
                    appended_acks: raw_flags & 0x10 != 0,
                },
                sequence: u32::from_be_bytes(
                    bytes[1..5]
                        .try_into()
                        .map_err(|_| PacketParseError::Truncated)?,
                ),
                extra,
                frequency,
                message_id,
            },
            offset,
        ))
    }

    pub fn encode(&self) -> Vec<u8> {
        let mut flags = 0;
        if self.flags.zerocoded {
            flags |= 0x80;
        }
        if self.flags.reliable {
            flags |= 0x40;
        }
        if self.flags.resent {
            flags |= 0x20;
        }
        if self.flags.appended_acks {
            flags |= 0x10;
        }
        let extra_len =
            u8::try_from(self.extra.len()).expect("extra packet header cannot exceed 255 bytes");
        let mut bytes = vec![flags];
        bytes.extend(self.sequence.to_be_bytes());
        bytes.push(extra_len);
        bytes.extend(&self.extra);
        match self.frequency {
            PacketFrequency::High => bytes.push(self.message_id as u8),
            PacketFrequency::Medium => bytes.extend([0xff, self.message_id as u8]),
            PacketFrequency::Low => {
                bytes.extend([0xff, 0xff]);
                bytes.extend(self.message_id.to_be_bytes());
            }
            PacketFrequency::Fixed => bytes.extend([0xff, 0xff, 0xff, self.message_id as u8]),
        }
        bytes
    }
}

fn take(bytes: &[u8], offset: &mut usize) -> Result<u8, PacketParseError> {
    let byte = bytes
        .get(*offset)
        .copied()
        .ok_or(PacketParseError::Truncated)?;
    *offset += 1;
    Ok(byte)
}

pub fn zero_decode(bytes: &[u8], limit: usize) -> Result<Vec<u8>, PacketParseError> {
    let mut output = Vec::with_capacity(bytes.len());
    let mut offset = 0;
    while offset < bytes.len() {
        let byte = bytes[offset];
        offset += 1;
        if byte == 0 {
            let count = bytes
                .get(offset)
                .copied()
                .ok_or(PacketParseError::InvalidZeroCode)?;
            offset += 1;
            if count == 0 || output.len().saturating_add(usize::from(count)) > limit {
                return Err(PacketParseError::InvalidZeroCode);
            }
            output.resize(output.len() + usize::from(count), 0);
        } else {
            if output.len() == limit {
                return Err(PacketParseError::InvalidZeroCode);
            }
            output.push(byte);
        }
    }
    Ok(output)
}

pub fn zero_encode(bytes: &[u8]) -> Vec<u8> {
    let mut output = Vec::with_capacity(bytes.len());
    let mut offset = 0;
    while offset < bytes.len() {
        if bytes[offset] != 0 {
            output.push(bytes[offset]);
            offset += 1;
            continue;
        }
        let start = offset;
        while offset < bytes.len() && bytes[offset] == 0 && offset - start < 255 {
            offset += 1;
        }
        output.extend([0, (offset - start) as u8]);
    }
    output
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn packet_header_round_trips_all_frequencies() {
        for (frequency, id) in [
            (PacketFrequency::High, 42),
            (PacketFrequency::Medium, 200),
            (PacketFrequency::Low, 0x1234),
            (PacketFrequency::Fixed, 5),
        ] {
            let header = PacketHeader {
                flags: PacketFlags {
                    reliable: true,
                    ..PacketFlags::default()
                },
                sequence: 0x01020304,
                extra: vec![],
                frequency,
                message_id: id,
            };
            let bytes = header.encode();
            assert_eq!(PacketHeader::decode(&bytes).unwrap().0, header);
        }
    }
    #[test]
    fn zero_codec_handles_long_runs_and_limits_expansion() {
        let mut input = vec![1];
        input.extend(vec![0; 300]);
        input.push(2);
        let encoded = zero_encode(&input);
        assert_eq!(zero_decode(&encoded, input.len()).unwrap(), input);
        assert_eq!(
            zero_decode(&encoded, 10),
            Err(PacketParseError::InvalidZeroCode)
        );
    }
}
