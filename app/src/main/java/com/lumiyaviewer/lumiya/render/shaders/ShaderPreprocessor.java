package com.lumiyaviewer.lumiya.render.shaders;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class ShaderPreprocessor {
    private final ImmutableMap<String, String> definedMacros;

    public ShaderPreprocessor(Map<String, String> map) {
        this.definedMacros = ImmutableMap.copyOf(map);
    }

    @Nullable
    private String processCode(BufferedReader bufferedReader, @Nullable StringBuilder stringBuilder) throws IOException {
        String str = null;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return str;
            }
            readLine = readLine.trim();
            if (readLine.startsWith("#endif") || readLine.startsWith("#else")) {
                return readLine;
            }
            if (readLine.startsWith("#ifdef") || readLine.startsWith("#ifndef")) {
                boolean startsWith = readLine.startsWith("#ifdef");
                boolean containsKey = this.definedMacros.containsKey(readLine.substring(readLine.indexOf(32)).trim());
                String processCode = processCode(bufferedReader, startsWith == containsKey ? stringBuilder : null);
                if (Objects.equal(processCode, "#else")) {
                    processCode = processCode(bufferedReader, startsWith != containsKey ? stringBuilder : null);
                }
                if (!Objects.equal(processCode, "#endif")) {
                    throw new IOException("#endif expected");
                }
            } else if (stringBuilder != null) {
                String str2 = readLine;
                for (Entry entry : this.definedMacros.entrySet()) {
                    str2 = str2.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
                }
                stringBuilder.append(str2).append("\r\n");
                str = str2;
            }
            str = readLine;
        }
    }

    public String processCode(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        processCode(bufferedReader, stringBuilder);
        return stringBuilder.toString();
    }
}
