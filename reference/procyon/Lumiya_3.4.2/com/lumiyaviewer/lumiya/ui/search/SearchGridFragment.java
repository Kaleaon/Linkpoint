// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import butterknife.OnEditorAction;
import android.view.KeyEvent;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import butterknife.ButterKnife;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import android.widget.EditText;
import android.support.v7.widget.RecyclerView;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import butterknife.BindView;
import android.widget.RadioGroup;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class SearchGridFragment extends FragmentWithTitle implements OnLoadableDataChangedListener, OnSearchResultClickListener
{
    private SearchGridAdapter adapter;
    private final LoadableMonitor loadableMonitor;
    @BindView(2131755641)
    RadioGroup radioGroupSearchType;
    private final SubscriptionData<SearchGridQuery, LazyList<SearchGridResult>> searchResults;
    @BindView(2131755645)
    RecyclerView searchResultsList;
    @BindView(2131755639)
    EditText searchString;
    private Unbinder unbinder;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues() {
        if (SearchGridFragment.-com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues != null) {
            return SearchGridFragment.-com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues = new int[SearchGridQuery.SearchType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.Groups.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.People.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.Places.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public SearchGridFragment() {
        this.searchResults = new SubscriptionData<SearchGridQuery, LazyList<SearchGridResult>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.searchResults }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
    }
    
    private void beginSearch() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final String trim = this.searchString.getText().toString().trim();
        if (!trim.isEmpty() && userManager != null) {
            SearchGridQuery.SearchType searchType = null;
            switch (this.radioGroupSearchType.getCheckedRadioButtonId()) {
                default: {
                    searchType = SearchGridQuery.SearchType.People;
                    break;
                }
                case 2131755642: {
                    searchType = SearchGridQuery.SearchType.People;
                    break;
                }
                case 2131755644: {
                    searchType = SearchGridQuery.SearchType.Groups;
                    break;
                }
                case 2131755643: {
                    searchType = SearchGridQuery.SearchType.Places;
                    break;
                }
            }
            this.searchResults.subscribe(userManager.getSearchManager().searchResults(), SearchGridQuery.create(UUID.randomUUID(), trim, searchType));
        }
    }
    
    public static SearchGridFragment newInstance(final UUID uuid) {
        final SearchGridFragment searchGridFragment = new SearchGridFragment();
        final Bundle arguments = new Bundle();
        ActivityUtils.setActiveAgentID(arguments, uuid);
        searchGridFragment.setArguments(arguments);
        return searchGridFragment;
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968728, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.adapter = new SearchGridAdapter(layoutInflater.getContext(), ActivityUtils.getActiveAgentID(this.getArguments()), (SearchGridAdapter.OnSearchResultClickListener)this);
        this.searchResultsList.setAdapter((RecyclerView.Adapter)this.adapter);
        this.setTitle(this.getString(2131297000), null);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296534), this.getString(2131297001));
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        this.unbinder.unbind();
        super.onDestroyView();
    }
    
    @Override
    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            final LazyList data = this.searchResults.getData();
            this.adapter.setData(data);
            this.loadableMonitor.setEmptyMessage(data != null && data.isEmpty(), this.getString(2131296766));
        }
    }
    
    @OnClick({ 2131755640 })
    public void onSearchButtonClicked() {
        this.beginSearch();
    }
    
    @Override
    public void onSearchResultClicked(final SearchGridResult searchGridResult) {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getArguments());
        if (searchGridResult != null && activeAgentID != null) {
            switch (-getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues()[SearchGridQuery.SearchType.values()[searchGridResult.getItemType()].ordinal()]) {
                case 2: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, searchGridResult.getItemUUID())));
                    break;
                }
                case 1: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupProfileFragment.class, ChatterFragment.makeSelection(ChatterID.getGroupChatterID(activeAgentID, searchGridResult.getItemUUID())));
                    break;
                }
                case 3: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), ParcelInfoFragment.class, ParcelInfoFragment.makeSelection(activeAgentID, searchGridResult.getItemUUID()));
                    break;
                }
            }
        }
    }
    
    @OnEditorAction({ 2131755639 })
    public boolean onSearchTextAction(final int n, final KeyEvent keyEvent) {
        if (n == 3 || (keyEvent != null && keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66)) {
            this.beginSearch();
            return true;
        }
        return false;
    }
}
