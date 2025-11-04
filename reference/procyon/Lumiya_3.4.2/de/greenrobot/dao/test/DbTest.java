// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import de.greenrobot.dao.DbUtils;
import android.database.sqlite.SQLiteDatabase$CursorFactory;
import android.app.Instrumentation;
import java.util.Random;
import android.database.sqlite.SQLiteDatabase;
import android.app.Application;
import android.test.AndroidTestCase;

public abstract class DbTest extends AndroidTestCase
{
    public static final String DB_NAME = "greendao-unittest-db.temp";
    private Application application;
    protected SQLiteDatabase db;
    protected final boolean inMemory;
    protected final Random random;
    
    public DbTest() {
        this(true);
    }
    
    public DbTest(final boolean inMemory) {
        this.inMemory = inMemory;
        this.random = new Random();
    }
    
    public <T extends Application> T createApplication(final Class<T> obj) {
        assertNull("Application already created", (Object)this.application);
        try {
            final Application application = Instrumentation.newApplication((Class)obj, this.getContext());
            application.onCreate();
            return (T)(this.application = application);
        }
        catch (final Exception cause) {
            throw new RuntimeException("Could not create application " + obj, cause);
        }
    }
    
    protected SQLiteDatabase createDatabase() {
        if (!this.inMemory) {
            this.getContext().deleteDatabase("greendao-unittest-db.temp");
            return this.getContext().openOrCreateDatabase("greendao-unittest-db.temp", 0, (SQLiteDatabase$CursorFactory)null);
        }
        return SQLiteDatabase.create((SQLiteDatabase$CursorFactory)null);
    }
    
    public <T extends Application> T getApplication() {
        assertNotNull("Application not yet created", (Object)this.application);
        return (T)this.application;
    }
    
    protected void logTableDump(final String s) {
        DbUtils.logTableDump(this.db, s);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.db = this.createDatabase();
    }
    
    protected void tearDown() throws Exception {
        if (this.application != null) {
            this.terminateApplication();
        }
        this.db.close();
        if (!this.inMemory) {
            this.getContext().deleteDatabase("greendao-unittest-db.temp");
        }
        super.tearDown();
    }
    
    public void terminateApplication() {
        assertNotNull("Application not yet created", (Object)this.application);
        this.application.onTerminate();
        this.application = null;
    }
}
