package com.lumiyaviewer.lumiya.ui.objects;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;
import com.lumiyaviewer.lumiya.ui.objects.TaskInventoryFragment;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class TaskInventoryFragment extends FragmentWithTitle {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f501x3332d2fb = null;
    private static final String OBJECT_LOCAL_ID_KEY = "objectLocalId";
    private static final String OBJECT_UUID_KEY = "objectUUID";
    private Subscription<Integer, SLObjectProfileData> objectProfileSubscription;
    @Nullable
    private SLTaskInventory taskInventory;
    private Subscription<Integer, SLTaskInventory> taskInventorySubscription;
    @Nullable
    private SLObjectProfileData objectProfileData = null;
    private final Subscription.OnData<SLTaskInventory> onTaskInventoryReceived = new Subscription.OnData<SLTaskInventory>() { // from class: com.lumiyaviewer.lumiya.ui.objects.TaskInventoryFragment.1
        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public void onData(SLTaskInventory sLTaskInventory) {
            TaskInventoryFragment.this.taskInventory = sLTaskInventory;
            View view = TaskInventoryFragment.this.getView();
            if (view != null) {
                ListAdapter adapter = ((ListView) view.findViewById(R.id.taskInventoryListView)).getAdapter();
                if (adapter instanceof TaskInventoryListAdapter) {
                    ((TaskInventoryListAdapter) adapter).setData(sLTaskInventory);
                }
                ((TextView) view.findViewById(R.id.taskInventoryEmptyText)).setText(R.string.object_contents_empty);
                view.findViewById(R.id.taskInventoryLoading).setVisibility(8);
            }
        }
    };
    private final Subscription.OnData<SLObjectProfileData> onObjectProfileData = new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.3
        private final /* synthetic */ void $m$0(Object obj) {
            ((TaskInventoryFragment) this).m700x1db91107((SLObjectProfileData) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.lumiyaviewer.lumiya.ui.objects.TaskInventoryFragment$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements Function<UUID, Void> {
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ int val$taskID;
        final /* synthetic */ UserManager val$userManager;

        AnonymousClass2(UserManager userManager, int i, ProgressDialog progressDialog) {
            this.val$userManager = userManager;
            this.val$taskID = i;
            this.val$progressDialog = progressDialog;
        }

        @Override // com.google.common.base.Function
        @Nullable
        public Void apply(@Nullable final UUID uuid) {
            Executor uIThreadExecutor = UIThreadExecutor.getInstance();
            final int i = this.val$taskID;
            final UserManager userManager = this.val$userManager;
            final ProgressDialog progressDialog = this.val$progressDialog;
            uIThreadExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.4
                private final /* synthetic */ void $m$0() {
                    ((TaskInventoryFragment.AnonymousClass2) this).m701x9b244ddf((UserManager) userManager, i, (ProgressDialog) progressDialog, (UUID) uuid);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment$2_10938  reason: not valid java name */
        public /* synthetic */ void m701x9b244ddf(UserManager userManager, int i, ProgressDialog progressDialog, UUID uuid) {
            userManager.getObjectsManager().requestTaskInventoryUpdate(i);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (uuid != null) {
                    Debug.Printf("TaskInventory: going to display target folder: %s", uuid);
                    TaskInventoryFragment.this.startActivity(InventoryActivity.makeFolderIntent(TaskInventoryFragment.this.getContext(), userManager.getUserID(), uuid));
                }
            }
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m695x9bd6d19f() {
        if (f501x3332d2fb != null) {
            return f501x3332d2fb;
        }
        int[] iArr = new int[SLAssetType.valuesCustom().length];
        try {
            iArr[SLAssetType.AT_ANIMATION.ordinal()] = 3;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SLAssetType.AT_BODYPART.ordinal()] = 4;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SLAssetType.AT_CALLINGCARD.ordinal()] = 5;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[SLAssetType.AT_CATEGORY.ordinal()] = 6;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[SLAssetType.AT_CLOTHING.ordinal()] = 7;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[SLAssetType.AT_GESTURE.ordinal()] = 8;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[SLAssetType.AT_IMAGE_JPEG.ordinal()] = 9;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[SLAssetType.AT_IMAGE_TGA.ordinal()] = 10;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[SLAssetType.AT_LANDMARK.ordinal()] = 11;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[SLAssetType.AT_LINK.ordinal()] = 12;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[SLAssetType.AT_LINK_FOLDER.ordinal()] = 13;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[SLAssetType.AT_LSL_BYTECODE.ordinal()] = 14;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[SLAssetType.AT_LSL_TEXT.ordinal()] = 1;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[SLAssetType.AT_MESH.ordinal()] = 15;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[SLAssetType.AT_NOTECARD.ordinal()] = 2;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[SLAssetType.AT_OBJECT.ordinal()] = 16;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[SLAssetType.AT_SCRIPT.ordinal()] = 17;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[SLAssetType.AT_SIMSTATE.ordinal()] = 18;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[SLAssetType.AT_SOUND.ordinal()] = 19;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[SLAssetType.AT_SOUND_WAV.ordinal()] = 20;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[SLAssetType.AT_TEXTURE.ordinal()] = 21;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[SLAssetType.AT_TEXTURE_TGA.ordinal()] = 22;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[SLAssetType.AT_UNKNOWN.ordinal()] = 23;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[SLAssetType.AT_WIDGET.ordinal()] = 24;
        } catch (NoSuchFieldError e24) {
        }
        f501x3332d2fb = iArr;
        return iArr;
    }

    private boolean canModifyObject() {
        UserManager userManager = getUserManager();
        if (this.objectProfileData == null || userManager == null || !userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
            return false;
        }
        return this.objectProfileData.isModifiable();
    }

    private boolean canModifyObjectContents(SLInventoryEntry sLInventoryEntry) {
        UserManager userManager = getUserManager();
        if (userManager != null) {
            return userManager.getUserID().equals(sLInventoryEntry.ownerUUID) ? (sLInventoryEntry.ownerMask & 16384) != 0 : (sLInventoryEntry.everyoneMask & 16384) != 0;
        }
        return false;
    }

    private void copyAllToInventory(boolean z) {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        boolean z2;
        boolean z3 = false;
        int objectLocalID = getObjectLocalID();
        UserManager userManager = getUserManager();
        if (this.taskInventory == null || this.objectProfileData == null || userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) {
            return;
        }
        SLInventory sLInventory = modules.inventory;
        if (this.taskInventory.entries.size() == 0) {
            return;
        }
        if (!userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
            Toast.makeText(getActivity(), (int) R.string.object_contents_not_owned, 1).show();
            return;
        }
        if (!z) {
            Iterator<SLInventoryEntry> it = this.taskInventory.entries.iterator();
            while (true) {
                z2 = z3;
                if (!it.hasNext()) {
                    break;
                }
                z3 = (it.next().ownerMask & 32768) == 0 ? true : z2;
            }
            if (z2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.object_contents_has_no_copy).setPositiveButton(R.string.object_contents_yes_move, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.1
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        ((TaskInventoryFragment) this).m698x992e5209(dialogInterface, i);
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                });
                builder.create().show();
                return;
            }
        }
        String or = this.objectProfileData.name().or((Optional<String>) getString(R.string.default_object_contents_folder));
        HashSet hashSet = new HashSet();
        for (SLInventoryEntry sLInventoryEntry : this.taskInventory.entries) {
            hashSet.add(sLInventoryEntry.uuid);
        }
        sLInventory.CopyObjectContents(or, objectLocalID, hashSet, new AnonymousClass2(userManager, objectLocalID, ProgressDialog.show(getContext(), null, getString(R.string.copying_object_contents), true, true)));
    }

    private int getObjectLocalID() {
        return getArguments().getInt(OBJECT_LOCAL_ID_KEY);
    }

    private UUID getObjectUUID() {
        return UUID.fromString(getArguments().getString(OBJECT_UUID_KEY));
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    private void handleObjectClick(int i) {
        SLInventoryEntry item;
        UserManager userManager = getUserManager();
        if (getView() == null || userManager == null) {
            return;
        }
        ListAdapter adapter = ((ListView) getView().findViewById(R.id.taskInventoryListView)).getAdapter();
        if (!(adapter instanceof TaskInventoryListAdapter) || (item = ((TaskInventoryListAdapter) adapter).getItem(i)) == null) {
            return;
        }
        switch (m695x9bd6d19f()[SLAssetType.getByType(item.assetType).ordinal()]) {
            case 1:
                if (canModifyObject() && canModifyObjectContents(item)) {
                    startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), null, item, true, getObjectUUID(), getObjectLocalID()));
                    return;
                }
                return;
            case 2:
                if (canModifyObject() && canModifyObjectContents(item)) {
                    startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), null, item, false, getObjectUUID(), getObjectLocalID()));
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid2, int i) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid2 != null) {
            bundle.putString(OBJECT_UUID_KEY, uuid2.toString());
        }
        bundle.putInt(OBJECT_LOCAL_ID_KEY, i);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_10017  reason: not valid java name */
    public /* synthetic */ void m698x992e5209(DialogInterface dialogInterface, int i) {
        copyAllToInventory(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_3527  reason: not valid java name */
    public /* synthetic */ void m699x1db827b5(AdapterView adapterView, View view, int i, long j) {
        handleObjectClick(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_5570  reason: not valid java name */
    public /* synthetic */ void m700x1db91107(SLObjectProfileData sLObjectProfileData) {
        this.objectProfileData = sLObjectProfileData;
        if (this.objectProfileData.name().isPresent()) {
            setTitle(getString(R.string.object_contents_title), this.objectProfileData.name().or((Optional<String>) getString(R.string.object_name_loading)));
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.task_inventory_menu, menu);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.task_inventory, viewGroup, false);
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setAdapter((ListAdapter) new TaskInventoryListAdapter(layoutInflater.getContext()));
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setEmptyView(inflate.findViewById(16908292));
        ((ListView) inflate.findViewById(R.id.taskInventoryListView)).setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0.2
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((TaskInventoryFragment) this).m699x1db827b5(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_copy_all /* 2131755858 */:
                copyAllToInventory(false);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.object_contents_title), null);
        UserManager userManager = getUserManager();
        if (userManager != null) {
            int objectLocalID = getObjectLocalID();
            this.taskInventorySubscription = userManager.getObjectsManager().getObjectTaskInventory().subscribe((Subscribable<Integer, SLTaskInventory>) Integer.valueOf(objectLocalID), UIThreadExecutor.getInstance(), this.onTaskInventoryReceived);
            this.objectProfileSubscription = userManager.getObjectsManager().getObjectProfile().subscribe((Subscribable<Integer, SLObjectProfileData>) Integer.valueOf(objectLocalID), UIThreadExecutor.getInstance(), this.onObjectProfileData);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        if (this.taskInventorySubscription != null) {
            this.taskInventorySubscription.unsubscribe();
            this.taskInventorySubscription = null;
        }
        if (this.objectProfileSubscription != null) {
            this.objectProfileSubscription.unsubscribe();
            this.objectProfileSubscription = null;
        }
        super.onStop();
    }
}
