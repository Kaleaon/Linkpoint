package com.lumiyaviewer.lumiya.ui.voice;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.render.OnHoverListenerCompat;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class VoiceStatusView extends FrameLayout {

    /* renamed from: -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f606xccb18979 = null;

    /* renamed from: -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f607xfb1ca037 = null;

    @Nullable
    private ChatterID activeChatterID;

    @Nullable
    private ChatterNameRetriever activeChatterNameRetriever;
    private boolean canConnect;

    @Nullable
    private ChatterID chatterID;
    private boolean hoverEnabled;
    private final ChatterNameRetriever.OnChatterNameUpdated onActiveSpeakerNameUpdated;
    private View.OnClickListener onCallButtonListener;
    private boolean showActiveChatterName;
    private boolean showWhenInactive;

    @Nullable
    private ChatterNameRetriever speakerNameRetriever;
    private boolean updatingAudioVolume;

    @BindView(R.id.voice_answer_button)
    ImageButton voiceAnswerButton;
    private final SubscriptionData<SubscriptionSingleKey, VoiceAudioProperties> voiceAudioProperties;

    @BindView(R.id.voice_bluetooth_button)
    Button voiceBluetoothButton;
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo;

    @BindView(R.id.voice_loudspeaker_button)
    Button voiceLoudspeakerButton;

    @BindView(R.id.voice_mic_off_button)
    ImageButton voiceMicOffButton;

    @BindView(R.id.voice_mic_on_button)
    ImageButton voiceMicOnButton;

    @BindView(R.id.voice_speak_indicator_left)
    ImageView voiceSpeakIndicatorLeft;

    @BindView(R.id.voice_speak_indicator_right)
    ImageView voiceSpeakIndicatorRight;

    @BindView(R.id.voice_speaker_volume_control)
    SeekBar voiceSpeakerVolumeControl;

    @BindView(R.id.voice_status_card_view)
    CardView voiceStatusCardView;

    @BindView(R.id.voice_status_controls)
    ViewGroup voiceStatusControls;

    @BindView(R.id.voice_status_small_text)
    TextView voiceStatusSmallText;

    @BindView(R.id.voice_status_text)
    TextView voiceStatusText;

    @BindView(R.id.voice_terminate_button)
    ImageButton voiceTerminateButton;
    private final SeekBar.OnSeekBarChangeListener volumeChangeListener;

    /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m874xc8b7c355() {
        if (f606xccb18979 != null) {
            return f606xccb18979;
        }
        int[] iArr = new int[VoiceBluetoothState.values().length];
        try {
            iArr[VoiceBluetoothState.Active.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[VoiceBluetoothState.Connected.ordinal()] = 6;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[VoiceBluetoothState.Connecting.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[VoiceBluetoothState.Disconnected.ordinal()] = 7;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[VoiceBluetoothState.Error.ordinal()] = 8;
        } catch (NoSuchFieldError e5) {
        }
        f606xccb18979 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m875x6f23bddb() {
        if (f607xfb1ca037 != null) {
            return f607xfb1ca037;
        }
        int[] iArr = new int[VoiceChatInfo.VoiceChatState.values().length];
        try {
            iArr[VoiceChatInfo.VoiceChatState.Active.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[VoiceChatInfo.VoiceChatState.Connecting.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[VoiceChatInfo.VoiceChatState.None.ordinal()] = 6;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[VoiceChatInfo.VoiceChatState.Ringing.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        f607xfb1ca037 = iArr;
        return iArr;
    }

    public VoiceStatusView(Context context) {
        super(context);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.1
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m877com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0((VoiceAudioProperties) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.2
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m878com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1((VoiceChatInfo) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = i / seekBar.getMax();
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null) {
                        return;
                    }
                    voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, null));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.9
            private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                ((VoiceStatusView) this).m879lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(chatterNameRetriever);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
            public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                $m$0(chatterNameRetriever);
            }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, (ViewGroup) this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.3
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m877com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0((VoiceAudioProperties) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.4
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m878com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1((VoiceChatInfo) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = i / seekBar.getMax();
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null) {
                        return;
                    }
                    voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, null));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.10
            private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                ((VoiceStatusView) this).m879lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(chatterNameRetriever);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
            public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                $m$0(chatterNameRetriever);
            }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, (ViewGroup) this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.5
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m877com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0((VoiceAudioProperties) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.6
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m878com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1((VoiceChatInfo) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = i2 / seekBar.getMax();
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null) {
                        return;
                    }
                    voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, null));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.11
            private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                ((VoiceStatusView) this).m879lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(chatterNameRetriever);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
            public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                $m$0(chatterNameRetriever);
            }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, (ViewGroup) this, true);
        initializeControls();
    }

    @TargetApi(21)
    public VoiceStatusView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.7
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m877com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0((VoiceAudioProperties) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.8
            private final /* synthetic */ void $m$0(Object obj) {
                ((VoiceStatusView) this).m878com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1((VoiceChatInfo) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.speakerNameRetriever = null;
        this.activeChatterNameRetriever = null;
        this.activeChatterID = null;
        this.showActiveChatterName = false;
        this.showWhenInactive = false;
        this.canConnect = false;
        this.hoverEnabled = false;
        this.onCallButtonListener = null;
        this.updatingAudioVolume = false;
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i22, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = i22 / seekBar.getMax();
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null) {
                        return;
                    }
                    voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, null));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.12
            private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                ((VoiceStatusView) this).m879lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(chatterNameRetriever);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
            public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                $m$0(chatterNameRetriever);
            }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, (ViewGroup) this, true);
        initializeControls();
    }

    private void initializeControls() {
        ButterKnife.bind(this);
        updateVoiceState();
        this.voiceSpeakerVolumeControl.setOnSeekBarChangeListener(this.volumeChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407, reason: not valid java name */
    public static /* synthetic */ boolean m876lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407(OnHoverListenerCompat onHoverListenerCompat, View view, MotionEvent motionEvent) {
        if (!(view instanceof ImageButton)) {
            return false;
        }
        switch (motionEvent.getActionMasked()) {
            case 9:
                onHoverListenerCompat.onHoverEnter(view);
                return false;
            case 10:
                onHoverListenerCompat.onHoverExit(view);
                return false;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceAudioProperties, reason: merged with bridge method [inline-methods] */
    public void m877com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0(VoiceAudioProperties voiceAudioProperties) {
        Object[] objArr = new Object[1];
        objArr[0] = voiceAudioProperties != null ? voiceAudioProperties.bluetoothState : null;
        Debug.Printf("Voice: voice audio properties updated, bluetooth state %s", objArr);
        updateVoiceState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo, reason: merged with bridge method [inline-methods] */
    public void m878com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1(VoiceChatInfo voiceChatInfo) {
        updateVoiceState();
    }

    private void updateVoiceState() {
        ChatterID.ChatterIDUser chatterIDUser;
        int i;
        String str;
        ChatterID.ChatterIDUser chatterIDUser2;
        VoiceChatInfo data = this.voiceChatInfo.getData();
        Debug.Printf("VoiceStatusView: voice state %s", data);
        if (data == null || !(!data.state.equals(VoiceChatInfo.VoiceChatState.None))) {
            this.voiceStatusControls.setVisibility(8);
            if (this.showWhenInactive) {
                setVisibility(0);
                this.voiceStatusText.setText(R.string.voice_not_connected);
                if (this.canConnect) {
                    this.voiceStatusSmallText.setText(R.string.voice_tap_to_connect);
                    this.voiceStatusSmallText.setVisibility(0);
                } else {
                    this.voiceStatusSmallText.setVisibility(8);
                }
                this.voiceSpeakIndicatorLeft.setVisibility(4);
                this.voiceSpeakIndicatorRight.setVisibility(4);
                this.voiceTerminateButton.setVisibility(4);
                this.voiceMicOnButton.setVisibility(4);
                this.voiceMicOffButton.setVisibility(4);
                this.voiceAnswerButton.setVisibility(this.canConnect ? 0 : 4);
            } else {
                setVisibility(8);
            }
            chatterIDUser = null;
        } else {
            setVisibility(0);
            this.voiceTerminateButton.setVisibility(0);
            boolean z = data.state == VoiceChatInfo.VoiceChatState.Active;
            this.voiceMicOnButton.setVisibility((z && data.localMicActive) ? 0 : 4);
            this.voiceMicOffButton.setVisibility((z && (data.localMicActive ^ true)) ? 0 : 4);
            this.voiceAnswerButton.setVisibility(data.state == VoiceChatInfo.VoiceChatState.Ringing ? 0 : 4);
            if (data.state == VoiceChatInfo.VoiceChatState.Active && data.numActiveSpeakers != 0) {
                if (this.voiceSpeakIndicatorLeft.getVisibility() != 0 || this.voiceSpeakIndicatorRight.getVisibility() != 0) {
                    this.voiceSpeakIndicatorLeft.setVisibility(0);
                    this.voiceSpeakIndicatorRight.setVisibility(0);
                    Drawable drawable = this.voiceSpeakIndicatorLeft.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        ((AnimationDrawable) drawable).start();
                    }
                    Drawable drawable2 = this.voiceSpeakIndicatorRight.getDrawable();
                    if (drawable2 instanceof AnimationDrawable) {
                        ((AnimationDrawable) drawable2).start();
                    }
                }
                if (data.numActiveSpeakers != 1 || data.activeSpeakerID == null || this.chatterID == null) {
                    str = null;
                    chatterIDUser2 = null;
                } else {
                    chatterIDUser2 = ChatterID.getUserChatterID(this.chatterID.agentUUID, data.activeSpeakerID);
                    str = this.speakerNameRetriever != null ? Objects.equal(chatterIDUser2, this.speakerNameRetriever.chatterID) ? this.speakerNameRetriever.getResolvedName() : null : null;
                }
                Debug.Printf("Voice: numActiveSpeakers %d, speakerName %s, activeChatterID %s (view chatterID %s)", Integer.valueOf(data.numActiveSpeakers), str, chatterIDUser2, this.chatterID);
                Object[] objArr = new Object[2];
                objArr[0] = this.speakerNameRetriever;
                objArr[1] = this.speakerNameRetriever != null ? this.speakerNameRetriever.chatterID : null;
                Debug.Printf("Voice: speakerNameRetriever %s, snr.chatterID %s", objArr);
                if (str == null) {
                    str = data.numActiveSpeakers != 1 ? getContext().getString(R.string.speakers_speaking, Integer.valueOf(data.numActiveSpeakers)) : null;
                }
            } else if (data.state == VoiceChatInfo.VoiceChatState.Ringing) {
                this.voiceSpeakIndicatorLeft.setVisibility(0);
                this.voiceSpeakIndicatorRight.setVisibility(0);
                str = null;
                chatterIDUser2 = null;
            } else {
                this.voiceSpeakIndicatorLeft.setVisibility(4);
                this.voiceSpeakIndicatorRight.setVisibility(4);
                str = null;
                chatterIDUser2 = null;
            }
            if (str == null) {
                switch (m875x6f23bddb()[data.state.ordinal()]) {
                    case 1:
                        if (!data.localMicActive) {
                            str = getContext().getString(R.string.voice_status_tap_mic);
                            break;
                        } else {
                            str = getContext().getString(R.string.voice_status_speak_now);
                            break;
                        }
                    case 2:
                        str = getContext().getString(R.string.voice_status_connecting);
                        break;
                    case 3:
                        str = getContext().getString(R.string.voice_status_ringing);
                        break;
                }
            }
            String resolvedName = this.showActiveChatterName ? this.activeChatterNameRetriever != null ? this.activeChatterNameRetriever.getResolvedName() : null : null;
            if (resolvedName != null) {
                this.voiceStatusSmallText.setVisibility(0);
                this.voiceStatusText.setText(resolvedName);
                this.voiceStatusSmallText.setText(str);
                chatterIDUser = chatterIDUser2;
            } else {
                this.voiceStatusSmallText.setText((CharSequence) null);
                this.voiceStatusText.setText(str);
                this.voiceStatusSmallText.setVisibility(8);
                if (data.state != VoiceChatInfo.VoiceChatState.Active) {
                    chatterIDUser = chatterIDUser2;
                } else if (data.localMicActive) {
                    this.voiceStatusSmallText.setText(R.string.tap_for_audio_controls);
                    this.voiceStatusSmallText.setVisibility(0);
                    chatterIDUser = chatterIDUser2;
                } else {
                    chatterIDUser = chatterIDUser2;
                }
            }
        }
        if (!Objects.equal(this.activeChatterID, chatterIDUser)) {
            Debug.Printf("Voice: new activeChatterID %s", chatterIDUser);
            this.activeChatterID = chatterIDUser;
            if (chatterIDUser != null) {
                if (this.speakerNameRetriever == null || (!Objects.equal(this.speakerNameRetriever.chatterID, chatterIDUser))) {
                    if (this.speakerNameRetriever != null) {
                        this.speakerNameRetriever.dispose();
                    }
                    this.speakerNameRetriever = new ChatterNameRetriever(chatterIDUser, this.onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false);
                    this.speakerNameRetriever.subscribe();
                }
            } else if (this.speakerNameRetriever != null) {
                this.speakerNameRetriever.dispose();
                this.speakerNameRetriever = null;
            }
        }
        VoiceAudioProperties data2 = this.voiceAudioProperties.getData();
        if (data2 != null) {
            Drawable[] compoundDrawables = this.voiceBluetoothButton.getCompoundDrawables();
            switch (m874xc8b7c355()[data2.bluetoothState.ordinal()]) {
                case 1:
                    i = R.drawable.active_button_underline;
                    break;
                case 2:
                    i = R.drawable.yellow_button_underline;
                    break;
                default:
                    i = R.drawable.inactive_button_underline;
                    break;
            }
            this.voiceBluetoothButton.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], (Drawable) null, (Drawable) null, ContextCompat.getDrawable(getContext(), i));
            this.voiceLoudspeakerButton.setCompoundDrawablesWithIntrinsicBounds(this.voiceLoudspeakerButton.getCompoundDrawables()[0], (Drawable) null, (Drawable) null, ContextCompat.getDrawable(getContext(), data2.speakerphoneOn ? R.drawable.active_button_underline : R.drawable.inactive_button_underline));
            this.updatingAudioVolume = true;
            this.voiceSpeakerVolumeControl.setProgress(Math.round(data2.speakerVolume * this.voiceSpeakerVolumeControl.getMax()));
            this.updatingAudioVolume = false;
        }
    }

    public void disableMic() {
        VoiceChatInfo data;
        GridConnectionService serviceInstance;
        if (this.chatterID == null || (data = this.voiceChatInfo.getData()) == null || data.state != VoiceChatInfo.VoiceChatState.Active || (serviceInstance = GridConnectionService.getServiceInstance()) == null) {
            return;
        }
        serviceInstance.enableVoiceMic(false);
    }

    public void enableHover(final OnHoverListenerCompat onHoverListenerCompat) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.hoverEnabled = true;
            View.OnHoverListener onHoverListener = new View.OnHoverListener() { // from class: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8
                private final /* synthetic */ boolean $m$0(View view, MotionEvent motionEvent) {
                    return VoiceStatusView.m876lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407((OnHoverListenerCompat) onHoverListenerCompat, view, motionEvent);
                }

                @Override // android.view.View.OnHoverListener
                public final boolean onHover(View view, MotionEvent motionEvent) {
                    return $m$0(view, motionEvent);
                }
            };
            int applyDimension = (int) TypedValue.applyDimension(1, 14.0f, getResources().getDisplayMetrics());
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{R.attr.buttonShapeMoveControl});
            for (ImageButton imageButton : new ImageButton[]{this.voiceAnswerButton, this.voiceTerminateButton, this.voiceMicOnButton, this.voiceMicOffButton}) {
                imageButton.setOnHoverListener(onHoverListener);
                if (imageButton == this.voiceMicOnButton) {
                    imageButton.setBackground(getContext().getDrawable(R.drawable.fab_shape_move_control_green));
                } else {
                    imageButton.setBackground(obtainStyledAttributes.getDrawable(0));
                }
                imageButton.setPadding(applyDimension, applyDimension, applyDimension, applyDimension);
                imageButton.setAlpha(0.5f);
            }
            obtainStyledAttributes.recycle();
        }
    }

    public void hideBackground() {
        this.voiceStatusCardView.setCardBackgroundColor(0);
        this.voiceStatusCardView.setCardElevation(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065, reason: not valid java name */
    public /* synthetic */ void m879lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(ChatterNameRetriever chatterNameRetriever) {
        Debug.Printf("Voice: chatter name updated: %s", chatterNameRetriever.getResolvedName());
        updateVoiceState();
    }

    @OnClick({R.id.voice_loudspeaker_button})
    public void onLoudspeakerButton() {
        VoicePluginServiceConnection voicePluginServiceConnection;
        VoiceAudioProperties data = this.voiceAudioProperties.getData();
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null || data == null) {
            return;
        }
        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, data.speakerphoneOn ? VoiceAudioDevice.Default : VoiceAudioDevice.Loudspeaker));
    }

    @OnClick({R.id.voice_answer_button})
    public void onVoiceAnswerButton() {
        GridConnectionService serviceInstance;
        VoiceChatInfo data = this.voiceChatInfo.getData();
        if (this.onCallButtonListener != null && (this.chatterID == null || data == null || data.state == VoiceChatInfo.VoiceChatState.None)) {
            this.onCallButtonListener.onClick(this.voiceAnswerButton);
        }
        if (this.chatterID == null || (serviceInstance = GridConnectionService.getServiceInstance()) == null) {
            return;
        }
        serviceInstance.acceptVoiceCall(this.chatterID);
    }

    @OnClick({R.id.voice_bluetooth_button})
    public void onVoiceBluetoothButton() {
        VoicePluginServiceConnection voicePluginServiceConnection;
        VoiceAudioProperties data = this.voiceAudioProperties.getData();
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance == null || (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) == null || data == null) {
            return;
        }
        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, data.bluetoothState == VoiceBluetoothState.Active ? VoiceAudioDevice.Default : VoiceAudioDevice.Bluetooth));
    }

    @OnClick({R.id.voice_mic_off_button})
    public void onVoiceMicOffButton() {
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(true);
        }
    }

    @OnClick({R.id.voice_mic_on_button})
    public void onVoiceMicOnButton() {
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(false);
        }
    }

    @OnClick({R.id.voice_status_card_view})
    public void onVoiceStatusCardClick() {
        if (this.voiceStatusControls != null) {
            if (this.voiceStatusControls.getVisibility() == 0) {
                this.voiceStatusControls.setVisibility(8);
                return;
            }
            this.voiceStatusControls.setVisibility(0);
            this.voiceStatusControls.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.expand_vertically));
        }
    }

    @OnClick({R.id.voice_terminate_button})
    public void onVoiceTerminateButton() {
        GridConnectionService serviceInstance;
        if (this.chatterID == null || (serviceInstance = GridConnectionService.getServiceInstance()) == null) {
            return;
        }
        serviceInstance.terminateVoiceCall(this.chatterID);
    }

    public void setCanConnect(boolean z) {
        this.canConnect = z;
        updateVoiceState();
    }

    public void setChatterID(@Nullable ChatterID chatterID) {
        this.chatterID = chatterID;
        if (chatterID != null) {
            UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                this.voiceAudioProperties.subscribe(userManager.getVoiceAudioProperties(), SubscriptionSingleKey.Value);
                this.voiceChatInfo.subscribe(userManager.getVoiceChatInfo(), chatterID);
                if (this.activeChatterNameRetriever != null && (!Objects.equal(this.activeChatterNameRetriever.chatterID, chatterID))) {
                    this.activeChatterNameRetriever.dispose();
                    this.activeChatterNameRetriever = null;
                }
                if (this.showActiveChatterName) {
                    this.activeChatterNameRetriever = new ChatterNameRetriever(chatterID, this.onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false);
                    this.activeChatterNameRetriever.subscribe();
                }
            }
        } else {
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
            GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.enableVoiceMic(false);
            }
        }
        updateVoiceState();
    }

    public void setLightTheme() {
        this.voiceStatusText.setTextColor(-1);
        this.voiceStatusSmallText.setTextColor(-1);
        this.voiceAnswerButton.setImageResource(R.drawable.icon_material_voice_call);
        this.voiceMicOnButton.setImageResource(R.drawable.icon_material_mic);
        this.voiceMicOffButton.setImageResource(R.drawable.icon_material_mic_off);
        this.voiceTerminateButton.setImageResource(R.drawable.menu_close_light);
    }

    public void setOnCallButtonListener(View.OnClickListener onClickListener) {
        this.onCallButtonListener = onClickListener;
    }

    public void setShowActiveChatterName(boolean z) {
        this.showActiveChatterName = z;
        updateVoiceState();
    }

    public void setShowWhenInactive(boolean z) {
        this.showWhenInactive = z;
        updateVoiceState();
    }
}
