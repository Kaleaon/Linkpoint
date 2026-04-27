// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            CachedResponseDao, CachedAssetDao, MoneyTransactionDao, MuteListCachedDataDao, 
//            SearchGridResultDao, GroupMemberDao, GroupMemberListDao, GroupRoleMemberDao, 
//            GroupRoleMemberListDao, UserDao, FriendDao, UserNameDao, 
//            UserPicDao, ChatMessageDao, ChatterDao, DaoSession

public class DaoMaster extends AbstractDaoMaster
{
    public static class DevOpenHelper extends OpenHelper
    {

        public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j)
        {
            Log.i("greenDAO", (new StringBuilder()).append("Upgrading schema from version ").append(i).append(" to ").append(j).append(" by dropping all tables").toString());
            DaoMaster.dropAllTables(sqlitedatabase, true);
            onCreate(sqlitedatabase);
        }

        public DevOpenHelper(Context context, String s, android.database.sqlite.SQLiteDatabase.CursorFactory cursorfactory)
        {
            super(context, s, cursorfactory);
        }
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper
    {

        public void onCreate(SQLiteDatabase sqlitedatabase)
        {
            Log.i("greenDAO", "Creating tables for schema version 71");
            DaoMaster.createAllTables(sqlitedatabase, false);
        }

        public OpenHelper(Context context, String s, android.database.sqlite.SQLiteDatabase.CursorFactory cursorfactory)
        {
            super(context, s, cursorfactory, 71);
        }
    }


    public static final int SCHEMA_VERSION = 71;

    public DaoMaster(SQLiteDatabase sqlitedatabase)
    {
        super(sqlitedatabase, 71);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/CachedResponseDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/CachedAssetDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/MoneyTransactionDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/MuteListCachedDataDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/SearchGridResultDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/GroupMemberDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/GroupMemberListDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/GroupRoleMemberDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/GroupRoleMemberListDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/UserDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/FriendDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/UserNameDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/UserPicDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/ChatMessageDao);
        registerDaoClass(com/lumiyaviewer/lumiya/dao/ChatterDao);
    }

    public static void createAllTables(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        CachedResponseDao.createTable(sqlitedatabase, flag);
        CachedAssetDao.createTable(sqlitedatabase, flag);
        MoneyTransactionDao.createTable(sqlitedatabase, flag);
        MuteListCachedDataDao.createTable(sqlitedatabase, flag);
        SearchGridResultDao.createTable(sqlitedatabase, flag);
        GroupMemberDao.createTable(sqlitedatabase, flag);
        GroupMemberListDao.createTable(sqlitedatabase, flag);
        GroupRoleMemberDao.createTable(sqlitedatabase, flag);
        GroupRoleMemberListDao.createTable(sqlitedatabase, flag);
        UserDao.createTable(sqlitedatabase, flag);
        FriendDao.createTable(sqlitedatabase, flag);
        UserNameDao.createTable(sqlitedatabase, flag);
        UserPicDao.createTable(sqlitedatabase, flag);
        ChatMessageDao.createTable(sqlitedatabase, flag);
        ChatterDao.createTable(sqlitedatabase, flag);
    }

    public static void dropAllTables(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        CachedResponseDao.dropTable(sqlitedatabase, flag);
        CachedAssetDao.dropTable(sqlitedatabase, flag);
        MoneyTransactionDao.dropTable(sqlitedatabase, flag);
        MuteListCachedDataDao.dropTable(sqlitedatabase, flag);
        SearchGridResultDao.dropTable(sqlitedatabase, flag);
        GroupMemberDao.dropTable(sqlitedatabase, flag);
        GroupMemberListDao.dropTable(sqlitedatabase, flag);
        GroupRoleMemberDao.dropTable(sqlitedatabase, flag);
        GroupRoleMemberListDao.dropTable(sqlitedatabase, flag);
        UserDao.dropTable(sqlitedatabase, flag);
        FriendDao.dropTable(sqlitedatabase, flag);
        UserNameDao.dropTable(sqlitedatabase, flag);
        UserPicDao.dropTable(sqlitedatabase, flag);
        ChatMessageDao.dropTable(sqlitedatabase, flag);
        ChatterDao.dropTable(sqlitedatabase, flag);
    }

    public DaoSession newSession()
    {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType identityscopetype)
    {
        return new DaoSession(db, identityscopetype, daoConfigMap);
    }

    public volatile AbstractDaoSession newSession()
    {
        return newSession();
    }

    public volatile AbstractDaoSession newSession(IdentityScopeType identityscopetype)
    {
        return newSession(identityscopetype);
    }
}
