#!/usr/bin/env python3
import hashlib
import os
import re
import sys
import time
import uuid

import pymysql


def log(msg):
    print(f"[grid-bootstrap] {msg}", flush=True)


def render_template(template_str, context):
    """
    Renders {{ VAR | default('val') }} templates using standard regex matching.
    """

    def replacer(match):
        expr = match.group(1).strip()
        parts = expr.split("|")
        var_name = parts[0].strip()
        val = os.environ.get(var_name, None)
        if val is not None and val != "":
            return str(val)
        if len(parts) > 1 and "default" in parts[1]:
            def_match = re.search(r"default\((['\"]?)(.*?)\1\)", parts[1])
            if def_match:
                return def_match.group(2)
        return context.get(var_name, "")

    return re.sub(r"\{\{\s*(.*?)\s*\}\}", replacer, template_str)


def wait_for_db(host, port, user, password, dbname, timeout=60):
    start = time.time()
    log(f"Waiting for database connection at {host}:{port}...")
    while time.time() - start < timeout:
        try:
            conn = pymysql.connect(
                host=host,
                port=int(port),
                user=user,
                password=password,
                database=dbname,
                connect_timeout=3,
            )
            conn.close()
            log("Database connection successful!")
            return True
        except Exception as e:
            log(f"Database not ready yet ({e}). Retrying in 2 seconds...")
            time.sleep(2)
    log("Timeout waiting for database!")
    return False


def apply_schema(host, port, user, password, dbname, schema_path):
    log(f"Applying schema from {schema_path}...")
    conn = pymysql.connect(
        host=host,
        port=int(port),
        user=user,
        password=password,
        database=dbname,
        autocommit=True,
    )
    with conn.cursor() as cursor:
        with open(schema_path, "r", encoding="utf-8") as f:
            sql_script = f.read()
        lines = [
            line
            for line in sql_script.splitlines()
            if not line.strip().startswith("--")
        ]
        clean_sql = "\n".join(lines)
        statements = clean_sql.split(";")
        for stmt in statements:
            stmt = stmt.strip()
            if stmt:
                cursor.execute(stmt)
    conn.close()
    log("Schema applied successfully!")


def seed_admin_user(host, port, user, password, dbname):
    first_name = os.environ.get("ADMIN_FIRST_NAME", "Admin")
    last_name = os.environ.get("ADMIN_LAST_NAME", "User")
    admin_pass = os.environ.get("ADMIN_PASSWORD", "password")

    conn = pymysql.connect(
        host=host,
        port=int(port),
        user=user,
        password=password,
        database=dbname,
        autocommit=True,
    )
    with conn.cursor() as cursor:
        cursor.execute(
            "SELECT PrincipalID FROM useraccounts WHERE FirstName=%s AND LastName=%s",
            (first_name, last_name),
        )
        row = cursor.fetchone()
        if row:
            log(f"Admin user {first_name} {last_name} already exists.")
        else:
            user_id = str(uuid.uuid4())
            now = int(time.time())
            log(f"Creating admin user {first_name} {last_name} ({user_id})...")
            cursor.execute(
                """INSERT INTO useraccounts
                   (PrincipalID, ScopeID, FirstName, LastName, Email, Created, UserLevel, UserFlags, UserTitle)
                   VALUES (%s, '00000000-0000-0000-0000-000000000000', %s, %s, %s, %s, 200, 0, 'Grid Admin')""",
                (
                    user_id,
                    first_name,
                    last_name,
                    f"{first_name.lower()}@grid.local",
                    now,
                ),
            )
            # Password salt and hash (MD5 hash of pass:salt)
            salt = hashlib.md5(str(uuid.uuid4()).encode()).hexdigest()[:32]
            pass_hash = hashlib.md5((admin_pass + ":" + salt).encode()).hexdigest()
            cursor.execute(
                """INSERT INTO auth (UUID, passwordHash, passwordSalt, accountType)
                   VALUES (%s, %s, %s, 'UserAccount')""",
                (user_id, pass_hash, salt),
            )
            log("Admin user created successfully.")
    conn.close()


def generate_configs(output_dir):
    os.makedirs(output_dir, exist_ok=True)
    template_dir = os.path.join(os.path.dirname(__file__), "templates")

    context = dict(os.environ)

    for filename in os.listdir(template_dir):
        if filename.endswith(".j2"):
            tmpl_path = os.path.join(template_dir, filename)
            out_filename = filename[:-3]  # remove .j2
            out_path = os.path.join(output_dir, out_filename)

            with open(tmpl_path, "r", encoding="utf-8") as f:
                content = f.read()

            rendered = render_template(content, context)

            with open(out_path, "w", encoding="utf-8") as f:
                f.write(rendered)

            log(f"Generated configuration: {out_path}")


def main():
    log("Starting grid-bootstrap...")

    db_host = os.environ.get("MYSQL_HOST", "db")
    db_port = os.environ.get("MYSQL_PORT", 3306)
    db_user = os.environ.get("MYSQL_USER", "opensim")
    db_pass = os.environ.get("MYSQL_PASSWORD", "opensimpass")
    db_name = os.environ.get("MYSQL_DATABASE", "opensim")

    config_dir = os.environ.get("CONFIG_DIR", "/config")
    schema_path = os.environ.get("SCHEMA_PATH", "/db/01-schema.sql")

    if not wait_for_db(db_host, db_port, db_user, db_pass, db_name):
        sys.exit(1)

    apply_schema(db_host, db_port, db_user, db_pass, db_name, schema_path)
    seed_admin_user(db_host, db_port, db_user, db_pass, db_name)
    generate_configs(config_dir)

    sentinel = os.path.join(config_dir, ".bootstrap_done")
    with open(sentinel, "w") as f:
        f.write("OK\n")
    log("Grid initialization complete! Sentinel file written.")


if __name__ == "__main__":
    main()
