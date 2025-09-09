package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;

public abstract class AbstractDaoTestStringPk<D extends AbstractDao<T, String>, T> extends AbstractDaoTestSinglePk<D, T, String> {
    public AbstractDaoTestStringPk(Class<D> cls) {
        super(cls);
    }

    /* access modifiers changed from: protected */
    public String createRandomPk() {
        int nextInt = this.random.nextInt(30) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextInt; i++) {
            sb.append((char) (this.random.nextInt(25) + 97));
        }
        return sb.toString();
    }
}
