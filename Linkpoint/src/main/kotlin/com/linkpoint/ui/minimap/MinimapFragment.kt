package com.linkpoint.ui.minimap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.modules.SLMinimap
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.minimap.MinimapView
import java.util.UUID

class MinimapFragment : Fragment(), MinimapView.OnUserClickListener {
    private val SubscriptionData<SubscriptionSingleKey, SLMinimap.MinimapBitmap> minimapBitmap = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM(this))
    private val SubscriptionData<SubscriptionSingleKey, SLMinimap.UserLocations> userLocations = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f454$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM.1.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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


    static Fragment newInstance(UUID uuid) {
        MinimapFragment minimapFragment = MinimapFragment()
        minimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null))
        return minimapFragment
    }

    /* access modifiers changed from: private */
    /* renamed from: onMinimapBitmap */
    public Unit m640com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref0(SLMinimap.MinimapBitmap minimapBitmap2) {
        View view = getView()
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setMinimapBitmap(minimapBitmap2)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onUserLocations */
    public Unit m641com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref1(SLMinimap.UserLocations userLocations2) {
        View view = getView()
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setUserLocations(userLocations2)
        }
    }

    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.minimap_fragment, viewGroup, false)
        ((MinimapView) inflate.findViewById(R.id.minimapView)).setOnUserClickListener(this)
        return inflate
    }

    public Unit onStart() {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.minimapBitmap.subscribe(userManager.getMinimapBitmapPool(), SubscriptionSingleKey.Value)
            this.userLocations.subscribe(userManager.getUserLocationsPool(), SubscriptionSingleKey.Value)
            return
        }
        this.minimapBitmap.unsubscribe()
        this.userLocations.unsubscribe()
    }

    public Unit onStop() {
        this.minimapBitmap.unsubscribe()
        this.userLocations.unsubscribe()
        super.onStop()
    }

    public Unit onUserClick(UUID uuid) {
        FragmentManager fragmentManager = getFragmentManager()
        if (fragmentManager != null) {
            Fragment findFragmentById = fragmentManager.findFragmentById(R.id.details)
            if (findFragmentById instanceof NearbyPeopleMinimapFragment) {
                ((NearbyPeopleMinimapFragment) findFragmentById).setSelectedUser(uuid)
            }
        }
        View view = getView()
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setSelectedUser(uuid)
        }
    }
}
