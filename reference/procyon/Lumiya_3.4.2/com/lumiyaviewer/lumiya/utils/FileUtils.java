// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.Debug;
import java.io.File;

public class FileUtils
{
    public static void clearFolder(final File file) {
        while (true) {
            int i = 0;
            while (true) {
                File file2 = null;
                Label_0101: {
                    try {
                        if (file.exists()) {
                            final File[] listFiles = file.listFiles();
                            if (listFiles != null) {
                                while (i < listFiles.length) {
                                    file2 = listFiles[i];
                                    if (file2 != null && !file2.getName().equals("..") && !file2.getName().equals(".")) {
                                        if (!file2.isDirectory()) {
                                            break Label_0101;
                                        }
                                        clearFolder(file2);
                                        Debug.Printf("ClearCache: Deleting directory %s", file2.getAbsolutePath());
                                        file2.delete();
                                    }
                                    ++i;
                                }
                            }
                        }
                    }
                    catch (final Exception ex) {}
                    break;
                }
                if (file2.isFile()) {
                    Debug.Printf("ClearCache: Deleting file %s", file2.getAbsolutePath());
                    file2.delete();
                    continue;
                }
                continue;
            }
        }
    }
}
