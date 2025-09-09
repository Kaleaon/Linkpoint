package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class GroupMemberDao extends AbstractDao<GroupMember, Void> {
    public static final String TABLENAME = "GroupMembers";

    public static class Properties {
        public static final Property AgentPowers = new Property(5, Long.TYPE, "agentPowers", false, "AGENT_POWERS");
        public static final Property Contribution = new Property(3, Integer.TYPE, "contribution", false, "CONTRIBUTION");
        public static final Property GroupID = new Property(0, UUID.class, "groupID", false, "GROUP_ID");
        public static final Property IsOwner = new Property(7, Boolean.TYPE, "isOwner", false, "IS_OWNER");
        public static final Property OnlineStatus = new Property(4, String.class, "onlineStatus", false, "ONLINE_STATUS");
        public static final Property RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
        public static final Property Title = new Property(6, String.class, "title", false, "TITLE");
        public static final Property UserID = new Property(2, UUID.class, "userID", false, "USER_ID");
    }

    public GroupMemberDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public GroupMemberDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL ," + "'CONTRIBUTION' INTEGER NOT NULL ," + "'ONLINE_STATUS' TEXT NOT NULL ," + "'AGENT_POWERS' INTEGER NOT NULL ," + "'TITLE' TEXT NOT NULL ," + "'IS_OWNER' INTEGER NOT NULL );");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_GroupMembers_GROUP_ID_REQUEST_ID ON GroupMembers" + " (GROUP_ID,REQUEST_ID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMembers'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, GroupMember groupMember) {
        sQLiteStatement.clearBindings();
        sQLiteStatement.bindString(1, groupMember.getGroupID().toString());
        sQLiteStatement.bindString(2, groupMember.getRequestID().toString());
        sQLiteStatement.bindString(3, groupMember.getUserID().toString());
        sQLiteStatement.bindLong(4, (long) groupMember.getContribution());
        sQLiteStatement.bindString(5, groupMember.getOnlineStatus());
        sQLiteStatement.bindLong(6, groupMember.getAgentPowers());
        sQLiteStatement.bindString(7, groupMember.getTitle());
        sQLiteStatement.bindLong(8, groupMember.getIsOwner() ? 1 : 0);
    }

    public Void getKey(GroupMember groupMember) {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public GroupMember readEntity(Cursor cursor, int i) {
        boolean z = false;
        UUID fromString = UUID.fromString(cursor.getString(i + 0));
        UUID fromString2 = UUID.fromString(cursor.getString(i + 1));
        UUID fromString3 = UUID.fromString(cursor.getString(i + 2));
        int i2 = cursor.getInt(i + 3);
        String string = cursor.getString(i + 4);
        long j = cursor.getLong(i + 5);
        String string2 = cursor.getString(i + 6);
        if (cursor.getShort(i + 7) != 0) {
            z = true;
        }
        return new GroupMember(fromString, fromString2, fromString3, i2, string, j, string2, z);
    }

    public void readEntity(Cursor cursor, GroupMember groupMember, int i) {
        boolean z = false;
        groupMember.setGroupID(UUID.fromString(cursor.getString(i + 0)));
        groupMember.setRequestID(UUID.fromString(cursor.getString(i + 1)));
        groupMember.setUserID(UUID.fromString(cursor.getString(i + 2)));
        groupMember.setContribution(cursor.getInt(i + 3));
        groupMember.setOnlineStatus(cursor.getString(i + 4));
        groupMember.setAgentPowers(cursor.getLong(i + 5));
        groupMember.setTitle(cursor.getString(i + 6));
        if (cursor.getShort(i + 7) != 0) {
            z = true;
        }
        groupMember.setIsOwner(z);
    }

    public Void readKey(Cursor cursor, int i) {
        return null;
    }

    /* access modifiers changed from: protected */
    public Void updateKeyAfterInsert(GroupMember groupMember, long j) {
        return null;
    }
}
