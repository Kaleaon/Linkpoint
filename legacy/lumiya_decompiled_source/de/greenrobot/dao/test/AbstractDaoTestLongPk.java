package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoLog;
/* loaded from: classes.dex */
public abstract class AbstractDaoTestLongPk<D extends AbstractDao<T, Long>, T> extends AbstractDaoTestSinglePk<D, T, Long> {
    public AbstractDaoTestLongPk(Class<D> cls) {
        super(cls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.test.AbstractDaoTestSinglePk
    public Long createRandomPk() {
        return Long.valueOf(this.random.nextLong());
    }

    public void testAssignPk() {
        if (!this.daoAccess.isEntityUpdateable()) {
            DaoLog.d("Skipping testAssignPk for not updateable " + this.daoClass);
            return;
        }
        T createEntity = createEntity(null);
        if (createEntity == null) {
            DaoLog.d("Skipping testAssignPk for " + this.daoClass + " (createEntity returned null for null key)");
            return;
        }
        T createEntity2 = createEntity(null);
        this.dao.insert(createEntity);
        this.dao.insert(createEntity2);
        Long l = (Long) this.daoAccess.getKey(createEntity);
        assertNotNull(l);
        Long l2 = (Long) this.daoAccess.getKey(createEntity2);
        assertNotNull(l2);
        assertFalse(l.equals(l2));
        assertNotNull(this.dao.load(l));
        assertNotNull(this.dao.load(l2));
    }
}
