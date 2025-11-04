// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import java.util.UUID;
import java.util.Date;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import java.util.List;

public interface WhereCondition
{
    void appendTo(final StringBuilder p0, final String p1);
    
    void appendValuesTo(final List<Object> p0);
    
    public abstract static class AbstractCondition implements WhereCondition
    {
        protected final boolean hasSingleValue;
        protected final Object value;
        protected final Object[] values;
        
        public AbstractCondition() {
            this.hasSingleValue = false;
            this.value = null;
            this.values = null;
        }
        
        public AbstractCondition(final Object value) {
            this.value = value;
            this.hasSingleValue = true;
            this.values = null;
        }
        
        public AbstractCondition(final Object[] values) {
            this.value = null;
            this.hasSingleValue = false;
            this.values = values;
        }
        
        @Override
        public void appendValuesTo(final List<Object> list) {
            int i = 0;
            if (this.hasSingleValue) {
                list.add(this.value);
            }
            if (this.values != null) {
                for (Object[] values = this.values; i < values.length; ++i) {
                    list.add(values[i]);
                }
            }
        }
    }
    
    public static class PropertyCondition extends AbstractCondition
    {
        public final String op;
        public final Property property;
        
        public PropertyCondition(final Property property, final String op) {
            this.property = property;
            this.op = op;
        }
        
        public PropertyCondition(final Property property, final String op, final Object o) {
            super(checkValueForType(property, o));
            this.property = property;
            this.op = op;
        }
        
        public PropertyCondition(final Property property, final String op, final Object[] array) {
            super(checkValuesForType(property, array));
            this.property = property;
            this.op = op;
        }
        
        private static Object checkValueForType(final Property property, final Object o) {
            if (o != null && o.getClass().isArray()) {
                throw new DaoException("Illegal value: found array, but simple object required");
            }
            final Class<?> type = property.type;
            if (type != Date.class) {
                if (type != UUID.class) {
                    if (property.type == Boolean.TYPE || property.type == Boolean.class) {
                        if (o instanceof Boolean) {
                            int i;
                            if (!(boolean)o) {
                                i = 0;
                            }
                            else {
                                i = 1;
                            }
                            return i;
                        }
                        if (!(o instanceof Number)) {
                            if (o instanceof String) {
                                final String s = (String)o;
                                if ("TRUE".equalsIgnoreCase(s)) {
                                    return 1;
                                }
                                if (!"FALSE".equalsIgnoreCase(s)) {
                                    throw new DaoException("Illegal boolean value: Strings must be \"TRUE\" or \"FALSE\" (case insesnsitive), but was " + o);
                                }
                                return 0;
                            }
                        }
                        else {
                            final int intValue = ((Number)o).intValue();
                            if (intValue != 0 && intValue != 1) {
                                throw new DaoException("Illegal boolean value: numbers must be 0 or 1, but was " + o);
                            }
                        }
                    }
                    return o;
                }
                if (o instanceof UUID) {
                    return ((UUID)o).toString();
                }
                if (!(o instanceof String)) {
                    throw new DaoException("Illegal UUID value: expected java.util.UUID or String for value " + o);
                }
                return o;
            }
            else {
                if (o instanceof Date) {
                    return ((Date)o).getTime();
                }
                if (!(o instanceof Long)) {
                    throw new DaoException("Illegal date value: expected java.util.Date or Long for value " + o);
                }
                return o;
            }
        }
        
        private static Object[] checkValuesForType(final Property property, final Object[] array) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = checkValueForType(property, array[i]);
            }
            return array;
        }
        
        @Override
        public void appendTo(final StringBuilder sb, final String str) {
            if (str != null) {
                sb.append(str).append('.');
            }
            sb.append('\'').append(this.property.columnName).append('\'').append(this.op);
        }
    }
    
    public static class StringCondition extends AbstractCondition
    {
        protected final String string;
        
        public StringCondition(final String string) {
            this.string = string;
        }
        
        public StringCondition(final String string, final Object o) {
            super(o);
            this.string = string;
        }
        
        public StringCondition(final String string, final Object... array) {
            super(array);
            this.string = string;
        }
        
        @Override
        public void appendTo(final StringBuilder sb, final String s) {
            sb.append(this.string);
        }
    }
}
