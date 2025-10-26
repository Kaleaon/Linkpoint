package com.linkpoint.ui.chat.profiles

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.messages.GroupRoleDataReply
import com.linkpoint.slproto.messages.GroupTitlesReply
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.GroupManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.BackButtonHandler
import com.linkpoint.ui.common.ChatterFragment
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import java.util.HashSet
import java.util.Iterator
import java.util.Set
import java.util.UUID
import javax.annotation.Nullable

class GroupMemberRolesFragment : ChatterReloadableFragment(), LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler {
    private const val MEMBER_ID_KEY: String = "memberID"
    /* access modifiers changed from: private */
    public UUID MemberID = null
    /* access modifiers changed from: private */
    val SubscriptionData<GroupManager.GroupMemberRolesQuery, Set<UUID>> activeRoles = SubscriptionData<>(UIThreadExecutor.getInstance())
    private MemberRoleAdapter adapter = null
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance())
    /* access modifiers changed from: private */
    val SubscriptionData<UUID, GroupProfileReply> groupProfile = SubscriptionData<>(UIThreadExecutor.getInstance())
    private val SubscriptionData<UUID, UUID> groupRoleMemberList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f286$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.3.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.3.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<UUID, GroupRoleDataReply> groupRoles = SubscriptionData<>(UIThreadExecutor.getInstance())
    /* access modifiers changed from: private */
    val SubscriptionData<UUID, GroupTitlesReply> groupTitles = SubscriptionData<>(UIThreadExecutor.getInstance())
    private Boolean hasChanged = false
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.groupRoles, this.groupProfile, this.myGroupList, this.groupRoleMemberList, this.activeRoles).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener(this)
    private ChatterNameRetriever memberNameRetriever = null
    private val SubscriptionData<UUID, AvatarGroupList> myGroupList = SubscriptionData<>(UIThreadExecutor.getInstance())
    private MenuItem undoMenuItem

    private class MemberRoleAdapter : BaseAdapter() {
        private GroupRoleDataReply data
        private val Set<UUID> selectedRoles

        private MemberRoleAdapter() {
            this.data = null
            this.selectedRoles = HashSet()
        }

        /* synthetic */ MemberRoleAdapter(GroupMemberRolesFragment groupMemberRolesFragment, MemberRoleAdapter memberRoleAdapter) {
            this()
        }

         public fun getCount(): Int {
            if (this.data != null) {
                return this.data.RoleData_Fields.size()
            }
            return 0
        }

        public GroupRoleDataReply.RoleData getItem(Int i) {
            if (this.data == null || i < 0 || i >= this.data.RoleData_Fields.size()) {
                return null
            }
            return this.data.RoleData_Fields.get(i)
        }

         public fun getItemId(i: Int): Long {
            return (Long) i
        }

        public Set<UUID> getSelectedRoles() {
            return this.selectedRoles
        }

         public fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
            if (view == null) {
                view = LayoutInflater.from(GroupMemberRolesFragment.this.getContext()).inflate(R.layout.group_member_role_list_item, viewGroup, false)
            }
            GroupRoleDataReply.RoleData item = getItem(i)
            if (item != null) {
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setText(SLMessage.stringFromVariableOEM(item.Name))
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setChecked(!item.RoleID.equals(UUIDPool.ZeroUUID) ? this.selectedRoles.contains(item.RoleID) : true)
            }
            return view
        }

         public fun hasStableIds(): Boolean {
            return false
        }

        fun setData(groupRoleDataReply: GroupRoleDataReply, set: Set<UUID>) {
            this.data = groupRoleDataReply
            this.selectedRoles.clear()
            if (set != null) {
                this.selectedRoles.addAll(set)
            }
            GroupMemberRolesFragment.this.updateUnsavedChanges()
            notifyDataSetInvalidated()
        }

        fun toggleChecked(uuid: UUID) {
            GroupTitlesReply groupTitlesReply
            if (!uuid.equals(UUIDPool.ZeroUUID) && GroupMemberRolesFragment.this.userManager != null && GroupMemberRolesFragment.this.MemberID != null) {
                val r4: Long = GroupMemberRolesFragment.this.getMyGroupPowers()
                try {
                    val contains: Boolean = ((Set) GroupMemberRolesFragment.this.activeRoles.get()).contains(uuid)
                    val z3: Boolean = !this.selectedRoles.contains(uuid)
                    if (contains == z3) {
                        if (z3) {
                            this.selectedRoles.add(uuid)
                        } else {
                            this.selectedRoles.remove(uuid)
                        }
                        z = true
                    } else {
                        if (z3) {
                            if ((256 & r4) != 0) {
                                z2 = true
                            } else {
                                if ((r4 & 128) != 0 && (groupTitlesReply = (GroupTitlesReply) GroupMemberRolesFragment.this.groupTitles.getData()) != null) {
                                    val it: Iterator<T> = groupTitlesReply.GroupData_Fields.iterator()
                                    while (true) {
                                        if (it.hasNext()) {
                                            if (((GroupTitlesReply.GroupData) it.next()).RoleID.equals(uuid)) {
                                                z2 = true
                                                break
                                            }
                                        } else {
                                            break
                                        }
                                    }
                                }
                                z2 = false
                            }
                            if (z2) {
                                this.selectedRoles.add(uuid)
                                z = true
                            }
                        } else if ((r4 & 512) != 0) {
                            val equals: Boolean = uuid.equals(((GroupProfileReply) GroupMemberRolesFragment.this.groupProfile.get()).GroupData_Field.OwnerRole)
                            val equals2: Boolean = GroupMemberRolesFragment.this.userManager.getUserID().equals(GroupMemberRolesFragment.this.MemberID)
                            if (!equals || equals2) {
                                this.selectedRoles.remove(uuid)
                                z = true
                            }
                        }
                        z = false
                    }
                } catch (SubscriptionData.DataNotReadyException e) {
                    z = false
                }
                if (z) {
                    GroupMemberRolesFragment.this.updateUnsavedChanges()
                    notifyDataSetChanged()
                }
            }
        }
    }

     private fun anyChanges(): Boolean {
        val data: Set = this.activeRoles.getData()
        if (this.adapter == null || data == null) {
            return false
        }
        return !data.equals(this.adapter.getSelectedRoles())
    }

     private fun closeFragment() {
        val activity: FragmentActivity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this)
        }
    }

    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        if (!(this.chatterID instanceof ChatterID.ChatterIDGroup)) {
            return null
        }
        try {
            return this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID())
        } catch (SubscriptionData.DataNotReadyException e) {
            return null
        }
    }

    /* access modifiers changed from: private */
     public fun getMyGroupPowers(): Long {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry()
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers
        }
        return 0
    }

    @JvmStatic
     fun makeSelection(chatterID: ChatterID, uuid: UUID): Bundle {
        val makeSelection: Bundle = ChatterFragment.makeSelection(chatterID)
        if (uuid != null) {
            makeSelection.putString(MEMBER_ID_KEY, uuid.toString())
        }
        return makeSelection
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupRoleMemberList */
    fun m459com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragmentmthref0(uuid: UUID) {
        if (this.userManager != null && (this.chatterID instanceof ChatterID.ChatterIDGroup) && this.MemberID != null) {
            this.activeRoles.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMemberRoleList(), GroupManager.GroupMemberRolesQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, uuid))
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onMemberNameUpdated */
    fun m460com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragmentmthref1(chatterNameRetriever: ChatterNameRetriever) {
        val resolvedName: String = chatterNameRetriever.getResolvedName()
        if (!Strings.isNullOrEmpty(resolvedName)) {
            setTitle(getString(R.string.member_roles_title_format, resolvedName), (String) null)
            return
        }
        setTitle(getString(R.string.name_loading_title), (String) null)
    }

    /* access modifiers changed from: private */
    fun updateUnsavedChanges() {
        val anyChanges: Boolean = anyChanges()
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged)
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425  reason: not valid java name */
    public /* synthetic */ Unit m461lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425(DialogInterface dialogInterface, Int i) {
        dialogInterface.cancel()
        closeFragment()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191  reason: not valid java name */
    public /* synthetic */ Unit m462lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191(AdapterView adapterView, View view, Int i, Long j) {
        GroupRoleDataReply.RoleData item
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            this.adapter.toggleChecked(item.RoleID)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245  reason: not valid java name */
    public /* synthetic */ Unit m463lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245(DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        try {
            if (!(this.adapter == null || this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDGroup))) {
                val selectedRoles: Set<UUID> = this.adapter.getSelectedRoles()
                val set: Set = this.activeRoles.get()
                val hashSet: HashSet = HashSet(selectedRoles)
                hashSet.removeAll(set)
                val hashSet2: HashSet = HashSet(set)
                hashSet2.removeAll(selectedRoles)
                this.agentCircuit.get().getModules().groupManager.RequestMemberRoleChanges(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, hashSet, hashSet2)
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        closeFragment()
    }

     public fun onBackButtonPressed(): Boolean {
        if (!anyChanges()) {
            return false
        }
        AlertDialog.Builder builder = AlertDialog.Builder(getContext())
        builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", $Lambda$jWSiK5iqzZfaogto6grdML6fzQ(this)).setNegativeButton("No", DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f284$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        builder.create().show()
        return true
    }

    fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
        setShowChatterTitle(false)
    }

    fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.group_member_roles_menu, menu)
        this.undoMenuItem = menu.findItem(R.id.item_undo)
        this.undoMenuItem.setVisible(this.hasChanged)
    }

     public fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        val inflate: View = layoutInflater.inflate(R.layout.group_member_roles, viewGroup, false)
        if (this.adapter == null) {
            this.adapter = MemberRoleAdapter(this, (MemberRoleAdapter) null)
        }
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setAdapter(this.adapter)
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setOnItemClickListener(AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f285$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.2.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.2.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, class status: UNLOADED
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

        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout))
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail))
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout))
        return inflate
    }

    fun onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.activeRoles.getData())
        }
        try {
            if (this.userManager != null) {
                AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry()
                if (myGroupEntry != null) {
                    Debug.Printf("GroupMemberRoles: my group powers are 0x%x", Long.valueOf(myGroupEntry.GroupPowers))
                    if (!this.groupTitles.isSubscribed()) {
                        this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID)
                        return
                    }
                    return
                }
                Debug.Printf("GroupMemberRoles: not my group", Object[0])
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
    }

     public fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        switch (menuItem.getItemId()) {
            case R.id.item_undo:
                try {
                    if (this.adapter != null) {
                        this.adapter.setData(this.groupRoles.get(), this.activeRoles.get())
                        break
                    }
                } catch (SubscriptionData.DataNotReadyException e) {
                    break
                }
                break
        }
        return super.onOptionsItemSelected(menuItem)
    }

    /* access modifiers changed from: protected */
    fun onShowUser(chatterID: ChatterID) {
        this.loadableMonitor.unsubscribeAll()
        if (this.memberNameRetriever != null) {
            this.memberNameRetriever.dispose()
            this.memberNameRetriever = null
        }
        this.MemberID = UUIDPool.getUUID(getArguments().getString(MEMBER_ID_KEY))
        setTitle(getString(R.string.member_roles_title_default), (String) null)
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            if (this.MemberID != null) {
                this.memberNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.MemberID), ChatterNameRetriever.OnChatterNameUpdated(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f287$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.4.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.4.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, class status: UNLOADED
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
            val chatterUUID: UUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID()
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID)
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID)
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID)
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID)
            this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID)
        } else if (this.adapter != null) {
            this.adapter.setData((GroupRoleDataReply) null, (Set<UUID>) null)
        }
    }
}
