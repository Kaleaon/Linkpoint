// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


public final class EDeRezDestination extends Enum
{

    private static final EDeRezDestination $VALUES[];
    public static final EDeRezDestination DRD_ACQUIRE_TO_AGENT_INVENTORY;
    public static final EDeRezDestination DRD_ATTACHMENT;
    public static final EDeRezDestination DRD_ATTACHMENT_EXISTS;
    public static final EDeRezDestination DRD_ATTACHMENT_TO_INV;
    public static final EDeRezDestination DRD_FORCE_TO_GOD_INVENTORY;
    public static final EDeRezDestination DRD_RETURN_TO_LAST_OWNER;
    public static final EDeRezDestination DRD_RETURN_TO_OWNER;
    public static final EDeRezDestination DRD_SAVE_INTO_AGENT_INVENTORY;
    public static final EDeRezDestination DRD_SAVE_INTO_TASK_INVENTORY;
    public static final EDeRezDestination DRD_TAKE_INTO_AGENT_INVENTORY;
    public static final EDeRezDestination DRD_TRASH;
    private final int code;

    private EDeRezDestination(String s, int i, int j)
    {
        super(s, i);
        code = j;
    }

    public static EDeRezDestination valueOf(String s)
    {
        return (EDeRezDestination)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/types/EDeRezDestination, s);
    }

    public static EDeRezDestination[] values()
    {
        return $VALUES;
    }

    public final int getCode()
    {
        return code;
    }

    static 
    {
        DRD_SAVE_INTO_AGENT_INVENTORY = new EDeRezDestination("DRD_SAVE_INTO_AGENT_INVENTORY", 0, 0);
        DRD_ACQUIRE_TO_AGENT_INVENTORY = new EDeRezDestination("DRD_ACQUIRE_TO_AGENT_INVENTORY", 1, 1);
        DRD_SAVE_INTO_TASK_INVENTORY = new EDeRezDestination("DRD_SAVE_INTO_TASK_INVENTORY", 2, 2);
        DRD_ATTACHMENT = new EDeRezDestination("DRD_ATTACHMENT", 3, 3);
        DRD_TAKE_INTO_AGENT_INVENTORY = new EDeRezDestination("DRD_TAKE_INTO_AGENT_INVENTORY", 4, 4);
        DRD_FORCE_TO_GOD_INVENTORY = new EDeRezDestination("DRD_FORCE_TO_GOD_INVENTORY", 5, 5);
        DRD_TRASH = new EDeRezDestination("DRD_TRASH", 6, 6);
        DRD_ATTACHMENT_TO_INV = new EDeRezDestination("DRD_ATTACHMENT_TO_INV", 7, 7);
        DRD_ATTACHMENT_EXISTS = new EDeRezDestination("DRD_ATTACHMENT_EXISTS", 8, 8);
        DRD_RETURN_TO_OWNER = new EDeRezDestination("DRD_RETURN_TO_OWNER", 9, 9);
        DRD_RETURN_TO_LAST_OWNER = new EDeRezDestination("DRD_RETURN_TO_LAST_OWNER", 10, 10);
        $VALUES = (new EDeRezDestination[] {
            DRD_SAVE_INTO_AGENT_INVENTORY, DRD_ACQUIRE_TO_AGENT_INVENTORY, DRD_SAVE_INTO_TASK_INVENTORY, DRD_ATTACHMENT, DRD_TAKE_INTO_AGENT_INVENTORY, DRD_FORCE_TO_GOD_INVENTORY, DRD_TRASH, DRD_ATTACHMENT_TO_INV, DRD_ATTACHMENT_EXISTS, DRD_RETURN_TO_OWNER, 
            DRD_RETURN_TO_LAST_OWNER
        });
    }
}
