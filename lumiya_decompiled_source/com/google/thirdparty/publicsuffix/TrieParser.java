package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
@GwtCompatible
/* loaded from: classes.dex */
class TrieParser {
    private static final Joiner PREFIX_JOINER = Joiner.on("");

    TrieParser() {
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x005a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
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
        Lf:
            if (r1 < r3) goto L33
        L11:
            java.lang.CharSequence r4 = r10.subSequence(r2, r1)
            java.lang.CharSequence r4 = reverse(r4)
            r9.add(r2, r4)
            if (r0 != r5) goto L46
        L1e:
            com.google.common.base.Joiner r4 = com.google.thirdparty.publicsuffix.TrieParser.PREFIX_JOINER
            java.lang.String r4 = r4.join(r9)
            int r5 = r4.length()
            if (r5 > 0) goto L4d
        L2a:
            int r1 = r1 + 1
            if (r0 != r7) goto L55
        L2e:
            r0 = r1
        L2f:
            r9.remove(r2)
            return r0
        L33:
            char r0 = r10.charAt(r1)
            r4 = 38
            if (r0 == r4) goto L11
            if (r0 == r7) goto L11
            if (r0 == r5) goto L11
            if (r0 == r8) goto L11
            if (r0 == r6) goto L11
            int r1 = r1 + 1
            goto Lf
        L46:
            if (r0 == r7) goto L1e
            if (r0 == r8) goto L1e
            if (r0 == r6) goto L1e
            goto L2a
        L4d:
            com.google.thirdparty.publicsuffix.PublicSuffixType r5 = com.google.thirdparty.publicsuffix.PublicSuffixType.fromCode(r0)
            r11.put(r4, r5)
            goto L2a
        L55:
            if (r0 == r6) goto L2e
            r0 = r1
        L58:
            if (r0 >= r3) goto L2f
            java.lang.CharSequence r1 = r10.subSequence(r0, r3)
            int r1 = doParseTrieToBuilder(r9, r1, r11)
            int r0 = r0 + r1
            char r1 = r10.charAt(r0)
            if (r1 != r7) goto L6c
        L69:
            int r0 = r0 + 1
            goto L2f
        L6c:
            char r1 = r10.charAt(r0)
            if (r1 == r6) goto L69
            goto L58
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.thirdparty.publicsuffix.TrieParser.doParseTrieToBuilder(java.util.List, java.lang.CharSequence, com.google.common.collect.ImmutableMap$Builder):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence charSequence) {
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
        if (length > 1) {
            char[] cArr = new char[length];
            cArr[0] = charSequence.charAt(length - 1);
            for (int i = 1; i < length; i++) {
                cArr[i] = charSequence.charAt((length - 1) - i);
                if (Character.isSurrogatePair(cArr[i], cArr[i - 1])) {
                    swap(cArr, i - 1, i);
                }
            }
            return new String(cArr);
        }
        return charSequence;
    }

    private static void swap(char[] cArr, int i, int i2) {
        char c = cArr[i];
        cArr[i] = cArr[i2];
        cArr[i2] = c;
    }
}
