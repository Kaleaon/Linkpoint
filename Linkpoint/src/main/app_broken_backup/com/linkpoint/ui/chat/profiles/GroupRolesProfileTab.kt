package com.linkpoint.ui.chat.profiles

import android.graphics.Typeface
import android.os.Bundle
import androidx.core.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.messages.GroupRoleDataReply
import com.linkpoint.slproto.messages.GroupTitlesReply
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import java.util.HashMap
import java.util.Map
import java.util.UUID
import androidx.annotation.Nullable

class GroupRolesProfileTab : ChatterReloadableFragment : LoadableMonitor.OnLoadableDataChangedListener {
    private GroupRoleAdapter adapter = null
    private SubscriptionData<UUID, GroupProfileReply> groupProfile = SubscriptionData<>(UIThreadExecutor.getInstance())
    private SubscriptionData<UUID, GroupRoleDataReply> groupRoles = SubscriptionData<>(UIThreadExecutor.getInstance())
    private SubscriptionData<UUID, GroupTitlesReply> groupTitles = SubscriptionData<>(UIThreadExecutor.getInstance())
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.groupProfile, this.groupRoles, this.myGroupList).withOptionalLoadables(this.groupTitles).withDataChangedListener(this)
    private SubscriptionData<UUID, AvatarGroupList> myGroupList = SubscriptionData<>(UIThreadExecutor.getInstance())

    private class GroupRoleAdapter : BaseAdapter {
        @Nullable
        private GroupRoleDataReply data
        @Nullable
        private GroupProfileReply groupProfile
        @Nullable
        private Map<UUID, GroupTitlesReply.GroupData> titlesByRole

        private GroupRoleAdapter() {
            this.groupProfile = null
            this.data = null
            this.titlesByRole = null
        }

        /* synthetic */ GroupRoleAdapter(GroupRolesProfileTab groupRolesProfileTab, GroupRoleAdapter groupRoleAdapter) {
            this()
        }

        fun getCount(): Int {
            if (this.data != null) {
                return this.data.RoleData_Fields.size()
            }
            return 0
        }

        GroupRoleDataReply.RoleData getItem(Int i) {
            if (this.data == null || i < 0 || i >= this.data.RoleData_Fields.size()) {
                return null
            }
            return this.data.RoleData_Fields.get(i)
        }

        fun getItemId(Int i): Long {
            return (Long) i
        }

        fun getView(Int i, View view, ViewGroup viewGroup): View {
            GroupTitlesReply.GroupData groupData
            Int i2 = 1
            if (view == null) {
                view = LayoutInflater.from(GroupRolesProfileTab.this.getContext()).inflate(R.layout.group_profile_role_list_item, viewGroup, false)
            }
            GroupRoleDataReply.RoleData item = getItem(i)
            if (item != null) {
                Int i3 = (!item.RoleID.equals(UUIDPool.ZeroUUID) || this.groupProfile == null) ? item.Members : this.groupProfile.GroupData_Field.GroupMembershipCount
                ((view as TextView).findViewById(R.id.role_name)).setText(SLMessage.stringFromVariableOEM(item.Name))
                ((view as TextView).findViewById(R.id.role_member_count)).setText(GroupRolesProfileTab.this.getResources().getQuantityString(R.plurals.members, i3, Any[]{Int.valueOf(i3)}))
                if (this.titlesByRole == null || (groupData = this.titlesByRole.get(item.RoleID)) == null) {
                    z = false
                    z2 = false
                } else {
                    z = groupData.Selected
                    z2 = true
                }
                view.findViewById(R.id.role_mine_check_mark).setVisibility(z2 ? 0 : 4)
                TextView textView = (view as TextView).findViewById(R.id.role_name)
                if (!z) {
                    i2 = 0
                }
                textView.setTypeface((Typeface) null, i2)
            }
            return view
        }

        fun hasStableIds(): Boolean {
            return false
        }

        fun setData(@Nullable GroupRoleDataReply groupRoleDataReply, @Nullable GroupTitlesReply groupTitlesReply, @Nullable GroupProfileReply groupProfileReply): Unit {
            this.data = groupRoleDataReply
            if (groupTitlesReply != null) {
                this.titlesByRole = HashMap()
                for (GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                    this.titlesByRole.put(groupData.RoleID, groupData)
                }
            } else {
                this.titlesByRole = null
            }
            this.groupProfile = groupProfileReply
            notifyDataSetInvalidated()
        }
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID)
        } catch (SubscriptionData.DataNotReadyException e) {
            return null
        }
    }

    private Long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry()
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers
        }
        return 0
    }

    /* access modifiers changed from: private */
    /* renamed from: onAddNewRoleButton */
    fun m497com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTabmthref0(View view): Unit {
        if ((getMyGroupPowers() & 16) != 0) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, (UUID) null))
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802  reason: not valid java name */
    /* synthetic */ Unit m498lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802(AdapterView adapterView, View view, Int i, Long j) {
        GroupRoleDataReply.RoleData item
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, item.RoleID))
        }
    }

    @Nullable
    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_roles, viewGroup, false)
        if (this.adapter == null) {
            this.adapter = GroupRoleAdapter(this, (GroupRoleAdapter) null)
        }
        ((inflate as ListView).findViewById(R.id.group_profile_roles_list)).setAdapter(this.adapter)
        ((inflate as ListView).findViewById(R.id.group_profile_roles_list)).setOnItemClickListener(AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f336$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU.1.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU.1.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, class status: UNLOADED
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

        inflate.findViewById(R.id.add_new_role_button).setOnClickListener($Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU(this))
        ((inflate as LoadingLayout).findViewById(R.id.loading_layout)).setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        this.loadableMonitor.setLoadingLayout((inflate as LoadingLayout).findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail))
        this.loadableMonitor.setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        return inflate
    }

    fun onLoadableDataChanged(): Unit {
        try {
            if (this.myGroupList.get().Groups.get(this.groupRoles.get().GroupData_Field.GroupID) != null && !this.groupTitles.isSubscribed()) {
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID)
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        Long myGroupPowers = getMyGroupPowers()
        View view = getView()
        if (view != null) {
            view.findViewById(R.id.add_new_role_button).setVisibility((myGroupPowers & 16) != 0 ? 0 : 8)
        }
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.groupTitles.getData(), this.groupProfile.getData())
        }
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID): Unit {
        this.loadableMonitor.unsubscribeAll()
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID()
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID)
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID)
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID)
        } else if (this.adapter != null) {
            this.adapter.setData((GroupRoleDataReply) null, (GroupTitlesReply) null, (GroupProfileReply) null)
        }
    }
}
