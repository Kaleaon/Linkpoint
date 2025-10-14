package com.lumiyaviewer.lumiya.ui.render

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
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionData
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.render.HeadTransformCompat
import com.lumiyaviewer.lumiya.render.WorldViewRenderer
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData
import com.lumiyaviewer.lumiya.slproto.types.CameraParams
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity
import com.lumiyaviewer.lumiya.ui.render.CardboardControlsPlaceholder
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
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
class CardboardActivity : DetailsActivity : ObjectPopupsManager.ObjectPopupListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues = null
    private Int DEFAULT_FONT_SIZE_SP = 16
    private Int LISTVIEW_SCROLL_DURATION = 500
    private Int LISTVIEW_SCROLL_OFFSET = 100
    private Int RECYCLERVIEW_SCROLL_OFFSET = 100
    private Float VOICE_VIEW_HEIGHT_ALLOWANCE_DP = 60.0f
    String VR_MODE_TAG = "vrMode";
    private Float controlDrawSizeFactor = 1.5f
    private Float controlSizeFactorX = 1.0f
    private Float controlSizeFactorY = 0.75f
    private Float crosshairSize = 0.1f
    /* access modifiers changed from: private */
    Int[] dialogButtonIds = {R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4, R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8, R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12}
    /* access modifiers changed from: private */
    SLChatScriptDialog activeScriptDialog = null
    private SLChatYesNoEvent activeYesNoEvent = null
    private SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f545$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.11.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.11.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    AtomicReference<SLAvatarControl> avatarControl = AtomicReference<>()
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
    private Any chatEventHandler = Any() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$5_68853  reason: not valid java name */
        /* synthetic */ Unit m776lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$5_68853(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            if (CardboardActivity.this.fadingTextViewLog != null) {
                CardboardActivity.this.fadingTextViewLog.handleChatEvent(chatMessageEvent)
            }
        }

        @Subscribe
        Unit onChatMessage(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
            CardboardActivity.this.runOnUiThread(Runnable(this, chatMessageEvent) {

                /* renamed from: -$f0 */
                private /* synthetic */ Any f561$f0

                /* renamed from: -$f1 */
                private /* synthetic */ Any f562$f1

                private /* synthetic */ Unit $m$0(
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
    Controller controller
    /* access modifiers changed from: private */
    AtomicInteger controllerConnectionState = AtomicInteger(0)
    private Controller.EventListener controllerEventListener = Controller.EventListener() {
        @Nullable
        private MoveControl activeMoveControl = null
        private Boolean appButtonPressed = false

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808  reason: not valid java name */
        /* synthetic */ Unit m777lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808() {
            CardboardActivity.this.onExternalButtonAction(true)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948  reason: not valid java name */
        /* synthetic */ Unit m778lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948() {
            CardboardActivity.this.onExternalButtonAction(false)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726  reason: not valid java name */
        /* synthetic */ Unit m779lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726(MoveControl moveControl, Float f) {
            CardboardActivity.this.handleMoveControl(moveControl, f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931  reason: not valid java name */
        /* synthetic */ Unit m780lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931(MoveControl moveControl) {
            CardboardActivity.this.handleMoveControl(moveControl, 0.0f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145  reason: not valid java name */
        /* synthetic */ Unit m781lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145(MoveControl moveControl, Float f) {
            if (CardboardActivity.this.ownAvatarVisible || moveControl == MoveControl.Right || moveControl == MoveControl.Left) {
                CardboardActivity.this.handleMoveControl(moveControl, f)
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581  reason: not valid java name */
        /* synthetic */ Unit m782lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581(MoveControl moveControl) {
            CardboardActivity.this.handleMoveControl(moveControl, 0.0f)
        }

        Unit onConnectionStateChanged(Int i) {
            super.onConnectionStateChanged(i)
            Any[] objArr = Any[1]
            objArr[0] = i == 3 ? "connected" : "disconnected";
            Debug.Printf("Cardboard: Daydream controller is now %s", objArr);
            CardboardActivity.this.controllerConnectionState.set(i)
        }

        Unit onUpdate() {
            MoveControl moveControl
            Float f = 0.0f
            super.onUpdate()
            CardboardActivity.this.controller.update()
            if (CardboardActivity.this.controller.appButtonState && (!this.appButtonPressed)) {
                CardboardActivity.this.runOnUiThread(Runnable(this) {

                    /* renamed from: -$f0 */
                    private /* synthetic */ Any f555$f0

                    private /* synthetic */ Unit $m$0(
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
                    private /* synthetic */ Any f556$f0

                    private /* synthetic */ Unit $m$0(
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
                Float f2 = (CardboardActivity.this.controller.touch.x * 2.0f) - 1.0f
                Float f3 = -((CardboardActivity.this.controller.touch.y * 2.0f) - 1.0f)
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
                            private /* synthetic */ Float f567$f0

                            /* renamed from: -$f1 */
                            private /* synthetic */ Any f568$f1

                            /* renamed from: -$f2 */
                            private /* synthetic */ Any f569$f2

                            private /* synthetic */ Unit $m$0(
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
                        private /* synthetic */ Any f563$f0

                        /* renamed from: -$f1 */
                        private /* synthetic */ Any f564$f1

                        private /* synthetic */ Unit $m$0(
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
                        private /* synthetic */ Float f571$f0

                        /* renamed from: -$f1 */
                        private /* synthetic */ Any f572$f1

                        /* renamed from: -$f2 */
                        private /* synthetic */ Any f573$f2

                        private /* synthetic */ Unit $m$0(
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
                    private /* synthetic */ Any f565$f0

                    /* renamed from: -$f1 */
                    private /* synthetic */ Any f566$f1

                    private /* synthetic */ Unit $m$0(
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
    private ControllerManager.EventListener controllerManagerEventListener = ControllerManager.EventListener() {
        Unit onApiStatusChanged(Int i) {
            Debug.Printf("Cardboard: controller API status: %d", Int.valueOf(i));
        }

        Unit onRecentered() {
            if (CardboardActivity.this.gvrView != null) {
                CardboardActivity.this.gvrView.recenterHeadTracker()
            }
        }
    }
    /* access modifiers changed from: private */
    @Nonnull
    volatile ControlsPage currentControlsPage = ControlsPage.pageDefault
    private SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f549$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.15.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.15.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    private AtomicReference<GLExternalTexture> externalTextureRef = AtomicReference<>((Any) null)
    /* access modifiers changed from: private */
    FadingTextViewLog fadingTextViewLog = null
    private FullscreenMode fullscreenMode
    /* access modifiers changed from: private */
    GvrView gvrView
    /* access modifiers changed from: private */
    @SuppressLint({"HandlerLeak"})
    Handler handler = Handler() {
        Unit handleMessage(Message message) {
            Boolean z = false
            switch (message.what) {
                case 1:
                    if (message.obj != null && (message.obj instanceof ObjectIntersectInfo)) {
                        ObjectIntersectInfo objectIntersectInfo = (ObjectIntersectInfo) message.obj
                        Debug.Printf("Cardboard: PICKED OBJECT isAvatar %b localID %d", Boolean.valueOf(objectIntersectInfo.objInfo.isAvatar()), Int.valueOf(objectIntersectInfo.objInfo.localID));
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
                        SLObjectInfo sLObjectInfo = (SLObjectInfo) message.obj
                        Debug.Printf("Cardboard: touched object isAvatar %b localID %d", Boolean.valueOf(sLObjectInfo.isAvatar()), Int.valueOf(sLObjectInfo.localID));
                        return
                    }
                    return
                default:
                    return
            }
        }
    }
    /* access modifiers changed from: private */
    volatile Float headAgentHeading = 0.0f
    /* access modifiers changed from: private */
    Any hitPointLock = Any()
    /* access modifiers changed from: private */
    AtomicBoolean hitPointUpdatePosted = AtomicBoolean(false)
    private Boolean hitPointValid = false
    private Int hitPointX = 0
    /* access modifiers changed from: private */
    Int hitPointY = 0
    private OnHoverListenerCompat hoverListener = OnHoverListenerCompat() {
        Boolean onHoverEnter(View view) {
            view.setAlpha(1.0f)
            Debug.Printf("Cardboard: hovering enter %d", Int.valueOf(view.getId()));
            View unused = CardboardActivity.this.hoveringOverButton = view
            CardboardActivity.this.m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12()
            return false
        }

        Boolean onHoverExit(View view) {
            view.setAlpha(0.5f)
            Debug.Printf("Cardboard: hovering exit %d", Int.valueOf(view.getId()));
            if (CardboardActivity.this.hoveringOverButton == view) {
                View unused = CardboardActivity.this.hoveringOverButton = null
            }
            CardboardActivity.this.m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12()
            return false
        }
    }
    /* access modifiers changed from: private */
    View hoveringOverButton = null
    private View hoveringPressedButton = null
    /* access modifiers changed from: private */
    volatile Boolean insideControls = false
    private Long insideSince = 0
    private Boolean isResumed = false
    /* access modifiers changed from: private */
    Boolean isSpeechFinished = false
    /* access modifiers changed from: private */
    volatile Boolean isWalking = false
    /* access modifiers changed from: private */
    AtomicBoolean keypadActive = AtomicBoolean(false)
    /* access modifiers changed from: private */
    AtomicDouble keypadTurning = AtomicDouble(0.0d)
    /* access modifiers changed from: private */
    String lastSpeechRecognitionResults = "";
    private Int[] locationInWindow = Int[2]
    @BindView(2131755259)
    ViewGroup moveButtonsLayout
    /* access modifiers changed from: private */
    Int moveButtonsTop = 0
    private SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f546$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.12.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.12.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    volatile Float neutralAgentHeading = Float.NaN
    @BindView(2131755275)
    ImageButton noButton
    @BindView(2131755271)
    TextView objectNameView
    private View.OnClickListener onDialogButtonClick = View.OnClickListener() {
        Unit onClick(View view) {
            if (CardboardActivity.this.activeScriptDialog != null) {
                Int i = 0
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
                SLChatScriptDialog unused = CardboardActivity.this.activeScriptDialog = null
            }
            CardboardActivity.this.handlePickedObject((ObjectIntersectInfo) null)
            CardboardActivity.this.setControlsPage(ControlsPage.pageDefault)
        }
    }
    private View.OnHoverListener onHoverListener = View.OnHoverListener(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f543$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onHover(
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
    private View.OnClickListener onVoiceCallButtonListener = $Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA(this)
    private ImmutableMap<ControlsPage, View.OnTouchListener> outsideTouchListeners = ImmutableMap.of(ControlsPage.pageSpeech, View.OnTouchListener(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f570$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onTouch(
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
        private /* synthetic */ Any f574$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onTouch(
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
        private /* synthetic */ Any f575$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onTouch(
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
        private /* synthetic */ Any f576$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onTouch(
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
        private /* synthetic */ Any f554$f0

        private /* synthetic */ Boolean $m$0(
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

        Boolean onTouch(
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
    volatile Boolean ownAvatarVisible = false
    private ChatterNameRetriever pickedAvatarNameRetriever = null
    /* access modifiers changed from: private */
    AtomicReference<ObjectIntersectInfo> pickedObject = AtomicReference<>()
    /* access modifiers changed from: private */
    Int postedHitPointX = 0
    /* access modifiers changed from: private */
    Int postedHitPointY = 0
    /* access modifiers changed from: private */
    Int primaryButtonsViewBottom = 0
    private RecognitionListener recognitionListener = RecognitionListener() {
        Unit onBeginningOfSpeech() {
            Debug.Printf("Cardboard: beginning of speech", Any[0]);
        }

        Unit onBufferReceived(Byte[] bArr) {
        }

        Unit onEndOfSpeech() {
            Debug.Printf("Cardboard: end of speech", Any[0]);
            CardboardActivity.this.speakLevelIndicator.setVisibility(4)
            CardboardActivity.this.speakNowText.setVisibility(4)
        }

        Unit onError(Int i) {
            String string
            Debug.Printf("Cardboard: speech error %d", Int.valueOf(i));
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

        Unit onEvent(Int i, Bundle bundle) {
        }

        Unit onPartialResults(Bundle bundle) {
            Debug.Printf("Cardboard: speech recognition: got partial results", Any[0]);
            ArrayList<String> stringArrayList = bundle.getStringArrayList("results_recognition");
            if (stringArrayList != null && stringArrayList.size() > 0) {
                String str = stringArrayList.get(0)
                String unused = CardboardActivity.this.lastSpeechRecognitionResults = str
                CardboardActivity.this.speechRecognitionResults.setText(str)
                if (!Strings.isNullOrEmpty(str)) {
                    CardboardActivity.this.buttonSpeechSend.setVisibility(0)
                }
            }
        }

        Unit onReadyForSpeech(Bundle bundle) {
            CardboardActivity.this.speakNowText.setVisibility(0)
        }

        Unit onResults(Bundle bundle) {
            Debug.Printf("Cardboard: speech recognition: got some results", Any[0]);
            ArrayList<String> stringArrayList = bundle.getStringArrayList("results_recognition");
            if (stringArrayList != null && stringArrayList.size() > 0) {
                String str = stringArrayList.get(0)
                CardboardActivity.this.speechRecognitionResults.setText(str)
                String unused = CardboardActivity.this.lastSpeechRecognitionResults = str
                if (!Strings.isNullOrEmpty(str)) {
                    CardboardActivity.this.buttonSpeechSend.setVisibility(0)
                    CardboardActivity.this.speakLevelIndicator.setVisibility(4)
                    Boolean unused2 = CardboardActivity.this.isSpeechFinished = true
                }
            }
        }

        Unit onRmsChanged(Float f) {
            if (!CardboardActivity.this.isSpeechFinished) {
                if (Float.isNaN(CardboardActivity.this.speechRmsMin) || f < CardboardActivity.this.speechRmsMin) {
                    Float unused = CardboardActivity.this.speechRmsMin = f
                }
                if (Float.isNaN(CardboardActivity.this.speechRmsMax) || f > CardboardActivity.this.speechRmsMax) {
                    Float unused2 = CardboardActivity.this.speechRmsMax = f
                }
                Float r0 = CardboardActivity.this.speechRmsMax
                if (r0 - CardboardActivity.this.speechRmsMin < 1.0f) {
                    r0 = CardboardActivity.this.speechRmsMin + 1.0f
                }
                Int round = Math.round(((f - CardboardActivity.this.speechRmsMin) * 100.0f) / (r0 - CardboardActivity.this.speechRmsMin))
                if (round < 0) {
                    round = 0
                }
                if (round > 100) {
                    round = 100
                }
                Debug.Printf("Cardboard: speech recognition: RMS %f", Float.valueOf(f));
                CardboardActivity.this.speakLevelIndicator.setVisibility(0)
                CardboardActivity.this.speakLevelIndicator.setProgress(round)
            }
        }
    }
    /* access modifiers changed from: private */
    RenderSettings renderSettings
    /* access modifiers changed from: private */
    WorldViewRenderer renderer
    private ScreenOnFlagHelper screenOnFlagHelper = ScreenOnFlagHelper(this)
    private Point scrollableViewPoint = Point()
    private SubscriptionData<Int, SLObjectProfileData> selectedObjectProfile = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f547$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.13.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.13.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    Float speechRmsMax = Float.NaN
    /* access modifiers changed from: private */
    Float speechRmsMin = Float.NaN
    private Handler stateHandler
    private GvrView.StereoRenderer stereoRenderer = WorldStereoRenderer()
    private Set<View> touchActivatedButtons = HashSet()
    /* access modifiers changed from: private */
    AtomicBoolean touchRequested = AtomicBoolean(false)
    /* access modifiers changed from: private */
    UserManager userManager
    private AtomicBoolean viewDrawPosted = AtomicBoolean(false)
    private SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f550$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.16.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.16.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

    private SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f551$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.17.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.17.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    private SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedIn = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f548$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.14.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.14.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
    Int voiceViewHeightAllowance
    @BindView(2131755274)
    ImageButton yesButton
    @BindView(2131755276)
    TextView yesNoText

    private enum class ControlsPage {
        pageDefault(R.id.cardboard_primary_controls),
        pageSpeech(R.id.cardboard_speak_controls),
        pageTouchAim(R.id.cardboard_aim_controls),
        pageObject(R.id.cardboard_object_controls),
        pageScriptDialog(R.id.cardboard_script_dialog),
        pageYesNo(R.id.cardboard_yesno_dialog),
        pageDetails(R.id.cardboard_details_page)
        
        Int pageViewId

        private ControlsPage(Int i) {
            this.pageViewId = i
        }
    }

    private class WorldStereoRenderer : GvrView.StereoRenderer {
        private Float TURN_DEGREES = 35.0f
        private Float TURN_DEGREES_PER_MS = 0.02f
        private Float YAW_AVERAGE_FACTOR = 1.0E-4f
        private Boolean agentHeadingAcquired = false
        private Boolean crosshairVisible = false
        private Float[] extTextureMatrixUV = Float[16]
        private GLExternalTexture externalTexture
        private Float[] eyeHitTests = Float[4]
        private Float[] eyeOffset = Float[4]
        private Float[] eyeOffsetMatrix = Float[16]
        private Float[] eyeProjection = Float[32]
        private Boolean[] eyeProjectionValid = Boolean[2]
        private Float eyeSeparation = 0.0f
        private Int[] eyeViewport = Int[4]
        private HeadTransformCompat headTransformCompat = HeadTransformCompat()
        private Long lastFrameTime = 0
        private Int viewportHeight = 0
        private Int viewportWidth = 0

        WorldStereoRenderer() {
            Matrix.setIdentityM(this.eyeOffsetMatrix, 0)
            Matrix.rotateM(this.eyeOffsetMatrix, 0, -90.0f, 1.0f, 0.0f, 0.0f)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer-mthref-0  reason: not valid java name */
        /* synthetic */ Unit m783com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderermthref0() {
            CardboardActivity.this.updateExternalTexturePointer()
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554  reason: not valid java name */
        /* synthetic */ Unit m784lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554() {
            CardboardActivity.this.onNewExternalTexture(this.externalTexture)
            CardboardActivity.this.draw2DUI()
        }

        Unit onDrawEye(Eye eye) {
            Int type = eye.getType()
            Float f = (type == 1 ? -0.5f : 0.5f) * this.eyeSeparation
            for (Int i = 0; i < 4; i++) {
                this.eyeOffset[i] = this.headTransformCompat.rightVector[i] * f
            }
            eye.getViewport().getAsArray(this.eyeViewport, 0)
            eye.getEyeView()
            Int i2 = type == 1 ? 0 : 1
            if (CardboardActivity.this.renderSettings != null && (!this.eyeProjectionValid[i2] || eye.getProjectionChanged())) {
                System.arraycopy(eye.getPerspective(0.5f, (Float) CardboardActivity.this.renderSettings.drawDistance), 0, this.eyeProjection, i2 * 16, 16)
            }
            CardboardActivity.this.renderer.onDrawFrame((GL10) null, this.headTransformCompat, this.eyeOffset, this.eyeViewport, (Float[]) null, (Float[]) null, 0)
            if (this.externalTexture != null) {
                CardboardActivity.this.renderer.drawExternalTexture(this.externalTexture, this.extTextureMatrixUV, f, this.headTransformCompat.pitchDegrees, this.headTransformCompat.useButtonsYaw, CardboardActivity.controlDrawSizeFactor, 1.125f, this.eyeHitTests, type == 1 ? 0 : 2)
                if (this.crosshairVisible) {
                    CardboardActivity.this.renderer.drawCrosshair(CardboardActivity.crosshairSize, f)
                }
            }
        }

        Unit onFinishFrame(Viewport viewport) {
            CardboardActivity.this.renderer.onFinishFrame()
            if (this.externalTexture != null) {
                Int width = (Int) (((((this.eyeHitTests[0] + this.eyeHitTests[2]) / 2.0f) * 2.0f) + 0.5f) * ((Float) this.externalTexture.getWidth()))
                Int height = (Int) (((-(((this.eyeHitTests[1] + this.eyeHitTests[3]) / 2.0f) * 2.0f)) + 0.5f) * ((Float) this.externalTexture.getHeight()))
                synchronized (CardboardActivity.this.hitPointLock) {
                    Int unused = CardboardActivity.this.postedHitPointX = width
                    Int unused2 = CardboardActivity.this.postedHitPointY = height
                }
                if (!CardboardActivity.this.hitPointUpdatePosted.getAndSet(true)) {
                    CardboardActivity.this.runOnUiThread(Runnable(this) {

                        /* renamed from: -$f0 */
                        private /* synthetic */ Any f557$f0

                        private /* synthetic */ Unit $m$0(
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

        Unit onNewFrame(HeadTransform headTransform) {
            SLAvatarControl sLAvatarControl = (SLAvatarControl) CardboardActivity.this.avatarControl.get()
            Long uptimeMillis = SystemClock.uptimeMillis()
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
                Debug.Printf("Cardboard: agent heading acquired: %.2f", Float.valueOf(this.headTransformCompat.viewExtraYaw));
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
                Boolean z = false
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
                Long j = uptimeMillis - this.lastFrameTime
                Float angleMinusAngle = CameraParams.angleMinusAngle(this.headTransformCompat.yawDegrees, this.headTransformCompat.neutralYaw)
                Boolean z2 = CardboardActivity.this.keypadActive.get() || CardboardActivity.this.controllerConnectionState.get() == 3
                Boolean z3 = false
                Boolean z4 = false
                Float f = 1.0f
                if (z2) {
                    Double d = CardboardActivity.this.keypadTurning.get()
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
                Float unused = CardboardActivity.this.neutralAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.viewExtraYaw)
            }
            Float unused2 = CardboardActivity.this.headAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.yawDegrees + this.headTransformCompat.viewExtraYaw)
            if (!Float.isNaN(CardboardActivity.this.neutralAgentHeading) && sLAvatarControl != null) {
                sLAvatarControl.setAgentHeading(CardboardActivity.this.isWalking ? CardboardActivity.this.headAgentHeading : CardboardActivity.this.neutralAgentHeading)
            }
            this.lastFrameTime = uptimeMillis
            CardboardActivity.this.renderer.setOwnAvatarHidden(!CardboardActivity.this.ownAvatarVisible)
            CardboardActivity.this.renderer.onPrepareFrame(this.headTransformCompat)
            if (CardboardActivity.this.touchRequested.getAndSet(false)) {
                CardboardActivity.this.renderer.pickObject((Float) (this.viewportWidth / 2), (Float) (this.viewportHeight / 2), CardboardActivity.this.handler)
            }
            ObjectIntersectInfo objectIntersectInfo = (ObjectIntersectInfo) CardboardActivity.this.pickedObject.get()
            CardboardActivity.this.renderer.setDrawPickedObject(objectIntersectInfo != null ? objectIntersectInfo.objInfo : null)
            this.crosshairVisible = CardboardActivity.this.currentControlsPage == ControlsPage.pageDetails ? CardboardActivity.this.insideControls : false
            if (this.externalTexture != null) {
                this.externalTexture.update(this.extTextureMatrixUV)
            }
        }

        Unit onRendererShutdown() {
            this.externalTexture.release()
            this.externalTexture = null
            CardboardActivity.this.renderer.onRendererShutdown()
        }

        Unit onSurfaceChanged(Int i, Int i2) {
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
                private /* synthetic */ Any f558$f0

                private /* synthetic */ Unit $m$0(
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

        Unit onSurfaceCreated(EGLConfig eGLConfig) {
            CardboardActivity.this.renderer.onSurfaceCreated((GL10) null, eGLConfig, true)
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] m734getcomlumiyaviewerlumiyauirenderMoveControlSwitchesValues() {
        if (f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues != null) {
            return f580comlumiyaviewerlumiyauirenderMoveControlSwitchesValues
        }
        Int[] iArr = Int[MoveControl.values().length]
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

    private Unit closeSpeechControls() {
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
    Unit draw2DUI() {
        GLExternalTexture gLExternalTexture = this.externalTextureRef.get()
        if (gLExternalTexture != null) {
            try {
                Canvas canvas = gLExternalTexture.getCanvas()
                drawExternalViews(canvas)
                gLExternalTexture.postCanvas(canvas)
            } catch (IllegalStateException e) {
            }
        }
    }

    private Unit drawExternalViews(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        this.onScreenControlsLayout.draw(canvas)
    }

    /* access modifiers changed from: private */
    /* renamed from: drawViews */
    Unit m761com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref15() {
        this.viewDrawPosted.set(false)
        Debug.Printf("Cardboard: drawing 2D UI", Any[0]);
        draw2DUI()
    }

    @TargetApi(19)
    private View findMatchingView(ViewGroup viewGroup, Int i, Int i2, Int i3, Int i4, Predicate<View> predicate, Point point) {
        View findMatchingView
        for (Int i5 = 0; i5 < viewGroup.getChildCount(); i5++) {
            View childAt = viewGroup.getChildAt(i5)
            if (childAt.getVisibility() == 0 && childAt.isAttachedToWindow() && predicate.apply(childAt)) {
                childAt.getLocationInWindow(this.locationInWindow)
                Int i6 = this.locationInWindow[0]
                Int i7 = this.locationInWindow[1]
                if (Rect(i6, i7, childAt.getWidth() + i6, childAt.getHeight() + i7).contains(i, i2)) {
                    point.set(i - i6, i2 - i7)
                    return childAt
                }
            }
        }
        Int i8 = 0
        while (true) {
            Int i9 = i8
            if (i9 >= viewGroup.getChildCount()) {
                return null
            }
            View childAt2 = viewGroup.getChildAt(i9)
            if (childAt2.getVisibility() == 0 && childAt2.isAttachedToWindow() && (childAt2 instanceof ViewGroup) && (findMatchingView = findMatchingView((ViewGroup) childAt2, i, i2, 0, 0, predicate, point)) != null) {
                return findMatchingView
            }
            i8 = i9 + 1
        }
    }

    /* access modifiers changed from: private */
    Unit handleMoveControl(@Nonnull MoveControl moveControl, Float f) {
        SLAvatarControl sLAvatarControl = this.avatarControl.get()
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
    Unit handlePickedObject(ObjectIntersectInfo objectIntersectInfo) {
        SLObjectInfo sLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
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
                        private /* synthetic */ Any f552$f0

                        private /* synthetic */ Unit $m$0(
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
                Debug.Printf("ObjectPick: picked object %d", Int.valueOf(sLObjectInfo.localID));
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), Int.valueOf(sLObjectInfo.localID))
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
        this.pickedObject.set((Any) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    /* access modifiers changed from: private */
    /* renamed from: isViewScrollable */
    Boolean m762com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref16(View view) {
        if (!(view instanceof ListView)) {
            return view instanceof RecyclerView
        }
        return true
    }

    private Boolean isVoiceLoggedIn() {
        Boolean data = this.voiceLoggedIn.getData()
        if (data != null) {
            return data.booleanValue()
        }
        return false
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    Unit m754com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref0(SLAgentCircuit sLAgentCircuit) {
        SLAvatarControl sLAvatarControl = null
        updateDrawingEnabled()
        AtomicReference<SLAvatarControl> atomicReference = this.avatarControl
        if (sLAgentCircuit != null) {
            sLAvatarControl = sLAgentCircuit.getModules().avatarControl
        }
        atomicReference.set(sLAvatarControl)
    }

    /* access modifiers changed from: private */
    /* renamed from: onCardboardTrigger */
    Unit m759com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref13() {
        Any[] objArr = Any[3]
        objArr[0] = Int.valueOf(this.hoveringOverButton != null ? this.hoveringOverButton.getId() : -1)
        objArr[1] = Int.valueOf(this.hoveringPressedButton != null ? this.hoveringPressedButton.getId() : -1)
        objArr[2] = Boolean.valueOf(this.hitPointValid)
        Debug.Printf("Cardboard: trigger, hover over %d, hover pressed %d, hitPointValid %b", objArr);
        if (this.hoveringPressedButton == null && this.hitPointValid) {
            Long uptimeMillis = SystemClock.uptimeMillis()
            if (!this.insideControls) {
                View.OnTouchListener onTouchListener = this.outsideTouchListeners.get(this.currentControlsPage)
                Debug.Printf("Cardboard: outside touch, listener %s", onTouchListener);
                if (onTouchListener != null) {
                    onTouchListener.onTouch(findViewById(this.currentControlsPage.pageViewId), MotionEvent.obtain(uptimeMillis, uptimeMillis + 100, 1, 0.0f, 0.0f, 0))
                }
            } else if (this.hoveringOverButton == null || (!this.touchActivatedButtons.contains(this.hoveringOverButton))) {
                MotionEvent obtain = MotionEvent.obtain(this.insideSince, uptimeMillis, 0, (Float) this.hitPointX, (Float) this.hitPointY, 0)
                obtain.setSource(2)
                this.onScreenControlsLayout.dispatchTouchEvent(obtain)
                MotionEvent obtain2 = MotionEvent.obtain(this.insideSince, uptimeMillis + 1, 1, (Float) this.hitPointX, (Float) this.hitPointY, 0)
                obtain2.setSource(2)
                this.onScreenControlsLayout.dispatchTouchEvent(obtain2)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocationChanged */
    Unit m765com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref3(CurrentLocationInfo currentLocationInfo2) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onDetailsOutsideTouch */
    Boolean m756com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref10(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1:
                handleBackPressed()
                return true
            default:
                return true
        }
    }

    /* access modifiers changed from: private */
    Unit onExternalButtonAction(Boolean z) {
        Long uptimeMillis = SystemClock.uptimeMillis()
        if (z) {
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, (Float) this.hitPointX, (Float) this.hitPointY, 0)
            obtain.setSource(2)
            onVrTouchInternal(obtain, true)
            m759com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref13()
            return
        }
        MotionEvent obtain2 = MotionEvent.obtain(uptimeMillis, uptimeMillis, 1, (Float) this.hitPointX, (Float) this.hitPointY, 0)
        obtain2.setSource(2)
        onVrTouchInternal(obtain2, true)
    }

    @TargetApi(14)
    private Unit onExternalTexturePointer(Int i, Int i2) {
        View findMatchingView
        this.hitPointValid = true
        this.hitPointX = i
        this.hitPointY = i2
        GLExternalTexture gLExternalTexture = this.externalTextureRef.get()
        if (gLExternalTexture != null) {
            if (i >= 0 && i < gLExternalTexture.getWidth() && i2 >= 0 && i2 < gLExternalTexture.getHeight()) {
                if (!this.insideControls) {
                    this.insideControls = true
                    this.insideSince = SystemClock.uptimeMillis()
                    MotionEvent obtain = MotionEvent.obtain(this.insideSince, this.insideSince, 9, (Float) i, (Float) i2, 0)
                    obtain.setSource(2)
                    this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain)
                }
                MotionEvent obtain2 = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 7, (Float) i, (Float) i2, 0)
                obtain2.setSource(2)
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain2)
                if (this.currentControlsPage == ControlsPage.pageDetails && (findMatchingView = findMatchingView(this.cardboardDetailsPage, i, i2, 0, 0, Predicate(this) {

                    /* renamed from: -$f0 */
                    private /* synthetic */ Any f544$f0

                    private /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.$m$0(java.lang.Any):Boolean, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.$m$0(java.lang.Any):Boolean, class status: UNLOADED
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

                    Boolean apply(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.apply(java.lang.Any):Boolean, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.render.-$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA.10.apply(java.lang.Any):Boolean, class status: UNLOADED
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
                    Int height = findMatchingView.getHeight()
                    if (this.scrollableViewPoint.y < height / 4) {
                        Debug.Printf("Cardboard: want to scroll up %s", findMatchingView);
                        if (findMatchingView instanceof ListView) {
                            ((ListView) findMatchingView).smoothScrollBy(-100, 500)
                        } else if (findMatchingView instanceof RecyclerView) {
                            ((RecyclerView) findMatchingView).smoothScrollBy(0, -100)
                        }
                    } else if (this.scrollableViewPoint.y > (height * 3) / 4) {
                        Debug.Printf("Cardboard: want to scroll down %s", findMatchingView);
                        if (findMatchingView instanceof ListView) {
                            ((ListView) findMatchingView).smoothScrollBy(100, 500)
                        } else if (findMatchingView instanceof RecyclerView) {
                            ((RecyclerView) findMatchingView).smoothScrollBy(0, 100)
                        }
                    }
                }
            } else if (this.insideControls) {
                this.insideControls = false
                MotionEvent obtain3 = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 10, (Float) i, (Float) i2, 0)
                obtain3.setSource(2)
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain3)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyAvatarState */
    Unit m755com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref1(MyAvatarState myAvatarState2) {
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
    Unit onNewExternalTexture(GLExternalTexture gLExternalTexture) {
        this.externalTextureRef.set(gLExternalTexture)
        ((CardboardControlsPlaceholder) findViewById(R.id.controls_placeholder)).setFixedSize(gLExternalTexture.getWidth(), gLExternalTexture.getHeight())
    }

    /* access modifiers changed from: private */
    /* renamed from: onPickedAvatarNameUpdated */
    Unit m763com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref17(ChatterNameRetriever chatterNameRetriever) {
        this.objectNameView.setText(chatterNameRetriever.getResolvedName())
    }

    /* access modifiers changed from: private */
    /* renamed from: onSelectedObjectProfile */
    Unit m757com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref11(SLObjectProfileData sLObjectProfileData) {
        this.objectNameView.setText(sLObjectProfileData.name().or(getString(R.string.object_name_loading)))
    }

    /* access modifiers changed from: private */
    /* renamed from: onViewsInvalidated */
    Unit m758com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref12() {
        if (!this.viewDrawPosted.getAndSet(true)) {
            Debug.Printf("Cardboard: posting draw views", Any[0]);
            this.handler.post(Runnable(this) {

                /* renamed from: -$f0 */
                private /* synthetic */ Any f560$f0

                private /* synthetic */ Unit $m$0(
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
    Unit m766com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref4(ChatterID chatterID) {
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
    Unit m767com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref5(VoiceChatInfo voiceChatInfo2) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceLoginStatusChanged */
    Unit m764com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref2(Boolean bool) {
        updateVoiceIndication()
    }

    /* access modifiers changed from: private */
    /* renamed from: onVrTouch */
    Boolean m760com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref14(View view, MotionEvent motionEvent) {
        return onVrTouchInternal(motionEvent, false)
    }

    private Boolean onVrTouchInternal(MotionEvent motionEvent, Boolean z) {
        Int i = -1
        Int actionMasked = motionEvent.getActionMasked()
        String str = actionMasked == 0 ? "down" : actionMasked == 1 ? "up" : null;
        if (str != null) {
            Any[] objArr = Any[3]
            objArr[0] = str
            objArr[1] = Int.valueOf(this.hoveringOverButton != null ? this.hoveringOverButton.getId() : -1)
            if (this.hoveringPressedButton != null) {
                i = this.hoveringPressedButton.getId()
            }
            objArr[2] = Int.valueOf(i)
            Debug.Printf("Cardboard: vr touch %s, hover over %d, hover pressed %d", objArr);
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
                                Debug.Printf("Cardboard: MotionEvent: %s", motionEvent.toString());
                                break
                        }
                    }
                } else if (this.touchActivatedButtons.contains(this.hoveringOverButton) && actionMasked == 0) {
                    Debug.Printf("Cardboard: touch act: press on button %s", this.hoveringOverButton);
                    this.hoveringPressedButton = this.hoveringOverButton
                    this.hoveringOverButton.dispatchTouchEvent(motionEvent)
                }
            }
            return false
        }
        Debug.Printf("Cardboard: touch act: release on button %s", this.hoveringPressedButton);
        this.hoveringPressedButton.dispatchTouchEvent(motionEvent)
        this.hoveringPressedButton = null
        return true
    }

    /* access modifiers changed from: private */
    Unit setControlsPage(@Nonnull ControlsPage controlsPage) {
        ControlsPage[] values = ControlsPage.values()
        Int length = values.length
        for (Int i = 0; i < length; i++) {
            ControlsPage controlsPage2 = values[i]
            findViewById(controlsPage2.pageViewId).setVisibility(controlsPage2 == controlsPage ? 0 : 4)
        }
        this.currentControlsPage = controlsPage
    }

    /* access modifiers changed from: private */
    Unit showSpeechRecognitionError(String str) {
        this.speakNowText.setVisibility(4)
        this.speakLevelIndicator.setVisibility(4)
        this.buttonSpeechSend.setVisibility(4)
        this.speechRecognitionResults.setText(str)
        this.isSpeechFinished = true
    }

    private Unit startWalking(Boolean z) {
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null) {
            this.isWalking = true
            SLAvatarControl sLAvatarControl = data.getModules().avatarControl
            sLAvatarControl.setAgentHeading(this.headAgentHeading)
            sLAvatarControl.StartAgentMotion(z ? 2 : 4)
        }
    }

    private Unit stopWalking() {
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null) {
            data.getModules().avatarControl.StopAgentMotion()
            this.isWalking = false
        }
    }

    private Unit updateDrawingEnabled() {
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null && this.renderSettings != null) {
            if (this.isResumed) {
                data.getModules().drawDistance.Enable3DView(this.renderSettings.drawDistance)
            } else {
                data.getModules().drawDistance.Disable3DView()
            }
        }
    }

    /* access modifiers changed from: private */
    Unit updateExternalTexturePointer() {
        if (this.hitPointUpdatePosted.getAndSet(false)) {
            synchronized (this.hitPointLock) {
                i = this.postedHitPointX
                i2 = this.postedHitPointY
            }
            onExternalTexturePointer(i, i2)
        }
    }

    private Unit updateObjectPanel() {
        ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get()
        SLObjectInfo sLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
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

    private Unit updateVoiceIndication() {
        Boolean isVoiceLoggedIn = isVoiceLoggedIn()
        CurrentLocationInfo data = this.currentLocationInfo.getData()
        this.voiceStatusView.setCanConnect((!isVoiceLoggedIn || data == null || data.parcelVoiceChannel() == null) ? false : true)
        ChatterID data2 = this.voiceActiveChatter.getData()
        VoiceChatInfo data3 = this.voiceChatInfo.getData()
        if (data2 == null || data3 == null || data3.state == VoiceChatInfo.VoiceChatState.None) {
            this.buttonSpeak.setVisibility(0)
        } else {
            this.buttonSpeak.setVisibility(4)
        }
    }

    /* access modifiers changed from: protected */
    Boolean acceptsDetailFragment(Class<? : Fragment> cls) {
        if (!ContactsFragment.class.isAssignableFrom(cls)) {
            return ChatFragment.class.isAssignableFrom(cls)
        }
        return true
    }

    Boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Debug.Printf("Cardboard: dispatch key event: keycode %d", Int.valueOf(keyEvent.getKeyCode()));
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

    Boolean handleBackPressed() {
        if (this.currentControlsPage != ControlsPage.pageDetails) {
            return false
        }
        return super.handleBackPressed()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_16797  reason: not valid java name */
    /* synthetic */ Unit m772lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_16797() {
        Int i = 1
        Int height = this.speechRecognitionResults.getHeight() / this.speechRecognitionResults.getLineHeight()
        Debug.Printf("Cardboard: setting max lines = %d", Int.valueOf(height));
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
    /* synthetic */ Unit m773lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_17418() {
        this.primaryButtonsViewBottom = this.buttonTouch.getTop() + this.buttonTouch.getHeight()
        this.moveButtonsTop = this.moveButtonsLayout.getTop()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338  reason: not valid java name */
    /* synthetic */ Boolean m774lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338(View view, MotionEvent motionEvent) {
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
    /* synthetic */ Unit m775lambda$com_lumiyaviewer_lumiya_ui_render_CardboardActivity_71435(View view) {
        SLAgentCircuit data = this.agentCircuit.getData()
        CurrentLocationInfo data2 = this.currentLocationInfo.getData()
        if (this.voiceEnabled && data != null && isVoiceLoggedIn() && data2 != null && data2.parcelVoiceChannel() != null) {
            data.getModules().voice.nearbyVoiceChatRequest(data2.parcelVoiceChannel())
        }
    }

    @OnTouch({2131755265})
    Boolean onAimControlsTouch(View view, MotionEvent motionEvent) {
        Debug.Printf("Cardboard: aim controls touched, view %s", view);
        switch (motionEvent.getActionMasked()) {
            case 1:
                this.touchRequested.set(true)
                break
        }
        return true
    }

    @OnTouch({2131755260, 2131755262, 2131755261, 2131755263})
    Boolean onCamButtonTouch(View view, MotionEvent motionEvent) {
        Float f = 1.0f
        Float f2 = -1.0f
        Debug.Printf("Cardboard: cam button: event %s button %s", motionEvent, view);
        SLAvatarControl sLAvatarControl = this.avatarControl.get()
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
    Unit onChatButton() {
        if (this.userManager != null) {
            setControlsPage(ControlsPage.pageDetails)
            Bundle makeFragmentArguments = ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), (Bundle) null)
            makeFragmentArguments.putBoolean(VR_MODE_TAG, true)
            DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, makeFragmentArguments)
        }
    }

    /* access modifiers changed from: protected */
    Unit onCreate(Bundle bundle) {
        setTheme(R.style.Theme_Lumiya_Light)
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
        Debug.Printf("Cardboard: creating VR view", Any[0]);
        GlobalOptions instance = GlobalOptions.getInstance()
        CardboardControlsPlaceholder cardboardControlsPlaceholder = (CardboardControlsPlaceholder) findViewById(R.id.controls_placeholder)
        this.onScreenControlsLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.cardboard_controls, cardboardControlsPlaceholder, true)
        cardboardControlsPlaceholder.setOnViewInvalidateListener(CardboardControlsPlaceholder.OnViewInvalidateListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f553$f0

            private /* synthetic */ Unit $m$0(
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

        Int applyDimension = (Int) TypedValue.applyDimension(2, 16.0f, getResources().getDisplayMetrics())
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
        Debug.Printf("Cardboard: has magnet: %b", Boolean.valueOf(this.gvrView.getGvrViewerParams().getHasMagnet()));
        ((FrameLayout) findViewById(R.id.vr_view_placeholder)).addView(this.gvrView, FrameLayout.LayoutParams(-1, -1))
        this.gvrView.setOnCardboardTriggerListener(Runnable(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f559$f0

            private /* synthetic */ Unit $m$0(
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

        ButterKnife.bind((Any) this, (View) this.onScreenControlsLayout)
        for (View view : View[]{this.buttonTouch, this.buttonSpeak, this.buttonChat, this.buttonSpeechSend, this.buttonSit, this.buttonTouchObject, this.buttonObjectChat, this.buttonMoveForward, this.buttonMoveBackward, this.buttonTurnLeft, this.buttonTurnRight, this.buttonStandUp, this.yesButton, this.noButton}) {
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
        Int applyDimension2 = (Int) TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics())
        for (Int findViewById : dialogButtonIds) {
            Button button = (Button) findViewById(findViewById)
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
            private /* synthetic */ Any f578$f0

            private /* synthetic */ Unit $m$0(
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
            private /* synthetic */ Any f579$f0

            private /* synthetic */ Unit $m$0(
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
            private /* synthetic */ Any f577$f0

            private /* synthetic */ Boolean $m$0(
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

            Boolean onTouch(
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
    Unit onDestroy() {
        if (this.gvrView != null) {
            this.gvrView.setOnCardboardTriggerListener((Runnable) null)
            this.gvrView.shutdown()
            this.gvrView = null
        }
        super.onDestroy()
    }

    /* access modifiers changed from: protected */
    Boolean onDetailsStackEmpty() {
        super.onDetailsStackEmpty()
        setControlsPage(ControlsPage.pageDefault)
        return true
    }

    Unit onNewObjectPopup(SLChatEvent sLChatEvent) {
        if (sLChatEvent instanceof SLChatScriptDialog) {
            if (this.currentControlsPage != ControlsPage.pageYesNo) {
                SLChatScriptDialog sLChatScriptDialog = (SLChatScriptDialog) sLChatEvent
                this.activeScriptDialog = sLChatScriptDialog
                setControlsPage(ControlsPage.pageScriptDialog)
                Array<String> buttons = sLChatScriptDialog.getButtons()
                for (Int i = 0; i < dialogButtonIds.length; i++) {
                    Button button = (Button) findViewById(dialogButtonIds[i])
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
    Unit onNoButton() {
        if (!(this.activeYesNoEvent == null || this.userManager == null)) {
            this.activeYesNoEvent.onYesAction(this, this.userManager)
            this.activeYesNoEvent = null
        }
        handlePickedObject((ObjectIntersectInfo) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    @OnClick({2131755270})
    Unit onObjectChat() {
        SLAgentCircuit data = this.agentCircuit.getData()
        ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get()
        SLObjectInfo sLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null && this.userManager != null && sLObjectInfo.isAvatar()) {
            startDictation(ChatterID.getUserChatterID(this.userManager.getUserID(), sLObjectInfo.getId()))
        }
    }

    @OnTouch({2131755266})
    /* renamed from: onObjectControlsTouch */
    Boolean m769com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref7(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1:
                handlePickedObject((ObjectIntersectInfo) null)
                setControlsPage(ControlsPage.pageDefault)
                return true
            default:
                return true
        }
    }

    Unit onObjectPopupCountChanged(Int i) {
    }

    @OnClick({2131755269})
    Unit onObjectSit() {
        SLAgentCircuit data = this.agentCircuit.getData()
        ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get()
        SLObjectInfo sLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null) {
            data.getModules().avatarControl.SitOnObject(sLObjectInfo.getId())
            handlePickedObject((ObjectIntersectInfo) null)
            setControlsPage(ControlsPage.pageDefault)
        }
    }

    @OnClick({2131755268})
    Unit onObjectTouch() {
        SLAgentCircuit data = this.agentCircuit.getData()
        ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get()
        SLObjectInfo sLObjectInfo = objectIntersectInfo != null ? objectIntersectInfo.objInfo : null
        if (data != null && sLObjectInfo != null && !sLObjectInfo.isAvatar()) {
            if (objectIntersectInfo.intersectInfo.faceKnown) {
                LLVector3 absolutePosition = sLObjectInfo.getAbsolutePosition()
                data.TouchObjectFace(sLObjectInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t)
                return
            }
            data.TouchObject(sLObjectInfo.localID)
        }
    }

    /* access modifiers changed from: protected */
    Unit onPause() {
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
    Unit onResume() {
        super.onResume()
        if (this.gvrView != null) {
            this.gvrView.onResume()
        }
        this.fullscreenMode.goFullscreen()
        this.screenOnFlagHelper.start()
        if (this.speechRecognizer == null) {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                Debug.Printf("Cardboard: speech recognition is available", Any[0]);
                this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                this.speechRecognizer.setRecognitionListener(this.recognitionListener)
            } else {
                Debug.Printf("Cardboard: speech recognition is not available", Any[0]);
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
    Boolean m770com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref8(View view, MotionEvent motionEvent) {
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
    Unit onSpeakButton() {
        if (this.userManager != null) {
            startDictation(ChatterID.getLocalChatterID(this.userManager.getUserID()))
        }
    }

    @OnTouch({2131755277})
    /* renamed from: onSpeakControlsTouch */
    Boolean m768com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref6(View view, MotionEvent motionEvent) {
        Debug.Printf("Cardboard: speak controls touched, view %s", view);
        switch (motionEvent.getActionMasked()) {
            case 1:
                closeSpeechControls()
                break
        }
        return true
    }

    @OnClick({2131755281})
    Unit onSpeechSendButton() {
        SLAgentCircuit activeAgentCircuit
        if (!Strings.isNullOrEmpty(this.lastSpeechRecognitionResults)) {
            if (!(this.userManager == null || (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) == null || this.dictationChatterID == null)) {
                activeAgentCircuit.SendChatMessage(this.dictationChatterID, this.lastSpeechRecognitionResults)
            }
            this.lastSpeechRecognitionResults = "";
        }
        closeSpeechControls()
    }

    @OnClick({2131755264})
    Unit onStandUpButton() {
        SLAvatarControl sLAvatarControl = this.avatarControl.get()
        if (sLAvatarControl != null) {
            sLAvatarControl.Stand()
        }
    }

    /* access modifiers changed from: protected */
    Unit onStart() {
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
    Unit onStop() {
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
    Unit onTouchButton() {
        setControlsPage(ControlsPage.pageTouchAim)
    }

    Unit onWindowFocusChanged(Boolean z) {
        super.onWindowFocusChanged(z)
        this.fullscreenMode.onWindowFocusChanged(z)
    }

    @OnClick({2131755274})
    Unit onYesButton() {
        if (!(this.activeYesNoEvent == null || this.userManager == null)) {
            this.activeYesNoEvent.onYesAction(this, this.userManager)
            this.activeYesNoEvent = null
        }
        handlePickedObject((ObjectIntersectInfo) null)
        setControlsPage(ControlsPage.pageDefault)
    }

    @OnTouch({2131755272})
    /* renamed from: onYesNoOutsideTouch */
    Boolean m771com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref9(View view, MotionEvent motionEvent) {
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

    Unit startDictation(ChatterID chatterID) {
        setControlsPage(ControlsPage.pageSpeech)
        this.dictationChatterID = chatterID
        this.speakNowText.setVisibility(4)
        this.speakLevelIndicator.setVisibility(4)
        this.buttonSpeechSend.setVisibility(4)
        this.speechRecognitionResults.setText("");
        if (this.speechRecognizer != null) {
            this.isSpeechFinished = false
            Intent intent = Intent()
            intent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
            this.speechRecognizer.startListening(intent)
            return
        }
        showSpeechRecognitionError(getString(R.string.speech_recognition_not_available))
    }
}
