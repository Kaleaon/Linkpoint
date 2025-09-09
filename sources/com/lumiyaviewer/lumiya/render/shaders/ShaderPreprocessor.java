package com.lumiyaviewer.lumiya.render.shaders;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

public class ShaderPreprocessor {
    private final ImmutableMap<String, String> definedMacros;

    public ShaderPreprocessor(Map<String, String> map) {
        this.definedMacros = ImmutableMap.copyOf(map);
    }

    @Nullable
    private String processCode(BufferedReader bufferedReader, @Nullable StringBuilder sb) throws IOException {
        String str = null;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return str;
            }
            String trim = readLine.trim();
            if (trim.startsWith("#endif") || trim.startsWith("#else")) {
                return trim;
            }
            if (trim.startsWith("#ifdef") || trim.startsWith("#ifndef")) {
                boolean startsWith = trim.startsWith("#ifdef");
                boolean containsKey = this.definedMacros.containsKey(trim.substring(trim.indexOf(32)).trim());
                String processCode = processCode(bufferedReader, startsWith == containsKey ? sb : null);
                if (Objects.equal(processCode, "#else")) {
                    processCode = processCode(bufferedReader, startsWith != containsKey ? sb : null);
                }
                if (!Objects.equal(processCode, "#endif")) {
                    throw new IOException("#endif expected");
                }
            } else if (sb != null) {
                String str2 = trim;
                for (Map.Entry entry : this.definedMacros.entrySet()) {
                    str2 = str2.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
                }
                sb.append(str2).append("\r\n");
                str = str2;
            }
            str = trim;
        }
    }

    public String processCode(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        processCode(bufferedReader, sb);
        return sb.toString();
    }
}
