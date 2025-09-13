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

public class VoiceStatusView extends FrameLayout {

    /* renamed from: -com-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f606comlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues = null;

    /* renamed from: -com-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f607comlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues = null;
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
    /* access modifiers changed from: private */
    public boolean updatingAudioVolume;
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
    private final SeekBar.OnSeekBarChangeListener volumeChangeListener;

    /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceBluetoothStateSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m864getcomlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues() {
        if (f606comlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues != null) {
            return f606comlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues;
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
        f606comlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-model-VoiceChatInfo$VoiceChatStateSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m865getcomlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues() {
        if (f607comlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues != null) {
            return f607comlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues;
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
        f607comlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues = iArr;
        return iArr;
    }

    public VoiceStatusView(Context context) {
        super(context);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f594$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.1.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.1.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f598$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.2.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.2.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
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
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = ((float) i) / ((float) seekBar.getMax());
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null) {
                        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, (VoiceAudioDevice) null));
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f605$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f599$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.3.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.3.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f600$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.4.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.4.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
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
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = ((float) i) / ((float) seekBar.getMax());
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null) {
                        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, (VoiceAudioDevice) null));
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f595$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.10.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.10.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, this, true);
        initializeControls();
    }

    public VoiceStatusView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f601$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.5.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.5.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f602$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.6.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.6.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
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
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = ((float) i) / ((float) seekBar.getMax());
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null) {
                        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, (VoiceAudioDevice) null));
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f596$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.11.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.11.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, this, true);
        initializeControls();
    }

    @TargetApi(21)
    public VoiceStatusView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.voiceAudioProperties = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f603$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.7.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.7.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        this.voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f604$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.8.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.8.$m$0(java.lang.Object):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
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
        this.volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoicePluginServiceConnection voicePluginServiceConnection;
                if (z && (!VoiceStatusView.this.updatingAudioVolume)) {
                    float max = ((float) i) / ((float) seekBar.getMax());
                    GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
                    if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null) {
                        voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(max, true, (VoiceAudioDevice) null));
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.onActiveSpeakerNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f597$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.12.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.voice.-$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8.12.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            public final void brokenMethod(
        // TODO: implement method
    }
        };
        LayoutInflater.from(context).inflate(R.layout.voice_status, this, true);
        initializeControls();
    }

    private void initializeControls() {
        ButterKnife.bind((View) this);
        updateVoiceState();
        this.voiceSpeakerVolumeControl.setOnSeekBarChangeListener(this.volumeChangeListener);
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407  reason: not valid java name */
    static /* synthetic */ boolean m866lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407(OnHoverListenerCompat onHoverListenerCompat, View view, MotionEvent motionEvent) {
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

    /* access modifiers changed from: private */
    /* renamed from: onVoiceAudioProperties */
    public void m867com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref0(VoiceAudioProperties voiceAudioProperties2) {
        VoiceBluetoothState voiceBluetoothState = null;
        Object[] objArr = new Object[1];
        if (voiceAudioProperties2 != null) {
            voiceBluetoothState = voiceAudioProperties2.bluetoothState;
        }
        objArr[0] = voiceBluetoothState;
        Debug.Printf("Voice: voice audio properties updated, bluetooth state %s", objArr);
        updateVoiceState();
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    public void m868com_lumiyaviewer_lumiya_ui_voice_VoiceStatusViewmthref1(VoiceChatInfo voiceChatInfo2) {
        updateVoiceState();
    }

    private void updateVoiceState() {
        ChatterID.ChatterIDUser chatterIDUser;
        int i;
        String str;
        ChatterID.ChatterIDUser chatterIDUser2;
        int i2 = 4;
        VoiceChatInfo data = this.voiceChatInfo.getData();
        Debug.Printf("VoiceStatusView: voice state %s", data);
        if (data == null || !(!data.state.equals(VoiceChatInfo.VoiceChatState.None))) {
            this.voiceStatusControls.setVisibility(8);
            if (!this.showWhenInactive) {
                setVisibility(8);
            } else {
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
                ImageButton imageButton = this.voiceAnswerButton;
                if (this.canConnect) {
                    i2 = 0;
                }
                imageButton.setVisibility(i2);
            }
            chatterIDUser = null;
        } else {
            setVisibility(0);
            this.voiceTerminateButton.setVisibility(0);
            boolean z = data.state == VoiceChatInfo.VoiceChatState.Active;
            this.voiceMicOnButton.setVisibility((!z || !data.localMicActive) ? 4 : 0);
            this.voiceMicOffButton.setVisibility((!z || !(data.localMicActive ^ true)) ? 4 : 0);
            this.voiceAnswerButton.setVisibility(data.state == VoiceChatInfo.VoiceChatState.Ringing ? 0 : 4);
            if (data.state == VoiceChatInfo.VoiceChatState.Active && data.numActiveSpeakers != 0) {
                if (!(this.voiceSpeakIndicatorLeft.getVisibility() == 0 && this.voiceSpeakIndicatorRight.getVisibility() == 0)) {
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
                    str = data.numActiveSpeakers != 1 ? getContext().getString(R.string.speakers_speaking, new Object[]{Integer.valueOf(data.numActiveSpeakers)}) : null;
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
                switch (m865getcomlumiyaviewerlumiyavoicecommonmodelVoiceChatInfo$VoiceChatStateSwitchesValues()[data.state.ordinal()]) {
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
            switch (m864getcomlumiyaviewerlumiyavoicecommonmodelVoiceBluetoothStateSwitchesValues()[data2.bluetoothState.ordinal()]) {
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
            this.voiceSpeakerVolumeControl.setProgress(Math.round(data2.speakerVolume * ((float) this.voiceSpeakerVolumeControl.getMax())));
            this.updatingAudioVolume = false;
        }
    }

    public void disableMic() {
        VoiceChatInfo data;
        GridConnectionService serviceInstance;
        if (this.chatterID != null && (data = this.voiceChatInfo.getData()) != null && data.state == VoiceChatInfo.VoiceChatState.Active && (serviceInstance = GridConnectionService.getServiceInstance()) != null) {
            serviceInstance.enableVoiceMic(false);
        }
    }

    public void enableHover(OnHoverListenerCompat onHoverListenerCompat) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.hoverEnabled = true;
            $Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8 r2 = new $Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8(onHoverListenerCompat);
            int applyDimension = (int) TypedValue.applyDimension(1, 14.0f, getResources().getDisplayMetrics());
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{R.attr.buttonShapeMoveControl});
            for (ImageButton imageButton : new ImageButton[]{this.voiceAnswerButton, this.voiceTerminateButton, this.voiceMicOnButton, this.voiceMicOffButton}) {
                imageButton.setOnHoverListener(r2);
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

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065  reason: not valid java name */
    public /* synthetic */ void m869lambda$com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(ChatterNameRetriever chatterNameRetriever) {
        Debug.Printf("Voice: chatter name updated: %s", chatterNameRetriever.getResolvedName());
        updateVoiceState();
    }

    @OnClick({2131755739})
    public void onLoudspeakerButton() {
        VoicePluginServiceConnection voicePluginServiceConnection;
        VoiceAudioProperties data = this.voiceAudioProperties.getData();
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null && data != null) {
            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, data.speakerphoneOn ? VoiceAudioDevice.Default : VoiceAudioDevice.Loudspeaker));
        }
    }

    @OnClick({2131755731})
    public void onVoiceAnswerButton() {
        GridConnectionService serviceInstance;
        VoiceChatInfo data = this.voiceChatInfo.getData();
        if (this.onCallButtonListener != null && (this.chatterID == null || data == null || data.state == VoiceChatInfo.VoiceChatState.None)) {
            this.onCallButtonListener.onClick(this.voiceAnswerButton);
        }
        if (this.chatterID != null && (serviceInstance = GridConnectionService.getServiceInstance()) != null) {
            serviceInstance.acceptVoiceCall(this.chatterID);
        }
    }

    @OnClick({2131755740})
    public void onVoiceBluetoothButton() {
        VoicePluginServiceConnection voicePluginServiceConnection;
        VoiceAudioProperties data = this.voiceAudioProperties.getData();
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null && (voicePluginServiceConnection = serviceInstance.getVoicePluginServiceConnection()) != null && data != null) {
            voicePluginServiceConnection.setVoiceAudioProperties(new VoiceSetAudioProperties(0.0f, false, data.bluetoothState == VoiceBluetoothState.Active ? VoiceAudioDevice.Default : VoiceAudioDevice.Bluetooth));
        }
    }

    @OnClick({2131755730})
    public void onVoiceMicOffButton() {
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(true);
        }
    }

    @OnClick({2131755729})
    public void onVoiceMicOnButton() {
        GridConnectionService serviceInstance = GridConnectionService.getServiceInstance();
        if (serviceInstance != null) {
            serviceInstance.enableVoiceMic(false);
        }
    }

    @OnClick({2131755728})
    public void onVoiceStatusCardClick() {
        if (this.voiceStatusControls == null) {
            return;
        }
        if (this.voiceStatusControls.getVisibility() != 0) {
            this.voiceStatusControls.setVisibility(0);
            this.voiceStatusControls.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.expand_vertically));
            return;
        }
        this.voiceStatusControls.setVisibility(8);
    }

    @OnClick({2131755736})
    public void onVoiceTerminateButton() {
        GridConnectionService serviceInstance;
        if (this.chatterID != null && (serviceInstance = GridConnectionService.getServiceInstance()) != null) {
            serviceInstance.terminateVoiceCall(this.chatterID);
        }
    }

    public void setCanConnect(boolean z) {
        this.canConnect = z;
        updateVoiceState();
    }

    public void setChatterID(@Nullable ChatterID chatterID2) {
        this.chatterID = chatterID2;
        if (chatterID2 != null) {
            UserManager userManager = chatterID2.getUserManager();
            if (userManager != null) {
                this.voiceAudioProperties.subscribe(userManager.getVoiceAudioProperties(), SubscriptionSingleKey.Value);
                this.voiceChatInfo.subscribe(userManager.getVoiceChatInfo(), chatterID2);
                if (this.activeChatterNameRetriever != null && (!Objects.equal(this.activeChatterNameRetriever.chatterID, chatterID2))) {
                    this.activeChatterNameRetriever.dispose();
                    this.activeChatterNameRetriever = null;
                }
                if (this.showActiveChatterName) {
                    this.activeChatterNameRetriever = new ChatterNameRetriever(chatterID2, this.onActiveSpeakerNameUpdated, UIThreadExecutor.getInstance(), false);
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
