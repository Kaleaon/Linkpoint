#!/bin/bash
set -euo pipefail

if [[ -n "$(git status --porcelain --untracked-files=normal)" ]]; then
  echo "ERROR: parity snapshot scripts require a clean working tree (no staged, unstaged, or untracked files)." >&2
  echo "Resolve local changes first (commit, stash, or clean) before running parity snapshot scripts." >&2
  git status --short >&2
  exit 1
fi
