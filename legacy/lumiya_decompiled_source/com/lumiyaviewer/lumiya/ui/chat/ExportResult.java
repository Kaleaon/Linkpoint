package com.lumiyaviewer.lumiya.ui.chat;

import java.io.File;
/* loaded from: classes.dex */
class ExportResult {
    final File outputFile;
    final String rawText;
    final String rawTextTitle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExportResult(File file, String str, String str2) {
        this.outputFile = file;
        this.rawText = str;
        this.rawTextTitle = str2;
    }
}
