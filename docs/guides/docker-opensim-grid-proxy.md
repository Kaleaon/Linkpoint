# Docker Compose Multi-Container OpenSim Grid & NGINX Proxy Deployment Guide

This guide describes how to deploy a modernized multi-container OpenSim virtual world grid with NGINX TLS reverse proxy using Docker Compose.

## Architecture Overview

The multi-container architecture containerizes all core grid microservices and standardizes client endpoints over standard HTTP (port 80) and HTTPS (port 443) ports.

```
                  +-----------------------------------+
                  |   Mobile & Desktop Clients       |
                  |  (Linkpoint / Second Life Viewers) |
                  +-----------------------------------+
                                    |
                            HTTPS:443 / HTTP:80
                                    v
                        +-----------------------+
                        | NGINX Reverse Proxy   |
                        | (TLS Termination)     |
                        +-----------------------+
                           /               \
            XML-RPC / LLSD / Microservices   CAPS / Region
                        /                     \
                       v                       v
            +--------------------+   +-----------------------+
            | Robust Grid Server |   | Region Simulator Node |
            |  (Internal 8002)   |   | (Internal 8003/9000)  |
            +--------------------+   +-----------------------+
                       \                       /
                        +----------+----------+
                                   |
                                   v
                        +---------------------+
                        | MariaDB Database    |
                        | (Persistent Data)   |
                        +---------------------+
```

### Seven Core Microservices Containerized
1. **UserAccount Service**: Manages avatar principal accounts and user identity profiles.
2. **Authentication Service**: Handles password salts, hashes, and web login session tokens.
3. **Grid Service**: Tracks registered region simulators and global spatial coordinate mapping.
4. **Asset Service**: Manages prim textures, meshes, wearables, scripts, and audio clips.
5. **Inventory Service**: Manages folder trees and inventory items for connected avatars.
6. **Presence Service**: Tracks online status and active region locations.
7. **Friends & GridUser Service**: Manages friends lists, IM dispatching, and last-seen metrics.

---

## Quick Start

### 1. Prerequisites
- Docker (v20.10+) with Docker Compose plugin (`docker compose`).
- Host platform: Linux x86_64 or ARM64 (Apple Silicon supported).

### 2. Startup Command
Copy `.env.example` to `.env` if custom parameters are required, then launch the grid:

```bash
docker compose up -d
```

To run automated verification tests against the running grid proxy:

```bash
./scripts/test-docker-grid.sh
```

---

## Configuration (`.env`)

| Variable | Default | Purpose |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | `rootpass` | MariaDB root administrative password |
| `MYSQL_DATABASE` | `opensim` | OpenSim database name |
| `MYSQL_USER` | `opensim` | Database user |
| `MYSQL_PASSWORD` | `opensimpass` | Database user password |
| `ROBUST_PORT` | `8002` | Internal Robust service port |
| `REGION_PORT` | `8003` | Internal Region simulator HTTP port |
| `UDP_PORT` | `9000` | Region simulator UDP circuit port |
| `PUBLIC_URI` | `https://localhost` | External grid URL for mobile clients |
| `ADMIN_FIRST_NAME` | `Admin` | Initial administrator avatar first name |
| `ADMIN_LAST_NAME` | `User` | Initial administrator avatar last name |
| `ADMIN_PASSWORD` | `password` | Initial administrator password |

---

## Persistent Volumes

All persistent state resides in mounted volume targets to prevent data loss on container updates or restarts:

- `grid_db_data`: MariaDB relational database state (`useraccounts`, `auth`, `inventory`, `assets`, `regions`).
- `grid_config`: Automated bootstrap generated configurations (`Robust.ini`, `OpenSim.ini`, `GridCommon.ini`).
- `grid_asset_storage`: Binary asset blob cache storage.
- `region_terrain_storage`: Region terrain heightmap and state files.
- `nginx_ssl`: Self-signed or custom TLS certificates.

---

## Container Health Checks

Each service includes a container-native `HEALTHCHECK`:
- `db`: Uses `mysqladmin ping` and InnoDB initialization checks.
- `grid-bootstrap`: Runs migrations and exits with status 0 upon completion.
- `robust-grid`: Probes `http://localhost:8002/simstatus/`.
- `opensim-region`: Probes `http://localhost:8003/simstatus/`.
- `nginx-proxy`: Probes `http://localhost/health` and `https://localhost/health`.
