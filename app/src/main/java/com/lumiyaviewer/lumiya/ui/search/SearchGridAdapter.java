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

class SearchGridAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    /* access modifiers changed from: private */
    public final UUID agentUUID;
    private final Context context;
    @Nullable
    private LazyList<SearchGridResult> data;
    private final LayoutInflater inflater;
    /* access modifiers changed from: private */
    public final OnSearchResultClickListener onSearchResultClickListener;

    interface OnSearchResultClickListener {
        void onSearchResultClicked(SearchGridResult searchGridResult);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private ChatterNameRetriever chatterNameRetriever = null;
        @BindView(2131755647)
        TextView resultItemName;
        @BindView(2131755648)
        TextView resultMemberCount;
        private SearchGridResult searchGridResult;
        @BindView(2131755327)
        ChatterPicView userPicView;

        SearchViewHolder(View view) {
            super(view);
            ButterKnife.bind((Object) this, view);
            view.setOnClickListener(this);
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bindToData(SearchGridResult searchGridResult2) {
            this.searchGridResult = searchGridResult2;
            this.resultItemName.setText(searchGridResult2.getItemName());
            if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                Integer memberCount = searchGridResult2.getMemberCount();
                this.resultMemberCount.setVisibility(0);
                this.resultMemberCount.setText(Integer.toString(memberCount != null ? memberCount.intValue() : 0));
            } else {
                this.resultMemberCount.setVisibility(8);
            }
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                this.userPicView.setChatterID(ChatterID.getGroupChatterID(SearchGridAdapter.this.agentUUID, searchGridResult2.getItemUUID()), searchGridResult2.getItemName());
                this.userPicView.setVisibility(0);
            } else if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.People.ordinal()) {
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(SearchGridAdapter.this.agentUUID, searchGridResult2.getItemUUID());
                this.userPicView.setChatterID(userChatterID, searchGridResult2.getItemName());
                this.userPicView.setVisibility(0);
                this.chatterNameRetriever = new ChatterNameRetriever(userChatterID, this, UIThreadExecutor.getInstance(), false);
                this.chatterNameRetriever.subscribe();
            } else {
                this.userPicView.setVisibility(8);
            }
        }

        public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever2) {
            String resolvedName;
            if (chatterNameRetriever2 == this.chatterNameRetriever && (resolvedName = chatterNameRetriever2.getResolvedName()) != null) {
                this.resultItemName.setText(resolvedName);
            }
        }

        public void onClick(View view) {
            if (SearchGridAdapter.this.onSearchResultClickListener != null && this.searchGridResult != null) {
                SearchGridAdapter.this.onSearchResultClickListener.onSearchResultClicked(this.searchGridResult);
            }
        }

        /* access modifiers changed from: package-private */
        public void onRecycled() {
            this.userPicView.setChatterID((ChatterID) null, (String) null);
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            this.searchGridResult = null;
        }
    }

    public class SearchViewHolder_ViewBinding implements Unbinder {
        private SearchViewHolder target;

        @UiThread
        public SearchViewHolder_ViewBinding(SearchViewHolder searchViewHolder, View view) {
            this.target = searchViewHolder;
            searchViewHolder.resultItemName = (TextView) Utils.findRequiredViewAsType(view, R.id.result_item_name, "field 'resultItemName'", TextView.class);
            searchViewHolder.userPicView = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.userPicView, "field 'userPicView'", ChatterPicView.class);
            searchViewHolder.resultMemberCount = (TextView) Utils.findRequiredViewAsType(view, R.id.result_member_count, "field 'resultMemberCount'", TextView.class);
        }

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

    SearchGridAdapter(Context context2, UUID uuid, OnSearchResultClickListener onSearchResultClickListener2) {
        this.context = context2;
        this.agentUUID = uuid;
        this.inflater = LayoutInflater.from(context2);
        this.onSearchResultClickListener = onSearchResultClickListener2;
        setHasStableIds(true);
    }

    public int getItemCount() {
        if (this.data != null) {
            return this.data.size();
        }
        return 0;
    }

    public long getItemId(int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return -1;
        }
        return this.data.get(i).getId().longValue();
    }

    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        if (this.data != null && i >= 0 && i < this.data.size()) {
            searchViewHolder.bindToData(this.data.get(i));
        }
    }

    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SearchViewHolder(this.inflater.inflate(R.layout.search_result_item, viewGroup, false));
    }

    public void onViewRecycled(SearchViewHolder searchViewHolder) {
        searchViewHolder.onRecycled();
    }

    public void setData(@Nullable LazyList<SearchGridResult> lazyList) {
        this.data = lazyList;
        notifyDataSetChanged();
    }
}
