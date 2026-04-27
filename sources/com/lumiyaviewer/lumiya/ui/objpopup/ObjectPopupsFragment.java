// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatLayoutManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objpopup:
//            ObjectPopupsAdapter

public class ObjectPopupsFragment extends Fragment
{

    private static final String AGENT_UUID_KEY = "agentUUID";
    private final android.support.v7.widget.helper.ItemTouchHelper.Callback itemTouchCallback = new android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback(0, 12) {

        final ObjectPopupsFragment this$0;

        public boolean onMove(RecyclerView recyclerview, android.support.v7.widget.RecyclerView.ViewHolder viewholder, android.support.v7.widget.RecyclerView.ViewHolder viewholder1)
        {
            return false;
        }

        public void onSwiped(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
        {
            UserManager usermanager = ObjectPopupsFragment._2D_wrap0(ObjectPopupsFragment.this);
            Object obj = getView();
            if (obj != null && usermanager != null)
            {
                obj = (RecyclerView)((View) (obj)).findViewById(0x7f10023f);
                if (obj != null)
                {
                    obj = ((RecyclerView) (obj)).getAdapter();
                    if (obj != null)
                    {
                        i = viewholder.getAdapterPosition();
                        if (obj instanceof ObjectPopupsAdapter)
                        {
                            viewholder = (SLChatEvent)((ObjectPopupsAdapter)obj).getObject(i);
                            usermanager.getObjectPopupsManager().cancelObjectPopup(viewholder);
                        }
                    }
                }
            }
        }

            
            {
                this$0 = ObjectPopupsFragment.this;
                super(i, j);
            }
    };

    static UserManager _2D_wrap0(ObjectPopupsFragment objectpopupsfragment)
    {
        return objectpopupsfragment.getUserManager();
    }

    public ObjectPopupsFragment()
    {
    }

    public static ObjectPopupsFragment create(UUID uuid)
    {
        ObjectPopupsFragment objectpopupsfragment = new ObjectPopupsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", uuid.toString());
        objectpopupsfragment.setArguments(bundle);
        return objectpopupsfragment;
    }

    private UserManager getUserManager()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("agentUUID"))
        {
            return UserManager.getUserManager(UUID.fromString(bundle.getString("agentUUID")));
        } else
        {
            return null;
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f04007a, viewgroup, false);
        bundle = (RecyclerView)viewgroup.findViewById(0x7f10023f);
        bundle.setHasFixedSize(true);
        bundle.setLayoutManager(new ChatLayoutManager(layoutinflater.getContext(), 1, false));
        (new ItemTouchHelper(itemTouchCallback)).attachToRecyclerView(bundle);
        return viewgroup;
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = getUserManager();
        Object obj = getView();
        if (usermanager != null && obj != null)
        {
            obj = (RecyclerView)((View) (obj)).findViewById(0x7f10023f);
            if (obj != null)
            {
                ((RecyclerView) (obj)).setAdapter(new ObjectPopupsAdapter(getContext(), usermanager.getObjectPopupsManager().getObjectPopups(), usermanager));
            }
        }
    }

    public void onStop()
    {
        Object obj = getView();
        if (obj != null)
        {
            obj = (RecyclerView)((View) (obj)).findViewById(0x7f10023f);
            if (obj != null)
            {
                ((RecyclerView) (obj)).setAdapter(null);
            }
        }
        super.onStop();
    }
}
