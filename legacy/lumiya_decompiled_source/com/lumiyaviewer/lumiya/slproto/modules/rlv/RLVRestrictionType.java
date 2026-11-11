package com.lumiyaviewer.lumiya.slproto.modules.rlv;
/* loaded from: classes.dex */
public enum RLVRestrictionType {
    detach(RLVRuleMatchType.TargetSpecifiesRestriction),
    sendchat(RLVRuleMatchType.TargetNoExceptions),
    recvchat(RLVRuleMatchType.TargetSpecifiesException),
    sendim(RLVRuleMatchType.TargetSpecifiesException),
    recvim(RLVRuleMatchType.TargetSpecifiesException),
    tplm(RLVRuleMatchType.TargetNoExceptions),
    tploc(RLVRuleMatchType.TargetNoExceptions),
    sittp(RLVRuleMatchType.TargetNoExceptions),
    tplure(RLVRuleMatchType.TargetSpecifiesException),
    accepttp(RLVRuleMatchType.TargetSpecifiesAllowance),
    showinv(RLVRuleMatchType.TargetNoExceptions),
    viewnote(RLVRuleMatchType.TargetNoExceptions),
    edit(RLVRuleMatchType.TargetSpecifiesException),
    rez(RLVRuleMatchType.TargetNoExceptions),
    unsit(RLVRuleMatchType.TargetNoExceptions),
    sit(RLVRuleMatchType.TargetNoExceptions),
    remoutfit(RLVRuleMatchType.TargetSpecifiesRestriction),
    addoutfit(RLVRuleMatchType.TargetSpecifiesRestriction),
    redirchat(RLVRuleMatchType.TargetSpecifiesRestriction),
    sendchannel(RLVRuleMatchType.TargetSpecifiesException);
    
    private RLVRuleMatchType ruleMatchType;

    /* loaded from: classes.dex */
    public enum RLVRuleMatchType {
        TargetSpecifiesException,
        TargetSpecifiesRestriction,
        TargetNoExceptions,
        TargetSpecifiesAllowance;

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static RLVRuleMatchType[] valuesCustom() {
            return values();
        }
    }

    RLVRestrictionType(RLVRuleMatchType rLVRuleMatchType) {
        this.ruleMatchType = rLVRuleMatchType;
    }

    /* renamed from: values  reason: to resolve conflict with enum method */
    public static RLVRestrictionType[] valuesCustom() {
        return values();
    }

    public RLVRuleMatchType getRuleMatchType() {
        return this.ruleMatchType;
    }
}
