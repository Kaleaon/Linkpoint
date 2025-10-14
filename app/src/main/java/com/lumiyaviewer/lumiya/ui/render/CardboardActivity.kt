package com.lumiyaviewer.lumiya.ui.render

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Rect
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import androidx.preference.PreferenceManager
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTouch
import com.google.common.base.Objects
import com.google.common.base.Predicate
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.eventbus.Subscribe
import com.google.common.util.concurrent.AtomicDouble
import com.google.vr.cardboard.FullscreenMode
import com.google.vr.sdk.base.*
import com.google.vr.sdk.controller.Controller
import com.google.vr.sdk.controller.ControllerManager
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper
import com.lumiyaviewer.lumiya.*
import com.lumiyaviewer.lumiya.react.*
import com.lumiyaviewer.lumiya.render.*
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo
import com.lumiyaviewer.lumiya.slproto.*
import com.lumiyaviewer.lumiya.slproto.chat.*
import com.lumiyaviewer.lumiya.slproto.chat.generic.*
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl
import com.lumiyaviewer.lumiya.slproto.objects.*
import com.lumiyaviewer.lumiya.slproto.types.CameraParams
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
import com.lumiyaviewer.lumiya.slproto.users.manager.*
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity
import com.lumiyaviewer.lumiya.ui.render.CardboardControlsPlaceholder
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Converted from Java to Kotlin
 * CardboardActivity - VR Cardboard activity for 3D rendering
 */
@TargetApi(16)
class CardboardActivity : DetailsActivity(), ObjectPopupsManager.ObjectPopupListener, GvrView.StereoRenderer {

    companion object {
        private const val DEFAULT_FONT_SIZE_SP = 16
        private const val LISTVIEW_SCROLL_DURATION = 500
        private const val LISTVIEW_SCROLL_OFFSET = 100
        private const val RECYCLERVIEW_SCROLL_OFFSET = 100
        private const val VOICE_VIEW_HEIGHT_ALLOWANCE_DP = 60.0f
        const val VR_MODE_TAG = "vrMode"
        private const val controlDrawSizeFactor = 1.5f
        private const val controlSizeFactorX = 1.0f
        private const val controlSizeFactorY = 0.75f
        private const val crosshairSize = 0.1f
        
        private val dialogButtonIds = intArrayOf(
            R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4,
            R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8,
            R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12
        )
    }

    // UI Components (ButterKnife bindings converted to lateinit var)
    @BindView(R.id.buttonChat) lateinit var buttonChat: ImageButton
    @BindView(R.id.buttonMoveBackward) lateinit var buttonMoveBackward: ImageButton
    @BindView(R.id.buttonMoveForward) lateinit var buttonMoveForward: ImageButton
    @BindView(R.id.buttonObjectChat) lateinit var buttonObjectChat: ImageButton
    @BindView(R.id.buttonSit) lateinit var buttonSit: ImageButton
    @BindView(R.id.buttonSpeak) lateinit var buttonSpeak: ImageButton
    @BindView(R.id.buttonSpeechSend) lateinit var buttonSpeechSend: ImageButton
    @BindView(R.id.buttonStandUp) lateinit var buttonStandUp: ImageButton
    @BindView(R.id.buttonTouch) lateinit var buttonTouch: ImageButton
    @BindView(R.id.buttonTouchObject) lateinit var buttonTouchObject: ImageButton
    @BindView(R.id.buttonTurnLeft) lateinit var buttonTurnLeft: ImageButton

    private var activeScriptDialog: SLChatScriptDialog? = null
    private var activeYesNoEvent: SLChatYesNoEvent? = null
    private val agentCircuit: SubscriptionData<UUID, SLAgentCircuit> = SubscriptionData(UIThreadExecutor.getInstance())
    private val avatarControl: AtomicReference<SLAvatarControl> = AtomicReference()
    
    private lateinit var gvrView: GvrView
    private var worldViewRenderer: WorldViewRenderer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardboard)
        ButterKnife.bind(this)
        
        initializeVRView()
    }
    
    private fun initializeVRView() {
        gvrView = findViewById(R.id.gvr_view)
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8)
        gvrView.setRenderer(this)
        gvrView.setTransitionViewEnabled(true)
        gvrView.setOnCardboardBackButtonListener { onBackPressed() }
    }

    // GvrView.StereoRenderer implementation
    override fun onNewFrame(headTransform: HeadTransform) {
        worldViewRenderer?.onNewFrame(headTransform)
    }

    override fun onDrawEye(eye: Eye) {
        worldViewRenderer?.onDrawEye(eye)
    }

    override fun onFinishFrame(viewport: Viewport) {
        worldViewRenderer?.onFinishFrame(viewport)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        worldViewRenderer?.onSurfaceChanged(width, height)
    }

    override fun onSurfaceCreated(config: EGLConfig) {
        worldViewRenderer?.onSurfaceCreated(config)
    }

    override fun onRendererShutdown() {
        worldViewRenderer?.onRendererShutdown()
    }

    // ObjectPopupListener implementation
    override fun onObjectPopup(objectInfo: SLObjectInfo) {
        // Handle object popup
    }

    override fun onResume() {
        super.onResume()
        gvrView.onResume()
    }

    override fun onPause() {
        gvrView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        worldViewRenderer?.cleanup()
        super.onDestroy()
    }
}
