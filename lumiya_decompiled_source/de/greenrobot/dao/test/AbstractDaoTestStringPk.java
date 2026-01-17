package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;
/* loaded from: classes.dex */
public abstract class AbstractDaoTestStringPk<D extends AbstractDao<T, String>, T> extends AbstractDaoTestSinglePk<D, T, String> {
    public AbstractDaoTestStringPk(Class<D> cls) {
        super(cls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.test.AbstractDaoTestSinglePk
    public String createRandomPk() {
        int nextInt = this.random.nextInt(30) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextInt; i++) {
            sb.append((char) (this.random.nextInt(25) + 97));
        }
        return sb.toString();
    }
}
