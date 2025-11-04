// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.TypedValue;
import android.widget.FrameLayout;
import com.lumiyaviewer.lumiya.ui.common.ButteryProgressBar;
import android.os.Build$VERSION;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.CompoundButton;
import java.util.Iterator;
import android.widget.ExpandableListAdapter;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfoWithChildren;
import java.util.HashSet;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import android.os.Bundle;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import android.support.v7.widget.SearchView;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import android.widget.ExpandableListView$OnChildClickListener;
import android.widget.ExpandableListView$OnGroupClickListener;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.support.v4.app.Fragment;

public class ObjectSelectorFragment extends Fragment implements SeekBar$OnSeekBarChangeListener, CompoundButton$OnCheckedChangeListener, ExpandableListView$OnGroupClickListener, ExpandableListView$OnChildClickListener
{
    private static final int MAX_FILTER_DISTANCE = 256;
    private static final int PROGRESS_BAR_SIZE_DIP = 4;
    private SLObjectFilterInfo filterInfo;
    private final Subscription.OnData<ObjectsManager.ObjectDisplayList> onObjectListData;
    private final Subscription.OnError onObjectListError;
    private SearchView searchView;
    private Subscription<SubscriptionSingleKey, ObjectsManager.ObjectDisplayList> subscription;
    
    public ObjectSelectorFragment() {
        this.filterInfo = SLObjectFilterInfo.create();
        this.onObjectListError = new _$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y$1(this);
        this.onObjectListData = new _$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(this);
    }
    
    private SLObjectFilterInfo getFilter() {
        final float n = 1.0f;
        final View view = this.getView();
        if (view == null) {
            return SLObjectFilterInfo.create();
        }
        if (view.findViewById(2131755561).getVisibility() == 0) {
            float n2 = (float)((SeekBar)view.findViewById(2131755563)).getProgress();
            if (n2 < 1.0f) {
                n2 = n;
            }
            return SLObjectFilterInfo.create(this.searchView.getQuery().toString(), ((CheckBox)view.findViewById(2131755567)).isChecked(), ((CheckBox)view.findViewById(2131755568)).isChecked(), ((CheckBox)view.findViewById(2131755566)).isChecked(), n2);
        }
        return SLObjectFilterInfo.create();
    }
    
    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(this.getArguments());
    }
    
    public static ObjectSelectorFragment newInstance(final Bundle arguments) {
        final ObjectSelectorFragment objectSelectorFragment = new ObjectSelectorFragment();
        objectSelectorFragment.setArguments(arguments);
        return objectSelectorFragment;
    }
    
    private void showObjectDetails(final SLObjectDisplayInfo slObjectDisplayInfo) {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getArguments());
        if (activeAgentID != null) {
            if (slObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, ((SLAvatarObjectDisplayInfo)slObjectDisplayInfo).uuid)));
            }
            else {
                DetailsActivity.showDetails(this.getActivity(), ObjectListNewActivity.ObjectDetailsActivityFactory.getInstance(), ObjectDetailsFragment.makeSelection(activeAgentID, slObjectDisplayInfo.localID));
            }
        }
    }
    
    private void updateFilter() {
        final SLObjectFilterInfo filter = this.getFilter();
        if (!filter.equals(this.filterInfo)) {
            this.filterInfo = filter;
            final UserManager userManager = this.getUserManager();
            if (userManager != null) {
                userManager.getObjectsManager().setFilter(this.filterInfo);
                if (this.filterInfo.range() != 0.0f) {
                    final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    if (activeAgentCircuit != null) {
                        final SLModules modules = activeAgentCircuit.getModules();
                        if (modules != null) {
                            modules.drawDistance.setObjectSelectRange(this.filterInfo.range());
                        }
                    }
                }
            }
        }
    }
    
    public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
        this.updateFilter();
    }
    
    public boolean onChildClick(final ExpandableListView expandableListView, final View view, final int n, final int n2, final long n3) {
        final ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
        if (expandableListAdapter instanceof ObjectListAdapter) {
            final SLObjectDisplayInfo child = ((ObjectListAdapter)expandableListAdapter).getChild(n, n2);
            if (child != null) {
                this.showObjectDetails(child);
            }
        }
        return true;
    }
    
    @Override
    public void onCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886098, menu);
        (this.searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(2131755824))).setOnQueryTextListener((SearchView.OnQueryTextListener)new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String s) {
                Debug.Printf("searchview: textchange", new Object[0]);
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }
            
            @Override
            public boolean onQueryTextSubmit(final String s) {
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(menu.findItem(2131755824), (MenuItemCompat.OnActionExpandListener)new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(final MenuItem menuItem) {
                final View view = ObjectSelectorFragment.this.getView();
                if (view != null) {
                    view.findViewById(2131755561).setVisibility(8);
                    final Animation animation = view.findViewById(2131755561).getAnimation();
                    if (animation != null) {
                        animation.cancel();
                    }
                }
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }
            
            @Override
            public boolean onMenuItemActionExpand(final MenuItem menuItem) {
                final View view = ObjectSelectorFragment.this.getView();
                if (view != null) {
                    view.findViewById(2131755561).setVisibility(0);
                    view.findViewById(2131755561).startAnimation(AnimationUtils.loadAnimation(ObjectSelectorFragment.this.getContext(), 2131034127));
                }
                ObjectSelectorFragment.this.updateFilter();
                return true;
            }
        });
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968694, viewGroup, false);
        ((ExpandableListView)inflate.findViewById(2131755569)).setAdapter((ExpandableListAdapter)new ObjectListAdapter(layoutInflater.getContext()));
        ((ExpandableListView)inflate.findViewById(2131755569)).setOnGroupClickListener((ExpandableListView$OnGroupClickListener)this);
        ((ExpandableListView)inflate.findViewById(2131755569)).setOnChildClickListener((ExpandableListView$OnChildClickListener)this);
        ((SeekBar)inflate.findViewById(2131755563)).setMax(256);
        ((SeekBar)inflate.findViewById(2131755563)).setOnSeekBarChangeListener((SeekBar$OnSeekBarChangeListener)this);
        ((CheckBox)inflate.findViewById(2131755567)).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this);
        ((CheckBox)inflate.findViewById(2131755568)).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this);
        ((CheckBox)inflate.findViewById(2131755566)).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this);
        if (Build$VERSION.SDK_INT >= 14) {
            final ButteryProgressBar butteryProgressBar = new ButteryProgressBar(layoutInflater.getContext());
            butteryProgressBar.setId(2131755041);
            ((FrameLayout)inflate.findViewById(2131755560)).addView((View)butteryProgressBar, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, (int)TypedValue.applyDimension(1, 4.0f, layoutInflater.getContext().getResources().getDisplayMetrics())));
        }
        return inflate;
    }
    
    public boolean onGroupClick(final ExpandableListView expandableListView, final View view, final int n, final long n2) {
        Debug.Printf("displayObjects: onGroupClick: view %s id %d", view, view.getId());
        final ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
        if (expandableListAdapter instanceof ObjectListAdapter) {
            final SLObjectDisplayInfo group = ((ObjectListAdapter)expandableListAdapter).getGroup(n);
            if (group != null) {
                this.showObjectDetails(group);
            }
        }
        return true;
    }
    
    public void onProgressChanged(final SeekBar seekBar, final int i, final boolean b) {
        final View view = this.getView();
        if (view != null) {
            ((TextView)view.findViewById(2131755564)).setText((CharSequence)this.getString(2131296836, i));
            if (b) {
                this.updateFilter();
            }
        }
    }
    
    @Override
    public void onStart() {
        int progress = 256;
        super.onStart();
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            userManager.getObjectsManager().setFilter(this.filterInfo);
            this.subscription = userManager.getObjectsManager().getObjectDisplayList().subscribe(SubscriptionSingleKey.Value, UIThreadExecutor.getInstance(), this.onObjectListData, this.onObjectListError);
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.drawDistance.EnableObjectSelect();
                    final View view = this.getView();
                    if (view != null) {
                        final int n = (int)modules.drawDistance.getObjectSelectRange();
                        if (n < 1) {
                            progress = 1;
                        }
                        else if (n <= 256) {
                            progress = n;
                        }
                        ((SeekBar)view.findViewById(2131755563)).setProgress(progress);
                    }
                }
            }
        }
    }
    
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }
    
    @Override
    public void onStop() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.drawDistance.DisableObjectSelect();
                }
            }
        }
        super.onStop();
    }
    
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }
}
