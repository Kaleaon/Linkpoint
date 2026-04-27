// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.voice;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
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
import butterknife.ButterKnife;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
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

public class VoiceStatusView extends FrameLayout
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues[];
    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues[];
    private ChatterID activeChatterID;
    private ChatterNameRetriever activeChatterNameRetriever;
    private boolean canConnect;
    private ChatterID chatterID;
    private boolean hoverEnabled;
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onActiveSpeakerNameUpdated;
    private android.view.View.OnClickListener onCallButtonListener;
    private boolean showActiveChatterName;
    private boolean showWhenInactive;
    private ChatterNameRetriever speakerNameRetriever;
    private boolean updatingAudioVolume;
    ImageButton voiceAnswerButton;
    private final SubscriptionData voiceAudioProperties;
    Button voiceBluetoothButton;
    private final SubscriptionData voiceChatInfo;
    Button voiceLoudspeakerButton;
    ImageButton voiceMicOffButton;
    ImageButton voiceMicOnButton;
    ImageView voiceSpeakIndicatorLeft;
    ImageView voiceSpeakIndicatorRight;
    SeekBar voiceSpeakerVolumeControl;
    CardView voiceStatusCardView;
    ViewGroup voiceStatusControls;
    TextView voiceStatusSmallText;
    TextView voiceStatusText;
    ImageButton voiceTerminateButton;
    private final android.widget.SeekBar.OnSeekBarChangeListener volumeChangeListener;

    static boolean _2D_get0(VoiceStatusView voicestatusview)
    {
        return voicestatusview.updatingAudioVolume;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues;
        }
        int ai[] = new int[VoiceBluetoothState.values().length];
        try
        {
            ai[VoiceBluetoothState.Active.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[VoiceBluetoothState.Connected.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[VoiceBluetoothState.Connecting.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[VoiceBluetoothState.Disconnected.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[VoiceBluetoothState.Error.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues = ai;
        return ai;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Connecting.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Ringing.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues = ai;
        return ai;
    }

    public VoiceStatusView(Context context)
    {
        super(context);
        voiceAudioProperties = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls1(this));
        voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls2(this));
        speakerNameRetriever = null;
        activeChatterNameRetriever = null;
        activeChatterID = null;
        showActiveChatterName = false;
        showWhenInactive = false;
        canConnect = false;
        hoverEnabled = false;
        onCallButtonListener = null;
        updatingAudioVolume = false;
        volumeChangeListener = new android.widget.SeekBar.OnSeekBarChangeListener() {

            final VoiceStatusView this$0;

            public void onProgressChanged(SeekBar seekbar, int i, boolean flag)
            {
                if (flag && VoiceStatusView._2D_get0(VoiceStatusView.this) ^ true)
                {
                    float f = (float)i / (float)seekbar.getMax();
                    seekbar = GridConnectionService.getServiceInstance();
                    if (seekbar != null)
                    {
                        seekbar = seekbar.getVoicePluginServiceConnection();
                        if (seekbar != null)
                        {
                            seekbar.setVoiceAudioProperties(new VoiceSetAudioProperties(f, true, null));
                        }
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekbar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekbar)
            {
            }

            
            {
                this$0 = VoiceStatusView.this;
                super();
            }
        };
        onActiveSpeakerNameUpdated = new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls9(this);
        LayoutInflater.from(context).inflate(0x7f0400bb, this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        voiceAudioProperties = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls3(this));
        voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls4(this));
        speakerNameRetriever = null;
        activeChatterNameRetriever = null;
        activeChatterID = null;
        showActiveChatterName = false;
        showWhenInactive = false;
        canConnect = false;
        hoverEnabled = false;
        onCallButtonListener = null;
        updatingAudioVolume = false;
        volumeChangeListener = new _cls1();
        onActiveSpeakerNameUpdated = new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls10(this);
        LayoutInflater.from(context).inflate(0x7f0400bb, this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        voiceAudioProperties = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls5(this));
        voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls6(this));
        speakerNameRetriever = null;
        activeChatterNameRetriever = null;
        activeChatterID = null;
        showActiveChatterName = false;
        showWhenInactive = false;
        canConnect = false;
        hoverEnabled = false;
        onCallButtonListener = null;
        updatingAudioVolume = false;
        volumeChangeListener = new _cls1();
        onActiveSpeakerNameUpdated = new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls11(this);
        LayoutInflater.from(context).inflate(0x7f0400bb, this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        voiceAudioProperties = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls7(this));
        voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls8(this));
        speakerNameRetriever = null;
        activeChatterNameRetriever = null;
        activeChatterID = null;
        showActiveChatterName = false;
        showWhenInactive = false;
        canConnect = false;
        hoverEnabled = false;
        onCallButtonListener = null;
        updatingAudioVolume = false;
        volumeChangeListener = new _cls1();
        onActiveSpeakerNameUpdated = new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8._cls12(this);
        LayoutInflater.from(context).inflate(0x7f0400bb, this, true);
        initializeControls();
    }

    private void initializeControls()
    {
        ButterKnife.bind(this);
        updateVoiceState();
        voiceSpeakerVolumeControl.setOnSeekBarChangeListener(volumeChangeListener);
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407(OnHoverListenerCompat onhoverlistenercompat, View view, MotionEvent motionevent)
    {
        if (!(view instanceof ImageButton)) goto _L2; else goto _L1
_L1:
        motionevent.getActionMasked();
        JVM INSTR tableswitch 9 10: default 32
    //                   9 34
    //                   10 45;
           goto _L2 _L3 _L4
_L2:
        return false;
_L3:
        onhoverlistenercompat.onHoverEnter(view);
        continue; /* Loop/switch isn't completed */
_L4:
        onhoverlistenercompat.onHoverExit(view);
        if (true) goto _L2; else goto _L5
_L5:
    }

    private void onVoiceAudioProperties(VoiceAudioProperties voiceaudioproperties)
    {
        VoiceBluetoothState voicebluetoothstate = null;
        if (voiceaudioproperties != null)
        {
            voicebluetoothstate = voiceaudioproperties.bluetoothState;
        }
        Debug.Printf("Voice: voice audio properties updated, bluetooth state %s", new Object[] {
            voicebluetoothstate
        });
        updateVoiceState();
    }

    private void onVoiceChatInfo(VoiceChatInfo voicechatinfo)
    {
        updateVoiceState();
    }

    private void updateVoiceState()
    {
        Object obj3;
        int i;
        i = 4;
        obj3 = (VoiceChatInfo)voiceChatInfo.getData();
        Debug.Printf("VoiceStatusView: voice state %s", new Object[] {
            obj3
        });
        if (obj3 == null || !(((VoiceChatInfo) (obj3)).state.equals(com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None) ^ true)) goto _L2; else goto _L1
_L1:
        setVisibility(0);
        voiceTerminateButton.setVisibility(0);
        ImageButton imagebutton;
        int j;
        if (((VoiceChatInfo) (obj3)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        imagebutton = voiceMicOnButton;
        if (i != 0 && ((VoiceChatInfo) (obj3)).localMicActive)
        {
            j = 0;
        } else
        {
            j = 4;
        }
        imagebutton.setVisibility(j);
        imagebutton = voiceMicOffButton;
        if (i != 0 && ((VoiceChatInfo) (obj3)).localMicActive ^ true)
        {
            i = 0;
        } else
        {
            i = 4;
        }
        imagebutton.setVisibility(i);
        imagebutton = voiceAnswerButton;
        if (((VoiceChatInfo) (obj3)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Ringing)
        {
            i = 0;
        } else
        {
            i = 4;
        }
        imagebutton.setVisibility(i);
        if (((VoiceChatInfo) (obj3)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active && ((VoiceChatInfo) (obj3)).numActiveSpeakers != 0)
        {
            if (voiceSpeakIndicatorLeft.getVisibility() != 0 || voiceSpeakIndicatorRight.getVisibility() != 0)
            {
                voiceSpeakIndicatorLeft.setVisibility(0);
                voiceSpeakIndicatorRight.setVisibility(0);
                android.graphics.drawable.Drawable drawable = voiceSpeakIndicatorLeft.getDrawable();
                if (drawable instanceof AnimationDrawable)
                {
                    ((AnimationDrawable)drawable).start();
                }
                drawable = voiceSpeakIndicatorRight.getDrawable();
                if (drawable instanceof AnimationDrawable)
                {
                    ((AnimationDrawable)drawable).start();
                }
            }
            if (((VoiceChatInfo) (obj3)).numActiveSpeakers == 1 && ((VoiceChatInfo) (obj3)).activeSpeakerID != null && chatterID != null)
            {
                Object obj = ChatterID.getUserChatterID(chatterID.agentUUID, ((VoiceChatInfo) (obj3)).activeSpeakerID);
                float f;
                Object obj1;
                Object obj2;
                ChatterNameRetriever chatternameretriever;
                if (speakerNameRetriever != null)
                {
                    if (Objects.equal(obj, speakerNameRetriever.chatterID))
                    {
                        obj1 = speakerNameRetriever.getResolvedName();
                    } else
                    {
                        obj1 = null;
                    }
                } else
                {
                    obj1 = null;
                }
            } else
            {
                obj1 = null;
                obj = null;
            }
            Debug.Printf("Voice: numActiveSpeakers %d, speakerName %s, activeChatterID %s (view chatterID %s)", new Object[] {
                Integer.valueOf(((VoiceChatInfo) (obj3)).numActiveSpeakers), obj1, obj, chatterID
            });
            chatternameretriever = speakerNameRetriever;
            if (speakerNameRetriever != null)
            {
                obj2 = speakerNameRetriever.chatterID;
            } else
            {
                obj2 = null;
            }
            Debug.Printf("Voice: speakerNameRetriever %s, snr.chatterID %s", new Object[] {
                chatternameretriever, obj2
            });
            if (obj1 == null)
            {
                if (((VoiceChatInfo) (obj3)).numActiveSpeakers != 1)
                {
                    obj1 = getContext().getString(0x7f090313, new Object[] {
                        Integer.valueOf(((VoiceChatInfo) (obj3)).numActiveSpeakers)
                    });
                } else
                {
                    obj1 = null;
                }
            }
        } else
        if (((VoiceChatInfo) (obj3)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Ringing)
        {
            voiceSpeakIndicatorLeft.setVisibility(0);
            voiceSpeakIndicatorRight.setVisibility(0);
            obj1 = null;
            obj = null;
        } else
        {
            voiceSpeakIndicatorLeft.setVisibility(4);
            voiceSpeakIndicatorRight.setVisibility(4);
            obj1 = null;
            obj = null;
        }
        obj2 = obj1;
        if (obj1 != null) goto _L4; else goto _L3
_L3:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceChatInfo$VoiceChatStateSwitchesValues()[((VoiceChatInfo) (obj3)).state.ordinal()];
        JVM INSTR tableswitch 1 3: default 456
    //                   1 948
    //                   2 933
    //                   3 918;
           goto _L5 _L6 _L7 _L8
_L5:
        obj2 = obj1;
_L4:
        if (showActiveChatterName)
        {
            if (activeChatterNameRetriever != null)
            {
                obj1 = activeChatterNameRetriever.getResolvedName();
            } else
            {
                obj1 = null;
            }
        } else
        {
            obj1 = null;
        }
        if (obj1 != null)
        {
            voiceStatusSmallText.setVisibility(0);
            voiceStatusText.setText(((CharSequence) (obj1)));
            voiceStatusSmallText.setText(((CharSequence) (obj2)));
        } else
        {
            voiceStatusSmallText.setText(null);
            voiceStatusText.setText(((CharSequence) (obj2)));
            voiceStatusSmallText.setVisibility(8);
            if (((VoiceChatInfo) (obj3)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active && ((VoiceChatInfo) (obj3)).localMicActive)
            {
                voiceStatusSmallText.setText(0x7f090339);
                voiceStatusSmallText.setVisibility(0);
            }
        }
_L14:
        if (!Objects.equal(activeChatterID, obj))
        {
            Debug.Printf("Voice: new activeChatterID %s", new Object[] {
                obj
            });
            activeChatterID = ((ChatterID) (obj));
            if (obj != null)
            {
                if (speakerNameRetriever == null || Objects.equal(speakerNameRetriever.chatterID, obj) ^ true)
                {
                    if (speakerNameRetriever != null)
                    {
                        speakerNameRetriever.dispose();
                    }
                    speakerNameRetriever = new ChatterNameRetriever(((ChatterID) (obj)), onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false);
                    speakerNameRetriever.subscribe();
                }
            } else
            if (speakerNameRetriever != null)
            {
                speakerNameRetriever.dispose();
                speakerNameRetriever = null;
            }
        }
        obj = (VoiceAudioProperties)voiceAudioProperties.getData();
        if (obj == null) goto _L10; else goto _L9
_L9:
        obj1 = voiceBluetoothButton.getCompoundDrawables();
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_model_2D_VoiceBluetoothStateSwitchesValues()[((VoiceAudioProperties) (obj)).bluetoothState.ordinal()];
        JVM INSTR tableswitch 1 2: default 664
    //                   1 1225
    //                   2 1233;
           goto _L11 _L12 _L13
_L13:
        break MISSING_BLOCK_LABEL_1233;
_L11:
        i = 0x7f0200bc;
_L15:
        voiceBluetoothButton.setCompoundDrawablesWithIntrinsicBounds(obj1[0], null, null, ContextCompat.getDrawable(getContext(), i));
        obj2 = voiceLoudspeakerButton.getCompoundDrawables();
        obj1 = voiceLoudspeakerButton;
        obj2 = obj2[0];
        obj3 = getContext();
        if (((VoiceAudioProperties) (obj)).speakerphoneOn)
        {
            i = 0x7f020055;
        } else
        {
            i = 0x7f0200bc;
        }
        ((Button) (obj1)).setCompoundDrawablesWithIntrinsicBounds(((android.graphics.drawable.Drawable) (obj2)), null, null, ContextCompat.getDrawable(((Context) (obj3)), i));
        updatingAudioVolume = true;
        obj1 = voiceSpeakerVolumeControl;
        f = voiceSpeakerVolumeControl.getMax();
        ((SeekBar) (obj1)).setProgress(Math.round(((VoiceAudioProperties) (obj)).speakerVolume * f));
        updatingAudioVolume = false;
_L10:
        return;
_L8:
        obj2 = getContext().getString(0x7f090381);
          goto _L4
_L7:
        obj2 = getContext().getString(0x7f090380);
          goto _L4
_L6:
        if (((VoiceChatInfo) (obj3)).localMicActive)
        {
            obj2 = getContext().getString(0x7f090382);
        } else
        {
            obj2 = getContext().getString(0x7f090383);
        }
          goto _L4
_L2:
        voiceStatusControls.setVisibility(8);
        if (!showWhenInactive)
        {
            setVisibility(8);
        } else
        {
            setVisibility(0);
            voiceStatusText.setText(0x7f09037d);
            if (canConnect)
            {
                voiceStatusSmallText.setText(0x7f090384);
                voiceStatusSmallText.setVisibility(0);
            } else
            {
                voiceStatusSmallText.setVisibility(8);
            }
            voiceSpeakIndicatorLeft.setVisibility(4);
            voiceSpeakIndicatorRight.setVisibility(4);
            voiceTerminateButton.setVisibility(4);
            voiceMicOnButton.setVisibility(4);
            voiceMicOffButton.setVisibility(4);
            obj = voiceAnswerButton;
            if (canConnect)
            {
                i = 0;
            }
            ((ImageButton) (obj)).setVisibility(i);
        }
        obj = null;
          goto _L14
_L12:
        i = 0x7f020055;
          goto _L15
        i = 0x7f020166;
          goto _L15
    }

    void _2D_com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_2D_mthref_2D_0(VoiceAudioProperties voiceaudioproperties)
    {
        onVoiceAudioProperties(voiceaudioproperties);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_2D_mthref_2D_1(VoiceChatInfo voicechatinfo)
    {
        onVoiceChatInfo(voicechatinfo);
    }

    public void disableMic()
    {
        if (chatterID != null)
        {
            VoiceChatInfo voicechatinfo = (VoiceChatInfo)voiceChatInfo.getData();
            if (voicechatinfo != null && voicechatinfo.state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active)
            {
                GridConnectionService gridconnectionservice = GridConnectionService.getServiceInstance();
                if (gridconnectionservice != null)
                {
                    gridconnectionservice.enableVoiceMic(false);
                }
            }
        }
    }

    public void enableHover(OnHoverListenerCompat onhoverlistenercompat)
    {
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            hoverEnabled = true;
            onhoverlistenercompat = new _2D_.Lambda.LRu9qjGWbEJmZF4NfrRGigLGXl8(onhoverlistenercompat);
            int j = (int)TypedValue.applyDimension(1, 14F, getResources().getDisplayMetrics());
            TypedArray typedarray = getContext().obtainStyledAttributes(new int[] {
                0x7f010059
            });
            ImageButton aimagebutton[] = new ImageButton[4];
            aimagebutton[0] = voiceAnswerButton;
            aimagebutton[1] = voiceTerminateButton;
            aimagebutton[2] = voiceMicOnButton;
            aimagebutton[3] = voiceMicOffButton;
            int k = aimagebutton.length;
            int i = 0;
            while (i < k) 
            {
                ImageButton imagebutton = aimagebutton[i];
                imagebutton.setOnHoverListener(onhoverlistenercompat);
                if (imagebutton == voiceMicOnButton)
                {
                    imagebutton.setBackground(getContext().getDrawable(0x7f02006c));
                } else
                {
                    imagebutton.setBackground(typedarray.getDrawable(0));
                }
                imagebutton.setPadding(j, j, j, j);
                imagebutton.setAlpha(0.5F);
                i++;
            }
            typedarray.recycle();
        }
    }

    public void hideBackground()
    {
        voiceStatusCardView.setCardBackgroundColor(0);
        voiceStatusCardView.setCardElevation(0.0F);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(ChatterNameRetriever chatternameretriever)
    {
        Debug.Printf("Voice: chatter name updated: %s", new Object[] {
            chatternameretriever.getResolvedName()
        });
        updateVoiceState();
    }

    public void onLoudspeakerButton()
    {
        Object obj = (VoiceAudioProperties)voiceAudioProperties.getData();
        Object obj1 = GridConnectionService.getServiceInstance();
        if (obj1 != null)
        {
            obj1 = ((GridConnectionService) (obj1)).getVoicePluginServiceConnection();
            if (obj1 != null && obj != null)
            {
                if (((VoiceAudioProperties) (obj)).speakerphoneOn)
                {
                    obj = VoiceAudioDevice.Default;
                } else
                {
                    obj = VoiceAudioDevice.Loudspeaker;
                }
                ((VoicePluginServiceConnection) (obj1)).setVoiceAudioProperties(new VoiceSetAudioProperties(0.0F, false, ((VoiceAudioDevice) (obj))));
            }
        }
    }

    public void onVoiceAnswerButton()
    {
        Object obj;
        obj = (VoiceChatInfo)voiceChatInfo.getData();
        break MISSING_BLOCK_LABEL_11;
        if (onCallButtonListener != null && (chatterID == null || obj == null || ((VoiceChatInfo) (obj)).state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None))
        {
            onCallButtonListener.onClick(voiceAnswerButton);
        }
        if (chatterID != null)
        {
            obj = GridConnectionService.getServiceInstance();
            if (obj != null)
            {
                ((GridConnectionService) (obj)).acceptVoiceCall(chatterID);
            }
        }
        return;
    }

    public void onVoiceBluetoothButton()
    {
        Object obj = (VoiceAudioProperties)voiceAudioProperties.getData();
        Object obj1 = GridConnectionService.getServiceInstance();
        if (obj1 != null)
        {
            obj1 = ((GridConnectionService) (obj1)).getVoicePluginServiceConnection();
            if (obj1 != null && obj != null)
            {
                if (((VoiceAudioProperties) (obj)).bluetoothState == VoiceBluetoothState.Active)
                {
                    obj = VoiceAudioDevice.Default;
                } else
                {
                    obj = VoiceAudioDevice.Bluetooth;
                }
                ((VoicePluginServiceConnection) (obj1)).setVoiceAudioProperties(new VoiceSetAudioProperties(0.0F, false, ((VoiceAudioDevice) (obj))));
            }
        }
    }

    public void onVoiceMicOffButton()
    {
        GridConnectionService gridconnectionservice = GridConnectionService.getServiceInstance();
        if (gridconnectionservice != null)
        {
            gridconnectionservice.enableVoiceMic(true);
        }
    }

    public void onVoiceMicOnButton()
    {
        GridConnectionService gridconnectionservice = GridConnectionService.getServiceInstance();
        if (gridconnectionservice != null)
        {
            gridconnectionservice.enableVoiceMic(false);
        }
    }

    public void onVoiceStatusCardClick()
    {
label0:
        {
            if (voiceStatusControls != null)
            {
                if (voiceStatusControls.getVisibility() == 0)
                {
                    break label0;
                }
                voiceStatusControls.setVisibility(0);
                android.view.animation.Animation animation = AnimationUtils.loadAnimation(getContext(), 0x7f05000e);
                voiceStatusControls.startAnimation(animation);
            }
            return;
        }
        voiceStatusControls.setVisibility(8);
    }

    public void onVoiceTerminateButton()
    {
        if (chatterID != null)
        {
            GridConnectionService gridconnectionservice = GridConnectionService.getServiceInstance();
            if (gridconnectionservice != null)
            {
                gridconnectionservice.terminateVoiceCall(chatterID);
            }
        }
    }

    public void setCanConnect(boolean flag)
    {
        canConnect = flag;
        updateVoiceState();
    }

    public void setChatterID(ChatterID chatterid)
    {
        chatterID = chatterid;
        if (chatterid == null) goto _L2; else goto _L1
_L1:
        UserManager usermanager = chatterid.getUserManager();
        if (usermanager != null)
        {
            voiceAudioProperties.subscribe(usermanager.getVoiceAudioProperties(), SubscriptionSingleKey.Value);
            voiceChatInfo.subscribe(usermanager.getVoiceChatInfo(), chatterid);
            if (activeChatterNameRetriever != null && Objects.equal(activeChatterNameRetriever.chatterID, chatterid) ^ true)
            {
                activeChatterNameRetriever.dispose();
                activeChatterNameRetriever = null;
            }
            if (showActiveChatterName)
            {
                activeChatterNameRetriever = new ChatterNameRetriever(chatterid, onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false);
                activeChatterNameRetriever.subscribe();
            }
        }
_L4:
        updateVoiceState();
        return;
_L2:
        voiceAudioProperties.unsubscribe();
        voiceChatInfo.unsubscribe();
        if (speakerNameRetriever != null)
        {
            speakerNameRetriever.dispose();
            speakerNameRetriever = null;
        }
        if (activeChatterNameRetriever != null)
        {
            activeChatterNameRetriever.dispose();
            activeChatterNameRetriever = null;
        }
        chatterid = GridConnectionService.getServiceInstance();
        if (chatterid != null)
        {
            chatterid.enableVoiceMic(false);
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void setLightTheme()
    {
        voiceStatusText.setTextColor(-1);
        voiceStatusSmallText.setTextColor(-1);
        voiceAnswerButton.setImageResource(0x7f0200ba);
        voiceMicOnButton.setImageResource(0x7f0200ad);
        voiceMicOffButton.setImageResource(0x7f0200af);
        voiceTerminateButton.setImageResource(0x7f0200e8);
    }

    public void setOnCallButtonListener(android.view.View.OnClickListener onclicklistener)
    {
        onCallButtonListener = onclicklistener;
    }

    public void setShowActiveChatterName(boolean flag)
    {
        showActiveChatterName = flag;
        updateVoiceState();
    }

    public void setShowWhenInactive(boolean flag)
    {
        showWhenInactive = flag;
        updateVoiceState();
    }
}
