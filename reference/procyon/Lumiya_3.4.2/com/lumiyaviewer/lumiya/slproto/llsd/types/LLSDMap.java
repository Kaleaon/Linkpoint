// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Date;
import java.net.URI;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDSerialized;
import java.util.Iterator;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.io.DataOutputStream;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDMap extends LLSDNode
{
    @Nonnull
    private final ImmutableMap<String, LLSDNode> items;
    
    public LLSDMap(final Map<String, LLSDNode> map) {
        this.items = ImmutableMap.copyOf((Map<? extends String, ? extends LLSDNode>)map);
    }
    
    public LLSDMap(final XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
        final HashMap hashMap = new HashMap();
        while (xmlPullParser.nextTag() != 3) {
            xmlPullParser.require(2, (String)null, "key");
            final String nextText = xmlPullParser.nextText();
            xmlPullParser.nextTag();
            hashMap.put(nextText, LLSDNodeFactory.parseNode(xmlPullParser));
        }
        this.items = (ImmutableMap<String, LLSDNode>)ImmutableMap.copyOf((Map<?, ?>)hashMap);
    }
    
    public LLSDMap(final LLSDMapEntry... array) {
        final HashMap hashMap = new HashMap(array.length);
        for (int i = 0; i < array.length; ++i) {
            final LLSDMapEntry llsdMapEntry = array[i];
            hashMap.put(llsdMapEntry.key, llsdMapEntry.value);
        }
        this.items = (ImmutableMap<String, LLSDNode>)ImmutableMap.copyOf((Map<?, ?>)hashMap);
    }
    
    @Override
    public LLSDNode byKey(final String str) throws LLSDInvalidKeyException {
        final LLSDNode llsdNode = this.items.get(str);
        if (llsdNode != null) {
            return llsdNode;
        }
        throw new LLSDInvalidKeyException("Map key not found, requested \"" + str + "\"");
    }
    
    public Set<Map.Entry<String, LLSDNode>> entrySet() {
        return this.items.entrySet();
    }
    
    @Override
    public boolean keyExists(final String s) {
        return this.items.containsKey(s);
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(123);
        final ImmutableSet<Map.Entry<String, LLSDNode>> entrySet = this.items.entrySet();
        dataOutputStream.writeInt(entrySet.size());
        for (final Map.Entry<String, V> entry : entrySet) {
            dataOutputStream.writeByte(107);
            final byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(entry.getKey());
            dataOutputStream.writeInt(stringToVariableUTF.length);
            dataOutputStream.write(stringToVariableUTF);
            ((LLSDNode)entry.getValue()).toBinary(dataOutputStream);
        }
        dataOutputStream.writeByte(125);
    }
    
    @Override
    public <T> T toObject(final Class<? extends T> clazz) throws LLSDException {
        while (true) {
            Class<?> type;
            LLSDNode byKey;
            while (true) {
                T instance;
                Field field;
                try {
                    instance = clazz.newInstance();
                    final Field[] declaredFields = clazz.getDeclaredFields();
                    for (int length = declaredFields.length, i = 0; i < length; ++i) {
                        field = declaredFields[i];
                        final LLSDSerialized llsdSerialized = field.getAnnotation(LLSDSerialized.class);
                        if (llsdSerialized != null) {
                            String s;
                            if (Strings.isNullOrEmpty(s = llsdSerialized.name())) {
                                s = field.getName();
                            }
                            type = field.getType();
                            if (this.keyExists(s)) {
                                byKey = this.byKey(s);
                                if (type.equals(Boolean.TYPE)) {
                                    field.setBoolean(instance, byKey.asBoolean());
                                }
                                else {
                                    if (!type.equals(Integer.TYPE)) {
                                        goto Label_0155;
                                    }
                                    field.setInt(instance, byKey.asInt());
                                }
                            }
                        }
                    }
                    return instance;
                }
                catch (final IllegalAccessException ex) {
                    throw new LLSDException(ex.getMessage());
                }
                catch (final InstantiationException ex2) {
                    throw new LLSDException(ex2.getMessage());
                }
                if (type.equals(Long.TYPE)) {
                    field.setLong(instance, byKey.asLong());
                    continue;
                }
                if (type.equals(String.class)) {
                    field.set(instance, byKey.asString());
                    continue;
                }
                if (type.equals(UUID.class)) {
                    field.set(instance, byKey.asUUID());
                    continue;
                }
                if (type.equals(URI.class)) {
                    field.set(instance, byKey.asURI());
                    continue;
                }
                if (type.equals(Date.class)) {
                    field.set(instance, byKey.asDate());
                    continue;
                }
                if (type.equals(byte[].class)) {
                    field.set(instance, byKey.asBinary());
                    continue;
                }
                if (!type.isAssignableFrom(List.class)) {
                    continue;
                }
                final Type genericType = field.getGenericType();
                if (!(genericType instanceof ParameterizedType)) {
                    throw new LLSDValueTypeException(type.getName(), byKey);
                }
                final Type[] actualTypeArguments = ((ParameterizedType)genericType).getActualTypeArguments();
                if (actualTypeArguments.length != 1) {
                    throw new LLSDValueTypeException(type.getName(), byKey);
                }
                final Type type2 = actualTypeArguments[0];
                if (type2 instanceof Class) {
                    final int count = byKey.getCount();
                    final ArrayList value = new ArrayList(count);
                    for (int j = 0; j < count; ++j) {
                        value.add(byKey.byIndex(j).toObject((Class<?>)type2));
                    }
                    field.set(instance, value);
                    continue;
                }
                break;
            }
            throw new LLSDValueTypeException(type.getName(), byKey);
        }
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "map");
        for (final Map.Entry<String, V> entry : this.items.entrySet()) {
            xmlSerializer.startTag("", "key");
            xmlSerializer.text((String)entry.getKey());
            xmlSerializer.endTag("", "key");
            ((LLSDNode)entry.getValue()).toXML(xmlSerializer);
        }
        xmlSerializer.endTag("", "map");
    }
    
    public static class LLSDMapEntry
    {
        final String key;
        final LLSDNode value;
        
        public LLSDMapEntry(final String key, final LLSDNode value) {
            this.key = key;
            this.value = value;
        }
    }
}
