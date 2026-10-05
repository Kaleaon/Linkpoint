#!/bin/sh
PORT="${REGION_PORT:-8003}"
curl -s -f "http://localhost:${PORT}/simstatus/" >/dev/null || exit 1
