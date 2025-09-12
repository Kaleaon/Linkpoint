// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupMembersProfileTab, GroupRoleMembersFragment

public class GroupRoleDetailsFragment extends ChatterFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler
{
    private static class RolePermission
    {

        final long permMask;
        final int permName;

        private RolePermission(long l, int i)
        {
            permMask = l;
            permName = i;
        }

        RolePermission(long l, int i, RolePermission rolepermission)
        {
            this(l, i);
        }
    }


    private static final String ROLE_ID_KEY = "role_id";
    private static final ImmutableList rolePermissions = ImmutableList.copyOf(new RolePermission[] {
        new RolePermission(2L, 0x7f0902c7, null), new RolePermission(4L, 0x7f0902c6, null), new RolePermission(8L, 0x7f0902c8, null), new RolePermission(16L, 0x7f0902d3, null), new RolePermission(32L, 0x7f0902d4, null), new RolePermission(64L, 0x7f0902d5, null), new RolePermission(128L, 0x7f0902d1, null), new RolePermission(256L, 0x7f0902d0, null), new RolePermission(512L, 0x7f0902d6, null), new RolePermission(1024L, 0x7f0902d2, null), 
        new RolePermission(2048L, 0x7f0902ad, null), new RolePermission(4096L, 0x7f0902b7, null), new RolePermission(8192L, 0x7f0902c0, null), new RolePermission(16384L, 0x7f0902c5, null), new RolePermission(32768L, 0x7f0902b8, null), new RolePermission(0x20000L, 0x7f0902ba, null), new RolePermission(0x40000L, 0x7f0902b5, null), new RolePermission(0x80000L, 0x7f0902c4, null), new RolePermission(0x100000L, 0x7f0902b6, null), new RolePermission(0x200000L, 0x7f0902b9, null), 
        new RolePermission(0x400000L, 0x7f0902bf, null), new RolePermission(0x800000L, 0x7f0902b0, null), new RolePermission(0x1000000L, 0x7f0902b1, null), new RolePermission(0x2000000L, 0x7f0902af, null), new RolePermission(0x4000000L, 0x7f0902b3, null), new RolePermission(0x10000000L, 0x7f0902b4, null), new RolePermission(0x20000000000L, 0x7f0902b2, null), new RolePermission(0x20000000L, 0x7f0902bc, null), new RolePermission(0x40000000L, 0x7f0902bd, null), new RolePermission(0x80000000L, 0x7f0902be, null), 
        new RolePermission(0x100000000L, 0x7f0902ae, null), new RolePermission(0x200000000L, 0x7f0902c2, null), new RolePermission(0x400000000L, 0x7f0902c3, null), new RolePermission(0x1000000000000L, 0x7f0902c1, null), new RolePermission(0x800000000L, 0x7f0902bb, null), new RolePermission(0x1000000000L, 0x7f0902cb, null), new RolePermission(0x4000000000L, 0x7f0902cc, null), new RolePermission(0x8000000000L, 0x7f0902cd, null), new RolePermission(0x10000000000L, 0x7f0902ac, null), new RolePermission(0x40000000000L, 0x7f0902ca, null), 
        new RolePermission(0x80000000000L, 0x7f0902c9, null), new RolePermission(0x100000000000L, 0x7f0902ce, null), new RolePermission(0x200000000000L, 0x7f0902cf, null), new RolePermission(0x10000L, 0x7f0902d7, null), new RolePermission(0x8000000L, 0x7f0902d9, null), new RolePermission(0x2000000000L, 0x7f0902d8, null)
    });
    private UUID RoleID;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private MenuItem deleteMenuItem;
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupRoles = new SubscriptionData(UIThreadExecutor.getInstance());
    private boolean hasChanged;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());
    private final android.view.View.OnClickListener permCheckboxClickListener = new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls3(this);
    private final TextWatcher textChangedListener = new TextWatcher() {

        final GroupRoleDetailsFragment this$0;

        public void afterTextChanged(Editable editable)
        {
            GroupRoleDetailsFragment._2D_wrap0(GroupRoleDetailsFragment.this);
        }

        public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
        {
        }

        public void onTextChanged(CharSequence charsequence, int i, int j, int k)
        {
        }

            
            {
                this$0 = GroupRoleDetailsFragment.this;
                super();
            }
    };
    private MenuItem undoMenuItem;

    static void _2D_wrap0(GroupRoleDetailsFragment grouproledetailsfragment)
    {
        grouproledetailsfragment.updateUnsavedChanges();
    }

    public GroupRoleDetailsFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupRoles, groupProfile, myGroupList
        })).withOptionalLoadables(new Loadable[] {
            agentCircuit
        }).withDataChangedListener(this);
        hasChanged = false;
    }

    private boolean anyChanges()
    {
        View view = getView();
        if (view != null)
        {
            String s;
            String s1;
            String s2;
            long l;
            if (RoleID == null)
            {
                l = getDefaultPowers();
                s = "";
                s2 = "";
                s1 = "";
            } else
            {
                com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData roledata = getSelectedRoleData();
                if (roledata == null)
                {
                    return false;
                }
                s = SLMessage.stringFromVariableOEM(roledata.Name);
                s2 = SLMessage.stringFromVariableOEM(roledata.Title);
                s1 = SLMessage.stringFromVariableOEM(roledata.Description);
                l = roledata.Powers;
            }
            if (!Objects.equal(s, ((TextView)view.findViewById(0x7f1001a3)).getText().toString()))
            {
                return true;
            }
            if (!Objects.equal(s2, ((TextView)view.findViewById(0x7f1001a5)).getText().toString()))
            {
                return true;
            }
            if (!Objects.equal(s1, ((TextView)view.findViewById(0x7f1001a7)).getText().toString()))
            {
                return true;
            }
            return l != getSelectedPowers(l, (ViewGroup)view.findViewById(0x7f1001ab));
        } else
        {
            return false;
        }
    }

    private void askForSavingChanges(Runnable runnable)
    {
        Object obj = getView();
        if (obj != null)
        {
            Object obj1 = getSelectedRoleData();
            String s;
            String s1;
            long l;
            if (obj1 == null)
            {
                l = getDefaultPowers();
            } else
            {
                l = ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Powers;
            }
            obj1 = ((TextView)((View) (obj)).findViewById(0x7f1001a3)).getText().toString();
            s = ((TextView)((View) (obj)).findViewById(0x7f1001a5)).getText().toString();
            s1 = ((TextView)((View) (obj)).findViewById(0x7f1001a7)).getText().toString();
            l = getSelectedPowers(l, (ViewGroup)((View) (obj)).findViewById(0x7f1001ab));
            obj = new android.app.AlertDialog.Builder(getContext());
            ((android.app.AlertDialog.Builder) (obj)).setMessage(getString(0x7f0902e0)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls6(l, this, obj1, s, s1, runnable)).setNegativeButton("No", new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls1(runnable));
            ((android.app.AlertDialog.Builder) (obj)).create().show();
            return;
        } else
        {
            runnable.run();
            return;
        }
    }

    private void confirmDeleteRole()
    {
        if ((getMyGroupPowers() & 32L) != 0L)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(getString(0x7f0900e1)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls2(this)).setNegativeButton("No", new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E());
            builder.create().show();
        }
    }

    private void createPermEntries(LayoutInflater layoutinflater, ViewGroup viewgroup)
    {
        View view;
        for (Iterator iterator = rolePermissions.iterator(); iterator.hasNext(); viewgroup.addView(view))
        {
            RolePermission rolepermission = (RolePermission)iterator.next();
            view = layoutinflater.inflate(0x7f04004e, viewgroup, false);
            ((CheckedTextView)view).setText(rolepermission.permName);
            view.setTag(0x7f100024, Long.valueOf(rolepermission.permMask));
            view.setEnabled(true);
            view.setClickable(true);
            view.setOnClickListener(permCheckboxClickListener);
        }

    }

    private long getDefaultPowers()
    {
        return 0x8010002L;
    }

    private int getMemberCount()
    {
        Object obj = getSelectedRoleData();
        if (obj != null)
        {
            if (((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj)).RoleID.equals(UUIDPool.ZeroUUID))
            {
                obj = (GroupProfileReply)groupProfile.getData();
                if (obj != null)
                {
                    return ((GroupProfileReply) (obj)).GroupData_Field.GroupMembershipCount;
                } else
                {
                    return 0;
                }
            } else
            {
                return ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj)).Members;
            }
        } else
        {
            return 0;
        }
    }

    private com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry getMyGroupEntry()
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        try
        {
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
        {
            return null;
        }
        return avatargroupentry;
    }

    private long getMyGroupPowers()
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry = getMyGroupEntry();
        if (avatargroupentry != null)
        {
            return avatargroupentry.GroupPowers;
        } else
        {
            return 0L;
        }
    }

    private long getSelectedPowers(long l, ViewGroup viewgroup)
    {
        int j = viewgroup.getChildCount();
        int i = 0;
        while (i < j) 
        {
            View view = viewgroup.getChildAt(i);
            if (!(view instanceof CheckedTextView))
            {
                continue;
            }
            Object obj = view.getTag(0x7f100024);
            if (obj instanceof Long)
            {
                long l1 = ((Long)obj).longValue();
                if (((CheckedTextView)view).isChecked())
                {
                    l |= l1;
                } else
                {
                    l = l1 & l;
                }
            }
            i++;
        }
        return l;
    }

    private com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData getSelectedRoleData()
    {
        if (RoleID == null)
        {
            break MISSING_BLOCK_LABEL_66;
        }
        Iterator iterator = ((GroupRoleDataReply)groupRoles.get()).RoleData_Fields.iterator();
_L1:
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData roledata;
        boolean flag;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_68;
        }
        roledata = (com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData)iterator.next();
        flag = Objects.equal(roledata.RoleID, RoleID);
        if (flag)
        {
            return roledata;
        }
          goto _L1
        com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception;
        datanotreadyexception;
        return null;
        return null;
        return null;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_23933(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_26404(Runnable runnable, DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
        runnable.run();
    }

    public static Bundle makeSelection(ChatterID chatterid, UUID uuid)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        if (uuid != null)
        {
            chatterid.putString("role_id", uuid.toString());
        }
        return chatterid;
    }

    private void setLoadedValues()
    {
        boolean flag = false;
        Object obj1 = getSelectedRoleData();
        long l = getMyGroupPowers();
        Object obj;
        int i;
        boolean flag1;
        boolean flag2;
        if ((64L & l) != 0L)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        flag2 = Objects.equal(RoleID, UUIDPool.ZeroUUID);
        obj = (GroupProfileReply)groupProfile.getData();
        if (obj != null)
        {
            flag1 = Objects.equal(((GroupProfileReply) (obj)).GroupData_Field.OwnerRole, RoleID);
        } else
        {
            flag1 = false;
        }
        if (deleteMenuItem != null)
        {
            obj = deleteMenuItem;
            int j;
            if (RoleID != null && (l & 32L) != 0L && flag1 ^ true)
            {
                flag1 = flag2 ^ true;
            } else
            {
                flag1 = false;
            }
            ((MenuItem) (obj)).setVisible(flag1);
        }
        obj = getView();
        if (obj != null)
        {
            int k;
            if (obj1 != null)
            {
                j = getMemberCount();
                ((EditText)((View) (obj)).findViewById(0x7f1001a3)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Name));
                ((TextView)((View) (obj)).findViewById(0x7f1001a2)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Name));
                ((EditText)((View) (obj)).findViewById(0x7f1001a5)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Title));
                ((TextView)((View) (obj)).findViewById(0x7f1001a4)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Title));
                ((EditText)((View) (obj)).findViewById(0x7f1001a7)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Description));
                ((TextView)((View) (obj)).findViewById(0x7f1001a6)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Description));
                ((TextView)((View) (obj)).findViewById(0x7f10017e)).setText(getResources().getQuantityString(0x7f110001, j, new Object[] {
                    Integer.valueOf(j)
                }));
                setPermissionCheckboxes(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (obj1)).Powers, (ViewGroup)((View) (obj)).findViewById(0x7f1001ab));
                ((View) (obj)).findViewById(0x7f1001a8).setVisibility(0);
            } else
            {
                ((View) (obj)).findViewById(0x7f1001a8).setVisibility(8);
                ((EditText)((View) (obj)).findViewById(0x7f1001a3)).setText("");
                ((EditText)((View) (obj)).findViewById(0x7f1001a5)).setText("");
                ((EditText)((View) (obj)).findViewById(0x7f1001a7)).setText("");
                ((TextView)((View) (obj)).findViewById(0x7f1001a2)).setText("");
                ((TextView)((View) (obj)).findViewById(0x7f1001a4)).setText("");
                ((TextView)((View) (obj)).findViewById(0x7f1001a6)).setText("");
                ((TextView)((View) (obj)).findViewById(0x7f10017e)).setText("");
                setPermissionCheckboxes(getDefaultPowers(), (ViewGroup)((View) (obj)).findViewById(0x7f1001ab));
            }
            obj1 = ((View) (obj)).findViewById(0x7f1001a3);
            if (i != 0)
            {
                k = 0;
            } else
            {
                k = 8;
            }
            ((View) (obj1)).setVisibility(k);
            obj1 = ((View) (obj)).findViewById(0x7f1001a2);
            if (i == 0)
            {
                k = 0;
            } else
            {
                k = 8;
            }
            ((View) (obj1)).setVisibility(k);
            obj1 = ((View) (obj)).findViewById(0x7f1001a5);
            if (i != 0)
            {
                k = 0;
            } else
            {
                k = 8;
            }
            ((View) (obj1)).setVisibility(k);
            obj1 = ((View) (obj)).findViewById(0x7f1001a4);
            if (i == 0)
            {
                k = 0;
            } else
            {
                k = 8;
            }
            ((View) (obj1)).setVisibility(k);
            obj1 = ((View) (obj)).findViewById(0x7f1001a7);
            if (i != 0)
            {
                k = 0;
            } else
            {
                k = 8;
            }
            ((View) (obj1)).setVisibility(k);
            obj = ((View) (obj)).findViewById(0x7f1001a6);
            if (i == 0)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 8;
            }
            ((View) (obj)).setVisibility(i);
        }
        updateUnsavedChanges();
    }

    private void setPermissionCheckboxes(long l, ViewGroup viewgroup)
    {
        int j = viewgroup.getChildCount();
        int i = 0;
        while (i < j) 
        {
            Object obj1 = viewgroup.getChildAt(i);
            if (!(obj1 instanceof CheckedTextView))
            {
                continue;
            }
            Object obj = ((View) (obj1)).getTag(0x7f100024);
            if (obj instanceof Long)
            {
                obj1 = (CheckedTextView)obj1;
                boolean flag;
                if ((((Long)obj).longValue() & l) != 0L)
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
                ((CheckedTextView) (obj1)).setChecked(flag);
            }
            i++;
        }
    }

    private void updateUnsavedChanges()
    {
        boolean flag = anyChanges();
        if (flag != hasChanged)
        {
            hasChanged = flag;
            if (undoMenuItem != null)
            {
                undoMenuItem.setVisible(hasChanged);
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_11905(View view)
    {
        long l = getMyGroupPowers();
        GroupProfileReply groupprofilereply = (GroupProfileReply)groupProfile.getData();
        boolean flag;
        boolean flag1;
        if (groupprofilereply != null)
        {
            flag1 = Objects.equal(groupprofilereply.GroupData_Field.OwnerRole, RoleID);
        } else
        {
            flag1 = false;
        }
        if ((l & 1024L) != 0L)
        {
            flag = flag1 ^ true;
        } else
        {
            flag = false;
        }
        if (flag)
        {
            if (view instanceof Checkable)
            {
                ((Checkable)view).toggle();
            }
            updateUnsavedChanges();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_23112(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        try
        {
            if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup) && RoleID != null)
            {
                ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.DeleteRole(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), RoleID);
            }
        }
        // Misplaced declaration of an exception variable
        catch (DialogInterface dialoginterface) { }
        dialoginterface = getActivity();
        if (dialoginterface instanceof DetailsActivity)
        {
            ((DetailsActivity)dialoginterface).closeDetailsFragment(this);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_24146()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).closeDetailsFragment(this);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_25587(String s, String s1, String s2, long l, Runnable runnable, DialogInterface dialoginterface, 
            int i)
    {
        dialoginterface.dismiss();
        try
        {
            if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
            {
                ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.SetRoleProperties(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), RoleID, s, s1, s2, l);
            }
        }
        // Misplaced declaration of an exception variable
        catch (String s) { }
        runnable.run();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_9928(View view)
    {
label0:
        {
            if (RoleID != null)
            {
                if (!RoleID.equals(UUIDPool.ZeroUUID))
                {
                    break label0;
                }
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMembersProfileTab, GroupMembersProfileTab.makeSelection(chatterID));
            }
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupRoleMembersFragment, GroupRoleMembersFragment.makeSelection(chatterID, RoleID));
    }

    public boolean onBackButtonPressed()
    {
        if (anyChanges())
        {
            askForSavingChanges(new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls5(this));
            return true;
        } else
        {
            return false;
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120008, menu);
        undoMenuItem = menu.findItem(0x7f100302);
        undoMenuItem.setVisible(hasChanged);
        deleteMenuItem = menu.findItem(0x7f10030c);
        deleteMenuItem.setVisible(false);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f04004c, viewgroup, false);
        ((LoadingLayout)viewgroup.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        ((TextView)viewgroup.findViewById(0x7f1001a3)).addTextChangedListener(textChangedListener);
        ((TextView)viewgroup.findViewById(0x7f1001a5)).addTextChangedListener(textChangedListener);
        ((TextView)viewgroup.findViewById(0x7f1001a7)).addTextChangedListener(textChangedListener);
        createPermEntries(layoutinflater, (ViewGroup)viewgroup.findViewById(0x7f1001ab));
        viewgroup.findViewById(0x7f1001a9).setOnClickListener(new _2D_.Lambda.oqvWEi5fLgnwnCXV95inckWtW_2D_E._cls4(this));
        return viewgroup;
    }

    public void onLoadableDataChanged()
    {
        setLoadedValues();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755778: 
            setLoadedValues();
            return true;

        case 2131755788: 
            confirmDeleteRole();
            break;
        }
        return true;
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        if (getArguments().containsKey("role_id"))
        {
            RoleID = UUIDPool.getUUID(getArguments().getString("role_id"));
        } else
        {
            RoleID = null;
        }
        Debug.Printf("Group role details: new role = %s", new Object[] {
            RoleID
        });
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            groupRoles.subscribe(userManager.getGroupRoles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
        }
    }

}
