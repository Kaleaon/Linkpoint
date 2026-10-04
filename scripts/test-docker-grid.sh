#!/bin/bash
set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${REPO_ROOT}"

echo "==========================================================="
echo "  OpenSim Docker Compose Grid & Reverse Proxy Test Suite   "
echo "==========================================================="

if [ ! -f .env ]; then
    echo "Creating .env from .env.example..."
    cp .env.example .env
fi

echo "[1/4] Validating Docker Compose configuration..."
docker compose config > /dev/null
echo "✅ Docker Compose YAML configuration is valid!"

echo "[2/4] Building and launching multi-container grid suite..."
docker compose up -d --build --wait

echo "[3/4] Checking container health statuses..."
docker compose ps

echo "[4/4] Executing endpoint verification test suite..."
python3 docker/grid-proxy/scripts/verify_grid.py

echo "==========================================================="
echo "  Grid Suite Verification Complete!                        "
echo "==========================================================="
