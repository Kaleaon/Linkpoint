package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ChatMessageLoader extends ChunkedListLoader<ChatMessage> {
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nullable
    private Chatter chatter;
    @Nonnull
    private final ChatterID chatterID;
    @Nonnull
    private final UserManager userManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatMessageLoader(@Nonnull UserManager userManager, @Nonnull ChatterID chatterID, int i, @Nonnull Executor executor, boolean z, @Nonnull ChunkedListLoader.EventListener eventListener) {
        super(i, executor, z, eventListener);
        this.chatter = null;
        this.chatterID = chatterID;
        this.userManager = userManager;
        this.chatMessageDao = userManager.getDaoSession().getChatMessageDao();
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader
    protected ChunkedListLoader.LoadResult<ChatMessage> loadInBackground(int i, long j, boolean z) {
        if (this.chatter == null) {
            this.chatter = this.userManager.getChatterList().getActiveChattersManager().getChatter(this.chatterID, true);
        }
        if (this.chatter != null) {
            QueryBuilder<ChatMessage> where = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(this.chatter.getId()), new WhereCondition[0]);
            QueryBuilder<ChatMessage> orderAsc = z ? where.where(ChatMessageDao.Properties.Id.gt(Long.valueOf(j)), new WhereCondition[0]).orderAsc(ChatMessageDao.Properties.Id) : where.where(ChatMessageDao.Properties.Id.lt(Long.valueOf(j)), new WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id);
            orderAsc.limit(i + 1);
            List<ChatMessage> list = orderAsc.list();
            boolean z2 = list.size() > i;
            if (z2) {
                list.remove(list.size() - 1);
            }
            return new ChunkedListLoader.LoadResult<>(list, z2, j);
        }
        return new ChunkedListLoader.LoadResult<>(new ArrayList(), false, j);
    }
}
