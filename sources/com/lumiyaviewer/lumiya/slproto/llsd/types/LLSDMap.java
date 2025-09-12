// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDSerialized;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDMap extends LLSDNode
{
    public static class LLSDMapEntry
    {

        final String key;
        final LLSDNode value;

        public LLSDMapEntry(String s, LLSDNode llsdnode)
        {
            key = s;
            value = llsdnode;
        }
    }


    private final ImmutableMap items;

    public LLSDMap(Map map)
    {
        items = ImmutableMap.copyOf(map);
    }

    public LLSDMap(XmlPullParser xmlpullparser)
        throws LLSDXMLException, XmlPullParserException, IOException
    {
        HashMap hashmap = new HashMap();
        String s;
        for (; xmlpullparser.nextTag() != 3; hashmap.put(s, LLSDNodeFactory.parseNode(xmlpullparser)))
        {
            xmlpullparser.require(2, null, "key");
            s = xmlpullparser.nextText();
            xmlpullparser.nextTag();
        }

        items = ImmutableMap.copyOf(hashmap);
    }

    public transient LLSDMap(LLSDMapEntry allsdmapentry[])
    {
        HashMap hashmap = new HashMap(allsdmapentry.length);
        int i = 0;
        for (int j = allsdmapentry.length; i < j; i++)
        {
            LLSDMapEntry llsdmapentry = allsdmapentry[i];
            hashmap.put(llsdmapentry.key, llsdmapentry.value);
        }

        items = ImmutableMap.copyOf(hashmap);
    }

    public LLSDNode byKey(String s)
        throws LLSDInvalidKeyException
    {
        LLSDNode llsdnode = (LLSDNode)items.get(s);
        if (llsdnode != null)
        {
            return llsdnode;
        } else
        {
            throw new LLSDInvalidKeyException((new StringBuilder()).append("Map key not found, requested \"").append(s).append("\"").toString());
        }
    }

    public Set entrySet()
    {
        return items.entrySet();
    }

    public boolean keyExists(String s)
    {
        return items.containsKey(s);
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(123);
        Object obj = items.entrySet();
        dataoutputstream.writeInt(((Set) (obj)).size());
        java.util.Map.Entry entry;
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((LLSDNode)entry.getValue()).toBinary(dataoutputstream))
        {
            entry = (java.util.Map.Entry)((Iterator) (obj)).next();
            dataoutputstream.writeByte(107);
            byte abyte0[] = SLMessage.stringToVariableUTF((String)entry.getKey());
            dataoutputstream.writeInt(abyte0.length);
            dataoutputstream.write(abyte0);
        }

        dataoutputstream.writeByte(125);
    }

    public Object toObject(Class class1)
        throws LLSDException
    {
        Object obj;
        Object obj1;
        Field field;
        int i;
        Field afield[];
        int k;
        try
        {
            obj1 = class1.newInstance();
            afield = class1.getDeclaredFields();
            k = afield.length;
        }
        // Misplaced declaration of an exception variable
        catch (Class class1)
        {
            throw new LLSDException(class1.getMessage());
        }
        // Misplaced declaration of an exception variable
        catch (Class class1)
        {
            throw new LLSDException(class1.getMessage());
        }
        i = 0;
_L4:
        if (i >= k)
        {
            break MISSING_BLOCK_LABEL_485;
        }
        field = afield[i];
        class1 = (LLSDSerialized)field.getAnnotation(com/lumiyaviewer/lumiya/slproto/llsd/LLSDSerialized);
        if (class1 == null)
        {
            break MISSING_BLOCK_LABEL_487;
        }
        obj = class1.name();
        class1 = ((Class) (obj));
        if (Strings.isNullOrEmpty(((String) (obj))))
        {
            class1 = field.getName();
        }
        obj = field.getType();
        if (!keyExists(class1))
        {
            break MISSING_BLOCK_LABEL_487;
        }
        class1 = byKey(class1);
        if (((Class) (obj)).equals(Boolean.TYPE))
        {
            field.setBoolean(obj1, class1.asBoolean());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(Integer.TYPE))
        {
            field.setInt(obj1, class1.asInt());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(Double.TYPE))
        {
            field.setDouble(obj1, class1.asDouble());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(Long.TYPE))
        {
            field.setLong(obj1, class1.asLong());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(java/lang/String))
        {
            field.set(obj1, class1.asString());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(java/util/UUID))
        {
            field.set(obj1, class1.asUUID());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(java/net/URI))
        {
            field.set(obj1, class1.asURI());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals(java/util/Date))
        {
            field.set(obj1, class1.asDate());
            break MISSING_BLOCK_LABEL_487;
        }
        if (((Class) (obj)).equals([B))
        {
            field.set(obj1, class1.asBinary());
            break MISSING_BLOCK_LABEL_487;
        }
        java.lang.reflect.Type atype[];
        if (!((Class) (obj)).isAssignableFrom(java/util/List))
        {
            break MISSING_BLOCK_LABEL_487;
        }
        java.lang.reflect.Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType))
        {
            break MISSING_BLOCK_LABEL_472;
        }
        atype = ((ParameterizedType)type).getActualTypeArguments();
        if (atype.length != 1)
        {
            throw new LLSDValueTypeException(((Class) (obj)).getName(), class1);
        }
        java.lang.reflect.Type type1 = atype[0];
        int l;
        if (!(type1 instanceof Class))
        {
            break MISSING_BLOCK_LABEL_459;
        }
        l = class1.getCount();
        obj = new ArrayList(l);
        int j = 0;
_L2:
        if (j >= l)
        {
            break; /* Loop/switch isn't completed */
        }
        ((List) (obj)).add(class1.byIndex(j).toObject((Class)type1));
        j++;
        if (true) goto _L2; else goto _L1
_L1:
        field.set(obj1, obj);
        break MISSING_BLOCK_LABEL_487;
        throw new LLSDValueTypeException(((Class) (obj)).getName(), class1);
        throw new LLSDValueTypeException(((Class) (obj)).getName(), class1);
        return obj1;
        i++;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "map");
        java.util.Map.Entry entry;
        for (Iterator iterator = items.entrySet().iterator(); iterator.hasNext(); ((LLSDNode)entry.getValue()).toXML(xmlserializer))
        {
            entry = (java.util.Map.Entry)iterator.next();
            xmlserializer.startTag("", "key");
            xmlserializer.text((String)entry.getKey());
            xmlserializer.endTag("", "key");
        }

        xmlserializer.endTag("", "map");
    }
}
