package de.greenrobot.dao.query;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.InternalQueryDaoAccess;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QueryBuilder<T> {
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

    protected QueryBuilder(AbstractDao<T, ?> abstractDao) {
        this(abstractDao, "T");
    }

    protected QueryBuilder(AbstractDao<T, ?> abstractDao, String str) {
        this.dao = abstractDao;
        this.tablePrefix = str;
        this.values = new ArrayList();
        this.whereConditions = new ArrayList();
    }

    private void appendWhereClause(StringBuilder sb, String str) {
        this.values.clear();
        if (!this.whereConditions.isEmpty()) {
            sb.append(" WHERE ");
            ListIterator<WhereCondition> listIterator = this.whereConditions.listIterator();
            while (listIterator.hasNext()) {
                if (listIterator.hasPrevious()) {
                    sb.append(" AND ");
                }
                WhereCondition next = listIterator.next();
                next.appendTo(sb, str);
                next.appendValuesTo(this.values);
            }
        }
    }

    private void checkOrderBuilder() {
        if (this.orderBuilder == null) {
            this.orderBuilder = new StringBuilder();
        } else if (this.orderBuilder.length() > 0) {
            this.orderBuilder.append(",");
        }
    }

    public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2, ?> abstractDao) {
        return new QueryBuilder<>(abstractDao);
    }

    private void orderAscOrDesc(String str, Property... propertyArr) {
        for (Property property : propertyArr) {
            checkOrderBuilder();
            append(this.orderBuilder, property);
            if (String.class.equals(property.type)) {
                this.orderBuilder.append(" COLLATE LOCALIZED");
            }
            this.orderBuilder.append(str);
        }
    }

    /* access modifiers changed from: protected */
    public void addCondition(StringBuilder sb, List<Object> list, WhereCondition whereCondition) {
        checkCondition(whereCondition);
        whereCondition.appendTo(sb, this.tablePrefix);
        whereCondition.appendValuesTo(list);
    }

    public WhereCondition and(WhereCondition whereCondition, WhereCondition whereCondition2, WhereCondition... whereConditionArr) {
        return combineWhereConditions(" AND ", whereCondition, whereCondition2, whereConditionArr);
    }

    /* access modifiers changed from: protected */
    public StringBuilder append(StringBuilder sb, Property property) {
        checkProperty(property);
        sb.append(this.tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
        return sb;
    }

    public Query<T> build() {
        int size;
        int i = -1;
        StringBuilder sb = new StringBuilder((this.joinBuilder == null || this.joinBuilder.length() == 0) ? InternalQueryDaoAccess.getStatements(this.dao).getSelectAll() : SqlUtils.createSqlSelect(this.dao.getTablename(), this.tablePrefix, this.dao.getAllColumns()));
        appendWhereClause(sb, this.tablePrefix);
        if (this.orderBuilder != null && this.orderBuilder.length() > 0) {
            sb.append(" ORDER BY ").append(this.orderBuilder);
        }
        if (this.limit == null) {
            size = -1;
        } else {
            sb.append(" LIMIT ?");
            this.values.add(this.limit);
            size = this.values.size() - 1;
        }
        if (this.offset != null) {
            if (this.limit != null) {
                sb.append(" OFFSET ?");
                this.values.add(this.offset);
                i = this.values.size() - 1;
            } else {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
        }
        String sb2 = sb.toString();
        if (LOG_SQL) {
            DaoLog.d("Built SQL for query: " + sb2);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for query: " + this.values);
        }
        return Query.create(this.dao, sb2, this.values.toArray(), size, i);
    }

    public CountQuery<T> buildCount() {
        StringBuilder sb = new StringBuilder(SqlUtils.createSqlSelectCountStar(this.dao.getTablename(), this.tablePrefix));
        appendWhereClause(sb, this.tablePrefix);
        String sb2 = sb.toString();
        if (LOG_SQL) {
            DaoLog.d("Built SQL for count query: " + sb2);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for count query: " + this.values);
        }
        return CountQuery.create(this.dao, sb2, this.values.toArray());
    }

    public DeleteQuery<T> buildDelete() {
        String tablename = this.dao.getTablename();
        StringBuilder sb = new StringBuilder(SqlUtils.createSqlDelete(tablename, (String[]) null));
        appendWhereClause(sb, this.tablePrefix);
        String replace = sb.toString().replace(this.tablePrefix + ".'", tablename + ".'");
        if (LOG_SQL) {
            DaoLog.d("Built SQL for delete query: " + replace);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for delete query: " + this.values);
        }
        return DeleteQuery.create(this.dao, replace, this.values.toArray());
    }

    /* access modifiers changed from: protected */
    public void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof WhereCondition.PropertyCondition) {
            checkProperty(((WhereCondition.PropertyCondition) whereCondition).property);
        }
    }

    /* access modifiers changed from: protected */
    public void checkProperty(Property property) {
        boolean z = false;
        if (this.dao != null) {
            Property[] properties = this.dao.getProperties();
            int length = properties.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    if (property == properties[i]) {
                        z = true;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (!z) {
                throw new DaoException("Property '" + property.name + "' is not part of " + this.dao);
            }
        }
    }

    /* access modifiers changed from: protected */
    public WhereCondition combineWhereConditions(String str, WhereCondition whereCondition, WhereCondition whereCondition2, WhereCondition... whereConditionArr) {
        StringBuilder sb = new StringBuilder("(");
        ArrayList arrayList = new ArrayList();
        addCondition(sb, arrayList, whereCondition);
        sb.append(str);
        addCondition(sb, arrayList, whereCondition2);
        for (WhereCondition addCondition : whereConditionArr) {
            sb.append(str);
            addCondition(sb, arrayList, addCondition);
        }
        sb.append(')');
        return new WhereCondition.StringCondition(sb.toString(), arrayList.toArray());
    }

    public long count() {
        return buildCount().count();
    }

    public <J> QueryBuilder<J> join(Class<J> cls, Property property) {
        throw new UnsupportedOperationException();
    }

    public <J> QueryBuilder<J> joinToMany(Class<J> cls, Property property) {
        throw new UnsupportedOperationException();
    }

    public QueryBuilder<T> limit(int i) {
        this.limit = Integer.valueOf(i);
        return this;
    }

    public List<T> list() {
        return build().list();
    }

    public CloseableListIterator<T> listIterator() {
        return build().listIterator();
    }

    public LazyList<T> listLazy() {
        return build().listLazy();
    }

    public LazyList<T> listLazyUncached() {
        return build().listLazyUncached();
    }

    public QueryBuilder<T> offset(int i) {
        this.offset = Integer.valueOf(i);
        return this;
    }

    public WhereCondition or(WhereCondition whereCondition, WhereCondition whereCondition2, WhereCondition... whereConditionArr) {
        return combineWhereConditions(" OR ", whereCondition, whereCondition2, whereConditionArr);
    }

    public QueryBuilder<T> orderAsc(Property... propertyArr) {
        orderAscOrDesc(" ASC", propertyArr);
        return this;
    }

    public QueryBuilder<T> orderCustom(Property property, String str) {
        checkOrderBuilder();
        append(this.orderBuilder, property).append(' ');
        this.orderBuilder.append(str);
        return this;
    }

    public QueryBuilder<T> orderDesc(Property... propertyArr) {
        orderAscOrDesc(" DESC", propertyArr);
        return this;
    }

    public QueryBuilder<T> orderRaw(String str) {
        checkOrderBuilder();
        this.orderBuilder.append(str);
        return this;
    }

    public T unique() {
        return build().unique();
    }

    public T uniqueOrThrow() {
        return build().uniqueOrThrow();
    }

    public QueryBuilder<T> where(WhereCondition whereCondition, WhereCondition... whereConditionArr) {
        this.whereConditions.add(whereCondition);
        for (WhereCondition whereCondition2 : whereConditionArr) {
            checkCondition(whereCondition2);
            this.whereConditions.add(whereCondition2);
        }
        return this;
    }

    public QueryBuilder<T> whereOr(WhereCondition whereCondition, WhereCondition whereCondition2, WhereCondition... whereConditionArr) {
        this.whereConditions.add(or(whereCondition, whereCondition2, whereConditionArr));
        return this;
    }
}
