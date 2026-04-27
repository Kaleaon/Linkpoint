// 
// Decompiled by Procyon v0.6.0
// 

package com.google.thirdparty.publicsuffix;

import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class TrieParser
{
    private static final Joiner PREFIX_JOINER;
    
    static {
        PREFIX_JOINER = Joiner.on("");
    }
    
    private static int doParseTrieToBuilder(final List<CharSequence> list, final CharSequence charSequence, final ImmutableMap.Builder<String, PublicSuffixType> builder) {
        final int length = charSequence.length();
        char c = '\0';
        while (true) {
            for (int i = 0; i < length; ++i) {
                char char1;
                c = (char1 = charSequence.charAt(i));
                if (c == '&' || (char1 = c) == '?' || (char1 = c) == '!' || (char1 = c) == ':' || (char1 = c) == ',') {
                    list.add(0, reverse(charSequence.subSequence(0, i)));
                    if (char1 == '!' || char1 == '?' || char1 == ':' || char1 == ',') {
                        final String join = TrieParser.PREFIX_JOINER.join(list);
                        if (join.length() > 0) {
                            builder.put(join, PublicSuffixType.fromCode(char1));
                        }
                    }
                    ++i;
                    Label_0076: {
                        if (char1 != '?' && char1 != ',') {
                            int n = i;
                            do {
                                i = n;
                                if (n >= length) {
                                    break Label_0076;
                                }
                                n += doParseTrieToBuilder(list, charSequence.subSequence(n, length), builder);
                            } while (charSequence.charAt(n) != '?' && charSequence.charAt(n) != ',');
                            i = n + 1;
                        }
                    }
                    list.remove(0);
                    return i;
                }
            }
            char char1 = c;
            continue;
        }
    }
    
    static ImmutableMap<String, PublicSuffixType> parseTrie(final CharSequence charSequence) {
        final ImmutableMap.Builder<String, PublicSuffixType> builder = ImmutableMap.builder();
        for (int length = charSequence.length(), i = 0; i < length; i += doParseTrieToBuilder((List<CharSequence>)Lists.newLinkedList(), charSequence.subSequence(i, length), builder)) {}
        return builder.build();
    }
    
    private static CharSequence reverse(final CharSequence charSequence) {
        int i = 1;
        final int length = charSequence.length();
        if (length > 1) {
            final char[] value = new char[length];
            value[0] = charSequence.charAt(length - 1);
            while (i < length) {
                value[i] = charSequence.charAt(length - 1 - i);
                if (Character.isSurrogatePair(value[i], value[i - 1])) {
                    swap(value, i - 1, i);
                }
                ++i;
            }
            return new String(value);
        }
        return charSequence;
    }
    
    private static void swap(final char[] array, final int n, final int n2) {
        final char c = array[n];
        array[n] = array[n2];
        array[n2] = c;
    }
}
