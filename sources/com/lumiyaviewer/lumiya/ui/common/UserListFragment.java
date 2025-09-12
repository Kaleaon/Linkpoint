// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            DetailsActivity, SwipeDismissListViewTouchListener, ActivityUtils, DismissableAdapter

public abstract class UserListFragment extends Fragment
{

    protected UserManager userManager;

    public UserListFragment()
    {
        userManager = null;
    }

    private void updateListViews()
    {
        Object obj = getView();
        if (obj != null)
        {
            obj = (ListView)((View) (obj)).findViewById(0x7f10014a);
            if (obj != null)
            {
                ((ListView) (obj)).invalidateViews();
            }
        }
    }

    protected abstract ListAdapter createListAdapter(Context context, LoaderManager loadermanager, UserManager usermanager);

    protected void handleUserDefaultAction(ChatterID chatterid)
    {
        if (userManager != null)
        {
            chatterid = ChatFragment.makeSelection(chatterid);
            Bundle bundle = getArguments();
            if (bundle.containsKey("vrMode"))
            {
                chatterid.putBoolean("vrMode", bundle.getBoolean("vrMode"));
            }
            DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), chatterid);
        }
    }

    protected boolean itemsMayBeDismissed()
    {
        return false;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = ((AdapterView) (adapterview.getItemAtPosition(i)));
        if ((adapterview instanceof ChatterDisplayInfo) && userManager != null)
        {
            adapterview = ((ChatterDisplayInfo)adapterview).getChatterID(userManager);
            if (adapterview != null)
            {
                handleUserDefaultAction(adapterview);
            }
        }
    }

    public void onActivityCreated(Bundle bundle)
    {
        super.onActivityCreated(bundle);
        bundle = getView();
        if (bundle != null)
        {
            bundle = (ListView)bundle.findViewById(0x7f10014a);
            bundle.setOnItemClickListener(new _2D_.Lambda._cls1wR8wJi1e_2D_GgAIYEhals_u5j3nM(this));
            registerForContextMenu(bundle);
            if (itemsMayBeDismissed())
            {
                SwipeDismissListViewTouchListener swipedismisslistviewtouchlistener = new SwipeDismissListViewTouchListener(bundle, new SwipeDismissListViewTouchListener.DismissCallbacks() {

                    final UserListFragment this$0;

                    public boolean canDismiss(ListView listview, int i)
                    {
                        listview = listview.getAdapter();
                        if (listview instanceof DismissableAdapter)
                        {
                            return ((DismissableAdapter)listview).canDismiss(i);
                        } else
                        {
                            return false;
                        }
                    }

                    public void onDismiss(ListView listview, int i)
                    {
                        listview = listview.getAdapter();
                        if (listview instanceof DismissableAdapter)
                        {
                            ((DismissableAdapter)listview).onDismiss(i);
                        }
                    }

            
            {
                this$0 = UserListFragment.this;
                super();
            }
                });
                bundle.setOnTouchListener(swipedismisslistviewtouchlistener);
                bundle.setOnScrollListener(swipedismisslistviewtouchlistener.makeScrollListener());
            }
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        userManager = ActivityUtils.getUserManager(getArguments());
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        return layoutinflater.inflate(0x7f04002e, viewgroup, false);
    }

    public void onStart()
    {
        ListAdapter listadapter = null;
        super.onStart();
        Object obj = getView();
        Debug.Printf("UserListFragment: onStart, rootView = %s", new Object[] {
            obj
        });
        if (obj != null)
        {
            obj = (ListView)((View) (obj)).findViewById(0x7f10014a);
            if (obj != null && ((ListView) (obj)).getAdapter() == null)
            {
                UserManager usermanager = ActivityUtils.getUserManager(getArguments());
                if (usermanager != null)
                {
                    listadapter = createListAdapter(getActivity(), getLoaderManager(), usermanager);
                }
                ((ListView) (obj)).setAdapter(listadapter);
            }
        }
    }

    public void onStop()
    {
        Object obj = getView();
        Debug.Printf("UserListFragment: onStop, rootView = %s", new Object[] {
            obj
        });
        if (obj != null)
        {
            obj = (ListView)((View) (obj)).findViewById(0x7f10014a);
            if (obj != null)
            {
                ListAdapter listadapter = ((ListView) (obj)).getAdapter();
                if (listadapter instanceof Closeable)
                {
                    try
                    {
                        ((Closeable)listadapter).close();
                    }
                    catch (IOException ioexception)
                    {
                        Debug.Warning(ioexception);
                    }
                }
                ((ListView) (obj)).setAdapter(null);
            }
        }
        super.onStop();
    }

    public void onUserInfoChanged(EventUserInfoChanged eventuserinfochanged)
    {
        if (userManager != null && userManager.getUserID().equals(eventuserinfochanged.agentUUID) && eventuserinfochanged.isProfileChanged())
        {
            updateListViews();
        }
    }
}
