package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupMemberDao : AbstractDao()<GroupMember, Void> {
    const val TABLENAME: String = "GroupMembers"

    @JvmStatic
    class Properties {
        const val Property AgentPowers = Property(5, Long.TYPE, "agentPowers", false, "AGENT_POWERS")
        const val Property Contribution = Property(3, Integer.TYPE, "contribution", false, "CONTRIBUTION")
        const val Property GroupID = Property(0, UUID.class, "groupID", false, "GROUP_ID")
        const val Property IsOwner = Property(7, Boolean.TYPE, "isOwner", false, "IS_OWNER")
        const val Property OnlineStatus = Property(4, String.class, "onlineStatus", false, "ONLINE_STATUS")
        const val Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID")
        const val Property Title = Property(6, String.class, "title", false, "TITLE")
        const val Property UserID = Property(2, UUID.class, "userID", false, "USER_ID")
    }

    public GroupMemberDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public GroupMemberDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL ," + "'CONTRIBUTION' INTEGER NOT NULL ," + "'ONLINE_STATUS' TEXT NOT NULL ," + "'AGENT_POWERS' INTEGER NOT NULL ," + "'TITLE' TEXT NOT NULL ," + "'IS_OWNER' INTEGER NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_GroupMembers_GROUP_ID_REQUEST_ID ON GroupMembers" + " (GROUP_ID,REQUEST_ID);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMembers'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, groupMember: GroupMember) {
        sQLiteStatement.clearBindings()
        sQLiteStatement.bindString(1, groupMember.getGroupID().toString())
        sQLiteStatement.bindString(2, groupMember.getRequestID().toString())
        sQLiteStatement.bindString(3, groupMember.getUserID().toString())
        sQLiteStatement.bindLong(4, (Long) groupMember.getContribution())
        sQLiteStatement.bindString(5, groupMember.getOnlineStatus())
        sQLiteStatement.bindLong(6, groupMember.getAgentPowers())
        sQLiteStatement.bindString(7, groupMember.getTitle())
        sQLiteStatement.bindLong(8, groupMember.getIsOwner() ? 1 : 0)
    }

     public fun getKey(groupMember: GroupMember): Void {
        return null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): GroupMember {
        val z: Boolean = false
        val fromString: UUID = UUID.fromString(cursor.getString(i + 0))
        val fromString2: UUID = UUID.fromString(cursor.getString(i + 1))
        val fromString3: UUID = UUID.fromString(cursor.getString(i + 2))
        val i2: Int = cursor.getInt(i + 3)
        val string: String = cursor.getString(i + 4)
        val j: Long = cursor.getLong(i + 5)
        val string2: String = cursor.getString(i + 6)
        if (cursor.getShort(i + 7) != (Short) 0) {
            z = true
        }
        return GroupMember(fromString, fromString2, fromString3, i2, string, j, string2, z)
    }

    fun readEntity(cursor: Cursor, groupMember: GroupMember, i: Int) {
        val z: Boolean = false
        groupMember.setGroupID(UUID.fromString(cursor.getString(i + 0)))
        groupMember.setRequestID(UUID.fromString(cursor.getString(i + 1)))
        groupMember.setUserID(UUID.fromString(cursor.getString(i + 2)))
        groupMember.setContribution(cursor.getInt(i + 3))
        groupMember.setOnlineStatus(cursor.getString(i + 4))
        groupMember.setAgentPowers(cursor.getLong(i + 5))
        groupMember.setTitle(cursor.getString(i + 6))
        if (cursor.getShort(i + 7) != (Short) 0) {
            z = true
        }
        groupMember.setIsOwner(z)
    }

     public fun readKey(cursor: Cursor, i: Int): Void {
        return null
    }

     protected fun updateKeyAfterInsert(groupMember: GroupMember, j: Long): Void {
        return null
    }
}
