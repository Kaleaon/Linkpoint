# Linkpoint security and privacy threat model

## Protected data

Passwords, session and secure-session identifiers, capability URLs, chat,
inventory names, account identity, and cached assets are private data. Passwords
may exist only in an in-flight login command and the native session's reconnect
state; they must be cleared on failure or logout and never logged or persisted.

## Trust boundaries

The React UI communicates exclusively through `ViewerClient`. Native commands
cross the Tauri IPC boundary into `linkpoint-core`; only the protocol layer may
interpret simulator messages. Login endpoints require HTTPS and an allowlisted
host. Simulator-provided capability URLs require HTTPS and must never be placed
in logs, analytics, browser caches, crash reports, or committed fixtures.

The Web/PWA build is a limited preview. It cannot provide raw UDP or native
credential storage. Its service worker bypasses cross-origin, `/api/`, and
`/caps/` traffic so bearer-equivalent URLs and private responses are not cached.

## Abuse cases and controls

| Threat | Required control |
| --- | --- |
| Credential/session disclosure | Redacted errors, no password persistence, secret scanning |
| SSRF or hostile custom grid | HTTPS, no embedded credentials, explicit host consent |
| Oversized/malformed protocol input | Response/datagram limits, parsers with bounded expansion, fuzz tests |
| Compromised UI dependency | CSP, locked dependencies, npm/cargo audit, SBOM |
| Replay or stale native session | Clear secrets on logout/failure; reconnect through the state machine |
| Private browser cache entries | Network-only cross-origin/capability requests |

## Release gate

A production release additionally requires external penetration review, signing
key custody, crash-report privacy review, platform privacy manifests, verified
data deletion, and device-level evidence. Never place resident data in protocol
fixtures; fixtures must be synthetic and reviewed before commit.
