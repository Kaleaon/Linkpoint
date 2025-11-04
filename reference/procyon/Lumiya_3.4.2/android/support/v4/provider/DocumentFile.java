// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import android.os.Build$VERSION;
import android.net.Uri;
import android.content.Context;
import java.io.File;

public abstract class DocumentFile
{
    static final String TAG = "DocumentFile";
    private final DocumentFile mParent;
    
    DocumentFile(final DocumentFile mParent) {
        this.mParent = mParent;
    }
    
    public static DocumentFile fromFile(final File file) {
        return new RawDocumentFile(null, file);
    }
    
    public static DocumentFile fromSingleUri(final Context context, final Uri uri) {
        if (Build$VERSION.SDK_INT < 19) {
            return null;
        }
        return new SingleDocumentFile(null, context, uri);
    }
    
    public static DocumentFile fromTreeUri(final Context context, final Uri uri) {
        if (Build$VERSION.SDK_INT < 21) {
            return null;
        }
        return new TreeDocumentFile(null, context, DocumentsContractApi21.prepareTreeUri(uri));
    }
    
    public static boolean isDocumentUri(final Context context, final Uri uri) {
        return Build$VERSION.SDK_INT >= 19 && DocumentsContractApi19.isDocumentUri(context, uri);
    }
    
    public abstract boolean canRead();
    
    public abstract boolean canWrite();
    
    public abstract DocumentFile createDirectory(final String p0);
    
    public abstract DocumentFile createFile(final String p0, final String p1);
    
    public abstract boolean delete();
    
    public abstract boolean exists();
    
    public DocumentFile findFile(final String s) {
        int i = 0;
        for (DocumentFile[] listFiles = this.listFiles(); i < listFiles.length; ++i) {
            final DocumentFile documentFile = listFiles[i];
            if (s.equals(documentFile.getName())) {
                return documentFile;
            }
        }
        return null;
    }
    
    public abstract String getName();
    
    public DocumentFile getParentFile() {
        return this.mParent;
    }
    
    public abstract String getType();
    
    public abstract Uri getUri();
    
    public abstract boolean isDirectory();
    
    public abstract boolean isFile();
    
    public abstract boolean isVirtual();
    
    public abstract long lastModified();
    
    public abstract long length();
    
    public abstract DocumentFile[] listFiles();
    
    public abstract boolean renameTo(final String p0);
}
