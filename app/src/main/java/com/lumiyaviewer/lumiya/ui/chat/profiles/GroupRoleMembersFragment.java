package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;

public class GroupRoleMembersFragment extends ChatterFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private static final String ROLE_ID_KEY = "role_id";
    @Nullable
    private UUID RoleID;
    private MemberListAdapter adapter = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private boolean canAddMembers = false;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, UUID> groupRoleMemberList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f278$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.2.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.2.$m$0(java.lang.Object):void, class status: UNLOADED
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
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoleMemberList, this.myGroupList, this.groupProfile, this.roleMembers).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener(this);
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<GroupManager.GroupRoleMembersQuery, LazyList<GroupRoleMember>> roleMembers = new SubscriptionData<>(UIThreadExecutor.getInstance());

    private class MemberListAdapter extends RecyclerView.Adapter<MemberViewHolder> {
        private boolean canDeleteMembers = false;
        private boolean canDeleteMyself = false;
        private LazyList<GroupRoleMember> data = null;
        private final LayoutInflater layoutInflater;

        MemberListAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        public int getItemCount() {
            if (this.data != null) {
                return this.data.size();
            }
            return 0;
        }

        public void onBindViewHolder(MemberViewHolder memberViewHolder, int i) {
            if (this.data != null && i >= 0 && i < this.data.size()) {
                memberViewHolder.bindToData(this.data.get(i), this.canDeleteMembers, this.canDeleteMyself);
            }
        }

        public MemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MemberViewHolder(this.layoutInflater.inflate(R.layout.group_role_member_list_item, viewGroup, false), GroupRoleMembersFragment.this.userManager.getUserID());
        }

        public void onViewRecycled(MemberViewHolder memberViewHolder) {
            memberViewHolder.recycle();
        }

        public void setData(LazyList<GroupRoleMember> lazyList, boolean z, boolean z2) {
            this.data = lazyList;
            this.canDeleteMembers = z;
            this.canDeleteMyself = z2;
            notifyDataSetChanged();
        }
    }

    private class MemberViewHolder extends RecyclerView.ViewHolder implements ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private final UUID agentUUID;
        private ChatterID.ChatterIDUser boundChatterID = null;
        private boolean canDelete = false;
        private ChatterNameRetriever chatterNameRetriever = null;
        private final ImageButton roleMemberRemoveButton;
        private final TextView userNameTextView;
        private final ChatterPicView userPicView;

        MemberViewHolder(View view, UUID uuid) {
            super(view);
            this.agentUUID = uuid;
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            this.userPicView = (ChatterPicView) view.findViewById(R.id.userPicView);
            this.roleMemberRemoveButton = (ImageButton) view.findViewById(R.id.role_member_remove_button);
            this.roleMemberRemoveButton.setOnClickListener(this);
        }

        /* access modifiers changed from: package-private */
        public void bindToData(GroupRoleMember groupRoleMember, boolean z, boolean z2) {
            int i = 0;
            ChatterID.ChatterIDUser userChatterID = groupRoleMember != null ? ChatterID.getUserChatterID(this.agentUUID, groupRoleMember.getUserID()) : null;
            if (!Objects.equal(userChatterID, this.boundChatterID)) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose();
                    this.chatterNameRetriever = null;
                }
                this.userNameTextView.setText((CharSequence) null);
                this.boundChatterID = userChatterID;
                if (userChatterID != null) {
                    this.chatterNameRetriever = new ChatterNameRetriever(this.boundChatterID, this, UIThreadExecutor.getInstance());
                    this.userPicView.setChatterID(userChatterID, this.chatterNameRetriever.getResolvedName());
                } else {
                    this.userPicView.setChatterID((ChatterID) null, (String) null);
                }
            }
            if (z) {
                z2 = true;
            } else if (!this.agentUUID.equals(this.boundChatterID.getChatterUUID())) {
                z2 = false;
            }
            this.canDelete = z2;
            ImageButton imageButton = this.roleMemberRemoveButton;
            if (!this.canDelete) {
                i = 8;
            }
            imageButton.setVisibility(i);
        }

        public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever2) {
            if (chatterNameRetriever2 != null) {
                this.userNameTextView.setText(chatterNameRetriever2.getResolvedName());
                this.userPicView.setChatterID(chatterNameRetriever2.chatterID, chatterNameRetriever2.getResolvedName());
            }
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.role_member_remove_button:
                    if (this.boundChatterID != null && this.canDelete) {
                        GroupRoleMembersFragment.this.removeMemberFromRole(this.boundChatterID);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: package-private */
        public void recycle() {
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            this.boundChatterID = null;
            this.userPicView.setChatterID((ChatterID) null, (String) null);
        }
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        if (!(this.chatterID instanceof ChatterID.ChatterIDGroup)) {
            return null;
        }
        try {
            return this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID());
        } catch (SubscriptionData.DataNotReadyException e) {
            return null;
        }
    }

    private long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0;
    }

    public static Bundle makeSelection(ChatterID chatterID, @Nullable UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(ROLE_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupRoleMemberList */
    public void m494com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragmentmthref0(UUID uuid) {
        if (this.userManager != null && (this.chatterID instanceof ChatterID.ChatterIDGroup) && this.RoleID != null) {
            this.roleMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMemberList(), GroupManager.GroupRoleMembersQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID, uuid));
        }
    }

    /* access modifiers changed from: private */
    public void removeMemberFromRole(ChatterID.ChatterIDUser chatterIDUser) {
        new AlertDialog.Builder(getContext()).setTitle(R.string.remove_member_from_role_confirm).setPositiveButton(R.string.yes_remove, new DialogInterface.OnClickListener(this, chatterIDUser) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f279$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f280$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.3.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.3.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
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
        }).setNegativeButton(R.string.cancel, new $Lambda$TbI0imFFmZKCR9nUgEGSvi_8Q0()).create().show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_11502  reason: not valid java name */
    public /* synthetic */ void m495lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_11502(ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RemoveMemberFromRole(this.groupProfile.get().GroupData_Field.GroupID, this.RoleID, chatterIDUser.getChatterUUID());
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_4418  reason: not valid java name */
    public /* synthetic */ void m496lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_4418(View view) {
        if (this.canAddMembers) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupMembersProfileTab.class, GroupMembersProfileTab.makeSelection(this.chatterID, this.RoleID));
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_profile_role_members, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        inflate.findViewById(R.id.add_role_member_button).setOnClickListener(new View.OnClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f277$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.1.$m$0(android.view.View):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.1.$m$0(android.view.View):void, class status: UNLOADED
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

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        this.adapter = new MemberListAdapter(getContext());
        ((RecyclerView) inflate.findViewById(R.id.group_profile_role_members_list)).setAdapter(this.adapter);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    public void onLoadableDataChanged() {
        GroupTitlesReply data;
        boolean z;
        boolean z2;
        AvatarGroupList.AvatarGroupEntry myGroupEntry;
        boolean z3 = true;
        int i = 0;
        if (!(this.userManager == null || (myGroupEntry = getMyGroupEntry()) == null || this.groupTitles.isSubscribed())) {
            this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), myGroupEntry.GroupID);
        }
        this.canAddMembers = false;
        long myGroupPowers = getMyGroupPowers();
        if ((256 & myGroupPowers) != 0) {
            this.canAddMembers = true;
        } else if (!((128 & myGroupPowers) == 0 || (data = this.groupTitles.getData()) == null)) {
            Iterator<T> it = data.GroupData_Fields.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((GroupTitlesReply.GroupData) it.next()).RoleID.equals(this.RoleID)) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            if (z) {
                this.canAddMembers = true;
            }
        }
        if ((myGroupPowers & 512) != 0) {
            GroupProfileReply data2 = this.groupProfile.getData();
            if ((data2 == null || this.RoleID == null) ? false : this.RoleID.equals(data2.GroupData_Field.OwnerRole)) {
                z2 = false;
            } else {
                z2 = true;
                z3 = false;
            }
        } else {
            z3 = false;
            z2 = false;
        }
        View view = getView();
        if (view != null) {
            View findViewById = view.findViewById(R.id.add_role_member_button);
            if (!this.canAddMembers) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        if (this.adapter != null) {
            this.adapter.setData(this.roleMembers.getData(), z2, z3);
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        this.RoleID = UUIDPool.getUUID(getArguments().getString(ROLE_ID_KEY));
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
            Debug.Printf("GroupRoleMemberList: subscribing for group %s", chatterUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
        }
    }
}
