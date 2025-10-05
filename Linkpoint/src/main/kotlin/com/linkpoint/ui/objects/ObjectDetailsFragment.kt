package com.linkpoint.ui.objects

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.OnChatEventListener
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.modules.mutelist.MuteListEntry
import com.linkpoint.slproto.modules.mutelist.MuteType
import com.linkpoint.slproto.objects.PayInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.manager.MyAvatarState
import com.linkpoint.slproto.users.manager.ObjectsManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.ChatterNameDisplayer
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.objects.ObjectDerezDialog
import java.util.UUID
import javax.annotation.Nullable

class ObjectDetailsFragment : FragmentWithTitle(), ReloadableFragment, View.OnClickListener, LoadableMonitor.OnLoadableDataChangedListener {
    private const val LOCAL_ID_KEY: String = "localID"
    private const val Int[] objectPayButtons = {R.id.object_pay_button1, R.id.object_pay_button2, R.id.object_pay_button3, R.id.object_pay_button4}
    private val SubscriptionData<SubscriptionSingleKey, Integer> balanceSubscription = SubscriptionData<>(UIThreadExecutor.getInstance())
    private val OnChatEventListener chatEventListener = OnChatEventListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f474$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.3.$m$0(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.3.$m$0(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):Unit, class status: UNLOADED
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
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.objectProfile).withOptionalLoadables(this.balanceSubscription, this.myAvatarState).withDataChangedListener(this)
    private MenuItem menuItemObjectBlock = null
    private MenuItem menuItemObjectDelete = null
    private MenuItem menuItemObjectTake = null
    private MenuItem menuItemObjectTakeCopy = null
    private val SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = SubscriptionData<>(UIThreadExecutor.getInstance())
    private Int objectLocalID = 0
    private val SubscriptionData<Integer, SLObjectProfileData> objectProfile = SubscriptionData<>(UIThreadExecutor.getInstance())
    private SLObjectProfileData objectProfileData = null
    private val ChatterNameDisplayer ownerNameDisplayer = ChatterNameDisplayer()

    private Unit buyObject() {
        SLObjectProfileData sLObjectProfileData = this.objectProfileData
        UserManager userManager = getUserManager()
        Int i = this.objectLocalID
        if (sLObjectProfileData != null && userManager != null) {
            AlertDialog.Builder builder = AlertDialog.Builder(getActivity())
            builder.setMessage((CharSequence) String.format(getString(R.string.object_buy_confirm), Object[]{sLObjectProfileData.name().or(getString(R.string.object_name_loading)), Integer.valueOf(sLObjectProfileData.salePrice())})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) DialogInterface.OnClickListener(i, userManager, sLObjectProfileData) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Int f478$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f479$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Object f480$f2

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.5.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.5.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) $Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8())
            builder.show()
        }
    }

    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments())
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291  reason: not valid java name */
    static /* synthetic */ Unit m669lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291(SLAgentCircuit sLAgentCircuit, SLObjectProfileData sLObjectProfileData, String str, DialogInterface dialogInterface, Int i) {
        sLAgentCircuit.getModules().muteList.Block(MuteListEntry(MuteType.OBJECT, sLObjectProfileData.objectUUID(), str, 15))
        dialogInterface.dismiss()
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012  reason: not valid java name */
    static /* synthetic */ Unit m671lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012(UserManager userManager, Int i, SLObjectProfileData sLObjectProfileData, DialogInterface dialogInterface, Int i2) {
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.BuyObject(i, sLObjectProfileData.saleType(), sLObjectProfileData.salePrice())
        }
        dialogInterface.dismiss()
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106  reason: not valid java name */
    static /* synthetic */ Unit m673lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106(UserManager userManager, SLObjectProfileData sLObjectProfileData, Int i, DialogInterface dialogInterface, Int i2) {
        SLModules modules
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
            modules.financialInfo.DoPayObject(sLObjectProfileData.objectUUID(), i)
        }
        dialogInterface.dismiss()
    }

    @JvmStatic
    Bundle makeSelection(UUID uuid, Int i) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        bundle.putInt(LOCAL_ID_KEY, i)
        return bundle
    }

    private Unit openObjectContents() {
        UserManager userManager = getUserManager()
        if (this.objectProfileData != null && this.objectLocalID != 0 && userManager != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), TaskInventoryFragment.class, TaskInventoryFragment.makeSelection(userManager.getUserID(), this.objectProfileData.objectUUID(), this.objectLocalID))
        }
    }

    private Unit payObject(Int i) {
        SLObjectProfileData sLObjectProfileData = this.objectProfileData
        UserManager userManager = getUserManager()
        if (sLObjectProfileData != null && userManager != null) {
            AlertDialog.Builder builder = AlertDialog.Builder(getActivity())
            builder.setMessage((CharSequence) String.format(getString(R.string.object_pay_confirm), Object[]{sLObjectProfileData.name().or(getString(R.string.object_name_loading)), Integer.valueOf(i)})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) DialogInterface.OnClickListener(i, userManager, sLObjectProfileData) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Int f481$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f482$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Object f483$f2

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.6.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.6.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) DialogInterface.OnClickListener() {
                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.2.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.2.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            builder.show()
        }
    }

    private Unit payObjectQuick(Int i) {
        PayInfo payInfo
        ImmutableList<Integer> payPrices
        Int intValue
        if (this.objectProfileData != null && (payInfo = this.objectProfileData.payInfo()) != null && (payPrices = payInfo.payPrices()) != null && i >= 0 && i < payPrices.size() && (intValue = ((Integer) payPrices.get(i)).intValue()) != 0) {
            payObject(intValue)
        }
    }

    private Unit showDeadObject() {
        View view = getView()
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8)
            view.findViewById(R.id.object_fail_to_load).setVisibility(0)
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted)
            view.findViewById(R.id.object_details).setVisibility(8)
        }
    }

    private Unit showObject(Int i) {
        this.objectLocalID = i
        UserManager userManager = getUserManager()
        if (userManager != null) {
            this.objectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(i))
            this.myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value)
            this.balanceSubscription.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value)
        }
    }

    private Unit showObjectNotLoaded() {
        View view = getView()
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8)
            view.findViewById(R.id.object_fail_to_load).setVisibility(0)
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_profile_cannot_be_loaded)
            view.findViewById(R.id.object_details).setVisibility(8)
        }
    }

    private Unit showObjectOwnerInfo() {
        UUID ownerUUID
        UserManager userManager = getUserManager()
        if (this.objectProfileData != null && userManager != null && (ownerUUID = this.objectProfileData.ownerUUID()) != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID)))
        }
    }

    private Unit showObjectProfile(SLObjectProfileData sLObjectProfileData) {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        this.objectProfileData = sLObjectProfileData
        View view = getView()
        UserManager userManager = getUserManager()
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8)
            if (sLObjectProfileData.isDead()) {
                view.findViewById(R.id.object_fail_to_load).setVisibility(0)
                view.findViewById(R.id.object_details).setVisibility(8)
                ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted)
            } else {
                view.findViewById(R.id.object_details).setVisibility(0)
                view.findViewById(R.id.object_fail_to_load).setVisibility(8)
                view.findViewById(R.id.object_touch_button).setVisibility(sLObjectProfileData.isTouchable() ? 0 : 8)
                ((Button) view.findViewById(R.id.object_touch_button)).setText(Strings.isNullOrEmpty(sLObjectProfileData.touchName()) ? getString(R.string.object_touch_button) : sLObjectProfileData.touchName())
                Boolean canSit = (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) ? false : modules.rlvController.canSit()
                MyAvatarState data = this.myAvatarState.getData()
                if (data == null) {
                    z = false
                } else if (!data.isSitting() || data.sittingOn() != this.objectLocalID) {
                    z = false
                } else {
                    z = true
                    canSit = false
                }
                view.findViewById(R.id.object_sit_button).setVisibility(canSit ? 0 : 8)
                view.findViewById(R.id.object_stand_button).setVisibility(z ? 0 : 8)
                ((TextView) view.findViewById(R.id.object_details_name)).setText(sLObjectProfileData.name().or(getString(R.string.object_name_loading)))
                ((TextView) view.findViewById(R.id.object_details_desc)).setText(sLObjectProfileData.description().or(""))
                view.findViewById(R.id.object_owner_card_view).setVisibility(sLObjectProfileData.ownerUUID() != null ? 0 : 8)
                view.findViewById(R.id.floating_text_card_view).setVisibility(sLObjectProfileData.floatingText().isPresent() ? 0 : 8)
                ((TextView) view.findViewById(R.id.object_hover_text)).setText(sLObjectProfileData.floatingText().or(""))
                view.findViewById(R.id.buy_object_card_view).setVisibility(sLObjectProfileData.saleType() != 0 ? 0 : 8)
                ((TextView) view.findViewById(R.id.object_buy_details)).setText(getString(R.string.object_buy_price_format, Integer.valueOf(sLObjectProfileData.salePrice())))
                Integer data2 = this.balanceSubscription.getData()
                if (data2 != null) {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText(getString(R.string.object_balance_format, data2))
                } else {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText("")
                }
                PayInfo payInfo = sLObjectProfileData.isPayable() ? sLObjectProfileData.payInfo() : null
                if (payInfo != null) {
                    ImmutableList<Integer> payPrices = payInfo.payPrices()
                    if (payPrices != null) {
                        Int i = 0
                        Int i2 = 0
                        while (i < objectPayButtons.length && i < payPrices.size()) {
                            Int intValue = ((Integer) payPrices.get(i)).intValue()
                            Int defaultPayPrice = intValue == -2 ? payInfo.defaultPayPrice() : intValue
                            if (defaultPayPrice <= 0) {
                                view.findViewById(objectPayButtons[i]).setVisibility(8)
                                view.findViewById(objectPayButtons[i]).setTag(R.id.object_pay_price_tag, 0)
                            } else {
                                ((Button) view.findViewById(objectPayButtons[i])).setText(String.format(getString(R.string.pay_button_format), Object[]{Integer.valueOf(defaultPayPrice)}))
                                view.findViewById(objectPayButtons[i]).setVisibility(0)
                                view.findViewById(objectPayButtons[i]).setTag(R.id.object_pay_price_tag, Integer.valueOf(defaultPayPrice))
                                i2++
                            }
                            i++
                        }
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(i2 != 0 ? 0 : 8)
                    } else {
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(8)
                    }
                    if (payInfo.defaultPayPrice() != -1) {
                        if (((EditText) view.findViewById(R.id.object_pay_amount)).getText().toString().equals("")) {
                            if (payInfo.defaultPayPrice() > 0) {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText(getString(R.string.object_pay_amount_format, Integer.valueOf(payInfo.defaultPayPrice())))
                            } else {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText("")
                            }
                        }
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(0)
                    } else {
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(8)
                    }
                    view.findViewById(R.id.pay_object_card_view).setVisibility(0)
                } else {
                    view.findViewById(R.id.pay_object_card_view).setVisibility(8)
                }
            }
        }
        if (userManager != null && (!sLObjectProfileData.isDead())) {
            if (sLObjectProfileData.isPayable() && sLObjectProfileData.payInfo() == null) {
                UUID objectUUID = sLObjectProfileData.objectUUID()
                SLAgentCircuit activeAgentCircuit2 = userManager.getActiveAgentCircuit()
                if (!(activeAgentCircuit2 == null || objectUUID == null)) {
                    activeAgentCircuit2.DoRequestPayPrice(objectUUID)
                }
            }
            UUID ownerUUID = sLObjectProfileData.ownerUUID()
            if (ownerUUID != null) {
                this.ownerNameDisplayer.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID))
            }
        }
        updateOptionsMenu()
    }

    private Unit sitOnObject() {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        UserManager userManager = getUserManager()
        if (userManager != null && this.objectProfileData != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.avatarControl.SitOnObject(this.objectProfileData.objectUUID())
        }
    }

    private Unit standUp() {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        UserManager userManager = getUserManager()
        if (userManager != null && this.objectProfileData != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.avatarControl.Stand()
        }
    }

    private Unit touchObject() {
        SLAgentCircuit activeAgentCircuit
        UserManager userManager = getUserManager()
        if (userManager != null && this.objectLocalID != 0 && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.TouchObject(this.objectLocalID)
        }
    }

    private Unit updateOptionsMenu() {
        Boolean z4 = false
        UserManager userManager = getUserManager()
        if (userManager == null || this.objectProfileData == null) {
            z = false
            z2 = false
            z3 = false
        } else if (!this.objectProfileData.isDead()) {
            z3 = userManager.getUserID().equals(this.objectProfileData.ownerUUID())
            if (userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
                z4 = this.objectProfileData.isCopyable()
            }
            z = userManager.getUserID().equals(this.objectProfileData.ownerUUID())
            z2 = z4
            z4 = true
        } else {
            z = false
            z2 = false
            z3 = false
        }
        if (this.menuItemObjectTake != null) {
            this.menuItemObjectTake.setVisible(z3)
        }
        if (this.menuItemObjectTakeCopy != null) {
            this.menuItemObjectTakeCopy.setVisible(z2)
        }
        if (this.menuItemObjectDelete != null) {
            this.menuItemObjectDelete.setVisible(z)
        }
        if (this.menuItemObjectBlock != null) {
            this.menuItemObjectBlock.setVisible(z4)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633  reason: not valid java name */
    public /* synthetic */ Unit m675lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633(SLChatEvent sLChatEvent) {
        if (isFragmentVisible()) {
            UserManager userManager = getUserManager()
            ChatMessageSource source = sLChatEvent.getSource()
            if ((source instanceof ChatMessageSourceObject) && this.objectProfileData != null && userManager != null && Objects.equal(source.getSourceUUID(), this.objectProfileData.objectUUID())) {
                Toast.makeText(getContext(), sLChatEvent.getPlainTextMessage(getContext(), userManager, false), 1).show()
            }
        }
    }

    fun onClick(View view) {
        Int id = view.getId()
        for (Int i = 0; i < objectPayButtons.length; i++) {
            if (objectPayButtons[i] == id) {
                payObjectQuick(i)
                return
            }
        }
        switch (id) {
            case R.id.object_touch_button:
                touchObject()
                return
            case R.id.object_sit_button:
                sitOnObject()
                return
            case R.id.object_stand_button:
                standUp()
                return
            case R.id.object_contents_button:
                openObjectContents()
                return
            case R.id.object_owner_button:
                showObjectOwnerInfo()
                return
            case R.id.object_button_buy:
                buyObject()
                return
            case R.id.object_pay_button:
                try {
                    View view2 = getView()
                    if (view2 != null) {
                        payObject(Integer.parseInt(((EditText) view2.findViewById(R.id.object_pay_amount)).getText().toString()))
                        return
                    }
                    return
                } catch (Exception e) {
                    e.printStackTrace()
                    return
                }
            default:
                return
        }
    }

    fun onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    fun onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.object_details_menu, menu)
        this.menuItemObjectTake = menu.findItem(R.id.item_object_take)
        this.menuItemObjectTakeCopy = menu.findItem(R.id.item_object_take_copy)
        this.menuItemObjectDelete = menu.findItem(R.id.item_object_delete)
        this.menuItemObjectBlock = menu.findItem(R.id.item_object_block)
        updateOptionsMenu()
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        Debug.Log("ObjectDetailsFragment: onCreateView called")
        View inflate = layoutInflater.inflate(R.layout.object_details, viewGroup, false)
        this.ownerNameDisplayer.bindViews((TextView) inflate.findViewById(R.id.object_details_owner), (ChatterPicView) inflate.findViewById(R.id.userPicView))
        inflate.findViewById(R.id.no_object_selected).setVisibility(0)
        inflate.findViewById(R.id.object_fail_to_load).setVisibility(8)
        inflate.findViewById(R.id.object_details).setVisibility(8)
        inflate.findViewById(R.id.object_touch_button).setOnClickListener(this)
        inflate.findViewById(R.id.object_sit_button).setOnClickListener(this)
        inflate.findViewById(R.id.object_stand_button).setOnClickListener(this)
        inflate.findViewById(R.id.object_owner_button).setOnClickListener(this)
        inflate.findViewById(R.id.object_button_buy).setOnClickListener(this)
        inflate.findViewById(R.id.object_pay_button).setOnClickListener(this)
        inflate.findViewById(R.id.object_contents_button).setOnClickListener(this)
        for (Int findViewById : objectPayButtons) {
            inflate.findViewById(findViewById).setOnClickListener(this)
        }
        final Button button = (Button) inflate.findViewById(R.id.object_pay_button)
        ((EditText) inflate.findViewById(R.id.object_pay_amount)).addTextChangedListener(TextWatcher() {
            fun afterTextChanged(Editable editable) {
                try {
                    Integer.parseInt(editable.toString())
                    button.setEnabled(true)
                } catch (NumberFormatException e) {
                    button.setEnabled(false)
                }
            }

            fun beforeTextChanged(CharSequence charSequence, Int i, Int i2, Int i3) {
            }

            fun onTextChanged(CharSequence charSequence, Int i, Int i2, Int i3) {
            }
        return inflate
    }

    fun onDestroyView() {
        this.ownerNameDisplayer.unbindViews()
        super.onDestroyView()
    }

    fun onLoadableDataChanged() {
        Throwable error = this.objectProfile.getError()
        SLObjectProfileData data = this.objectProfile.getData()
        if (error instanceof ObjectsManager.ObjectDoesNotExistException) {
            showDeadObject()
        } else if (error != null || data == null) {
            showObjectNotLoaded()
        } else {
            showObjectProfile(data)
        }
    }

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        String str = null
        UserManager userManager = getUserManager()
        Int i = getArguments().getInt(LOCAL_ID_KEY)
        if (!(userManager == null || this.objectLocalID == 0)) {
            switch (menuItem.getItemId()) {
                case R.id.item_object_take:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Take, userManager.getUserID(), i)
                    return true
                case R.id.item_object_take_copy:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.TakeCopy, userManager.getUserID(), i)
                    return true
                case R.id.item_object_delete:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Delete, userManager.getUserID(), i)
                    return true
                case R.id.item_object_block:
                    SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
                    SLObjectProfileData data = this.objectProfile.getData()
                    if (data != null) {
                        str = data.name().orNull()
                    }
                    if (!(activeAgentCircuit == null || data == null || str == null)) {
                        AlertDialog.Builder builder = AlertDialog.Builder(getContext())
                        builder.setMessage((Int) R.string.object_block_question)
                        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) DialogInterface.OnClickListener(activeAgentCircuit, data, str) {

                            /* renamed from: -$f0 */
                            private val /* synthetic */ Object f475$f0

                            /* renamed from: -$f1 */
                            private val /* synthetic */ Object f476$f1

                            /* renamed from: -$f2 */
                            private val /* synthetic */ Object f477$f2

                            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.4.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.4.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeSwitch(RegionGen.java:298)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:64)
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

                        builder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) DialogInterface.OnClickListener() {
                            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeSwitch(RegionGen.java:298)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:64)
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

                        builder.setCancelable(true)
                        builder.create().show()
                    }
                    return true
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    fun onPause() {
        UserManager userManager = getUserManager()
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().removeObjectMessageListener(this.chatEventListener)
        }
        super.onPause()
    }

    fun onResume() {
        super.onResume()
        UserManager userManager = getUserManager()
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().addObjectMessageListener(this.chatEventListener, UIThreadExecutor.getInstance())
        }
    }

    fun onStart() {
        super.onStart()
        setTitle(getString(R.string.object_details_caption), (String) null)
        showObject(getArguments().getInt(LOCAL_ID_KEY))
    }

    fun onStop() {
        this.loadableMonitor.unsubscribeAll()
        this.ownerNameDisplayer.setChatterID((ChatterID) null)
        super.onStop()
    }

    fun setFragmentArgs(Intent intent, Bundle bundle) {
        if (bundle != null) {
            getArguments().putAll(bundle)
            Int i = bundle.getInt(LOCAL_ID_KEY)
            if (isFragmentStarted()) {
                showObject(i)
            }
        }
    }
}
