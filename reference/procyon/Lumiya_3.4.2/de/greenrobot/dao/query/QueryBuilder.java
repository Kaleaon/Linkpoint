// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.InternalQueryDaoAccess;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.Property;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.dao.AbstractDao;

public class QueryBuilder<T>
{
    public static boolean LOG_SQL;
    public static boolean LOG_VALUES;
    private final AbstractDao<T, ?> dao;
    private StringBuilder joinBuilder;
    private Integer limit;
    private Integer offset;
    private StringBuilder orderBuilder;
    private final String tablePrefix;
    private final List<Object> values;
    private final List<WhereCondition> whereConditions;
    
    protected QueryBuilder(final AbstractDao<T, ?> abstractDao) {
        this(abstractDao, "T");
    }
    
    protected QueryBuilder(final AbstractDao<T, ?> dao, final String tablePrefix) {
        this.dao = dao;
        this.tablePrefix = tablePrefix;
        this.values = new ArrayList<Object>();
        this.whereConditions = new ArrayList<WhereCondition>();
    }
    
    private void appendWhereClause(final StringBuilder sb, final String s) {
        this.values.clear();
        if (!this.whereConditions.isEmpty()) {
            sb.append(" WHERE ");
            final ListIterator<WhereCondition> listIterator = this.whereConditions.listIterator();
            while (listIterator.hasNext()) {
                if (listIterator.hasPrevious()) {
                    sb.append(" AND ");
                }
                final WhereCondition whereCondition = listIterator.next();
                whereCondition.appendTo(sb, s);
                whereCondition.appendValuesTo(this.values);
            }
        }
    }
    
    private void checkOrderBuilder() {
        if (this.orderBuilder != null) {
            if (this.orderBuilder.length() > 0) {
                this.orderBuilder.append(",");
            }
        }
        else {
            this.orderBuilder = new StringBuilder();
        }
    }
    
    public static <T2> QueryBuilder<T2> internalCreate(final AbstractDao<T2, ?> abstractDao) {
        return new QueryBuilder<T2>(abstractDao);
    }
    
    private void orderAscOrDesc(final String str, final Property... array) {
        for (int i = 0; i < array.length; ++i) {
            final Property property = array[i];
            this.checkOrderBuilder();
            this.append(this.orderBuilder, property);
            if (String.class.equals(property.type)) {
                this.orderBuilder.append(" COLLATE LOCALIZED");
            }
            this.orderBuilder.append(str);
        }
    }
    
    protected void addCondition(final StringBuilder sb, final List<Object> list, final WhereCondition whereCondition) {
        this.checkCondition(whereCondition);
        whereCondition.appendTo(sb, this.tablePrefix);
        whereCondition.appendValuesTo(list);
    }
    
    public WhereCondition and(final WhereCondition whereCondition, final WhereCondition whereCondition2, final WhereCondition... array) {
        return this.combineWhereConditions(" AND ", whereCondition, whereCondition2, array);
    }
    
    protected StringBuilder append(final StringBuilder sb, final Property property) {
        this.checkProperty(property);
        sb.append(this.tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
        return sb;
    }
    
    public Query<T> build() {
        int n = -1;
        String str;
        if (this.joinBuilder != null && this.joinBuilder.length() != 0) {
            str = SqlUtils.createSqlSelect(this.dao.getTablename(), this.tablePrefix, this.dao.getAllColumns());
        }
        else {
            str = InternalQueryDaoAccess.getStatements(this.dao).getSelectAll();
        }
        final StringBuilder sb = new StringBuilder(str);
        this.appendWhereClause(sb, this.tablePrefix);
        if (this.orderBuilder != null && this.orderBuilder.length() > 0) {
            sb.append(" ORDER BY ").append((CharSequence)this.orderBuilder);
        }
        int n2;
        if (this.limit == null) {
            n2 = -1;
        }
        else {
            sb.append(" LIMIT ?");
            this.values.add(this.limit);
            n2 = this.values.size() - 1;
        }
        if (this.offset != null) {
            if (this.limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            sb.append(" OFFSET ?");
            this.values.add(this.offset);
            n = this.values.size() - 1;
        }
        final String string = sb.toString();
        if (QueryBuilder.LOG_SQL) {
            DaoLog.d("Built SQL for query: " + string);
        }
        if (QueryBuilder.LOG_VALUES) {
            DaoLog.d("Values for query: " + this.values);
        }
        return Query.create(this.dao, string, this.values.toArray(), n2, n);
    }
    
    public CountQuery<T> buildCount() {
        final StringBuilder sb = new StringBuilder(SqlUtils.createSqlSelectCountStar(this.dao.getTablename(), this.tablePrefix));
        this.appendWhereClause(sb, this.tablePrefix);
        final String string = sb.toString();
        if (QueryBuilder.LOG_SQL) {
            DaoLog.d("Built SQL for count query: " + string);
        }
        if (QueryBuilder.LOG_VALUES) {
            DaoLog.d("Values for count query: " + this.values);
        }
        return CountQuery.create(this.dao, string, this.values.toArray());
    }
    
    public DeleteQuery<T> buildDelete() {
        final String tablename = this.dao.getTablename();
        final StringBuilder sb = new StringBuilder(SqlUtils.createSqlDelete(tablename, null));
        this.appendWhereClause(sb, this.tablePrefix);
        final String replace = sb.toString().replace(this.tablePrefix + ".'", tablename + ".'");
        if (QueryBuilder.LOG_SQL) {
            DaoLog.d("Built SQL for delete query: " + replace);
        }
        if (QueryBuilder.LOG_VALUES) {
            DaoLog.d("Values for delete query: " + this.values);
        }
        return DeleteQuery.create(this.dao, replace, this.values.toArray());
    }
    
    protected void checkCondition(final WhereCondition whereCondition) {
        if (whereCondition instanceof WhereCondition.PropertyCondition) {
            this.checkProperty(((WhereCondition.PropertyCondition)whereCondition).property);
        }
    }
    
    protected void checkProperty(final Property property) {
        final int n = 0;
        if (this.dao != null) {
            final Property[] properties = this.dao.getProperties();
            final int length = properties.length;
            int i = 0;
            while (true) {
                while (i < length) {
                    if (property != properties[i]) {
                        ++i;
                    }
                    else {
                        final int n2 = 1;
                        if (n2 == 0) {
                            throw new DaoException("Property '" + property.name + "' is not part of " + this.dao);
                        }
                        return;
                    }
                }
                final int n2 = n;
                continue;
            }
        }
    }
    
    protected WhereCondition combineWhereConditions(final String s, WhereCondition whereCondition, final WhereCondition whereCondition2, final WhereCondition... array) {
        final StringBuilder sb = new StringBuilder("(");
        final ArrayList list = new ArrayList();
        this.addCondition(sb, list, whereCondition);
        sb.append(s);
        this.addCondition(sb, list, whereCondition2);
        for (int length = array.length, i = 0; i < length; ++i) {
            whereCondition = array[i];
            sb.append(s);
            this.addCondition(sb, list, whereCondition);
        }
        sb.append(')');
        return new WhereCondition.StringCondition(sb.toString(), list.toArray());
    }
    
    public long count() {
        return this.buildCount().count();
    }
    
    public <J> QueryBuilder<J> join(final Class<J> clazz, final Property property) {
        throw new UnsupportedOperationException();
    }
    
    public <J> QueryBuilder<J> joinToMany(final Class<J> clazz, final Property property) {
        throw new UnsupportedOperationException();
    }
    
    public QueryBuilder<T> limit(final int i) {
        this.limit = i;
        return this;
    }
    
    public List<T> list() {
        return this.build().list();
    }
    
    public CloseableListIterator<T> listIterator() {
        return this.build().listIterator();
    }
    
    public LazyList<T> listLazy() {
        return this.build().listLazy();
    }
    
    public LazyList<T> listLazyUncached() {
        return this.build().listLazyUncached();
    }
    
    public QueryBuilder<T> offset(final int i) {
        this.offset = i;
        return this;
    }
    
    public WhereCondition or(final WhereCondition whereCondition, final WhereCondition whereCondition2, final WhereCondition... array) {
        return this.combineWhereConditions(" OR ", whereCondition, whereCondition2, array);
    }
    
    public QueryBuilder<T> orderAsc(final Property... array) {
        this.orderAscOrDesc(" ASC", array);
        return this;
    }
    
    public QueryBuilder<T> orderCustom(final Property property, final String str) {
        this.checkOrderBuilder();
        this.append(this.orderBuilder, property).append(' ');
        this.orderBuilder.append(str);
        return this;
    }
    
    public QueryBuilder<T> orderDesc(final Property... array) {
        this.orderAscOrDesc(" DESC", array);
        return this;
    }
    
    public QueryBuilder<T> orderRaw(final String str) {
        this.checkOrderBuilder();
        this.orderBuilder.append(str);
        return this;
    }
    
    public T unique() {
        return this.build().unique();
    }
    
    public T uniqueOrThrow() {
        return this.build().uniqueOrThrow();
    }
    
    public QueryBuilder<T> where(WhereCondition whereCondition, final WhereCondition... array) {
        this.whereConditions.add(whereCondition);
        for (int length = array.length, i = 0; i < length; ++i) {
            whereCondition = array[i];
            this.checkCondition(whereCondition);
            this.whereConditions.add(whereCondition);
        }
        return this;
    }
    
    public QueryBuilder<T> whereOr(final WhereCondition whereCondition, final WhereCondition whereCondition2, final WhereCondition... array) {
        this.whereConditions.add(this.or(whereCondition, whereCondition2, array));
        return this;
    }
}
