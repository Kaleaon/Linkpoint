package com.lumiyaviewer.lumiya.ui.minimap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapView;
import java.util.UUID;

public class MinimapFragment extends Fragment implements MinimapView.OnUserClickListener {
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.MinimapBitmap> minimapBitmap = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM(this));
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.UserLocations> userLocations = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f454$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM.1.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM.1.$m$0(java.lang.Object):void, class status: UNLOADED
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

    static Fragment newInstance(UUID uuid) {
        MinimapFragment minimapFragment = new MinimapFragment();
        minimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null));
        return minimapFragment;
    }

    /* access modifiers changed from: private */
    /* renamed from: onMinimapBitmap */
    public void m640com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref0(SLMinimap.MinimapBitmap minimapBitmap2) {
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setMinimapBitmap(minimapBitmap2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onUserLocations */
    public void m641com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref1(SLMinimap.UserLocations userLocations2) {
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setUserLocations(userLocations2);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.minimap_fragment, viewGroup, false);
        ((MinimapView) inflate.findViewById(R.id.minimapView)).setOnUserClickListener(this);
        return inflate;
    }

    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.minimapBitmap.subscribe(userManager.getMinimapBitmapPool(), SubscriptionSingleKey.Value);
            this.userLocations.subscribe(userManager.getUserLocationsPool(), SubscriptionSingleKey.Value);
            return;
        }
        this.minimapBitmap.unsubscribe();
        this.userLocations.unsubscribe();
    }

    public void onStop() {
        this.minimapBitmap.unsubscribe();
        this.userLocations.unsubscribe();
        super.onStop();
    }

    public void onUserClick(UUID uuid) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            Fragment findFragmentById = fragmentManager.findFragmentById(R.id.details);
            if (findFragmentById instanceof NearbyPeopleMinimapFragment) {
                ((NearbyPeopleMinimapFragment) findFragmentById).setSelectedUser(uuid);
            }
        }
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setSelectedUser(uuid);
        }
    }
}
