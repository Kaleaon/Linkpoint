package com.lumiyaviewer.lumiya.slproto.modules.rlv;

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

    public enum RLVRuleMatchType {
        TargetSpecifiesException,
        TargetSpecifiesRestriction,
        TargetNoExceptions,
        TargetSpecifiesAllowance
    }

    private RLVRestrictionType(RLVRuleMatchType rLVRuleMatchType) {
        this.ruleMatchType = rLVRuleMatchType;
    }

    public RLVRuleMatchType getRuleMatchType() {
        return this.ruleMatchType;
    }
}
