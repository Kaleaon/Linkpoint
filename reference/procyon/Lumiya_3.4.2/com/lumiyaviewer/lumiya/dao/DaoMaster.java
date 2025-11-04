// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase$CursorFactory;
import android.content.Context;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.AbstractDao;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;

public class DaoMaster extends AbstractDaoMaster
{
    public static final int SCHEMA_VERSION = 71;
    
    public DaoMaster(final SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, 71);
        this.registerDaoClass(CachedResponseDao.class);
        this.registerDaoClass(CachedAssetDao.class);
        this.registerDaoClass(MoneyTransactionDao.class);
        this.registerDaoClass(MuteListCachedDataDao.class);
        this.registerDaoClass(SearchGridResultDao.class);
        this.registerDaoClass(GroupMemberDao.class);
        this.registerDaoClass(GroupMemberListDao.class);
        this.registerDaoClass(GroupRoleMemberDao.class);
        this.registerDaoClass(GroupRoleMemberListDao.class);
        this.registerDaoClass(UserDao.class);
        this.registerDaoClass(FriendDao.class);
        this.registerDaoClass(UserNameDao.class);
        this.registerDaoClass(UserPicDao.class);
        this.registerDaoClass(ChatMessageDao.class);
        this.registerDaoClass(ChatterDao.class);
    }
    
    public static void createAllTables(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        CachedResponseDao.createTable(sqLiteDatabase, b);
        CachedAssetDao.createTable(sqLiteDatabase, b);
        MoneyTransactionDao.createTable(sqLiteDatabase, b);
        MuteListCachedDataDao.createTable(sqLiteDatabase, b);
        SearchGridResultDao.createTable(sqLiteDatabase, b);
        GroupMemberDao.createTable(sqLiteDatabase, b);
        GroupMemberListDao.createTable(sqLiteDatabase, b);
        GroupRoleMemberDao.createTable(sqLiteDatabase, b);
        GroupRoleMemberListDao.createTable(sqLiteDatabase, b);
        UserDao.createTable(sqLiteDatabase, b);
        FriendDao.createTable(sqLiteDatabase, b);
        UserNameDao.createTable(sqLiteDatabase, b);
        UserPicDao.createTable(sqLiteDatabase, b);
        ChatMessageDao.createTable(sqLiteDatabase, b);
        ChatterDao.createTable(sqLiteDatabase, b);
    }
    
    public static void dropAllTables(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        CachedResponseDao.dropTable(sqLiteDatabase, b);
        CachedAssetDao.dropTable(sqLiteDatabase, b);
        MoneyTransactionDao.dropTable(sqLiteDatabase, b);
        MuteListCachedDataDao.dropTable(sqLiteDatabase, b);
        SearchGridResultDao.dropTable(sqLiteDatabase, b);
        GroupMemberDao.dropTable(sqLiteDatabase, b);
        GroupMemberListDao.dropTable(sqLiteDatabase, b);
        GroupRoleMemberDao.dropTable(sqLiteDatabase, b);
        GroupRoleMemberListDao.dropTable(sqLiteDatabase, b);
        UserDao.dropTable(sqLiteDatabase, b);
        FriendDao.dropTable(sqLiteDatabase, b);
        UserNameDao.dropTable(sqLiteDatabase, b);
        UserPicDao.dropTable(sqLiteDatabase, b);
        ChatMessageDao.dropTable(sqLiteDatabase, b);
        ChatterDao.dropTable(sqLiteDatabase, b);
    }
    
    @Override
    public DaoSession newSession() {
        return new DaoSession(this.db, IdentityScopeType.Session, this.daoConfigMap);
    }
    
    @Override
    public DaoSession newSession(final IdentityScopeType identityScopeType) {
        return new DaoSession(this.db, identityScopeType, this.daoConfigMap);
    }
    
    public static class DevOpenHelper extends OpenHelper
    {
        public DevOpenHelper(final Context context, final String s, final SQLiteDatabase$CursorFactory sqLiteDatabase$CursorFactory) {
            super(context, s, sqLiteDatabase$CursorFactory);
        }
        
        public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int j) {
            Log.i("greenDAO", "Upgrading schema from version " + i + " to " + j + " by dropping all tables");
            DaoMaster.dropAllTables(sqLiteDatabase, true);
            ((OpenHelper)this).onCreate(sqLiteDatabase);
        }
    }
    
    public abstract static class OpenHelper extends SQLiteOpenHelper
    {
        public OpenHelper(final Context context, final String s, final SQLiteDatabase$CursorFactory sqLiteDatabase$CursorFactory) {
            super(context, s, sqLiteDatabase$CursorFactory, 71);
        }
        
        public void onCreate(final SQLiteDatabase sqLiteDatabase) {
            Log.i("greenDAO", "Creating tables for schema version 71");
            DaoMaster.createAllTables(sqLiteDatabase, false);
        }
    }
}
