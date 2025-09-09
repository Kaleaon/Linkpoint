package de.greenrobot.dao.internal;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.identityscope.IdentityScopeLong;
import de.greenrobot.dao.identityscope.IdentityScopeObject;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public final class DaoConfig implements Cloneable {
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

    public DaoConfig(SQLiteDatabase sQLiteDatabase, Class<? extends AbstractDao<?, ?>> cls) {
        this.db = sQLiteDatabase;
        try {
            this.tablename = (String) cls.getField("TABLENAME").get((Object) null);
            Property[] reflectProperties = reflectProperties(cls);
            this.properties = reflectProperties;
            this.allColumns = new String[reflectProperties.length];
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            Property property = null;
            while (i < reflectProperties.length) {
                Property property2 = reflectProperties[i];
                String str = property2.columnName;
                this.allColumns[i] = str;
                if (!property2.primaryKey) {
                    arrayList2.add(str);
                    property2 = property;
                } else {
                    arrayList.add(str);
                }
                i++;
                property = property2;
            }
            this.nonPkColumns = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
            this.pkColumns = (String[]) arrayList.toArray(new String[arrayList.size()]);
            this.pkProperty = this.pkColumns.length != 1 ? null : property;
            this.statements = new TableStatements(sQLiteDatabase, this.tablename, this.allColumns, this.pkColumns);
            if (this.pkProperty == null) {
                this.keyIsNumeric = false;
                return;
            }
            Class<?> cls2 = this.pkProperty.type;
            this.keyIsNumeric = cls2.equals(Long.TYPE) || cls2.equals(Long.class) || cls2.equals(Integer.TYPE) || cls2.equals(Integer.class) || cls2.equals(Short.TYPE) || cls2.equals(Short.class) || cls2.equals(Byte.TYPE) || cls2.equals(Byte.class);
        } catch (Exception e) {
            throw new DaoException("Could not init DAOConfig", e);
        }
    }

    public DaoConfig(DaoConfig daoConfig) {
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

    private static Property[] reflectProperties(Class<? extends AbstractDao<?, ?>> cls) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        Field[] declaredFields = Class.forName(cls.getName() + "$Properties").getDeclaredFields();
        ArrayList arrayList = new ArrayList();
        for (Field field : declaredFields) {
            if ((field.getModifiers() & 9) == 9) {
                Object obj = field.get((Object) null);
                if (obj instanceof Property) {
                    arrayList.add((Property) obj);
                }
            }
        }
        Property[] propertyArr = new Property[arrayList.size()];
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Property property = (Property) it.next();
            if (propertyArr[property.ordinal] == null) {
                propertyArr[property.ordinal] = property;
            } else {
                throw new DaoException("Duplicate property ordinals");
            }
        }
        return propertyArr;
    }

    public DaoConfig clone() {
        return new DaoConfig(this);
    }

    public IdentityScope<?, ?> getIdentityScope() {
        return this.identityScope;
    }

    public void initIdentityScope(IdentityScopeType identityScopeType) {
        if (identityScopeType == IdentityScopeType.None) {
            this.identityScope = null;
        } else if (identityScopeType != IdentityScopeType.Session) {
            throw new IllegalArgumentException("Unsupported type: " + identityScopeType);
        } else if (!this.keyIsNumeric) {
            this.identityScope = new IdentityScopeObject();
        } else {
            this.identityScope = new IdentityScopeLong();
        }
    }

    public void setIdentityScope(IdentityScope<?, ?> identityScope2) {
        this.identityScope = identityScope2;
    }
}
