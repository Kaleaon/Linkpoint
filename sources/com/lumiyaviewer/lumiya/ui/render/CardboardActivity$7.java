// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.graphics.PointF;
import com.google.vr.sdk.controller.Controller;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity, MoveControl

class activeMoveControl extends com.google.vr.sdk.controller.ener
{

    private MoveControl activeMoveControl;
    private boolean appButtonPressed;
    final CardboardActivity this$0;

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808()
    {
        CardboardActivity._2D_wrap3(CardboardActivity.this, true);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948()
    {
        CardboardActivity._2D_wrap3(CardboardActivity.this, false);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726(MoveControl movecontrol, float f)
    {
        CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, f);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931(MoveControl movecontrol)
    {
        CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, 0.0F);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145(MoveControl movecontrol, float f)
    {
        if (CardboardActivity._2D_get21(CardboardActivity.this) || movecontrol == MoveControl.Right || movecontrol == MoveControl.Left)
        {
            CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, f);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581(MoveControl movecontrol)
    {
        CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, 0.0F);
    }

    public void onConnectionStateChanged(int i)
    {
        super.onConnectionStateChanged(i);
        String s;
        if (i == 3)
        {
            s = "connected";
        } else
        {
            s = "disconnected";
        }
        Debug.Printf("Cardboard: Daydream controller is now %s", new Object[] {
            s
        });
        CardboardActivity._2D_get3(CardboardActivity.this).set(i);
    }

    public void onUpdate()
    {
        float f2;
        f2 = 0.0F;
        super.onUpdate();
        CardboardActivity._2D_get2(CardboardActivity.this).update();
        if (!CardboardActivity._2D_get2(CardboardActivity.this).appButtonState || !(appButtonPressed ^ true)) goto _L2; else goto _L1
_L1:
        runOnUiThread(new hPHTLXL5B0hI4gXA._cls20(this));
_L13:
        appButtonPressed = CardboardActivity._2D_get2(CardboardActivity.this).appButtonState;
        if (!CardboardActivity._2D_get2(CardboardActivity.this).isTouching) goto _L4; else goto _L3
_L3:
        float f;
        MoveControl movecontrol;
        float f1 = CardboardActivity._2D_get2(CardboardActivity.this).touch.x * 2.0F - 1.0F;
        float f3 = -(CardboardActivity._2D_get2(CardboardActivity.this).touch.y * 2.0F - 1.0F);
        f = f1;
        if (Math.abs(f1) < 0.5F)
        {
            f = 0.0F;
        }
        f1 = f3;
        if (Math.abs(f3) < 0.5F)
        {
            f1 = 0.0F;
        }
        if (Math.abs(f) >= Math.abs(f1))
        {
            if (f > 0.0F)
            {
                movecontrol = MoveControl.Right;
                f *= 2.0F;
            } else
            if (f < 0.0F)
            {
                movecontrol = MoveControl.Left;
                f = -f * 2.0F;
            } else
            {
                movecontrol = null;
                f = f2;
            }
        } else
        if (f1 > 0.0F)
        {
            movecontrol = MoveControl.Forward;
            f = f1 * 2.0F;
        } else
        if (f1 < 0.0F)
        {
            movecontrol = MoveControl.Backward;
            f = -f1 * 2.0F;
        } else
        {
            movecontrol = null;
            f = f2;
        }
        if (movecontrol == activeMoveControl) goto _L6; else goto _L5
_L5:
        if (movecontrol == null) goto _L8; else goto _L7
_L7:
        activeMoveControl = movecontrol;
        runOnUiThread(new hPHTLXL5B0hI4gXA._cls29(f, this, movecontrol));
_L10:
        return;
_L2:
        if (!CardboardActivity._2D_get2(CardboardActivity.this).appButtonState && appButtonPressed)
        {
            runOnUiThread(new hPHTLXL5B0hI4gXA._cls21(this));
        }
        continue; /* Loop/switch isn't completed */
_L8:
        movecontrol = activeMoveControl;
        runOnUiThread(new hPHTLXL5B0hI4gXA._cls27(this, movecontrol));
        activeMoveControl = null;
        return;
_L6:
        if (movecontrol == null) goto _L10; else goto _L9
_L9:
        runOnUiThread(new hPHTLXL5B0hI4gXA._cls30(f, this, movecontrol));
        return;
_L4:
        if (activeMoveControl == null) goto _L10; else goto _L11
_L11:
        MoveControl movecontrol1 = activeMoveControl;
        runOnUiThread(new hPHTLXL5B0hI4gXA._cls28(this, movecontrol1));
        activeMoveControl = null;
        return;
        if (true) goto _L13; else goto _L12
_L12:
    }

    hPHTLXL5B0hI4gXA._cls28()
    {
        this$0 = CardboardActivity.this;
        super();
        appButtonPressed = false;
        activeMoveControl = null;
    }
}
