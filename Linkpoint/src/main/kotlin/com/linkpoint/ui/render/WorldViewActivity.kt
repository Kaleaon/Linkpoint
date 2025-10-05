package com.linkpoint.ui.render

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GestureDetectorCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.util.TypedValue
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.eventbus.Subscribe
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.events.SLBakingProgressEvent
import com.linkpoint.slproto.modules.SLAvatarControl
import com.linkpoint.slproto.modules.SLDrawDistance
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.slproto.users.manager.ActiveChattersManager
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.MyAvatarState
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.slproto.users.manager.UnreadNotificationManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.ThemeMapper
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.ContactsFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.MasterDetailsActivity
import com.linkpoint.ui.common.ScriptDialogHandler
import com.linkpoint.ui.objects.ObjectDetailsFragment
import com.linkpoint.ui.objects.ObjectPayDialog
import com.linkpoint.ui.objects.TouchableObjectsFragment
import com.linkpoint.ui.outfits.OutfitsFragment
import com.linkpoint.ui.voice.VoiceStatusView
import com.linkpoint.voice.common.model.VoiceChatInfo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.List
import java.util.Locale
import java.util.NoSuchElementException
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class WorldViewActivity : DetailsActivity(), View.OnTouchListener, ThemeMapper, ScriptDialogHandler, UnreadNotificationManager.NotifyCapture {
    private val BUTTONS_FADE_TIMEOUT_MILLIS: Long = 7500
    private val FROM_NOTIFICATION_TAG: String = "fromNotification"
    private val OBJECT_DESELECT_TIMEOUT_MILLIS: Long = 6000
    private val PERMISSION_AUDIO_REQUEST_CODE: Int = 100
    private val TURNING_SPEED: Float = 50.0f
    /* access modifiers changed from: private */
    val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f535$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.3.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.3.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private Boolean arrowsToTurn = false
    /* access modifiers changed from: private */
    public SLAvatarControl avatarControl
    @BindView(2131755576)
    ImageView avatarIconView
    @BindView(2131755762)
    ImageButton buttonCamOff
    @BindView(2131755761)
    ImageButton buttonCamOn
    @BindView(2131755757)
    ImageButton buttonFlyDownward
    @BindView(2131755756)
    ImageButton buttonFlyUpward
    @BindView(2131755763)
    Button buttonHUD
    @BindView(2131755262)
    ImageButton buttonMoveBackward
    @BindView(2131755260)
    ImageButton buttonMoveForward
    @BindView(2131755264)
    ImageButton buttonStandUp
    @BindView(2131755758)
    ImageButton buttonStopFlying
    @BindView(2131755261)
    ImageButton buttonTurnLeft
    @BindView(2131755263)
    ImageButton buttonTurnRight
    /* access modifiers changed from: private */
    public ValueAnimator buttonsFadeAnimator = null
    /* access modifiers changed from: private */
    val Runnable buttonsFadeTask = Runnable() {
        public Unit run() {
            Boolean unused = WorldViewActivity.this.buttonsFadeTimerStarted = false
            if (!WorldViewActivity.this.detailsVisible() && (!WorldViewActivity.this.isDragging) && WorldViewActivity.this.agentCircuit.hasData()) {
                VoiceChatInfo voiceChatInfo = (VoiceChatInfo) WorldViewActivity.this.voiceChatInfo.getData()
                if (!((voiceChatInfo == null || voiceChatInfo.state != VoiceChatInfo.VoiceChatState.Active) ? false : voiceChatInfo.localMicActive)) {
                    Long r2 = (WorldViewActivity.this.lastActivityTime + WorldViewActivity.BUTTONS_FADE_TIMEOUT_MILLIS) - SystemClock.uptimeMillis()
                    Debug.Printf("ButtonsFade: remaining %d", Long.valueOf(r2))
                    if (r2 <= 0) {
                        WorldViewActivity.this.startFadingButtons()
                        return
                    }
                    Boolean unused2 = WorldViewActivity.this.buttonsFadeTimerStarted = true
                    WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.buttonsFadeTask, r2)
                }
            }
        }
    }
    /* access modifiers changed from: private */
    public Boolean buttonsFadeTimerStarted = false
    private val Runnable buttonsRestoreTask = Runnable() {
        public Unit run() {
            if (Build.VERSION.SDK_INT >= 11) {
                if (WorldViewActivity.this.buttonsFadeAnimator != null) {
                    WorldViewActivity.this.buttonsFadeAnimator.cancel()
                }
                WorldViewActivity.this.insetsBackground.setAlpha(1.0f)
            }
        }
    }
    private Boolean camButtonEnabled = false
    private val Object chatEventHandler = Object() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity$1_50098  reason: not valid java name */
        public /* synthetic */ Unit m846lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity$1_50098(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            WorldViewActivity.this.handleChatEvent(chatMessageEvent)
        }

        @Subscribe
        public Unit onChatMessage(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            WorldViewActivity.this.mHandler.post(Runnable(this, chatMessageEvent) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f532$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f533$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.11.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.11.$m$0():Unit, class status: UNLOADED
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

        }
    }
    private Boolean chatOver3D = false
    @BindView(2131755759)
    LinearLayout chatsOverlayLayout
    private val SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f538$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.6.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.6.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755284)
    View detailsContainer
    /* access modifiers changed from: private */
    public Int displayedHUDid = 0
    @BindView(2131755748)
    View dragPointer
    @BindView(2131755747)
    ViewGroup dragPointerLayout
    private SLDrawDistance drawDistance
    private FadingTextViewLog fadingTextViewLog
    @BindView(2131755755)
    LinearLayout flyButtonsLayout
    /* access modifiers changed from: private */
    public GestureDetectorCompat gestureDetector
    private val GestureDetector.OnGestureListener gestureListener = GestureDetector.SimpleOnGestureListener() {
        public Boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, Float f, Float f2) {
            if (WorldViewActivity.this.isInScaling || !(!WorldViewActivity.this.wasInScaling) || !(!WorldViewActivity.this.isDragging)) {
                return false
            }
            Float height = (f * 60.0f) / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())
            Float height2 = ((-f2) * 60.0f) / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())
            if (WorldViewActivity.this.avatarControl == null) {
                return true
            }
            WorldViewActivity.this.avatarControl.processCameraFling(height / 1.5f, height2 / 2.5f)
            return true
        }

        public Unit onLongPress(MotionEvent motionEvent) {
            Float rawX = motionEvent.getRawX()
            Float rawY = motionEvent.getRawY()
            if (WorldViewActivity.this.isDragging) {
                WorldViewActivity.this.dragSelectorSetRawPosition((Int) rawX, (Int) rawY)
            } else if (!WorldViewActivity.this.isInScaling && (!WorldViewActivity.this.wasInScaling)) {
                Int[] iArr = Int[2]
                WorldViewActivity.this.worldViewHolder.getLocationOnScreen(iArr)
                WorldViewActivity.this.mGLView.pickObjectHover(rawX - ((Float) iArr[0]), rawY - ((Float) iArr[1]))
            }
        }

        public Boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, Float f, Float f2) {
            if (WorldViewActivity.this.isDragging) {
                AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) WorldViewActivity.this.dragPointer.getLayoutParams()
                if (layoutParams != null) {
                    layoutParams.x = Math.max(Math.min((Int) (((Float) layoutParams.x) - f), WorldViewActivity.this.dragPointerLayout.getWidth() - WorldViewActivity.this.dragPointer.getWidth()), 0)
                    layoutParams.y = Math.max(Math.min((Int) (((Float) layoutParams.y) - f2), WorldViewActivity.this.dragPointerLayout.getHeight() - WorldViewActivity.this.dragPointer.getHeight()), 0)
                    WorldViewActivity.this.dragPointer.setLayoutParams(layoutParams)
                    WorldViewActivity.this.selectByDragPointer(layoutParams.x, layoutParams.y)
                }
                return true
            } else if (WorldViewActivity.this.isInScaling || !(!WorldViewActivity.this.wasInScaling)) {
                return false
            } else {
                if (WorldViewActivity.this.displayedHUDid != 0) {
                    WorldViewActivity worldViewActivity = WorldViewActivity.this
                    Float unused = worldViewActivity.hudOffsetX = worldViewActivity.hudOffsetX + ((f / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())) / 2.0f)
                    WorldViewActivity worldViewActivity2 = WorldViewActivity.this
                    Float unused2 = worldViewActivity2.hudOffsetY = worldViewActivity2.hudOffsetY + ((f2 / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())) / 2.0f)
                    WorldViewActivity.this.mGLView.setHUDOffset(WorldViewActivity.this.hudOffsetX, WorldViewActivity.this.hudOffsetY)
                } else {
                    Float height = ((-f) * 60.0f) / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())
                    Float height2 = (f2 * 60.0f) / ((Float) WorldViewActivity.this.worldViewHolder.getHeight())
                    if (WorldViewActivity.this.avatarControl != null) {
                        WorldViewActivity.this.avatarControl.processCameraRotate(height, height2)
                    }
                }
                return true
            }
        }

        public Boolean onSingleTapUp(MotionEvent motionEvent) {
            if (WorldViewActivity.this.isDragging) {
                WorldViewActivity.this.dragSelectorSetRawPosition((Int) motionEvent.getRawX(), (Int) motionEvent.getRawY())
            } else if (WorldViewActivity.this.displayedHUDid != 0) {
                Int[] iArr = Int[2]
                WorldViewActivity.this.worldViewHolder.getLocationOnScreen(iArr)
                WorldViewActivity.this.mGLView.touchHUD(motionEvent.getRawX() - ((Float) iArr[0]), motionEvent.getRawY() - ((Float) iArr[1]))
            } else {
                WorldViewActivity.this.handlePickedObject((ObjectIntersectInfo) null)
            }
            return true
        }
    }
    /* access modifiers changed from: private */
    public Float hudOffsetX = 0.0f
    /* access modifiers changed from: private */
    public Float hudOffsetY = 0.0f
    /* access modifiers changed from: private */
    public Float hudScaleFactor = 1.0f
    @BindView(2131755744)
    FrameLayout insetsBackground
    /* access modifiers changed from: private */
    public Boolean isDragging = false
    /* access modifiers changed from: private */
    public Boolean isInScaling = false
    /* access modifiers changed from: private */
    public Boolean isInteracting = false
    private Boolean isSplitScreen
    /* access modifiers changed from: private */
    public Long lastActivityTime = SystemClock.uptimeMillis()
    /* access modifiers changed from: private */
    public Long lastObjectActivityTime = SystemClock.uptimeMillis()
    private UUID lastTouchUUID = null
    private Boolean localDrawingEnabled = false
    /* access modifiers changed from: private */
    public WorldSurfaceView mGLView
    /* access modifiers changed from: private */
    public Handler mHandler = Handler()
    private Boolean manualCamMode = false
    @BindView(2131755259)
    View moveButtonsLayout
    private val SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f536$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.4.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.4.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755270)
    ImageButton objectChatButton
    @BindView(2131755753)
    View objectControlsPanel
    /* access modifiers changed from: private */
    public Boolean objectDeselectTimerStarted = false
    /* access modifiers changed from: private */
    val Runnable objectDeselectTimerTask = Runnable() {
        public Unit run() {
            Boolean unused = WorldViewActivity.this.objectDeselectTimerStarted = false
            if (!WorldViewActivity.this.detailsVisible() && (!WorldViewActivity.this.isDragging)) {
                Long r0 = (WorldViewActivity.this.lastObjectActivityTime + WorldViewActivity.OBJECT_DESELECT_TIMEOUT_MILLIS) - SystemClock.uptimeMillis()
                Debug.Printf("ObjectDeselect: remaining %d", Long.valueOf(r0))
                if (r0 <= 0) {
                    WorldViewActivity.this.handlePickedObject((ObjectIntersectInfo) null)
                    return
                }
                Boolean unused2 = WorldViewActivity.this.objectDeselectTimerStarted = true
                WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.objectDeselectTimerTask, r0)
            }
        }
    }
    @BindView(2131755754)
    ImageButton objectMoreButton
    @BindView(2131755577)
    TextView objectNameTextView
    @BindView(2131755554)
    ImageButton objectPayButton
    @BindView(2131755764)
    View objectPopupLeftSpacer
    @BindView(2131755269)
    ImageButton objectSitButton
    @BindView(2131755538)
    ImageButton objectStandButton
    @BindView(2131755268)
    ImageButton objectTouchButton
    /* access modifiers changed from: private */
    public Float oldScaleFocusX = Float.NaN
    /* access modifiers changed from: private */
    public Float oldScaleFocusY = Float.NaN
    private ChatterNameRetriever pickedAvatarNameRetriever = null
    private ObjectIntersectInfo pickedIntersectInfo = null
    private SLObjectInfo pickedObject = null
    private Int prefDrawDistance = 20
    private Int prevDisplayedHUDid = 0
    /* access modifiers changed from: private */
    public ScaleGestureDetector scaleGestureDetector
    private val ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = ScaleGestureDetector.SimpleOnScaleGestureListener() {
        public Boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            Debug.Printf("Gesture: scale factor: %f", Float.valueOf(scaleGestureDetector.getScaleFactor()))
            if (WorldViewActivity.this.displayedHUDid != 0) {
                Float unused = WorldViewActivity.this.hudScaleFactor = Math.max(0.1f, Math.min(WorldViewActivity.this.hudScaleFactor * scaleGestureDetector.getScaleFactor(), 10.0f))
                WorldViewActivity.this.mGLView.setHUDScaleFactor(WorldViewActivity.this.hudScaleFactor)
            } else {
                Float width = (Float) WorldViewActivity.this.worldViewTouchReceiver.getWidth()
                Float height = (Float) WorldViewActivity.this.worldViewTouchReceiver.getHeight()
                Float focusX = scaleGestureDetector.getFocusX()
                Float focusY = scaleGestureDetector.getFocusY()
                Float f = ((focusX / width) - 0.5f) * (height / width)
                Float f2 = (focusY / height) - 0.5f
                Float r4 = (focusX - WorldViewActivity.this.oldScaleFocusX) / height
                Float r5 = (focusY - WorldViewActivity.this.oldScaleFocusY) / height
                Float unused2 = WorldViewActivity.this.oldScaleFocusX = focusX
                Float unused3 = WorldViewActivity.this.oldScaleFocusY = focusY
                if (WorldViewActivity.this.avatarControl != null) {
                    WorldViewActivity.this.avatarControl.processCameraZoom(scaleGestureDetector.getScaleFactor(), (-f) * 2.0f, (-f2) * 2.0f, r4, r5)
                }
            }
            return true
        }

        public Boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Boolean unused = WorldViewActivity.this.isInScaling = true
            Float unused2 = WorldViewActivity.this.oldScaleFocusX = scaleGestureDetector.getFocusX()
            Float unused3 = WorldViewActivity.this.oldScaleFocusY = scaleGestureDetector.getFocusY()
            return true
        }

        public Unit onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            Boolean unused = WorldViewActivity.this.isInScaling = false
        }
    }
    private val SubscriptionData<Integer, SLObjectProfileData> selectedObjectProfile = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f537$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.5.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.5.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private UserManager userManager
    private val SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f539$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.7.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.7.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    /* access modifiers changed from: private */
    val SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f540$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.8.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.8.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755752)
    VoiceStatusView voiceStatusView
    /* access modifiers changed from: private */
    public Boolean wasInScaling = false
    @BindView(2131755750)
    ViewGroup worldOverlaysContainer
    @BindView(2131755743)
    FrameLayout worldViewHolder
    private val View.OnTouchListener worldViewTouchListener = View.OnTouchListener() {
        public Boolean onTouch(View view, MotionEvent motionEvent) {
            Boolean r3 = WorldViewActivity.this.isInteracting
            switch (motionEvent.getActionMasked()) {
                case 0:
                    Boolean unused = WorldViewActivity.this.isInteracting = true
                    z = true
                    break
                case 1:
                    Boolean unused2 = WorldViewActivity.this.isInteracting = false
                    z = true
                    break
                default:
                    z = false
                    break
            }
            if (WorldViewActivity.this.isInteracting && (!r3)) {
                WorldViewActivity.this.mGLView.setIsInteracting(true)
            }
            Boolean unused3 = WorldViewActivity.this.wasInScaling = WorldViewActivity.this.isInScaling
            Boolean onTouchEvent = z | WorldViewActivity.this.scaleGestureDetector.onTouchEvent(motionEvent) | WorldViewActivity.this.gestureDetector.onTouchEvent(motionEvent)
            if (r3 && (!WorldViewActivity.this.isInteracting)) {
                WorldViewActivity.this.mGLView.setIsInteracting(false)
            }
            return onTouchEvent
        }
    }
    @BindView(2131755746)
    View worldViewTouchReceiver

    @JvmStatic
private class SelectableAttachment {
        private String attachmentName
        private Int localID

        public SelectableAttachment(Int i, String str) {
            this.localID = i
            this.attachmentName = str
        }

        public Int getLocalID() {
            return this.localID
        }

        public String toString() {
            return this.attachmentName
        }
    }

    private Unit beginCountingButtonsFade() {
        this.lastActivityTime = SystemClock.uptimeMillis()
        this.lastObjectActivityTime = this.lastActivityTime
        startFadingButtonsTimer()
    }

    private Unit beginCountingObjectDeselect() {
        if (this.pickedObject != null) {
            this.lastObjectActivityTime = SystemClock.uptimeMillis()
            if (!this.objectDeselectTimerStarted) {
                this.objectDeselectTimerStarted = true
                this.mHandler.postDelayed(this.objectDeselectTimerTask, OBJECT_DESELECT_TIMEOUT_MILLIS)
            }
        }
    }

    private Unit beginDragSelection() {
        this.isDragging = true
        removeAllDetails()
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) this.dragPointer.getLayoutParams()
        layoutParams.x = (this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()) / 2
        layoutParams.y = (this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()) / 2
        this.dragPointer.setLayoutParams(layoutParams)
        selectByDragPointer(layoutParams.x, layoutParams.y)
        this.mGLView.setOwnAvatarHidden(true)
        updateObjectPanel()
    }

    private Unit chatWithObject(SLObjectInfo sLObjectInfo) {
        if ((sLObjectInfo instanceof SLObjectAvatarInfo) && !((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() && sLObjectInfo.getId() != null) {
            DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, ChatFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId())))
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.findFragmentById(com.lumiyaviewer.lumiya.R.id.details)
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Boolean detailsVisible() {
        /*
            r2 = this
            android.support.v4.app.FragmentManager r0 = r2.getSupportFragmentManager()
            if (r0 == 0) goto L_0x0017
            r1 = 2131755284(0x7f100114, Float:1.9141443E38)
            android.support.v4.app.Fragment r0 = r0.findFragmentById(r1)
            if (r0 == 0) goto L_0x0017
            Boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0017
            r0 = 1
            return r0
        L_0x0017:
            r0 = 0
            return r0
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.render.WorldViewActivity.detailsVisible():Boolean")
    }

    private Unit displayHUD(Int i) {
        Debug.Printf("Displaying HUD with ID %d", Integer.valueOf(i))
        this.displayedHUDid = i
        this.mGLView.setDisplayedHUDid(i)
        if (this.displayedHUDid != this.prevDisplayedHUDid) {
            this.hudScaleFactor = 1.0f
            this.hudOffsetX = 0.0f
            this.hudOffsetY = 0.0f
            this.prevDisplayedHUDid = this.displayedHUDid
        }
        this.mGLView.setHUDScaleFactor(this.hudScaleFactor)
        this.mGLView.setHUDOffset(this.hudOffsetX, this.hudOffsetY)
        if (this.displayedHUDid != 0) {
            handlePickedObject((ObjectIntersectInfo) null)
        }
        updateObjectPanel()
    }

    /* access modifiers changed from: private */
    public Unit dragSelectorSetRawPosition(Int i, Int i2) {
        Int[] iArr = Int[2]
        this.dragPointerLayout.getLocationOnScreen(iArr)
        Int width = i - (this.dragPointer.getWidth() / 2)
        Int height = i2 - (this.dragPointer.getHeight() / 2)
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) this.dragPointer.getLayoutParams()
        if (layoutParams != null) {
            layoutParams.x = Math.max(Math.min(width - iArr[0], this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()), 0)
            layoutParams.y = Math.max(Math.min(height - iArr[1], this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()), 0)
            this.dragPointer.setLayoutParams(layoutParams)
            selectByDragPointer(layoutParams.x, layoutParams.y)
        }
    }

    private Unit endDragSelection() {
        this.isDragging = false
        this.mGLView.setOwnAvatarHidden(false)
        updateObjectPanel()
        beginCountingButtonsFade()
        beginCountingObjectDeselect()
    }

    private Unit enterCardboardView() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            Debug.Printf("Cardboard: audio permission not yet granted", Object[0])
            ActivityCompat.requestPermissions(this, String[]{"android.permission.RECORD_AUDIO"}, 100)
            return
        }
        Debug.Printf("Cardboard: audio permission already granted", Object[0])
        startCardboardActivity()
    }

    private Unit initContentView() {
        setContentView((Int) R.layout.world_view)
        ButterKnife.bind((Activity) this)
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar))
        this.mGLView = WorldSurfaceView(this, this.userManager)
        this.worldViewHolder.addView(this.mGLView)
        this.buttonMoveForward.setOnTouchListener(this)
        this.buttonMoveBackward.setOnTouchListener(this)
        this.buttonTurnLeft.setOnTouchListener(this)
        this.buttonTurnRight.setOnTouchListener(this)
        this.buttonMoveForward.setFocusable(false)
        this.buttonMoveBackward.setFocusable(false)
        this.buttonTurnLeft.setFocusable(false)
        this.buttonTurnRight.setFocusable(false)
        this.buttonFlyUpward.setOnTouchListener(this)
        this.buttonFlyDownward.setOnTouchListener(this)
        this.buttonFlyUpward.setFocusable(false)
        this.buttonFlyDownward.setFocusable(false)
        this.voiceStatusView.setShowActiveChatterName(true)
        this.worldViewTouchReceiver.setOnTouchListener(this.worldViewTouchListener)
        this.objectControlsPanel.setVisibility(8)
        View findViewById = findViewById(R.id.offline_notify_status_layout)
        if (findViewById != null) {
            findViewById.setBackgroundColor(Color.argb(128, 0, 0, 0))
            Int applyDimension = (Int) TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics())
            findViewById.setPadding(applyDimension, applyDimension, applyDimension, applyDimension)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public Unit m836com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref0(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit != null) {
            this.avatarControl = sLAgentCircuit.getModules().avatarControl
            this.drawDistance = sLAgentCircuit.getModules().drawDistance
            if (this.localDrawingEnabled) {
                this.drawDistance.Enable3DView(this.prefDrawDistance)
            }
            if (this.camButtonEnabled) {
                this.manualCamMode = this.avatarControl.getIsManualCamming()
            }
        } else {
            handlePickedObject((ObjectIntersectInfo) null)
            this.avatarControl = null
            this.drawDistance = null
        }
        this.mHandler.post(this.buttonsRestoreTask)
        beginCountingButtonsFade()
        beginCountingObjectDeselect()
        updateObjectPanel()
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocation */
    public Unit m839com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref3(CurrentLocationInfo currentLocationInfo2) {
        ParcelData parcelData = currentLocationInfo2 != null ? currentLocationInfo2.parcelData() : null
        String name = parcelData != null ? parcelData.getName() : null
        if (name == null) {
            name = getString(R.string.name_loading_title)
        }
        setDefaultTitle(name, (String) null)
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyAvatarState */
    public Unit m837com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref1(MyAvatarState myAvatarState2) {
        updateObjectPanel()
    }

    /* access modifiers changed from: private */
    /* renamed from: onPickedAvatarNameUpdated */
    public Unit m842com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref6(ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.pickedAvatarNameRetriever) {
            updateObjectPanel()
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onSelectedObjectProfile */
    public Unit m838com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref2(SLObjectProfileData sLObjectProfileData) {
        Debug.Printf("got selected object profile: %s", sLObjectProfileData)
        updateObjectPanel()
        if (sLObjectProfileData != null) {
            SLAgentCircuit data = this.agentCircuit.getData()
            if (sLObjectProfileData.isPayable() && sLObjectProfileData.payInfo() == null && data != null) {
                data.DoRequestPayPrice(sLObjectProfileData.objectUUID())
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceActiveChatter */
    public Unit m840com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref4(ChatterID chatterID) {
        if (this.voiceStatusView != null) {
            this.voiceStatusView.setChatterID(chatterID)
        }
        if (chatterID == null || this.userManager == null) {
            this.voiceChatInfo.unsubscribe()
        } else {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    public Unit m841com_lumiyaviewer_lumiya_ui_render_WorldViewActivitymthref5(VoiceChatInfo voiceChatInfo2) {
    }

    /* access modifiers changed from: private */
    public Unit selectByDragPointer(Int i, Int i2) {
        Int[] iArr = Int[2]
        this.dragPointerLayout.getLocationOnScreen(iArr)
        Int[] iArr2 = Int[2]
        this.worldViewHolder.getLocationOnScreen(iArr2)
        this.mGLView.pickObjectHover((Float) (((iArr[0] + (this.dragPointer.getWidth() / 2)) + i) - iArr2[0]), (Float) (((iArr[1] + (this.dragPointer.getHeight() / 2)) + i2) - iArr2[1]))
    }

    private Unit selectHUDtoDisplay() {
        SLAttachmentPoint sLAttachmentPoint
        ArrayList arrayList = ArrayList()
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null) {
            SLObjectAvatarInfo agentAvatar = data.getGridConnection().parcelInfo.getAgentAvatar()
            if (agentAvatar != null) {
                try {
                    for (SLObjectInfo sLObjectInfo : agentAvatar.treeNode) {
                        if (!Strings.nullToEmpty(sLObjectInfo.getName()).startsWith("#") && (i = sLObjectInfo.attachmentID) >= 0 && i < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]) != null && sLAttachmentPoint.isHUD) {
                            arrayList.add(SelectableAttachment(sLObjectInfo.localID, sLObjectInfo.name))
                        }
                    }
                } catch (NoSuchElementException e) {
                    Debug.Warning(e)
                }
            }
            if (arrayList.isEmpty()) {
                return
            }
            if (arrayList.size() == 1) {
                displayHUD(((SelectableAttachment) arrayList.get(0)).getLocalID())
                return
            }
            ArrayAdapter arrayAdapter = ArrayAdapter(this, 17367043, arrayList)
            AlertDialog.Builder builder = AlertDialog.Builder(this)
            builder.setTitle((Int) R.string.select_hud_title)
            builder.setAdapter(arrayAdapter, DialogInterface.OnClickListener(this, arrayList) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f530$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f531$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.10.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.10.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            builder.create().show()
        }
    }

    private Unit showObjectInfo(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo.getId() != null && this.userManager != null) {
            if (!sLObjectInfo.isAvatar()) {
                DetailsActivity.showEmbeddedDetails(this, ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), sLObjectInfo.localID))
            } else if (sLObjectInfo instanceof SLObjectAvatarInfo) {
                DetailsActivity.showEmbeddedDetails(this, UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId())))
            }
        }
    }

    private Unit startCardboardActivity() {
        if (this.userManager != null) {
            Intent intent = Intent(this, CardboardTransitionActivity.class)
            ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID())
            intent.addFlags(16777216)
            startActivity(intent)
        }
        finish()
    }

    /* access modifiers changed from: private */
    public Unit startFadingButtons() {
        if (Build.VERSION.SDK_INT >= 11 && this.buttonsFadeAnimator != null) {
            this.buttonsFadeAnimator.start()
        }
    }

    private Unit startFadingButtonsTimer() {
        if (!this.buttonsFadeTimerStarted) {
            Debug.Printf("ButtonsFade: starting timer", Object[0])
            this.buttonsFadeTimerStarted = true
            this.mHandler.postDelayed(this.buttonsFadeTask, BUTTONS_FADE_TIMEOUT_MILLIS)
        }
    }

    private Unit stopAvatarAnimations() {
        if (this.avatarControl != null) {
            this.avatarControl.StopAvatarAnimations()
        }
    }

    private Unit takeScreenshot() {
        Toast.makeText(this, R.string.taking_screenshot, 0).show()
        this.mGLView.takeScreenshot()
    }

    private Unit touchObject(SLObjectInfo sLObjectInfo, ObjectIntersectInfo objectIntersectInfo) {
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null && sLObjectInfo != null) {
            if (!sLObjectInfo.isAvatar()) {
                this.lastTouchUUID = sLObjectInfo.getId()
                Boolean z = false
                if (objectIntersectInfo != null) {
                    z = objectIntersectInfo.intersectInfo.faceKnown
                }
                if (z) {
                    LLVector3 absolutePosition = sLObjectInfo.getAbsolutePosition()
                    data.TouchObjectFace(sLObjectInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t)
                    return
                }
                data.TouchObject(sLObjectInfo.localID)
            } else if (sLObjectInfo.hasTouchableChildren()) {
                DetailsActivity.showEmbeddedDetails(this, TouchableObjectsFragment.class, TouchableObjectsFragment.makeSelection(this.userManager.getUserID(), sLObjectInfo.getId()))
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        if ((!(r12.camButtonEnabled ? r12.manualCamMode : false)) == false) goto L_0x0094
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00af, code lost:
        if ((!(!r12.camButtonEnabled ? r12.manualCamMode : false)) != false) goto L_0x00b7
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
    private Unit updateObjectPanel() {
        /*
            r12 = this
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
            Boolean r2 = r0.isSitting()
            r7 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x014b
            Boolean r2 = r0.hasHUDs()
            r6 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x014f
            Boolean r2 = r0.isFlying()
            r5 = r2
        L_0x0029:
            if (r1 == 0) goto L_0x0153
            com.lumiyaviewer.lumiya.slproto.modules.SLModules r2 = r1.getModules()
            com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController r2 = r2.rlvController
            Boolean r2 = r2.canStandUp()
            r4 = r2
        L_0x0036:
            if (r1 == 0) goto L_0x0157
            com.lumiyaviewer.lumiya.slproto.modules.SLModules r1 = r1.getModules()
            com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController r1 = r1.rlvController
            Boolean r1 = r1.canSit()
        L_0x0042:
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            if (r2 == 0) goto L_0x015a
            r2 = 1
        L_0x0047:
            java.lang.String r3 = "isSitting %b, isFlying %b, hasHUDs %b, isDragging %b"
            r9 = 4
            java.lang.Object[] r9 = java.lang.Object[r9]
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r7)
            r11 = 0
            r9[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r5)
            r11 = 1
            r9[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r6)
            r11 = 2
            r9[r11] = r10
            Boolean r10 = r12.isDragging
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r10)
            r11 = 3
            r9[r11] = r10
            com.lumiyaviewer.lumiya.Debug.Printf(r3, r9)
            android.view.ViewGroup r9 = r12.dragPointerLayout
            Boolean r3 = r12.isDragging
            if (r3 == 0) goto L_0x015d
            r3 = 0
        L_0x0075:
            r9.setVisibility(r3)
            android.view.View r9 = r12.dragPointer
            Boolean r3 = r12.isDragging
            if (r3 == 0) goto L_0x0160
            r3 = 0
        L_0x007f:
            r9.setVisibility(r3)
            android.widget.LinearLayout r9 = r12.flyButtonsLayout
            if (r7 != 0) goto L_0x008a
            r3 = r8 ^ 1
            if (r3 == 0) goto L_0x0094
        L_0x008a:
            Boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0163
            Boolean r3 = r12.manualCamMode
        L_0x0090:
            r3 = r3 ^ 1
            if (r3 != 0) goto L_0x009a
        L_0x0094:
            Boolean r3 = r12.isDragging
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
            Boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0169
            Boolean r3 = r12.manualCamMode
        L_0x00ad:
            r3 = r3 ^ 1
            if (r3 != 0) goto L_0x00b7
        L_0x00b1:
            Boolean r3 = r12.isDragging
            if (r3 != 0) goto L_0x00b7
            if (r2 == 0) goto L_0x016c
        L_0x00b7:
            r3 = 4
        L_0x00b8:
            r9.setVisibility(r3)
            android.widget.ImageButton r9 = r12.buttonStandUp
            if (r4 == 0) goto L_0x016f
            if (r7 == 0) goto L_0x016f
            Boolean r3 = r12.isDragging
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x016f
            r3 = 0
        L_0x00c8:
            r9.setVisibility(r3)
            android.widget.Button r9 = r12.buttonHUD
            if (r6 == 0) goto L_0x0173
            Boolean r3 = r12.isDragging
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
            Boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0177
            Boolean r3 = r12.manualCamMode
            if (r3 == 0) goto L_0x0177
        L_0x00e9:
            r3 = 0
        L_0x00ea:
            r6.setVisibility(r3)
            android.widget.ImageButton r6 = r12.buttonStopFlying
            if (r5 == 0) goto L_0x017e
            if (r8 == 0) goto L_0x017e
            Boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x017b
            Boolean r3 = r12.manualCamMode
        L_0x00f9:
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x017e
            r3 = 0
        L_0x00fe:
            r6.setVisibility(r3)
            android.widget.ImageButton r5 = r12.buttonCamOn
            Boolean r3 = r12.camButtonEnabled
            if (r3 == 0) goto L_0x0182
            Boolean r3 = r12.manualCamMode
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x0182
            Boolean r3 = r12.isDragging
            r3 = r3 ^ 1
            if (r3 == 0) goto L_0x0182
            r3 = r2 ^ 1
            if (r3 == 0) goto L_0x0182
            r3 = 0
        L_0x0118:
            r5.setVisibility(r3)
            android.widget.ImageButton r3 = r12.buttonCamOff
            Boolean r5 = r12.camButtonEnabled
            if (r5 == 0) goto L_0x0185
            Boolean r5 = r12.manualCamMode
            if (r5 == 0) goto L_0x0185
            Boolean r5 = r12.isDragging
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
            Boolean r2 = r2.isTouchable()
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = r12.pickedObject
            Boolean r3 = r3.isAvatar()
            if (r3 == 0) goto L_0x01a3
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = r12.pickedObject
            Boolean r3 = r3.hasTouchableChildren()
            r2 = r2 | r3
        L_0x01a3:
            android.widget.ImageButton r3 = r12.objectTouchButton
            if (r2 == 0) goto L_0x021f
            r2 = 0
        L_0x01a8:
            r3.setVisibility(r2)
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            Boolean r3 = r2.isAvatar()
            if (r7 == 0) goto L_0x0222
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = r12.pickedObject
            Int r2 = r2.localID
            Int r0 = r0.sittingOn()
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
            Boolean r0 = r0.isPayable()
            if (r0 != 0) goto L_0x01f8
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            Byte r0 = r0.saleType
            if (r0 == 0) goto L_0x0234
        L_0x01f8:
            r0 = 0
        L_0x01f9:
            r1.setVisibility(r0)
            r1 = 0
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r12.pickedObject
            Boolean r0 = r0.isAvatar()
            if (r0 == 0) goto L_0x0237
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r0 = r12.pickedAvatarNameRetriever
            if (r0 == 0) goto L_0x0264
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r0 = r12.pickedAvatarNameRetriever
            java.lang.String r0 = r0.getResolvedName()
        L_0x020f:
            if (r0 != 0) goto L_0x0218
            r0 = 2131296825(0x7f090239, Float:1.8211578E38)
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
            Boolean r2 = com.google.common.base.Objects.equal(r2, r3)
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
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.render.WorldViewActivity.updateObjectPanel():Unit")
    }

    private Unit updateSimTimeOverride() {
        if (this.mGLView != null) {
            GlobalOptions instance = GlobalOptions.getInstance()
            this.mGLView.setForcedTime(instance.getForceDaylightTime(), instance.getForceDaylightHour())
        }
    }

    private Unit updateSplitScreenLayout() {
        Int i = 0
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.details)
        Object[] objArr = Object[3]
        objArr[0] = Boolean.valueOf(this.isSplitScreen)
        objArr[1] = Boolean.valueOf(findFragmentById != null)
        objArr[2] = Boolean.valueOf(findFragmentById != null ? findFragmentById.isDetached() : false)
        Debug.Printf("updateSplitScreenLayout: isSplitScreen now %b details has %b detached %b", objArr)
        if (findFragmentById == null || !(!findFragmentById.isDetached())) {
            this.worldOverlaysContainer.setVisibility(0)
        } else {
            this.detailsContainer.setVisibility(0)
            this.worldOverlaysContainer.setVisibility(this.isSplitScreen ? 0 : 8)
        }
        View view = this.objectPopupLeftSpacer
        if (!this.isSplitScreen) {
            i = 8
        }
        view.setVisibility(i)
    }

    public Boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.detailsContainer.getVisibility() == 0) {
            return super.dispatchKeyEvent(keyEvent)
        }
        switch (keyEvent.getKeyCode()) {
            case 19:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming()
                        this.avatarControl.StartAgentMotion(2)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion()
                    }
                }
                return true
            case 20:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming()
                        this.avatarControl.StartAgentMotion(4)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion()
                    }
                }
                return true
            case 21:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(TURNING_SPEED)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning()
                    }
                }
                return true
            case 22:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(-50.0f)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning()
                    }
                }
                return true
            case 92:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming()
                        this.avatarControl.StartAgentMotion(8)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion()
                    }
                }
                return true
            case 93:
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming()
                        this.avatarControl.StartAgentMotion(16)
                    } else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion()
                    }
                }
                return true
            default:
                return super.dispatchKeyEvent(keyEvent)
        }
    }

    @EventHandler
    public Unit handleBakingProgressEvent(SLBakingProgressEvent sLBakingProgressEvent) {
        if (sLBakingProgressEvent.first) {
            Toast.makeText(this, "Updating avatar appearance...", 0).show()
        }
    }

    public Unit handleChatEvent(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
        if (this.chatOver3D && !detailsVisible() && this.userManager != null && this.chatsOverlayLayout != null && this.fadingTextViewLog != null) {
            this.fadingTextViewLog.handleChatEvent(chatMessageEvent)
        }
    }

    public Unit handlePickedObject(ObjectIntersectInfo objectIntersectInfo) {
        this.pickedIntersectInfo = objectIntersectInfo
        this.pickedObject = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (this.pickedObject != null) {
            if (this.pickedObject.isAvatar()) {
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), this.pickedObject.getId())
                if (!Objects.equal(this.pickedAvatarNameRetriever != null ? this.pickedAvatarNameRetriever.chatterID : null, userChatterID)) {
                    if (this.pickedAvatarNameRetriever != null) {
                        this.pickedAvatarNameRetriever.dispose()
                        this.pickedAvatarNameRetriever = null
                    }
                    this.pickedAvatarNameRetriever = ChatterNameRetriever(userChatterID, ChatterNameRetriever.OnChatterNameUpdated(this) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f541$f0

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.9.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, class status: UNLOADED
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

                    }, UIThreadExecutor.getInstance())
                }
            } else {
                Debug.Printf("ObjectPick: picked object %d", Integer.valueOf(this.pickedObject.localID))
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(this.pickedObject.localID))
            }
            updateObjectPanel()
            beginCountingObjectDeselect()
        } else {
            this.selectedObjectProfile.unsubscribe()
            if (this.pickedAvatarNameRetriever != null) {
                this.pickedAvatarNameRetriever.dispose()
                this.pickedAvatarNameRetriever = null
            }
        }
        this.mGLView.setDrawPickedObject(this.pickedObject)
        updateObjectPanel()
    }

    /* access modifiers changed from: protected */
    public Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return cls == ContactsFragment.class || cls == ChatFragment.class || cls == ObjectDetailsFragment.class || cls == OutfitsFragment.class || cls == UserProfileFragment.class
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_10269  reason: not valid java name */
    public /* synthetic */ Unit m843lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_10269(ValueAnimator valueAnimator) {
        this.insetsBackground.setAlpha(1.0f - valueAnimator.getAnimatedFraction())
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43183  reason: not valid java name */
    public /* synthetic */ Unit m844lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43183(DialogInterface dialogInterface, Int i) {
        SLAgentCircuit activeAgentCircuit
        if (!(this.userManager == null || (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) == null || this.pickedObject == null)) {
            activeAgentCircuit.BuyObject(this.pickedObject.localID, this.pickedObject.saleType, this.pickedObject.salePrice)
        }
        dialogInterface.dismiss()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_48476  reason: not valid java name */
    public /* synthetic */ Unit m845lambda$com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_48476(List list, DialogInterface dialogInterface, Int i) {
        if (i >= 0 && i < list.size()) {
            displayHUD(((SelectableAttachment) list.get(i)).getLocalID())
        }
    }

    public Int mapThemeResourceId(Int i) {
        return i == 2131427372 ? R.style.Theme_Linkpoint_Light_NoActionBar : i == 2131427375 ? R.style.Theme_Linkpoint_Pink_NoActionBar : R.style.Theme_Linkpoint_NoActionBar
    }

    public Unit moveTouchEvent(Int i, MotionEvent motionEvent) {
        Float f = 1.0f
        Float f2 = -1.0f
        if (this.avatarControl == null) {
            return
        }
        if (motionEvent.getAction() == 0) {
            if (!this.manualCamMode || !this.camButtonEnabled) {
                this.avatarControl.stopCamming()
                this.avatarControl.StartAgentMotion(i)
                if ((i & 8) != 0 || (i & 16) != 0) {
                    updateObjectPanel()
                    return
                }
                return
            }
            Float f3 = (i & 16) != 0 ? -1.0f : (i & 8) != 0 ? 1.0f : 0.0f
            Float f4 = (i & 2) != 0 ? 1.0f : 0.0f
            if ((i & 4) != 0) {
                f4 = -1.0f
            }
            if ((i & 32) == 0) {
                f = 0.0f
            }
            if ((i & 64) == 0) {
                f2 = f
            }
            this.avatarControl.startCameraManualControl(0.0f, f4, f3, f2)
        } else if (motionEvent.getAction() != 1) {
        } else {
            if (!this.manualCamMode || !this.camButtonEnabled) {
                this.avatarControl.StopAgentMotion()
            } else {
                this.avatarControl.stopCameraManualControl()
            }
        }
    }

    @OnClick({2131755762})
    public Unit onCamOffButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = false
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(false)
            }
            updateObjectPanel()
        }
    }

    @OnClick({2131755761})
    public Unit onCamOnButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = true
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(true)
            }
            updateObjectPanel()
        }
    }

    public Unit onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration)
        this.isSplitScreen = LinkpointApp.isSplitScreenNeeded(this)
        updateSplitScreenLayout()
    }

    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        this.userManager = ActivityUtils.getUserManager(getIntent())
        if (this.userManager == null) {
            finish()
            return
        }
        this.isSplitScreen = LinkpointApp.isSplitScreenNeeded(this)
        this.scaleGestureDetector = ScaleGestureDetector(this, this.scaleGestureListener)
        if (Build.VERSION.SDK_INT >= 19) {
            this.scaleGestureDetector.setQuickScaleEnabled(true)
        }
        this.gestureDetector = GestureDetectorCompat(this, this.gestureListener)
        initContentView()
        this.fadingTextViewLog = FadingTextViewLog(this.userManager, this, this.chatsOverlayLayout, Color.rgb(192, 192, 192), Color.argb(160, 0, 0, 0))
        if (Build.VERSION.SDK_INT >= 12) {
            this.buttonsFadeAnimator = ValueAnimator.ofFloat(Float[]{0.0f, 1.0f})
            this.buttonsFadeAnimator.setDuration(1000)
            this.buttonsFadeAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener(this) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f529$f0

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.1.$m$0(android.animation.ValueAnimator):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.1.$m$0(android.animation.ValueAnimator):Unit, class status: UNLOADED
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

        }
        updateSplitScreenLayout()
        startFadingButtonsTimer()
    }

    public Boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu)
        getMenuInflater().inflate(R.menu.world_view_menu, menu)
        return true
    }

    /* access modifiers changed from: protected */
    public Boolean onDetailsStackEmpty() {
        if (super.onDetailsStackEmpty()) {
            return true
        }
        this.detailsContainer.setVisibility(8)
        if (!this.isSplitScreen) {
            this.worldOverlaysContainer.setVisibility(0)
        }
        beginCountingButtonsFade()
        beginCountingObjectDeselect()
        return false
    }

    public Intent onGetNotifyCaptureIntent(UnreadNotificationInfo unreadNotificationInfo, Intent intent) {
        Debug.Printf("NotifyCapture: returning intent", Object[0])
        Intent intent2 = Intent(this, WorldViewActivity.class)
        intent2.putExtras(intent)
        intent2.putExtra(FROM_NOTIFICATION_TAG, true)
        intent2.addFlags(536870912)
        return intent2
    }

    @EventHandler
    public Unit onGlobalOptionsChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        updateSimTimeOverride()
    }

    @OnClick({2131755763})
    public Unit onHUDButton() {
        if (this.displayedHUDid != 0) {
            this.displayedHUDid = 0
            this.mGLView.setDisplayedHUDid(this.displayedHUDid)
            updateObjectPanel()
            return
        }
        selectHUDtoDisplay()
    }

    /* access modifiers changed from: protected */
    public Unit onNewIntent(Intent intent) {
        super.onNewIntent(intent)
        Debug.Printf("NotifyCapture: got newIntent: %s", intent)
        this.mHandler.post(this.buttonsRestoreTask)
        beginCountingButtonsFade()
        beginCountingObjectDeselect()
        if (intent.hasExtra(FROM_NOTIFICATION_TAG)) {
            Bundle bundleExtra = intent.hasExtra(MasterDetailsActivity.INTENT_SELECTION_KEY) ? intent.getBundleExtra(MasterDetailsActivity.INTENT_SELECTION_KEY) : null
            if (bundleExtra != null) {
                DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, bundleExtra)
            } else if (this.userManager != null) {
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null))
            }
        }
    }

    @OnClick({2131755270})
    public Unit onObjectChatButton() {
        if (this.pickedObject != null) {
            chatWithObject(this.pickedObject)
        }
    }

    @OnClick({2131755754})
    public Unit onObjectMoreButton() {
        if (this.pickedObject != null) {
            showObjectInfo(this.pickedObject)
        }
    }

    @OnClick({2131755554})
    public Unit onObjectPayButton() {
        SLObjectProfileData data
        if (this.pickedObject != null) {
            String name = this.pickedObject.getName()
            if (Strings.isNullOrEmpty(name)) {
                name = getString(R.string.object_name_loading)
            }
            if (this.pickedObject.saleType != 0) {
                AlertDialog.Builder builder = AlertDialog.Builder(this)
                builder.setMessage((CharSequence) String.format(getString(R.string.object_buy_confirm), Object[]{name, Integer.valueOf(this.pickedObject.salePrice)})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) DialogInterface.OnClickListener(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f534$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.2.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8.2.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

                }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) $Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8())
                builder.show()
            } else if (this.pickedObject.isPayable() && (data = this.selectedObjectProfile.getData()) != null && this.userManager != null) {
                ObjectPayDialog.show(this, this.userManager, data)
            }
        }
    }

    @OnClick({2131755269})
    public Unit onObjectSitButton() {
        if (this.pickedObject != null && this.avatarControl != null) {
            this.avatarControl.SitOnObject(this.pickedObject.getId())
        }
    }

    @OnClick({2131755264, 2131755538})
    public Unit onObjectStandButton() {
        if (this.avatarControl != null) {
            this.avatarControl.Stand()
        }
    }

    @OnClick({2131755268})
    public Unit onObjectTouchButton() {
        if (this.pickedObject != null) {
            touchObject(this.pickedObject, this.pickedIntersectInfo)
        }
    }

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_chat:
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null))
                return true
            case R.id.item_outfits:
                DetailsActivity.showEmbeddedDetails(this, OutfitsFragment.class, OutfitsFragment.makeSelection(this.userManager.getUserID(), (UUID) null))
                return true
            case R.id.item_select_object:
                if (!this.isDragging) {
                    beginDragSelection()
                } else {
                    endDragSelection()
                }
                return true
            case R.id.item_cardboard_view:
                enterCardboardView()
                return true
            case R.id.item_take_screenshot:
                takeScreenshot()
                return true
            case R.id.item_stop_animations:
                stopAvatarAnimations()
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    public Unit onPause() {
        Debug.Printf("WorldViewActivity: onPause", Object[0])
        if (this.userManager != null) {
            this.userManager.getUnreadNotificationManager().clearNotifyCapture(this)
            try {
                this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(this.chatEventHandler)
            } catch (IllegalArgumentException e) {
            }
            if (this.fadingTextViewLog != null) {
                this.fadingTextViewLog.clearChatEvents()
            }
        }
        this.localDrawingEnabled = false
        if (this.drawDistance != null) {
            this.drawDistance.Disable3DView()
        }
        this.mGLView.onPause()
        this.mHandler.removeCallbacks(this.objectDeselectTimerTask)
        this.mHandler.removeCallbacks(this.buttonsFadeTask)
        this.objectDeselectTimerStarted = false
        this.buttonsFadeTimerStarted = false
        this.myAvatarState.unsubscribe()
        this.selectedObjectProfile.unsubscribe()
        this.currentLocationInfo.unsubscribe()
        System.gc()
        super.onPause()
    }

    public Boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(R.id.item_cardboard_view)
        if (findItem != null) {
            findItem.setVisible(Build.VERSION.SDK_INT >= 23)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    public Unit onRequestPermissionsResult(Int i, String[] strArr, Int[] iArr) {
        Debug.Printf("Cardboard: onRequestPermissionResult, code %d", Integer.valueOf(i))
        if (i == 100) {
            startCardboardActivity()
        }
    }

    public Unit onResume() {
        super.onResume()
        Debug.Printf("WorldViewActivity: onResume", Object[0])
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
        this.chatOver3D = defaultSharedPreferences.getBoolean("chat_over_3d", true)
        this.arrowsToTurn = defaultSharedPreferences.getBoolean("arrows_rotate_avatar", false)
        this.camButtonEnabled = defaultSharedPreferences.getBoolean("cam_button_enabled", false)
        if (this.userManager != null) {
            this.userManager.getUnreadNotificationManager().setNotifyCapture(this)
            this.myAvatarState.subscribe(this.userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value)
            this.currentLocationInfo.subscribe(this.userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value)
            this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(this.chatEventHandler)
        }
        RenderSettings renderSettings = RenderSettings(defaultSharedPreferences)
        this.prefDrawDistance = renderSettings.drawDistance
        this.localDrawingEnabled = true
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance)
        }
        if (this.camButtonEnabled && this.avatarControl != null) {
            this.manualCamMode = this.avatarControl.getIsManualCamming()
        }
        this.mGLView.setDrawDistance(this.prefDrawDistance)
        this.mGLView.setAvatarCountLimit(renderSettings.avatarCountLimit)
        this.mGLView.onResume()
        updateObjectPanel()
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.postRemovingStaleChats()
        }
    }

    /* access modifiers changed from: protected */
    public Unit onStart() {
        super.onStart()
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID())
            this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value)
        }
        updateSimTimeOverride()
    }

    /* access modifiers changed from: protected */
    public Unit onStop() {
        this.agentCircuit.unsubscribe()
        this.voiceActiveChatter.unsubscribe()
        this.voiceChatInfo.unsubscribe()
        this.voiceStatusView.setChatterID((ChatterID) null)
        super.onStop()
    }

    @OnClick({2131755758})
    public Unit onStopFlyingButton() {
        if (this.avatarControl != null) {
            this.avatarControl.stopFlying()
            updateObjectPanel()
        }
    }

    public Boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.button_move_forward:
                moveTouchEvent(2, motionEvent)
                break
            case R.id.button_turn_left:
                if (this.arrowsToTurn) {
                    if (this.avatarControl != null) {
                        if (this.manualCamMode && this.camButtonEnabled) {
                            if (motionEvent.getActionMasked() != 0) {
                                if (motionEvent.getActionMasked() == 1) {
                                    this.avatarControl.stopCameraManualControl()
                                    break
                                }
                            } else {
                                this.avatarControl.startCameraManualControl(TURNING_SPEED, 0.0f, 0.0f, 0.0f)
                                break
                            }
                        } else if (motionEvent.getActionMasked() != 0) {
                            if (motionEvent.getActionMasked() == 1) {
                                this.avatarControl.stopTurning()
                                break
                            }
                        } else {
                            this.avatarControl.startTurning(TURNING_SPEED)
                            break
                        }
                    }
                } else {
                    moveTouchEvent(32, motionEvent)
                    break
                }
                break
            case R.id.button_move_backward:
                moveTouchEvent(4, motionEvent)
                break
            case R.id.button_turn_right:
                if (this.arrowsToTurn) {
                    if (this.avatarControl != null) {
                        if (this.manualCamMode && this.camButtonEnabled) {
                            if (motionEvent.getActionMasked() != 0) {
                                if (motionEvent.getActionMasked() == 1) {
                                    this.avatarControl.stopCameraManualControl()
                                    break
                                }
                            } else {
                                this.avatarControl.startCameraManualControl(-50.0f, 0.0f, 0.0f, 0.0f)
                                break
                            }
                        } else if (motionEvent.getActionMasked() != 0) {
                            if (motionEvent.getActionMasked() == 1) {
                                this.avatarControl.stopTurning()
                                break
                            }
                        } else {
                            this.avatarControl.startTurning(-50.0f)
                            break
                        }
                    }
                } else {
                    moveTouchEvent(64, motionEvent)
                    break
                }
                break
            case R.id.button_fly_upward:
                moveTouchEvent(8, motionEvent)
                break
            case R.id.button_fly_downward:
                moveTouchEvent(16, motionEvent)
                break
        }
        return false
    }

    public Unit onUserInteraction() {
        super.onUserInteraction()
        Debug.Printf("ButtonsFade: some user interaction", Object[0])
        this.mHandler.post(this.buttonsRestoreTask)
        beginCountingButtonsFade()
        beginCountingObjectDeselect()
    }

    public Unit processScreenshot(Bitmap bitmap) {
        try {
            File file = File(getCacheDir(), "screenshots")
            file.mkdirs()
            File file2 = File(file, "Linkpoint-" + SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US).format(Date()) + ".jpg")
            FileOutputStream fileOutputStream = FileOutputStream(file2)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.close()
            Uri uriForFile = FileProvider.getUriForFile(this, "com.lumiyaviewer.files", file2)
            SLAgentCircuit data = this.agentCircuit.getData()
            String agentSLURL = data != null ? data.getAgentSLURL() : null
            String string = agentSLURL != null ? getString(R.string.screenshot_taken_slurl, Object[]{agentSLURL}) : getString(R.string.screenshot_taken_lumiya)
            Intent intent = Intent()
            intent.setAction("android.intent.action.SEND")
            intent.putExtra("android.intent.extra.TEXT", string)
            intent.putExtra("android.intent.extra.STREAM", uriForFile)
            intent.putExtra("android.intent.extra.SUBJECT", "Screenshot from Linkpoint")
            intent.setType("image/jpeg")
            intent.setFlags(1)
            startActivity(Intent.createChooser(intent, getString(R.string.export_screenshot_to)))
        } catch (Exception e) {
            Toast.makeText(this, R.string.failed_to_make_screenshot, 0).show()
        }
    }

    public Unit rendererAdvancedRenderingChanged() {
        Intent intent = getIntent()
        finish()
        startActivity(intent)
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"CommitPrefEdits"})
    public Unit rendererShaderCompileError() {
        Toast.makeText(this, "Advanced rendering is not available on your hardware. Falling back to basic rendering.", 1).show()
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit()
        edit.putBoolean("advanced_rendering", false)
        edit.commit()
        finish()
        startActivity(Intent(this, getClass()))
    }

    /* access modifiers changed from: package-private */
    public Unit rendererSurfaceCreated() {
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance)
        }
    }

    public Unit setLastTouchUUID(UUID uuid) {
        this.lastTouchUUID = uuid
    }

    public Unit setTouchedObject(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo != null) {
            this.lastTouchUUID = sLObjectInfo.getId()
            if (this.lastTouchUUID != null) {
                Debug.Log("Touch: Last touched object set to " + this.lastTouchUUID)
            }
        }
    }

    public Fragment showDetailsFragment(Class<? : Fragment> cls, Intent intent, Bundle bundle) {
        this.detailsContainer.setVisibility(0)
        if (!this.isSplitScreen) {
            this.worldOverlaysContainer.setVisibility(8)
            this.voiceStatusView.disableMic()
        }
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.clearChatEvents()
        }
        return super.showDetailsFragment(cls, intent, bundle)
    }
}
