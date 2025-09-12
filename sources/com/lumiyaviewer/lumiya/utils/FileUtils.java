// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.Debug;
import java.io.File;

public class FileUtils
{

    public FileUtils()
    {
    }

    public static void clearFolder(File file)
    {
        int i = 0;
        if (!file.exists()) goto _L2; else goto _L1
_L1:
        file = file.listFiles();
        if (file == null) goto _L2; else goto _L3
_L3:
        File file1;
        int j;
        try
        {
            j = file.length;
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            break; /* Loop/switch isn't completed */
        }
_L4:
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        file1 = file[i];
        if (file1 == null)
        {
            break MISSING_BLOCK_LABEL_124;
        }
        if (file1.getName().equals("..") || file1.getName().equals("."))
        {
            break MISSING_BLOCK_LABEL_124;
        }
        if (file1.isDirectory())
        {
            clearFolder(file1);
            Debug.Printf("ClearCache: Deleting directory %s", new Object[] {
                file1.getAbsolutePath()
            });
            file1.delete();
            break MISSING_BLOCK_LABEL_124;
        }
        if (file1.isFile())
        {
            Debug.Printf("ClearCache: Deleting file %s", new Object[] {
                file1.getAbsolutePath()
            });
            file1.delete();
        }
        i++;
        continue; /* Loop/switch isn't completed */
        if (true) goto _L4; else goto _L2
_L2:
    }
}
