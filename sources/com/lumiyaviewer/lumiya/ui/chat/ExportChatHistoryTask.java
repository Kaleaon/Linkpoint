// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import de.greenrobot.dao.query.LazyList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ExportResult

public class ExportChatHistoryTask extends AsyncTask
    implements android.content.DialogInterface.OnCancelListener
{

    private static final String forbiddenChars = "./\\*?:\"'~";
    private final Context context;
    private final AtomicReference gotChatterName = new AtomicReference();
    private final AtomicBoolean isNameReady = new AtomicBoolean();
    private final Condition nameReadyCondition;
    private final Lock nameReadyLock = new ReentrantLock();
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated = new _2D_.Lambda.D705oXX7BTh_Xc4P_mIDvS9cOZI(this);
    private ProgressDialog progressDialog;

    public ExportChatHistoryTask(Context context1)
    {
        nameReadyCondition = nameReadyLock.newCondition();
        context = context1;
    }

    private String sanitizeName(String s)
    {
        String s1 = s;
        if (s == null)
        {
            s1 = "";
        }
        s = new StringBuilder();
        int j = s1.length();
        for (int i = 0; i < j; i++)
        {
            char c1 = s1.charAt(i);
            char c = c1;
            if (c1 < ' ')
            {
                c = ' ';
            }
            c1 = c;
            if (c > '\177')
            {
                c1 = '_';
            }
            if ("./\\*?:\"'~".indexOf(c1) < 0)
            {
                s.append(c1);
            }
        }

        s1 = s.toString().trim();
        s = s1;
        if (s1.isEmpty())
        {
            s = "Chat Log";
        }
        return s;
    }

    protected transient ExportResult doInBackground(ChatterID achatterid[])
    {
        UserManager usermanager;
        ChatterID chatterid;
        if (achatterid.length != 1)
        {
            return null;
        }
        chatterid = achatterid[0];
        usermanager = chatterid.getUserManager();
        if (usermanager == null)
        {
            return null;
        }
        achatterid = new ChatterNameRetriever(chatterid, onChatterNameUpdated, UIThreadExecutor.getInstance());
        nameReadyLock.lock();
        for (; !isNameReady.get() && isCancelled() ^ true; nameReadyCondition.await()) { }
        break MISSING_BLOCK_LABEL_96;
        achatterid;
        nameReadyLock.unlock();
        throw achatterid;
        nameReadyLock.unlock();
        Object obj;
        Object obj1;
        DateFormat dateformat;
        achatterid.dispose();
        if (isCancelled())
        {
            return null;
        }
        Iterator iterator;
        String s1;
        Object obj3;
        boolean flag;
        if (android.os.Build.VERSION.SDK_INT >= 19)
        {
            achatterid = Environment.DIRECTORY_DOCUMENTS;
        } else
        {
            achatterid = Environment.DIRECTORY_DOWNLOADS;
        }
        obj = new File(Environment.getExternalStoragePublicDirectory(achatterid), "Lumiya");
        obj1 = new File(((File) (obj)), "Chat Logs");
        achatterid = new File(((File) (obj1)), (new StringBuilder()).append(sanitizeName((String)gotChatterName.get())).append(".txt").toString());
        dateformat = DateFormat.getDateTimeInstance(3, 3);
        ((File) (obj)).mkdirs();
        ((File) (obj1)).mkdirs();
        obj1 = new FileOutputStream(achatterid, false);
        obj = usermanager.getChatterList().getActiveChattersManager().getMessages(chatterid);
        if (obj == null) goto _L2; else goto _L1
_L1:
        iterator = ((Iterable) (obj)).iterator();
_L3:
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj3 = SLChatEvent.loadFromDatabaseObject((ChatMessage)iterator.next(), usermanager.getUserID());
        if (obj3 == null)
        {
            break MISSING_BLOCK_LABEL_359;
        }
        s1 = dateformat.format(((SLChatEvent) (obj3)).getTimestamp());
        obj3 = ((SLChatEvent) (obj3)).getPlainTextMessage(context, usermanager, false);
        ((FileOutputStream) (obj1)).write((new StringBuilder()).append("[").append(s1).append("] ").append(((CharSequence) (obj3)).toString()).append("\n").toString().getBytes());
        flag = isCancelled();
        if (!flag) goto _L3; else goto _L2
_L2:
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_378;
        }
        ((LazyList) (obj)).close();
        if (obj1 != null)
        {
            try
            {
                ((FileOutputStream) (obj1)).close();
            }
            // Misplaced declaration of an exception variable
            catch (ChatterID achatterid[])
            {
                Debug.Warning(achatterid);
                achatterid = null;
            }
            // Misplaced declaration of an exception variable
            catch (ChatterID achatterid[])
            {
                Debug.Warning(achatterid);
                achatterid = null;
            }
            // Misplaced declaration of an exception variable
            catch (ChatterID achatterid[])
            {
                Debug.Warning(achatterid);
                achatterid = null;
            }
        }
        break MISSING_BLOCK_LABEL_386;
        achatterid;
        obj1 = null;
        obj = null;
_L5:
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_428;
        }
        ((LazyList) (obj)).close();
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_436;
        }
        ((FileOutputStream) (obj1)).close();
        throw achatterid;
_L7:
        if (!isCancelled())
        {
            if (achatterid != null)
            {
                return new ExportResult(achatterid, null, null);
            }
            achatterid = (new StringBuilder()).append(sanitizeName((String)gotChatterName.get())).append(".txt").toString();
            obj = new StringBuilder();
            obj1 = usermanager.getChatterList().getActiveChattersManager().getMessages(chatterid);
            if (obj1 != null)
            {
                obj1 = ((Iterable) (obj1)).iterator();
                do
                {
                    if (!((Iterator) (obj1)).hasNext())
                    {
                        break;
                    }
                    Object obj2 = SLChatEvent.loadFromDatabaseObject((ChatMessage)((Iterator) (obj1)).next(), usermanager.getUserID());
                    if (obj2 != null)
                    {
                        String s = dateformat.format(((SLChatEvent) (obj2)).getTimestamp());
                        obj2 = ((SLChatEvent) (obj2)).getPlainTextMessage(context, usermanager, false);
                        ((StringBuilder) (obj)).append("[").append(s).append("] ").append(((CharSequence) (obj2)).toString()).append("\n");
                    }
                } while (!isCancelled());
            }
            obj = ((StringBuilder) (obj)).toString();
            if (!isCancelled())
            {
                return new ExportResult(null, ((String) (obj)), achatterid);
            }
        }
        return null;
        achatterid;
        obj = null;
        continue; /* Loop/switch isn't completed */
        achatterid;
        if (true) goto _L5; else goto _L4
_L4:
        achatterid;
        return null;
        if (true) goto _L7; else goto _L6
_L6:
    }

    protected volatile Object doInBackground(Object aobj[])
    {
        return doInBackground((ChatterID[])aobj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_ExportChatHistoryTask_2020(ChatterNameRetriever chatternameretriever)
    {
        nameReadyLock.lock();
        isNameReady.set(true);
        gotChatterName.set(chatternameretriever.getResolvedName());
        nameReadyCondition.signal();
        nameReadyLock.unlock();
        return;
        chatternameretriever;
        nameReadyLock.unlock();
        throw chatternameretriever;
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        cancel(false);
        nameReadyLock.lock();
        nameReadyCondition.signal();
        nameReadyLock.unlock();
        return;
        dialoginterface;
        nameReadyLock.unlock();
        throw dialoginterface;
    }

    protected void onCancelled()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    protected void onPostExecute(ExportResult exportresult)
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        if (exportresult != null)
        {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("text/plain");
            if (exportresult.outputFile != null)
            {
                Debug.Printf("Export: exported as stream %s", new Object[] {
                    exportresult.outputFile
                });
                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(exportresult.outputFile));
            } else
            {
                Debug.Printf("Export: exported as text, %d bytes", new Object[] {
                    Integer.valueOf(exportresult.rawText.length())
                });
                intent.putExtra("android.intent.extra.TEXT", exportresult.rawText);
                intent.putExtra("android.intent.extra.SUBJECT", exportresult.rawTextTitle);
            }
            context.startActivity(Intent.createChooser(intent, context.getText(0x7f090117)));
        }
    }

    protected volatile void onPostExecute(Object obj)
    {
        onPostExecute((ExportResult)obj);
    }

    protected void onPreExecute()
    {
        progressDialog = ProgressDialog.show(context, context.getString(0x7f090278), context.getString(0x7f090119), true, true, this);
    }
}
