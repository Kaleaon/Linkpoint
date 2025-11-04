// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import android.util.Log;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

public class AtomicFile
{
    private final File mBackupName;
    private final File mBaseName;
    
    public AtomicFile(final File mBaseName) {
        this.mBaseName = mBaseName;
        this.mBackupName = new File(mBaseName.getPath() + ".bak");
    }
    
    static boolean sync(final FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.getFD().sync();
            }
            catch (final IOException ex) {
                return false;
            }
        }
        return true;
    }
    
    public void delete() {
        this.mBaseName.delete();
        this.mBackupName.delete();
    }
    
    public void failWrite(final FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            sync(fileOutputStream);
            try {
                fileOutputStream.close();
                this.mBaseName.delete();
                this.mBackupName.renameTo(this.mBaseName);
            }
            catch (final IOException ex) {
                Log.w("AtomicFile", "failWrite: Got exception:", (Throwable)ex);
            }
        }
    }
    
    public void finishWrite(final FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            sync(fileOutputStream);
            try {
                fileOutputStream.close();
                this.mBackupName.delete();
            }
            catch (final IOException ex) {
                Log.w("AtomicFile", "finishWrite: Got exception:", (Throwable)ex);
            }
        }
    }
    
    public File getBaseFile() {
        return this.mBaseName;
    }
    
    public FileInputStream openRead() throws FileNotFoundException {
        if (this.mBackupName.exists()) {
            this.mBaseName.delete();
            this.mBackupName.renameTo(this.mBaseName);
        }
        return new FileInputStream(this.mBaseName);
    }
    
    public byte[] readFully() throws IOException {
        int off = 0;
        final FileInputStream openRead = this.openRead();
        try {
            byte[] b = new byte[openRead.available()];
            while (true) {
                final int read = openRead.read(b, off, b.length - off);
                if (read <= 0) {
                    break;
                }
                off += read;
                final int available = openRead.available();
                if (available <= b.length - off) {
                    continue;
                }
                final byte[] array = new byte[available + off];
                System.arraycopy(b, 0, array, 0, off);
                b = array;
            }
            return b;
        }
        finally {
            openRead.close();
        }
    }
    
    public FileOutputStream startWrite() throws IOException {
        Label_0024: {
            if (this.mBaseName.exists()) {
                break Label_0024;
            }
            try {
                final FileOutputStream fileOutputStream;
                Label_0010: {
                    fileOutputStream = new FileOutputStream(this.mBaseName);
                }
                return fileOutputStream;
                iftrue(Label_0045:)(!this.mBackupName.exists());
                Block_2: {
                    break Block_2;
                    Label_0045:
                    iftrue(Label_0010:)(this.mBaseName.renameTo(this.mBackupName));
                    Log.w("AtomicFile", "Couldn't rename file " + this.mBaseName + " to backup file " + this.mBackupName);
                    return new FileOutputStream(this.mBaseName);
                }
                this.mBaseName.delete();
                return new FileOutputStream(this.mBaseName);
            }
            catch (final FileNotFoundException ex) {
                if (this.mBaseName.getParentFile().mkdirs()) {
                    try {
                        return new FileOutputStream(this.mBaseName);
                    }
                    catch (final FileNotFoundException ex2) {
                        throw new IOException("Couldn't create " + this.mBaseName);
                    }
                }
                throw new IOException("Couldn't create directory " + this.mBaseName);
            }
        }
    }
}
