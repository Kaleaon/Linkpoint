// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import java.util.Iterator;
import java.io.IOException;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

public class ShaderPreprocessor
{
    private final ImmutableMap<String, String> definedMacros;
    
    public ShaderPreprocessor(final Map<String, String> map) {
        this.definedMacros = ImmutableMap.copyOf((Map<? extends String, ? extends String>)map);
    }
    
    @Nullable
    private String processCode(final BufferedReader bufferedReader, @Nullable final StringBuilder sb) throws IOException {
        String replace = null;
        String trim;
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null) {
                return replace;
            }
            replace = (trim = line.trim());
            if (replace.startsWith("#endif")) {
                break;
            }
            if (replace.startsWith("#else")) {
                trim = replace;
                break;
            }
            if (replace.startsWith("#ifdef") || replace.startsWith("#ifndef")) {
                final boolean startsWith = replace.startsWith("#ifdef");
                final boolean containsKey = this.definedMacros.containsKey(replace.substring(replace.indexOf(32)).trim());
                StringBuilder sb2;
                if (startsWith == containsKey) {
                    sb2 = sb;
                }
                else {
                    sb2 = null;
                }
                String s;
                if (Objects.equal(s = this.processCode(bufferedReader, sb2), "#else")) {
                    StringBuilder sb3;
                    if (startsWith != containsKey) {
                        sb3 = sb;
                    }
                    else {
                        sb3 = null;
                    }
                    s = this.processCode(bufferedReader, sb3);
                }
                if (!Objects.equal(s, "#endif")) {
                    throw new IOException("#endif expected");
                }
                continue;
            }
            else {
                if (sb == null) {
                    continue;
                }
                for (final Map.Entry entry : this.definedMacros.entrySet()) {
                    replace = replace.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
                }
                sb.append(replace).append("\r\n");
            }
        }
        return trim;
        trim = replace;
        return trim;
    }
    
    public String processCode(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.processCode(bufferedReader, sb);
        return sb.toString();
    }
}
