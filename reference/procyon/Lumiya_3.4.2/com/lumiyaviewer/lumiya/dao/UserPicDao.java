// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class UserPicDao extends AbstractDao<UserPic, Long>
{
    public static final String TABLENAME = "USER_PIC";
    
    public UserPicDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public UserPicDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String s;
        if (b) {
            s = "IF NOT EXISTS ";
        }
        else {
            s = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'USER_PIC' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'BITMAP' BLOB);");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_USER_PIC_UUID ON USER_PIC" + " (UUID);");
    }
    
    public static void dropTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        final StringBuilder append = new StringBuilder().append("DROP TABLE ");
        String str;
        if (b) {
            str = "IF EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL(append.append(str).append("'USER_PIC'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final UserPic userPic) {
        sqLiteStatement.clearBindings();
        final Long id = userPic.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        final String uuid = userPic.getUuid();
        if (uuid != null) {
            sqLiteStatement.bindString(2, uuid);
        }
        final byte[] bitmap = userPic.getBitmap();
        if (bitmap != null) {
            sqLiteStatement.bindBlob(3, bitmap);
        }
    }
    
    public Long getKey(final UserPic userPic) {
        if (userPic != null) {
            return userPic.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public UserPic readEntity(final Cursor cursor, final int n) {
        final byte[] array = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        String string;
        if (cursor.isNull(n + 1)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 1);
        }
        byte[] blob;
        if (cursor.isNull(n + 2)) {
            blob = array;
        }
        else {
            blob = cursor.getBlob(n + 2);
        }
        return new UserPic(value, string, blob);
    }
    
    public void readEntity(final Cursor cursor, final UserPic userPic, final int n) {
        final byte[] array = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        userPic.setId(value);
        String string;
        if (cursor.isNull(n + 1)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 1);
        }
        userPic.setUuid(string);
        byte[] blob;
        if (cursor.isNull(n + 2)) {
            blob = array;
        }
        else {
            blob = cursor.getBlob(n + 2);
        }
        userPic.setBitmap(blob);
    }
    
    public Long readKey(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return value;
    }
    
    @Override
    protected Long updateKeyAfterInsert(final UserPic userPic, final long n) {
        userPic.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property Bitmap;
        public static final Property Id;
        public static final Property Uuid;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            Uuid = new Property(1, String.class, "uuid", false, "UUID");
            Bitmap = new Property(2, byte[].class, "bitmap", false, "BITMAP");
        }
    }
}
