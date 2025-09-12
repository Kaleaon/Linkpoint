// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfoWithChildren;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ButteryProgressBar;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectDetailsFragment, ObjectListAdapter

public class ObjectSelectorFragment extends Fragment
    implements android.widget.SeekBar.OnSeekBarChangeListener, android.widget.CompoundButton.OnCheckedChangeListener, android.widget.ExpandableListView.OnGroupClickListener, android.widget.ExpandableListView.OnChildClickListener
{

    private static final int MAX_FILTER_DISTANCE = 256;
    private static final int PROGRESS_BAR_SIZE_DIP = 4;
    private SLObjectFilterInfo filterInfo;
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onObjectListData = new _2D_.Lambda.rXtKRyOts6GGB3GxWNYA5oEvU2Y(this);
    private final com.lumiyaviewer.lumiya.react.Subscription.OnError onObjectListError = new _2D_.Lambda.rXtKRyOts6GGB3GxWNYA5oEvU2Y._cls1(this);
    private SearchView searchView;
    private Subscription subscription;

    static void _2D_wrap0(ObjectSelectorFragment objectselectorfragment)
    {
        objectselectorfragment.updateFilter();
    }

    public ObjectSelectorFragment()
    {
        filterInfo = SLObjectFilterInfo.create();
    }

    private SLObjectFilterInfo getFilter()
    {
        float f = 1.0F;
        View view = getView();
        if (view == null)
        {
            return SLObjectFilterInfo.create();
        }
        if (view.findViewById(0x7f100229).getVisibility() == 0)
        {
            float f1 = ((SeekBar)view.findViewById(0x7f10022b)).getProgress();
            if (f1 >= 1.0F)
            {
                f = f1;
            }
            return SLObjectFilterInfo.create(searchView.getQuery().toString(), ((CheckBox)view.findViewById(0x7f10022f)).isChecked(), ((CheckBox)view.findViewById(0x7f100230)).isChecked(), ((CheckBox)view.findViewById(0x7f10022e)).isChecked(), f);
        } else
        {
            return SLObjectFilterInfo.create();
        }
    }

    private UserManager getUserManager()
    {
        return ActivityUtils.getUserManager(getArguments());
    }

    public static ObjectSelectorFragment newInstance(Bundle bundle)
    {
        ObjectSelectorFragment objectselectorfragment = new ObjectSelectorFragment();
        objectselectorfragment.setArguments(bundle);
        return objectselectorfragment;
    }

    private void showObjectDetails(SLObjectDisplayInfo slobjectdisplayinfo)
    {
        java.util.UUID uuid;
label0:
        {
            uuid = ActivityUtils.getActiveAgentID(getArguments());
            if (uuid != null)
            {
                if (!(slobjectdisplayinfo instanceof SLAvatarObjectDisplayInfo))
                {
                    break label0;
                }
                slobjectdisplayinfo = ((SLAvatarObjectDisplayInfo)slobjectdisplayinfo).uuid;
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(uuid, slobjectdisplayinfo)));
            }
            return;
        }
        DetailsActivity.showDetails(getActivity(), ObjectListNewActivity.ObjectDetailsActivityFactory.getInstance(), ObjectDetailsFragment.makeSelection(uuid, slobjectdisplayinfo.localID));
    }

    private void updateFilter()
    {
        Object obj = getFilter();
        if (!((SLObjectFilterInfo) (obj)).equals(filterInfo))
        {
            filterInfo = ((SLObjectFilterInfo) (obj));
            obj = getUserManager();
            if (obj != null)
            {
                ((UserManager) (obj)).getObjectsManager().setFilter(filterInfo);
                if (filterInfo.range() != 0.0F)
                {
                    obj = ((UserManager) (obj)).getActiveAgentCircuit();
                    if (obj != null)
                    {
                        obj = ((SLAgentCircuit) (obj)).getModules();
                        if (obj != null)
                        {
                            ((SLModules) (obj)).drawDistance.setObjectSelectRange(filterInfo.range());
                        }
                    }
                }
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(Throwable throwable)
    {
        if (throwable instanceof com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException)
        {
            throwable = getView();
            if (throwable != null)
            {
                View view = throwable.findViewById(0x7f100021);
                if (view != null)
                {
                    view.setVisibility(8);
                }
                throwable.findViewById(0x7f100234).setVisibility(8);
                ((TextView)throwable.findViewById(0x7f100233)).setText(0x7f090231);
                throwable.findViewById(0x7f100232).setVisibility(0);
                throwable.findViewById(0x7f100231).setVisibility(8);
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971(com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDisplayList objectdisplaylist)
    {
        byte byte0 = 8;
        boolean flag = false;
        ImmutableList immutablelist = objectdisplaylist.objects;
        Object obj = getView();
        if (obj != null)
        {
            Object obj1 = ((View) (obj)).findViewById(0x7f100021);
            int i;
            if (obj1 != null)
            {
                Iterator iterator1;
                SLObjectDisplayInfo slobjectdisplayinfo;
                if (objectdisplaylist.isLoading)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((View) (obj1)).setVisibility(i);
            }
            obj1 = ((View) (obj)).findViewById(0x7f100234);
            if (objectdisplaylist.isLoading)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            ((View) (obj1)).setVisibility(i);
            obj1 = (TextView)((View) (obj)).findViewById(0x7f100233);
            if (objectdisplaylist.isLoading)
            {
                i = 0x7f090230;
            } else
            {
                i = 0x7f090232;
            }
            ((TextView) (obj1)).setText(i);
            objectdisplaylist = ((View) (obj)).findViewById(0x7f100232);
            if (immutablelist.isEmpty())
            {
                i = 0;
            } else
            {
                i = 8;
            }
            objectdisplaylist.setVisibility(i);
            objectdisplaylist = ((View) (obj)).findViewById(0x7f100231);
            if (immutablelist.isEmpty())
            {
                i = byte0;
            } else
            {
                i = 0;
            }
            objectdisplaylist.setVisibility(i);
            objectdisplaylist = (ExpandableListView)((View) (obj)).findViewById(0x7f100231);
            obj = objectdisplaylist.getExpandableListAdapter();
            if (obj instanceof ObjectListAdapter)
            {
                HashSet hashset = new HashSet();
                iterator1 = ((ObjectListAdapter)obj).getData().iterator();
                do
                {
                    if (!iterator1.hasNext())
                    {
                        break;
                    }
                    slobjectdisplayinfo = (SLObjectDisplayInfo)iterator1.next();
                    if ((slobjectdisplayinfo instanceof SLPrimObjectDisplayInfoWithChildren) && !((SLPrimObjectDisplayInfoWithChildren)slobjectdisplayinfo).isImplicitlyAdded())
                    {
                        hashset.add(Integer.valueOf(slobjectdisplayinfo.localID));
                    }
                } while (true);
                ArrayList arraylist = new ArrayList();
                for (int j = ((flag) ? 1 : 0); j < immutablelist.size(); j++)
                {
                    SLObjectDisplayInfo slobjectdisplayinfo1 = (SLObjectDisplayInfo)immutablelist.get(j);
                    if ((slobjectdisplayinfo1 instanceof SLPrimObjectDisplayInfoWithChildren) && ((SLPrimObjectDisplayInfoWithChildren)slobjectdisplayinfo1).isImplicitlyAdded() && !hashset.contains(Integer.valueOf(slobjectdisplayinfo1.localID)))
                    {
                        arraylist.add(Integer.valueOf(j));
                    }
                }

                ((ObjectListAdapter)obj).setData(immutablelist);
                for (Iterator iterator = arraylist.iterator(); iterator.hasNext(); objectdisplaylist.expandGroup(((Integer)iterator.next()).intValue())) { }
            }
        }
    }

    public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
    {
        updateFilter();
    }

    public boolean onChildClick(ExpandableListView expandablelistview, View view, int i, int j, long l)
    {
        expandablelistview = expandablelistview.getExpandableListAdapter();
        if (expandablelistview instanceof ObjectListAdapter)
        {
            expandablelistview = ((ObjectListAdapter)expandablelistview).getChild(i, j);
            if (expandablelistview != null)
            {
                showObjectDetails(expandablelistview);
            }
        }
        return true;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120012, menu);
        searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(0x7f100330));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            final ObjectSelectorFragment this$0;

            public boolean onQueryTextChange(String s)
            {
                Debug.Printf("searchview: textchange", new Object[0]);
                ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
                return true;
            }

            public boolean onQueryTextSubmit(String s)
            {
                return true;
            }

            
            {
                this$0 = ObjectSelectorFragment.this;
                super();
            }
        });
        MenuItemCompat.setOnActionExpandListener(menu.findItem(0x7f100330), new android.support.v4.view.MenuItemCompat.OnActionExpandListener() {

            final ObjectSelectorFragment this$0;

            public boolean onMenuItemActionCollapse(MenuItem menuitem)
            {
                menuitem = getView();
                if (menuitem != null)
                {
                    menuitem.findViewById(0x7f100229).setVisibility(8);
                    menuitem = menuitem.findViewById(0x7f100229).getAnimation();
                    if (menuitem != null)
                    {
                        menuitem.cancel();
                    }
                }
                ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
                return true;
            }

            public boolean onMenuItemActionExpand(MenuItem menuitem)
            {
                menuitem = getView();
                if (menuitem != null)
                {
                    menuitem.findViewById(0x7f100229).setVisibility(0);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), 0x7f05000f);
                    menuitem.findViewById(0x7f100229).startAnimation(animation);
                }
                ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
                return true;
            }

            
            {
                this$0 = ObjectSelectorFragment.this;
                super();
            }
        });
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f040076, viewgroup, false);
        ((ExpandableListView)viewgroup.findViewById(0x7f100231)).setAdapter(new ObjectListAdapter(layoutinflater.getContext()));
        ((ExpandableListView)viewgroup.findViewById(0x7f100231)).setOnGroupClickListener(this);
        ((ExpandableListView)viewgroup.findViewById(0x7f100231)).setOnChildClickListener(this);
        ((SeekBar)viewgroup.findViewById(0x7f10022b)).setMax(256);
        ((SeekBar)viewgroup.findViewById(0x7f10022b)).setOnSeekBarChangeListener(this);
        ((CheckBox)viewgroup.findViewById(0x7f10022f)).setOnCheckedChangeListener(this);
        ((CheckBox)viewgroup.findViewById(0x7f100230)).setOnCheckedChangeListener(this);
        ((CheckBox)viewgroup.findViewById(0x7f10022e)).setOnCheckedChangeListener(this);
        if (android.os.Build.VERSION.SDK_INT >= 14)
        {
            bundle = new ButteryProgressBar(layoutinflater.getContext());
            bundle.setId(0x7f100021);
            ((FrameLayout)viewgroup.findViewById(0x7f100228)).addView(bundle, new android.widget.FrameLayout.LayoutParams(-1, (int)TypedValue.applyDimension(1, 4F, layoutinflater.getContext().getResources().getDisplayMetrics())));
        }
        return viewgroup;
    }

    public boolean onGroupClick(ExpandableListView expandablelistview, View view, int i, long l)
    {
        Debug.Printf("displayObjects: onGroupClick: view %s id %d", new Object[] {
            view, Integer.valueOf(view.getId())
        });
        expandablelistview = expandablelistview.getExpandableListAdapter();
        if (expandablelistview instanceof ObjectListAdapter)
        {
            expandablelistview = ((ObjectListAdapter)expandablelistview).getGroup(i);
            if (expandablelistview != null)
            {
                showObjectDetails(expandablelistview);
            }
        }
        return true;
    }

    public void onProgressChanged(SeekBar seekbar, int i, boolean flag)
    {
        seekbar = getView();
        if (seekbar != null)
        {
            ((TextView)seekbar.findViewById(0x7f10022c)).setText(getString(0x7f090244, new Object[] {
                Integer.valueOf(i)
            }));
            if (flag)
            {
                updateFilter();
            }
        }
    }

    public void onStart()
    {
        Object obj;
        int i;
        i = 256;
        super.onStart();
        obj = getUserManager();
        if (obj == null) goto _L2; else goto _L1
_L1:
        ((UserManager) (obj)).getObjectsManager().setFilter(filterInfo);
        subscription = ((UserManager) (obj)).getObjectsManager().getObjectDisplayList().subscribe(SubscriptionSingleKey.Value, UIThreadExecutor.getInstance(), onObjectListData, onObjectListError);
        obj = ((UserManager) (obj)).getActiveAgentCircuit();
        if (obj == null) goto _L2; else goto _L3
_L3:
        obj = ((SLAgentCircuit) (obj)).getModules();
        if (obj == null) goto _L2; else goto _L4
_L4:
        View view;
        ((SLModules) (obj)).drawDistance.EnableObjectSelect();
        view = getView();
        if (view == null) goto _L2; else goto _L5
_L5:
        int j = (int)((SLModules) (obj)).drawDistance.getObjectSelectRange();
        if (j >= 1) goto _L7; else goto _L6
_L6:
        i = 1;
_L9:
        ((SeekBar)view.findViewById(0x7f10022b)).setProgress(i);
_L2:
        return;
_L7:
        if (j <= 256)
        {
            i = j;
        }
        if (true) goto _L9; else goto _L8
_L8:
    }

    public void onStartTrackingTouch(SeekBar seekbar)
    {
    }

    public void onStop()
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
            subscription = null;
        }
        Object obj = getUserManager();
        if (obj != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules();
                if (obj != null)
                {
                    ((SLModules) (obj)).drawDistance.DisableObjectSelect();
                }
            }
        }
        super.onStop();
    }

    public void onStopTrackingTouch(SeekBar seekbar)
    {
    }
}
