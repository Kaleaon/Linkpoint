# Linkpoint Protocol Conformance Tooling

Implements `LP-P0-001` (template SHA drift gate) and the runner skeleton
of `LP-P0-002` from
[`docs/AGENT_PARSABLE_MODERNIZATION_TASKLIST.md`](../../../docs/AGENT_PARSABLE_MODERNIZATION_TASKLIST.md).

## Files

| Path | Role |
| --- | --- |
| `sync_master_template.sh` | Offline `--check` / online `--fetch` / `--sync` for the SL master message template. |
| `message_template.msg.sha1` | Pinned SHA1 of the upstream `message_template.msg`. CI gates on this. |
| `message_template_mismatches.txt` | Allowlist for intentional drift between Linkpoint and upstream template. |
| `verify_message_template_conformance.py` | JDK-free classification audit against `MessageTemplateCatalog.kt` + `LinkpointApp.kt`. |
| `verify_protocol_docs.py` | Validates the parse contract of `AGENT_PARSABLE_MODERNIZATION_TASKLIST.md`. |
| `run_conformance.sh` | One-shot runner: SHA gate → classification audit → docs audit → JVM conformance suite. |

## Local quick start

Fast offline gate (no Android SDK / JDK needed):

```bash
Linkpoint/tools/protocol/run_conformance.sh --skip-gradle
```

Full suite (requires JDK 17 and the Android SDK provisioned):

```bash
Linkpoint/tools/protocol/run_conformance.sh
```

Reports land under `Linkpoint/build/reports/protocol/`.

## CI

`.github/workflows/protocol-conformance.yml` runs in two stages:

1. `offline-gate` (under 1 minute): runs `run_conformance.sh --skip-gradle`
   so a SHA drift / classification regression fails CI fast.
2. `jvm-suite` (gated on `offline-gate`): runs the full Gradle suite and
   uploads `Linkpoint/build/reports/protocol/` and
   `Linkpoint/build/reports/tests/` as workflow artifacts.

## Updating the pin

When the upstream Second Life master template changes:

```bash
# 1. Pull upstream + verify integrity
Linkpoint/tools/protocol/sync_master_template.sh --fetch

# 2. If the diff is intentional, sync into the repo
Linkpoint/tools/protocol/sync_master_template.sh --sync

# 3. Review tools/protocol/message_template_mismatches.txt, then run
Linkpoint/tools/protocol/run_conformance.sh
```

`--sync` rewrites both the committed `message_template.msg` and the
pinned `.sha1` in one shot; commit them together so CI sees a clean
state.
