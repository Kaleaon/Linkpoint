//! Second Life/OpenSimulator wire-protocol boundary.

/// Reliable packet sequencing is intentionally independent from sockets so it
/// can be driven by captured, sanitized Lumiya parity fixtures.
#[derive(Debug, Default)]
pub struct ReliableSequence {
    next: u32,
}

impl ReliableSequence {
    pub fn take(&mut self) -> u32 {
        let current = self.next;
        self.next = self.next.wrapping_add(1);
        current
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn sequence_wraps_as_the_udp_field_requires() {
        let mut sequence = ReliableSequence { next: u32::MAX };
        assert_eq!(sequence.take(), u32::MAX);
        assert_eq!(sequence.take(), 0);
    }
}
