package de.greenrobot.dao.test;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class AbstractDaoTestSinglePk<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {
    private Property pkColumn;
    protected Set<K> usedPks;

    public AbstractDaoTestSinglePk(Class<D> cls) {
        super(cls);
        this.usedPks = new HashSet();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract T createEntity(K k);

    protected T createEntityWithRandomPk() {
        return createEntity(nextPk());
    }

    protected abstract K createRandomPk();

    protected K nextPk() {
        for (int i = 0; i < 100000; i++) {
            K createRandomPk = createRandomPk();
            if (this.usedPks.add(createRandomPk)) {
                return createRandomPk;
            }
        }
        throw new IllegalStateException("Could not find a new PK");
    }

    protected Cursor queryWithDummyColumnsInFront(int i, String str, K k) {
        StringBuilder sb = new StringBuilder("SELECT ");
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(str).append(",");
        }
        SqlUtils.appendColumns(sb, "T", this.dao.getAllColumns()).append(" FROM ");
        sb.append(this.dao.getTablename()).append(" T");
        if (k != null) {
            sb.append(" WHERE ");
            assertEquals(1, this.dao.getPkColumns().length);
            sb.append(this.dao.getPkColumns()[0]).append("=");
            DatabaseUtils.appendValueToSql(sb, k);
        }
        Cursor rawQuery = this.db.rawQuery(sb.toString(), null);
        assertTrue(rawQuery.moveToFirst());
        for (int i3 = 0; i3 < i; i3++) {
            try {
                assertEquals(str, rawQuery.getString(i3));
            } catch (RuntimeException e) {
                rawQuery.close();
                throw e;
            }
        }
        if (k != null) {
            assertEquals(1, rawQuery.getCount());
        }
        return rawQuery;
    }

    protected void runLoadPkTest(int i) {
        K nextPk = nextPk();
        this.dao.insert(createEntity(nextPk));
        Cursor queryWithDummyColumnsInFront = queryWithDummyColumnsInFront(i, "42", nextPk);
        try {
            assertEquals(nextPk, this.daoAccess.readKey(queryWithDummyColumnsInFront, i));
        } finally {
            queryWithDummyColumnsInFront.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.test.AbstractDaoTest, de.greenrobot.dao.test.DbTest
    public void setUp() throws Exception {
        Property[] properties;
        super.setUp();
        for (Property property : this.daoAccess.getProperties()) {
            if (property.primaryKey) {
                if (this.pkColumn != null) {
                    throw new RuntimeException("Test does not work with multiple PK columns");
                }
                this.pkColumn = property;
            }
        }
        if (this.pkColumn == null) {
            throw new RuntimeException("Test does not work without a PK column");
        }
    }

    public void testCount() {
        this.dao.deleteAll();
        assertEquals(0L, this.dao.count());
        this.dao.insert(createEntityWithRandomPk());
        assertEquals(1L, this.dao.count());
        this.dao.insert(createEntityWithRandomPk());
        assertEquals(2L, this.dao.count());
    }

    public void testDelete() {
        K nextPk = nextPk();
        this.dao.deleteByKey(nextPk);
        this.dao.insert(createEntity(nextPk));
        assertNotNull(this.dao.load(nextPk));
        this.dao.deleteByKey(nextPk);
        assertNull(this.dao.load(nextPk));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void testDeleteAll() {
        ArrayList<Object> arrayList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            arrayList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(arrayList);
        this.dao.deleteAll();
        assertEquals(0L, this.dao.count());
        for (Object obj : arrayList) {
            K key = this.daoAccess.getKey(obj);
            assertNotNull(key);
            assertNull(this.dao.load(key));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void testDeleteByKeyInTx() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            arrayList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(arrayList);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(this.daoAccess.getKey(arrayList.get(0)));
        arrayList2.add(this.daoAccess.getKey(arrayList.get(3)));
        arrayList2.add(this.daoAccess.getKey(arrayList.get(4)));
        arrayList2.add(this.daoAccess.getKey(arrayList.get(8)));
        this.dao.deleteByKeyInTx(arrayList2);
        assertEquals(arrayList.size() - arrayList2.size(), this.dao.count());
        for (Object obj : arrayList2) {
            assertNotNull(obj);
            assertNull(this.dao.load(obj));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void testDeleteInTx() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            arrayList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(arrayList);
        ArrayList<Object> arrayList2 = new ArrayList();
        arrayList2.add(arrayList.get(0));
        arrayList2.add(arrayList.get(3));
        arrayList2.add(arrayList.get(4));
        arrayList2.add(arrayList.get(8));
        this.dao.deleteInTx(arrayList2);
        assertEquals(arrayList.size() - arrayList2.size(), this.dao.count());
        for (Object obj : arrayList2) {
            K key = this.daoAccess.getKey(obj);
            assertNotNull(key);
            assertNull(this.dao.load(key));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void testInsertAndLoad() {
        K nextPk = nextPk();
        T createEntity = createEntity(nextPk);
        this.dao.insert(createEntity);
        assertEquals(nextPk, this.daoAccess.getKey(createEntity));
        Object load = this.dao.load(nextPk);
        assertNotNull(load);
        assertEquals(this.daoAccess.getKey(createEntity), this.daoAccess.getKey(load));
    }

    public void testInsertInTx() {
        this.dao.deleteAll();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 20; i++) {
            arrayList.add(createEntityWithRandomPk());
        }
        this.dao.insertInTx(arrayList);
        assertEquals(arrayList.size(), this.dao.count());
    }

    public void testInsertOrReplaceInTx() {
        this.dao.deleteAll();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < 20; i++) {
            T createEntityWithRandomPk = createEntityWithRandomPk();
            if (i % 2 == 0) {
                arrayList.add(createEntityWithRandomPk);
            }
            arrayList2.add(createEntityWithRandomPk);
        }
        this.dao.insertOrReplaceInTx(arrayList);
        this.dao.insertOrReplaceInTx(arrayList2);
        assertEquals(arrayList2.size(), this.dao.count());
    }

    public void testInsertOrReplaceTwice() {
        T createEntityWithRandomPk = createEntityWithRandomPk();
        long insert = this.dao.insert(createEntityWithRandomPk);
        long insertOrReplace = this.dao.insertOrReplace(createEntityWithRandomPk);
        if (this.dao.getPkProperty().type != Long.class) {
            return;
        }
        assertEquals(insert, insertOrReplace);
    }

    public void testInsertTwice() {
        T createEntity = createEntity(nextPk());
        this.dao.insert(createEntity);
        try {
            this.dao.insert(createEntity);
            fail("Inserting twice should not work");
        } catch (SQLException e) {
        }
    }

    public void testLoadAll() {
        this.dao.deleteAll();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 15; i++) {
            arrayList.add(createEntity(nextPk()));
        }
        this.dao.insertInTx(arrayList);
        assertEquals(arrayList.size(), this.dao.loadAll().size());
    }

    public void testLoadPk() {
        runLoadPkTest(0);
    }

    public void testLoadPkWithOffset() {
        runLoadPkTest(10);
    }

    public void testQuery() {
        this.dao.insert(createEntityWithRandomPk());
        K nextPk = nextPk();
        this.dao.insert(createEntity(nextPk));
        this.dao.insert(createEntityWithRandomPk());
        List<T> queryRaw = this.dao.queryRaw("WHERE " + this.dao.getPkColumns()[0] + "=?", nextPk.toString());
        assertEquals(1, queryRaw.size());
        assertEquals(nextPk, this.daoAccess.getKey(queryRaw.get(0)));
    }

    public void testReadWithOffset() {
        K nextPk = nextPk();
        this.dao.insert(createEntity(nextPk));
        Cursor queryWithDummyColumnsInFront = queryWithDummyColumnsInFront(5, "42", nextPk);
        try {
            assertEquals(nextPk, this.daoAccess.getKey(this.daoAccess.readEntity(queryWithDummyColumnsInFront, 5)));
        } finally {
            queryWithDummyColumnsInFront.close();
        }
    }

    public void testRowId() {
        assertTrue(this.dao.insert(createEntityWithRandomPk()) != this.dao.insert(createEntityWithRandomPk()));
    }

    public void testUpdate() {
        this.dao.deleteAll();
        T createEntityWithRandomPk = createEntityWithRandomPk();
        this.dao.insert(createEntityWithRandomPk);
        this.dao.update(createEntityWithRandomPk);
        assertEquals(1L, this.dao.count());
    }
}
