// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.voice;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.animation.AnimationUtils;
import butterknife.OnClick;
import android.content.res.TypedArray;
import android.view.View$OnHoverListener;
import android.util.TypedValue;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import com.google.common.base.Objects;
import android.graphics.drawable.AnimationDrawable;
import com.lumiyaviewer.lumiya.Debug;
import android.view.MotionEvent;
import com.lumiyaviewer.lumiya.ui.render.OnHoverListenerCompat;
import android.view.View;
import butterknife.ButterKnife;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.content.Context;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.SeekBar;
import android.widget.ImageView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import android.widget.Button;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import butterknife.BindView;
import android.widget.ImageButton;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.FrameLayout;

public class VoiceStatusView extends FrameLayout
{
    @Nullable
    private ChatterID activeChatterID;
    @Nullable
    private ChatterNameRetriever activeChatterNameRetriever;
    private boolean canConnect;
    @Nullable
    private ChatterID chatterID;
    private boolean hoverEnabled;
    private final ChatterNameRetriever.OnChatterNameUpdated onActiveSpeakerNameUpdated;
    private View$OnClickListener onCallButtonListener;
    private boolean showActiveChatterName;
    private boolean showWhenInactive;
    @Nullable
    private ChatterNameRetriever speakerNameRetriever;
    private boolean updatingAudioVolume;
    @BindView(2131755731)
    ImageButton voiceAnswerButton;
    private final SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties> voiceAudioProperties;
    @BindView(2131755740)
    Button voiceBluetoothButton;
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo;
    @BindView(2131755739)
    Button voiceLoudspeakerButton;
    @BindView(2131755730)
    ImageButton voiceMicOffButton;
    @BindView(2131755729)
    ImageButton voiceMicOnButton;
    @BindView(2131755732)
    ImageView voiceSpeakIndicatorLeft;
    @BindView(2131755735)
    ImageView voiceSpeakIndicatorRight;
    @BindView(2131755738)
    SeekBar voiceSpeakerVolumeControl;
    @BindView(2131755728)
    CardView voiceStatusCardView;
    @BindView(2131755737)
    ViewGroup voiceStatusControls;
    @BindView(2131755734)
    TextView voiceStatusSmallText;
    @BindView(2131755733)
    TextView voiceStatusText;
    @BindView(2131755736)
    ImageButton voiceTerminateButton;
    private final SeekBar$OnSeekBarChangeListener volumeChangeListener;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues() {
        if (VoiceStatusView.-com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues != null) {
            return VoiceStatusView.-com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues = new int[VoiceBluetoothState.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues[VoiceBluetoothState.Active.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues[VoiceBluetoothState.Connected.ordinal()] = 6;
                    try {
                        -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues[VoiceBluetoothState.Connecting.ordinal()] = 2;
                        try {
                            -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues[VoiceBluetoothState.Disconnected.ordinal()] = 7;
                            try {
                                -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues[VoiceBluetoothState.Error.ordinal()] = 8;
                                return -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues = -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {}
            }
            catch (final NoSuchFieldError noSuchFieldError5) {
                continue;
            }
            break;
        }
    }
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues() {
        if (VoiceStatusView.-com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues != null) {
            return VoiceStatusView.-com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues = new int[VoiceChatInfo.VoiceChatState.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues[VoiceChatInfo.VoiceChatState.Active.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues[VoiceChatInfo.VoiceChatState.Connecting.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues[VoiceChatInfo.VoiceChatState.None.ordinal()] = 6;
                        try {
                            -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues[VoiceChatInfo.VoiceChatState.Ringing.ordinal()] = 3;
                            return -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues = -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues;
                        }
                        catch (final NoSuchFieldError noSuchFieldError) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError2) {}
                }
                catch (final NoSuchFieldError noSuchFieldError3) {}
            }
            catch (final NoSuchFieldError noSuchFieldError4) {
                continue;
            }
            break;
        }
    }
    
    public VoiceStatusView(final Context context) {
        super(context);
        this.voiceAudioProperties = new SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$1(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$2(this));
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b && (VoiceStatusView.this.updatingAudioVolume ^ true)) {
                    final float n2 = n / (float)seekBar.getMax();
                    final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null) {
                        final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
                        if (voicePluginServiceConnection != null) {
                            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(n2, true, null));
                        }
                    }
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$9(this);
        LayoutInflater.from(context).inflate(2130968763, (ViewGroup)this, true);
        this.initializeControls();
    }
    
    public VoiceStatusView(final Context context, final AttributeSet set) {
        super(context, set);
        this.voiceAudioProperties = new SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$3(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$4(this));
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b && (VoiceStatusView.this.updatingAudioVolume ^ true)) {
                    final float n2 = n / (float)seekBar.getMax();
                    final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null) {
                        final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
                        if (voicePluginServiceConnection != null) {
                            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(n2, true, null));
                        }
                    }
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$10(this);
        LayoutInflater.from(context).inflate(2130968763, (ViewGroup)this, true);
        this.initializeControls();
    }
    
    public VoiceStatusView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.voiceAudioProperties = new SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$5(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$6(this));
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b && (VoiceStatusView.this.updatingAudioVolume ^ true)) {
                    final float n2 = n / (float)seekBar.getMax();
                    final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null) {
                        final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
                        if (voicePluginServiceConnection != null) {
                            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(n2, true, null));
                        }
                    }
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$11(this);
        LayoutInflater.from(context).inflate(2130968763, (ViewGroup)this, true);
        this.initializeControls();
    }
    
    @TargetApi(21)
    public VoiceStatusView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.voiceAudioProperties = new SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$7(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$8(this));
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b && (VoiceStatusView.this.updatingAudioVolume ^ true)) {
                    final float n2 = n / (float)seekBar.getMax();
                    final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null) {
                        final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
                        if (voicePluginServiceConnection != null) {
                            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(n2, true, null));
                        }
                    }
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$12(this);
        LayoutInflater.from(context).inflate(2130968763, (ViewGroup)this, true);
        this.initializeControls();
    }
    
    private void initializeControls() {
        ButterKnife.bind((View)this);
        this.updateVoiceState();
        this.voiceSpeakerVolumeControl.setOnSeekBarChangeListener(this.volumeChangeListener);
    }
    
    private void onVoiceAudioProperties(final VoiceAudioProperties voiceAudioProperties) {
        Object bluetoothState = null;
        if (voiceAudioProperties != null) {
            bluetoothState = voiceAudioProperties.bluetoothState;
        }
        Debug.Printf("Voice: voice audio properties updated, bluetooth state %s", bluetoothState);
        this.updateVoiceState();
    }
    
    private void onVoiceChatInfo(final VoiceChatInfo voiceChatInfo) {
        this.updateVoiceState();
    }
    
    private void updateVoiceState() {
        int visibility = 4;
        final VoiceChatInfo voiceChatInfo = this.voiceChatInfo.getData();
        Debug.Printf("VoiceStatusView: voice state %s", voiceChatInfo);
        ChatterID userChatterID;
        if (voiceChatInfo != null && (voiceChatInfo.state.equals(VoiceChatInfo.VoiceChatState.None) ^ true)) {
            this.setVisibility(0);
            this.voiceTerminateButton.setVisibility(0);
            boolean b;
            if (voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                b = true;
            }
            else {
                b = false;
            }
            final ImageButton voiceMicOnButton = this.voiceMicOnButton;
            int visibility2;
            if (b && voiceChatInfo.localMicActive) {
                visibility2 = 0;
            }
            else {
                visibility2 = 4;
            }
            voiceMicOnButton.setVisibility(visibility2);
            final ImageButton voiceMicOffButton = this.voiceMicOffButton;
            int visibility3;
            if (b && (voiceChatInfo.localMicActive ^ true)) {
                visibility3 = 0;
            }
            else {
                visibility3 = 4;
            }
            voiceMicOffButton.setVisibility(visibility3);
            final ImageButton voiceAnswerButton = this.voiceAnswerButton;
            int visibility4;
            if (voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Ringing) {
                visibility4 = 0;
            }
            else {
                visibility4 = 4;
            }
            voiceAnswerButton.setVisibility(visibility4);
            String s;
            if (voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Active && voiceChatInfo.numActiveSpeakers != 0) {
                if (this.voiceSpeakIndicatorLeft.getVisibility() != 0 || this.voiceSpeakIndicatorRight.getVisibility() != 0) {
                    this.voiceSpeakIndicatorLeft.setVisibility(0);
                    this.voiceSpeakIndicatorRight.setVisibility(0);
                    final Drawable drawable = this.voiceSpeakIndicatorLeft.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        ((AnimationDrawable)drawable).start();
                    }
                    final Drawable drawable2 = this.voiceSpeakIndicatorRight.getDrawable();
                    if (drawable2 instanceof AnimationDrawable) {
                        ((AnimationDrawable)drawable2).start();
                    }
                }
                if (voiceChatInfo.numActiveSpeakers == 1 && voiceChatInfo.activeSpeakerID != null && this.chatterID != null) {
                    userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, voiceChatInfo.activeSpeakerID);
                    if (this.speakerNameRetriever != null) {
                        if (Objects.equal(userChatterID, this.speakerNameRetriever.chatterID)) {
                            s = this.speakerNameRetriever.getResolvedName();
                        }
                        else {
                            s = null;
                        }
                    }
                    else {
                        s = null;
                    }
                }
                else {
                    s = null;
                    userChatterID = null;
                }
                Debug.Printf("Voice: numActiveSpeakers %d, speakerName %s, activeChatterID %s (view chatterID %s)", voiceChatInfo.numActiveSpeakers, s, userChatterID, this.chatterID);
                final ChatterNameRetriever speakerNameRetriever = this.speakerNameRetriever;
                ChatterID chatterID;
                if (this.speakerNameRetriever != null) {
                    chatterID = this.speakerNameRetriever.chatterID;
                }
                else {
                    chatterID = null;
                }
                Debug.Printf("Voice: speakerNameRetriever %s, snr.chatterID %s", speakerNameRetriever, chatterID);
                if (s == null) {
                    if (voiceChatInfo.numActiveSpeakers != 1) {
                        s = this.getContext().getString(2131297043, new Object[] { voiceChatInfo.numActiveSpeakers });
                    }
                    else {
                        s = null;
                    }
                }
            }
            else if (voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Ringing) {
                this.voiceSpeakIndicatorLeft.setVisibility(0);
                this.voiceSpeakIndicatorRight.setVisibility(0);
                s = null;
                userChatterID = null;
            }
            else {
                this.voiceSpeakIndicatorLeft.setVisibility(4);
                this.voiceSpeakIndicatorRight.setVisibility(4);
                s = null;
                userChatterID = null;
            }
            String s2;
            if ((s2 = s) == null) {
                switch (-getcom-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues()[voiceChatInfo.state.ordinal()]) {
                    default: {
                        s2 = s;
                        break;
                    }
                    case 3: {
                        s2 = this.getContext().getString(2131297153);
                        break;
                    }
                    case 2: {
                        s2 = this.getContext().getString(2131297152);
                        break;
                    }
                    case 1: {
                        if (voiceChatInfo.localMicActive) {
                            s2 = this.getContext().getString(2131297154);
                            break;
                        }
                        s2 = this.getContext().getString(2131297155);
                        break;
                    }
                }
            }
            String resolvedName;
            if (this.showActiveChatterName) {
                if (this.activeChatterNameRetriever != null) {
                    resolvedName = this.activeChatterNameRetriever.getResolvedName();
                }
                else {
                    resolvedName = null;
                }
            }
            else {
                resolvedName = null;
            }
            if (resolvedName != null) {
                this.voiceStatusSmallText.setVisibility(0);
                this.voiceStatusText.setText((CharSequence)resolvedName);
                this.voiceStatusSmallText.setText((CharSequence)s2);
            }
            else {
                this.voiceStatusSmallText.setText((CharSequence)null);
                this.voiceStatusText.setText((CharSequence)s2);
                this.voiceStatusSmallText.setVisibility(8);
                if (voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                    if (voiceChatInfo.localMicActive) {
                        this.voiceStatusSmallText.setText(2131297081);
                        this.voiceStatusSmallText.setVisibility(0);
                    }
                }
            }
        }
        else {
            this.voiceStatusControls.setVisibility(8);
            if (!this.showWhenInactive) {
                this.setVisibility(8);
            }
            else {
                this.setVisibility(0);
                this.voiceStatusText.setText(2131297149);
                if (this.canConnect) {
                    this.voiceStatusSmallText.setText(2131297156);
                    this.voiceStatusSmallText.setVisibility(0);
                }
                else {
                    this.voiceStatusSmallText.setVisibility(8);
                }
                this.voiceSpeakIndicatorLeft.setVisibility(4);
                this.voiceSpeakIndicatorRight.setVisibility(4);
                this.voiceTerminateButton.setVisibility(4);
                this.voiceMicOnButton.setVisibility(4);
                this.voiceMicOffButton.setVisibility(4);
                final ImageButton voiceAnswerButton2 = this.voiceAnswerButton;
                if (this.canConnect) {
                    visibility = 0;
                }
                voiceAnswerButton2.setVisibility(visibility);
            }
            userChatterID = null;
        }
        if (!Objects.equal(this.activeChatterID, userChatterID)) {
            Debug.Printf("Voice: new activeChatterID %s", userChatterID);
            if ((this.activeChatterID = userChatterID) != null) {
                if (this.speakerNameRetriever == null || (Objects.equal(this.speakerNameRetriever.chatterID, userChatterID) ^ true)) {
                    if (this.speakerNameRetriever != null) {
                        this.speakerNameRetriever.dispose();
                    }
                    (this.speakerNameRetriever = new ChatterNameRetriever(userChatterID, this.onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false)).subscribe();
                }
            }
            else if (this.speakerNameRetriever != null) {
                this.speakerNameRetriever.dispose();
                this.speakerNameRetriever = null;
            }
        }
        final VoiceAudioProperties voiceAudioProperties = this.voiceAudioProperties.getData();
        if (voiceAudioProperties != null) {
            final Drawable[] compoundDrawables = this.voiceBluetoothButton.getCompoundDrawables();
            int n = 0;
            switch (-getcom-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues()[voiceAudioProperties.bluetoothState.ordinal()]) {
                default: {
                    n = 2130837692;
                    break;
                }
                case 1: {
                    n = 2130837589;
                    break;
                }
                case 2: {
                    n = 2130837862;
                    break;
                }
            }
            this.voiceBluetoothButton.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], (Drawable)null, (Drawable)null, ContextCompat.getDrawable(this.getContext(), n));
            final Drawable[] compoundDrawables2 = this.voiceLoudspeakerButton.getCompoundDrawables();
            final Button voiceLoudspeakerButton = this.voiceLoudspeakerButton;
            final Drawable drawable3 = compoundDrawables2[0];
            final Context context = this.getContext();
            int n2;
            if (voiceAudioProperties.speakerphoneOn) {
                n2 = 2130837589;
            }
            else {
                n2 = 2130837692;
            }
            voiceLoudspeakerButton.setCompoundDrawablesWithIntrinsicBounds(drawable3, (Drawable)null, (Drawable)null, ContextCompat.getDrawable(context, n2));
            this.updatingAudioVolume = true;
            this.voiceSpeakerVolumeControl.setProgress(Math.round(voiceAudioProperties.speakerVolume * this.voiceSpeakerVolumeControl.getMax()));
            this.updatingAudioVolume = false;
        }
    }
    
    public void disableMic() {
        if (this.chatterID != null) {
            final VoiceChatInfo voiceChatInfo = this.voiceChatInfo.getData();
            if (voiceChatInfo != null && voiceChatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                if (serviceInstance != null) {
                    serviceInstance.enableVoiceMic(false);
                }
            }
        }
    }
    
    public void enableHover(final OnHoverListenerCompat onHoverListenerCompat) {
        if (Build$VERSION.SDK_INT >= 21) {
            this.hoverEnabled = true;
            final _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8 onHoverListener = new _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8(onHoverListenerCompat);
            final int n = (int)TypedValue.applyDimension(1, 14.0f, this.getResources().getDisplayMetrics());
            final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(new int[] { 2130772057 });
            for (final ImageButton imageButton : new ImageButton[] { this.voiceAnswerButton, this.voiceTerminateButton, this.voiceMicOnButton, this.voiceMicOffButton }) {
                imageButton.setOnHoverListener((View$OnHoverListener)onHoverListener);
                if (imageButton == this.voiceMicOnButton) {
                    imageButton.setBackground(this.getContext().getDrawable(2130837612));
                }
                else {
                    imageButton.setBackground(obtainStyledAttributes.getDrawable(0));
                }
                imageButton.setPadding(n, n, n, n);
                imageButton.setAlpha(0.5f);
            }
            obtainStyledAttributes.recycle();
        }
    }
    
    public void hideBackground() {
        this.voiceStatusCardView.setCardBackgroundColor(0);
        this.voiceStatusCardView.setCardElevation(0.0f);
    }
    
    @OnClick({ 2131755739 })
    public void onLoudspeakerButton() {
        final VoiceAudioProperties voiceAudioProperties = this.voiceAudioProperties.getData();
        final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
            if (voicePluginServiceConnection != null && voiceAudioProperties != null) {
                VoiceAudioDevice voiceAudioDevice;
                if (voiceAudioProperties.speakerphoneOn) {
                    voiceAudioDevice = VoiceAudioDevice.Default;
                }
                else {
                    voiceAudioDevice = VoiceAudioDevice.Loudspeaker;
                }
                voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, voiceAudioDevice));
            }
        }
    }
    
    @OnClick({ 2131755731 })
    public void onVoiceAnswerButton() {
        final VoiceChatInfo voiceChatInfo = this.voiceChatInfo.getData();
        if (this.onCallButtonListener != null && (this.chatterID == null || voiceChatInfo == null || voiceChatInfo.state == VoiceChatInfo.VoiceChatState.None)) {
            this.onCallButtonListener.onClick((View)this.voiceAnswerButton);
        }
        if (this.chatterID != null) {
            final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.acceptVoiceCall(this.chatterID);
            }
        }
    }
    
    @OnClick({ 2131755740 })
    public void onVoiceBluetoothButton() {
        final VoiceAudioProperties voiceAudioProperties = this.voiceAudioProperties.getData();
        final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            final VoicePluginServiceConnection voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection();
            if (voicePluginServiceConnection != null && voiceAudioProperties != null) {
                VoiceAudioDevice voiceAudioDevice;
                if (voiceAudioProperties.bluetoothState == VoiceBluetoothState.Active) {
                    voiceAudioDevice = VoiceAudioDevice.Default;
                }
                else {
                    voiceAudioDevice = VoiceAudioDevice.Bluetooth;
                }
                voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, voiceAudioDevice));
            }
        }
    }
    
    @OnClick({ 2131755730 })
    public void onVoiceMicOffButton() {
        final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(true);
        }
    }
    
    @OnClick({ 2131755729 })
    public void onVoiceMicOnButton() {
        final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(false);
        }
    }
    
    @OnClick({ 2131755728 })
    public void onVoiceStatusCardClick() {
        if (this.voiceStatusControls != null) {
            if (this.voiceStatusControls.getVisibility() != 0) {
                this.voiceStatusControls.setVisibility(0);
                this.voiceStatusControls.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 2131034126));
            }
            else {
                this.voiceStatusControls.setVisibility(8);
            }
        }
    }
    
    @OnClick({ 2131755736 })
    public void onVoiceTerminateButton() {
        if (this.chatterID != null) {
            final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.terminateVoiceCall(this.chatterID);
            }
        }
    }
    
    public void setCanConnect(final boolean canConnect) {
        this.canConnect = canConnect;
        this.updateVoiceState();
    }
    
    public void setChatterID(@Nullable final ChatterID chatterID) {
        this.chatterID = chatterID;
        if (chatterID != null) {
            final UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                this.voiceAudioProperties.subscribe(userManager.getVoiceAudioProperties(), SubscriptionSingleKey.Value);
                this.voiceChatInfo.subscribe(userManager.getVoiceChatInfo(), chatterID);
                if (this.activeChatterNameRetriever != null && (Objects.equal(this.activeChatterNameRetriever.chatterID, chatterID) ^ true)) {
                    this.activeChatterNameRetriever.dispose();
                    this.activeChatterNameRetriever = null;
                }
                if (this.showActiveChatterName) {
                    (this.activeChatterNameRetriever = new ChatterNameRetriever(chatterID, this.onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false)).subscribe();
                }
            }
        }
        else {
            this.voiceAudioProperties.unsubscribe();
            this.voiceChatInfo.unsubscribe();
            if (this.speakerNameRetriever != null) {
                this.speakerNameRetriever.dispose();
                this.speakerNameRetriever = null;
            }
            if (this.activeChatterNameRetriever != null) {
                this.activeChatterNameRetriever.dispose();
                this.activeChatterNameRetriever = null;
            }
            final GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.enableVoiceMic(false);
            }
        }
        this.updateVoiceState();
    }
    
    public void setLightTheme() {
        this.voiceStatusText.setTextColor(-1);
        this.voiceStatusSmallText.setTextColor(-1);
        this.voiceAnswerButton.setImageResource(2130837690);
        this.voiceMicOnButton.setImageResource(2130837677);
        this.voiceMicOffButton.setImageResource(2130837679);
        this.voiceTerminateButton.setImageResource(2130837736);
    }
    
    public void setOnCallButtonListener(final View$OnClickListener onCallButtonListener) {
        this.onCallButtonListener = onCallButtonListener;
    }
    
    public void setShowActiveChatterName(final boolean showActiveChatterName) {
        this.showActiveChatterName = showActiveChatterName;
        this.updateVoiceState();
    }
    
    public void setShowWhenInactive(final boolean showWhenInactive) {
        this.showWhenInactive = showWhenInactive;
        this.updateVoiceState();
    }
}
