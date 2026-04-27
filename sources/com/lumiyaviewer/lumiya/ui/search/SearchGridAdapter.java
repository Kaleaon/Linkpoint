// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;

class SearchGridAdapter extends android.support.v7.widget.RecyclerView.Adapter
{
    static interface OnSearchResultClickListener
    {

        public abstract void onSearchResultClicked(SearchGridResult searchgridresult);
    }

    class SearchViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
        implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated, android.view.View.OnClickListener
    {

        private ChatterNameRetriever chatterNameRetriever;
        TextView resultItemName;
        TextView resultMemberCount;
        private SearchGridResult searchGridResult;
        final SearchGridAdapter this$0;
        ChatterPicView userPicView;

        void bindToData(SearchGridResult searchgridresult)
        {
            searchGridResult = searchgridresult;
            resultItemName.setText(searchgridresult.getItemName());
            if (searchgridresult.getItemType() == com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Groups.ordinal())
            {
                Integer integer = searchgridresult.getMemberCount();
                resultMemberCount.setVisibility(0);
                TextView textview = resultMemberCount;
                int i;
                if (integer != null)
                {
                    i = integer.intValue();
                } else
                {
                    i = 0;
                }
                textview.setText(Integer.toString(i));
            } else
            {
                resultMemberCount.setVisibility(8);
            }
            if (chatterNameRetriever != null)
            {
                chatterNameRetriever.dispose();
                chatterNameRetriever = null;
            }
            if (searchgridresult.getItemType() == com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.Groups.ordinal())
            {
                userPicView.setChatterID(ChatterID.getGroupChatterID(SearchGridAdapter._2D_get0(SearchGridAdapter.this), searchgridresult.getItemUUID()), searchgridresult.getItemName());
                userPicView.setVisibility(0);
                return;
            }
            if (searchgridresult.getItemType() == com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery.SearchType.People.ordinal())
            {
                com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser = ChatterID.getUserChatterID(SearchGridAdapter._2D_get0(SearchGridAdapter.this), searchgridresult.getItemUUID());
                userPicView.setChatterID(chatteriduser, searchgridresult.getItemName());
                userPicView.setVisibility(0);
                chatterNameRetriever = new ChatterNameRetriever(chatteriduser, this, UIThreadExecutor.getInstance(), false);
                chatterNameRetriever.subscribe();
                return;
            } else
            {
                userPicView.setVisibility(8);
                return;
            }
        }

        public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
        {
            if (chatternameretriever == chatterNameRetriever)
            {
                chatternameretriever = chatternameretriever.getResolvedName();
                if (chatternameretriever != null)
                {
                    resultItemName.setText(chatternameretriever);
                }
            }
        }

        public void onClick(View view)
        {
            if (SearchGridAdapter._2D_get1(SearchGridAdapter.this) != null && searchGridResult != null)
            {
                SearchGridAdapter._2D_get1(SearchGridAdapter.this).onSearchResultClicked(searchGridResult);
            }
        }

        void onRecycled()
        {
            userPicView.setChatterID(null, null);
            if (chatterNameRetriever != null)
            {
                chatterNameRetriever.dispose();
                chatterNameRetriever = null;
            }
            searchGridResult = null;
        }

        SearchViewHolder(View view)
        {
            this$0 = SearchGridAdapter.this;
            super(view);
            chatterNameRetriever = null;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }
    }


    private final UUID agentUUID;
    private final Context context;
    private LazyList data;
    private final LayoutInflater inflater;
    private final OnSearchResultClickListener onSearchResultClickListener;

    static UUID _2D_get0(SearchGridAdapter searchgridadapter)
    {
        return searchgridadapter.agentUUID;
    }

    static OnSearchResultClickListener _2D_get1(SearchGridAdapter searchgridadapter)
    {
        return searchgridadapter.onSearchResultClickListener;
    }

    SearchGridAdapter(Context context1, UUID uuid, OnSearchResultClickListener onsearchresultclicklistener)
    {
        context = context1;
        agentUUID = uuid;
        inflater = LayoutInflater.from(context1);
        onSearchResultClickListener = onsearchresultclicklistener;
        setHasStableIds(true);
    }

    public int getItemCount()
    {
        if (data != null)
        {
            return data.size();
        } else
        {
            return 0;
        }
    }

    public long getItemId(int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            return ((SearchGridResult)data.get(i)).getId().longValue();
        } else
        {
            return -1L;
        }
    }

    public volatile void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        onBindViewHolder((SearchViewHolder)viewholder, i);
    }

    public void onBindViewHolder(SearchViewHolder searchviewholder, int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            searchviewholder.bindToData((SearchGridResult)data.get(i));
        }
    }

    public volatile android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return onCreateViewHolder(viewgroup, i);
    }

    public SearchViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return new SearchViewHolder(inflater.inflate(0x7f04009a, viewgroup, false));
    }

    public volatile void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        onViewRecycled((SearchViewHolder)viewholder);
    }

    public void onViewRecycled(SearchViewHolder searchviewholder)
    {
        searchviewholder.onRecycled();
    }

    public void setData(LazyList lazylist)
    {
        data = lazylist;
        notifyDataSetChanged();
    }
}
