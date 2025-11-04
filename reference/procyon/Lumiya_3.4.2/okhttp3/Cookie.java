// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.http.HttpDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import okhttp3.internal.Util;
import java.util.regex.Pattern;

public final class Cookie
{
    private static final Pattern DAY_OF_MONTH_PATTERN;
    private static final Pattern MONTH_PATTERN;
    private static final Pattern TIME_PATTERN;
    private static final Pattern YEAR_PATTERN;
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;
    
    static {
        YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
        MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
        DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
        TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    }
    
    private Cookie(final String name, final String value, final long expiresAt, final String domain, final String path, final boolean secure, final boolean httpOnly, final boolean hostOnly, final boolean persistent) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }
    
    Cookie(final Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        }
        if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        }
        if (builder.domain != null) {
            this.name = builder.name;
            this.value = builder.value;
            this.expiresAt = builder.expiresAt;
            this.domain = builder.domain;
            this.path = builder.path;
            this.secure = builder.secure;
            this.httpOnly = builder.httpOnly;
            this.persistent = builder.persistent;
            this.hostOnly = builder.hostOnly;
            return;
        }
        throw new NullPointerException("builder.domain == null");
    }
    
    private static int dateCharacterOffset(final String s, int i, final int n, final boolean b) {
        while (i < n) {
            final char char1 = s.charAt(i);
            int n2 = 0;
            Label_0031: {
                if (char1 >= ' ' || char1 == '\t') {
                    if (char1 < '\u007f') {
                        if (char1 < '0' || char1 > '9') {
                            if (char1 < 'a' || char1 > 'z') {
                                if (char1 < 'A' || char1 > 'Z') {
                                    if (char1 != ':') {
                                        n2 = 0;
                                        break Label_0031;
                                    }
                                }
                            }
                        }
                    }
                }
                n2 = 1;
            }
            int n3;
            if (b) {
                n3 = 0;
            }
            else {
                n3 = 1;
            }
            if (n2 == n3) {
                return i;
            }
            ++i;
        }
        return n;
    }
    
    private static boolean domainMatch(final HttpUrl httpUrl, final String s) {
        final String host = httpUrl.host();
        return host.equals(s) || (host.endsWith(s) && host.charAt(host.length() - s.length() - 1) == '.' && !Util.verifyAsIpAddress(host));
    }
    
    static Cookie parse(final long p0, final HttpUrl p1, final String p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   java/lang/String.length:()I
        //     4: istore          4
        //     6: aload_3        
        //     7: iconst_0       
        //     8: iload           4
        //    10: bipush          59
        //    12: invokestatic    okhttp3/internal/Util.delimiterOffset:(Ljava/lang/String;IIC)I
        //    15: istore          5
        //    17: aload_3        
        //    18: iconst_0       
        //    19: iload           5
        //    21: bipush          61
        //    23: invokestatic    okhttp3/internal/Util.delimiterOffset:(Ljava/lang/String;IIC)I
        //    26: istore          6
        //    28: iload           6
        //    30: iload           5
        //    32: if_icmpeq       182
        //    35: aload_3        
        //    36: iconst_0       
        //    37: iload           6
        //    39: invokestatic    okhttp3/internal/Util.trimSubstring:(Ljava/lang/String;II)Ljava/lang/String;
        //    42: astore          7
        //    44: aload           7
        //    46: invokevirtual   java/lang/String.isEmpty:()Z
        //    49: ifne            184
        //    52: aload_3        
        //    53: iload           6
        //    55: iconst_1       
        //    56: iadd           
        //    57: iload           5
        //    59: invokestatic    okhttp3/internal/Util.trimSubstring:(Ljava/lang/String;II)Ljava/lang/String;
        //    62: astore          8
        //    64: ldc2_w          253402300799999
        //    67: lstore          9
        //    69: ldc2_w          -1
        //    72: lstore          11
        //    74: aconst_null    
        //    75: astore          13
        //    77: aconst_null    
        //    78: astore          14
        //    80: iconst_0       
        //    81: istore          15
        //    83: iconst_0       
        //    84: istore          16
        //    86: iconst_1       
        //    87: istore          17
        //    89: iconst_0       
        //    90: istore          18
        //    92: iload           5
        //    94: iconst_1       
        //    95: iadd           
        //    96: istore          6
        //    98: iload           6
        //   100: iload           4
        //   102: if_icmplt       186
        //   105: lload           11
        //   107: ldc2_w          -9223372036854775808
        //   110: lcmp           
        //   111: ifne            493
        //   114: ldc2_w          -9223372036854775808
        //   117: lstore_0       
        //   118: aload           13
        //   120: ifnull          607
        //   123: aload_2        
        //   124: aload           13
        //   126: invokestatic    okhttp3/Cookie.domainMatch:(Lokhttp3/HttpUrl;Ljava/lang/String;)Z
        //   129: ifeq            616
        //   132: aload           14
        //   134: ifnonnull       618
        //   137: aload_2        
        //   138: invokevirtual   okhttp3/HttpUrl.encodedPath:()Ljava/lang/String;
        //   141: astore_2       
        //   142: aload_2        
        //   143: bipush          47
        //   145: invokevirtual   java/lang/String.lastIndexOf:(I)I
        //   148: istore          6
        //   150: iload           6
        //   152: ifne            634
        //   155: ldc             "/"
        //   157: astore_2       
        //   158: new             Lokhttp3/Cookie;
        //   161: dup            
        //   162: aload           7
        //   164: aload           8
        //   166: lload_0        
        //   167: aload           13
        //   169: aload_2        
        //   170: iload           15
        //   172: iload           16
        //   174: iload           17
        //   176: iload           18
        //   178: invokespecial   okhttp3/Cookie.<init>:(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;ZZZZ)V
        //   181: areturn        
        //   182: aconst_null    
        //   183: areturn        
        //   184: aconst_null    
        //   185: areturn        
        //   186: aload_3        
        //   187: iload           6
        //   189: iload           4
        //   191: bipush          59
        //   193: invokestatic    okhttp3/internal/Util.delimiterOffset:(Ljava/lang/String;IIC)I
        //   196: istore          5
        //   198: aload_3        
        //   199: iload           6
        //   201: iload           5
        //   203: bipush          61
        //   205: invokestatic    okhttp3/internal/Util.delimiterOffset:(Ljava/lang/String;IIC)I
        //   208: istore          19
        //   210: aload_3        
        //   211: iload           6
        //   213: iload           19
        //   215: invokestatic    okhttp3/internal/Util.trimSubstring:(Ljava/lang/String;II)Ljava/lang/String;
        //   218: astore          20
        //   220: iload           19
        //   222: iload           5
        //   224: if_icmplt       324
        //   227: ldc             ""
        //   229: astore          21
        //   231: aload           20
        //   233: ldc             "expires"
        //   235: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   238: ifne            339
        //   241: aload           20
        //   243: ldc             "max-age"
        //   245: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   248: ifne            374
        //   251: aload           20
        //   253: ldc             "domain"
        //   255: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   258: ifne            408
        //   261: aload           20
        //   263: ldc             "path"
        //   265: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   268: ifne            446
        //   271: aload           20
        //   273: ldc             "secure"
        //   275: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   278: ifne            457
        //   281: aload           20
        //   283: ldc             "httponly"
        //   285: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   288: ifne            475
        //   291: aload           14
        //   293: astore          21
        //   295: aload           13
        //   297: astore          14
        //   299: aload           21
        //   301: astore          13
        //   303: iload           5
        //   305: iconst_1       
        //   306: iadd           
        //   307: istore          6
        //   309: aload           14
        //   311: astore          21
        //   313: aload           13
        //   315: astore          14
        //   317: aload           21
        //   319: astore          13
        //   321: goto            98
        //   324: aload_3        
        //   325: iload           19
        //   327: iconst_1       
        //   328: iadd           
        //   329: iload           5
        //   331: invokestatic    okhttp3/internal/Util.trimSubstring:(Ljava/lang/String;II)Ljava/lang/String;
        //   334: astore          21
        //   336: goto            231
        //   339: aload           21
        //   341: iconst_0       
        //   342: aload           21
        //   344: invokevirtual   java/lang/String.length:()I
        //   347: invokestatic    okhttp3/Cookie.parseExpires:(Ljava/lang/String;II)J
        //   350: lstore          22
        //   352: lload           22
        //   354: lstore          9
        //   356: iconst_1       
        //   357: istore          18
        //   359: aload           14
        //   361: astore          21
        //   363: aload           13
        //   365: astore          14
        //   367: aload           21
        //   369: astore          13
        //   371: goto            303
        //   374: aload           21
        //   376: invokestatic    okhttp3/Cookie.parseMaxAge:(Ljava/lang/String;)J
        //   379: lstore          22
        //   381: lload           22
        //   383: lstore          11
        //   385: iconst_1       
        //   386: istore          18
        //   388: aload           14
        //   390: astore          21
        //   392: aload           13
        //   394: astore          14
        //   396: aload           21
        //   398: astore          13
        //   400: goto            303
        //   403: astore          21
        //   405: goto            388
        //   408: aload           21
        //   410: invokestatic    okhttp3/Cookie.parseDomain:(Ljava/lang/String;)Ljava/lang/String;
        //   413: astore          21
        //   415: iconst_0       
        //   416: istore          17
        //   418: aload           14
        //   420: astore          13
        //   422: aload           21
        //   424: astore          14
        //   426: goto            303
        //   429: astore          21
        //   431: aload           14
        //   433: astore          21
        //   435: aload           13
        //   437: astore          14
        //   439: aload           21
        //   441: astore          13
        //   443: goto            303
        //   446: aload           13
        //   448: astore          14
        //   450: aload           21
        //   452: astore          13
        //   454: goto            303
        //   457: iconst_1       
        //   458: istore          15
        //   460: aload           13
        //   462: astore          21
        //   464: aload           14
        //   466: astore          13
        //   468: aload           21
        //   470: astore          14
        //   472: goto            303
        //   475: iconst_1       
        //   476: istore          16
        //   478: aload           13
        //   480: astore          21
        //   482: aload           14
        //   484: astore          13
        //   486: aload           21
        //   488: astore          14
        //   490: goto            303
        //   493: lload           11
        //   495: ldc2_w          -1
        //   498: lcmp           
        //   499: ifeq            575
        //   502: lload           11
        //   504: ldc2_w          9223372036854775
        //   507: lcmp           
        //   508: ifle            581
        //   511: iconst_1       
        //   512: istore          6
        //   514: iload           6
        //   516: ifne            587
        //   519: lload           11
        //   521: ldc2_w          1000
        //   524: lmul           
        //   525: lstore          9
        //   527: lload           9
        //   529: lload_0        
        //   530: ladd           
        //   531: lstore          9
        //   533: lload           9
        //   535: lload_0        
        //   536: lcmp           
        //   537: ifge            595
        //   540: iconst_1       
        //   541: istore          6
        //   543: iload           6
        //   545: ifne            568
        //   548: lload           9
        //   550: ldc2_w          253402300799999
        //   553: lcmp           
        //   554: ifgt            601
        //   557: iconst_1       
        //   558: istore          6
        //   560: lload           9
        //   562: lstore_0       
        //   563: iload           6
        //   565: ifne            118
        //   568: ldc2_w          253402300799999
        //   571: lstore_0       
        //   572: goto            118
        //   575: lload           9
        //   577: lstore_0       
        //   578: goto            118
        //   581: iconst_0       
        //   582: istore          6
        //   584: goto            514
        //   587: ldc2_w          9223372036854775807
        //   590: lstore          9
        //   592: goto            527
        //   595: iconst_0       
        //   596: istore          6
        //   598: goto            543
        //   601: iconst_0       
        //   602: istore          6
        //   604: goto            560
        //   607: aload_2        
        //   608: invokevirtual   okhttp3/HttpUrl.host:()Ljava/lang/String;
        //   611: astore          13
        //   613: goto            132
        //   616: aconst_null    
        //   617: areturn        
        //   618: aload           14
        //   620: ldc             "/"
        //   622: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   625: ifeq            137
        //   628: aload           14
        //   630: astore_2       
        //   631: goto            158
        //   634: aload_2        
        //   635: iconst_0       
        //   636: iload           6
        //   638: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //   641: astore_2       
        //   642: goto            158
        //   645: astore          21
        //   647: goto            359
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                
        //  -----  -----  -----  -----  ------------------------------------
        //  339    352    645    650    Ljava/lang/IllegalArgumentException;
        //  374    381    403    408    Ljava/lang/NumberFormatException;
        //  408    415    429    446    Ljava/lang/IllegalArgumentException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokevirtual:boolean(String::startsWith, var_14_4E:String, ldc:String("/")))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Cookie parse(final HttpUrl httpUrl, final String s) {
        return parse(System.currentTimeMillis(), httpUrl, s);
    }
    
    public static List<Cookie> parseAll(final HttpUrl httpUrl, final Headers headers) {
        final List<? extends T> list = null;
        final List<String> values = headers.values("Set-Cookie");
        final int size = values.size();
        int i = 0;
        List<? extends T> list2 = list;
        while (i < size) {
            final Cookie parse = parse(httpUrl, values.get(i));
            if (parse != null) {
                if (list2 == null) {
                    list2 = (List<? extends T>)new ArrayList<Object>();
                }
                list2.add(parse);
            }
            ++i;
        }
        List<Object> list3;
        if (list2 == null) {
            list3 = Collections.emptyList();
        }
        else {
            list3 = Collections.unmodifiableList((List<?>)list2);
        }
        return (List<Cookie>)list3;
    }
    
    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            s = s.substring(1);
        }
        s = Util.domainToAscii(s);
        if (s != null) {
            return s;
        }
        throw new IllegalArgumentException();
    }
    
    private static long parseExpires(final String input, int int1, int n) {
        int i = dateCharacterOffset(input, int1, n, false);
        int value = -1;
        int value2 = -1;
        int value3 = -1;
        int value4 = -1;
        int n2 = -1;
        int1 = -1;
        final Matcher matcher = Cookie.TIME_PATTERN.matcher(input);
        while (i < n) {
            final int dateCharacterOffset = dateCharacterOffset(input, i + 1, n, true);
            matcher.region(i, dateCharacterOffset);
            int int2;
            int int3;
            int int4;
            int n3;
            int int5;
            if (value == -1 && matcher.usePattern(Cookie.TIME_PATTERN).matches()) {
                int2 = Integer.parseInt(matcher.group(1));
                int3 = Integer.parseInt(matcher.group(2));
                int4 = Integer.parseInt(matcher.group(3));
                n3 = n2;
                int5 = value4;
            }
            else if (value4 == -1 && matcher.usePattern(Cookie.DAY_OF_MONTH_PATTERN).matches()) {
                int5 = Integer.parseInt(matcher.group(1));
                n3 = n2;
                int4 = value3;
                int3 = value2;
                int2 = value;
            }
            else if (n2 == -1 && matcher.usePattern(Cookie.MONTH_PATTERN).matches()) {
                n3 = Cookie.MONTH_PATTERN.pattern().indexOf(matcher.group(1).toLowerCase(Locale.US)) / 4;
                int5 = value4;
                int4 = value3;
                int3 = value2;
                int2 = value;
            }
            else if (int1 != -1) {
                int2 = value;
                int3 = value2;
                int4 = value3;
                int5 = value4;
                n3 = n2;
            }
            else {
                n3 = n2;
                int5 = value4;
                int4 = value3;
                int3 = value2;
                int2 = value;
                if (matcher.usePattern(Cookie.YEAR_PATTERN).matches()) {
                    int1 = Integer.parseInt(matcher.group(1));
                    n3 = n2;
                    int5 = value4;
                    int4 = value3;
                    int3 = value2;
                    int2 = value;
                }
            }
            final int dateCharacterOffset2 = dateCharacterOffset(input, dateCharacterOffset + 1, n, false);
            n2 = n3;
            value4 = int5;
            value3 = int4;
            value2 = int3;
            value = int2;
            i = dateCharacterOffset2;
        }
        if (int1 < 70) {
            n = int1;
        }
        else if ((n = int1) <= 99) {
            n = int1 + 1900;
        }
        if (n < 0) {
            int1 = n;
        }
        else if ((int1 = n) <= 69) {
            int1 = n + 2000;
        }
        if (int1 < 1601) {
            throw new IllegalArgumentException();
        }
        if (n2 == -1) {
            throw new IllegalArgumentException();
        }
        if (value4 < 1 || value4 > 31) {
            throw new IllegalArgumentException();
        }
        if (value < 0 || value > 23) {
            throw new IllegalArgumentException();
        }
        if (value2 < 0 || value2 > 59) {
            throw new IllegalArgumentException();
        }
        if (value3 >= 0 && value3 <= 59) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar(Util.UTC);
            gregorianCalendar.setLenient(false);
            gregorianCalendar.set(1, int1);
            gregorianCalendar.set(2, n2 - 1);
            gregorianCalendar.set(5, value4);
            gregorianCalendar.set(11, value);
            gregorianCalendar.set(12, value2);
            gregorianCalendar.set(13, value3);
            gregorianCalendar.set(14, 0);
            return gregorianCalendar.getTimeInMillis();
        }
        throw new IllegalArgumentException();
    }
    
    private static long parseMaxAge(final String s) {
        long n = Long.MIN_VALUE;
        boolean b = false;
        try {
            final long long1 = Long.parseLong(s);
            if (long1 > 0L) {
                b = true;
            }
            if (b) {
                n = long1;
            }
            return n;
        }
        catch (final NumberFormatException ex) {
            if (!s.matches("-?\\d+")) {
                throw ex;
            }
            if (!s.startsWith("-")) {
                n = Long.MAX_VALUE;
            }
            return n;
        }
    }
    
    private static boolean pathMatch(final HttpUrl httpUrl, final String s) {
        final String encodedPath = httpUrl.encodedPath();
        if (!encodedPath.equals(s)) {
            if (encodedPath.startsWith(s)) {
                if (s.endsWith("/")) {
                    return true;
                }
                if (encodedPath.charAt(s.length()) == '/') {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public String domain() {
        return this.domain;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o instanceof Cookie) {
            final Cookie cookie = (Cookie)o;
            boolean b2;
            if (!cookie.name.equals(this.name)) {
                b2 = b;
            }
            else {
                b2 = b;
                if (cookie.value.equals(this.value)) {
                    b2 = b;
                    if (cookie.domain.equals(this.domain)) {
                        b2 = b;
                        if (cookie.path.equals(this.path)) {
                            b2 = b;
                            if (cookie.expiresAt == this.expiresAt) {
                                b2 = b;
                                if (cookie.secure == this.secure) {
                                    b2 = b;
                                    if (cookie.httpOnly == this.httpOnly) {
                                        b2 = b;
                                        if (cookie.persistent == this.persistent) {
                                            b2 = b;
                                            if (cookie.hostOnly == this.hostOnly) {
                                                b2 = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    public long expiresAt() {
        return this.expiresAt;
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        final int hashCode = this.name.hashCode();
        final int hashCode2 = this.value.hashCode();
        final int hashCode3 = this.domain.hashCode();
        final int hashCode4 = this.path.hashCode();
        final int n2 = (int)(this.expiresAt ^ this.expiresAt >>> 32);
        int n3;
        if (!this.secure) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        int n4;
        if (!this.httpOnly) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        int n5;
        if (!this.persistent) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (this.hostOnly) {
            n = 0;
        }
        return (n5 + (n4 + (n3 + (((((hashCode + 527) * 31 + hashCode2) * 31 + hashCode3) * 31 + hashCode4) * 31 + n2) * 31) * 31) * 31) * 31 + n;
    }
    
    public boolean hostOnly() {
        return this.hostOnly;
    }
    
    public boolean httpOnly() {
        return this.httpOnly;
    }
    
    public boolean matches(final HttpUrl httpUrl) {
        boolean b;
        if (!this.hostOnly) {
            b = domainMatch(httpUrl, this.domain);
        }
        else {
            b = httpUrl.host().equals(this.domain);
        }
        return b && pathMatch(httpUrl, this.path) && (!this.secure || httpUrl.isHttps());
    }
    
    public String name() {
        return this.name;
    }
    
    public String path() {
        return this.path;
    }
    
    public boolean persistent() {
        return this.persistent;
    }
    
    public boolean secure() {
        return this.secure;
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    String toString(final boolean b) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append('=');
        sb.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                sb.append("; max-age=0");
            }
            else {
                sb.append("; expires=").append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            sb.append("; domain=");
            if (b) {
                sb.append(".");
            }
            sb.append(this.domain);
        }
        sb.append("; path=").append(this.path);
        if (this.secure) {
            sb.append("; secure");
        }
        if (this.httpOnly) {
            sb.append("; httponly");
        }
        return sb.toString();
    }
    
    public String value() {
        return this.value;
    }
    
    public static final class Builder
    {
        String domain;
        long expiresAt;
        boolean hostOnly;
        boolean httpOnly;
        String name;
        String path;
        boolean persistent;
        boolean secure;
        String value;
        
        public Builder() {
            this.expiresAt = 253402300799999L;
            this.path = "/";
        }
        
        private Builder domain(final String str, final boolean hostOnly) {
            if (str == null) {
                throw new NullPointerException("domain == null");
            }
            final String domainToAscii = Util.domainToAscii(str);
            if (domainToAscii != null) {
                this.domain = domainToAscii;
                this.hostOnly = hostOnly;
                return this;
            }
            throw new IllegalArgumentException("unexpected domain: " + str);
        }
        
        public Cookie build() {
            return new Cookie(this);
        }
        
        public Builder domain(final String s) {
            return this.domain(s, false);
        }
        
        public Builder expiresAt(long expiresAt) {
            final long n = 253402300799999L;
            final int n2 = 0;
            int n3;
            if (expiresAt > 0L) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                expiresAt = Long.MIN_VALUE;
            }
            int n4 = n2;
            if (expiresAt <= 253402300799999L) {
                n4 = 1;
            }
            if (n4 == 0) {
                expiresAt = n;
            }
            this.expiresAt = expiresAt;
            this.persistent = true;
            return this;
        }
        
        public Builder hostOnlyDomain(final String s) {
            return this.domain(s, true);
        }
        
        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }
        
        public Builder name(final String s) {
            if (s == null) {
                throw new NullPointerException("name == null");
            }
            if (s.trim().equals(s)) {
                this.name = s;
                return this;
            }
            throw new IllegalArgumentException("name is not trimmed");
        }
        
        public Builder path(final String path) {
            if (path.startsWith("/")) {
                this.path = path;
                return this;
            }
            throw new IllegalArgumentException("path must start with '/'");
        }
        
        public Builder secure() {
            this.secure = true;
            return this;
        }
        
        public Builder value(final String s) {
            if (s == null) {
                throw new NullPointerException("value == null");
            }
            if (s.trim().equals(s)) {
                this.value = s;
                return this;
            }
            throw new IllegalArgumentException("value is not trimmed");
        }
    }
}
