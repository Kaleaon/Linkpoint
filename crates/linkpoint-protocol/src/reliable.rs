use std::collections::{BTreeMap, BTreeSet};
use std::time::{Duration, Instant};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReliablePacket {
    pub sequence: u32,
    pub payload: Vec<u8>,
    pub attempts: u8,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum AckResult {
    Acknowledged,
    Unknown,
}

#[derive(Debug)]
struct Pending {
    packet: ReliablePacket,
    sent_at: Instant,
}

/// Transport-independent reliable-UDP sequencing, duplicate suppression and
/// exponential retransmission scheduling.
#[derive(Debug)]
pub struct ReliableUdp {
    next: u32,
    pending: BTreeMap<u32, Pending>,
    received: BTreeSet<u32>,
    base_timeout: Duration,
    max_attempts: u8,
}

impl Default for ReliableUdp {
    fn default() -> Self {
        Self::new(Duration::from_millis(500), 5)
    }
}

impl ReliableUdp {
    pub fn new(base_timeout: Duration, max_attempts: u8) -> Self {
        Self {
            next: 0,
            pending: BTreeMap::new(),
            received: BTreeSet::new(),
            base_timeout,
            max_attempts,
        }
    }

    pub fn queue(&mut self, payload: Vec<u8>, now: Instant) -> ReliablePacket {
        let packet = ReliablePacket {
            sequence: self.next,
            payload,
            attempts: 1,
        };
        self.next = self.next.wrapping_add(1);
        self.pending.insert(
            packet.sequence,
            Pending {
                packet: packet.clone(),
                sent_at: now,
            },
        );
        packet
    }

    pub fn acknowledge(&mut self, sequence: u32) -> AckResult {
        if self.pending.remove(&sequence).is_some() {
            AckResult::Acknowledged
        } else {
            AckResult::Unknown
        }
    }

    /// Returns `true` once for a newly received reliable sequence and `false`
    /// for retransmitted duplicates. A bounded window avoids unbounded growth.
    pub fn accept_incoming(&mut self, sequence: u32) -> bool {
        let fresh = self.received.insert(sequence);
        while self.received.len() > 4096 {
            if let Some(oldest) = self.received.first().copied() {
                self.received.remove(&oldest);
            }
        }
        fresh
    }

    pub fn retransmit_due(&mut self, now: Instant) -> Vec<ReliablePacket> {
        let mut result = Vec::new();
        for pending in self.pending.values_mut() {
            let multiplier = 1u32 << u32::from(pending.packet.attempts.saturating_sub(1).min(8));
            if now.duration_since(pending.sent_at) >= self.base_timeout.saturating_mul(multiplier)
                && pending.packet.attempts < self.max_attempts
            {
                pending.packet.attempts += 1;
                pending.sent_at = now;
                result.push(pending.packet.clone());
            }
        }
        result
    }

    pub fn expired(&self) -> impl Iterator<Item = u32> + '_ {
        self.pending
            .values()
            .filter(|p| p.packet.attempts >= self.max_attempts)
            .map(|p| p.packet.sequence)
    }

    pub fn pending_count(&self) -> usize {
        self.pending.len()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn sequence_wraps_and_ack_removes_pending_packet() {
        let now = Instant::now();
        let mut udp = ReliableUdp {
            next: u32::MAX,
            ..ReliableUdp::default()
        };
        assert_eq!(udp.queue(vec![1], now).sequence, u32::MAX);
        assert_eq!(udp.queue(vec![2], now).sequence, 0);
        assert_eq!(udp.acknowledge(u32::MAX), AckResult::Acknowledged);
        assert_eq!(udp.pending_count(), 1);
    }

    #[test]
    fn retries_with_backoff_and_suppresses_duplicates() {
        let now = Instant::now();
        let mut udp = ReliableUdp::new(Duration::from_millis(100), 3);
        udp.queue(vec![7], now);
        assert!(
            udp.retransmit_due(now + Duration::from_millis(99))
                .is_empty()
        );
        assert_eq!(
            udp.retransmit_due(now + Duration::from_millis(100))[0].attempts,
            2
        );
        assert!(udp.accept_incoming(8));
        assert!(!udp.accept_incoming(8));
    }
}
