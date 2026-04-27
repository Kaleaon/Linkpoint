package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@GwtCompatible
class TrieParser {
    private static final Joiner PREFIX_JOINER = Joiner.on("");

    TrieParser() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x002f A[EDGE_INSN: B:38:0x002f->B:9:0x002f ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int doParseTrieToBuilder(java.util.List<java.lang.CharSequence> r9, java.lang.CharSequence r10, com.google.common.collect.ImmutableMap.Builder<java.lang.String, com.google.thirdparty.publicsuffix.PublicSuffixType> r11) {
        /*
            r8 = 58
            r5 = 33
            r7 = 63
            r6 = 44
            r2 = 0
            int r3 = r10.length()
            r0 = r2
            r1 = r2
        L_0x000f:
            if (r1 < r3) goto L_0x0033
        L_0x0011:
            java.lang.CharSequence r4 = r10.subSequence(r2, r1)
            java.lang.CharSequence r4 = reverse(r4)
            r9.add(r2, r4)
            if (r0 != r5) goto L_0x0046
        L_0x001e:
            com.google.common.base.Joiner r4 = PREFIX_JOINER
            java.lang.String r4 = r4.join((java.lang.Iterable<?>) r9)
            int r5 = r4.length()
            if (r5 > 0) goto L_0x004d
        L_0x002a:
            int r1 = r1 + 1
            if (r0 != r7) goto L_0x0055
        L_0x002e:
            r0 = r1
        L_0x002f:
            r9.remove(r2)
            return r0
        L_0x0033:
            char r0 = r10.charAt(r1)
            r4 = 38
            if (r0 == r4) goto L_0x0011
            if (r0 == r7) goto L_0x0011
            if (r0 == r5) goto L_0x0011
            if (r0 == r8) goto L_0x0011
            if (r0 == r6) goto L_0x0011
            int r1 = r1 + 1
            goto L_0x000f
        L_0x0046:
            if (r0 == r7) goto L_0x001e
            if (r0 == r8) goto L_0x001e
            if (r0 == r6) goto L_0x001e
            goto L_0x002a
        L_0x004d:
            com.google.thirdparty.publicsuffix.PublicSuffixType r5 = com.google.thirdparty.publicsuffix.PublicSuffixType.fromCode(r0)
            r11.put(r4, r5)
            goto L_0x002a
        L_0x0055:
            if (r0 == r6) goto L_0x002e
            r0 = r1
        L_0x0058:
            if (r0 >= r3) goto L_0x002f
            java.lang.CharSequence r1 = r10.subSequence(r0, r3)
            int r1 = doParseTrieToBuilder(r9, r1, r11)
            int r0 = r0 + r1
            char r1 = r10.charAt(r0)
            if (r1 != r7) goto L_0x006c
        L_0x0069:
            int r0 = r0 + 1
            goto L_0x002f
        L_0x006c:
            char r1 = r10.charAt(r0)
            if (r1 == r6) goto L_0x0069
            goto L_0x0058
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.thirdparty.publicsuffix.TrieParser.doParseTrieToBuilder(java.util.List, java.lang.CharSequence, com.google.common.collect.ImmutableMap$Builder):int");
    }

    static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence charSequence) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        int length = charSequence.length();
        int i = 0;
        while (i < length) {
            i += doParseTrieToBuilder(Lists.newLinkedList(), charSequence.subSequence(i, length), builder);
        }
        return builder.build();
    }

    private static CharSequence reverse(CharSequence charSequence) {
        int length = charSequence.length();
        if (length <= 1) {
            return charSequence;
        }
        char[] cArr = new char[length];
        cArr[0] = (char) charSequence.charAt(length - 1);
        for (int i = 1; i < length; i++) {
            cArr[i] = (char) charSequence.charAt((length - 1) - i);
            if (Character.isSurrogatePair(cArr[i], cArr[i - 1])) {
                swap(cArr, i - 1, i);
            }
        }
        return new String(cArr);
    }

    private static void swap(char[] cArr, int i, int i2) {
        char c = cArr[i];
        cArr[i] = (char) cArr[i2];
        cArr[i2] = (char) c;
    }
}
