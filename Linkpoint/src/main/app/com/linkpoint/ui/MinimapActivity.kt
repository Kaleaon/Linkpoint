package com.linkpoint.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Minimap activity showing region and nearby avatars
 */
class MinimapActivity : AppCompatActivity() {
    
    private lateinit var minimapView: MinimapView
    private lateinit var regionNameText: TextView
    private lateinit var positionText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minimap)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Minimap"
        
        minimapView = findViewById(R.id.minimapView)
        regionNameText = findViewById(R.id.regionNameText)
        positionText = findViewById(R.id.positionText)
        
        // Set mock data for demonstration
        regionNameText.text = "Welcome Island"
        positionText.text = "Position: (128, 128, 25)"
        
        // Add some mock avatars
        minimapView.avatars = listOf(
            AvatarMarker("You", 128f, 128f, true),
            AvatarMarker("Friend1", 100f, 150f, false),
            AvatarMarker("Friend2", 180f, 100f, false),
            AvatarMarker("Stranger", 200f, 200f, false),
        )
        
        minimapView.onTeleportClick = { x, y ->
            Toast.makeText(this, "Teleport to ($x, $y)?", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

data class AvatarMarker(
    val name: String,
    val x: Float,
    val y: Float,
    val isSelf: Boolean
)

class MinimapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    var avatars: List<AvatarMarker> = emptyList()
        set(value) {
            field = value
            invalidate()
        }
    
    var onTeleportClick: ((Int, Int) -> Unit)? = null
    
    private val regionSize = 256f
    
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#333333")
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    
    private val terrainPaint = Paint().apply {
        color = Color.parseColor("#2E7D32")
        style = Paint.Style.FILL
    }
    
    private val waterPaint = Paint().apply {
        color = Color.parseColor("#1565C0")
        style = Paint.Style.FILL
    }
    
    private val selfPaint = Paint().apply {
        color = Color.parseColor("#FFEB3B")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val avatarPaint = Paint().apply {
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        isAntiAlias = true
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val scale = minOf(width, height) / regionSize
        val offsetX = (width - regionSize * scale) / 2
        val offsetY = (height - regionSize * scale) / 2
        
        // Draw water background
        canvas.drawRect(offsetX, offsetY, offsetX + regionSize * scale, offsetY + regionSize * scale, waterPaint)
        
        // Draw some terrain (mock island)
        val terrainRect = RectF(
            offsetX + 50 * scale,
            offsetY + 50 * scale,
            offsetX + 206 * scale,
            offsetY + 206 * scale
        )
        canvas.drawRoundRect(terrainRect, 20 * scale, 20 * scale, terrainPaint)
        
        // Draw grid
        for (i in 0..8) {
            val pos = i * 32f * scale
            canvas.drawLine(offsetX + pos, offsetY, offsetX + pos, offsetY + regionSize * scale, gridPaint)
            canvas.drawLine(offsetX, offsetY + pos, offsetX + regionSize * scale, offsetY + pos, gridPaint)
        }
        
        // Draw avatars
        avatars.forEach { avatar ->
            val screenX = offsetX + avatar.x * scale
            val screenY = offsetY + (regionSize - avatar.y) * scale // Flip Y axis
            
            val paint = if (avatar.isSelf) selfPaint else avatarPaint
            val radius = if (avatar.isSelf) 12f else 8f
            
            canvas.drawCircle(screenX, screenY, radius, paint)
            
            // Draw direction indicator for self
            if (avatar.isSelf) {
                val dirLength = 20f
                val angle = Math.toRadians(45.0) // Mock heading
                val endX = screenX + (cos(angle) * dirLength).toFloat()
                val endY = screenY - (sin(angle) * dirLength).toFloat()
                canvas.drawLine(screenX, screenY, endX, endY, selfPaint)
            }
            
            // Draw name
            canvas.drawText(avatar.name, screenX + 15, screenY + 5, textPaint)
        }
        
        // Draw compass
        val compassX = width - 50f
        val compassY = 50f
        textPaint.textSize = 20f
        canvas.drawText("N", compassX - 5, compassY - 20, textPaint)
        canvas.drawText("S", compassX - 5, compassY + 35, textPaint)
        canvas.drawText("E", compassX + 20, compassY + 5, textPaint)
        canvas.drawText("W", compassX - 30, compassY + 5, textPaint)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val scale = minOf(width, height) / regionSize
            val offsetX = (width - regionSize * scale) / 2
            val offsetY = (height - regionSize * scale) / 2
            
            val regionX = ((event.x - offsetX) / scale).toInt()
            val regionY = (regionSize - (event.y - offsetY) / scale).toInt()
            
            if (regionX in 0..255 && regionY in 0..255) {
                onTeleportClick?.invoke(regionX, regionY)
            }
        }
        return true
    }
}
