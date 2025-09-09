package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.Debug;
import java.io.File;

public class FileUtils {
    public static void clearFolder(File file) {
        File[] listFiles;
        try {
            if (file.exists() && (listFiles = file.listFiles()) != null) {
                for (File file2 : listFiles) {
                    if (file2 != null && !file2.getName().equals("..") && !file2.getName().equals(".")) {
                        if (file2.isDirectory()) {
                            clearFolder(file2);
                            Debug.Printf("ClearCache: Deleting directory %s", file2.getAbsolutePath());
                            file2.delete();
                        } else if (file2.isFile()) {
                            Debug.Printf("ClearCache: Deleting file %s", file2.getAbsolutePath());
                            file2.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
