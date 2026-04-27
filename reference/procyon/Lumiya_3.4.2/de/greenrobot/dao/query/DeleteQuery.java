// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;

public class DeleteQuery<T> extends AbstractQuery<T>
{
    private final QueryData<T> queryData;
    
    private DeleteQuery(final QueryData<T> queryData, final AbstractDao<T, ?> abstractDao, final String s, final String[] array) {
        super(abstractDao, s, array);
        this.queryData = queryData;
    }
    
    static <T2> DeleteQuery<T2> create(final AbstractDao<T2, ?> abstractDao, final String s, final Object[] array) {
        return (DeleteQuery)new QueryData((AbstractDao)abstractDao, s, AbstractQuery.toStringArray(array)).forCurrentThread();
    }
    
    public void executeDeleteWithoutDetachingEntities() {
        this.checkThread();
        final SQLiteDatabase database = this.dao.getDatabase();
        Label_0050: {
            if (database.isDbLockedByCurrentThread()) {
                break Label_0050;
            }
            database.beginTransaction();
            try {
                this.dao.getDatabase().execSQL(this.sql, (Object[])this.parameters);
                database.setTransactionSuccessful();
                return;
                this.dao.getDatabase().execSQL(this.sql, (Object[])this.parameters);
            }
            finally {
                database.endTransaction();
            }
        }
    }
    
    public DeleteQuery<T> forCurrentThread() {
        return (DeleteQuery)this.queryData.forCurrentThread((DeleteQuery<T2>)this);
    }
    
    private static final class QueryData<T2> extends AbstractQueryData<T2, DeleteQuery<T2>>
    {
        private QueryData(final AbstractDao<T2, ?> abstractDao, final String s, final String[] array) {
            super(abstractDao, s, array);
        }
        
        @Override
        protected DeleteQuery<T2> createQuery() {
            return new DeleteQuery<T2>(this, this.dao, this.sql, this.initialValues.clone(), null);
        }
    }
}
