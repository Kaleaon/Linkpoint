// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

public class CardboardActivity_ViewBinding
    implements Unbinder
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

    public CardboardActivity_ViewBinding(CardboardActivity cardboardactivity)
    {
        this(cardboardactivity, cardboardactivity.getWindow().getDecorView());
    }

    public CardboardActivity_ViewBinding(final CardboardActivity target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f1000f8, "field 'buttonSpeak' and method 'onSpeakButton'");
        target.buttonSpeak = (ImageButton)Utils.castView(view1, 0x7f1000f8, "field 'buttonSpeak'", android/widget/ImageButton);
        view2131755256 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onSpeakButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1000f9, "field 'buttonChat' and method 'onChatButton'");
        target.buttonChat = (ImageButton)Utils.castView(view1, 0x7f1000f9, "field 'buttonChat'", android/widget/ImageButton);
        view2131755257 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onChatButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100111, "field 'buttonSpeechSend' and method 'onSpeechSendButton'");
        target.buttonSpeechSend = (ImageButton)Utils.castView(view1, 0x7f100111, "field 'buttonSpeechSend'", android/widget/ImageButton);
        view2131755281 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onSpeechSendButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.speechRecognitionResults = (TextView)Utils.findRequiredViewAsType(view, 0x7f100110, "field 'speechRecognitionResults'", android/widget/TextView);
        target.chatsOverlayLayout = (LinearLayout)Utils.findRequiredViewAsType(view, 0x7f1000fa, "field 'chatsOverlayLayout'", android/widget/LinearLayout);
        view1 = Utils.findRequiredView(view, 0x7f100101, "field 'cardboardAimControls' and method 'onAimControlsTouch'");
        target.cardboardAimControls = (ViewGroup)Utils.castView(view1, 0x7f100101, "field 'cardboardAimControls'", android/view/ViewGroup);
        view2131755265 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onAimControlsTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f10010d, "field 'cardboardSpeakControls' and method 'onSpeakControlsTouch'");
        target.cardboardSpeakControls = (ViewGroup)Utils.castView(view1, 0x7f10010d, "field 'cardboardSpeakControls'", android/view/ViewGroup);
        view2131755277 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onSpeakControlsTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100102, "field 'cardboardObjectControls' and method 'onObjectControlsTouch'");
        target.cardboardObjectControls = (ViewGroup)Utils.castView(view1, 0x7f100102, "field 'cardboardObjectControls'", android/view/ViewGroup);
        view2131755266 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onObjectControlsTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100104, "field 'buttonTouchObject' and method 'onObjectTouch'");
        target.buttonTouchObject = (ImageButton)Utils.castView(view1, 0x7f100104, "field 'buttonTouchObject'", android/widget/ImageButton);
        view2131755268 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectTouch();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100112, "field 'cardboardScriptDialog' and method 'onScriptDialogOutsideTouch'");
        target.cardboardScriptDialog = (ViewGroup)Utils.castView(view1, 0x7f100112, "field 'cardboardScriptDialog'", android/view/ViewGroup);
        view2131755282 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onScriptDialogOutsideTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.speakLevelIndicator = (ProgressBar)Utils.findRequiredViewAsType(view, 0x7f10010f, "field 'speakLevelIndicator'", android/widget/ProgressBar);
        view1 = Utils.findRequiredView(view, 0x7f10010a, "field 'yesButton' and method 'onYesButton'");
        target.yesButton = (ImageButton)Utils.castView(view1, 0x7f10010a, "field 'yesButton'", android/widget/ImageButton);
        view2131755274 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onYesButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100106, "field 'buttonObjectChat' and method 'onObjectChat'");
        target.buttonObjectChat = (ImageButton)Utils.castView(view1, 0x7f100106, "field 'buttonObjectChat'", android/widget/ImageButton);
        view2131755270 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectChat();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.yesNoText = (TextView)Utils.findRequiredViewAsType(view, 0x7f10010c, "field 'yesNoText'", android/widget/TextView);
        target.cardboardDetailsPage = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f100113, "field 'cardboardDetailsPage'", android/view/ViewGroup);
        target.moveButtonsLayout = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f1000fb, "field 'moveButtonsLayout'", android/view/ViewGroup);
        view1 = Utils.findRequiredView(view, 0x7f100100, "field 'buttonStandUp' and method 'onStandUpButton'");
        target.buttonStandUp = (ImageButton)Utils.castView(view1, 0x7f100100, "field 'buttonStandUp'", android/widget/ImageButton);
        view2131755264 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onStandUpButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.speakNowText = (TextView)Utils.findRequiredViewAsType(view, 0x7f10010e, "field 'speakNowText'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1000fc, "field 'buttonMoveForward' and method 'onCamButtonTouch'");
        target.buttonMoveForward = (ImageButton)Utils.castView(view1, 0x7f1000fc, "field 'buttonMoveForward'", android/widget/ImageButton);
        view2131755260 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onCamButtonTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.dialogQuestionText = (TextView)Utils.findRequiredViewAsType(view, 0x7f100274, "field 'dialogQuestionText'", android/widget/TextView);
        target.objectNameView = (TextView)Utils.findRequiredViewAsType(view, 0x7f100107, "field 'objectNameView'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1000fe, "field 'buttonMoveBackward' and method 'onCamButtonTouch'");
        target.buttonMoveBackward = (ImageButton)Utils.castView(view1, 0x7f1000fe, "field 'buttonMoveBackward'", android/widget/ImageButton);
        view2131755262 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onCamButtonTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.voiceStatusView = (VoiceStatusView)Utils.findRequiredViewAsType(view, 0x7f1000f6, "field 'voiceStatusView'", com/lumiyaviewer/lumiya/ui/voice/VoiceStatusView);
        view1 = Utils.findRequiredView(view, 0x7f10010b, "field 'noButton' and method 'onNoButton'");
        target.noButton = (ImageButton)Utils.castView(view1, 0x7f10010b, "field 'noButton'", android/widget/ImageButton);
        view2131755275 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onNoButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1000f7, "field 'buttonTouch' and method 'onTouchButton'");
        target.buttonTouch = (ImageButton)Utils.castView(view1, 0x7f1000f7, "field 'buttonTouch'", android/widget/ImageButton);
        view2131755255 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onTouchButton();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1000fd, "field 'buttonTurnLeft' and method 'onCamButtonTouch'");
        target.buttonTurnLeft = (ImageButton)Utils.castView(view1, 0x7f1000fd, "field 'buttonTurnLeft'", android/widget/ImageButton);
        view2131755261 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onCamButtonTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1000ff, "field 'buttonTurnRight' and method 'onCamButtonTouch'");
        target.buttonTurnRight = (ImageButton)Utils.castView(view1, 0x7f1000ff, "field 'buttonTurnRight'", android/widget/ImageButton);
        view2131755263 = view1;
        view1.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onCamButtonTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100105, "field 'buttonSit' and method 'onObjectSit'");
        target.buttonSit = (ImageButton)Utils.castView(view1, 0x7f100105, "field 'buttonSit'", android/widget/ImageButton);
        view2131755269 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public void doClick(View view2)
            {
                target.onObjectSit();
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
        target.cardboardPrimaryControls = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f1000f5, "field 'cardboardPrimaryControls'", android/view/ViewGroup);
        view = Utils.findRequiredView(view, 0x7f100108, "method 'onYesNoOutsideTouch'");
        view2131755272 = view;
        view.setOnTouchListener(new android.view.View.OnTouchListener() {

            final CardboardActivity_ViewBinding this$0;
            final CardboardActivity val$target;

            public boolean onTouch(View view2, MotionEvent motionevent)
            {
                return target.onYesNoOutsideTouch(view2, motionevent);
            }

            
            {
                this$0 = CardboardActivity_ViewBinding.this;
                target = cardboardactivity;
                super();
            }
        });
    }

    public void unbind()
    {
        CardboardActivity cardboardactivity = target;
        if (cardboardactivity == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            cardboardactivity.buttonSpeak = null;
            cardboardactivity.buttonChat = null;
            cardboardactivity.buttonSpeechSend = null;
            cardboardactivity.speechRecognitionResults = null;
            cardboardactivity.chatsOverlayLayout = null;
            cardboardactivity.cardboardAimControls = null;
            cardboardactivity.cardboardSpeakControls = null;
            cardboardactivity.cardboardObjectControls = null;
            cardboardactivity.buttonTouchObject = null;
            cardboardactivity.cardboardScriptDialog = null;
            cardboardactivity.speakLevelIndicator = null;
            cardboardactivity.yesButton = null;
            cardboardactivity.buttonObjectChat = null;
            cardboardactivity.yesNoText = null;
            cardboardactivity.cardboardDetailsPage = null;
            cardboardactivity.moveButtonsLayout = null;
            cardboardactivity.buttonStandUp = null;
            cardboardactivity.speakNowText = null;
            cardboardactivity.buttonMoveForward = null;
            cardboardactivity.dialogQuestionText = null;
            cardboardactivity.objectNameView = null;
            cardboardactivity.buttonMoveBackward = null;
            cardboardactivity.voiceStatusView = null;
            cardboardactivity.noButton = null;
            cardboardactivity.buttonTouch = null;
            cardboardactivity.buttonTurnLeft = null;
            cardboardactivity.buttonTurnRight = null;
            cardboardactivity.buttonSit = null;
            cardboardactivity.cardboardPrimaryControls = null;
            view2131755256.setOnClickListener(null);
            view2131755256 = null;
            view2131755257.setOnClickListener(null);
            view2131755257 = null;
            view2131755281.setOnClickListener(null);
            view2131755281 = null;
            view2131755265.setOnTouchListener(null);
            view2131755265 = null;
            view2131755277.setOnTouchListener(null);
            view2131755277 = null;
            view2131755266.setOnTouchListener(null);
            view2131755266 = null;
            view2131755268.setOnClickListener(null);
            view2131755268 = null;
            view2131755282.setOnTouchListener(null);
            view2131755282 = null;
            view2131755274.setOnClickListener(null);
            view2131755274 = null;
            view2131755270.setOnClickListener(null);
            view2131755270 = null;
            view2131755264.setOnClickListener(null);
            view2131755264 = null;
            view2131755260.setOnTouchListener(null);
            view2131755260 = null;
            view2131755262.setOnTouchListener(null);
            view2131755262 = null;
            view2131755275.setOnClickListener(null);
            view2131755275 = null;
            view2131755255.setOnClickListener(null);
            view2131755255 = null;
            view2131755261.setOnTouchListener(null);
            view2131755261 = null;
            view2131755263.setOnTouchListener(null);
            view2131755263 = null;
            view2131755269.setOnClickListener(null);
            view2131755269 = null;
            view2131755272.setOnTouchListener(null);
            view2131755272 = null;
            return;
        }
    }
}
