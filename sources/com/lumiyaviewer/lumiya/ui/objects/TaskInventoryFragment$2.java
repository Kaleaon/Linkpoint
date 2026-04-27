// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.app.ProgressDialog;
import com.google.common.base.Function;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            TaskInventoryFragment

class val.progressDialog
    implements Function
{

    final TaskInventoryFragment this$0;
    final ProgressDialog val$progressDialog;
    final int val$taskID;
    final UserManager val$userManager;

    public volatile Object apply(Object obj)
    {
        return apply((UUID)obj);
    }

    public Void apply(UUID uuid)
    {
        UIThreadExecutor.getInstance().execute(new Bm9YiYK9KyJ0._cls4(val$taskID, this, val$userManager, val$progressDialog, uuid));
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

    r()
    {
        this$0 = final_taskinventoryfragment;
        val$userManager = usermanager;
        val$taskID = i;
        val$progressDialog = ProgressDialog.this;
        super();
    }
}
