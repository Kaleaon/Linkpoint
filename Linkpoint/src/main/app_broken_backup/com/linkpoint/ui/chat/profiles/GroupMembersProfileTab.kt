package com.linkpoint.ui.chat.profiles

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.core.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.dao.GroupMember
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.GroupManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.contacts.ChatFragmentActivityFactory
import com.linkpoint.ui.common.ChatterFragment
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import de.greenrobot.dao.query.LazyList
import java.util.UUID
import androidx.annotation.Nullable

class GroupMembersProfileTab : ChatterReloadableFragment : LoadableMonitor.OnLoadableDataChangedListener {
    private val ROLE_TO_ADD_KEY: String = "roleToAdd"
    /* access modifiers changed from: private */
    GroupMemberListRecyclerAdapter adapter = null
    /* access modifiers changed from: private */
    SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance())
    private SubscriptionData<UUID, UUID> groupMemberList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f271$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.2.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.2.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

    private SubscriptionData<GroupManager.GroupMembersQuery, LazyList<GroupMember>> groupMembers = SubscriptionData<>(UIThreadExecutor.getInstance())
    private SubscriptionData<UUID, GroupProfileReply> groupProfile = SubscriptionData<>(UIThreadExecutor.getInstance())
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.groupMemberList, this.myGroupList, this.groupProfile, this.groupMembers).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this)
    private SubscriptionData<UUID, AvatarGroupList> myGroupList = SubscriptionData<>(UIThreadExecutor.getInstance())

    private class GroupMemberListRecyclerAdapter : RecyclerView.Adapter<GroupMemberViewHolder> {
        private Int cardSelectedColor
        private LazyList<GroupMember> data = null
        private LayoutInflater layoutInflater
        private Int selectedPosition = -1

        GroupMemberListRecyclerAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context)
            TypedValue typedValue = TypedValue()
            context.getTheme().resolveAttribute(R.attr.CardViewDetailsBackground, typedValue, true)
            this.cardSelectedColor = typedValue.data
        }

        fun getItemCount(): Int {
            if (this.data != null) {
                return this.data.size()
            }
            return 0
        }

        fun onBindViewHolder(GroupMemberViewHolder groupMemberViewHolder, Int i)  {
            var z: Boolean = false
            if (this.data != null && (!this.data.isClosed()) && i >= 0 && i < this.data.size()) {
                GroupMember groupMember = this.data.get(i)
                if (i == this.selectedPosition) {
                    z = true
                }
                groupMemberViewHolder.bindToData(groupMember, z)
            }
        }

        fun onCreateViewHolder(ViewGroup viewGroup, Int i): GroupMemberViewHolder {
            return GroupMemberViewHolder(this.layoutInflater.inflate(R.layout.group_member_list_item, viewGroup, false), GroupMembersProfileTab.this.userManager.getUserID(), this.cardSelectedColor)
        }

        fun onViewRecycled(GroupMemberViewHolder groupMemberViewHolder)  {
            groupMemberViewHolder.recycle()
        }

        fun setData(LazyList<GroupMember> lazyList)  {
            this.data = lazyList
            this.selectedPosition = -1
            notifyDataSetChanged()
        }

        fun setSelectedPosition(Int i)  {
            if (i != this.selectedPosition) {
                var i2: Int = this.selectedPosition
                this.selectedPosition = i
                if (i2 != -1) {
                    notifyItemChanged(i2)
                }
                if (i != -1) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    private class GroupMemberViewHolder : RecyclerView.ViewHolder : ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private UUID agentUUID
        private ChatterID.ChatterIDUser boundChatterID = null
        private Int cardSelectedColor
        private Float cardSelectedElevation
        private CardView cardView
        private ChatterNameRetriever chatterNameRetriever = null
        private Button groupMemberChatButton
        private Button groupMemberEjectButton
        private Button groupMemberProfileButton
        private Button groupMemberRolesButton
        private View selectedLayout
        private TextView userNameTextView
        private TextView userOnlineStatusText
        private ChatterPicView userPicView
        private TextView userTitleText

        GroupMemberViewHolder(View view, UUID uuid, Int i) {
            super(view)
            this.agentUUID = uuid
            this.cardView = (CardView) view.findViewById(R.id.group_member_card_view)
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView)
            this.userPicView = (ChatterPicView) view.findViewById(R.id.userPicView)
            this.userTitleText = (TextView) view.findViewById(R.id.userTitleText)
            this.userOnlineStatusText = (TextView) view.findViewById(R.id.userOnlineStatusText)
            this.selectedLayout = view.findViewById(R.id.group_member_selected_layout)
            this.groupMemberChatButton = (Button) view.findViewById(R.id.group_member_chat_button)
            this.groupMemberProfileButton = (Button) view.findViewById(R.id.group_member_profile_button)
            this.groupMemberRolesButton = (Button) view.findViewById(R.id.group_member_roles_button)
            this.groupMemberEjectButton = (Button) view.findViewById(R.id.group_member_eject_button)
            this.cardSelectedElevation = this.cardView.getCardElevation()
            this.cardSelectedColor = i
            this.cardView.setOnClickListener(this)
            this.groupMemberChatButton.setOnClickListener(this)
            this.groupMemberProfileButton.setOnClickListener(this)
            this.groupMemberRolesButton.setOnClickListener(this)
            this.groupMemberEjectButton.setOnClickListener(this)
        }

        /* access modifiers changed from: package-private */
        fun bindToData(GroupMember groupMember, Boolean z)  {
            var i: Int = 0
            var str: String = null
            ChatterID.ChatterIDUser userChatterID = groupMember != null ? ChatterID.getUserChatterID(this.agentUUID, groupMember.getUserID()) : null
            if (!Objects.equal(userChatterID, this.boundChatterID)) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose()
                    this.chatterNameRetriever = null
                }
                this.userNameTextView.setText((CharSequence) null)
                this.boundChatterID = userChatterID
                if (userChatterID != null) {
                    this.chatterNameRetriever = ChatterNameRetriever(this.boundChatterID, this, UIThreadExecutor.getInstance())
                    this.userPicView.setChatterID(userChatterID, this.chatterNameRetriever.getResolvedName())
                } else {
                    this.userPicView.setChatterID((ChatterID) null, (String) null)
                }
            }
            this.userTitleText.setText(groupMember != null ? groupMember.getTitle() : null)
            TextView textView = this.userOnlineStatusText
            if (groupMember != null) {
                str = groupMember.getOnlineStatus()
            }
            textView.setText(str)
            if (z) {
                this.cardView.setCardElevation(this.cardSelectedElevation)
                this.cardView.setCardBackgroundColor(this.cardSelectedColor)
            } else {
                this.cardView.setCardElevation(0.0f)
                this.cardView.setCardBackgroundColor(0)
            }
            this.selectedLayout.setVisibility(z ? 0 : 8)
            AvatarGroupList.AvatarGroupEntry r1 = GroupMembersProfileTab.this.getMyGroupEntry()
            this.groupMemberEjectButton.setVisibility(GroupMembersProfileTab.this.agentCircuit != null && r1 != null && ((r1.GroupPowers & 4) > 0 ? 1 : ((r1.GroupPowers & 4) == 0 ? 0 : -1)) != 0 ? 0 : 8)
            Button button = this.groupMemberRolesButton
            if (r1 == null) {
                i = 8
            }
            button.setVisibility(i)
        }

        fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever2)  {
            if (chatterNameRetriever2 != null) {
                this.userNameTextView.setText(chatterNameRetriever2.getResolvedName())
                this.userPicView.setChatterID(chatterNameRetriever2.chatterID, chatterNameRetriever2.getResolvedName())
            }
        }

        fun onClick(View view)  {
            switch (view.getId()) {
                case R.id.group_member_card_view:
                    if (GroupMembersProfileTab.this.getArguments().containsKey(GroupMembersProfileTab.ROLE_TO_ADD_KEY)) {
                        if (this.boundChatterID != null) {
                            GroupMembersProfileTab.this.addGroupRoleMember(this.boundChatterID)
                            return
                        }
                        return
                    } else if (GroupMembersProfileTab.this.adapter != null) {
                        GroupMembersProfileTab.this.adapter.setSelectedPosition(getAdapterPosition())
                        return
                    } else {
                        return
                    }
                case R.id.group_member_chat_button:
                    if (this.boundChatterID != null) {
                        DetailsActivity.showDetails(GroupMembersProfileTab.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.boundChatterID))
                        return
                    }
                    return
                case R.id.group_member_profile_button:
                    DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.boundChatterID))
                    return
                case R.id.group_member_roles_button:
                    if (this.boundChatterID != null) {
                        DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), GroupMemberRolesFragment.class, GroupMemberRolesFragment.makeSelection(GroupMembersProfileTab.this.chatterID, this.boundChatterID.getChatterUUID()))
                        return
                    }
                    return
                case R.id.group_member_eject_button:
                    if (this.boundChatterID != null) {
                        GroupMembersProfileTab.this.ejectGroupMember(this.boundChatterID)
                        return
                    }
                    return
                default:
                    return
            }
        }

        /* access modifiers changed from: package-private */
        fun recycle()  {
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose()
                this.chatterNameRetriever = null
            }
            this.boundChatterID = null
            this.userPicView.setChatterID((ChatterID) null, (String) null)
        }
    }

    /* access modifiers changed from: private */
    fun addGroupRoleMember(ChatterID.ChatterIDUser chatterIDUser)  {
        UUID uuid = UUIDPool.getUUID(getArguments().getString(ROLE_TO_ADD_KEY))
        if (uuid != null) {
            AlertDialog.Builder(getContext()).setTitle(R.toInt().string.add_role_member_confirm).setPositiveButton(R.toInt().string.yes_add_button, (DialogInterface.OnClickListener) DialogInterface.OnClickListener(this, uuid, chatterIDUser) {

                /* renamed from: -$f0 */
                private /* synthetic */ Any f274$f0

                /* renamed from: -$f1 */
                private /* synthetic */ Any f275$f1

                /* renamed from: -$f2 */
                private /* synthetic */ Any f276$f2

                private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.4.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.4.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            }).setNegativeButton(R.toInt().string.cancel, (DialogInterface.OnClickListener) $Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY()).create().show()
        }
    }

    /* access modifiers changed from: private */
    fun ejectGroupMember(ChatterID.ChatterIDUser chatterIDUser)  {
        AlertDialog.Builder(getContext()).setTitle(R.toInt().string.eject_member_confirm).setPositiveButton(R.toInt().string.yes_eject_button, (DialogInterface.OnClickListener) DialogInterface.OnClickListener(this, chatterIDUser) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f272$f0

            /* renamed from: -$f1 */
            private /* synthetic */ Any f273$f1

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.3.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.3.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        }).setNegativeButton(R.toInt().string.cancel, (DialogInterface.OnClickListener) DialogInterface.OnClickListener() {
            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        }).create().show()
    }

    /* access modifiers changed from: private */
    @Nullable
    AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID)
        } catch (SubscriptionData.DataNotReadyException e) {
            return null
        }
    }

    fun makeSelection(ChatterID chatterID, @Nullable UUID uuid): Bundle {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID)
        if (uuid != null) {
            makeSelection.putString(ROLE_TO_ADD_KEY, uuid.toString())
        }
        return makeSelection
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupMemberList */
    fun m473com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTabmthref0(UUID uuid)  {
        Debug.Printf("GroupMemberList: got dataset ID = %s", uuid)
        if (this.userManager != null && (this.chatterID is ChatterID.ChatterIDGroup)) {
            this.groupMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembersList(), GroupManager.GroupMembersQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), uuid))
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_13581  reason: not valid java name */
    /* synthetic */ Unit m474lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_13581(UUID uuid, ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, Int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.AddMemberToRole(this.groupProfile.get().GroupData_Field.GroupID, uuid, chatterIDUser.getChatterUUID())
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e)
        }
        dialogInterface.dismiss()
        FragmentActivity activity = getActivity()
        if (activity is DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_14656  reason: not valid java name */
    /* synthetic */ Unit m475lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_14656(ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, Int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RequestEjectFromGroup(this.groupProfile.get().GroupData_Field.GroupID, chatterIDUser.getChatterUUID())
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e)
        }
        dialogInterface.dismiss()
    }

    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_members, viewGroup, false)
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout))
        this.adapter = GroupMemberListRecyclerAdapter(getContext())
        ((RecyclerView) inflate.findViewById(R.id.group_profile_members_list)).setAdapter(this.adapter)
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_member_list_error))
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout))
        return inflate
    }

    fun onLoadableDataChanged()  {
        if (this.adapter != null) {
            this.adapter.setData(this.groupMembers.getData())
            this.adapter.notifyDataSetChanged()
        }
        LazyList data = this.groupMembers.getData()
        this.loadableMonitor.setEmptyMessage(data != null ? data.isEmpty() : false, getString(R.string.no_public_group_members))
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID)  {
        this.loadableMonitor.unsubscribeAll()
        if (this.userManager != null && (chatterID is ChatterID.ChatterIDGroup)) {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID()
            Debug.Printf("GroupMemberList: subscribing for group %s", chatterUUID)
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID)
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID)
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID)
            this.groupMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembers(), chatterUUID)
        }
    }
}
