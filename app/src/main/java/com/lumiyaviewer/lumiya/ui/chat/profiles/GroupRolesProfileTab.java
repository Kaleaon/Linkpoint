package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class GroupRolesProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private GroupRoleAdapter adapter = null;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupProfile, this.groupRoles, this.myGroupList).withOptionalLoadables(this.groupTitles).withDataChangedListener(this);
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());

    private class GroupRoleAdapter extends BaseAdapter {
        @Nullable
        private GroupRoleDataReply data;
        @Nullable
        private GroupProfileReply groupProfile;
        @Nullable
        private Map<UUID, GroupTitlesReply.GroupData> titlesByRole;

        private GroupRoleAdapter() {
            this.groupProfile = null;
            this.data = null;
            this.titlesByRole = null;
        }

        /* synthetic */ GroupRoleAdapter(GroupRolesProfileTab groupRolesProfileTab, GroupRoleAdapter groupRoleAdapter) {
            this();
        }

        public int getCount() {
            if (this.data != null) {
                return this.data.RoleData_Fields.size();
            }
            return 0;
        }

        public GroupRoleDataReply.RoleData getItem(int i) {
            if (this.data == null || i < 0 || i >= this.data.RoleData_Fields.size()) {
                return null;
            }
            return this.data.RoleData_Fields.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z;
            boolean z2;
            GroupTitlesReply.GroupData groupData;
            int i2 = 1;
            if (view == null) {
                view = LayoutInflater.from(GroupRolesProfileTab.this.getContext()).inflate(R.layout.group_profile_role_list_item, viewGroup, false);
            }
            GroupRoleDataReply.RoleData item = getItem(i);
            if (item != null) {
                int i3 = (!item.RoleID.equals(UUIDPool.ZeroUUID) || this.groupProfile == null) ? item.Members : this.groupProfile.GroupData_Field.GroupMembershipCount;
                ((TextView) view.findViewById(R.id.role_name)).setText(SLMessage.stringFromVariableOEM(item.Name));
                ((TextView) view.findViewById(R.id.role_member_count)).setText(GroupRolesProfileTab.this.getResources().getQuantityString(R.plurals.members, i3, new Object[]{Integer.valueOf(i3)}));
                if (this.titlesByRole == null || (groupData = this.titlesByRole.get(item.RoleID)) == null) {
                    z = false;
                    z2 = false;
                } else {
                    z = groupData.Selected;
                    z2 = true;
                }
                view.findViewById(R.id.role_mine_check_mark).setVisibility(z2 ? 0 : 4);
                TextView textView = (TextView) view.findViewById(R.id.role_name);
                if (!z) {
                    i2 = 0;
                }
                textView.setTypeface((Typeface) null, i2);
            }
            return view;
        }

        public boolean hasStableIds() {
            return false;
        }

        public void setData(@Nullable GroupRoleDataReply groupRoleDataReply, @Nullable GroupTitlesReply groupTitlesReply, @Nullable GroupProfileReply groupProfileReply) {
            this.data = groupRoleDataReply;
            if (groupTitlesReply != null) {
                this.titlesByRole = new HashMap();
                for (GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                    this.titlesByRole.put(groupData.RoleID, groupData);
                }
            } else {
                this.titlesByRole = null;
            }
            this.groupProfile = groupProfileReply;
            notifyDataSetInvalidated();
        }
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
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

    /* access modifiers changed from: private */
    /* renamed from: onAddNewRoleButton */
    public void m497com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTabmthref0(View view) {
        if ((getMyGroupPowers() & 16) != 0) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, (UUID) null));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802  reason: not valid java name */
    public /* synthetic */ void m498lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802(AdapterView adapterView, View view, int i, long j) {
        GroupRoleDataReply.RoleData item;
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, item.RoleID));
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_roles, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new GroupRoleAdapter(this, (GroupRoleAdapter) null);
        }
        ((ListView) inflate.findViewById(R.id.group_profile_roles_list)).setAdapter(this.adapter);
        ((ListView) inflate.findViewById(R.id.group_profile_roles_list)).setOnItemClickListener(new AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f336$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU.1.$m$0(android.widget.AdapterView, android.view.View, int, long):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU.1.$m$0(android.widget.AdapterView, android.view.View, int, long):void, class status: UNLOADED
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
        inflate.findViewById(R.id.add_new_role_button).setOnClickListener(new $Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU(this));
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    public void onLoadableDataChanged() {
        try {
            if (this.myGroupList.get().Groups.get(this.groupRoles.get().GroupData_Field.GroupID) != null && !this.groupTitles.isSubscribed()) {
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        long myGroupPowers = getMyGroupPowers();
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.add_new_role_button).setVisibility((myGroupPowers & 16) != 0 ? 0 : 8);
        }
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.groupTitles.getData(), this.groupProfile.getData());
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        } else if (this.adapter != null) {
            this.adapter.setData((GroupRoleDataReply) null, (GroupTitlesReply) null, (GroupProfileReply) null);
        }
    }
}
