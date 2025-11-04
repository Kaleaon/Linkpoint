// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.messages.ReplyTaskInventory;
import java.util.Collection;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.RequestTaskInventory;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer;

public class SLTaskInventories extends SLModule implements SLXferCompletionListener
{
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private final RequestHandler<Integer> requestHandler;
    private final ResultHandler<Integer, SLTaskInventory> resultHandler;
    private final UserManager userManager;
    
    public SLTaskInventories(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.requestHandler = new AsyncRequestHandler<Integer>(slAgentCircuit, new SimpleRequestHandler<Integer>() {
            @Override
            public void onRequest(@Nonnull final Integer n) {
                SLTaskInventories.this.RequestTaskInventory(n);
            }
        });
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.resultHandler = this.userManager.getObjectsManager().getTaskInventoryRequestSource().attachRequestHandler(this.requestHandler);
        }
        else {
            this.resultHandler = null;
        }
    }
    
    private void RequestTaskInventory(final int n) {
        Debug.Printf("taskID = %d", n);
        final RequestTaskInventory requestTaskInventory = new RequestTaskInventory();
        requestTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        requestTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        requestTaskInventory.InventoryData_Field.LocalID = n;
        requestTaskInventory.isReliable = true;
        this.SendMessage(requestTaskInventory);
    }
    
    private SLTaskInventory parseTaskInventory(final byte[] array) {
        if (array == null) {
            return new SLTaskInventory();
        }
        ImmutableList.Builder<SLInventoryEntry> builder;
        try {
            builder = ImmutableList.builder();
            final SimpleStringParser simpleStringParser = new SimpleStringParser(SLMessage.stringFromVariableUTF(array), " \t\n");
            while (!simpleStringParser.endOfString()) {
                final String nextToken = simpleStringParser.nextToken(" \t\n");
                Debug.Printf("TaskInventory: got token: '%s'", nextToken);
                if (nextToken.equalsIgnoreCase("inv_object")) {
                    simpleStringParser.nextToken("\n");
                    simpleStringParser.expectToken("{", "\n");
                    while (!simpleStringParser.nextToken("\n").equals("}")) {}
                }
                else {
                    if (!nextToken.equalsIgnoreCase("inv_item")) {
                        continue;
                    }
                    simpleStringParser.getIntToken("\n");
                    builder.add(SLInventoryEntry.parseString(simpleStringParser));
                }
            }
        }
        catch (final SimpleStringParser.StringParsingException ex) {
            Debug.Warning(ex);
            return new SLTaskInventory();
        }
        return new SLTaskInventory(builder.build());
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().getTaskInventoryRequestSource().detachRequestHandler(this.requestHandler);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleReplyTaskInventory(final ReplyTaskInventory replyTaskInventory) {
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(replyTaskInventory.InventoryData_Field.Filename);
        Debug.Printf("taskID = %s, serial = %d, filename = '%s'", replyTaskInventory.InventoryData_Field.TaskID.toString(), replyTaskInventory.InventoryData_Field.Serial, stringFromVariableOEM);
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, replyTaskInventory.InventoryData_Field.TaskID);
        }
        else if (this.resultHandler != null) {
            this.resultHandler.onResultData(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(replyTaskInventory.InventoryData_Field.TaskID), new SLTaskInventory());
        }
    }
    
    @Override
    public void onXferComplete(final Object o, final String s, final byte[] array) {
        if (o instanceof UUID) {
            final UUID uuid = (UUID)o;
            Debug.Printf("onXferComplete with file = '%s', data length = %d", s, array.length);
            final SLTaskInventory taskInventory = this.parseTaskInventory(array);
            Debug.Printf("task inventory count = %d", taskInventory.entries.size());
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(uuid), taskInventory);
            }
        }
    }
}
