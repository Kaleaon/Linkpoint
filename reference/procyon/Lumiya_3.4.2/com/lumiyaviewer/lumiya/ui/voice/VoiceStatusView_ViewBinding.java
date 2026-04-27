// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.voice;

import android.support.annotation.CallSuper;
import android.widget.SeekBar;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.ImageButton;
import butterknife.internal.Utils;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;

public class VoiceStatusView_ViewBinding implements Unbinder
{
    private VoiceStatusView target;
    private View view2131755728;
    private View view2131755729;
    private View view2131755730;
    private View view2131755731;
    private View view2131755736;
    private View view2131755739;
    private View view2131755740;
    
    @UiThread
    public VoiceStatusView_ViewBinding(final VoiceStatusView voiceStatusView) {
        this(voiceStatusView, (View)voiceStatusView);
    }
    
    @UiThread
    public VoiceStatusView_ViewBinding(final VoiceStatusView target, final View view) {
        this.target = target;
        final View requiredView = Utils.findRequiredView(view, 2131755731, "field 'voiceAnswerButton' and method 'onVoiceAnswerButton'");
        target.voiceAnswerButton = Utils.castView(requiredView, 2131755731, "field 'voiceAnswerButton'", ImageButton.class);
        (this.view2131755731 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceAnswerButton();
            }
        });
        final View requiredView2 = Utils.findRequiredView(view, 2131755736, "field 'voiceTerminateButton' and method 'onVoiceTerminateButton'");
        target.voiceTerminateButton = Utils.castView(requiredView2, 2131755736, "field 'voiceTerminateButton'", ImageButton.class);
        (this.view2131755736 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceTerminateButton();
            }
        });
        target.voiceStatusSmallText = Utils.findRequiredViewAsType(view, 2131755734, "field 'voiceStatusSmallText'", TextView.class);
        final View requiredView3 = Utils.findRequiredView(view, 2131755740, "field 'voiceBluetoothButton' and method 'onVoiceBluetoothButton'");
        target.voiceBluetoothButton = Utils.castView(requiredView3, 2131755740, "field 'voiceBluetoothButton'", Button.class);
        (this.view2131755740 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceBluetoothButton();
            }
        });
        target.voiceSpeakIndicatorLeft = Utils.findRequiredViewAsType(view, 2131755732, "field 'voiceSpeakIndicatorLeft'", ImageView.class);
        final View requiredView4 = Utils.findRequiredView(view, 2131755729, "field 'voiceMicOnButton' and method 'onVoiceMicOnButton'");
        target.voiceMicOnButton = Utils.castView(requiredView4, 2131755729, "field 'voiceMicOnButton'", ImageButton.class);
        (this.view2131755729 = requiredView4).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceMicOnButton();
            }
        });
        target.voiceStatusControls = Utils.findRequiredViewAsType(view, 2131755737, "field 'voiceStatusControls'", ViewGroup.class);
        final View requiredView5 = Utils.findRequiredView(view, 2131755739, "field 'voiceLoudspeakerButton' and method 'onLoudspeakerButton'");
        target.voiceLoudspeakerButton = Utils.castView(requiredView5, 2131755739, "field 'voiceLoudspeakerButton'", Button.class);
        (this.view2131755739 = requiredView5).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onLoudspeakerButton();
            }
        });
        target.voiceStatusText = Utils.findRequiredViewAsType(view, 2131755733, "field 'voiceStatusText'", TextView.class);
        target.voiceSpeakIndicatorRight = Utils.findRequiredViewAsType(view, 2131755735, "field 'voiceSpeakIndicatorRight'", ImageView.class);
        final View requiredView6 = Utils.findRequiredView(view, 2131755730, "field 'voiceMicOffButton' and method 'onVoiceMicOffButton'");
        target.voiceMicOffButton = Utils.castView(requiredView6, 2131755730, "field 'voiceMicOffButton'", ImageButton.class);
        (this.view2131755730 = requiredView6).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceMicOffButton();
            }
        });
        final View requiredView7 = Utils.findRequiredView(view, 2131755728, "field 'voiceStatusCardView' and method 'onVoiceStatusCardClick'");
        target.voiceStatusCardView = Utils.castView(requiredView7, 2131755728, "field 'voiceStatusCardView'", CardView.class);
        (this.view2131755728 = requiredView7).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onVoiceStatusCardClick();
            }
        });
        target.voiceSpeakerVolumeControl = Utils.findRequiredViewAsType(view, 2131755738, "field 'voiceSpeakerVolumeControl'", SeekBar.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final VoiceStatusView target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.voiceAnswerButton = null;
        target.voiceTerminateButton = null;
        target.voiceStatusSmallText = null;
        target.voiceBluetoothButton = null;
        target.voiceSpeakIndicatorLeft = null;
        target.voiceMicOnButton = null;
        target.voiceStatusControls = null;
        target.voiceLoudspeakerButton = null;
        target.voiceStatusText = null;
        target.voiceSpeakIndicatorRight = null;
        target.voiceMicOffButton = null;
        target.voiceStatusCardView = null;
        target.voiceSpeakerVolumeControl = null;
        this.view2131755731.setOnClickListener((View$OnClickListener)null);
        this.view2131755731 = null;
        this.view2131755736.setOnClickListener((View$OnClickListener)null);
        this.view2131755736 = null;
        this.view2131755740.setOnClickListener((View$OnClickListener)null);
        this.view2131755740 = null;
        this.view2131755729.setOnClickListener((View$OnClickListener)null);
        this.view2131755729 = null;
        this.view2131755739.setOnClickListener((View$OnClickListener)null);
        this.view2131755739 = null;
        this.view2131755730.setOnClickListener((View$OnClickListener)null);
        this.view2131755730 = null;
        this.view2131755728.setOnClickListener((View$OnClickListener)null);
        this.view2131755728 = null;
    }
}
