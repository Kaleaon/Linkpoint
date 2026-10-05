#!/bin/sh
set -e

CONFIG_DIR="${CONFIG_DIR:-/config}"
SENTINEL="${CONFIG_DIR}/.bootstrap_done"

echo "[robust-grid] Waiting for grid-bootstrap sentinel file at ${SENTINEL}..."
while [ ! -f "${SENTINEL}" ]; do
    sleep 1
done

echo "[robust-grid] Sentinel file found. Launching OpenSim Robust Server..."
exec python3 /app/robust_server.py
