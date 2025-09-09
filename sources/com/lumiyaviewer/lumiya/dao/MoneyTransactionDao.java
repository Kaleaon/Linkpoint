package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Date;
import java.util.UUID;

public class MoneyTransactionDao extends AbstractDao<MoneyTransaction, Long> {
    public static final String TABLENAME = "MONEY_TRANSACTION";

    public static class Properties {
        public static final Property AgentUUID = new Property(2, UUID.class, "agentUUID", false, "AGENT_UUID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property NewBalance = new Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE");
        public static final Property Timestamp = new Property(1, Date.class, "timestamp", false, "TIMESTAMP");
        public static final Property TransactionAmount = new Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT");
    }

    public MoneyTransactionDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public MoneyTransactionDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MONEY_TRANSACTION' (" + "'_id' INTEGER PRIMARY KEY ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'AGENT_UUID' TEXT," + "'TRANSACTION_AMOUNT' INTEGER NOT NULL ," + "'NEW_BALANCE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MONEY_TRANSACTION'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, MoneyTransaction moneyTransaction) {
        sQLiteStatement.clearBindings();
        Long id = moneyTransaction.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        sQLiteStatement.bindLong(2, moneyTransaction.getTimestamp().getTime());
        UUID agentUUID = moneyTransaction.getAgentUUID();
        if (agentUUID != null) {
            sQLiteStatement.bindString(3, agentUUID.toString());
        }
        sQLiteStatement.bindLong(4, (long) moneyTransaction.getTransactionAmount());
        sQLiteStatement.bindLong(5, (long) moneyTransaction.getNewBalance());
    }

    public Long getKey(MoneyTransaction moneyTransaction) {
        if (moneyTransaction != null) {
            return moneyTransaction.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public MoneyTransaction readEntity(Cursor cursor, int i) {
        UUID uuid = null;
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        Date date = new Date(cursor.getLong(i + 1));
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2));
        }
        return new MoneyTransaction(valueOf, date, uuid, cursor.getInt(i + 3), cursor.getInt(i + 4));
    }

    public void readEntity(Cursor cursor, MoneyTransaction moneyTransaction, int i) {
        UUID uuid = null;
        moneyTransaction.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        moneyTransaction.setTimestamp(new Date(cursor.getLong(i + 1)));
        if (!cursor.isNull(i + 2)) {
            uuid = UUID.fromString(cursor.getString(i + 2));
        }
        moneyTransaction.setAgentUUID(uuid);
        moneyTransaction.setTransactionAmount(cursor.getInt(i + 3));
        moneyTransaction.setNewBalance(cursor.getInt(i + 4));
    }

    public Long readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i + 0));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(MoneyTransaction moneyTransaction, long j) {
        moneyTransaction.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
