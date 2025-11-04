// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import java.util.ArrayList;
import android.net.Uri;
import java.io.IOException;
import android.webkit.MimeTypeMap;
import android.util.Log;
import java.io.File;

class RawDocumentFile extends DocumentFile
{
    private File mFile;
    
    RawDocumentFile(final DocumentFile documentFile, final File mFile) {
        super(documentFile);
        this.mFile = mFile;
    }
    
    private static boolean deleteContents(File obj) {
        final File[] listFiles = obj.listFiles();
        int n = 1;
        boolean b = true;
        if (listFiles != null) {
            final int length = listFiles.length;
            int n2 = 0;
            while (true) {
                b = (n != 0);
                if (n2 >= length) {
                    break;
                }
                obj = listFiles[n2];
                if (obj.isDirectory()) {
                    n &= (deleteContents(obj) ? 1 : 0);
                }
                if (!obj.delete()) {
                    Log.w("DocumentFile", "Failed to delete " + obj);
                    n = (false ? 1 : 0);
                }
                ++n2;
            }
        }
        return b;
    }
    
    private static String getTypeForName(String s) {
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex >= 0) {
            s = s.substring(lastIndex + 1).toLowerCase();
            s = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s);
            if (s != null) {
                return s;
            }
        }
        return "application/octet-stream";
    }
    
    @Override
    public boolean canRead() {
        return this.mFile.canRead();
    }
    
    @Override
    public boolean canWrite() {
        return this.mFile.canWrite();
    }
    
    @Override
    public DocumentFile createDirectory(final String child) {
        final File file = new File(this.mFile, child);
        if (!file.isDirectory() && !file.mkdir()) {
            return null;
        }
        return new RawDocumentFile(this, file);
    }
    
    @Override
    public DocumentFile createFile(String extensionFromMimeType, String string) {
        extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(extensionFromMimeType);
        Label_0042: {
            if (extensionFromMimeType != null) {
                break Label_0042;
            }
            while (true) {
                final File file = new File(this.mFile, string);
                try {
                    file.createNewFile();
                    return new RawDocumentFile(this, file);
                    string = string + "." + extensionFromMimeType;
                }
                catch (final IOException obj) {
                    Log.w("DocumentFile", "Failed to createFile: " + obj);
                    return null;
                }
            }
        }
    }
    
    @Override
    public boolean delete() {
        deleteContents(this.mFile);
        return this.mFile.delete();
    }
    
    @Override
    public boolean exists() {
        return this.mFile.exists();
    }
    
    @Override
    public String getName() {
        return this.mFile.getName();
    }
    
    @Override
    public String getType() {
        if (!this.mFile.isDirectory()) {
            return getTypeForName(this.mFile.getName());
        }
        return null;
    }
    
    @Override
    public Uri getUri() {
        return Uri.fromFile(this.mFile);
    }
    
    @Override
    public boolean isDirectory() {
        return this.mFile.isDirectory();
    }
    
    @Override
    public boolean isFile() {
        return this.mFile.isFile();
    }
    
    @Override
    public boolean isVirtual() {
        return false;
    }
    
    @Override
    public long lastModified() {
        return this.mFile.lastModified();
    }
    
    @Override
    public long length() {
        return this.mFile.length();
    }
    
    @Override
    public DocumentFile[] listFiles() {
        final ArrayList list = new ArrayList();
        final File[] listFiles = this.mFile.listFiles();
        if (listFiles != null) {
            for (int length = listFiles.length, i = 0; i < length; ++i) {
                list.add(new RawDocumentFile(this, listFiles[i]));
            }
        }
        return list.toArray(new DocumentFile[list.size()]);
    }
    
    @Override
    public boolean renameTo(final String child) {
        final File file = new File(this.mFile.getParentFile(), child);
        if (!this.mFile.renameTo(file)) {
            return false;
        }
        this.mFile = file;
        return true;
    }
}
