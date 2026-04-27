// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
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

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

public class WorldSurfaceView extends GLSurfaceView
{

    private static final int DEFAULT_FONT_SIZE_SP = 16;
    private final WorldViewActivity activity;
    private final Handler mHandler = new Handler() {

        final WorldSurfaceView this$0;

        public void handleMessage(Message message)
        {
            message.what;
            JVM INSTR tableswitch 1 5: default 40
        //                       1 41
        //                       2 154
        //                       3 191
        //                       4 202
        //                       5 213;
               goto _L1 _L2 _L3 _L4 _L5 _L6
_L1:
            return;
_L2:
            if (message.obj != null && (message.obj instanceof ObjectIntersectInfo))
            {
                message = (ObjectIntersectInfo)message.obj;
                Debug.Log((new StringBuilder()).append("UI!!! PICKED OBJECT isAvatar ").append(((ObjectIntersectInfo) (message)).objInfo.isAvatar()).append(" local ID ").append(Integer.toString(((ObjectIntersectInfo) (message)).objInfo.localID)).toString());
                boolean flag1;
                if (((ObjectIntersectInfo) (message)).objInfo instanceof SLObjectAvatarInfo)
                {
                    flag1 = ((SLObjectAvatarInfo)((ObjectIntersectInfo) (message)).objInfo).isMyAvatar();
                } else
                {
                    flag1 = false;
                }
                if (!flag1)
                {
                    WorldSurfaceView._2D_get0(WorldSurfaceView.this).handlePickedObject(message);
                    return;
                }
            }
            continue; /* Loop/switch isn't completed */
_L3:
            if (message.obj != null && (message.obj instanceof SLObjectInfo))
            {
                message = (SLObjectInfo)message.obj;
                WorldSurfaceView._2D_get0(WorldSurfaceView.this).setTouchedObject(message);
                return;
            }
            if (true) goto _L1; else goto _L4
_L4:
            WorldSurfaceView._2D_get0(WorldSurfaceView.this).rendererSurfaceCreated();
            return;
_L5:
            WorldSurfaceView._2D_get0(WorldSurfaceView.this).rendererShaderCompileError();
            return;
_L6:
            if (message.obj instanceof Bitmap)
            {
                WorldSurfaceView._2D_get0(WorldSurfaceView.this).processScreenshot((Bitmap)message.obj);
                return;
            }
            if (true) goto _L1; else goto _L7
_L7:
        }

            
            {
                this$0 = WorldSurfaceView.this;
                super();
            }
    };
    private boolean ownAvatarHidden;
    private final WorldViewRenderer renderer;
    private final boolean supportsGL20;
    private boolean wantGL20;

    static void _2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_2D_mthref_2D_0(WorldViewRenderer worldviewrenderer)
    {
        worldviewrenderer.enableDrawing();
    }

    static WorldViewActivity _2D_get0(WorldSurfaceView worldsurfaceview)
    {
        return worldsurfaceview.activity;
    }

    WorldSurfaceView(WorldViewActivity worldviewactivity, UserManager usermanager)
    {
        super(worldviewactivity);
        ownAvatarHidden = false;
        activity = worldviewactivity;
        int i = (int)TypedValue.applyDimension(2, 16F, getResources().getDisplayMetrics());
        int j;
        boolean flag;
        if (((ActivityManager)getContext().getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        supportsGL20 = flag;
        if (Debug.isDebugBuild())
        {
            setDebugFlags(3);
        }
        wantGL20 = getWantGL20();
        j = android.os.Build.VERSION.SDK_INT;
        if (wantGL20)
        {
            worldviewactivity = "yes";
        } else
        {
            worldviewactivity = "no";
        }
        Debug.Printf("WorldSurfaceView: API level %d, wantGL20 %s", new Object[] {
            Integer.valueOf(j), worldviewactivity
        });
        if (wantGL20)
        {
            setEGLContextClientVersion(2);
        }
        if (android.os.Build.VERSION.SDK_INT >= 11 && wantGL20)
        {
            setPreserveEGLContextOnPause(true);
        }
        renderer = new WorldViewRenderer(mHandler, wantGL20, usermanager, i);
        setEGLContextFactory(renderer);
        setRenderer(renderer);
    }

    private boolean getWantGL20()
    {
        if (GlobalOptions.getInstance().getAdvancedRendering())
        {
            return supportsGL20;
        } else
        {
            return false;
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5461(int i)
    {
        renderer.setDisplayedHUDid(i);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5602(float f)
    {
        renderer.setHUDScaleFactor(f);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5758(float f, float f1)
    {
        renderer.setHUDOffset(f, f1);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_5904(SLObjectInfo slobjectinfo)
    {
        renderer.setDrawPickedObject(slobjectinfo);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6157(boolean flag)
    {
        renderer.setOwnAvatarHidden(flag);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6395(float f, float f1)
    {
        renderer.touchHUD(f, f1, mHandler);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldSurfaceView_6612()
    {
        renderer.requestScreenshot(mHandler);
    }

    public void onPause()
    {
        Debug.Log("GLView: onPause () entered.");
        renderer.disableDrawing();
        Debug.Log("GLView: calling super.onPause ().");
        super.onPause();
        Debug.Log("GLView: onPause () exiting");
    }

    public void onResume()
    {
        super.onResume();
        if (wantGL20 != getWantGL20() && activity != null)
        {
            activity.rendererAdvancedRenderingChanged();
            return;
        } else
        {
            WorldViewRenderer worldviewrenderer = renderer;
            worldviewrenderer.getClass();
            queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ(worldviewrenderer));
            return;
        }
    }

    void pickObjectHover(float f, float f1)
    {
        renderer.pickObject(f, f1, mHandler);
    }

    public void setAvatarCountLimit(int i)
    {
        renderer.setAvatarCountLimit(i);
    }

    void setDisplayedHUDid(int i)
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls3(i, this));
    }

    public void setDrawDistance(int i)
    {
        renderer.setDrawDistance(i);
    }

    void setDrawPickedObject(SLObjectInfo slobjectinfo)
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls2(this, slobjectinfo));
    }

    void setForcedTime(boolean flag, float f)
    {
        if (renderer != null)
        {
            renderer.setForcedTime(flag, f);
        }
    }

    void setHUDOffset(float f, float f1)
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls5(f, f1, this));
    }

    void setHUDScaleFactor(float f)
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls4(f, this));
    }

    void setIsInteracting(boolean flag)
    {
        renderer.setIsInteracting(flag);
    }

    void setOwnAvatarHidden(boolean flag)
    {
        if (ownAvatarHidden != flag)
        {
            ownAvatarHidden = flag;
            queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls7(flag, this));
        }
    }

    void takeScreenshot()
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls1(this));
    }

    void touchHUD(float f, float f1)
    {
        queueEvent(new _2D_.Lambda.WbegR8yVWPTDY8X58dwHEd9HRSQ._cls6(f, f1, this));
    }
}
