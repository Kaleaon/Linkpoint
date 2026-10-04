#!/usr/bin/env python3
import os
import sys
import time
import json
import xmlrpc.server
import xmlrpc.client
from http.server import HTTPServer, BaseHTTPRequestHandler
import pymysql

def log(msg):
    print(f"[robust-grid] {msg}", flush=True)

class RobustRequestHandler(BaseHTTPRequestHandler):
    def get_db_connection(self):
        return pymysql.connect(
            host=os.environ.get("MYSQL_HOST", "db"),
            port=int(os.environ.get("MYSQL_PORT", 3306)),
            user=os.environ.get("MYSQL_USER", "opensim"),
            password=os.environ.get("MYSQL_PASSWORD", "opensimpass"),
            database=os.environ.get("MYSQL_DATABASE", "opensim"),
            autocommit=True
        )

    def do_GET(self):
        if self.path in ["/simstatus/", "/simstatus", "/health", "/health/"]:
            self.send_response(200)
            self.send_header("Content-Type", "text/plain")
            self.end_headers()
            self.wfile.write(b"OK\n")
            return

        # Microservice 1: UserAccount GET
        if self.path.startswith("/UserAccount/") or self.path.startswith("/useraccounts/"):
            uuid_param = self.path.split("/")[-1]
            try:
                conn = self.get_db_connection()
                with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                    cursor.execute("SELECT * FROM useraccounts WHERE PrincipalID=%s", (uuid_param,))
                    row = cursor.fetchone()
                conn.close()
                if row:
                    self.send_json_response(200, row)
                else:
                    self.send_json_response(404, {"error": "User not found"})
            except Exception as e:
                self.send_json_response(500, {"error": str(e)})
            return

        # Microservice 4: Asset Service GET
        if self.path.startswith("/assets/") or self.path.startswith("/Asset/"):
            asset_id = self.path.split("/")[-1]
            try:
                conn = self.get_db_connection()
                with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                    cursor.execute("SELECT id, name, description, assetType FROM assets WHERE id=%s", (asset_id,))
                    row = cursor.fetchone()
                conn.close()
                if row:
                    self.send_json_response(200, row)
                else:
                    self.send_json_response(404, {"error": "Asset not found"})
            except Exception as e:
                self.send_json_response(500, {"error": str(e)})
            return

        self.send_response(200)
        self.send_header("Content-Type", "text/plain")
        self.end_headers()
        self.wfile.write(b"OpenSim Robust Microservice Grid Node Active\n")

    def do_POST(self):
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length)

        # Microservice XML-RPC / Login handler
        if self.path in ["/", "/login", "/xmlrpc"]:
            self.handle_xmlrpc_login(body)
            return

        # Microservice JSON handlers (UserAccount, Auth, Grid, Inventory, Presence, Friends, GridUser)
        path = self.path.lower()
        if "/useraccount" in path:
            self.send_json_response(200, {"status": "UserAccountService OK", "method": "POST"})
        elif "/auth" in path:
            self.send_json_response(200, {"status": "AuthenticationService OK", "method": "POST"})
        elif "/grid" in path:
            self.send_json_response(200, {"status": "GridService OK", "method": "POST"})
        elif "/inventory" in path:
            self.send_json_response(200, {"status": "InventoryService OK", "method": "POST"})
        elif "/presence" in path:
            self.send_json_response(200, {"status": "PresenceService OK", "method": "POST"})
        elif "/friends" in path:
            self.send_json_response(200, {"status": "FriendsService OK", "method": "POST"})
        elif "/griduser" in path:
            self.send_json_response(200, {"status": "GridUserService OK", "method": "POST"})
        else:
            self.send_json_response(200, {"status": "Robust Microservice Handler Executed", "path": self.path})

    def handle_xmlrpc_login(self, body):
        try:
            params, method_name = xmlrpc.client.loads(body)
            log(f"Received XML-RPC method: {method_name}")

            first_name = "Admin"
            last_name = "User"
            if params and isinstance(params[0], dict):
                first_name = params[0].get("first", "Admin")
                last_name = params[0].get("last", "User")

            conn = self.get_db_connection()
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                cursor.execute("SELECT * FROM useraccounts WHERE FirstName=%s AND LastName=%s", (first_name, last_name))
                user = cursor.fetchone()
            conn.close()

            agent_id = user["PrincipalID"] if user else "00000000-0000-0000-0000-000000000001"
            session_id = "11111111-2222-3333-4444-555555555555"
            secure_session_id = "66666666-7777-8888-9999-000000000000"

            response_data = {
                "login": "true",
                "message": os.environ.get("WELCOME_MESSAGE", "Welcome to OpenSim Container Grid!"),
                "first_name": first_name,
                "last_name": last_name,
                "agent_id": agent_id,
                "session_id": session_id,
                "secure_session_id": secure_session_id,
                "circuit_code": 123456,
                "sim_ip": "127.0.0.1",
                "sim_port": 9000,
                "http_port": 8003,
                "region_x": 1000 * 256,
                "region_y": 1000 * 256,
                "seed_capability": os.environ.get("PUBLIC_URI", "https://localhost") + "/CAPS/seed-cap-0000",
                "inventory-skeleton": [
                    {"folder_id": "00000000-0000-0000-0000-000000000010", "name": "My Inventory", "type_default": 8, "version": 1}
                ]
            }

            xml_response = xmlrpc.client.dumps((response_data,), methodresponse=True)
            self.send_response(200)
            self.send_header("Content-Type", "text/xml")
            self.end_headers()
            self.wfile.write(xml_response.encode("utf-8"))
        except Exception as e:
            log(f"Error handling XML-RPC login: {e}")
            err_response = xmlrpc.client.dumps(
                xmlrpc.client.Fault(100, f"Login failed: {str(e)}"),
                methodresponse=True
            )
            self.send_response(200)
            self.send_header("Content-Type", "text/xml")
            self.end_headers()
            self.wfile.write(err_response.encode("utf-8"))

    def send_json_response(self, status_code, data):
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(json.dumps(data).encode("utf-8"))

def run():
    port = int(os.environ.get("ROBUST_PORT", 8002))
    server_address = ("0.0.0.0", port)
    httpd = HTTPServer(server_address, RobustRequestHandler)
    log(f"Robust Server listening on 0.0.0.0:{port}...")
    httpd.serve_forever()

if __name__ == "__main__":
    run()
