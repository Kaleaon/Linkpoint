package com.lumiyaviewer.lumiya.ui.render;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes.dex */
public class WorldSurfaceView extends GLSurfaceView {
    private static final int DEFAULT_FONT_SIZE_SP = 16;
    private final WorldViewActivity activity;
    @SuppressLint({"HandlerLeak"})
    private final Handler mHandler;
    private boolean ownAvatarHidden;
    private final WorldViewRenderer renderer;
    private final boolean supportsGL20;
    private boolean wantGL20;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WorldSurfaceView(WorldViewActivity worldViewActivity, UserManager userManager) {
        super(worldViewActivity);
        this.ownAvatarHidden = false;
        this.mHandler = new Handler() { // from class: com.lumiyaviewer.lumiya.ui.render.WorldSurfaceView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        if (message.obj == null || !(message.obj instanceof ObjectIntersectInfo)) {
                            return;
                        }
                        ObjectIntersectInfo objectIntersectInfo = (ObjectIntersectInfo) message.obj;
                        Debug.Log("UI!!! PICKED OBJECT isAvatar " + objectIntersectInfo.objInfo.isAvatar() + " local ID " + Integer.toString(objectIntersectInfo.objInfo.localID));
                        if (objectIntersectInfo.objInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) objectIntersectInfo.objInfo).isMyAvatar() : false) {
                            return;
                        }
                        WorldSurfaceView.this.activity.handlePickedObject(objectIntersectInfo);
                        return;
                    case 2:
                        if (message.obj == null || !(message.obj instanceof SLObjectInfo)) {
                            return;
                        }
                        WorldSurfaceView.this.activity.setTouchedObject((SLObjectInfo) message.obj);
                        return;
                    case 3:
                        WorldSurfaceView.this.activity.rendererSurfaceCreated();
                        return;
                    case 4:
                        WorldSurfaceView.this.activity.rendererShaderCompileError();
                        return;
                    case 5:
                        if (message.obj instanceof Bitmap) {
                            WorldSurfaceView.this.activity.processScreenshot((Bitmap) message.obj);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.activity = worldViewActivity;
        int applyDimension = (int) TypedValue.applyDimension(2, 16.0f, getResources().getDisplayMetrics());
        this.supportsGL20 = ((ActivityManager) getContext().getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= 131072;
        if (Debug.isDebugBuild()) {
            setDebugFlags(3);
        }
        this.wantGL20 = getWantGL20();
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(Build.VERSION.SDK_INT);
        objArr[1] = this.wantGL20 ? "yes" : "no";
        Debug.Printf("WorldSurfaceView: API level %d, wantGL20 %s", objArr);
        if (this.wantGL20) {
            setEGLContextClientVersion(2);
        }
        if (Build.VERSION.SDK_INT >= 11 && this.wantGL20) {
            setPreserveEGLContextOnPause(true);
        }
        this.renderer = new WorldViewRenderer(this.mHandler, this.wantGL20, userManager, applyDimension);
        setEGLContextFactory(this.renderer);
        setRenderer(this.renderer);
    }

    private boolean getWantGL20() {
        if (GlobalOptions.getInstance().getAdvancedRendering()) {
            return this.supportsGL20;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5461  reason: not valid java name */
    public /* synthetic */ void m803lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5461(int i) {
        this.renderer.setDisplayedHUDid(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5602  reason: not valid java name */
    public /* synthetic */ void m804lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5602(float f) {
        this.renderer.setHUDScaleFactor(f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5758  reason: not valid java name */
    public /* synthetic */ void m805lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5758(float f, float f2) {
        this.renderer.setHUDOffset(f, f2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5904  reason: not valid java name */
    public /* synthetic */ void m806lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5904(SLObjectInfo sLObjectInfo) {
        this.renderer.setDrawPickedObject(sLObjectInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6157  reason: not valid java name */
    public /* synthetic */ void m807lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6157(boolean z) {
        this.renderer.setOwnAvatarHidden(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6395  reason: not valid java name */
    public /* synthetic */ void m808lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6395(float f, float f2) {
        this.renderer.touchHUD(f, f2, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6612  reason: not valid java name */
    public /* synthetic */ void m809lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6612() {
        this.renderer.requestScreenshot(this.mHandler);
    }

    @Override // android.opengl.GLSurfaceView
    public void onPause() {
        Debug.Log("GLView: onPause () entered.");
        this.renderer.disableDrawing();
        Debug.Log("GLView: calling super.onPause ().");
        super.onPause();
        Debug.Log("GLView: onPause () exiting");
    }

    @Override // android.opengl.GLSurfaceView
    public void onResume() {
        super.onResume();
        if (this.wantGL20 != getWantGL20() && this.activity != null) {
            this.activity.rendererAdvancedRenderingChanged();
            return;
        }
        final WorldViewRenderer worldViewRenderer = this.renderer;
        worldViewRenderer.getClass();
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ
            private final /* synthetic */ void $m$0() {
                ((WorldViewRenderer) worldViewRenderer).enableDrawing();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pickObjectHover(float f, float f2) {
        this.renderer.pickObject(f, f2, this.mHandler);
    }

    public void setAvatarCountLimit(int i) {
        this.renderer.setAvatarCountLimit(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDisplayedHUDid(final int i) {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.3
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m803lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5461(i);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void setDrawDistance(int i) {
        this.renderer.setDrawDistance(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDrawPickedObject(final SLObjectInfo sLObjectInfo) {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.2
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m806lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5904((SLObjectInfo) sLObjectInfo);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setForcedTime(boolean z, float f) {
        if (this.renderer != null) {
            this.renderer.setForcedTime(z, f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHUDOffset(final float f, final float f2) {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.5
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m805lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5758(f, f2);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHUDScaleFactor(final float f) {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.4
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m804lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5602(f);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsInteracting(boolean z) {
        this.renderer.setIsInteracting(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOwnAvatarHidden(final boolean z) {
        if (this.ownAvatarHidden != z) {
            this.ownAvatarHidden = z;
            queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.7
                private final /* synthetic */ void $m$0() {
                    ((WorldSurfaceView) this).m807lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6157(z);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takeScreenshot() {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.1
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m809lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6612();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void touchHUD(final float f, final float f2) {
        queueEvent(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$WbegR8yVWPTDY8X58dwHEd9HRSQ.6
            private final /* synthetic */ void $m$0() {
                ((WorldSurfaceView) this).m808lambda$com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6395(f, f2);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }
}
