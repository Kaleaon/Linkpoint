// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

public class WorldViewActivity_ViewBinding
    implements Unbinder
{

    private WorldViewActivity target;
    private View view2131755264;
    private View view2131755268;
    private View view2131755269;
    private View view2131755270;
    private View view2131755538;
    private View view2131755554;
    private View view2131755754;
    private View view2131755758;
    private View view2131755761;
    private View view2131755762;
    private View view2131755763;

    public WorldViewActivity_ViewBinding(WorldViewActivity worldviewactivity)
    {
        this(worldviewactivity, worldviewactivity.getWindow().getDecorView());
    }

    public WorldViewActivity_ViewBinding(final WorldViewActivity target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f100222, "field 'objectPayButton' and method 'onObjectPayButton'");
        target.objectPayButton = (ImageButton)Utils.castView(view1, 0x7f100222, "field 'objectPayButton'", android/widget/ImageButton);
        view2131755554 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectPayButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.buttonFlyUpward = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1002ec, "field 'buttonFlyUpward'", android/widget/ImageButton);
        view1 = Utils.findRequiredView(view, 0x7f1002f3, "field 'buttonHUD' and method 'onHUDButton'");
        target.buttonHUD = (Button)Utils.castView(view1, 0x7f1002f3, "field 'buttonHUD'", android/widget/Button);
        view2131755763 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onHUDButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100100, "field 'buttonStandUp' and method 'onObjectStandButton'");
        target.buttonStandUp = (ImageButton)Utils.castView(view1, 0x7f100100, "field 'buttonStandUp'", android/widget/ImageButton);
        view2131755264 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectStandButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1002ea, "field 'objectMoreButton' and method 'onObjectMoreButton'");
        target.objectMoreButton = (ImageButton)Utils.castView(view1, 0x7f1002ea, "field 'objectMoreButton'", android/widget/ImageButton);
        view2131755754 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectMoreButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.flyButtonsLayout = (LinearLayout)Utils.findRequiredViewAsType(view, 0x7f1002eb, "field 'flyButtonsLayout'", android/widget/LinearLayout);
        target.objectPopupLeftSpacer = Utils.findRequiredView(view, 0x7f1002f4, "field 'objectPopupLeftSpacer'");
        view1 = Utils.findRequiredView(view, 0x7f100104, "field 'objectTouchButton' and method 'onObjectTouchButton'");
        target.objectTouchButton = (ImageButton)Utils.castView(view1, 0x7f100104, "field 'objectTouchButton'", android/widget/ImageButton);
        view2131755268 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectTouchButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.buttonFlyDownward = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1002ed, "field 'buttonFlyDownward'", android/widget/ImageButton);
        target.chatsOverlayLayout = (LinearLayout)Utils.findRequiredViewAsType(view, 0x7f1002ef, "field 'chatsOverlayLayout'", android/widget/LinearLayout);
        target.moveButtonsLayout = Utils.findRequiredView(view, 0x7f1000fb, "field 'moveButtonsLayout'");
        target.avatarIconView = (ImageView)Utils.findRequiredViewAsType(view, 0x7f100238, "field 'avatarIconView'", android/widget/ImageView);
        target.worldViewHolder = (FrameLayout)Utils.findRequiredViewAsType(view, 0x7f1002df, "field 'worldViewHolder'", android/widget/FrameLayout);
        target.worldOverlaysContainer = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f1002e6, "field 'worldOverlaysContainer'", android/view/ViewGroup);
        view1 = Utils.findRequiredView(view, 0x7f100106, "field 'objectChatButton' and method 'onObjectChatButton'");
        target.objectChatButton = (ImageButton)Utils.castView(view1, 0x7f100106, "field 'objectChatButton'", android/widget/ImageButton);
        view2131755270 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectChatButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.detailsContainer = Utils.findRequiredView(view, 0x7f100114, "field 'detailsContainer'");
        target.worldViewTouchReceiver = Utils.findRequiredView(view, 0x7f1002e2, "field 'worldViewTouchReceiver'");
        target.buttonTurnRight = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1000ff, "field 'buttonTurnRight'", android/widget/ImageButton);
        view1 = Utils.findRequiredView(view, 0x7f100105, "field 'objectSitButton' and method 'onObjectSitButton'");
        target.objectSitButton = (ImageButton)Utils.castView(view1, 0x7f100105, "field 'objectSitButton'", android/widget/ImageButton);
        view2131755269 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectSitButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.buttonTurnLeft = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1000fd, "field 'buttonTurnLeft'", android/widget/ImageButton);
        target.buttonMoveForward = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1000fc, "field 'buttonMoveForward'", android/widget/ImageButton);
        target.objectControlsPanel = Utils.findRequiredView(view, 0x7f1002e9, "field 'objectControlsPanel'");
        target.dragPointerLayout = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f1002e3, "field 'dragPointerLayout'", android/view/ViewGroup);
        view1 = Utils.findRequiredView(view, 0x7f1002f2, "field 'buttonCamOff' and method 'onCamOffButton'");
        target.buttonCamOff = (ImageButton)Utils.castView(view1, 0x7f1002f2, "field 'buttonCamOff'", android/widget/ImageButton);
        view2131755762 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onCamOffButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.voiceStatusView = (VoiceStatusView)Utils.findRequiredViewAsType(view, 0x7f1002e8, "field 'voiceStatusView'", com/lumiyaviewer/lumiya/ui/voice/VoiceStatusView);
        target.insetsBackground = (FrameLayout)Utils.findRequiredViewAsType(view, 0x7f1002e0, "field 'insetsBackground'", android/widget/FrameLayout);
        target.buttonMoveBackward = (ImageButton)Utils.findRequiredViewAsType(view, 0x7f1000fe, "field 'buttonMoveBackward'", android/widget/ImageButton);
        view1 = Utils.findRequiredView(view, 0x7f1002f1, "field 'buttonCamOn' and method 'onCamOnButton'");
        target.buttonCamOn = (ImageButton)Utils.castView(view1, 0x7f1002f1, "field 'buttonCamOn'", android/widget/ImageButton);
        view2131755761 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onCamOnButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100212, "field 'objectStandButton' and method 'onObjectStandButton'");
        target.objectStandButton = (ImageButton)Utils.castView(view1, 0x7f100212, "field 'objectStandButton'", android/widget/ImageButton);
        view2131755538 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectStandButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
        target.dragPointer = Utils.findRequiredView(view, 0x7f1002e4, "field 'dragPointer'");
        target.objectNameTextView = (TextView)Utils.findRequiredViewAsType(view, 0x7f100239, "field 'objectNameTextView'", android/widget/TextView);
        view = Utils.findRequiredView(view, 0x7f1002ee, "field 'buttonStopFlying' and method 'onStopFlyingButton'");
        target.buttonStopFlying = (ImageButton)Utils.castView(view, 0x7f1002ee, "field 'buttonStopFlying'", android/widget/ImageButton);
        view2131755758 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final WorldViewActivity_ViewBinding this$0;
            final WorldViewActivity val$target;

            public void doClick(View view2)
            {
                target.onStopFlyingButton();
            }

            
            {
                this$0 = WorldViewActivity_ViewBinding.this;
                target = worldviewactivity;
                super();
            }
        });
    }

    public void unbind()
    {
        WorldViewActivity worldviewactivity = target;
        if (worldviewactivity == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            worldviewactivity.objectPayButton = null;
            worldviewactivity.buttonFlyUpward = null;
            worldviewactivity.buttonHUD = null;
            worldviewactivity.buttonStandUp = null;
            worldviewactivity.objectMoreButton = null;
            worldviewactivity.flyButtonsLayout = null;
            worldviewactivity.objectPopupLeftSpacer = null;
            worldviewactivity.objectTouchButton = null;
            worldviewactivity.buttonFlyDownward = null;
            worldviewactivity.chatsOverlayLayout = null;
            worldviewactivity.moveButtonsLayout = null;
            worldviewactivity.avatarIconView = null;
            worldviewactivity.worldViewHolder = null;
            worldviewactivity.worldOverlaysContainer = null;
            worldviewactivity.objectChatButton = null;
            worldviewactivity.detailsContainer = null;
            worldviewactivity.worldViewTouchReceiver = null;
            worldviewactivity.buttonTurnRight = null;
            worldviewactivity.objectSitButton = null;
            worldviewactivity.buttonTurnLeft = null;
            worldviewactivity.buttonMoveForward = null;
            worldviewactivity.objectControlsPanel = null;
            worldviewactivity.dragPointerLayout = null;
            worldviewactivity.buttonCamOff = null;
            worldviewactivity.voiceStatusView = null;
            worldviewactivity.insetsBackground = null;
            worldviewactivity.buttonMoveBackward = null;
            worldviewactivity.buttonCamOn = null;
            worldviewactivity.objectStandButton = null;
            worldviewactivity.dragPointer = null;
            worldviewactivity.objectNameTextView = null;
            worldviewactivity.buttonStopFlying = null;
            view2131755554.setOnClickListener(null);
            view2131755554 = null;
            view2131755763.setOnClickListener(null);
            view2131755763 = null;
            view2131755264.setOnClickListener(null);
            view2131755264 = null;
            view2131755754.setOnClickListener(null);
            view2131755754 = null;
            view2131755268.setOnClickListener(null);
            view2131755268 = null;
            view2131755270.setOnClickListener(null);
            view2131755270 = null;
            view2131755269.setOnClickListener(null);
            view2131755269 = null;
            view2131755762.setOnClickListener(null);
            view2131755762 = null;
            view2131755761.setOnClickListener(null);
            view2131755761 = null;
            view2131755538.setOnClickListener(null);
            view2131755538 = null;
            view2131755758.setOnClickListener(null);
            view2131755758 = null;
            return;
        }
    }
}
