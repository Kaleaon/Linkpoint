# Rust metaverse crate evaluation

Reviewed 2026-09-24 against `metaverse_core` 0.3.0 and its published dependency
graph.

## Decision

`metaverse_core` is useful interoperability evidence, but it is not linked into
Linkpoint. The crate and its domain dependencies (`metaverse_messages`,
`metaverse_environment`, `metaverse_agent`, `metaverse_inventory`, and
`metaverse_mesh`) are AGPL-3.0-or-later, while this workspace is distributed as
LGPL-3.0-only. Adding them would silently change the obligations of distributed
Linkpoint binaries. Their Actix/AWC runtime and filesystem-oriented renderer
pipeline also conflict with the current plan's portable, transport-independent
core and thin platform adapters.

No source was copied. The published APIs were used as a checklist alongside the
Second Life protocol documentation. That review found several gaps in the
initial protocol foundation:

- login is XML-RPC rather than LLSD XML and must represent the
  `indeterminate` redirect response separately from rejection;
- viewer/platform metadata, circuit code, and optional seed/session fields are
  part of session establishment;
- UDP framing needs the four frequency encodings, header flags, extra-header
  bytes, and bounded zero coding before message-template decoding;
- the HTTP and UDP adapters must agree on the local interface used for login and
  `UseCircuitCode`, especially on multihomed hosts;
- packet ACK batching, appended ACKs, ping/pong, region handshake, simulator
  disable, object updates, appearance, layer data, and chat are required
  dispatch paths;
- asset work needs bounded downloads and typed decoders for JPEG2000 textures,
  mesh LLSD, wearables, animations, sounds, and scene objects.

This change implements the first three items in portable code. The remaining
items stay explicit roadmap work rather than being hidden behind a dependency
whose current implementation still contains incomplete packet and avatar paths.

## Revisit criteria

Reconsider direct integration only if the dependency licensing becomes
compatible with Linkpoint's distribution, the runtime can be feature-gated out,
and fixture tests demonstrate better conformance than the local protocol
boundary. Until then, small broadly useful dependencies such as `url` and
`serde-llsd-benthic` are preferred over adopting an application framework.
