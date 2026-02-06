package com.linkpoint.ui.minimap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Minimap Activity - Shows overhead view of the region
 * Based on the reference viewer's MinimapActivity
 */
class MinimapActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MinimapActivity"
        private const val REGION_SIZE = 256f
    }
    
    private lateinit var mapContainer: FrameLayout
    private lateinit var surfaceView: SurfaceView
    private lateinit var regionNameText: TextView
    private lateinit var coordinatesText: TextView
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    // Avatar position (simulated)
    private var avatarX = 128f
    private var avatarY = 128f
    private var avatarHeading = 0f
    
    // Map scale
    private var scale = 1.0f
    
    // Pan offset
    private var panX = 0f
    private var panY = 0f

    // Paints
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#1a472a") // Green land
        style = Paint.Style.FILL
    }
    
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#225533")
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    private val avatarPaint = Paint().apply {
        color = Color.parseColor("#00FF00")
        style = Paint.Style.FILL
    }
    
    private val objectPaint = Paint().apply {
        color = Color.parseColor("#888888")
        style = Paint.Style.FILL
    }
    
    private val agentPaint = Paint().apply {
        color = Color.parseColor("#00FFFF")
        style = Paint.Style.FILL
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        isAntiAlias = true
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minimap)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Minimap"
        }
        
        initViews()
        observeRegion()
    }
    
    private fun initViews() {
        mapContainer = findViewById(R.id.mapContainer)
        regionNameText = findViewById(R.id.regionNameText)
        coordinatesText = findViewById(R.id.coordinatesText)
        
        surfaceView = SurfaceView(this)
        mapContainer.addView(surfaceView)
        
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startMapLoop()
            }
            
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Calculate scale
                scale = minOf(width.toFloat(), height.toFloat()) / REGION_SIZE
            }
            
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
        
        // Touch handling for pan/zoom
        var lastTouchX = 0f
        var lastTouchY = 0f
        
        surfaceView.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    // Pan the map
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    
                    panX += dx
                    panY += dy
                    
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
            }
            true
        }
    }
    
    private fun observeRegion() {
        lifecycleScope.launch {
            app.sessionManager.currentRegion.collectLatest { region ->
                regionNameText.text = region?.name ?: "Unknown Region"
            }
        }
    }
    
    private fun startMapLoop() {
        surfaceView.post(object : Runnable {
            override fun run() {
                if (!isFinishing) {
                    drawMap()
                    updateCoordinates()
                    surfaceView.postDelayed(this, 100) // 10fps is enough for minimap
                }
            }
        })
    }
    
    private fun drawMap() {
        val holder = surfaceView.holder
        val canvas = holder.lockCanvas() ?: return
        
        try {
            val width = canvas.width.toFloat()
            val height = canvas.height.toFloat()
            val size = minOf(width, height)
            val offsetX = (width - size) / 2 + panX
            val offsetY = (height - size) / 2 + panY
            
            // Clear
            canvas.drawColor(Color.BLACK)
            
            // Draw region background
            canvas.drawRect(offsetX, offsetY, offsetX + size, offsetY + size, backgroundPaint)
            
            // Draw grid (every 16 meters)
            val gridStep = size / 16
            for (i in 0..16) {
                val pos = offsetX + i * gridStep
                canvas.drawLine(pos, offsetY, pos, offsetY + size, gridPaint)
                canvas.drawLine(offsetX, offsetY + i * gridStep, offsetX + size, offsetY + i * gridStep, gridPaint)
            }
            
            // Draw simulated objects
            drawObjects(canvas, offsetX, offsetY, size)
            
            // Draw other avatars
            drawOtherAvatars(canvas, offsetX, offsetY, size)
            
            // Draw self avatar
            drawAvatar(canvas, offsetX, offsetY, size)
            
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }
    
    private fun drawObjects(canvas: Canvas, offsetX: Float, offsetY: Float, size: Float) {
        // Simulated objects - would come from region data
        val objects = listOf(
            Pair(64f, 64f),
            Pair(192f, 64f),
            Pair(128f, 192f)
        )
        
        for ((objX, objY) in objects) {
            val screenX = offsetX + (objX / REGION_SIZE) * size
            val screenY = offsetY + ((REGION_SIZE - objY) / REGION_SIZE) * size
            canvas.drawRect(screenX - 4, screenY - 4, screenX + 4, screenY + 4, objectPaint)
        }
    }
    
    private fun drawOtherAvatars(canvas: Canvas, offsetX: Float, offsetY: Float, size: Float) {
        // Simulated other avatars - would come from region data
        val avatars = listOf(
            Triple(100f, 100f, "User1"),
            Triple(150f, 180f, "User2")
        )
        
        for ((avX, avY, name) in avatars) {
            val screenX = offsetX + (avX / REGION_SIZE) * size
            val screenY = offsetY + ((REGION_SIZE - avY) / REGION_SIZE) * size
            
            canvas.drawCircle(screenX, screenY, 6f, agentPaint)
        }
    }
    
    private fun drawAvatar(canvas: Canvas, offsetX: Float, offsetY: Float, size: Float) {
        val screenX = offsetX + (avatarX / REGION_SIZE) * size
        val screenY = offsetY + ((REGION_SIZE - avatarY) / REGION_SIZE) * size
        
        // Draw heading indicator (triangle)
        val headingRad = Math.toRadians(avatarHeading.toDouble()).toFloat()
        val arrowSize = 12f
        
        val tipX = screenX + sin(headingRad) * arrowSize
        val tipY = screenY - cos(headingRad) * arrowSize
        val leftX = screenX + sin(headingRad + 2.5f) * arrowSize * 0.5f
        val leftY = screenY - cos(headingRad + 2.5f) * arrowSize * 0.5f
        val rightX = screenX + sin(headingRad - 2.5f) * arrowSize * 0.5f
        val rightY = screenY - cos(headingRad - 2.5f) * arrowSize * 0.5f
        
        val path = android.graphics.Path()
        path.moveTo(tipX, tipY)
        path.lineTo(leftX, leftY)
        path.lineTo(rightX, rightY)
        path.close()
        
        canvas.drawPath(path, avatarPaint)
        
        // Draw name
        canvas.drawText("You", screenX + 10, screenY, textPaint)
    }
    
    private fun updateCoordinates() {
        coordinatesText.text = String.format("%.0f, %.0f, 0", avatarX, avatarY)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
