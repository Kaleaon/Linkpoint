// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager, ChatterList, ActiveChattersManager

public class ChatMessageLoader extends ChunkedListLoader
{

    private final ChatMessageDao chatMessageDao;
    private Chatter chatter;
    private final ChatterID chatterID;
    private final UserManager userManager;

    ChatMessageLoader(UserManager usermanager, ChatterID chatterid, int i, Executor executor, boolean flag, com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener eventlistener)
    {
        super(i, executor, flag, eventlistener);
        chatter = null;
        chatterID = chatterid;
        userManager = usermanager;
        chatMessageDao = usermanager.getDaoSession().getChatMessageDao();
    }

    protected com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.LoadResult loadInBackground(int i, long l, boolean flag)
    {
        if (chatter == null)
        {
            chatter = userManager.getChatterList().getActiveChattersManager().getChatter(chatterID, true);
        }
        if (chatter != null)
        {
            Object obj = chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), new WhereCondition[0]);
            if (flag)
            {
                obj = ((QueryBuilder) (obj)).where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id.gt(Long.valueOf(l)), new WhereCondition[0]).orderAsc(new Property[] {
                    com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
                });
            } else
            {
                obj = ((QueryBuilder) (obj)).where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id.lt(Long.valueOf(l)), new WhereCondition[0]).orderDesc(new Property[] {
                    com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
                });
            }
            ((QueryBuilder) (obj)).limit(i + 1);
            obj = ((QueryBuilder) (obj)).list();
            if (((List) (obj)).size() > i)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if (flag)
            {
                ((List) (obj)).remove(((List) (obj)).size() - 1);
            }
            return new com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.LoadResult(((List) (obj)), flag, l);
        } else
        {
            return new com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.LoadResult(new ArrayList(), false, l);
        }
    }
}
