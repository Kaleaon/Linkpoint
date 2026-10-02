"""
test_inventory_cache.py - Unit and Integration Tests for Inventory Cache.

Verifies SQLite persistent inventory cache, immediate launch loading,
HTTP delta updates, schema corruption recovery, query speeds, and thread safety.
"""

import os
import tempfile
import threading
import time
import unittest
from inventory_cache import InventoryCache, CURRENT_SCHEMA_VERSION


class TestInventoryCache(unittest.TestCase):
    def setUp(self):
        self.temp_db = tempfile.NamedTemporaryFile(delete=False, suffix=".sqlite")
        self.temp_db.close()
        self.db_path = self.temp_db.name
        self.cache = InventoryCache(self.db_path)

    def tearDown(self):
        if os.path.exists(self.db_path):
            try:
                os.remove(self.db_path)
            except OSError:
                pass

    def test_immediate_launch_loading(self):
        # Empty launch load
        loaded = self.cache.load_cached_inventory()
        self.assertEqual(loaded["folder_count"], 0)
        self.assertEqual(loaded["item_count"], 0)
        self.assertIsNone(loaded["update_token"])

        # Populate full inventory
        full_data = {
            "folders": [
                {"folder_id": "f1", "parent_id": None, "name": "Objects", "type_default": 6},
                {"folder_id": "f2", "parent_id": "f1", "name": "Clothing", "type_default": 5}
            ],
            "items": [
                {"item_id": "i1", "folder_id": "f1", "name": "Shirt", "asset_id": "a1", "type": 5},
                {"item_id": "i2", "folder_id": "f2", "name": "Pants", "asset_id": "a2", "type": 5}
            ]
        }
        self.cache.reload_full_inventory(full_data, new_token="token_v1")

        # Create new cache instance pointing to same DB (simulating launch)
        new_cache_instance = InventoryCache(self.db_path)
        start_time = time.time()
        launch_loaded = new_cache_instance.load_cached_inventory()
        elapsed = time.time() - start_time

        self.assertEqual(launch_loaded["folder_count"], 2)
        self.assertEqual(launch_loaded["item_count"], 2)
        self.assertEqual(launch_loaded["update_token"], "token_v1")
        self.assertLess(elapsed, 0.05)  # Fast launch loading under 50ms

    def test_http_delta_updates(self):
        # Initial state
        full_data = {
            "folders": [{"folder_id": "f1", "parent_id": None, "name": "Root"}],
            "items": [{"item_id": "i1", "folder_id": "f1", "name": "Hat", "asset_id": "a1"}]
        }
        self.cache.reload_full_inventory(full_data, new_token="token_v1")

        # Apply delta update
        delta = {
            "folders_to_add_or_update": [{"folder_id": "f2", "parent_id": "f1", "name": "Subfolder"}],
            "items_to_add_or_update": [{"item_id": "i2", "folder_id": "f2", "name": "Shoes", "asset_id": "a2"}],
            "items_to_remove": ["i1"]
        }
        success = self.cache.apply_delta_update(delta, new_token="token_v2")
        self.assertTrue(success)

        self.assertEqual(self.cache.get_update_token(), "token_v2")
        self.assertIsNone(self.cache.get_item("i1"))  # Removed
        self.assertIsNotNone(self.cache.get_item("i2"))  # Added
        self.assertEqual(len(self.cache.get_folder_items("f2")), 1)

    def test_schema_mismatch_recovery(self):
        # Corrupt / modify schema_version table manually
        conn = self.cache._get_connection()
        conn.execute("UPDATE schema_version SET version = 999;")
        conn.commit()
        conn.close()

        # Initializing new instance should detect mismatch, wipe, and recreate cleanly
        new_cache = InventoryCache(self.db_path)
        loaded = new_cache.load_cached_inventory()
        self.assertEqual(loaded["folder_count"], 0)
        self.assertFalse(new_cache.is_corrupted())

    def test_database_corruption_recovery(self):
        # Overwrite file with corrupt bytes
        with open(self.db_path, "wb") as f:
            f.write(b"NOT_A_VALID_SQLITE_DATABASE_FILE")

        # Instantiating InventoryCache should handle corruption gracefully
        corrupt_cache = InventoryCache(self.db_path)
        loaded = corrupt_cache.load_cached_inventory()
        self.assertEqual(loaded["folder_count"], 0)

    def test_thread_safety(self):
        errors = []

        def worker_writer(thread_id):
            try:
                for i in range(20):
                    folder_id = f"f_{thread_id}_{i}"
                    item_id = f"i_{thread_id}_{i}"
                    delta = {
                        "folders_to_add_or_update": [{"folder_id": folder_id, "parent_id": None, "name": f"Folder {i}"}],
                        "items_to_add_or_update": [{"item_id": item_id, "folder_id": folder_id, "name": f"Item {i}", "asset_id": f"asset_{i}"}]
                    }
                    self.cache.apply_delta_update(delta, new_token=f"token_{thread_id}_{i}")
            except Exception as e:
                errors.append(e)

        def worker_reader():
            try:
                for _ in range(20):
                    self.cache.load_cached_inventory()
                    time.sleep(0.001)
            except Exception as e:
                errors.append(e)

        threads = []
        for t in range(3):
            threads.append(threading.Thread(target=worker_writer, args=(t,)))
            threads.append(threading.Thread(target=worker_reader))

        for thread in threads:
            thread.start()
        for thread in threads:
            thread.join()

        self.assertEqual(len(errors), 0, f"Thread safety errors encountered: {errors}")


if __name__ == "__main__":
    unittest.main()
