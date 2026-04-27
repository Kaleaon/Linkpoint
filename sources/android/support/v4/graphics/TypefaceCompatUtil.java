package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources resources, int i) {
        File tempFile = getTempFile(context);
        if (tempFile == null) {
            return null;
        }
        try {
            if (copyToFile(tempFile, resources, i)) {
                return mmap(tempFile);
            }
            tempFile.delete();
            return null;
        } finally {
            tempFile.delete();
        }
    }

    public static boolean copyToFile(File file, Resources resources, int i) {
        InputStream inputStream = null;
        try {
            inputStream = resources.openRawResource(i);
            return copyToFile(file, inputStream);
        } finally {
            closeQuietly(inputStream);
        }
    }

    public static boolean copyToFile(File file, InputStream inputStream) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file, false);
            try {
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        closeQuietly(fileOutputStream);
                        return true;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (IOException e) {
                e = e;
            }
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = null;
            try {
                Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
                closeQuietly(fileOutputStream);
                return false;
            } catch (Throwable th) {
                th = th;
                closeQuietly(fileOutputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream = null;
            closeQuietly(fileOutputStream);
            throw th;
        }
    }

    public static File getTempFile(Context context) {
        int i = 0;
        String str = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        while (i < 100) {
            File file = new File(context.getCacheDir(), str + i);
            try {
                if (file.createNewFile()) {
                    return file;
                }
                i++;
            } catch (IOException e) {
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x005a, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x005b, code lost:
        r11 = r1;
        r1 = r0;
        r0 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x007f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0080, code lost:
        r1 = null;
     */
    @android.support.annotation.RequiresApi(19)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.nio.ByteBuffer mmap(android.content.Context r12, android.os.CancellationSignal r13, android.net.Uri r14) {
        /*
            r6 = 0
            android.content.ContentResolver r0 = r12.getContentResolver()
            java.lang.String r1 = "r"
            android.os.ParcelFileDescriptor r7 = r0.openFileDescriptor(r14, r1, r13)     // Catch:{ IOException -> 0x003b }
            r8 = 0
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            java.io.FileDescriptor r0 = r7.getFileDescriptor()     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            r9.<init>(r0)     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            r10 = 0
            java.nio.channels.FileChannel r0 = r9.getChannel()     // Catch:{ Throwable -> 0x0058, all -> 0x007f }
            long r4 = r0.size()     // Catch:{ Throwable -> 0x0058, all -> 0x007f }
            java.nio.channels.FileChannel$MapMode r1 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Throwable -> 0x0058, all -> 0x007f }
            r2 = 0
            java.nio.MappedByteBuffer r0 = r0.map(r1, r2, r4)     // Catch:{ Throwable -> 0x0058, all -> 0x007f }
            if (r9 != 0) goto L_0x002c
        L_0x0029:
            if (r7 != 0) goto L_0x0049
        L_0x002b:
            return r0
        L_0x002c:
            if (r6 != 0) goto L_0x003d
            r9.close()     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            goto L_0x0029
        L_0x0032:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0034 }
        L_0x0034:
            r1 = move-exception
            r11 = r1
            r1 = r0
            r0 = r11
        L_0x0038:
            if (r7 != 0) goto L_0x0070
        L_0x003a:
            throw r0     // Catch:{ IOException -> 0x003b }
        L_0x003b:
            r0 = move-exception
            return r6
        L_0x003d:
            r9.close()     // Catch:{ Throwable -> 0x0041 }
            goto L_0x0029
        L_0x0041:
            r1 = move-exception
            r10.addSuppressed(r1)     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            goto L_0x0029
        L_0x0046:
            r0 = move-exception
            r1 = r6
            goto L_0x0038
        L_0x0049:
            if (r6 != 0) goto L_0x004f
            r7.close()     // Catch:{ IOException -> 0x003b }
            goto L_0x002b
        L_0x004f:
            r7.close()     // Catch:{ Throwable -> 0x0053 }
            goto L_0x002b
        L_0x0053:
            r1 = move-exception
            r8.addSuppressed(r1)     // Catch:{ IOException -> 0x003b }
            goto L_0x002b
        L_0x0058:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x005a }
        L_0x005a:
            r1 = move-exception
            r11 = r1
            r1 = r0
            r0 = r11
        L_0x005e:
            if (r9 != 0) goto L_0x0061
        L_0x0060:
            throw r0     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
        L_0x0061:
            if (r1 != 0) goto L_0x0067
            r9.close()     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            goto L_0x0060
        L_0x0067:
            r9.close()     // Catch:{ Throwable -> 0x006b }
            goto L_0x0060
        L_0x006b:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ Throwable -> 0x0032, all -> 0x0046 }
            goto L_0x0060
        L_0x0070:
            if (r1 != 0) goto L_0x0076
            r7.close()     // Catch:{ IOException -> 0x003b }
            goto L_0x003a
        L_0x0076:
            r7.close()     // Catch:{ Throwable -> 0x007a }
            goto L_0x003a
        L_0x007a:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ IOException -> 0x003b }
            goto L_0x003a
        L_0x007f:
            r0 = move-exception
            r1 = r6
            goto L_0x005e
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002d, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002e, code lost:
        r9 = r1;
        r1 = r0;
        r0 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0043, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0044, code lost:
        r1 = null;
     */
    @android.support.annotation.RequiresApi(19)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.nio.ByteBuffer mmap(java.io.File r10) {
        /*
            r6 = 0
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0020 }
            r7.<init>(r10)     // Catch:{ IOException -> 0x0020 }
            r8 = 0
            java.nio.channels.FileChannel r0 = r7.getChannel()     // Catch:{ Throwable -> 0x002b, all -> 0x0043 }
            long r4 = r0.size()     // Catch:{ Throwable -> 0x002b, all -> 0x0043 }
            java.nio.channels.FileChannel$MapMode r1 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Throwable -> 0x002b, all -> 0x0043 }
            r2 = 0
            java.nio.MappedByteBuffer r0 = r0.map(r1, r2, r4)     // Catch:{ Throwable -> 0x002b, all -> 0x0043 }
            if (r7 != 0) goto L_0x001a
        L_0x0019:
            return r0
        L_0x001a:
            if (r6 != 0) goto L_0x0022
            r7.close()     // Catch:{ IOException -> 0x0020 }
            goto L_0x0019
        L_0x0020:
            r0 = move-exception
            return r6
        L_0x0022:
            r7.close()     // Catch:{ Throwable -> 0x0026 }
            goto L_0x0019
        L_0x0026:
            r1 = move-exception
            r8.addSuppressed(r1)     // Catch:{ IOException -> 0x0020 }
            goto L_0x0019
        L_0x002b:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x002d }
        L_0x002d:
            r1 = move-exception
            r9 = r1
            r1 = r0
            r0 = r9
        L_0x0031:
            if (r7 != 0) goto L_0x0034
        L_0x0033:
            throw r0     // Catch:{ IOException -> 0x0020 }
        L_0x0034:
            if (r1 != 0) goto L_0x003a
            r7.close()     // Catch:{ IOException -> 0x0020 }
            goto L_0x0033
        L_0x003a:
            r7.close()     // Catch:{ Throwable -> 0x003e }
            goto L_0x0033
        L_0x003e:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ IOException -> 0x0020 }
            goto L_0x0033
        L_0x0043:
            r0 = move-exception
            r1 = r6
            goto L_0x0031
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(java.io.File):java.nio.ByteBuffer");
    }
}
