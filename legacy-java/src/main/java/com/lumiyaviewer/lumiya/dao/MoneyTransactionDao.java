package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Date;
import java.util.UUID;

/* loaded from: classes.dex */
public class MoneyTransactionDao extends AbstractDao<MoneyTransaction, Long> {
    public static final String TABLENAME = "MONEY_TRANSACTION";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Timestamp = new Property(1, Date.class, "timestamp", false, "TIMESTAMP");
        public static final Property AgentUUID = new Property(2, UUID.class, "agentUUID", false, "AGENT_UUID");
        public static final Property TransactionAmount = new Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT");
        public static final Property NewBalance = new Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE");
    }

    public MoneyTransactionDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public MoneyTransactionDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MONEY_TRANSACTION' ('_id' INTEGER PRIMARY KEY ,'TIMESTAMP' INTEGER NOT NULL ,'AGENT_UUID' TEXT,'TRANSACTION_AMOUNT' INTEGER NOT NULL ,'NEW_BALANCE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MONEY_TRANSACTION'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
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
        sQLiteStatement.bindLong(4, moneyTransaction.getTransactionAmount());
        sQLiteStatement.bindLong(5, moneyTransaction.getNewBalance());
    }

    @Override // de.greenrobot.dao.AbstractDao
    public Long getKey(MoneyTransaction moneyTransaction) {
        if (moneyTransaction != null) {
            return moneyTransaction.getId();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public boolean isEntityUpdateable() {
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.AbstractDao
    public MoneyTransaction readEntity(Cursor cursor, int i) {
        return new MoneyTransaction(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), new Date(cursor.getLong(i + 1)), cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2)), cursor.getInt(i + 3), cursor.getInt(i + 4));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, MoneyTransaction moneyTransaction, int i) {
        moneyTransaction.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        moneyTransaction.setTimestamp(new Date(cursor.getLong(i + 1)));
        moneyTransaction.setAgentUUID(cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2)));
        moneyTransaction.setTransactionAmount(cursor.getInt(i + 3));
        moneyTransaction.setNewBalance(cursor.getInt(i + 4));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.AbstractDao
    public Long readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i + 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public Long updateKeyAfterInsert(MoneyTransaction moneyTransaction, long j) {
        moneyTransaction.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
