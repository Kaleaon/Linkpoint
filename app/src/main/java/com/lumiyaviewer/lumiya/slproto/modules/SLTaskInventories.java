package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.ReplyTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.RequestTaskInventory;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SLTaskInventories extends SLModule implements SLXfer.SLXferCompletionListener {
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private final RequestHandler<Integer> requestHandler;
    private final ResultHandler<Integer, SLTaskInventory> resultHandler;
    private final UserManager userManager;

    public SLTaskInventories(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.requestHandler = new AsyncRequestHandler(sLAgentCircuit, new SimpleRequestHandler<Integer>() {
            public void onRequest(@Nonnull Integer num) {
                SLTaskInventories.this.RequestTaskInventory(num.intValue());
            }
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.resultHandler = this.userManager.getObjectsManager().getTaskInventoryRequestSource().attachRequestHandler(this.requestHandler);
        } else {
            this.resultHandler = null;
        }
    }

    /* access modifiers changed from: private */
    public void RequestTaskInventory(int i) {
        Debug.Printf("taskID = %d", Integer.valueOf(i));
        RequestTaskInventory requestTaskInventory = new RequestTaskInventory();
        requestTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        requestTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        requestTaskInventory.InventoryData_Field.LocalID = i;
        requestTaskInventory.isReliable = true;
        SendMessage(requestTaskInventory);
    }

    private SLTaskInventory parseTaskInventory(byte[] bArr) {
        if (bArr == null) {
            return new SLTaskInventory();
        }
        try {
            ImmutableList.Builder builder = ImmutableList.builder();
            SimpleStringParser simpleStringParser = new SimpleStringParser(SLMessage.stringFromVariableUTF(bArr), DELIM_ANY);
            while (!simpleStringParser.endOfString()) {
                String nextToken = simpleStringParser.nextToken(DELIM_ANY);
                Debug.Printf("TaskInventory: got token: '%s'", nextToken);
                if (nextToken.equalsIgnoreCase("inv_object")) {
                    simpleStringParser.nextToken(DELIM_EOL);
                    simpleStringParser.expectToken("{", DELIM_EOL);
                    while (!simpleStringParser.nextToken(DELIM_EOL).equals("}")) {
                    }
                } else if (nextToken.equalsIgnoreCase("inv_item")) {
                    simpleStringParser.getIntToken(DELIM_EOL);
                    builder.add((Object) SLInventoryEntry.parseString(simpleStringParser));
                }
            }
            return new SLTaskInventory(builder.build());
        } catch (SimpleStringParser.StringParsingException e) {
            Debug.Warning(e);
            return new SLTaskInventory();
        }
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().getTaskInventoryRequestSource().detachRequestHandler(this.requestHandler);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(replyTaskInventory.InventoryData_Field.Filename);
        Debug.Printf("taskID = %s, serial = %d, filename = '%s'", replyTaskInventory.InventoryData_Field.TaskID.toString(), Integer.valueOf(replyTaskInventory.InventoryData_Field.Serial), stringFromVariableOEM);
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, replyTaskInventory.InventoryData_Field.TaskID);
        } else if (this.resultHandler != null) {
            this.resultHandler.onResultData(Integer.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(replyTaskInventory.InventoryData_Field.TaskID)), new SLTaskInventory());
        }
    }

    public void onXferComplete(Object obj, String str, byte[] bArr) {
        if (obj instanceof UUID) {
            UUID uuid = (UUID) obj;
            Debug.Printf("onXferComplete with file = '%s', data length = %d", str, Integer.valueOf(bArr.length));
            SLTaskInventory parseTaskInventory = parseTaskInventory(bArr);
            Debug.Printf("task inventory count = %d", Integer.valueOf(parseTaskInventory.entries.size()));
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(Integer.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(uuid)), parseTaskInventory);
            }
        }
    }
}
