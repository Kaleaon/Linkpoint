//! Circuit activation messages used immediately after login.
//!
//! `UseCircuitCode` is deliberately kept as a small, deterministic codec. The
//! socket and retry policy remain the responsibility of the native adapter and
//! [`crate::reliable`] respectively.

use crate::packet::{PacketFlags, PacketFrequency, PacketHeader, PacketParseError};

pub const USE_CIRCUIT_CODE_MESSAGE_ID: u16 = 3;
const USE_CIRCUIT_CODE_BODY_LEN: usize = 36;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct UseCircuitCode {
    pub circuit_code: u32,
    pub session_id: [u8; 16],
    pub agent_id: [u8; 16],
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum CircuitCodecError {
    #[error(transparent)]
    Packet(#[from] PacketParseError),
    #[error("unexpected packet type for UseCircuitCode")]
    UnexpectedPacket,
    #[error("UseCircuitCode body must be exactly 36 bytes")]
    InvalidBodyLength,
}

impl UseCircuitCode {
    /// Encodes the fixed-size message body without UDP framing.
    pub fn encode_body(&self) -> [u8; USE_CIRCUIT_CODE_BODY_LEN] {
        let mut body = [0; USE_CIRCUIT_CODE_BODY_LEN];
        body[0..4].copy_from_slice(&self.circuit_code.to_le_bytes());
        body[4..20].copy_from_slice(&self.session_id);
        body[20..36].copy_from_slice(&self.agent_id);
        body
    }

    /// Decodes the fixed-size message body without UDP framing.
    pub fn decode_body(body: &[u8]) -> Result<Self, CircuitCodecError> {
        if body.len() != USE_CIRCUIT_CODE_BODY_LEN {
            return Err(CircuitCodecError::InvalidBodyLength);
        }

        let mut circuit_code = [0; 4];
        circuit_code.copy_from_slice(&body[0..4]);
        let mut session_id = [0; 16];
        session_id.copy_from_slice(&body[4..20]);
        let mut agent_id = [0; 16];
        agent_id.copy_from_slice(&body[20..36]);
        Ok(Self {
            circuit_code: u32::from_le_bytes(circuit_code),
            session_id,
            agent_id,
        })
    }

    /// Encodes the complete reliable, unencoded Low-frequency UDP packet.
    pub fn encode_packet(&self, sequence: u32) -> Vec<u8> {
        let header = PacketHeader {
            flags: PacketFlags {
                reliable: true,
                ..PacketFlags::default()
            },
            sequence,
            extra: Vec::new(),
            frequency: PacketFrequency::Low,
            message_id: USE_CIRCUIT_CODE_MESSAGE_ID,
        };
        let mut packet = header.encode();
        packet.extend(self.encode_body());
        packet
    }

    /// Decodes a complete packet and rejects alternate framing or trailing data.
    pub fn decode_packet(packet: &[u8]) -> Result<(Self, u32), CircuitCodecError> {
        let (header, body_offset) = PacketHeader::decode(packet)?;
        // A retransmission has the same body and sequence with `resent` set.
        // It is therefore valid input, unlike zero coding or appended ACKs.
        if header.frequency != PacketFrequency::Low
            || header.message_id != USE_CIRCUIT_CODE_MESSAGE_ID
            || !header.flags.reliable
            || header.flags.zerocoded
            || header.flags.appended_acks
            || !header.extra.is_empty()
        {
            return Err(CircuitCodecError::UnexpectedPacket);
        }
        Ok((Self::decode_body(&packet[body_offset..])?, header.sequence))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn fixture() -> UseCircuitCode {
        UseCircuitCode {
            circuit_code: 0x1234_5678,
            session_id: [
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                0x1e, 0x1f,
            ],
            agent_id: [
                0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad,
                0xae, 0xaf,
            ],
        }
    }

    #[test]
    fn use_circuit_code_matches_synthetic_golden_packet() {
        let expected = [
            0x40, 0x01, 0x02, 0x03, 0x04, 0x00, 0xff, 0xff, 0x00, 0x03, 0x78, 0x56, 0x34, 0x12,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
            0x1e, 0x1f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab,
            0xac, 0xad, 0xae, 0xaf,
        ];
        let message = fixture();
        assert_eq!(message.encode_packet(0x0102_0304), expected);
        assert_eq!(
            UseCircuitCode::decode_packet(&expected),
            Ok((message, 0x0102_0304))
        );
    }

    #[test]
    fn body_codec_is_available_without_packet_framing() {
        let message = fixture();
        let body = message.encode_body();
        assert_eq!(body.len(), USE_CIRCUIT_CODE_BODY_LEN);
        assert_eq!(UseCircuitCode::decode_body(&body), Ok(message));
    }

    #[test]
    fn use_circuit_code_rejects_truncation_and_trailing_data() {
        let packet = fixture().encode_packet(7);
        assert_eq!(
            UseCircuitCode::decode_packet(&packet[..packet.len() - 1]),
            Err(CircuitCodecError::InvalidBodyLength)
        );
        let mut extended = packet;
        extended.push(0);
        assert_eq!(
            UseCircuitCode::decode_packet(&extended),
            Err(CircuitCodecError::InvalidBodyLength)
        );
    }

    #[test]
    fn use_circuit_code_requires_reliable_unencoded_framing() {
        let mut packet = fixture().encode_packet(7);
        packet[0] = 0;
        assert_eq!(
            UseCircuitCode::decode_packet(&packet),
            Err(CircuitCodecError::UnexpectedPacket)
        );

        packet[0] = 0xc0;
        assert_eq!(
            UseCircuitCode::decode_packet(&packet),
            Err(CircuitCodecError::UnexpectedPacket)
        );
    }

    #[test]
    fn use_circuit_code_accepts_retransmission_framing() {
        let message = fixture();
        let mut packet = message.encode_packet(7);
        packet[0] |= 0x20;
        assert_eq!(UseCircuitCode::decode_packet(&packet), Ok((message, 7)));
    }
}
