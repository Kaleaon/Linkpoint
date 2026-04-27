// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import android.database.Cursor;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.AbstractDao;

public class CountQuery<T> extends AbstractQuery<T>
{
    private final QueryData<T> queryData;
    
    private CountQuery(final QueryData<T> queryData, final AbstractDao<T, ?> abstractDao, final String s, final String[] array) {
        super(abstractDao, s, array);
        this.queryData = queryData;
    }
    
    static <T2> CountQuery<T2> create(final AbstractDao<T2, ?> abstractDao, final String s, final Object[] array) {
        return (CountQuery)new QueryData((AbstractDao)abstractDao, s, AbstractQuery.toStringArray(array)).forCurrentThread();
    }
    
    public long count() {
        this.checkThread();
        final Cursor rawQuery = this.dao.getDatabase().rawQuery(this.sql, this.parameters);
        Label_0135: {
            try {
                if (!rawQuery.moveToNext()) {
                    throw new DaoException("No result for count");
                }
                if (rawQuery.isLast()) {
                    if (rawQuery.getColumnCount() == 1) {
                        final long long1 = rawQuery.getLong(0);
                        rawQuery.close();
                        return long1;
                    }
                    break Label_0135;
                }
            }
            finally {
                rawQuery.close();
            }
            throw new DaoException("Unexpected row count: " + rawQuery.getCount());
        }
        new DaoException("Unexpected column count: " + rawQuery.getColumnCount());
    }
    
    public CountQuery<T> forCurrentThread() {
        return (CountQuery)this.queryData.forCurrentThread((CountQuery<T2>)this);
    }
    
    private static final class QueryData<T2> extends AbstractQueryData<T2, CountQuery<T2>>
    {
        private QueryData(final AbstractDao<T2, ?> abstractDao, final String s, final String[] array) {
            super(abstractDao, s, array);
        }
        
        @Override
        protected CountQuery<T2> createQuery() {
            return new CountQuery<T2>(this, this.dao, this.sql, this.initialValues.clone(), null);
        }
    }
}
