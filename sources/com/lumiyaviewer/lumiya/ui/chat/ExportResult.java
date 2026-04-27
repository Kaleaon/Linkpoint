// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import java.io.File;

class ExportResult
{

    final File outputFile;
    final String rawText;
    final String rawTextTitle;

    ExportResult(File file, String s, String s1)
    {
        outputFile = file;
        rawText = s;
        rawTextTitle = s1;
    }
}
