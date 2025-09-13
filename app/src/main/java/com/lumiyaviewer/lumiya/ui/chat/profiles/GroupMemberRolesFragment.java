package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class GroupMemberRolesFragment extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler {
    private static final String MEMBER_ID_KEY = "memberID";
    /* access modifiers changed from: private */
    @Nullable
    public UUID MemberID = null;
    /* access modifiers changed from: private */
    public final SubscriptionData<GroupManager.GroupMemberRolesQuery, Set<UUID>> activeRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private MemberRoleAdapter adapter = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    /* access modifiers changed from: private */
    public final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, UUID> groupRoleMemberList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f286$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.3.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.3.$m$0(java.lang.Object):void, class status: UNLOADED
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
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    /* access modifiers changed from: private */
    public final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private boolean hasChanged = false;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoles, this.groupProfile, this.myGroupList, this.groupRoleMemberList, this.activeRoles).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener(this);
    @Nullable
    private ChatterNameRetriever memberNameRetriever = null;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private MenuItem undoMenuItem;

    private class MemberRoleAdapter extends BaseAdapter {
        @Nullable
        private GroupRoleDataReply data;
        private final Set<UUID> selectedRoles;

        private MemberRoleAdapter() {
            this.data = null;
            this.selectedRoles = new HashSet();
        }

        /* synthetic */ MemberRoleAdapter(GroupMemberRolesFragment groupMemberRolesFragment, MemberRoleAdapter memberRoleAdapter) {
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

        public Set<UUID> getSelectedRoles() {
            return this.selectedRoles;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(GroupMemberRolesFragment.this.getContext()).inflate(R.layout.group_member_role_list_item, viewGroup, false);
            }
            GroupRoleDataReply.RoleData item = getItem(i);
            if (item != null) {
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setText(SLMessage.stringFromVariableOEM(item.Name));
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setChecked(!item.RoleID.equals(UUIDPool.ZeroUUID) ? this.selectedRoles.contains(item.RoleID) : true);
            }
            return view;
        }

        public boolean hasStableIds() {
            return false;
        }

        public void setData(@Nullable GroupRoleDataReply groupRoleDataReply, Set<UUID> set) {
            this.data = groupRoleDataReply;
            this.selectedRoles.clear();
            if (set != null) {
                this.selectedRoles.addAll(set);
            }
            GroupMemberRolesFragment.this.updateUnsavedChanges();
            notifyDataSetInvalidated();
        }

        public void toggleChecked(UUID uuid) {
            boolean z;
            boolean z2;
            GroupTitlesReply groupTitlesReply;
            if (!uuid.equals(UUIDPool.ZeroUUID) && GroupMemberRolesFragment.this.userManager != null && GroupMemberRolesFragment.this.MemberID != null) {
                long r4 = GroupMemberRolesFragment.this.getMyGroupPowers();
                try {
                    boolean contains = ((Set) GroupMemberRolesFragment.this.activeRoles.get()).contains(uuid);
                    boolean z3 = !this.selectedRoles.contains(uuid);
                    if (contains == z3) {
                        if (z3) {
                            this.selectedRoles.add(uuid);
                        } else {
                            this.selectedRoles.remove(uuid);
                        }
                        z = true;
                    } else {
                        if (z3) {
                            if ((256 & r4) != 0) {
                                z2 = true;
                            } else {
                                if ((r4 & 128) != 0 && (groupTitlesReply = (GroupTitlesReply) GroupMemberRolesFragment.this.groupTitles.getData()) != null) {
                                    Iterator<T> it = groupTitlesReply.GroupData_Fields.iterator();
                                    while (true) {
                                        if (it.hasNext()) {
                                            if (((GroupTitlesReply.GroupData) it.next()).RoleID.equals(uuid)) {
                                                z2 = true;
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                z2 = false;
                            }
                            if (z2) {
                                this.selectedRoles.add(uuid);
                                z = true;
                            }
                        } else if ((r4 & 512) != 0) {
                            boolean equals = uuid.equals(((GroupProfileReply) GroupMemberRolesFragment.this.groupProfile.get()).GroupData_Field.OwnerRole);
                            boolean equals2 = GroupMemberRolesFragment.this.userManager.getUserID().equals(GroupMemberRolesFragment.this.MemberID);
                            if (!equals || equals2) {
                                this.selectedRoles.remove(uuid);
                                z = true;
                            }
                        }
                        z = false;
                    }
                } catch (SubscriptionData.DataNotReadyException e) {
                    z = false;
                }
                if (z) {
                    GroupMemberRolesFragment.this.updateUnsavedChanges();
                    notifyDataSetChanged();
                }
            }
        }
    }

    private boolean anyChanges() {
        Set data = this.activeRoles.getData();
        if (this.adapter == null || data == null) {
            return false;
        }
        return !data.equals(this.adapter.getSelectedRoles());
    }

    private void closeFragment() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
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

    /* access modifiers changed from: private */
    public long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0;
    }

    public static Bundle makeSelection(ChatterID chatterID, UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(MEMBER_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupRoleMemberList */
    public void m459com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragmentmthref0(UUID uuid) {
        if (this.userManager != null && (this.chatterID instanceof ChatterID.ChatterIDGroup) && this.MemberID != null) {
            this.activeRoles.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMemberRoleList(), GroupManager.GroupMemberRolesQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, uuid));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onMemberNameUpdated */
    public void m460com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragmentmthref1(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName();
        if (!Strings.isNullOrEmpty(resolvedName)) {
            setTitle(getString(R.string.member_roles_title_format, resolvedName), (String) null);
            return;
        }
        setTitle(getString(R.string.name_loading_title), (String) null);
    }

    /* access modifiers changed from: private */
    public void updateUnsavedChanges() {
        boolean anyChanges = anyChanges();
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges;
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425  reason: not valid java name */
    public /* synthetic */ void m461lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        closeFragment();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191  reason: not valid java name */
    public /* synthetic */ void m462lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191(AdapterView adapterView, View view, int i, long j) {
        GroupRoleDataReply.RoleData item;
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            this.adapter.toggleChecked(item.RoleID);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245  reason: not valid java name */
    public /* synthetic */ void m463lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if (!(this.adapter == null || this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDGroup))) {
                Set<UUID> selectedRoles = this.adapter.getSelectedRoles();
                Set set = this.activeRoles.get();
                HashSet hashSet = new HashSet(selectedRoles);
                hashSet.removeAll(set);
                HashSet hashSet2 = new HashSet(set);
                hashSet2.removeAll(selectedRoles);
                this.agentCircuit.get().getModules().groupManager.RequestMemberRoleChanges(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, hashSet, hashSet2);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        closeFragment();
    }

    public boolean onBackButtonPressed() {
        if (!anyChanges()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", new $Lambda$jWSiK5iqzZfaogto6grdML6fzQ(this)).setNegativeButton("No", new DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f284$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            public final void brokenMethod(
        // TODO: implement method
    }
        });
        builder.create().show();
        return true;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        setShowChatterTitle(false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.group_member_roles_menu, menu);
        this.undoMenuItem = menu.findItem(R.id.item_undo);
        this.undoMenuItem.setVisible(this.hasChanged);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_member_roles, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new MemberRoleAdapter(this, (MemberRoleAdapter) null);
        }
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setAdapter(this.adapter);
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setOnItemClickListener(new AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f285$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.2.$m$0(android.widget.AdapterView, android.view.View, int, long):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.2.$m$0(android.widget.AdapterView, android.view.View, int, long):void, class status: UNLOADED
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
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.activeRoles.getData());
        }
        try {
            if (this.userManager != null) {
                AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
                if (myGroupEntry != null) {
                    Debug.Printf("GroupMemberRoles: my group powers are 0x%x", Long.valueOf(myGroupEntry.GroupPowers));
                    if (!this.groupTitles.isSubscribed()) {
                        this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
                        return;
                    }
                    return;
                }
                Debug.Printf("GroupMemberRoles: not my group", new Object[0]);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_undo:
                try {
                    if (this.adapter != null) {
                        this.adapter.setData(this.groupRoles.get(), this.activeRoles.get());
                        break;
                    }
                } catch (SubscriptionData.DataNotReadyException e) {
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.memberNameRetriever != null) {
            this.memberNameRetriever.dispose();
            this.memberNameRetriever = null;
        }
        this.MemberID = UUIDPool.getUUID(getArguments().getString(MEMBER_ID_KEY));
        setTitle(getString(R.string.member_roles_title_default), (String) null);
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            if (this.MemberID != null) {
                this.memberNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.MemberID), new ChatterNameRetriever.OnChatterNameUpdated(this) {

                    /* renamed from: -$f0 */
                    private final /* synthetic */ Object f287$f0;

                    private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.4.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.4.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
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

                    public final void brokenMethod(
        // TODO: implement method
    }
                }, UIThreadExecutor.getInstance());
            }
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
        } else if (this.adapter != null) {
            this.adapter.setData((GroupRoleDataReply) null, (Set<UUID>) null);
        }
    }
}
