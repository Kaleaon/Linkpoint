// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

public enum SLSaleType
{
    FS_CONTENTS("FS_CONTENTS", 3, 3, "cntn"), 
    FS_COPY("FS_COPY", 2, 2, "copy"), 
    FS_NOT("FS_NOT", 0, 0, "not"), 
    FS_ORIGINAL("FS_ORIGINAL", 1, 1, "orig"), 
    FS_UNKNOWN("FS_UNKNOWN", 4, -1, "unknown");
    
    private String stringCode;
    private int typeCode;
    
    private SLSaleType(final String name, final int ordinal, final int typeCode, final String stringCode) {
        this.typeCode = typeCode;
        this.stringCode = stringCode;
    }
    
    public static SLSaleType getByString(final String anotherString) {
        final SLSaleType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLSaleType slSaleType = values[i];
            if (slSaleType.stringCode.equalsIgnoreCase(anotherString)) {
                return slSaleType;
            }
        }
        return SLSaleType.FS_UNKNOWN;
    }
    
    public static SLSaleType getByType(final int n) {
        final SLSaleType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLSaleType slSaleType = values[i];
            if (slSaleType.typeCode == n) {
                return slSaleType;
            }
        }
        return SLSaleType.FS_UNKNOWN;
    }
    
    public String getStringCode() {
        return this.stringCode;
    }
    
    public int getTypeCode() {
        return this.typeCode;
    }
}
