// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Parcelable;
import android.net.Uri;
import com.lumiyaviewer.lumiya.Debug;
import android.content.Intent;
import android.content.DialogInterface;
import java.util.concurrent.locks.ReentrantLock;
import android.app.ProgressDialog;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import android.content.Context;
import android.content.DialogInterface$OnCancelListener;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.AsyncTask;

public class ExportChatHistoryTask extends AsyncTask<ChatterID, Void, ExportResult> implements DialogInterface$OnCancelListener
{
    private static final String forbiddenChars = "./\\*?:\"'~";
    private final Context context;
    private final AtomicReference<String> gotChatterName;
    private final AtomicBoolean isNameReady;
    private final Condition nameReadyCondition;
    private final Lock nameReadyLock;
    private final ChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated;
    private ProgressDialog progressDialog;
    
    public ExportChatHistoryTask(final Context context) {
        this.nameReadyLock = new ReentrantLock();
        this.nameReadyCondition = this.nameReadyLock.newCondition();
        this.isNameReady = new AtomicBoolean();
        this.gotChatterName = new AtomicReference<String>();
        this.onChatterNameUpdated = new _$Lambda$D705oXX7BTh_Xc4P_mIDvS9cOZI(this);
        this.context = context;
    }
    
    private String sanitizeName(String trim) {
        String s = trim;
        if (trim == null) {
            s = "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int length = s.length(), i = 0; i < length; ++i) {
            char char1;
            if ((char1 = s.charAt(i)) < ' ') {
                char1 = ' ';
            }
            char c;
            if ((c = char1) > '\u007f') {
                c = '_';
            }
            if ("./\\*?:\"'~".indexOf(c) < 0) {
                sb.append(c);
            }
        }
        if ((trim = sb.toString().trim()).isEmpty()) {
            trim = "Chat Log";
        }
        return trim;
    }
    
    protected ExportResult doInBackground(final ChatterID... p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: arraylength    
        //     2: iconst_1       
        //     3: if_icmpeq       8
        //     6: aconst_null    
        //     7: areturn        
        //     8: aload_1        
        //     9: iconst_0       
        //    10: aaload         
        //    11: astore_2       
        //    12: aload_2        
        //    13: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/ChatterID.getUserManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //    16: astore_3       
        //    17: aload_3        
        //    18: ifnonnull       23
        //    21: aconst_null    
        //    22: areturn        
        //    23: new             Lcom/lumiyaviewer/lumiya/slproto/users/ChatterNameRetriever;
        //    26: dup            
        //    27: aload_2        
        //    28: aload_0        
        //    29: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.onChatterNameUpdated:Lcom/lumiyaviewer/lumiya/slproto/users/ChatterNameRetriever$OnChatterNameUpdated;
        //    32: invokestatic    com/lumiyaviewer/lumiya/react/UIThreadExecutor.getInstance:()Ljava/util/concurrent/Executor;
        //    35: invokespecial   com/lumiyaviewer/lumiya/slproto/users/ChatterNameRetriever.<init>:(Lcom/lumiyaviewer/lumiya/slproto/users/ChatterID;Lcom/lumiyaviewer/lumiya/slproto/users/ChatterNameRetriever$OnChatterNameUpdated;Ljava/util/concurrent/Executor;)V
        //    38: astore_1       
        //    39: aload_0        
        //    40: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.nameReadyLock:Ljava/util/concurrent/locks/Lock;
        //    43: invokeinterface java/util/concurrent/locks/Lock.lock:()V
        //    48: aload_0        
        //    49: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isNameReady:Ljava/util/concurrent/atomic/AtomicBoolean;
        //    52: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
        //    55: ifne            94
        //    58: aload_0        
        //    59: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //    62: iconst_1       
        //    63: ixor           
        //    64: ifeq            94
        //    67: aload_0        
        //    68: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.nameReadyCondition:Ljava/util/concurrent/locks/Condition;
        //    71: invokeinterface java/util/concurrent/locks/Condition.await:()V
        //    76: goto            48
        //    79: astore_1       
        //    80: aload_0        
        //    81: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.nameReadyLock:Ljava/util/concurrent/locks/Lock;
        //    84: invokeinterface java/util/concurrent/locks/Lock.unlock:()V
        //    89: aload_1        
        //    90: athrow         
        //    91: astore_1       
        //    92: aconst_null    
        //    93: areturn        
        //    94: aload_0        
        //    95: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.nameReadyLock:Ljava/util/concurrent/locks/Lock;
        //    98: invokeinterface java/util/concurrent/locks/Lock.unlock:()V
        //   103: aload_1        
        //   104: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/ChatterNameRetriever.dispose:()V
        //   107: aload_0        
        //   108: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //   111: ifeq            116
        //   114: aconst_null    
        //   115: areturn        
        //   116: getstatic       android/os/Build$VERSION.SDK_INT:I
        //   119: bipush          19
        //   121: if_icmplt       423
        //   124: getstatic       android/os/Environment.DIRECTORY_DOCUMENTS:Ljava/lang/String;
        //   127: astore_1       
        //   128: new             Ljava/io/File;
        //   131: dup            
        //   132: aload_1        
        //   133: invokestatic    android/os/Environment.getExternalStoragePublicDirectory:(Ljava/lang/String;)Ljava/io/File;
        //   136: ldc             "Lumiya"
        //   138: invokespecial   java/io/File.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //   141: astore          4
        //   143: new             Ljava/io/File;
        //   146: dup            
        //   147: aload           4
        //   149: ldc             "Chat Logs"
        //   151: invokespecial   java/io/File.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //   154: astore          5
        //   156: new             Ljava/io/File;
        //   159: dup            
        //   160: aload           5
        //   162: new             Ljava/lang/StringBuilder;
        //   165: dup            
        //   166: invokespecial   java/lang/StringBuilder.<init>:()V
        //   169: aload_0        
        //   170: aload_0        
        //   171: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.gotChatterName:Ljava/util/concurrent/atomic/AtomicReference;
        //   174: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //   177: checkcast       Ljava/lang/String;
        //   180: invokespecial   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.sanitizeName:(Ljava/lang/String;)Ljava/lang/String;
        //   183: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   186: ldc             ".txt"
        //   188: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   191: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   194: invokespecial   java/io/File.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //   197: astore_1       
        //   198: iconst_3       
        //   199: iconst_3       
        //   200: invokestatic    java/text/DateFormat.getDateTimeInstance:(II)Ljava/text/DateFormat;
        //   203: astore          6
        //   205: aload           4
        //   207: invokevirtual   java/io/File.mkdirs:()Z
        //   210: pop            
        //   211: aload           5
        //   213: invokevirtual   java/io/File.mkdirs:()Z
        //   216: pop            
        //   217: new             Ljava/io/FileOutputStream;
        //   220: astore          4
        //   222: aload           4
        //   224: aload_1        
        //   225: iconst_0       
        //   226: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/File;Z)V
        //   229: aload_3        
        //   230: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getChatterList:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ChatterList;
        //   233: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ChatterList.getActiveChattersManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ActiveChattersManager;
        //   236: aload_2        
        //   237: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ActiveChattersManager.getMessages:(Lcom/lumiyaviewer/lumiya/slproto/users/ChatterID;)Lde/greenrobot/dao/query/LazyList;
        //   240: astore          5
        //   242: aload           5
        //   244: ifnull          381
        //   247: aload           5
        //   249: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   254: astore          7
        //   256: aload           7
        //   258: invokeinterface java/util/Iterator.hasNext:()Z
        //   263: ifeq            381
        //   266: aload           7
        //   268: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   273: checkcast       Lcom/lumiyaviewer/lumiya/dao/ChatMessage;
        //   276: aload_3        
        //   277: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getUserID:()Ljava/util/UUID;
        //   280: invokestatic    com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.loadFromDatabaseObject:(Lcom/lumiyaviewer/lumiya/dao/ChatMessage;Ljava/util/UUID;)Lcom/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent;
        //   283: astore          8
        //   285: aload           8
        //   287: ifnull          370
        //   290: aload           6
        //   292: aload           8
        //   294: invokevirtual   com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.getTimestamp:()Ljava/util/Date;
        //   297: invokevirtual   java/text/DateFormat.format:(Ljava/util/Date;)Ljava/lang/String;
        //   300: astore          9
        //   302: aload           8
        //   304: aload_0        
        //   305: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.context:Landroid/content/Context;
        //   308: aload_3        
        //   309: iconst_0       
        //   310: invokevirtual   com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.getPlainTextMessage:(Landroid/content/Context;Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;Z)Ljava/lang/CharSequence;
        //   313: astore          8
        //   315: new             Ljava/lang/StringBuilder;
        //   318: astore          10
        //   320: aload           10
        //   322: invokespecial   java/lang/StringBuilder.<init>:()V
        //   325: aload           4
        //   327: aload           10
        //   329: ldc             "["
        //   331: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   334: aload           9
        //   336: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   339: ldc_w           "] "
        //   342: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   345: aload           8
        //   347: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
        //   352: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   355: ldc_w           "\n"
        //   358: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   361: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   364: invokevirtual   java/lang/String.getBytes:()[B
        //   367: invokevirtual   java/io/FileOutputStream.write:([B)V
        //   370: aload_0        
        //   371: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //   374: istore          11
        //   376: iload           11
        //   378: ifeq            256
        //   381: aload           5
        //   383: ifnull          391
        //   386: aload           5
        //   388: invokevirtual   de/greenrobot/dao/query/LazyList.close:()V
        //   391: aload           4
        //   393: ifnull          401
        //   396: aload           4
        //   398: invokevirtual   java/io/FileOutputStream.close:()V
        //   401: aload_0        
        //   402: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //   405: ifne            682
        //   408: aload_1        
        //   409: ifnull          489
        //   412: new             Lcom/lumiyaviewer/lumiya/ui/chat/ExportResult;
        //   415: dup            
        //   416: aload_1        
        //   417: aconst_null    
        //   418: aconst_null    
        //   419: invokespecial   com/lumiyaviewer/lumiya/ui/chat/ExportResult.<init>:(Ljava/io/File;Ljava/lang/String;Ljava/lang/String;)V
        //   422: areturn        
        //   423: getstatic       android/os/Environment.DIRECTORY_DOWNLOADS:Ljava/lang/String;
        //   426: astore_1       
        //   427: goto            128
        //   430: astore_1       
        //   431: aconst_null    
        //   432: astore          4
        //   434: aconst_null    
        //   435: astore          5
        //   437: aload           5
        //   439: ifnull          447
        //   442: aload           5
        //   444: invokevirtual   de/greenrobot/dao/query/LazyList.close:()V
        //   447: aload           4
        //   449: ifnull          457
        //   452: aload           4
        //   454: invokevirtual   java/io/FileOutputStream.close:()V
        //   457: aload_1        
        //   458: athrow         
        //   459: astore_1       
        //   460: aload_1        
        //   461: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   464: aconst_null    
        //   465: astore_1       
        //   466: goto            401
        //   469: astore_1       
        //   470: aload_1        
        //   471: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   474: aconst_null    
        //   475: astore_1       
        //   476: goto            401
        //   479: astore_1       
        //   480: aload_1        
        //   481: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   484: aconst_null    
        //   485: astore_1       
        //   486: goto            401
        //   489: new             Ljava/lang/StringBuilder;
        //   492: dup            
        //   493: invokespecial   java/lang/StringBuilder.<init>:()V
        //   496: aload_0        
        //   497: aload_0        
        //   498: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.gotChatterName:Ljava/util/concurrent/atomic/AtomicReference;
        //   501: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //   504: checkcast       Ljava/lang/String;
        //   507: invokespecial   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.sanitizeName:(Ljava/lang/String;)Ljava/lang/String;
        //   510: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   513: ldc             ".txt"
        //   515: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   518: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   521: astore_1       
        //   522: new             Ljava/lang/StringBuilder;
        //   525: dup            
        //   526: invokespecial   java/lang/StringBuilder.<init>:()V
        //   529: astore          5
        //   531: aload_3        
        //   532: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getChatterList:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ChatterList;
        //   535: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ChatterList.getActiveChattersManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ActiveChattersManager;
        //   538: aload_2        
        //   539: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ActiveChattersManager.getMessages:(Lcom/lumiyaviewer/lumiya/slproto/users/ChatterID;)Lde/greenrobot/dao/query/LazyList;
        //   542: astore          4
        //   544: aload           4
        //   546: ifnull          656
        //   549: aload           4
        //   551: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   556: astore_2       
        //   557: aload_2        
        //   558: invokeinterface java/util/Iterator.hasNext:()Z
        //   563: ifeq            656
        //   566: aload_2        
        //   567: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   572: checkcast       Lcom/lumiyaviewer/lumiya/dao/ChatMessage;
        //   575: aload_3        
        //   576: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getUserID:()Ljava/util/UUID;
        //   579: invokestatic    com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.loadFromDatabaseObject:(Lcom/lumiyaviewer/lumiya/dao/ChatMessage;Ljava/util/UUID;)Lcom/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent;
        //   582: astore          9
        //   584: aload           9
        //   586: ifnull          649
        //   589: aload           6
        //   591: aload           9
        //   593: invokevirtual   com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.getTimestamp:()Ljava/util/Date;
        //   596: invokevirtual   java/text/DateFormat.format:(Ljava/util/Date;)Ljava/lang/String;
        //   599: astore          4
        //   601: aload           9
        //   603: aload_0        
        //   604: getfield        com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.context:Landroid/content/Context;
        //   607: aload_3        
        //   608: iconst_0       
        //   609: invokevirtual   com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent.getPlainTextMessage:(Landroid/content/Context;Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;Z)Ljava/lang/CharSequence;
        //   612: astore          9
        //   614: aload           5
        //   616: ldc             "["
        //   618: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   621: aload           4
        //   623: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   626: ldc_w           "] "
        //   629: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   632: aload           9
        //   634: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
        //   639: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   642: ldc_w           "\n"
        //   645: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   648: pop            
        //   649: aload_0        
        //   650: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //   653: ifeq            557
        //   656: aload           5
        //   658: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   661: astore          5
        //   663: aload_0        
        //   664: invokevirtual   com/lumiyaviewer/lumiya/ui/chat/ExportChatHistoryTask.isCancelled:()Z
        //   667: ifne            682
        //   670: new             Lcom/lumiyaviewer/lumiya/ui/chat/ExportResult;
        //   673: dup            
        //   674: aconst_null    
        //   675: aload           5
        //   677: aload_1        
        //   678: invokespecial   com/lumiyaviewer/lumiya/ui/chat/ExportResult.<init>:(Ljava/io/File;Ljava/lang/String;Ljava/lang/String;)V
        //   681: areturn        
        //   682: aconst_null    
        //   683: areturn        
        //   684: astore_1       
        //   685: aconst_null    
        //   686: astore          5
        //   688: goto            437
        //   691: astore_1       
        //   692: goto            437
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  39     48     79     91     Any
        //  48     76     79     91     Any
        //  80     91     91     94     Ljava/lang/InterruptedException;
        //  94     103    91     94     Ljava/lang/InterruptedException;
        //  205    217    459    469    Ljava/lang/SecurityException;
        //  205    217    479    489    Ljava/io/IOException;
        //  205    217    469    479    Ljava/lang/Exception;
        //  217    229    430    437    Any
        //  229    242    684    691    Any
        //  247    256    691    695    Any
        //  256    285    691    695    Any
        //  290    370    691    695    Any
        //  370    376    691    695    Any
        //  386    391    459    469    Ljava/lang/SecurityException;
        //  386    391    479    489    Ljava/io/IOException;
        //  386    391    469    479    Ljava/lang/Exception;
        //  396    401    459    469    Ljava/lang/SecurityException;
        //  396    401    479    489    Ljava/io/IOException;
        //  396    401    469    479    Ljava/lang/Exception;
        //  442    447    459    469    Ljava/lang/SecurityException;
        //  442    447    479    489    Ljava/io/IOException;
        //  442    447    469    479    Ljava/lang/Exception;
        //  452    457    459    469    Ljava/lang/SecurityException;
        //  452    457    479    489    Ljava/io/IOException;
        //  452    457    469    479    Ljava/lang/Exception;
        //  457    459    459    469    Ljava/lang/SecurityException;
        //  457    459    479    489    Ljava/io/IOException;
        //  457    459    469    479    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 318 out of bounds for length 318
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
        this.cancel(false);
        try {
            this.nameReadyLock.lock();
            this.nameReadyCondition.signal();
        }
        finally {
            this.nameReadyLock.unlock();
        }
    }
    
    protected void onCancelled() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }
    
    protected void onPostExecute(final ExportResult exportResult) {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        if (exportResult != null) {
            final Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("text/plain");
            if (exportResult.outputFile != null) {
                Debug.Printf("Export: exported as stream %s", exportResult.outputFile);
                intent.putExtra("android.intent.extra.STREAM", (Parcelable)Uri.fromFile(exportResult.outputFile));
            }
            else {
                Debug.Printf("Export: exported as text, %d bytes", exportResult.rawText.length());
                intent.putExtra("android.intent.extra.TEXT", exportResult.rawText);
                intent.putExtra("android.intent.extra.SUBJECT", exportResult.rawTextTitle);
            }
            this.context.startActivity(Intent.createChooser(intent, this.context.getText(2131296535)));
        }
    }
    
    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(this.context, (CharSequence)this.context.getString(2131296888), (CharSequence)this.context.getString(2131296537), true, true, (DialogInterface$OnCancelListener)this);
    }
}
