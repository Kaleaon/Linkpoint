package com.lumiyaviewer.lumiya.ui.search;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.search.SearchGridAdapter;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;

public class SearchGridFragment extends FragmentWithTitle implements LoadableMonitor.OnLoadableDataChangedListener, SearchGridAdapter.OnSearchResultClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = null;
    private SearchGridAdapter adapter;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.searchResults).withDataChangedListener(this);
    @BindView(2131755641)
    RadioGroup radioGroupSearchType;
    private final SubscriptionData<SearchGridQuery, LazyList<SearchGridResult>> searchResults = new SubscriptionData<>(UIThreadExecutor.getInstance());
    @BindView(2131755645)
    RecyclerView searchResultsList;
    @BindView(2131755639)
    EditText searchString;
    private Unbinder unbinder;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m851getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues() {
        if (f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues != null) {
            return f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues;
        }
        int[] iArr = new int[SearchGridQuery.SearchType.values().length];
        try {
            iArr[SearchGridQuery.SearchType.Groups.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SearchGridQuery.SearchType.People.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SearchGridQuery.SearchType.Places.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = iArr;
        return iArr;
    }

    private void beginSearch() {
        SearchGridQuery.SearchType searchType;
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        String trim = this.searchString.getText().toString().trim();
        if (!trim.isEmpty() && userManager != null) {
            switch (this.radioGroupSearchType.getCheckedRadioButtonId()) {
                case R.id.radio_people:
                    searchType = SearchGridQuery.SearchType.People;
                    break;
                case R.id.radio_places:
                    searchType = SearchGridQuery.SearchType.Places;
                    break;
                case R.id.radio_groups:
                    searchType = SearchGridQuery.SearchType.Groups;
                    break;
                default:
                    searchType = SearchGridQuery.SearchType.People;
                    break;
            }
            this.searchResults.subscribe(userManager.getSearchManager().searchResults(), SearchGridQuery.create(UUID.randomUUID(), trim, searchType));
        }
    }

    public static SearchGridFragment newInstance(UUID uuid) {
        SearchGridFragment searchGridFragment = new SearchGridFragment();
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        searchGridFragment.setArguments(bundle);
        return searchGridFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.search_fragment, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
        this.adapter = new SearchGridAdapter(layoutInflater.getContext(), ActivityUtils.getActiveAgentID(getArguments()), this);
        this.searchResultsList.setAdapter(this.adapter);
        setTitle(getString(R.string.search), (String) null);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.enter_text_to_search), getString(R.string.search_fail));
        return inflate;
    }

    public void onDestroyView() {
        this.unbinder.unbind();
        super.onDestroyView();
    }

    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            LazyList data = this.searchResults.getData();
            this.adapter.setData(data);
            this.loadableMonitor.setEmptyMessage(data != null ? data.isEmpty() : false, getString(R.string.nothing_found));
        }
    }

    @OnClick({2131755640})
    public void onSearchButtonClicked() {
        beginSearch();
    }

    public void onSearchResultClicked(SearchGridResult searchGridResult) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        if (searchGridResult != null && activeAgentID != null) {
            switch (m851getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues()[SearchGridQuery.SearchType.values()[searchGridResult.getItemType()].ordinal()]) {
                case 1:
                    DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(ChatterID.getGroupChatterID(activeAgentID, searchGridResult.getItemUUID())));
                    return;
                case 2:
                    DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, searchGridResult.getItemUUID())));
                    return;
                case 3:
                    DetailsActivity.showEmbeddedDetails(getActivity(), ParcelInfoFragment.class, ParcelInfoFragment.makeSelection(activeAgentID, searchGridResult.getItemUUID()));
                    return;
                default:
                    return;
            }
        }
    }

    @OnEditorAction({2131755639})
    public boolean onSearchTextAction(int i, KeyEvent keyEvent) {
        if (i != 3 && (keyEvent == null || keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        beginSearch();
        return true;
    }
}
