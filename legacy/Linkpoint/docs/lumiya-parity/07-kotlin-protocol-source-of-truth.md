# Kotlin Protocol Source-of-Truth Strategy (Firestorm + LLSD-JAVA + Ghidra)

This repository's protocol modernization should treat the following as primary references:

1. Firestorm viewer protocol behavior and message semantics:
   - https://github.com/FirestormViewer/phoenix-firestorm
2. LLSD-JAVA type/parsing model and cross-format behavior:
   - https://github.com/Kaleaon/LLSD-JAVA

## Implementation rules

- New protocol work lands in Kotlin-first packages under `com.linkpoint.protocol.*`.
- Decompiled Java is for compatibility recovery only; Kotlin models/parsers are the long-term target.
- For any missing/ambiguous implementation:
  1. Check Firestorm message handling and template usage.
  2. Check LLSD-JAVA for LLSD parsing/serialization behavior.
  3. If still unresolved, use Ghidra against native/decompiled artifacts to recover field semantics.

## Conversion checklist per message family

- Define Kotlin data model (`data class` / sealed hierarchy) for payload and decode result.
- Implement parser in `com.linkpoint.protocol.messages` with strict bounds checks.
- Add/extend parser registration in `MessageParserRegistry`.
- Add parity-focused tests for:
  - binary decoding
  - LLSD/XML/JSON behavior (when applicable)
  - malformed packet handling
- Record unresolved fields as `TODO(issue-id)` only when source parity is genuinely unknown.

## Priority order

1. Login/circuit/session lifecycle.
2. Object/agent update messages.
3. Inventory/capability/event queue messages.
4. Terrain/texture/asset transfer details.
5. Script/LSL event and capability bridges.

## Ghidra usage notes

- Use Ghidra to recover:
  - message block layouts where decompiler output is incomplete,
  - native helper behavior for texture/audio/protocol edge cases,
  - enum/value mappings that are not obvious from Java output.
- Any Ghidra-derived mapping should be documented adjacent to parser code with a short provenance note.
