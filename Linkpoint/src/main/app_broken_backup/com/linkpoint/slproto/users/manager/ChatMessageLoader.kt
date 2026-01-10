package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.ChatMessage
import com.linkpoint.dao.ChatMessageDao
import com.linkpoint.dao.Chatter
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.utils.wlist.ChunkedListLoader
import de.greenrobot.dao.query.QueryBuilder
import de.greenrobot.dao.query.WhereCondition
import java.util.ArrayList
import java.util.List
import java.util.concurrent.Executor
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageLoader : ChunkedListLoader<ChatMessage> {
    @NonNull
    private ChatMessageDao chatMessageDao
    @Nullable
    private Chatter chatter = null
    @NonNull
    private ChatterID chatterID
    @NonNull
    private UserManager userManager

    ChatMessageLoader(@NonNull UserManager userManager2, @NonNull ChatterID chatterID2, Int i, @NonNull Executor executor, Boolean z, @NonNull ChunkedListLoader.EventListener eventListener) {
        super(i, executor, z, eventListener)
        this.chatterID = chatterID2
        this.userManager = userManager2
        this.chatMessageDao = userManager2.getDaoSession().getChatMessageDao()
    }

    /* access modifiers changed from: protected */
    ChunkedListLoader.LoadResult<ChatMessage> loadInBackground(Int i, Long j, Boolean z) {
        if (this.chatter == null) {
            this.chatter = this.userManager.getChatterList().getActiveChattersManager().getChatter(this.chatterID, true)
        }
        if (this.chatter == null) {
            return ChunkedListLoader.LoadResult<>(ArrayList(), false, j)
        }
        QueryBuilder where = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(this.chatter.getId()), WhereCondition[0])
        QueryBuilder orderAsc = z ? where.where(ChatMessageDao.Properties.Id.gt(Long.valueOf(j)), WhereCondition[0]).orderAsc(ChatMessageDao.Properties.Id) : where.where(ChatMessageDao.Properties.Id.lt(Long.valueOf(j)), WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id)
        orderAsc.limit(i + 1)
        List list = orderAsc.list()
        var z2: Boolean = list.size() > i
        if (z2) {
            list.remove(list.size() - 1)
        }
        return ChunkedListLoader.LoadResult<>(list, z2, j)
    }
}
