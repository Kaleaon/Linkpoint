package android.support.v4.content;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.OperationCanceledException;
import android.support.v4.os.CancellationSignal;

public final class ContentResolverCompat {
    private ContentResolverCompat() {
    }

    public static Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        Object obj = null;
        if (Build.VERSION.SDK_INT < 16) {
            if (cancellationSignal != null) {
                cancellationSignal.throwIfCanceled();
            }
            return contentResolver.query(uri, strArr, str, strArr2, str2);
        }
        if (cancellationSignal != null) {
            obj = cancellationSignal.getCancellationSignalObject();
        }
        try {
            return contentResolver.query(uri, strArr, str, strArr2, str2, (android.os.CancellationSignal) obj);
        } catch (Exception e) {
            if (!(e instanceof OperationCanceledException)) {
                throw e;
            }
            throw new android.support.v4.os.OperationCanceledException();
        }
    }
}
