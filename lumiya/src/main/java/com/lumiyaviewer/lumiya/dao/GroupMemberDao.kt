package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupMemberDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<GroupMember, Void>(config, daoSession) {

    companion object {
        const val TABLENAME = "GroupMembers"

        object Properties {
            @JvmField val AgentPowers = Property(5, Long::class.java, "agentPowers", false, "AGENT_POWERS")
            @JvmField val Contribution = Property(3, Int::class.java, "contribution", false, "CONTRIBUTION")
            @JvmField val GroupID = Property(0, String::class.java, "groupID", false, "GROUP_ID")
            @JvmField val IsOwner = Property(7, Boolean::class.java, "isOwner", false, "IS_OWNER")
            @JvmField val OnlineStatus = Property(4, String::class.java, "onlineStatus", false, "ONLINE_STATUS")
            @JvmField val RequestID = Property(1, String::class.java, "requestID", false, "REQUEST_ID")
            @JvmField val Title = Property(6, String::class.java, "title", false, "TITLE")
            @JvmField val UserID = Property(2, String::class.java, "userID", false, "USER_ID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'GroupMembers' (" +
                    "'GROUP_ID' TEXT NOT NULL ," +
                    "'REQUEST_ID' TEXT NOT NULL ," +
                    "'USER_ID' TEXT NOT NULL ," +
                    "'CONTRIBUTION' INTEGER NOT NULL ," +
                    "'ONLINE_STATUS' TEXT NOT NULL ," +
                    "'AGENT_POWERS' INTEGER NOT NULL ," +
                    "'TITLE' TEXT NOT NULL ," +
                    "'IS_OWNER' INTEGER NOT NULL );")
            db.execSQL("CREATE INDEX ${constraint}IDX_GroupMembers_GROUP_ID_REQUEST_ID ON GroupMembers (GROUP_ID,REQUEST_ID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'GroupMembers'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: GroupMember) {
        stmt.clearBindings()
        stmt.bindString(1, entity.groupID.toString())
        stmt.bindString(2, entity.requestID.toString())
        stmt.bindString(3, entity.userID.toString())
        stmt.bindLong(4, entity.contribution.toLong())
        stmt.bindString(5, entity.onlineStatus)
        stmt.bindLong(6, entity.agentPowers)
        stmt.bindString(7, entity.title)
        stmt.bindLong(8, if (entity.isOwner) 1L else 0L)
    }

    override fun getKey(entity: GroupMember?): Void? {
        return null
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): GroupMember {
        return GroupMember(
            UUID.fromString(cursor.getString(offset + 0)),
            UUID.fromString(cursor.getString(offset + 1)),
            UUID.fromString(cursor.getString(offset + 2)),
            cursor.getInt(offset + 3),
            cursor.getString(offset + 4),
            cursor.getLong(offset + 5),
            cursor.getString(offset + 6),
            cursor.getShort(offset + 7).toInt() != 0
        )
    }

    override fun readEntity(cursor: Cursor, entity: GroupMember, offset: Int) {
        entity.groupID = UUID.fromString(cursor.getString(offset + 0))
        entity.requestID = UUID.fromString(cursor.getString(offset + 1))
        entity.userID = UUID.fromString(cursor.getString(offset + 2))
        entity.contribution = cursor.getInt(offset + 3)
        entity.onlineStatus = cursor.getString(offset + 4)
        entity.agentPowers = cursor.getLong(offset + 5)
        entity.title = cursor.getString(offset + 6)
        entity.isOwner = cursor.getShort(offset + 7).toInt() != 0
    }

    override fun readKey(cursor: Cursor, offset: Int): Void? {
        return null
    }

    override fun updateKeyAfterInsert(entity: GroupMember, rowId: Long): Void? {
        return null
    }
}
