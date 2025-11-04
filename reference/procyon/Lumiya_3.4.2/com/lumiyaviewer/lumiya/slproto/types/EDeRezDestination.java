// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public enum EDeRezDestination
{
    DRD_ACQUIRE_TO_AGENT_INVENTORY("DRD_ACQUIRE_TO_AGENT_INVENTORY", 1, 1), 
    DRD_ATTACHMENT("DRD_ATTACHMENT", 3, 3), 
    DRD_ATTACHMENT_EXISTS("DRD_ATTACHMENT_EXISTS", 8, 8), 
    DRD_ATTACHMENT_TO_INV("DRD_ATTACHMENT_TO_INV", 7, 7), 
    DRD_FORCE_TO_GOD_INVENTORY("DRD_FORCE_TO_GOD_INVENTORY", 5, 5), 
    DRD_RETURN_TO_LAST_OWNER("DRD_RETURN_TO_LAST_OWNER", 10, 10), 
    DRD_RETURN_TO_OWNER("DRD_RETURN_TO_OWNER", 9, 9), 
    DRD_SAVE_INTO_AGENT_INVENTORY("DRD_SAVE_INTO_AGENT_INVENTORY", 0, 0), 
    DRD_SAVE_INTO_TASK_INVENTORY("DRD_SAVE_INTO_TASK_INVENTORY", 2, 2), 
    DRD_TAKE_INTO_AGENT_INVENTORY("DRD_TAKE_INTO_AGENT_INVENTORY", 4, 4), 
    DRD_TRASH("DRD_TRASH", 6, 6);
    
    private final int code;
    
    private EDeRezDestination(final String name, final int ordinal, final int code) {
        this.code = code;
    }
    
    public final int getCode() {
        return this.code;
    }
}
