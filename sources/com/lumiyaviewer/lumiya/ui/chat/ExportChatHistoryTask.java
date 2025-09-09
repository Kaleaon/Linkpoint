package com.lumiyaviewer.lumiya.ui.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExportChatHistoryTask extends AsyncTask<ChatterID, Void, ExportResult> implements DialogInterface.OnCancelListener {
    private static final String forbiddenChars = "./\\*?:\"'~";
    private final Context context;
    private final AtomicReference<String> gotChatterName = new AtomicReference<>();
    private final AtomicBoolean isNameReady = new AtomicBoolean();
    private final Condition nameReadyCondition = this.nameReadyLock.newCondition();
    private final Lock nameReadyLock = new ReentrantLock();
    private final ChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated = new $Lambda$D705oXX7BTh_Xc4P_mIDvS9cOZI(this);
    private ProgressDialog progressDialog;

    public ExportChatHistoryTask(Context context2) {
        this.context = context2;
    }

    private String sanitizeName(String str) {
        if (str == null) {
            str = "";
        }
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt < ' ') {
                charAt = ' ';
            }
            if (charAt > 127) {
                charAt = '_';
            }
            if (forbiddenChars.indexOf(charAt) < 0) {
                sb.append(charAt);
            }
        }
        String trim = sb.toString().trim();
        return trim.isEmpty() ? "Chat Log" : trim;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0130 A[SYNTHETIC, Splitter:B:59:0x0130] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0135 A[Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.lumiyaviewer.lumiya.ui.chat.ExportResult doInBackground(com.lumiyaviewer.lumiya.slproto.users.ChatterID... r14) {
        /*
            r13 = this;
            r8 = 3
            r12 = 0
            r2 = 0
            int r0 = r14.length
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return r2
        L_0x0008:
            r5 = r14[r12]
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r6 = r5.getUserManager()
            if (r6 != 0) goto L_0x0011
            return r2
        L_0x0011:
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r0 = new com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
            com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever$OnChatterNameUpdated r1 = r13.onChatterNameUpdated
            java.util.concurrent.Executor r3 = com.lumiyaviewer.lumiya.react.UIThreadExecutor.getInstance()
            r0.<init>(r5, r1, r3)
            java.util.concurrent.locks.Lock r1 = r13.nameReadyLock     // Catch:{ all -> 0x0037 }
            r1.lock()     // Catch:{ all -> 0x0037 }
        L_0x0021:
            java.util.concurrent.atomic.AtomicBoolean r1 = r13.isNameReady     // Catch:{ all -> 0x0037 }
            boolean r1 = r1.get()     // Catch:{ all -> 0x0037 }
            if (r1 != 0) goto L_0x0040
            boolean r1 = r13.isCancelled()     // Catch:{ all -> 0x0037 }
            r1 = r1 ^ 1
            if (r1 == 0) goto L_0x0040
            java.util.concurrent.locks.Condition r1 = r13.nameReadyCondition     // Catch:{ all -> 0x0037 }
            r1.await()     // Catch:{ all -> 0x0037 }
            goto L_0x0021
        L_0x0037:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r13.nameReadyLock     // Catch:{ InterruptedException -> 0x003e }
            r1.unlock()     // Catch:{ InterruptedException -> 0x003e }
            throw r0     // Catch:{ InterruptedException -> 0x003e }
        L_0x003e:
            r0 = move-exception
            return r2
        L_0x0040:
            java.util.concurrent.locks.Lock r1 = r13.nameReadyLock     // Catch:{ InterruptedException -> 0x003e }
            r1.unlock()     // Catch:{ InterruptedException -> 0x003e }
            r0.dispose()
            boolean r0 = r13.isCancelled()
            if (r0 == 0) goto L_0x004f
            return r2
        L_0x004f:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 19
            if (r0 < r1) goto L_0x0127
            java.lang.String r0 = android.os.Environment.DIRECTORY_DOCUMENTS
        L_0x0057:
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)
            java.io.File r1 = new java.io.File
            java.lang.String r3 = "Lumiya"
            r1.<init>(r0, r3)
            java.io.File r3 = new java.io.File
            java.lang.String r0 = "Chat Logs"
            r3.<init>(r1, r0)
            java.io.File r4 = new java.io.File
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.util.concurrent.atomic.AtomicReference<java.lang.String> r0 = r13.gotChatterName
            java.lang.Object r0 = r0.get()
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r0 = r13.sanitizeName(r0)
            java.lang.StringBuilder r0 = r7.append(r0)
            java.lang.String r7 = ".txt"
            java.lang.StringBuilder r0 = r0.append(r7)
            java.lang.String r0 = r0.toString()
            r4.<init>(r3, r0)
            java.text.DateFormat r7 = java.text.DateFormat.getDateTimeInstance(r8, r8)
            r1.mkdirs()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
            r3.mkdirs()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x012b }
            r0 = 0
            r1.<init>(r4, r0)     // Catch:{ all -> 0x012b }
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r0 = r6.getChatterList()     // Catch:{ all -> 0x01dd }
            com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager r0 = r0.getActiveChattersManager()     // Catch:{ all -> 0x01dd }
            de.greenrobot.dao.query.LazyList r3 = r0.getMessages(r5)     // Catch:{ all -> 0x01dd }
            if (r3 == 0) goto L_0x010e
            java.util.Iterator r8 = r3.iterator()     // Catch:{ all -> 0x01e1 }
        L_0x00b2:
            boolean r0 = r8.hasNext()     // Catch:{ all -> 0x01e1 }
            if (r0 == 0) goto L_0x010e
            java.lang.Object r0 = r8.next()     // Catch:{ all -> 0x01e1 }
            com.lumiyaviewer.lumiya.dao.ChatMessage r0 = (com.lumiyaviewer.lumiya.dao.ChatMessage) r0     // Catch:{ all -> 0x01e1 }
            java.util.UUID r9 = r6.getUserID()     // Catch:{ all -> 0x01e1 }
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r0 = com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.loadFromDatabaseObject(r0, r9)     // Catch:{ all -> 0x01e1 }
            if (r0 == 0) goto L_0x0108
            java.util.Date r9 = r0.getTimestamp()     // Catch:{ all -> 0x01e1 }
            java.lang.String r9 = r7.format(r9)     // Catch:{ all -> 0x01e1 }
            android.content.Context r10 = r13.context     // Catch:{ all -> 0x01e1 }
            r11 = 0
            java.lang.CharSequence r0 = r0.getPlainTextMessage(r10, r6, r11)     // Catch:{ all -> 0x01e1 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e1 }
            r10.<init>()     // Catch:{ all -> 0x01e1 }
            java.lang.String r11 = "["
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x01e1 }
            java.lang.StringBuilder r9 = r10.append(r9)     // Catch:{ all -> 0x01e1 }
            java.lang.String r10 = "] "
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ all -> 0x01e1 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e1 }
            java.lang.StringBuilder r0 = r9.append(r0)     // Catch:{ all -> 0x01e1 }
            java.lang.String r9 = "\n"
            java.lang.StringBuilder r0 = r0.append(r9)     // Catch:{ all -> 0x01e1 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e1 }
            byte[] r0 = r0.getBytes()     // Catch:{ all -> 0x01e1 }
            r1.write(r0)     // Catch:{ all -> 0x01e1 }
        L_0x0108:
            boolean r0 = r13.isCancelled()     // Catch:{ all -> 0x01e1 }
            if (r0 == 0) goto L_0x00b2
        L_0x010e:
            if (r3 == 0) goto L_0x0113
            r3.close()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
        L_0x0113:
            if (r1 == 0) goto L_0x0118
            r1.close()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
        L_0x0118:
            r0 = r4
        L_0x0119:
            boolean r1 = r13.isCancelled()
            if (r1 != 0) goto L_0x01dc
            if (r0 == 0) goto L_0x014b
            com.lumiyaviewer.lumiya.ui.chat.ExportResult r1 = new com.lumiyaviewer.lumiya.ui.chat.ExportResult
            r1.<init>(r0, r2, r2)
            return r1
        L_0x0127:
            java.lang.String r0 = android.os.Environment.DIRECTORY_DOWNLOADS
            goto L_0x0057
        L_0x012b:
            r0 = move-exception
            r1 = r2
            r3 = r2
        L_0x012e:
            if (r3 == 0) goto L_0x0133
            r3.close()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
        L_0x0133:
            if (r1 == 0) goto L_0x0138
            r1.close()     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
        L_0x0138:
            throw r0     // Catch:{ SecurityException -> 0x0139, IOException -> 0x0145, Exception -> 0x013f }
        L_0x0139:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)
            r0 = r2
            goto L_0x0119
        L_0x013f:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)
            r0 = r2
            goto L_0x0119
        L_0x0145:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)
            r0 = r2
            goto L_0x0119
        L_0x014b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.util.concurrent.atomic.AtomicReference<java.lang.String> r0 = r13.gotChatterName
            java.lang.Object r0 = r0.get()
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r0 = r13.sanitizeName(r0)
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.String r1 = ".txt"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = r0.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r0 = r6.getChatterList()
            com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager r0 = r0.getActiveChattersManager()
            de.greenrobot.dao.query.LazyList r0 = r0.getMessages(r5)
            if (r0 == 0) goto L_0x01cc
            java.util.Iterator r4 = r0.iterator()
        L_0x0182:
            boolean r0 = r4.hasNext()
            if (r0 == 0) goto L_0x01cc
            java.lang.Object r0 = r4.next()
            com.lumiyaviewer.lumiya.dao.ChatMessage r0 = (com.lumiyaviewer.lumiya.dao.ChatMessage) r0
            java.util.UUID r5 = r6.getUserID()
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r0 = com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.loadFromDatabaseObject(r0, r5)
            if (r0 == 0) goto L_0x01c6
            java.util.Date r5 = r0.getTimestamp()
            java.lang.String r5 = r7.format(r5)
            android.content.Context r8 = r13.context
            java.lang.CharSequence r0 = r0.getPlainTextMessage(r8, r6, r12)
            java.lang.String r8 = "["
            java.lang.StringBuilder r8 = r3.append(r8)
            java.lang.StringBuilder r5 = r8.append(r5)
            java.lang.String r8 = "] "
            java.lang.StringBuilder r5 = r5.append(r8)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r0 = r5.append(r0)
            java.lang.String r5 = "\n"
            r0.append(r5)
        L_0x01c6:
            boolean r0 = r13.isCancelled()
            if (r0 == 0) goto L_0x0182
        L_0x01cc:
            java.lang.String r0 = r3.toString()
            boolean r3 = r13.isCancelled()
            if (r3 != 0) goto L_0x01dc
            com.lumiyaviewer.lumiya.ui.chat.ExportResult r3 = new com.lumiyaviewer.lumiya.ui.chat.ExportResult
            r3.<init>(r2, r0, r1)
            return r3
        L_0x01dc:
            return r2
        L_0x01dd:
            r0 = move-exception
            r3 = r2
            goto L_0x012e
        L_0x01e1:
            r0 = move-exception
            goto L_0x012e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.chat.ExportChatHistoryTask.doInBackground(com.lumiyaviewer.lumiya.slproto.users.ChatterID[]):com.lumiyaviewer.lumiya.ui.chat.ExportResult");
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_ExportChatHistoryTask_2020  reason: not valid java name */
    public /* synthetic */ void m421lambda$com_lumiyaviewer_lumiya_ui_chat_ExportChatHistoryTask_2020(ChatterNameRetriever chatterNameRetriever) {
        try {
            this.nameReadyLock.lock();
            this.isNameReady.set(true);
            this.gotChatterName.set(chatterNameRetriever.getResolvedName());
            this.nameReadyCondition.signal();
        } finally {
            this.nameReadyLock.unlock();
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        cancel(false);
        try {
            this.nameReadyLock.lock();
            this.nameReadyCondition.signal();
        } finally {
            this.nameReadyLock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void onCancelled() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(ExportResult exportResult) {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        if (exportResult != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("text/plain");
            if (exportResult.outputFile != null) {
                Debug.Printf("Export: exported as stream %s", exportResult.outputFile);
                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(exportResult.outputFile));
            } else {
                Debug.Printf("Export: exported as text, %d bytes", Integer.valueOf(exportResult.rawText.length()));
                intent.putExtra("android.intent.extra.TEXT", exportResult.rawText);
                intent.putExtra("android.intent.extra.SUBJECT", exportResult.rawTextTitle);
            }
            this.context.startActivity(Intent.createChooser(intent, this.context.getText(R.string.export_chat_history_to)));
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        this.progressDialog = ProgressDialog.show(this.context, this.context.getString(R.string.please_wait_title), this.context.getString(R.string.exporting_chat_history), true, true, this);
    }
}
