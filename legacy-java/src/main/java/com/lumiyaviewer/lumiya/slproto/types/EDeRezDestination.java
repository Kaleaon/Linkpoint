package com.lumiyaviewer.lumiya.slproto.types;

/* loaded from: classes.dex */
public enum EDeRezDestination {
    DRD_SAVE_INTO_AGENT_INVENTORY(0),
    DRD_ACQUIRE_TO_AGENT_INVENTORY(1),
    DRD_SAVE_INTO_TASK_INVENTORY(2),
    DRD_ATTACHMENT(3),
    DRD_TAKE_INTO_AGENT_INVENTORY(4),
    DRD_FORCE_TO_GOD_INVENTORY(5),
    DRD_TRASH(6),
    DRD_ATTACHMENT_TO_INV(7),
    DRD_ATTACHMENT_EXISTS(8),
    DRD_RETURN_TO_OWNER(9),
    DRD_RETURN_TO_LAST_OWNER(10);

    private final int code;

    EDeRezDestination(int i) {
        this.code = i;
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static EDeRezDestination[] valuesCustom() {
        return values();
    }

    public final int getCode() {
        return this.code;
    }
}
