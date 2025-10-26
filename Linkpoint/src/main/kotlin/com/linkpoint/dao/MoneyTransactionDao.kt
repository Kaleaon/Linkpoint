package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.Date
import java.util.UUID

class MoneyTransactionDao : AbstractDao()<MoneyTransaction, Long> {
    const val TABLENAME: String = "MONEY_TRANSACTION"

    @JvmStatic
    class Properties {
        const val Property AgentUUID = Property(2, UUID.class, "agentUUID", false, "AGENT_UUID")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property NewBalance = Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE")
        const val Property Timestamp = Property(1, Date.class, "timestamp", false, "TIMESTAMP")
        const val Property TransactionAmount = Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT")
    }

    public MoneyTransactionDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public MoneyTransactionDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MONEY_TRANSACTION' (" + "'_id' INTEGER PRIMARY KEY ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'AGENT_UUID' TEXT," + "'TRANSACTION_AMOUNT' INTEGER NOT NULL ," + "'NEW_BALANCE' INTEGER NOT NULL );")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MONEY_TRANSACTION'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, moneyTransaction: MoneyTransaction) {
        sQLiteStatement.clearBindings()
        val id: Long = moneyTransaction.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, moneyTransaction.getTimestamp().getTime())
        val agentUUID: UUID = moneyTransaction.getAgentUUID()
        if (agentUUID != null) {
            sQLiteStatement.bindString(3, agentUUID.toString())
        }
        sQLiteStatement.bindLong(4, (Long) moneyTransaction.getTransactionAmount())
        sQLiteStatement.bindLong(5, (Long) moneyTransaction.getNewBalance())
    }

     public fun getKey(moneyTransaction: MoneyTransaction): Long {
        return moneyTransaction != null ? moneyTransaction.getId() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): MoneyTransaction {
        val uuid: UUID = null
        val valueOf: Long = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        val date: Date = Date(cursor.getLong(i + 1))
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2))
        }
        return MoneyTransaction(valueOf, date, uuid, cursor.getInt(i + 3), cursor.getInt(i + 4))
    }

    fun readEntity(cursor: Cursor, moneyTransaction: MoneyTransaction, i: Int) {
        val uuid: UUID = null
        moneyTransaction.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        moneyTransaction.setTimestamp(Date(cursor.getLong(i + 1)))
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2))
        }
        moneyTransaction.setAgentUUID(uuid)
        moneyTransaction.setTransactionAmount(cursor.getInt(i + 3))
        moneyTransaction.setNewBalance(cursor.getInt(i + 4))
    }

     public fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

     protected fun updateKeyAfterInsert(moneyTransaction: MoneyTransaction, j: Long): Long {
        moneyTransaction.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
