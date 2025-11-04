// 
// Decompiled by Procyon v0.6.0
// 

package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
enum PublicSuffixType
{
    ICANN('!', '?'), 
    PRIVATE(':', ',');
    
    private final char innerNodeCode;
    private final char leafNodeCode;
    
    private PublicSuffixType(final char c, final char c2) {
        this.innerNodeCode = c;
        this.leafNodeCode = c2;
    }
    
    static PublicSuffixType fromCode(final char c) {
        for (final PublicSuffixType publicSuffixType : values()) {
            if (publicSuffixType.getInnerNodeCode() == c || publicSuffixType.getLeafNodeCode() == c) {
                return publicSuffixType;
            }
        }
        throw new IllegalArgumentException("No enum corresponding to given code: " + c);
    }
    
    static PublicSuffixType fromIsPrivate(final boolean b) {
        PublicSuffixType publicSuffixType;
        if (!b) {
            publicSuffixType = PublicSuffixType.ICANN;
        }
        else {
            publicSuffixType = PublicSuffixType.PRIVATE;
        }
        return publicSuffixType;
    }
    
    char getInnerNodeCode() {
        return this.innerNodeCode;
    }
    
    char getLeafNodeCode() {
        return this.leafNodeCode;
    }
}
