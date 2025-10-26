package com.linkpoint.ui.render

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Rect
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.preference.PreferenceManager
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTouch
import com.google.common.base.Objects
import com.google.common.base.Predicate
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.eventbus.Subscribe
import com.google.common.util.concurrent.AtomicDouble
import com.google.vr.cardboard.FullscreenMode
import com.google.vr.sdk.base.AndroidCompat
import com.google.vr.sdk.base.Eye
import com.google.vr.sdk.base.GvrView
import com.google.vr.sdk.base.HeadTransform
import com.google.vr.sdk.base.Viewport
import com.google.vr.sdk.controller.Controller
import com.google.vr.sdk.controller.ControllerManager
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.render.HeadTransformCompat
import com.linkpoint.render.WorldViewRenderer
import com.linkpoint.render.glres.textures.GLExternalTexture
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.SLChatPermissionRequestEvent
import com.linkpoint.slproto.chat.SLChatScriptDialog
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.modules.SLAvatarControl
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.types.CameraParams
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.ActiveChattersManager
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.MyAvatarState
import com.linkpoint.slproto.users.manager.ObjectPopupsManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.ContactsFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.render.CardboardControlsPlaceholder
import com.linkpoint.ui.voice.VoiceStatusView
import com.linkpoint.voice.common.model.VoiceChatInfo
import java.util.ArrayList
import java.util.HashSet
import java.util.Set
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@TargetApi(16)
class CardboardActivity : DetailsActivity(), ObjectPopupsManager.ObjectPopupListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues = null
    private const val DEFAULT_FONT_SIZE_SP: Int = 16
    private const val LISTVIEW_SCROLL_DURATION: Int = 500
    private const val LISTVIEW_SCROLL_OFFSET: Int = 100
    private const val RECYCLERVIEW_SCROLL_OFFSET: Int = 100
    private const val VOICE_VIEW_HEIGHT_ALLOWANCE_DP: Float = 60.0f
    const val VR_MODE_TAG: String = "vrMode"
    private const val Float controlDrawSizeFactor = 1.5f
    private const val Float controlSizeFactorX = 1.0f
    private const val Float controlSizeFactorY = 0.75f
    private const val Float crosshairSize = 0.1f
    /* access modifiers changed from: private */
    const val IntArray dialogButtonIds = {R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4, R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8, R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12}
    /* access modifiers changed from: private */
    public SLChatScriptDialog activeScriptDialog = null
    private SLChatYesNoEvent activeYesNoEvent = null
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f545$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.11.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.11.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
    val AtomicReference<SLAvatarControl> avatarControl = AtomicReference<>()
    @BindView(2131755257)
    ImageButton buttonChat
    @BindView(2131755262)
    ImageButton buttonMoveBackward
    @BindView(2131755260)
    ImageButton buttonMoveForward
    @BindView(2131755270)
    ImageButton buttonObjectChat
    @BindView(2131755269)
    ImageButton buttonSit
    @BindView(2131755256)
    ImageButton buttonSpeak
    @BindView(2131755281)
    ImageButton buttonSpeechSend
    @BindView(2131755264)
    ImageButton buttonStandUp
    @BindView(2131755255)
    ImageButton buttonTouch
    @BindView(2131755268)
    ImageButton buttonTouchObject
    @BindView(2131755261)
    ImageButton buttonTurnLeft
    @BindView(2131755263)
    ImageButton buttonTurnRight
    @BindView(2131755265)
    ViewGroup cardboardAimControls
    @BindView(2131755283)
    ViewGroup cardboardDetailsPage
    @BindView(2131755266)
    ViewGroup cardboardObjectControls
    @BindView(2131755253)
    ViewGroup cardboardPrimaryControls
    @BindView(2131755282)
    ViewGroup cardboardScriptDialog
    @BindView(2131755277)
    ViewGroup cardboardSpeakControls
    private val Object chatEventHandler = Object() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$5_68853  reason: not valid java name */
        public /* synthetic */ Unit m776lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$5_68853(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            if (CardboardActivity.this.fadingTextViewLog != null) {
                CardboardActivity.this.fadingTextViewLog.handleChatEvent(chatMessageEvent)
            }
        }

        @Subscribe
        fun onChatMessage(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            CardboardActivity.this.runOnUiThread(Runnable(this, chatMessageEvent) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f561$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f562$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.26.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.26.$m$0():Unit, class status: UNLOADED
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
    @BindView(2131755258)
    LinearLayout chatsOverlayLayout
    /* access modifiers changed from: private */
    public Controller controller
    /* access modifiers changed from: private */
    val AtomicInteger controllerConnectionState = AtomicInteger(0)
    private val Controller.EventListener controllerEventListener = Controller.EventListener() {
        private MoveControl activeMoveControl = null
        private Boolean appButtonPressed = false

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808  reason: not valid java name */
        public /* synthetic */ Unit m777lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808() {
            CardboardActivity.this.onExternalButtonAction(true)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948  reason: not valid java name */
        public /* synthetic */ Unit m778lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948() {
            CardboardActivity.this.onExternalButtonAction(false)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726  reason: not valid java name */
        public /* synthetic */ Unit m779lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726(MoveControl moveControl, Float f) {
            CardboardActivity.this.handleMoveControl(moveControl, f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931  reason: not valid java name */
        public /* synthetic */ Unit m780lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931(MoveControl moveControl) {
            CardboardActivity.this.handleMoveControl(moveControl, 0.0f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145  reason: not valid java name */
        public /* synthetic */ Unit m781lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145(MoveControl moveControl, Float f) {
            if (CardboardActivity.this.ownAvatarVisible || moveControl == MoveControl.Right || moveControl == MoveControl.Left) {
                CardboardActivity.this.handleMoveControl(moveControl, f)
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581  reason: not valid java name */
        public /* synthetic */ Unit m782lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581(MoveControl moveControl) {
            CardboardActivity.this.handleMoveControl(moveControl, 0.0f)
        }

        fun onConnectionStateChanged(i: Int) {
            super.onConnectionStateChanged(i)
            val objArr: Array<Any> = Object[1]
            objArr[0] = i == 3 ? "connected" : "disconnected"
            Debug.Printf("Cardboard: Daydream controller is now %s", objArr)
            CardboardActivity.this.controllerConnectionState.set(i)
        }

        fun onUpdate() {
            MoveControl moveControl
            val f: Float = 0.0f
            super.onUpdate()
            CardboardActivity.this.controller.update()
            if (CardboardActivity.this.controller.appButtonState && (!this.appButtonPressed)) {
                CardboardActivity.this.runOnUiThread(Runnable(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f555$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.20.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.20.$m$0():Unit, class status: UNLOADED
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

            } else if (!CardboardActivity.this.controller.appButtonState && this.appButtonPressed) {
                CardboardActivity.this.runOnUiThread(Runnable(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f556$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.21.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.21.$m$0():Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
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
            this.appButtonPressed = CardboardActivity.this.controller.appButtonState
            if (CardboardActivity.this.controller.isTouching) {
                val f2: Float = (CardboardActivity.this.controller.touch.x * 2.0f) - 1.0f
                val f3: Float = -((CardboardActivity.this.controller.touch.y * 2.0f) - 1.0f)
                if (Math.abs(f2) < 0.5f) {
                    f2 = 0.0f
                }
                if (Math.abs(f3) < 0.5f) {
                    f3 = 0.0f
                }
                if (Math.abs(f2) >= Math.abs(f3)) {
                    if (f2 > 0.0f) {
                        f = f2 * 2.0f
                        moveControl = MoveControl.Right
                    } else if (f2 < 0.0f) {
                        f = (-f2) * 2.0f
                        moveControl = MoveControl.Left
                    } else {
                        moveControl = null
                    }
                } else if (f3 > 0.0f) {
                    moveControl = MoveControl.Forward
                    f = f3 * 2.0f
                } else if (f3 < 0.0f) {
                    moveControl = MoveControl.Backward
                    f = (-f3) * 2.0f
                } else {
                    moveControl = null
                }
                if (moveControl != this.activeMoveControl) {
                    if (moveControl != null) {
                        this.activeMoveControl = moveControl
                        CardboardActivity.this.runOnUiThread(Runnable(f, this, moveControl) {

                            /* renamed from: -$f0 */
                            private val /* synthetic */ Float f567$f0

                            /* renamed from: -$f1 */
                            private val /* synthetic */ Object f568$f1

                            /* renamed from: -$f2 */
                            private val /* synthetic */ Object f569$f2

                            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.29.$m$0():Unit, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.29.$m$0():Unit, class status: UNLOADED
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

                        return
                    }
                    CardboardActivity.this.runOnUiThread(Runnable(this, this.activeMoveControl) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f563$f0

                        /* renamed from: -$f1 */
                        private val /* synthetic */ Object f564$f1

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.27.$m$0():Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.27.$m$0():Unit, class status: UNLOADED
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

                    this.activeMoveControl = null
                } else if (moveControl != null) {
                    CardboardActivity.this.runOnUiThread(Runnable(f, this, moveControl) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Float f571$f0

                        /* renamed from: -$f1 */
                        private val /* synthetic */ Object f572$f1

                        /* renamed from: -$f2 */
                        private val /* synthetic */ Object f573$f2

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.30.$m$0():Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.30.$m$0():Unit, class status: UNLOADED
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
                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
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
            } else if (this.activeMoveControl != null) {
                CardboardActivity.this.runOnUiThread(Runnable(this, this.activeMoveControl) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f565$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f566$f1

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.28.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.28.$m$0():Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
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

                this.activeMoveControl = null
            }
        }
    }
    private ControllerManager controllerManager
    private val ControllerManager.EventListener controllerManagerEventListener = ControllerManager.EventListener() {
        fun onApiStatusChanged(i: Int) {
            Debug.Printf("Cardboard: controller API status: %d", Integer.valueOf(i))
        }

        fun onRecentered() {
            if (CardboardActivity.this.gvrView != null) {
                CardboardActivity.this.gvrView.recenterHeadTracker()
            }
        }
    }
    /* access modifiers changed from: private */
    public volatile ControlsPage currentControlsPage = ControlsPage.pageDefault
    private val SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f549$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.15.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.15.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755636)
    TextView dialogQuestionText
    private ChatterID dictationChatterID = null
    private val AtomicReference<GLExternalTexture> externalTextureRef = AtomicReference<>((Object) null)
    /* access modifiers changed from: private */
    public FadingTextViewLog fadingTextViewLog = null
    private FullscreenMode fullscreenMode
    /* access modifiers changed from: private */
    public GvrView gvrView
    /* access modifiers changed from: private */
    @SuppressLint({"HandlerLeak"})
    val Handler handler = Handler() {
        fun handleMessage(message: Message) {
            val z: Boolean = false
            switch (message.what) {
                case 1:
                    if (message.obj != null && (message.obj instanceof ObjectIntersectInfo)) {
                        val objectIntersectInfo: ObjectIntersectInfo = (ObjectIntersectInfo) message.obj
                        Debug.Printf("Cardboard: PICKED OBJECT isAvatar %b localID %d", Boolean.valueOf(objectIntersectInfo.objInfo.isAvatar()), Integer.valueOf(objectIntersectInfo.objInfo.localID))
                        if (objectIntersectInfo.objInfo instanceof SLObjectAvatarInfo) {
                            z = ((SLObjectAvatarInfo) objectIntersectInfo.objInfo).isMyAvatar()
                        }
                        if (!z) {
                            CardboardActivity.this.handlePickedObject(objectIntersectInfo)
                            return
                        }
                        return
                    }
                    return
                case 2:
                    if (message.obj != null && (message.obj instanceof SLObjectInfo)) {
                        val sLObjectInfo: SLObjectInfo = (SLObjectInfo) message.obj
                        Debug.Printf("Cardboard: touched object isAvatar %b localID %d", Boolean.valueOf(sLObjectInfo.isAvatar()), Integer.valueOf(sLObjectInfo.localID))
                        return
                    }
                    return
                default:
                    return
            }
        }
    }
    /* access modifiers changed from: private */
    public volatile Float headAgentHeading = 0.0f
    /* access modifiers changed from: private */
    val Object hitPointLock = Object()
    /* access modifiers changed from: private */
    val AtomicBoolean hitPointUpdatePosted = AtomicBoolean(false)
    private Boolean hitPointValid = false
    private Int hitPointX = 0
    /* access modifiers changed from: private */
    public Int hitPointY = 0
    private val OnHoverListenerCompat hoverListener = OnHoverListenerCompat() {
         public fun onHoverEnter(view: View): Boolean {
            view.setAlpha(1.0f)
            Debug.Printf("Cardboard: hovering enter %d", Integer.valueOf(view.getId()))
            val unused: View = CardboardActivity.this.hoveringOverButton = view
            CardboardActivity.this.m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12()
            return false
        }

         public fun onHoverExit(view: View): Boolean {
            view.setAlpha(0.5f)
            Debug.Printf("Cardboard: hovering exit %d", Integer.valueOf(view.getId()))
            if (CardboardActivity.this.hoveringOverButton == view) {
                val unused: View = CardboardActivity.this.hoveringOverButton = null
            }
            CardboardActivity.this.m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12()
            return false
        }
    }
    /* access modifiers changed from: private */
    public View hoveringOverButton = null
    private View hoveringPressedButton = null
    /* access modifiers changed from: private */
    public volatile Boolean insideControls = false
    private Long insideSince = 0
    private Boolean isResumed = false
    /* access modifiers changed from: private */
    public Boolean isSpeechFinished = false
    /* access modifiers changed from: private */
    public volatile Boolean isWalking = false
    /* access modifiers changed from: private */
    val AtomicBoolean keypadActive = AtomicBoolean(false)
    /* access modifiers changed from: private */
    val AtomicDouble keypadTurning = AtomicDouble(0.0d)
    /* access modifiers changed from: private */
    public String lastSpeechRecognitionResults = ""
    private val IntArray locationInWindow = Int[2]
    @BindView(2131755259)
    ViewGroup moveButtonsLayout
    /* access modifiers changed from: private */
    public Int moveButtonsTop = 0
    private val SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f546$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.12.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.12.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
    public volatile Float neutralAgentHeading = Float.NaN
    @BindView(2131755275)
    ImageButton noButton
    @BindView(2131755271)
    TextView objectNameView
    private val View.OnClickListener onDialogButtonClick = View.OnClickListener() {
        fun onClick(view: View) {
            if (CardboardActivity.this.activeScriptDialog != null) {
                val i: Int = 0
                while (true) {
                    if (i >= CardboardActivity.dialogButtonIds.length) {
                        i = -1
                        break
                    } else if (view.getId() == CardboardActivity.dialogButtonIds[i]) {
                        break
                    } else {
                        i++
                    }
                }
                CardboardActivity.this.activeScriptDialog.onDialogButton(CardboardActivity.this.userManager, i)
                val unused: SLChatScriptDialog = CardboardActivity.this.activeScriptDialog = null
            }
            CardboardActivity.this.handlePickedObject((ObjectIntersectInfo) null)
            CardboardActivity.this.setControlsPage(ControlsPage.pageDefault)
        }
    }
    private val View.OnHoverListener onHoverListener = View.OnHoverListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f543$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.1.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.1.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onHover(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.1.onHover(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.1.onHover(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    private ViewGroup onScreenControlsLayout
    private val View.OnClickListener onVoiceCallButtonListener = $Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA(this)
    private val ImmutableMap<ControlsPage, View.OnTouchListener> outsideTouchListeners = ImmutableMap.of(ControlsPage.pageSpeech, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f570$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.3.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.3.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.3.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.3.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    }, ControlsPage.pageObject, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f574$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.4.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.4.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.4.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.4.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    }, ControlsPage.pageScriptDialog, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f575$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.5.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.5.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.5.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.5.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    }, ControlsPage.pageYesNo, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f576$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.6.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.6.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.6.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.6.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    }, ControlsPage.pageDetails, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f554$f0

        private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.2.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.2.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

        val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.2.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.2.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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
    public volatile Boolean ownAvatarVisible = false
    private ChatterNameRetriever pickedAvatarNameRetriever = null
    /* access modifiers changed from: private */
    val AtomicReference<ObjectIntersectInfo> pickedObject = AtomicReference<>()
    /* access modifiers changed from: private */
    public Int postedHitPointX = 0
    /* access modifiers changed from: private */
    public Int postedHitPointY = 0
    /* access modifiers changed from: private */
    public Int primaryButtonsViewBottom = 0
    private val RecognitionListener recognitionListener = RecognitionListener() {
        fun onBeginningOfSpeech() {
            Debug.Printf("Cardboard: beginning of speech", Object[0])
        }

        fun onBufferReceived(bArr: ByteArray) {
        }

        fun onEndOfSpeech() {
            Debug.Printf("Cardboard: end of speech", Object[0])
            CardboardActivity.this.speakLevelIndicator.setVisibility(4)
            CardboardActivity.this.speakNowText.setVisibility(4)
        }

        fun onError(i: Int) {
            String string
            Debug.Printf("Cardboard: speech error %d", Integer.valueOf(i))
            switch (i) {
                case 1:
                case 2:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_network)
                    break
                case 3:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_audio_recording_error)
                    break
                case 4:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_error_server)
                    break
                case 6:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_error_timeout)
                    break
                case 7:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_no_match)
                    break
                case 8:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_error_busy)
                    break
                case 9:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_error_permissions)
                    break
                default:
                    string = CardboardActivity.this.getString(R.string.speech_recognition_error)
                    break
            }
            CardboardActivity.this.showSpeechRecognitionError(string)
        }

        fun onEvent(i: Int, bundle: Bundle) {
        }

        fun onPartialResults(bundle: Bundle) {
            Debug.Printf("Cardboard: speech recognition: got partial results", Object[0])
            val stringArrayList: ArrayList<String> = bundle.getStringArrayList("results_recognition")
            if (stringArrayList != null && stringArrayList.size() > 0) {
                val str: String = stringArrayList.get(0)
                val unused: String = CardboardActivity.this.lastSpeechRecognitionResults = str
                CardboardActivity.this.speechRecognitionResults.setText(str)
                if (!Strings.isNullOrEmpty(str)) {
                    CardboardActivity.this.buttonSpeechSend.setVisibility(0)
                }
            }
        }

        fun onReadyForSpeech(bundle: Bundle) {
            CardboardActivity.this.speakNowText.setVisibility(0)
        }

        fun onResults(bundle: Bundle) {
            Debug.Printf("Cardboard: speech recognition: got some results", Object[0])
            val stringArrayList: ArrayList<String> = bundle.getStringArrayList("results_recognition")
            if (stringArrayList != null && stringArrayList.size() > 0) {
                val str: String = stringArrayList.get(0)
                CardboardActivity.this.speechRecognitionResults.setText(str)
                val unused: String = CardboardActivity.this.lastSpeechRecognitionResults = str
                if (!Strings.isNullOrEmpty(str)) {
                    CardboardActivity.this.buttonSpeechSend.setVisibility(0)
                    CardboardActivity.this.speakLevelIndicator.setVisibility(4)
                    val unused2: Boolean = CardboardActivity.this.isSpeechFinished = true
                }
            }
        }

        fun onRmsChanged(f: Float) {
            if (!CardboardActivity.this.isSpeechFinished) {
                if (Float.isNaN(CardboardActivity.this.speechRmsMin) || f < CardboardActivity.this.speechRmsMin) {
                    val unused: Float = CardboardActivity.this.speechRmsMin = f
                }
                if (Float.isNaN(CardboardActivity.this.speechRmsMax) || f > CardboardActivity.this.speechRmsMax) {
                    val unused2: Float = CardboardActivity.this.speechRmsMax = f
                }
                val r0: Float = CardboardActivity.this.speechRmsMax
                if (r0 - CardboardActivity.this.speechRmsMin < 1.0f) {
                    r0 = CardboardActivity.this.speechRmsMin + 1.0f
                }
                val round: Int = Math.round(((f - CardboardActivity.this.speechRmsMin) * 100.0f) / (r0 - CardboardActivity.this.speechRmsMin))
                if (round < 0) {
                    round = 0
                }
                if (round > 100) {
                    round = 100
                }
                Debug.Printf("Cardboard: speech recognition: RMS %f", Float.valueOf(f))
                CardboardActivity.this.speakLevelIndicator.setVisibility(0)
                CardboardActivity.this.speakLevelIndicator.setProgress(round)
            }
        }
    }
    /* access modifiers changed from: private */
    public RenderSettings renderSettings
    /* access modifiers changed from: private */
    public WorldViewRenderer renderer
    private val ScreenOnFlagHelper screenOnFlagHelper = ScreenOnFlagHelper(this)
    private Point scrollableViewPoint = Point()
    private val SubscriptionData<Integer, SLObjectProfileData> selectedObjectProfile = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f547$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.13.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.13.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755279)
    ProgressBar speakLevelIndicator
    @BindView(2131755278)
    TextView speakNowText
    @BindView(2131755280)
    TextView speechRecognitionResults
    private SpeechRecognizer speechRecognizer
    /* access modifiers changed from: private */
    public Float speechRmsMax = Float.NaN
    /* access modifiers changed from: private */
    public Float speechRmsMin = Float.NaN
    private Handler stateHandler
    private val GvrView.StereoRenderer stereoRenderer = WorldStereoRenderer()
    private val Set<View> touchActivatedButtons = HashSet()
    /* access modifiers changed from: private */
    val AtomicBoolean touchRequested = AtomicBoolean(false)
    /* access modifiers changed from: private */
    public UserManager userManager
    private val AtomicBoolean viewDrawPosted = AtomicBoolean(false)
    private val SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f550$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.16.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.16.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f551$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.17.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.17.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private Boolean voiceEnabled = false
    private val SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedIn = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f548$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.14.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.14.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    @BindView(2131755254)
    VoiceStatusView voiceStatusView
    /* access modifiers changed from: private */
    public Int voiceViewHeightAllowance
    @BindView(2131755274)
    ImageButton yesButton
    @BindView(2131755276)
    TextView yesNoText

    private enum ControlsPage {
        pageDefault(R.id.cardboard_primary_controls),
        pageSpeech(R.id.cardboard_speak_controls),
        pageTouchAim(R.id.cardboard_aim_controls),
        pageObject(R.id.cardboard_object_controls),
        pageScriptDialog(R.id.cardboard_script_dialog),
        pageYesNo(R.id.cardboard_yesno_dialog),
        pageDetails(R.id.cardboard_details_page)
        
        final Int pageViewId

        private ControlsPage(Int i) {
            this.pageViewId = i
        }
    }

    private class WorldStereoRenderer : GvrView.StereoRenderer {
        private const val TURN_DEGREES: Float = 35.0f
        private const val TURN_DEGREES_PER_MS: Float = 0.02f
        private const val YAW_AVERAGE_FACTOR: Float = 1.0E-4f
        private Boolean agentHeadingAcquired = false
        private Boolean crosshairVisible = false
        private val FloatArray extTextureMatrixUV = Float[16]
        private GLExternalTexture externalTexture
        private val FloatArray eyeHitTests = Float[4]
        private val FloatArray eyeOffset = Float[4]
        private val FloatArray eyeOffsetMatrix = Float[16]
        private val FloatArray eyeProjection = Float[32]
        private val BooleanArray eyeProjectionValid = Boolean[2]
        private Float eyeSeparation = 0.0f
        private val IntArray eyeViewport = Int[4]
        private val HeadTransformCompat headTransformCompat = HeadTransformCompat()
        private Long lastFrameTime = 0
        private Int viewportHeight = 0
        private Int viewportWidth = 0

        WorldStereoRenderer() {
            Matrix.setIdentityM(this.eyeOffsetMatrix, 0)
            Matrix.rotateM(this.eyeOffsetMatrix, 0, -90.0f, 1.0f, 0.0f, 0.0f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer-mthref-0  reason: not valid java name */
        public /* synthetic */ Unit m783com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderermthref0() {
            CardboardActivity.this.updateExternalTexturePointer()
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554  reason: not valid java name */
        public /* synthetic */ Unit m784lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554() {
            CardboardActivity.this.onNewExternalTexture(this.externalTexture)
            CardboardActivity.this.draw2DUI()
        }

        fun onDrawEye(eye: Eye) {
            val type: Int = eye.getType()
            val f: Float = (type == 1 ? -0.5f : 0.5f) * this.eyeSeparation
            for (Int i = 0; i < 4; i++) {
                this.eyeOffset[i] = this.headTransformCompat.rightVector[i] * f
            }
            eye.getViewport().getAsArray(this.eyeViewport, 0)
            eye.getEyeView()
            val i2: Int = type == 1 ? 0 : 1
            if (CardboardActivity.this.renderSettings != null && (!this.eyeProjectionValid[i2] || eye.getProjectionChanged())) {
                System.arraycopy(eye.getPerspective(0.5f, (Float) CardboardActivity.this.renderSettings.drawDistance), 0, this.eyeProjection, i2 * 16, 16)
            }
            CardboardActivity.this.renderer.onDrawFrame((GL10) null, this.headTransformCompat, this.eyeOffset, this.eyeViewport, (FloatArray) null, (FloatArray) null, 0)
            if (this.externalTexture != null) {
                CardboardActivity.this.renderer.drawExternalTexture(this.externalTexture, this.extTextureMatrixUV, f, this.headTransformCompat.pitchDegrees, this.headTransformCompat.useButtonsYaw, CardboardActivity.controlDrawSizeFactor, 1.125f, this.eyeHitTests, type == 1 ? 0 : 2)
                if (this.crosshairVisible) {
                    CardboardActivity.this.renderer.drawCrosshair(CardboardActivity.crosshairSize, f)
                }
            }
        }

        fun onFinishFrame(viewport: Viewport) {
            CardboardActivity.this.renderer.onFinishFrame()
            if (this.externalTexture != null) {
                val width: Int = (Int) (((((this.eyeHitTests[0] + this.eyeHitTests[2]) / 2.0f) * 2.0f) + 0.5f) * ((Float) this.externalTexture.getWidth()))
                val height: Int = (Int) (((-(((this.eyeHitTests[1] + this.eyeHitTests[3]) / 2.0f) * 2.0f)) + 0.5f) * ((Float) this.externalTexture.getHeight()))
                synchronized (CardboardActivity.this.hitPointLock) {
                    val unused: Int = CardboardActivity.this.postedHitPointX = width
                    val unused2: Int = CardboardActivity.this.postedHitPointY = height
                }
                if (!CardboardActivity.this.hitPointUpdatePosted.getAndSet(true)) {
                    CardboardActivity.this.runOnUiThread(Runnable(this) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f557$f0

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.22.$m$0():Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.22.$m$0():Unit, class status: UNLOADED
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
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
            }
        }

        fun onNewFrame(headTransform: HeadTransform) {
            val sLAvatarControl: SLAvatarControl = (SLAvatarControl) CardboardActivity.this.avatarControl.get()
            val uptimeMillis: Long = SystemClock.uptimeMillis()
            headTransform.getQuaternion(this.headTransformCompat.rotationQuat, 0)
            headTransform.getTranslation(this.headTransformCompat.translationVector, 0)
            headTransform.getHeadView(this.headTransformCompat.headTransformMatrix, 0)
            headTransform.getEulerAngles(this.headTransformCompat.eulerAngles, 0)
            headTransform.getRightVector(this.headTransformCompat.rightVectorRaw, 0)
            Matrix.multiplyMV(this.headTransformCompat.rightVector, 0, this.eyeOffsetMatrix, 0, this.headTransformCompat.rightVectorRaw, 0)
            this.headTransformCompat.yawDegrees = CameraParams.wrapAngle(this.headTransformCompat.eulerAngles[1] / 0.017453292f)
            if (sLAvatarControl != null && (!this.agentHeadingAcquired)) {
                this.headTransformCompat.viewExtraYaw = sLAvatarControl.getAgentHeading()
                this.agentHeadingAcquired = true
                Debug.Printf("Cardboard: agent heading acquired: %.2f", Float.valueOf(this.headTransformCompat.viewExtraYaw))
            }
            if (CardboardActivity.this.currentControlsPage == ControlsPage.pageTouchAim) {
                this.headTransformCompat.pitchDegrees = 0.0f
                this.headTransformCompat.useButtonsYaw = 0.0f
                this.headTransformCompat.lastYaw = this.headTransformCompat.eulerAngles[1]
            } else if (CardboardActivity.this.currentControlsPage == ControlsPage.pageObject || CardboardActivity.this.currentControlsPage == ControlsPage.pageYesNo) {
                this.headTransformCompat.pitchDegrees = 0.0f
                this.headTransformCompat.useButtonsYaw = (this.headTransformCompat.eulerAngles[1] - this.headTransformCompat.lastYaw) / 0.017453292f
            } else {
                this.headTransformCompat.pitchDegrees = this.headTransformCompat.eulerAngles[0] / 0.017453292f
                val z: Boolean = false
                if (CardboardActivity.this.currentControlsPage == ControlsPage.pageSpeech) {
                    z = true
                } else if (CardboardActivity.this.currentControlsPage == ControlsPage.pageDefault) {
                    z = true
                    if (!CardboardActivity.this.insideControls || CardboardActivity.this.hitPointY < CardboardActivity.this.primaryButtonsViewBottom) {
                        z = false
                    }
                    if ((!CardboardActivity.this.insideControls || CardboardActivity.this.hitPointY > CardboardActivity.this.moveButtonsTop) && CardboardActivity.this.ownAvatarVisible) {
                        z = false
                    }
                }
                if (z) {
                    this.headTransformCompat.lastYaw = this.headTransformCompat.eulerAngles[1]
                    this.headTransformCompat.useButtonsYaw = 0.0f
                } else {
                    this.headTransformCompat.useButtonsYaw = (this.headTransformCompat.eulerAngles[1] - this.headTransformCompat.lastYaw) / 0.017453292f
                }
            }
            if (!this.headTransformCompat.neutralYawValid) {
                this.headTransformCompat.neutralYawValid = true
                this.headTransformCompat.neutralYaw = this.headTransformCompat.yawDegrees
            } else {
                val j: Long = uptimeMillis - this.lastFrameTime
                val angleMinusAngle: Float = CameraParams.angleMinusAngle(this.headTransformCompat.yawDegrees, this.headTransformCompat.neutralYaw)
                val z2: Boolean = CardboardActivity.this.keypadActive.get() || CardboardActivity.this.controllerConnectionState.get() == 3
                val z3: Boolean = false
                val z4: Boolean = false
                val f: Float = 1.0f
                if (z2) {
                    val d: Double = CardboardActivity.this.keypadTurning.get()
                    z3 = d < 0.0d
                    z4 = d > 0.0d
                    f = (Float) Math.abs(d)
                }
                if ((angleMinusAngle < -35.0f && (!z2)) || z4) {
                    this.headTransformCompat.viewExtraYaw = CameraParams.wrapAngle(this.headTransformCompat.viewExtraYaw - (f * (((Float) j) * TURN_DEGREES_PER_MS)))
                } else if ((angleMinusAngle <= TURN_DEGREES || !(!z2)) && !z3) {
                    this.headTransformCompat.neutralYaw = CameraParams.wrapAngle(this.headTransformCompat.neutralYaw + (YAW_AVERAGE_FACTOR * angleMinusAngle * ((Float) j)))
                } else {
                    this.headTransformCompat.viewExtraYaw = CameraParams.wrapAngle((f * ((Float) j) * TURN_DEGREES_PER_MS) + this.headTransformCompat.viewExtraYaw)
                }
            }
            if (this.headTransformCompat.viewExtraYaw != CardboardActivity.this.neutralAgentHeading || Float.isNaN(CardboardActivity.this.neutralAgentHeading)) {
                val unused: Float = CardboardActivity.this.neutralAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.viewExtraYaw)
            }
            val unused2: Float = CardboardActivity.this.headAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.yawDegrees + this.headTransformCompat.viewExtraYaw)
            if (!Float.isNaN(CardboardActivity.this.neutralAgentHeading) && sLAvatarControl != null) {
                sLAvatarControl.setAgentHeading(CardboardActivity.this.isWalking ? CardboardActivity.this.headAgentHeading : CardboardActivity.this.neutralAgentHeading)
            }
            this.lastFrameTime = uptimeMillis
            CardboardActivity.this.renderer.setOwnAvatarHidden(!CardboardActivity.this.ownAvatarVisible)
            CardboardActivity.this.renderer.onPrepareFrame(this.headTransformCompat)
            if (CardboardActivity.this.touchRequested.getAndSet(false)) {
                CardboardActivity.this.renderer.pickObject((Float) (this.viewportWidth / 2), (Float) (this.viewportHeight / 2), CardboardActivity.this.handler)
            }
            val objectIntersectInfo: ObjectIntersectInfo = (ObjectIntersectInfo) CardboardActivity.this.pickedObject.get()
            CardboardActivity.this.renderer.setDrawPickedObject(objectIntersectInfo != null ? objectIntersectInfo.objInfo : null)
            this.crosshairVisible = CardboardActivity.this.currentControlsPage == ControlsPage.pageDetails ? CardboardActivity.this.insideControls : false
            if (this.externalTexture != null) {
                this.externalTexture.update(this.extTextureMatrixUV)
            }
        }

        fun onRendererShutdown() {
            this.externalTexture.release()
            this.externalTexture = null
            CardboardActivity.this.renderer.onRendererShutdown()
        }

        fun onSurfaceChanged(i: Int, i2: Int) {
            this.viewportWidth = i
            this.viewportHeight = i2
            CardboardActivity.this.renderer.onSurfaceChanged((GL10) null, i, i2)
            if (this.externalTexture != null) {
                this.externalTexture.release()
            }
            this.externalTexture = GLExternalTexture((Int) (((Float) i) * 1.0f), ((Int) (((Float) i2) * CardboardActivity.controlSizeFactorY)) + CardboardActivity.this.voiceViewHeightAllowance)
            this.eyeSeparation = CardboardActivity.this.gvrView.getInterpupillaryDistance()
            this.headTransformCompat.neutralYawValid = false
            CardboardActivity.this.runOnUiThread(Runnable(this) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f558$f0

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.23.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.23.$m$0():Unit, class status: UNLOADED
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
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

        fun onSurfaceCreated(eGLConfig: EGLConfig) {
            CardboardActivity.this.renderer.onSurfaceCreated((GL10) null, eGLConfig, true)
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m734getcomlumiyaviewerlumiyauirenderMoveControlSwitchesValues() {
        if (f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues != null) {
            return f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues
        }
        val iArr: IntArray = Int[MoveControl.values().length]
        try {
            iArr[MoveControl.Backward.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[MoveControl.Forward.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[MoveControl.Left.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[MoveControl.Right.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues = iArr
        return iArr
    }

     private fun closeSpeechControls() {
        if (this.speechRecognizer != null) {
            this.speechRecognizer.stopListening()
        }
        if (getCurrentDetailsFragment() != null) {
            setControlsPage(ControlsPage.pageDetails)
        } else {
            setControlsPage(ControlsPage.pageDefault)
        }
    }

    /* access modifiers changed from: private */
    fun draw2DUI() {
        val gLExternalTexture: GLExternalTexture = this.externalTextureRef.get()
        if (gLExternalTexture != null) {
            try {
                val canvas: Canvas = gLExternalTexture.getCanvas()
                drawExternalViews(canvas)
                gLExternalTexture.postCanvas(canvas)
            } catch (IllegalStateException e) {
            }
        }
    }

     private fun drawExternalViews(canvas: Canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        this.onScreenControlsLayout.draw(canvas)
    }

    /* access modifiers changed from: private */
    /* renamed from: drawViews */
    fun m761com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref15() {
        this.viewDrawPosted.set(false)
        Debug.Printf("Cardboard: drawing 2D UI", Object[0])
        draw2DUI()
    }

    @TargetApi(19)
     private fun findMatchingView(viewGroup: ViewGroup, i: Int, i2: Int, i3: Int, i4: Int, predicate: Predicate<View>, point: Point): View {
        View findMatchingView
        for (Int i5 = 0; i5 < viewGroup.getChildCount(); i5++) {
            val childAt: View = viewGroup.getChildAt(i5)
            if (childAt.getVisibility() == 0 && childAt.isAttachedToWindow() && predicate.apply(childAt)) {
                childAt.getLocationInWindow(this.locationInWindow)
                val i6: Int = this.locationInWindow[0]
                val i7: Int = this.locationInWindow[1]
                if (Rect(i6, i7, childAt.getWidth() + i6, childAt.getHeight() + i7).contains(i, i2)) {
                    point.set(i - i6, i2 - i7)
                    return childAt
                }
            }
        }
        val i8: Int = 0
        while (true) {
            val i9: Int = i8
            if (i9 >= viewGroup.getChildCount()) {
                return null
            }
            val childAt2: View = viewGroup.getChildAt(i9)
            if (childAt2.getVisibility() == 0 && childAt2.isAttachedToWindow() && (childAt2 instanceof ViewGroup) && (findMatchingView = findMatchingView((ViewGroup) childAt2, i, i2, 0, 0, predicate, point)) != null) {
                return findMatchingView
            }
            i8 = i9 + 1
        }
    }

    /* access modifiers changed from: private */
    fun handleMoveControl(moveControl: MoveControl, f: Float) {
        val sLAvatarControl: SLAvatarControl = this.avatarControl.get()
        if (sLAvatarControl != null) {
            switch (m734getcomlumiyaviewerlumiyauirenderMoveControlSwitchesValues()[moveControl.ordinal()]) {
                case 1:
                    if (f != 0.0f) {
                        this.keypadActive.set(true)
                        if (this.ownAvatarVisible) {
                            sLAvatarControl.startCameraManualControl(0.0f, -1.0f * f, 0.0f, 0.0f)
                            return
                        } else {
                            startWalking(false)
                            return
                        }
                    } else if (this.ownAvatarVisible) {
                        sLAvatarControl.stopCameraManualControl()
                        return
                    } else {
                        stopWalking()
                        return
                    }
                case 2:
                    if (f != 0.0f) {
                        this.keypadActive.set(true)
                        if (this.ownAvatarVisible) {
                            sLAvatarControl.startCameraManualControl(0.0f, 1.0f * f, 0.0f, 0.0f)
                            return
                        } else {
                            startWalking(true)
                            return
                        }
                    } else if (this.ownAvatarVisible) {
                        sLAvatarControl.stopCameraManualControl()
                        return
                    } else {
                        stopWalking()
                        return
                    }
                case 3:
                    if (f != 0.0f) {
                        this.keypadActive.set(true)
                        this.keypadTurning.set((Double) (-f))
                        return
                    }
                    this.keypadTurning.set(0.0d)
                    return
                case 4:
                    if (f != 0.0f) {
                        this.keypadActive.set(true)
                        this.keypadTurning.set((Double) f)
                        return
                    }
                    this.keypadTurning.set(0.0d)
                    return
                default:
                    return
            }
        }
    }

    /* access modifiers changed from: private */
    fun handlePickedObject(objectIntersectInfo: ObjectIntersectInfo) {
        val sLObjectInfo: SLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (sLObjectInfo != null) {
            this.pickedObject.set(objectIntersectInfo)
            if (sLObjectInfo.isAvatar()) {
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId())
                if (!Objects.equal(this.pickedAvatarNameRetriever != null ? this.pickedAvatarNameRetriever.chatterID : null, userChatterID)) {
                    if (this.pickedAvatarNameRetriever != null) {
                        this.pickedAvatarNameRetriever.dispose()
                        this.pickedAvatarNameRetriever = null
                    }
                    this.pickedAvatarNameRetriever = ChatterNameRetriever(userChatterID, ChatterNameRetriever.OnChatterNameUpdated(this) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f552$f0

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.18.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.18.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, class status: UNLOADED
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
                Debug.Printf("ObjectPick: picked object %d", Integer.valueOf(sLObjectInfo.localID))
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(sLObjectInfo.localID))
            }
            setControlsPage(ControlsPage.pageObject)
            updateObjectPanel()
            return
        }
        this.selectedObjectProfile.unsubscribe()
        if (this.pickedAvatarNameRetriever != null) {
            this.pickedAvatarNameRetriever.dispose()
            this.pickedAvatarNameRetriever = null
        }
        this.pickedObject.set((Object) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    /* access modifiers changed from: private */
    /* renamed from: isViewScrollable */
     public fun m762com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref16(view: View): Boolean {
        if (!(view instanceof ListView)) {
            return view instanceof RecyclerView
        }
        return true
    }

     private fun isVoiceLoggedIn(): Boolean {
        val data: Boolean = this.voiceLoggedIn.getData()
        if (data != null) {
            return data.booleanValue()
        }
        return false
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    fun m754com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref0(sLAgentCircuit: SLAgentCircuit) {
        val sLAvatarControl: SLAvatarControl = null
        updateDrawingEnabled()
        val atomicReference: AtomicReference<SLAvatarControl> = this.avatarControl
        if (sLAgentCircuit != null) {
            sLAvatarControl = sLAgentCircuit.getModules().avatarControl
        }
        atomicReference.set(sLAvatarControl)
    }

    /* access modifiers changed from: private */
    /* renamed from: onCardboardTrigger */
    fun m759com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref13() {
        val objArr: Array<Any> = Object[3]
        objArr[0] = Integer.valueOf(this.hoveringOverButton != null ? this.hoveringOverButton.getId() : -1)
        objArr[1] = Integer.valueOf(this.hoveringPressedButton != null ? this.hoveringPressedButton.getId() : -1)
        objArr[2] = Boolean.valueOf(this.hitPointValid)
        Debug.Printf("Cardboard: trigger, hover over %d, hover pressed %d, hitPointValid %b", objArr)
        if (this.hoveringPressedButton == null && this.hitPointValid) {
            val uptimeMillis: Long = SystemClock.uptimeMillis()
            if (!this.insideControls) {
                View.OnTouchListener onTouchListener = this.outsideTouchListeners.get(this.currentControlsPage)
                Debug.Printf("Cardboard: outside touch, listener %s", onTouchListener)
                if (onTouchListener != null) {
                    onTouchListener.onTouch(findViewById(this.currentControlsPage.pageViewId), MotionEvent.obtain(uptimeMillis, uptimeMillis + 100, 1, 0.0f, 0.0f, 0))
                }
            } else if (this.hoveringOverButton == null || (!this.touchActivatedButtons.contains(this.hoveringOverButton))) {
                val obtain: MotionEvent = MotionEvent.obtain(this.insideSince, uptimeMillis, 0, (Float) this.hitPointX, (Float) this.hitPointY, 0)
                obtain.setSource(2)
                this.onScreenControlsLayout.dispatchTouchEvent(obtain)
                val obtain2: MotionEvent = MotionEvent.obtain(this.insideSince, uptimeMillis + 1, 1, (Float) this.hitPointX, (Float) this.hitPointY, 0)
                obtain2.setSource(2)
                this.onScreenControlsLayout.dispatchTouchEvent(obtain2)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocationChanged */
    fun m765com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref3(currentLocationInfo2: CurrentLocationInfo) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onDetailsOutsideTouch */
     public fun m756com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref10(view: View, motionEvent: MotionEvent): Boolean {
        switch (motionEvent.getActionMasked()) {
            case 1:
                handleBackPressed()
                return true
            default:
                return true
        }
    }

    /* access modifiers changed from: private */
    fun onExternalButtonAction(z: Boolean) {
        val uptimeMillis: Long = SystemClock.uptimeMillis()
        if (z) {
            val obtain: MotionEvent = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, (Float) this.hitPointX, (Float) this.hitPointY, 0)
            obtain.setSource(2)
            onVrTouchInternal(obtain, true)
            m759com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref13()
            return
        }
        val obtain2: MotionEvent = MotionEvent.obtain(uptimeMillis, uptimeMillis, 1, (Float) this.hitPointX, (Float) this.hitPointY, 0)
        obtain2.setSource(2)
        onVrTouchInternal(obtain2, true)
    }

    @TargetApi(14)
     private fun onExternalTexturePointer(i: Int, i2: Int) {
        View findMatchingView
        this.hitPointValid = true
        this.hitPointX = i
        this.hitPointY = i2
        val gLExternalTexture: GLExternalTexture = this.externalTextureRef.get()
        if (gLExternalTexture != null) {
            if (i >= 0 && i < gLExternalTexture.getWidth() && i2 >= 0 && i2 < gLExternalTexture.getHeight()) {
                if (!this.insideControls) {
                    this.insideControls = true
                    this.insideSince = SystemClock.uptimeMillis()
                    val obtain: MotionEvent = MotionEvent.obtain(this.insideSince, this.insideSince, 9, (Float) i, (Float) i2, 0)
                    obtain.setSource(2)
                    this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain)
                }
                val obtain2: MotionEvent = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 7, (Float) i, (Float) i2, 0)
                obtain2.setSource(2)
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain2)
                if (this.currentControlsPage == ControlsPage.pageDetails && (findMatchingView = findMatchingView(this.cardboardDetailsPage, i, i2, 0, 0, Predicate(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f544$f0

                    private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.$m$0(java.lang.Object):Boolean, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.$m$0(java.lang.Object):Boolean, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:119)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.ConditionGen.addCompare(ConditionGen.java:129)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:57)
                    	at jadx.core.codegen.ConditionGen.wrap(ConditionGen.java:84)
                    	at jadx.core.codegen.ConditionGen.addAndOr(ConditionGen.java:151)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:70)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:46)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:140)
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

                    val Boolean apply(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.apply(java.lang.Object):Boolean, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.apply(java.lang.Object):Boolean, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:119)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.ConditionGen.addCompare(ConditionGen.java:129)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:57)
                    	at jadx.core.codegen.ConditionGen.wrap(ConditionGen.java:84)
                    	at jadx.core.codegen.ConditionGen.addAndOr(ConditionGen.java:151)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:70)
                    	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:46)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:140)
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
                }, this.scrollableViewPoint)) != null) {
                    val height: Int = findMatchingView.getHeight()
                    if (this.scrollableViewPoint.y < height / 4) {
                        Debug.Printf("Cardboard: want to scroll up %s", findMatchingView)
                        if (findMatchingView instanceof ListView) {
                            ((ListView) findMatchingView).smoothScrollBy(-100, 500)
                        } else if (findMatchingView instanceof RecyclerView) {
                            ((RecyclerView) findMatchingView).smoothScrollBy(0, -100)
                        }
                    } else if (this.scrollableViewPoint.y > (height * 3) / 4) {
                        Debug.Printf("Cardboard: want to scroll down %s", findMatchingView)
                        if (findMatchingView instanceof ListView) {
                            ((ListView) findMatchingView).smoothScrollBy(100, 500)
                        } else if (findMatchingView instanceof RecyclerView) {
                            ((RecyclerView) findMatchingView).smoothScrollBy(0, 100)
                        }
                    }
                }
            } else if (this.insideControls) {
                this.insideControls = false
                val obtain3: MotionEvent = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 10, (Float) i, (Float) i2, 0)
                obtain3.setSource(2)
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain3)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyAvatarState */
    fun m755com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref1(myAvatarState2: MyAvatarState) {
        SLAvatarControl sLAvatarControl
        if (myAvatarState2.isSitting()) {
            this.moveButtonsLayout.setVisibility(0)
            this.buttonStandUp.setVisibility(0)
            this.ownAvatarVisible = true
            return
        }
        if (this.ownAvatarVisible && (sLAvatarControl = this.avatarControl.get()) != null) {
            sLAvatarControl.setCameraManualControl(false)
            sLAvatarControl.setAgentHeading(this.neutralAgentHeading)
        }
        this.moveButtonsLayout.setVisibility(8)
        this.buttonStandUp.setVisibility(8)
        this.ownAvatarVisible = false
    }

    /* access modifiers changed from: private */
    fun onNewExternalTexture(gLExternalTexture: GLExternalTexture) {
        this.externalTextureRef.set(gLExternalTexture)
        ((CardboardControlsPlaceholder) findViewById(R.id.controls_placeholder)).setFixedSize(gLExternalTexture.getWidth(), gLExternalTexture.getHeight())
    }

    /* access modifiers changed from: private */
    /* renamed from: onPickedAvatarNameUpdated */
    fun m763com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref17(chatterNameRetriever: ChatterNameRetriever) {
        this.objectNameView.setText(chatterNameRetriever.getResolvedName())
    }

    /* access modifiers changed from: private */
    /* renamed from: onSelectedObjectProfile */
    fun m757com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref11(sLObjectProfileData: SLObjectProfileData) {
        this.objectNameView.setText(sLObjectProfileData.name().or(getString(R.string.object_name_loading)))
    }

    /* access modifiers changed from: private */
    /* renamed from: onViewsInvalidated */
    fun m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12() {
        if (!this.viewDrawPosted.getAndSet(true)) {
            Debug.Printf("Cardboard: posting draw views", Object[0])
            this.handler.post(Runnable(this) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f560$f0

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.25.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.25.$m$0():Unit, class status: UNLOADED
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
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceActiveChatter */
    fun m766com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref4(chatterID: ChatterID) {
        if (this.voiceStatusView != null) {
            this.voiceStatusView.setChatterID(chatterID)
        }
        if (this.userManager == null || chatterID == null) {
            this.voiceChatInfo.unsubscribe()
        } else {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    fun m767com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref5(voiceChatInfo2: VoiceChatInfo) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceLoginStatusChanged */
    fun m764com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref2(bool: Boolean) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onVrTouch */
     public fun m760com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref14(view: View, motionEvent: MotionEvent): Boolean {
        return onVrTouchInternal(motionEvent, false)
    }

     private fun onVrTouchInternal(motionEvent: MotionEvent, z: Boolean): Boolean {
        val i: Int = -1
        val actionMasked: Int = motionEvent.getActionMasked()
        val str: String = actionMasked == 0 ? "down" : actionMasked == 1 ? "up" : null
        if (str != null) {
            val objArr: Array<Any> = Object[3]
            objArr[0] = str
            objArr[1] = Integer.valueOf(this.hoveringOverButton != null ? this.hoveringOverButton.getId() : -1)
            if (this.hoveringPressedButton != null) {
                i = this.hoveringPressedButton.getId()
            }
            objArr[2] = Integer.valueOf(i)
            Debug.Printf("Cardboard: vr touch %s, hover over %d, hover pressed %d", objArr)
        }
        if (this.hoveringPressedButton == null || actionMasked != 1) {
            if (this.currentControlsPage == ControlsPage.pageDefault) {
                if (this.hoveringOverButton == null) {
                    if (!z) {
                        switch (actionMasked) {
                            case 0:
                                startWalking(true)
                                break
                            case 1:
                                stopWalking()
                                break
                            default:
                                Debug.Printf("Cardboard: MotionEvent: %s", motionEvent.toString())
                                break
                        }
                    }
                } else if (this.touchActivatedButtons.contains(this.hoveringOverButton) && actionMasked == 0) {
                    Debug.Printf("Cardboard: touch act: press on button %s", this.hoveringOverButton)
                    this.hoveringPressedButton = this.hoveringOverButton
                    this.hoveringOverButton.dispatchTouchEvent(motionEvent)
                }
            }
            return false
        }
        Debug.Printf("Cardboard: touch act: release on button %s", this.hoveringPressedButton)
        this.hoveringPressedButton.dispatchTouchEvent(motionEvent)
        this.hoveringPressedButton = null
        return true
    }

    /* access modifiers changed from: private */
    fun setControlsPage(controlsPage: ControlsPage) {
        val values: Array<ControlsPage> = ControlsPage.values()
        val length: Int = values.length
        for (Int i = 0; i < length; i++) {
            val controlsPage2: ControlsPage = values[i]
            findViewById(controlsPage2.pageViewId).setVisibility(controlsPage2 == controlsPage ? 0 : 4)
        }
        this.currentControlsPage = controlsPage
    }

    /* access modifiers changed from: private */
    fun showSpeechRecognitionError(str: String) {
        this.speakNowText.setVisibility(4)
        this.speakLevelIndicator.setVisibility(4)
        this.buttonSpeechSend.setVisibility(4)
        this.speechRecognitionResults.setText(str)
        this.isSpeechFinished = true
    }

     private fun startWalking(z: Boolean) {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        if (data != null) {
            this.isWalking = true
            val sLAvatarControl: SLAvatarControl = data.getModules().avatarControl
            sLAvatarControl.setAgentHeading(this.headAgentHeading)
            sLAvatarControl.StartAgentMotion(z ? 2 : 4)
        }
    }

     private fun stopWalking() {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        if (data != null) {
            data.getModules().avatarControl.StopAgentMotion()
            this.isWalking = false
        }
    }

     private fun updateDrawingEnabled() {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        if (data != null && this.renderSettings != null) {
            if (this.isResumed) {
                data.getModules().drawDistance.Enable3DView(this.renderSettings.drawDistance)
            } else {
                data.getModules().drawDistance.Disable3DView()
            }
        }
    }

    /* access modifiers changed from: private */
    fun updateExternalTexturePointer() {
        if (this.hitPointUpdatePosted.getAndSet(false)) {
            synchronized (this.hitPointLock) {
                i = this.postedHitPointX
                i2 = this.postedHitPointY
            }
            onExternalTexturePointer(i, i2)
        }
    }

     private fun updateObjectPanel() {
        val objectIntersectInfo: ObjectIntersectInfo = this.pickedObject.get()
        val sLObjectInfo: SLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (sLObjectInfo == null) {
            setControlsPage(ControlsPage.pageDefault)
        } else if (sLObjectInfo.isAvatar()) {
            this.buttonSit.setVisibility(8)
            this.buttonTouchObject.setVisibility(8)
            this.buttonObjectChat.setVisibility(0)
        } else {
            this.buttonSit.setVisibility(0)
            this.buttonTouchObject.setVisibility(sLObjectInfo.isTouchable() ? 0 : 8)
            this.buttonObjectChat.setVisibility(8)
        }
    }

     private fun updateVoiceIndication() {
        val isVoiceLoggedIn: Boolean = isVoiceLoggedIn()
        val data: CurrentLocationInfo = this.currentLocationInfo.getData()
        this.voiceStatusView.setCanConnect((!isVoiceLoggedIn || data == null || data.parcelVoiceChannel() == null) ? false : true)
        val data2: ChatterID = this.voiceActiveChatter.getData()
        val data3: VoiceChatInfo = this.voiceChatInfo.getData()
        if (data2 == null || data3 == null || data3.state == VoiceChatInfo.VoiceChatState.None) {
            this.buttonSpeak.setVisibility(0)
        } else {
            this.buttonSpeak.setVisibility(4)
        }
    }

    /* access modifiers changed from: protected */
     public fun acceptsDetailFragment(cls: Class<? : Fragment>): Boolean {
        if (!ContactsFragment.class.isAssignableFrom(cls)) {
            return ChatFragment.class.isAssignableFrom(cls)
        }
        return true
    }

     public fun dispatchKeyEvent(keyEvent: KeyEvent): Boolean {
        Debug.Printf("Cardboard: dispatch key event: keycode %d", Integer.valueOf(keyEvent.getKeyCode()))
        if (this.avatarControl.get() != null) {
            switch (keyEvent.getKeyCode()) {
                case 19:
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true)
                        handleMoveControl(MoveControl.Forward, 1.0f)
                    } else if (keyEvent.getAction() == 1) {
                        handleMoveControl(MoveControl.Forward, 0.0f)
                    }
                    return true
                case 20:
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true)
                        handleMoveControl(MoveControl.Backward, 1.0f)
                    } else if (keyEvent.getAction() == 1) {
                        handleMoveControl(MoveControl.Backward, 0.0f)
                    }
                    return true
                case 21:
                    if (keyEvent.getAction() == 0) {
                        handleMoveControl(MoveControl.Left, 1.0f)
                    } else if (keyEvent.getAction() == 1) {
                        handleMoveControl(MoveControl.Left, 0.0f)
                    }
                    return true
                case 22:
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true)
                        handleMoveControl(MoveControl.Right, 1.0f)
                    } else if (keyEvent.getAction() == 1) {
                        handleMoveControl(MoveControl.Right, 0.0f)
                    }
                    return true
                case 23:
                    return true
                case 62:
                case 96:
                    if (keyEvent.getAction() == 0) {
                        onExternalButtonAction(true)
                    } else if (keyEvent.getAction() == 1) {
                        onExternalButtonAction(false)
                    }
                    return true
            }
        }
        return super.dispatchKeyEvent(keyEvent)
    }

     public fun handleBackPressed(): Boolean {
        if (this.currentControlsPage != ControlsPage.pageDetails) {
            return false
        }
        return super.handleBackPressed()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_16797  reason: not valid java name */
    public /* synthetic */ Unit m772lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_16797() {
        val i: Int = 1
        val height: Int = this.speechRecognitionResults.getHeight() / this.speechRecognitionResults.getLineHeight()
        Debug.Printf("Cardboard: setting max lines = %d", Integer.valueOf(height))
        if (height >= 1) {
            i = height
        }
        if (this.speechRecognitionResults.getMaxLines() != i) {
            this.speechRecognitionResults.setMaxLines(i)
            this.speechRecognitionResults.setEllipsize(TextUtils.TruncateAt.END)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_17418  reason: not valid java name */
    public /* synthetic */ Unit m773lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_17418() {
        this.primaryButtonsViewBottom = this.buttonTouch.getTop() + this.buttonTouch.getHeight()
        this.moveButtonsTop = this.moveButtonsLayout.getTop()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338  reason: not valid java name */
    public /* synthetic */ Boolean m774lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 9:
                return this.hoverListener.onHoverEnter(view)
            case 10:
                return this.hoverListener.onHoverExit(view)
            default:
                return false
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_71435  reason: not valid java name */
    public /* synthetic */ Unit m775lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_71435(View view) {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        val data2: CurrentLocationInfo = this.currentLocationInfo.getData()
        if (this.voiceEnabled && data != null && isVoiceLoggedIn() && data2 != null && data2.parcelVoiceChannel() != null) {
            data.getModules().voice.nearbyVoiceChatRequest(data2.parcelVoiceChannel())
        }
    }

    @OnTouch({2131755265})
     public fun onAimControlsTouch(view: View, motionEvent: MotionEvent): Boolean {
        Debug.Printf("Cardboard: aim controls touched, view %s", view)
        switch (motionEvent.getActionMasked()) {
            case 1:
                this.touchRequested.set(true)
                break
        }
        return true
    }

    @OnTouch({2131755260, 2131755262, 2131755261, 2131755263})
     public fun onCamButtonTouch(view: View, motionEvent: MotionEvent): Boolean {
        val f: Float = 1.0f
        val f2: Float = -1.0f
        Debug.Printf("Cardboard: cam button: event %s button %s", motionEvent, view)
        val sLAvatarControl: SLAvatarControl = this.avatarControl.get()
        if (sLAvatarControl != null) {
            if (motionEvent.getActionMasked() == 0) {
                switch (view.getId()) {
                    case R.id.button_move_forward:
                        f2 = 1.0f
                        f = 0.0f
                        break
                    case R.id.button_turn_left:
                        f2 = 0.0f
                        break
                    case R.id.button_move_backward:
                        f = 0.0f
                        break
                    case R.id.button_turn_right:
                        f = -1.0f
                        f2 = 0.0f
                        break
                    default:
                        f = 0.0f
                        f2 = 0.0f
                        break
                }
                sLAvatarControl.startCameraManualControl(0.0f, f2, 0.0f, f)
            } else if (motionEvent.getActionMasked() == 1) {
                sLAvatarControl.stopCameraManualControl()
            }
        }
        return true
    }

    @OnClick({2131755257})
    fun onChatButton() {
        if (this.userManager != null) {
            setControlsPage(ControlsPage.pageDetails)
            val makeFragmentArguments: Bundle = ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null)
            makeFragmentArguments.putBoolean(VR_MODE_TAG, true)
            DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, makeFragmentArguments)
        }
    }

    /* access modifiers changed from: protected */
    fun onCreate(bundle: Bundle) {
        setTheme(R.style.Theme_Linkpoint_Light)
        super.onCreate(bundle)
        requestWindowFeature(1)
        getWindow().setFlags(1024, 1024)
        this.fullscreenMode = FullscreenMode(getWindow())
        setContentView((Int) R.layout.cardboard_layout)
        this.userManager = ActivityUtils.getUserManager(getIntent())
        if (this.userManager == null) {
            finish()
            return
        }
        AndroidCompat.trySetVrModeEnabled(this, true)
        AndroidCompat.setSustainedPerformanceMode(this, true)
        this.renderSettings = RenderSettings(PreferenceManager.getDefaultSharedPreferences(getBaseContext()))
        this.stateHandler = Handler()
        Debug.Printf("Cardboard: creating VR view", Object[0])
        val instance: GlobalOptions = GlobalOptions.getInstance()
        val cardboardControlsPlaceholder: CardboardControlsPlaceholder = (CardboardControlsPlaceholder) findViewById(R.id.controls_placeholder)
        this.onScreenControlsLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.cardboard_controls, cardboardControlsPlaceholder, true)
        cardboardControlsPlaceholder.setOnViewInvalidateListener(CardboardControlsPlaceholder.OnViewInvalidateListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f553$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.19.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.19.$m$0():Unit, class status: UNLOADED
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

        val applyDimension: Int = (Int) TypedValue.applyDimension(2, 16.0f, getResources().getDisplayMetrics())
        this.voiceViewHeightAllowance = instance.getVoiceEnabled() ? (Int) TypedValue.applyDimension(1, VOICE_VIEW_HEIGHT_ALLOWANCE_DP, getResources().getDisplayMetrics()) : 0
        this.renderer = WorldViewRenderer(this.stateHandler, true, this.userManager, applyDimension)
        this.renderer.setDrawDistance(this.renderSettings.drawDistance)
        this.renderer.setAvatarCountLimit(this.renderSettings.avatarCountLimit)
        this.renderer.setForcedTime(instance.getForceDaylightTime(), instance.getForceDaylightHour())
        this.gvrView = GvrView(this)
        this.gvrView.setDistortionCorrectionEnabled(true)
        this.gvrView.setAsyncReprojectionEnabled(true)
        this.gvrView.setRenderer(this.stereoRenderer)
        this.controllerManager = ControllerManager(this, this.controllerManagerEventListener)
        Debug.Printf("Cardboard: has magnet: %b", Boolean.valueOf(this.gvrView.getGvrViewerParams().getHasMagnet()))
        ((FrameLayout) findViewById(R.id.vr_view_placeholder)).addView(this.gvrView, FrameLayout.LayoutParams(-1, -1))
        this.gvrView.setOnCardboardTriggerListener(Runnable(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f559$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.24.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.24.$m$0():Unit, class status: UNLOADED
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

        ButterKnife.bind((Object) this, (View) this.onScreenControlsLayout)
        for (View view : Array<View>{this.buttonTouch, this.buttonSpeak, this.buttonChat, this.buttonSpeechSend, this.buttonSit, this.buttonTouchObject, this.buttonObjectChat, this.buttonMoveForward, this.buttonMoveBackward, this.buttonTurnLeft, this.buttonTurnRight, this.buttonStandUp, this.yesButton, this.noButton}) {
            view.setAlpha(0.5f)
            view.setOnHoverListener(this.onHoverListener)
        }
        this.touchActivatedButtons.add(this.buttonMoveForward)
        this.touchActivatedButtons.add(this.buttonMoveBackward)
        this.touchActivatedButtons.add(this.buttonTurnLeft)
        this.touchActivatedButtons.add(this.buttonTurnRight)
        this.buttonStandUp.setVisibility(8)
        this.moveButtonsLayout.setVisibility(8)
        this.voiceStatusView.setShowActiveChatterName(true)
        this.voiceStatusView.hideBackground()
        this.voiceStatusView.setLightTheme()
        this.voiceStatusView.enableHover(this.hoverListener)
        this.voiceStatusView.setOnCallButtonListener(this.onVoiceCallButtonListener)
        val applyDimension2: Int = (Int) TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics())
        for (Int findViewById : dialogButtonIds) {
            val button: Button = (Button) findViewById(findViewById)
            button.setAlpha(0.5f)
            button.setOnHoverListener(this.onHoverListener)
            button.setOnClickListener(this.onDialogButtonClick)
            button.setPadding(applyDimension2, applyDimension2, applyDimension2, applyDimension2)
            button.setCompoundDrawablePadding(applyDimension2)
        }
        this.dialogQuestionText.setTextColor(-1)
        this.fadingTextViewLog = FadingTextViewLog(this.userManager, this, this.chatsOverlayLayout, -1, 0)
        setControlsPage(ControlsPage.pageDefault)
        this.speechRecognitionResults.getViewTreeObserver().addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f578$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.8.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.8.$m$0():Unit, class status: UNLOADED
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

        this.cardboardPrimaryControls.getViewTreeObserver().addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f579$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.9.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.9.$m$0():Unit, class status: UNLOADED
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

        this.gvrView.setOnTouchListener(View.OnTouchListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f577$f0

            private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.7.$m$0(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.7.$m$0(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

            val Boolean onTouch(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.7.onTouch(android.view.View, android.view.MotionEvent):Boolean, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.7.onTouch(android.view.View, android.view.MotionEvent):Boolean, class status: UNLOADED
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

    /* access modifiers changed from: protected */
    fun onDestroy() {
        if (this.gvrView != null) {
            this.gvrView.setOnCardboardTriggerListener((Runnable) null)
            this.gvrView.shutdown()
            this.gvrView = null
        }
        super.onDestroy()
    }

    /* access modifiers changed from: protected */
     public fun onDetailsStackEmpty(): Boolean {
        super.onDetailsStackEmpty()
        setControlsPage(ControlsPage.pageDefault)
        return true
    }

    fun onNewObjectPopup(sLChatEvent: SLChatEvent) {
        if (sLChatEvent instanceof SLChatScriptDialog) {
            if (this.currentControlsPage != ControlsPage.pageYesNo) {
                val sLChatScriptDialog: SLChatScriptDialog = (SLChatScriptDialog) sLChatEvent
                this.activeScriptDialog = sLChatScriptDialog
                setControlsPage(ControlsPage.pageScriptDialog)
                val buttons: Array<String> = sLChatScriptDialog.getButtons()
                for (Int i = 0; i < dialogButtonIds.length; i++) {
                    val button: Button = (Button) findViewById(dialogButtonIds[i])
                    if (i < buttons.length) {
                        button.setVisibility(0)
                        button.setText(buttons[i])
                    } else {
                        button.setVisibility(8)
                    }
                }
                this.dialogQuestionText.setText(sLChatScriptDialog.getRawText())
            }
        } else if (sLChatEvent instanceof SLChatPermissionRequestEvent) {
            this.activeYesNoEvent = (SLChatYesNoEvent) sLChatEvent
            this.yesNoText.setText(((SLChatPermissionRequestEvent) sLChatEvent).getQuestion(this))
            setControlsPage(ControlsPage.pageYesNo)
        }
    }

    @OnClick({2131755275})
    fun onNoButton() {
        if (!(this.activeYesNoEvent == null || this.userManager == null)) {
            this.activeYesNoEvent.onYesAction(this, this.userManager)
            this.activeYesNoEvent = null
        }
        handlePickedObject((ObjectIntersectInfo) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    @OnClick({2131755270})
    fun onObjectChat() {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        val objectIntersectInfo: ObjectIntersectInfo = this.pickedObject.get()
        val sLObjectInfo: SLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null && this.userManager != null && sLObjectInfo.isAvatar()) {
            startDictation(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId()))
        }
    }

    @OnTouch({2131755266})
    /* renamed from: onObjectControlsTouch */
     public fun m769com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref7(view: View, motionEvent: MotionEvent): Boolean {
        switch (motionEvent.getActionMasked()) {
            case 1:
                handlePickedObject((ObjectIntersectInfo) null)
                setControlsPage(ControlsPage.pageDefault)
                return true
            default:
                return true
        }
    }

    fun onObjectPopupCountChanged(i: Int) {
    }

    @OnClick({2131755269})
    fun onObjectSit() {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        val objectIntersectInfo: ObjectIntersectInfo = this.pickedObject.get()
        val sLObjectInfo: SLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null) {
            data.getModules().avatarControl.SitOnObject(sLObjectInfo.getId())
            handlePickedObject((ObjectIntersectInfo) null)
            setControlsPage(ControlsPage.pageDefault)
        }
    }

    @OnClick({2131755268})
    fun onObjectTouch() {
        val data: SLAgentCircuit = this.agentCircuit.getData()
        val objectIntersectInfo: ObjectIntersectInfo = this.pickedObject.get()
        val sLObjectInfo: SLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null && !sLObjectInfo.isAvatar()) {
            if (objectIntersectInfo.intersectInfo.faceKnown) {
                val absolutePosition: LLVector3 = sLObjectInfo.getAbsolutePosition()
                data.TouchObjectFace(sLObjectInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t)
                return
            }
            data.TouchObject(sLObjectInfo.localID)
        }
    }

    /* access modifiers changed from: protected */
    fun onPause() {
        if (this.speechRecognizer != null) {
            this.speechRecognizer.destroy()
            this.speechRecognizer = null
        }
        this.myAvatarState.unsubscribe()
        if (this.userManager != null) {
            this.userManager.getObjectPopupsManager().removeObjectPopupListener(this)
            this.userManager.getObjectPopupsManager().removePopupWatcher(this)
            try {
                this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(this.chatEventHandler)
            } catch (IllegalArgumentException e) {
            }
        }
        this.isResumed = false
        updateDrawingEnabled()
        if (this.gvrView != null) {
            this.gvrView.onPause()
        }
        this.screenOnFlagHelper.stop()
        super.onPause()
    }

    /* access modifiers changed from: protected */
    fun onResume() {
        super.onResume()
        if (this.gvrView != null) {
            this.gvrView.onResume()
        }
        this.fullscreenMode.goFullscreen()
        this.screenOnFlagHelper.start()
        if (this.speechRecognizer == null) {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                Debug.Printf("Cardboard: speech recognition is available", Object[0])
                this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                this.speechRecognizer.setRecognitionListener(this.recognitionListener)
            } else {
                Debug.Printf("Cardboard: speech recognition is not available", Object[0])
                this.speechRecognizer = null
            }
        }
        this.isResumed = true
        updateDrawingEnabled()
        if (this.userManager != null) {
            this.userManager.getObjectPopupsManager().addPopupWatcher(this)
            this.userManager.getObjectPopupsManager().setObjectPopupListener(this, UIThreadExecutor.getInstance())
            this.myAvatarState.subscribe(this.userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value)
            this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(this.chatEventHandler)
        }
    }

    @OnTouch({2131755282})
    /* renamed from: onScriptDialogOutsideTouch */
     public fun m770com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref8(view: View, motionEvent: MotionEvent): Boolean {
        switch (motionEvent.getActionMasked()) {
            case 1:
                if (this.activeScriptDialog != null) {
                    this.activeScriptDialog.onDialogIgnored(this.userManager)
                    this.activeScriptDialog = null
                }
                handlePickedObject((ObjectIntersectInfo) null)
                setControlsPage(ControlsPage.pageDefault)
                return true
            default:
                return true
        }
    }

    @OnClick({2131755256})
    fun onSpeakButton() {
        if (this.userManager != null) {
            startDictation(ChatterID.getLocalChatterID(this.userManager.getUserID()))
        }
    }

    @OnTouch({2131755277})
    /* renamed from: onSpeakControlsTouch */
     public fun m768com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref6(view: View, motionEvent: MotionEvent): Boolean {
        Debug.Printf("Cardboard: speak controls touched, view %s", view)
        switch (motionEvent.getActionMasked()) {
            case 1:
                closeSpeechControls()
                break
        }
        return true
    }

    @OnClick({2131755281})
    fun onSpeechSendButton() {
        SLAgentCircuit activeAgentCircuit
        if (!Strings.isNullOrEmpty(this.lastSpeechRecognitionResults)) {
            if (!(this.userManager == null || (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) == null || this.dictationChatterID == null)) {
                activeAgentCircuit.SendChatMessage(this.dictationChatterID, this.lastSpeechRecognitionResults)
            }
            this.lastSpeechRecognitionResults = ""
        }
        closeSpeechControls()
    }

    @OnClick({2131755264})
    fun onStandUpButton() {
        val sLAvatarControl: SLAvatarControl = this.avatarControl.get()
        if (sLAvatarControl != null) {
            sLAvatarControl.Stand()
        }
    }

    /* access modifiers changed from: protected */
    fun onStart() {
        super.onStart()
        this.voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled()
        this.controllerManager.start()
        this.controller = this.controllerManager.getController()
        if (this.controller != null) {
            this.controller.setEventListener(this.controllerEventListener)
        }
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID())
            this.voiceLoggedIn.subscribe(this.userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value)
            this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value)
            this.currentLocationInfo.subscribe(this.userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value)
        }
        if (this.voiceEnabled) {
            this.voiceStatusView.setShowWhenInactive(true)
        } else {
            this.voiceStatusView.setShowWhenInactive(false)
        }
    }

    /* access modifiers changed from: protected */
    fun onStop() {
        this.voiceActiveChatter.unsubscribe()
        this.voiceLoggedIn.unsubscribe()
        this.currentLocationInfo.unsubscribe()
        this.agentCircuit.unsubscribe()
        this.voiceStatusView.setChatterID((ChatterID) null)
        this.keypadActive.set(false)
        this.controllerManager.stop()
        super.onStop()
    }

    @OnClick({2131755255})
    fun onTouchButton() {
        setControlsPage(ControlsPage.pageTouchAim)
    }

    fun onWindowFocusChanged(z: Boolean) {
        super.onWindowFocusChanged(z)
        this.fullscreenMode.onWindowFocusChanged(z)
    }

    @OnClick({2131755274})
    fun onYesButton() {
        if (!(this.activeYesNoEvent == null || this.userManager == null)) {
            this.activeYesNoEvent.onYesAction(this, this.userManager)
            this.activeYesNoEvent = null
        }
        handlePickedObject((ObjectIntersectInfo) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    @OnTouch({2131755272})
    /* renamed from: onYesNoOutsideTouch */
     public fun m771com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref9(view: View, motionEvent: MotionEvent): Boolean {
        switch (motionEvent.getActionMasked()) {
            case 1:
                this.activeYesNoEvent = null
                handlePickedObject((ObjectIntersectInfo) null)
                setControlsPage(ControlsPage.pageDefault)
                return true
            default:
                return true
        }
    }

    fun startDictation(chatterID: ChatterID) {
        setControlsPage(ControlsPage.pageSpeech)
        this.dictationChatterID = chatterID
        this.speakNowText.setVisibility(4)
        this.speakLevelIndicator.setVisibility(4)
        this.buttonSpeechSend.setVisibility(4)
        this.speechRecognitionResults.setText("")
        if (this.speechRecognizer != null) {
            this.isSpeechFinished = false
            val intent: Intent = Intent()
            intent.putExtra("android.speech.extra.PARTIAL_RESULTS", true)
            this.speechRecognizer.startListening(intent)
            return
        }
        showSpeechRecognitionError(getString(R.string.speech_recognition_not_available))
    }
}
