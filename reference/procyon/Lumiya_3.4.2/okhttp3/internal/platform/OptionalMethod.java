// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class OptionalMethod<T>
{
    private final String methodName;
    private final Class[] methodParams;
    private final Class<?> returnType;
    
    public OptionalMethod(final Class<?> returnType, final String methodName, final Class... methodParams) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.methodParams = methodParams;
    }
    
    private Method getMethod(final Class<?> clazz) {
        final Method method = null;
        Method method2;
        if (this.methodName == null) {
            method2 = method;
        }
        else {
            final Method publicMethod = getPublicMethod(clazz, this.methodName, this.methodParams);
            if (publicMethod != null && this.returnType != null) {
                method2 = method;
                if (this.returnType.isAssignableFrom(publicMethod.getReturnType())) {
                    method2 = publicMethod;
                }
            }
            else {
                method2 = publicMethod;
            }
        }
        return method2;
    }
    
    private static Method getPublicMethod(final Class<?> p0, final String p1, final Class[] p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1        
        //     2: aload_2        
        //     3: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //     6: astore_0       
        //     7: aload_0        
        //     8: invokevirtual   java/lang/reflect/Method.getModifiers:()I
        //    11: istore_3       
        //    12: iload_3        
        //    13: iconst_1       
        //    14: iand           
        //    15: ifeq            20
        //    18: aload_0        
        //    19: areturn        
        //    20: aconst_null    
        //    21: astore_0       
        //    22: goto            18
        //    25: astore_0       
        //    26: aconst_null    
        //    27: astore_0       
        //    28: goto            18
        //    31: astore_1       
        //    32: goto            18
        //    Signature:
        //  (Ljava/lang/Class<*>;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  0      7      25     31     Ljava/lang/NoSuchMethodException;
        //  7      12     31     35     Ljava/lang/NoSuchMethodException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0018:
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
    
    public Object invoke(final T t, final Object... args) throws InvocationTargetException {
        final Method method = this.getMethod(t.getClass());
        Label_0022: {
            if (method == null) {
                break Label_0022;
            }
            try {
                return method.invoke(t, args);
                throw new AssertionError((Object)("Method " + this.methodName + " not supported for object " + t));
            }
            catch (final IllegalAccessException cause) {
                final AssertionError assertionError = new AssertionError((Object)("Unexpectedly could not call: " + method));
                assertionError.initCause(cause);
                throw assertionError;
            }
        }
    }
    
    public Object invokeOptional(final T obj, final Object... args) throws InvocationTargetException {
        final Method method = this.getMethod(obj.getClass());
        Label_0022: {
            if (method == null) {
                break Label_0022;
            }
            try {
                return method.invoke(obj, args);
                return null;
            }
            catch (final IllegalAccessException ex) {
                return null;
            }
        }
    }
    
    public Object invokeOptionalWithoutCheckedException(final T t, final Object... array) {
        try {
            return this.invokeOptional(t, array);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (!(targetException instanceof RuntimeException)) {
                final AssertionError assertionError = new AssertionError((Object)"Unexpected exception");
                assertionError.initCause(targetException);
                throw assertionError;
            }
            throw (RuntimeException)targetException;
        }
    }
    
    public Object invokeWithoutCheckedException(final T t, final Object... array) {
        try {
            return this.invoke(t, array);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (!(targetException instanceof RuntimeException)) {
                final AssertionError assertionError = new AssertionError((Object)"Unexpected exception");
                assertionError.initCause(targetException);
                throw assertionError;
            }
            throw (RuntimeException)targetException;
        }
    }
    
    public boolean isSupported(final T t) {
        return this.getMethod(t.getClass()) != null;
    }
}
