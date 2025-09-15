package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDerezDialog;
import java.util.UUID;
import javax.annotation.Nullable;

public class ObjectDetailsFragment extends FragmentWithTitle implements ReloadableFragment, View.OnClickListener, LoadableMonitor.OnLoadableDataChangedListener {
    private static final String LOCAL_ID_KEY = "localID";
    private static final int[] objectPayButtons = {R.id.object_pay_button1, R.id.object_pay_button2, R.id.object_pay_button3, R.id.object_pay_button4};
    private final SubscriptionData<SubscriptionSingleKey, Integer> balanceSubscription = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final OnChatEventListener chatEventListener = new OnChatEventListener(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f474$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.3.$m$0(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.3.$m$0(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):void, class status: UNLOADED
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

    };
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.objectProfile).withOptionalLoadables(this.balanceSubscription, this.myAvatarState).withDataChangedListener(this);
    @Nullable
    private MenuItem menuItemObjectBlock = null;
    @Nullable
    private MenuItem menuItemObjectDelete = null;
    @Nullable
    private MenuItem menuItemObjectTake = null;
    @Nullable
    private MenuItem menuItemObjectTakeCopy = null;
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private int objectLocalID = 0;
    private final SubscriptionData<Integer, SLObjectProfileData> objectProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    @Nullable
    private SLObjectProfileData objectProfileData = null;
    private final ChatterNameDisplayer ownerNameDisplayer = new ChatterNameDisplayer();

    private void buyObject() {
        SLObjectProfileData sLObjectProfileData = this.objectProfileData;
        UserManager userManager = getUserManager();
        int i = this.objectLocalID;
        if (sLObjectProfileData != null && userManager != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage((CharSequence) String.format(getString(R.string.object_buy_confirm), new Object[]{sLObjectProfileData.name().or(getString(R.string.object_name_loading)), Integer.valueOf(sLObjectProfileData.salePrice())})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener(i, userManager, sLObjectProfileData) {

                /* renamed from: -$f0 */
                private final /* synthetic */ int f478$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f479$f1;

                /* renamed from: -$f2 */
                private final /* synthetic */ Object f480$f2;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.5.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.5.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new $Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8());
            builder.show();
        }
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291  reason: not valid java name */
    static /* synthetic */ void m669lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291(SLAgentCircuit sLAgentCircuit, SLObjectProfileData sLObjectProfileData, String str, DialogInterface dialogInterface, int i) {
        sLAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.OBJECT, sLObjectProfileData.objectUUID(), str, 15));
        dialogInterface.dismiss();
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012  reason: not valid java name */
    static /* synthetic */ void m671lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012(UserManager userManager, int i, SLObjectProfileData sLObjectProfileData, DialogInterface dialogInterface, int i2) {
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.BuyObject(i, sLObjectProfileData.saleType(), sLObjectProfileData.salePrice());
        }
        dialogInterface.dismiss();
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106  reason: not valid java name */
    static /* synthetic */ void m673lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106(UserManager userManager, SLObjectProfileData sLObjectProfileData, int i, DialogInterface dialogInterface, int i2) {
        SLModules modules;
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
            modules.financialInfo.DoPayObject(sLObjectProfileData.objectUUID(), i);
        }
        dialogInterface.dismiss();
    }

    public static Bundle makeSelection(UUID uuid, int i) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putInt(LOCAL_ID_KEY, i);
        return bundle;
    }

    private void openObjectContents() {
        UserManager userManager = getUserManager();
        if (this.objectProfileData != null && this.objectLocalID != 0 && userManager != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), TaskInventoryFragment.class, TaskInventoryFragment.makeSelection(userManager.getUserID(), this.objectProfileData.objectUUID(), this.objectLocalID));
        }
    }

    private void payObject(int i) {
        SLObjectProfileData sLObjectProfileData = this.objectProfileData;
        UserManager userManager = getUserManager();
        if (sLObjectProfileData != null && userManager != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage((CharSequence) String.format(getString(R.string.object_pay_confirm), new Object[]{sLObjectProfileData.name().or(getString(R.string.object_name_loading)), Integer.valueOf(i)})).setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener(i, userManager, sLObjectProfileData) {

                /* renamed from: -$f0 */
                private final /* synthetic */ int f481$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f482$f1;

                /* renamed from: -$f2 */
                private final /* synthetic */ Object f483$f2;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.6.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.6.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.2.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.2.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            builder.show();
        }
    }

    private void payObjectQuick(int i) {
        PayInfo payInfo;
        ImmutableList<Integer> payPrices;
        int intValue;
        if (this.objectProfileData != null && (payInfo = this.objectProfileData.payInfo()) != null && (payPrices = payInfo.payPrices()) != null && i >= 0 && i < payPrices.size() && (intValue = ((Integer) payPrices.get(i)).intValue()) != 0) {
            payObject(intValue);
        }
    }

    private void showDeadObject() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            view.findViewById(R.id.object_fail_to_load).setVisibility(0);
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted);
            view.findViewById(R.id.object_details).setVisibility(8);
        }
    }

    private void showObject(int i) {
        this.objectLocalID = i;
        UserManager userManager = getUserManager();
        if (userManager != null) {
            this.objectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(i));
            this.myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.balanceSubscription.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
    }

    private void showObjectNotLoaded() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            view.findViewById(R.id.object_fail_to_load).setVisibility(0);
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_profile_cannot_be_loaded);
            view.findViewById(R.id.object_details).setVisibility(8);
        }
    }

    private void showObjectOwnerInfo() {
        UUID ownerUUID;
        UserManager userManager = getUserManager();
        if (this.objectProfileData != null && userManager != null && (ownerUUID = this.objectProfileData.ownerUUID()) != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID)));
        }
    }

    private void showObjectProfile(SLObjectProfileData sLObjectProfileData) {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        this.objectProfileData = sLObjectProfileData;
        View view = getView();
        UserManager userManager = getUserManager();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            if (sLObjectProfileData.isDead()) {
                view.findViewById(R.id.object_fail_to_load).setVisibility(0);
                view.findViewById(R.id.object_details).setVisibility(8);
                ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted);
            } else {
                view.findViewById(R.id.object_details).setVisibility(0);
                view.findViewById(R.id.object_fail_to_load).setVisibility(8);
                view.findViewById(R.id.object_touch_button).setVisibility(sLObjectProfileData.isTouchable() ? 0 : 8);
                ((Button) view.findViewById(R.id.object_touch_button)).setText(Strings.isNullOrEmpty(sLObjectProfileData.touchName()) ? getString(R.string.object_touch_button) : sLObjectProfileData.touchName());
                boolean canSit = (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) ? false : modules.rlvController.canSit();
                MyAvatarState data = this.myAvatarState.getData();
                if (data == null) {
                    z = false;
                } else if (!data.isSitting() || data.sittingOn() != this.objectLocalID) {
                    z = false;
                } else {
                    z = true;
                    canSit = false;
                }
                view.findViewById(R.id.object_sit_button).setVisibility(canSit ? 0 : 8);
                view.findViewById(R.id.object_stand_button).setVisibility(z ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_details_name)).setText(sLObjectProfileData.name().or(getString(R.string.object_name_loading)));
                ((TextView) view.findViewById(R.id.object_details_desc)).setText(sLObjectProfileData.description().or(""));
                view.findViewById(R.id.object_owner_card_view).setVisibility(sLObjectProfileData.ownerUUID() != null ? 0 : 8);
                view.findViewById(R.id.floating_text_card_view).setVisibility(sLObjectProfileData.floatingText().isPresent() ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_hover_text)).setText(sLObjectProfileData.floatingText().or(""));
                view.findViewById(R.id.buy_object_card_view).setVisibility(sLObjectProfileData.saleType() != 0 ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_buy_details)).setText(getString(R.string.object_buy_price_format, Integer.valueOf(sLObjectProfileData.salePrice())));
                Integer data2 = this.balanceSubscription.getData();
                if (data2 != null) {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText(getString(R.string.object_balance_format, data2));
                } else {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText("");
                }
                PayInfo payInfo = sLObjectProfileData.isPayable() ? sLObjectProfileData.payInfo() : null;
                if (payInfo != null) {
                    ImmutableList<Integer> payPrices = payInfo.payPrices();
                    if (payPrices != null) {
                        int i = 0;
                        int i2 = 0;
                        while (i < objectPayButtons.length && i < payPrices.size()) {
                            int intValue = ((Integer) payPrices.get(i)).intValue();
                            int defaultPayPrice = intValue == -2 ? payInfo.defaultPayPrice() : intValue;
                            if (defaultPayPrice <= 0) {
                                view.findViewById(objectPayButtons[i]).setVisibility(8);
                                view.findViewById(objectPayButtons[i]).setTag(R.id.object_pay_price_tag, 0);
                            } else {
                                ((Button) view.findViewById(objectPayButtons[i])).setText(String.format(getString(R.string.pay_button_format), new Object[]{Integer.valueOf(defaultPayPrice)}));
                                view.findViewById(objectPayButtons[i]).setVisibility(0);
                                view.findViewById(objectPayButtons[i]).setTag(R.id.object_pay_price_tag, Integer.valueOf(defaultPayPrice));
                                i2++;
                            }
                            i++;
                        }
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(i2 != 0 ? 0 : 8);
                    } else {
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(8);
                    }
                    if (payInfo.defaultPayPrice() != -1) {
                        if (((EditText) view.findViewById(R.id.object_pay_amount)).getText().toString().equals("")) {
                            if (payInfo.defaultPayPrice() > 0) {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText(getString(R.string.object_pay_amount_format, Integer.valueOf(payInfo.defaultPayPrice())));
                            } else {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText("");
                            }
                        }
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(0);
                    } else {
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(8);
                    }
                    view.findViewById(R.id.pay_object_card_view).setVisibility(0);
                } else {
                    view.findViewById(R.id.pay_object_card_view).setVisibility(8);
                }
            }
        }
        if (userManager != null && (!sLObjectProfileData.isDead())) {
            if (sLObjectProfileData.isPayable() && sLObjectProfileData.payInfo() == null) {
                UUID objectUUID = sLObjectProfileData.objectUUID();
                SLAgentCircuit activeAgentCircuit2 = userManager.getActiveAgentCircuit();
                if (!(activeAgentCircuit2 == null || objectUUID == null)) {
                    activeAgentCircuit2.DoRequestPayPrice(objectUUID);
                }
            }
            UUID ownerUUID = sLObjectProfileData.ownerUUID();
            if (ownerUUID != null) {
                this.ownerNameDisplayer.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID));
            }
        }
        updateOptionsMenu();
    }

    private void sitOnObject() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        UserManager userManager = getUserManager();
        if (userManager != null && this.objectProfileData != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.avatarControl.SitOnObject(this.objectProfileData.objectUUID());
        }
    }

    private void standUp() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        UserManager userManager = getUserManager();
        if (userManager != null && this.objectProfileData != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.avatarControl.Stand();
        }
    }

    private void touchObject() {
        SLAgentCircuit activeAgentCircuit;
        UserManager userManager = getUserManager();
        if (userManager != null && this.objectLocalID != 0 && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.TouchObject(this.objectLocalID);
        }
    }

    private void updateOptionsMenu() {
        boolean z4 = false;
        UserManager userManager = getUserManager();
        if (userManager == null || this.objectProfileData == null) {
            z = false;
            z2 = false;
            z3 = false;
        } else if (!this.objectProfileData.isDead()) {
            z3 = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
            if (userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
                z4 = this.objectProfileData.isCopyable();
            }
            z = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
            z2 = z4;
            z4 = true;
        } else {
            z = false;
            z2 = false;
            z3 = false;
        }
        if (this.menuItemObjectTake != null) {
            this.menuItemObjectTake.setVisible(z3);
        }
        if (this.menuItemObjectTakeCopy != null) {
            this.menuItemObjectTakeCopy.setVisible(z2);
        }
        if (this.menuItemObjectDelete != null) {
            this.menuItemObjectDelete.setVisible(z);
        }
        if (this.menuItemObjectBlock != null) {
            this.menuItemObjectBlock.setVisible(z4);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633  reason: not valid java name */
    public /* synthetic */ void m675lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633(SLChatEvent sLChatEvent) {
        if (isFragmentVisible()) {
            UserManager userManager = getUserManager();
            ChatMessageSource source = sLChatEvent.getSource();
            if ((source instanceof ChatMessageSourceObject) && this.objectProfileData != null && userManager != null && Objects.equal(source.getSourceUUID(), this.objectProfileData.objectUUID())) {
                Toast.makeText(getContext(), sLChatEvent.getPlainTextMessage(getContext(), userManager, false), 1).show();
            }
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        for (int i = 0; i < objectPayButtons.length; i++) {
            if (objectPayButtons[i] == id) {
                payObjectQuick(i);
                return;
            }
        }
        switch (id) {
            case R.id.object_touch_button:
                touchObject();
                return;
            case R.id.object_sit_button:
                sitOnObject();
                return;
            case R.id.object_stand_button:
                standUp();
                return;
            case R.id.object_contents_button:
                openObjectContents();
                return;
            case R.id.object_owner_button:
                showObjectOwnerInfo();
                return;
            case R.id.object_button_buy:
                buyObject();
                return;
            case R.id.object_pay_button:
                try {
                    View view2 = getView();
                    if (view2 != null) {
                        payObject(Integer.parseInt(((EditText) view2.findViewById(R.id.object_pay_amount)).getText().toString()));
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.object_details_menu, menu);
        this.menuItemObjectTake = menu.findItem(R.id.item_object_take);
        this.menuItemObjectTakeCopy = menu.findItem(R.id.item_object_take_copy);
        this.menuItemObjectDelete = menu.findItem(R.id.item_object_delete);
        this.menuItemObjectBlock = menu.findItem(R.id.item_object_block);
        updateOptionsMenu();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Debug.Log("ObjectDetailsFragment: onCreateView called");
        View inflate = layoutInflater.inflate(R.layout.object_details, viewGroup, false);
        this.ownerNameDisplayer.bindViews((TextView) inflate.findViewById(R.id.object_details_owner), (ChatterPicView) inflate.findViewById(R.id.userPicView));
        inflate.findViewById(R.id.no_object_selected).setVisibility(0);
        inflate.findViewById(R.id.object_fail_to_load).setVisibility(8);
        inflate.findViewById(R.id.object_details).setVisibility(8);
        inflate.findViewById(R.id.object_touch_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_sit_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_stand_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_owner_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_button_buy).setOnClickListener(this);
        inflate.findViewById(R.id.object_pay_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_contents_button).setOnClickListener(this);
        for (int findViewById : objectPayButtons) {
            inflate.findViewById(findViewById).setOnClickListener(this);
        }
        final Button button = (Button) inflate.findViewById(R.id.object_pay_button);
        ((EditText) inflate.findViewById(R.id.object_pay_amount)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                try {
                    Integer.parseInt(editable.toString());
                    button.setEnabled(true);
                } catch (NumberFormatException e) {
                    button.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        return inflate;
    }

    public void onDestroyView() {
        this.ownerNameDisplayer.unbindViews();
        super.onDestroyView();
    }

    public void onLoadableDataChanged() {
        Throwable error = this.objectProfile.getError();
        SLObjectProfileData data = this.objectProfile.getData();
        if (error instanceof ObjectsManager.ObjectDoesNotExistException) {
            showDeadObject();
        } else if (error != null || data == null) {
            showObjectNotLoaded();
        } else {
            showObjectProfile(data);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        String str = null;
        UserManager userManager = getUserManager();
        int i = getArguments().getInt(LOCAL_ID_KEY);
        if (!(userManager == null || this.objectLocalID == 0)) {
            switch (menuItem.getItemId()) {
                case R.id.item_object_take:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Take, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_take_copy:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.TakeCopy, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_delete:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Delete, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_block:
                    SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    SLObjectProfileData data = this.objectProfile.getData();
                    if (data != null) {
                        str = data.name().orNull();
                    }
                    if (!(activeAgentCircuit == null || data == null || str == null)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage((int) R.string.object_block_question);
                        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener(activeAgentCircuit, data, str) {

                            /* renamed from: -$f0 */
                            private final /* synthetic */ Object f475$f0;

                            /* renamed from: -$f1 */
                            private final /* synthetic */ Object f476$f1;

                            /* renamed from: -$f2 */
                            private final /* synthetic */ Object f477$f2;

                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.4.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.4.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

                        builder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

                        builder.setCancelable(true);
                        builder.create().show();
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onPause() {
        UserManager userManager = getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().removeObjectMessageListener(this.chatEventListener);
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        UserManager userManager = getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().addObjectMessageListener(this.chatEventListener, UIThreadExecutor.getInstance());
        }
    }

    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.object_details_caption), (String) null);
        showObject(getArguments().getInt(LOCAL_ID_KEY));
    }

    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        this.ownerNameDisplayer.setChatterID((ChatterID) null);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle) {
        if (bundle != null) {
            getArguments().putAll(bundle);
            int i = bundle.getInt(LOCAL_ID_KEY);
            if (isFragmentStarted()) {
                showObject(i);
            }
        }
    }
}
