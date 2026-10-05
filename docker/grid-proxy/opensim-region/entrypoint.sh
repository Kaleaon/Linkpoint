#!/bin/sh
set -e

ROBUST_HOST="${ROBUST_HOST:-robust-grid}"
ROBUST_PORT="${ROBUST_PORT:-8002}"
URL="http://${ROBUST_HOST}:${ROBUST_PORT}/simstatus/"

echo "[opensim-region] Waiting for Robust Grid microservices at ${URL}..."
while ! curl -s -f "${URL}" >/dev/null; do
    sleep 1
done

echo "[opensim-region] Robust Grid microservices are healthy! Launching Region Simulator..."
exec python3 /app/region_server.py
