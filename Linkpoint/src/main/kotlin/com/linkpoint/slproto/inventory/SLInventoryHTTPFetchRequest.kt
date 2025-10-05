package com.linkpoint.slproto.inventory

import com.linkpoint.Debug
import com.linkpoint.slproto.https.GenericHTTPExecutor
import com.linkpoint.slproto.https.LLSDStreamingXMLRequest
import com.linkpoint.slproto.inventory.SLInventory
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDStreamingParser
import com.linkpoint.slproto.llsd.LLSDValueTypeException
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.util.HashMap
import java.util.Map
import java.util.UUID
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class SLInventoryHTTPFetchRequest : SLInventoryFetchRequest() {
    /* access modifiers changed from: private */
    val String capURL
    private val AtomicReference<Future<?>> futureRef = AtomicReference<>((Object) null)
    private val Runnable httpRequest = Runnable() {
        /* JADX WARNING: Removed duplicated region for block: B:16:0x00f9  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0114  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x0170  */
        /* JADX WARNING: Removed duplicated region for block: B:40:0x0172  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public Unit run() {
            /*
                r15 = this
                r14 = 3
                r13 = 2
                r2 = 0
                r3 = 1
                Long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0176 }
                java.lang.String r0 = "InventoryFetcher: Going to fetch folder: %s"
                r1 = 1
                java.lang.Object[] r1 = java.lang.Object[r1]     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r4 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x0176 }
                java.util.UUID r4 = r4.folderUUID     // Catch:{ Exception -> 0x0176 }
                r5 = 0
                r1[r5] = r4     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.https.LLSDStreamingXMLRequest r5 = com.lumiyaviewer.lumiya.slproto.https.LLSDStreamingXMLRequest     // Catch:{ Exception -> 0x0176 }
                r5.<init>()     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray     // Catch:{ Exception -> 0x0176 }
                r0.<init>()     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap     // Catch:{ Exception -> 0x0176 }
                r4 = 3
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry[] r4 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[r4]     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry r8 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry     // Catch:{ Exception -> 0x0176 }
                java.lang.String r9 = "folder_id"
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID r10 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r11 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x0176 }
                java.util.UUID r11 = r11.folderUUID     // Catch:{ Exception -> 0x0176 }
                r10.<init>((java.util.UUID) r11)     // Catch:{ Exception -> 0x0176 }
                r8.<init>(r9, r10)     // Catch:{ Exception -> 0x0176 }
                r9 = 0
                r4[r9] = r8     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry r8 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry     // Catch:{ Exception -> 0x0176 }
                java.lang.String r9 = "fetch_folders"
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r10 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean     // Catch:{ Exception -> 0x0176 }
                r11 = 1
                r10.<init>((Boolean) r11)     // Catch:{ Exception -> 0x0176 }
                r8.<init>(r9, r10)     // Catch:{ Exception -> 0x0176 }
                r9 = 1
                r4[r9] = r8     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry r8 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry     // Catch:{ Exception -> 0x0176 }
                java.lang.String r9 = "fetch_items"
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r10 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean     // Catch:{ Exception -> 0x0176 }
                r11 = 1
                r10.<init>((Boolean) r11)     // Catch:{ Exception -> 0x0176 }
                r8.<init>(r9, r10)     // Catch:{ Exception -> 0x0176 }
                r9 = 2
                r4[r9] = r8     // Catch:{ Exception -> 0x0176 }
                r1.<init>((com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[]) r4)     // Catch:{ Exception -> 0x0176 }
                r0.add(r1)     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap r8 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap     // Catch:{ Exception -> 0x0176 }
                r1 = 1
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry[] r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[r1]     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry r4 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap$LLSDMapEntry     // Catch:{ Exception -> 0x0176 }
                java.lang.String r9 = "folders"
                r4.<init>(r9, r0)     // Catch:{ Exception -> 0x0176 }
                r0 = 0
                r1[r0] = r4     // Catch:{ Exception -> 0x0176 }
                r8.<init>((com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[]) r1)     // Catch:{ Exception -> 0x0176 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x0176 }
                java.util.concurrent.atomic.AtomicReference r0 = r0.streamingXmlReqRef     // Catch:{ Exception -> 0x0176 }
                r0.set(r5)     // Catch:{ Exception -> 0x0176 }
                r4 = r2
                r1 = r2
            L_0x0081:
                if (r4 >= r14) goto L_0x00c8
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest$DatabaseCommitThread r9 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest$DatabaseCommitThread     // Catch:{ Exception -> 0x012d }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x012d }
                r10 = 0
                r9.<init>(r0, r10)     // Catch:{ Exception -> 0x012d }
                r9.start()     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                java.lang.String r0 = "InventoryFetcher: Starting HTTP request for folder: %s"
                r10 = 1
                java.lang.Object[] r10 = java.lang.Object[r10]     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r11 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                java.util.UUID r11 = r11.folderUUID     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                r12 = 0
                r10[r12] = r11     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r10)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                java.lang.String r0 = r0.capURL     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest$RootContentHandler r10 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest$RootContentHandler     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r11 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                r12 = 0
                r10.<init>(r11, r9, r12)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                r5.PerformRequest(r0, r8, r10)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                java.lang.String r0 = "InvFetch: done parsing,  waiting for commit thread"
                r10 = 0
                java.lang.Object[] r10 = java.lang.Object[r10]     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r10)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                r0 = 1
                r9.stopAndWait(r0)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                java.lang.String r0 = "InvFetch: commit thread finished"
                r10 = 0
                java.lang.Object[] r10 = java.lang.Object[r10]     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r10)     // Catch:{ LLSDXMLException -> 0x0132, IOException -> 0x0128 }
                r1 = r3
            L_0x00c6:
                if (r1 == 0) goto L_0x0156
            L_0x00c8:
                java.lang.String r0 = "InventoryFetcher: Fetched folder: %s (fetch time = %d)"
                r4 = 2
                java.lang.Object[] r4 = java.lang.Object[r4]     // Catch:{ Exception -> 0x012d }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r5 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x012d }
                java.util.UUID r5 = r5.folderUUID     // Catch:{ Exception -> 0x012d }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x012d }
                r8 = 0
                r4[r8] = r5     // Catch:{ Exception -> 0x012d }
                Long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x012d }
                Long r6 = r8 - r6
                java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x012d }
                r6 = 1
                r4[r6] = r5     // Catch:{ Exception -> 0x012d }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)     // Catch:{ Exception -> 0x012d }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x012d }
                java.util.concurrent.atomic.AtomicReference r0 = r0.streamingXmlReqRef     // Catch:{ Exception -> 0x012d }
                r4 = 0
                r0.set(r4)     // Catch:{ Exception -> 0x012d }
            L_0x00f3:
                Boolean r0 = java.lang.Thread.interrupted()
                if (r0 != 0) goto L_0x0170
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                java.util.concurrent.atomic.AtomicBoolean r0 = r0.isCancelled
                Boolean r0 = r0.get()
            L_0x0103:
                java.lang.String r4 = "InventoryFetcher: done processing folder %s: success %s cancelled %b"
                java.lang.Object[] r5 = java.lang.Object[r14]
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r6 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                java.util.UUID r6 = r6.folderUUID
                java.lang.String r6 = r6.toString()
                r5[r2] = r6
                if (r1 == 0) goto L_0x0172
                java.lang.String r2 = "true"
            L_0x0117:
                r5[r3] = r2
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r0)
                r5[r13] = r2
                com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r2 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                r2.completeFetch(r1, r0)
                return
            L_0x0128:
                r0 = move-exception
                r0.printStackTrace()     // Catch:{ Exception -> 0x012d }
                goto L_0x00c6
            L_0x012d:
                r0 = move-exception
            L_0x012e:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                goto L_0x00f3
            L_0x0132:
                r0 = move-exception
                r0.printStackTrace()     // Catch:{ Exception -> 0x012d }
                java.lang.StringBuilder r0 = java.lang.StringBuilder     // Catch:{ Exception -> 0x0153 }
                r0.<init>()     // Catch:{ Exception -> 0x0153 }
                java.lang.String r10 = "InventoryFetcher: malformed xml after req = "
                java.lang.StringBuilder r0 = r0.append(r10)     // Catch:{ Exception -> 0x0153 }
                java.lang.String r10 = r8.serializeToXML()     // Catch:{ Exception -> 0x0153 }
                java.lang.StringBuilder r0 = r0.append(r10)     // Catch:{ Exception -> 0x0153 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0153 }
                com.lumiyaviewer.lumiya.Debug.Log(r0)     // Catch:{ Exception -> 0x0153 }
                goto L_0x00c6
            L_0x0153:
                r0 = move-exception
                goto L_0x00c6
            L_0x0156:
                r9.interrupt()     // Catch:{ Exception -> 0x012d }
                Boolean r0 = java.lang.Thread.interrupted()     // Catch:{ Exception -> 0x012d }
                if (r0 != 0) goto L_0x00c8
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ Exception -> 0x012d }
                java.util.concurrent.atomic.AtomicBoolean r0 = r0.isCancelled     // Catch:{ Exception -> 0x012d }
                Boolean r0 = r0.get()     // Catch:{ Exception -> 0x012d }
                if (r0 != 0) goto L_0x00c8
                Int r0 = r4 + 1
                r4 = r0
                goto L_0x0081
            L_0x0170:
                r0 = r3
                goto L_0x0103
            L_0x0172:
                java.lang.String r2 = "false"
                goto L_0x0117
            L_0x0176:
                r0 = move-exception
                r1 = r2
                goto L_0x012e
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.AnonymousClass1.run():Unit")
        }
    }
    /* access modifiers changed from: private */
    val AtomicBoolean isCancelled = AtomicBoolean(false)
    /* access modifiers changed from: private */
    val AtomicReference<LLSDStreamingXMLRequest> streamingXmlReqRef = AtomicReference<>((Object) null)

    private val class DatabaseCommitThread : Thread() {
        private volatile Boolean aborted
        private val BlockingQueue<SLInventoryEntry> commitEntryQueue
        private val SLInventoryEntry stopEntry

        private DatabaseCommitThread() {
            this.commitEntryQueue = LinkedBlockingQueue(100)
            this.stopEntry = SLInventoryEntry()
            this.aborted = false
        }

        /* synthetic */ DatabaseCommitThread(SLInventoryHTTPFetchRequest sLInventoryHTTPFetchRequest, DatabaseCommitThread databaseCommitThread) {
            this()
        }

        /* access modifiers changed from: package-private */
        public Unit addEntry(SLInventoryEntry sLInventoryEntry) throws InterruptedException {
            this.commitEntryQueue.put(sLInventoryEntry)
        }

        /* JADX WARNING: Removed duplicated region for block: B:16:0x0045  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x006f  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0074  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public Unit run() {
            /*
                r11 = this
                r3 = 1
                r1 = 0
                r4 = 0
                java.util.HashSet r8 = java.util.HashSet
                r8.<init>()
                r2 = r1
                r5 = r4
                r6 = r4
            L_0x000b:
                Boolean r0 = java.lang.Thread.interrupted()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                if (r0 != 0) goto L_0x003b
                java.util.concurrent.BlockingQueue<com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry> r0 = r11.commitEntryQueue     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                java.lang.Object r0 = r0.poll()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r0 = (com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry) r0     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                if (r0 != 0) goto L_0x010c
                if (r5 == 0) goto L_0x002d
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r0.setTransactionSuccessful()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r0.endTransaction()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r5 = r4
                r6 = r4
            L_0x002d:
                java.util.concurrent.BlockingQueue<com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry> r0 = r11.commitEntryQueue     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                java.lang.Object r0 = r0.take()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r0 = (com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry) r0     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r7 = r0
                r0 = r6
            L_0x0037:
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r6 = r11.stopEntry     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                if (r7 != r6) goto L_0x0093
            L_0x003b:
                Boolean r0 = java.lang.Thread.interrupted()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                if (r0 != 0) goto L_0x0105
                r0 = r3
            L_0x0042:
                r6 = r0
            L_0x0043:
                if (r5 == 0) goto L_0x006d
                java.lang.String r5 = "InvFetch: commit thread ending transaction (success: %s, count %d)."
                r0 = 2
                java.lang.Object[] r7 = java.lang.Object[r0]
                if (r6 == 0) goto L_0x00f6
                java.lang.String r0 = "true"
            L_0x0050:
                r7[r4] = r0
                Int r0 = r8.size()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r7[r3] = r0
                com.lumiyaviewer.lumiya.Debug.Printf(r5, r7)
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db
                r0.setTransactionSuccessful()
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db
                r0.endTransaction()
            L_0x006d:
                if (r2 == 0) goto L_0x0072
                r2.close()
            L_0x0072:
                if (r1 == 0) goto L_0x0077
                r1.close()
            L_0x0077:
                if (r6 == 0) goto L_0x0092
                Boolean r0 = r11.aborted
                r0 = r0 ^ 1
                if (r0 == 0) goto L_0x0092
                java.lang.String r0 = "InvFetch: commit thread successful, calling retainChildren."
                java.lang.Object[] r1 = java.lang.Object[r4]
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r1 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this
                Long r2 = r1.folderId
                r0.retainChildren(r2, r8)
            L_0x0092:
                return
            L_0x0093:
                if (r5 != 0) goto L_0x009d
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r6 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r6 = r6.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r6.beginTransaction()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r5 = r3
            L_0x009d:
                Int r0 = r0 + 1
                r6 = 16
                if (r0 < r6) goto L_0x010a
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r0.yieldIfContendedSafely()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r6 = r4
            L_0x00ab:
                java.util.UUID r0 = r7.uuid     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r8.add(r0)     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                Int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r9 = 11
                if (r0 < r9) goto L_0x00da
                if (r2 != 0) goto L_0x00c4
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                android.database.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                android.database.sqlite.SQLiteStatement r2 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry.getInsertStatement(r0)     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
            L_0x00c4:
                if (r1 != 0) goto L_0x0108
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                android.database.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                android.database.sqlite.SQLiteStatement r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry.getUpdateStatement(r0)     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
            L_0x00d2:
                r7.updateOrInsert(r0, r2)     // Catch:{ InterruptedException -> 0x00fb, DatabaseBindingException -> 0x0100 }
                r1 = r2
            L_0x00d6:
                r2 = r1
                r1 = r0
                goto L_0x000b
            L_0x00da:
                com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.this     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                com.lumiyaviewer.lumiya.orm.InventoryDB r0 = r0.db     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                android.database.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r7.updateOrInsert(r0)     // Catch:{ InterruptedException -> 0x00ef, DatabaseBindingException -> 0x00e8 }
                r0 = r1
                r1 = r2
                goto L_0x00d6
            L_0x00e8:
                r0 = move-exception
            L_0x00e9:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                r6 = r4
                goto L_0x0043
            L_0x00ef:
                r0 = move-exception
            L_0x00f0:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                r6 = r4
                goto L_0x0043
            L_0x00f6:
                java.lang.String r0 = "false"
                goto L_0x0050
            L_0x00fb:
                r1 = move-exception
                r10 = r1
                r1 = r0
                r0 = r10
                goto L_0x00f0
            L_0x0100:
                r1 = move-exception
                r10 = r1
                r1 = r0
                r0 = r10
                goto L_0x00e9
            L_0x0105:
                r0 = r4
                goto L_0x0042
            L_0x0108:
                r0 = r1
                goto L_0x00d2
            L_0x010a:
                r6 = r0
                goto L_0x00ab
            L_0x010c:
                r7 = r0
                r0 = r6
                goto L_0x0037
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.DatabaseCommitThread.run():Unit")
        }

        /* access modifiers changed from: package-private */
        public Unit stopAndWait(Boolean z) throws InterruptedException {
            if (!z) {
                this.aborted = true
            }
            this.commitEntryQueue.put(this.stopEntry)
            join()
        }
    }

    private class FolderDataContentHandler : LLSDStreamingParser().LLSDDefaultContentHandler {
        /* access modifiers changed from: private */
        val DatabaseCommitThread commitThread
        private UUID gotUUID
        private Int gotVersion

        private FolderDataContentHandler(DatabaseCommitThread databaseCommitThread) {
            this.commitThread = databaseCommitThread
        }

        /* synthetic */ FolderDataContentHandler(SLInventoryHTTPFetchRequest sLInventoryHTTPFetchRequest, DatabaseCommitThread databaseCommitThread, FolderDataContentHandler folderDataContentHandler) {
            this(databaseCommitThread)
        }

        public LLSDStreamingParser.LLSDContentHandler onArrayBegin(String str) throws LLSDXMLException {
            return str.equals("categories") ? LLSDStreamingParser.LLSDDefaultContentHandler() {
                public LLSDStreamingParser.LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
                    return FolderEntryContentHandler(FolderDataContentHandler.this.commitThread)
                }
            } : str.equals("items") ? LLSDStreamingParser.LLSDDefaultContentHandler() {
                public LLSDStreamingParser.LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
                    return ItemEntryContentHandler(FolderDataContentHandler.this.commitThread)
                }
            } : super.onArrayBegin(str)
        }

        public Unit onMapEnd(String str) throws LLSDXMLException, InterruptedException {
            if (this.gotUUID != null && this.gotUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID) && this.gotVersion != SLInventoryHTTPFetchRequest.this.folderEntry.version) {
                SLInventoryHTTPFetchRequest.this.folderEntry.version = this.gotVersion
                this.commitThread.addEntry(SLInventoryHTTPFetchRequest.this.folderEntry)
            }
        }

        public Unit onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
            Debug.Printf("InvFetch: FolderDataContentHandler: key '%s' value '%s'", str, lLSDNode)
            if (str.equals("version")) {
                this.gotVersion = lLSDNode.asInt()
            } else if (str.equals("folder_id")) {
                this.gotUUID = lLSDNode.asUUID()
            }
        }
    }

    private class FolderEntryContentHandler : LLSDStreamingParser().LLSDDefaultContentHandler {

        /* renamed from: -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f111comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = null
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$FolderValueKey
        private val DatabaseCommitThread commitThread
        private val SLInventoryEntry entry = SLInventoryEntry()

        /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m189getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues() {
            if (f111comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues != null) {
                return f111comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues
            }
            Int[] iArr = Int[FolderValueKey.values().length]
            try {
                iArr[FolderValueKey.agent_id.ordinal()] = 1
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[FolderValueKey.category_id.ordinal()] = 2
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[FolderValueKey.folder_id.ordinal()] = 3
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[FolderValueKey.name.ordinal()] = 4
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[FolderValueKey.parent_id.ordinal()] = 5
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[FolderValueKey.preferred_type.ordinal()] = 6
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[FolderValueKey.type.ordinal()] = 7
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[FolderValueKey.type_default.ordinal()] = 8
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[FolderValueKey.version.ordinal()] = 9
            } catch (NoSuchFieldError e9) {
            }
            f111comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = iArr
            return iArr
        }

        FolderEntryContentHandler(DatabaseCommitThread databaseCommitThread) {
            this.commitThread = databaseCommitThread
            this.entry.isFolder = true
        }

        public Unit onMapEnd(String str) throws LLSDXMLException, InterruptedException {
            if (this.entry.parentUUID == null) {
                this.entry.parentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.parentUUID
                this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId()
            }
            if (this.entry.agentUUID == null) {
                this.entry.agentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.agentUUID
            }
            this.commitThread.addEntry(this.entry)
        }

        public Unit onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
            FolderValueKey byTag = FolderValueKey.byTag(str)
            if (byTag != null) {
                switch (m189getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()[byTag.ordinal()]) {
                    case 1:
                        this.entry.agentUUID = lLSDNode.asUUID()
                        return
                    case 2:
                        this.entry.uuid = lLSDNode.asUUID()
                        return
                    case 3:
                        this.entry.uuid = lLSDNode.asUUID()
                        return
                    case 4:
                        this.entry.name = lLSDNode.asString()
                        return
                    case 5:
                        this.entry.parentUUID = lLSDNode.asUUID()
                        if (this.entry.parentUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID)) {
                            this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId()
                            return
                        }
                        SLInventoryEntry findEntry = SLInventoryHTTPFetchRequest.this.db.findEntry(this.entry.parentUUID)
                        if (findEntry != null) {
                            this.entry.parent_id = findEntry.getId()
                            return
                        }
                        this.entry.parent_id = 0
                        return
                    case 7:
                        if (lLSDNode.isInt()) {
                            this.entry.typeDefault = lLSDNode.asInt()
                            return
                        }
                        SLAssetType byString = SLAssetType.getByString(lLSDNode.asString())
                        if (byString != SLAssetType.AT_UNKNOWN) {
                            this.entry.typeDefault = byString.getInventoryType().getTypeCode()
                            return
                        }
                        this.entry.typeDefault = SLInventoryType.getByString(lLSDNode.asString()).getTypeCode()
                        return
                    case 8:
                        this.entry.typeDefault = lLSDNode.asInt()
                        return
                    case 9:
                        this.entry.version = lLSDNode.asInt()
                        return
                    default:
                        return
                }
            } else {
                Debug.Printf("InvFetch: Folder unknown key '%s'", str)
            }
        }
    }

    private enum FolderValueKey {
        category_id,
        folder_id,
        agent_id,
        name,
        type_default,
        type,
        version,
        parent_id,
        preferred_type
        
        private const val Map<String, FolderValueKey> tagMap = null

        static {
            tagMap = HashMap(values().length * 2)
            for (FolderValueKey folderValueKey : values()) {
                tagMap.put(folderValueKey.toString(), folderValueKey)
            }
        }

        @JvmStatic
    FolderValueKey byTag(String str) {
            return tagMap.get(str)
        }
    }

    private class ItemEntryContentHandler : LLSDStreamingParser().LLSDDefaultContentHandler {

        /* renamed from: -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f112comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = null
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$ItemValueKey
        private val DatabaseCommitThread commitThread
        /* access modifiers changed from: private */
        val SLInventoryEntry entry
        private val LLSDStreamingParser.LLSDContentHandler permissionsHandler = LLSDStreamingParser.LLSDDefaultContentHandler() {

            /* renamed from: -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues  reason: not valid java name */
            private const val /* synthetic */ Int[] f113comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = null
            final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$PermissionsValueKey

            /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues  reason: not valid java name */
            @JvmStatic
private /* synthetic */ Int[] m192getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues() {
                if (f113comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues != null) {
                    return f113comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues
                }
                Int[] iArr = Int[PermissionsValueKey.values().length]
                try {
                    iArr[PermissionsValueKey.base_mask.ordinal()] = 1
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[PermissionsValueKey.creator_id.ordinal()] = 2
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[PermissionsValueKey.everyone_mask.ordinal()] = 3
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[PermissionsValueKey.group_id.ordinal()] = 4
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[PermissionsValueKey.group_mask.ordinal()] = 5
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[PermissionsValueKey.is_owner_group.ordinal()] = 6
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[PermissionsValueKey.last_owner_id.ordinal()] = 7
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[PermissionsValueKey.next_owner_mask.ordinal()] = 8
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[PermissionsValueKey.owner_id.ordinal()] = 9
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[PermissionsValueKey.owner_mask.ordinal()] = 10
                } catch (NoSuchFieldError e10) {
                }
                f113comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = iArr
                return iArr
            }

            public Unit onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
                PermissionsValueKey byTag = PermissionsValueKey.byTag(str)
                if (byTag != null) {
                    switch (m192getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()[byTag.ordinal()]) {
                        case 1:
                            ItemEntryContentHandler.this.entry.baseMask = lLSDNode.asInt()
                            return
                        case 2:
                            ItemEntryContentHandler.this.entry.creatorUUID = lLSDNode.asUUID()
                            return
                        case 3:
                            ItemEntryContentHandler.this.entry.everyoneMask = lLSDNode.asInt()
                            return
                        case 4:
                            ItemEntryContentHandler.this.entry.groupUUID = lLSDNode.asUUID()
                            return
                        case 5:
                            ItemEntryContentHandler.this.entry.groupMask = lLSDNode.asInt()
                            return
                        case 6:
                            ItemEntryContentHandler.this.entry.isGroupOwned = lLSDNode.asBoolean()
                            return
                        case 7:
                            ItemEntryContentHandler.this.entry.lastOwnerUUID = lLSDNode.asUUID()
                            return
                        case 8:
                            ItemEntryContentHandler.this.entry.nextOwnerMask = lLSDNode.asInt()
                            return
                        case 9:
                            ItemEntryContentHandler.this.entry.ownerUUID = lLSDNode.asUUID()
                            return
                        case 10:
                            ItemEntryContentHandler.this.entry.ownerMask = lLSDNode.asInt()
                            return
                        default:
                            return
                    }
                } else {
                    Debug.Printf("InvFetch: Permissions unknown key '%s'", str)
                }
            }
        }
        private val LLSDStreamingParser.LLSDContentHandler saleInfoHandler = LLSDStreamingParser.LLSDDefaultContentHandler() {
            public Unit onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
                if (str.equals("sale_type")) {
                    if (lLSDNode.isString()) {
                        ItemEntryContentHandler.this.entry.saleType = SLSaleType.getByString(lLSDNode.asString()).getTypeCode()
                        return
                    }
                    ItemEntryContentHandler.this.entry.saleType = lLSDNode.asInt()
                } else if (str.equals("sale_price")) {
                    ItemEntryContentHandler.this.entry.salePrice = lLSDNode.asInt()
                } else {
                    Debug.Printf("InvFetch: Sale info unknown key '%s'", str)
                }
            }
        }

        /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m191getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues() {
            if (f112comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues != null) {
                return f112comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues
            }
            Int[] iArr = Int[ItemValueKey.values().length]
            try {
                iArr[ItemValueKey.agent_id.ordinal()] = 1
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ItemValueKey.asset_id.ordinal()] = 2
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ItemValueKey.created_at.ordinal()] = 3
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[ItemValueKey.desc.ordinal()] = 4
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[ItemValueKey.flags.ordinal()] = 5
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[ItemValueKey.inv_type.ordinal()] = 6
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[ItemValueKey.item_id.ordinal()] = 7
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[ItemValueKey.name.ordinal()] = 8
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[ItemValueKey.parent_id.ordinal()] = 9
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[ItemValueKey.type.ordinal()] = 10
            } catch (NoSuchFieldError e10) {
            }
            f112comlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = iArr
            return iArr
        }

        ItemEntryContentHandler(DatabaseCommitThread databaseCommitThread) {
            this.commitThread = databaseCommitThread
            this.entry = SLInventoryEntry()
            this.entry.isFolder = false
        }

        public LLSDStreamingParser.LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
            return str.equals("permissions") ? this.permissionsHandler : str.equals("sale_info") ? this.saleInfoHandler : super.onMapBegin(str)
        }

        public Unit onMapEnd(String str) throws LLSDXMLException, InterruptedException {
            if (this.entry.parentUUID == null) {
                this.entry.parentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.parentUUID
                this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId()
            }
            if (this.entry.agentUUID == null) {
                this.entry.agentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.agentUUID
            }
            this.commitThread.addEntry(this.entry)
        }

        public Unit onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
            ItemValueKey byTag = ItemValueKey.byTag(str)
            if (byTag != null) {
                switch (m191getcomlumiyaviewerlumiyaslprotoinventorySLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()[byTag.ordinal()]) {
                    case 1:
                        this.entry.agentUUID = lLSDNode.asUUID()
                        return
                    case 2:
                        this.entry.assetUUID = lLSDNode.asUUID()
                        return
                    case 3:
                        this.entry.creationDate = lLSDNode.asInt()
                        return
                    case 4:
                        this.entry.description = lLSDNode.asString()
                        return
                    case 5:
                        this.entry.flags = lLSDNode.asInt()
                        return
                    case 6:
                        if (lLSDNode.isInt()) {
                            this.entry.invType = lLSDNode.asInt()
                            return
                        }
                        this.entry.invType = SLInventoryType.getByString(lLSDNode.asString()).getTypeCode()
                        return
                    case 7:
                        this.entry.uuid = lLSDNode.asUUID()
                        return
                    case 8:
                        this.entry.name = lLSDNode.asString()
                        return
                    case 9:
                        this.entry.parentUUID = lLSDNode.asUUID()
                        if (this.entry.parentUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID)) {
                            this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId()
                            return
                        }
                        SLInventoryEntry findEntry = SLInventoryHTTPFetchRequest.this.db.findEntry(this.entry.parentUUID)
                        if (findEntry != null) {
                            this.entry.parent_id = findEntry.getId()
                            return
                        }
                        this.entry.parent_id = 0
                        return
                    case 10:
                        if (lLSDNode.isInt()) {
                            this.entry.assetType = lLSDNode.asInt()
                            return
                        }
                        this.entry.assetType = SLAssetType.getByString(lLSDNode.asString()).getTypeCode()
                        return
                    default:
                        return
                }
            } else {
                Debug.Printf("InvFetch: Item unknown key '%s'", str)
            }
        }
    }

    private enum ItemValueKey {
        item_id,
        name,
        parent_id,
        agent_id,
        type,
        inv_type,
        desc,
        flags,
        created_at,
        asset_id
        
        private const val Map<String, ItemValueKey> tagMap = null

        static {
            tagMap = HashMap(values().length * 2)
            for (ItemValueKey itemValueKey : values()) {
                tagMap.put(itemValueKey.toString(), itemValueKey)
            }
        }

        @JvmStatic
    ItemValueKey byTag(String str) {
            return tagMap.get(str)
        }
    }

    private enum PermissionsValueKey {
        creator_id,
        group_id,
        owner_id,
        last_owner_id,
        is_owner_group,
        base_mask,
        owner_mask,
        next_owner_mask,
        group_mask,
        everyone_mask
        
        private const val Map<String, PermissionsValueKey> tagMap = null

        static {
            tagMap = HashMap(values().length * 2)
            for (PermissionsValueKey permissionsValueKey : values()) {
                tagMap.put(permissionsValueKey.toString(), permissionsValueKey)
            }
        }

        @JvmStatic
    PermissionsValueKey byTag(String str) {
            return tagMap.get(str)
        }
    }

    private class RootContentHandler : LLSDStreamingParser().LLSDDefaultContentHandler {
        /* access modifiers changed from: private */
        val DatabaseCommitThread commitThread

        private RootContentHandler(DatabaseCommitThread databaseCommitThread) {
            this.commitThread = databaseCommitThread
        }

        /* synthetic */ RootContentHandler(SLInventoryHTTPFetchRequest sLInventoryHTTPFetchRequest, DatabaseCommitThread databaseCommitThread, RootContentHandler rootContentHandler) {
            this(databaseCommitThread)
        }

        public LLSDStreamingParser.LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
            return LLSDStreamingParser.LLSDDefaultContentHandler() {
                public LLSDStreamingParser.LLSDContentHandler onArrayBegin(String str) throws LLSDXMLException {
                    return str.equals("folders") ? LLSDStreamingParser.LLSDDefaultContentHandler() {
                        public LLSDStreamingParser.LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
                            return FolderDataContentHandler(SLInventoryHTTPFetchRequest.this, RootContentHandler.this.commitThread, (FolderDataContentHandler) null)
                        }
                    } : super.onArrayBegin(str)
                }
            }
        }
    }

    SLInventoryHTTPFetchRequest(SLInventory sLInventory, UUID uuid, String str) throws SLInventory.NoInventoryItemException {
        super(sLInventory, uuid)
        this.capURL = str
    }

    public Unit cancel() {
        this.isCancelled.set(true)
        LLSDStreamingXMLRequest lLSDStreamingXMLRequest = this.streamingXmlReqRef.get()
        if (lLSDStreamingXMLRequest != null) {
            lLSDStreamingXMLRequest.InterruptRequest()
        }
        Future andSet = this.futureRef.getAndSet((Object) null)
        if (andSet != null) {
            andSet.cancel(true)
        }
    }

    public Unit start() {
        if (!this.isCancelled.get() && this.futureRef.get() == null) {
            this.futureRef.set(GenericHTTPExecutor.getInstance().submit(this.httpRequest))
        }
    }
}
