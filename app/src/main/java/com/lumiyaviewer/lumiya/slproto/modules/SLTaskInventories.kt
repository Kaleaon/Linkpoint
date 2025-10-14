package com.lumiyaviewer.lumiya.slproto.modules

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler
import com.lumiyaviewer.lumiya.react.RequestHandler
import com.lumiyaviewer.lumiya.react.ResultHandler
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory
import com.lumiyaviewer.lumiya.slproto.messages.ReplyTaskInventory
import com.lumiyaviewer.lumiya.slproto.messages.RequestTaskInventory
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.SimpleStringParser
import java.util.UUID
import javax.annotation.Nonnull

class SLTaskInventories : SLModule : SLXfer.SLXferCompletionListener {
    private String DELIM_ANY = " \t\n"
    private String DELIM_EOL = "\n"
    private RequestHandler<Int> requestHandler
    private ResultHandler<Int, SLTaskInventory> resultHandler
    private UserManager userManager

    SLTaskInventories(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.requestHandler = AsyncRequestHandler(sLAgentCircuit, SimpleRequestHandler<Int>() {
            Unit onRequest(@Nonnull Int num) {
                SLTaskInventories.this.RequestTaskInventory(num.intValue())
            }
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        if (this.userManager != null) {
            this.resultHandler = this.userManager.getObjectsManager().getTaskInventoryRequestSource().attachRequestHandler(this.requestHandler)
        } else {
            this.resultHandler = null
        }
    }

    /* access modifiers changed from: private */
    Unit RequestTaskInventory(Int i) {
        Debug.Printf("taskID = %d", Int.valueOf(i))
        RequestTaskInventory requestTaskInventory = RequestTaskInventory()
        requestTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID
        requestTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID
        requestTaskInventory.InventoryData_Field.LocalID = i
        requestTaskInventory.isReliable = true
        SendMessage(requestTaskInventory)
    }

    private SLTaskInventory parseTaskInventory(Byte[] bArr) {
        if (bArr == null) {
            return SLTaskInventory()
        }
        try {
            ImmutableList.Builder builder = ImmutableList.builder()
            SimpleStringParser simpleStringParser = SimpleStringParser(SLMessage.stringFromVariableUTF(bArr), DELIM_ANY)
            while (!simpleStringParser.endOfString()) {
                String nextToken = simpleStringParser.nextToken(DELIM_ANY)
                Debug.Printf("TaskInventory: got token: '%s'", nextToken)
                if (nextToken.equalsIgnoreCase("inv_object")) {
                    simpleStringParser.nextToken(DELIM_EOL)
                    simpleStringParser.expectToken("{", DELIM_EOL)
                    while (!simpleStringParser.nextToken(DELIM_EOL).equals("}")) {
                    }
                } else if (nextToken.equalsIgnoreCase("inv_item")) {
                    simpleStringParser.getIntToken(DELIM_EOL)
                    builder.add((Any) SLInventoryEntry.parseString(simpleStringParser))
                }
            }
            return SLTaskInventory(builder.build())
        } catch (SimpleStringParser.StringParsingException e) {
            Debug.Warning(e)
            return SLTaskInventory()
        }
    }

    Unit HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().getTaskInventoryRequestSource().detachRequestHandler(this.requestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    Unit HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(replyTaskInventory.InventoryData_Field.Filename)
        Debug.Printf("taskID = %s, serial = %d, filename = '%s'", replyTaskInventory.InventoryData_Field.TaskID.toString(), Int.valueOf(replyTaskInventory.InventoryData_Field.Serial), stringFromVariableOEM)
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, replyTaskInventory.InventoryData_Field.TaskID)
        } else if (this.resultHandler != null) {
            this.resultHandler.onResultData(Int.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(replyTaskInventory.InventoryData_Field.TaskID)), SLTaskInventory())
        }
    }

    Unit onXferComplete(Any obj, String str, Byte[] bArr) {
        if (obj instanceof UUID) {
            UUID uuid = (UUID) obj
            Debug.Printf("onXferComplete with file = '%s', data length = %d", str, Int.valueOf(bArr.length))
            SLTaskInventory parseTaskInventory = parseTaskInventory(bArr)
            Debug.Printf("task inventory count = %d", Int.valueOf(parseTaskInventory.entries.size()))
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(Int.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(uuid)), parseTaskInventory)
            }
        }
    }
}
