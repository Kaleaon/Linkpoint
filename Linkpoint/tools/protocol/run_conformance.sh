#!/usr/bin/env bash
# Unified Linkpoint protocol conformance driver.
#
# Runs every conformance gate in deterministic order, archives every report
# under build/reports/protocol/, and exits non-zero on the first failure.
#
# Layers (in order):
#   1. tools/protocol/sync_master_template.sh --check
#      Offline SHA drift gate against the upstream Second Life
#      master-message-template pin (no JDK required).
#   2. tools/protocol/verify_message_template_conformance.py
#      Offline parser-only classification audit of message_template.msg
#      vs MessageTemplateCatalog.kt (no JDK required).
#   3. tools/protocol/verify_protocol_docs.py
#      Offline parse-contract validation of
#      docs/AGENT_PARSABLE_MODERNIZATION_TASKLIST.md (no JDK required).
#   4. ./gradlew testDebugUnitTest --tests <conformance suite>
#      JVM conformance tests covering message template classification,
#      parser fixtures, declared-only slices, capability audits, and
#      LLSD parser fuzz targets.
#
# Flags:
#   --skip-gradle   Skip layer (4). Useful for fast pre-commit runs and
#                   for environments without a full Android SDK.
#   --offline       Forwarded to gradle as --offline.
#
# Exit codes:
#   0  - all configured layers pass
#   1  - one or more layers reported drift / failure
#   2  - tooling missing (python3, sha1sum, gradle wrapper)

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
LINKPOINT_ROOT="$(cd -- "${SCRIPT_DIR}/../.." &> /dev/null && pwd)"
REPO_ROOT="$(cd -- "${LINKPOINT_ROOT}/.." &> /dev/null && pwd)"
REPORTS_DIR="${LINKPOINT_ROOT}/build/reports/protocol"

skip_gradle=0
gradle_extra=()
for arg in "$@"; do
  case "${arg}" in
    --skip-gradle) skip_gradle=1 ;;
    --offline)     gradle_extra+=("--offline") ;;
    -h|--help)
      sed -n '2,/^set -e/p' "$0" | sed '/^set -e/d'
      exit 0
      ;;
    *)
      echo "[run-conformance] unknown flag: ${arg}" >&2
      exit 2
      ;;
  esac
done

mkdir -p "${REPORTS_DIR}"
SUMMARY="${REPORTS_DIR}/run_conformance.summary.txt"
: > "${SUMMARY}"

log() { printf '[run-conformance] %s\n' "$*" | tee -a "${SUMMARY}" >&2; }
section() {
  printf '\n[run-conformance] === %s ===\n' "$*" | tee -a "${SUMMARY}" >&2
}

require_tool() {
  command -v "$1" >/dev/null 2>&1 || { log "missing required tool: $1"; exit 2; }
}

require_tool python3
require_tool sha1sum
if [[ ${skip_gradle} -eq 0 ]]; then
  [[ -x "${REPO_ROOT}/gradlew" ]] || { log "missing gradle wrapper at ${REPO_ROOT}/gradlew"; exit 2; }
fi

failures=()
record_layer() {
  local name="$1" rc="$2"
  if [[ "${rc}" -eq 0 ]]; then
    log "OK   ${name}"
  else
    log "FAIL ${name} (rc=${rc})"
    failures+=("${name}")
  fi
}

# Layer 1: SHA drift gate
section "layer 1: SHA drift gate"
set +e
"${SCRIPT_DIR}/sync_master_template.sh" --check 2>&1 \
  | tee "${REPORTS_DIR}/sha-drift-check.log"
rc=${PIPESTATUS[0]}
set -e
record_layer "sync_master_template.sh --check" "${rc}"

# Layer 2: Python message-template classification audit
section "layer 2: message_template.msg classification audit"
set +e
python3 "${SCRIPT_DIR}/verify_message_template_conformance.py" \
  --report "${REPORTS_DIR}/message-template-audit.txt" 2>&1 \
  | tee "${REPORTS_DIR}/message-template-audit.log"
rc=${PIPESTATUS[0]}
set -e
record_layer "verify_message_template_conformance.py" "${rc}"

# Layer 3: Protocol docs parse-contract validation
section "layer 3: docs parse-contract validation"
set +e
python3 "${SCRIPT_DIR}/verify_protocol_docs.py" \
  --report "${REPORTS_DIR}/protocol-docs-audit.txt" 2>&1 \
  | tee "${REPORTS_DIR}/protocol-docs-audit.log"
rc=${PIPESTATUS[0]}
set -e
record_layer "verify_protocol_docs.py" "${rc}"

# Layer 4: Gradle conformance test bundle
if [[ ${skip_gradle} -eq 0 ]]; then
  section "layer 4: gradle conformance suite"
  set +e
  # The Linkpoint module declares an `xr` flavor dimension; the conformance
  # tests live under the `stable` flavor, so we target that variant directly
  # via the top-level gradle wrapper (which composite-builds Linkpoint).
  ( cd "${REPO_ROOT}" && ./gradlew "${gradle_extra[@]}" \
      :Linkpoint:testStableDebugUnitTest \
      --tests 'com.linkpoint.protocol.messages.MessageTemplateProtocolConformanceTest' \
      --tests 'com.linkpoint.protocol.messages.MessageParserConformanceFixtureTest' \
      --tests 'com.linkpoint.protocol.messages.DeclaredMessageSlicesTest' \
      --tests 'com.linkpoint.protocol.messages.ManagerMessageIdUsageTest' \
      --tests 'com.linkpoint.protocol.capabilities.CapabilityDeclarationAuditTest' \
      --tests 'com.linkpoint.protocol.llsd.LLSDParserFuzzTargetTest' \
  ) 2>&1 | tee "${REPORTS_DIR}/gradle-conformance.log"
  rc=${PIPESTATUS[0]}
  set -e
  record_layer "gradlew conformance suite" "${rc}"
else
  log "SKIP layer 4 (--skip-gradle)"
fi

section "summary"
if [[ ${#failures[@]} -eq 0 ]]; then
  log "ALL LAYERS PASSED"
  log "reports under: ${REPORTS_DIR}"
  exit 0
fi
log "FAILED LAYERS:"
for f in "${failures[@]}"; do log " - ${f}"; done
log "reports under: ${REPORTS_DIR}"
exit 1
