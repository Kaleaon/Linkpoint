// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.AbstractDao;

public abstract class AbstractDaoTestLongPk<D extends AbstractDao<T, Long>, T> extends AbstractDaoTestSinglePk<D, T, Long>
{
    public AbstractDaoTestLongPk(final Class<D> clazz) {
        super(clazz);
    }
    
    @Override
    protected Long createRandomPk() {
        return this.random.nextLong();
    }
    
    public void testAssignPk() {
        if (!this.daoAccess.isEntityUpdateable()) {
            DaoLog.d("Skipping testAssignPk for not updateable " + this.daoClass);
        }
        else {
            final Object entity = this.createEntity(null);
            if (entity == null) {
                DaoLog.d("Skipping testAssignPk for " + this.daoClass + " (createEntity returned null for null key)");
            }
            else {
                final Object entity2 = this.createEntity(null);
                ((AbstractDao<T, K>)this.dao).insert((T)entity);
                ((AbstractDao<T, K>)this.dao).insert((T)entity2);
                final Long n = (Long)this.daoAccess.getKey((T)entity);
                assertNotNull((Object)n);
                final Long obj = (Long)this.daoAccess.getKey((T)entity2);
                assertNotNull((Object)obj);
                assertFalse(n.equals(obj));
                assertNotNull(((AbstractDao<Object, K>)this.dao).load((K)n));
                assertNotNull(((AbstractDao<Object, K>)this.dao).load((K)obj));
            }
        }
    }
}
