// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import com.lumiyaviewer.lumiya.GlobalOptions;
import android.opengl.GLSurfaceView$Renderer;
import android.opengl.GLSurfaceView$EGLContextFactory;
import android.os.Build$VERSION;
import android.app.ActivityManager;
import android.util.TypedValue;
import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import android.os.Message;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;

@SuppressLint({ "ViewConstructor" })
public class WorldSurfaceView extends GLSurfaceView
{
    private static final int DEFAULT_FONT_SIZE_SP = 16;
    private final WorldViewActivity activity;
    @SuppressLint({ "HandlerLeak" })
    private final Handler mHandler;
    private boolean ownAvatarHidden;
    private final WorldViewRenderer renderer;
    private final boolean supportsGL20;
    private boolean wantGL20;
    
    WorldSurfaceView(final WorldViewActivity activity, final UserManager userManager) {
        super((Context)activity);
        this.ownAvatarHidden = false;
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    case 1: {
                        if (message.obj == null || !(message.obj instanceof ObjectIntersectInfo)) {
                            break;
                        }
                        final ObjectIntersectInfo objectIntersectInfo = (ObjectIntersectInfo)message.obj;
                        Debug.Log("UI!!! PICKED OBJECT isAvatar " + objectIntersectInfo.objInfo.isAvatar() + " local ID " + Integer.toString(objectIntersectInfo.objInfo.localID));
                        if (!(objectIntersectInfo.objInfo instanceof SLObjectAvatarInfo) || !((SLObjectAvatarInfo)objectIntersectInfo.objInfo).isMyAvatar()) {
                            WorldSurfaceView.this.activity.handlePickedObject(objectIntersectInfo);
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (message.obj != null && message.obj instanceof SLObjectInfo) {
                            WorldSurfaceView.this.activity.setTouchedObject((SLObjectInfo)message.obj);
                            break;
                        }
                        break;
                    }
                    case 3: {
                        WorldSurfaceView.this.activity.rendererSurfaceCreated();
                        break;
                    }
                    case 4: {
                        WorldSurfaceView.this.activity.rendererShaderCompileError();
                        break;
                    }
                    case 5: {
                        if (message.obj instanceof Bitmap) {
                            WorldSurfaceView.this.activity.processScreenshot((Bitmap)message.obj);
                            break;
                        }
                        break;
                    }
                }
            }
        };
        this.activity = activity;
        final int n = (int)TypedValue.applyDimension(2, 16.0f, this.getResources().getDisplayMetrics());
        this.supportsGL20 = (((ActivityManager)this.getContext().getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= 131072);
        if (Debug.isDebugBuild()) {
            this.setDebugFlags(3);
        }
        this.wantGL20 = this.getWantGL20();
        final int sdk_INT = Build$VERSION.SDK_INT;
        String s;
        if (this.wantGL20) {
            s = "yes";
        }
        else {
            s = "no";
        }
        Debug.Printf("WorldSurfaceView: API level %d, wantGL20 %s", sdk_INT, s);
        if (this.wantGL20) {
            this.setEGLContextClientVersion(2);
        }
        if (Build$VERSION.SDK_INT >= 11 && this.wantGL20) {
            this.setPreserveEGLContextOnPause(true);
        }
        this.setEGLContextFactory((GLSurfaceView$EGLContextFactory)(this.renderer = new WorldViewRenderer(this.mHandler, this.wantGL20, userManager, n)));
        this.setRenderer((GLSurfaceView$Renderer)this.renderer);
    }
    
    private boolean getWantGL20() {
        return GlobalOptions.getInstance().getAdvancedRendering() && this.supportsGL20;
    }
    
    public void onPause() {
        Debug.Log("GLView: onPause () entered.");
        this.renderer.disableDrawing();
        Debug.Log("GLView: calling super.onPause ().");
        super.onPause();
        Debug.Log("GLView: onPause () exiting");
    }
    
    public void onResume() {
        super.onResume();
        if (this.wantGL20 != this.getWantGL20() && this.activity != null) {
            this.activity.rendererAdvancedRenderingChanged();
        }
        else {
            final WorldViewRenderer renderer = this.renderer;
            renderer.getClass();
            this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ(renderer));
        }
    }
    
    void pickObjectHover(final float n, final float n2) {
        this.renderer.pickObject(n, n2, this.mHandler);
    }
    
    public void setAvatarCountLimit(final int avatarCountLimit) {
        this.renderer.setAvatarCountLimit(avatarCountLimit);
    }
    
    void setDisplayedHUDid(final int n) {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$3(n, this));
    }
    
    public void setDrawDistance(final int drawDistance) {
        this.renderer.setDrawDistance(drawDistance);
    }
    
    void setDrawPickedObject(final SLObjectInfo slObjectInfo) {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$2(this, slObjectInfo));
    }
    
    void setForcedTime(final boolean b, final float n) {
        if (this.renderer != null) {
            this.renderer.setForcedTime(b, n);
        }
    }
    
    void setHUDOffset(final float n, final float n2) {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$5(n, n2, this));
    }
    
    void setHUDScaleFactor(final float n) {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$4(n, this));
    }
    
    void setIsInteracting(final boolean isInteracting) {
        this.renderer.setIsInteracting(isInteracting);
    }
    
    void setOwnAvatarHidden(final boolean ownAvatarHidden) {
        if (this.ownAvatarHidden != ownAvatarHidden) {
            this.ownAvatarHidden = ownAvatarHidden;
            this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$7(ownAvatarHidden, this));
        }
    }
    
    void takeScreenshot() {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$1(this));
    }
    
    void touchHUD(final float n, final float n2) {
        this.queueEvent((Runnable)new _$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ$6(n, n2, this));
    }
}
