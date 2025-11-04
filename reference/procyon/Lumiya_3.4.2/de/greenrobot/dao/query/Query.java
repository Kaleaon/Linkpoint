// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import de.greenrobot.dao.DaoException;
import java.util.List;
import de.greenrobot.dao.AbstractDao;

public class Query<T> extends AbstractQuery<T>
{
    private final int limitPosition;
    private final int offsetPosition;
    private final QueryData<T> queryData;
    
    private Query(final QueryData<T> queryData, final AbstractDao<T, ?> abstractDao, final String s, final String[] array, final int limitPosition, final int offsetPosition) {
        super(abstractDao, s, array);
        this.queryData = queryData;
        this.limitPosition = limitPosition;
        this.offsetPosition = offsetPosition;
    }
    
    static <T2> Query<T2> create(final AbstractDao<T2, ?> abstractDao, final String s, final Object[] array, final int n, final int n2) {
        return (Query)new QueryData((AbstractDao<Object, ?>)abstractDao, s, AbstractQuery.toStringArray(array), n, n2).forCurrentThread();
    }
    
    public static <T2> Query<T2> internalCreate(final AbstractDao<T2, ?> abstractDao, final String s, final Object[] array) {
        return create(abstractDao, s, array, -1, -1);
    }
    
    public Query<T> forCurrentThread() {
        return (Query)this.queryData.forCurrentThread((Query<T2>)this);
    }
    
    public List<T> list() {
        this.checkThread();
        return this.daoAccess.loadAllAndCloseCursor(this.dao.getDatabase().rawQuery(this.sql, this.parameters));
    }
    
    public CloseableListIterator<T> listIterator() {
        return this.listLazyUncached().listIteratorAutoClose();
    }
    
    public LazyList<T> listLazy() {
        this.checkThread();
        return new LazyList<T>(this.daoAccess, this.dao.getDatabase().rawQuery(this.sql, this.parameters), true);
    }
    
    public LazyList<T> listLazyUncached() {
        this.checkThread();
        return new LazyList<T>(this.daoAccess, this.dao.getDatabase().rawQuery(this.sql, this.parameters), false);
    }
    
    public void setLimit(final int i) {
        this.checkThread();
        if (this.limitPosition != -1) {
            this.parameters[this.limitPosition] = Integer.toString(i);
            return;
        }
        throw new IllegalStateException("Limit must be set with QueryBuilder before it can be used here");
    }
    
    public void setOffset(final int i) {
        this.checkThread();
        if (this.offsetPosition != -1) {
            this.parameters[this.offsetPosition] = Integer.toString(i);
            return;
        }
        throw new IllegalStateException("Offset must be set with QueryBuilder before it can be used here");
    }
    
    @Override
    public void setParameter(final int i, final Object o) {
        if (i >= 0 && (i == this.limitPosition || i == this.offsetPosition)) {
            throw new IllegalArgumentException("Illegal parameter index: " + i);
        }
        super.setParameter(i, o);
    }
    
    public T unique() {
        this.checkThread();
        return this.daoAccess.loadUniqueAndCloseCursor(this.dao.getDatabase().rawQuery(this.sql, this.parameters));
    }
    
    public T uniqueOrThrow() {
        final T unique = this.unique();
        if (unique != null) {
            return unique;
        }
        throw new DaoException("No entity found for query");
    }
    
    private static final class QueryData<T2> extends AbstractQueryData<T2, Query<T2>>
    {
        private final int limitPosition;
        private final int offsetPosition;
        
        QueryData(final AbstractDao<T2, ?> abstractDao, final String s, final String[] array, final int limitPosition, final int offsetPosition) {
            super(abstractDao, s, array);
            this.limitPosition = limitPosition;
            this.offsetPosition = offsetPosition;
        }
        
        @Override
        protected Query<T2> createQuery() {
            return new Query<T2>(this, this.dao, this.sql, this.initialValues.clone(), this.limitPosition, this.offsetPosition, null);
        }
    }
}
