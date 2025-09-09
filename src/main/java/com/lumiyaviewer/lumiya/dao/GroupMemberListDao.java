package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class GroupMemberListDao extends AbstractDao<GroupMemberList, UUID> {
    public static final String TABLENAME = "GroupMemberLists";

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
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMemberLists'");
    }

    protected void bindValues(SQLiteStatement sQLiteStatement, GroupMemberList groupMemberList) {
        sQLiteStatement.clearBindings();
        UUID groupID = groupMemberList.getGroupID();
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString());
        }
        sQLiteStatement.bindString(2, groupMemberList.getRequestID().toString());
    }

    public UUID getKey(GroupMemberList groupMemberList) {
        return groupMemberList != null ? groupMemberList.getGroupID() : null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public GroupMemberList readEntity(Cursor cursor, int i) {
        return new GroupMemberList(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)));
    }

    public void readEntity(Cursor cursor, GroupMemberList groupMemberList, int i) {
        groupMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        groupMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)));
    }

    public UUID readKey(Cursor cursor, int i) {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0));
    }

    protected UUID updateKeyAfterInsert(GroupMemberList groupMemberList, long j) {
        return groupMemberList.getGroupID();
    }
}
