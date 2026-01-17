package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources resources, int i) {
        File tempFile = getTempFile(context);
        if (tempFile != null) {
            try {
                if (copyToFile(tempFile, resources, i)) {
                    return mmap(tempFile);
                }
                return null;
            } finally {
                tempFile.delete();
            }
        }
        return null;
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
                    Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
                    closeQuietly(fileOutputStream);
                    return false;
                }
            } catch (Throwable th) {
                th = th;
                closeQuietly(null);
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = null;
        } catch (Throwable th2) {
            th = th2;
            closeQuietly(null);
            throw th;
        }
    }

    public static File getTempFile(Context context) {
        String str = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        for (int i = 0; i < 100; i++) {
            File file = new File(context.getCacheDir(), str + i);
            if (file.createNewFile()) {
                return file;
            }
        }
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    @android.support.annotation.RequiresApi(19)
    public static java.nio.ByteBuffer mmap(android.content.Context r12, android.os.CancellationSignal r13, android.net.Uri r14) {
        /*
            r6 = 0
            android.content.ContentResolver r0 = r12.getContentResolver()
            java.lang.String r1 = "r"
            android.os.ParcelFileDescriptor r7 = r0.openFileDescriptor(r14, r1, r13)     // Catch: java.io.IOException -> L3b
            r8 = 0
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L32
            java.io.FileDescriptor r0 = r7.getFileDescriptor()     // Catch: java.lang.Throwable -> L32
            r9.<init>(r0)     // Catch: java.lang.Throwable -> L32
            r10 = 0
            java.nio.channels.FileChannel r0 = r9.getChannel()     // Catch: java.lang.Throwable -> L58
            long r4 = r0.size()     // Catch: java.lang.Throwable -> L58
            java.nio.channels.FileChannel$MapMode r1 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch: java.lang.Throwable -> L58
            r2 = 0
            java.nio.MappedByteBuffer r0 = r0.map(r1, r2, r4)     // Catch: java.lang.Throwable -> L58
            if (r9 != 0) goto L2c
        L29:
            if (r7 != 0) goto L49
        L2b:
            return r0
        L2c:
            if (r6 != 0) goto L3d
            r9.close()     // Catch: java.lang.Throwable -> L32
            goto L29
        L32:
            r0 = move-exception
            throw r0     // Catch: java.lang.Throwable -> L34
        L34:
            r1 = move-exception
            r11 = r1
            r1 = r0
            r0 = r11
        L38:
            if (r7 != 0) goto L70
        L3a:
            throw r0     // Catch: java.io.IOException -> L3b
        L3b:
            r0 = move-exception
            return r6
        L3d:
            r9.close()     // Catch: java.lang.Throwable -> L41
            goto L29
        L41:
            r1 = move-exception
            r10.addSuppressed(r1)     // Catch: java.lang.Throwable -> L32
            goto L29
        L46:
            r0 = move-exception
            r1 = r6
            goto L38
        L49:
            if (r6 != 0) goto L4f
            r7.close()     // Catch: java.io.IOException -> L3b
            goto L2b
        L4f:
            r7.close()     // Catch: java.lang.Throwable -> L53
            goto L2b
        L53:
            r1 = move-exception
            r8.addSuppressed(r1)     // Catch: java.io.IOException -> L3b
            goto L2b
        L58:
            r0 = move-exception
            throw r0     // Catch: java.lang.Throwable -> L5a
        L5a:
            r1 = move-exception
            r11 = r1
            r1 = r0
            r0 = r11
        L5e:
            if (r9 != 0) goto L61
        L60:
            throw r0     // Catch: java.lang.Throwable -> L32
        L61:
            if (r1 != 0) goto L67
            r9.close()     // Catch: java.lang.Throwable -> L32
            goto L60
        L67:
            r9.close()     // Catch: java.lang.Throwable -> L6b
            goto L60
        L6b:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch: java.lang.Throwable -> L32
            goto L60
        L70:
            if (r1 != 0) goto L76
            r7.close()     // Catch: java.io.IOException -> L3b
            goto L3a
        L76:
            r7.close()     // Catch: java.lang.Throwable -> L7a
            goto L3a
        L7a:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch: java.io.IOException -> L3b
            goto L3a
        L7f:
            r0 = move-exception
            r1 = r6
            goto L5e
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    @RequiresApi(19)
    private static ByteBuffer mmap(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel channel = fileInputStream.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
            if (fileInputStream != null) {
                if (0 == 0) {
                    fileInputStream.close();
                } else {
                    fileInputStream.close();
                }
            }
            return map;
        } catch (IOException e) {
            return null;
        }
    }
}
