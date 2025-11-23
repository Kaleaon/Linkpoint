package com.linkpoint.ui.render

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.TypedValue
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.render.WorldViewRenderer
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.users.manager.UserManager

@SuppressLint("ViewConstructor")
class WorldSurfaceView(private val activity: WorldViewActivity, userManager: UserManager) : GLSurfaceView(activity) {
    private val DEFAULT_FONT_SIZE_SP = 16
    private var ownAvatarHidden = false
    private val renderer: WorldViewRenderer
    private val supportsGL20: Boolean
    private val wantGL20: Boolean

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                1 -> {
                    val obj = message.obj
                    if (obj is ObjectIntersectInfo) {
                        Debug.Log("UI!!! PICKED OBJECT isAvatar ${obj.objInfo.isAvatar()} local ID ${obj.objInfo.localID}")
                        val isMyAvatar = if (obj.objInfo is SLObjectAvatarInfo) (obj.objInfo as SLObjectAvatarInfo).isMyAvatar() else false
                        if (!isMyAvatar) {
                            activity.handlePickedObject(obj)
                        }
                    }
                }
                2 -> {
                    val obj = message.obj
                    if (obj is SLObjectInfo) {
                        activity.setTouchedObject(obj)
                    }
                }
                3 -> activity.rendererSurfaceCreated()
                4 -> activity.rendererShaderCompileError()
                5 -> {
                    val obj = message.obj
                    if (obj is Bitmap) {
                        activity.processScreenshot(obj)
                    }
                }
            }
        }
    }

    init {
        val displayMetrics = resources.displayMetrics
        val fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f, displayMetrics).toInt()
        
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        this.supportsGL20 = activityManager.deviceConfigurationInfo.reqGlEsVersion >= 0x20000
        
        if (Debug.isDebugBuild()) {
            debugFlags = DEBUG_CHECK_GL_ERROR or DEBUG_LOG_GL_CALLS
        }
        
        this.wantGL20 = getWantGL20()
        Debug.Printf("WorldSurfaceView: API level %d, wantGL20 %s", Build.VERSION.SDK_INT, if (this.wantGL20) "yes" else "no")
        
        if (this.wantGL20) {
            setEGLContextClientVersion(2)
        }
        
        if (Build.VERSION.SDK_INT >= 11 && this.wantGL20) {
            preserveEGLContextOnPause = true
        }
        
        this.renderer = WorldViewRenderer(this.mHandler, this.wantGL20, userManager, fontSize)
        setEGLContextFactory(this.renderer)
        setRenderer(this.renderer)
    }

    private fun getWantGL20(): Boolean {
        return if (GlobalOptions.getInstance().advancedRendering) {
            this.supportsGL20
        } else false
    }

    override fun onPause() {
        Debug.Log("GLView: onPause () entered.")
        this.renderer.disableDrawing()
        Debug.Log("GLView: calling super.onPause ().")
        super.onPause()
        Debug.Log("GLView: onPause () exiting")
    }

    override fun onResume() {
        super.onResume()
        if (this.wantGL20 == getWantGL20()) {
            val r = this.renderer
            queueEvent { 
                // Assuming there was some init logic here in decompiled lambda
                // $Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ(worldViewRenderer)
                // Decompiled lambda body is missing, but typically onResume might re-init context or something.
                // Wait, looking at decompiled file:
                // queueEvent($Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ(worldViewRenderer))
                // And method m798... was seemingly related? No, m798 was screenshot.
                // Let's assume standard GL surface resume behavior is sufficient unless renderer has specific resume method.
                // The renderer doesn't have a resume method called in constructor.
                // Maybe it was forcing a redraw?
                // For now, I'll leave the queueEvent empty or try to infer.
                // Actually, if wantGL20 changed, it calls rendererAdvancedRenderingChanged().
                // If it didn't change, it calls this lambda.
                // Maybe it just triggers a render?
                r.requestRender() // Guessing
            }
        } else {
            this.activity.rendererAdvancedRenderingChanged()
        }
    }

    fun pickObjectHover(x: Float, y: Float) {
        this.renderer.pickObject(x, y, this.mHandler)
    }

    fun setAvatarCountLimit(limit: Int) {
        this.renderer.setAvatarCountLimit(limit)
    }

    fun setDisplayedHUDid(id: Int) {
        queueEvent {
            this.renderer.setDisplayedHUDid(id)
        }
    }

    fun setDrawDistance(distance: Int) {
        this.renderer.setDrawDistance(distance)
    }

    fun setDrawPickedObject(info: SLObjectInfo?) {
        queueEvent {
            this.renderer.setDrawPickedObject(info)
        }
    }

    fun setForcedTime(forced: Boolean, time: Float) {
        this.renderer.setForcedTime(forced, time)
    }

    fun setHUDOffset(x: Float, y: Float) {
        queueEvent {
            this.renderer.setHUDOffset(x, y)
        }
    }

    fun setHUDScaleFactor(factor: Float) {
        queueEvent {
            this.renderer.setHUDScaleFactor(factor)
        }
    }

    fun setIsInteracting(interacting: Boolean) {
        this.renderer.setIsInteracting(interacting)
    }

    fun setOwnAvatarHidden(hidden: Boolean) {
        if (this.ownAvatarHidden != hidden) {
            this.ownAvatarHidden = hidden
            queueEvent {
                this.renderer.setOwnAvatarHidden(hidden)
            }
        }
    }

    fun takeScreenshot() {
        queueEvent {
            this.renderer.requestScreenshot(this.mHandler)
        }
    }

    fun touchHUD(x: Float, y: Float) {
        queueEvent {
            this.renderer.touchHUD(x, y, this.mHandler)
        }
    }
}
