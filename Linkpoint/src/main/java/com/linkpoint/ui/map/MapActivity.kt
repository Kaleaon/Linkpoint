package com.linkpoint.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.MapPosition
import com.linkpoint.world.WorldMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * World map viewer with teleport capability
 */
class MapActivity : AppCompatActivity(), SurfaceHolder.Callback {
    
    private lateinit var surfaceView: SurfaceView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var zoomInButton: ImageButton
    private lateinit var zoomOutButton: ImageButton
    private lateinit var currentLocationButton: ImageButton
    private lateinit var teleportButton: Button
    private lateinit var regionNameText: TextView
    private lateinit var coordinatesText: TextView
    
    private lateinit var worldMap: WorldMap
    
    private var centerX = 1000 // Grid X center
    private var centerY = 1000 // Grid Y center
    private var zoomLevel = WorldMap.ZOOM_REGION
    
    private var selectedRegionX = -1
    private var selectedRegionY = -1
    private var selectedLocalX = 128f
    private var selectedLocalY = 128f
    
    private var mapTiles = mutableMapOf<String, Bitmap>()
    private var surfaceReady = false
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectionPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val avatarPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }
    
    private lateinit var gestureDetector: GestureDetector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        
        worldMap = (application as LinkpointApp).worldMap
        
        setupViews()
        setupGestures()
        
        // Center on current position
        worldMap.currentPosition.value?.let { pos ->
            centerX = pos.gridX
            centerY = pos.gridY
        }
    }
    
    private fun setupViews() {
        surfaceView = findViewById(R.id.mapSurface)
        surfaceView.holder.addCallback(this)
        
        searchInput = findViewById(R.id.searchInput)
        searchButton = findViewById(R.id.searchButton)
        zoomInButton = findViewById(R.id.zoomInButton)
        zoomOutButton = findViewById(R.id.zoomOutButton)
        currentLocationButton = findViewById(R.id.currentLocationButton)
        teleportButton = findViewById(R.id.teleportButton)
        regionNameText = findViewById(R.id.regionNameText)
        coordinatesText = findViewById(R.id.coordinatesText)
        
        searchButton.setOnClickListener { searchRegion() }
        zoomInButton.setOnClickListener { zoomIn() }
        zoomOutButton.setOnClickListener { zoomOut() }
        currentLocationButton.setOnClickListener { goToCurrentLocation() }
        teleportButton.setOnClickListener { teleportToSelected() }
        
        teleportButton.isEnabled = false
    }
    
    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                handleTap(e.x, e.y)
                return true
            }
            
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                handlePan(-distanceX, -distanceY)
                return true
            }
            
            override fun onDoubleTap(e: MotionEvent): Boolean {
                handleDoubleTap(e.x, e.y)
                return true
            }
        })
        
        surfaceView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
    
    private fun handleTap(x: Float, y: Float) {
        val width = surfaceView.width
        val height = surfaceView.height
        val tileSize = getTileSize()
        
        // Calculate which region was tapped
        val tilesX = (width / tileSize).toInt() + 2
        val tilesY = (height / tileSize).toInt() + 2
        
        val offsetX = (width - tilesX * tileSize) / 2
        val offsetY = (height - tilesY * tileSize) / 2
        
        val tileX = ((x - offsetX) / tileSize).toInt()
        val tileY = ((y - offsetY) / tileSize).toInt()
        
        val startX = centerX - tilesX / 2
        val startY = centerY - tilesY / 2
        
        selectedRegionX = startX + tileX
        selectedRegionY = startY + (tilesY - 1 - tileY) // Flip Y
        selectedLocalX = ((x - offsetX) % tileSize / tileSize * 256).coerceIn(0f, 256f)
        selectedLocalY = ((tileSize - (y - offsetY) % tileSize) / tileSize * 256).coerceIn(0f, 256f)
        
        regionNameText.text = "Region: $selectedRegionX, $selectedRegionY"
        coordinatesText.text = "Local: ${selectedLocalX.toInt()}, ${selectedLocalY.toInt()}, 0"
        teleportButton.isEnabled = true
        
        renderMap()
    }
    
    private fun handlePan(dx: Float, dy: Float) {
        val tileSize = getTileSize()
        
        centerX -= (dx / tileSize).toInt()
        centerY += (dy / tileSize).toInt() // Y is flipped
        
        loadVisibleTiles()
        renderMap()
    }
    
    private fun handleDoubleTap(x: Float, y: Float) {
        handleTap(x, y)
        zoomIn()
    }
    
    private fun zoomIn() {
        if (zoomLevel < 8) {
            zoomLevel++
            loadVisibleTiles()
            renderMap()
        }
    }
    
    private fun zoomOut() {
        if (zoomLevel > 1) {
            zoomLevel--
            loadVisibleTiles()
            renderMap()
        }
    }
    
    private fun goToCurrentLocation() {
        worldMap.currentPosition.value?.let { pos ->
            centerX = pos.gridX
            centerY = pos.gridY
            loadVisibleTiles()
            renderMap()
        }
    }
    
    private fun searchRegion() {
        val query = searchInput.text.toString()
        if (query.isEmpty()) return
        
        lifecycleScope.launch {
            val searchManager = (application as LinkpointApp).searchManager
            val results = searchManager.searchPlaces(query, "", 0, 10)
            
            if (results.results.isNotEmpty()) {
                val first = results.results.first()
                // Parse region coordinates
                // Center on result
                loadVisibleTiles()
                renderMap()
            } else {
                Toast.makeText(this@MapActivity, "No results found", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun teleportToSelected() {
        if (selectedRegionX < 0) return
        
        val protocol = (application as LinkpointApp).protocol
        
        lifecycleScope.launch {
            protocol.teleport(
                "Region $selectedRegionX,$selectedRegionY",
                selectedLocalX,
                selectedLocalY,
                0f
            )
            Toast.makeText(this@MapActivity, "Teleporting...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getTileSize(): Float {
        return when (zoomLevel) {
            1 -> 32f
            2 -> 64f
            3 -> 128f
            4 -> 256f
            else -> 128f
        }
    }
    
    private fun loadVisibleTiles() {
        val width = surfaceView.width
        val height = surfaceView.height
        val tileSize = getTileSize()
        
        val tilesX = (width / tileSize).toInt() + 2
        val tilesY = (height / tileSize).toInt() + 2
        
        val startX = centerX - tilesX / 2
        val startY = centerY - tilesY / 2
        
        lifecycleScope.launch {
            for (y in 0 until tilesY) {
                for (x in 0 until tilesX) {
                    val gridX = startX + x
                    val gridY = startY + y
                    val key = "$zoomLevel-$gridX-$gridY"
                    
                    if (!mapTiles.containsKey(key)) {
                        val tile = worldMap.getMapTile(gridX, gridY, zoomLevel)
                        if (tile != null) {
                            mapTiles[key] = tile
                            withContext(Dispatchers.Main) {
                                renderMap()
                            }
                        }
                    }
                }
            }
        }
    }
    
    private fun renderMap() {
        if (!surfaceReady) return
        
        val canvas = surfaceView.holder.lockCanvas() ?: return
        
        try {
            canvas.drawColor(Color.rgb(40, 60, 80)) // Ocean color
            
            val width = surfaceView.width
            val height = surfaceView.height
            val tileSize = getTileSize()
            
            val tilesX = (width / tileSize).toInt() + 2
            val tilesY = (height / tileSize).toInt() + 2
            
            val offsetX = (width - tilesX * tileSize) / 2
            val offsetY = (height - tilesY * tileSize) / 2
            
            val startX = centerX - tilesX / 2
            val startY = centerY - tilesY / 2
            
            // Draw tiles
            for (y in 0 until tilesY) {
                for (x in 0 until tilesX) {
                    val gridX = startX + x
                    val gridY = startY + (tilesY - 1 - y) // Flip Y
                    val key = "$zoomLevel-$gridX-$gridY"
                    
                    val screenX = offsetX + x * tileSize
                    val screenY = offsetY + y * tileSize
                    
                    mapTiles[key]?.let { tile ->
                        canvas.drawBitmap(
                            tile,
                            null,
                            android.graphics.RectF(
                                screenX, screenY,
                                screenX + tileSize, screenY + tileSize
                            ),
                            paint
                        )
                    }
                    
                    // Draw selection
                    if (gridX == selectedRegionX && gridY == selectedRegionY) {
                        canvas.drawRect(
                            screenX, screenY,
                            screenX + tileSize, screenY + tileSize,
                            selectionPaint
                        )
                    }
                }
            }
            
            // Draw current position marker
            worldMap.currentPosition.value?.let { pos ->
                val relX = pos.gridX - startX
                val relY = (tilesY - 1) - (pos.gridY - startY)
                
                if (relX in 0 until tilesX && relY in 0 until tilesY) {
                    val markerX = offsetX + relX * tileSize + pos.localX / 256f * tileSize
                    val markerY = offsetY + relY * tileSize + (256f - pos.localY) / 256f * tileSize
                    
                    canvas.drawCircle(markerX, markerY, 8f, avatarPaint)
                }
            }
            
        } finally {
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }
    
    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        loadVisibleTiles()
        renderMap()
    }
    
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        loadVisibleTiles()
        renderMap()
    }
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
    }
}
