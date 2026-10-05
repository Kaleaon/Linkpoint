#!/bin/sh
set -e

SSL_DIR="/etc/nginx/ssl"
mkdir -p "${SSL_DIR}"

if [ ! -f "${SSL_DIR}/server.crt" ] || [ ! -f "${SSL_DIR}/server.key" ]; then
    echo "[nginx-proxy] Generating self-signed TLS certificates for local grid proxy..."
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout "${SSL_DIR}/server.key" \
        -out "${SSL_DIR}/server.crt" \
        -subj "/CN=localhost/O=OpenSim Grid/C=US"
fi

echo "[nginx-proxy] Starting NGINX reverse proxy..."
exec nginx -g "daemon off;"
