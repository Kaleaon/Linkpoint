// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ShaderPreprocessor
{

    private final ImmutableMap definedMacros;

    public ShaderPreprocessor(Map map)
    {
        definedMacros = ImmutableMap.copyOf(map);
    }

    private String processCode(BufferedReader bufferedreader, StringBuilder stringbuilder)
        throws IOException
    {
        String s = null;
        do
        {
            String s1 = bufferedreader.readLine();
            if (s1 == null)
            {
                break;
            }
            s = s1.trim();
            if (s.startsWith("#endif") || s.startsWith("#else"))
            {
                return s;
            }
            if (s.startsWith("#ifdef") || s.startsWith("#ifndef"))
            {
                boolean flag = s.startsWith("#ifdef");
                Object obj = s.substring(s.indexOf(' ')).trim();
                boolean flag1 = definedMacros.containsKey(obj);
                String s2;
                if (flag == flag1)
                {
                    obj = stringbuilder;
                } else
                {
                    obj = null;
                }
                s2 = processCode(bufferedreader, ((StringBuilder) (obj)));
                obj = s2;
                if (Objects.equal(s2, "#else"))
                {
                    if (flag != flag1)
                    {
                        obj = stringbuilder;
                    } else
                    {
                        obj = null;
                    }
                    obj = processCode(bufferedreader, ((StringBuilder) (obj)));
                }
                if (!Objects.equal(obj, "#endif"))
                {
                    throw new IOException("#endif expected");
                }
            } else
            if (stringbuilder != null)
            {
                for (Iterator iterator = definedMacros.entrySet().iterator(); iterator.hasNext();)
                {
                    java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                    s = s.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
                }

                stringbuilder.append(s).append("\r\n");
            }
        } while (true);
        return s;
    }

    public String processCode(BufferedReader bufferedreader)
        throws IOException
    {
        StringBuilder stringbuilder = new StringBuilder();
        processCode(bufferedreader, stringbuilder);
        return stringbuilder.toString();
    }
}
