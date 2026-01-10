package com.linkpoint.ui.voice

import kotlin.math.*

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.linkpoint.voice.common.model.VoiceChatInfo
import java.util.UUID

/**
 * Converted from Java to Kotlin
 * VoiceStatusView - Custom view displaying voice chat status and audio levels
 */
class VoiceStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val BAR_COUNT = 10
        private const val BAR_SPACING = 4f
        private const val MAX_AUDIO_LEVEL = 100f
        private const val ANIMATION_DURATION_MS = 100L
    }

    data class ParticipantVoiceState(
        val participantUUID: UUID,
        val displayName: String,
        val audioLevel: Float,
        val isSpeaking: Boolean,
        val isMuted: Boolean
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val participants = mutableMapOf<UUID, ParticipantVoiceState>()
    private var voiceChatInfo: VoiceChatInfo? = null
    private var isVoiceEnabled = false
    private var isMicMuted = false
    private var currentAudioLevel = 0f
    private var targetAudioLevel = 0f

    private val barRect = RectF()
    private var barWidth = 0f
    private var barHeight = 0f

    init {
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateBarDimensions()
    }

    private fun calculateBarDimensions() {
        val totalSpacing = BAR_SPACING * (BAR_COUNT - 1)
        barWidth = (width - totalSpacing) / BAR_COUNT
        barHeight = height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isVoiceEnabled) {
            drawDisabledState(canvas)
            return
        }

        if (isMicMuted) {
            drawMutedState(canvas)
            return
        }

        drawAudioLevel(canvas, currentAudioLevel)
    }

    private fun drawDisabledState(canvas: Canvas) {
        paint.color = Color.GRAY
        paint.alpha = 64
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun drawMutedState(canvas: Canvas) {
        paint.color = Color.RED
        paint.alpha = 128
        
        for (i in 0 until BAR_COUNT) {
            val x = i * (barWidth + BAR_SPACING)
            barRect.set(x, 0f, x + barWidth, barHeight)
            canvas.drawRect(barRect, paint)
        }
    }

    private fun drawAudioLevel(canvas: Canvas, level: Float) {
        val normalizedLevel = level / MAX_AUDIO_LEVEL
        val activeBars = (BAR_COUNT * normalizedLevel).toInt()

        for (i in 0 until BAR_COUNT) {
            val x = i * (barWidth + BAR_SPACING)
            
            if (i < activeBars) {
                // Active bar - green gradient
                paint.color = getBarColor(i, BAR_COUNT)
                paint.alpha = 255
            } else {
                // Inactive bar - gray
                paint.color = Color.GRAY
                paint.alpha = 64
            }
            
            barRect.set(x, 0f, x + barWidth, barHeight)
            canvas.drawRect(barRect, paint)
        }
    }

    private fun getBarColor(barIndex: Int, totalBars: Int): Int {
        val ratio = barIndex.toFloat() / totalBars
        return when {
            ratio < 0.5f -> Color.GREEN
            ratio < 0.75f -> Color.YELLOW
            else -> Color.RED
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        if (this.isVoiceEnabled != enabled) {
            this.isVoiceEnabled = enabled
            invalidate()
        }
    }

    fun setMicMuted(muted: Boolean) {
        if (this.isMicMuted != muted) {
            this.isMicMuted = muted
            invalidate()
        }
    }

    fun setAudioLevel(level: Float) {
        targetAudioLevel = level.coerceIn(0f, MAX_AUDIO_LEVEL)
        animateToTargetLevel()
    }

    private fun animateToTargetLevel() {
        val diff = targetAudioLevel - currentAudioLevel
        val step = diff / 10f
        
        currentAudioLevel += step
        
        if (abs(diff) > 0.5f) {
            postDelayed({
                animateToTargetLevel()
            }, ANIMATION_DURATION_MS / 10)
        } else {
            currentAudioLevel = targetAudioLevel
        }
        
        invalidate()
    }

    fun setVoiceChatInfo(info: VoiceChatInfo) {
        this.voiceChatInfo = info
        invalidate()
    }

    fun addParticipant(state: ParticipantVoiceState) {
        participants[state.participantUUID] = state
        invalidate()
    }

    fun removeParticipant(participantUUID: UUID) {
        participants.remove(participantUUID)
        invalidate()
    }

    fun updateParticipant(state: ParticipantVoiceState) {
        participants[state.participantUUID] = state
        invalidate()
    }

    fun getParticipants(): List<ParticipantVoiceState> {
        return participants.values.toList()
    }

    fun clearParticipants() {
        participants.clear()
        invalidate()
    }

    fun isVoiceEnabled(): Boolean {
        return isVoiceEnabled
    }

    fun isMicMuted(): Boolean {
        return isMicMuted
    }

    fun getCurrentAudioLevel(): Float {
        return currentAudioLevel
    }
}
