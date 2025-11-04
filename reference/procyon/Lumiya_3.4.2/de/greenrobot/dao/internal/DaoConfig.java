// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.internal;

import de.greenrobot.dao.identityscope.IdentityScopeLong;
import de.greenrobot.dao.identityscope.IdentityScopeObject;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import java.util.Iterator;
import java.lang.reflect.Field;
import de.greenrobot.dao.DaoException;
import java.util.ArrayList;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;
import android.database.sqlite.SQLiteDatabase;

public final class DaoConfig implements Cloneable
{
    public final String[] allColumns;
    public final SQLiteDatabase db;
    private IdentityScope<?, ?> identityScope;
    public final boolean keyIsNumeric;
    public final String[] nonPkColumns;
    public final String[] pkColumns;
    public final Property pkProperty;
    public final Property[] properties;
    public final TableStatements statements;
    public final String tablename;
    
    public DaoConfig(final SQLiteDatabase db, final Class<? extends AbstractDao<?, ?>> clazz) {
        while (true) {
            this.db = db;
            try {
                this.tablename = (String)clazz.getField("TABLENAME").get(null);
                final Property[] reflectProperties = reflectProperties(clazz);
                this.properties = reflectProperties;
                this.allColumns = new String[reflectProperties.length];
                final ArrayList list = new ArrayList();
                final ArrayList list2 = new ArrayList();
                int i = 0;
                Property pkProperty = null;
                while (i < reflectProperties.length) {
                    final Property property = reflectProperties[i];
                    final String columnName = property.columnName;
                    this.allColumns[i] = columnName;
                    if (!property.primaryKey) {
                        list2.add(columnName);
                    }
                    else {
                        list.add(columnName);
                        pkProperty = property;
                    }
                    ++i;
                }
                this.nonPkColumns = (String[])list2.toArray(new String[list2.size()]);
                this.pkColumns = (String[])list.toArray(new String[list.size()]);
                if (this.pkColumns.length != 1) {
                    pkProperty = null;
                }
                this.pkProperty = pkProperty;
                this.statements = new TableStatements(db, this.tablename, this.allColumns, this.pkColumns);
                if (this.pkProperty == null) {
                    this.keyIsNumeric = false;
                    return;
                }
            }
            catch (final Exception ex) {
                throw new DaoException("Could not init DAOConfig", ex);
            }
            final Class<?> type = this.pkProperty.type;
            this.keyIsNumeric = (type.equals(Long.TYPE) || type.equals(Long.class) || type.equals(Integer.TYPE) || type.equals(Integer.class) || type.equals(Short.TYPE) || type.equals(Short.class) || type.equals(Byte.TYPE) || type.equals(Byte.class));
        }
    }
    
    public DaoConfig(final DaoConfig daoConfig) {
        this.db = daoConfig.db;
        this.tablename = daoConfig.tablename;
        this.properties = daoConfig.properties;
        this.allColumns = daoConfig.allColumns;
        this.pkColumns = daoConfig.pkColumns;
        this.nonPkColumns = daoConfig.nonPkColumns;
        this.pkProperty = daoConfig.pkProperty;
        this.statements = daoConfig.statements;
        this.keyIsNumeric = daoConfig.keyIsNumeric;
    }
    
    private static Property[] reflectProperties(final Class<? extends AbstractDao<?, ?>> clazz) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        final Field[] declaredFields = Class.forName(clazz.getName() + "$Properties").getDeclaredFields();
        final ArrayList list = new ArrayList();
        for (final Field field : declaredFields) {
            if ((field.getModifiers() & 0x9) == 0x9) {
                final Object value = field.get(null);
                if (value instanceof Property) {
                    list.add(value);
                }
            }
        }
        final Property[] array = new Property[list.size()];
        for (final Property property : list) {
            if (array[property.ordinal] != null) {
                throw new DaoException("Duplicate property ordinals");
            }
            array[property.ordinal] = property;
        }
        return array;
    }
    
    public DaoConfig clone() {
        return new DaoConfig(this);
    }
    
    public IdentityScope<?, ?> getIdentityScope() {
        return this.identityScope;
    }
    
    public void initIdentityScope(final IdentityScopeType obj) {
        if (obj != IdentityScopeType.None) {
            if (obj != IdentityScopeType.Session) {
                throw new IllegalArgumentException("Unsupported type: " + obj);
            }
            if (!this.keyIsNumeric) {
                this.identityScope = new IdentityScopeObject<Object, Object>();
            }
            else {
                this.identityScope = new IdentityScopeLong<Object>();
            }
        }
        else {
            this.identityScope = null;
        }
    }
    
    public void setIdentityScope(final IdentityScope<?, ?> identityScope) {
        this.identityScope = identityScope;
    }
}
