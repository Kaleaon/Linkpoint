package com.linkpoint.ui.objects

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
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
import javax.annotation.Nullable

class ObjectSelectorFragment : Fragment(), SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    private const val MAX_FILTER_DISTANCE: Int = 256
    private const val PROGRESS_BAR_SIZE_DIP: Int = 4
    private SLObjectFilterInfo filterInfo = SLObjectFilterInfo.create()
    private val Subscription.OnData<ObjectsManager.ObjectDisplayList> onObjectListData = $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(this)
    private val Subscription.OnError onObjectListError = Subscription.OnError(this) {

        /* renamed from: -$f0  reason: not valid java name */
        private val /* synthetic */ Object f500$f0

        private val /* synthetic */ Unit $m$0(Throwable th) {
            ((ObjectSelectorFragment) this.f500$f0).m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(th)
        }

        {
            /*
                r0 = this
                r0.<init>()
                r0.f500$f0 = r1
                return
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.objects.$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y.AnonymousClass1.<init>(java.lang.Object):Unit")
        }

    }
    private SearchView searchView
    private Subscription<SubscriptionSingleKey, ObjectsManager.ObjectDisplayList> subscription

     private fun getFilter(): SLObjectFilterInfo {
        val f: Float = 1.0f
        val view: View = getView()
        if (view == null) {
            return SLObjectFilterInfo.create()
        }
        if (view.findViewById(R.id.filterPanel).getVisibility() != 0) {
            return SLObjectFilterInfo.create()
        }
        val progress: Float = (Float) ((SeekBar) view.findViewById(R.id.objectListSeekBar)).getProgress()
        if (progress >= 1.0f) {
            f = progress
        }
        return SLObjectFilterInfo.create(this.searchView.getQuery().toString(), ((CheckBox) view.findViewById(R.id.includeAttachments)).isChecked(), ((CheckBox) view.findViewById(R.id.includeStubs)).isChecked(), ((CheckBox) view.findViewById(R.id.includeNonTouchable)).isChecked(), f)
    }

     private fun getUserManager(): UserManager {
        return ActivityUtils.getUserManager(getArguments())
    }

    @JvmStatic
     fun newInstance(bundle: Bundle): ObjectSelectorFragment {
        val objectSelectorFragment: ObjectSelectorFragment = ObjectSelectorFragment()
        objectSelectorFragment.setArguments(bundle)
        return objectSelectorFragment
    }

     private fun showObjectDetails(sLObjectDisplayInfo: SLObjectDisplayInfo) {
        val activeAgentID: UUID = ActivityUtils.getActiveAgentID(getArguments())
        if (activeAgentID == null) {
            return
        }
        if (sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, ((SLAvatarObjectDisplayInfo) sLObjectDisplayInfo).uuid)))
            return
        }
        DetailsActivity.showDetails(getActivity(), ObjectListNewActivity.ObjectDetailsActivityFactory.getInstance(), ObjectDetailsFragment.makeSelection(activeAgentID, sLObjectDisplayInfo.localID))
    }

    /* access modifiers changed from: private */
    fun updateFilter() {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        val filter: SLObjectFilterInfo = getFilter()
        if (!filter.equals(this.filterInfo)) {
            this.filterInfo = filter
            val userManager: UserManager = getUserManager()
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
    public /* synthetic */ Unit m682lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10042(Throwable th) {
        View view
        if ((th instanceof SLGridConnection.NotConnectedException) && (view = getView()) != null) {
            val findViewById: View = view.findViewById(R.id.object_progress_bar)
            if (findViewById != null) {
                findViewById.setVisibility(8)
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(8)
            ((TextView) view.findViewById(R.id.empty_object_list_message)).setText(R.string.object_list_not_connected)
            view.findViewById(R.id.empty_object_list).setVisibility(0)
            view.findViewById(R.id.objectListView).setVisibility(8)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971  reason: not valid java name */
    public /* synthetic */ Unit m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971(ObjectsManager.ObjectDisplayList objectDisplayList) {
        val i: Int = 8
        val immutableList: ImmutableList<SLObjectDisplayInfo> = objectDisplayList.objects
        val view: View = getView()
        if (view != null) {
            val findViewById: View = view.findViewById(R.id.object_progress_bar)
            if (findViewById != null) {
                findViewById.setVisibility(objectDisplayList.isLoading ? 0 : 8)
            }
            view.findViewById(R.id.empty_object_list_progress).setVisibility(objectDisplayList.isLoading ? 0 : 8)
            ((TextView) view.findViewById(R.id.empty_object_list_message)).setText(objectDisplayList.isLoading ? R.string.object_list_loading : R.string.object_list_result_empty)
            view.findViewById(R.id.empty_object_list).setVisibility(immutableList.isEmpty() ? 0 : 8)
            val findViewById2: View = view.findViewById(R.id.objectListView)
            if (!immutableList.isEmpty()) {
                i = 0
            }
            findViewById2.setVisibility(i)
            val expandableListView: ExpandableListView = (ExpandableListView) view.findViewById(R.id.objectListView)
            val expandableListAdapter: ExpandableListAdapter = expandableListView.getExpandableListAdapter()
            if (expandableListAdapter instanceof ObjectListAdapter) {
                val hashSet: HashSet = HashSet()
                for (SLObjectDisplayInfo sLObjectDisplayInfo : ((ObjectListAdapter) expandableListAdapter).getData()) {
                    if ((sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfoWithChildren) && !((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo).isImplicitlyAdded()) {
                        hashSet.add(Integer.valueOf(sLObjectDisplayInfo.localID))
                    }
                }
                val arrayList: ArrayList<Integer> = ArrayList<>()
                for (Int i2 = 0; i2 < immutableList.size(); i2++) {
                    val sLObjectDisplayInfo2: SLObjectDisplayInfo = (SLObjectDisplayInfo) immutableList.get(i2)
                    if ((sLObjectDisplayInfo2 instanceof SLPrimObjectDisplayInfoWithChildren) && ((SLPrimObjectDisplayInfoWithChildren) sLObjectDisplayInfo2).isImplicitlyAdded() && !hashSet.contains(Integer.valueOf(sLObjectDisplayInfo2.localID))) {
                        arrayList.add(Integer.valueOf(i2))
                    }
                }
                ((ObjectListAdapter) expandableListAdapter).setData(immutableList)
                for (Integer intValue : arrayList) {
                    expandableListView.expandGroup(intValue.intValue())
                }
            }
        }
    }

    fun onCheckedChanged(compoundButton: CompoundButton, z: Boolean) {
        updateFilter()
    }

     public fun onChildClick(expandableListView: ExpandableListView, view: View, i: Int, i2: Int, j: Long): Boolean {
        SLObjectDisplayInfo child
        val expandableListAdapter: ExpandableListAdapter = expandableListView.getExpandableListAdapter()
        if (!(expandableListAdapter instanceof ObjectListAdapter) || (child = ((ObjectListAdapter) expandableListAdapter).getChild(i, i2)) == null) {
            return true
        }
        showObjectDetails(child)
        return true
    }

    override fun onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_object_selector, menu)
        this.searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))
        this.searchView.setOnQueryTextListener(SearchView.OnQueryTextListener() {
             public fun onQueryTextChange(str: String): Boolean {
                Debug.Printf("searchview: textchange", Object[0])
                ObjectSelectorFragment.this.updateFilter()
                return true
            }

             public fun onQueryTextSubmit(str: String): Boolean {
                return true
            }
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), MenuItemCompat.OnActionExpandListener() {
             public fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                val view: View = ObjectSelectorFragment.this.getView()
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(8)
                    val animation: Animation = view.findViewById(R.id.filterPanel).getAnimation()
                    if (animation != null) {
                        animation.cancel()
                    }
                }
                ObjectSelectorFragment.this.updateFilter()
                return true
            }

             public fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                val view: View = ObjectSelectorFragment.this.getView()
                if (view != null) {
                    view.findViewById(R.id.filterPanel).setVisibility(0)
                    view.findViewById(R.id.filterPanel).startAnimation(AnimationUtils.loadAnimation(ObjectSelectorFragment.this.getContext(), R.anim.slide_from_above))
                }
                ObjectSelectorFragment.this.updateFilter()
                return true
            }
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.object_list, viewGroup, false)
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setAdapter(ObjectListAdapter(layoutInflater.getContext()))
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setOnGroupClickListener(this)
        ((ExpandableListView) inflate.findViewById(R.id.objectListView)).setOnChildClickListener(this)
        ((SeekBar) inflate.findViewById(R.id.objectListSeekBar)).setMax(256)
        ((SeekBar) inflate.findViewById(R.id.objectListSeekBar)).setOnSeekBarChangeListener(this)
        ((CheckBox) inflate.findViewById(R.id.includeAttachments)).setOnCheckedChangeListener(this)
        ((CheckBox) inflate.findViewById(R.id.includeStubs)).setOnCheckedChangeListener(this)
        ((CheckBox) inflate.findViewById(R.id.includeNonTouchable)).setOnCheckedChangeListener(this)
        if (Build.VERSION.SDK_INT >= 14) {
            val butteryProgressBar: ButteryProgressBar = ButteryProgressBar(layoutInflater.getContext())
            butteryProgressBar.setId(R.id.object_progress_bar)
            ((FrameLayout) inflate.findViewById(R.id.object_list_root_layout)).addView(butteryProgressBar, FrameLayout.LayoutParams(-1, (Int) TypedValue.applyDimension(1, 4.0f, layoutInflater.getContext().getResources().getDisplayMetrics())))
        }
        return inflate
    }

     public fun onGroupClick(expandableListView: ExpandableListView, view: View, i: Int, j: Long): Boolean {
        SLObjectDisplayInfo group
        Debug.Printf("displayObjects: onGroupClick: view %s id %d", view, Integer.valueOf(view.getId()))
        val expandableListAdapter: ExpandableListAdapter = expandableListView.getExpandableListAdapter()
        if ((expandableListAdapter instanceof ObjectListAdapter) && (group = ((ObjectListAdapter) expandableListAdapter).getGroup(i)) != null) {
            showObjectDetails(group)
        }
        return true
    }

    fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
        val view: View = getView()
        if (view != null) {
            ((TextView) view.findViewById(R.id.objectListRangeDisplay)).setText(getString(R.string.object_range_format, Integer.valueOf(i)))
            if (z) {
                updateFilter()
            }
        }
    }

    override fun onStart() {
        SLModules modules
        val i: Int = 256
        super.onStart()
        val userManager: UserManager = getUserManager()
        if (userManager != null) {
            userManager.getObjectsManager().setFilter(this.filterInfo)
            this.subscription = userManager.getObjectsManager().getObjectDisplayList().subscribe(SubscriptionSingleKey.Value, UIThreadExecutor.getInstance(), this.onObjectListData, this.onObjectListError)
            val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.drawDistance.EnableObjectSelect()
                val view: View = getView()
                if (view != null) {
                    val objectSelectRange: Int = (Int) modules.drawDistance.getObjectSelectRange()
                    if (objectSelectRange < 1) {
                        i = 1
                    } else if (objectSelectRange <= 256) {
                        i = objectSelectRange
                    }
                    ((SeekBar) view.findViewById(R.id.objectListSeekBar)).setProgress(i)
                }
            }
        }
    }

    fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStop() {
        SLAgentCircuit activeAgentCircuit
        SLModules modules
        if (this.subscription != null) {
            this.subscription.unsubscribe()
            this.subscription = null
        }
        val userManager: UserManager = getUserManager()
        if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null)) {
            modules.drawDistance.DisableObjectSelect()
        }
        super.onStop()
    }

    fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}
