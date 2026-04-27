// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.support.annotation.CallSuper;
import android.annotation.SuppressLint;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import android.widget.ProgressBar;
import android.view.MotionEvent;
import android.view.View$OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.ImageButton;
import butterknife.internal.Utils;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;

public class CardboardActivity_ViewBinding implements Unbinder
{
    private CardboardActivity target;
    private View view2131755255;
    private View view2131755256;
    private View view2131755257;
    private View view2131755260;
    private View view2131755261;
    private View view2131755262;
    private View view2131755263;
    private View view2131755264;
    private View view2131755265;
    private View view2131755266;
    private View view2131755268;
    private View view2131755269;
    private View view2131755270;
    private View view2131755272;
    private View view2131755274;
    private View view2131755275;
    private View view2131755277;
    private View view2131755281;
    private View view2131755282;
    
    @UiThread
    public CardboardActivity_ViewBinding(final CardboardActivity cardboardActivity) {
        this(cardboardActivity, cardboardActivity.getWindow().getDecorView());
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    @UiThread
    public CardboardActivity_ViewBinding(final CardboardActivity target, View requiredView) {
        this.target = target;
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755256, "field 'buttonSpeak' and method 'onSpeakButton'");
        target.buttonSpeak = Utils.castView(requiredView2, 2131755256, "field 'buttonSpeak'", ImageButton.class);
        (this.view2131755256 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSpeakButton();
            }
        });
        final View requiredView3 = Utils.findRequiredView(requiredView, 2131755257, "field 'buttonChat' and method 'onChatButton'");
        target.buttonChat = Utils.castView(requiredView3, 2131755257, "field 'buttonChat'", ImageButton.class);
        (this.view2131755257 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onChatButton();
            }
        });
        final View requiredView4 = Utils.findRequiredView(requiredView, 2131755281, "field 'buttonSpeechSend' and method 'onSpeechSendButton'");
        target.buttonSpeechSend = Utils.castView(requiredView4, 2131755281, "field 'buttonSpeechSend'", ImageButton.class);
        (this.view2131755281 = requiredView4).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSpeechSendButton();
            }
        });
        target.speechRecognitionResults = Utils.findRequiredViewAsType(requiredView, 2131755280, "field 'speechRecognitionResults'", TextView.class);
        target.chatsOverlayLayout = Utils.findRequiredViewAsType(requiredView, 2131755258, "field 'chatsOverlayLayout'", LinearLayout.class);
        final View requiredView5 = Utils.findRequiredView(requiredView, 2131755265, "field 'cardboardAimControls' and method 'onAimControlsTouch'");
        target.cardboardAimControls = Utils.castView(requiredView5, 2131755265, "field 'cardboardAimControls'", ViewGroup.class);
        (this.view2131755265 = requiredView5).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onAimControlsTouch(view, motionEvent);
            }
        });
        final View requiredView6 = Utils.findRequiredView(requiredView, 2131755277, "field 'cardboardSpeakControls' and method 'onSpeakControlsTouch'");
        target.cardboardSpeakControls = Utils.castView(requiredView6, 2131755277, "field 'cardboardSpeakControls'", ViewGroup.class);
        (this.view2131755277 = requiredView6).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onSpeakControlsTouch(view, motionEvent);
            }
        });
        final View requiredView7 = Utils.findRequiredView(requiredView, 2131755266, "field 'cardboardObjectControls' and method 'onObjectControlsTouch'");
        target.cardboardObjectControls = Utils.castView(requiredView7, 2131755266, "field 'cardboardObjectControls'", ViewGroup.class);
        (this.view2131755266 = requiredView7).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onObjectControlsTouch(view, motionEvent);
            }
        });
        final View requiredView8 = Utils.findRequiredView(requiredView, 2131755268, "field 'buttonTouchObject' and method 'onObjectTouch'");
        target.buttonTouchObject = Utils.castView(requiredView8, 2131755268, "field 'buttonTouchObject'", ImageButton.class);
        (this.view2131755268 = requiredView8).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectTouch();
            }
        });
        final View requiredView9 = Utils.findRequiredView(requiredView, 2131755282, "field 'cardboardScriptDialog' and method 'onScriptDialogOutsideTouch'");
        target.cardboardScriptDialog = Utils.castView(requiredView9, 2131755282, "field 'cardboardScriptDialog'", ViewGroup.class);
        (this.view2131755282 = requiredView9).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onScriptDialogOutsideTouch(view, motionEvent);
            }
        });
        target.speakLevelIndicator = Utils.findRequiredViewAsType(requiredView, 2131755279, "field 'speakLevelIndicator'", ProgressBar.class);
        final View requiredView10 = Utils.findRequiredView(requiredView, 2131755274, "field 'yesButton' and method 'onYesButton'");
        target.yesButton = Utils.castView(requiredView10, 2131755274, "field 'yesButton'", ImageButton.class);
        (this.view2131755274 = requiredView10).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onYesButton();
            }
        });
        final View requiredView11 = Utils.findRequiredView(requiredView, 2131755270, "field 'buttonObjectChat' and method 'onObjectChat'");
        target.buttonObjectChat = Utils.castView(requiredView11, 2131755270, "field 'buttonObjectChat'", ImageButton.class);
        (this.view2131755270 = requiredView11).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectChat();
            }
        });
        target.yesNoText = Utils.findRequiredViewAsType(requiredView, 2131755276, "field 'yesNoText'", TextView.class);
        target.cardboardDetailsPage = Utils.findRequiredViewAsType(requiredView, 2131755283, "field 'cardboardDetailsPage'", ViewGroup.class);
        target.moveButtonsLayout = Utils.findRequiredViewAsType(requiredView, 2131755259, "field 'moveButtonsLayout'", ViewGroup.class);
        final View requiredView12 = Utils.findRequiredView(requiredView, 2131755264, "field 'buttonStandUp' and method 'onStandUpButton'");
        target.buttonStandUp = Utils.castView(requiredView12, 2131755264, "field 'buttonStandUp'", ImageButton.class);
        (this.view2131755264 = requiredView12).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onStandUpButton();
            }
        });
        target.speakNowText = Utils.findRequiredViewAsType(requiredView, 2131755278, "field 'speakNowText'", TextView.class);
        final View requiredView13 = Utils.findRequiredView(requiredView, 2131755260, "field 'buttonMoveForward' and method 'onCamButtonTouch'");
        target.buttonMoveForward = Utils.castView(requiredView13, 2131755260, "field 'buttonMoveForward'", ImageButton.class);
        (this.view2131755260 = requiredView13).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onCamButtonTouch(view, motionEvent);
            }
        });
        target.dialogQuestionText = Utils.findRequiredViewAsType(requiredView, 2131755636, "field 'dialogQuestionText'", TextView.class);
        target.objectNameView = Utils.findRequiredViewAsType(requiredView, 2131755271, "field 'objectNameView'", TextView.class);
        final View requiredView14 = Utils.findRequiredView(requiredView, 2131755262, "field 'buttonMoveBackward' and method 'onCamButtonTouch'");
        target.buttonMoveBackward = Utils.castView(requiredView14, 2131755262, "field 'buttonMoveBackward'", ImageButton.class);
        (this.view2131755262 = requiredView14).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onCamButtonTouch(view, motionEvent);
            }
        });
        target.voiceStatusView = Utils.findRequiredViewAsType(requiredView, 2131755254, "field 'voiceStatusView'", VoiceStatusView.class);
        final View requiredView15 = Utils.findRequiredView(requiredView, 2131755275, "field 'noButton' and method 'onNoButton'");
        target.noButton = Utils.castView(requiredView15, 2131755275, "field 'noButton'", ImageButton.class);
        (this.view2131755275 = requiredView15).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onNoButton();
            }
        });
        final View requiredView16 = Utils.findRequiredView(requiredView, 2131755255, "field 'buttonTouch' and method 'onTouchButton'");
        target.buttonTouch = Utils.castView(requiredView16, 2131755255, "field 'buttonTouch'", ImageButton.class);
        (this.view2131755255 = requiredView16).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onTouchButton();
            }
        });
        final View requiredView17 = Utils.findRequiredView(requiredView, 2131755261, "field 'buttonTurnLeft' and method 'onCamButtonTouch'");
        target.buttonTurnLeft = Utils.castView(requiredView17, 2131755261, "field 'buttonTurnLeft'", ImageButton.class);
        (this.view2131755261 = requiredView17).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onCamButtonTouch(view, motionEvent);
            }
        });
        final View requiredView18 = Utils.findRequiredView(requiredView, 2131755263, "field 'buttonTurnRight' and method 'onCamButtonTouch'");
        target.buttonTurnRight = Utils.castView(requiredView18, 2131755263, "field 'buttonTurnRight'", ImageButton.class);
        (this.view2131755263 = requiredView18).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onCamButtonTouch(view, motionEvent);
            }
        });
        final View requiredView19 = Utils.findRequiredView(requiredView, 2131755269, "field 'buttonSit' and method 'onObjectSit'");
        target.buttonSit = Utils.castView(requiredView19, 2131755269, "field 'buttonSit'", ImageButton.class);
        (this.view2131755269 = requiredView19).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onObjectSit();
            }
        });
        target.cardboardPrimaryControls = Utils.findRequiredViewAsType(requiredView, 2131755253, "field 'cardboardPrimaryControls'", ViewGroup.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755272, "method 'onYesNoOutsideTouch'");
        (this.view2131755272 = requiredView).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return target.onYesNoOutsideTouch(view, motionEvent);
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final CardboardActivity target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.buttonSpeak = null;
        target.buttonChat = null;
        target.buttonSpeechSend = null;
        target.speechRecognitionResults = null;
        target.chatsOverlayLayout = null;
        target.cardboardAimControls = null;
        target.cardboardSpeakControls = null;
        target.cardboardObjectControls = null;
        target.buttonTouchObject = null;
        target.cardboardScriptDialog = null;
        target.speakLevelIndicator = null;
        target.yesButton = null;
        target.buttonObjectChat = null;
        target.yesNoText = null;
        target.cardboardDetailsPage = null;
        target.moveButtonsLayout = null;
        target.buttonStandUp = null;
        target.speakNowText = null;
        target.buttonMoveForward = null;
        target.dialogQuestionText = null;
        target.objectNameView = null;
        target.buttonMoveBackward = null;
        target.voiceStatusView = null;
        target.noButton = null;
        target.buttonTouch = null;
        target.buttonTurnLeft = null;
        target.buttonTurnRight = null;
        target.buttonSit = null;
        target.cardboardPrimaryControls = null;
        this.view2131755256.setOnClickListener((View$OnClickListener)null);
        this.view2131755256 = null;
        this.view2131755257.setOnClickListener((View$OnClickListener)null);
        this.view2131755257 = null;
        this.view2131755281.setOnClickListener((View$OnClickListener)null);
        this.view2131755281 = null;
        this.view2131755265.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755265 = null;
        this.view2131755277.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755277 = null;
        this.view2131755266.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755266 = null;
        this.view2131755268.setOnClickListener((View$OnClickListener)null);
        this.view2131755268 = null;
        this.view2131755282.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755282 = null;
        this.view2131755274.setOnClickListener((View$OnClickListener)null);
        this.view2131755274 = null;
        this.view2131755270.setOnClickListener((View$OnClickListener)null);
        this.view2131755270 = null;
        this.view2131755264.setOnClickListener((View$OnClickListener)null);
        this.view2131755264 = null;
        this.view2131755260.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755260 = null;
        this.view2131755262.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755262 = null;
        this.view2131755275.setOnClickListener((View$OnClickListener)null);
        this.view2131755275 = null;
        this.view2131755255.setOnClickListener((View$OnClickListener)null);
        this.view2131755255 = null;
        this.view2131755261.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755261 = null;
        this.view2131755263.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755263 = null;
        this.view2131755269.setOnClickListener((View$OnClickListener)null);
        this.view2131755269 = null;
        this.view2131755272.setOnTouchListener((View$OnTouchListener)null);
        this.view2131755272 = null;
    }
}
