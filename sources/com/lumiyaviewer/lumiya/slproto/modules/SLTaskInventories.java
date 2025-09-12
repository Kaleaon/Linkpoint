// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.ReplyTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.RequestTaskInventory;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules

public class SLTaskInventories extends SLModule
    implements com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer.SLXferCompletionListener
{

    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private final RequestHandler requestHandler;
    private final ResultHandler resultHandler;
    private final UserManager userManager;

    static void _2D_wrap0(SLTaskInventories sltaskinventories, int i)
    {
        sltaskinventories.RequestTaskInventory(i);
    }

    public SLTaskInventories(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        requestHandler = new AsyncRequestHandler(slagentcircuit, new SimpleRequestHandler() {

            final SLTaskInventories this$0;

            public void onRequest(Integer integer)
            {
                SLTaskInventories._2D_wrap0(SLTaskInventories.this, integer.intValue());
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((Integer)obj);
            }

            
            {
                this$0 = SLTaskInventories.this;
                super();
            }
        });
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        if (userManager != null)
        {
            resultHandler = userManager.getObjectsManager().getTaskInventoryRequestSource().attachRequestHandler(requestHandler);
            return;
        } else
        {
            resultHandler = null;
            return;
        }
    }

    private void RequestTaskInventory(int i)
    {
        Debug.Printf("taskID = %d", new Object[] {
            Integer.valueOf(i)
        });
        RequestTaskInventory requesttaskinventory = new RequestTaskInventory();
        requesttaskinventory.AgentData_Field.AgentID = circuitInfo.agentID;
        requesttaskinventory.AgentData_Field.SessionID = circuitInfo.sessionID;
        requesttaskinventory.InventoryData_Field.LocalID = i;
        requesttaskinventory.isReliable = true;
        SendMessage(requesttaskinventory);
    }

    private SLTaskInventory parseTaskInventory(byte abyte0[])
    {
        if (abyte0 == null)
        {
            return new SLTaskInventory();
        }
        com.google.common.collect.ImmutableList.Builder builder;
        builder = ImmutableList.builder();
        abyte0 = new SimpleStringParser(SLMessage.stringFromVariableUTF(abyte0), " \t\n");
_L3:
        String s;
        if (abyte0.endOfString())
        {
            break MISSING_BLOCK_LABEL_140;
        }
        s = abyte0.nextToken(" \t\n");
        Debug.Printf("TaskInventory: got token: '%s'", new Object[] {
            s
        });
        if (!s.equalsIgnoreCase("inv_object")) goto _L2; else goto _L1
_L1:
        abyte0.nextToken("\n");
        abyte0.expectToken("{", "\n");
        while (!abyte0.nextToken("\n").equals("}")) ;
          goto _L3
_L2:
        if (!s.equalsIgnoreCase("inv_item")) goto _L3; else goto _L4
_L4:
        abyte0.getIntToken("\n");
        builder.add(SLInventoryEntry.parseString(abyte0));
          goto _L3
        try
        {
            abyte0 = new SLTaskInventory(builder.build());
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            Debug.Warning(abyte0);
            return new SLTaskInventory();
        }
        return abyte0;
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getObjectsManager().getTaskInventoryRequestSource().detachRequestHandler(requestHandler);
        }
        super.HandleCloseCircuit();
    }

    public void HandleReplyTaskInventory(ReplyTaskInventory replytaskinventory)
    {
        String s = SLMessage.stringFromVariableOEM(replytaskinventory.InventoryData_Field.Filename);
        Debug.Printf("taskID = %s, serial = %d, filename = '%s'", new Object[] {
            replytaskinventory.InventoryData_Field.TaskID.toString(), Integer.valueOf(replytaskinventory.InventoryData_Field.Serial), s
        });
        if (!s.equals(""))
        {
            agentCircuit.getModules().xferManager.RequestXfer(s, ELLPath.LL_PATH_CACHE, true, this, replytaskinventory.InventoryData_Field.TaskID);
        } else
        if (resultHandler != null)
        {
            int i = agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(replytaskinventory.InventoryData_Field.TaskID);
            resultHandler.onResultData(Integer.valueOf(i), new SLTaskInventory());
            return;
        }
    }

    public void onXferComplete(Object obj, String s, byte abyte0[])
    {
        if (obj instanceof UUID)
        {
            obj = (UUID)obj;
            Debug.Printf("onXferComplete with file = '%s', data length = %d", new Object[] {
                s, Integer.valueOf(abyte0.length)
            });
            s = parseTaskInventory(abyte0);
            Debug.Printf("task inventory count = %d", new Object[] {
                Integer.valueOf(((SLTaskInventory) (s)).entries.size())
            });
            if (resultHandler != null)
            {
                int i = agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(((UUID) (obj)));
                resultHandler.onResultData(Integer.valueOf(i), s);
            }
        }
    }
}
