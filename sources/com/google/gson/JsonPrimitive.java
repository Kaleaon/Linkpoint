package com.google.gson;

import com.google.gson.internal.C$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
    private static final Class<?>[] PRIMITIVE_TYPES = {Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public JsonPrimitive(Boolean bool) {
        setValue(bool);
    }

    public JsonPrimitive(Character ch) {
        setValue(ch);
    }

    public JsonPrimitive(Number number) {
        setValue(number);
    }

    JsonPrimitive(Object obj) {
        setValue(obj);
    }

    public JsonPrimitive(String str) {
        setValue(str);
    }

    private static boolean isIntegral(JsonPrimitive jsonPrimitive) {
        if (!(jsonPrimitive.value instanceof Number)) {
            return false;
        }
        Number number = (Number) jsonPrimitive.value;
        return (number instanceof BigInteger) || (number instanceof Long) || (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte);
    }

    private static boolean isPrimitiveOrString(Object obj) {
        if (obj instanceof String) {
            return true;
        }
        Class<?> cls = obj.getClass();
        for (Class<?> isAssignableFrom : PRIMITIVE_TYPES) {
            if (isAssignableFrom.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public JsonPrimitive deepCopy() {
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JsonPrimitive jsonPrimitive = (JsonPrimitive) obj;
        if (this.value == null) {
            return jsonPrimitive.value == null;
        }
        if (isIntegral(this) && isIntegral(jsonPrimitive)) {
            return getAsNumber().longValue() == jsonPrimitive.getAsNumber().longValue();
        }
        if (!(this.value instanceof Number) || !(jsonPrimitive.value instanceof Number)) {
            return this.value.equals(jsonPrimitive.value);
        }
        double doubleValue = getAsNumber().doubleValue();
        double doubleValue2 = jsonPrimitive.getAsNumber().doubleValue();
        return doubleValue == doubleValue2 || (Double.isNaN(doubleValue) && Double.isNaN(doubleValue2));
    }

    public BigDecimal getAsBigDecimal() {
        return !(this.value instanceof BigDecimal) ? new BigDecimal(this.value.toString()) : (BigDecimal) this.value;
    }

    public BigInteger getAsBigInteger() {
        return !(this.value instanceof BigInteger) ? new BigInteger(this.value.toString()) : (BigInteger) this.value;
    }

    public boolean getAsBoolean() {
        return !isBoolean() ? Boolean.parseBoolean(getAsString()) : getAsBooleanWrapper().booleanValue();
    }

    /* access modifiers changed from: package-private */
    public Boolean getAsBooleanWrapper() {
        return (Boolean) this.value;
    }

    public byte getAsByte() {
        return !isNumber() ? Byte.parseByte(getAsString()) : getAsNumber().byteValue();
    }

    public char getAsCharacter() {
        return getAsString().charAt(0);
    }

    public double getAsDouble() {
        return !isNumber() ? Double.parseDouble(getAsString()) : getAsNumber().doubleValue();
    }

    public float getAsFloat() {
        return !isNumber() ? Float.parseFloat(getAsString()) : getAsNumber().floatValue();
    }

    public int getAsInt() {
        return !isNumber() ? Integer.parseInt(getAsString()) : getAsNumber().intValue();
    }

    public long getAsLong() {
        return !isNumber() ? Long.parseLong(getAsString()) : getAsNumber().longValue();
    }

    public Number getAsNumber() {
        return !(this.value instanceof String) ? (Number) this.value : new LazilyParsedNumber((String) this.value);
    }

    public short getAsShort() {
        return !isNumber() ? Short.parseShort(getAsString()) : getAsNumber().shortValue();
    }

    public String getAsString() {
        return !isNumber() ? !isBoolean() ? (String) this.value : getAsBooleanWrapper().toString() : getAsNumber().toString();
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        if (isIntegral(this)) {
            long longValue = getAsNumber().longValue();
            return (int) (longValue ^ (longValue >>> 32));
        } else if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        } else {
            long doubleToLongBits = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
        }
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

    /* access modifiers changed from: package-private */
    public void setValue(Object obj) {
        boolean z = false;
        if (!(obj instanceof Character)) {
            if ((obj instanceof Number) || isPrimitiveOrString(obj)) {
                z = true;
            }
            C$Gson$Preconditions.checkArgument(z);
            this.value = obj;
            return;
        }
        this.value = String.valueOf(((Character) obj).charValue());
    }
}
