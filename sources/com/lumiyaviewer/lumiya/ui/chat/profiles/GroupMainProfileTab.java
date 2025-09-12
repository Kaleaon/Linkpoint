// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.ClipboardManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.AvatarPickerForInvite;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserProfileFragment

public class GroupMainProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{

    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls12(this));
    private final SubscriptionData agentDataUpdate = new SubscriptionData(UIThreadExecutor.getInstance());
    private ChatterNameRetriever founderNameRetriever;
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupRoles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupTitles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onFounderNameReady = new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls13(this);

    public GroupMainProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupProfile, myGroupList, agentDataUpdate, groupRoles
        })).withDataChangedListener(this).withOptionalLoadables(new Loadable[] {
            groupTitles
        });
        founderNameRetriever = null;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_11281(AtomicInteger atomicinteger, DialogInterface dialoginterface, int i)
    {
        atomicinteger.set(i);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_6996(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_8516(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void onActiveGroupCheckboxClicked(View view)
    {
        if (((CheckBox)view).isChecked())
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.ActivateGroup(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
            return;
        }
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.ActivateGroup(UUIDPool.ZeroUUID);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
        }
        return;
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        slagentcircuit = getView();
        if (slagentcircuit != null)
        {
            int ai[] = new int[7];
            int[] _tmp = ai;
            ai[0] = 0x7f100192;
            ai[1] = 0x7f100193;
            ai[2] = 0x7f100194;
            ai[3] = 0x7f10018c;
            ai[4] = 0x7f10018d;
            ai[5] = 0x7f10018e;
            ai[6] = 0x7f10018f;
            int i = 0;
            for (int j = ai.length; i < j; i++)
            {
                slagentcircuit.findViewById(ai[i]).setEnabled(agentCircuit.hasData());
            }

        }
    }

    private void onChangeRoleClicked(View view)
    {
        int i = 0;
        CharSequence acharsequence[];
        agentCircuit.assertHasData();
        view = ((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID;
        acharsequence = new CharSequence[((GroupTitlesReply)groupTitles.get()).GroupData_Fields.size()];
        int j = 0;
_L2:
        if (i >= ((GroupTitlesReply)groupTitles.get()).GroupData_Fields.size())
        {
            break MISSING_BLOCK_LABEL_136;
        }
        acharsequence[i] = SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((GroupTitlesReply)groupTitles.get()).GroupData_Fields.get(i)).Title);
        if (((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((GroupTitlesReply)groupTitles.get()).GroupData_Fields.get(i)).Selected)
        {
            j = i;
        }
        break MISSING_BLOCK_LABEL_211;
        try
        {
            AtomicInteger atomicinteger = new AtomicInteger(j);
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle(0x7f0902f2).setSingleChoiceItems(acharsequence, j, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls2(atomicinteger)).setPositiveButton(0x7f090301, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls17(this, atomicinteger, view));
            builder.create().show();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
        }
        return;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
    }

    private void onContributeLandClicked(View view)
    {
        android.support.v7.app.AlertDialog.Builder builder;
        View view1;
        try
        {
            agentCircuit.assertHasData();
            view = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
            return;
        }
        if (view == null)
        {
            break MISSING_BLOCK_LABEL_153;
        }
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(0x7f0902ff);
        view1 = LayoutInflater.from(getContext()).inflate(0x7f04002f, (ViewGroup)getView(), false);
        builder.setView(view1);
        ((EditText)view1.findViewById(0x7f10014b)).setText(getString(0x7f09012e, new Object[] {
            Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (view)).Contribution)
        }));
        builder.setPositiveButton(0x7f0902fe, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls14(this, view1));
        builder.create().show();
    }

    private void onCopyGroupKeyClicked(View view)
    {
        if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
        {
            view = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID().toString();
            if (android.os.Build.VERSION.SDK_INT < 11)
            {
                ((ClipboardManager)getActivity().getSystemService("clipboard")).setText(view);
            } else
            {
                ((android.content.ClipboardManager)getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Group key", view));
            }
            Toast.makeText(getActivity(), "Group key copied to clipboard", 0).show();
        }
    }

    private void onInviteClicked(View view)
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        try
        {
            if (chatterID == null || !agentCircuit.hasData())
            {
                break MISSING_BLOCK_LABEL_134;
            }
            view = ((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID;
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(view);
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
            return;
        }
        if (avatargroupentry == null)
        {
            break MISSING_BLOCK_LABEL_134;
        }
        if ((avatargroupentry.GroupPowers & 2L) != 0L)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/AvatarPickerForInvite, AvatarPickerForInvite.makeArguments(chatterID.agentUUID, view, (GroupProfileReply)groupProfile.get(), (AvatarGroupList)myGroupList.get(), (GroupTitlesReply)groupTitles.get(), (GroupRoleDataReply)groupRoles.get()));
        }
    }

    private void onJoinClicked(View view)
    {
        android.support.v7.app.AlertDialog.Builder builder;
        agentCircuit.assertHasData();
        view = ((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID;
        if ((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(view) != null)
        {
            break MISSING_BLOCK_LABEL_180;
        }
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(0x7f090184);
        if (((GroupProfileReply)groupProfile.get()).GroupData_Field.MembershipFee != 0)
        {
            break MISSING_BLOCK_LABEL_134;
        }
        builder.setMessage(0x7f09018a);
_L2:
        builder.setPositiveButton(0x7f09038e, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls15(this, view));
        builder.setNegativeButton(0x7f0900a8, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms());
        builder.create().show();
        return;
        builder.setMessage(getString(0x7f09018b, new Object[] {
            Integer.valueOf(((GroupProfileReply)groupProfile.get()).GroupData_Field.MembershipFee)
        }));
        if (true) goto _L2; else goto _L1
_L1:
        view;
        Debug.Warning(view);
    }

    private void onLeaveClicked(View view)
    {
        try
        {
            agentCircuit.assertHasData();
            view = ((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID;
            if ((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(view) != null)
            {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle(0x7f09018f);
                builder.setPositiveButton(0x7f09038f, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls16(this, view));
                builder.setNegativeButton(0x7f0900a8, new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls1());
                builder.create().show();
            }
            return;
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
        }
    }

    private void onShowInProfileCheckboxClicked(View view)
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        try
        {
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
            return;
        }
        if (avatargroupentry == null)
        {
            break MISSING_BLOCK_LABEL_86;
        }
        ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.SetGroupOptions(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID, avatargroupentry.AcceptNotices, ((CheckBox)view).isChecked());
    }

    private void onViewProfileClicked(View view)
    {
        view = (GroupProfileReply)groupProfile.getData();
        if (view != null && chatterID != null)
        {
            view = ((GroupProfileReply) (view)).GroupData_Field.FounderID;
            if (view != null && view.equals(UUIDPool.ZeroUUID) ^ true && chatterID != null)
            {
                view = ChatterID.getUserChatterID(chatterID.agentUUID, view);
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(view));
            }
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_0(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_1(View view)
    {
        onViewProfileClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_2(View view)
    {
        onCopyGroupKeyClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_3(View view)
    {
        onActiveGroupCheckboxClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_4(View view)
    {
        onShowInProfileCheckboxClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_5(View view)
    {
        onChangeRoleClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_6(View view)
    {
        onContributeLandClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_7(View view)
    {
        onJoinClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_8(View view)
    {
        onLeaveClicked(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_2D_mthref_2D_9(View view)
    {
        onInviteClicked(view);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_11438(AtomicInteger atomicinteger, UUID uuid, DialogInterface dialoginterface, int i)
    {
        i = atomicinteger.get();
        if (i < 0)
        {
            break MISSING_BLOCK_LABEL_78;
        }
        if (i < ((GroupTitlesReply)groupTitles.get()).GroupData_Fields.size())
        {
            atomicinteger = ((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((GroupTitlesReply)groupTitles.get()).GroupData_Fields.get(i)).RoleID;
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.SetGroupRole(uuid, atomicinteger);
        }
        return;
        atomicinteger;
        Debug.Warning(atomicinteger);
        return;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_21725(ChatterNameRetriever chatternameretriever)
    {
        View view = getView();
        if (view != null)
        {
            ((TextView)view.findViewById(0x7f100196)).setText(chatternameretriever.getResolvedName());
            ((ChatterPicView)view.findViewById(0x7f100197)).setChatterID(chatternameretriever.chatterID, chatternameretriever.getResolvedName());
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_6577(UUID uuid, DialogInterface dialoginterface, int i)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.RequestLeaveGroup(uuid);
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_8098(UUID uuid, DialogInterface dialoginterface, int i)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.RequestJoinGroup(uuid);
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_9645(View view, DialogInterface dialoginterface, int i)
    {
        try
        {
            i = Integer.parseInt(((EditText)view.findViewById(0x7f10014b)).getText().toString());
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.SetGroupContribution(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID, i);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040049, viewgroup, false);
        ((ImageAssetView)layoutinflater.findViewById(0x7f100183)).setAlignTop(true);
        ((ImageAssetView)layoutinflater.findViewById(0x7f100183)).setVerticalFit(true);
        layoutinflater.findViewById(0x7f100198).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls3(this));
        layoutinflater.findViewById(0x7f10019d).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls4(this));
        layoutinflater.findViewById(0x7f100193).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls5(this));
        layoutinflater.findViewById(0x7f100192).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls6(this));
        layoutinflater.findViewById(0x7f10018f).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls7(this));
        layoutinflater.findViewById(0x7f100194).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls8(this));
        layoutinflater.findViewById(0x7f10018c).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls9(this));
        layoutinflater.findViewById(0x7f10018d).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls10(this));
        layoutinflater.findViewById(0x7f10018e).setOnClickListener(new _2D_.Lambda.qgA5NpRVpRFsQYZFFPT9VQYjWms._cls11(this));
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        Object obj;
        View view;
        int i;
        boolean flag = false;
        view = getView();
        Object obj1;
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        Iterator iterator;
        try
        {
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
            return;
        }
        if (avatargroupentry == null)
        {
            break MISSING_BLOCK_LABEL_104;
        }
        if (Strings.isNullOrEmpty(avatargroupentry.GroupTitle) && !groupTitles.isSubscribed())
        {
            groupTitles.subscribe(userManager.getGroupTitles().getPool(), ((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        if (view == null) goto _L2; else goto _L1
_L1:
        ((ImageAssetView)view.findViewById(0x7f100183)).setAssetID(((GroupProfileReply)groupProfile.get()).GroupData_Field.InsigniaID);
        obj = SLMessage.stringFromVariableUTF(((GroupProfileReply)groupProfile.get()).GroupData_Field.Charter);
        obj1 = view.findViewById(0x7f100199);
        if (Strings.isNullOrEmpty(((String) (obj))))
        {
            i = 8;
        } else
        {
            i = 0;
        }
        ((View) (obj1)).setVisibility(i);
        ((TextView)view.findViewById(0x7f10019a)).setText(((CharSequence) (obj)));
        ((TextView)view.findViewById(0x7f100185)).setText(getString(0x7f090142, new Object[] {
            Integer.valueOf(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupMembershipCount)
        }));
        obj1 = (TextView)view.findViewById(0x7f100186);
        if (((GroupProfileReply)groupProfile.get()).GroupData_Field.MembershipFee != 0) goto _L4; else goto _L3
_L3:
        obj = getString(0x7f09013c);
_L25:
        ((TextView) (obj1)).setText(((CharSequence) (obj)));
        obj1 = (TextView)view.findViewById(0x7f100187);
        if (!((GroupProfileReply)groupProfile.get()).GroupData_Field.OpenEnrollment) goto _L6; else goto _L5
_L5:
        obj = getString(0x7f09014e);
_L20:
        ((TextView) (obj1)).setText(((CharSequence) (obj)));
        if (!((GroupProfileReply)groupProfile.get()).GroupData_Field.AllowPublish) goto _L8; else goto _L7
_L7:
        if (!((GroupProfileReply)groupProfile.get()).GroupData_Field.MaturePublish) goto _L10; else goto _L9
_L9:
        obj = getString(0x7f090155);
_L21:
        ((TextView)view.findViewById(0x7f100188)).setText(((CharSequence) (obj)));
        if (founderNameRetriever != null)
        {
            founderNameRetriever.dispose();
            founderNameRetriever = null;
        }
        obj = ((GroupProfileReply)groupProfile.get()).GroupData_Field.FounderID;
        Debug.Printf("GroupProfile: founderID = %s", new Object[] {
            obj
        });
        if (obj == null) goto _L12; else goto _L11
_L11:
        if (!(((UUID) (obj)).equals(UUIDPool.ZeroUUID) ^ true) || chatterID == null) goto _L12; else goto _L13
_L13:
        obj = ChatterID.getUserChatterID(chatterID.agentUUID, ((UUID) (obj)));
        view.findViewById(0x7f100195).setVisibility(0);
        founderNameRetriever = new ChatterNameRetriever(((ChatterID) (obj)), onFounderNameReady, UIThreadExecutor.getInstance());
_L22:
        if (avatargroupentry == null) goto _L15; else goto _L14
_L14:
        ((TextView)view.findViewById(0x7f10018a)).setText(0x7f0901a5);
        view.findViewById(0x7f10018c).setVisibility(8);
        view.findViewById(0x7f10018d).setVisibility(0);
        if ((avatargroupentry.GroupPowers & 2L) != 0L)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        obj = view.findViewById(0x7f10018e);
        if (i != 0)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        view.findViewById(0x7f10018f).setVisibility(0);
        view.findViewById(0x7f10018b).setVisibility(0);
        obj1 = avatargroupentry.GroupTitle;
        obj = obj1;
        if (!Strings.isNullOrEmpty(((String) (obj1))))
        {
            break MISSING_BLOCK_LABEL_690;
        }
        obj = obj1;
        if (!groupTitles.hasData())
        {
            break MISSING_BLOCK_LABEL_690;
        }
        iterator = ((GroupTitlesReply)groupTitles.get()).GroupData_Fields.iterator();
_L17:
        obj = obj1;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_690;
        }
        obj = (com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)iterator.next();
        if (!((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData) (obj)).Selected) goto _L17; else goto _L16
_L16:
        obj = SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData) (obj)).Title);
        ((TextView)view.findViewById(0x7f10018b)).setText(((CharSequence) (obj)));
        view.findViewById(0x7f100190).setVisibility(0);
        obj1 = (TextView)view.findViewById(0x7f100191);
        if (avatargroupentry.Contribution == 0) goto _L19; else goto _L18
_L18:
        obj = getString(0x7f090123, new Object[] {
            Integer.valueOf(avatargroupentry.Contribution)
        });
_L23:
        ((TextView) (obj1)).setText(((CharSequence) (obj)));
        ((CheckBox)view.findViewById(0x7f100192)).setChecked(avatargroupentry.ListInProfile);
        ((CheckBox)view.findViewById(0x7f100193)).setChecked(((AgentDataUpdate)agentDataUpdate.get()).AgentData_Field.ActiveGroupID.equals(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID));
        return;
_L4:
        obj = getString(0x7f09013f, new Object[] {
            Integer.valueOf(((GroupProfileReply)groupProfile.get()).GroupData_Field.MembershipFee)
        });
        continue; /* Loop/switch isn't completed */
_L6:
        obj = getString(0x7f09013b);
          goto _L20
_L10:
        obj = getString(0x7f090157);
          goto _L21
_L8:
        obj = getString(0x7f090156);
          goto _L21
_L12:
        view.findViewById(0x7f100195).setVisibility(8);
        ((ChatterPicView)view.findViewById(0x7f100197)).setChatterID(null, null);
          goto _L22
_L19:
        obj = getString(0x7f0901e9);
          goto _L23
_L15:
        ((TextView)view.findViewById(0x7f10018a)).setText(0x7f0901aa);
        obj = view.findViewById(0x7f10018c);
        if (((GroupProfileReply)groupProfile.get()).GroupData_Field.OpenEnrollment)
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        view.findViewById(0x7f10018d).setVisibility(8);
        view.findViewById(0x7f10018e).setVisibility(8);
        view.findViewById(0x7f10018f).setVisibility(8);
        view.findViewById(0x7f10018b).setVisibility(8);
        view.findViewById(0x7f100190).setVisibility(8);
        return;
_L2:
        return;
        if (true) goto _L25; else goto _L24
_L24:
    }

    protected void onShowUser(ChatterID chatterid)
    {
        View view = getView();
        loadableMonitor.unsubscribeAll();
        agentCircuit.unsubscribe();
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            if (view != null)
            {
                ((TextView)view.findViewById(0x7f10019c)).setText(uuid.toString());
            }
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
            agentDataUpdate.subscribe(userManager.getAgentDataUpdates().getPool(), chatterid.agentUUID);
            groupRoles.subscribe(userManager.getGroupRoles().getPool(), uuid);
        } else
        if (view != null)
        {
            ((TextView)view.findViewById(0x7f10019c)).setText("");
            return;
        }
    }
}
