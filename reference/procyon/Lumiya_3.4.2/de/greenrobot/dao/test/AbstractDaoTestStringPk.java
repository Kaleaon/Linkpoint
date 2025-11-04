// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;

public abstract class AbstractDaoTestStringPk<D extends AbstractDao<T, String>, T> extends AbstractDaoTestSinglePk<D, T, String>
{
    public AbstractDaoTestStringPk(final Class<D> clazz) {
        super(clazz);
    }
    
    @Override
    protected String createRandomPk() {
        final int nextInt = this.random.nextInt(30);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextInt + 1; ++i) {
            sb.append((char)(this.random.nextInt(25) + 97));
        }
        return sb.toString();
    }
}
