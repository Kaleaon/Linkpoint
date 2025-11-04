// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number
{
    private final String value;
    
    public LazilyParsedNumber(final String value) {
        this.value = value;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new BigDecimal(this.value);
    }
    
    @Override
    public double doubleValue() {
        return Double.parseDouble(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof LazilyParsedNumber)) {
            return false;
        }
        final LazilyParsedNumber lazilyParsedNumber = (LazilyParsedNumber)o;
        if (this.value == lazilyParsedNumber.value || this.value.equals(lazilyParsedNumber.value)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public float floatValue() {
        return Float.parseFloat(this.value);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public int intValue() {
        try {
            return Integer.parseInt(this.value);
        }
        catch (final NumberFormatException ex) {
            try {
                return (int)Long.parseLong(this.value);
            }
            catch (final NumberFormatException ex2) {
                return new BigDecimal(this.value).intValue();
            }
        }
    }
    
    @Override
    public long longValue() {
        try {
            return Long.parseLong(this.value);
        }
        catch (final NumberFormatException ex) {
            return new BigDecimal(this.value).longValue();
        }
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
