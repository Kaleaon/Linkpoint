"""
inventory_cache.py - Persistent SQLite Inventory Cache.

Provides SQLite persistent storage for folder structures, item metadata,
and update tokens. Loads cached folders immediately on launch before
starting background HTTP delta updates.
"""

import os
import sqlite3
import threading
import time
from typing import Dict, List, Optional, Any, Tuple

CURRENT_SCHEMA_VERSION = 1


class InventoryCache:
    def __init__(self, db_path: str = ":memory:"):
        self.db_path = db_path
        self._lock = threading.Lock()
        self._is_corrupted = False
        self._init_db()

    def _get_connection(self) -> sqlite3.Connection:
        if self.db_path == ":memory:":
            if not hasattr(self, "_memory_conn") or self._memory_conn is None:
                self._memory_conn = sqlite3.connect(":memory:", check_same_thread=False)
                self._memory_conn.execute("PRAGMA foreign_keys = ON;")
                self._memory_conn.row_factory = sqlite3.Row
            return self._memory_conn
        conn = sqlite3.connect(self.db_path, check_same_thread=False)
        conn.execute("PRAGMA foreign_keys = ON;")
        conn.row_factory = sqlite3.Row
        return conn

    def _close_connection(self, conn: sqlite3.Connection):
        if self.db_path != ":memory:":
            conn.close()

    def _init_db(self):
        with self._lock:
            try:
                conn = self._get_connection()
                try:
                    cursor = conn.cursor()
                    cursor.execute(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name='schema_version';"
                    )
                    has_schema_table = cursor.fetchone() is not None

                    if has_schema_table:
                        cursor.execute("SELECT version FROM schema_version LIMIT 1;")
                        row = cursor.fetchone()
                        version = row["version"] if row else 0
                        if version != CURRENT_SCHEMA_VERSION:
                            self._wipe_and_recreate(conn)
                    else:
                        self._create_tables(conn)
                    conn.commit()
                finally:
                    self._close_connection(conn)
            except (sqlite3.DatabaseError, sqlite3.OperationalError):
                self._is_corrupted = True
                self._reset_and_recreate_db()

    def _create_tables(self, conn: sqlite3.Connection):
        cursor = conn.cursor()
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS schema_version (
                version INTEGER PRIMARY KEY
            );
            """
        )
        cursor.execute("DELETE FROM schema_version;")
        cursor.execute("INSERT INTO schema_version (version) VALUES (?);", (CURRENT_SCHEMA_VERSION,))

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS folders (
                folder_id TEXT PRIMARY KEY,
                parent_id TEXT,
                name TEXT NOT NULL,
                type_default INTEGER DEFAULT 0,
                version INTEGER DEFAULT 0,
                update_token TEXT
            );
            """
        )

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS items (
                item_id TEXT PRIMARY KEY,
                folder_id TEXT NOT NULL,
                name TEXT NOT NULL,
                asset_id TEXT NOT NULL,
                type INTEGER DEFAULT 0,
                inv_type INTEGER DEFAULT 0,
                flags INTEGER DEFAULT 0,
                creation_date INTEGER DEFAULT 0,
                updated_at REAL NOT NULL,
                FOREIGN KEY (folder_id) REFERENCES folders(folder_id) ON DELETE CASCADE
            );
            """
        )

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS update_tokens (
                token_id TEXT PRIMARY KEY,
                token_value TEXT NOT NULL,
                last_synced REAL NOT NULL
            );
            """
        )

        cursor.execute("CREATE INDEX IF NOT EXISTS idx_folders_parent ON folders(parent_id);")
        cursor.execute("CREATE INDEX IF NOT EXISTS idx_items_folder ON items(folder_id);")

    def _wipe_and_recreate(self, conn: sqlite3.Connection):
        cursor = conn.cursor()
        cursor.execute("DROP TABLE IF EXISTS items;")
        cursor.execute("DROP TABLE IF EXISTS folders;")
        cursor.execute("DROP TABLE IF EXISTS update_tokens;")
        cursor.execute("DROP TABLE IF EXISTS schema_version;")
        self._create_tables(conn)

    def _reset_and_recreate_db(self):
        if self.db_path != ":memory:" and os.path.exists(self.db_path):
            try:
                os.remove(self.db_path)
            except OSError:
                pass
        elif hasattr(self, "_memory_conn") and self._memory_conn:
            try:
                self._memory_conn.close()
            except Exception:
                pass
            self._memory_conn = None
        conn = self._get_connection()
        try:
            self._create_tables(conn)
            conn.commit()
            self._is_corrupted = False
        finally:
            self._close_connection(conn)

    def is_corrupted(self) -> bool:
        return self._is_corrupted

    def load_cached_inventory(self) -> Dict[str, Any]:
        """
        Immediately loads cached folder structures and item metadata from local SQLite storage on startup.
        Returns dict containing 'folders' and 'items'.
        """
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM folders;")
                folders = [dict(row) for row in cursor.fetchall()]

                cursor.execute("SELECT * FROM items;")
                items = [dict(row) for row in cursor.fetchall()]

                cursor.execute("SELECT token_value FROM update_tokens WHERE token_id='default';")
                token_row = cursor.fetchone()
                update_token = token_row["token_value"] if token_row else None

                return {
                    "folders": folders,
                    "items": items,
                    "update_token": update_token,
                    "folder_count": len(folders),
                    "item_count": len(items)
                }
            except (sqlite3.DatabaseError, sqlite3.OperationalError):
                self._is_corrupted = True
                return {"folders": [], "items": [], "update_token": None, "folder_count": 0, "item_count": 0}
            finally:
                self._close_connection(conn)

    def get_update_token(self, token_id: str = "default") -> Optional[str]:
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                cursor.execute("SELECT token_value FROM update_tokens WHERE token_id=?;", (token_id,))
                row = cursor.fetchone()
                return row["token_value"] if row else None
            except sqlite3.DatabaseError:
                return None
            finally:
                self._close_connection(conn)

    def apply_delta_update(self, delta_data: Dict[str, Any], new_token: str, token_id: str = "default") -> bool:
        """
        Applies HTTP delta updates (added/updated/removed folders and items) to SQLite cache.
        """
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                now = time.time()

                for folder in delta_data.get("folders_to_add_or_update", []):
                    cursor.execute(
                        """
                        INSERT INTO folders (folder_id, parent_id, name, type_default, version, update_token)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT(folder_id) DO UPDATE SET
                            parent_id=excluded.parent_id,
                            name=excluded.name,
                            type_default=excluded.type_default,
                            version=excluded.version,
                            update_token=excluded.update_token;
                        """,
                        (
                            folder["folder_id"],
                            folder.get("parent_id"),
                            folder["name"],
                            folder.get("type_default", 0),
                            folder.get("version", 0),
                            folder.get("update_token")
                        )
                    )

                for folder_id in delta_data.get("folders_to_remove", []):
                    cursor.execute("DELETE FROM folders WHERE folder_id=?;", (folder_id,))

                for item in delta_data.get("items_to_add_or_update", []):
                    cursor.execute(
                        """
                        INSERT INTO items (item_id, folder_id, name, asset_id, type, inv_type, flags, creation_date, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT(item_id) DO UPDATE SET
                            folder_id=excluded.folder_id,
                            name=excluded.name,
                            asset_id=excluded.asset_id,
                            type=excluded.type,
                            inv_type=excluded.inv_type,
                            flags=excluded.flags,
                            creation_date=excluded.creation_date,
                            updated_at=excluded.updated_at;
                        """,
                        (
                            item["item_id"],
                            item["folder_id"],
                            item["name"],
                            item["asset_id"],
                            item.get("type", 0),
                            item.get("inv_type", 0),
                            item.get("flags", 0),
                            item.get("creation_date", 0),
                            now
                        )
                    )

                for item_id in delta_data.get("items_to_remove", []):
                    cursor.execute("DELETE FROM items WHERE item_id=?;", (item_id,))

                cursor.execute(
                    """
                    INSERT INTO update_tokens (token_id, token_value, last_synced)
                    VALUES (?, ?, ?)
                    ON CONFLICT(token_id) DO UPDATE SET
                        token_value=excluded.token_value,
                        last_synced=excluded.last_synced;
                    """,
                    (token_id, new_token, now)
                )

                conn.commit()
                return True
            except (sqlite3.DatabaseError, sqlite3.OperationalError, KeyError):
                conn.rollback()
                return False
            finally:
                self._close_connection(conn)

    def reload_full_inventory(self, full_data: Dict[str, Any], new_token: str, token_id: str = "default") -> bool:
        """
        Clears existing cache and replaces with full HTTP sync data (used on fallback or initial sync).
        """
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                now = time.time()

                cursor.execute("DELETE FROM items;")
                cursor.execute("DELETE FROM folders;")

                for folder in full_data.get("folders", []):
                    cursor.execute(
                        """
                        INSERT INTO folders (folder_id, parent_id, name, type_default, version, update_token)
                        VALUES (?, ?, ?, ?, ?, ?);
                        """,
                        (
                            folder["folder_id"],
                            folder.get("parent_id"),
                            folder["name"],
                            folder.get("type_default", 0),
                            folder.get("version", 0),
                            folder.get("update_token")
                        )
                    )

                for item in full_data.get("items", []):
                    cursor.execute(
                        """
                        INSERT INTO items (item_id, folder_id, name, asset_id, type, inv_type, flags, creation_date, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                        """,
                        (
                            item["item_id"],
                            item["folder_id"],
                            item["name"],
                            item["asset_id"],
                            item.get("type", 0),
                            item.get("inv_type", 0),
                            item.get("flags", 0),
                            item.get("creation_date", 0),
                            now
                        )
                    )

                cursor.execute(
                    """
                    INSERT INTO update_tokens (token_id, token_value, last_synced)
                    VALUES (?, ?, ?)
                    ON CONFLICT(token_id) DO UPDATE SET
                        token_value=excluded.token_value,
                        last_synced=excluded.last_synced;
                    """,
                    (token_id, new_token, now)
                )

                conn.commit()
                return True
            except (sqlite3.DatabaseError, sqlite3.OperationalError, KeyError):
                conn.rollback()
                return False
            finally:
                self._close_connection(conn)

    def get_item(self, item_id: str) -> Optional[Dict[str, Any]]:
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM items WHERE item_id=?;", (item_id,))
                row = cursor.fetchone()
                return dict(row) if row else None
            finally:
                self._close_connection(conn)

    def get_folder_items(self, folder_id: str) -> List[Dict[str, Any]]:
        with self._lock:
            conn = self._get_connection()
            try:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM items WHERE folder_id=?;", (folder_id,))
                return [dict(row) for row in cursor.fetchall()]
            finally:
                self._close_connection(conn)
