// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import java.io.ByteArrayOutputStream;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class DbUtils
{
    public static int copyAllBytes(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[4096];
        int n = 0;
        while (true) {
            final int read = inputStream.read(array);
            if (read == -1) {
                break;
            }
            outputStream.write(array, 0, read);
            n += read;
        }
        return n;
    }
    
    public static int executeSqlScript(final Context context, final SQLiteDatabase sqLiteDatabase, final String s) throws IOException {
        return executeSqlScript(context, sqLiteDatabase, s, true);
    }
    
    public static int executeSqlScript(final Context context, final SQLiteDatabase sqLiteDatabase, final String str, final boolean b) throws IOException {
        final String[] split = new String(readAsset(context, str), "UTF-8").split(";(\\s)*[\n\r]");
        int i;
        if (!b) {
            i = executeSqlStatements(sqLiteDatabase, split);
        }
        else {
            i = executeSqlStatementsInTx(sqLiteDatabase, split);
        }
        DaoLog.i("Executed " + i + " statements from SQL script '" + str + "'");
        return i;
    }
    
    public static int executeSqlStatements(final SQLiteDatabase sqLiteDatabase, final String[] array) {
        int n = 0;
        for (int length = array.length, i = 0; i < length; ++i) {
            final String trim = array[i].trim();
            if (trim.length() > 0) {
                sqLiteDatabase.execSQL(trim);
                ++n;
            }
        }
        return n;
    }
    
    public static int executeSqlStatementsInTx(final SQLiteDatabase sqLiteDatabase, final String[] array) {
        sqLiteDatabase.beginTransaction();
        try {
            final int executeSqlStatements = executeSqlStatements(sqLiteDatabase, array);
            sqLiteDatabase.setTransactionSuccessful();
            return executeSqlStatements;
        }
        finally {
            sqLiteDatabase.endTransaction();
        }
    }
    
    public static void logTableDump(final SQLiteDatabase sqLiteDatabase, String query) {
        query = (String)sqLiteDatabase.query(query, (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
        try {
            DaoLog.d(DatabaseUtils.dumpCursorToString((Cursor)query));
        }
        finally {
            ((Cursor)query).close();
        }
    }
    
    public static byte[] readAllBytes(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copyAllBytes(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] readAsset(Context open, final String s) throws IOException {
        open = (Context)open.getResources().getAssets().open(s);
        try {
            return readAllBytes((InputStream)open);
        }
        finally {
            ((InputStream)open).close();
        }
    }
    
    public static void vacuum(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("VACUUM");
    }
}
