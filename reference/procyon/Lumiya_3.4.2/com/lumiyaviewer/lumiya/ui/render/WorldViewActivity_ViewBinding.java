// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.support.annotation.CallSuper;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.ImageButton;
import butterknife.internal.Utils;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;

public class WorldViewActivity_ViewBinding implements Unbinder
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
    
    @UiThread
    public WorldViewActivity_ViewBinding(final WorldViewActivity worldViewActivity) {
        this(worldViewActivity, worldViewActivity.getWindow().getDecorView());
    }
    
    @UiThread
    public WorldViewActivity_ViewBinding(final WorldViewActivity target, View requiredView) {
        this.target = target;
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755554, "field 'objectPayButton' and method 'onObjectPayButton'");
        target.objectPayButton = Utils.castView(requiredView2, 2131755554, "field 'objectPayButton'", ImageButton.class);
        (this.view2131755554 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectPayButton();
            }
        });
        target.buttonFlyUpward = Utils.findRequiredViewAsType(requiredView, 2131755756, "field 'buttonFlyUpward'", ImageButton.class);
        final View requiredView3 = Utils.findRequiredView(requiredView, 2131755763, "field 'buttonHUD' and method 'onHUDButton'");
        target.buttonHUD = Utils.castView(requiredView3, 2131755763, "field 'buttonHUD'", Button.class);
        (this.view2131755763 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onHUDButton();
            }
        });
        final View requiredView4 = Utils.findRequiredView(requiredView, 2131755264, "field 'buttonStandUp' and method 'onObjectStandButton'");
        target.buttonStandUp = Utils.castView(requiredView4, 2131755264, "field 'buttonStandUp'", ImageButton.class);
        (this.view2131755264 = requiredView4).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectStandButton();
            }
        });
        final View requiredView5 = Utils.findRequiredView(requiredView, 2131755754, "field 'objectMoreButton' and method 'onObjectMoreButton'");
        target.objectMoreButton = Utils.castView(requiredView5, 2131755754, "field 'objectMoreButton'", ImageButton.class);
        (this.view2131755754 = requiredView5).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectMoreButton();
            }
        });
        target.flyButtonsLayout = Utils.findRequiredViewAsType(requiredView, 2131755755, "field 'flyButtonsLayout'", LinearLayout.class);
        target.objectPopupLeftSpacer = Utils.findRequiredView(requiredView, 2131755764, "field 'objectPopupLeftSpacer'");
        final View requiredView6 = Utils.findRequiredView(requiredView, 2131755268, "field 'objectTouchButton' and method 'onObjectTouchButton'");
        target.objectTouchButton = Utils.castView(requiredView6, 2131755268, "field 'objectTouchButton'", ImageButton.class);
        (this.view2131755268 = requiredView6).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectTouchButton();
            }
        });
        target.buttonFlyDownward = Utils.findRequiredViewAsType(requiredView, 2131755757, "field 'buttonFlyDownward'", ImageButton.class);
        target.chatsOverlayLayout = Utils.findRequiredViewAsType(requiredView, 2131755759, "field 'chatsOverlayLayout'", LinearLayout.class);
        target.moveButtonsLayout = Utils.findRequiredView(requiredView, 2131755259, "field 'moveButtonsLayout'");
        target.avatarIconView = Utils.findRequiredViewAsType(requiredView, 2131755576, "field 'avatarIconView'", ImageView.class);
        target.worldViewHolder = Utils.findRequiredViewAsType(requiredView, 2131755743, "field 'worldViewHolder'", FrameLayout.class);
        target.worldOverlaysContainer = Utils.findRequiredViewAsType(requiredView, 2131755750, "field 'worldOverlaysContainer'", ViewGroup.class);
        final View requiredView7 = Utils.findRequiredView(requiredView, 2131755270, "field 'objectChatButton' and method 'onObjectChatButton'");
        target.objectChatButton = Utils.castView(requiredView7, 2131755270, "field 'objectChatButton'", ImageButton.class);
        (this.view2131755270 = requiredView7).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectChatButton();
            }
        });
        target.detailsContainer = Utils.findRequiredView(requiredView, 2131755284, "field 'detailsContainer'");
        target.worldViewTouchReceiver = Utils.findRequiredView(requiredView, 2131755746, "field 'worldViewTouchReceiver'");
        target.buttonTurnRight = Utils.findRequiredViewAsType(requiredView, 2131755263, "field 'buttonTurnRight'", ImageButton.class);
        final View requiredView8 = Utils.findRequiredView(requiredView, 2131755269, "field 'objectSitButton' and method 'onObjectSitButton'");
        target.objectSitButton = Utils.castView(requiredView8, 2131755269, "field 'objectSitButton'", ImageButton.class);
        (this.view2131755269 = requiredView8).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectSitButton();
            }
        });
        target.buttonTurnLeft = Utils.findRequiredViewAsType(requiredView, 2131755261, "field 'buttonTurnLeft'", ImageButton.class);
        target.buttonMoveForward = Utils.findRequiredViewAsType(requiredView, 2131755260, "field 'buttonMoveForward'", ImageButton.class);
        target.objectControlsPanel = Utils.findRequiredView(requiredView, 2131755753, "field 'objectControlsPanel'");
        target.dragPointerLayout = Utils.findRequiredViewAsType(requiredView, 2131755747, "field 'dragPointerLayout'", ViewGroup.class);
        final View requiredView9 = Utils.findRequiredView(requiredView, 2131755762, "field 'buttonCamOff' and method 'onCamOffButton'");
        target.buttonCamOff = Utils.castView(requiredView9, 2131755762, "field 'buttonCamOff'", ImageButton.class);
        (this.view2131755762 = requiredView9).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onCamOffButton();
            }
        });
        target.voiceStatusView = Utils.findRequiredViewAsType(requiredView, 2131755752, "field 'voiceStatusView'", VoiceStatusView.class);
        target.insetsBackground = Utils.findRequiredViewAsType(requiredView, 2131755744, "field 'insetsBackground'", FrameLayout.class);
        target.buttonMoveBackward = Utils.findRequiredViewAsType(requiredView, 2131755262, "field 'buttonMoveBackward'", ImageButton.class);
        final View requiredView10 = Utils.findRequiredView(requiredView, 2131755761, "field 'buttonCamOn' and method 'onCamOnButton'");
        target.buttonCamOn = Utils.castView(requiredView10, 2131755761, "field 'buttonCamOn'", ImageButton.class);
        (this.view2131755761 = requiredView10).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onCamOnButton();
            }
        });
        final View requiredView11 = Utils.findRequiredView(requiredView, 2131755538, "field 'objectStandButton' and method 'onObjectStandButton'");
        target.objectStandButton = Utils.castView(requiredView11, 2131755538, "field 'objectStandButton'", ImageButton.class);
        (this.view2131755538 = requiredView11).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectStandButton();
            }
        });
        target.dragPointer = Utils.findRequiredView(requiredView, 2131755748, "field 'dragPointer'");
        target.objectNameTextView = Utils.findRequiredViewAsType(requiredView, 2131755577, "field 'objectNameTextView'", TextView.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755758, "field 'buttonStopFlying' and method 'onStopFlyingButton'");
        target.buttonStopFlying = Utils.castView(requiredView, 2131755758, "field 'buttonStopFlying'", ImageButton.class);
        (this.view2131755758 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onStopFlyingButton();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final WorldViewActivity target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.objectPayButton = null;
        target.buttonFlyUpward = null;
        target.buttonHUD = null;
        target.buttonStandUp = null;
        target.objectMoreButton = null;
        target.flyButtonsLayout = null;
        target.objectPopupLeftSpacer = null;
        target.objectTouchButton = null;
        target.buttonFlyDownward = null;
        target.chatsOverlayLayout = null;
        target.moveButtonsLayout = null;
        target.avatarIconView = null;
        target.worldViewHolder = null;
        target.worldOverlaysContainer = null;
        target.objectChatButton = null;
        target.detailsContainer = null;
        target.worldViewTouchReceiver = null;
        target.buttonTurnRight = null;
        target.objectSitButton = null;
        target.buttonTurnLeft = null;
        target.buttonMoveForward = null;
        target.objectControlsPanel = null;
        target.dragPointerLayout = null;
        target.buttonCamOff = null;
        target.voiceStatusView = null;
        target.insetsBackground = null;
        target.buttonMoveBackward = null;
        target.buttonCamOn = null;
        target.objectStandButton = null;
        target.dragPointer = null;
        target.objectNameTextView = null;
        target.buttonStopFlying = null;
        this.view2131755554.setOnClickListener((View$OnClickListener)null);
        this.view2131755554 = null;
        this.view2131755763.setOnClickListener((View$OnClickListener)null);
        this.view2131755763 = null;
        this.view2131755264.setOnClickListener((View$OnClickListener)null);
        this.view2131755264 = null;
        this.view2131755754.setOnClickListener((View$OnClickListener)null);
        this.view2131755754 = null;
        this.view2131755268.setOnClickListener((View$OnClickListener)null);
        this.view2131755268 = null;
        this.view2131755270.setOnClickListener((View$OnClickListener)null);
        this.view2131755270 = null;
        this.view2131755269.setOnClickListener((View$OnClickListener)null);
        this.view2131755269 = null;
        this.view2131755762.setOnClickListener((View$OnClickListener)null);
        this.view2131755762 = null;
        this.view2131755761.setOnClickListener((View$OnClickListener)null);
        this.view2131755761 = null;
        this.view2131755538.setOnClickListener((View$OnClickListener)null);
        this.view2131755538 = null;
        this.view2131755758.setOnClickListener((View$OnClickListener)null);
        this.view2131755758 = null;
    }
}
