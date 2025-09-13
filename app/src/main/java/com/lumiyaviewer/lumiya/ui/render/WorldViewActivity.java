package com.lumiyaviewer.lumiya.ui.render;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ScriptDialogHandler;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectPayDialog;
import com.lumiyaviewer.lumiya.ui.objects.TouchableObjectsFragment;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldViewActivity extends DetailsActivity implements View.OnTouchListener, ThemeMapper, ScriptDialogHandler, UnreadNotificationManager.NotifyCapture {
    private static final long BUTTONS_FADE_TIMEOUT_MILLIS = 7500;
    private static final String FROM_NOTIFICATION_TAG = "fromNotification";
    private static final long OBJECT_DESELECT_TIMEOUT_MILLIS = 6000;
    private static final int PERMISSION_AUDIO_REQUEST_CODE = 100;
    private static final float TURNING_SPEED = 50.0f;
    /* access modifiers changed from: private */
    public final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f535$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.3.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.3.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    private boolean arrowsToTurn = false;
    /* access modifiers changed from: private */
    @Nullable
    public SLAvatarControl avatarControl;
    @BindView(2131755576)
    ImageView avatarIconView;
    @BindView(2131755762)
    ImageButton buttonCamOff;
    @BindView(2131755761)
    ImageButton buttonCamOn;
    @BindView(2131755757)
    ImageButton buttonFlyDownward;
    @BindView(2131755756)
    ImageButton buttonFlyUpward;
    @BindView(2131755763)
    Button buttonHUD;
    @BindView(2131755262)
    ImageButton buttonMoveBackward;
    @BindView(2131755260)
    ImageButton buttonMoveForward;
    @BindView(2131755264)
    ImageButton buttonStandUp;
    @BindView(2131755758)
    ImageButton buttonStopFlying;
    @BindView(2131755261)
    ImageButton buttonTurnLeft;
    @BindView(2131755263)
    ImageButton buttonTurnRight;
    /* access modifiers changed from: private */
    public ValueAnimator buttonsFadeAnimator = null;
    /* access modifiers changed from: private */
    public final Runnable buttonsFadeTask = new Runnable() {
        public void run() {
            boolean unused = WorldViewActivity.this.buttonsFadeTimerStarted = false;
            if (!WorldViewActivity.this.detailsVisible() && (!WorldViewActivity.this.isDragging) && WorldViewActivity.this.agentCircuit.hasData()) {
                VoiceChatInfo voiceChatInfo = (VoiceChatInfo) WorldViewActivity.this.voiceChatInfo.getData();
                if (!((voiceChatInfo == null || voiceChatInfo.state != VoiceChatInfo.VoiceChatState.Active) ? false : voiceChatInfo.localMicActive)) {
                    long r2 = (WorldViewActivity.this.lastActivityTime + WorldViewActivity.BUTTONS_FADE_TIMEOUT_MILLIS) - SystemClock.uptimeMillis();
                    Debug.Printf("ButtonsFade: remaining %d", Long.valueOf(r2));
                    if (r2 <= 0) {
                        WorldViewActivity.this.startFadingButtons();
                        return;
                    }
                    boolean unused2 = WorldViewActivity.this.buttonsFadeTimerStarted = true;
                    WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.buttonsFadeTask, r2);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean buttonsFadeTimerStarted = false;
    private final Runnable buttonsRestoreTask = new Runnable() {
        public void run() {
            if (Build.VERSION.SDK_INT >= 11) {
                if (WorldViewActivity.this.buttonsFadeAnimator != null) {
                    WorldViewActivity.this.buttonsFadeAnimator.cancel();
                }
                WorldViewActivity.this.insetsBackground.setAlpha(1.0f);
            }
        }
    };
    private boolean camButtonEnabled = false;
    private final Object chatEventHandler = new Object() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity$1_50098  reason: not valid java name */
        public /* synthetic */ void m846lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity$1_50098(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            WorldViewActivity.this.handleChatEvent(chatMessageEvent);
        }

        @Subscribe
        public void onChatMessage(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            WorldViewActivity.this.mHandler.post(new Runnable(this, chatMessageEvent) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f532$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f533$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.11.$m$0():void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.11.$m$0():void, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
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
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
        }
    };
    private boolean chatOver3D = false;
    @BindView(2131755759)
    LinearLayout chatsOverlayLayout;
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f538$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.6.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.6.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    @BindView(2131755284)
    View detailsContainer;
    /* access modifiers changed from: private */
    public int displayedHUDid = 0;
    @BindView(2131755748)
    View dragPointer;
    @BindView(2131755747)
    ViewGroup dragPointerLayout;
    @Nullable
    private SLDrawDistance drawDistance;
    private FadingTextViewLog fadingTextViewLog;
    @BindView(2131755755)
    LinearLayout flyButtonsLayout;
    /* access modifiers changed from: private */
    public GestureDetectorCompat gestureDetector;
    private final GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (WorldViewActivity.this.isInScaling || !(!WorldViewActivity.this.wasInScaling) || !(!WorldViewActivity.this.isDragging)) {
                return false;
            }
            float height = (f * 60.0f) / ((float) WorldViewActivity.this.worldViewHolder.getHeight());
            float height2 = ((-f2) * 60.0f) / ((float) WorldViewActivity.this.worldViewHolder.getHeight());
            if (WorldViewActivity.this.avatarControl == null) {
                return true;
            }
            WorldViewActivity.this.avatarControl.processCameraFling(height / 1.5f, height2 / 2.5f);
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            float rawX = motionEvent.getRawX();
            float rawY = motionEvent.getRawY();
            if (WorldViewActivity.this.isDragging) {
                WorldViewActivity.this.dragSelectorSetRawPosition((int) rawX, (int) rawY);
            } else if (!WorldViewActivity.this.isInScaling && (!WorldViewActivity.this.wasInScaling)) {
                int[] iArr = new int[2];
                WorldViewActivity.this.worldViewHolder.getLocationOnScreen(iArr);
                WorldViewActivity.this.mGLView.pickObjectHover(rawX - ((float) iArr[0]), rawY - ((float) iArr[1]));
            }
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (WorldViewActivity.this.isDragging) {
                AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) WorldViewActivity.this.dragPointer.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.x = Math.max(Math.min((int) (((float) layoutParams.x) - f), WorldViewActivity.this.dragPointerLayout.getWidth() - WorldViewActivity.this.dragPointer.getWidth()), 0);
                    layoutParams.y = Math.max(Math.min((int) (((float) layoutParams.y) - f2), WorldViewActivity.this.dragPointerLayout.getHeight() - WorldViewActivity.this.dragPointer.getHeight()), 0);
                    WorldViewActivity.this.dragPointer.setLayoutParams(layoutParams);
                    WorldViewActivity.this.selectByDragPointer(layoutParams.x, layoutParams.y);
                }
                return true;
            } else if (WorldViewActivity.this.isInScaling || !(!WorldViewActivity.this.wasInScaling)) {
                return false;
            } else {
                if (WorldViewActivity.this.displayedHUDid != 0) {
                    WorldViewActivity worldViewActivity = WorldViewActivity.this;
                    float unused = worldViewActivity.hudOffsetX = worldViewActivity.hudOffsetX + ((f / ((float) WorldViewActivity.this.worldViewHolder.getHeight())) / 2.0f);
                    WorldViewActivity worldViewActivity2 = WorldViewActivity.this;
                    float unused2 = worldViewActivity2.hudOffsetY = worldViewActivity2.hudOffsetY + ((f2 / ((float) WorldViewActivity.this.worldViewHolder.getHeight())) / 2.0f);
                    WorldViewActivity.this.mGLView.setHUDOffset(WorldViewActivity.this.hudOffsetX, WorldViewActivity.this.hudOffsetY);
                } else {
                    float height = ((-f) * 60.0f) / ((float) WorldViewActivity.this.worldViewHolder.getHeight());
                    float height2 = (f2 * 60.0f) / ((float) WorldViewActivity.this.worldViewHolder.getHeight());
                    if (WorldViewActivity.this.avatarControl != null) {
                        WorldViewActivity.this.avatarControl.processCameraRotate(height, height2);
                    }
                }
                return true;
            }
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (WorldViewActivity.this.isDragging) {
                WorldViewActivity.this.dragSelectorSetRawPosition((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
            } else if (WorldViewActivity.this.displayedHUDid != 0) {
                int[] iArr = new int[2];
                WorldViewActivity.this.worldViewHolder.getLocationOnScreen(iArr);
                WorldViewActivity.this.mGLView.touchHUD(motionEvent.getRawX() - ((float) iArr[0]), motionEvent.getRawY() - ((float) iArr[1]));
            } else {
                WorldViewActivity.this.handlePickedObject((ObjectIntersectInfo) null);
            }
            return true;
        }
    };
    /* access modifiers changed from: private */
    public float hudOffsetX = 0.0f;
    /* access modifiers changed from: private */
    public float hudOffsetY = 0.0f;
    /* access modifiers changed from: private */
    public float hudScaleFactor = 1.0f;
    @BindView(2131755744)
    FrameLayout insetsBackground;
    /* access modifiers changed from: private */
    public boolean isDragging = false;
    /* access modifiers changed from: private */
    public boolean isInScaling = false;
    /* access modifiers changed from: private */
    public boolean isInteracting = false;
    private boolean isSplitScreen;
    /* access modifiers changed from: private */
    public long lastActivityTime = SystemClock.uptimeMillis();
    /* access modifiers changed from: private */
    public long lastObjectActivityTime = SystemClock.uptimeMillis();
    private UUID lastTouchUUID = null;
    private boolean localDrawingEnabled = false;
    /* access modifiers changed from: private */
    public WorldSurfaceView mGLView;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private boolean manualCamMode = false;
    @BindView(2131755259)
    View moveButtonsLayout;
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f536$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.4.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.4.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    @BindView(2131755270)
    ImageButton objectChatButton;
    @BindView(2131755753)
    View objectControlsPanel;
    /* access modifiers changed from: private */
    public boolean objectDeselectTimerStarted = false;
    /* access modifiers changed from: private */
    public final Runnable objectDeselectTimerTask = new Runnable() {
        public void run() {
            boolean unused = WorldViewActivity.this.objectDeselectTimerStarted = false;
            if (!WorldViewActivity.this.detailsVisible() && (!WorldViewActivity.this.isDragging)) {
                long r0 = (WorldViewActivity.this.lastObjectActivityTime + WorldViewActivity.OBJECT_DESELECT_TIMEOUT_MILLIS) - SystemClock.uptimeMillis();
                Debug.Printf("ObjectDeselect: remaining %d", Long.valueOf(r0));
                if (r0 <= 0) {
                    WorldViewActivity.this.handlePickedObject((ObjectIntersectInfo) null);
                    return;
                }
                boolean unused2 = WorldViewActivity.this.objectDeselectTimerStarted = true;
                WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.objectDeselectTimerTask, r0);
            }
        }
    };
    @BindView(2131755754)
    ImageButton objectMoreButton;
    @BindView(2131755577)
    TextView objectNameTextView;
    @BindView(2131755554)
    ImageButton objectPayButton;
    @BindView(2131755764)
    View objectPopupLeftSpacer;
    @BindView(2131755269)
    ImageButton objectSitButton;
    @BindView(2131755538)
    ImageButton objectStandButton;
    @BindView(2131755268)
    ImageButton objectTouchButton;
    /* access modifiers changed from: private */
    public float oldScaleFocusX = Float.NaN;
    /* access modifiers changed from: private */
    public float oldScaleFocusY = Float.NaN;
    private ChatterNameRetriever pickedAvatarNameRetriever = null;
    private ObjectIntersectInfo pickedIntersectInfo = null;
    private SLObjectInfo pickedObject = null;
    private int prefDrawDistance = 20;
    private int prevDisplayedHUDid = 0;
    /* access modifiers changed from: private */
    public ScaleGestureDetector scaleGestureDetector;
    private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            Debug.Printf("Gesture: scale factor: %f", Float.valueOf(scaleGestureDetector.getScaleFactor()));
            if (WorldViewActivity.this.displayedHUDid != 0) {
                float unused = WorldViewActivity.this.hudScaleFactor = Math.max(0.1f, Math.min(WorldViewActivity.this.hudScaleFactor * scaleGestureDetector.getScaleFactor(), 10.0f));
                WorldViewActivity.this.mGLView.setHUDScaleFactor(WorldViewActivity.this.hudScaleFactor);
            } else {
                float width = (float) WorldViewActivity.this.worldViewTouchReceiver.getWidth();
                float height = (float) WorldViewActivity.this.worldViewTouchReceiver.getHeight();
                float focusX = scaleGestureDetector.getFocusX();
                float focusY = scaleGestureDetector.getFocusY();
                float f = ((focusX / width) - 0.5f) * (height / width);
                float f2 = (focusY / height) - 0.5f;
                float r4 = (focusX - WorldViewActivity.this.oldScaleFocusX) / height;
                float r5 = (focusY - WorldViewActivity.this.oldScaleFocusY) / height;
                float unused2 = WorldViewActivity.this.oldScaleFocusX = focusX;
                float unused3 = WorldViewActivity.this.oldScaleFocusY = focusY;
                if (WorldViewActivity.this.avatarControl != null) {
                    WorldViewActivity.this.avatarControl.processCameraZoom(scaleGestureDetector.getScaleFactor(), (-f) * 2.0f, (-f2) * 2.0f, r4, r5);
                }
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            boolean unused = WorldViewActivity.this.isInScaling = true;
            float unused2 = WorldViewActivity.this.oldScaleFocusX = scaleGestureDetector.getFocusX();
            float unused3 = WorldViewActivity.this.oldScaleFocusY = scaleGestureDetector.getFocusY();
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            boolean unused = WorldViewActivity.this.isInScaling = false;
        }
    };
    private final SubscriptionData<Integer, SLObjectProfileData> selectedObjectProfile = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f537$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.5.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.5.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    private UserManager userManager;
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f539$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.7.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.7.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    /* access modifiers changed from: private */
    public final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f540$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.8.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.8.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
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
    @BindView(2131755752)
    VoiceStatusView voiceStatusView;
    /* access modifiers changed from: private */
    public boolean wasInScaling = false;
    @BindView(2131755750)
    ViewGroup worldOverlaysContainer;
    @BindView(2131755743)
    FrameLayout worldViewHolder;
    private final View.OnTouchListener worldViewTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z;
            boolean r3 = WorldViewActivity.this.isInteracting;
            switch (motionEvent.getActionMasked()) {
                case 0:
                    boolean unused = WorldViewActivity.this.isInteracting = true;
                    z = true;
                    break;
                case 1:
                    boolean unused2 = WorldViewActivity.this.isInteracting = false;
                    z = true;
                    break;
                default:
                    z = false;
                    break;
            }
            if (WorldViewActivity.this.isInteracting && (!r3)) {
                WorldViewActivity.this.mGLView.setIsInteracting(true);
            }
            boolean unused3 = WorldViewActivity.this.wasInScaling = WorldViewActivity.this.isInScaling;
            boolean onTouchEvent = z | WorldViewActivity.this.scaleGestureDetector.onTouchEvent(motionEvent) | WorldViewActivity.this.gestureDetector.onTouchEvent(motionEvent);
            if (r3 && (!WorldViewActivity.this.isInteracting)) {
                WorldViewActivity.this.mGLView.setIsInteracting(false);
            }
            return onTouchEvent;
        }
    };
    @BindView(2131755746)
    View worldViewTouchReceiver;

    private static class SelectableAttachment {
        private String attachmentName;
        private int localID;

        public SelectableAttachment(int i, String str) {
            this.localID = i;
            this.attachmentName = str;
        }

        public int getLocalID() {
            return this.localID;
        }

        public String toString() {
            return this.attachmentName;
        }
    }

    private void beginCountingButtonsFade() {
        this.lastActivityTime = SystemClock.uptimeMillis();
        this.lastObjectActivityTime = this.lastActivityTime;
        startFadingButtonsTimer();
    }

    private void beginCountingObjectDeselect() {
        if (this.pickedObject != null) {
            this.lastObjectActivityTime = SystemClock.uptimeMillis();
            if (!this.objectDeselectTimerStarted) {
                this.objectDeselectTimerStarted = true;
                this.mHandler.postDelayed(this.objectDeselectTimerTask, OBJECT_DESELECT_TIMEOUT_MILLIS);
            }
        }
    }

    private void beginDragSelection() {
        this.isDragging = true;
        removeAllDetails();
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) this.dragPointer.getLayoutParams();
        layoutParams.x = (this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()) / 2;
        layoutParams.y = (this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()) / 2;
        this.dragPointer.setLayoutParams(layoutParams);
        selectByDragPointer(layoutParams.x, layoutParams.y);
        this.mGLView.setOwnAvatarHidden(true);
        updateObjectPanel();
    }

    private void chatWithObject(SLObjectInfo sLObjectInfo) {
        if ((sLObjectInfo instanceof SLObjectAvatarInfo) && !((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() && sLObjectInfo.getId() != null) {
            DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, ChatFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId())));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.findFragmentById(com.lumiyaviewer.lumiya.R.id.details);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean detailsVisible() {
        /*
            r2 = this;
            android.support.v4.app.FragmentManager r0 = r2.getSupportFragmentManager()
            if (r0 == 0) goto L_0x0017
            r1 = 2131755284(0x7f100114, float:1.9141443E38)
            android.support.v4.app.Fragment r0 = r0.findFragmentById(r1)
            if (r0 == 0) goto L_0x0017
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0017
            r0 = 1
            return r0
        L_0x0017:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.render.WorldViewActivity.detailsVisible():boolean");
    }

    private void displayHUD(int i) {
        Debug.Printf("Displaying HUD with ID %d", Integer.valueOf(i));
        this.displayedHUDid = i;
        this.mGLView.setDisplayedHUDid(i);
        if (this.displayedHUDid != this.prevDisplayedHUDid) {
            this.hudScaleFactor = 1.0f;
            this.hudOffsetX = 0.0f;
            this.hudOffsetY = 0.0f;
            this.prevDisplayedHUDid = this.displayedHUDid;
        }
        this.mGLView.setHUDScaleFactor(this.hudScaleFactor);
        this.mGLView.setHUDOffset(this.hudOffsetX, this.hudOffsetY);
        if (this.displayedHUDid != 0) {
            handlePickedObject((ObjectIntersectInfo) null);
        }
        updateObjectPanel();
    }

    /* access modifiers changed from: private */
    public void dragSelectorSetRawPosition(int i, int i2) {
        int[] iArr = new int[2];
        this.dragPointerLayout.getLocationOnScreen(iArr);
        int width = i - (this.dragPointer.getWidth() / 2);
        int height = i2 - (this.dragPointer.getHeight() / 2);
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) this.dragPointer.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.x = Math.max(Math.min(width - iArr[0], this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()), 0);
            layoutParams.y = Math.max(Math.min(height - iArr[1], this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()), 0);
            this.dragPointer.setLayoutParams(layoutParams);
            selectByDragPointer(layoutParams.x, layoutParams.y);
        }
    }

    private void endDragSelection() {
        this.isDragging = false;
        this.mGLView.setOwnAvatarHidden(false);
        updateObjectPanel();
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
    }

    private void enterCardboardView() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            Debug.Printf("Cardboard: audio permission not yet granted", new Object[0]);
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, 100);
            return;
        }
        Debug.Printf("Cardboard: audio permission already granted", new Object[0]);
        startCardboardActivity();
    }

    private void initContentView() {
        setContentView((int) R.layout.world_view);
        ButterKnife.bind((Activity) this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.mGLView = new WorldSurfaceView(this, this.userManager);
        this.worldViewHolder.addView(this.mGLView);
        this.buttonMoveForward.setOnTouchListener(this);
        this.buttonMoveBackward.setOnTouchListener(this);
        this.buttonTurnLeft.setOnTouchListener(this);
        this.buttonTurnRight.setOnTouchListener(this);
        this.buttonMoveForward.setFocusable(false);
        this.buttonMoveBackward.setFocusable(false);
        this.buttonTurnLeft.setFocusable(false);
        this.buttonTurnRight.setFocusable(false);
        this.buttonFlyUpward.setOnTouchListener(this);
        this.buttonFlyDownward.setOnTouchListener(this);
        this.buttonFlyUpward.setFocusable(false);
        this.buttonFlyDownward.setFocusable(false);
        this.voiceStatusView.setShowActiveChatterName(true);
        this.worldViewTouchReceiver.setOnTouchListener(this.worldViewTouchListener);
        this.objectControlsPanel.setVisibility(8);
        View findViewById = findViewById(R.id.offline_notify_status_layout);
        if (findViewById != null) {
            findViewById.setBackgroundColor(Color.argb(128, 0, 0, 0));
            int applyDimension = (int) TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics());
            findViewById.setPadding(applyDimension, applyDimension, applyDimension, applyDimension);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m836com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref0(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit != null) {
            this.avatarControl = sLAgentCircuit.getModules().avatarControl;
            this.drawDistance = sLAgentCircuit.getModules().drawDistance;
            if (this.localDrawingEnabled) {
                this.drawDistance.Enable3DView(this.prefDrawDistance);
            }
            if (this.camButtonEnabled) {
                this.manualCamMode = this.avatarControl.getIsManualCamming();
            }
        } else {
            handlePickedObject((ObjectIntersectInfo) null);
            this.avatarControl = null;
            this.drawDistance = null;
        }
        this.mHandler.post(this.buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
        updateObjectPanel();
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocation */
    public void m839com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref3(CurrentLocationInfo currentLocationInfo2) {
        ParcelData parcelData = currentLocationInfo2 != null ? currentLocationInfo2.parcelData() : null;
        String name = parcelData != null ? parcelData.getName() : null;
        if (name == null) {
            name = getString(R.string.name_loading_title);
        }
        setDefaultTitle(name, (String) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyAvatarState */
    public void m837com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref1(MyAvatarState myAvatarState2) {
        updateObjectPanel();
    }

    /* access modifiers changed from: private */
    /* renamed from: onPickedAvatarNameUpdated */
    public void m842com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref6(ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.pickedAvatarNameRetriever) {
            updateObjectPanel();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onSelectedObjectProfile */
    public void m838com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref2(SLObjectProfileData sLObjectProfileData) {
        Debug.Printf("got selected object profile: %s", sLObjectProfileData);
        updateObjectPanel();
        if (sLObjectProfileData != null) {
            SLAgentCircuit data = this.agentCircuit.getData();
            if (sLObjectProfileData.isPayable() && sLObjectProfileData.payInfo() == null && data != null) {
                data.DoRequestPayPrice(sLObjectProfileData.objectUUID());
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceActiveChatter */
    public void m840com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref4(ChatterID chatterID) {
        if (this.voiceStatusView != null) {
            this.voiceStatusView.setChatterID(chatterID);
        }
        if (chatterID == null || this.userManager == null) {
            this.voiceChatInfo.unsubscribe();
        } else {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    public void m841com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref5(VoiceChatInfo voiceChatInfo2) {
    }

    /* access modifiers changed from: private */
    public void selectByDragPointer(int i, int i2) {
        int[] iArr = new int[2];
        this.dragPointerLayout.getLocationOnScreen(iArr);
        int[] iArr2 = new int[2];
        this.worldViewHolder.getLocationOnScreen(iArr2);
        this.mGLView.pickObjectHover((float) (((iArr[0] + (this.dragPointer.getWidth() / 2)) + i) - iArr2[0]), (float) (((iArr[1] + (this.dragPointer.getHeight() / 2)) + i2) - iArr2[1]));
    }

    private void selectHUDtoDisplay() {
        int i;
        SLAttachmentPoint sLAttachmentPoint;
        ArrayList arrayList = new ArrayList();
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null) {
            SLObjectAvatarInfo agentAvatar = data.getGridConnection().parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (SLObjectInfo sLObjectInfo : agentAvatar.treeNode) {
                        if (!Strings.nullToEmpty(sLObjectInfo.getName()).startsWith("#") && (i = sLObjectInfo.attachmentID) >= 0 && i < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]) != null && sLAttachmentPoint.isHUD) {
                            arrayList.add(new SelectableAttachment(sLObjectInfo.localID, sLObjectInfo.name));
                        }
                    }
                } catch (NoSuchElementException e) {
                    Debug.Warning(e);
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            if (arrayList.size() == 1) {
                displayHUD(((SelectableAttachment) arrayList.get(0)).getLocalID());
                return;
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367043, arrayList);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((int) R.string.select_hud_title);
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener(this, arrayList) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f530$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f531$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.10.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.10.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
            builder.create().show();
        }
    }

    private void showObjectInfo(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo.getId() != null && this.userManager != null) {
            if (!sLObjectInfo.isAvatar()) {
                DetailsActivity.showEmbeddedDetails(this, ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), sLObjectInfo.localID));
            } else if (sLObjectInfo instanceof SLObjectAvatarInfo) {
                DetailsActivity.showEmbeddedDetails(this, UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId())));
            }
        }
    }

    private void startCardboardActivity() {
        if (this.userManager != null) {
            Intent intent = new Intent(this, CardboardTransitionActivity.class);
            ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID());
            intent.addFlags(16777216);
            startActivity(intent);
        }
        finish();
    }

    /* access modifiers changed from: private */
    public void startFadingButtons() {
        if (Build.VERSION.SDK_INT >= 11 && this.buttonsFadeAnimator != null) {
            this.buttonsFadeAnimator.start();
        }
    }

    private void startFadingButtonsTimer() {
        if (!this.buttonsFadeTimerStarted) {
            Debug.Printf("ButtonsFade: starting timer", new Object[0]);
            this.buttonsFadeTimerStarted = true;
            this.mHandler.postDelayed(this.buttonsFadeTask, BUTTONS_FADE_TIMEOUT_MILLIS);
        }
    }

    private void stopAvatarAnimations() {
        if (this.avatarControl != null) {
            this.avatarControl.StopAvatarAnimations();
        }
    }

    private void takeScreenshot() {
        Toast.makeText(this, R.string.taking_screenshot, 0).show();
        this.mGLView.takeScreenshot();
    }

    private void touchObject(SLObjectInfo sLObjectInfo, ObjectIntersectInfo objectIntersectInfo) {
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null && sLObjectInfo != null) {
            if (!sLObjectInfo.isAvatar()) {
                this.lastTouchUUID = sLObjectInfo.getId();
                boolean z = false;
                if (objectIntersectInfo != null) {
                    z = objectIntersectInfo.intersectInfo.faceKnown;
                }
                if (z) {
                    LLVector3 absolutePosition = sLObjectInfo.getAbsolutePosition();
                    data.TouchObjectFace(sLObjectInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
                    return;
                }
                data.TouchObject(sLObjectInfo.localID);
            } else if (sLObjectInfo.hasTouchableChildren()) {
                DetailsActivity.showEmbeddedDetails(this, TouchableObjectsFragment.class, TouchableObjectsFragment.makeSelection(this.userManager.getUserID(), sLObjectInfo.getId()));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        if ((!(r12.camButtonEnabled ? r12.manualCamMode : false)) == false) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00af, code lost:
        if ((!(!r12.camButtonEnabled ? r12.manualCamMode : false)) != false) goto L_0x00b7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x013b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateObjectPanel() {
        /*
            r12 = this;
            com.lumiyaviewer.lumiya.react.SubscriptionData<com.lumiyaviewer.lumiya.react.SubscriptionSingleKey, com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState> r0 = r12.myAvatarState
            java.lang.Object r0 = r0.getData()
            com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState r0 = (com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState) r0
            com.lumiyaviewer.lumiya.react.SubscriptionData<java.util.UUID, com.lumiyaviewer.lumiya.slproto.SLAgentCircuit> r1 = r12.agentCircuit
            java.lang.Object r1 = r1.getData()
            com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r1 = (com.lumiyaviewer.lumiya.slproto.SLAgentCircuit) r1
            if (r1 == 0) goto L_0x0143
            r2 = 1
            r8 = r2
        L_0x0014:
            if (r0 == 0) goto L_0x0147
            boolean r2 = r0.isSitting()
            r7 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x014b
            boolean r2 = r0.hasHUDs()
            r6 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x014f
            boolean r2 = r0.isFlying()
            r5 = r2
        L_0x0029:
            if (r1 == 0) goto L_0x0153
            com.lumiyaviewer.lumiya.slproto.modules.SLModules r2 = r1.getModules()
            com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController r2 = r2.rlvController
            boolean r2 = r2.canStandUp()
            r4 = r2
        L_0x0036:
            if (r1 == 0) goto L_0x0157
            com.lumiyaviewer.lumiya.slproto.modules.SLModules r1 = r1.getModules()
            com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController r1 = r1.rlvController
            boolean r1 = r1.canSit()
        L_0x0042:
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            if (r2 == 0) goto L_0x015a
            r2 = 1
        L_0x0047:
            java.lang.String r3 = "isSitting %b, isFlying %b, hasHUDs %b, isDragging %b"
            r9 = 4
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r7)
            r11 = 0
            r9[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r5)
            r11 = 1
            r9[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r6)
            r11 = 2
            r9[r11] = r10
            boolean r10 = r12.isDragging
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r10)
            r11 = 3
            r9[r11] = r10
            com.lumiyaviewer.lumiya.Debug.Printf(r3, r9)
            android.view.ViewGroup r9 = r12.dragPointerLayout
            boolean r3 = r12.isDragging
            if (r3 == 0) goto L_0x015d
            r3 = 0
        L_0x0075:
            r9.setVisibility(r3)
            android.view.View r9 = r12.dragPointer
            boolean r3 = r12.isDragging
            if (r3 == 0) goto L_0x0160
            r3 = 0
        L_0x007f:
            r9.setVisibility(r3)
            android.widget.LinearLayout r9 = r12.flyButtonsLayout
            if (r7 != 0) goto L_0x008a
            r3 = r8 ^ 1
            if (r3 == 0) goto L_0x0094
        L_0x008a:
            boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0163
            boolean r3 = r12.manualCamMode
        L_0x0090:
            r3 = r3 ^ 1
            if (r3 != 0) goto L_0x009a
        L_0x0094:
            boolean r3 = r12.isDragging
            if (r3 != 0) goto L_0x009a
            if (r2 == 0) goto L_0x0166
        L_0x009a:
            r3 = 8
        L_0x009c:
            r9.setVisibility(r3)
            android.view.View r9 = r12.moveButtonsLayout
            if (r7 != 0) goto L_0x00a7
            r3 = r8 ^ 1
            if (r3 == 0) goto L_0x00b1
        L_0x00a7:
            boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0169
            boolean r3 = r12.manualCamMode
        L_0x00ad:
            r3 = r3 ^ 1
            if (r3 != 0) goto L_0x00b7
        L_0x00b1:
            boolean r3 = r12.isDragging
            if (r3 != 0) goto L_0x00b7
            if (r2 == 0) goto L_0x016c
        L_0x00b7:
            r3 = 4
        L_0x00b8:
            r9.setVisibility(r3)
            android.widget.ImageButton r9 = r12.buttonStandUp
            if (r4 == 0) goto L_0x016f
            if (r7 == 0) goto L_0x016f
            boolean r3 = r12.isDragging
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x016f
            r3 = 0
        L_0x00c8:
            r9.setVisibility(r3)
            android.widget.Button r9 = r12.buttonHUD
            if (r6 == 0) goto L_0x0173
            boolean r3 = r12.isDragging
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x0173
            if (r8 == 0) goto L_0x0173
            r3 = 0
        L_0x00d8:
            r9.setVisibility(r3)
            android.widget.ImageButton r6 = r12.buttonFlyDownward
            if (r5 == 0) goto L_0x00e1
            if (r8 != 0) goto L_0x00e9
        L_0x00e1:
            boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0177
            boolean r3 = r12.manualCamMode
            if (r3 == 0) goto L_0x0177
        L_0x00e9:
            r3 = 0
        L_0x00ea:
            r6.setVisibility(r3)
            android.widget.ImageButton r6 = r12.buttonStopFlying
            if (r5 == 0) goto L_0x017e
            if (r8 == 0) goto L_0x017e
            boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x017b
            boolean r3 = r12.manualCamMode
        L_0x00f9:
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x017e
            r3 = 0
        L_0x00fe:
            r6.setVisibility(r3)
            android.widget.ImageButton r5 = r12.buttonCamOn
            boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0182
            boolean r3 = r12.manualCamMode
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x0182
            boolean r3 = r12.isDragging
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x0182
            r3 = r2 ^ 1
            if (r3 == 0) goto L_0x0182
            r3 = 0
        L_0x0118:
            r5.setVisibility(r3)
            android.widget.ImageButton r3 = r12.buttonCamOff
            boolean r5 = r12.camButtonEnabled
            if (r5 == 0) goto L_0x0185
            boolean r5 = r12.manualCamMode
            if (r5 == 0) goto L_0x0185
            boolean r5 = r12.isDragging
            r5 = r5 ^ 1
            if (r5 == 0) goto L_0x0185
            r2 = r2 ^ 1
            if (r2 == 0) goto L_0x0185
            r2 = 0
        L_0x0130:
            r3.setVisibility(r2)
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            if (r2 == 0) goto L_0x013b
            r2 = r8 ^ 1
            if (r2 == 0) goto L_0x0188
        L_0x013b:
            android.view.View r0 = r12.objectControlsPanel
            r1 = 8
            r0.setVisibility(r1)
        L_0x0142:
            return
        L_0x0143:
            r2 = 0
            r8 = r2
            goto L_0x0014
        L_0x0147:
            r2 = 0
            r7 = r2
            goto L_0x001b
        L_0x014b:
            r2 = 0
            r6 = r2
            goto L_0x0022
        L_0x014f:
            r2 = 0
            r5 = r2
            goto L_0x0029
        L_0x0153:
            r2 = 0
            r4 = r2
            goto L_0x0036
        L_0x0157:
            r1 = 0
            goto L_0x0042
        L_0x015a:
            r2 = 0
            goto L_0x0047
        L_0x015d:
            r3 = 4
            goto L_0x0075
        L_0x0160:
            r3 = 4
            goto L_0x007f
        L_0x0163:
            r3 = 0
            goto L_0x0090
        L_0x0166:
            r3 = 0
            goto L_0x009c
        L_0x0169:
            r3 = 0
            goto L_0x00ad
        L_0x016c:
            r3 = 0
            goto L_0x00b8
        L_0x016f:
            r3 = 8
            goto L_0x00c8
        L_0x0173:
            r3 = 8
            goto L_0x00d8
        L_0x0177:
            r3 = 8
            goto L_0x00ea
        L_0x017b:
            r3 = 0
            goto L_0x00f9
        L_0x017e:
            r3 = 8
            goto L_0x00fe
        L_0x0182:
            r3 = 8
            goto L_0x0118
        L_0x0185:
            r2 = 8
            goto L_0x0130
        L_0x0188:
            android.view.View r2 = r12.objectControlsPanel
            r3 = 0
            r2.setVisibility(r3)
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            boolean r2 = r2.isTouchable()
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = r12.pickedObject
            boolean r3 = r3.isAvatar()
            if (r3 == 0) goto L_0x01a3
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = r12.pickedObject
            boolean r3 = r3.hasTouchableChildren()
            r2 = r2 | r3
        L_0x01a3:
            android.widget.ImageButton r3 = r12.objectTouchButton
            if (r2 == 0) goto L_0x021f
            r2 = 0
        L_0x01a8:
            r3.setVisibility(r2)
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            boolean r3 = r2.isAvatar()
            if (r7 == 0) goto L_0x0222
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            int r2 = r2.localID
            int r0 = r0.sittingOn()
            if (r2 != r0) goto L_0x0222
            r0 = 1
        L_0x01be:
            if (r3 != 0) goto L_0x0224
            r2 = r0 ^ 1
        L_0x01c2:
            if (r3 != 0) goto L_0x0226
        L_0x01c4:
            android.widget.ImageButton r5 = r12.objectSitButton
            if (r2 == 0) goto L_0x0228
            if (r1 == 0) goto L_0x0228
            r1 = 0
        L_0x01cb:
            r5.setVisibility(r1)
            android.widget.ImageButton r1 = r12.objectStandButton
            if (r0 == 0) goto L_0x022b
            if (r4 == 0) goto L_0x022b
            r0 = 0
        L_0x01d5:
            r1.setVisibility(r0)
            android.widget.ImageButton r1 = r12.objectChatButton
            if (r3 == 0) goto L_0x022e
            r0 = 0
        L_0x01dd:
            r1.setVisibility(r0)
            android.widget.ImageView r1 = r12.avatarIconView
            if (r3 == 0) goto L_0x0231
            r0 = 0
        L_0x01e5:
            r1.setVisibility(r0)
            android.widget.ImageButton r1 = r12.objectPayButton
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            boolean r0 = r0.isPayable()
            if (r0 != 0) goto L_0x01f8
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            byte r0 = r0.saleType
            if (r0 == 0) goto L_0x0234
        L_0x01f8:
            r0 = 0
        L_0x01f9:
            r1.setVisibility(r0)
            r1 = 0
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            boolean r0 = r0.isAvatar()
            if (r0 == 0) goto L_0x0237
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r0 = r12.pickedAvatarNameRetriever
            if (r0 == 0) goto L_0x0264
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r0 = r12.pickedAvatarNameRetriever
            java.lang.String r0 = r0.getResolvedName()
        L_0x020f:
            if (r0 != 0) goto L_0x0218
            r0 = 2131296825(0x7f090239, float:1.8211578E38)
            java.lang.String r0 = r12.getString(r0)
        L_0x0218:
            android.widget.TextView r1 = r12.objectNameTextView
            r1.setText(r0)
            goto L_0x0142
        L_0x021f:
            r2 = 8
            goto L_0x01a8
        L_0x0222:
            r0 = 0
            goto L_0x01be
        L_0x0224:
            r2 = 0
            goto L_0x01c2
        L_0x0226:
            r0 = 0
            goto L_0x01c4
        L_0x0228:
            r1 = 8
            goto L_0x01cb
        L_0x022b:
            r0 = 8
            goto L_0x01d5
        L_0x022e:
            r0 = 8
            goto L_0x01dd
        L_0x0231:
            r0 = 8
            goto L_0x01e5
        L_0x0234:
            r0 = 8
            goto L_0x01f9
        L_0x0237:
            com.lumiyaviewer.lumiya.react.SubscriptionData<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData> r0 = r12.selectedObjectProfile
            java.lang.Object r0 = r0.getData()
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData) r0
            if (r0 == 0) goto L_0x0262
            java.util.UUID r2 = r0.objectUUID()
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = r12.pickedObject
            java.util.UUID r3 = r3.getId()
            boolean r2 = com.google.common.base.Objects.equal(r2, r3)
            if (r2 == 0) goto L_0x0262
            com.google.common.base.Optional r0 = r0.name()
            java.lang.Object r0 = r0.orNull()
            java.lang.String r0 = (java.lang.String) r0
        L_0x025b:
            if (r0 != 0) goto L_0x020f
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            java.lang.String r0 = r0.name
            goto L_0x020f
        L_0x0262:
            r0 = r1
            goto L_0x025b
        L_0x0264:
            r0 = r1
            goto L_0x020f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.render.WorldViewActivity.updateObjectPanel():void");
    }

    private void updateSimTimeOverride() {
        if (this.mGLView != null) {
            GlobalOptions instance = GlobalOptions.getInstance();
            this.mGLView.setForcedTime(instance.getForceDaylightTime(), instance.getForceDaylightHour());
        }
    }

    private void updateSplitScreenLayout() {
        int i = 0;
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.details);
        Object[] objArr = new Object[3];
        objArr[0] = Boolean.valueOf(this.isSplitScreen);
        objArr[1] = Boolean.valueOf(findFragmentById != null);
        objArr[2] = Boolean.valueOf(findFragmentById != null ? findFragmentById.isDetached() : false);
        Debug.Printf("updateSplitScreenLayout: isSplitScreen now %b details has %b detached %b", objArr);
        if (findFragmentById == null || !(!findFragmentById.isDetached())) {
            this.worldOverlaysContainer.setVisibility(0);
        } else {
            this.detailsContainer.setVisibility(0);
            this.worldOverlaysContainer.setVisibility(this.isSplitScreen ? 0 : 8);
        }
        View view = this.objectPopupLeftSpacer;
        if (!this.isSplitScreen) {
            i = 8;
        }
        view.setVisibility(i);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.detailsContainer.getVisibility() == 0) {
            return super.dispatchKeyEvent(keyEvent);
        }
        switch (keyEvent.getKeyCode()) {
            case 19:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(2);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            case 20:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(4);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            case 21:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(TURNING_SPEED);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning();
                    }
                }
                return true;
            case 22:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(-50.0f);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning();
                    }
                }
                return true;
            case 92:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(8);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            case 93:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(16);
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(keyEvent);
        }
    }

    @EventHandler
    public void handleBakingProgressEvent(SLBakingProgressEvent sLBakingProgressEvent) {
        if (sLBakingProgressEvent.first) {
            Toast.makeText(this, "Updating avatar appearance...", 0).show();
        }
    }

    public void handleChatEvent(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
        if (this.chatOver3D && !detailsVisible() && this.userManager != null && this.chatsOverlayLayout != null && this.fadingTextViewLog != null) {
            this.fadingTextViewLog.handleChatEvent(chatMessageEvent);
        }
    }

    public void handlePickedObject(ObjectIntersectInfo objectIntersectInfo) {
        this.pickedIntersectInfo = objectIntersectInfo;
        this.pickedObject = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null;
        if (this.pickedObject != null) {
            if (this.pickedObject.isAvatar()) {
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), this.pickedObject.getId());
                if (!Objects.equal(this.pickedAvatarNameRetriever != null ? this.pickedAvatarNameRetriever.chatterID : null, userChatterID)) {
                    if (this.pickedAvatarNameRetriever != null) {
                        this.pickedAvatarNameRetriever.dispose();
                        this.pickedAvatarNameRetriever = null;
                    }
                    this.pickedAvatarNameRetriever = new ChatterNameRetriever(userChatterID, new ChatterNameRetriever.OnChatterNameUpdated(this) {

                        /* renamed from: -$f0 */
                        private final /* synthetic */ Object f541$f0;

                        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
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
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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
                    }, UIThreadExecutor.getInstance());
                }
            } else {
                Debug.Printf("ObjectPick: picked object %d", Integer.valueOf(this.pickedObject.localID));
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(this.pickedObject.localID));
            }
            updateObjectPanel();
            beginCountingObjectDeselect();
        } else {
            this.selectedObjectProfile.unsubscribe();
            if (this.pickedAvatarNameRetriever != null) {
                this.pickedAvatarNameRetriever.dispose();
                this.pickedAvatarNameRetriever = null;
            }
        }
        this.mGLView.setDrawPickedObject(this.pickedObject);
        updateObjectPanel();
    }

    /* access modifiers changed from: protected */
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return cls == ContactsFragment.class || cls == ChatFragment.class || cls == ObjectDetailsFragment.class || cls == OutfitsFragment.class || cls == UserProfileFragment.class;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_10269  reason: not valid java name */
    public /* synthetic */ void m843lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_10269(ValueAnimator valueAnimator) {
        this.insetsBackground.setAlpha(1.0f - valueAnimator.getAnimatedFraction());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43183  reason: not valid java name */
    public /* synthetic */ void m844lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43183(DialogInterface dialogInterface, int i) {
        SLAgentCircuit activeAgentCircuit;
        if (!(this.userManager == null || (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) == null || this.pickedObject == null)) {
            activeAgentCircuit.BuyObject(this.pickedObject.localID, this.pickedObject.saleType, this.pickedObject.salePrice);
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_48476  reason: not valid java name */
    public /* synthetic */ void m845lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_48476(List list, DialogInterface dialogInterface, int i) {
        if (i >= 0 && i < list.size()) {
            displayHUD(((SelectableAttachment) list.get(i)).getLocalID());
        }
    }

    public int mapThemeResourceId(int i) {
        return i == 2131427372 ? R.style.Theme_Lumiya_Light_NoActionBar : i == 2131427375 ? R.style.Theme_Lumiya_Pink_NoActionBar : R.style.Theme_Lumiya_NoActionBar;
    }

    public void moveTouchEvent(int i, MotionEvent motionEvent) {
        float f = 1.0f;
        float f2 = -1.0f;
        if (this.avatarControl == null) {
            return;
        }
        if (motionEvent.getAction() == 0) {
            if (!this.manualCamMode || !this.camButtonEnabled) {
                this.avatarControl.stopCamming();
                this.avatarControl.StartAgentMotion(i);
                if ((i & 8) != 0 || (i & 16) != 0) {
                    updateObjectPanel();
                    return;
                }
                return;
            }
            float f3 = (i & 16) != 0 ? -1.0f : (i & 8) != 0 ? 1.0f : 0.0f;
            float f4 = (i & 2) != 0 ? 1.0f : 0.0f;
            if ((i & 4) != 0) {
                f4 = -1.0f;
            }
            if ((i & 32) == 0) {
                f = 0.0f;
            }
            if ((i & 64) == 0) {
                f2 = f;
            }
            this.avatarControl.startCameraManualControl(0.0f, f4, f3, f2);
        } else if (motionEvent.getAction() != 1) {
        } else {
            if (!this.manualCamMode || !this.camButtonEnabled) {
                this.avatarControl.StopAgentMotion();
            } else {
                this.avatarControl.stopCameraManualControl();
            }
        }
    }

    @OnClick({2131755762})
    public void onCamOffButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = false;
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(false);
            }
            updateObjectPanel();
        }
    }

    @OnClick({2131755761})
    public void onCamOnButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = true;
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(true);
            }
            updateObjectPanel();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.isSplitScreen = LumiyaApp.isSplitScreenNeeded(this);
        updateSplitScreenLayout();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.userManager = ActivityUtils.getUserManager(getIntent());
        if (this.userManager == null) {
            finish();
            return;
        }
        this.isSplitScreen = LumiyaApp.isSplitScreenNeeded(this);
        this.scaleGestureDetector = new ScaleGestureDetector(this, this.scaleGestureListener);
        if (Build.VERSION.SDK_INT >= 19) {
            this.scaleGestureDetector.setQuickScaleEnabled(true);
        }
        this.gestureDetector = new GestureDetectorCompat(this, this.gestureListener);
        initContentView();
        this.fadingTextViewLog = new FadingTextViewLog(this.userManager, this, this.chatsOverlayLayout, Color.rgb(192, 192, 192), Color.argb(160, 0, 0, 0));
        if (Build.VERSION.SDK_INT >= 12) {
            this.buttonsFadeAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.buttonsFadeAnimator.setDuration(1000);
            this.buttonsFadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f529$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.1.$m$0(android.animation.ValueAnimator):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.1.$m$0(android.animation.ValueAnimator):void, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
        }
        updateSplitScreenLayout();
        startFadingButtonsTimer();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.world_view_menu, menu);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onDetailsStackEmpty() {
        if (super.onDetailsStackEmpty()) {
            return true;
        }
        this.detailsContainer.setVisibility(8);
        if (!this.isSplitScreen) {
            this.worldOverlaysContainer.setVisibility(0);
        }
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
        return false;
    }

    @Nullable
    public Intent onGetNotifyCaptureIntent(@Nonnull UnreadNotificationInfo unreadNotificationInfo, Intent intent) {
        Debug.Printf("NotifyCapture: returning new intent", new Object[0]);
        Intent intent2 = new Intent(this, WorldViewActivity.class);
        intent2.putExtras(intent);
        intent2.putExtra(FROM_NOTIFICATION_TAG, true);
        intent2.addFlags(536870912);
        return intent2;
    }

    @EventHandler
    public void onGlobalOptionsChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        updateSimTimeOverride();
    }

    @OnClick({2131755763})
    public void onHUDButton() {
        if (this.displayedHUDid != 0) {
            this.displayedHUDid = 0;
            this.mGLView.setDisplayedHUDid(this.displayedHUDid);
            updateObjectPanel();
            return;
        }
        selectHUDtoDisplay();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Debug.Printf("NotifyCapture: got newIntent: %s", intent);
        this.mHandler.post(this.buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
        if (intent.hasExtra(FROM_NOTIFICATION_TAG)) {
            Bundle bundleExtra = intent.hasExtra(MasterDetailsActivity.INTENT_SELECTION_KEY) ? intent.getBundleExtra(MasterDetailsActivity.INTENT_SELECTION_KEY) : null;
            if (bundleExtra != null) {
                DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, bundleExtra);
            } else if (this.userManager != null) {
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null));
            }
        }
    }

    @OnClick({2131755270})
    public void onObjectChatButton() {
        if (this.pickedObject != null) {
            chatWithObject(this.pickedObject);
        }
    }

    @OnClick({2131755754})
    public void onObjectMoreButton() {
        if (this.pickedObject != null) {
            showObjectInfo(this.pickedObject);
        }
    }

    @OnClick({2131755554})
    public void onObjectPayButton() {
        SLObjectProfileData data;
        if (this.pickedObject != null) {
            String name = this.pickedObject.getName();
            if (Strings.isNullOrEmpty(name)) {
                name = getString(R.string.object_name_loading);
            }
            if (this.pickedObject.saleType != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage((CharSequence) String.format(getString(R.string.object_buy_confirm), new Object[]{name, Integer.valueOf(this.pickedObject.salePrice)})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener(this) {

                    /* renamed from: -$f0 */
                    private final /* synthetic */ Object f534$f0;

                    private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.2.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.2.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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
                }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new $Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8());
                builder.show();
            } else if (this.pickedObject.isPayable() && (data = this.selectedObjectProfile.getData()) != null && this.userManager != null) {
                ObjectPayDialog.show(this, this.userManager, data);
            }
        }
    }

    @OnClick({2131755269})
    public void onObjectSitButton() {
        if (this.pickedObject != null && this.avatarControl != null) {
            this.avatarControl.SitOnObject(this.pickedObject.getId());
        }
    }

    @OnClick({2131755264, 2131755538})
    public void onObjectStandButton() {
        if (this.avatarControl != null) {
            this.avatarControl.Stand();
        }
    }

    @OnClick({2131755268})
    public void onObjectTouchButton() {
        if (this.pickedObject != null) {
            touchObject(this.pickedObject, this.pickedIntersectInfo);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_chat:
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null));
                return true;
            case R.id.item_outfits:
                DetailsActivity.showEmbeddedDetails(this, OutfitsFragment.class, OutfitsFragment.makeSelection(this.userManager.getUserID(), (UUID) null));
                return true;
            case R.id.item_select_object:
                if (!this.isDragging) {
                    beginDragSelection();
                } else {
                    endDragSelection();
                }
                return true;
            case R.id.item_cardboard_view:
                enterCardboardView();
                return true;
            case R.id.item_take_screenshot:
                takeScreenshot();
                return true;
            case R.id.item_stop_animations:
                stopAvatarAnimations();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPause() {
        Debug.Printf("WorldViewActivity: onPause", new Object[0]);
        if (this.userManager != null) {
            this.userManager.getUnreadNotificationManager().clearNotifyCapture(this);
            try {
                this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(this.chatEventHandler);
            } catch (IllegalArgumentException e) {
            }
            if (this.fadingTextViewLog != null) {
                this.fadingTextViewLog.clearChatEvents();
            }
        }
        this.localDrawingEnabled = false;
        if (this.drawDistance != null) {
            this.drawDistance.Disable3DView();
        }
        this.mGLView.onPause();
        this.mHandler.removeCallbacks(this.objectDeselectTimerTask);
        this.mHandler.removeCallbacks(this.buttonsFadeTask);
        this.objectDeselectTimerStarted = false;
        this.buttonsFadeTimerStarted = false;
        this.myAvatarState.unsubscribe();
        this.selectedObjectProfile.unsubscribe();
        this.currentLocationInfo.unsubscribe();
        System.gc();
        super.onPause();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(R.id.item_cardboard_view);
        if (findItem != null) {
            findItem.setVisible(Build.VERSION.SDK_INT >= 23);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void onRequestPermissionsResult(int i, @Nonnull String[] strArr, @Nonnull int[] iArr) {
        Debug.Printf("Cardboard: onRequestPermissionResult, code %d", Integer.valueOf(i));
        if (i == 100) {
            startCardboardActivity();
        }
    }

    public void onResume() {
        super.onResume();
        Debug.Printf("WorldViewActivity: onResume", new Object[0]);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.chatOver3D = defaultSharedPreferences.getBoolean("chat_over_3d", true);
        this.arrowsToTurn = defaultSharedPreferences.getBoolean("arrows_rotate_avatar", false);
        this.camButtonEnabled = defaultSharedPreferences.getBoolean("cam_button_enabled", false);
        if (this.userManager != null) {
            this.userManager.getUnreadNotificationManager().setNotifyCapture(this);
            this.myAvatarState.subscribe(this.userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.currentLocationInfo.subscribe(this.userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
            this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(this.chatEventHandler);
        }
        RenderSettings renderSettings = new RenderSettings(defaultSharedPreferences);
        this.prefDrawDistance = renderSettings.drawDistance;
        this.localDrawingEnabled = true;
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance);
        }
        if (this.camButtonEnabled && this.avatarControl != null) {
            this.manualCamMode = this.avatarControl.getIsManualCamming();
        }
        this.mGLView.setDrawDistance(this.prefDrawDistance);
        this.mGLView.setAvatarCountLimit(renderSettings.avatarCountLimit);
        this.mGLView.onResume();
        updateObjectPanel();
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.postRemovingStaleChats();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
            this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
        }
        updateSimTimeOverride();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.agentCircuit.unsubscribe();
        this.voiceActiveChatter.unsubscribe();
        this.voiceChatInfo.unsubscribe();
        this.voiceStatusView.setChatterID((ChatterID) null);
        super.onStop();
    }

    @OnClick({2131755758})
    public void onStopFlyingButton() {
        if (this.avatarControl != null) {
            this.avatarControl.stopFlying();
            updateObjectPanel();
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.button_move_forward:
                moveTouchEvent(2, motionEvent);
                break;
            case R.id.button_turn_left:
                if (this.arrowsToTurn) {
                    if (this.avatarControl != null) {
                        if (this.manualCamMode && this.camButtonEnabled) {
                            if (motionEvent.getActionMasked() != 0) {
                                if (motionEvent.getActionMasked() == 1) {
                                    this.avatarControl.stopCameraManualControl();
                                    break;
                                }
                            } else {
                                this.avatarControl.startCameraManualControl(TURNING_SPEED, 0.0f, 0.0f, 0.0f);
                                break;
                            }
                        } else if (motionEvent.getActionMasked() != 0) {
                            if (motionEvent.getActionMasked() == 1) {
                                this.avatarControl.stopTurning();
                                break;
                            }
                        } else {
                            this.avatarControl.startTurning(TURNING_SPEED);
                            break;
                        }
                    }
                } else {
                    moveTouchEvent(32, motionEvent);
                    break;
                }
                break;
            case R.id.button_move_backward:
                moveTouchEvent(4, motionEvent);
                break;
            case R.id.button_turn_right:
                if (this.arrowsToTurn) {
                    if (this.avatarControl != null) {
                        if (this.manualCamMode && this.camButtonEnabled) {
                            if (motionEvent.getActionMasked() != 0) {
                                if (motionEvent.getActionMasked() == 1) {
                                    this.avatarControl.stopCameraManualControl();
                                    break;
                                }
                            } else {
                                this.avatarControl.startCameraManualControl(-50.0f, 0.0f, 0.0f, 0.0f);
                                break;
                            }
                        } else if (motionEvent.getActionMasked() != 0) {
                            if (motionEvent.getActionMasked() == 1) {
                                this.avatarControl.stopTurning();
                                break;
                            }
                        } else {
                            this.avatarControl.startTurning(-50.0f);
                            break;
                        }
                    }
                } else {
                    moveTouchEvent(64, motionEvent);
                    break;
                }
                break;
            case R.id.button_fly_upward:
                moveTouchEvent(8, motionEvent);
                break;
            case R.id.button_fly_downward:
                moveTouchEvent(16, motionEvent);
                break;
        }
        return false;
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        Debug.Printf("ButtonsFade: some user interaction", new Object[0]);
        this.mHandler.post(this.buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
    }

    public void processScreenshot(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "screenshots");
            file.mkdirs();
            File file2 = new File(file, "Lumiya-" + new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US).format(new Date()) + ".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.close();
            Uri uriForFile = FileProvider.getUriForFile(this, "com.lumiyaviewer.files", file2);
            SLAgentCircuit data = this.agentCircuit.getData();
            String agentSLURL = data != null ? data.getAgentSLURL() : null;
            String string = agentSLURL != null ? getString(R.string.screenshot_taken_slurl, new Object[]{agentSLURL}) : getString(R.string.screenshot_taken_lumiya);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.TEXT", string);
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.putExtra("android.intent.extra.SUBJECT", "Screenshot from Lumiya");
            intent.setType("image/jpeg");
            intent.setFlags(1);
            startActivity(Intent.createChooser(intent, getString(R.string.export_screenshot_to)));
        } catch (Exception e) {
            Toast.makeText(this, R.string.failed_to_make_screenshot, 0).show();
        }
    }

    public void rendererAdvancedRenderingChanged() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"CommitPrefEdits"})
    public void rendererShaderCompileError() {
        Toast.makeText(this, "Advanced rendering is not available on your hardware. Falling back to basic rendering.", 1).show();
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        edit.putBoolean("advanced_rendering", false);
        edit.commit();
        finish();
        startActivity(new Intent(this, getClass()));
    }

    /* access modifiers changed from: package-private */
    public void rendererSurfaceCreated() {
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance);
        }
    }

    public void setLastTouchUUID(UUID uuid) {
        this.lastTouchUUID = uuid;
    }

    public void setTouchedObject(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo != null) {
            this.lastTouchUUID = sLObjectInfo.getId();
            if (this.lastTouchUUID != null) {
                Debug.Log("Touch: Last touched object set to " + this.lastTouchUUID);
            }
        }
    }

    @Nullable
    public Fragment showDetailsFragment(Class<? extends Fragment> cls, Intent intent, Bundle bundle) {
        this.detailsContainer.setVisibility(0);
        if (!this.isSplitScreen) {
            this.worldOverlaysContainer.setVisibility(8);
            this.voiceStatusView.disableMic();
        }
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.clearChatEvents();
        }
        return super.showDetailsFragment(cls, intent, bundle);
    }
}
