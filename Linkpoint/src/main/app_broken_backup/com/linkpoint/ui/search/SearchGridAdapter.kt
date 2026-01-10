package com.linkpoint.ui.search

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.dao.SearchGridResult
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.modules.search.SearchGridQuery
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.ui.chat.ChatterPicView
import de.greenrobot.dao.query.LazyList
import java.util.UUID
import androidx.annotation.Nullable

class SearchGridAdapter : RecyclerView.Adapter<SearchViewHolder> {
    /* access modifiers changed from: private */
    UUID agentUUID
    private Context context
    @Nullable
    private LazyList<SearchGridResult> data
    private LayoutInflater inflater
    /* access modifiers changed from: private */
    OnSearchResultClickListener onSearchResultClickListener

    interface OnSearchResultClickListener {
        fun onSearchResultClicked(SearchGridResult searchGridResult)
    }

    class SearchViewHolder : RecyclerView.ViewHolder : ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private ChatterNameRetriever chatterNameRetriever = null
        @BindView(2131755647)
        TextView resultItemName
        @BindView(2131755648)
        TextView resultMemberCount
        private SearchGridResult searchGridResult
        @BindView(2131755327)
        ChatterPicView userPicView

        SearchViewHolder(View view) {
            super(view)
            ButterKnife.bind((Any) this, view)
            view.setOnClickListener(this)
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        fun bindToData(SearchGridResult searchGridResult2)  {
            this.searchGridResult = searchGridResult2
            this.resultItemName.setText(searchGridResult2.getItemName())
            if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                var memberCount: Int = searchGridResult2.getMemberCount()
                this.resultMemberCount.setVisibility(0)
                this.resultMemberCount.setText(Int.toString(memberCount != null ? memberCount.intValue() : 0))
            } else {
                this.resultMemberCount.setVisibility(8)
            }
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose()
                this.chatterNameRetriever = null
            }
            if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.Groups.ordinal()) {
                this.userPicView.setChatterID(ChatterID.getGroupChatterID(SearchGridAdapter.this.agentUUID, searchGridResult2.getItemUUID()), searchGridResult2.getItemName())
                this.userPicView.setVisibility(0)
            } else if (searchGridResult2.getItemType() == SearchGridQuery.SearchType.People.ordinal()) {
                ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(SearchGridAdapter.this.agentUUID, searchGridResult2.getItemUUID())
                this.userPicView.setChatterID(userChatterID, searchGridResult2.getItemName())
                this.userPicView.setVisibility(0)
                this.chatterNameRetriever = ChatterNameRetriever(userChatterID, this, UIThreadExecutor.getInstance(), false)
                this.chatterNameRetriever.subscribe()
            } else {
                this.userPicView.setVisibility(8)
            }
        }

        fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever2)  {
            String resolvedName
            if (chatterNameRetriever2 == this.chatterNameRetriever && (resolvedName = chatterNameRetriever2.getResolvedName()) != null) {
                this.resultItemName.setText(resolvedName)
            }
        }

        fun onClick(View view)  {
            if (SearchGridAdapter.this.onSearchResultClickListener != null && this.searchGridResult != null) {
                SearchGridAdapter.this.onSearchResultClickListener.onSearchResultClicked(this.searchGridResult)
            }
        }

        /* access modifiers changed from: package-private */
        fun onRecycled()  {
            this.userPicView.setChatterID((ChatterID) null, (String) null)
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose()
                this.chatterNameRetriever = null
            }
            this.searchGridResult = null
        }
    }

    class SearchViewHolder_ViewBinding : Unbinder {
        private SearchViewHolder target

        @UiThread
        SearchViewHolder_ViewBinding(SearchViewHolder searchViewHolder, View view) {
            this.target = searchViewHolder
            searchViewHolder.resultItemName = (Utils as TextView).findRequiredViewAsType(view, R.id.result_item_name, "field 'resultItemName'", TextView.class)
            searchViewHolder.userPicView = (Utils as ChatterPicView).findRequiredViewAsType(view, R.id.userPicView, "field 'userPicView'", ChatterPicView.class)
            searchViewHolder.resultMemberCount = (Utils as TextView).findRequiredViewAsType(view, R.id.result_member_count, "field 'resultMemberCount'", TextView.class)
        }

        @CallSuper
        fun unbind()  {
            SearchViewHolder searchViewHolder = this.target
            if (searchViewHolder == null) {
                throw IllegalStateException("Bindings already cleared.")
            }
            this.target = null
            searchViewHolder.resultItemName = null
            searchViewHolder.userPicView = null
            searchViewHolder.resultMemberCount = null
        }
    }

    SearchGridAdapter(Context context2, UUID uuid, OnSearchResultClickListener onSearchResultClickListener2) {
        this.context = context2
        this.agentUUID = uuid
        this.inflater = LayoutInflater.from(context2)
        this.onSearchResultClickListener = onSearchResultClickListener2
        setHasStableIds(true)
    }

    fun getItemCount(): Int {
        if (this.data != null) {
            return this.data.size()
        }
        return 0
    }

    fun getItemId(Int i): Long {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return -1
        }
        return this.data.get(i).getId().longValue()
    }

    fun onBindViewHolder(SearchViewHolder searchViewHolder, Int i)  {
        if (this.data != null && i >= 0 && i < this.data.size()) {
            searchViewHolder.bindToData(this.data.get(i))
        }
    }

    fun onCreateViewHolder(ViewGroup viewGroup, Int i): SearchViewHolder {
        return SearchViewHolder(this.inflater.inflate(R.layout.search_result_item, viewGroup, false))
    }

    fun onViewRecycled(SearchViewHolder searchViewHolder)  {
        searchViewHolder.onRecycled()
    }

    fun setData(@Nullable LazyList<SearchGridResult> lazyList)  {
        this.data = lazyList
        notifyDataSetChanged()
    }
}
