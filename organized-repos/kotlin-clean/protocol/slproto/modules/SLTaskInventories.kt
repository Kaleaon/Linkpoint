package com.linkpoint.slproto.modules

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLTaskInventory
import com.linkpoint.slproto.messages.ReplyTaskInventory
import com.linkpoint.slproto.messages.RequestTaskInventory
import com.linkpoint.slproto.modules.xfer.ELLPath
import com.linkpoint.slproto.modules.xfer.SLXfer
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.SimpleStringParser
import java.util.UUID
import javax.annotation.Nonnull

class SLTaskInventories : SLModule(), SLXfer.SLXferCompletionListener {
    private const val DELIM_ANY: String = " \t\n"
    private const val DELIM_EOL: String = "\n"
    private val RequestHandler<Integer> requestHandler
    private val ResultHandler<Integer, SLTaskInventory> resultHandler
    private val UserManager userManager

    public SLTaskInventories(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.requestHandler = AsyncRequestHandler(sLAgentCircuit, SimpleRequestHandler<Integer>() {
            fun onRequest(Integer num) {
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
    fun RequestTaskInventory(Int i) {
        Debug.Printf("taskID = %d", Integer.valueOf(i))
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
                    builder.add((Object) SLInventoryEntry.parseString(simpleStringParser))
                }
            }
            return SLTaskInventory(builder.build())
        } catch (SimpleStringParser.StringParsingException e) {
            Debug.Warning(e)
            return SLTaskInventory()
        }
    }

    fun HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().getTaskInventoryRequestSource().detachRequestHandler(this.requestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(replyTaskInventory.InventoryData_Field.Filename)
        Debug.Printf("taskID = %s, serial = %d, filename = '%s'", replyTaskInventory.InventoryData_Field.TaskID.toString(), Integer.valueOf(replyTaskInventory.InventoryData_Field.Serial), stringFromVariableOEM)
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, replyTaskInventory.InventoryData_Field.TaskID)
        } else if (this.resultHandler != null) {
            this.resultHandler.onResultData(Integer.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(replyTaskInventory.InventoryData_Field.TaskID)), SLTaskInventory())
        }
    }

    fun onXferComplete(Object obj, String str, Byte[] bArr) {
        if (obj instanceof UUID) {
            UUID uuid = (UUID) obj
            Debug.Printf("onXferComplete with file = '%s', data length = %d", str, Integer.valueOf(bArr.length))
            SLTaskInventory parseTaskInventory = parseTaskInventory(bArr)
            Debug.Printf("task inventory count = %d", Integer.valueOf(parseTaskInventory.entries.size()))
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(Integer.valueOf(this.agentCircuit.getGridConnection().parcelInfo.getObjectLocalID(uuid)), parseTaskInventory)
            }
        }
    }
}
