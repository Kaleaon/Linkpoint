# Backup & data-extraction security policy

This project now uses **allowlist-first backup rules** for Android Auto Backup and data extraction.

## Included in backup (safe user settings only)

- `shared_prefs/com.linkpoint_preferences.xml` (general app settings)
- `shared_prefs/theme_manager.xml` (theme/UI customization)
- `shared_prefs/cache_settings.xml` (cache size/location preferences only)
- `shared_prefs/destination_guide.xml` (destination browsing preferences)

## Explicitly excluded sensitive app-private data

### Tokens / credentials
- `shared_prefs/com_linkpoint_mfa_hashes.xml`
- `shared_prefs/com_linkpoint_mfa_hashes_encrypted.xml`
- `shared_prefs/device.xml` (device identity metadata)

### Session state / user world data
- `databases/inventory.db`
- `databases/inventory.db-wal`
- `databases/inventory.db-shm`

### Logs
- `files/crash_logs/`
- `files/Linkpoint Logs/` (network/session diagnostics)

### Caches with personal/world data
- `cache/` (entire internal app cache domain)

## Verification

Run:

```bash
python3 Linkpoint/tools/check_backup_sensitive_excludes.py
```

The check fails if:
- any known sensitive path is not explicitly excluded, or
- includes are broadened beyond the approved safe shared-preference allowlist.
