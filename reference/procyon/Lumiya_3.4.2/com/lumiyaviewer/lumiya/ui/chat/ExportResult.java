// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import java.io.File;

class ExportResult
{
    final File outputFile;
    final String rawText;
    final String rawTextTitle;
    
    ExportResult(final File outputFile, final String rawText, final String rawTextTitle) {
        this.outputFile = outputFile;
        this.rawText = rawText;
        this.rawTextTitle = rawTextTitle;
    }
}
