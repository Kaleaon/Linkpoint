package com.lumiyaviewer.lumiya.ui.objects;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;

public class TaskInventoryFragment extends FragmentWithTitle {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f501comlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues = null;
    private static final String OBJECT_LOCAL_ID_KEY = "objectLocalId";
    private static final String OBJECT_UUID_KEY = "objectUUID";
    @Nullable
    private SLObjectProfileData objectProfileData = null;
    private Subscription<Integer, SLObjectProfileData> objectProfileSubscription;
    private final Subscription.OnData<SLObjectProfileData> onObjectProfileData = new Subscription.OnData(this) {

        /* renamed from: -$f0  reason: not valid java name */
        private final /* synthetic */ Object f468$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.3.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.3.$m$0(java.lang.Object):void, class status: UNLOADED
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

        {
            /*
                r0 = this;
                r0.<init>()
                r0.f468$f0 = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.objects.$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.AnonymousClass3.<init>(java.lang.Object):void");
        }

    };
    private final Subscription.OnData<SLTaskInventory> onTaskInventoryReceived = new Subscription.OnData<SLTaskInventory>() {
        public void onData(SLTaskInventory sLTaskInventory) {
            SLTaskInventory unused = TaskInventoryFragment.this.taskInventory = sLTaskInventory;
            View view = TaskInventoryFragment.this.getView();
            if (view != null) {
                ListAdapter adapter = ((ListView) view.findViewById(R.id.taskInventoryListView)).getAdapter();
                if (adapter instanceof TaskInventoryListAdapter) {
                    ((TaskInventoryListAdapter) adapter).setData(sLTaskInventory);
                }
                ((TextView) view.findViewById(R.id.taskInventoryEmptyText)).setText(R.string.object_contents_empty);
                view.findViewById(R.id.taskInventoryLoading).setVisibility(8);
            }
        }
    };
    /* access modifiers changed from: private */
    @Nullable
    public SLTaskInventory taskInventory;
    private Subscription<Integer, SLTaskInventory> taskInventorySubscription;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m684getcomlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues() {
        if (f501comlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues != null) {
            return f501comlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues;
        }
        int[] iArr = new int[SLAssetType.values().length];
        try {
            iArr[SLAssetType.AT_ANIMATION.ordinal()] = 3;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SLAssetType.AT_BODYPART.ordinal()] = 4;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SLAssetType.AT_CALLINGCARD.ordinal()] = 5;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[SLAssetType.AT_CATEGORY.ordinal()] = 6;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[SLAssetType.AT_CLOTHING.ordinal()] = 7;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[SLAssetType.AT_GESTURE.ordinal()] = 8;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[SLAssetType.AT_IMAGE_JPEG.ordinal()] = 9;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[SLAssetType.AT_IMAGE_TGA.ordinal()] = 10;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[SLAssetType.AT_LANDMARK.ordinal()] = 11;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[SLAssetType.AT_LINK.ordinal()] = 12;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[SLAssetType.AT_LINK_FOLDER.ordinal()] = 13;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[SLAssetType.AT_LSL_BYTECODE.ordinal()] = 14;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[SLAssetType.AT_LSL_TEXT.ordinal()] = 1;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[SLAssetType.AT_MESH.ordinal()] = 15;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[SLAssetType.AT_NOTECARD.ordinal()] = 2;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[SLAssetType.AT_OBJECT.ordinal()] = 16;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[SLAssetType.AT_SCRIPT.ordinal()] = 17;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[SLAssetType.AT_SIMSTATE.ordinal()] = 18;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[SLAssetType.AT_SOUND.ordinal()] = 19;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[SLAssetType.AT_SOUND_WAV.ordinal()] = 20;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[SLAssetType.AT_TEXTURE.ordinal()] = 21;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[SLAssetType.AT_TEXTURE_TGA.ordinal()] = 22;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[SLAssetType.AT_UNKNOWN.ordinal()] = 23;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[SLAssetType.AT_WIDGET.ordinal()] = 24;
        } catch (NoSuchFieldError e24) {
        }
        f501comlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues = iArr;
        return iArr;
    }

    private boolean canModifyObject() {
        UserManager userManager = getUserManager();
        if (this.objectProfileData == null || userManager == null || !userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
            return false;
        }
        return this.objectProfileData.isModifiable();
    }

    private boolean canModifyObjectContents(SLInventoryEntry sLInventoryEntry) {
        UserManager userManager = getUserManager();
        if (userManager != null) {
            return userManager.getUserID().equals(sLInventoryEntry.ownerUUID) ? (sLInventoryEntry.ownerMask & 16384) != 0 : (sLInventoryEntry.everyoneMask & 16384) != 0;
        }
        return false;
    }

    private void copyAllToInventory(boolean z) {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        boolean z3 = false;
        final int objectLocalID = getObjectLocalID();
        final UserManager userManager = getUserManager();
        if (this.taskInventory != null && this.objectProfileData != null && userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
            SLInventory sLInventory = modules.inventory;
            if (this.taskInventory.entries.size() != 0) {
                if (!userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
                    Toast.makeText(getActivity(), R.string.object_contents_not_owned, 1).show();
                    return;
                }
                if (!z) {
                    Iterator<T> it = this.taskInventory.entries.iterator();
                    while (true) {
                        z2 = z3;
                        if (!it.hasNext()) {
                            break;
                        }
                        z3 = (((SLInventoryEntry) it.next()).ownerMask & 32768) == 0 ? true : z2;
                    }
                    if (z2) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage((int) R.string.object_contents_has_no_copy).setPositiveButton((int) R.string.object_contents_yes_move, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener(this) {

                            /* renamed from: -$f0 */
                            private final /* synthetic */ Object f466$f0;

                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

                        }).setNegativeButton((int) R.string.cancel, (DialogInterface.OnClickListener) new $Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0());
                        builder.create().show();
                        return;
                    }
                }
                String or = this.objectProfileData.name().or(getString(R.string.default_object_contents_folder));
                HashSet hashSet = new HashSet();
                for (SLInventoryEntry sLInventoryEntry : this.taskInventory.entries) {
                    hashSet.add(sLInventoryEntry.uuid);
                }
                final ProgressDialog show = ProgressDialog.show(getContext(), (CharSequence) null, getString(R.string.copying_object_contents), true, true);
                sLInventory.CopyObjectContents(or, objectLocalID, hashSet, new Function<UUID, Void>() {
                    @Nullable
                    public Void apply(@Nullable UUID uuid) {
                        UIThreadExecutor.getInstance().execute(new Runnable(objectLocalID, this, userManager, show, uuid) {

                            /* renamed from: -$f0 */
                            private final /* synthetic */ int f469$f0;

                            /* renamed from: -$f1 */
                            private final /* synthetic */ Object f470$f1;

                            /* renamed from: -$f2 */
                            private final /* synthetic */ Object f471$f2;

                            /* renamed from: -$f3 */
                            private final /* synthetic */ Object f472$f3;

                            /* renamed from: -$f4 */
                            private final /* synthetic */ Object f473$f4;

                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.4.$m$0():void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.4.$m$0():void, class status: UNLOADED
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

                        return null;
                    }

                    /* access modifiers changed from: package-private */
                    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment$2_10938  reason: not valid java name */
                    public /* synthetic */ void m690lambda$com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment$2_10938(UserManager userManager, int i, ProgressDialog progressDialog, UUID uuid) {
                        userManager.getObjectsManager().requestTaskInventoryUpdate(i);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            if (uuid != null) {
                                Debug.Printf("TaskInventory: going to display target folder: %s", uuid);
                                TaskInventoryFragment.this.startActivity(InventoryActivity.makeFolderIntent(TaskInventoryFragment.this.getContext(), userManager.getUserID(), uuid));
                            }
                        }
                    }
            }
        }
    }

    private int getObjectLocalID() {
        return getArguments().getInt(OBJECT_LOCAL_ID_KEY);
    }

    private UUID getObjectUUID() {
        return UUID.fromString(getArguments().getString(OBJECT_UUID_KEY));
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    private void handleObjectClick(int i) {
        SLInventoryEntry item;
        UserManager userManager = getUserManager();
        if (getView() != null && userManager != null) {
            ListAdapter adapter = ((ListView) getView().findViewById(R.id.taskInventoryListView)).getAdapter();
            if ((adapter instanceof TaskInventoryListAdapter) && (item = ((TaskInventoryListAdapter) adapter).getItem(i)) != null) {
                switch (m684getcomlumiyaviewerlumiyaslprotoinventorySLAssetTypeSwitchesValues()[SLAssetType.getByType(item.assetType).ordinal()]) {
                    case 1:
                        if (canModifyObject() && canModifyObjectContents(item)) {
                            startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), (UUID) null, item, true, getObjectUUID(), getObjectLocalID()));
                            return;
                        }
                        return;
                    case 2:
                        if (canModifyObject() && canModifyObjectContents(item)) {
                            startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), (UUID) null, item, false, getObjectUUID(), getObjectLocalID()));
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid2, int i) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid2 != null) {
            bundle.putString(OBJECT_UUID_KEY, uuid2.toString());
        }
        bundle.putInt(OBJECT_LOCAL_ID_KEY, i);
        return bundle;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_10017  reason: not valid java name */
    public /* synthetic */ void m687lambda$com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_10017(DialogInterface dialogInterface, int i) {
        copyAllToInventory(true);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_3527  reason: not valid java name */
    public /* synthetic */ void m688lambda$com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_3527(AdapterView adapterView, View view, int i, long j) {
        handleObjectClick(i);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_5570  reason: not valid java name */
    public /* synthetic */ void m689lambda$com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_5570(SLObjectProfileData sLObjectProfileData) {
        this.objectProfileData = sLObjectProfileData;
        if (this.objectProfileData.name().isPresent()) {
            setTitle(getString(R.string.object_contents_title), this.objectProfileData.name().or(getString(R.string.object_name_loading)));
        }
    }

    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.task_inventory_menu, menu);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.task_inventory, viewGroup, false);
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setAdapter(new TaskInventoryListAdapter(layoutInflater.getContext()));
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setEmptyView(inflate.findViewById(16908292));
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setOnItemClickListener(new AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f467$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.2.$m$0(android.widget.AdapterView, android.view.View, int, long):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.2.$m$0(android.widget.AdapterView, android.view.View, int, long):void, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        return inflate;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_copy_all:
                copyAllToInventory(false);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.object_contents_title), (String) null);
        UserManager userManager = getUserManager();
        if (userManager != null) {
            int objectLocalID = getObjectLocalID();
            this.taskInventorySubscription = userManager.getObjectsManager().getObjectTaskInventory().subscribe(Integer.valueOf(objectLocalID), UIThreadExecutor.getInstance(), this.onTaskInventoryReceived);
            this.objectProfileSubscription = userManager.getObjectsManager().getObjectProfile().subscribe(Integer.valueOf(objectLocalID), UIThreadExecutor.getInstance(), this.onObjectProfileData);
        }
    }

    public void onStop() {
        if (this.taskInventorySubscription != null) {
            this.taskInventorySubscription.unsubscribe();
            this.taskInventorySubscription = null;
        }
        if (this.objectProfileSubscription != null) {
            this.objectProfileSubscription.unsubscribe();
            this.objectProfileSubscription = null;
        }
        super.onStop();
    }
}
