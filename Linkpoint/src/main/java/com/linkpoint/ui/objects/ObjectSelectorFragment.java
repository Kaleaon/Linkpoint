package com.lumiyaviewer.lumiya.ui.objects;

import android.os.Build;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
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
import com.lumiyaviewer.lumiya.ui.objects.ObjectListNewActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nullable;

public class ObjectSelectorFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    private static final int MAX_FILTER_DISTANCE = 256;
    private static final int PROGRESS_BAR_SIZE_DIP = 4;
    private SLObjectFilterInfo filterInfo = SLObjectFilterInfo.create();
    private final Subscription.OnData<ObjectsManager.ObjectDisplayList> onObjectListData = new $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(this);
    private final Subscription.OnError onObjectListError = new Subscription.OnError(this) {

        /* renamed from: -$f0  reason: not valid java name */
        private final /* synthetic */ Object f500$f0;

        private final /* synthetic */ void $m$0(Throwable th) {
            ((ObjectSelectorFragment) this.f500$f0).m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(th);
        }

        {
            /*
                r0 = this;
                r0.<init>()
                r0.f500$f0 = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.objects.$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y.AnonymousClass1.<init>(java.lang.Object):void");
        }

    };
    private SearchView searchView;
    private Subscription<SubscriptionSingleKey, ObjectsManager.ObjectDisplayList> subscription;

    private SLObjectFilterInfo getFilter() {
        float f = 1.0f;
        View view = getView();
        if (view == null) {
            return SLObjectFilterInfo.create();
        }
        if (view.findViewById(R.id.filterPanel).getVisibility() != 0) {
            return SLObjectFilterInfo.create();
        }
        float progress = (float) ((SeekBar) view.findViewById(R.id.objectListSeekBar)).getProgress();
        if (progress >= 1.0f) {
            f = progress;
        }
        return SLObjectFilterInfo.create(this.searchView.getQuery().toString(), ((CheckBox) view.findViewById(R.id.includeAttachments)).isChecked(), ((CheckBox) view.findViewById(R.id.includeStubs)).isChecked(), ((CheckBox) view.findViewById(R.id.includeNonTouchable)).isChecked(), f);
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    public static ObjectSelectorFragment newInstance(Bundle bundle) {
        ObjectSelectorFragment objectSelectorFragment = new ObjectSelectorFragment();
        objectSelectorFragment.setArguments(bundle);
        return objectSelectorFragment;
    }

    private void showObjectDetails(SLObjectDisplayInfo sLObjectDisplayInfo) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        if (activeAgentID == null) {
            return;
        }
        if (sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, ((SLAvatarObjectDisplayInfo) sLObjectDisplayInfo).uuid)));
            return;
        }
        DetailsActivity.showDetails(getActivity(), ObjectListNewActivity.ObjectDetailsActivityFactory.getInstance(), ObjectDetailsFragment.makeSelection(activeAgentID, sLObjectDisplayInfo.localID));
    }

    /* access modifiers changed from: private */
    public void updateFilter() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        SLObjectFilterInfo filter = getFilter();
        if (!filter.equals(this.filterInfo)) {
            this.filterInfo = filter;
            UserManager userManager = getUserManager();
            if (userManager != null) {
                userManager.getObjectsManager().setFilter(this.filterInfo);
                if (this.filterInfo.range() != 0.0f && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
                    modules.drawDistance.setObjectSelectRange(this.filterInfo.range());
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042  reason: not valid java name */
    public /* synthetic */ void m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(Throwable th) {
        View view;
        if ((th instanceof SLGridConnection.NotConnectedException) && (view = getView()) != null) {
            View findViewById = view.findViewById(R.id.object_progress_bar);
            if (findViewById != null) {
                findViewById.setVisibility(8);
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(8);
            ((TextView) view.findViewById(R.id.empty_object_list_message)).setText(R.string.object_list_not_connected);
            view.findViewById(R.id.empty_object_list).setVisibility(0);
            view.findViewById(R.id.objectListView).setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971  reason: not valid java name */
    public /* synthetic */ void m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971(ObjectsManager.ObjectDisplayList objectDisplayList) {
        int i = 8;
        ImmutableList<SLObjectDisplayInfo> immutableList = objectDisplayList.objects;
        View view = getView();
        if (view != null) {
            View findViewById = view.findViewById(R.id.object_progress_bar);
            if (findViewById != null) {
                findViewById.setVisibility(objectDisplayList.isLoading ? 0 : 8);
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(objectDisplayList.isLoading ? 0 : 8);
            ((TextView) view.findViewById(R.id.empty_object_list_message)).setText(objectDisplayList.isLoading ? R.string.object_list_loading : R.string.object_list_result_empty);
            view.findViewById(R.id.empty_object_list).setVisibility(immutableList.isEmpty() ? 0 : 8);
            View findViewById2 = view.findViewById(R.id.objectListView);
            if (!immutableList.isEmpty()) {
                i = 0;
            }
            findViewById2.setVisibility(i);
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.objectListView);
            ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
            if (expandableListAdapter instanceof ObjectListAdapter) {
                HashSet hashSet = new HashSet();
                for (SLObjectDisplayInfo sLObjectDisplayInfo : ((ObjectListAdapter) expandableListAdapter).getData()) {
                    if ((sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfoWithChildren) && !((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo).isImplicitlyAdded()) {
                        hashSet.add(Integer.valueOf(sLObjectDisplayInfo.localID));
                    }
                }
                ArrayList<Integer> arrayList = new ArrayList<>();
                for (int i2 = 0; i2 < immutableList.size(); i2++) {
                    SLObjectDisplayInfo sLObjectDisplayInfo2 = (SLObjectDisplayInfo) immutableList.get(i2);
                    if ((sLObjectDisplayInfo2 instanceof SLPrimObjectDisplayInfoWithChildren) && ((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo2).isImplicitlyAdded() && !hashSet.contains(Integer.valueOf(sLObjectDisplayInfo2.localID))) {
                        arrayList.add(Integer.valueOf(i2));
                    }
                }
                ((ObjectListAdapter) expandableListAdapter).setData(immutableList);
                for (Integer intValue : arrayList) {
                    expandableListView.expandGroup(intValue.intValue());
                }
            }
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        updateFilter();
    }

    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
        SLObjectDisplayInfo child;
        ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
        if (!(expandableListAdapter instanceof ObjectListAdapter) || (child = ((ObjectListAdapter) expandableListAdapter).getChild(i, i2)) == null) {
            return true;
        }
        showObjectDetails(child);
        return true;
    }

    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_object_selector, menu);
        this.searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String str) {
                Debug.Printf("searchview: textchange", new Object[0]);
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }

            public boolean onQueryTextSubmit(String str) {
                return true;
            }
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                View view = ObjectSelectorFragment.this.getView();
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(8);
                    Animation animation = view.findViewById(R.id.filterPanel).getAnimation();
                    if (animation != null) {
                        animation.cancel();
                    }
                }
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }

            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                View view = ObjectSelectorFragment.this.getView();
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(0);
                    view.findViewById(R.id.filterPanel).startAnimation(AnimationUtils.loadAnimation(ObjectSelectorFragment.this.getContext(), R.anim.slide_from_above));
                }
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.object_list, viewGroup, false);
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setAdapter(new ObjectListAdapter(layoutInflater.getContext()));
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setOnGroupClickListener(this);
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setOnChildClickListener(this);
        ((SeekBar) inflate.findViewById(R.id.objectListSeekBar)).setMax(256);
        ((SeekBar) inflate.findViewById(R.id.objectListSeekBar)).setOnSeekBarChangeListener(this);
        ((CheckBox) inflate.findViewById(R.id.includeAttachments)).setOnCheckedChangeListener(this);
        ((CheckBox) inflate.findViewById(R.id.includeStubs)).setOnCheckedChangeListener(this);
        ((CheckBox) inflate.findViewById(R.id.includeNonTouchable)).setOnCheckedChangeListener(this);
        if (Build.VERSION.SDK_INT >= 14) {
            ButteryProgressBar butteryProgressBar = new ButteryProgressBar(layoutInflater.getContext());
            butteryProgressBar.setId(R.id.object_progress_bar);
            ((FrameLayout) inflate.findViewById(R.id.object_list_root_layout)).addView(butteryProgressBar, new FrameLayout.LayoutParams(-1, (int) TypedValue.applyDimension(1, 4.0f, layoutInflater.getContext().getResources().getDisplayMetrics())));
        }
        return inflate;
    }

    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long j) {
        SLObjectDisplayInfo group;
        Debug.Printf("displayObjects: onGroupClick: view %s id %d", view, Integer.valueOf(view.getId()));
        ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
        if ((expandableListAdapter instanceof ObjectListAdapter) && (group = ((ObjectListAdapter) expandableListAdapter).getGroup(i)) != null) {
            showObjectDetails(group);
        }
        return true;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.objectListRangeDisplay)).setText(getString(R.string.object_range_format, Integer.valueOf(i)));
            if (z) {
                updateFilter();
            }
        }
    }

    public void onStart() {
        SLModules modules;
        int i = 256;
        super.onStart();
        UserManager userManager = getUserManager();
        if (userManager != null) {
            userManager.getObjectsManager().setFilter(this.filterInfo);
            this.subscription = userManager.getObjectsManager().getObjectDisplayList().subscribe(SubscriptionSingleKey.Value, UIThreadExecutor.getInstance(), this.onObjectListData, this.onObjectListError);
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.drawDistance.EnableObjectSelect();
                View view = getView();
                if (view != null) {
                    int objectSelectRange = (int) modules.drawDistance.getObjectSelectRange();
                    if (objectSelectRange < 1) {
                        i = 1;
                    } else if (objectSelectRange <= 256) {
                        i = objectSelectRange;
                    }
                    ((SeekBar) view.findViewById(R.id.objectListSeekBar)).setProgress(i);
                }
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStop() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        UserManager userManager = getUserManager();
        if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null)) {
            modules.drawDistance.DisableObjectSelect();
        }
        super.onStop();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
