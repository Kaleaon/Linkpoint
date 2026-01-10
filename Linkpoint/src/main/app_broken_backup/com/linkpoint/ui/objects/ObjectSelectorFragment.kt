package com.linkpoint.ui.objects

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.SearchView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.objects.SLAvatarObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectFilterInfo
import com.linkpoint.slproto.objects.SLPrimObjectDisplayInfoWithChildren
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.ObjectsManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.ButteryProgressBar
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.objects.ObjectListNewActivity
import java.util.ArrayList
import java.util.HashSet
import java.util.UUID
import androidx.annotation.Nullable

class ObjectSelectorFragment : Fragment : SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    private val MAX_FILTER_DISTANCE: Int = 256
    private val PROGRESS_BAR_SIZE_DIP: Int = 4
    private SLObjectFilterInfo filterInfo = SLObjectFilterInfo.create()
    private Subscription.OnData<ObjectsManager.ObjectDisplayList> onObjectListData = $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(this)
    private Subscription.OnError onObjectListError = Subscription.OnError(this) {

        /* renamed from: -$f0  reason: not valid java name */
        private /* synthetic */ Any f500$f0

        private /* synthetic */ Unit $m$0(Throwable th) {
            ((this as ObjectSelectorFragment).f500$f0).m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(th)
        }

        {
            /*
                r0 = this
                r0.<init>()
                r0.f500$f0 = r1
                return
            */
            throw UnsupportedOperationException("Method not decompiled: com.linkpoint.ui.objects.$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y.AnonymousClass1.<init>(java.lang.Any):Unit")
        }

    }
    private SearchView searchView
    private Subscription<SubscriptionSingleKey, ObjectsManager.ObjectDisplayList> subscription

    private SLObjectFilterInfo getFilter() {
        var f: Float = 1.0f
        View view = getView()
        if (view == null) {
            return SLObjectFilterInfo.create()
        }
        if (view.findViewById(R.id.filterPanel).getVisibility() != 0) {
            return SLObjectFilterInfo.create()
        }
        var progress: Float = (Float) ((view as SeekBar).findViewById(R.id.objectListSeekBar)).getProgress()
        if (progress >= 1.0f) {
            f = progress
        }
        return SLObjectFilterInfo.create(this.searchView.getQuery().toString(), ((view as CheckBox).findViewById(R.id.includeAttachments)).isChecked(), ((view as CheckBox).findViewById(R.id.includeStubs)).isChecked(), ((view as CheckBox).findViewById(R.id.includeNonTouchable)).isChecked(), f)
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments())
    }

    fun newInstance(Bundle bundle): ObjectSelectorFragment {
        ObjectSelectorFragment objectSelectorFragment = ObjectSelectorFragment()
        objectSelectorFragment.setArguments(bundle)
        return objectSelectorFragment
    }

    private Unit showObjectDetails(SLObjectDisplayInfo sLObjectDisplayInfo) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments())
        if (activeAgentID == null) {
            return
        }
        if (sLObjectDisplayInfo is SLAvatarObjectDisplayInfo) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, ((SLAvatarObjectDisplayInfo) sLObjectDisplayInfo).uuid)))
            return
        }
        DetailsActivity.showDetails(getActivity(), ObjectListNewActivity.ObjectDetailsActivityFactory.getInstance(), ObjectDetailsFragment.makeSelection(activeAgentID, sLObjectDisplayInfo.localID))
    }

    /* access modifiers changed from: private */
    fun updateFilter()  {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        SLObjectFilterInfo filter = getFilter()
        if (!filter.equals(this.filterInfo)) {
            this.filterInfo = filter
            UserManager userManager = getUserManager()
            if (userManager != null) {
                userManager.getObjectsManager().setFilter(this.filterInfo)
                if (this.filterInfo.range() != 0.0f && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (modules = activeAgentCircuit.getModules()) != null) {
                    modules.drawDistance.setObjectSelectRange(this.filterInfo.range())
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042  reason: not valid java name */
    /* synthetic */ Unit m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(Throwable th) {
        View view
        if ((th is SLGridConnection.NotConnectedException) && (view = getView()) != null) {
            View findViewById = view.findViewById(R.id.object_progress_bar)
            if (findViewById != null) {
                findViewById.setVisibility(8)
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(8)
            ((view as TextView).findViewById(R.id.empty_object_list_message)).setText(R.string.object_list_not_connected)
            view.findViewById(R.id.empty_object_list).setVisibility(0)
            view.findViewById(R.id.objectListView).setVisibility(8)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971  reason: not valid java name */
    /* synthetic */ Unit m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971(ObjectsManager.ObjectDisplayList objectDisplayList) {
        var i: Int = 8
        ImmutableList<SLObjectDisplayInfo> immutableList = objectDisplayList.objects
        View view = getView()
        if (view != null) {
            View findViewById = view.findViewById(R.id.object_progress_bar)
            if (findViewById != null) {
                findViewById.setVisibility(objectDisplayList.isLoading ? 0 : 8)
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(objectDisplayList.isLoading ? 0 : 8)
            ((view as TextView).findViewById(R.id.empty_object_list_message)).setText(objectDisplayList.isLoading ? R.string.object_list_loading : R.string.object_list_result_empty)
            view.findViewById(R.id.empty_object_list).setVisibility(immutableList.isEmpty() ? 0 : 8)
            View findViewById2 = view.findViewById(R.id.objectListView)
            if (!immutableList.isEmpty()) {
                i = 0
            }
            findViewById2.setVisibility(i)
            ExpandableListView expandableListView = (view as ExpandableListView).findViewById(R.id.objectListView)
            ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter()
            if (expandableListAdapter is ObjectListAdapter) {
                HashSet hashSet = HashSet()
                for (SLObjectDisplayInfo sLObjectDisplayInfo : ((ObjectListAdapter) expandableListAdapter).getData()) {
                    if ((sLObjectDisplayInfo is SLPrimObjectDisplayInfoWithChildren) && !((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo).isImplicitlyAdded()) {
                        hashSet.add(Int.valueOf(sLObjectDisplayInfo.localID))
                    }
                }
                ArrayList<Int> arrayList = ArrayList<>()
                for (i2 in 0 until immutableList.size()) {
                    SLObjectDisplayInfo sLObjectDisplayInfo2 = (immutableList as SLObjectDisplayInfo).get(i2)
                    if ((sLObjectDisplayInfo2 is SLPrimObjectDisplayInfoWithChildren) && ((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo2).isImplicitlyAdded() && !hashSet.contains(Int.valueOf(sLObjectDisplayInfo2.localID))) {
                        arrayList.add(Int.valueOf(i2))
                    }
                }
                ((ObjectListAdapter) expandableListAdapter).setData(immutableList)
                for (Int intValue : arrayList) {
                    expandableListView.expandGroup(intValue.intValue())
                }
            }
        }
    }

    fun onCheckedChanged(CompoundButton compoundButton, Boolean z)  {
        updateFilter()
    }

    fun onChildClick(ExpandableListView expandableListView, View view, Int i, Int i2, Long j): Boolean {
        SLObjectDisplayInfo child
        ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter()
        if (!(expandableListAdapter is ObjectListAdapter) || (child = ((ObjectListAdapter) expandableListAdapter).getChild(i, i2)) == null) {
            return true
        }
        showObjectDetails(child)
        return true
    }

    fun onCreate(@android.support.annotation.Nullable Bundle bundle)  {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    fun onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)  {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_object_selector, menu)
        this.searchView = (MenuItemCompat as SearchView).getActionView(menu.findItem(R.id.action_search))
        this.searchView.setOnQueryTextListener(SearchView.OnQueryTextListener() {
            fun onQueryTextChange(String str): Boolean {
                Debug.Printf("searchview: textchange", Any[0])
                ObjectSelectorFragment.this.updateFilter()
                return true
            }

            fun onQueryTextSubmit(String str): Boolean {
                return true
            }
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), MenuItemCompat.OnActionExpandListener() {
            fun onMenuItemActionCollapse(MenuItem menuItem): Boolean {
                View view = ObjectSelectorFragment.this.getView()
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(8)
                    Animation animation = view.findViewById(R.id.filterPanel).getAnimation()
                    if (animation != null) {
                        animation.cancel()
                    }
                }
                ObjectSelectorFragment.this.updateFilter()
                return true
            }

            fun onMenuItemActionExpand(MenuItem menuItem): Boolean {
                View view = ObjectSelectorFragment.this.getView()
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(0)
                    view.findViewById(R.id.filterPanel).startAnimation(AnimationUtils.loadAnimation(ObjectSelectorFragment.this.getContext(), R.anim.slide_from_above))
                }
                ObjectSelectorFragment.this.updateFilter()
                return true
            }
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.object_list, viewGroup, false)
        ((inflate as ExpandableListView).findViewById(R.id.objectListView)).setAdapter(ObjectListAdapter(layoutInflater.getContext()))
        ((inflate as ExpandableListView).findViewById(R.id.objectListView)).setOnGroupClickListener(this)
        ((inflate as ExpandableListView).findViewById(R.id.objectListView)).setOnChildClickListener(this)
        ((inflate as SeekBar).findViewById(R.id.objectListSeekBar)).setMax(256)
        ((inflate as SeekBar).findViewById(R.id.objectListSeekBar)).setOnSeekBarChangeListener(this)
        ((inflate as CheckBox).findViewById(R.id.includeAttachments)).setOnCheckedChangeListener(this)
        ((inflate as CheckBox).findViewById(R.id.includeStubs)).setOnCheckedChangeListener(this)
        ((inflate as CheckBox).findViewById(R.id.includeNonTouchable)).setOnCheckedChangeListener(this)
        if (Build.VERSION.SDK_INT >= 14) {
            ButteryProgressBar butteryProgressBar = ButteryProgressBar(layoutInflater.getContext())
            butteryProgressBar.setId(R.id.object_progress_bar)
            ((inflate as FrameLayout).findViewById(R.id.object_list_root_layout)).addView(butteryProgressBar, FrameLayout.LayoutParams(-1, TypedValue.toInt().applyDimension(1, 4.0f, layoutInflater.getContext().getResources().getDisplayMetrics())))
        }
        return inflate
    }

    fun onGroupClick(ExpandableListView expandableListView, View view, Int i, Long j): Boolean {
        SLObjectDisplayInfo group
        Debug.Printf("displayObjects: onGroupClick: view %s id %d", view, Int.valueOf(view.getId()))
        ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter()
        if ((expandableListAdapter is ObjectListAdapter) && (group = ((ObjectListAdapter) expandableListAdapter).getGroup(i)) != null) {
            showObjectDetails(group)
        }
        return true
    }

    fun onProgressChanged(SeekBar seekBar, Int i, Boolean z)  {
        View view = getView()
        if (view != null) {
            ((view as TextView).findViewById(R.id.objectListRangeDisplay)).setText(getString(R.string.object_range_format, Int.valueOf(i)))
            if (z) {
                updateFilter()
            }
        }
    }

    fun onStart()  {
        SLModules modules
        var i: Int = 256
        super.onStart()
        UserManager userManager = getUserManager()
        if (userManager != null) {
            userManager.getObjectsManager().setFilter(this.filterInfo)
            this.subscription = userManager.getObjectsManager().getObjectDisplayList().subscribe(SubscriptionSingleKey.Value, UIThreadExecutor.getInstance(), this.onObjectListData, this.onObjectListError)
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.drawDistance.EnableObjectSelect()
                View view = getView()
                if (view != null) {
                    var objectSelectRange: Int = modules.toInt().drawDistance.getObjectSelectRange()
                    if (objectSelectRange < 1) {
                        i = 1
                    } else if (objectSelectRange <= 256) {
                        i = objectSelectRange
                    }
                    ((view as SeekBar).findViewById(R.id.objectListSeekBar)).setProgress(i)
                }
            }
        }
    }

    fun onStartTrackingTouch(SeekBar seekBar)  {
    }

    fun onStop()  {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        if (this.subscription != null) {
            this.subscription.unsubscribe()
            this.subscription = null
        }
        UserManager userManager = getUserManager()
        if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null)) {
            modules.drawDistance.DisableObjectSelect()
        }
        super.onStop()
    }

    fun onStopTrackingTouch(SeekBar seekBar)  {
    }
}
