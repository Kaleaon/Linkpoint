// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.internal;

import android.content.ContentResolver;
import android.database.DataSetObserver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.database.CharArrayBuffer;
import android.database.CursorWindow;
import android.database.Cursor;

public final class FastCursor implements Cursor
{
    private final int count;
    private int position;
    private final CursorWindow window;
    
    public FastCursor(final CursorWindow window) {
        this.window = window;
        this.count = window.getNumRows();
    }
    
    public void close() {
        throw new UnsupportedOperationException();
    }
    
    public void copyStringToBuffer(final int n, final CharArrayBuffer charArrayBuffer) {
        throw new UnsupportedOperationException();
    }
    
    public void deactivate() {
        throw new UnsupportedOperationException();
    }
    
    public byte[] getBlob(final int n) {
        return this.window.getBlob(this.position, n);
    }
    
    public int getColumnCount() {
        throw new UnsupportedOperationException();
    }
    
    public int getColumnIndex(final String s) {
        throw new UnsupportedOperationException();
    }
    
    public int getColumnIndexOrThrow(final String s) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public String getColumnName(final int n) {
        throw new UnsupportedOperationException();
    }
    
    public String[] getColumnNames() {
        throw new UnsupportedOperationException();
    }
    
    public int getCount() {
        return this.window.getNumRows();
    }
    
    public double getDouble(final int n) {
        return this.window.getDouble(this.position, n);
    }
    
    public Bundle getExtras() {
        throw new UnsupportedOperationException();
    }
    
    public float getFloat(final int n) {
        return this.window.getFloat(this.position, n);
    }
    
    public int getInt(final int n) {
        return this.window.getInt(this.position, n);
    }
    
    public long getLong(final int n) {
        return this.window.getLong(this.position, n);
    }
    
    public Uri getNotificationUri() {
        return null;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public short getShort(final int n) {
        return this.window.getShort(this.position, n);
    }
    
    public String getString(final int n) {
        return this.window.getString(this.position, n);
    }
    
    public int getType(final int n) {
        throw new UnsupportedOperationException();
    }
    
    public boolean getWantsAllOnMoveCalls() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isAfterLast() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isBeforeFirst() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isFirst() {
        boolean b = false;
        if (this.position == 0) {
            b = true;
        }
        return b;
    }
    
    public boolean isLast() {
        return this.position == this.count - 1;
    }
    
    public boolean isNull(final int n) {
        return this.window.isNull(this.position, n);
    }
    
    public boolean move(final int n) {
        return this.moveToPosition(this.position + n);
    }
    
    public boolean moveToFirst() {
        boolean b = false;
        this.position = 0;
        if (this.count > 0) {
            b = true;
        }
        return b;
    }
    
    public boolean moveToLast() {
        if (this.count <= 0) {
            return false;
        }
        this.position = this.count - 1;
        return true;
    }
    
    public boolean moveToNext() {
        if (this.position >= this.count - 1) {
            return false;
        }
        ++this.position;
        return true;
    }
    
    public boolean moveToPosition(final int position) {
        if (position >= 0 && position < this.count) {
            this.position = position;
            return true;
        }
        return false;
    }
    
    public boolean moveToPrevious() {
        if (this.position <= 0) {
            return false;
        }
        --this.position;
        return true;
    }
    
    public void registerContentObserver(final ContentObserver contentObserver) {
        throw new UnsupportedOperationException();
    }
    
    public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
        throw new UnsupportedOperationException();
    }
    
    public boolean requery() {
        throw new UnsupportedOperationException();
    }
    
    public Bundle respond(final Bundle bundle) {
        throw new UnsupportedOperationException();
    }
    
    public void setExtras(final Bundle bundle) {
        throw new UnsupportedOperationException();
    }
    
    public void setNotificationUri(final ContentResolver contentResolver, final Uri uri) {
        throw new UnsupportedOperationException();
    }
    
    public void unregisterContentObserver(final ContentObserver contentObserver) {
        throw new UnsupportedOperationException();
    }
    
    public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
        throw new UnsupportedOperationException();
    }
}
