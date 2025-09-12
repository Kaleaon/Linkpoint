// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
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
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            TaskInventoryListAdapter

public class TaskInventoryFragment extends FragmentWithTitle
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues[];
    private static final String OBJECT_LOCAL_ID_KEY = "objectLocalId";
    private static final String OBJECT_UUID_KEY = "objectUUID";
    private SLObjectProfileData objectProfileData;
    private Subscription objectProfileSubscription;
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onObjectProfileData = new _2D_.Lambda._cls2R1p9WuPUwPagPVBm9YiYK9KyJ0._cls3(this);
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onTaskInventoryReceived = new com.lumiyaviewer.lumiya.react.Subscription.OnData() {

        final TaskInventoryFragment this$0;

        public void onData(SLTaskInventory sltaskinventory)
        {
            TaskInventoryFragment._2D_set0(TaskInventoryFragment.this, sltaskinventory);
            View view = getView();
            if (view != null)
            {
                android.widget.ListAdapter listadapter = ((ListView)view.findViewById(0x7f10028c)).getAdapter();
                if (listadapter instanceof TaskInventoryListAdapter)
                {
                    ((TaskInventoryListAdapter)listadapter).setData(sltaskinventory);
                }
                ((TextView)view.findViewById(0x7f10028e)).setText(0x7f090218);
                view.findViewById(0x7f10028d).setVisibility(8);
            }
        }

        public volatile void onData(Object obj)
        {
            onData((SLTaskInventory)obj);
        }

            
            {
                this$0 = TaskInventoryFragment.this;
                super();
            }
    };
    private SLTaskInventory taskInventory;
    private Subscription taskInventorySubscription;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues;
        }
        int ai[] = new int[SLAssetType.values().length];
        try
        {
            ai[SLAssetType.AT_ANIMATION.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror23) { }
        try
        {
            ai[SLAssetType.AT_BODYPART.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror22) { }
        try
        {
            ai[SLAssetType.AT_CALLINGCARD.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror21) { }
        try
        {
            ai[SLAssetType.AT_CATEGORY.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror20) { }
        try
        {
            ai[SLAssetType.AT_CLOTHING.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror19) { }
        try
        {
            ai[SLAssetType.AT_GESTURE.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror18) { }
        try
        {
            ai[SLAssetType.AT_IMAGE_JPEG.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror17) { }
        try
        {
            ai[SLAssetType.AT_IMAGE_TGA.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror16) { }
        try
        {
            ai[SLAssetType.AT_LANDMARK.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror15) { }
        try
        {
            ai[SLAssetType.AT_LINK.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror14) { }
        try
        {
            ai[SLAssetType.AT_LINK_FOLDER.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror13) { }
        try
        {
            ai[SLAssetType.AT_LSL_BYTECODE.ordinal()] = 14;
        }
        catch (NoSuchFieldError nosuchfielderror12) { }
        try
        {
            ai[SLAssetType.AT_LSL_TEXT.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror11) { }
        try
        {
            ai[SLAssetType.AT_MESH.ordinal()] = 15;
        }
        catch (NoSuchFieldError nosuchfielderror10) { }
        try
        {
            ai[SLAssetType.AT_NOTECARD.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[SLAssetType.AT_OBJECT.ordinal()] = 16;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[SLAssetType.AT_SCRIPT.ordinal()] = 17;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[SLAssetType.AT_SIMSTATE.ordinal()] = 18;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[SLAssetType.AT_SOUND.ordinal()] = 19;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[SLAssetType.AT_SOUND_WAV.ordinal()] = 20;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[SLAssetType.AT_TEXTURE.ordinal()] = 21;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[SLAssetType.AT_TEXTURE_TGA.ordinal()] = 22;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[SLAssetType.AT_UNKNOWN.ordinal()] = 23;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[SLAssetType.AT_WIDGET.ordinal()] = 24;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues = ai;
        return ai;
    }

    static SLTaskInventory _2D_set0(TaskInventoryFragment taskinventoryfragment, SLTaskInventory sltaskinventory)
    {
        taskinventoryfragment.taskInventory = sltaskinventory;
        return sltaskinventory;
    }

    public TaskInventoryFragment()
    {
        objectProfileData = null;
    }

    private boolean canModifyObject()
    {
        UserManager usermanager = getUserManager();
        if (objectProfileData != null && usermanager != null && usermanager.getUserID().equals(objectProfileData.ownerUUID()))
        {
            return objectProfileData.isModifiable();
        } else
        {
            return false;
        }
    }

    private boolean canModifyObjectContents(SLInventoryEntry slinventoryentry)
    {
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            if (usermanager.getUserID().equals(slinventoryentry.ownerUUID))
            {
                return (slinventoryentry.ownerMask & 0x4000) != 0;
            }
            return (slinventoryentry.everyoneMask & 0x4000) != 0;
        } else
        {
            return false;
        }
    }

    private void copyAllToInventory(boolean flag)
    {
        final int taskID = getObjectLocalID();
        final Object userManager;
        for (userManager = getUserManager(); taskInventory == null || objectProfileData == null || userManager == null;)
        {
            return;
        }

        Object obj = ((UserManager) (userManager)).getActiveAgentCircuit();
        if (obj == null)
        {
            return;
        }
        obj = ((SLAgentCircuit) (obj)).getModules();
        if (obj == null)
        {
            return;
        }
        obj = ((SLModules) (obj)).inventory;
        if (taskInventory.entries.size() == 0)
        {
            return;
        }
        if (!((UserManager) (userManager)).getUserID().equals(objectProfileData.ownerUUID()))
        {
            Toast.makeText(getActivity(), 0x7f09021b, 1).show();
            return;
        }
        if (!flag)
        {
            Iterator iterator = taskInventory.entries.iterator();
            boolean flag1 = false;
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                if ((((SLInventoryEntry)iterator.next()).ownerMask & 0x8000) == 0)
                {
                    flag1 = true;
                }
            } while (true);
            if (flag1)
            {
                userManager = new android.support.v7.app.AlertDialog.Builder(getActivity());
                ((android.support.v7.app.AlertDialog.Builder) (userManager)).setMessage(0x7f090219).setPositiveButton(0x7f09021d, new _2D_.Lambda._cls2R1p9WuPUwPagPVBm9YiYK9KyJ0._cls1(this)).setNegativeButton(0x7f0900a8, new _2D_.Lambda._cls2R1p9WuPUwPagPVBm9YiYK9KyJ0());
                ((android.support.v7.app.AlertDialog.Builder) (userManager)).create().show();
                return;
            }
        }
        String s = (String)objectProfileData.name().or(getString(0x7f0900da));
        HashSet hashset = new HashSet();
        for (Iterator iterator1 = taskInventory.entries.iterator(); iterator1.hasNext(); hashset.add(((SLInventoryEntry)iterator1.next()).uuid)) { }
        ((SLInventory) (obj)).CopyObjectContents(s, taskID, hashset, new Function() {

            final TaskInventoryFragment this$0;
            final ProgressDialog val$progressDialog;
            final int val$taskID;
            final UserManager val$userManager;

            public volatile Object apply(Object obj1)
            {
                return apply((UUID)obj1);
            }

            public Void apply(UUID uuid)
            {
                UIThreadExecutor.getInstance().execute(new _2D_.Lambda._cls2R1p9WuPUwPagPVBm9YiYK9KyJ0._cls4(taskID, this, userManager, progressDialog, uuid));
                return null;
            }

            void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment$2_10938(UserManager usermanager, int i, ProgressDialog progressdialog, UUID uuid)
            {
                usermanager.getObjectsManager().requestTaskInventoryUpdate(i);
                if (progressdialog.isShowing())
                {
                    progressdialog.dismiss();
                    if (uuid != null)
                    {
                        Debug.Printf("TaskInventory: going to display target folder: %s", new Object[] {
                            uuid
                        });
                        startActivity(InventoryActivity.makeFolderIntent(getContext(), usermanager.getUserID(), uuid));
                    }
                }
            }

            
            {
                this$0 = TaskInventoryFragment.this;
                userManager = usermanager;
                taskID = i;
                progressDialog = progressdialog;
                super();
            }
        });
    }

    private int getObjectLocalID()
    {
        return getArguments().getInt("objectLocalId");
    }

    private UUID getObjectUUID()
    {
        return UUID.fromString(getArguments().getString("objectUUID"));
    }

    private UserManager getUserManager()
    {
        return ActivityUtils.getUserManager(getArguments());
    }

    private void handleObjectClick(int i)
    {
        UserManager usermanager = getUserManager();
        if (getView() == null || usermanager == null) goto _L2; else goto _L1
_L1:
        Object obj = ((ListView)getView().findViewById(0x7f10028c)).getAdapter();
        if (!(obj instanceof TaskInventoryListAdapter)) goto _L2; else goto _L3
_L3:
        obj = ((TaskInventoryListAdapter)obj).getItem(i);
        if (obj == null) goto _L2; else goto _L4
_L4:
        SLAssetType slassettype = SLAssetType.getByType(((SLInventoryEntry) (obj)).assetType);
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLAssetTypeSwitchesValues()[slassettype.ordinal()];
        JVM INSTR tableswitch 1 2: default 92
    //                   1 135
    //                   2 93;
           goto _L2 _L5 _L6
_L2:
        return;
_L6:
        if (canModifyObject() && canModifyObjectContents(((SLInventoryEntry) (obj))))
        {
            startActivity(NotecardEditActivity.createIntent(getContext(), usermanager.getUserID(), null, ((SLInventoryEntry) (obj)), false, getObjectUUID(), getObjectLocalID()));
            return;
        }
        continue; /* Loop/switch isn't completed */
_L5:
        if (canModifyObject() && canModifyObjectContents(((SLInventoryEntry) (obj))))
        {
            startActivity(NotecardEditActivity.createIntent(getContext(), usermanager.getUserID(), null, ((SLInventoryEntry) (obj)), true, getObjectUUID(), getObjectLocalID()));
            return;
        }
        if (true) goto _L2; else goto _L7
_L7:
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_10120(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid1, int i)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid1 != null)
        {
            bundle.putString("objectUUID", uuid1.toString());
        }
        bundle.putInt("objectLocalId", i);
        return bundle;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_10017(DialogInterface dialoginterface, int i)
    {
        copyAllToInventory(true);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_3527(AdapterView adapterview, View view, int i, long l)
    {
        handleObjectClick(i);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_TaskInventoryFragment_5570(SLObjectProfileData slobjectprofiledata)
    {
        objectProfileData = slobjectprofiledata;
        if (objectProfileData.name().isPresent())
        {
            setTitle(getString(0x7f09021c), (String)objectProfileData.name().or(getString(0x7f090239)));
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
        menuinflater.inflate(0x7f120020, menu);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f0400a7, viewgroup, false);
        layoutinflater = new TaskInventoryListAdapter(layoutinflater.getContext());
        ((ListView)viewgroup.findViewById(0x7f10028c)).setAdapter(layoutinflater);
        ((ListView)viewgroup.findViewById(0x7f10028c)).setEmptyView(viewgroup.findViewById(0x1020004));
        ((ListView)viewgroup.findViewById(0x7f10028c)).setOnItemClickListener(new _2D_.Lambda._cls2R1p9WuPUwPagPVBm9YiYK9KyJ0._cls2(this));
        return viewgroup;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755858: 
            copyAllToInventory(false);
            break;
        }
        return true;
    }

    public void onStart()
    {
        super.onStart();
        setTitle(getString(0x7f09021c), null);
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            int i = getObjectLocalID();
            taskInventorySubscription = usermanager.getObjectsManager().getObjectTaskInventory().subscribe(Integer.valueOf(i), UIThreadExecutor.getInstance(), onTaskInventoryReceived);
            objectProfileSubscription = usermanager.getObjectsManager().getObjectProfile().subscribe(Integer.valueOf(i), UIThreadExecutor.getInstance(), onObjectProfileData);
        }
    }

    public void onStop()
    {
        if (taskInventorySubscription != null)
        {
            taskInventorySubscription.unsubscribe();
            taskInventorySubscription = null;
        }
        if (objectProfileSubscription != null)
        {
            objectProfileSubscription.unsubscribe();
            objectProfileSubscription = null;
        }
        super.onStop();
    }
}
