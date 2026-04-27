// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.internal;

import android.database.sqlite.SQLiteStatement;
<<<<<<<< HEAD:lumiya_decompiled_source/de/greenrobot/dao/internal/TableStatements.java
/* loaded from: classes.dex */
public class TableStatements {
========
import android.database.sqlite.SQLiteDatabase;

public class TableStatements
{
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/de/greenrobot/dao/internal/TableStatements.java
    private final String[] allColumns;
    private final SQLiteDatabase db;
    private SQLiteStatement deleteStatement;
    private SQLiteStatement insertOrReplaceStatement;
    private SQLiteStatement insertStatement;
    private final String[] pkColumns;
    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;
    private volatile String selectKeys;
    private final String tablename;
    private SQLiteStatement updateStatement;
    
    public TableStatements(final SQLiteDatabase db, final String tablename, final String[] allColumns, final String[] pkColumns) {
        this.db = db;
        this.tablename = tablename;
        this.allColumns = allColumns;
        this.pkColumns = pkColumns;
    }
    
    public SQLiteStatement getDeleteStatement() {
        if (this.deleteStatement == null) {
            this.deleteStatement = this.db.compileStatement(SqlUtils.createSqlDelete(this.tablename, this.pkColumns));
        }
        return this.deleteStatement;
    }
    
    public SQLiteStatement getInsertOrReplaceStatement() {
        if (this.insertOrReplaceStatement == null) {
            this.insertOrReplaceStatement = this.db.compileStatement(SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", this.tablename, this.allColumns));
        }
        return this.insertOrReplaceStatement;
    }
    
    public SQLiteStatement getInsertStatement() {
        if (this.insertStatement == null) {
            this.insertStatement = this.db.compileStatement(SqlUtils.createSqlInsert("INSERT INTO ", this.tablename, this.allColumns));
        }
        return this.insertStatement;
    }
    
    public String getSelectAll() {
        if (this.selectAll == null) {
            this.selectAll = SqlUtils.createSqlSelect(this.tablename, "T", this.allColumns);
        }
        return this.selectAll;
    }
    
    public String getSelectByKey() {
        if (this.selectByKey == null) {
            final StringBuilder sb = new StringBuilder(this.getSelectAll());
            sb.append("WHERE ");
            SqlUtils.appendColumnsEqValue(sb, "T", this.pkColumns);
            this.selectByKey = sb.toString();
        }
        return this.selectByKey;
    }
    
    public String getSelectByRowId() {
        if (this.selectByRowId == null) {
            this.selectByRowId = this.getSelectAll() + "WHERE ROWID=?";
        }
        return this.selectByRowId;
    }
    
    public String getSelectKeys() {
        if (this.selectKeys == null) {
            this.selectKeys = SqlUtils.createSqlSelect(this.tablename, "T", this.pkColumns);
        }
        return this.selectKeys;
    }
    
    public SQLiteStatement getUpdateStatement() {
        if (this.updateStatement == null) {
            this.updateStatement = this.db.compileStatement(SqlUtils.createSqlUpdate(this.tablename, this.allColumns, this.pkColumns));
        }
        return this.updateStatement;
    }
}
