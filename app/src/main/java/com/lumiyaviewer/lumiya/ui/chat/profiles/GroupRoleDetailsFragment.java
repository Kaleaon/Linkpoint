package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLGroupInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;

public class GroupRoleDetailsFragment extends ChatterFragment implements LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler {
    private static final String ROLE_ID_KEY = "role_id";
    private static final ImmutableList<RolePermission> rolePermissions = ImmutableList.copyOf((E[]) new RolePermission[]{new RolePermission(2, R.string.role_gp_member_invite, (RolePermission) null), new RolePermission(4, R.string.role_gp_member_eject, (RolePermission) null), new RolePermission(8, R.string.role_gp_member_options, (RolePermission) null), new RolePermission(16, R.string.role_gp_role_create, (RolePermission) null), new RolePermission(32, R.string.role_gp_role_delete, (RolePermission) null), new RolePermission(64, R.string.role_gp_role_properties, (RolePermission) null), new RolePermission(128, R.string.role_gp_role_assign_member_limited, (RolePermission) null), new RolePermission(256, R.string.role_gp_role_assign_member, (RolePermission) null), new RolePermission(512, R.string.role_gp_role_remove_member, (RolePermission) null), new RolePermission(1024, R.string.role_gp_role_change_actions, (RolePermission) null), new RolePermission(2048, R.string.role_gp_group_change_identity, (RolePermission) null), new RolePermission(4096, R.string.role_gp_land_deed, (RolePermission) null), new RolePermission(8192, R.string.role_gp_land_release, (RolePermission) null), new RolePermission(16384, R.string.role_gp_land_set_sale_info, (RolePermission) null), new RolePermission(32768, R.string.role_gp_land_divide_join, (RolePermission) null), new RolePermission(131072, R.string.role_gp_land_find_places, (RolePermission) null), new RolePermission(262144, R.string.role_gp_land_change_identity, (RolePermission) null), new RolePermission(524288, R.string.role_gp_land_set_landing_point, (RolePermission) null), new RolePermission(1048576, R.string.role_gp_land_change_media, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_EDIT, R.string.role_gp_land_edit, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_OPTIONS, R.string.role_gp_land_options, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_EDIT_LAND, R.string.role_gp_land_allow_edit_land, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_FLY, R.string.role_gp_land_allow_fly, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_CREATE, R.string.role_gp_land_allow_create, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_LANDMARK, R.string.role_gp_land_allow_landmark, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_SET_HOME, R.string.role_gp_land_allow_set_home, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_HOLD_EVENT, R.string.role_gp_land_allow_hold_event, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_ALLOWED, R.string.role_gp_land_manage_allowed, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_BANNED, R.string.role_gp_land_manage_banned, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_PASSES, R.string.role_gp_land_manage_passes, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_ADMIN, R.string.role_gp_land_admin, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_GROUP_SET, R.string.role_gp_land_return_group_set, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_NON_GROUP, R.string.role_gp_land_return_non_group, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_GROUP_OWNED, R.string.role_gp_land_return_group_owned, (RolePermission) null), new RolePermission(SLGroupInfo.GP_LAND_GARDENING, R.string.role_gp_land_gardening, (RolePermission) null), new RolePermission(SLGroupInfo.GP_OBJECT_DEED, R.string.role_gp_object_deed, (RolePermission) null), new RolePermission(SLGroupInfo.GP_OBJECT_MANIPULATE, R.string.role_gp_object_manipulate, (RolePermission) null), new RolePermission(SLGroupInfo.GP_OBJECT_SET_SALE, R.string.role_gp_object_set_sale, (RolePermission) null), new RolePermission(SLGroupInfo.GP_ACCOUNTING_ACCOUNTABLE, R.string.role_gp_accounting_accountable, (RolePermission) null), new RolePermission(SLGroupInfo.GP_NOTICES_SEND, R.string.role_gp_notices_send, (RolePermission) null), new RolePermission(SLGroupInfo.GP_NOTICES_RECEIVE, R.string.role_gp_notices_receive, (RolePermission) null), new RolePermission(SLGroupInfo.GP_PROPOSAL_START, R.string.role_gp_proposal_start, (RolePermission) null), new RolePermission(SLGroupInfo.GP_PROPOSAL_VOTE, R.string.role_gp_proposal_vote, (RolePermission) null), new RolePermission(65536, R.string.role_gp_session_join, (RolePermission) null), new RolePermission(SLGroupInfo.GP_SESSION_VOICE, R.string.role_gp_session_voice, (RolePermission) null), new RolePermission(SLGroupInfo.GP_SESSION_MODERATOR, R.string.role_gp_session_moderator, (RolePermission) null)});
    @Nullable
    private UUID RoleID;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private MenuItem deleteMenuItem;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private boolean hasChanged = false;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoles, this.groupProfile, this.myGroupList).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this);
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final View.OnClickListener permCheckboxClickListener = new View.OnClickListener(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f290$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.3.$m$0(android.view.View):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.3.$m$0(android.view.View):void, class status: UNLOADED
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
    private final TextWatcher textChangedListener = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            GroupRoleDetailsFragment.this.updateUnsavedChanges();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    private MenuItem undoMenuItem;

    private static class RolePermission {
        final long permMask;
        final int permName;

        private RolePermission(long j, int i) {
            this.permMask = j;
            this.permName = i;
        }

        /* synthetic */ RolePermission(long j, int i, RolePermission rolePermission) {
            this(j, i);
        }
    }

    private boolean anyChanges() {
        String str;
        String stringFromVariableOEM;
        long j;
        String str2;
        View view = getView();
        if (view == null) {
            return false;
        }
        if (this.RoleID == null) {
            str = "";
            stringFromVariableOEM = "";
            j = getDefaultPowers();
            str2 = "";
        } else {
            GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
            if (selectedRoleData == null) {
                return false;
            }
            String stringFromVariableOEM2 = SLMessage.stringFromVariableOEM(selectedRoleData.Name);
            str = stringFromVariableOEM2;
            stringFromVariableOEM = SLMessage.stringFromVariableOEM(selectedRoleData.Title);
            String stringFromVariableOEM3 = SLMessage.stringFromVariableOEM(selectedRoleData.Description);
            j = selectedRoleData.Powers;
            str2 = stringFromVariableOEM3;
        }
        return !Objects.equal(str, ((TextView) view.findViewById(R.id.role_name_edit)).getText().toString()) || !Objects.equal(stringFromVariableOEM, ((TextView) view.findViewById(R.id.role_title_edit)).getText().toString()) || !Objects.equal(str2, ((TextView) view.findViewById(R.id.role_description_edit)).getText().toString()) || j != getSelectedPowers(j, (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
    }

    private void askForSavingChanges(Runnable runnable) {
        View view = getView();
        if (view != null) {
            GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
            long defaultPowers = selectedRoleData == null ? getDefaultPowers() : selectedRoleData.Powers;
            String charSequence = ((TextView) view.findViewById(R.id.role_name_edit)).getText().toString();
            String charSequence2 = ((TextView) view.findViewById(R.id.role_title_edit)).getText().toString();
            String charSequence3 = ((TextView) view.findViewById(R.id.role_description_edit)).getText().toString();
            long selectedPowers = getSelectedPowers(defaultPowers, (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(selectedPowers, this, charSequence, charSequence2, charSequence3, runnable) {

                /* renamed from: -$f0 */
                private final /* synthetic */ long f293$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f294$f1;

                /* renamed from: -$f2 */
                private final /* synthetic */ Object f295$f2;

                /* renamed from: -$f3 */
                private final /* synthetic */ Object f296$f3;

                /* renamed from: -$f4 */
                private final /* synthetic */ Object f297$f4;

                /* renamed from: -$f5 */
                private final /* synthetic */ Object f298$f5;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.6.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.6.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            }).setNegativeButton("No", new DialogInterface.OnClickListener(runnable) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f288$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            builder.create().show();
            return;
        }
        runnable.run();
    }

    private void confirmDeleteRole() {
        if ((getMyGroupPowers() & 32) != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.delete_role_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f289$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.2.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.2.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            }).setNegativeButton("No", new $Lambda$oqvWEi5fLgnwnCXV95inckWtWE());
            builder.create().show();
        }
    }

    private void createPermEntries(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        for (RolePermission rolePermission : rolePermissions) {
            View inflate = layoutInflater.inflate(R.layout.group_role_perm_item, viewGroup, false);
            ((CheckedTextView) inflate).setText(rolePermission.permName);
            inflate.setTag(R.id.perm_checkbox_mask, Long.valueOf(rolePermission.permMask));
            inflate.setEnabled(true);
            inflate.setClickable(true);
            inflate.setOnClickListener(this.permCheckboxClickListener);
            viewGroup.addView(inflate);
        }
    }

    private long getDefaultPowers() {
        return 134283266;
    }

    private int getMemberCount() {
        GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
        if (selectedRoleData == null) {
            return 0;
        }
        if (!selectedRoleData.RoleID.equals(UUIDPool.ZeroUUID)) {
            return selectedRoleData.Members;
        }
        GroupProfileReply data = this.groupProfile.getData();
        if (data != null) {
            return data.GroupData_Field.GroupMembershipCount;
        }
        return 0;
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

    private long getSelectedPowers(long j, ViewGroup viewGroup) {
        long j2;
        int childCount = viewGroup.getChildCount();
        int i = 0;
        long j3 = j;
        while (i < childCount) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CheckedTextView) {
                Object tag = childAt.getTag(R.id.perm_checkbox_mask);
                if (tag instanceof Long) {
                    long longValue = ((Long) tag).longValue();
                    j2 = ((CheckedTextView) childAt).isChecked() ? j3 | longValue : (~longValue) & j3;
                    i++;
                    j3 = j2;
                }
            }
            j2 = j3;
            i++;
            j3 = j2;
        }
        return j3;
    }

    @Nullable
    private GroupRoleDataReply.RoleData getSelectedRoleData() {
        if (this.RoleID == null) {
            return null;
        }
        try {
            for (GroupRoleDataReply.RoleData roleData : this.groupRoles.get().RoleData_Fields) {
                if (Objects.equal(roleData.RoleID, this.RoleID)) {
                    return roleData;
                }
            }
            return null;
        } catch (SubscriptionData.DataNotReadyException e) {
            return null;
        }
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_26404  reason: not valid java name */
    static /* synthetic */ void m485lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_26404(Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        runnable.run();
    }

    public static Bundle makeSelection(ChatterID chatterID, @Nullable UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(ROLE_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    private void setLoadedValues() {
        int i = 0;
        GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
        long myGroupPowers = getMyGroupPowers();
        boolean z = (64 & myGroupPowers) != 0;
        boolean equal = Objects.equal(this.RoleID, UUIDPool.ZeroUUID);
        GroupProfileReply data = this.groupProfile.getData();
        boolean equal2 = data != null ? Objects.equal(data.GroupData_Field.OwnerRole, this.RoleID) : false;
        if (this.deleteMenuItem != null) {
            this.deleteMenuItem.setVisible((this.RoleID == null || (myGroupPowers & 32) == 0 || !(equal2 ^ true)) ? false : !equal);
        }
        View view = getView();
        if (view != null) {
            if (selectedRoleData != null) {
                int memberCount = getMemberCount();
                ((EditText) view.findViewById(R.id.role_name_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((TextView) view.findViewById(R.id.role_name_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((EditText) view.findViewById(R.id.role_title_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((TextView) view.findViewById(R.id.role_title_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((EditText) view.findViewById(R.id.role_description_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView) view.findViewById(R.id.role_description_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView) view.findViewById(R.id.role_member_count)).setText(getResources().getQuantityString(R.plurals.members, memberCount, new Object[]{Integer.valueOf(memberCount)}));
                setPermissionCheckboxes(selectedRoleData.Powers, (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
                view.findViewById(R.id.group_role_members_card_view).setVisibility(0);
            } else {
                view.findViewById(R.id.group_role_members_card_view).setVisibility(8);
                ((EditText) view.findViewById(R.id.role_name_edit)).setText("");
                ((EditText) view.findViewById(R.id.role_title_edit)).setText("");
                ((EditText) view.findViewById(R.id.role_description_edit)).setText("");
                ((TextView) view.findViewById(R.id.role_name_view)).setText("");
                ((TextView) view.findViewById(R.id.role_title_view)).setText("");
                ((TextView) view.findViewById(R.id.role_description_view)).setText("");
                ((TextView) view.findViewById(R.id.role_member_count)).setText("");
                setPermissionCheckboxes(getDefaultPowers(), (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
            }
            view.findViewById(R.id.role_name_edit).setVisibility(z ? 0 : 8);
            view.findViewById(R.id.role_name_view).setVisibility(!z ? 0 : 8);
            view.findViewById(R.id.role_title_edit).setVisibility(z ? 0 : 8);
            view.findViewById(R.id.role_title_view).setVisibility(!z ? 0 : 8);
            view.findViewById(R.id.role_description_edit).setVisibility(z ? 0 : 8);
            View findViewById = view.findViewById(R.id.role_description_view);
            if (z) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        updateUnsavedChanges();
    }

    private void setPermissionCheckboxes(long j, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CheckedTextView) {
                Object tag = childAt.getTag(R.id.perm_checkbox_mask);
                if (tag instanceof Long) {
                    ((CheckedTextView) childAt).setChecked((((Long) tag).longValue() & j) != 0);
                }
            }
        }
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
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_11905  reason: not valid java name */
    public /* synthetic */ void m486lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_11905(View view) {
        long myGroupPowers = getMyGroupPowers();
        GroupProfileReply data = this.groupProfile.getData();
        if ((myGroupPowers & 1024) != 0 ? !(data != null ? Objects.equal(data.GroupData_Field.OwnerRole, this.RoleID) : false) : false) {
            if (view instanceof Checkable) {
                ((Checkable) view).toggle();
            }
            updateUnsavedChanges();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_23112  reason: not valid java name */
    public /* synthetic */ void m487lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_23112(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if ((this.chatterID instanceof ChatterID.ChatterIDGroup) && this.RoleID != null) {
                this.agentCircuit.get().getModules().groupManager.DeleteRole(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_24146  reason: not valid java name */
    public /* synthetic */ void m488lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_24146() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_25587  reason: not valid java name */
    public /* synthetic */ void m489lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_25587(String str, String str2, String str3, long j, Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
                this.agentCircuit.get().getModules().groupManager.SetRoleProperties(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID, str, str2, str3, j);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        runnable.run();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_9928  reason: not valid java name */
    public /* synthetic */ void m490lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_9928(View view) {
        if (this.RoleID == null) {
            return;
        }
        if (this.RoleID.equals(UUIDPool.ZeroUUID)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupMembersProfileTab.class, GroupMembersProfileTab.makeSelection(this.chatterID));
        } else {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleMembersFragment.class, GroupRoleMembersFragment.makeSelection(this.chatterID, this.RoleID));
        }
    }

    public boolean onBackButtonPressed() {
        if (!anyChanges()) {
            return false;
        }
        askForSavingChanges(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f292$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.5.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.5.$m$0():void, class status: UNLOADED
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

        return true;
    }

    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.group_role_edit_menu, menu);
        this.undoMenuItem = menu.findItem(R.id.item_undo);
        this.undoMenuItem.setVisible(this.hasChanged);
        this.deleteMenuItem = menu.findItem(R.id.item_delete_role);
        this.deleteMenuItem.setVisible(false);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_role_details_layout, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        ((TextView) inflate.findViewById(R.id.role_name_edit)).addTextChangedListener(this.textChangedListener);
        ((TextView) inflate.findViewById(R.id.role_title_edit)).addTextChangedListener(this.textChangedListener);
        ((TextView) inflate.findViewById(R.id.role_description_edit)).addTextChangedListener(this.textChangedListener);
        createPermEntries(layoutInflater, (ViewGroup) inflate.findViewById(R.id.role_permission_list_layout));
        inflate.findViewById(R.id.button_view_role_members).setOnClickListener(new View.OnClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f291$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.4.$m$0(android.view.View):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.4.$m$0(android.view.View):void, class status: UNLOADED
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

    public void onLoadableDataChanged() {
        setLoadedValues();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_undo:
                setLoadedValues();
                return true;
            case R.id.item_delete_role:
                confirmDeleteRole();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (getArguments().containsKey(ROLE_ID_KEY)) {
            this.RoleID = UUIDPool.getUUID(getArguments().getString(ROLE_ID_KEY));
        } else {
            this.RoleID = null;
        }
        Debug.Printf("Group role details: new role = %s", this.RoleID);
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        }
    }
}
