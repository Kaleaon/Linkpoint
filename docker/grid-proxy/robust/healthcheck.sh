#!/bin/sh
PORT="${ROBUST_PORT:-8002}"
curl -s -f "http://localhost:${PORT}/simstatus/" >/dev/null || exit 1
