// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class GroupMemberDao extends AbstractDao<GroupMember, Void>
{
    public static final String TABLENAME = "GroupMembers";
    
    public GroupMemberDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public GroupMemberDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String s;
        if (b) {
            s = "IF NOT EXISTS ";
        }
        else {
            s = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'GroupMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL ," + "'CONTRIBUTION' INTEGER NOT NULL ," + "'ONLINE_STATUS' TEXT NOT NULL ," + "'AGENT_POWERS' INTEGER NOT NULL ," + "'TITLE' TEXT NOT NULL ," + "'IS_OWNER' INTEGER NOT NULL );");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_GroupMembers_GROUP_ID_REQUEST_ID ON GroupMembers" + " (GROUP_ID,REQUEST_ID);");
    }
    
    public static void dropTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        final StringBuilder append = new StringBuilder().append("DROP TABLE ");
        String str;
        if (b) {
            str = "IF EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL(append.append(str).append("'GroupMembers'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final GroupMember groupMember) {
        sqLiteStatement.clearBindings();
        sqLiteStatement.bindString(1, groupMember.getGroupID().toString());
        sqLiteStatement.bindString(2, groupMember.getRequestID().toString());
        sqLiteStatement.bindString(3, groupMember.getUserID().toString());
        sqLiteStatement.bindLong(4, (long)groupMember.getContribution());
        sqLiteStatement.bindString(5, groupMember.getOnlineStatus());
        sqLiteStatement.bindLong(6, groupMember.getAgentPowers());
        sqLiteStatement.bindString(7, groupMember.getTitle());
        long n;
        if (groupMember.getIsOwner()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(8, n);
    }
    
    public Void getKey(final GroupMember groupMember) {
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public GroupMember readEntity(final Cursor cursor, final int n) {
        boolean b = false;
        final UUID fromString = UUID.fromString(cursor.getString(n + 0));
        final UUID fromString2 = UUID.fromString(cursor.getString(n + 1));
        final UUID fromString3 = UUID.fromString(cursor.getString(n + 2));
        final int int1 = cursor.getInt(n + 3);
        final String string = cursor.getString(n + 4);
        final long long1 = cursor.getLong(n + 5);
        final String string2 = cursor.getString(n + 6);
        if (cursor.getShort(n + 7) != 0) {
            b = true;
        }
        return new GroupMember(fromString, fromString2, fromString3, int1, string, long1, string2, b);
    }
    
    public void readEntity(final Cursor cursor, final GroupMember groupMember, final int n) {
        boolean isOwner = false;
        groupMember.setGroupID(UUID.fromString(cursor.getString(n + 0)));
        groupMember.setRequestID(UUID.fromString(cursor.getString(n + 1)));
        groupMember.setUserID(UUID.fromString(cursor.getString(n + 2)));
        groupMember.setContribution(cursor.getInt(n + 3));
        groupMember.setOnlineStatus(cursor.getString(n + 4));
        groupMember.setAgentPowers(cursor.getLong(n + 5));
        groupMember.setTitle(cursor.getString(n + 6));
        if (cursor.getShort(n + 7) != 0) {
            isOwner = true;
        }
        groupMember.setIsOwner(isOwner);
    }
    
    public Void readKey(final Cursor cursor, final int n) {
        return null;
    }
    
    @Override
    protected Void updateKeyAfterInsert(final GroupMember groupMember, final long n) {
        return null;
    }
    
    public static class Properties
    {
        public static final Property AgentPowers;
        public static final Property Contribution;
        public static final Property GroupID;
        public static final Property IsOwner;
        public static final Property OnlineStatus;
        public static final Property RequestID;
        public static final Property Title;
        public static final Property UserID;
        
        static {
            GroupID = new Property(0, UUID.class, "groupID", false, "GROUP_ID");
            RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
            UserID = new Property(2, UUID.class, "userID", false, "USER_ID");
            Contribution = new Property(3, Integer.TYPE, "contribution", false, "CONTRIBUTION");
            OnlineStatus = new Property(4, String.class, "onlineStatus", false, "ONLINE_STATUS");
            AgentPowers = new Property(5, Long.TYPE, "agentPowers", false, "AGENT_POWERS");
            Title = new Property(6, String.class, "title", false, "TITLE");
            IsOwner = new Property(7, Boolean.TYPE, "isOwner", false, "IS_OWNER");
        }
    }
}
