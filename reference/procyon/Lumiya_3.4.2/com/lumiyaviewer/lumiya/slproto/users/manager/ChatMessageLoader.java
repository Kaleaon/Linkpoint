// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCondition;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.Chatter;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;

public class ChatMessageLoader extends ChunkedListLoader<ChatMessage>
{
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nullable
    private Chatter chatter;
    @Nonnull
    private final ChatterID chatterID;
    @Nonnull
    private final UserManager userManager;
    
    ChatMessageLoader(@Nonnull final UserManager userManager, @Nonnull final ChatterID chatterID, final int n, @Nonnull final Executor executor, final boolean b, @Nonnull final EventListener eventListener) {
        super(n, executor, b, eventListener);
        this.chatter = null;
        this.chatterID = chatterID;
        this.userManager = userManager;
        this.chatMessageDao = userManager.getDaoSession().getChatMessageDao();
    }
    
    @Override
    protected LoadResult<ChatMessage> loadInBackground(final int n, final long n2, final boolean b) {
        if (this.chatter == null) {
            this.chatter = this.userManager.getChatterList().getActiveChattersManager().getChatter(this.chatterID, true);
        }
        if (this.chatter != null) {
            final QueryBuilder<ChatMessage> where = ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(this.chatter.getId()), new WhereCondition[0]);
            QueryBuilder<ChatMessage> queryBuilder;
            if (b) {
                queryBuilder = where.where(ChatMessageDao.Properties.Id.gt(n2), new WhereCondition[0]).orderAsc(ChatMessageDao.Properties.Id);
            }
            else {
                queryBuilder = where.where(ChatMessageDao.Properties.Id.lt(n2), new WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id);
            }
            queryBuilder.limit(n + 1);
            final List<ChatMessage> list = queryBuilder.list();
            final boolean b2 = list.size() > n;
            if (b2) {
                list.remove(list.size() - 1);
            }
            return (LoadResult<ChatMessage>)new LoadResult((List<Object>)list, b2, n2);
        }
        return (LoadResult<ChatMessage>)new LoadResult(new ArrayList<Object>(), false, n2);
    }
}
