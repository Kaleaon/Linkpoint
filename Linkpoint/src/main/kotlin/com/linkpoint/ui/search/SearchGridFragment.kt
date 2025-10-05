package com.linkpoint.ui.search

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.Unbinder
import com.linkpoint.R
import com.linkpoint.dao.SearchGridResult
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.modules.search.SearchGridQuery
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.profiles.GroupProfileFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.search.SearchGridAdapter
import de.greenrobot.dao.query.LazyList
import java.util.UUID

class SearchGridFragment : FragmentWithTitle() : LoadableMonitor.OnLoadableDataChangedListener, SearchGridAdapter.OnSearchResultClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = null
    private SearchGridAdapter adapter
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.searchResults).withDataChangedListener(this)
    @BindView(2131755641)
    RadioGroup radioGroupSearchType
    private val SubscriptionData<SearchGridQuery, LazyList<SearchGridResult>> searchResults = SubscriptionData<>(UIThreadExecutor.getInstance())
    @BindView(2131755645)
    RecyclerView searchResultsList
    @BindView(2131755639)
    EditText searchString
    private Unbinder unbinder

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m851getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues() {
        if (f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues != null) {
            return f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues
        }
        Int[] iArr = Int[SearchGridQuery.SearchType.values().length]
        try {
            iArr[SearchGridQuery.SearchType.Groups.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SearchGridQuery.SearchType.People.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SearchGridQuery.SearchType.Places.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f584comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = iArr
        return iArr
    }

    private Unit beginSearch() {
        SearchGridQuery.SearchType searchType
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        String trim = this.searchString.getText().toString().trim()
        if (!trim.isEmpty() && userManager != null) {
            switch (this.radioGroupSearchType.getCheckedRadioButtonId()) {
                case R.id.radio_people:
                    searchType = SearchGridQuery.SearchType.People
                    break
                case R.id.radio_places:
                    searchType = SearchGridQuery.SearchType.Places
                    break
                case R.id.radio_groups:
                    searchType = SearchGridQuery.SearchType.Groups
                    break
                default:
                    searchType = SearchGridQuery.SearchType.People
                    break
            }
            this.searchResults.subscribe(userManager.getSearchManager().searchResults(), SearchGridQuery.create(UUID.randomUUID(), trim, searchType))
        }
    }

    @JvmStatic
    SearchGridFragment newInstance(UUID uuid) {
        SearchGridFragment searchGridFragment = SearchGridFragment()
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        searchGridFragment.setArguments(bundle)
        return searchGridFragment
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.search_fragment, viewGroup, false)
        this.unbinder = ButterKnife.bind((Object) this, inflate)
        this.adapter = SearchGridAdapter(layoutInflater.getContext(), ActivityUtils.getActiveAgentID(getArguments()), this)
        this.searchResultsList.setAdapter(this.adapter)
        setTitle(getString(R.string.search), (String) null)
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.enter_text_to_search), getString(R.string.search_fail))
        return inflate
    }

    public Unit onDestroyView() {
        this.unbinder.unbind()
        super.onDestroyView()
    }

    public Unit onLoadableDataChanged() {
        if (this.adapter != null) {
            LazyList data = this.searchResults.getData()
            this.adapter.setData(data)
            this.loadableMonitor.setEmptyMessage(data != null ? data.isEmpty() : false, getString(R.string.nothing_found))
        }
    }

    @OnClick({2131755640})
    public Unit onSearchButtonClicked() {
        beginSearch()
    }

    public Unit onSearchResultClicked(SearchGridResult searchGridResult) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments())
        if (searchGridResult != null && activeAgentID != null) {
            switch (m851getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues()[SearchGridQuery.SearchType.values()[searchGridResult.getItemType()].ordinal()]) {
                case 1:
                    DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(ChatterID.getGroupChatterID(activeAgentID, searchGridResult.getItemUUID())))
                    return
                case 2:
                    DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, searchGridResult.getItemUUID())))
                    return
                case 3:
                    DetailsActivity.showEmbeddedDetails(getActivity(), ParcelInfoFragment.class, ParcelInfoFragment.makeSelection(activeAgentID, searchGridResult.getItemUUID()))
                    return
                default:
                    return
            }
        }
    }

    @OnEditorAction({2131755639})
    public Boolean onSearchTextAction(Int i, KeyEvent keyEvent) {
        if (i != 3 && (keyEvent == null || keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false
        }
        beginSearch()
        return true
    }
}
