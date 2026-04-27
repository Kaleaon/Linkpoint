package com.lumiyaviewer.lumiya.ui.render;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;

public class WorldViewActivity_ViewBinding implements Unbinder {
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
    public WorldViewActivity_ViewBinding(WorldViewActivity worldViewActivity) {
        this(worldViewActivity, worldViewActivity.getWindow().getDecorView());
    }

    @UiThread
    public WorldViewActivity_ViewBinding(final WorldViewActivity worldViewActivity, View view) {
        this.target = worldViewActivity;
        View findRequiredView = Utils.findRequiredView(view, R.id.object_pay_button, "field 'objectPayButton' and method 'onObjectPayButton'");
        worldViewActivity.objectPayButton = (ImageButton) Utils.castView(findRequiredView, R.id.object_pay_button, "field 'objectPayButton'", ImageButton.class);
        this.view2131755554 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectPayButton();
            }
        worldViewActivity.buttonFlyUpward = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_fly_upward, "field 'buttonFlyUpward'", ImageButton.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.button_hud, "field 'buttonHUD' and method 'onHUDButton'");
        worldViewActivity.buttonHUD = (Button) Utils.castView(findRequiredView2, R.id.button_hud, "field 'buttonHUD'", Button.class);
        this.view2131755763 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onHUDButton();
            }
        View findRequiredView3 = Utils.findRequiredView(view, R.id.button_stand_up, "field 'buttonStandUp' and method 'onObjectStandButton'");
        worldViewActivity.buttonStandUp = (ImageButton) Utils.castView(findRequiredView3, R.id.button_stand_up, "field 'buttonStandUp'", ImageButton.class);
        this.view2131755264 = findRequiredView3;
        findRequiredView3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectStandButton();
            }
        View findRequiredView4 = Utils.findRequiredView(view, R.id.object_more_button, "field 'objectMoreButton' and method 'onObjectMoreButton'");
        worldViewActivity.objectMoreButton = (ImageButton) Utils.castView(findRequiredView4, R.id.object_more_button, "field 'objectMoreButton'", ImageButton.class);
        this.view2131755754 = findRequiredView4;
        findRequiredView4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectMoreButton();
            }
        worldViewActivity.flyButtonsLayout = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.fly_buttons_layout, "field 'flyButtonsLayout'", LinearLayout.class);
        worldViewActivity.objectPopupLeftSpacer = Utils.findRequiredView(view, R.id.object_popup_left_spacer, "field 'objectPopupLeftSpacer'");
        View findRequiredView5 = Utils.findRequiredView(view, R.id.object_touch_button, "field 'objectTouchButton' and method 'onObjectTouchButton'");
        worldViewActivity.objectTouchButton = (ImageButton) Utils.castView(findRequiredView5, R.id.object_touch_button, "field 'objectTouchButton'", ImageButton.class);
        this.view2131755268 = findRequiredView5;
        findRequiredView5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectTouchButton();
            }
        worldViewActivity.buttonFlyDownward = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_fly_downward, "field 'buttonFlyDownward'", ImageButton.class);
        worldViewActivity.chatsOverlayLayout = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.chats_overlay_layout, "field 'chatsOverlayLayout'", LinearLayout.class);
        worldViewActivity.moveButtonsLayout = Utils.findRequiredView(view, R.id.move_buttons_layout, "field 'moveButtonsLayout'");
        worldViewActivity.avatarIconView = (ImageView) Utils.findRequiredViewAsType(view, R.id.avatarIconView, "field 'avatarIconView'", ImageView.class);
        worldViewActivity.worldViewHolder = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.world_view_holder, "field 'worldViewHolder'", FrameLayout.class);
        worldViewActivity.worldOverlaysContainer = (ViewGroup) Utils.findRequiredViewAsType(view, R.id.world_overlays_container, "field 'worldOverlaysContainer'", ViewGroup.class);
        View findRequiredView6 = Utils.findRequiredView(view, R.id.object_chat_button, "field 'objectChatButton' and method 'onObjectChatButton'");
        worldViewActivity.objectChatButton = (ImageButton) Utils.castView(findRequiredView6, R.id.object_chat_button, "field 'objectChatButton'", ImageButton.class);
        this.view2131755270 = findRequiredView6;
        findRequiredView6.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectChatButton();
            }
        worldViewActivity.detailsContainer = Utils.findRequiredView(view, R.id.details, "field 'detailsContainer'");
        worldViewActivity.worldViewTouchReceiver = Utils.findRequiredView(view, R.id.world_view_touch_receiver, "field 'worldViewTouchReceiver'");
        worldViewActivity.buttonTurnRight = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_turn_right, "field 'buttonTurnRight'", ImageButton.class);
        View findRequiredView7 = Utils.findRequiredView(view, R.id.object_sit_button, "field 'objectSitButton' and method 'onObjectSitButton'");
        worldViewActivity.objectSitButton = (ImageButton) Utils.castView(findRequiredView7, R.id.object_sit_button, "field 'objectSitButton'", ImageButton.class);
        this.view2131755269 = findRequiredView7;
        findRequiredView7.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectSitButton();
            }
        worldViewActivity.buttonTurnLeft = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_turn_left, "field 'buttonTurnLeft'", ImageButton.class);
        worldViewActivity.buttonMoveForward = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_move_forward, "field 'buttonMoveForward'", ImageButton.class);
        worldViewActivity.objectControlsPanel = Utils.findRequiredView(view, R.id.object_controls_panel, "field 'objectControlsPanel'");
        worldViewActivity.dragPointerLayout = (ViewGroup) Utils.findRequiredViewAsType(view, R.id.drag_pointer_layout, "field 'dragPointerLayout'", ViewGroup.class);
        View findRequiredView8 = Utils.findRequiredView(view, R.id.button_cam_off, "field 'buttonCamOff' and method 'onCamOffButton'");
        worldViewActivity.buttonCamOff = (ImageButton) Utils.castView(findRequiredView8, R.id.button_cam_off, "field 'buttonCamOff'", ImageButton.class);
        this.view2131755762 = findRequiredView8;
        findRequiredView8.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onCamOffButton();
            }
        worldViewActivity.voiceStatusView = (VoiceStatusView) Utils.findRequiredViewAsType(view, R.id.voice_status_view_3d, "field 'voiceStatusView'", VoiceStatusView.class);
        worldViewActivity.insetsBackground = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.insets_background, "field 'insetsBackground'", FrameLayout.class);
        worldViewActivity.buttonMoveBackward = (ImageButton) Utils.findRequiredViewAsType(view, R.id.button_move_backward, "field 'buttonMoveBackward'", ImageButton.class);
        View findRequiredView9 = Utils.findRequiredView(view, R.id.button_cam_on, "field 'buttonCamOn' and method 'onCamOnButton'");
        worldViewActivity.buttonCamOn = (ImageButton) Utils.castView(findRequiredView9, R.id.button_cam_on, "field 'buttonCamOn'", ImageButton.class);
        this.view2131755761 = findRequiredView9;
        findRequiredView9.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onCamOnButton();
            }
        View findRequiredView10 = Utils.findRequiredView(view, R.id.object_stand_button, "field 'objectStandButton' and method 'onObjectStandButton'");
        worldViewActivity.objectStandButton = (ImageButton) Utils.castView(findRequiredView10, R.id.object_stand_button, "field 'objectStandButton'", ImageButton.class);
        this.view2131755538 = findRequiredView10;
        findRequiredView10.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onObjectStandButton();
            }
        worldViewActivity.dragPointer = Utils.findRequiredView(view, R.id.drag_pointer_view, "field 'dragPointer'");
        worldViewActivity.objectNameTextView = (TextView) Utils.findRequiredViewAsType(view, R.id.objectNameTextView, "field 'objectNameTextView'", TextView.class);
        View findRequiredView11 = Utils.findRequiredView(view, R.id.button_stop_flying, "field 'buttonStopFlying' and method 'onStopFlyingButton'");
        worldViewActivity.buttonStopFlying = (ImageButton) Utils.castView(findRequiredView11, R.id.button_stop_flying, "field 'buttonStopFlying'", ImageButton.class);
        this.view2131755758 = findRequiredView11;
        findRequiredView11.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                worldViewActivity.onStopFlyingButton();
            }
    }

    @CallSuper
    public void unbind() {
        WorldViewActivity worldViewActivity = this.target;
        if (worldViewActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        worldViewActivity.objectPayButton = null;
        worldViewActivity.buttonFlyUpward = null;
        worldViewActivity.buttonHUD = null;
        worldViewActivity.buttonStandUp = null;
        worldViewActivity.objectMoreButton = null;
        worldViewActivity.flyButtonsLayout = null;
        worldViewActivity.objectPopupLeftSpacer = null;
        worldViewActivity.objectTouchButton = null;
        worldViewActivity.buttonFlyDownward = null;
        worldViewActivity.chatsOverlayLayout = null;
        worldViewActivity.moveButtonsLayout = null;
        worldViewActivity.avatarIconView = null;
        worldViewActivity.worldViewHolder = null;
        worldViewActivity.worldOverlaysContainer = null;
        worldViewActivity.objectChatButton = null;
        worldViewActivity.detailsContainer = null;
        worldViewActivity.worldViewTouchReceiver = null;
        worldViewActivity.buttonTurnRight = null;
        worldViewActivity.objectSitButton = null;
        worldViewActivity.buttonTurnLeft = null;
        worldViewActivity.buttonMoveForward = null;
        worldViewActivity.objectControlsPanel = null;
        worldViewActivity.dragPointerLayout = null;
        worldViewActivity.buttonCamOff = null;
        worldViewActivity.voiceStatusView = null;
        worldViewActivity.insetsBackground = null;
        worldViewActivity.buttonMoveBackward = null;
        worldViewActivity.buttonCamOn = null;
        worldViewActivity.objectStandButton = null;
        worldViewActivity.dragPointer = null;
        worldViewActivity.objectNameTextView = null;
        worldViewActivity.buttonStopFlying = null;
        this.view2131755554.setOnClickListener((View.OnClickListener) null);
        this.view2131755554 = null;
        this.view2131755763.setOnClickListener((View.OnClickListener) null);
        this.view2131755763 = null;
        this.view2131755264.setOnClickListener((View.OnClickListener) null);
        this.view2131755264 = null;
        this.view2131755754.setOnClickListener((View.OnClickListener) null);
        this.view2131755754 = null;
        this.view2131755268.setOnClickListener((View.OnClickListener) null);
        this.view2131755268 = null;
        this.view2131755270.setOnClickListener((View.OnClickListener) null);
        this.view2131755270 = null;
        this.view2131755269.setOnClickListener((View.OnClickListener) null);
        this.view2131755269 = null;
        this.view2131755762.setOnClickListener((View.OnClickListener) null);
        this.view2131755762 = null;
        this.view2131755761.setOnClickListener((View.OnClickListener) null);
        this.view2131755761 = null;
        this.view2131755538.setOnClickListener((View.OnClickListener) null);
        this.view2131755538 = null;
        this.view2131755758.setOnClickListener((View.OnClickListener) null);
        this.view2131755758 = null;
    }
}
