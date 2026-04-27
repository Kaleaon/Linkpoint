// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.widget.Checkable;
import android.widget.EditText;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import android.widget.CheckedTextView;
import android.view.LayoutInflater;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.view.View;
import android.view.ViewGroup;
import com.google.common.base.Objects;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.text.Editable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.text.TextWatcher;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import javax.annotation.Nullable;
import java.util.UUID;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

public class GroupRoleDetailsFragment extends ChatterFragment implements OnLoadableDataChangedListener, BackButtonHandler
{
    private static final String ROLE_ID_KEY = "role_id";
    private static final ImmutableList<RolePermission> rolePermissions;
    @Nullable
    private UUID RoleID;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private MenuItem deleteMenuItem;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles;
    private boolean hasChanged;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    private final View$OnClickListener permCheckboxClickListener;
    private final TextWatcher textChangedListener;
    private MenuItem undoMenuItem;
    
    static {
        rolePermissions = ImmutableList.copyOf(new RolePermission[] { new RolePermission(2L, 2131296967, null), new RolePermission(4L, 2131296966, null), new RolePermission(8L, 2131296968, null), new RolePermission(16L, 2131296979, null), new RolePermission(32L, 2131296980, null), new RolePermission(64L, 2131296981, null), new RolePermission(128L, 2131296977, null), new RolePermission(256L, 2131296976, null), new RolePermission(512L, 2131296982, null), new RolePermission(1024L, 2131296978, null), new RolePermission(2048L, 2131296941, null), new RolePermission(4096L, 2131296951, null), new RolePermission(8192L, 2131296960, null), new RolePermission(16384L, 2131296965, null), new RolePermission(32768L, 2131296952, null), new RolePermission(131072L, 2131296954, null), new RolePermission(262144L, 2131296949, null), new RolePermission(524288L, 2131296964, null), new RolePermission(1048576L, 2131296950, null), new RolePermission(2097152L, 2131296953, null), new RolePermission(4194304L, 2131296959, null), new RolePermission(8388608L, 2131296944, null), new RolePermission(16777216L, 2131296945, null), new RolePermission(33554432L, 2131296943, null), new RolePermission(67108864L, 2131296947, null), new RolePermission(268435456L, 2131296948, null), new RolePermission(2199023255552L, 2131296946, null), new RolePermission(536870912L, 2131296956, null), new RolePermission(1073741824L, 2131296957, null), new RolePermission(2147483648L, 2131296958, null), new RolePermission(4294967296L, 2131296942, null), new RolePermission(8589934592L, 2131296962, null), new RolePermission(17179869184L, 2131296963, null), new RolePermission(281474976710656L, 2131296961, null), new RolePermission(34359738368L, 2131296955, null), new RolePermission(68719476736L, 2131296971, null), new RolePermission(274877906944L, 2131296972, null), new RolePermission(549755813888L, 2131296973, null), new RolePermission(1099511627776L, 2131296940, null), new RolePermission(4398046511104L, 2131296970, null), new RolePermission(8796093022208L, 2131296969, null), new RolePermission(17592186044416L, 2131296974, null), new RolePermission(35184372088832L, 2131296975, null), new RolePermission(65536L, 2131296983, null), new RolePermission(134217728L, 2131296985, null), new RolePermission(137438953472L, 2131296984, null) });
    }
    
    public GroupRoleDetailsFragment() {
        this.groupRoles = new SubscriptionData<UUID, GroupRoleDataReply>(UIThreadExecutor.getInstance());
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupRoles, this.groupProfile, this.myGroupList }).withOptionalLoadables(this.agentCircuit).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.hasChanged = false;
        this.textChangedListener = (TextWatcher)new TextWatcher() {
            public void afterTextChanged(final Editable editable) {
                GroupRoleDetailsFragment.this.updateUnsavedChanges();
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
        };
        this.permCheckboxClickListener = (View$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$3(this);
    }
    
    private boolean anyChanges() {
        final View view = this.getView();
        if (view != null) {
            long n;
            String stringFromVariableOEM;
            String stringFromVariableOEM2;
            String stringFromVariableOEM3;
            if (this.RoleID == null) {
                n = this.getDefaultPowers();
                stringFromVariableOEM = "";
                stringFromVariableOEM2 = "";
                stringFromVariableOEM3 = "";
            }
            else {
                final GroupRoleDataReply.RoleData selectedRoleData = this.getSelectedRoleData();
                if (selectedRoleData == null) {
                    return false;
                }
                stringFromVariableOEM = SLMessage.stringFromVariableOEM(selectedRoleData.Name);
                stringFromVariableOEM2 = SLMessage.stringFromVariableOEM(selectedRoleData.Title);
                stringFromVariableOEM3 = SLMessage.stringFromVariableOEM(selectedRoleData.Description);
                n = selectedRoleData.Powers;
            }
            return !Objects.equal(stringFromVariableOEM, ((TextView)view.findViewById(2131755427)).getText().toString()) || !Objects.equal(stringFromVariableOEM2, ((TextView)view.findViewById(2131755429)).getText().toString()) || !Objects.equal(stringFromVariableOEM3, ((TextView)view.findViewById(2131755431)).getText().toString()) || n != this.getSelectedPowers(n, (ViewGroup)view.findViewById(2131755435));
        }
        return false;
    }
    
    private void askForSavingChanges(final Runnable runnable) {
        final View view = this.getView();
        if (view != null) {
            final GroupRoleDataReply.RoleData selectedRoleData = this.getSelectedRoleData();
            long n;
            if (selectedRoleData == null) {
                n = this.getDefaultPowers();
            }
            else {
                n = selectedRoleData.Powers;
            }
            final String string = ((TextView)view.findViewById(2131755427)).getText().toString();
            final String string2 = ((TextView)view.findViewById(2131755429)).getText().toString();
            final String string3 = ((TextView)view.findViewById(2131755431)).getText().toString();
            final long selectedPowers = this.getSelectedPowers(n, (ViewGroup)view.findViewById(2131755435));
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131296992)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$6(selectedPowers, this, string, string2, string3, runnable)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$1(runnable));
            alertDialog$Builder.create().show();
        }
        else {
            runnable.run();
        }
    }
    
    private void confirmDeleteRole() {
        if ((this.getMyGroupPowers() & 0x20L) != 0x0L) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131296481)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$2(this)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E());
            alertDialog$Builder.create().show();
        }
    }
    
    private void createPermEntries(final LayoutInflater layoutInflater, final ViewGroup viewGroup) {
        for (final RolePermission rolePermission : GroupRoleDetailsFragment.rolePermissions) {
            final View inflate = layoutInflater.inflate(2130968654, viewGroup, false);
            ((CheckedTextView)inflate).setText(rolePermission.permName);
            inflate.setTag(2131755044, (Object)rolePermission.permMask);
            inflate.setEnabled(true);
            inflate.setClickable(true);
            inflate.setOnClickListener(this.permCheckboxClickListener);
            viewGroup.addView(inflate);
        }
    }
    
    private long getDefaultPowers() {
        return 134283266L;
    }
    
    private int getMemberCount() {
        final GroupRoleDataReply.RoleData selectedRoleData = this.getSelectedRoleData();
        if (selectedRoleData == null) {
            return 0;
        }
        if (!selectedRoleData.RoleID.equals(UUIDPool.ZeroUUID)) {
            return selectedRoleData.Members;
        }
        final GroupProfileReply groupProfileReply = this.groupProfile.getData();
        if (groupProfileReply != null) {
            return groupProfileReply.GroupData_Field.GroupMembershipCount;
        }
        return 0;
    }
    
    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return (AvatarGroupList.AvatarGroupEntry)this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            return null;
        }
    }
    
    private long getMyGroupPowers() {
        final AvatarGroupList.AvatarGroupEntry myGroupEntry = this.getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }
    
    private long getSelectedPowers(long n, final ViewGroup viewGroup) {
        final int childCount = viewGroup.getChildCount();
        int i = 0;
    Label_0076_Outer:
        while (i < childCount) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof CheckedTextView) {
                final Object tag = child.getTag(2131755044);
                if (tag instanceof Long) {
                    final long longValue = (long)tag;
                    if (((CheckedTextView)child).isChecked()) {
                        n |= longValue;
                    }
                    else {
                        n &= longValue;
                    }
                }
            }
            while (true) {
                ++i;
                continue Label_0076_Outer;
                continue;
            }
        }
        return n;
    }
    
    @Nullable
    private GroupRoleDataReply.RoleData getSelectedRoleData() {
        if (this.RoleID != null) {
            while (true) {
                try {
                    for (final GroupRoleDataReply.RoleData roleData : this.groupRoles.get().RoleData_Fields) {
                        if (Objects.equal(roleData.RoleID, this.RoleID)) {
                            return roleData;
                        }
                    }
                    return null;
                }
                catch (final SubscriptionData.DataNotReadyException ex) {
                    return null;
                }
                break;
                return null;
            }
        }
        return null;
    }
    
    public static Bundle makeSelection(final ChatterID chatterID, @Nullable final UUID uuid) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            selection.putString("role_id", uuid.toString());
        }
        return selection;
    }
    
    private void setLoadedValues() {
        final int n = 0;
        final GroupRoleDataReply.RoleData selectedRoleData = this.getSelectedRoleData();
        final long myGroupPowers = this.getMyGroupPowers();
        boolean b;
        if ((0x40L & myGroupPowers) != 0x0L) {
            b = true;
        }
        else {
            b = false;
        }
        final boolean equal = Objects.equal(this.RoleID, UUIDPool.ZeroUUID);
        final GroupProfileReply groupProfileReply = this.groupProfile.getData();
        final boolean b2 = groupProfileReply != null && Objects.equal(groupProfileReply.GroupData_Field.OwnerRole, this.RoleID);
        if (this.deleteMenuItem != null) {
            this.deleteMenuItem.setVisible(this.RoleID != null && (myGroupPowers & 0x20L) != 0x0L && (b2 ^ true) && (equal ^ true));
        }
        final View view = this.getView();
        if (view != null) {
            if (selectedRoleData != null) {
                final int memberCount = this.getMemberCount();
                ((EditText)view.findViewById(2131755427)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((TextView)view.findViewById(2131755426)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((EditText)view.findViewById(2131755429)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((TextView)view.findViewById(2131755428)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((EditText)view.findViewById(2131755431)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView)view.findViewById(2131755430)).setText((CharSequence)SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView)view.findViewById(2131755390)).setText((CharSequence)this.getResources().getQuantityString(2131820545, memberCount, new Object[] { memberCount }));
                this.setPermissionCheckboxes(selectedRoleData.Powers, (ViewGroup)view.findViewById(2131755435));
                view.findViewById(2131755432).setVisibility(0);
            }
            else {
                view.findViewById(2131755432).setVisibility(8);
                ((EditText)view.findViewById(2131755427)).setText((CharSequence)"");
                ((EditText)view.findViewById(2131755429)).setText((CharSequence)"");
                ((EditText)view.findViewById(2131755431)).setText((CharSequence)"");
                ((TextView)view.findViewById(2131755426)).setText((CharSequence)"");
                ((TextView)view.findViewById(2131755428)).setText((CharSequence)"");
                ((TextView)view.findViewById(2131755430)).setText((CharSequence)"");
                ((TextView)view.findViewById(2131755390)).setText((CharSequence)"");
                this.setPermissionCheckboxes(this.getDefaultPowers(), (ViewGroup)view.findViewById(2131755435));
            }
            final View viewById = view.findViewById(2131755427);
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
            final View viewById2 = view.findViewById(2131755426);
            int visibility2;
            if (!b) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            viewById2.setVisibility(visibility2);
            final View viewById3 = view.findViewById(2131755429);
            int visibility3;
            if (b) {
                visibility3 = 0;
            }
            else {
                visibility3 = 8;
            }
            viewById3.setVisibility(visibility3);
            final View viewById4 = view.findViewById(2131755428);
            int visibility4;
            if (!b) {
                visibility4 = 0;
            }
            else {
                visibility4 = 8;
            }
            viewById4.setVisibility(visibility4);
            final View viewById5 = view.findViewById(2131755431);
            int visibility5;
            if (b) {
                visibility5 = 0;
            }
            else {
                visibility5 = 8;
            }
            viewById5.setVisibility(visibility5);
            final View viewById6 = view.findViewById(2131755430);
            int visibility6;
            if (!b) {
                visibility6 = n;
            }
            else {
                visibility6 = 8;
            }
            viewById6.setVisibility(visibility6);
        }
        this.updateUnsavedChanges();
    }
    
    private void setPermissionCheckboxes(final long n, final ViewGroup viewGroup) {
        for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof CheckedTextView) {
                final Object tag = child.getTag(2131755044);
                if (tag instanceof Long) {
                    ((CheckedTextView)child).setChecked(((long)tag & n) != 0x0L);
                }
            }
        }
    }
    
    private void updateUnsavedChanges() {
        final boolean anyChanges = this.anyChanges();
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges;
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged);
            }
        }
    }
    
    @Override
    public boolean onBackButtonPressed() {
        if (this.anyChanges()) {
            this.askForSavingChanges(new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$5(this));
            return true;
        }
        return false;
    }
    
    @Override
    public void onCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886088, menu);
        (this.undoMenuItem = menu.findItem(2131755778)).setVisible(this.hasChanged);
        (this.deleteMenuItem = menu.findItem(2131755788)).setVisible(false);
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968652, viewGroup, false);
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        ((TextView)inflate.findViewById(2131755427)).addTextChangedListener(this.textChangedListener);
        ((TextView)inflate.findViewById(2131755429)).addTextChangedListener(this.textChangedListener);
        ((TextView)inflate.findViewById(2131755431)).addTextChangedListener(this.textChangedListener);
        this.createPermEntries(layoutInflater, (ViewGroup)inflate.findViewById(2131755435));
        inflate.findViewById(2131755433).setOnClickListener((View$OnClickListener)new _$Lambda$oqvWEi5fLgnwnCXV95inckWtW_E$4(this));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        this.setLoadedValues();
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755778: {
                this.setLoadedValues();
                return true;
            }
            case 2131755788: {
                this.confirmDeleteRole();
                return true;
            }
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.getArguments().containsKey("role_id")) {
            this.RoleID = UUIDPool.getUUID(this.getArguments().getString("role_id"));
        }
        else {
            this.RoleID = null;
        }
        Debug.Printf("Group role details: new role = %s", this.RoleID);
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        }
    }
    
    private static class RolePermission
    {
        final long permMask;
        final int permName;
        
        private RolePermission(final long permMask, final int permName) {
            this.permMask = permMask;
            this.permName = permName;
        }
    }
}
