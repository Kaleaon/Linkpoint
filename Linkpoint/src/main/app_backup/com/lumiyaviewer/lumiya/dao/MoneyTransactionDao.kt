package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.Date
import java.util.UUID

class MoneyTransactionDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<MoneyTransaction, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "MONEY_TRANSACTION"

        object Properties {
            @JvmField val AgentUUID = Property(2, String::class.java, "agentUUID", false, "AGENT_UUID")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val NewBalance = Property(4, Int::class.java, "newBalance", false, "NEW_BALANCE")
            @JvmField val Timestamp = Property(1, Long::class.java, "timestamp", false, "TIMESTAMP")
            @JvmField val TransactionAmount = Property(3, Int::class.java, "transactionAmount", false, "TRANSACTION_AMOUNT")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'MONEY_TRANSACTION' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'TIMESTAMP' INTEGER NOT NULL ," +
                    "'AGENT_UUID' TEXT," +
                    "'TRANSACTION_AMOUNT' INTEGER NOT NULL ," +
                    "'NEW_BALANCE' INTEGER NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'MONEY_TRANSACTION'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: MoneyTransaction) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        stmt.bindLong(2, entity.timestamp?.time ?: 0L)
        
        val agentUUID = entity.agentUUID
        if (agentUUID != null) {
            stmt.bindString(3, agentUUID.toString())
        }
        stmt.bindLong(4, entity.transactionAmount.toLong())
        stmt.bindLong(5, entity.newBalance.toLong())
    }

    override fun getKey(entity: MoneyTransaction?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): MoneyTransaction {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val timestamp = Date(cursor.getLong(offset + 1))
        val uuidStr = cursor.getString(offset + 2)
        val agentUUID = if (cursor.isNull(offset + 2) || uuidStr == null) null else UUID.fromString(uuidStr)
        val transactionAmount = cursor.getInt(offset + 3)
        val newBalance = cursor.getInt(offset + 4)
        return MoneyTransaction(id, timestamp, agentUUID, transactionAmount, newBalance)
    }

    override fun readEntity(cursor: Cursor, entity: MoneyTransaction, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.timestamp = Date(cursor.getLong(offset + 1))
        val uuidStr = cursor.getString(offset + 2)
        entity.agentUUID = if (cursor.isNull(offset + 2) || uuidStr == null) null else UUID.fromString(uuidStr)
        entity.transactionAmount = cursor.getInt(offset + 3)
        entity.newBalance = cursor.getInt(offset + 4)
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: MoneyTransaction, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
