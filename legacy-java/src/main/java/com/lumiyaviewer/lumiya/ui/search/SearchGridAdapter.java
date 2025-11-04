package com.lumiyaviewer.lumiya.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;
import javax.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SearchGridAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private final UUID agentUUID;
    private final Context context;

    @Nullable
    private LazyList<SearchGridResult> data;
    private final LayoutInflater inflater;
    private final OnSearchResultClickListener onSearchResultClickListener;

    /* loaded from: classes.dex */
    interface OnSearchResultClickListener {
        void onSearchResultClicked(SearchGridResult searchGridResult);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SearchViewHolder extends RecyclerView.ViewHolder implements ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private ChatterNameRetriever chatterNameRetriever;

        @BindView(R.id.result_item_name)
        TextView resultItemName;

        @BindView(R.id.result_member_count)
        TextView resultMemberCount;
        private SearchGridResult searchGridResult;

        @BindView(R.id.userPicView)
        ChatterPicView userPicView;

        SearchViewHolder(View view) {
            super(view);
            this.chatterNameRetriever = null;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bindToData(SearchGridResult searchGridResult) {
            this.searchGridResult = searchGridResult;
            this.resultItemName.setText(searchGridResult.getItemName());
            if (searchGridResult.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                Integer memberCount = searchGridResult.getMemberCount();
                this.resultMemberCount.setVisibility(0);
                this.resultMemberCount.setText(Integer.toString(memberCount != null ? memberCount.intValue() : 0));
            } else {
                this.resultMemberCount.setVisibility(8);
            }
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            if (searchGridResult.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                this.userPicView.setChatterID(ChatterID.getGroupChatterID(SearchGridAdapter.this.agentUUID, searchGridResult.getItemUUID()), searchGridResult.getItemName());
                this.userPicView.setVisibility(0);
            } else {
                if (searchGridResult.getItemType() != SearchGridQuery.SearchType.People.ordinal()) {
                    this.userPicView.setVisibility(8);
                    return;
                }
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(SearchGridAdapter.this.agentUUID, searchGridResult.getItemUUID());
                this.userPicView.setChatterID(userChatterID, searchGridResult.getItemName());
                this.userPicView.setVisibility(0);
                this.chatterNameRetriever = new ChatterNameRetriever(userChatterID, this, UIThreadExecutor.getInstance(), false);
                this.chatterNameRetriever.subscribe();
            }
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
        public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
            String resolvedName;
            if (chatterNameRetriever != this.chatterNameRetriever || (resolvedName = chatterNameRetriever.getResolvedName()) == null) {
                return;
            }
            this.resultItemName.setText(resolvedName);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (SearchGridAdapter.this.onSearchResultClickListener == null || this.searchGridResult == null) {
                return;
            }
            SearchGridAdapter.this.onSearchResultClickListener.onSearchResultClicked(this.searchGridResult);
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

    /* loaded from: classes.dex */
    public class SearchViewHolder_ViewBinding implements Unbinder {
        private SearchViewHolder target;

        @UiThread
        public SearchViewHolder_ViewBinding(SearchViewHolder searchViewHolder, View view) {
            this.target = searchViewHolder;
            searchViewHolder.resultItemName = (TextView) Utils.findRequiredViewAsType(view, R.id.result_item_name, "field 'resultItemName'", TextView.class);
            searchViewHolder.userPicView = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.userPicView, "field 'userPicView'", ChatterPicView.class);
            searchViewHolder.resultMemberCount = (TextView) Utils.findRequiredViewAsType(view, R.id.result_member_count, "field 'resultMemberCount'", TextView.class);
        }

        @Override // butterknife.Unbinder
        @CallSuper
        public void unbind() {
            SearchViewHolder searchViewHolder = this.target;
            if (searchViewHolder == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            searchViewHolder.resultItemName = null;
            searchViewHolder.userPicView = null;
            searchViewHolder.resultMemberCount = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SearchGridAdapter(Context context, UUID uuid, OnSearchResultClickListener onSearchResultClickListener) {
        this.context = context;
        this.agentUUID = uuid;
        this.inflater = LayoutInflater.from(context);
        this.onSearchResultClickListener = onSearchResultClickListener;
        setHasStableIds(true);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.data != null) {
            return this.data.size();
        }
        return 0;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return -1L;
        }
        return this.data.get(i).getId().longValue();
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return;
        }
        searchViewHolder.bindToData(this.data.get(i));
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SearchViewHolder(this.inflater.inflate(R.layout.search_result_item, viewGroup, false));
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onViewRecycled(SearchViewHolder searchViewHolder) {
        searchViewHolder.onRecycled();
    }

    public void setData(@Nullable LazyList<SearchGridResult> lazyList) {
        this.data = lazyList;
        notifyDataSetChanged();
    }
}
