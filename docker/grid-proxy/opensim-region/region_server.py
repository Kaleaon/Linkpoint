#!/usr/bin/env python3
import json
import os
from http.server import BaseHTTPRequestHandler, HTTPServer

import pymysql


def log(msg):
    print(f"[opensim-region] {msg}", flush=True)


class RegionRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path in ["/simstatus/", "/simstatus", "/health", "/health/"]:
            self.send_response(200)
            self.send_header("Content-Type", "text/plain")
            self.end_headers()
            self.wfile.write(b"OK\n")
            return

        if self.path.startswith("/CAPS/"):
            self.send_response(200)
            self.send_header("Content-Type", "application/llsd+xml")
            self.end_headers()
            self.wfile.write(
                b"<llsd><map><key>EventQueueGet</key><string>https://localhost/CAPS/eqg</string></map></llsd>"
            )
            return

        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        region_info = {
            "region_name": os.environ.get("REGION_NAME", "Welcome Region"),
            "region_id": os.environ.get(
                "REGION_ID", "11111111-2222-3333-4444-555555555555"
            ),
            "loc_x": int(os.environ.get("REGION_LOC_X", 1000)),
            "loc_y": int(os.environ.get("REGION_LOC_Y", 1000)),
            "http_port": int(os.environ.get("REGION_PORT", 8003)),
            "udp_port": int(os.environ.get("UDP_PORT", 9000)),
            "status": "online",
        }
        self.wfile.write(json.dumps(region_info).encode("utf-8"))

    def do_POST(self):
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(
            json.dumps({"status": "Region Endpoint Handled", "path": self.path}).encode(
                "utf-8"
            )
        )


def register_region():
    host = os.environ.get("MYSQL_HOST", "db")
    port = int(os.environ.get("MYSQL_PORT", 3306))
    user = os.environ.get("MYSQL_USER", "opensim")
    password = os.environ.get("MYSQL_PASSWORD", "opensimpass")
    dbname = os.environ.get("MYSQL_DATABASE", "opensim")

    region_name = os.environ.get("REGION_NAME", "Welcome Region")
    region_id = os.environ.get("REGION_ID", "11111111-2222-3333-4444-555555555555")
    loc_x = int(os.environ.get("REGION_LOC_X", 1000))
    loc_y = int(os.environ.get("REGION_LOC_Y", 1000))
    handle = (loc_x * 1000000) + loc_y

    try:
        conn = pymysql.connect(
            host=host,
            port=port,
            user=user,
            password=password,
            database=dbname,
            autocommit=True,
        )
        with conn.cursor() as cursor:
            cursor.execute(
                """INSERT INTO regions (uuid, regionHandle, regionName, serverIP, serverPort, serverURI, locX, locY)
                   VALUES (%s, %s, %s, '127.0.0.1', 9000, %s, %s, %s)
                   ON DUPLICATE KEY UPDATE regionName=%s, locX=%s, locY=%s""",
                (
                    region_id,
                    handle,
                    region_name,
                    os.environ.get("PUBLIC_URI", "https://localhost"),
                    loc_x,
                    loc_y,
                    region_name,
                    loc_x,
                    loc_y,
                ),
            )
        conn.close()
        log(
            f"Registered region '{region_name}' at ({loc_x},{loc_y}) into Grid Service database."
        )
    except Exception as e:
        log(f"Warning: Region registration database update failed: {e}")


def run():
    port = int(os.environ.get("REGION_PORT", 8003))
    register_region()
    server_address = ("0.0.0.0", port)
    httpd = HTTPServer(server_address, RegionRequestHandler)
    log(f"OpenSim Region Simulator listening on 0.0.0.0:{port}...")
    httpd.serve_forever()


if __name__ == "__main__":
    run()
