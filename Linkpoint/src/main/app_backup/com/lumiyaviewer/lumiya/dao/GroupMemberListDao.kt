package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupMemberListDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<GroupMemberList, UUID>(config, daoSession) {

    companion object {
        const val TABLENAME = "GroupMemberLists"

        object Properties {
            @JvmField val GroupID = Property(0, String::class.java, "groupID", true, "GROUP_ID")
            @JvmField val RequestID = Property(1, String::class.java, "requestID", false, "REQUEST_ID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'GroupMemberLists' (" +
                    "'GROUP_ID' TEXT PRIMARY KEY ," +
                    "'REQUEST_ID' TEXT NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'GroupMemberLists'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: GroupMemberList) {
        stmt.clearBindings()
        val groupID = entity.groupID
        stmt.bindString(1, groupID.toString())
        stmt.bindString(2, entity.requestID.toString())
    }

    override fun getKey(entity: GroupMemberList?): UUID? {
        return entity?.groupID
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): GroupMemberList {
        val groupIDStr = cursor.getString(offset + 0)
        // Ensure we return a valid UUID, using a zero UUID if null to satisfy non-null requirement of data class
        val groupID = if (cursor.isNull(offset + 0) || groupIDStr == null) UUID(0, 0) else UUID.fromString(groupIDStr)
        val requestID = if (cursor.isNull(offset + 1)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 1))
        return GroupMemberList(groupID, requestID)
    }

    override fun readEntity(cursor: Cursor, entity: GroupMemberList, offset: Int) {
        val groupIDStr = cursor.getString(offset + 0)
        entity.groupID = if (cursor.isNull(offset + 0) || groupIDStr == null) UUID(0, 0) else UUID.fromString(groupIDStr)
        entity.requestID = if (cursor.isNull(offset + 1)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 1))
    }

    override fun readKey(cursor: Cursor, offset: Int): UUID? {
        val groupIDStr = cursor.getString(offset + 0)
        return if (cursor.isNull(offset + 0) || groupIDStr == null) null else UUID.fromString(groupIDStr)
    }

    override fun updateKeyAfterInsert(entity: GroupMemberList, rowId: Long): UUID? {
        return entity.groupID
    }
}
