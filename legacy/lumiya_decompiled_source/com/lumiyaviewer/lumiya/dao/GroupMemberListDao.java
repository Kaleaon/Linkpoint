package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;
/* loaded from: classes.dex */
public class GroupMemberListDao extends AbstractDao<GroupMemberList, UUID> {
    public static final String TABLENAME = "GroupMemberLists";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property GroupID = new Property(0, UUID.class, "groupID", true, "GROUP_ID");
        public static final Property RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
    }

    public GroupMemberListDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public GroupMemberListDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupMemberLists' ('GROUP_ID' TEXT PRIMARY KEY ,'REQUEST_ID' TEXT NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMemberLists'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, GroupMemberList groupMemberList) {
        sQLiteStatement.clearBindings();
        UUID groupID = groupMemberList.getGroupID();
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString());
        }
        sQLiteStatement.bindString(2, groupMemberList.getRequestID().toString());
    }

    @Override // de.greenrobot.dao.AbstractDao
    public UUID getKey(GroupMemberList groupMemberList) {
        if (groupMemberList != null) {
            return groupMemberList.getGroupID();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public boolean isEntityUpdateable() {
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.AbstractDao
    public GroupMemberList readEntity(Cursor cursor, int i) {
        return new GroupMemberList(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, GroupMemberList groupMemberList, int i) {
        groupMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        groupMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public UUID readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return UUID.fromString(cursor.getString(i + 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public UUID updateKeyAfterInsert(GroupMemberList groupMemberList, long j) {
        return groupMemberList.getGroupID();
    }
}
