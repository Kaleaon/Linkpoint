package com.linkpoint.grid.network

import android.util.Log
import com.linkpoint.grid.persistence.GridDirectoryDao
import com.linkpoint.grid.persistence.GridProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service that synchronizes grid directory metadata asynchronously from a central JSON API
 * into local SQLite storage upon application launch.
 */
class GridDirectorySync(
    private val dao: GridDirectoryDao,
    private val directoryApiUrl: String = DEFAULT_DIRECTORY_API_URL,
    private val client: OkHttpClient = defaultClient
) {

    companion object {
        private const val TAG = "GridDirectorySync"
        const val DEFAULT_DIRECTORY_API_URL = "https://directory.linkpoint.app/v1/grids"

        private val defaultClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    suspend fun syncGridDirectory(): SyncResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting background directory sync from $directoryApiUrl")
            val request = Request.Builder()
                .url(directoryApiUrl)
                .header("User-Agent", "Linkpoint-Viewer/2.0 GridDirectorySync")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Grid directory API returned HTTP status ${response.code}")
                return@withContext SyncResult.Failure("HTTP ${response.code}")
            }

            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty()) {
                Log.w(TAG, "Empty response from grid directory API")
                return@withContext SyncResult.Failure("Empty response body")
            }

            val jsonArray = JSONArray(responseBody)
            val syncedGrids = mutableListOf<GridProfileEntity>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", "")
                val name = obj.optString("name", "")
                val loginUri = obj.optString("loginUri", "")

                if (id.isNotBlank() && loginUri.isNotBlank()) {
                    syncedGrids.add(
                        GridProfileEntity(
                            id = id,
                            name = name.ifBlank { id },
                            gridNick = obj.optString("gridNick", id),
                            loginUri = loginUri,
                            helperUri = obj.optNullableString("helperUri"),
                            website = obj.optNullableString("website"),
                            support = obj.optNullableString("support"),
                            registerUri = obj.optNullableString("registerUri"),
                            passwordUri = obj.optNullableString("passwordUri"),
                            logoUrl = obj.optNullableString("logoUrl"),
                            status = obj.optString("status", "online"),
                            isCustom = false,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (syncedGrids.isNotEmpty()) {
                dao.insertGrids(syncedGrids)
                Log.i(TAG, "Successfully synchronized ${syncedGrids.size} grid profiles into SQLite cache")
                SyncResult.Success(syncedGrids.size)
            } else {
                Log.w(TAG, "No valid grid profiles parsed from API response")
                SyncResult.Failure("No profiles parsed")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Async grid directory sync failed gracefully (using local cache): ${e.message}")
            SyncResult.Failure(e.message ?: "Sync error")
        }
    }

    sealed class SyncResult {
        data class Success(val count: Int) : SyncResult()
        data class Failure(val reason: String) : SyncResult()
    }
}

internal fun JSONObject.optNullableString(key: String): String? {
    return if (this.has(key) && !this.isNull(key)) {
        val str = this.getString(key)
        if (str.isBlank()) null else str
    } else null
}
