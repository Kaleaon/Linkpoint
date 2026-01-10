package com.linkpoint.core

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages grid connections and configurations
 * Supports Second Life, OpenSim, and other compatible grids
 */
class GridManager(private val context: Context) {
    
    companion object {
        private const val TAG = "GridManager"
        
        // Built-in grids
        val GRIDS = listOf(
            GridInfo(
                id = "secondlife",
                name = "Second Life",
                loginUri = "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
                gridNick = "agni",
                isSecure = true
            ),
            GridInfo(
                id = "secondlife_beta",
                name = "Second Life Beta",
                loginUri = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi",
                gridNick = "aditi",
                isSecure = true
            ),
            GridInfo(
                id = "kitely",
                name = "Kitely",
                loginUri = "https://login.kitely.com/",
                gridNick = "kitely",
                isSecure = true
            )
        )
    }
    
    private val customGrids = mutableListOf<GridInfo>()
    private var selectedGrid: GridInfo = GRIDS[0]
    
    fun getAvailableGrids(): List<GridInfo> {
        return GRIDS + customGrids
    }
    
    fun getSelectedGrid(): GridInfo = selectedGrid
    
    fun selectGrid(gridId: String) {
        val grid = getAvailableGrids().find { it.id == gridId }
        if (grid != null) {
            selectedGrid = grid
            Log.i(TAG, "Selected grid: ${grid.name}")
        }
    }
    
    fun addCustomGrid(grid: GridInfo) {
        if (customGrids.none { it.id == grid.id }) {
            customGrids.add(grid)
            Log.i(TAG, "Added custom grid: ${grid.name}")
        }
    }
    
    fun removeCustomGrid(gridId: String) {
        customGrids.removeAll { it.id == gridId }
    }
    
    suspend fun testGridConnection(grid: GridInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            // Simple connectivity test
            val url = java.net.URL(grid.loginUri)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode in 200..499
        } catch (e: Exception) {
            Log.w(TAG, "Grid connection test failed: ${e.message}")
            false
        }
    }
}

/**
 * Grid configuration data
 */
data class GridInfo(
    val id: String,
    val name: String,
    val loginUri: String,
    val gridNick: String,
    val isSecure: Boolean = true,
    val helperUri: String? = null,
    val website: String? = null,
    val support: String? = null,
    val registerUri: String? = null,
    val passwordUri: String? = null
)
