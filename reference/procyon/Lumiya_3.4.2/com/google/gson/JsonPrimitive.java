// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement
{
    private static final Class<?>[] PRIMITIVE_TYPES;
    private Object value;
    
    static {
        PRIMITIVE_TYPES = new Class[] { Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };
    }
    
    public JsonPrimitive(final Boolean value) {
        this.setValue(value);
    }
    
    public JsonPrimitive(final Character value) {
        this.setValue(value);
    }
    
    public JsonPrimitive(final Number value) {
        this.setValue(value);
    }
    
    JsonPrimitive(final Object value) {
        this.setValue(value);
    }
    
    public JsonPrimitive(final String value) {
        this.setValue(value);
    }
    
    private static boolean isIntegral(final JsonPrimitive jsonPrimitive) {
        if (!(jsonPrimitive.value instanceof Number)) {
            return false;
        }
        final Number n = (Number)jsonPrimitive.value;
        return n instanceof BigInteger || n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte;
    }
    
    private static boolean isPrimitiveOrString(final Object o) {
        if (!(o instanceof String)) {
            final Class<?> class1 = o.getClass();
            final Class<?>[] primitive_TYPES = JsonPrimitive.PRIMITIVE_TYPES;
            for (int length = primitive_TYPES.length, i = 0; i < length; ++i) {
                if (primitive_TYPES[i].isAssignableFrom(class1)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    JsonPrimitive deepCopy() {
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = true;
        final boolean b2 = false;
        boolean b3 = false;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final JsonPrimitive jsonPrimitive = (JsonPrimitive)o;
        if (this.value == null) {
            if (jsonPrimitive.value == null) {
                b3 = true;
            }
            return b3;
        }
        if (isIntegral(this) && isIntegral(jsonPrimitive)) {
            return this.getAsNumber().longValue() == jsonPrimitive.getAsNumber().longValue() && b;
        }
        if (this.value instanceof Number && jsonPrimitive.value instanceof Number) {
            final double doubleValue = this.getAsNumber().doubleValue();
            final double doubleValue2 = jsonPrimitive.getAsNumber().doubleValue();
            if (doubleValue == doubleValue2) {
                return true;
            }
            boolean b4;
            if (!Double.isNaN(doubleValue)) {
                b4 = b2;
            }
            else {
                b4 = b2;
                if (Double.isNaN(doubleValue2)) {
                    return true;
                }
            }
            return b4;
            b4 = true;
            return b4;
        }
        return this.value.equals(jsonPrimitive.value);
    }
    
    @Override
    public BigDecimal getAsBigDecimal() {
        BigDecimal bigDecimal;
        if (!(this.value instanceof BigDecimal)) {
            bigDecimal = new BigDecimal(this.value.toString());
        }
        else {
            bigDecimal = (BigDecimal)this.value;
        }
        return bigDecimal;
    }
    
    @Override
    public BigInteger getAsBigInteger() {
        BigInteger bigInteger;
        if (!(this.value instanceof BigInteger)) {
            bigInteger = new BigInteger(this.value.toString());
        }
        else {
            bigInteger = (BigInteger)this.value;
        }
        return bigInteger;
    }
    
    @Override
    public boolean getAsBoolean() {
        if (!this.isBoolean()) {
            return Boolean.parseBoolean(this.getAsString());
        }
        return this.getAsBooleanWrapper();
    }
    
    @Override
    Boolean getAsBooleanWrapper() {
        return (Boolean)this.value;
    }
    
    @Override
    public byte getAsByte() {
        byte b;
        if (!this.isNumber()) {
            b = Byte.parseByte(this.getAsString());
        }
        else {
            b = this.getAsNumber().byteValue();
        }
        return b;
    }
    
    @Override
    public char getAsCharacter() {
        return this.getAsString().charAt(0);
    }
    
    @Override
    public double getAsDouble() {
        double n;
        if (!this.isNumber()) {
            n = Double.parseDouble(this.getAsString());
        }
        else {
            n = this.getAsNumber().doubleValue();
        }
        return n;
    }
    
    @Override
    public float getAsFloat() {
        float n;
        if (!this.isNumber()) {
            n = Float.parseFloat(this.getAsString());
        }
        else {
            n = this.getAsNumber().floatValue();
        }
        return n;
    }
    
    @Override
    public int getAsInt() {
        int n;
        if (!this.isNumber()) {
            n = Integer.parseInt(this.getAsString());
        }
        else {
            n = this.getAsNumber().intValue();
        }
        return n;
    }
    
    @Override
    public long getAsLong() {
        long n;
        if (!this.isNumber()) {
            n = Long.parseLong(this.getAsString());
        }
        else {
            n = this.getAsNumber().longValue();
        }
        return n;
    }
    
    @Override
    public Number getAsNumber() {
        Number n;
        if (!(this.value instanceof String)) {
            n = (Number)this.value;
        }
        else {
            n = new LazilyParsedNumber((String)this.value);
        }
        return n;
    }
    
    @Override
    public short getAsShort() {
        short n;
        if (!this.isNumber()) {
            n = Short.parseShort(this.getAsString());
        }
        else {
            n = this.getAsNumber().shortValue();
        }
        return n;
    }
    
    @Override
    public String getAsString() {
        if (this.isNumber()) {
            return this.getAsNumber().toString();
        }
        if (!this.isBoolean()) {
            return (String)this.value;
        }
        return this.getAsBooleanWrapper().toString();
    }
    
    @Override
    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        if (isIntegral(this)) {
            final long longValue = this.getAsNumber().longValue();
            return (int)(longValue ^ longValue >>> 32);
        }
        if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        }
        final long doubleToLongBits = Double.doubleToLongBits(this.getAsNumber().doubleValue());
        return (int)(doubleToLongBits ^ doubleToLongBits >>> 32);
    }
    
    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }
    
    public boolean isNumber() {
        return this.value instanceof Number;
    }
    
    public boolean isString() {
        return this.value instanceof String;
    }
    
    void setValue(final Object value) {
        boolean b = false;
        if (!(value instanceof Character)) {
            if (value instanceof Number || isPrimitiveOrString(value)) {
                b = true;
            }
            $Gson$Preconditions.checkArgument(b);
            this.value = value;
        }
        else {
            this.value = String.valueOf((char)value);
        }
    }
}
