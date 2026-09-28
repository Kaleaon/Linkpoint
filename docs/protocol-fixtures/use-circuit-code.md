# `UseCircuitCode` golden fixture

This fixture characterizes the first viewer-to-simulator UDP message after a
successful login. It is synthetic: the circuit code and UUID-shaped byte arrays
do not come from a resident or a live session.

## Provenance

- Canonical shape: Second Life `message_template.msg`, `UseCircuitCode Low 3
  NotTrusted Unencoded`.
- Mobile behavior cross-check: `Kaleaon/Lumiya-Redux` commit
  `4bf5ca476c79eeb583f18e9960df2ea2b422b0f5`,
  `UseCircuitCode.java` and `SLMessage.java`.
- Independent Rust cross-check: `benthic-mmo/metaverse_client` commit
  `f7de677ed26cf53a12e217764bb48f0ebed21701`,
  `crates/messages/src/udp/core/circuit_code.rs` and its circuit-code test.

The references agree that the packet is Low-frequency message 3, is not
zero-coded, and contains a little-endian U32 followed by the session and agent
UUIDs in wire order. No source was copied: Linkpoint uses a newly constructed
fictional packet to test the public wire behavior.

## Synthetic values

| Field | Value |
| --- | --- |
| Sequence | `0x01020304` |
| Circuit code | `0x12345678` |
| Session bytes | `10 11 12 13 14 15 16 17 18 19 1a 1b 1c 1d 1e 1f` |
| Agent bytes | `a0 a1 a2 a3 a4 a5 a6 a7 a8 a9 aa ab ac ad ae af` |

The complete expected packet is asserted in the colocated Rust unit test.
Decoding is intentionally bounded to exactly 36 body bytes and rejects
truncation, trailing bytes, alternate flags, extra headers, or another message
identifier.
