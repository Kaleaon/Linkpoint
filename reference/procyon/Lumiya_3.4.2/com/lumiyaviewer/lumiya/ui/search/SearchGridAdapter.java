// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import android.annotation.SuppressLint;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import butterknife.ButterKnife;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import butterknife.BindView;
import android.widget.TextView;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import de.greenrobot.dao.query.LazyList;
import android.content.Context;
import java.util.UUID;
import android.support.v7.widget.RecyclerView;

class SearchGridAdapter extends Adapter<SearchViewHolder>
{
    private final UUID agentUUID;
    private final Context context;
    @Nullable
    private LazyList<SearchGridResult> data;
    private final LayoutInflater inflater;
    private final OnSearchResultClickListener onSearchResultClickListener;
    
    SearchGridAdapter(final Context context, final UUID agentUUID, final OnSearchResultClickListener onSearchResultClickListener) {
        this.context = context;
        this.agentUUID = agentUUID;
        this.inflater = LayoutInflater.from(context);
        this.onSearchResultClickListener = onSearchResultClickListener;
        ((RecyclerView.Adapter)this).setHasStableIds(true);
    }
    
    @Override
    public int getItemCount() {
        int size;
        if (this.data != null) {
            size = this.data.size();
        }
        else {
            size = 0;
        }
        return size;
    }
    
    @Override
    public long getItemId(final int n) {
        if (this.data != null && n >= 0 && n < this.data.size()) {
            return this.data.get(n).getId();
        }
        return -1L;
    }
    
    public void onBindViewHolder(final SearchViewHolder searchViewHolder, final int n) {
        if (this.data != null && n >= 0 && n < this.data.size()) {
            searchViewHolder.bindToData(this.data.get(n));
        }
    }
    
    public SearchViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new SearchViewHolder(this.inflater.inflate(2130968730, viewGroup, false));
    }
    
    public void onViewRecycled(final SearchViewHolder searchViewHolder) {
        searchViewHolder.onRecycled();
    }
    
    public void setData(@Nullable final LazyList<SearchGridResult> data) {
        this.data = data;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    interface OnSearchResultClickListener
    {
        void onSearchResultClicked(final SearchGridResult p0);
    }
    
    class SearchViewHolder extends ViewHolder implements OnChatterNameUpdated, View$OnClickListener
    {
        private ChatterNameRetriever chatterNameRetriever;
        @BindView(2131755647)
        TextView resultItemName;
        @BindView(2131755648)
        TextView resultMemberCount;
        private SearchGridResult searchGridResult;
        @BindView(2131755327)
        ChatterPicView userPicView;
        
        SearchViewHolder(final View view) {
            super(view);
            this.chatterNameRetriever = null;
            ButterKnife.bind(this, view);
            view.setOnClickListener((View$OnClickListener)this);
        }
        
        @SuppressLint({ "DefaultLocale", "SetTextI18n" })
        void bindToData(final SearchGridResult searchGridResult) {
            this.searchGridResult = searchGridResult;
            this.resultItemName.setText((CharSequence)searchGridResult.getItemName());
            if (searchGridResult.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                final Integer memberCount = searchGridResult.getMemberCount();
                this.resultMemberCount.setVisibility(0);
                final TextView resultMemberCount = this.resultMemberCount;
                int intValue;
                if (memberCount != null) {
                    intValue = memberCount;
                }
                else {
                    intValue = 0;
                }
                resultMemberCount.setText((CharSequence)Integer.toString(intValue));
            }
            else {
                this.resultMemberCount.setVisibility(8);
            }
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            if (searchGridResult.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                this.userPicView.setChatterID(ChatterID.getGroupChatterID(SearchGridAdapter.this.agentUUID, searchGridResult.getItemUUID()), searchGridResult.getItemName());
                this.userPicView.setVisibility(0);
            }
            else if (searchGridResult.getItemType() == SearchGridQuery.SearchType.People.ordinal()) {
                final ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(SearchGridAdapter.this.agentUUID, searchGridResult.getItemUUID());
                this.userPicView.setChatterID(userChatterID, searchGridResult.getItemName());
                this.userPicView.setVisibility(0);
                (this.chatterNameRetriever = new ChatterNameRetriever(userChatterID, (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getInstance(), false)).subscribe();
            }
            else {
                this.userPicView.setVisibility(8);
            }
        }
        
        @Override
        public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
            if (chatterNameRetriever == this.chatterNameRetriever) {
                final String resolvedName = chatterNameRetriever.getResolvedName();
                if (resolvedName != null) {
                    this.resultItemName.setText((CharSequence)resolvedName);
                }
            }
        }
        
        public void onClick(final View view) {
            if (SearchGridAdapter.this.onSearchResultClickListener != null && this.searchGridResult != null) {
                SearchGridAdapter.this.onSearchResultClickListener.onSearchResultClicked(this.searchGridResult);
            }
        }
        
        void onRecycled() {
            this.userPicView.setChatterID(null, null);
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            this.searchGridResult = null;
        }
    }
}
