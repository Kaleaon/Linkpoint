// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import de.greenrobot.dao.internal.SqlUtils;
import java.util.Collection;
import de.greenrobot.dao.query.WhereCondition;

public class Property
{
    public final String columnName;
    public final String name;
    public final int ordinal;
    public final boolean primaryKey;
    public final Class<?> type;
    
    public Property(final int ordinal, final Class<?> type, final String name, final boolean primaryKey, final String columnName) {
        this.ordinal = ordinal;
        this.type = type;
        this.name = name;
        this.primaryKey = primaryKey;
        this.columnName = columnName;
    }
    
    public WhereCondition between(final Object o, final Object o2) {
        return new WhereCondition.PropertyCondition(this, " BETWEEN ? AND ?", new Object[] { o, o2 });
    }
    
    public WhereCondition eq(final Object o) {
        return new WhereCondition.PropertyCondition(this, "=?", o);
    }
    
    public WhereCondition ge(final Object o) {
        return new WhereCondition.PropertyCondition(this, ">=?", o);
    }
    
    public WhereCondition gt(final Object o) {
        return new WhereCondition.PropertyCondition(this, ">?", o);
    }
    
    public WhereCondition in(final Collection<?> collection) {
        return this.in(collection.toArray());
    }
    
    public WhereCondition in(final Object... array) {
        final StringBuilder sb = new StringBuilder(" IN (");
        SqlUtils.appendPlaceholders(sb, array.length).append(')');
        return new WhereCondition.PropertyCondition(this, sb.toString(), array);
    }
    
    public WhereCondition isNotNull() {
        return new WhereCondition.PropertyCondition(this, " IS NOT NULL");
    }
    
    public WhereCondition isNull() {
        return new WhereCondition.PropertyCondition(this, " IS NULL");
    }
    
    public WhereCondition le(final Object o) {
        return new WhereCondition.PropertyCondition(this, "<=?", o);
    }
    
    public WhereCondition like(final String s) {
        return new WhereCondition.PropertyCondition(this, " LIKE ?", s);
    }
    
    public WhereCondition lt(final Object o) {
        return new WhereCondition.PropertyCondition(this, "<?", o);
    }
    
    public WhereCondition notEq(final Object o) {
        return new WhereCondition.PropertyCondition(this, "<>?", o);
    }
    
    public WhereCondition notIn(final Collection<?> collection) {
        return this.notIn(collection.toArray());
    }
    
    public WhereCondition notIn(final Object... array) {
        final StringBuilder sb = new StringBuilder(" NOT IN (");
        SqlUtils.appendPlaceholders(sb, array.length).append(')');
        return new WhereCondition.PropertyCondition(this, sb.toString(), array);
    }
}
