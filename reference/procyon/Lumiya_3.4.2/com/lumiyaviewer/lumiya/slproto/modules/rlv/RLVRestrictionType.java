// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

public enum RLVRestrictionType
{
    accepttp("accepttp", 9, RLVRuleMatchType.TargetSpecifiesAllowance), 
    addoutfit("addoutfit", 17, RLVRuleMatchType.TargetSpecifiesRestriction), 
    detach("detach", 0, RLVRuleMatchType.TargetSpecifiesRestriction), 
    edit("edit", 12, RLVRuleMatchType.TargetSpecifiesException), 
    recvchat("recvchat", 2, RLVRuleMatchType.TargetSpecifiesException), 
    recvim("recvim", 4, RLVRuleMatchType.TargetSpecifiesException), 
    redirchat("redirchat", 18, RLVRuleMatchType.TargetSpecifiesRestriction), 
    remoutfit("remoutfit", 16, RLVRuleMatchType.TargetSpecifiesRestriction), 
    rez("rez", 13, RLVRuleMatchType.TargetNoExceptions), 
    sendchannel("sendchannel", 19, RLVRuleMatchType.TargetSpecifiesException), 
    sendchat("sendchat", 1, RLVRuleMatchType.TargetNoExceptions), 
    sendim("sendim", 3, RLVRuleMatchType.TargetSpecifiesException), 
    showinv("showinv", 10, RLVRuleMatchType.TargetNoExceptions), 
    sit("sit", 15, RLVRuleMatchType.TargetNoExceptions), 
    sittp("sittp", 7, RLVRuleMatchType.TargetNoExceptions), 
    tplm("tplm", 5, RLVRuleMatchType.TargetNoExceptions), 
    tploc("tploc", 6, RLVRuleMatchType.TargetNoExceptions), 
    tplure("tplure", 8, RLVRuleMatchType.TargetSpecifiesException), 
    unsit("unsit", 14, RLVRuleMatchType.TargetNoExceptions), 
    viewnote("viewnote", 11, RLVRuleMatchType.TargetNoExceptions);
    
    private RLVRuleMatchType ruleMatchType;
    
    private RLVRestrictionType(final String name, final int ordinal, final RLVRuleMatchType ruleMatchType) {
        this.ruleMatchType = ruleMatchType;
    }
    
    public RLVRuleMatchType getRuleMatchType() {
        return this.ruleMatchType;
    }
    
    public enum RLVRuleMatchType
    {
        TargetNoExceptions("TargetNoExceptions", 2), 
        TargetSpecifiesAllowance("TargetSpecifiesAllowance", 3), 
        TargetSpecifiesException("TargetSpecifiesException", 0), 
        TargetSpecifiesRestriction("TargetSpecifiesRestriction", 1);
        
        private RLVRuleMatchType(final String name, final int ordinal) {
        }
    }
}
