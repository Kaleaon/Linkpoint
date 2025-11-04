// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.util.Xml;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Intent;
import org.xmlpull.v1.XmlPullParser;
import android.view.InflateException;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.content.Context;
import java.lang.reflect.Constructor;
import java.util.HashMap;

class PreferenceInflater
{
    private static final HashMap<String, Constructor> CONSTRUCTOR_MAP;
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE;
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private static final String TAG = "PreferenceInflater";
    private final Object[] mConstructorArgs;
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;
    
    static {
        CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class };
        CONSTRUCTOR_MAP = new HashMap<String, Constructor>();
    }
    
    public PreferenceInflater(final Context mContext, final PreferenceManager preferenceManager) {
        this.mConstructorArgs = new Object[2];
        this.mContext = mContext;
        this.init(preferenceManager);
    }
    
    private Preference createItem(@NonNull final String p0, @Nullable final String[] p1, final AttributeSet p2) throws ClassNotFoundException, InflateException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore          4
        //     3: getstatic       android/support/v7/preference/PreferenceInflater.CONSTRUCTOR_MAP:Ljava/util/HashMap;
        //     6: aload_1        
        //     7: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    10: checkcast       Ljava/lang/reflect/Constructor;
        //    13: astore          5
        //    15: aload           5
        //    17: ifnull          44
        //    20: aload           5
        //    22: astore_2       
        //    23: aload_0        
        //    24: getfield        android/support/v7/preference/PreferenceInflater.mConstructorArgs:[Ljava/lang/Object;
        //    27: astore          5
        //    29: aload           5
        //    31: iconst_1       
        //    32: aload_3        
        //    33: aastore        
        //    34: aload_2        
        //    35: aload           5
        //    37: invokevirtual   java/lang/reflect/Constructor.newInstance:([Ljava/lang/Object;)Ljava/lang/Object;
        //    40: checkcast       Landroid/support/v7/preference/Preference;
        //    43: areturn        
        //    44: aload_0        
        //    45: getfield        android/support/v7/preference/PreferenceInflater.mContext:Landroid/content/Context;
        //    48: invokevirtual   android/content/Context.getClassLoader:()Ljava/lang/ClassLoader;
        //    51: astore          6
        //    53: aload_2        
        //    54: ifnonnull       94
        //    57: aload           6
        //    59: aload_1        
        //    60: invokevirtual   java/lang/ClassLoader.loadClass:(Ljava/lang/String;)Ljava/lang/Class;
        //    63: astore          7
        //    65: aload           7
        //    67: getstatic       android/support/v7/preference/PreferenceInflater.CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;
        //    70: invokevirtual   java/lang/Class.getConstructor:([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
        //    73: astore_2       
        //    74: aload_2        
        //    75: iconst_1       
        //    76: invokevirtual   java/lang/reflect/Constructor.setAccessible:(Z)V
        //    79: getstatic       android/support/v7/preference/PreferenceInflater.CONSTRUCTOR_MAP:Ljava/util/HashMap;
        //    82: aload_1        
        //    83: aload_2        
        //    84: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    87: pop            
        //    88: goto            23
        //    91: astore_1       
        //    92: aload_1        
        //    93: athrow         
        //    94: aload_2        
        //    95: arraylength    
        //    96: ifeq            57
        //    99: aload_2        
        //   100: arraylength    
        //   101: istore          8
        //   103: aconst_null    
        //   104: astore          5
        //   106: iload           4
        //   108: iload           8
        //   110: if_icmplt       175
        //   113: aconst_null    
        //   114: astore_2       
        //   115: aload_2        
        //   116: astore          7
        //   118: aload_2        
        //   119: ifnonnull       65
        //   122: aload           5
        //   124: ifnull          226
        //   127: aload           5
        //   129: athrow         
        //   130: astore_2       
        //   131: new             Landroid/view/InflateException;
        //   134: dup            
        //   135: new             Ljava/lang/StringBuilder;
        //   138: dup            
        //   139: invokespecial   java/lang/StringBuilder.<init>:()V
        //   142: aload_3        
        //   143: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   148: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   151: ldc             ": Error inflating class "
        //   153: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   156: aload_1        
        //   157: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   160: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   163: invokespecial   android/view/InflateException.<init>:(Ljava/lang/String;)V
        //   166: astore_1       
        //   167: aload_1        
        //   168: aload_2        
        //   169: invokevirtual   android/view/InflateException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   172: pop            
        //   173: aload_1        
        //   174: athrow         
        //   175: aload_2        
        //   176: iload           4
        //   178: aaload         
        //   179: astore          7
        //   181: new             Ljava/lang/StringBuilder;
        //   184: astore          9
        //   186: aload           9
        //   188: invokespecial   java/lang/StringBuilder.<init>:()V
        //   191: aload           6
        //   193: aload           9
        //   195: aload           7
        //   197: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   200: aload_1        
        //   201: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   204: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   207: invokevirtual   java/lang/ClassLoader.loadClass:(Ljava/lang/String;)Ljava/lang/Class;
        //   210: astore          7
        //   212: aload           7
        //   214: astore_2       
        //   215: goto            115
        //   218: astore          5
        //   220: iinc            4, 1
        //   223: goto            106
        //   226: new             Landroid/view/InflateException;
        //   229: astore          5
        //   231: new             Ljava/lang/StringBuilder;
        //   234: astore_2       
        //   235: aload_2        
        //   236: invokespecial   java/lang/StringBuilder.<init>:()V
        //   239: aload           5
        //   241: aload_2        
        //   242: aload_3        
        //   243: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   248: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   251: ldc             ": Error inflating class "
        //   253: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   256: aload_1        
        //   257: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   260: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   263: invokespecial   android/view/InflateException.<init>:(Ljava/lang/String;)V
        //   266: aload           5
        //   268: athrow         
        //    Exceptions:
        //  throws java.lang.ClassNotFoundException
        //  throws android.view.InflateException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  23     29     91     94     Ljava/lang/ClassNotFoundException;
        //  23     29     130    175    Ljava/lang/Exception;
        //  34     44     91     94     Ljava/lang/ClassNotFoundException;
        //  34     44     130    175    Ljava/lang/Exception;
        //  44     53     91     94     Ljava/lang/ClassNotFoundException;
        //  44     53     130    175    Ljava/lang/Exception;
        //  57     65     91     94     Ljava/lang/ClassNotFoundException;
        //  57     65     130    175    Ljava/lang/Exception;
        //  65     88     91     94     Ljava/lang/ClassNotFoundException;
        //  65     88     130    175    Ljava/lang/Exception;
        //  94     103    91     94     Ljava/lang/ClassNotFoundException;
        //  94     103    130    175    Ljava/lang/Exception;
        //  127    130    91     94     Ljava/lang/ClassNotFoundException;
        //  127    130    130    175    Ljava/lang/Exception;
        //  181    212    218    226    Ljava/lang/ClassNotFoundException;
        //  181    212    91     94     Ljava/lang/ClassNotFoundException;
        //  181    212    130    175    Ljava/lang/Exception;
        //  226    269    91     94     Ljava/lang/ClassNotFoundException;
        //  226    269    130    175    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
    
    private Preference createItemFromTag(String o, final AttributeSet set) {
        try {
            if (-1 != ((String)o).indexOf(46)) {
                o = this.createItem((String)o, null, set);
            }
            else {
                o = this.onCreateItem((String)o, set);
            }
            return (Preference)o;
        }
        catch (final InflateException ex) {
            throw ex;
        }
        catch (final ClassNotFoundException ex2) {
            final InflateException ex3 = new InflateException(set.getPositionDescription() + ": Error inflating class (not found)" + (String)o);
            ex3.initCause((Throwable)ex2);
            throw ex3;
        }
        catch (final Exception ex4) {
            final InflateException ex5 = new InflateException(set.getPositionDescription() + ": Error inflating class " + (String)o);
            ex5.initCause((Throwable)ex4);
            throw ex5;
        }
    }
    
    private void init(final PreferenceManager mPreferenceManager) {
        this.mPreferenceManager = mPreferenceManager;
        this.setDefaultPackages(new String[] { "android.support.v14.preference.", "android.support.v7.preference." });
    }
    
    @NonNull
    private PreferenceGroup onMergeRoots(final PreferenceGroup preferenceGroup, @NonNull final PreferenceGroup preferenceGroup2) {
        if (preferenceGroup != null) {
            return preferenceGroup;
        }
        preferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
        return preferenceGroup2;
    }
    
    private void rInflate(final XmlPullParser xmlPullParser, final Preference preference, final AttributeSet set) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        while (true) {
            final int next = xmlPullParser.next();
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                break;
            }
            if (next == 1) {
                break;
            }
            if (next != 2) {
                continue;
            }
            final String name = xmlPullParser.getName();
            if (!"intent".equals(name)) {
                if (!"extra".equals(name)) {
                    final Preference itemFromTag = this.createItemFromTag(name, set);
                    ((PreferenceGroup)preference).addItemFromInflater(itemFromTag);
                    this.rInflate(xmlPullParser, itemFromTag, set);
                    continue;
                }
            }
            else {
                try {
                    preference.setIntent(Intent.parseIntent(this.getContext().getResources(), xmlPullParser, set));
                    continue;
                }
                catch (final IOException ex) {
                    final XmlPullParserException ex2 = new XmlPullParserException("Error parsing preference");
                    ex2.initCause((Throwable)ex);
                    throw ex2;
                }
            }
            this.getContext().getResources().parseBundleExtra("extra", set, preference.getExtras());
            try {
                skipCurrentTag(xmlPullParser);
            }
            catch (final IOException ex3) {
                final XmlPullParserException ex4 = new XmlPullParserException("Error parsing preference");
                ex4.initCause((Throwable)ex3);
                throw ex4;
            }
        }
    }
    
    private static void skipCurrentTag(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        while (true) {
            final int next = xmlPullParser.next();
            if (next == 1) {
                break;
            }
            if (next != 3) {
                continue;
            }
            if (xmlPullParser.getDepth() > depth) {
                continue;
            }
            break;
        }
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public String[] getDefaultPackages() {
        return this.mDefaultPackages;
    }
    
    public Preference inflate(final int n, @Nullable final PreferenceGroup preferenceGroup) {
        final XmlResourceParser xml = this.getContext().getResources().getXml(n);
        try {
            return this.inflate((XmlPullParser)xml, preferenceGroup);
        }
        finally {
            xml.close();
        }
    }
    
    public Preference inflate(final XmlPullParser xmlPullParser, @Nullable PreferenceGroup onMergeRoots) {
        monitorenter(this.mConstructorArgs);
        try {
            final AttributeSet attributeSet = Xml.asAttributeSet(xmlPullParser);
            this.mConstructorArgs[0] = this.mContext;
            try {
                int next;
                do {
                    next = xmlPullParser.next();
                } while (next != 2 && next != 1);
                if (next == 2) {
                    onMergeRoots = this.onMergeRoots(onMergeRoots, (PreferenceGroup)this.createItemFromTag(xmlPullParser.getName(), attributeSet));
                    this.rInflate(xmlPullParser, onMergeRoots, attributeSet);
                    return onMergeRoots;
                }
                throw new InflateException(xmlPullParser.getPositionDescription() + ": No start tag found!");
            }
            catch (final InflateException ex) {
                throw ex;
            }
            catch (final XmlPullParserException ex2) {
                final InflateException ex3 = new InflateException(ex2.getMessage());
                ex3.initCause((Throwable)ex2);
                throw ex3;
            }
            catch (final IOException ex4) {
                final InflateException ex5 = new InflateException(xmlPullParser.getPositionDescription() + ": " + ex4.getMessage());
                ex5.initCause((Throwable)ex4);
                throw ex5;
            }
        }
        finally {}
    }
    
    protected Preference onCreateItem(final String s, final AttributeSet set) throws ClassNotFoundException {
        return this.createItem(s, this.mDefaultPackages, set);
    }
    
    public void setDefaultPackages(final String[] mDefaultPackages) {
        this.mDefaultPackages = mDefaultPackages;
    }
}
