// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.internal;

import de.greenrobot.dao.DaoException;

public class SqlUtils
{
    public static StringBuilder appendColumn(final StringBuilder sb, final String str) {
        sb.append('\'').append(str).append('\'');
        return sb;
    }
    
    public static StringBuilder appendColumn(final StringBuilder sb, final String str, final String str2) {
        sb.append(str).append(".'").append(str2).append('\'');
        return sb;
    }
    
    public static StringBuilder appendColumns(final StringBuilder sb, final String s, final String[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            appendColumn(sb, s, array[i]);
            if (i < length - 1) {
                sb.append(',');
            }
        }
        return sb;
    }
    
    public static StringBuilder appendColumns(final StringBuilder sb, final String[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            sb.append('\'').append(array[i]).append('\'');
            if (i < length - 1) {
                sb.append(',');
            }
        }
        return sb;
    }
    
    public static StringBuilder appendColumnsEqValue(final StringBuilder sb, final String s, final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            appendColumn(sb, s, array[i]).append("=?");
            if (i < array.length - 1) {
                sb.append(',');
            }
        }
        return sb;
    }
    
    public static StringBuilder appendColumnsEqualPlaceholders(final StringBuilder sb, final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            appendColumn(sb, array[i]).append("=?");
            if (i < array.length - 1) {
                sb.append(',');
            }
        }
        return sb;
    }
    
    public static StringBuilder appendPlaceholders(final StringBuilder sb, final int n) {
        for (int i = 0; i < n; ++i) {
            if (i >= n - 1) {
                sb.append('?');
            }
            else {
                sb.append("?,");
            }
        }
        return sb;
    }
    
    public static String createSqlDelete(final String str, final String[] array) {
        final StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(str);
        if (array != null && array.length > 0) {
            sb.append(" WHERE ");
            appendColumnsEqValue(sb, str, array);
        }
        return sb.toString();
    }
    
    public static String createSqlInsert(final String str, final String str2, final String[] array) {
        final StringBuilder sb = new StringBuilder(str);
        sb.append(str2).append(" (");
        appendColumns(sb, array);
        sb.append(") VALUES (");
        appendPlaceholders(sb, array.length);
        sb.append(')');
        return sb.toString();
    }
    
    public static String createSqlSelect(final String str, final String str2, final String[] array) {
        final StringBuilder sb = new StringBuilder("SELECT ");
        if (str2 != null && str2.length() >= 0) {
            appendColumns(sb, str2, array).append(" FROM ");
            sb.append(str).append(' ').append(str2).append(' ');
            return sb.toString();
        }
        throw new DaoException("Table alias required");
    }
    
    public static String createSqlSelectCountStar(final String str, final String str2) {
        final StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ");
        sb.append(str).append(' ');
        if (str2 != null) {
            sb.append(str2).append(' ');
        }
        return sb.toString();
    }
    
    public static String createSqlUpdate(final String str, final String[] array, final String[] array2) {
        final StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(str).append(" SET ");
        appendColumnsEqualPlaceholders(sb, array);
        sb.append(" WHERE ");
        appendColumnsEqValue(sb, str, array2);
        return sb.toString();
    }
}
