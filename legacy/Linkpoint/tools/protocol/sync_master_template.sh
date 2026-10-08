#!/usr/bin/env bash
# Sync / verify the Second Life master-message-template against the
# committed Linkpoint copy.
#
# Modes:
#   --check    (default) Offline. Compute SHA1 of the committed template
#              and assert it matches tools/protocol/message_template.msg.sha1.
#              Exits non-zero on drift.
#   --fetch    Online. Download upstream template + .sha1, verify upstream
#              integrity, then diff upstream vs committed template. Reports
#              drift but does not modify on-disk state.
#   --sync     Online. Performs --fetch, and if upstream is healthy writes
#              the upstream content into the committed template path and
#              updates message_template.msg.sha1. Caller must then review
#              the diff and update message_template_mismatches.txt as
#              appropriate before committing.
#
# Exit codes:
#   0  - success / no unapproved drift
#   1  - drift detected without matching allowlist entry
#   2  - missing tooling or unreachable upstream
#   3  - integrity failure (upstream content does not match upstream SHA)

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
PROJECT_ROOT="$(cd -- "${SCRIPT_DIR}/../.." &> /dev/null && pwd)"
PINNED_SHA_FILE="${SCRIPT_DIR}/message_template.msg.sha1"
MISMATCHES_FILE="${SCRIPT_DIR}/message_template_mismatches.txt"
COMMITTED_TEMPLATE="${PROJECT_ROOT}/src/test/resources/protocol/message_template.msg"

UPSTREAM_TEMPLATE_URL="https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg"
UPSTREAM_SHA_URL="https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg.sha1"

mode="${1:---check}"

log() { printf '[sync-template] %s\n' "$*" >&2; }
die() { log "error: $*"; exit "${2:-1}"; }

require_file() {
  [[ -f "$1" ]] || die "missing required file: $1" 2
}

read_pinned_sha() {
  require_file "${PINNED_SHA_FILE}"
  awk 'NF { print $1; exit }' "${PINNED_SHA_FILE}"
}

compute_sha1() {
  if command -v sha1sum >/dev/null 2>&1; then
    sha1sum "$1" | awk '{ print $1 }'
  elif command -v shasum >/dev/null 2>&1; then
    shasum -a 1 "$1" | awk '{ print $1 }'
  else
    die "neither sha1sum nor shasum found on PATH" 2
  fi
}

require_curl() {
  command -v curl >/dev/null 2>&1 || die "curl is required for --fetch/--sync" 2
}

count_allowlist_entries() {
  if [[ -f "${MISMATCHES_FILE}" ]]; then
    grep -cvE '^\s*(#|$)' "${MISMATCHES_FILE}" || true
  else
    echo 0
  fi
}

cmd_check() {
  require_file "${COMMITTED_TEMPLATE}"
  local pinned actual
  pinned="$(read_pinned_sha)"
  actual="$(compute_sha1 "${COMMITTED_TEMPLATE}")"
  log "pinned   sha1 = ${pinned}"
  log "committed sha1 = ${actual}"
  if [[ "${pinned}" != "${actual}" ]]; then
    log "committed template diverges from pinned SHA"
    log "if this drift is intentional, run --sync (online) and review"
    log "tools/protocol/message_template_mismatches.txt before committing"
    return 1
  fi
  log "OK: committed template matches pinned SHA1"
  log "allowlist entries: $(count_allowlist_entries)"
  return 0
}

fetch_upstream() {
  require_curl
  local out_template="$1" out_sha="$2"
  log "fetching ${UPSTREAM_TEMPLATE_URL}"
  curl --fail --silent --show-error --location \
       --output "${out_template}" "${UPSTREAM_TEMPLATE_URL}" \
    || die "failed to download upstream template" 2
  log "fetching ${UPSTREAM_SHA_URL}"
  curl --fail --silent --show-error --location \
       --output "${out_sha}" "${UPSTREAM_SHA_URL}" \
    || die "failed to download upstream SHA" 2
}

verify_upstream_integrity() {
  local template_path="$1" upstream_sha_file="$2"
  local upstream_sha actual_sha
  upstream_sha="$(awk 'NF { print $1; exit }' "${upstream_sha_file}")"
  actual_sha="$(compute_sha1 "${template_path}")"
  if [[ "${upstream_sha}" != "${actual_sha}" ]]; then
    log "upstream content sha1 (${actual_sha}) != upstream published sha1 (${upstream_sha})"
    return 3
  fi
  log "upstream integrity OK (${actual_sha})"
  printf '%s\n' "${actual_sha}"
}

cmd_fetch() {
  require_file "${COMMITTED_TEMPLATE}"
  local tmpdir
  tmpdir="$(mktemp -d)"
  trap 'rm -rf "${tmpdir}"' EXIT

  fetch_upstream "${tmpdir}/upstream.msg" "${tmpdir}/upstream.msg.sha1"
  local upstream_sha
  if ! upstream_sha="$(verify_upstream_integrity "${tmpdir}/upstream.msg" "${tmpdir}/upstream.msg.sha1")"; then
    return 3
  fi

  local pinned committed
  pinned="$(read_pinned_sha)"
  committed="$(compute_sha1 "${COMMITTED_TEMPLATE}")"

  log "pinned    sha1 = ${pinned}"
  log "committed sha1 = ${committed}"
  log "upstream  sha1 = ${upstream_sha}"

  local rc=0
  if [[ "${committed}" != "${pinned}" ]]; then
    log "drift: committed template != pinned SHA"
    rc=1
  fi
  if [[ "${upstream_sha}" != "${pinned}" ]]; then
    log "drift: upstream template != pinned SHA"
    log "diff (committed vs upstream):"
    diff -u "${COMMITTED_TEMPLATE}" "${tmpdir}/upstream.msg" || true
    rc=1
  fi
  if [[ ${rc} -eq 0 ]]; then
    log "OK: upstream, pinned, and committed all match"
  fi
  return ${rc}
}

cmd_sync() {
  local tmpdir
  tmpdir="$(mktemp -d)"
  trap 'rm -rf "${tmpdir}"' EXIT

  fetch_upstream "${tmpdir}/upstream.msg" "${tmpdir}/upstream.msg.sha1"
  local upstream_sha
  if ! upstream_sha="$(verify_upstream_integrity "${tmpdir}/upstream.msg" "${tmpdir}/upstream.msg.sha1")"; then
    return 3
  fi

  mkdir -p "$(dirname "${COMMITTED_TEMPLATE}")"
  cp "${tmpdir}/upstream.msg" "${COMMITTED_TEMPLATE}"
  printf '%s\n' "${upstream_sha}" > "${PINNED_SHA_FILE}"
  log "synced committed template + pinned sha1 = ${upstream_sha}"
  log "review tools/protocol/message_template_mismatches.txt and the conformance report"
  return 0
}

case "${mode}" in
  --check) cmd_check ;;
  --fetch) cmd_fetch ;;
  --sync)  cmd_sync ;;
  -h|--help)
    sed -n '2,/^set -e/p' "$0" | sed '/^set -e/d'
    exit 0
    ;;
  *)
    die "unknown mode: ${mode} (try --check, --fetch, --sync)"
    ;;
esac
