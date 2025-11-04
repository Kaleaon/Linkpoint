// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.Date;
import android.database.Cursor;
import java.util.UUID;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class MoneyTransactionDao extends AbstractDao<MoneyTransaction, Long>
{
    public static final String TABLENAME = "MONEY_TRANSACTION";
    
    public MoneyTransactionDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public MoneyTransactionDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String str;
        if (b) {
            str = "IF NOT EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'MONEY_TRANSACTION' (" + "'_id' INTEGER PRIMARY KEY ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'AGENT_UUID' TEXT," + "'TRANSACTION_AMOUNT' INTEGER NOT NULL ," + "'NEW_BALANCE' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'MONEY_TRANSACTION'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final MoneyTransaction moneyTransaction) {
        sqLiteStatement.clearBindings();
        final Long id = moneyTransaction.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        sqLiteStatement.bindLong(2, moneyTransaction.getTimestamp().getTime());
        final UUID agentUUID = moneyTransaction.getAgentUUID();
        if (agentUUID != null) {
            sqLiteStatement.bindString(3, agentUUID.toString());
        }
        sqLiteStatement.bindLong(4, (long)moneyTransaction.getTransactionAmount());
        sqLiteStatement.bindLong(5, (long)moneyTransaction.getNewBalance());
    }
    
    public Long getKey(final MoneyTransaction moneyTransaction) {
        if (moneyTransaction != null) {
            return moneyTransaction.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public MoneyTransaction readEntity(final Cursor cursor, final int n) {
        UUID fromString = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        final Date date = new Date(cursor.getLong(n + 1));
        if (!cursor.isNull(n + 2)) {
            fromString = UUID.fromString(cursor.getString(n + 2));
        }
        return new MoneyTransaction(value, date, fromString, cursor.getInt(n + 3), cursor.getInt(n + 4));
    }
    
    public void readEntity(final Cursor cursor, final MoneyTransaction moneyTransaction, final int n) {
        final UUID uuid = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        moneyTransaction.setId(value);
        moneyTransaction.setTimestamp(new Date(cursor.getLong(n + 1)));
        UUID fromString;
        if (cursor.isNull(n + 2)) {
            fromString = uuid;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 2));
        }
        moneyTransaction.setAgentUUID(fromString);
        moneyTransaction.setTransactionAmount(cursor.getInt(n + 3));
        moneyTransaction.setNewBalance(cursor.getInt(n + 4));
    }
    
    public Long readKey(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return value;
    }
    
    @Override
    protected Long updateKeyAfterInsert(final MoneyTransaction moneyTransaction, final long n) {
        moneyTransaction.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property AgentUUID;
        public static final Property Id;
        public static final Property NewBalance;
        public static final Property Timestamp;
        public static final Property TransactionAmount;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            Timestamp = new Property(1, Date.class, "timestamp", false, "TIMESTAMP");
            AgentUUID = new Property(2, UUID.class, "agentUUID", false, "AGENT_UUID");
            TransactionAmount = new Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT");
            NewBalance = new Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE");
        }
    }
}
