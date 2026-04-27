// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.Util;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import okhttp3.internal.http.HttpDate;
import java.util.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public final class Headers
{
    private final String[] namesAndValues;
    
    Headers(final Builder builder) {
        this.namesAndValues = builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }
    
    private Headers(final String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }
    
    private static String get(final String[] array, final String s) {
        int length = array.length;
        int n;
        do {
            n = length - 2;
            if (n < 0) {
                return null;
            }
            length = n;
        } while (!s.equalsIgnoreCase(array[n]));
        return array[n + 1];
    }
    
    public static Headers of(final Map<String, String> map) {
        if (map != null) {
            final String[] array = new String[map.size() * 2];
            final Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Map.Entry<String, V> entry = (Map.Entry<String, V>)iterator.next();
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new IllegalArgumentException("Headers cannot be null");
                }
                final String trim = entry.getKey().trim();
                final String trim2 = ((String)entry.getValue()).trim();
                if (trim.length() == 0 || trim.indexOf(0) != -1 || trim2.indexOf(0) != -1) {
                    throw new IllegalArgumentException("Unexpected header: " + trim + ": " + trim2);
                }
                array[n] = trim;
                array[n + 1] = trim2;
                n += 2;
            }
            return new Headers(array);
        }
        throw new NullPointerException("headers == null");
    }
    
    public static Headers of(final String... array) {
        if (array == null) {
            throw new NullPointerException("namesAndValues == null");
        }
        if (array.length % 2 == 0) {
            final String[] array2 = array.clone();
            for (int i = 0; i < array2.length; ++i) {
                if (array2[i] == null) {
                    throw new IllegalArgumentException("Headers cannot be null");
                }
                array2[i] = array2[i].trim();
            }
            for (int j = 0; j < array2.length; j += 2) {
                final String str = array2[j];
                final String str2 = array2[j + 1];
                if (str.length() == 0 || str.indexOf(0) != -1 || str2.indexOf(0) != -1) {
                    throw new IllegalArgumentException("Unexpected header: " + str + ": " + str2);
                }
            }
            return new Headers(array2);
        }
        throw new IllegalArgumentException("Expected alternating header names and values");
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof Headers && Arrays.equals(((Headers)o).namesAndValues, this.namesAndValues)) {
            b = true;
        }
        return b;
    }
    
    public String get(final String s) {
        return get(this.namesAndValues, s);
    }
    
    public Date getDate(String value) {
        final Date date = null;
        value = this.get(value);
        Date parse;
        if (value == null) {
            parse = date;
        }
        else {
            parse = HttpDate.parse(value);
        }
        return parse;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
    }
    
    public String name(final int n) {
        return this.namesAndValues[n * 2];
    }
    
    public Set<String> names() {
        final TreeSet s = new TreeSet((Comparator<? super E>)String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < this.size(); ++i) {
            s.add(this.name(i));
        }
        return (Set<String>)Collections.unmodifiableSet((Set<?>)s);
    }
    
    public Builder newBuilder() {
        final Builder builder = new Builder();
        Collections.addAll(builder.namesAndValues, this.namesAndValues);
        return builder;
    }
    
    public int size() {
        return this.namesAndValues.length / 2;
    }
    
    public Map<String, List<String>> toMultimap() {
        final TreeMap treeMap = new TreeMap((Comparator<? super K>)String.CASE_INSENSITIVE_ORDER);
        for (int size = this.size(), i = 0; i < size; ++i) {
            final String lowerCase = this.name(i).toLowerCase(Locale.US);
            List list = (List)treeMap.get(lowerCase);
            if (list == null) {
                list = new ArrayList(2);
                treeMap.put(lowerCase, list);
            }
            list.add(this.value(i));
        }
        return treeMap;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); ++i) {
            sb.append(this.name(i)).append(": ").append(this.value(i)).append("\n");
        }
        return sb.toString();
    }
    
    public String value(final int n) {
        return this.namesAndValues[n * 2 + 1];
    }
    
    public List<String> values(final String s) {
        final int size = this.size();
        Object list = null;
        for (int i = 0; i < size; ++i) {
            if (s.equalsIgnoreCase(this.name(i))) {
                if (list == null) {
                    list = new ArrayList<String>(2);
                }
                ((List<String>)list).add(this.value(i));
            }
        }
        List<Object> list2;
        if (list == null) {
            list2 = Collections.emptyList();
        }
        else {
            list2 = Collections.unmodifiableList((List<?>)list);
        }
        return (List<String>)list2;
    }
    
    public static final class Builder
    {
        final List<String> namesAndValues;
        
        public Builder() {
            this.namesAndValues = new ArrayList<String>(20);
        }
        
        private void checkNameAndValue(final String s, final String s2) {
            if (s == null) {
                throw new NullPointerException("name == null");
            }
            if (s.isEmpty()) {
                throw new IllegalArgumentException("name is empty");
            }
            for (int length = s.length(), i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (char1 <= '\u001f' || char1 >= '\u007f') {
                    throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", (int)char1, i, s));
                }
            }
            if (s2 != null) {
                for (int length2 = s2.length(), j = 0; j < length2; ++j) {
                    final char char2 = s2.charAt(j);
                    if ((char2 <= '\u001f' && char2 != '\t') || char2 >= '\u007f') {
                        throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in %s value: %s", (int)char2, j, s, s2));
                    }
                }
                return;
            }
            throw new NullPointerException("value == null");
        }
        
        public Builder add(final String str) {
            final int index = str.indexOf(":");
            if (index != -1) {
                return this.add(str.substring(0, index).trim(), str.substring(index + 1));
            }
            throw new IllegalArgumentException("Unexpected header: " + str);
        }
        
        public Builder add(final String s, final String s2) {
            this.checkNameAndValue(s, s2);
            return this.addLenient(s, s2);
        }
        
        Builder addLenient(final String s) {
            final int index = s.indexOf(":", 1);
            if (index != -1) {
                return this.addLenient(s.substring(0, index), s.substring(index + 1));
            }
            if (!s.startsWith(":")) {
                return this.addLenient("", s);
            }
            return this.addLenient("", s.substring(1));
        }
        
        Builder addLenient(final String s, final String s2) {
            this.namesAndValues.add(s);
            this.namesAndValues.add(s2.trim());
            return this;
        }
        
        public Headers build() {
            return new Headers(this);
        }
        
        public String get(final String s) {
            for (int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (s.equalsIgnoreCase(this.namesAndValues.get(i))) {
                    return this.namesAndValues.get(i + 1);
                }
            }
            return null;
        }
        
        public Builder removeAll(final String s) {
            for (int i = 0; i < this.namesAndValues.size(); i += 2) {
                if (s.equalsIgnoreCase(this.namesAndValues.get(i))) {
                    this.namesAndValues.remove(i);
                    this.namesAndValues.remove(i);
                    i -= 2;
                }
            }
            return this;
        }
        
        public Builder set(final String s, final String s2) {
            this.checkNameAndValue(s, s2);
            this.removeAll(s);
            this.addLenient(s, s2);
            return this;
        }
    }
}
