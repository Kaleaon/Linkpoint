package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class GroupRoleMemberListDao extends AbstractDao<GroupRoleMemberList, UUID> {
    public static final String TABLENAME = "GroupRoleMemberLists";

    public static class Properties {
        public static final Property GroupID = new Property(0, UUID.class, "groupID", true, "GROUP_ID");
        public static final Property MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        public static final Property RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
    }

    public GroupRoleMemberListDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public GroupRoleMemberListDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupRoleMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL ," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupRoleMemberLists'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, GroupRoleMemberList groupRoleMemberList) {
        sQLiteStatement.clearBindings();
        UUID groupID = groupRoleMemberList.getGroupID();
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString());
        }
        sQLiteStatement.bindString(2, groupRoleMemberList.getRequestID().toString());
        sQLiteStatement.bindLong(3, groupRoleMemberList.getMustRevalidate() ? 1 : 0);
    }

    public UUID getKey(GroupRoleMemberList groupRoleMemberList) {
        if (groupRoleMemberList != null) {
            return groupRoleMemberList.getGroupID();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public GroupRoleMemberList readEntity(Cursor cursor, int i) {
        boolean z = false;
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0));
        UUID fromString2 = UUID.fromString(cursor.getString(i + 1));
        if (cursor.getShort(i + 2) != 0) {
            z = true;
        }
        return new GroupRoleMemberList(fromString, fromString2, z);
    }

    public void readEntity(Cursor cursor, GroupRoleMemberList groupRoleMemberList, int i) {
        groupRoleMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        groupRoleMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)));
        groupRoleMemberList.setMustRevalidate(cursor.getShort(i + 2) != 0);
    }

    public UUID readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return UUID.fromString(cursor.getString(i + 0));
    }

    /* access modifiers changed from: protected */
    public UUID updateKeyAfterInsert(GroupRoleMemberList groupRoleMemberList, long j) {
        return groupRoleMemberList.getGroupID();
    }
}
