"""
test_inventory_cache.py - Unit and Integration Tests for Inventory Cache.

Verifies SQLite persistent inventory cache, immediate launch loading,
HTTP delta updates, schema corruption recovery, query speeds, and thread safety.
"""

import gc
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

    def test_wal_mode_initialization(self):
        conn = self.cache._get_connection()
        try:
            cursor = conn.cursor()
            cursor.execute("PRAGMA journal_mode;")
            journal_mode = cursor.fetchone()[0]
            cursor.execute("PRAGMA synchronous;")
            sync_mode = cursor.fetchone()[0]
            self.assertEqual(str(journal_mode).lower(), "wal")
            self.assertIn(sync_mode, (1, "1", "NORMAL", "normal"))
        finally:
            self.cache._close_connection(conn)

    def test_bulk_synchronization_speed_10k_items(self):
        folders = [{"folder_id": f"f_{i}", "parent_id": None, "name": f"Folder {i}"} for i in range(100)]
        items = [
            {
                "item_id": f"item_{i}",
                "folder_id": f"f_{i % 100}",
                "name": f"Inventory Item {i}",
                "asset_id": f"asset_{i}",
                "type": 5,
                "inv_type": 5,
                "flags": 0,
                "creation_date": 1600000000,
            }
            for i in range(10000)
        ]
        full_data = {"folders": folders, "items": items}

        gc.collect()
        start = time.time()
        success = self.cache.reload_full_inventory(full_data, new_token="token_10k")
        elapsed = time.time() - start

        self.assertTrue(success)
        self.assertLess(elapsed, 0.100, f"10k item sync took {elapsed * 1000:.2f}ms, expected under 100ms")

        loaded = self.cache.load_cached_inventory()
        self.assertEqual(loaded["folder_count"], 100)
        self.assertEqual(loaded["item_count"], 10000)
        self.assertEqual(loaded["update_token"], "token_10k")

    def test_non_blocking_concurrent_readers_during_background_writes(self):
        folders = [{"folder_id": f"f_{i}", "parent_id": None, "name": f"Folder {i}"} for i in range(100)]
        items = [
            {
                "item_id": f"item_bg_{i}",
                "folder_id": f"f_{i % 100}",
                "name": f"Background Item {i}",
                "asset_id": f"asset_bg_{i}",
            }
            for i in range(10000)
        ]
        full_data = {"folders": folders, "items": items}

        write_done = threading.Event()
        reader_durations = []
        errors = []

        def background_writer():
            try:
                self.cache.reload_full_inventory(full_data, new_token="bg_token")
            except Exception as e:
                errors.append(e)
            finally:
                write_done.set()

        writer_thread = threading.Thread(target=background_writer)
        writer_thread.start()

        time.sleep(0.002)  # Give writer a moment to start
        while not write_done.is_set():
            t0 = time.time()
            try:
                self.cache.get_item("item_bg_1")
                self.cache.get_folder_items("f_0")
                self.cache.get_update_token()
            except Exception as e:
                errors.append(e)
            dur = time.time() - t0
            reader_durations.append(dur)
            time.sleep(0.001)

        writer_thread.join()

        self.assertEqual(len(errors), 0, f"Errors during concurrent read/write: {errors}")
        self.assertGreater(len(reader_durations), 0, "Expected readers to execute during background write")
        for dur in reader_durations:
            self.assertLess(dur, 0.050, f"Reader query took {dur * 1000:.2f}ms, expected under 50ms without write locks")

    def test_wal_sidecar_file_cleanup(self):
        full_data = {
            "folders": [{"folder_id": "f1", "parent_id": None, "name": "Folder 1"}],
            "items": [{"item_id": "i1", "folder_id": "f1", "name": "Item 1", "asset_id": "a1"}],
        }
        self.cache.reload_full_inventory(full_data, new_token="tok1")

        wal_path = f"{self.db_path}-wal"
        shm_path = f"{self.db_path}-shm"

        self.cache.close()

        self.assertFalse(os.path.exists(wal_path), "WAL file was not cleaned up after close()")
        self.assertFalse(os.path.exists(shm_path), "SHM file was not cleaned up after close()")

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

    def test_cascading_deletes_and_deduplication(self):
        full_data = {
            "folders": [
                {"folder_id": "f_root", "parent_id": None, "name": "Root Folder"},
                {"folder_id": "f_sub", "parent_id": "f_root", "name": "Sub Folder"}
            ],
            "items": [
                {"item_id": "i_1", "folder_id": "f_sub", "name": "Item 1", "asset_id": "a1"},
                {"item_id": "i_2", "folder_id": "f_sub", "name": "Item 2", "asset_id": "a2"}
            ]
        }
        self.cache.reload_full_inventory(full_data, new_token="token_v1")
        self.assertEqual(len(self.cache.get_folder_items("f_sub")), 2)

        # Move item i_1 from f_sub to f_root
        delta_move = {
            "items_to_add_or_update": [{"item_id": "i_1", "folder_id": "f_root", "name": "Item 1", "asset_id": "a1"}]
        }
        self.cache.apply_delta_update(delta_move, new_token="token_v2")
        self.assertEqual(len(self.cache.get_folder_items("f_sub")), 1)
        self.assertEqual(len(self.cache.get_folder_items("f_root")), 1)

        # Cascading delete of folder f_sub
        delta_delete = {
            "folders_to_remove": ["f_sub"]
        }
        self.cache.apply_delta_update(delta_delete, new_token="token_v3")
        self.assertIsNone(self.cache.get_item("i_2"))  # Cascaded deletion
        self.assertIsNotNone(self.cache.get_item("i_1"))  # Preserved in f_root


if __name__ == "__main__":
    unittest.main()
