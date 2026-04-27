// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.SearchManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            SearchGridAdapter, ParcelInfoFragment

public class SearchGridFragment extends FragmentWithTitle
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, SearchGridAdapter.OnSearchResultClickListener
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues[];
    private SearchGridAdapter adapter;
    private final LoadableMonitor loadableMonitor;
    RadioGroup radioGroupSearchType;
    private final SubscriptionData searchResults = new SubscriptionData(UIThreadExecutor.getInstance());
    RecyclerView searchResultsList;
    EditText searchString;
    private Unbinder unbinder;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Groups.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.People.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Places.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues = ai;
        return ai;
    }

    public SearchGridFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            searchResults
        })).withDataChangedListener(this);
    }

    private void beginSearch()
    {
        UserManager usermanager;
        String s;
        usermanager = ActivityUtils.getUserManager(getArguments());
        s = searchString.getText().toString().trim();
        if (s.isEmpty() || usermanager == null) goto _L2; else goto _L1
_L1:
        radioGroupSearchType.getCheckedRadioButtonId();
        JVM INSTR tableswitch 2131755642 2131755644: default 68
    //                   2131755642 97
    //                   2131755643 111
    //                   2131755644 104;
           goto _L3 _L4 _L5 _L6
_L3:
        Object obj = com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.People;
_L8:
        obj = SearchGridQuery.create(UUID.randomUUID(), s, ((com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType) (obj)));
        searchResults.subscribe(usermanager.getSearchManager().searchResults(), obj);
_L2:
        return;
_L4:
        obj = com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.People;
        continue; /* Loop/switch isn't completed */
_L6:
        obj = com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Groups;
        continue; /* Loop/switch isn't completed */
_L5:
        obj = com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Places;
        if (true) goto _L8; else goto _L7
_L7:
    }

    public static SearchGridFragment newInstance(UUID uuid)
    {
        SearchGridFragment searchgridfragment = new SearchGridFragment();
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        searchgridfragment.setArguments(bundle);
        return searchgridfragment;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f040098, viewgroup, false);
        unbinder = ButterKnife.bind(this, viewgroup);
        adapter = new SearchGridAdapter(layoutinflater.getContext(), ActivityUtils.getActiveAgentID(getArguments()), this);
        searchResultsList.setAdapter(adapter);
        setTitle(getString(0x7f0902e8), null);
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f090116), getString(0x7f0902e9));
        return viewgroup;
    }

    public void onDestroyView()
    {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void onLoadableDataChanged()
    {
        if (adapter != null)
        {
            LazyList lazylist = (LazyList)searchResults.getData();
            adapter.setData(lazylist);
            LoadableMonitor loadablemonitor = loadableMonitor;
            boolean flag;
            if (lazylist != null)
            {
                flag = lazylist.isEmpty();
            } else
            {
                flag = false;
            }
            loadablemonitor.setEmptyMessage(flag, getString(0x7f0901fe));
        }
    }

    public void onSearchButtonClicked()
    {
        beginSearch();
    }

    public void onSearchResultClicked(SearchGridResult searchgridresult)
    {
        UUID uuid = ActivityUtils.getActiveAgentID(getArguments());
        if (searchgridresult == null || uuid == null) goto _L2; else goto _L1
_L1:
        com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType searchtype = com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.values()[searchgridresult.getItemType()];
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues()[searchtype.ordinal()];
        JVM INSTR tableswitch 1 3: default 60
    //                   1 86
    //                   2 61
    //                   3 111;
           goto _L2 _L3 _L4 _L5
_L2:
        return;
_L4:
        searchgridresult = ChatterID.getUserChatterID(uuid, searchgridresult.getItemUUID());
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(searchgridresult));
        return;
_L3:
        searchgridresult = ChatterID.getGroupChatterID(uuid, searchgridresult.getItemUUID());
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment, GroupProfileFragment.makeSelection(searchgridresult));
        return;
_L5:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/search/ParcelInfoFragment, ParcelInfoFragment.makeSelection(uuid, searchgridresult.getItemUUID()));
        return;
    }

    public boolean onSearchTextAction(int i, KeyEvent keyevent)
    {
        if (i == 3 || keyevent != null && keyevent.getAction() == 0 && keyevent.getKeyCode() == 66)
        {
            beginSearch();
            return true;
        } else
        {
            return false;
        }
    }
}
