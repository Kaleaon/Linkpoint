// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import android.os.OperationCanceledException;
import android.os.Build$VERSION;
import android.database.Cursor;
import android.support.v4.os.CancellationSignal;
import android.net.Uri;
import android.content.ContentResolver;

public final class ContentResolverCompat
{
    private ContentResolverCompat() {
    }
    
    public static Cursor query(final ContentResolver contentResolver, final Uri uri, final String[] array, final String s, final String[] array2, final String s2, final CancellationSignal cancellationSignal) {
        final android.os.CancellationSignal cancellationSignal2 = null;
        Label_0082: {
            if (Build$VERSION.SDK_INT >= 16) {
                Label_0054: {
                    if (cancellationSignal != null) {
                        break Label_0054;
                    }
                    Object cancellationSignalObject = cancellationSignal2;
                    try {
                        return contentResolver.query(uri, array, s, array2, s2, (android.os.CancellationSignal)cancellationSignalObject);
                        cancellationSignalObject = cancellationSignal.getCancellationSignalObject();
                        return contentResolver.query(uri, array, s, array2, s2, (android.os.CancellationSignal)cancellationSignalObject);
                    }
                    catch (final Exception ex) {
                        if (!(ex instanceof OperationCanceledException)) {
                            throw ex;
                        }
                        throw new android.support.v4.os.OperationCanceledException();
                    }
                }
                break Label_0082;
            }
            if (cancellationSignal != null) {
                break Label_0082;
            }
            return contentResolver.query(uri, array, s, array2, s2);
        }
        cancellationSignal.throwIfCanceled();
        return contentResolver.query(uri, array, s, array2, s2);
    }
}
