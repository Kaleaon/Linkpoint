package com.lumiyaviewer.lumiya.ui.voice;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class VoiceStatusView_ViewBinding implements Unbinder {
    private VoiceStatusView target;
    private View view2131755728;
    private View view2131755729;
    private View view2131755730;
    private View view2131755731;
    private View view2131755736;
    private View view2131755739;
    private View view2131755740;

    @UiThread
    public VoiceStatusView_ViewBinding(VoiceStatusView voiceStatusView) {
        this(voiceStatusView, voiceStatusView);
    }

    @UiThread
    public VoiceStatusView_ViewBinding(final VoiceStatusView voiceStatusView, View view) {
        this.target = voiceStatusView;
        View findRequiredView = Utils.findRequiredView(view, R.id.voice_answer_button, "field 'voiceAnswerButton' and method 'onVoiceAnswerButton'");
        voiceStatusView.voiceAnswerButton = (ImageButton) Utils.castView(findRequiredView, R.id.voice_answer_button, "field 'voiceAnswerButton'", ImageButton.class);
        this.view2131755731 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceAnswerButton();
            }
        View findRequiredView2 = Utils.findRequiredView(view, R.id.voice_terminate_button, "field 'voiceTerminateButton' and method 'onVoiceTerminateButton'");
        voiceStatusView.voiceTerminateButton = (ImageButton) Utils.castView(findRequiredView2, R.id.voice_terminate_button, "field 'voiceTerminateButton'", ImageButton.class);
        this.view2131755736 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceTerminateButton();
            }
        voiceStatusView.voiceStatusSmallText = (TextView) Utils.findRequiredViewAsType(view, R.id.voice_status_small_text, "field 'voiceStatusSmallText'", TextView.class);
        View findRequiredView3 = Utils.findRequiredView(view, R.id.voice_bluetooth_button, "field 'voiceBluetoothButton' and method 'onVoiceBluetoothButton'");
        voiceStatusView.voiceBluetoothButton = (Button) Utils.castView(findRequiredView3, R.id.voice_bluetooth_button, "field 'voiceBluetoothButton'", Button.class);
        this.view2131755740 = findRequiredView3;
        findRequiredView3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceBluetoothButton();
            }
        voiceStatusView.voiceSpeakIndicatorLeft = (ImageView) Utils.findRequiredViewAsType(view, R.id.voice_speak_indicator_left, "field 'voiceSpeakIndicatorLeft'", ImageView.class);
        View findRequiredView4 = Utils.findRequiredView(view, R.id.voice_mic_on_button, "field 'voiceMicOnButton' and method 'onVoiceMicOnButton'");
        voiceStatusView.voiceMicOnButton = (ImageButton) Utils.castView(findRequiredView4, R.id.voice_mic_on_button, "field 'voiceMicOnButton'", ImageButton.class);
        this.view2131755729 = findRequiredView4;
        findRequiredView4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceMicOnButton();
            }
        voiceStatusView.voiceStatusControls = (ViewGroup) Utils.findRequiredViewAsType(view, R.id.voice_status_controls, "field 'voiceStatusControls'", ViewGroup.class);
        View findRequiredView5 = Utils.findRequiredView(view, R.id.voice_loudspeaker_button, "field 'voiceLoudspeakerButton' and method 'onLoudspeakerButton'");
        voiceStatusView.voiceLoudspeakerButton = (Button) Utils.castView(findRequiredView5, R.id.voice_loudspeaker_button, "field 'voiceLoudspeakerButton'", Button.class);
        this.view2131755739 = findRequiredView5;
        findRequiredView5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onLoudspeakerButton();
            }
        voiceStatusView.voiceStatusText = (TextView) Utils.findRequiredViewAsType(view, R.id.voice_status_text, "field 'voiceStatusText'", TextView.class);
        voiceStatusView.voiceSpeakIndicatorRight = (ImageView) Utils.findRequiredViewAsType(view, R.id.voice_speak_indicator_right, "field 'voiceSpeakIndicatorRight'", ImageView.class);
        View findRequiredView6 = Utils.findRequiredView(view, R.id.voice_mic_off_button, "field 'voiceMicOffButton' and method 'onVoiceMicOffButton'");
        voiceStatusView.voiceMicOffButton = (ImageButton) Utils.castView(findRequiredView6, R.id.voice_mic_off_button, "field 'voiceMicOffButton'", ImageButton.class);
        this.view2131755730 = findRequiredView6;
        findRequiredView6.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceMicOffButton();
            }
        View findRequiredView7 = Utils.findRequiredView(view, R.id.voice_status_card_view, "field 'voiceStatusCardView' and method 'onVoiceStatusCardClick'");
        voiceStatusView.voiceStatusCardView = (CardView) Utils.castView(findRequiredView7, R.id.voice_status_card_view, "field 'voiceStatusCardView'", CardView.class);
        this.view2131755728 = findRequiredView7;
        findRequiredView7.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                voiceStatusView.onVoiceStatusCardClick();
            }
        voiceStatusView.voiceSpeakerVolumeControl = (SeekBar) Utils.findRequiredViewAsType(view, R.id.voice_speaker_volume_control, "field 'voiceSpeakerVolumeControl'", SeekBar.class);
    }

    @CallSuper
    public void unbind() {
        VoiceStatusView voiceStatusView = this.target;
        if (voiceStatusView == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        voiceStatusView.voiceAnswerButton = null;
        voiceStatusView.voiceTerminateButton = null;
        voiceStatusView.voiceStatusSmallText = null;
        voiceStatusView.voiceBluetoothButton = null;
        voiceStatusView.voiceSpeakIndicatorLeft = null;
        voiceStatusView.voiceMicOnButton = null;
        voiceStatusView.voiceStatusControls = null;
        voiceStatusView.voiceLoudspeakerButton = null;
        voiceStatusView.voiceStatusText = null;
        voiceStatusView.voiceSpeakIndicatorRight = null;
        voiceStatusView.voiceMicOffButton = null;
        voiceStatusView.voiceStatusCardView = null;
        voiceStatusView.voiceSpeakerVolumeControl = null;
        this.view2131755731.setOnClickListener((View.OnClickListener) null);
        this.view2131755731 = null;
        this.view2131755736.setOnClickListener((View.OnClickListener) null);
        this.view2131755736 = null;
        this.view2131755740.setOnClickListener((View.OnClickListener) null);
        this.view2131755740 = null;
        this.view2131755729.setOnClickListener((View.OnClickListener) null);
        this.view2131755729 = null;
        this.view2131755739.setOnClickListener((View.OnClickListener) null);
        this.view2131755739 = null;
        this.view2131755730.setOnClickListener((View.OnClickListener) null);
        this.view2131755730 = null;
        this.view2131755728.setOnClickListener((View.OnClickListener) null);
        this.view2131755728 = null;
    }
}
