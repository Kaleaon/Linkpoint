package com.linkpoint.ui.voice

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.linkpoint.LinkpointApp
import com.linkpoint.R

/**
 * Voice control widget for toggling voice chat
 */
class VoiceControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var voiceToggleButton: ImageButton
    private lateinit var voiceStatusText: TextView
    private lateinit var voiceMuteButton: ImageButton

    private val voiceManager by lazy { 
        LinkpointApp.getInstance().voiceManager 
    }

    var onVoiceToggleListener: ((Boolean) -> Unit)? = null
    var onMuteToggleListener: ((Boolean) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_voice_control, this, true)
        setupViews()
        updateVoiceUI()
    }

    private fun setupViews() {
        voiceToggleButton = findViewById(R.id.voice_toggle_button)
        voiceStatusText = findViewById(R.id.voice_status_text)
        voiceMuteButton = findViewById(R.id.voice_mute_button)

        voiceToggleButton.setOnClickListener {
            toggleVoice()
        }

        voiceMuteButton.setOnClickListener {
            toggleMute()
        }
    }

    private fun toggleVoice() {
        val isConnected = voiceManager.isConnected()
        if (isConnected) {
            voiceManager.disconnect()
            onVoiceToggleListener?.invoke(false)
        } else {
            voiceManager.connect()
            onVoiceToggleListener?.invoke(true)
        }
        updateVoiceUI()
    }

    private fun toggleMute() {
        val isMuted = voiceManager.isMuted()
        voiceManager.setMuted(!isMuted)
        onMuteToggleListener?.invoke(!isMuted)
        updateVoiceUI()
    }

    private fun updateVoiceUI() {
        val isConnected = voiceManager.isConnected()
        val isMuted = voiceManager.isMuted()

        // Update toggle button
        if (isConnected) {
            voiceToggleButton.setImageResource(R.drawable.ic_voice_on)
            voiceStatusText.text = if (isMuted) {
                "Voice (Muted)"
            } else {
                "Voice On"
            }
            voiceStatusText.setTextColor(
                ContextCompat.getColor(context, R.color.voice_connected)
            )
        } else {
            voiceToggleButton.setImageResource(R.drawable.ic_voice_off)
            voiceStatusText.text = "Voice Off"
            voiceStatusText.setTextColor(
                ContextCompat.getColor(context, R.color.voice_disconnected)
            )
        }

        // Update mute button
        if (isMuted) {
            voiceMuteButton.setImageResource(R.drawable.ic_mic_off)
        } else {
            voiceMuteButton.setImageResource(R.drawable.ic_mic_on)
        }
    }

    /**
     * Check if voice is connected
     */
    fun isVoiceConnected(): Boolean {
        return voiceManager.isConnected()
    }

    /**
     * Check if voice is muted
     */
    fun isVoiceMuted(): Boolean {
        return voiceManager.isMuted()
    }
}