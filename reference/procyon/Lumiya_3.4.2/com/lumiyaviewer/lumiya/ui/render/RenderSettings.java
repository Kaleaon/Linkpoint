// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.SharedPreferences;

public class RenderSettings
{
    public final int avatarCountLimit;
    public final int drawDistance;
    
    public RenderSettings(final SharedPreferences p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.<init>:()V
        //     4: aload_1        
        //     5: ldc             "drawDistance"
        //     7: ldc             "20"
        //     9: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    14: astore_2       
        //    15: bipush          20
        //    17: istore_3       
        //    18: aload_2        
        //    19: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    22: istore          4
        //    24: aload_1        
        //    25: ldc             "avatarCountLimit"
        //    27: ldc             "5"
        //    29: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    34: astore_1       
        //    35: iconst_5       
        //    36: istore          5
        //    38: aload_1        
        //    39: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    42: istore_3       
        //    43: aload_0        
        //    44: iload           4
        //    46: putfield        com/lumiyaviewer/lumiya/ui/render/RenderSettings.drawDistance:I
        //    49: aload_0        
        //    50: iload_3        
        //    51: putfield        com/lumiyaviewer/lumiya/ui/render/RenderSettings.avatarCountLimit:I
        //    54: return         
        //    55: astore_2       
        //    56: iload_3        
        //    57: istore          4
        //    59: goto            24
        //    62: astore_1       
        //    63: iload           5
        //    65: istore_3       
        //    66: goto            43
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  18     24     55     62     Ljava/lang/Exception;
        //  38     43     62     69     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0043:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
}
