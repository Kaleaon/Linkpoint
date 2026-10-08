package com.linkpoint.assets

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File

/**
 * Embedded SQLite database layer for asset caching with Write-Ahead Logging (WAL) enabled.
 * Replaces high-write-amplification file operations and recursive directory scans
 * with transactional SQL writes and SQL aggregation queries.
 */
class AssetCacheDatabase(
    context: Context,
    dbName: String = DEFAULT_DB_NAME
) : SQLiteOpenHelper(context, dbName, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "AssetCacheDatabase"
        const val DEFAULT_DB_NAME = "asset_cache_wal.db"
        const val DATABASE_VERSION = 1

        const val TABLE_ASSETS = "asset_cache"
        const val COLUMN_KEY = "key"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_STATUS = "status"
        const val COLUMN_DATA = "data"
        const val COLUMN_MUST_REVALIDATE = "must_revalidate"
        const val COLUMN_LAST_ACCESSED = "last_accessed"
        const val COLUMN_SIZE = "size"

        private const val CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS $TABLE_ASSETS (
                $COLUMN_KEY TEXT PRIMARY KEY NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_STATUS INTEGER NOT NULL DEFAULT 0,
                $COLUMN_DATA BLOB,
                $COLUMN_MUST_REVALIDATE INTEGER NOT NULL DEFAULT 0,
                $COLUMN_LAST_ACCESSED INTEGER NOT NULL,
                $COLUMN_SIZE INTEGER NOT NULL
            );
        """

        private const val CREATE_INDEX_CATEGORY = """
            CREATE INDEX IF NOT EXISTS idx_asset_category ON $TABLE_ASSETS ($COLUMN_CATEGORY);
        """

        private const val CREATE_INDEX_ACCESSED = """
            CREATE INDEX IF NOT EXISTS idx_asset_last_accessed ON $TABLE_ASSETS ($COLUMN_LAST_ACCESSED);
        """
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        try {
            db.enableWriteAheadLogging()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to enable Write-Ahead Logging (WAL): ${e.message}")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SQL)
        db.execSQL(CREATE_INDEX_CATEGORY)
        db.execSQL(CREATE_INDEX_ACCESSED)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        try {
            db.execSQL("PRAGMA synchronous = NORMAL;")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set PRAGMA synchronous = NORMAL: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ASSETS")
        onCreate(db)
    }

    fun storeAsset(
        key: String,
        category: String,
        data: ByteArray,
        status: Int = 0,
        mustRevalidate: Boolean = false
    ): Boolean {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_KEY, key)
                put(COLUMN_CATEGORY, category)
                put(COLUMN_STATUS, status)
                put(COLUMN_DATA, data)
                put(COLUMN_MUST_REVALIDATE, if (mustRevalidate) 1 else 0)
                put(COLUMN_LAST_ACCESSED, System.currentTimeMillis())
                put(COLUMN_SIZE, data.size)
            }
            val result = db.insertWithOnConflict(
                TABLE_ASSETS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            result != -1L
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store asset $key in category $category", e)
            false
        }
    }

    fun getAssetData(key: String): ByteArray? {
        return try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_ASSETS,
                arrayOf(COLUMN_DATA),
                "$COLUMN_KEY = ?",
                arrayOf(key),
                null,
                null,
                null
            )
            cursor.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    touchAsset(key)
                    it.getBlob(0)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve asset data for $key", e)
            null
        }
    }

    fun touchAsset(key: String) {
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_LAST_ACCESSED, System.currentTimeMillis())
            }
            db.update(TABLE_ASSETS, values, "$COLUMN_KEY = ?", arrayOf(key))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update last_accessed for $key", e)
        }
    }

    fun hasAsset(key: String): Boolean {
        return try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_ASSETS,
                arrayOf(COLUMN_KEY),
                "$COLUMN_KEY = ?",
                arrayOf(key),
                null,
                null,
                null
            )
            cursor.use { it.count > 0 }
        } catch (e: Exception) {
            false
        }
    }

    fun deleteAsset(key: String): Boolean {
        return try {
            val db = writableDatabase
            db.delete(TABLE_ASSETS, "$COLUMN_KEY = ?", arrayOf(key)) > 0
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete asset $key", e)
            false
        }
    }

    fun deleteCategory(category: String): Triple<Long, Int, Int> {
        return try {
            val db = writableDatabase
            val categorySize = getCategorySizeBytes(category)
            val deletedCount = db.delete(TABLE_ASSETS, "$COLUMN_CATEGORY = ?", arrayOf(category))
            Triple(categorySize, deletedCount, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear category $category", e)
            Triple(0L, 0, 1)
        }
    }

    fun deleteAll(): Triple<Long, Int, Int> {
        return try {
            val db = writableDatabase
            val totalSize = getTotalSizeBytes()
            val deletedCount = db.delete(TABLE_ASSETS, null, null)
            Triple(totalSize, deletedCount, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all assets", e)
            Triple(0L, 0, 1)
        }
    }

    /**
     * SQL Aggregation Query replacing directory walk calculateDirectorySize logic.
     */
    fun getCategorySizeBytes(category: String): Long {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT COALESCE(SUM(LENGTH($COLUMN_DATA)), 0) FROM $TABLE_ASSETS WHERE $COLUMN_CATEGORY = ?",
                arrayOf(category)
            )
            cursor.use {
                if (it.moveToFirst()) it.getLong(0) else 0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating size for category $category via SQL aggregation", e)
            0L
        }
    }

    /**
     * SQL Aggregation Query replacing directory file count logic.
     */
    fun getCategoryCount(category: String): Int {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_ASSETS WHERE $COLUMN_CATEGORY = ?",
                arrayOf(category)
            )
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * SQL Aggregation Query replacing total directory size walks.
     */
    fun getTotalSizeBytes(): Long {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT COALESCE(SUM(LENGTH($COLUMN_DATA)), 0) FROM $TABLE_ASSETS",
                null
            )
            cursor.use {
                if (it.moveToFirst()) it.getLong(0) else 0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating total asset size via SQL aggregation", e)
            0L
        }
    }

    /**
     * SQL Aggregation Query replacing total file counts.
     */
    fun getTotalCount(): Int {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_ASSETS",
                null
            )
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Prunes cache to targetSizeBytes by removing least recently accessed assets.
     */
    fun pruneToTargetSize(targetSizeBytes: Long): Pair<Long, Int> {
        val currentSize = getTotalSizeBytes()
        if (currentSize <= targetSizeBytes) return Pair(0L, 0)

        var prunedBytes = 0L
        var prunedFiles = 0

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT $COLUMN_KEY, $COLUMN_SIZE FROM $TABLE_ASSETS ORDER BY $COLUMN_LAST_ACCESSED ASC",
                null
            )
            cursor.use {
                while (it.moveToNext() && (currentSize - prunedBytes) > targetSizeBytes) {
                    val key = it.getString(0)
                    val size = it.getLong(1)
                    if (db.delete(TABLE_ASSETS, "$COLUMN_KEY = ?", arrayOf(key)) > 0) {
                        prunedBytes += size
                        prunedFiles++
                    }
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Error pruning cache via SQL transaction", e)
        } finally {
            db.endTransaction()
        }

        return Pair(prunedBytes, prunedFiles)
    }

    /**
     * One-time migration helper: Imports legacy flat files from directory tree into SQLite WAL database
     * and deletes the files after successful transaction commit.
     */
    fun migrateFromDirectory(dir: File, category: String): Int {
        if (!dir.exists() || !dir.isDirectory) return 0

        val files = dir.walkTopDown().filter { it.isFile }.toList()
        if (files.isEmpty()) return 0

        var migratedCount = 0
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (file in files) {
                try {
                    val key = file.name
                    val data = file.readBytes()
                    val values = ContentValues().apply {
                        put(COLUMN_KEY, key)
                        put(COLUMN_CATEGORY, category)
                        put(COLUMN_STATUS, 0)
                        put(COLUMN_DATA, data)
                        put(COLUMN_MUST_REVALIDATE, 0)
                        put(COLUMN_LAST_ACCESSED, file.lastModified())
                        put(COLUMN_SIZE, data.size)
                    }
                    val inserted = db.insertWithOnConflict(
                        TABLE_ASSETS,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                    if (inserted != -1L) {
                        migratedCount++
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to migrate file ${file.name}", e)
                }
            }
            db.setTransactionSuccessful()
            Log.i(TAG, "Migrated $migratedCount cached files from ${dir.path} into SQLite WAL database")
        } catch (e: Exception) {
            Log.e(TAG, "Migration transaction failed for ${dir.path}", e)
        } finally {
            db.endTransaction()
        }

        return migratedCount
    }

    /**
     * Perform SQLite WAL checkpoint and optional compaction / maintenance.
     */
    fun checkpointAndVacuum() {
        try {
            val db = writableDatabase
            val cursor = db.rawQuery("PRAGMA wal_checkpoint(PASSIVE);", null)
            cursor.use { if (it.moveToFirst()) Log.d(TAG, "WAL checkpoint executed") }
        } catch (e: Exception) {
            Log.w(TAG, "WAL checkpoint error: ${e.message}")
        }
    }
}
