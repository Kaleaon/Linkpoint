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
    const val String TABLENAME = "MONEY_TRANSACTION"

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
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MONEY_TRANSACTION' (" + "'_id' INTEGER PRIMARY KEY ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'AGENT_UUID' TEXT," + "'TRANSACTION_AMOUNT' INTEGER NOT NULL ," + "'NEW_BALANCE' INTEGER NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MONEY_TRANSACTION'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, MoneyTransaction moneyTransaction) {
        sQLiteStatement.clearBindings()
        Long id = moneyTransaction.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, moneyTransaction.getTimestamp().getTime())
        UUID agentUUID = moneyTransaction.getAgentUUID()
        if (agentUUID != null) {
            sQLiteStatement.bindString(3, agentUUID.toString())
        }
        sQLiteStatement.bindLong(4, (Long) moneyTransaction.getTransactionAmount())
        sQLiteStatement.bindLong(5, (Long) moneyTransaction.getNewBalance())
    }

    public Long getKey(MoneyTransaction moneyTransaction) {
        return moneyTransaction != null ? moneyTransaction.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public MoneyTransaction readEntity(Cursor cursor, Int i) {
        UUID uuid = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        Date date = Date(cursor.getLong(i + 1))
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2))
        }
        return MoneyTransaction(valueOf, date, uuid, cursor.getInt(i + 3), cursor.getInt(i + 4))
    }

    public Unit readEntity(Cursor cursor, MoneyTransaction moneyTransaction, Int i) {
        UUID uuid = null
        moneyTransaction.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        moneyTransaction.setTimestamp(Date(cursor.getLong(i + 1)))
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2))
        }
        moneyTransaction.setAgentUUID(uuid)
        moneyTransaction.setTransactionAmount(cursor.getInt(i + 3))
        moneyTransaction.setNewBalance(cursor.getInt(i + 4))
    }

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(MoneyTransaction moneyTransaction, Long j) {
        moneyTransaction.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
