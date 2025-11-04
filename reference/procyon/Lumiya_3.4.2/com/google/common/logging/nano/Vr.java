// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.logging.nano;

import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface Vr
{
    public static final class VREvent extends ExtendableMessageNano<VREvent> implements Cloneable
    {
        private static volatile VREvent[] _emptyArray;
        public Application application;
        public AudioStats audioStats;
        public String cohort;
        public Cyclops cyclops;
        public Long durationMs;
        public EarthVr earthVr;
        public EmbedVrWidget embedVrWidget;
        public HeadMount headMount;
        public Application[] installedVrApplications;
        public Keyboard keyboard;
        public Launcher launcher;
        public Integer lifetimeCountBucket;
        public Lullaby lullaby;
        public PerformanceStats performanceStats;
        public Photos photos;
        public QrCodeScan qrCodeScan;
        public Renderer renderer;
        public SdkConfigurationParams sdkConfiguration;
        public SensorStats sensorStats;
        public StreetView streetView;
        public VrCore vrCore;
        public VrHome vrHome;
        
        public VREvent() {
            this.clear();
        }
        
        public static VREvent[] emptyArray() {
            if (VREvent._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (VREvent._emptyArray != null) {
                                break;
                            }
                        }
                        VREvent._emptyArray = new VREvent[0];
                        continue;
                    }
                }
            }
            return VREvent._emptyArray;
        }
        
        public static VREvent parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new VREvent().mergeFrom(codedInputByteBufferNano);
        }
        
        public static VREvent parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new VREvent(), array);
        }
        
        public final VREvent clear() {
            this.headMount = null;
            this.application = null;
            this.durationMs = null;
            this.installedVrApplications = Application.emptyArray();
            this.cyclops = null;
            this.qrCodeScan = null;
            this.cohort = null;
            this.performanceStats = null;
            this.sensorStats = null;
            this.audioStats = null;
            this.embedVrWidget = null;
            this.vrCore = null;
            this.earthVr = null;
            this.launcher = null;
            this.keyboard = null;
            this.renderer = null;
            this.lullaby = null;
            this.streetView = null;
            this.photos = null;
            this.vrHome = null;
            this.sdkConfiguration = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }
        
        @Override
        public final VREvent clone() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore_1       
            //     2: aload_0        
            //     3: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
            //     6: checkcast       Lcom/google/common/logging/nano/Vr$VREvent;
            //     9: astore_2       
            //    10: aload_0        
            //    11: getfield        com/google/common/logging/nano/Vr$VREvent.headMount:Lcom/google/common/logging/nano/Vr$VREvent$HeadMount;
            //    14: ifnonnull       155
            //    17: aload_0        
            //    18: getfield        com/google/common/logging/nano/Vr$VREvent.application:Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //    21: ifnonnull       169
            //    24: aload_0        
            //    25: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //    28: ifnonnull       183
            //    31: aload_0        
            //    32: getfield        com/google/common/logging/nano/Vr$VREvent.cyclops:Lcom/google/common/logging/nano/Vr$VREvent$Cyclops;
            //    35: ifnonnull       245
            //    38: aload_0        
            //    39: getfield        com/google/common/logging/nano/Vr$VREvent.qrCodeScan:Lcom/google/common/logging/nano/Vr$VREvent$QrCodeScan;
            //    42: ifnonnull       259
            //    45: aload_0        
            //    46: getfield        com/google/common/logging/nano/Vr$VREvent.performanceStats:Lcom/google/common/logging/nano/Vr$VREvent$PerformanceStats;
            //    49: ifnonnull       273
            //    52: aload_0        
            //    53: getfield        com/google/common/logging/nano/Vr$VREvent.sensorStats:Lcom/google/common/logging/nano/Vr$VREvent$SensorStats;
            //    56: ifnonnull       287
            //    59: aload_0        
            //    60: getfield        com/google/common/logging/nano/Vr$VREvent.audioStats:Lcom/google/common/logging/nano/Vr$VREvent$AudioStats;
            //    63: ifnonnull       301
            //    66: aload_0        
            //    67: getfield        com/google/common/logging/nano/Vr$VREvent.embedVrWidget:Lcom/google/common/logging/nano/Vr$VREvent$EmbedVrWidget;
            //    70: ifnonnull       315
            //    73: aload_0        
            //    74: getfield        com/google/common/logging/nano/Vr$VREvent.vrCore:Lcom/google/common/logging/nano/Vr$VREvent$VrCore;
            //    77: ifnonnull       329
            //    80: aload_0        
            //    81: getfield        com/google/common/logging/nano/Vr$VREvent.earthVr:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr;
            //    84: ifnonnull       343
            //    87: aload_0        
            //    88: getfield        com/google/common/logging/nano/Vr$VREvent.launcher:Lcom/google/common/logging/nano/Vr$VREvent$Launcher;
            //    91: ifnonnull       357
            //    94: aload_0        
            //    95: getfield        com/google/common/logging/nano/Vr$VREvent.keyboard:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard;
            //    98: ifnonnull       371
            //   101: aload_0        
            //   102: getfield        com/google/common/logging/nano/Vr$VREvent.renderer:Lcom/google/common/logging/nano/Vr$VREvent$Renderer;
            //   105: ifnonnull       385
            //   108: aload_0        
            //   109: getfield        com/google/common/logging/nano/Vr$VREvent.lullaby:Lcom/google/common/logging/nano/Vr$VREvent$Lullaby;
            //   112: ifnonnull       399
            //   115: aload_0        
            //   116: getfield        com/google/common/logging/nano/Vr$VREvent.streetView:Lcom/google/common/logging/nano/Vr$VREvent$StreetView;
            //   119: ifnonnull       413
            //   122: aload_0        
            //   123: getfield        com/google/common/logging/nano/Vr$VREvent.photos:Lcom/google/common/logging/nano/Vr$VREvent$Photos;
            //   126: ifnonnull       427
            //   129: aload_0        
            //   130: getfield        com/google/common/logging/nano/Vr$VREvent.vrHome:Lcom/google/common/logging/nano/Vr$VREvent$VrHome;
            //   133: ifnonnull       441
            //   136: aload_0        
            //   137: getfield        com/google/common/logging/nano/Vr$VREvent.sdkConfiguration:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
            //   140: ifnonnull       455
            //   143: aload_2        
            //   144: areturn        
            //   145: astore_2       
            //   146: new             Ljava/lang/AssertionError;
            //   149: dup            
            //   150: aload_2        
            //   151: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
            //   154: athrow         
            //   155: aload_2        
            //   156: aload_0        
            //   157: getfield        com/google/common/logging/nano/Vr$VREvent.headMount:Lcom/google/common/logging/nano/Vr$VREvent$HeadMount;
            //   160: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HeadMount.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HeadMount;
            //   163: putfield        com/google/common/logging/nano/Vr$VREvent.headMount:Lcom/google/common/logging/nano/Vr$VREvent$HeadMount;
            //   166: goto            17
            //   169: aload_2        
            //   170: aload_0        
            //   171: getfield        com/google/common/logging/nano/Vr$VREvent.application:Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   174: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Application.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   177: putfield        com/google/common/logging/nano/Vr$VREvent.application:Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   180: goto            24
            //   183: aload_0        
            //   184: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   187: arraylength    
            //   188: ifle            31
            //   191: aload_2        
            //   192: aload_0        
            //   193: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   196: arraylength    
            //   197: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   200: putfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   203: iload_1        
            //   204: aload_0        
            //   205: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   208: arraylength    
            //   209: if_icmpge       31
            //   212: aload_0        
            //   213: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   216: iload_1        
            //   217: aaload         
            //   218: ifnonnull       227
            //   221: iinc            1, 1
            //   224: goto            203
            //   227: aload_2        
            //   228: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   231: iload_1        
            //   232: aload_0        
            //   233: getfield        com/google/common/logging/nano/Vr$VREvent.installedVrApplications:[Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   236: iload_1        
            //   237: aaload         
            //   238: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Application.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Application;
            //   241: aastore        
            //   242: goto            221
            //   245: aload_2        
            //   246: aload_0        
            //   247: getfield        com/google/common/logging/nano/Vr$VREvent.cyclops:Lcom/google/common/logging/nano/Vr$VREvent$Cyclops;
            //   250: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Cyclops.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Cyclops;
            //   253: putfield        com/google/common/logging/nano/Vr$VREvent.cyclops:Lcom/google/common/logging/nano/Vr$VREvent$Cyclops;
            //   256: goto            38
            //   259: aload_2        
            //   260: aload_0        
            //   261: getfield        com/google/common/logging/nano/Vr$VREvent.qrCodeScan:Lcom/google/common/logging/nano/Vr$VREvent$QrCodeScan;
            //   264: invokevirtual   com/google/common/logging/nano/Vr$VREvent$QrCodeScan.clone:()Lcom/google/common/logging/nano/Vr$VREvent$QrCodeScan;
            //   267: putfield        com/google/common/logging/nano/Vr$VREvent.qrCodeScan:Lcom/google/common/logging/nano/Vr$VREvent$QrCodeScan;
            //   270: goto            45
            //   273: aload_2        
            //   274: aload_0        
            //   275: getfield        com/google/common/logging/nano/Vr$VREvent.performanceStats:Lcom/google/common/logging/nano/Vr$VREvent$PerformanceStats;
            //   278: invokevirtual   com/google/common/logging/nano/Vr$VREvent$PerformanceStats.clone:()Lcom/google/common/logging/nano/Vr$VREvent$PerformanceStats;
            //   281: putfield        com/google/common/logging/nano/Vr$VREvent.performanceStats:Lcom/google/common/logging/nano/Vr$VREvent$PerformanceStats;
            //   284: goto            52
            //   287: aload_2        
            //   288: aload_0        
            //   289: getfield        com/google/common/logging/nano/Vr$VREvent.sensorStats:Lcom/google/common/logging/nano/Vr$VREvent$SensorStats;
            //   292: invokevirtual   com/google/common/logging/nano/Vr$VREvent$SensorStats.clone:()Lcom/google/common/logging/nano/Vr$VREvent$SensorStats;
            //   295: putfield        com/google/common/logging/nano/Vr$VREvent.sensorStats:Lcom/google/common/logging/nano/Vr$VREvent$SensorStats;
            //   298: goto            59
            //   301: aload_2        
            //   302: aload_0        
            //   303: getfield        com/google/common/logging/nano/Vr$VREvent.audioStats:Lcom/google/common/logging/nano/Vr$VREvent$AudioStats;
            //   306: invokevirtual   com/google/common/logging/nano/Vr$VREvent$AudioStats.clone:()Lcom/google/common/logging/nano/Vr$VREvent$AudioStats;
            //   309: putfield        com/google/common/logging/nano/Vr$VREvent.audioStats:Lcom/google/common/logging/nano/Vr$VREvent$AudioStats;
            //   312: goto            66
            //   315: aload_2        
            //   316: aload_0        
            //   317: getfield        com/google/common/logging/nano/Vr$VREvent.embedVrWidget:Lcom/google/common/logging/nano/Vr$VREvent$EmbedVrWidget;
            //   320: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EmbedVrWidget.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EmbedVrWidget;
            //   323: putfield        com/google/common/logging/nano/Vr$VREvent.embedVrWidget:Lcom/google/common/logging/nano/Vr$VREvent$EmbedVrWidget;
            //   326: goto            73
            //   329: aload_2        
            //   330: aload_0        
            //   331: getfield        com/google/common/logging/nano/Vr$VREvent.vrCore:Lcom/google/common/logging/nano/Vr$VREvent$VrCore;
            //   334: invokevirtual   com/google/common/logging/nano/Vr$VREvent$VrCore.clone:()Lcom/google/common/logging/nano/Vr$VREvent$VrCore;
            //   337: putfield        com/google/common/logging/nano/Vr$VREvent.vrCore:Lcom/google/common/logging/nano/Vr$VREvent$VrCore;
            //   340: goto            80
            //   343: aload_2        
            //   344: aload_0        
            //   345: getfield        com/google/common/logging/nano/Vr$VREvent.earthVr:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr;
            //   348: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr;
            //   351: putfield        com/google/common/logging/nano/Vr$VREvent.earthVr:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr;
            //   354: goto            87
            //   357: aload_2        
            //   358: aload_0        
            //   359: getfield        com/google/common/logging/nano/Vr$VREvent.launcher:Lcom/google/common/logging/nano/Vr$VREvent$Launcher;
            //   362: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Launcher.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Launcher;
            //   365: putfield        com/google/common/logging/nano/Vr$VREvent.launcher:Lcom/google/common/logging/nano/Vr$VREvent$Launcher;
            //   368: goto            94
            //   371: aload_2        
            //   372: aload_0        
            //   373: getfield        com/google/common/logging/nano/Vr$VREvent.keyboard:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard;
            //   376: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Keyboard.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Keyboard;
            //   379: putfield        com/google/common/logging/nano/Vr$VREvent.keyboard:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard;
            //   382: goto            101
            //   385: aload_2        
            //   386: aload_0        
            //   387: getfield        com/google/common/logging/nano/Vr$VREvent.renderer:Lcom/google/common/logging/nano/Vr$VREvent$Renderer;
            //   390: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Renderer.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Renderer;
            //   393: putfield        com/google/common/logging/nano/Vr$VREvent.renderer:Lcom/google/common/logging/nano/Vr$VREvent$Renderer;
            //   396: goto            108
            //   399: aload_2        
            //   400: aload_0        
            //   401: getfield        com/google/common/logging/nano/Vr$VREvent.lullaby:Lcom/google/common/logging/nano/Vr$VREvent$Lullaby;
            //   404: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Lullaby.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Lullaby;
            //   407: putfield        com/google/common/logging/nano/Vr$VREvent.lullaby:Lcom/google/common/logging/nano/Vr$VREvent$Lullaby;
            //   410: goto            115
            //   413: aload_2        
            //   414: aload_0        
            //   415: getfield        com/google/common/logging/nano/Vr$VREvent.streetView:Lcom/google/common/logging/nano/Vr$VREvent$StreetView;
            //   418: invokevirtual   com/google/common/logging/nano/Vr$VREvent$StreetView.clone:()Lcom/google/common/logging/nano/Vr$VREvent$StreetView;
            //   421: putfield        com/google/common/logging/nano/Vr$VREvent.streetView:Lcom/google/common/logging/nano/Vr$VREvent$StreetView;
            //   424: goto            122
            //   427: aload_2        
            //   428: aload_0        
            //   429: getfield        com/google/common/logging/nano/Vr$VREvent.photos:Lcom/google/common/logging/nano/Vr$VREvent$Photos;
            //   432: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Photos.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Photos;
            //   435: putfield        com/google/common/logging/nano/Vr$VREvent.photos:Lcom/google/common/logging/nano/Vr$VREvent$Photos;
            //   438: goto            129
            //   441: aload_2        
            //   442: aload_0        
            //   443: getfield        com/google/common/logging/nano/Vr$VREvent.vrHome:Lcom/google/common/logging/nano/Vr$VREvent$VrHome;
            //   446: invokevirtual   com/google/common/logging/nano/Vr$VREvent$VrHome.clone:()Lcom/google/common/logging/nano/Vr$VREvent$VrHome;
            //   449: putfield        com/google/common/logging/nano/Vr$VREvent.vrHome:Lcom/google/common/logging/nano/Vr$VREvent$VrHome;
            //   452: goto            136
            //   455: aload_2        
            //   456: aload_0        
            //   457: getfield        com/google/common/logging/nano/Vr$VREvent.sdkConfiguration:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
            //   460: invokevirtual   com/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams.clone:()Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
            //   463: putfield        com/google/common/logging/nano/Vr$VREvent.sdkConfiguration:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
            //   466: goto            143
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                  
            //  -----  -----  -----  -----  --------------------------------------
            //  2      10     145    155    Ljava/lang/CloneNotSupportedException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:Application[](VREvent::installedVrApplications, this:VREvent)), ldc:int(0))
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.headMount != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.headMount);
            }
            if (this.application != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.application);
            }
            if (this.durationMs != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.durationMs);
            }
            int n;
            if (this.installedVrApplications == null) {
                n = computeSerializedSize;
            }
            else {
                n = computeSerializedSize;
                if (this.installedVrApplications.length > 0) {
                    for (int i = 0; i < this.installedVrApplications.length; ++i) {
                        final Application application = this.installedVrApplications[i];
                        if (application != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, application);
                        }
                    }
                    n = computeSerializedSize;
                }
            }
            if (this.cyclops != null) {
                n += CodedOutputByteBufferNano.computeMessageSize(5, this.cyclops);
            }
            if (this.qrCodeScan != null) {
                n += CodedOutputByteBufferNano.computeMessageSize(6, this.qrCodeScan);
            }
            if (this.cohort != null) {
                n += CodedOutputByteBufferNano.computeStringSize(7, this.cohort);
            }
            int n2;
            if (this.lifetimeCountBucket == null) {
                n2 = n;
            }
            else {
                n2 = n + CodedOutputByteBufferNano.computeInt32Size(8, this.lifetimeCountBucket);
            }
            if (this.performanceStats != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(9, this.performanceStats);
            }
            if (this.sensorStats != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(10, this.sensorStats);
            }
            if (this.audioStats != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(11, this.audioStats);
            }
            if (this.embedVrWidget != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(12, this.embedVrWidget);
            }
            if (this.vrCore != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(13, this.vrCore);
            }
            if (this.earthVr != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(14, this.earthVr);
            }
            if (this.launcher != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(15, this.launcher);
            }
            if (this.keyboard != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(16, this.keyboard);
            }
            if (this.renderer != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(17, this.renderer);
            }
            if (this.lullaby != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(18, this.lullaby);
            }
            if (this.streetView != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(19, this.streetView);
            }
            if (this.photos != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(20, this.photos);
            }
            if (this.vrHome != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(21, this.vrHome);
            }
            if (this.sdkConfiguration != null) {
                n2 += CodedOutputByteBufferNano.computeMessageSize(22, this.sdkConfiguration);
            }
            return n2;
        }
        
        @Override
        public final VREvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 10: {
                        if (this.headMount == null) {
                            this.headMount = new HeadMount();
                        }
                        codedInputByteBufferNano.readMessage(this.headMount);
                        continue;
                    }
                    case 18: {
                        if (this.application == null) {
                            this.application = new Application();
                        }
                        codedInputByteBufferNano.readMessage(this.application);
                        continue;
                    }
                    case 24: {
                        this.durationMs = codedInputByteBufferNano.readInt64();
                        continue;
                    }
                    case 34: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                        int i;
                        if (this.installedVrApplications != null) {
                            i = this.installedVrApplications.length;
                        }
                        else {
                            i = 0;
                        }
                        final Application[] installedVrApplications = new Application[repeatedFieldArrayLength + i];
                        if (i != 0) {
                            System.arraycopy(this.installedVrApplications, 0, installedVrApplications, 0, i);
                        }
                        while (i < installedVrApplications.length - 1) {
                            codedInputByteBufferNano.readMessage(installedVrApplications[i] = new Application());
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        codedInputByteBufferNano.readMessage(installedVrApplications[i] = new Application());
                        this.installedVrApplications = installedVrApplications;
                        continue;
                    }
                    case 42: {
                        if (this.cyclops == null) {
                            this.cyclops = new Cyclops();
                        }
                        codedInputByteBufferNano.readMessage(this.cyclops);
                        continue;
                    }
                    case 50: {
                        if (this.qrCodeScan == null) {
                            this.qrCodeScan = new QrCodeScan();
                        }
                        codedInputByteBufferNano.readMessage(this.qrCodeScan);
                        continue;
                    }
                    case 58: {
                        this.cohort = codedInputByteBufferNano.readString();
                        continue;
                    }
                    case 64: {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 11:
                            case 21: {
                                this.lifetimeCountBucket = int32;
                            }
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20: {
                                continue;
                            }
                            default: {
                                continue;
                            }
                        }
                        break;
                    }
                    case 74: {
                        if (this.performanceStats == null) {
                            this.performanceStats = new PerformanceStats();
                        }
                        codedInputByteBufferNano.readMessage(this.performanceStats);
                        continue;
                    }
                    case 82: {
                        if (this.sensorStats == null) {
                            this.sensorStats = new SensorStats();
                        }
                        codedInputByteBufferNano.readMessage(this.sensorStats);
                        continue;
                    }
                    case 90: {
                        if (this.audioStats == null) {
                            this.audioStats = new AudioStats();
                        }
                        codedInputByteBufferNano.readMessage(this.audioStats);
                        continue;
                    }
                    case 98: {
                        if (this.embedVrWidget == null) {
                            this.embedVrWidget = new EmbedVrWidget();
                        }
                        codedInputByteBufferNano.readMessage(this.embedVrWidget);
                        continue;
                    }
                    case 106: {
                        if (this.vrCore == null) {
                            this.vrCore = new VrCore();
                        }
                        codedInputByteBufferNano.readMessage(this.vrCore);
                        continue;
                    }
                    case 114: {
                        if (this.earthVr == null) {
                            this.earthVr = new EarthVr();
                        }
                        codedInputByteBufferNano.readMessage(this.earthVr);
                        continue;
                    }
                    case 122: {
                        if (this.launcher == null) {
                            this.launcher = new Launcher();
                        }
                        codedInputByteBufferNano.readMessage(this.launcher);
                        continue;
                    }
                    case 130: {
                        if (this.keyboard == null) {
                            this.keyboard = new Keyboard();
                        }
                        codedInputByteBufferNano.readMessage(this.keyboard);
                        continue;
                    }
                    case 138: {
                        if (this.renderer == null) {
                            this.renderer = new Renderer();
                        }
                        codedInputByteBufferNano.readMessage(this.renderer);
                        continue;
                    }
                    case 146: {
                        if (this.lullaby == null) {
                            this.lullaby = new Lullaby();
                        }
                        codedInputByteBufferNano.readMessage(this.lullaby);
                        continue;
                    }
                    case 154: {
                        if (this.streetView == null) {
                            this.streetView = new StreetView();
                        }
                        codedInputByteBufferNano.readMessage(this.streetView);
                        continue;
                    }
                    case 162: {
                        if (this.photos == null) {
                            this.photos = new Photos();
                        }
                        codedInputByteBufferNano.readMessage(this.photos);
                        continue;
                    }
                    case 170: {
                        if (this.vrHome == null) {
                            this.vrHome = new VrHome();
                        }
                        codedInputByteBufferNano.readMessage(this.vrHome);
                        continue;
                    }
                    case 178: {
                        if (this.sdkConfiguration == null) {
                            this.sdkConfiguration = new SdkConfigurationParams();
                        }
                        codedInputByteBufferNano.readMessage(this.sdkConfiguration);
                        continue;
                    }
                }
            }
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = 0;
            if (this.headMount != null) {
                codedOutputByteBufferNano.writeMessage(1, this.headMount);
            }
            if (this.application != null) {
                codedOutputByteBufferNano.writeMessage(2, this.application);
            }
            if (this.durationMs != null) {
                codedOutputByteBufferNano.writeInt64(3, this.durationMs);
            }
            if (this.installedVrApplications != null && this.installedVrApplications.length > 0) {
                while (i < this.installedVrApplications.length) {
                    final Application application = this.installedVrApplications[i];
                    if (application != null) {
                        codedOutputByteBufferNano.writeMessage(4, application);
                    }
                    ++i;
                }
            }
            if (this.cyclops != null) {
                codedOutputByteBufferNano.writeMessage(5, this.cyclops);
            }
            if (this.qrCodeScan != null) {
                codedOutputByteBufferNano.writeMessage(6, this.qrCodeScan);
            }
            if (this.cohort != null) {
                codedOutputByteBufferNano.writeString(7, this.cohort);
            }
            if (this.lifetimeCountBucket != null) {
                codedOutputByteBufferNano.writeInt32(8, this.lifetimeCountBucket);
            }
            if (this.performanceStats != null) {
                codedOutputByteBufferNano.writeMessage(9, this.performanceStats);
            }
            if (this.sensorStats != null) {
                codedOutputByteBufferNano.writeMessage(10, this.sensorStats);
            }
            if (this.audioStats != null) {
                codedOutputByteBufferNano.writeMessage(11, this.audioStats);
            }
            if (this.embedVrWidget != null) {
                codedOutputByteBufferNano.writeMessage(12, this.embedVrWidget);
            }
            if (this.vrCore != null) {
                codedOutputByteBufferNano.writeMessage(13, this.vrCore);
            }
            if (this.earthVr != null) {
                codedOutputByteBufferNano.writeMessage(14, this.earthVr);
            }
            if (this.launcher != null) {
                codedOutputByteBufferNano.writeMessage(15, this.launcher);
            }
            if (this.keyboard != null) {
                codedOutputByteBufferNano.writeMessage(16, this.keyboard);
            }
            if (this.renderer != null) {
                codedOutputByteBufferNano.writeMessage(17, this.renderer);
            }
            if (this.lullaby != null) {
                codedOutputByteBufferNano.writeMessage(18, this.lullaby);
            }
            if (this.streetView != null) {
                codedOutputByteBufferNano.writeMessage(19, this.streetView);
            }
            if (this.photos != null) {
                codedOutputByteBufferNano.writeMessage(20, this.photos);
            }
            if (this.vrHome != null) {
                codedOutputByteBufferNano.writeMessage(21, this.vrHome);
            }
            if (this.sdkConfiguration != null) {
                codedOutputByteBufferNano.writeMessage(22, this.sdkConfiguration);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
        
        public static final class Application extends ExtendableMessageNano<Application> implements Cloneable
        {
            private static volatile Application[] _emptyArray;
            public String name;
            public String packageName;
            public String version;
            
            public Application() {
                this.clear();
            }
            
            public static Application[] emptyArray() {
                if (Application._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Application._emptyArray != null) {
                                    break;
                                }
                            }
                            Application._emptyArray = new Application[0];
                            continue;
                        }
                    }
                }
                return Application._emptyArray;
            }
            
            public static Application parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Application().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Application parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Application(), array);
            }
            
            public final Application clear() {
                this.packageName = null;
                this.name = null;
                this.version = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Application clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.packageName != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
                }
                if (this.name != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.name);
                }
                if (this.version != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.version);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Application mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            this.packageName = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 18: {
                            this.name = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 26: {
                            this.version = codedInputByteBufferNano.readString();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.packageName != null) {
                    codedOutputByteBufferNano.writeString(1, this.packageName);
                }
                if (this.name != null) {
                    codedOutputByteBufferNano.writeString(2, this.name);
                }
                if (this.version != null) {
                    codedOutputByteBufferNano.writeString(3, this.version);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class AudioStats extends ExtendableMessageNano<AudioStats> implements Cloneable
        {
            private static volatile AudioStats[] _emptyArray;
            public HistogramBucket[] cpuMeasurementsPercent;
            public Integer framesPerBuffer;
            public HistogramBucket[] numberOfSimultaneousSoundFields;
            public HistogramBucket[] numberOfSimultaneousSoundObjects;
            public Integer renderingMode;
            public HistogramBucket[] renderingTimePerBufferMilliseconds;
            public Integer sampleRate;
            
            public AudioStats() {
                this.clear();
            }
            
            public static AudioStats[] emptyArray() {
                if (AudioStats._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (AudioStats._emptyArray != null) {
                                    break;
                                }
                            }
                            AudioStats._emptyArray = new AudioStats[0];
                            continue;
                        }
                    }
                }
                return AudioStats._emptyArray;
            }
            
            public static AudioStats parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new AudioStats().mergeFrom(codedInputByteBufferNano);
            }
            
            public static AudioStats parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new AudioStats(), array);
            }
            
            public final AudioStats clear() {
                this.sampleRate = null;
                this.framesPerBuffer = null;
                this.renderingTimePerBufferMilliseconds = HistogramBucket.emptyArray();
                this.numberOfSimultaneousSoundObjects = HistogramBucket.emptyArray();
                this.numberOfSimultaneousSoundFields = HistogramBucket.emptyArray();
                this.cpuMeasurementsPercent = HistogramBucket.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final AudioStats clone() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: aload_0        
                //     3: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                //     6: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$AudioStats;
                //     9: astore_2       
                //    10: aload_0        
                //    11: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    14: ifnonnull       50
                //    17: aload_0        
                //    18: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    21: ifnonnull       114
                //    24: aload_0        
                //    25: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    28: ifnonnull       178
                //    31: aload_0        
                //    32: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    35: ifnonnull       242
                //    38: aload_2        
                //    39: areturn        
                //    40: astore_2       
                //    41: new             Ljava/lang/AssertionError;
                //    44: dup            
                //    45: aload_2        
                //    46: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                //    49: athrow         
                //    50: aload_0        
                //    51: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    54: arraylength    
                //    55: ifle            17
                //    58: aload_2        
                //    59: aload_0        
                //    60: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    63: arraylength    
                //    64: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    67: putfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    70: iconst_0       
                //    71: istore_3       
                //    72: iload_3        
                //    73: aload_0        
                //    74: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    77: arraylength    
                //    78: if_icmpge       17
                //    81: aload_0        
                //    82: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    85: iload_3        
                //    86: aaload         
                //    87: ifnonnull       96
                //    90: iinc            3, 1
                //    93: goto            72
                //    96: aload_2        
                //    97: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   100: iload_3        
                //   101: aload_0        
                //   102: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.renderingTimePerBufferMilliseconds:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   105: iload_3        
                //   106: aaload         
                //   107: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   110: aastore        
                //   111: goto            90
                //   114: aload_0        
                //   115: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   118: arraylength    
                //   119: ifle            24
                //   122: aload_2        
                //   123: aload_0        
                //   124: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   127: arraylength    
                //   128: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   131: putfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   134: iconst_0       
                //   135: istore_3       
                //   136: iload_3        
                //   137: aload_0        
                //   138: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   141: arraylength    
                //   142: if_icmpge       24
                //   145: aload_0        
                //   146: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   149: iload_3        
                //   150: aaload         
                //   151: ifnonnull       160
                //   154: iinc            3, 1
                //   157: goto            136
                //   160: aload_2        
                //   161: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   164: iload_3        
                //   165: aload_0        
                //   166: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundObjects:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   169: iload_3        
                //   170: aaload         
                //   171: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   174: aastore        
                //   175: goto            154
                //   178: aload_0        
                //   179: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   182: arraylength    
                //   183: ifle            31
                //   186: aload_2        
                //   187: aload_0        
                //   188: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   191: arraylength    
                //   192: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   195: putfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   198: iconst_0       
                //   199: istore_3       
                //   200: iload_3        
                //   201: aload_0        
                //   202: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   205: arraylength    
                //   206: if_icmpge       31
                //   209: aload_0        
                //   210: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   213: iload_3        
                //   214: aaload         
                //   215: ifnonnull       224
                //   218: iinc            3, 1
                //   221: goto            200
                //   224: aload_2        
                //   225: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   228: iload_3        
                //   229: aload_0        
                //   230: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.numberOfSimultaneousSoundFields:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   233: iload_3        
                //   234: aaload         
                //   235: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   238: aastore        
                //   239: goto            218
                //   242: aload_0        
                //   243: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   246: arraylength    
                //   247: ifle            38
                //   250: aload_2        
                //   251: aload_0        
                //   252: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   255: arraylength    
                //   256: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   259: putfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   262: iload_1        
                //   263: istore_3       
                //   264: iload_3        
                //   265: aload_0        
                //   266: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   269: arraylength    
                //   270: if_icmpge       38
                //   273: aload_0        
                //   274: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   277: iload_3        
                //   278: aaload         
                //   279: ifnonnull       288
                //   282: iinc            3, 1
                //   285: goto            264
                //   288: aload_2        
                //   289: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   292: iload_3        
                //   293: aload_0        
                //   294: getfield        com/google/common/logging/nano/Vr$VREvent$AudioStats.cpuMeasurementsPercent:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   297: iload_3        
                //   298: aaload         
                //   299: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   302: aastore        
                //   303: goto            282
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                  
                //  -----  -----  -----  -----  --------------------------------------
                //  2      10     40     50     Ljava/lang/CloneNotSupportedException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:HistogramBucket[](AudioStats::numberOfSimultaneousSoundFields, this:AudioStats)), ldc:int(0))
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
            
            @Override
            protected final int computeSerializedSize() {
                final int n = 0;
                int computeSerializedSize = super.computeSerializedSize();
                if (this.renderingMode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.renderingMode);
                }
                if (this.sampleRate != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.sampleRate);
                }
                int n2;
                if (this.framesPerBuffer == null) {
                    n2 = computeSerializedSize;
                }
                else {
                    n2 = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, this.framesPerBuffer);
                }
                int n3;
                if (this.renderingTimePerBufferMilliseconds == null) {
                    n3 = n2;
                }
                else {
                    n3 = n2;
                    if (this.renderingTimePerBufferMilliseconds.length > 0) {
                        n3 = n2;
                        for (int i = 0; i < this.renderingTimePerBufferMilliseconds.length; ++i) {
                            final HistogramBucket histogramBucket = this.renderingTimePerBufferMilliseconds[i];
                            if (histogramBucket != null) {
                                n3 += CodedOutputByteBufferNano.computeMessageSize(4, histogramBucket);
                            }
                        }
                    }
                }
                int n4;
                if (this.numberOfSimultaneousSoundObjects == null) {
                    n4 = n3;
                }
                else {
                    n4 = n3;
                    if (this.numberOfSimultaneousSoundObjects.length > 0) {
                        for (int j = 0; j < this.numberOfSimultaneousSoundObjects.length; ++j) {
                            final HistogramBucket histogramBucket2 = this.numberOfSimultaneousSoundObjects[j];
                            if (histogramBucket2 != null) {
                                n3 += CodedOutputByteBufferNano.computeMessageSize(5, histogramBucket2);
                            }
                        }
                        n4 = n3;
                    }
                }
                int n5;
                if (this.numberOfSimultaneousSoundFields == null) {
                    n5 = n4;
                }
                else {
                    n5 = n4;
                    if (this.numberOfSimultaneousSoundFields.length > 0) {
                        n5 = n4;
                        for (int k = 0; k < this.numberOfSimultaneousSoundFields.length; ++k) {
                            final HistogramBucket histogramBucket3 = this.numberOfSimultaneousSoundFields[k];
                            if (histogramBucket3 != null) {
                                n5 += CodedOutputByteBufferNano.computeMessageSize(6, histogramBucket3);
                            }
                        }
                    }
                }
                int n6;
                if (this.cpuMeasurementsPercent == null) {
                    n6 = n5;
                }
                else {
                    n6 = n5;
                    if (this.cpuMeasurementsPercent.length > 0) {
                        int n7 = n;
                        while (true) {
                            n6 = n5;
                            if (n7 >= this.cpuMeasurementsPercent.length) {
                                break;
                            }
                            final HistogramBucket histogramBucket4 = this.cpuMeasurementsPercent[n7];
                            if (histogramBucket4 != null) {
                                n5 += CodedOutputByteBufferNano.computeMessageSize(7, histogramBucket4);
                            }
                            ++n7;
                        }
                    }
                }
                return n6;
            }
            
            @Override
            public final AudioStats mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3: {
                                    this.renderingMode = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 16: {
                            this.sampleRate = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 24: {
                            this.framesPerBuffer = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 34: {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                            int i;
                            if (this.renderingTimePerBufferMilliseconds != null) {
                                i = this.renderingTimePerBufferMilliseconds.length;
                            }
                            else {
                                i = 0;
                            }
                            final HistogramBucket[] renderingTimePerBufferMilliseconds = new HistogramBucket[repeatedFieldArrayLength + i];
                            if (i != 0) {
                                System.arraycopy(this.renderingTimePerBufferMilliseconds, 0, renderingTimePerBufferMilliseconds, 0, i);
                            }
                            while (i < renderingTimePerBufferMilliseconds.length - 1) {
                                codedInputByteBufferNano.readMessage(renderingTimePerBufferMilliseconds[i] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(renderingTimePerBufferMilliseconds[i] = new HistogramBucket());
                            this.renderingTimePerBufferMilliseconds = renderingTimePerBufferMilliseconds;
                            continue;
                        }
                        case 42: {
                            final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                            int j;
                            if (this.numberOfSimultaneousSoundObjects != null) {
                                j = this.numberOfSimultaneousSoundObjects.length;
                            }
                            else {
                                j = 0;
                            }
                            final HistogramBucket[] numberOfSimultaneousSoundObjects = new HistogramBucket[repeatedFieldArrayLength2 + j];
                            if (j != 0) {
                                System.arraycopy(this.numberOfSimultaneousSoundObjects, 0, numberOfSimultaneousSoundObjects, 0, j);
                            }
                            while (j < numberOfSimultaneousSoundObjects.length - 1) {
                                codedInputByteBufferNano.readMessage(numberOfSimultaneousSoundObjects[j] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++j;
                            }
                            codedInputByteBufferNano.readMessage(numberOfSimultaneousSoundObjects[j] = new HistogramBucket());
                            this.numberOfSimultaneousSoundObjects = numberOfSimultaneousSoundObjects;
                            continue;
                        }
                        case 50: {
                            final int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                            int k;
                            if (this.numberOfSimultaneousSoundFields != null) {
                                k = this.numberOfSimultaneousSoundFields.length;
                            }
                            else {
                                k = 0;
                            }
                            final HistogramBucket[] numberOfSimultaneousSoundFields = new HistogramBucket[repeatedFieldArrayLength3 + k];
                            if (k != 0) {
                                System.arraycopy(this.numberOfSimultaneousSoundFields, 0, numberOfSimultaneousSoundFields, 0, k);
                            }
                            while (k < numberOfSimultaneousSoundFields.length - 1) {
                                codedInputByteBufferNano.readMessage(numberOfSimultaneousSoundFields[k] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++k;
                            }
                            codedInputByteBufferNano.readMessage(numberOfSimultaneousSoundFields[k] = new HistogramBucket());
                            this.numberOfSimultaneousSoundFields = numberOfSimultaneousSoundFields;
                            continue;
                        }
                        case 58: {
                            final int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 58);
                            int l;
                            if (this.cpuMeasurementsPercent != null) {
                                l = this.cpuMeasurementsPercent.length;
                            }
                            else {
                                l = 0;
                            }
                            final HistogramBucket[] cpuMeasurementsPercent = new HistogramBucket[repeatedFieldArrayLength4 + l];
                            if (l != 0) {
                                System.arraycopy(this.cpuMeasurementsPercent, 0, cpuMeasurementsPercent, 0, l);
                            }
                            while (l < cpuMeasurementsPercent.length - 1) {
                                codedInputByteBufferNano.readMessage(cpuMeasurementsPercent[l] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++l;
                            }
                            codedInputByteBufferNano.readMessage(cpuMeasurementsPercent[l] = new HistogramBucket());
                            this.cpuMeasurementsPercent = cpuMeasurementsPercent;
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final int n = 0;
                if (this.renderingMode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.renderingMode);
                }
                if (this.sampleRate != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.sampleRate);
                }
                if (this.framesPerBuffer != null) {
                    codedOutputByteBufferNano.writeInt32(3, this.framesPerBuffer);
                }
                if (this.renderingTimePerBufferMilliseconds != null && this.renderingTimePerBufferMilliseconds.length > 0) {
                    for (int i = 0; i < this.renderingTimePerBufferMilliseconds.length; ++i) {
                        final HistogramBucket histogramBucket = this.renderingTimePerBufferMilliseconds[i];
                        if (histogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(4, histogramBucket);
                        }
                    }
                }
                if (this.numberOfSimultaneousSoundObjects != null && this.numberOfSimultaneousSoundObjects.length > 0) {
                    for (int j = 0; j < this.numberOfSimultaneousSoundObjects.length; ++j) {
                        final HistogramBucket histogramBucket2 = this.numberOfSimultaneousSoundObjects[j];
                        if (histogramBucket2 != null) {
                            codedOutputByteBufferNano.writeMessage(5, histogramBucket2);
                        }
                    }
                }
                if (this.numberOfSimultaneousSoundFields != null && this.numberOfSimultaneousSoundFields.length > 0) {
                    for (int k = 0; k < this.numberOfSimultaneousSoundFields.length; ++k) {
                        final HistogramBucket histogramBucket3 = this.numberOfSimultaneousSoundFields[k];
                        if (histogramBucket3 != null) {
                            codedOutputByteBufferNano.writeMessage(6, histogramBucket3);
                        }
                    }
                }
                if (this.cpuMeasurementsPercent != null && this.cpuMeasurementsPercent.length > 0) {
                    for (int l = n; l < this.cpuMeasurementsPercent.length; ++l) {
                        final HistogramBucket histogramBucket4 = this.cpuMeasurementsPercent[l];
                        if (histogramBucket4 != null) {
                            codedOutputByteBufferNano.writeMessage(7, histogramBucket4);
                        }
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public interface RenderingMode
            {
                public static final int BINAURAL_HIGH_QUALITY = 3;
                public static final int BINAURAL_LOW_QUALITY = 2;
                public static final int STEREO_PANNING = 1;
                public static final int UNKNOWN = 0;
            }
        }
        
        public interface Bucket
        {
            public static final int ELEVEN_TO_TWENTY = 11;
            public static final int FIVE = 5;
            public static final int FOUR = 4;
            public static final int ONE = 1;
            public static final int SIX_TO_TEN = 6;
            public static final int THREE = 3;
            public static final int TWENTYONE_PLUS = 21;
            public static final int TWO = 2;
            public static final int UNKNOWN_BUCKET = 0;
        }
        
        public static final class Cyclops extends ExtendableMessageNano<Cyclops> implements Cloneable
        {
            private static volatile Cyclops[] _emptyArray;
            public Capture capture;
            public Share share;
            public ShareStart shareStart;
            public View view;
            
            public Cyclops() {
                this.clear();
            }
            
            public static Cyclops[] emptyArray() {
                if (Cyclops._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Cyclops._emptyArray != null) {
                                    break;
                                }
                            }
                            Cyclops._emptyArray = new Cyclops[0];
                            continue;
                        }
                    }
                }
                return Cyclops._emptyArray;
            }
            
            public static Cyclops parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Cyclops().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Cyclops parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Cyclops(), array);
            }
            
            public final Cyclops clear() {
                this.capture = null;
                this.view = null;
                this.share = null;
                this.shareStart = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Cyclops clone() {
                Cyclops cyclops = null;
                Label_0022_Outer:Label_0029_Outer:
                while (true) {
                Label_0090:
                    while (true) {
                    Label_0076:
                        while (true) {
                        Label_0062:
                            while (true) {
                                try {
                                    cyclops = super.clone();
                                    if (this.capture == null) {
                                        if (this.view != null) {
                                            break Label_0062;
                                        }
                                        if (this.share != null) {
                                            break Label_0076;
                                        }
                                        if (this.shareStart == null) {
                                            return cyclops;
                                        }
                                        break Label_0090;
                                    }
                                }
                                catch (final CloneNotSupportedException detailMessage) {
                                    throw new AssertionError((Object)detailMessage);
                                }
                                cyclops.capture = this.capture.clone();
                                continue Label_0022_Outer;
                            }
                            cyclops.view = this.view.clone();
                            continue Label_0029_Outer;
                        }
                        cyclops.share = this.share.clone();
                        continue;
                    }
                    cyclops.shareStart = this.shareStart.clone();
                    return cyclops;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.capture != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.capture);
                }
                if (this.view != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.view);
                }
                if (this.share != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.share);
                }
                if (this.shareStart != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.shareStart);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Cyclops mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            if (this.capture == null) {
                                this.capture = new Capture();
                            }
                            codedInputByteBufferNano.readMessage(this.capture);
                            continue;
                        }
                        case 18: {
                            if (this.view == null) {
                                this.view = new View();
                            }
                            codedInputByteBufferNano.readMessage(this.view);
                            continue;
                        }
                        case 26: {
                            if (this.share == null) {
                                this.share = new Share();
                            }
                            codedInputByteBufferNano.readMessage(this.share);
                            continue;
                        }
                        case 34: {
                            if (this.shareStart == null) {
                                this.shareStart = new ShareStart();
                            }
                            codedInputByteBufferNano.readMessage(this.shareStart);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.capture != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.capture);
                }
                if (this.view != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.view);
                }
                if (this.share != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.share);
                }
                if (this.shareStart != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.shareStart);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class Capture extends ExtendableMessageNano<Capture> implements Cloneable
            {
                private static volatile Capture[] _emptyArray;
                public Float angleDegrees;
                public Long captureTimeMs;
                public Boolean captureWarnings;
                public Long compositionTimeMs;
                public Integer outcome;
                public Long processingTimeMs;
                public Boolean withSound;
                
                public Capture() {
                    this.clear();
                }
                
                public static Capture[] emptyArray() {
                    if (Capture._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Capture._emptyArray != null) {
                                        break;
                                    }
                                }
                                Capture._emptyArray = new Capture[0];
                                continue;
                            }
                        }
                    }
                    return Capture._emptyArray;
                }
                
                public static Capture parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Capture().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Capture parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Capture(), array);
                }
                
                public final Capture clear() {
                    this.angleDegrees = null;
                    this.withSound = null;
                    this.captureWarnings = null;
                    this.compositionTimeMs = null;
                    this.captureTimeMs = null;
                    this.processingTimeMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Capture clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.outcome != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.outcome);
                    }
                    if (this.angleDegrees != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.angleDegrees);
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.withSound);
                    }
                    if (this.captureWarnings != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, this.captureWarnings);
                    }
                    if (this.compositionTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(5, this.compositionTimeMs);
                    }
                    if (this.captureTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, this.captureTimeMs);
                    }
                    if (this.processingTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(7, this.processingTimeMs);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Capture mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4: {
                                        this.outcome = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 21: {
                                this.angleDegrees = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                            case 24: {
                                this.withSound = codedInputByteBufferNano.readBool();
                                continue;
                            }
                            case 32: {
                                this.captureWarnings = codedInputByteBufferNano.readBool();
                                continue;
                            }
                            case 40: {
                                this.compositionTimeMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 48: {
                                this.captureTimeMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 56: {
                                this.processingTimeMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.outcome != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.outcome);
                    }
                    if (this.angleDegrees != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.angleDegrees);
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(3, this.withSound);
                    }
                    if (this.captureWarnings != null) {
                        codedOutputByteBufferNano.writeBool(4, this.captureWarnings);
                    }
                    if (this.compositionTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(5, this.compositionTimeMs);
                    }
                    if (this.captureTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(6, this.captureTimeMs);
                    }
                    if (this.processingTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(7, this.processingTimeMs);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Outcome
                {
                    public static final int FAILURE_CAPTURE = 2;
                    public static final int FAILURE_PROCESSING = 3;
                    public static final int SUCCESS = 1;
                    public static final int UNKNOWN = 0;
                    public static final int USER_CANCELLED = 4;
                }
            }
            
            public static final class Share extends ExtendableMessageNano<Share> implements Cloneable
            {
                private static volatile Share[] _emptyArray;
                public Integer numPhotos;
                public Integer type;
                public Boolean withSound;
                
                public Share() {
                    this.clear();
                }
                
                public static Share[] emptyArray() {
                    if (Share._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Share._emptyArray != null) {
                                        break;
                                    }
                                }
                                Share._emptyArray = new Share[0];
                                continue;
                            }
                        }
                    }
                    return Share._emptyArray;
                }
                
                public static Share parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Share().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Share parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Share(), array);
                }
                
                public final Share clear() {
                    this.withSound = null;
                    this.numPhotos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Share clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.withSound);
                    }
                    if (this.numPhotos != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.numPhotos);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Share mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7: {
                                        this.type = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.withSound = codedInputByteBufferNano.readBool();
                                continue;
                            }
                            case 24: {
                                this.numPhotos = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type);
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(2, this.withSound);
                    }
                    if (this.numPhotos != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.numPhotos);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Type
                {
                    public static final int CANCELLED = 1;
                    public static final int COPY_LINK = 2;
                    public static final int EMAIL = 7;
                    public static final int SOCIAL_FACEBOOK = 4;
                    public static final int SOCIAL_GPLUS = 6;
                    public static final int SOCIAL_OTHER = 3;
                    public static final int SOCIAL_TWITTER = 5;
                    public static final int UNKNOWN = 0;
                }
            }
            
            public static final class ShareStart extends ExtendableMessageNano<ShareStart> implements Cloneable
            {
                private static volatile ShareStart[] _emptyArray;
                public Integer numPhotos;
                public Integer originScreen;
                
                public ShareStart() {
                    this.clear();
                }
                
                public static ShareStart[] emptyArray() {
                    if (ShareStart._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (ShareStart._emptyArray != null) {
                                        break;
                                    }
                                }
                                ShareStart._emptyArray = new ShareStart[0];
                                continue;
                            }
                        }
                    }
                    return ShareStart._emptyArray;
                }
                
                public static ShareStart parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new ShareStart().mergeFrom(codedInputByteBufferNano);
                }
                
                public static ShareStart parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new ShareStart(), array);
                }
                
                public final ShareStart clear() {
                    this.numPhotos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final ShareStart clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.originScreen != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.originScreen);
                    }
                    if (this.numPhotos != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.numPhotos);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final ShareStart mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.originScreen = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.numPhotos = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.originScreen != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.originScreen);
                    }
                    if (this.numPhotos != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.numPhotos);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface OriginScreen
                {
                    public static final int GALLERY = 1;
                    public static final int ONE_UP_VIEW = 2;
                    public static final int UNKNOWN = 0;
                }
            }
            
            public static final class View extends ExtendableMessageNano<View> implements Cloneable
            {
                private static volatile View[] _emptyArray;
                public Boolean interaction;
                public Integer numPanos;
                public Integer orientation;
                public Boolean withSound;
                
                public View() {
                    this.clear();
                }
                
                public static View[] emptyArray() {
                    if (View._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (View._emptyArray != null) {
                                        break;
                                    }
                                }
                                View._emptyArray = new View[0];
                                continue;
                            }
                        }
                    }
                    return View._emptyArray;
                }
                
                public static View parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new View().mergeFrom(codedInputByteBufferNano);
                }
                
                public static View parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new View(), array);
                }
                
                public final View clear() {
                    this.interaction = null;
                    this.withSound = null;
                    this.numPanos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final View clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.orientation != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.orientation);
                    }
                    if (this.interaction != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.interaction);
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.withSound);
                    }
                    if (this.numPanos != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.numPanos);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final View mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.orientation = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.interaction = codedInputByteBufferNano.readBool();
                                continue;
                            }
                            case 24: {
                                this.withSound = codedInputByteBufferNano.readBool();
                                continue;
                            }
                            case 32: {
                                this.numPanos = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.orientation != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.orientation);
                    }
                    if (this.interaction != null) {
                        codedOutputByteBufferNano.writeBool(2, this.interaction);
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(3, this.withSound);
                    }
                    if (this.numPanos != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.numPanos);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Orientation
                {
                    public static final int LANDSCAPE = 1;
                    public static final int PORTRAIT = 2;
                    public static final int UNKNOWN = 0;
                }
            }
        }
        
        public static final class DoublePrecisionTransform extends ExtendableMessageNano<DoublePrecisionTransform> implements Cloneable
        {
            private static volatile DoublePrecisionTransform[] _emptyArray;
            public Double rotationQx;
            public Double rotationQy;
            public Double rotationQz;
            public Double scale;
            public Double translationX;
            public Double translationY;
            public Double translationZ;
            
            public DoublePrecisionTransform() {
                this.clear();
            }
            
            public static DoublePrecisionTransform[] emptyArray() {
                if (DoublePrecisionTransform._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (DoublePrecisionTransform._emptyArray != null) {
                                    break;
                                }
                            }
                            DoublePrecisionTransform._emptyArray = new DoublePrecisionTransform[0];
                            continue;
                        }
                    }
                }
                return DoublePrecisionTransform._emptyArray;
            }
            
            public static DoublePrecisionTransform parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new DoublePrecisionTransform().mergeFrom(codedInputByteBufferNano);
            }
            
            public static DoublePrecisionTransform parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new DoublePrecisionTransform(), array);
            }
            
            public final DoublePrecisionTransform clear() {
                this.translationX = null;
                this.translationY = null;
                this.translationZ = null;
                this.rotationQx = null;
                this.rotationQy = null;
                this.rotationQz = null;
                this.scale = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final DoublePrecisionTransform clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.translationX != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(1, this.translationX);
                }
                if (this.translationY != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(2, this.translationY);
                }
                if (this.translationZ != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(3, this.translationZ);
                }
                if (this.rotationQx != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(4, this.rotationQx);
                }
                if (this.rotationQy != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(5, this.rotationQy);
                }
                if (this.rotationQz != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(6, this.rotationQz);
                }
                if (this.scale != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(7, this.scale);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final DoublePrecisionTransform mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 9: {
                            this.translationX = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 17: {
                            this.translationY = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 25: {
                            this.translationZ = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 33: {
                            this.rotationQx = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 41: {
                            this.rotationQy = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 49: {
                            this.rotationQz = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                        case 57: {
                            this.scale = codedInputByteBufferNano.readDouble();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.translationX != null) {
                    codedOutputByteBufferNano.writeDouble(1, this.translationX);
                }
                if (this.translationY != null) {
                    codedOutputByteBufferNano.writeDouble(2, this.translationY);
                }
                if (this.translationZ != null) {
                    codedOutputByteBufferNano.writeDouble(3, this.translationZ);
                }
                if (this.rotationQx != null) {
                    codedOutputByteBufferNano.writeDouble(4, this.rotationQx);
                }
                if (this.rotationQy != null) {
                    codedOutputByteBufferNano.writeDouble(5, this.rotationQy);
                }
                if (this.rotationQz != null) {
                    codedOutputByteBufferNano.writeDouble(6, this.rotationQz);
                }
                if (this.scale != null) {
                    codedOutputByteBufferNano.writeDouble(7, this.scale);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class EarthVr extends ExtendableMessageNano<EarthVr> implements Cloneable
        {
            private static volatile EarthVr[] _emptyArray;
            public Actor[] actors;
            public AppState appState;
            public ControllerState[] controllerStates;
            public Environment environment;
            public Menu menu;
            public Preferences preferences;
            public SplashScreen splashScreen;
            public Transform startFromHeadTransform;
            public DoublePrecisionTransform startFromKeyholeTransform;
            public Tour tour;
            public Tutorial tutorial;
            public View view;
            
            public EarthVr() {
                this.clear();
            }
            
            public static EarthVr[] emptyArray() {
                if (EarthVr._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (EarthVr._emptyArray != null) {
                                    break;
                                }
                            }
                            EarthVr._emptyArray = new EarthVr[0];
                            continue;
                        }
                    }
                }
                return EarthVr._emptyArray;
            }
            
            public static EarthVr parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new EarthVr().mergeFrom(codedInputByteBufferNano);
            }
            
            public static EarthVr parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new EarthVr(), array);
            }
            
            public final EarthVr clear() {
                this.startFromKeyholeTransform = null;
                this.startFromHeadTransform = null;
                this.controllerStates = ControllerState.emptyArray();
                this.appState = null;
                this.view = null;
                this.menu = null;
                this.preferences = null;
                this.tour = null;
                this.tutorial = null;
                this.actors = Actor.emptyArray();
                this.environment = null;
                this.splashScreen = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final EarthVr clone() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: aload_0        
                //     3: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                //     6: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$EarthVr;
                //     9: astore_2       
                //    10: aload_0        
                //    11: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromKeyholeTransform:Lcom/google/common/logging/nano/Vr$VREvent$DoublePrecisionTransform;
                //    14: ifnonnull       106
                //    17: aload_0        
                //    18: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                //    21: ifnonnull       120
                //    24: aload_0        
                //    25: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //    28: ifnonnull       134
                //    31: aload_0        
                //    32: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.appState:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$AppState;
                //    35: ifnonnull       198
                //    38: aload_0        
                //    39: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.view:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$View;
                //    42: ifnonnull       212
                //    45: aload_0        
                //    46: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.menu:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Menu;
                //    49: ifnonnull       226
                //    52: aload_0        
                //    53: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.preferences:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Preferences;
                //    56: ifnonnull       240
                //    59: aload_0        
                //    60: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tour:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tour;
                //    63: ifnonnull       254
                //    66: aload_0        
                //    67: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tutorial:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tutorial;
                //    70: ifnonnull       268
                //    73: aload_0        
                //    74: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //    77: ifnonnull       282
                //    80: aload_0        
                //    81: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.environment:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Environment;
                //    84: ifnonnull       346
                //    87: aload_0        
                //    88: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.splashScreen:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$SplashScreen;
                //    91: ifnonnull       360
                //    94: aload_2        
                //    95: areturn        
                //    96: astore_2       
                //    97: new             Ljava/lang/AssertionError;
                //   100: dup            
                //   101: aload_2        
                //   102: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                //   105: athrow         
                //   106: aload_2        
                //   107: aload_0        
                //   108: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromKeyholeTransform:Lcom/google/common/logging/nano/Vr$VREvent$DoublePrecisionTransform;
                //   111: invokevirtual   com/google/common/logging/nano/Vr$VREvent$DoublePrecisionTransform.clone:()Lcom/google/common/logging/nano/Vr$VREvent$DoublePrecisionTransform;
                //   114: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromKeyholeTransform:Lcom/google/common/logging/nano/Vr$VREvent$DoublePrecisionTransform;
                //   117: goto            17
                //   120: aload_2        
                //   121: aload_0        
                //   122: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                //   125: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Transform.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                //   128: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                //   131: goto            24
                //   134: aload_0        
                //   135: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   138: arraylength    
                //   139: ifle            31
                //   142: aload_2        
                //   143: aload_0        
                //   144: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   147: arraylength    
                //   148: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   151: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   154: iconst_0       
                //   155: istore_3       
                //   156: iload_3        
                //   157: aload_0        
                //   158: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   161: arraylength    
                //   162: if_icmpge       31
                //   165: aload_0        
                //   166: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   169: iload_3        
                //   170: aaload         
                //   171: ifnonnull       180
                //   174: iinc            3, 1
                //   177: goto            156
                //   180: aload_2        
                //   181: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   184: iload_3        
                //   185: aload_0        
                //   186: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   189: iload_3        
                //   190: aaload         
                //   191: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$ControllerState;
                //   194: aastore        
                //   195: goto            174
                //   198: aload_2        
                //   199: aload_0        
                //   200: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.appState:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$AppState;
                //   203: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$AppState.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$AppState;
                //   206: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.appState:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$AppState;
                //   209: goto            38
                //   212: aload_2        
                //   213: aload_0        
                //   214: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.view:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$View;
                //   217: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$View.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$View;
                //   220: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.view:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$View;
                //   223: goto            45
                //   226: aload_2        
                //   227: aload_0        
                //   228: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.menu:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Menu;
                //   231: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Menu.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Menu;
                //   234: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.menu:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Menu;
                //   237: goto            52
                //   240: aload_2        
                //   241: aload_0        
                //   242: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.preferences:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Preferences;
                //   245: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Preferences.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Preferences;
                //   248: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.preferences:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Preferences;
                //   251: goto            59
                //   254: aload_2        
                //   255: aload_0        
                //   256: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tour:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tour;
                //   259: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Tour.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tour;
                //   262: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tour:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tour;
                //   265: goto            66
                //   268: aload_2        
                //   269: aload_0        
                //   270: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tutorial:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tutorial;
                //   273: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Tutorial.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tutorial;
                //   276: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.tutorial:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Tutorial;
                //   279: goto            73
                //   282: aload_0        
                //   283: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   286: arraylength    
                //   287: ifle            80
                //   290: aload_2        
                //   291: aload_0        
                //   292: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   295: arraylength    
                //   296: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   299: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   302: iload_1        
                //   303: istore_3       
                //   304: iload_3        
                //   305: aload_0        
                //   306: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   309: arraylength    
                //   310: if_icmpge       80
                //   313: aload_0        
                //   314: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   317: iload_3        
                //   318: aaload         
                //   319: ifnonnull       328
                //   322: iinc            3, 1
                //   325: goto            304
                //   328: aload_2        
                //   329: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   332: iload_3        
                //   333: aload_0        
                //   334: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.actors:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   337: iload_3        
                //   338: aaload         
                //   339: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                //   342: aastore        
                //   343: goto            322
                //   346: aload_2        
                //   347: aload_0        
                //   348: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.environment:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Environment;
                //   351: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Environment.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Environment;
                //   354: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.environment:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Environment;
                //   357: goto            87
                //   360: aload_2        
                //   361: aload_0        
                //   362: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.splashScreen:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$SplashScreen;
                //   365: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$SplashScreen.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$SplashScreen;
                //   368: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr.splashScreen:Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$SplashScreen;
                //   371: goto            94
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                  
                //  -----  -----  -----  -----  --------------------------------------
                //  2      10     96     106    Ljava/lang/CloneNotSupportedException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:ControllerState[](EarthVr::controllerStates, this:EarthVr)), ldc:int(0))
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
            
            @Override
            protected final int computeSerializedSize() {
                int n = 0;
                int computeSerializedSize = super.computeSerializedSize();
                if (this.startFromKeyholeTransform != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.startFromKeyholeTransform);
                }
                if (this.startFromHeadTransform != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromHeadTransform);
                }
                int n2;
                if (this.controllerStates == null) {
                    n2 = computeSerializedSize;
                }
                else {
                    n2 = computeSerializedSize;
                    if (this.controllerStates.length > 0) {
                        for (int i = 0; i < this.controllerStates.length; ++i) {
                            final ControllerState controllerState = this.controllerStates[i];
                            if (controllerState != null) {
                                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, controllerState);
                            }
                        }
                        n2 = computeSerializedSize;
                    }
                }
                if (this.appState != null) {
                    n2 += CodedOutputByteBufferNano.computeMessageSize(4, this.appState);
                }
                if (this.view != null) {
                    n2 += CodedOutputByteBufferNano.computeMessageSize(5, this.view);
                }
                int n3;
                if (this.menu == null) {
                    n3 = n2;
                }
                else {
                    n3 = n2 + CodedOutputByteBufferNano.computeMessageSize(6, this.menu);
                }
                if (this.preferences != null) {
                    n3 += CodedOutputByteBufferNano.computeMessageSize(7, this.preferences);
                }
                if (this.tour != null) {
                    n3 += CodedOutputByteBufferNano.computeMessageSize(8, this.tour);
                }
                if (this.tutorial != null) {
                    n3 += CodedOutputByteBufferNano.computeMessageSize(9, this.tutorial);
                }
                int n4;
                if (this.actors == null) {
                    n4 = n3;
                }
                else {
                    n4 = n3;
                    if (this.actors.length > 0) {
                        while (true) {
                            n4 = n3;
                            if (n >= this.actors.length) {
                                break;
                            }
                            final Actor actor = this.actors[n];
                            if (actor != null) {
                                n3 += CodedOutputByteBufferNano.computeMessageSize(10, actor);
                            }
                            ++n;
                        }
                    }
                }
                if (this.environment != null) {
                    n4 += CodedOutputByteBufferNano.computeMessageSize(11, this.environment);
                }
                if (this.splashScreen != null) {
                    n4 += CodedOutputByteBufferNano.computeMessageSize(12, this.splashScreen);
                }
                return n4;
            }
            
            @Override
            public final EarthVr mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            if (this.startFromKeyholeTransform == null) {
                                this.startFromKeyholeTransform = new DoublePrecisionTransform();
                            }
                            codedInputByteBufferNano.readMessage(this.startFromKeyholeTransform);
                            continue;
                        }
                        case 18: {
                            if (this.startFromHeadTransform == null) {
                                this.startFromHeadTransform = new Transform();
                            }
                            codedInputByteBufferNano.readMessage(this.startFromHeadTransform);
                            continue;
                        }
                        case 26: {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                            int i;
                            if (this.controllerStates != null) {
                                i = this.controllerStates.length;
                            }
                            else {
                                i = 0;
                            }
                            final ControllerState[] controllerStates = new ControllerState[repeatedFieldArrayLength + i];
                            if (i != 0) {
                                System.arraycopy(this.controllerStates, 0, controllerStates, 0, i);
                            }
                            while (i < controllerStates.length - 1) {
                                codedInputByteBufferNano.readMessage(controllerStates[i] = new ControllerState());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(controllerStates[i] = new ControllerState());
                            this.controllerStates = controllerStates;
                            continue;
                        }
                        case 34: {
                            if (this.appState == null) {
                                this.appState = new AppState();
                            }
                            codedInputByteBufferNano.readMessage(this.appState);
                            continue;
                        }
                        case 42: {
                            if (this.view == null) {
                                this.view = new View();
                            }
                            codedInputByteBufferNano.readMessage(this.view);
                            continue;
                        }
                        case 50: {
                            if (this.menu == null) {
                                this.menu = new Menu();
                            }
                            codedInputByteBufferNano.readMessage(this.menu);
                            continue;
                        }
                        case 58: {
                            if (this.preferences == null) {
                                this.preferences = new Preferences();
                            }
                            codedInputByteBufferNano.readMessage(this.preferences);
                            continue;
                        }
                        case 66: {
                            if (this.tour == null) {
                                this.tour = new Tour();
                            }
                            codedInputByteBufferNano.readMessage(this.tour);
                            continue;
                        }
                        case 74: {
                            if (this.tutorial == null) {
                                this.tutorial = new Tutorial();
                            }
                            codedInputByteBufferNano.readMessage(this.tutorial);
                            continue;
                        }
                        case 82: {
                            final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                            int j;
                            if (this.actors != null) {
                                j = this.actors.length;
                            }
                            else {
                                j = 0;
                            }
                            final Actor[] actors = new Actor[repeatedFieldArrayLength2 + j];
                            if (j != 0) {
                                System.arraycopy(this.actors, 0, actors, 0, j);
                            }
                            while (j < actors.length - 1) {
                                codedInputByteBufferNano.readMessage(actors[j] = new Actor());
                                codedInputByteBufferNano.readTag();
                                ++j;
                            }
                            codedInputByteBufferNano.readMessage(actors[j] = new Actor());
                            this.actors = actors;
                            continue;
                        }
                        case 90: {
                            if (this.environment == null) {
                                this.environment = new Environment();
                            }
                            codedInputByteBufferNano.readMessage(this.environment);
                            continue;
                        }
                        case 98: {
                            if (this.splashScreen == null) {
                                this.splashScreen = new SplashScreen();
                            }
                            codedInputByteBufferNano.readMessage(this.splashScreen);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final int n = 0;
                if (this.startFromKeyholeTransform != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.startFromKeyholeTransform);
                }
                if (this.startFromHeadTransform != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.startFromHeadTransform);
                }
                if (this.controllerStates != null && this.controllerStates.length > 0) {
                    for (int i = 0; i < this.controllerStates.length; ++i) {
                        final ControllerState controllerState = this.controllerStates[i];
                        if (controllerState != null) {
                            codedOutputByteBufferNano.writeMessage(3, controllerState);
                        }
                    }
                }
                if (this.appState != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.appState);
                }
                if (this.view != null) {
                    codedOutputByteBufferNano.writeMessage(5, this.view);
                }
                if (this.menu != null) {
                    codedOutputByteBufferNano.writeMessage(6, this.menu);
                }
                if (this.preferences != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.preferences);
                }
                if (this.tour != null) {
                    codedOutputByteBufferNano.writeMessage(8, this.tour);
                }
                if (this.tutorial != null) {
                    codedOutputByteBufferNano.writeMessage(9, this.tutorial);
                }
                if (this.actors != null && this.actors.length > 0) {
                    for (int j = n; j < this.actors.length; ++j) {
                        final Actor actor = this.actors[j];
                        if (actor != null) {
                            codedOutputByteBufferNano.writeMessage(10, actor);
                        }
                    }
                }
                if (this.environment != null) {
                    codedOutputByteBufferNano.writeMessage(11, this.environment);
                }
                if (this.splashScreen != null) {
                    codedOutputByteBufferNano.writeMessage(12, this.splashScreen);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class Actor extends ExtendableMessageNano<Actor> implements Cloneable
            {
                private static volatile Actor[] _emptyArray;
                public ControllerState[] controllerStates;
                public Transform startFromHeadTransform;
                
                public Actor() {
                    this.clear();
                }
                
                public static Actor[] emptyArray() {
                    if (Actor._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Actor._emptyArray != null) {
                                        break;
                                    }
                                }
                                Actor._emptyArray = new Actor[0];
                                continue;
                            }
                        }
                    }
                    return Actor._emptyArray;
                }
                
                public static Actor parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Actor().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Actor parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Actor(), array);
                }
                
                public final Actor clear() {
                    this.startFromHeadTransform = null;
                    this.controllerStates = ControllerState.emptyArray();
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Actor clone() {
                    // 
                    // This method could not be decompiled.
                    // 
                    // Original Bytecode:
                    // 
                    //     1: istore_1       
                    //     2: aload_0        
                    //     3: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                    //     6: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor;
                    //     9: astore_2       
                    //    10: aload_0        
                    //    11: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                    //    14: ifnonnull       36
                    //    17: aload_0        
                    //    18: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    21: ifnonnull       50
                    //    24: aload_2        
                    //    25: areturn        
                    //    26: astore_2       
                    //    27: new             Ljava/lang/AssertionError;
                    //    30: dup            
                    //    31: aload_2        
                    //    32: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                    //    35: athrow         
                    //    36: aload_2        
                    //    37: aload_0        
                    //    38: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                    //    41: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Transform.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                    //    44: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.startFromHeadTransform:Lcom/google/common/logging/nano/Vr$VREvent$Transform;
                    //    47: goto            17
                    //    50: aload_0        
                    //    51: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    54: arraylength    
                    //    55: ifle            24
                    //    58: aload_2        
                    //    59: aload_0        
                    //    60: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    63: arraylength    
                    //    64: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    67: putfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    70: iload_1        
                    //    71: aload_0        
                    //    72: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    75: arraylength    
                    //    76: if_icmpge       24
                    //    79: aload_0        
                    //    80: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    83: iload_1        
                    //    84: aaload         
                    //    85: ifnonnull       94
                    //    88: iinc            1, 1
                    //    91: goto            70
                    //    94: aload_2        
                    //    95: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //    98: iload_1        
                    //    99: aload_0        
                    //   100: getfield        com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor.controllerStates:[Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //   103: iload_1        
                    //   104: aaload         
                    //   105: invokevirtual   com/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState.clone:()Lcom/google/common/logging/nano/Vr$VREvent$EarthVr$Actor$ControllerState;
                    //   108: aastore        
                    //   109: goto            88
                    //    Exceptions:
                    //  Try           Handler
                    //  Start  End    Start  End    Type                                  
                    //  -----  -----  -----  -----  --------------------------------------
                    //  2      10     26     36     Ljava/lang/CloneNotSupportedException;
                    // 
                    // The error that occurred was:
                    // 
                    // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:ControllerState[](Actor::controllerStates, this:Actor)), ldc:int(0))
                    //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                    //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                    //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.startFromHeadTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromHeadTransform);
                    }
                    int n;
                    if (this.controllerStates == null) {
                        n = computeSerializedSize;
                    }
                    else {
                        n = computeSerializedSize;
                        if (this.controllerStates.length > 0) {
                            for (int i = 0; i < this.controllerStates.length; ++i) {
                                final ControllerState controllerState = this.controllerStates[i];
                                if (controllerState != null) {
                                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, controllerState);
                                }
                            }
                            n = computeSerializedSize;
                        }
                    }
                    return n;
                }
                
                @Override
                public final Actor mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 18: {
                                if (this.startFromHeadTransform == null) {
                                    this.startFromHeadTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromHeadTransform);
                                continue;
                            }
                            case 26: {
                                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                                int i;
                                if (this.controllerStates != null) {
                                    i = this.controllerStates.length;
                                }
                                else {
                                    i = 0;
                                }
                                final ControllerState[] controllerStates = new ControllerState[repeatedFieldArrayLength + i];
                                if (i != 0) {
                                    System.arraycopy(this.controllerStates, 0, controllerStates, 0, i);
                                }
                                while (i < controllerStates.length - 1) {
                                    codedInputByteBufferNano.readMessage(controllerStates[i] = new ControllerState());
                                    codedInputByteBufferNano.readTag();
                                    ++i;
                                }
                                codedInputByteBufferNano.readMessage(controllerStates[i] = new ControllerState());
                                this.controllerStates = controllerStates;
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    int i = 0;
                    if (this.startFromHeadTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromHeadTransform);
                    }
                    if (this.controllerStates != null && this.controllerStates.length > 0) {
                        while (i < this.controllerStates.length) {
                            final ControllerState controllerState = this.controllerStates[i];
                            if (controllerState != null) {
                                codedOutputByteBufferNano.writeMessage(3, controllerState);
                            }
                            ++i;
                        }
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public static final class ControllerState extends ExtendableMessageNano<ControllerState> implements Cloneable
                {
                    private static volatile ControllerState[] _emptyArray;
                    public Integer role;
                    public Transform startFromControllerTransform;
                    
                    public ControllerState() {
                        this.clear();
                    }
                    
                    public static ControllerState[] emptyArray() {
                        if (ControllerState._emptyArray == null) {
                            while (true) {
                                while (true) {
                                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                                        if (ControllerState._emptyArray != null) {
                                            break;
                                        }
                                    }
                                    ControllerState._emptyArray = new ControllerState[0];
                                    continue;
                                }
                            }
                        }
                        return ControllerState._emptyArray;
                    }
                    
                    public static ControllerState parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new ControllerState().mergeFrom(codedInputByteBufferNano);
                    }
                    
                    public static ControllerState parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                        return MessageNano.mergeFrom(new ControllerState(), array);
                    }
                    
                    public final ControllerState clear() {
                        this.startFromControllerTransform = null;
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }
                    
                    @Override
                    public final ControllerState clone() {
                        while (true) {
                            ControllerState controllerState;
                            try {
                                controllerState = super.clone();
                                if (this.startFromControllerTransform == null) {
                                    return controllerState;
                                }
                            }
                            catch (final CloneNotSupportedException detailMessage) {
                                throw new AssertionError((Object)detailMessage);
                            }
                            controllerState.startFromControllerTransform = this.startFromControllerTransform.clone();
                            return controllerState;
                        }
                    }
                    
                    @Override
                    protected final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.role != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.role);
                        }
                        if (this.startFromControllerTransform != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromControllerTransform);
                        }
                        return computeSerializedSize;
                    }
                    
                    @Override
                    public final ControllerState mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        while (true) {
                            final int tag = codedInputByteBufferNano.readTag();
                            switch (tag) {
                                default: {
                                    if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                case 0: {
                                    return this;
                                }
                                case 8: {
                                    final int int32 = codedInputByteBufferNano.readInt32();
                                    switch (int32) {
                                        default: {
                                            continue;
                                        }
                                        case 0:
                                        case 1:
                                        case 2: {
                                            this.role = int32;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                                case 18: {
                                    if (this.startFromControllerTransform == null) {
                                        this.startFromControllerTransform = new Transform();
                                    }
                                    codedInputByteBufferNano.readMessage(this.startFromControllerTransform);
                                    continue;
                                }
                            }
                        }
                    }
                    
                    @Override
                    public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.role != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.role);
                        }
                        if (this.startFromControllerTransform != null) {
                            codedOutputByteBufferNano.writeMessage(2, this.startFromControllerTransform);
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                    
                    public interface Role
                    {
                        public static final int PRIMARY = 1;
                        public static final int SECONDARY = 2;
                        public static final int UNKNOWN = 0;
                    }
                }
            }
            
            public static final class AppState extends ExtendableMessageNano<AppState> implements Cloneable
            {
                private static volatile AppState[] _emptyArray;
                public Long appModeId;
                
                public AppState() {
                    this.clear();
                }
                
                public static AppState[] emptyArray() {
                    if (AppState._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (AppState._emptyArray != null) {
                                        break;
                                    }
                                }
                                AppState._emptyArray = new AppState[0];
                                continue;
                            }
                        }
                    }
                    return AppState._emptyArray;
                }
                
                public static AppState parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new AppState().mergeFrom(codedInputByteBufferNano);
                }
                
                public static AppState parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new AppState(), array);
                }
                
                public final AppState clear() {
                    this.appModeId = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final AppState clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.appModeId != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.appModeId);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final AppState mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.appModeId = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.appModeId != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.appModeId);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class ControllerState extends ExtendableMessageNano<ControllerState> implements Cloneable
            {
                private static volatile ControllerState[] _emptyArray;
                public Integer role;
                public Transform startFromControllerTransform;
                
                public ControllerState() {
                    this.clear();
                }
                
                public static ControllerState[] emptyArray() {
                    if (ControllerState._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (ControllerState._emptyArray != null) {
                                        break;
                                    }
                                }
                                ControllerState._emptyArray = new ControllerState[0];
                                continue;
                            }
                        }
                    }
                    return ControllerState._emptyArray;
                }
                
                public static ControllerState parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new ControllerState().mergeFrom(codedInputByteBufferNano);
                }
                
                public static ControllerState parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new ControllerState(), array);
                }
                
                public final ControllerState clear() {
                    this.startFromControllerTransform = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final ControllerState clone() {
                    while (true) {
                        ControllerState controllerState;
                        try {
                            controllerState = super.clone();
                            if (this.startFromControllerTransform == null) {
                                return controllerState;
                            }
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                        controllerState.startFromControllerTransform = this.startFromControllerTransform.clone();
                        return controllerState;
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.role != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.role);
                    }
                    if (this.startFromControllerTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromControllerTransform);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final ControllerState mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.role = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 18: {
                                if (this.startFromControllerTransform == null) {
                                    this.startFromControllerTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromControllerTransform);
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.role != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.role);
                    }
                    if (this.startFromControllerTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromControllerTransform);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Role
                {
                    public static final int PRIMARY = 1;
                    public static final int SECONDARY = 2;
                    public static final int UNKNOWN = 0;
                }
            }
            
            public static final class Environment extends ExtendableMessageNano<Environment> implements Cloneable
            {
                private static volatile Environment[] _emptyArray;
                public Transform startFromEnvironmentTransform;
                
                public Environment() {
                    this.clear();
                }
                
                public static Environment[] emptyArray() {
                    if (Environment._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Environment._emptyArray != null) {
                                        break;
                                    }
                                }
                                Environment._emptyArray = new Environment[0];
                                continue;
                            }
                        }
                    }
                    return Environment._emptyArray;
                }
                
                public static Environment parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Environment().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Environment parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Environment(), array);
                }
                
                public final Environment clear() {
                    this.startFromEnvironmentTransform = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Environment clone() {
                    while (true) {
                        Environment environment;
                        try {
                            environment = super.clone();
                            if (this.startFromEnvironmentTransform == null) {
                                return environment;
                            }
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                        environment.startFromEnvironmentTransform = this.startFromEnvironmentTransform.clone();
                        return environment;
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.startFromEnvironmentTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.startFromEnvironmentTransform);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Environment mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                if (this.startFromEnvironmentTransform == null) {
                                    this.startFromEnvironmentTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromEnvironmentTransform);
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.startFromEnvironmentTransform != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.startFromEnvironmentTransform);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class Menu extends ExtendableMessageNano<Menu> implements Cloneable
            {
                private static volatile Menu[] _emptyArray;
                public String categoryName;
                public String contentKey;
                public Integer pageIndex;
                
                public Menu() {
                    this.clear();
                }
                
                public static Menu[] emptyArray() {
                    if (Menu._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Menu._emptyArray != null) {
                                        break;
                                    }
                                }
                                Menu._emptyArray = new Menu[0];
                                continue;
                            }
                        }
                    }
                    return Menu._emptyArray;
                }
                
                public static Menu parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Menu().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Menu parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Menu(), array);
                }
                
                public final Menu clear() {
                    this.categoryName = null;
                    this.pageIndex = null;
                    this.contentKey = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Menu clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.categoryName != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.categoryName);
                    }
                    if (this.pageIndex != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.pageIndex);
                    }
                    if (this.contentKey != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.contentKey);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Menu mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                this.categoryName = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 16: {
                                this.pageIndex = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 26: {
                                this.contentKey = codedInputByteBufferNano.readString();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.categoryName != null) {
                        codedOutputByteBufferNano.writeString(1, this.categoryName);
                    }
                    if (this.pageIndex != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.pageIndex);
                    }
                    if (this.contentKey != null) {
                        codedOutputByteBufferNano.writeString(3, this.contentKey);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class Preferences extends ExtendableMessageNano<Preferences> implements Cloneable
            {
                private static volatile Preferences[] _emptyArray;
                public Integer comfortModeState;
                public Integer guestMode;
                public Integer humanScaleMode;
                public Integer labelsState;
                public Integer startConfiguration;
                
                public Preferences() {
                    this.clear();
                }
                
                public static Preferences[] emptyArray() {
                    if (Preferences._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Preferences._emptyArray != null) {
                                        break;
                                    }
                                }
                                Preferences._emptyArray = new Preferences[0];
                                continue;
                            }
                        }
                    }
                    return Preferences._emptyArray;
                }
                
                public static Preferences parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Preferences().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Preferences parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Preferences(), array);
                }
                
                public final Preferences clear() {
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Preferences clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.labelsState != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.labelsState);
                    }
                    if (this.comfortModeState != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.comfortModeState);
                    }
                    if (this.startConfiguration != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.startConfiguration);
                    }
                    if (this.guestMode != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.guestMode);
                    }
                    if (this.humanScaleMode != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.humanScaleMode);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Preferences mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.labelsState = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                final int int33 = codedInputByteBufferNano.readInt32();
                                switch (int33) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.comfortModeState = int33;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 24: {
                                final int int34 = codedInputByteBufferNano.readInt32();
                                switch (int34) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.startConfiguration = int34;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 32: {
                                final int int35 = codedInputByteBufferNano.readInt32();
                                switch (int35) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.guestMode = int35;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 40: {
                                final int int36 = codedInputByteBufferNano.readInt32();
                                switch (int36) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.humanScaleMode = int36;
                                        continue;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.labelsState != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.labelsState);
                    }
                    if (this.comfortModeState != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.comfortModeState);
                    }
                    if (this.startConfiguration != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.startConfiguration);
                    }
                    if (this.guestMode != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.guestMode);
                    }
                    if (this.humanScaleMode != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.humanScaleMode);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface ComfortModeState
                {
                    public static final int COMFORT_MODE_DISABLED = 1;
                    public static final int COMFORT_MODE_ENABLED = 2;
                    public static final int COMFORT_MODE_UNKNOWN = 0;
                }
                
                public interface GuestMode
                {
                    public static final int GUEST_MODE_OFF = 1;
                    public static final int GUEST_MODE_ON = 2;
                    public static final int GUEST_MODE_UNKNOWN = 0;
                }
                
                public interface HumanScaleMode
                {
                    public static final int ALLOW_HUMAN_SCALE = 2;
                    public static final int DISALLOW_HUMAN_SCALE = 1;
                    public static final int HUMAN_SCALE_UNKNOWN = 0;
                }
                
                public interface LabelsState
                {
                    public static final int LABELS_DISABLED = 1;
                    public static final int LABELS_ENABLED = 2;
                    public static final int LABELS_UNKNOWN = 0;
                }
                
                public interface MusicMode
                {
                    public static final int MUSIC_OFF = 1;
                    public static final int MUSIC_ON = 2;
                    public static final int MUSIC_UNKNOWN = 0;
                }
                
                public interface StartConfiguration
                {
                    public static final int APP_HAS_BEEN_RUN_BEFORE = 2;
                    public static final int APP_HAS_NEVER_BEEN_RUN = 1;
                    public static final int CONFIGURATION_UNKNOWN = 0;
                }
            }
            
            public static final class SplashScreen extends ExtendableMessageNano<SplashScreen> implements Cloneable
            {
                private static volatile SplashScreen[] _emptyArray;
                public Integer exitType;
                public Long numberOfViewPreloadsAttempted;
                public Long numberOfViewPreloadsSucceeded;
                public Long viewPreloadDurationMs;
                
                public SplashScreen() {
                    this.clear();
                }
                
                public static SplashScreen[] emptyArray() {
                    if (SplashScreen._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (SplashScreen._emptyArray != null) {
                                        break;
                                    }
                                }
                                SplashScreen._emptyArray = new SplashScreen[0];
                                continue;
                            }
                        }
                    }
                    return SplashScreen._emptyArray;
                }
                
                public static SplashScreen parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new SplashScreen().mergeFrom(codedInputByteBufferNano);
                }
                
                public static SplashScreen parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new SplashScreen(), array);
                }
                
                public final SplashScreen clear() {
                    this.numberOfViewPreloadsAttempted = null;
                    this.numberOfViewPreloadsSucceeded = null;
                    this.viewPreloadDurationMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final SplashScreen clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.exitType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.exitType);
                    }
                    if (this.numberOfViewPreloadsAttempted != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.numberOfViewPreloadsAttempted);
                    }
                    if (this.numberOfViewPreloadsSucceeded != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.numberOfViewPreloadsSucceeded);
                    }
                    if (this.viewPreloadDurationMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, this.viewPreloadDurationMs);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final SplashScreen mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1: {
                                        this.exitType = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.numberOfViewPreloadsAttempted = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 24: {
                                this.numberOfViewPreloadsSucceeded = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 32: {
                                this.viewPreloadDurationMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.exitType != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.exitType);
                    }
                    if (this.numberOfViewPreloadsAttempted != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.numberOfViewPreloadsAttempted);
                    }
                    if (this.numberOfViewPreloadsSucceeded != null) {
                        codedOutputByteBufferNano.writeInt64(3, this.numberOfViewPreloadsSucceeded);
                    }
                    if (this.viewPreloadDurationMs != null) {
                        codedOutputByteBufferNano.writeInt64(4, this.viewPreloadDurationMs);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface ExitType
                {
                    public static final int EXIT_TYPE_START_ACTION = 1;
                    public static final int EXIT_TYPE_UNKNOWN = 0;
                }
            }
            
            public static final class Tour extends ExtendableMessageNano<Tour> implements Cloneable
            {
                private static volatile Tour[] _emptyArray;
                public String name;
                public Long playbackMs;
                
                public Tour() {
                    this.clear();
                }
                
                public static Tour[] emptyArray() {
                    if (Tour._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Tour._emptyArray != null) {
                                        break;
                                    }
                                }
                                Tour._emptyArray = new Tour[0];
                                continue;
                            }
                        }
                    }
                    return Tour._emptyArray;
                }
                
                public static Tour parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Tour().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Tour parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Tour(), array);
                }
                
                public final Tour clear() {
                    this.name = null;
                    this.playbackMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Tour clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.name != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.name);
                    }
                    if (this.playbackMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.playbackMs);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Tour mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                this.name = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 16: {
                                this.playbackMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.name != null) {
                        codedOutputByteBufferNano.writeString(1, this.name);
                    }
                    if (this.playbackMs != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.playbackMs);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class Tutorial extends ExtendableMessageNano<Tutorial> implements Cloneable
            {
                private static volatile Tutorial[] _emptyArray;
                public Integer stage;
                public String stageName;
                
                public Tutorial() {
                    this.clear();
                }
                
                public static Tutorial[] emptyArray() {
                    if (Tutorial._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Tutorial._emptyArray != null) {
                                        break;
                                    }
                                }
                                Tutorial._emptyArray = new Tutorial[0];
                                continue;
                            }
                        }
                    }
                    return Tutorial._emptyArray;
                }
                
                public static Tutorial parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Tutorial().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Tutorial parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Tutorial(), array);
                }
                
                public final Tutorial clear() {
                    this.stage = null;
                    this.stageName = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Tutorial clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.stage != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.stage);
                    }
                    if (this.stageName != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.stageName);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Tutorial mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.stage = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 18: {
                                this.stageName = codedInputByteBufferNano.readString();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.stage != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.stage);
                    }
                    if (this.stageName != null) {
                        codedOutputByteBufferNano.writeString(2, this.stageName);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class View extends ExtendableMessageNano<View> implements Cloneable
            {
                private static volatile View[] _emptyArray;
                public Integer mode;
                public Long simulationSecondsSinceEpoch;
                public DoublePrecisionTransform startFromKeyholeTransform;
                
                public View() {
                    this.clear();
                }
                
                public static View[] emptyArray() {
                    if (View._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (View._emptyArray != null) {
                                        break;
                                    }
                                }
                                View._emptyArray = new View[0];
                                continue;
                            }
                        }
                    }
                    return View._emptyArray;
                }
                
                public static View parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new View().mergeFrom(codedInputByteBufferNano);
                }
                
                public static View parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new View(), array);
                }
                
                public final View clear() {
                    this.startFromKeyholeTransform = null;
                    this.simulationSecondsSinceEpoch = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final View clone() {
                    while (true) {
                        View view;
                        try {
                            view = super.clone();
                            if (this.startFromKeyholeTransform == null) {
                                return view;
                            }
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                        view.startFromKeyholeTransform = this.startFromKeyholeTransform.clone();
                        return view;
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.mode != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.mode);
                    }
                    if (this.startFromKeyholeTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromKeyholeTransform);
                    }
                    if (this.simulationSecondsSinceEpoch != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.simulationSecondsSinceEpoch);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final View mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3: {
                                        this.mode = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 18: {
                                if (this.startFromKeyholeTransform == null) {
                                    this.startFromKeyholeTransform = new DoublePrecisionTransform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromKeyholeTransform);
                                continue;
                            }
                            case 24: {
                                this.simulationSecondsSinceEpoch = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.mode != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.mode);
                    }
                    if (this.startFromKeyholeTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromKeyholeTransform);
                    }
                    if (this.simulationSecondsSinceEpoch != null) {
                        codedOutputByteBufferNano.writeInt64(3, this.simulationSecondsSinceEpoch);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Mode
                {
                    public static final int PLANET = 1;
                    public static final int TERRAIN = 2;
                    public static final int TRANSITION = 3;
                    public static final int UNKNOWN = 0;
                }
            }
        }
        
        public static final class EmbedVrWidget extends ExtendableMessageNano<EmbedVrWidget> implements Cloneable
        {
            private static volatile EmbedVrWidget[] _emptyArray;
            public String errorMsg;
            public Pano pano;
            public Video video;
            public Integer viewMode;
            
            public EmbedVrWidget() {
                this.clear();
            }
            
            public static EmbedVrWidget[] emptyArray() {
                if (EmbedVrWidget._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (EmbedVrWidget._emptyArray != null) {
                                    break;
                                }
                            }
                            EmbedVrWidget._emptyArray = new EmbedVrWidget[0];
                            continue;
                        }
                    }
                }
                return EmbedVrWidget._emptyArray;
            }
            
            public static EmbedVrWidget parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new EmbedVrWidget().mergeFrom(codedInputByteBufferNano);
            }
            
            public static EmbedVrWidget parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new EmbedVrWidget(), array);
            }
            
            public final EmbedVrWidget clear() {
                this.pano = null;
                this.video = null;
                this.errorMsg = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final EmbedVrWidget clone() {
                while (true) {
                    EmbedVrWidget embedVrWidget = null;
                Label_0048:
                    while (true) {
                        try {
                            embedVrWidget = super.clone();
                            if (this.pano == null) {
                                if (this.video == null) {
                                    return embedVrWidget;
                                }
                                break Label_0048;
                            }
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                        embedVrWidget.pano = this.pano.clone();
                        continue;
                    }
                    embedVrWidget.video = this.video.clone();
                    return embedVrWidget;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.viewMode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.viewMode);
                }
                if (this.pano != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.pano);
                }
                if (this.video != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.video);
                }
                if (this.errorMsg != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.errorMsg);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final EmbedVrWidget mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3: {
                                    this.viewMode = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 18: {
                            if (this.pano == null) {
                                this.pano = new Pano();
                            }
                            codedInputByteBufferNano.readMessage(this.pano);
                            continue;
                        }
                        case 26: {
                            if (this.video == null) {
                                this.video = new Video();
                            }
                            codedInputByteBufferNano.readMessage(this.video);
                            continue;
                        }
                        case 34: {
                            this.errorMsg = codedInputByteBufferNano.readString();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.viewMode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.viewMode);
                }
                if (this.pano != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.pano);
                }
                if (this.video != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.video);
                }
                if (this.errorMsg != null) {
                    codedOutputByteBufferNano.writeString(4, this.errorMsg);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class Pano extends ExtendableMessageNano<Pano> implements Cloneable
            {
                private static volatile Pano[] _emptyArray;
                public Integer heightPixels;
                public Integer stereoFormat;
                public Integer widthPixels;
                
                public Pano() {
                    this.clear();
                }
                
                public static Pano[] emptyArray() {
                    if (Pano._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Pano._emptyArray != null) {
                                        break;
                                    }
                                }
                                Pano._emptyArray = new Pano[0];
                                continue;
                            }
                        }
                    }
                    return Pano._emptyArray;
                }
                
                public static Pano parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Pano().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Pano parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Pano(), array);
                }
                
                public final Pano clear() {
                    this.widthPixels = null;
                    this.heightPixels = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Pano clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.widthPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.widthPixels);
                    }
                    if (this.heightPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.heightPixels);
                    }
                    if (this.stereoFormat != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.stereoFormat);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Pano mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.widthPixels = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 16: {
                                this.heightPixels = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 24: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.stereoFormat = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.widthPixels != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.widthPixels);
                    }
                    if (this.heightPixels != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.heightPixels);
                    }
                    if (this.stereoFormat != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.stereoFormat);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public interface StereoFormat
            {
                public static final int MONO = 1;
                public static final int STEREO_OVER_UNDER = 2;
                public static final int UNKNOWN_FORMAT = 0;
            }
            
            public static final class Video extends ExtendableMessageNano<Video> implements Cloneable
            {
                private static volatile Video[] _emptyArray;
                public Integer heightPixels;
                public Integer stereoFormat;
                public Integer videoDurationMs;
                public Integer widthPixels;
                
                public Video() {
                    this.clear();
                }
                
                public static Video[] emptyArray() {
                    if (Video._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Video._emptyArray != null) {
                                        break;
                                    }
                                }
                                Video._emptyArray = new Video[0];
                                continue;
                            }
                        }
                    }
                    return Video._emptyArray;
                }
                
                public static Video parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Video().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Video parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Video(), array);
                }
                
                public final Video clear() {
                    this.widthPixels = null;
                    this.heightPixels = null;
                    this.videoDurationMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Video clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.widthPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.widthPixels);
                    }
                    if (this.heightPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.heightPixels);
                    }
                    if (this.stereoFormat != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.stereoFormat);
                    }
                    if (this.videoDurationMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.videoDurationMs);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Video mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.widthPixels = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 16: {
                                this.heightPixels = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 24: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.stereoFormat = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 32: {
                                this.videoDurationMs = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.widthPixels != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.widthPixels);
                    }
                    if (this.heightPixels != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.heightPixels);
                    }
                    if (this.stereoFormat != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.stereoFormat);
                    }
                    if (this.videoDurationMs != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.videoDurationMs);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public interface ViewMode
            {
                public static final int EMBEDDED = 1;
                public static final int FULLSCREEN_MONO = 2;
                public static final int FULLSCREEN_VR = 3;
                public static final int UNKNOWN_MODE = 0;
            }
        }
        
        public interface EventType
        {
            public static final int AUDIO_INITIALIZATION = 4000;
            public static final int AUDIO_PERFORMANCE_REPORT = 4002;
            public static final int AUDIO_SHUTDOWN = 4001;
            public static final int CYCLOPS_ACCOUNT_SWITCH = 1011;
            public static final int CYCLOPS_APPLICATION = 1000;
            public static final int CYCLOPS_CAPTURE = 1006;
            public static final int CYCLOPS_DELETE = 1003;
            public static final int CYCLOPS_FEEDBACK = 1012;
            public static final int CYCLOPS_GALLERY_CONTEXT_MENU = 1007;
            public static final int CYCLOPS_GALLERY_TO_CAPTURE = 1009;
            public static final int CYCLOPS_GALLERY_TO_GALLERY_HMD = 1008;
            public static final int CYCLOPS_RECEIVE = 1002;
            public static final int CYCLOPS_SETTINGS = 1010;
            public static final int CYCLOPS_SHARE = 1001;
            public static final int CYCLOPS_SHARE_START = 1013;
            public static final int CYCLOPS_VIEW = 1004;
            public static final int CYCLOPS_VIEW_HMD = 1005;
            public static final int EARTHVR_APP_ENVIRONMENT_CHANGED = 8006;
            public static final int EARTHVR_APP_IDLE = 8002;
            public static final int EARTHVR_APP_INITIALIZED = 8007;
            public static final int EARTHVR_APP_MODE_ENTERED = 8003;
            public static final int EARTHVR_APP_MODE_EXITED = 8004;
            public static final int EARTHVR_APP_PREFERENCES_CHANGED = 8005;
            public static final int EARTHVR_APP_STARTED = 8000;
            public static final int EARTHVR_APP_STOPPED = 8001;
            public static final int EARTHVR_DESKTOP_WINDOW_MENU_ITEM_SELECTED = 8308;
            public static final int EARTHVR_MENU_CARD_CLICKED = 8306;
            public static final int EARTHVR_MENU_CATEGORY_SELECTED = 8304;
            public static final int EARTHVR_MENU_CLOSED = 8301;
            public static final int EARTHVR_MENU_OPENED = 8300;
            public static final int EARTHVR_MENU_PAGE_CHANGED = 8305;
            public static final int EARTHVR_MENU_PLACE_DELETION_REQUESTED = 8307;
            public static final int EARTHVR_MENU_PREFERENCES_CLOSED = 8303;
            public static final int EARTHVR_MENU_PREFERENCES_OPENED = 8302;
            public static final int EARTHVR_MINIGLOBE_BECAME_LARGE = 8503;
            public static final int EARTHVR_MINIGLOBE_BECAME_SMALL = 8504;
            public static final int EARTHVR_MINIGLOBE_ROTATED = 8505;
            public static final int EARTHVR_MINIGLOBE_TELEPORT_TRIGGERED = 8506;
            public static final int EARTHVR_NAVIGATION_ARCBALL_STARTED = 8100;
            public static final int EARTHVR_NAVIGATION_ARCBALL_STOPPED = 8102;
            public static final int EARTHVR_NAVIGATION_ARCBALL_UPDATED = 8101;
            public static final int EARTHVR_NAVIGATION_FLYING_STARTED = 8103;
            public static final int EARTHVR_NAVIGATION_FLYING_STOPPED = 8105;
            public static final int EARTHVR_NAVIGATION_FLYING_UPDATED = 8104;
            public static final int EARTHVR_NAVIGATION_ROTATING_STARTED = 8109;
            public static final int EARTHVR_NAVIGATION_ROTATING_STOPPED = 8111;
            public static final int EARTHVR_NAVIGATION_ROTATING_UPDATED = 8110;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_STARTED = 8106;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_STOPPED = 8108;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_UPDATED = 8107;
            public static final int EARTHVR_NAVIGATION_TRANSITION_STARTED = 8150;
            public static final int EARTHVR_NAVIGATION_TRANSITION_STOPPED = 8151;
            public static final int EARTHVR_PLACE_SAVED = 8500;
            public static final int EARTHVR_RENDERING_TUNNEL_VISION_APPEARED = 8200;
            public static final int EARTHVR_RENDERING_TUNNEL_VISION_DISAPPEARED = 8201;
            public static final int EARTHVR_REVEAL_QUERY_RESULT_READY = 8502;
            public static final int EARTHVR_REVEAL_QUERY_STARTED = 8501;
            public static final int EARTHVR_SPLASH_SCREEN_EXITED = 8405;
            public static final int EARTHVR_TOUR_COMPLETED = 8402;
            public static final int EARTHVR_TOUR_PAUSED = 8400;
            public static final int EARTHVR_TOUR_RESUMED = 8401;
            public static final int EARTHVR_TOUR_STARTED = 8404;
            public static final int EARTHVR_TUTORIAL_STAGE_CHANGED = 8403;
            public static final int EMBEDVR_LOAD_ERROR = 6003;
            public static final int EMBEDVR_LOAD_SUCCESS = 6002;
            public static final int EMBEDVR_PERFORMANCE_REPORT = 6008;
            public static final int EMBEDVR_START_SESSION = 6000;
            public static final int EMBEDVR_STOP_SESSION = 6001;
            public static final int EMBEDVR_VIDEO_PAUSE = 6006;
            public static final int EMBEDVR_VIDEO_PLAY = 6005;
            public static final int EMBEDVR_VIDEO_SEEK_TO = 6007;
            public static final int EMBEDVR_VIEW_CLICK = 6004;
            public static final int FEATURED_APPS_TAB = 23;
            public static final int GET_INSTALLED_APPLICATIONS = 2;
            public static final int GO_TO_STORE = 5;
            public static final int KEYBOARD_EVENT = 9000;
            public static final int LAUNCHER_START_APPLICATION = 3001;
            public static final int LAUNCHER_STOP_APPLICATION = 3002;
            public static final int LULLABY_ACTION = 5003;
            public static final int LULLABY_IMPRESSION = 5002;
            public static final int LULLABY_LOAD = 5004;
            public static final int LULLABY_MUTE = 5000;
            public static final int LULLABY_UNMUTE = 5001;
            public static final int MY_APPS_TAB = 22;
            public static final int NAV_ITEM_SELECTED = 24;
            public static final int PHOTOS_ACCOUNT_CHANGE = 11020;
            public static final int PHOTOS_ACCOUNT_INVALID = 11021;
            public static final int PHOTOS_APPLICATION = 11000;
            public static final int PHOTOS_BACKUP_CARD_DISMISS = 11051;
            public static final int PHOTOS_BACKUP_CARD_SHOWN = 11050;
            public static final int PHOTOS_BACKUP_CARD_SUCCESS = 11052;
            public static final int PHOTOS_CAROUSEL_NEXT = 11031;
            public static final int PHOTOS_CAROUSEL_PREV = 11032;
            public static final int PHOTOS_CC_CARD_DISMISS = 11041;
            public static final int PHOTOS_CC_CARD_SHOWN = 11040;
            public static final int PHOTOS_CC_CARD_SUCCESS = 11042;
            public static final int PHOTOS_GALLERY_NEXT = 11010;
            public static final int PHOTOS_GALLERY_PREV = 11011;
            public static final int PHOTOS_OPEN_MEDIA = 11030;
            public static final int PHOTOS_SIGN_CARD_SHOWN = 11060;
            public static final int PHOTOS_WARM_WELCOME_SHOWN = 11061;
            public static final int SDK_PERFORMANCE_REPORT = 2003;
            public static final int SDK_SCANLINE_RACING_ENABLED = 2007;
            public static final int SDK_SCANLINE_RACING_VSYNC_OVERSHOOT_FATAL = 2008;
            public static final int SDK_SENSOR_REPORT = 2004;
            public static final int SDK_SENSOR_STALL = 2009;
            public static final int SETTINGS_QR_CODE_HELP = 21;
            public static final int SETTINGS_QR_CODE_SCAN = 9;
            public static final int SETTINGS_QR_CODE_SCAN_SKIP = 10;
            public static final int SETUP_HEAD_MOUNT_INSERTED = 17;
            public static final int SETUP_HEAD_MOUNT_SWITCH = 18;
            public static final int SETUP_PAIRED_NEXT = 16;
            public static final int SETUP_QR_CODE_HELP = 15;
            public static final int SETUP_QR_CODE_SCAN = 8;
            public static final int SETUP_QR_CODE_SCAN_SKIP = 7;
            public static final int SETUP_VR_INTRO_START = 19;
            public static final int SETUP_VR_INTRO_STOP = 20;
            public static final int SETUP_WELCOME_GET_VIEWER = 12;
            public static final int SETUP_WELCOME_NEXT = 11;
            public static final int SETUP_WELCOME_SWITCH_VIEWER = 13;
            public static final int SETUP_WELCOME_USE_VIEWER = 14;
            public static final int START_APPLICATION = 3;
            public static final int START_LAUNCHER = 1;
            public static final int START_SDK_APPLICATION = 2000;
            public static final int START_VR_APPLICATION = 2005;
            public static final int START_VR_LAUNCHER_COLD = 3000;
            public static final int STOP_APPLICATION = 4;
            public static final int STOP_VR_APPLICATION = 2006;
            public static final int STREET_VIEW_APP_BUTTON = 10010;
            public static final int STREET_VIEW_COLLECTION = 10000;
            public static final int STREET_VIEW_NO_KEYBOARD = 10003;
            public static final int STREET_VIEW_PANO_IN_COLLECTION = 10001;
            public static final int STREET_VIEW_PANO_IN_SEARCH_RESULTS = 10002;
            public static final int STREET_VIEW_PANO_NAV_SESSION = 10009;
            public static final int STREET_VIEW_SEARCH_DISMISS = 10007;
            public static final int STREET_VIEW_SEARCH_EDIT = 10008;
            public static final int STREET_VIEW_SEARCH_NO_PANOS = 10011;
            public static final int STREET_VIEW_SEARCH_NO_RESULTS = 10006;
            public static final int STREET_VIEW_SEARCH_RESULTS = 10005;
            public static final int STREET_VIEW_SEARCH_START = 10004;
            public static final int SWITCH_HEAD_MOUNT = 6;
            public static final int TRANSITION_HEAD_MOUNT_INSERTED = 2001;
            public static final int TRANSITION_HEAD_MOUNT_SWITCH = 2002;
            public static final int UNKNOWN_EVENT_TYPE = 0;
            public static final int VRCORE_COMMON_CONTINUE_VRMODE = 7002;
            public static final int VRCORE_COMMON_DISABLE_VRMODE = 7001;
            public static final int VRCORE_COMMON_ENABLE_VRMODE = 7000;
            public static final int VRCORE_COMMON_ERROR = 7049;
            public static final int VRCORE_COMMON_PERMISSION_DENIED = 7204;
            public static final int VRCORE_COMMON_PERMISSION_GRANTED = 7203;
            public static final int VRCORE_CONTROLLER_CONNECTED = 7050;
            public static final int VRCORE_CONTROLLER_EMULATOR_CONNECTED = 7065;
            public static final int VRCORE_CONTROLLER_EMULATOR_ERROR = 7066;
            public static final int VRCORE_CONTROLLER_ERROR = 7051;
            public static final int VRCORE_CONTROLLER_OTA_DATA_TRANSFER_SUCCESS = 7056;
            public static final int VRCORE_CONTROLLER_OTA_ERROR = 7052;
            public static final int VRCORE_CONTROLLER_OTA_STARTED = 7053;
            public static final int VRCORE_CONTROLLER_OTA_SUCCESS = 7054;
            public static final int VRCORE_CONTROLLER_OTA_SUCCESS_TRIVIAL = 7055;
            public static final int VRCORE_CONTROLLER_PAIRING_ERROR = 7057;
            public static final int VRCORE_CONTROLLER_PAIRING_STARTED = 7058;
            public static final int VRCORE_CONTROLLER_PAIRING_SUCCESS = 7059;
            public static final int VRCORE_CONTROLLER_RAIL_REPORT = 7067;
            public static final int VRCORE_CONTROLLER_RECALIBRATED = 7060;
            public static final int VRCORE_CONTROLLER_RECALIBRATION_ERROR = 7061;
            public static final int VRCORE_CONTROLLER_SLEPT_END = 7062;
            public static final int VRCORE_CONTROLLER_SLEPT_IDLE = 7063;
            public static final int VRCORE_CONTROLLER_STUCK_EXITED_VR = 7069;
            public static final int VRCORE_CONTROLLER_STUCK_NOTIFICATION_SHOWN = 7068;
            public static final int VRCORE_CONTROLLER_STUCK_NOTIFICATION_TAPPED = 7070;
            public static final int VRCORE_CONTROLLER_VOLUME_ADJUSTED = 7064;
            public static final int VRCORE_DAYDREAM_DON_ERROR = 7252;
            public static final int VRCORE_DAYDREAM_DON_STARTED = 7250;
            public static final int VRCORE_DAYDREAM_DON_SUCCESS = 7251;
            public static final int VRCORE_DAYDREAM_HOME_LAUNCHED = 7255;
            public static final int VRCORE_DAYDREAM_METAWORLD_DISMISSED = 7257;
            public static final int VRCORE_DAYDREAM_METAWORLD_STARTED = 7256;
            public static final int VRCORE_DAYDREAM_SESSION_ENDED = 7254;
            public static final int VRCORE_DAYDREAM_SESSION_STARTED = 7253;
            public static final int VRCORE_DAYDREAM_SYSTEM_UPDATE_ACCEPTED = 7258;
            public static final int VRCORE_DAYDREAM_SYSTEM_UPDATE_DECLINED = 7259;
            public static final int VRCORE_NFC_ERROR = 7149;
            public static final int VRCORE_NFC_TRIGGER_INTENT = 7100;
            public static final int VRCORE_NOTIFICATION_ERROR = 7199;
            public static final int VRCORE_NOTIFICATION_POSTED = 7150;
            public static final int VRCORE_NOTIFICATION_REMOVED = 7151;
            public static final int VRCORE_PERFORMANCE_REPORT = 7999;
            public static final int VRCORE_SETTINGS_DISABLE_NOTIFICATION = 7202;
            public static final int VRCORE_SETTINGS_ENABLE_NOTIFICATION = 7201;
            public static final int VRCORE_SETTINGS_ERROR = 7249;
            public static final int VRCORE_SETTINGS_LAUNCHED = 7200;
            public static final int VRHOME_SETUP_STEP_END = 12001;
            public static final int VRHOME_SETUP_STEP_START = 12000;
            public static final int VRHOME_SETUP_STEP_STATE_CHANGE = 12002;
        }
        
        public static final class HeadMount extends ExtendableMessageNano<HeadMount> implements Cloneable
        {
            private static volatile HeadMount[] _emptyArray;
            public String model;
            public String vendor;
            
            public HeadMount() {
                this.clear();
            }
            
            public static HeadMount[] emptyArray() {
                if (HeadMount._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (HeadMount._emptyArray != null) {
                                    break;
                                }
                            }
                            HeadMount._emptyArray = new HeadMount[0];
                            continue;
                        }
                    }
                }
                return HeadMount._emptyArray;
            }
            
            public static HeadMount parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HeadMount().mergeFrom(codedInputByteBufferNano);
            }
            
            public static HeadMount parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new HeadMount(), array);
            }
            
            public final HeadMount clear() {
                this.vendor = null;
                this.model = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final HeadMount clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.vendor != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.vendor);
                }
                if (this.model != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.model);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final HeadMount mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            this.vendor = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 18: {
                            this.model = codedInputByteBufferNano.readString();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.vendor != null) {
                    codedOutputByteBufferNano.writeString(1, this.vendor);
                }
                if (this.model != null) {
                    codedOutputByteBufferNano.writeString(2, this.model);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class HistogramBucket extends ExtendableMessageNano<HistogramBucket> implements Cloneable
        {
            private static volatile HistogramBucket[] _emptyArray;
            public Integer count;
            public Integer minimumValue;
            
            public HistogramBucket() {
                this.clear();
            }
            
            public static HistogramBucket[] emptyArray() {
                if (HistogramBucket._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (HistogramBucket._emptyArray != null) {
                                    break;
                                }
                            }
                            HistogramBucket._emptyArray = new HistogramBucket[0];
                            continue;
                        }
                    }
                }
                return HistogramBucket._emptyArray;
            }
            
            public static HistogramBucket parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
            
            public static HistogramBucket parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new HistogramBucket(), array);
            }
            
            public final HistogramBucket clear() {
                this.minimumValue = null;
                this.count = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final HistogramBucket clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.minimumValue != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.minimumValue);
                }
                if (this.count != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.count);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final HistogramBucket mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            this.minimumValue = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 16: {
                            this.count = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.minimumValue != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.minimumValue);
                }
                if (this.count != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.count);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class Keyboard extends ExtendableMessageNano<Keyboard> implements Cloneable
        {
            private static volatile Keyboard[] _emptyArray;
            public KeyboardEvent[] keyboardEvents;
            
            public Keyboard() {
                this.clear();
            }
            
            public static Keyboard[] emptyArray() {
                if (Keyboard._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Keyboard._emptyArray != null) {
                                    break;
                                }
                            }
                            Keyboard._emptyArray = new Keyboard[0];
                            continue;
                        }
                    }
                }
                return Keyboard._emptyArray;
            }
            
            public static Keyboard parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Keyboard().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Keyboard parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Keyboard(), array);
            }
            
            public final Keyboard clear() {
                this.keyboardEvents = KeyboardEvent.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Keyboard clone() {
                while (true) {
                    int i = 0;
                    Keyboard keyboard;
                    try {
                        keyboard = super.clone();
                        if (this.keyboardEvents == null) {
                            return keyboard;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    if (this.keyboardEvents.length > 0) {
                        keyboard.keyboardEvents = new KeyboardEvent[this.keyboardEvents.length];
                        while (i < this.keyboardEvents.length) {
                            if (this.keyboardEvents[i] != null) {
                                keyboard.keyboardEvents[i] = this.keyboardEvents[i].clone();
                            }
                            ++i;
                        }
                        return keyboard;
                    }
                    return keyboard;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int n = 0;
                int computeSerializedSize = super.computeSerializedSize();
                int n2;
                if (this.keyboardEvents == null) {
                    n2 = computeSerializedSize;
                }
                else {
                    n2 = computeSerializedSize;
                    if (this.keyboardEvents.length > 0) {
                        while (true) {
                            n2 = computeSerializedSize;
                            if (n >= this.keyboardEvents.length) {
                                break;
                            }
                            final KeyboardEvent keyboardEvent = this.keyboardEvents[n];
                            if (keyboardEvent != null) {
                                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, keyboardEvent);
                            }
                            ++n;
                        }
                    }
                }
                return n2;
            }
            
            @Override
            public final Keyboard mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 18: {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int i;
                            if (this.keyboardEvents != null) {
                                i = this.keyboardEvents.length;
                            }
                            else {
                                i = 0;
                            }
                            final KeyboardEvent[] keyboardEvents = new KeyboardEvent[repeatedFieldArrayLength + i];
                            if (i != 0) {
                                System.arraycopy(this.keyboardEvents, 0, keyboardEvents, 0, i);
                            }
                            while (i < keyboardEvents.length - 1) {
                                codedInputByteBufferNano.readMessage(keyboardEvents[i] = new KeyboardEvent());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(keyboardEvents[i] = new KeyboardEvent());
                            this.keyboardEvents = keyboardEvents;
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = 0;
                if (this.keyboardEvents != null && this.keyboardEvents.length > 0) {
                    while (i < this.keyboardEvents.length) {
                        final KeyboardEvent keyboardEvent = this.keyboardEvents[i];
                        if (keyboardEvent != null) {
                            codedOutputByteBufferNano.writeMessage(2, keyboardEvent);
                        }
                        ++i;
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class KeyboardEvent extends ExtendableMessageNano<KeyboardEvent> implements Cloneable
            {
                private static volatile KeyboardEvent[] _emptyArray;
                public Long clientTimestamp;
                public String[] enabledLanguages;
                public Integer eventType;
                public Integer inputType;
                public Application keyboardService;
                public String language;
                public String layout;
                public String[] systemLanguages;
                public KeyboardTextEntry textEntry;
                
                public KeyboardEvent() {
                    this.clear();
                }
                
                public static KeyboardEvent[] emptyArray() {
                    if (KeyboardEvent._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (KeyboardEvent._emptyArray != null) {
                                        break;
                                    }
                                }
                                KeyboardEvent._emptyArray = new KeyboardEvent[0];
                                continue;
                            }
                        }
                    }
                    return KeyboardEvent._emptyArray;
                }
                
                public static KeyboardEvent parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new KeyboardEvent().mergeFrom(codedInputByteBufferNano);
                }
                
                public static KeyboardEvent parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new KeyboardEvent(), array);
                }
                
                public final KeyboardEvent clear() {
                    this.clientTimestamp = null;
                    this.textEntry = null;
                    this.keyboardService = null;
                    this.systemLanguages = WireFormatNano.EMPTY_STRING_ARRAY;
                    this.enabledLanguages = WireFormatNano.EMPTY_STRING_ARRAY;
                    this.language = null;
                    this.layout = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final KeyboardEvent clone() {
                    // 
                    // This method could not be decompiled.
                    // 
                    // Original Bytecode:
                    // 
                    //     1: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                    //     4: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent;
                    //     7: astore_1       
                    //     8: aload_0        
                    //     9: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.textEntry:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardTextEntry;
                    //    12: ifnonnull       48
                    //    15: aload_0        
                    //    16: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.keyboardService:Lcom/google/common/logging/nano/Vr$VREvent$Application;
                    //    19: ifnonnull       62
                    //    22: aload_0        
                    //    23: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.systemLanguages:[Ljava/lang/String;
                    //    26: ifnonnull       76
                    //    29: aload_0        
                    //    30: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.enabledLanguages:[Ljava/lang/String;
                    //    33: ifnonnull       101
                    //    36: aload_1        
                    //    37: areturn        
                    //    38: astore_1       
                    //    39: new             Ljava/lang/AssertionError;
                    //    42: dup            
                    //    43: aload_1        
                    //    44: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                    //    47: athrow         
                    //    48: aload_1        
                    //    49: aload_0        
                    //    50: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.textEntry:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardTextEntry;
                    //    53: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardTextEntry.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardTextEntry;
                    //    56: putfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.textEntry:Lcom/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardTextEntry;
                    //    59: goto            15
                    //    62: aload_1        
                    //    63: aload_0        
                    //    64: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.keyboardService:Lcom/google/common/logging/nano/Vr$VREvent$Application;
                    //    67: invokevirtual   com/google/common/logging/nano/Vr$VREvent$Application.clone:()Lcom/google/common/logging/nano/Vr$VREvent$Application;
                    //    70: putfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.keyboardService:Lcom/google/common/logging/nano/Vr$VREvent$Application;
                    //    73: goto            22
                    //    76: aload_0        
                    //    77: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.systemLanguages:[Ljava/lang/String;
                    //    80: arraylength    
                    //    81: ifle            29
                    //    84: aload_1        
                    //    85: aload_0        
                    //    86: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.systemLanguages:[Ljava/lang/String;
                    //    89: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                    //    92: checkcast       [Ljava/lang/String;
                    //    95: putfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.systemLanguages:[Ljava/lang/String;
                    //    98: goto            29
                    //   101: aload_0        
                    //   102: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.enabledLanguages:[Ljava/lang/String;
                    //   105: arraylength    
                    //   106: ifle            36
                    //   109: aload_1        
                    //   110: aload_0        
                    //   111: getfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.enabledLanguages:[Ljava/lang/String;
                    //   114: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                    //   117: checkcast       [Ljava/lang/String;
                    //   120: putfield        com/google/common/logging/nano/Vr$VREvent$Keyboard$KeyboardEvent.enabledLanguages:[Ljava/lang/String;
                    //   123: goto            36
                    //    Exceptions:
                    //  Try           Handler
                    //  Start  End    Start  End    Type                                  
                    //  -----  -----  -----  -----  --------------------------------------
                    //  0      8      38     48     Ljava/lang/CloneNotSupportedException;
                    // 
                    // The error that occurred was:
                    // 
                    // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:String[](KeyboardEvent::systemLanguages, this:KeyboardEvent)), ldc:int(0))
                    //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                    //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                    //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
                
                @Override
                protected final int computeSerializedSize() {
                    final int n = 0;
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.clientTimestamp != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.clientTimestamp);
                    }
                    if (this.eventType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.eventType);
                    }
                    if (this.textEntry != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.textEntry);
                    }
                    if (this.keyboardService != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.keyboardService);
                    }
                    int n2;
                    if (this.systemLanguages == null) {
                        n2 = computeSerializedSize;
                    }
                    else {
                        n2 = computeSerializedSize;
                        if (this.systemLanguages.length > 0) {
                            int i = 0;
                            int n3 = 0;
                            int n4 = 0;
                            while (i < this.systemLanguages.length) {
                                final String s = this.systemLanguages[i];
                                if (s != null) {
                                    ++n4;
                                    n3 += CodedOutputByteBufferNano.computeStringSizeNoTag(s);
                                }
                                ++i;
                            }
                            n2 = computeSerializedSize + n3 + n4 * 1;
                        }
                    }
                    int n5;
                    if (this.enabledLanguages == null) {
                        n5 = n2;
                    }
                    else {
                        n5 = n2;
                        if (this.enabledLanguages.length > 0) {
                            int n6 = 0;
                            int n7 = 0;
                            for (int j = n; j < this.enabledLanguages.length; ++j) {
                                final String s2 = this.enabledLanguages[j];
                                if (s2 != null) {
                                    ++n7;
                                    n6 += CodedOutputByteBufferNano.computeStringSizeNoTag(s2);
                                }
                            }
                            n5 = n2 + n6 + n7 * 1;
                        }
                    }
                    if (this.language != null) {
                        n5 += CodedOutputByteBufferNano.computeStringSize(7, this.language);
                    }
                    if (this.inputType != null) {
                        n5 += CodedOutputByteBufferNano.computeInt32Size(8, this.inputType);
                    }
                    if (this.layout != null) {
                        n5 += CodedOutputByteBufferNano.computeStringSize(9, this.layout);
                    }
                    return n5;
                }
                
                @Override
                public final KeyboardEvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.clientTimestamp = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 16: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 1000:
                                    case 1001:
                                    case 2000: {
                                        this.eventType = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 26: {
                                if (this.textEntry == null) {
                                    this.textEntry = new KeyboardTextEntry();
                                }
                                codedInputByteBufferNano.readMessage(this.textEntry);
                                continue;
                            }
                            case 34: {
                                if (this.keyboardService == null) {
                                    this.keyboardService = new Application();
                                }
                                codedInputByteBufferNano.readMessage(this.keyboardService);
                                continue;
                            }
                            case 42: {
                                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                                int i;
                                if (this.systemLanguages != null) {
                                    i = this.systemLanguages.length;
                                }
                                else {
                                    i = 0;
                                }
                                final String[] systemLanguages = new String[repeatedFieldArrayLength + i];
                                if (i != 0) {
                                    System.arraycopy(this.systemLanguages, 0, systemLanguages, 0, i);
                                }
                                while (i < systemLanguages.length - 1) {
                                    systemLanguages[i] = codedInputByteBufferNano.readString();
                                    codedInputByteBufferNano.readTag();
                                    ++i;
                                }
                                systemLanguages[i] = codedInputByteBufferNano.readString();
                                this.systemLanguages = systemLanguages;
                                continue;
                            }
                            case 50: {
                                final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                                int j;
                                if (this.enabledLanguages != null) {
                                    j = this.enabledLanguages.length;
                                }
                                else {
                                    j = 0;
                                }
                                final String[] enabledLanguages = new String[repeatedFieldArrayLength2 + j];
                                if (j != 0) {
                                    System.arraycopy(this.enabledLanguages, 0, enabledLanguages, 0, j);
                                }
                                while (j < enabledLanguages.length - 1) {
                                    enabledLanguages[j] = codedInputByteBufferNano.readString();
                                    codedInputByteBufferNano.readTag();
                                    ++j;
                                }
                                enabledLanguages[j] = codedInputByteBufferNano.readString();
                                this.enabledLanguages = enabledLanguages;
                                continue;
                            }
                            case 58: {
                                this.language = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 64: {
                                final int int33 = codedInputByteBufferNano.readInt32();
                                switch (int33) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1: {
                                        this.inputType = int33;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 74: {
                                this.layout = codedInputByteBufferNano.readString();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    final int n = 0;
                    if (this.clientTimestamp != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.clientTimestamp);
                    }
                    if (this.eventType != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.eventType);
                    }
                    if (this.textEntry != null) {
                        codedOutputByteBufferNano.writeMessage(3, this.textEntry);
                    }
                    if (this.keyboardService != null) {
                        codedOutputByteBufferNano.writeMessage(4, this.keyboardService);
                    }
                    if (this.systemLanguages != null && this.systemLanguages.length > 0) {
                        for (int i = 0; i < this.systemLanguages.length; ++i) {
                            final String s = this.systemLanguages[i];
                            if (s != null) {
                                codedOutputByteBufferNano.writeString(5, s);
                            }
                        }
                    }
                    if (this.enabledLanguages != null && this.enabledLanguages.length > 0) {
                        for (int j = n; j < this.enabledLanguages.length; ++j) {
                            final String s2 = this.enabledLanguages[j];
                            if (s2 != null) {
                                codedOutputByteBufferNano.writeString(6, s2);
                            }
                        }
                    }
                    if (this.language != null) {
                        codedOutputByteBufferNano.writeString(7, this.language);
                    }
                    if (this.inputType != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.inputType);
                    }
                    if (this.layout != null) {
                        codedOutputByteBufferNano.writeString(9, this.layout);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public interface KeyboardEventType
            {
                public static final int KEYBOARD_ERROR_NOT_INSTALLED = 1000;
                public static final int KEYBOARD_ERROR_NO_LOCALES = 1001;
                public static final int KEYBOARD_EVENT_CONNECTED = 2000;
                public static final int KEYBOARD_EVENT_UNKNOWN = 0;
                public static final int KEYBOARD_USER_EVENT_CHANGE_LANGUAGE = 7;
                public static final int KEYBOARD_USER_EVENT_CHANGE_LAYOUT = 8;
                public static final int KEYBOARD_USER_EVENT_COMMIT = 3;
                public static final int KEYBOARD_USER_EVENT_DELETE = 4;
                public static final int KEYBOARD_USER_EVENT_DISMISS = 6;
                public static final int KEYBOARD_USER_EVENT_HIDE = 2;
                public static final int KEYBOARD_USER_EVENT_RETURN = 5;
                public static final int KEYBOARD_USER_EVENT_SHOW = 1;
            }
            
            public interface KeyboardInputType
            {
                public static final int KEYBOARD_INPUT_TYPE_DEFAULT = 1;
                public static final int KEYBOARD_INPUT_TYPE_UNKNOWN = 0;
            }
            
            public static final class KeyboardTextEntry extends ExtendableMessageNano<KeyboardTextEntry> implements Cloneable
            {
                private static volatile KeyboardTextEntry[] _emptyArray;
                public String language;
                public String layout;
                public Integer length;
                public Integer type;
                
                public KeyboardTextEntry() {
                    this.clear();
                }
                
                public static KeyboardTextEntry[] emptyArray() {
                    if (KeyboardTextEntry._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (KeyboardTextEntry._emptyArray != null) {
                                        break;
                                    }
                                }
                                KeyboardTextEntry._emptyArray = new KeyboardTextEntry[0];
                                continue;
                            }
                        }
                    }
                    return KeyboardTextEntry._emptyArray;
                }
                
                public static KeyboardTextEntry parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new KeyboardTextEntry().mergeFrom(codedInputByteBufferNano);
                }
                
                public static KeyboardTextEntry parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new KeyboardTextEntry(), array);
                }
                
                public final KeyboardTextEntry clear() {
                    this.length = null;
                    this.layout = null;
                    this.language = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final KeyboardTextEntry clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
                    }
                    if (this.length != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.length);
                    }
                    if (this.layout != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.layout);
                    }
                    if (this.language != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.language);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final KeyboardTextEntry mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 4:
                                    case 5: {
                                        this.type = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.length = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 26: {
                                this.layout = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 34: {
                                this.language = codedInputByteBufferNano.readString();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type);
                    }
                    if (this.length != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.length);
                    }
                    if (this.layout != null) {
                        codedOutputByteBufferNano.writeString(3, this.layout);
                    }
                    if (this.language != null) {
                        codedOutputByteBufferNano.writeString(4, this.language);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public interface KeyboardTextType
            {
                public static final int TEXT_SPACE = 4;
                public static final int TEXT_SUGGESTION = 5;
                public static final int TEXT_UNKNOWN = 0;
            }
        }
        
        public static final class Launcher extends ExtendableMessageNano<Launcher> implements Cloneable
        {
            private static volatile Launcher[] _emptyArray;
            public Integer navItem;
            
            public Launcher() {
                this.clear();
            }
            
            public static Launcher[] emptyArray() {
                if (Launcher._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Launcher._emptyArray != null) {
                                    break;
                                }
                            }
                            Launcher._emptyArray = new Launcher[0];
                            continue;
                        }
                    }
                }
                return Launcher._emptyArray;
            }
            
            public static Launcher parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Launcher().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Launcher parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Launcher(), array);
            }
            
            public final Launcher clear() {
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Launcher clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.navItem != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.navItem);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Launcher mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 6:
                                case 7:
                                case 8: {
                                    this.navItem = int32;
                                }
                                case 4:
                                case 5: {
                                    continue;
                                }
                                default: {
                                    continue;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.navItem != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.navItem);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public interface NavItem
            {
                public static final int DISCOVERY = 6;
                public static final int FEEDBACK = 2;
                public static final int HELP = 3;
                public static final int LIBRARY = 7;
                public static final int SETTINGS = 1;
                public static final int UNKNOWN = 0;
                public static final int WISHLIST = 8;
            }
        }
        
        public static final class Lullaby extends ExtendableMessageNano<Lullaby> implements Cloneable
        {
            private static volatile Lullaby[] _emptyArray;
            public String contentId;
            public Integer index;
            public LoadTime loadTime;
            public Integer uiElement;
            
            public Lullaby() {
                this.clear();
            }
            
            public static Lullaby[] emptyArray() {
                if (Lullaby._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Lullaby._emptyArray != null) {
                                    break;
                                }
                            }
                            Lullaby._emptyArray = new Lullaby[0];
                            continue;
                        }
                    }
                }
                return Lullaby._emptyArray;
            }
            
            public static Lullaby parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Lullaby().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Lullaby parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Lullaby(), array);
            }
            
            public final Lullaby clear() {
                this.index = null;
                this.contentId = null;
                this.loadTime = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Lullaby clone() {
                while (true) {
                    Lullaby lullaby;
                    try {
                        lullaby = super.clone();
                        if (this.loadTime == null) {
                            return lullaby;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    lullaby.loadTime = this.loadTime.clone();
                    return lullaby;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.uiElement != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.uiElement);
                }
                if (this.index != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.index);
                }
                if (this.contentId != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.contentId);
                }
                if (this.loadTime != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.loadTime);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Lullaby mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 1000:
                                case 1001:
                                case 1002:
                                case 1003:
                                case 1004:
                                case 1005:
                                case 1006:
                                case 1007:
                                case 2000:
                                case 2001:
                                case 2002:
                                case 2003:
                                case 2004:
                                case 2005:
                                case 2006:
                                case 2007:
                                case 2008:
                                case 2009:
                                case 2010:
                                case 2011:
                                case 2012:
                                case 2013:
                                case 2014:
                                case 2015:
                                case 2016:
                                case 2017:
                                case 2018:
                                case 2019:
                                case 2020:
                                case 2021: {
                                    this.uiElement = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 16: {
                            this.index = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 26: {
                            this.contentId = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 34: {
                            if (this.loadTime == null) {
                                this.loadTime = new LoadTime();
                            }
                            codedInputByteBufferNano.readMessage(this.loadTime);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.uiElement != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.uiElement);
                }
                if (this.index != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.index);
                }
                if (this.contentId != null) {
                    codedOutputByteBufferNano.writeString(3, this.contentId);
                }
                if (this.loadTime != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.loadTime);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class LoadTime extends ExtendableMessageNano<LoadTime> implements Cloneable
            {
                private static volatile LoadTime[] _emptyArray;
                public Integer assetType;
                public Long loadTimeMs;
                
                public LoadTime() {
                    this.clear();
                }
                
                public static LoadTime[] emptyArray() {
                    if (LoadTime._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (LoadTime._emptyArray != null) {
                                        break;
                                    }
                                }
                                LoadTime._emptyArray = new LoadTime[0];
                                continue;
                            }
                        }
                    }
                    return LoadTime._emptyArray;
                }
                
                public static LoadTime parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new LoadTime().mergeFrom(codedInputByteBufferNano);
                }
                
                public static LoadTime parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new LoadTime(), array);
                }
                
                public final LoadTime clear() {
                    this.loadTimeMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final LoadTime clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.assetType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.assetType);
                    }
                    if (this.loadTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.loadTimeMs);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final LoadTime mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    case 10: {
                                        this.assetType = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.loadTimeMs = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.assetType != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.assetType);
                    }
                    if (this.loadTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.loadTimeMs);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface AssetType
                {
                    public static final int PAGE = 1;
                    public static final int SOUND_OGG = 4;
                    public static final int SOUND_WAV = 3;
                    public static final int TEXT = 2;
                    public static final int TEXTURE_FIFE_JPEG = 9;
                    public static final int TEXTURE_FIFE_PNG = 8;
                    public static final int TEXTURE_FIFE_WEBP = 10;
                    public static final int TEXTURE_JPEG = 6;
                    public static final int TEXTURE_PNG = 5;
                    public static final int TEXTURE_WEBP = 7;
                    public static final int UNKNOWN = 0;
                }
            }
            
            public interface UiElement
            {
                public static final int BACK_BUTTON = 1;
                public static final int LAUNCHER_DISCOVERY_WINDOW = 1001;
                public static final int LAUNCHER_LIBRARY = 1006;
                public static final int LAUNCHER_LIBRARY_APP_CARD = 1007;
                public static final int LAUNCHER_LIBRARY_BUTTON = 1004;
                public static final int LAUNCHER_MAIN = 1000;
                public static final int LAUNCHER_PLAY_STORE_BUTTON = 1005;
                public static final int LAUNCHER_RECENT_APP_CARD = 1002;
                public static final int LAUNCHER_SETTINGS_BUTTON = 1003;
                public static final int PLAY_STORE_COLLECTION_APP_CARD = 2008;
                public static final int PLAY_STORE_COLLECTION_PAGE = 2007;
                public static final int PLAY_STORE_DETAILS = 2009;
                public static final int PLAY_STORE_DETAILS_BUY_BUTTON = 2014;
                public static final int PLAY_STORE_DETAILS_INSTALL_BUTTON = 2013;
                public static final int PLAY_STORE_DETAILS_MORE_DETAILS_BUTTON = 2011;
                public static final int PLAY_STORE_DETAILS_NO_DAYDREAM = 2016;
                public static final int PLAY_STORE_DETAILS_OPEN_BUTTON = 2012;
                public static final int PLAY_STORE_DETAILS_PANO = 2010;
                public static final int PLAY_STORE_DETAILS_UPDATE_BUTTON = 2015;
                public static final int PLAY_STORE_MAIN = 2000;
                public static final int PLAY_STORE_MAIN_COLLECTION = 2003;
                public static final int PLAY_STORE_MAIN_COLLECTION_APP_CARD = 2005;
                public static final int PLAY_STORE_MAIN_COLLECTION_TITLE_CARD = 2004;
                public static final int PLAY_STORE_MAIN_FEATURED_ACTIVE_CARD = 2002;
                public static final int PLAY_STORE_MAIN_FEATURED_CAROUSEL_CARD = 2001;
                public static final int PLAY_STORE_MAIN_FEATURED_COLLECTION = 2006;
                public static final int PLAY_STORE_MORE_DETAILS = 2017;
                public static final int PLAY_STORE_NETWORK_ERROR = 2020;
                public static final int PLAY_STORE_NETWORK_ERROR_RETRY_BUTTON = 2021;
                public static final int PLAY_STORE_TOS_DIALOG = 2018;
                public static final int PLAY_STORE_TOS_DIALOG_EXIT_VR_BUTTON = 2019;
                public static final int UNKNOWN = 0;
            }
        }
        
        public static final class PerformanceStats extends ExtendableMessageNano<PerformanceStats> implements Cloneable
        {
            private static volatile PerformanceStats[] _emptyArray;
            public HistogramBucket[] appRenderTime;
            public Integer averageFps;
            public float[] batteryShutdownTemperature;
            public float[] batteryThrottlingTemperature;
            public HistogramBucket[] consecutiveDroppedFrames;
            public float[] cpuShutdownTemperature;
            public float[] cpuThrottlingTemperature;
            public HistogramBucket[] frameTime;
            public float[] gpuShutdownTemperature;
            public float[] gpuThrottlingTemperature;
            public Integer memoryConsumptionKilobytes;
            public HistogramBucket[] postFrameTime;
            public HistogramBucket[] presentTime;
            public HistogramBucket[] scanlineRacingVsyncOvershootUs;
            public Float shutdownSkinTemperatureCelsius;
            public Integer thermalExitFlowShown;
            public Float throttleSkinTemperatureCelsius;
            public TimeSeriesData timeSeriesData;
            public HistogramBucket[] totalRenderTime;
            public Float vrMaxSkinTemperatureCelsius;
            
            public PerformanceStats() {
                this.clear();
            }
            
            public static PerformanceStats[] emptyArray() {
                if (PerformanceStats._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (PerformanceStats._emptyArray != null) {
                                    break;
                                }
                            }
                            PerformanceStats._emptyArray = new PerformanceStats[0];
                            continue;
                        }
                    }
                }
                return PerformanceStats._emptyArray;
            }
            
            public static PerformanceStats parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new PerformanceStats().mergeFrom(codedInputByteBufferNano);
            }
            
            public static PerformanceStats parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new PerformanceStats(), array);
            }
            
            public final PerformanceStats clear() {
                this.averageFps = null;
                this.frameTime = HistogramBucket.emptyArray();
                this.memoryConsumptionKilobytes = null;
                this.throttleSkinTemperatureCelsius = null;
                this.vrMaxSkinTemperatureCelsius = null;
                this.shutdownSkinTemperatureCelsius = null;
                this.timeSeriesData = null;
                this.appRenderTime = HistogramBucket.emptyArray();
                this.presentTime = HistogramBucket.emptyArray();
                this.totalRenderTime = HistogramBucket.emptyArray();
                this.postFrameTime = HistogramBucket.emptyArray();
                this.consecutiveDroppedFrames = HistogramBucket.emptyArray();
                this.scanlineRacingVsyncOvershootUs = HistogramBucket.emptyArray();
                this.thermalExitFlowShown = null;
                this.cpuThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.gpuThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.batteryThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.cpuShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.gpuShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.batteryShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final PerformanceStats clone() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: aload_0        
                //     3: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                //     6: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$PerformanceStats;
                //     9: astore_2       
                //    10: aload_0        
                //    11: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    14: ifnonnull       120
                //    17: aload_0        
                //    18: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.timeSeriesData:Lcom/google/common/logging/nano/Vr$VREvent$TimeSeriesData;
                //    21: ifnonnull       184
                //    24: aload_0        
                //    25: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    28: ifnonnull       198
                //    31: aload_0        
                //    32: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    35: ifnonnull       262
                //    38: aload_0        
                //    39: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    42: ifnonnull       326
                //    45: aload_0        
                //    46: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    49: ifnonnull       390
                //    52: aload_0        
                //    53: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    56: ifnonnull       454
                //    59: aload_0        
                //    60: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //    63: ifnonnull       518
                //    66: aload_0        
                //    67: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuThrottlingTemperature:[F
                //    70: ifnonnull       582
                //    73: aload_0        
                //    74: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuThrottlingTemperature:[F
                //    77: ifnonnull       607
                //    80: aload_0        
                //    81: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryThrottlingTemperature:[F
                //    84: ifnonnull       632
                //    87: aload_0        
                //    88: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuShutdownTemperature:[F
                //    91: ifnonnull       657
                //    94: aload_0        
                //    95: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuShutdownTemperature:[F
                //    98: ifnonnull       682
                //   101: aload_0        
                //   102: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryShutdownTemperature:[F
                //   105: ifnonnull       707
                //   108: aload_2        
                //   109: areturn        
                //   110: astore_2       
                //   111: new             Ljava/lang/AssertionError;
                //   114: dup            
                //   115: aload_2        
                //   116: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                //   119: athrow         
                //   120: aload_0        
                //   121: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   124: arraylength    
                //   125: ifle            17
                //   128: aload_2        
                //   129: aload_0        
                //   130: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   133: arraylength    
                //   134: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   137: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   140: iconst_0       
                //   141: istore_3       
                //   142: iload_3        
                //   143: aload_0        
                //   144: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   147: arraylength    
                //   148: if_icmpge       17
                //   151: aload_0        
                //   152: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   155: iload_3        
                //   156: aaload         
                //   157: ifnonnull       166
                //   160: iinc            3, 1
                //   163: goto            142
                //   166: aload_2        
                //   167: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   170: iload_3        
                //   171: aload_0        
                //   172: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.frameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   175: iload_3        
                //   176: aaload         
                //   177: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   180: aastore        
                //   181: goto            160
                //   184: aload_2        
                //   185: aload_0        
                //   186: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.timeSeriesData:Lcom/google/common/logging/nano/Vr$VREvent$TimeSeriesData;
                //   189: invokevirtual   com/google/common/logging/nano/Vr$VREvent$TimeSeriesData.clone:()Lcom/google/common/logging/nano/Vr$VREvent$TimeSeriesData;
                //   192: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.timeSeriesData:Lcom/google/common/logging/nano/Vr$VREvent$TimeSeriesData;
                //   195: goto            24
                //   198: aload_0        
                //   199: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   202: arraylength    
                //   203: ifle            31
                //   206: aload_2        
                //   207: aload_0        
                //   208: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   211: arraylength    
                //   212: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   215: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   218: iconst_0       
                //   219: istore_3       
                //   220: iload_3        
                //   221: aload_0        
                //   222: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   225: arraylength    
                //   226: if_icmpge       31
                //   229: aload_0        
                //   230: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   233: iload_3        
                //   234: aaload         
                //   235: ifnonnull       244
                //   238: iinc            3, 1
                //   241: goto            220
                //   244: aload_2        
                //   245: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   248: iload_3        
                //   249: aload_0        
                //   250: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.appRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   253: iload_3        
                //   254: aaload         
                //   255: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   258: aastore        
                //   259: goto            238
                //   262: aload_0        
                //   263: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   266: arraylength    
                //   267: ifle            38
                //   270: aload_2        
                //   271: aload_0        
                //   272: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   275: arraylength    
                //   276: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   279: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   282: iconst_0       
                //   283: istore_3       
                //   284: iload_3        
                //   285: aload_0        
                //   286: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   289: arraylength    
                //   290: if_icmpge       38
                //   293: aload_0        
                //   294: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   297: iload_3        
                //   298: aaload         
                //   299: ifnonnull       308
                //   302: iinc            3, 1
                //   305: goto            284
                //   308: aload_2        
                //   309: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   312: iload_3        
                //   313: aload_0        
                //   314: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.presentTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   317: iload_3        
                //   318: aaload         
                //   319: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   322: aastore        
                //   323: goto            302
                //   326: aload_0        
                //   327: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   330: arraylength    
                //   331: ifle            45
                //   334: aload_2        
                //   335: aload_0        
                //   336: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   339: arraylength    
                //   340: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   343: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   346: iconst_0       
                //   347: istore_3       
                //   348: iload_3        
                //   349: aload_0        
                //   350: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   353: arraylength    
                //   354: if_icmpge       45
                //   357: aload_0        
                //   358: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   361: iload_3        
                //   362: aaload         
                //   363: ifnonnull       372
                //   366: iinc            3, 1
                //   369: goto            348
                //   372: aload_2        
                //   373: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   376: iload_3        
                //   377: aload_0        
                //   378: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.totalRenderTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   381: iload_3        
                //   382: aaload         
                //   383: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   386: aastore        
                //   387: goto            366
                //   390: aload_0        
                //   391: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   394: arraylength    
                //   395: ifle            52
                //   398: aload_2        
                //   399: aload_0        
                //   400: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   403: arraylength    
                //   404: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   407: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   410: iconst_0       
                //   411: istore_3       
                //   412: iload_3        
                //   413: aload_0        
                //   414: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   417: arraylength    
                //   418: if_icmpge       52
                //   421: aload_0        
                //   422: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   425: iload_3        
                //   426: aaload         
                //   427: ifnonnull       436
                //   430: iinc            3, 1
                //   433: goto            412
                //   436: aload_2        
                //   437: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   440: iload_3        
                //   441: aload_0        
                //   442: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.postFrameTime:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   445: iload_3        
                //   446: aaload         
                //   447: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   450: aastore        
                //   451: goto            430
                //   454: aload_0        
                //   455: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   458: arraylength    
                //   459: ifle            59
                //   462: aload_2        
                //   463: aload_0        
                //   464: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   467: arraylength    
                //   468: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   471: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   474: iconst_0       
                //   475: istore_3       
                //   476: iload_3        
                //   477: aload_0        
                //   478: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   481: arraylength    
                //   482: if_icmpge       59
                //   485: aload_0        
                //   486: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   489: iload_3        
                //   490: aaload         
                //   491: ifnonnull       500
                //   494: iinc            3, 1
                //   497: goto            476
                //   500: aload_2        
                //   501: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   504: iload_3        
                //   505: aload_0        
                //   506: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.consecutiveDroppedFrames:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   509: iload_3        
                //   510: aaload         
                //   511: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   514: aastore        
                //   515: goto            494
                //   518: aload_0        
                //   519: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   522: arraylength    
                //   523: ifle            66
                //   526: aload_2        
                //   527: aload_0        
                //   528: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   531: arraylength    
                //   532: anewarray       Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   535: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   538: iload_1        
                //   539: istore_3       
                //   540: iload_3        
                //   541: aload_0        
                //   542: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   545: arraylength    
                //   546: if_icmpge       66
                //   549: aload_0        
                //   550: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   553: iload_3        
                //   554: aaload         
                //   555: ifnonnull       564
                //   558: iinc            3, 1
                //   561: goto            540
                //   564: aload_2        
                //   565: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   568: iload_3        
                //   569: aload_0        
                //   570: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.scanlineRacingVsyncOvershootUs:[Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   573: iload_3        
                //   574: aaload         
                //   575: invokevirtual   com/google/common/logging/nano/Vr$VREvent$HistogramBucket.clone:()Lcom/google/common/logging/nano/Vr$VREvent$HistogramBucket;
                //   578: aastore        
                //   579: goto            558
                //   582: aload_0        
                //   583: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuThrottlingTemperature:[F
                //   586: arraylength    
                //   587: ifle            73
                //   590: aload_2        
                //   591: aload_0        
                //   592: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuThrottlingTemperature:[F
                //   595: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   598: checkcast       [F
                //   601: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuThrottlingTemperature:[F
                //   604: goto            73
                //   607: aload_0        
                //   608: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuThrottlingTemperature:[F
                //   611: arraylength    
                //   612: ifle            80
                //   615: aload_2        
                //   616: aload_0        
                //   617: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuThrottlingTemperature:[F
                //   620: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   623: checkcast       [F
                //   626: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuThrottlingTemperature:[F
                //   629: goto            80
                //   632: aload_0        
                //   633: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryThrottlingTemperature:[F
                //   636: arraylength    
                //   637: ifle            87
                //   640: aload_2        
                //   641: aload_0        
                //   642: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryThrottlingTemperature:[F
                //   645: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   648: checkcast       [F
                //   651: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryThrottlingTemperature:[F
                //   654: goto            87
                //   657: aload_0        
                //   658: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuShutdownTemperature:[F
                //   661: arraylength    
                //   662: ifle            94
                //   665: aload_2        
                //   666: aload_0        
                //   667: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuShutdownTemperature:[F
                //   670: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   673: checkcast       [F
                //   676: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.cpuShutdownTemperature:[F
                //   679: goto            94
                //   682: aload_0        
                //   683: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuShutdownTemperature:[F
                //   686: arraylength    
                //   687: ifle            101
                //   690: aload_2        
                //   691: aload_0        
                //   692: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuShutdownTemperature:[F
                //   695: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   698: checkcast       [F
                //   701: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.gpuShutdownTemperature:[F
                //   704: goto            101
                //   707: aload_0        
                //   708: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryShutdownTemperature:[F
                //   711: arraylength    
                //   712: ifle            108
                //   715: aload_2        
                //   716: aload_0        
                //   717: getfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryShutdownTemperature:[F
                //   720: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                //   723: checkcast       [F
                //   726: putfield        com/google/common/logging/nano/Vr$VREvent$PerformanceStats.batteryShutdownTemperature:[F
                //   729: goto            108
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                  
                //  -----  -----  -----  -----  --------------------------------------
                //  2      10     110    120    Ljava/lang/CloneNotSupportedException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:HistogramBucket[](PerformanceStats::scanlineRacingVsyncOvershootUs, this:PerformanceStats)), ldc:int(0))
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
            
            @Override
            protected final int computeSerializedSize() {
                final int n = 0;
                int computeSerializedSize = super.computeSerializedSize();
                if (this.averageFps != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.averageFps);
                }
                int n2;
                if (this.frameTime == null) {
                    n2 = computeSerializedSize;
                }
                else {
                    n2 = computeSerializedSize;
                    if (this.frameTime.length > 0) {
                        for (int i = 0; i < this.frameTime.length; ++i) {
                            final HistogramBucket histogramBucket = this.frameTime[i];
                            if (histogramBucket != null) {
                                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, histogramBucket);
                            }
                        }
                        n2 = computeSerializedSize;
                    }
                }
                int n3;
                if (this.memoryConsumptionKilobytes == null) {
                    n3 = n2;
                }
                else {
                    n3 = n2 + CodedOutputByteBufferNano.computeInt32Size(3, this.memoryConsumptionKilobytes);
                }
                if (this.throttleSkinTemperatureCelsius != null) {
                    n3 += CodedOutputByteBufferNano.computeFloatSize(4, this.throttleSkinTemperatureCelsius);
                }
                if (this.vrMaxSkinTemperatureCelsius != null) {
                    n3 += CodedOutputByteBufferNano.computeFloatSize(5, this.vrMaxSkinTemperatureCelsius);
                }
                if (this.shutdownSkinTemperatureCelsius != null) {
                    n3 += CodedOutputByteBufferNano.computeFloatSize(6, this.shutdownSkinTemperatureCelsius);
                }
                if (this.timeSeriesData != null) {
                    n3 += CodedOutputByteBufferNano.computeMessageSize(7, this.timeSeriesData);
                }
                int n4;
                if (this.appRenderTime == null) {
                    n4 = n3;
                }
                else {
                    n4 = n3;
                    if (this.appRenderTime.length > 0) {
                        for (int j = 0; j < this.appRenderTime.length; ++j) {
                            final HistogramBucket histogramBucket2 = this.appRenderTime[j];
                            if (histogramBucket2 != null) {
                                n3 += CodedOutputByteBufferNano.computeMessageSize(8, histogramBucket2);
                            }
                        }
                        n4 = n3;
                    }
                }
                int n5;
                if (this.presentTime == null) {
                    n5 = n4;
                }
                else {
                    n5 = n4;
                    if (this.presentTime.length > 0) {
                        n5 = n4;
                        for (int k = 0; k < this.presentTime.length; ++k) {
                            final HistogramBucket histogramBucket3 = this.presentTime[k];
                            if (histogramBucket3 != null) {
                                n5 += CodedOutputByteBufferNano.computeMessageSize(9, histogramBucket3);
                            }
                        }
                    }
                }
                int n6;
                if (this.totalRenderTime == null) {
                    n6 = n5;
                }
                else {
                    n6 = n5;
                    if (this.totalRenderTime.length > 0) {
                        for (int l = 0; l < this.totalRenderTime.length; ++l) {
                            final HistogramBucket histogramBucket4 = this.totalRenderTime[l];
                            if (histogramBucket4 != null) {
                                n5 += CodedOutputByteBufferNano.computeMessageSize(10, histogramBucket4);
                            }
                        }
                        n6 = n5;
                    }
                }
                int n7;
                if (this.postFrameTime == null) {
                    n7 = n6;
                }
                else {
                    n7 = n6;
                    if (this.postFrameTime.length > 0) {
                        int n8 = n6;
                        for (int n9 = 0; n9 < this.postFrameTime.length; ++n9) {
                            final HistogramBucket histogramBucket5 = this.postFrameTime[n9];
                            if (histogramBucket5 != null) {
                                n8 += CodedOutputByteBufferNano.computeMessageSize(11, histogramBucket5);
                            }
                        }
                        n7 = n8;
                    }
                }
                int n10;
                if (this.consecutiveDroppedFrames == null) {
                    n10 = n7;
                }
                else {
                    n10 = n7;
                    if (this.consecutiveDroppedFrames.length > 0) {
                        n10 = n7;
                        for (int n11 = 0; n11 < this.consecutiveDroppedFrames.length; ++n11) {
                            final HistogramBucket histogramBucket6 = this.consecutiveDroppedFrames[n11];
                            if (histogramBucket6 != null) {
                                n10 += CodedOutputByteBufferNano.computeMessageSize(12, histogramBucket6);
                            }
                        }
                    }
                }
                int n12;
                if (this.scanlineRacingVsyncOvershootUs == null) {
                    n12 = n10;
                }
                else {
                    n12 = n10;
                    if (this.scanlineRacingVsyncOvershootUs.length > 0) {
                        int n13 = n;
                        while (true) {
                            n12 = n10;
                            if (n13 >= this.scanlineRacingVsyncOvershootUs.length) {
                                break;
                            }
                            final HistogramBucket histogramBucket7 = this.scanlineRacingVsyncOvershootUs[n13];
                            if (histogramBucket7 != null) {
                                n10 += CodedOutputByteBufferNano.computeMessageSize(13, histogramBucket7);
                            }
                            ++n13;
                        }
                    }
                }
                if (this.thermalExitFlowShown != null) {
                    n12 += CodedOutputByteBufferNano.computeInt32Size(14, this.thermalExitFlowShown);
                }
                int n14;
                if (this.cpuThrottlingTemperature == null) {
                    n14 = n12;
                }
                else {
                    n14 = n12;
                    if (this.cpuThrottlingTemperature.length > 0) {
                        n14 = n12 + this.cpuThrottlingTemperature.length * 4 + this.cpuThrottlingTemperature.length * 1;
                    }
                }
                int n15;
                if (this.gpuThrottlingTemperature == null) {
                    n15 = n14;
                }
                else {
                    n15 = n14;
                    if (this.gpuThrottlingTemperature.length > 0) {
                        n15 = n14 + this.gpuThrottlingTemperature.length * 4 + this.gpuThrottlingTemperature.length * 2;
                    }
                }
                int n16;
                if (this.batteryThrottlingTemperature == null) {
                    n16 = n15;
                }
                else {
                    n16 = n15;
                    if (this.batteryThrottlingTemperature.length > 0) {
                        n16 = n15 + this.batteryThrottlingTemperature.length * 4 + this.batteryThrottlingTemperature.length * 2;
                    }
                }
                int n17;
                if (this.cpuShutdownTemperature == null) {
                    n17 = n16;
                }
                else {
                    n17 = n16;
                    if (this.cpuShutdownTemperature.length > 0) {
                        n17 = n16 + this.cpuShutdownTemperature.length * 4 + this.cpuShutdownTemperature.length * 2;
                    }
                }
                int n18;
                if (this.gpuShutdownTemperature == null) {
                    n18 = n17;
                }
                else {
                    n18 = n17;
                    if (this.gpuShutdownTemperature.length > 0) {
                        n18 = n17 + this.gpuShutdownTemperature.length * 4 + this.gpuShutdownTemperature.length * 2;
                    }
                }
                int n19;
                if (this.batteryShutdownTemperature == null) {
                    n19 = n18;
                }
                else {
                    n19 = n18;
                    if (this.batteryShutdownTemperature.length > 0) {
                        n19 = n18 + this.batteryShutdownTemperature.length * 4 + this.batteryShutdownTemperature.length * 2;
                    }
                }
                return n19;
            }
            
            @Override
            public final PerformanceStats mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            this.averageFps = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 18: {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int i;
                            if (this.frameTime != null) {
                                i = this.frameTime.length;
                            }
                            else {
                                i = 0;
                            }
                            final HistogramBucket[] frameTime = new HistogramBucket[repeatedFieldArrayLength + i];
                            if (i != 0) {
                                System.arraycopy(this.frameTime, 0, frameTime, 0, i);
                            }
                            while (i < frameTime.length - 1) {
                                codedInputByteBufferNano.readMessage(frameTime[i] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(frameTime[i] = new HistogramBucket());
                            this.frameTime = frameTime;
                            continue;
                        }
                        case 24: {
                            this.memoryConsumptionKilobytes = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 37: {
                            this.throttleSkinTemperatureCelsius = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 45: {
                            this.vrMaxSkinTemperatureCelsius = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 53: {
                            this.shutdownSkinTemperatureCelsius = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 58: {
                            if (this.timeSeriesData == null) {
                                this.timeSeriesData = new TimeSeriesData();
                            }
                            codedInputByteBufferNano.readMessage(this.timeSeriesData);
                            continue;
                        }
                        case 66: {
                            final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 66);
                            int j;
                            if (this.appRenderTime != null) {
                                j = this.appRenderTime.length;
                            }
                            else {
                                j = 0;
                            }
                            final HistogramBucket[] appRenderTime = new HistogramBucket[repeatedFieldArrayLength2 + j];
                            if (j != 0) {
                                System.arraycopy(this.appRenderTime, 0, appRenderTime, 0, j);
                            }
                            while (j < appRenderTime.length - 1) {
                                codedInputByteBufferNano.readMessage(appRenderTime[j] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++j;
                            }
                            codedInputByteBufferNano.readMessage(appRenderTime[j] = new HistogramBucket());
                            this.appRenderTime = appRenderTime;
                            continue;
                        }
                        case 74: {
                            final int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 74);
                            int k;
                            if (this.presentTime != null) {
                                k = this.presentTime.length;
                            }
                            else {
                                k = 0;
                            }
                            final HistogramBucket[] presentTime = new HistogramBucket[repeatedFieldArrayLength3 + k];
                            if (k != 0) {
                                System.arraycopy(this.presentTime, 0, presentTime, 0, k);
                            }
                            while (k < presentTime.length - 1) {
                                codedInputByteBufferNano.readMessage(presentTime[k] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++k;
                            }
                            codedInputByteBufferNano.readMessage(presentTime[k] = new HistogramBucket());
                            this.presentTime = presentTime;
                            continue;
                        }
                        case 82: {
                            final int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                            int l;
                            if (this.totalRenderTime != null) {
                                l = this.totalRenderTime.length;
                            }
                            else {
                                l = 0;
                            }
                            final HistogramBucket[] totalRenderTime = new HistogramBucket[repeatedFieldArrayLength4 + l];
                            if (l != 0) {
                                System.arraycopy(this.totalRenderTime, 0, totalRenderTime, 0, l);
                            }
                            while (l < totalRenderTime.length - 1) {
                                codedInputByteBufferNano.readMessage(totalRenderTime[l] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++l;
                            }
                            codedInputByteBufferNano.readMessage(totalRenderTime[l] = new HistogramBucket());
                            this.totalRenderTime = totalRenderTime;
                            continue;
                        }
                        case 90: {
                            final int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 90);
                            int length;
                            if (this.postFrameTime != null) {
                                length = this.postFrameTime.length;
                            }
                            else {
                                length = 0;
                            }
                            final HistogramBucket[] postFrameTime = new HistogramBucket[repeatedFieldArrayLength5 + length];
                            if (length != 0) {
                                System.arraycopy(this.postFrameTime, 0, postFrameTime, 0, length);
                            }
                            while (length < postFrameTime.length - 1) {
                                codedInputByteBufferNano.readMessage(postFrameTime[length] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++length;
                            }
                            codedInputByteBufferNano.readMessage(postFrameTime[length] = new HistogramBucket());
                            this.postFrameTime = postFrameTime;
                            continue;
                        }
                        case 98: {
                            final int repeatedFieldArrayLength6 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                            int length2;
                            if (this.consecutiveDroppedFrames != null) {
                                length2 = this.consecutiveDroppedFrames.length;
                            }
                            else {
                                length2 = 0;
                            }
                            final HistogramBucket[] consecutiveDroppedFrames = new HistogramBucket[repeatedFieldArrayLength6 + length2];
                            if (length2 != 0) {
                                System.arraycopy(this.consecutiveDroppedFrames, 0, consecutiveDroppedFrames, 0, length2);
                            }
                            while (length2 < consecutiveDroppedFrames.length - 1) {
                                codedInputByteBufferNano.readMessage(consecutiveDroppedFrames[length2] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++length2;
                            }
                            codedInputByteBufferNano.readMessage(consecutiveDroppedFrames[length2] = new HistogramBucket());
                            this.consecutiveDroppedFrames = consecutiveDroppedFrames;
                            continue;
                        }
                        case 106: {
                            final int repeatedFieldArrayLength7 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 106);
                            int length3;
                            if (this.scanlineRacingVsyncOvershootUs != null) {
                                length3 = this.scanlineRacingVsyncOvershootUs.length;
                            }
                            else {
                                length3 = 0;
                            }
                            final HistogramBucket[] scanlineRacingVsyncOvershootUs = new HistogramBucket[repeatedFieldArrayLength7 + length3];
                            if (length3 != 0) {
                                System.arraycopy(this.scanlineRacingVsyncOvershootUs, 0, scanlineRacingVsyncOvershootUs, 0, length3);
                            }
                            while (length3 < scanlineRacingVsyncOvershootUs.length - 1) {
                                codedInputByteBufferNano.readMessage(scanlineRacingVsyncOvershootUs[length3] = new HistogramBucket());
                                codedInputByteBufferNano.readTag();
                                ++length3;
                            }
                            codedInputByteBufferNano.readMessage(scanlineRacingVsyncOvershootUs[length3] = new HistogramBucket());
                            this.scanlineRacingVsyncOvershootUs = scanlineRacingVsyncOvershootUs;
                            continue;
                        }
                        case 112: {
                            this.thermalExitFlowShown = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 125: {
                            final int repeatedFieldArrayLength8 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 125);
                            int length4;
                            if (this.cpuThrottlingTemperature != null) {
                                length4 = this.cpuThrottlingTemperature.length;
                            }
                            else {
                                length4 = 0;
                            }
                            final float[] cpuThrottlingTemperature = new float[repeatedFieldArrayLength8 + length4];
                            if (length4 != 0) {
                                System.arraycopy(this.cpuThrottlingTemperature, 0, cpuThrottlingTemperature, 0, length4);
                            }
                            while (length4 < cpuThrottlingTemperature.length - 1) {
                                cpuThrottlingTemperature[length4] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length4;
                            }
                            cpuThrottlingTemperature[length4] = codedInputByteBufferNano.readFloat();
                            this.cpuThrottlingTemperature = cpuThrottlingTemperature;
                            continue;
                        }
                        case 122: {
                            final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                            final int n = rawVarint32 / 4;
                            int length5;
                            if (this.cpuThrottlingTemperature != null) {
                                length5 = this.cpuThrottlingTemperature.length;
                            }
                            else {
                                length5 = 0;
                            }
                            final float[] cpuThrottlingTemperature2 = new float[n + length5];
                            if (length5 != 0) {
                                System.arraycopy(this.cpuThrottlingTemperature, 0, cpuThrottlingTemperature2, 0, length5);
                            }
                            while (length5 < cpuThrottlingTemperature2.length) {
                                cpuThrottlingTemperature2[length5] = codedInputByteBufferNano.readFloat();
                                ++length5;
                            }
                            this.cpuThrottlingTemperature = cpuThrottlingTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit);
                            continue;
                        }
                        case 133: {
                            final int repeatedFieldArrayLength9 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 133);
                            int length6;
                            if (this.gpuThrottlingTemperature != null) {
                                length6 = this.gpuThrottlingTemperature.length;
                            }
                            else {
                                length6 = 0;
                            }
                            final float[] gpuThrottlingTemperature = new float[repeatedFieldArrayLength9 + length6];
                            if (length6 != 0) {
                                System.arraycopy(this.gpuThrottlingTemperature, 0, gpuThrottlingTemperature, 0, length6);
                            }
                            while (length6 < gpuThrottlingTemperature.length - 1) {
                                gpuThrottlingTemperature[length6] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length6;
                            }
                            gpuThrottlingTemperature[length6] = codedInputByteBufferNano.readFloat();
                            this.gpuThrottlingTemperature = gpuThrottlingTemperature;
                            continue;
                        }
                        case 130: {
                            final int rawVarint33 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit2 = codedInputByteBufferNano.pushLimit(rawVarint33);
                            final int n2 = rawVarint33 / 4;
                            int length7;
                            if (this.gpuThrottlingTemperature != null) {
                                length7 = this.gpuThrottlingTemperature.length;
                            }
                            else {
                                length7 = 0;
                            }
                            final float[] gpuThrottlingTemperature2 = new float[n2 + length7];
                            if (length7 != 0) {
                                System.arraycopy(this.gpuThrottlingTemperature, 0, gpuThrottlingTemperature2, 0, length7);
                            }
                            while (length7 < gpuThrottlingTemperature2.length) {
                                gpuThrottlingTemperature2[length7] = codedInputByteBufferNano.readFloat();
                                ++length7;
                            }
                            this.gpuThrottlingTemperature = gpuThrottlingTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit2);
                            continue;
                        }
                        case 141: {
                            final int repeatedFieldArrayLength10 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 141);
                            int length8;
                            if (this.batteryThrottlingTemperature != null) {
                                length8 = this.batteryThrottlingTemperature.length;
                            }
                            else {
                                length8 = 0;
                            }
                            final float[] batteryThrottlingTemperature = new float[repeatedFieldArrayLength10 + length8];
                            if (length8 != 0) {
                                System.arraycopy(this.batteryThrottlingTemperature, 0, batteryThrottlingTemperature, 0, length8);
                            }
                            while (length8 < batteryThrottlingTemperature.length - 1) {
                                batteryThrottlingTemperature[length8] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length8;
                            }
                            batteryThrottlingTemperature[length8] = codedInputByteBufferNano.readFloat();
                            this.batteryThrottlingTemperature = batteryThrottlingTemperature;
                            continue;
                        }
                        case 138: {
                            final int rawVarint34 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit3 = codedInputByteBufferNano.pushLimit(rawVarint34);
                            final int n3 = rawVarint34 / 4;
                            int length9;
                            if (this.batteryThrottlingTemperature != null) {
                                length9 = this.batteryThrottlingTemperature.length;
                            }
                            else {
                                length9 = 0;
                            }
                            final float[] batteryThrottlingTemperature2 = new float[n3 + length9];
                            if (length9 != 0) {
                                System.arraycopy(this.batteryThrottlingTemperature, 0, batteryThrottlingTemperature2, 0, length9);
                            }
                            while (length9 < batteryThrottlingTemperature2.length) {
                                batteryThrottlingTemperature2[length9] = codedInputByteBufferNano.readFloat();
                                ++length9;
                            }
                            this.batteryThrottlingTemperature = batteryThrottlingTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit3);
                            continue;
                        }
                        case 149: {
                            final int repeatedFieldArrayLength11 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 149);
                            int length10;
                            if (this.cpuShutdownTemperature != null) {
                                length10 = this.cpuShutdownTemperature.length;
                            }
                            else {
                                length10 = 0;
                            }
                            final float[] cpuShutdownTemperature = new float[repeatedFieldArrayLength11 + length10];
                            if (length10 != 0) {
                                System.arraycopy(this.cpuShutdownTemperature, 0, cpuShutdownTemperature, 0, length10);
                            }
                            while (length10 < cpuShutdownTemperature.length - 1) {
                                cpuShutdownTemperature[length10] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length10;
                            }
                            cpuShutdownTemperature[length10] = codedInputByteBufferNano.readFloat();
                            this.cpuShutdownTemperature = cpuShutdownTemperature;
                            continue;
                        }
                        case 146: {
                            final int rawVarint35 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit4 = codedInputByteBufferNano.pushLimit(rawVarint35);
                            final int n4 = rawVarint35 / 4;
                            int length11;
                            if (this.cpuShutdownTemperature != null) {
                                length11 = this.cpuShutdownTemperature.length;
                            }
                            else {
                                length11 = 0;
                            }
                            final float[] cpuShutdownTemperature2 = new float[n4 + length11];
                            if (length11 != 0) {
                                System.arraycopy(this.cpuShutdownTemperature, 0, cpuShutdownTemperature2, 0, length11);
                            }
                            while (length11 < cpuShutdownTemperature2.length) {
                                cpuShutdownTemperature2[length11] = codedInputByteBufferNano.readFloat();
                                ++length11;
                            }
                            this.cpuShutdownTemperature = cpuShutdownTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit4);
                            continue;
                        }
                        case 157: {
                            final int repeatedFieldArrayLength12 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 157);
                            int length12;
                            if (this.gpuShutdownTemperature != null) {
                                length12 = this.gpuShutdownTemperature.length;
                            }
                            else {
                                length12 = 0;
                            }
                            final float[] gpuShutdownTemperature = new float[repeatedFieldArrayLength12 + length12];
                            if (length12 != 0) {
                                System.arraycopy(this.gpuShutdownTemperature, 0, gpuShutdownTemperature, 0, length12);
                            }
                            while (length12 < gpuShutdownTemperature.length - 1) {
                                gpuShutdownTemperature[length12] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length12;
                            }
                            gpuShutdownTemperature[length12] = codedInputByteBufferNano.readFloat();
                            this.gpuShutdownTemperature = gpuShutdownTemperature;
                            continue;
                        }
                        case 154: {
                            final int rawVarint36 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit5 = codedInputByteBufferNano.pushLimit(rawVarint36);
                            final int n5 = rawVarint36 / 4;
                            int length13;
                            if (this.gpuShutdownTemperature != null) {
                                length13 = this.gpuShutdownTemperature.length;
                            }
                            else {
                                length13 = 0;
                            }
                            final float[] gpuShutdownTemperature2 = new float[n5 + length13];
                            if (length13 != 0) {
                                System.arraycopy(this.gpuShutdownTemperature, 0, gpuShutdownTemperature2, 0, length13);
                            }
                            while (length13 < gpuShutdownTemperature2.length) {
                                gpuShutdownTemperature2[length13] = codedInputByteBufferNano.readFloat();
                                ++length13;
                            }
                            this.gpuShutdownTemperature = gpuShutdownTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit5);
                            continue;
                        }
                        case 165: {
                            final int repeatedFieldArrayLength13 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 165);
                            int length14;
                            if (this.batteryShutdownTemperature != null) {
                                length14 = this.batteryShutdownTemperature.length;
                            }
                            else {
                                length14 = 0;
                            }
                            final float[] batteryShutdownTemperature = new float[repeatedFieldArrayLength13 + length14];
                            if (length14 != 0) {
                                System.arraycopy(this.batteryShutdownTemperature, 0, batteryShutdownTemperature, 0, length14);
                            }
                            while (length14 < batteryShutdownTemperature.length - 1) {
                                batteryShutdownTemperature[length14] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                ++length14;
                            }
                            batteryShutdownTemperature[length14] = codedInputByteBufferNano.readFloat();
                            this.batteryShutdownTemperature = batteryShutdownTemperature;
                            continue;
                        }
                        case 162: {
                            final int rawVarint37 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit6 = codedInputByteBufferNano.pushLimit(rawVarint37);
                            final int n6 = rawVarint37 / 4;
                            int length15;
                            if (this.batteryShutdownTemperature != null) {
                                length15 = this.batteryShutdownTemperature.length;
                            }
                            else {
                                length15 = 0;
                            }
                            final float[] batteryShutdownTemperature2 = new float[n6 + length15];
                            if (length15 != 0) {
                                System.arraycopy(this.batteryShutdownTemperature, 0, batteryShutdownTemperature2, 0, length15);
                            }
                            while (length15 < batteryShutdownTemperature2.length) {
                                batteryShutdownTemperature2[length15] = codedInputByteBufferNano.readFloat();
                                ++length15;
                            }
                            this.batteryShutdownTemperature = batteryShutdownTemperature2;
                            codedInputByteBufferNano.popLimit(pushLimit6);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final int n = 0;
                if (this.averageFps != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.averageFps);
                }
                if (this.frameTime != null && this.frameTime.length > 0) {
                    for (int i = 0; i < this.frameTime.length; ++i) {
                        final HistogramBucket histogramBucket = this.frameTime[i];
                        if (histogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(2, histogramBucket);
                        }
                    }
                }
                if (this.memoryConsumptionKilobytes != null) {
                    codedOutputByteBufferNano.writeInt32(3, this.memoryConsumptionKilobytes);
                }
                if (this.throttleSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(4, this.throttleSkinTemperatureCelsius);
                }
                if (this.vrMaxSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(5, this.vrMaxSkinTemperatureCelsius);
                }
                if (this.shutdownSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(6, this.shutdownSkinTemperatureCelsius);
                }
                if (this.timeSeriesData != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.timeSeriesData);
                }
                if (this.appRenderTime != null && this.appRenderTime.length > 0) {
                    for (int j = 0; j < this.appRenderTime.length; ++j) {
                        final HistogramBucket histogramBucket2 = this.appRenderTime[j];
                        if (histogramBucket2 != null) {
                            codedOutputByteBufferNano.writeMessage(8, histogramBucket2);
                        }
                    }
                }
                if (this.presentTime != null && this.presentTime.length > 0) {
                    for (int k = 0; k < this.presentTime.length; ++k) {
                        final HistogramBucket histogramBucket3 = this.presentTime[k];
                        if (histogramBucket3 != null) {
                            codedOutputByteBufferNano.writeMessage(9, histogramBucket3);
                        }
                    }
                }
                if (this.totalRenderTime != null && this.totalRenderTime.length > 0) {
                    for (int l = 0; l < this.totalRenderTime.length; ++l) {
                        final HistogramBucket histogramBucket4 = this.totalRenderTime[l];
                        if (histogramBucket4 != null) {
                            codedOutputByteBufferNano.writeMessage(10, histogramBucket4);
                        }
                    }
                }
                if (this.postFrameTime != null && this.postFrameTime.length > 0) {
                    for (int n2 = 0; n2 < this.postFrameTime.length; ++n2) {
                        final HistogramBucket histogramBucket5 = this.postFrameTime[n2];
                        if (histogramBucket5 != null) {
                            codedOutputByteBufferNano.writeMessage(11, histogramBucket5);
                        }
                    }
                }
                if (this.consecutiveDroppedFrames != null && this.consecutiveDroppedFrames.length > 0) {
                    for (int n3 = 0; n3 < this.consecutiveDroppedFrames.length; ++n3) {
                        final HistogramBucket histogramBucket6 = this.consecutiveDroppedFrames[n3];
                        if (histogramBucket6 != null) {
                            codedOutputByteBufferNano.writeMessage(12, histogramBucket6);
                        }
                    }
                }
                if (this.scanlineRacingVsyncOvershootUs != null && this.scanlineRacingVsyncOvershootUs.length > 0) {
                    for (int n4 = 0; n4 < this.scanlineRacingVsyncOvershootUs.length; ++n4) {
                        final HistogramBucket histogramBucket7 = this.scanlineRacingVsyncOvershootUs[n4];
                        if (histogramBucket7 != null) {
                            codedOutputByteBufferNano.writeMessage(13, histogramBucket7);
                        }
                    }
                }
                if (this.thermalExitFlowShown != null) {
                    codedOutputByteBufferNano.writeInt32(14, this.thermalExitFlowShown);
                }
                if (this.cpuThrottlingTemperature != null && this.cpuThrottlingTemperature.length > 0) {
                    for (int n5 = 0; n5 < this.cpuThrottlingTemperature.length; ++n5) {
                        codedOutputByteBufferNano.writeFloat(15, this.cpuThrottlingTemperature[n5]);
                    }
                }
                if (this.gpuThrottlingTemperature != null && this.gpuThrottlingTemperature.length > 0) {
                    for (int n6 = 0; n6 < this.gpuThrottlingTemperature.length; ++n6) {
                        codedOutputByteBufferNano.writeFloat(16, this.gpuThrottlingTemperature[n6]);
                    }
                }
                if (this.batteryThrottlingTemperature != null && this.batteryThrottlingTemperature.length > 0) {
                    for (int n7 = 0; n7 < this.batteryThrottlingTemperature.length; ++n7) {
                        codedOutputByteBufferNano.writeFloat(17, this.batteryThrottlingTemperature[n7]);
                    }
                }
                if (this.cpuShutdownTemperature != null && this.cpuShutdownTemperature.length > 0) {
                    for (int n8 = 0; n8 < this.cpuShutdownTemperature.length; ++n8) {
                        codedOutputByteBufferNano.writeFloat(18, this.cpuShutdownTemperature[n8]);
                    }
                }
                if (this.gpuShutdownTemperature != null && this.gpuShutdownTemperature.length > 0) {
                    for (int n9 = 0; n9 < this.gpuShutdownTemperature.length; ++n9) {
                        codedOutputByteBufferNano.writeFloat(19, this.gpuShutdownTemperature[n9]);
                    }
                }
                if (this.batteryShutdownTemperature != null && this.batteryShutdownTemperature.length > 0) {
                    for (int n10 = n; n10 < this.batteryShutdownTemperature.length; ++n10) {
                        codedOutputByteBufferNano.writeFloat(20, this.batteryShutdownTemperature[n10]);
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class Photos extends ExtendableMessageNano<Photos> implements Cloneable
        {
            private static volatile Photos[] _emptyArray;
            public Integer numPhotos;
            public OpenMedia openMedia;
            public WarmWelcome warmWelcome;
            
            public Photos() {
                this.clear();
            }
            
            public static Photos[] emptyArray() {
                if (Photos._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Photos._emptyArray != null) {
                                    break;
                                }
                            }
                            Photos._emptyArray = new Photos[0];
                            continue;
                        }
                    }
                }
                return Photos._emptyArray;
            }
            
            public static Photos parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Photos().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Photos parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Photos(), array);
            }
            
            public final Photos clear() {
                this.numPhotos = null;
                this.openMedia = null;
                this.warmWelcome = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Photos clone() {
                while (true) {
                    Photos photos = null;
                Label_0048:
                    while (true) {
                        try {
                            photos = super.clone();
                            if (this.openMedia == null) {
                                if (this.warmWelcome == null) {
                                    return photos;
                                }
                                break Label_0048;
                            }
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                        photos.openMedia = this.openMedia.clone();
                        continue;
                    }
                    photos.warmWelcome = this.warmWelcome.clone();
                    return photos;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.numPhotos != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.numPhotos);
                }
                if (this.openMedia != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.openMedia);
                }
                if (this.warmWelcome != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.warmWelcome);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Photos mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            this.numPhotos = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 18: {
                            if (this.openMedia == null) {
                                this.openMedia = new OpenMedia();
                            }
                            codedInputByteBufferNano.readMessage(this.openMedia);
                            continue;
                        }
                        case 26: {
                            if (this.warmWelcome == null) {
                                this.warmWelcome = new WarmWelcome();
                            }
                            codedInputByteBufferNano.readMessage(this.warmWelcome);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.numPhotos != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.numPhotos);
                }
                if (this.openMedia != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.openMedia);
                }
                if (this.warmWelcome != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.warmWelcome);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class OpenMedia extends ExtendableMessageNano<OpenMedia> implements Cloneable
            {
                private static volatile OpenMedia[] _emptyArray;
                public Boolean isSample;
                public Integer source;
                public Integer type;
                
                public OpenMedia() {
                    this.clear();
                }
                
                public static OpenMedia[] emptyArray() {
                    if (OpenMedia._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (OpenMedia._emptyArray != null) {
                                        break;
                                    }
                                }
                                OpenMedia._emptyArray = new OpenMedia[0];
                                continue;
                            }
                        }
                    }
                    return OpenMedia._emptyArray;
                }
                
                public static OpenMedia parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new OpenMedia().mergeFrom(codedInputByteBufferNano);
                }
                
                public static OpenMedia parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new OpenMedia(), array);
                }
                
                public final OpenMedia clear() {
                    this.isSample = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final OpenMedia clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
                    }
                    if (this.source != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.source);
                    }
                    if (this.isSample != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.isSample);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final OpenMedia mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4: {
                                        this.type = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                final int int33 = codedInputByteBufferNano.readInt32();
                                switch (int33) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3: {
                                        this.source = int33;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 24: {
                                this.isSample = codedInputByteBufferNano.readBool();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type);
                    }
                    if (this.source != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.source);
                    }
                    if (this.isSample != null) {
                        codedOutputByteBufferNano.writeBool(3, this.isSample);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface MediaSource
                {
                    public static final int CAROUSEL = 2;
                    public static final int GALLERY = 1;
                    public static final int INTENT = 3;
                    public static final int UNSPECIFIED = 0;
                }
                
                public interface MediaType
                {
                    public static final int FLAT_PANORAMA = 2;
                    public static final int PANORAMA = 3;
                    public static final int PHOTO = 1;
                    public static final int UNKNOWN = 0;
                    public static final int VR = 4;
                }
            }
            
            public static final class WarmWelcome extends ExtendableMessageNano<WarmWelcome> implements Cloneable
            {
                private static volatile WarmWelcome[] _emptyArray;
                public Float exitProgress;
                
                public WarmWelcome() {
                    this.clear();
                }
                
                public static WarmWelcome[] emptyArray() {
                    if (WarmWelcome._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (WarmWelcome._emptyArray != null) {
                                        break;
                                    }
                                }
                                WarmWelcome._emptyArray = new WarmWelcome[0];
                                continue;
                            }
                        }
                    }
                    return WarmWelcome._emptyArray;
                }
                
                public static WarmWelcome parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new WarmWelcome().mergeFrom(codedInputByteBufferNano);
                }
                
                public static WarmWelcome parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new WarmWelcome(), array);
                }
                
                public final WarmWelcome clear() {
                    this.exitProgress = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final WarmWelcome clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.exitProgress != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.exitProgress);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final WarmWelcome mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 13: {
                                this.exitProgress = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.exitProgress != null) {
                        codedOutputByteBufferNano.writeFloat(1, this.exitProgress);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
        }
        
        public static final class QrCodeScan extends ExtendableMessageNano<QrCodeScan> implements Cloneable
        {
            private static volatile QrCodeScan[] _emptyArray;
            public String headMountConfigUrl;
            public Integer status;
            
            public QrCodeScan() {
                this.clear();
            }
            
            public static QrCodeScan[] emptyArray() {
                if (QrCodeScan._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (QrCodeScan._emptyArray != null) {
                                    break;
                                }
                            }
                            QrCodeScan._emptyArray = new QrCodeScan[0];
                            continue;
                        }
                    }
                }
                return QrCodeScan._emptyArray;
            }
            
            public static QrCodeScan parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new QrCodeScan().mergeFrom(codedInputByteBufferNano);
            }
            
            public static QrCodeScan parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new QrCodeScan(), array);
            }
            
            public final QrCodeScan clear() {
                this.headMountConfigUrl = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final QrCodeScan clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.status != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.status);
                }
                if (this.headMountConfigUrl != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.headMountConfigUrl);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final QrCodeScan mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3: {
                                    this.status = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 18: {
                            this.headMountConfigUrl = codedInputByteBufferNano.readString();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.status != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.status);
                }
                if (this.headMountConfigUrl != null) {
                    codedOutputByteBufferNano.writeString(2, this.headMountConfigUrl);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public interface Status
            {
                public static final int CONNECTION_ERROR = 3;
                public static final int OK = 1;
                public static final int UNEXPECTED_FORMAT = 2;
                public static final int UNKNOWN = 0;
            }
        }
        
        public static final class Renderer extends ExtendableMessageNano<Renderer> implements Cloneable
        {
            private static volatile Renderer[] _emptyArray;
            public String glRenderer;
            public String glVendor;
            public String glVersion;
            
            public Renderer() {
                this.clear();
            }
            
            public static Renderer[] emptyArray() {
                if (Renderer._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Renderer._emptyArray != null) {
                                    break;
                                }
                            }
                            Renderer._emptyArray = new Renderer[0];
                            continue;
                        }
                    }
                }
                return Renderer._emptyArray;
            }
            
            public static Renderer parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Renderer().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Renderer parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Renderer(), array);
            }
            
            public final Renderer clear() {
                this.glVendor = null;
                this.glRenderer = null;
                this.glVersion = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Renderer clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.glVendor != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.glVendor);
                }
                if (this.glRenderer != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.glRenderer);
                }
                if (this.glVersion != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.glVersion);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Renderer mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            this.glVendor = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 18: {
                            this.glRenderer = codedInputByteBufferNano.readString();
                            continue;
                        }
                        case 26: {
                            this.glVersion = codedInputByteBufferNano.readString();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.glVendor != null) {
                    codedOutputByteBufferNano.writeString(1, this.glVendor);
                }
                if (this.glRenderer != null) {
                    codedOutputByteBufferNano.writeString(2, this.glRenderer);
                }
                if (this.glVersion != null) {
                    codedOutputByteBufferNano.writeString(3, this.glVersion);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class SdkConfigurationParams extends ExtendableMessageNano<SdkConfigurationParams> implements Cloneable
        {
            private static volatile SdkConfigurationParams[] _emptyArray;
            public Boolean allowDynamicLibraryLoading;
            public AsyncReprojectionConfig asyncReprojectionConfig;
            public Boolean cpuLateLatchingEnabled;
            public Integer daydreamImageAlignment;
            public Boolean daydreamImageAlignmentEnabled;
            public Boolean useMagnetometerInSensorFusion;
            public Boolean useOnlineMagnetometerCalibration;
            public Boolean useSystemClockForSensorTimestamps;
            
            public SdkConfigurationParams() {
                this.clear();
            }
            
            public static SdkConfigurationParams[] emptyArray() {
                if (SdkConfigurationParams._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (SdkConfigurationParams._emptyArray != null) {
                                    break;
                                }
                            }
                            SdkConfigurationParams._emptyArray = new SdkConfigurationParams[0];
                            continue;
                        }
                    }
                }
                return SdkConfigurationParams._emptyArray;
            }
            
            public static SdkConfigurationParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new SdkConfigurationParams().mergeFrom(codedInputByteBufferNano);
            }
            
            public static SdkConfigurationParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new SdkConfigurationParams(), array);
            }
            
            public final SdkConfigurationParams clear() {
                this.daydreamImageAlignmentEnabled = null;
                this.useSystemClockForSensorTimestamps = null;
                this.useMagnetometerInSensorFusion = null;
                this.allowDynamicLibraryLoading = null;
                this.cpuLateLatchingEnabled = null;
                this.asyncReprojectionConfig = null;
                this.useOnlineMagnetometerCalibration = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final SdkConfigurationParams clone() {
                while (true) {
                    SdkConfigurationParams sdkConfigurationParams;
                    try {
                        sdkConfigurationParams = super.clone();
                        if (this.asyncReprojectionConfig == null) {
                            return sdkConfigurationParams;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    sdkConfigurationParams.asyncReprojectionConfig = this.asyncReprojectionConfig.clone();
                    return sdkConfigurationParams;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.daydreamImageAlignmentEnabled != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.daydreamImageAlignmentEnabled);
                }
                if (this.useSystemClockForSensorTimestamps != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.useSystemClockForSensorTimestamps);
                }
                if (this.useMagnetometerInSensorFusion != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.useMagnetometerInSensorFusion);
                }
                if (this.allowDynamicLibraryLoading != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, this.allowDynamicLibraryLoading);
                }
                if (this.cpuLateLatchingEnabled != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, this.cpuLateLatchingEnabled);
                }
                if (this.daydreamImageAlignment != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.daydreamImageAlignment);
                }
                if (this.asyncReprojectionConfig != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, this.asyncReprojectionConfig);
                }
                if (this.useOnlineMagnetometerCalibration != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(8, this.useOnlineMagnetometerCalibration);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final SdkConfigurationParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            this.daydreamImageAlignmentEnabled = codedInputByteBufferNano.readBool();
                            continue;
                        }
                        case 16: {
                            this.useSystemClockForSensorTimestamps = codedInputByteBufferNano.readBool();
                            continue;
                        }
                        case 24: {
                            this.useMagnetometerInSensorFusion = codedInputByteBufferNano.readBool();
                            continue;
                        }
                        case 32: {
                            this.allowDynamicLibraryLoading = codedInputByteBufferNano.readBool();
                            continue;
                        }
                        case 40: {
                            this.cpuLateLatchingEnabled = codedInputByteBufferNano.readBool();
                            continue;
                        }
                        case 48: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3: {
                                    this.daydreamImageAlignment = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 58: {
                            if (this.asyncReprojectionConfig == null) {
                                this.asyncReprojectionConfig = new AsyncReprojectionConfig();
                            }
                            codedInputByteBufferNano.readMessage(this.asyncReprojectionConfig);
                            continue;
                        }
                        case 64: {
                            this.useOnlineMagnetometerCalibration = codedInputByteBufferNano.readBool();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.daydreamImageAlignmentEnabled != null) {
                    codedOutputByteBufferNano.writeBool(1, this.daydreamImageAlignmentEnabled);
                }
                if (this.useSystemClockForSensorTimestamps != null) {
                    codedOutputByteBufferNano.writeBool(2, this.useSystemClockForSensorTimestamps);
                }
                if (this.useMagnetometerInSensorFusion != null) {
                    codedOutputByteBufferNano.writeBool(3, this.useMagnetometerInSensorFusion);
                }
                if (this.allowDynamicLibraryLoading != null) {
                    codedOutputByteBufferNano.writeBool(4, this.allowDynamicLibraryLoading);
                }
                if (this.cpuLateLatchingEnabled != null) {
                    codedOutputByteBufferNano.writeBool(5, this.cpuLateLatchingEnabled);
                }
                if (this.daydreamImageAlignment != null) {
                    codedOutputByteBufferNano.writeInt32(6, this.daydreamImageAlignment);
                }
                if (this.asyncReprojectionConfig != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.asyncReprojectionConfig);
                }
                if (this.useOnlineMagnetometerCalibration != null) {
                    codedOutputByteBufferNano.writeBool(8, this.useOnlineMagnetometerCalibration);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class AsyncReprojectionConfig extends ExtendableMessageNano<AsyncReprojectionConfig> implements Cloneable
            {
                private static volatile AsyncReprojectionConfig[] _emptyArray;
                public Long displayLatencyMicros;
                public Long flags;
                
                public AsyncReprojectionConfig() {
                    this.clear();
                }
                
                public static AsyncReprojectionConfig[] emptyArray() {
                    if (AsyncReprojectionConfig._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (AsyncReprojectionConfig._emptyArray != null) {
                                        break;
                                    }
                                }
                                AsyncReprojectionConfig._emptyArray = new AsyncReprojectionConfig[0];
                                continue;
                            }
                        }
                    }
                    return AsyncReprojectionConfig._emptyArray;
                }
                
                public static AsyncReprojectionConfig parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new AsyncReprojectionConfig().mergeFrom(codedInputByteBufferNano);
                }
                
                public static AsyncReprojectionConfig parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new AsyncReprojectionConfig(), array);
                }
                
                public final AsyncReprojectionConfig clear() {
                    this.flags = null;
                    this.displayLatencyMicros = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final AsyncReprojectionConfig clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.flags != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.flags);
                    }
                    if (this.displayLatencyMicros != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.displayLatencyMicros);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final AsyncReprojectionConfig mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.flags = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                            case 16: {
                                this.displayLatencyMicros = codedInputByteBufferNano.readInt64();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.flags != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.flags);
                    }
                    if (this.displayLatencyMicros != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.displayLatencyMicros);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Flags
                {
                    public static final int AGGRESSIVE_SCHEDULING = 4;
                    public static final int CONFIGURE_BINNING = 1;
                    public static final int EXCLUSIVE_CORE = 8;
                    public static final int LATE_LATCHING = 2;
                    public static final int UNSPECIFIED = 0;
                }
            }
            
            public interface DaydreamImageAlignment
            {
                public static final int DISABLED = 1;
                public static final int ENABLED_NO_FILTERING = 2;
                public static final int ENABLED_WITH_MEDIAN_FILTER = 3;
                public static final int UNKNOWN_ALIGNMENT = 0;
            }
        }
        
        public static final class SensorStats extends ExtendableMessageNano<SensorStats> implements Cloneable
        {
            private static volatile SensorStats[] _emptyArray;
            public GyroscopeStats gyroscopeStats;
            
            public SensorStats() {
                this.clear();
            }
            
            public static SensorStats[] emptyArray() {
                if (SensorStats._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (SensorStats._emptyArray != null) {
                                    break;
                                }
                            }
                            SensorStats._emptyArray = new SensorStats[0];
                            continue;
                        }
                    }
                }
                return SensorStats._emptyArray;
            }
            
            public static SensorStats parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new SensorStats().mergeFrom(codedInputByteBufferNano);
            }
            
            public static SensorStats parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new SensorStats(), array);
            }
            
            public final SensorStats clear() {
                this.gyroscopeStats = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final SensorStats clone() {
                while (true) {
                    SensorStats sensorStats;
                    try {
                        sensorStats = super.clone();
                        if (this.gyroscopeStats == null) {
                            return sensorStats;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    sensorStats.gyroscopeStats = this.gyroscopeStats.clone();
                    return sensorStats;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.gyroscopeStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.gyroscopeStats);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final SensorStats mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            if (this.gyroscopeStats == null) {
                                this.gyroscopeStats = new GyroscopeStats();
                            }
                            codedInputByteBufferNano.readMessage(this.gyroscopeStats);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.gyroscopeStats != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.gyroscopeStats);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class GyroscopeStats extends ExtendableMessageNano<GyroscopeStats> implements Cloneable
            {
                private static volatile GyroscopeStats[] _emptyArray;
                public Vector3 bias;
                public Vector3 lowerBound;
                public Vector3 standardDeviation;
                public Vector3 upperBound;
                
                public GyroscopeStats() {
                    this.clear();
                }
                
                public static GyroscopeStats[] emptyArray() {
                    if (GyroscopeStats._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (GyroscopeStats._emptyArray != null) {
                                        break;
                                    }
                                }
                                GyroscopeStats._emptyArray = new GyroscopeStats[0];
                                continue;
                            }
                        }
                    }
                    return GyroscopeStats._emptyArray;
                }
                
                public static GyroscopeStats parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new GyroscopeStats().mergeFrom(codedInputByteBufferNano);
                }
                
                public static GyroscopeStats parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new GyroscopeStats(), array);
                }
                
                public final GyroscopeStats clear() {
                    this.bias = null;
                    this.lowerBound = null;
                    this.upperBound = null;
                    this.standardDeviation = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final GyroscopeStats clone() {
                    GyroscopeStats gyroscopeStats = null;
                    Label_0022_Outer:Label_0029_Outer:
                    while (true) {
                    Label_0090:
                        while (true) {
                        Label_0076:
                            while (true) {
                            Label_0062:
                                while (true) {
                                    try {
                                        gyroscopeStats = super.clone();
                                        if (this.bias == null) {
                                            if (this.lowerBound != null) {
                                                break Label_0062;
                                            }
                                            if (this.upperBound != null) {
                                                break Label_0076;
                                            }
                                            if (this.standardDeviation == null) {
                                                return gyroscopeStats;
                                            }
                                            break Label_0090;
                                        }
                                    }
                                    catch (final CloneNotSupportedException detailMessage) {
                                        throw new AssertionError((Object)detailMessage);
                                    }
                                    gyroscopeStats.bias = this.bias.clone();
                                    continue Label_0022_Outer;
                                }
                                gyroscopeStats.lowerBound = this.lowerBound.clone();
                                continue Label_0029_Outer;
                            }
                            gyroscopeStats.upperBound = this.upperBound.clone();
                            continue;
                        }
                        gyroscopeStats.standardDeviation = this.standardDeviation.clone();
                        return gyroscopeStats;
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.bias != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.bias);
                    }
                    if (this.lowerBound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.lowerBound);
                    }
                    if (this.upperBound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.upperBound);
                    }
                    if (this.standardDeviation != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.standardDeviation);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final GyroscopeStats mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                if (this.bias == null) {
                                    this.bias = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.bias);
                                continue;
                            }
                            case 18: {
                                if (this.lowerBound == null) {
                                    this.lowerBound = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.lowerBound);
                                continue;
                            }
                            case 26: {
                                if (this.upperBound == null) {
                                    this.upperBound = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.upperBound);
                                continue;
                            }
                            case 34: {
                                if (this.standardDeviation == null) {
                                    this.standardDeviation = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.standardDeviation);
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.bias != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.bias);
                    }
                    if (this.lowerBound != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.lowerBound);
                    }
                    if (this.upperBound != null) {
                        codedOutputByteBufferNano.writeMessage(3, this.upperBound);
                    }
                    if (this.standardDeviation != null) {
                        codedOutputByteBufferNano.writeMessage(4, this.standardDeviation);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
            
            public static final class Vector3 extends ExtendableMessageNano<Vector3> implements Cloneable
            {
                private static volatile Vector3[] _emptyArray;
                public Float x;
                public Float y;
                public Float z;
                
                public Vector3() {
                    this.clear();
                }
                
                public static Vector3[] emptyArray() {
                    if (Vector3._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Vector3._emptyArray != null) {
                                        break;
                                    }
                                }
                                Vector3._emptyArray = new Vector3[0];
                                continue;
                            }
                        }
                    }
                    return Vector3._emptyArray;
                }
                
                public static Vector3 parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Vector3().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Vector3 parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Vector3(), array);
                }
                
                public final Vector3 clear() {
                    this.x = null;
                    this.y = null;
                    this.z = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Vector3 clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.x != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.x);
                    }
                    if (this.y != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.y);
                    }
                    if (this.z != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.z);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Vector3 mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 13: {
                                this.x = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                            case 21: {
                                this.y = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                            case 29: {
                                this.z = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.x != null) {
                        codedOutputByteBufferNano.writeFloat(1, this.x);
                    }
                    if (this.y != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.y);
                    }
                    if (this.z != null) {
                        codedOutputByteBufferNano.writeFloat(3, this.z);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
        }
        
        public static final class StreetView extends ExtendableMessageNano<StreetView> implements Cloneable
        {
            private static volatile StreetView[] _emptyArray;
            public PanoSession panoSession;
            
            public StreetView() {
                this.clear();
            }
            
            public static StreetView[] emptyArray() {
                if (StreetView._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (StreetView._emptyArray != null) {
                                    break;
                                }
                            }
                            StreetView._emptyArray = new StreetView[0];
                            continue;
                        }
                    }
                }
                return StreetView._emptyArray;
            }
            
            public static StreetView parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new StreetView().mergeFrom(codedInputByteBufferNano);
            }
            
            public static StreetView parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new StreetView(), array);
            }
            
            public final StreetView clear() {
                this.panoSession = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final StreetView clone() {
                while (true) {
                    StreetView streetView;
                    try {
                        streetView = super.clone();
                        if (this.panoSession == null) {
                            return streetView;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    streetView.panoSession = this.panoSession.clone();
                    return streetView;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.panoSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.panoSession);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final StreetView mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            if (this.panoSession == null) {
                                this.panoSession = new PanoSession();
                            }
                            codedInputByteBufferNano.readMessage(this.panoSession);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.panoSession != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.panoSession);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class PanoSession extends ExtendableMessageNano<PanoSession> implements Cloneable
            {
                private static volatile PanoSession[] _emptyArray;
                public Integer infoClicks;
                public Integer nextClicks;
                public Integer nodesNavigated;
                public Integer panosAvailable;
                public Integer panosViewed;
                public Integer playPauseClicks;
                public Integer prevClicks;
                public Integer source;
                
                public PanoSession() {
                    this.clear();
                }
                
                public static PanoSession[] emptyArray() {
                    if (PanoSession._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (PanoSession._emptyArray != null) {
                                        break;
                                    }
                                }
                                PanoSession._emptyArray = new PanoSession[0];
                                continue;
                            }
                        }
                    }
                    return PanoSession._emptyArray;
                }
                
                public static PanoSession parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new PanoSession().mergeFrom(codedInputByteBufferNano);
                }
                
                public static PanoSession parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new PanoSession(), array);
                }
                
                public final PanoSession clear() {
                    this.panosAvailable = null;
                    this.panosViewed = null;
                    this.nodesNavigated = null;
                    this.nextClicks = null;
                    this.prevClicks = null;
                    this.playPauseClicks = null;
                    this.infoClicks = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final PanoSession clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.source != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.source);
                    }
                    if (this.panosAvailable != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.panosAvailable);
                    }
                    if (this.panosViewed != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.panosViewed);
                    }
                    if (this.nodesNavigated != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.nodesNavigated);
                    }
                    if (this.nextClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.nextClicks);
                    }
                    if (this.prevClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.prevClicks);
                    }
                    if (this.playPauseClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, this.playPauseClicks);
                    }
                    if (this.infoClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.infoClicks);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final PanoSession mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4: {
                                        this.source = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 16: {
                                this.panosAvailable = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 24: {
                                this.panosViewed = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 32: {
                                this.nodesNavigated = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 40: {
                                this.nextClicks = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 48: {
                                this.prevClicks = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 56: {
                                this.playPauseClicks = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 64: {
                                this.infoClicks = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.source != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.source);
                    }
                    if (this.panosAvailable != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.panosAvailable);
                    }
                    if (this.panosViewed != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.panosViewed);
                    }
                    if (this.nodesNavigated != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.nodesNavigated);
                    }
                    if (this.nextClicks != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.nextClicks);
                    }
                    if (this.prevClicks != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.prevClicks);
                    }
                    if (this.playPauseClicks != null) {
                        codedOutputByteBufferNano.writeInt32(7, this.playPauseClicks);
                    }
                    if (this.infoClicks != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.infoClicks);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Source
                {
                    public static final int SOURCE_COLLECTION = 1;
                    public static final int SOURCE_PANO = 2;
                    public static final int SOURCE_SEARCH = 3;
                    public static final int SOURCE_SEARCH_RESULTS = 4;
                    public static final int SOURCE_UNKNOWN = 0;
                }
            }
        }
        
        public static final class TimeSeriesData extends ExtendableMessageNano<TimeSeriesData> implements Cloneable
        {
            private static volatile TimeSeriesData[] _emptyArray;
            public TimeIntervalData[] timeIntervalData;
            public Integer timeIntervalSeconds;
            
            public TimeSeriesData() {
                this.clear();
            }
            
            public static TimeSeriesData[] emptyArray() {
                if (TimeSeriesData._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (TimeSeriesData._emptyArray != null) {
                                    break;
                                }
                            }
                            TimeSeriesData._emptyArray = new TimeSeriesData[0];
                            continue;
                        }
                    }
                }
                return TimeSeriesData._emptyArray;
            }
            
            public static TimeSeriesData parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new TimeSeriesData().mergeFrom(codedInputByteBufferNano);
            }
            
            public static TimeSeriesData parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new TimeSeriesData(), array);
            }
            
            public final TimeSeriesData clear() {
                this.timeIntervalSeconds = null;
                this.timeIntervalData = TimeIntervalData.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final TimeSeriesData clone() {
                while (true) {
                    int i = 0;
                    TimeSeriesData timeSeriesData;
                    try {
                        timeSeriesData = super.clone();
                        if (this.timeIntervalData == null) {
                            return timeSeriesData;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    if (this.timeIntervalData.length > 0) {
                        timeSeriesData.timeIntervalData = new TimeIntervalData[this.timeIntervalData.length];
                        while (i < this.timeIntervalData.length) {
                            if (this.timeIntervalData[i] != null) {
                                timeSeriesData.timeIntervalData[i] = this.timeIntervalData[i].clone();
                            }
                            ++i;
                        }
                        return timeSeriesData;
                    }
                    return timeSeriesData;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.timeIntervalSeconds != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.timeIntervalSeconds);
                }
                int n;
                if (this.timeIntervalData == null) {
                    n = computeSerializedSize;
                }
                else {
                    n = computeSerializedSize;
                    if (this.timeIntervalData.length > 0) {
                        for (int i = 0; i < this.timeIntervalData.length; ++i) {
                            final TimeIntervalData timeIntervalData = this.timeIntervalData[i];
                            if (timeIntervalData != null) {
                                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, timeIntervalData);
                            }
                        }
                        n = computeSerializedSize;
                    }
                }
                return n;
            }
            
            @Override
            public final TimeSeriesData mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            this.timeIntervalSeconds = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 18: {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int i;
                            if (this.timeIntervalData != null) {
                                i = this.timeIntervalData.length;
                            }
                            else {
                                i = 0;
                            }
                            final TimeIntervalData[] timeIntervalData = new TimeIntervalData[repeatedFieldArrayLength + i];
                            if (i != 0) {
                                System.arraycopy(this.timeIntervalData, 0, timeIntervalData, 0, i);
                            }
                            while (i < timeIntervalData.length - 1) {
                                codedInputByteBufferNano.readMessage(timeIntervalData[i] = new TimeIntervalData());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(timeIntervalData[i] = new TimeIntervalData());
                            this.timeIntervalData = timeIntervalData;
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = 0;
                if (this.timeIntervalSeconds != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.timeIntervalSeconds);
                }
                if (this.timeIntervalData != null && this.timeIntervalData.length > 0) {
                    while (i < this.timeIntervalData.length) {
                        final TimeIntervalData timeIntervalData = this.timeIntervalData[i];
                        if (timeIntervalData != null) {
                            codedOutputByteBufferNano.writeMessage(2, timeIntervalData);
                        }
                        ++i;
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class TimeIntervalData extends ExtendableMessageNano<TimeIntervalData> implements Cloneable
            {
                private static volatile TimeIntervalData[] _emptyArray;
                public Integer batteryLevel;
                public Integer batteryLevelDelta;
                public float[] batteryTemperature;
                public float[] cpuTemperature;
                public Integer edsThreadFrameDropCount;
                public float[] gpuTemperature;
                public Integer intervalStartTimeSeconds;
                public Float skinTemperature;
                public Integer thermalWarningsShown;
                
                public TimeIntervalData() {
                    this.clear();
                }
                
                public static TimeIntervalData[] emptyArray() {
                    if (TimeIntervalData._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (TimeIntervalData._emptyArray != null) {
                                        break;
                                    }
                                }
                                TimeIntervalData._emptyArray = new TimeIntervalData[0];
                                continue;
                            }
                        }
                    }
                    return TimeIntervalData._emptyArray;
                }
                
                public static TimeIntervalData parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new TimeIntervalData().mergeFrom(codedInputByteBufferNano);
                }
                
                public static TimeIntervalData parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new TimeIntervalData(), array);
                }
                
                public final TimeIntervalData clear() {
                    this.intervalStartTimeSeconds = null;
                    this.skinTemperature = null;
                    this.edsThreadFrameDropCount = null;
                    this.batteryLevel = null;
                    this.batteryLevelDelta = null;
                    this.thermalWarningsShown = null;
                    this.cpuTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.gpuTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.batteryTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final TimeIntervalData clone() {
                    // 
                    // This method could not be decompiled.
                    // 
                    // Original Bytecode:
                    // 
                    //     1: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
                    //     4: checkcast       Lcom/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData;
                    //     7: astore_1       
                    //     8: aload_0        
                    //     9: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.cpuTemperature:[F
                    //    12: ifnonnull       41
                    //    15: aload_0        
                    //    16: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.gpuTemperature:[F
                    //    19: ifnonnull       66
                    //    22: aload_0        
                    //    23: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.batteryTemperature:[F
                    //    26: ifnonnull       91
                    //    29: aload_1        
                    //    30: areturn        
                    //    31: astore_1       
                    //    32: new             Ljava/lang/AssertionError;
                    //    35: dup            
                    //    36: aload_1        
                    //    37: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
                    //    40: athrow         
                    //    41: aload_0        
                    //    42: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.cpuTemperature:[F
                    //    45: arraylength    
                    //    46: ifle            15
                    //    49: aload_1        
                    //    50: aload_0        
                    //    51: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.cpuTemperature:[F
                    //    54: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                    //    57: checkcast       [F
                    //    60: putfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.cpuTemperature:[F
                    //    63: goto            15
                    //    66: aload_0        
                    //    67: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.gpuTemperature:[F
                    //    70: arraylength    
                    //    71: ifle            22
                    //    74: aload_1        
                    //    75: aload_0        
                    //    76: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.gpuTemperature:[F
                    //    79: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                    //    82: checkcast       [F
                    //    85: putfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.gpuTemperature:[F
                    //    88: goto            22
                    //    91: aload_0        
                    //    92: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.batteryTemperature:[F
                    //    95: arraylength    
                    //    96: ifle            29
                    //    99: aload_1        
                    //   100: aload_0        
                    //   101: getfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.batteryTemperature:[F
                    //   104: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
                    //   107: checkcast       [F
                    //   110: putfield        com/google/common/logging/nano/Vr$VREvent$TimeSeriesData$TimeIntervalData.batteryTemperature:[F
                    //   113: goto            29
                    //    Exceptions:
                    //  Try           Handler
                    //  Start  End    Start  End    Type                                  
                    //  -----  -----  -----  -----  --------------------------------------
                    //  0      8      31     41     Ljava/lang/CloneNotSupportedException;
                    // 
                    // The error that occurred was:
                    // 
                    // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:float[](TimeIntervalData::batteryTemperature, this:TimeIntervalData)), ldc:int(0))
                    //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                    //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                    //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.intervalStartTimeSeconds != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.intervalStartTimeSeconds);
                    }
                    if (this.skinTemperature != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.skinTemperature);
                    }
                    if (this.edsThreadFrameDropCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.edsThreadFrameDropCount);
                    }
                    int n;
                    if (this.batteryLevel == null) {
                        n = computeSerializedSize;
                    }
                    else {
                        n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, this.batteryLevel);
                    }
                    if (this.batteryLevelDelta != null) {
                        n += CodedOutputByteBufferNano.computeInt32Size(5, this.batteryLevelDelta);
                    }
                    if (this.thermalWarningsShown != null) {
                        n += CodedOutputByteBufferNano.computeInt32Size(6, this.thermalWarningsShown);
                    }
                    int n2;
                    if (this.cpuTemperature == null) {
                        n2 = n;
                    }
                    else {
                        n2 = n;
                        if (this.cpuTemperature.length > 0) {
                            n2 = n + this.cpuTemperature.length * 4 + this.cpuTemperature.length * 1;
                        }
                    }
                    int n3;
                    if (this.gpuTemperature == null) {
                        n3 = n2;
                    }
                    else {
                        n3 = n2;
                        if (this.gpuTemperature.length > 0) {
                            n3 = n2 + this.gpuTemperature.length * 4 + this.gpuTemperature.length * 1;
                        }
                    }
                    int n4;
                    if (this.batteryTemperature == null) {
                        n4 = n3;
                    }
                    else {
                        n4 = n3;
                        if (this.batteryTemperature.length > 0) {
                            n4 = n3 + this.batteryTemperature.length * 4 + this.batteryTemperature.length * 1;
                        }
                    }
                    return n4;
                }
                
                @Override
                public final TimeIntervalData mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 8: {
                                this.intervalStartTimeSeconds = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 21: {
                                this.skinTemperature = codedInputByteBufferNano.readFloat();
                                continue;
                            }
                            case 24: {
                                this.edsThreadFrameDropCount = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 32: {
                                this.batteryLevel = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 40: {
                                this.batteryLevelDelta = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 48: {
                                this.thermalWarningsShown = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 61: {
                                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 61);
                                int i;
                                if (this.cpuTemperature != null) {
                                    i = this.cpuTemperature.length;
                                }
                                else {
                                    i = 0;
                                }
                                final float[] cpuTemperature = new float[repeatedFieldArrayLength + i];
                                if (i != 0) {
                                    System.arraycopy(this.cpuTemperature, 0, cpuTemperature, 0, i);
                                }
                                while (i < cpuTemperature.length - 1) {
                                    cpuTemperature[i] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    ++i;
                                }
                                cpuTemperature[i] = codedInputByteBufferNano.readFloat();
                                this.cpuTemperature = cpuTemperature;
                                continue;
                            }
                            case 58: {
                                final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                                final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                                final int n = rawVarint32 / 4;
                                int j;
                                if (this.cpuTemperature != null) {
                                    j = this.cpuTemperature.length;
                                }
                                else {
                                    j = 0;
                                }
                                final float[] cpuTemperature2 = new float[n + j];
                                if (j != 0) {
                                    System.arraycopy(this.cpuTemperature, 0, cpuTemperature2, 0, j);
                                }
                                while (j < cpuTemperature2.length) {
                                    cpuTemperature2[j] = codedInputByteBufferNano.readFloat();
                                    ++j;
                                }
                                this.cpuTemperature = cpuTemperature2;
                                codedInputByteBufferNano.popLimit(pushLimit);
                                continue;
                            }
                            case 69: {
                                final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 69);
                                int k;
                                if (this.gpuTemperature != null) {
                                    k = this.gpuTemperature.length;
                                }
                                else {
                                    k = 0;
                                }
                                final float[] gpuTemperature = new float[repeatedFieldArrayLength2 + k];
                                if (k != 0) {
                                    System.arraycopy(this.gpuTemperature, 0, gpuTemperature, 0, k);
                                }
                                while (k < gpuTemperature.length - 1) {
                                    gpuTemperature[k] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    ++k;
                                }
                                gpuTemperature[k] = codedInputByteBufferNano.readFloat();
                                this.gpuTemperature = gpuTemperature;
                                continue;
                            }
                            case 66: {
                                final int rawVarint33 = codedInputByteBufferNano.readRawVarint32();
                                final int pushLimit2 = codedInputByteBufferNano.pushLimit(rawVarint33);
                                final int n2 = rawVarint33 / 4;
                                int l;
                                if (this.gpuTemperature != null) {
                                    l = this.gpuTemperature.length;
                                }
                                else {
                                    l = 0;
                                }
                                final float[] gpuTemperature2 = new float[n2 + l];
                                if (l != 0) {
                                    System.arraycopy(this.gpuTemperature, 0, gpuTemperature2, 0, l);
                                }
                                while (l < gpuTemperature2.length) {
                                    gpuTemperature2[l] = codedInputByteBufferNano.readFloat();
                                    ++l;
                                }
                                this.gpuTemperature = gpuTemperature2;
                                codedInputByteBufferNano.popLimit(pushLimit2);
                                continue;
                            }
                            case 77: {
                                final int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 77);
                                int length;
                                if (this.batteryTemperature != null) {
                                    length = this.batteryTemperature.length;
                                }
                                else {
                                    length = 0;
                                }
                                final float[] batteryTemperature = new float[repeatedFieldArrayLength3 + length];
                                if (length != 0) {
                                    System.arraycopy(this.batteryTemperature, 0, batteryTemperature, 0, length);
                                }
                                while (length < batteryTemperature.length - 1) {
                                    batteryTemperature[length] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    ++length;
                                }
                                batteryTemperature[length] = codedInputByteBufferNano.readFloat();
                                this.batteryTemperature = batteryTemperature;
                                continue;
                            }
                            case 74: {
                                final int rawVarint34 = codedInputByteBufferNano.readRawVarint32();
                                final int pushLimit3 = codedInputByteBufferNano.pushLimit(rawVarint34);
                                final int n3 = rawVarint34 / 4;
                                int length2;
                                if (this.batteryTemperature != null) {
                                    length2 = this.batteryTemperature.length;
                                }
                                else {
                                    length2 = 0;
                                }
                                final float[] batteryTemperature2 = new float[n3 + length2];
                                if (length2 != 0) {
                                    System.arraycopy(this.batteryTemperature, 0, batteryTemperature2, 0, length2);
                                }
                                while (length2 < batteryTemperature2.length) {
                                    batteryTemperature2[length2] = codedInputByteBufferNano.readFloat();
                                    ++length2;
                                }
                                this.batteryTemperature = batteryTemperature2;
                                codedInputByteBufferNano.popLimit(pushLimit3);
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    final int n = 0;
                    if (this.intervalStartTimeSeconds != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.intervalStartTimeSeconds);
                    }
                    if (this.skinTemperature != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.skinTemperature);
                    }
                    if (this.edsThreadFrameDropCount != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.edsThreadFrameDropCount);
                    }
                    if (this.batteryLevel != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.batteryLevel);
                    }
                    if (this.batteryLevelDelta != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.batteryLevelDelta);
                    }
                    if (this.thermalWarningsShown != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.thermalWarningsShown);
                    }
                    if (this.cpuTemperature != null && this.cpuTemperature.length > 0) {
                        for (int i = 0; i < this.cpuTemperature.length; ++i) {
                            codedOutputByteBufferNano.writeFloat(7, this.cpuTemperature[i]);
                        }
                    }
                    if (this.gpuTemperature != null && this.gpuTemperature.length > 0) {
                        for (int j = 0; j < this.gpuTemperature.length; ++j) {
                            codedOutputByteBufferNano.writeFloat(8, this.gpuTemperature[j]);
                        }
                    }
                    if (this.batteryTemperature != null && this.batteryTemperature.length > 0) {
                        for (int k = n; k < this.batteryTemperature.length; ++k) {
                            codedOutputByteBufferNano.writeFloat(9, this.batteryTemperature[k]);
                        }
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }
        }
        
        public static final class Transform extends ExtendableMessageNano<Transform> implements Cloneable
        {
            private static volatile Transform[] _emptyArray;
            public Float rotationQx;
            public Float rotationQy;
            public Float rotationQz;
            public Float scale;
            public Float translationX;
            public Float translationY;
            public Float translationZ;
            
            public Transform() {
                this.clear();
            }
            
            public static Transform[] emptyArray() {
                if (Transform._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (Transform._emptyArray != null) {
                                    break;
                                }
                            }
                            Transform._emptyArray = new Transform[0];
                            continue;
                        }
                    }
                }
                return Transform._emptyArray;
            }
            
            public static Transform parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Transform().mergeFrom(codedInputByteBufferNano);
            }
            
            public static Transform parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new Transform(), array);
            }
            
            public final Transform clear() {
                this.translationX = null;
                this.translationY = null;
                this.translationZ = null;
                this.rotationQx = null;
                this.rotationQy = null;
                this.rotationQz = null;
                this.scale = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final Transform clone() {
                try {
                    return super.clone();
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.translationX != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.translationX);
                }
                if (this.translationY != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.translationY);
                }
                if (this.translationZ != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.translationZ);
                }
                if (this.rotationQx != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.rotationQx);
                }
                if (this.rotationQy != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(5, this.rotationQy);
                }
                if (this.rotationQz != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(6, this.rotationQz);
                }
                if (this.scale != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(7, this.scale);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final Transform mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 13: {
                            this.translationX = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 21: {
                            this.translationY = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 29: {
                            this.translationZ = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 37: {
                            this.rotationQx = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 45: {
                            this.rotationQy = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 53: {
                            this.rotationQz = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                        case 61: {
                            this.scale = codedInputByteBufferNano.readFloat();
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.translationX != null) {
                    codedOutputByteBufferNano.writeFloat(1, this.translationX);
                }
                if (this.translationY != null) {
                    codedOutputByteBufferNano.writeFloat(2, this.translationY);
                }
                if (this.translationZ != null) {
                    codedOutputByteBufferNano.writeFloat(3, this.translationZ);
                }
                if (this.rotationQx != null) {
                    codedOutputByteBufferNano.writeFloat(4, this.rotationQx);
                }
                if (this.rotationQy != null) {
                    codedOutputByteBufferNano.writeFloat(5, this.rotationQy);
                }
                if (this.rotationQz != null) {
                    codedOutputByteBufferNano.writeFloat(6, this.rotationQz);
                }
                if (this.scale != null) {
                    codedOutputByteBufferNano.writeFloat(7, this.scale);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class VrCore extends ExtendableMessageNano<VrCore> implements Cloneable
        {
            private static volatile VrCore[] _emptyArray;
            public Integer clientApiVersion;
            public Controller controller;
            public Integer errorCode;
            public Application foregroundApplication;
            public Integer permission;
            public Application previousForegroundApplication;
            
            public VrCore() {
                this.clear();
            }
            
            public static VrCore[] emptyArray() {
                if (VrCore._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (VrCore._emptyArray != null) {
                                    break;
                                }
                            }
                            VrCore._emptyArray = new VrCore[0];
                            continue;
                        }
                    }
                }
                return VrCore._emptyArray;
            }
            
            public static VrCore parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new VrCore().mergeFrom(codedInputByteBufferNano);
            }
            
            public static VrCore parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new VrCore(), array);
            }
            
            public final VrCore clear() {
                this.foregroundApplication = null;
                this.clientApiVersion = null;
                this.previousForegroundApplication = null;
                this.controller = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final VrCore clone() {
            Label_0022_Outer:
                while (true) {
                    VrCore vrCore = null;
                Label_0069:
                    while (true) {
                    Label_0055:
                        while (true) {
                            try {
                                vrCore = super.clone();
                                if (this.foregroundApplication == null) {
                                    if (this.previousForegroundApplication != null) {
                                        break Label_0055;
                                    }
                                    if (this.controller == null) {
                                        return vrCore;
                                    }
                                    break Label_0069;
                                }
                            }
                            catch (final CloneNotSupportedException detailMessage) {
                                throw new AssertionError((Object)detailMessage);
                            }
                            vrCore.foregroundApplication = this.foregroundApplication.clone();
                            continue Label_0022_Outer;
                        }
                        vrCore.previousForegroundApplication = this.previousForegroundApplication.clone();
                        continue;
                    }
                    vrCore.controller = this.controller.clone();
                    return vrCore;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.errorCode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.errorCode);
                }
                if (this.permission != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.permission);
                }
                if (this.foregroundApplication != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.foregroundApplication);
                }
                if (this.clientApiVersion != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.clientApiVersion);
                }
                if (this.previousForegroundApplication != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, this.previousForegroundApplication);
                }
                if (this.controller != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, this.controller);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final VrCore mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 8: {
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 101:
                                case 102:
                                case 103:
                                case 104:
                                case 105:
                                case 106:
                                case 107:
                                case 108:
                                case 109:
                                case 110:
                                case 111:
                                case 112:
                                case 113:
                                case 114:
                                case 115:
                                case 116:
                                case 117:
                                case 118:
                                case 119:
                                case 120:
                                case 121:
                                case 122:
                                case 123:
                                case 124:
                                case 125:
                                case 126:
                                case 127:
                                case 151:
                                case 152:
                                case 153:
                                case 176:
                                case 177:
                                case 178:
                                case 179:
                                case 180:
                                case 181:
                                case 182:
                                case 183:
                                case 184:
                                case 185:
                                case 186:
                                case 187:
                                case 188:
                                case 189:
                                case 190:
                                case 191:
                                case 201:
                                case 202:
                                case 203:
                                case 301:
                                case 401:
                                case 402: {
                                    this.errorCode = int32;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 16: {
                            final int int33 = codedInputByteBufferNano.readInt32();
                            switch (int33) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8: {
                                    this.permission = int33;
                                    continue;
                                }
                            }
                            break;
                        }
                        case 26: {
                            if (this.foregroundApplication == null) {
                                this.foregroundApplication = new Application();
                            }
                            codedInputByteBufferNano.readMessage(this.foregroundApplication);
                            continue;
                        }
                        case 32: {
                            this.clientApiVersion = codedInputByteBufferNano.readInt32();
                            continue;
                        }
                        case 42: {
                            if (this.previousForegroundApplication == null) {
                                this.previousForegroundApplication = new Application();
                            }
                            codedInputByteBufferNano.readMessage(this.previousForegroundApplication);
                            continue;
                        }
                        case 50: {
                            if (this.controller == null) {
                                this.controller = new Controller();
                            }
                            codedInputByteBufferNano.readMessage(this.controller);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.errorCode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.errorCode);
                }
                if (this.permission != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.permission);
                }
                if (this.foregroundApplication != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.foregroundApplication);
                }
                if (this.clientApiVersion != null) {
                    codedOutputByteBufferNano.writeInt32(4, this.clientApiVersion);
                }
                if (this.previousForegroundApplication != null) {
                    codedOutputByteBufferNano.writeMessage(5, this.previousForegroundApplication);
                }
                if (this.controller != null) {
                    codedOutputByteBufferNano.writeMessage(6, this.controller);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class Controller extends ExtendableMessageNano<Controller> implements Cloneable
            {
                private static volatile Controller[] _emptyArray;
                public String availableFirmware;
                public Integer axis;
                public Integer batteryLevel;
                public String firmware;
                public String hardwareRevision;
                public String manufacturer;
                public String model;
                public Integer sampleCount;
                public Integer sensorType;
                public String softwareRevision;
                public Integer xRailCount;
                public Integer yRailCount;
                public Integer zRailCount;
                
                public Controller() {
                    this.clear();
                }
                
                public static Controller[] emptyArray() {
                    if (Controller._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Controller._emptyArray != null) {
                                        break;
                                    }
                                }
                                Controller._emptyArray = new Controller[0];
                                continue;
                            }
                        }
                    }
                    return Controller._emptyArray;
                }
                
                public static Controller parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Controller().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Controller parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Controller(), array);
                }
                
                public final Controller clear() {
                    this.manufacturer = null;
                    this.model = null;
                    this.firmware = null;
                    this.availableFirmware = null;
                    this.softwareRevision = null;
                    this.batteryLevel = null;
                    this.hardwareRevision = null;
                    this.xRailCount = null;
                    this.yRailCount = null;
                    this.zRailCount = null;
                    this.sampleCount = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Controller clone() {
                    try {
                        return super.clone();
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.manufacturer != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.manufacturer);
                    }
                    if (this.model != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.model);
                    }
                    if (this.firmware != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.firmware);
                    }
                    if (this.availableFirmware != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.availableFirmware);
                    }
                    if (this.softwareRevision != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(5, this.softwareRevision);
                    }
                    if (this.batteryLevel != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.batteryLevel);
                    }
                    if (this.hardwareRevision != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.hardwareRevision);
                    }
                    if (this.xRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.xRailCount);
                    }
                    if (this.yRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, this.yRailCount);
                    }
                    if (this.zRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, this.zRailCount);
                    }
                    if (this.sampleCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, this.sampleCount);
                    }
                    if (this.sensorType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, this.sensorType);
                    }
                    if (this.axis != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, this.axis);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Controller mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                this.manufacturer = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 18: {
                                this.model = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 26: {
                                this.firmware = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 34: {
                                this.availableFirmware = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 42: {
                                this.softwareRevision = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 48: {
                                this.batteryLevel = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 58: {
                                this.hardwareRevision = codedInputByteBufferNano.readString();
                                continue;
                            }
                            case 64: {
                                this.xRailCount = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 72: {
                                this.yRailCount = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 80: {
                                this.zRailCount = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 88: {
                                this.sampleCount = codedInputByteBufferNano.readInt32();
                                continue;
                            }
                            case 96: {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                switch (int32) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2: {
                                        this.sensorType = int32;
                                        continue;
                                    }
                                }
                                break;
                            }
                            case 104: {
                                final int int33 = codedInputByteBufferNano.readInt32();
                                switch (int33) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3: {
                                        this.axis = int33;
                                        continue;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.manufacturer != null) {
                        codedOutputByteBufferNano.writeString(1, this.manufacturer);
                    }
                    if (this.model != null) {
                        codedOutputByteBufferNano.writeString(2, this.model);
                    }
                    if (this.firmware != null) {
                        codedOutputByteBufferNano.writeString(3, this.firmware);
                    }
                    if (this.availableFirmware != null) {
                        codedOutputByteBufferNano.writeString(4, this.availableFirmware);
                    }
                    if (this.softwareRevision != null) {
                        codedOutputByteBufferNano.writeString(5, this.softwareRevision);
                    }
                    if (this.batteryLevel != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.batteryLevel);
                    }
                    if (this.hardwareRevision != null) {
                        codedOutputByteBufferNano.writeString(7, this.hardwareRevision);
                    }
                    if (this.xRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.xRailCount);
                    }
                    if (this.yRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(9, this.yRailCount);
                    }
                    if (this.zRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(10, this.zRailCount);
                    }
                    if (this.sampleCount != null) {
                        codedOutputByteBufferNano.writeInt32(11, this.sampleCount);
                    }
                    if (this.sensorType != null) {
                        codedOutputByteBufferNano.writeInt32(12, this.sensorType);
                    }
                    if (this.axis != null) {
                        codedOutputByteBufferNano.writeInt32(13, this.axis);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface ControllerAxis
                {
                    public static final int AXIS_UNKNOWN = 0;
                    public static final int AXIS_X = 1;
                    public static final int AXIS_Y = 2;
                    public static final int AXIS_Z = 3;
                }
                
                public interface SensorType
                {
                    public static final int SENSOR_ACCEL = 2;
                    public static final int SENSOR_GYRO = 1;
                    public static final int SENSOR_UNKNOWN = 0;
                }
            }
            
            public interface ErrorCode
            {
                public static final int BAD_STATE = 1;
                public static final int CONTROLLER_BATTERY_READ_FAILED = 125;
                public static final int CONTROLLER_BATTERY_TOO_LOW = 101;
                public static final int CONTROLLER_BLUETOOTH_ERROR = 102;
                public static final int CONTROLLER_BLUETOOTH_OFF = 103;
                public static final int CONTROLLER_BLUETOOTH_SCAN_FAILURE = 116;
                public static final int CONTROLLER_BOND_FAILED = 104;
                public static final int CONTROLLER_CONNECTION_FAILURE = 105;
                public static final int CONTROLLER_CREATE_BOND_FAILURE = 117;
                public static final int CONTROLLER_FIRMWARE_ERROR = 113;
                public static final int CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND = 123;
                public static final int CONTROLLER_GATT_DISCOVERY_FAILED = 121;
                public static final int CONTROLLER_GATT_NOTIFY_FAILED = 124;
                public static final int CONTROLLER_GATT_SERVICE_NOT_FOUND = 122;
                public static final int CONTROLLER_HANDSHAKE_FAILURE = 106;
                public static final int CONTROLLER_INFO_READ_ERROR = 120;
                public static final int CONTROLLER_INSUFFICIENT_PERMS = 107;
                public static final int CONTROLLER_INVALID_MANUFACTURER = 114;
                public static final int CONTROLLER_LOST_CONNECTION = 108;
                public static final int CONTROLLER_MISMATCH = 109;
                public static final int CONTROLLER_NOT_BONDED = 118;
                public static final int CONTROLLER_NOT_FOUND = 110;
                public static final int CONTROLLER_OTA_SERVICE_ERROR = 115;
                public static final int CONTROLLER_PROTOCOL_FAILURE = 111;
                public static final int CONTROLLER_STUCK = 126;
                public static final int CONTROLLER_TIMEOUT = 112;
                public static final int CONTROLLER_UNSTUCK = 127;
                public static final int CONTROLLER_UNSUPPORTED = 119;
                public static final int DAYDREAM_TRACKING_ACQUISITION_FAILED = 151;
                public static final int DAYDREAM_TRACKING_HANDOFF_FAILED = 153;
                public static final int DAYDREAM_TRACKING_TRANSITIONAL_FAILED = 152;
                public static final int DISALLOWED_WRITE = 402;
                public static final int DON_APP_INCOMPATIBLE = 190;
                public static final int DON_BAD_POSE = 176;
                public static final int DON_BLUETOOTH_OFF = 177;
                public static final int DON_CANCELLED = 178;
                public static final int DON_DAYDREAM_APP_INSTALLING = 179;
                public static final int DON_DAYDREAM_APP_NOT_PRESENT = 180;
                public static final int DON_DAYDREAM_APP_OUT_OF_DATE = 181;
                public static final int DON_DAYDREAM_SETUP_NOT_COMPLETE = 182;
                public static final int DON_DEVICE_INCOMPATIBLE = 183;
                public static final int DON_HEADSET_INCOMPATIBLE = 184;
                public static final int DON_LOCATION_OFF = 185;
                public static final int DON_MISSING_PERMISSION = 186;
                public static final int DON_NFC_OFF = 187;
                public static final int DON_SYSTEM_UPDATE_REQUESTED = 191;
                public static final int DON_VRCORE_OUT_OF_DATE = 188;
                public static final int DON_VR_KEYBOARD_NOT_PRESENT = 189;
                public static final int EMPTY_PLAYLOAD = 201;
                public static final int INVALID_PLAYLOAD = 202;
                public static final int INVALID_READ = 401;
                public static final int LAUNCH_FAILURE = 203;
                public static final int NO_ZEN_RULE = 301;
                public static final int UNKNOWN_ERROR = 0;
            }
            
            public interface Permission
            {
                public static final int ACCESS_COARSE_LOCATION = 1;
                public static final int ACCESS_NOTIFICATION_POLICY = 8;
                public static final int BIND_CONDITION_PROVIDER_SERVICE = 7;
                public static final int BIND_NOTIFICATION_LISTENER_SERVICE = 6;
                public static final int CAMERA = 2;
                public static final int READ_EXTERNAL_STORAGE = 3;
                public static final int SYSTEM_ALERT_WINDOW = 5;
                public static final int UNKNOWN_PERMISSION = 0;
                public static final int WRITE_EXTERNAL_STORAGE = 4;
            }
        }
        
        public static final class VrHome extends ExtendableMessageNano<VrHome> implements Cloneable
        {
            private static volatile VrHome[] _emptyArray;
            public Setup setup;
            
            public VrHome() {
                this.clear();
            }
            
            public static VrHome[] emptyArray() {
                if (VrHome._emptyArray == null) {
                    while (true) {
                        while (true) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (VrHome._emptyArray != null) {
                                    break;
                                }
                            }
                            VrHome._emptyArray = new VrHome[0];
                            continue;
                        }
                    }
                }
                return VrHome._emptyArray;
            }
            
            public static VrHome parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new VrHome().mergeFrom(codedInputByteBufferNano);
            }
            
            public static VrHome parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                return MessageNano.mergeFrom(new VrHome(), array);
            }
            
            public final VrHome clear() {
                this.setup = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }
            
            @Override
            public final VrHome clone() {
                while (true) {
                    VrHome vrHome;
                    try {
                        vrHome = super.clone();
                        if (this.setup == null) {
                            return vrHome;
                        }
                    }
                    catch (final CloneNotSupportedException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                    vrHome.setup = this.setup.clone();
                    return vrHome;
                }
            }
            
            @Override
            protected final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.setup != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.setup);
                }
                return computeSerializedSize;
            }
            
            @Override
            public final VrHome mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        default: {
                            if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        case 0: {
                            return this;
                        }
                        case 10: {
                            if (this.setup == null) {
                                this.setup = new Setup();
                            }
                            codedInputByteBufferNano.readMessage(this.setup);
                            continue;
                        }
                    }
                }
            }
            
            @Override
            public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.setup != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.setup);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class Setup extends ExtendableMessageNano<Setup> implements Cloneable
            {
                private static volatile Setup[] _emptyArray;
                public StepStateChange stepStateChange;
                public View view;
                
                public Setup() {
                    this.clear();
                }
                
                public static Setup[] emptyArray() {
                    if (Setup._emptyArray == null) {
                        while (true) {
                            while (true) {
                                synchronized (InternalNano.LAZY_INIT_LOCK) {
                                    if (Setup._emptyArray != null) {
                                        break;
                                    }
                                }
                                Setup._emptyArray = new Setup[0];
                                continue;
                            }
                        }
                    }
                    return Setup._emptyArray;
                }
                
                public static Setup parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Setup().mergeFrom(codedInputByteBufferNano);
                }
                
                public static Setup parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                    return MessageNano.mergeFrom(new Setup(), array);
                }
                
                public final Setup clear() {
                    this.view = null;
                    this.stepStateChange = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }
                
                @Override
                public final Setup clone() {
                    while (true) {
                        Setup setup = null;
                    Label_0048:
                        while (true) {
                            try {
                                setup = super.clone();
                                if (this.view == null) {
                                    if (this.stepStateChange == null) {
                                        return setup;
                                    }
                                    break Label_0048;
                                }
                            }
                            catch (final CloneNotSupportedException detailMessage) {
                                throw new AssertionError((Object)detailMessage);
                            }
                            setup.view = this.view.clone();
                            continue;
                        }
                        setup.stepStateChange = this.stepStateChange.clone();
                        return setup;
                    }
                }
                
                @Override
                protected final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.view != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.view);
                    }
                    if (this.stepStateChange != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.stepStateChange);
                    }
                    return computeSerializedSize;
                }
                
                @Override
                public final Setup mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        switch (tag) {
                            default: {
                                if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            case 0: {
                                return this;
                            }
                            case 10: {
                                if (this.view == null) {
                                    this.view = new View();
                                }
                                codedInputByteBufferNano.readMessage(this.view);
                                continue;
                            }
                            case 18: {
                                if (this.stepStateChange == null) {
                                    this.stepStateChange = new StepStateChange();
                                }
                                codedInputByteBufferNano.readMessage(this.stepStateChange);
                                continue;
                            }
                        }
                    }
                }
                
                @Override
                public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.view != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.view);
                    }
                    if (this.stepStateChange != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.stepStateChange);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public interface Step
                {
                    public static final int ACCOUNT_SELECTION = 1;
                    public static final int CHECKING_ACCOUNT_INFO = 4;
                    public static final int DOWNLOADING_VR_COMPONENTS = 7;
                    public static final int FOP = 6;
                    public static final int GETTING_STARTED = 2;
                    public static final int HEALTH_AND_SAFETY = 3;
                    public static final int OVERALL = 8;
                    public static final int PIN = 5;
                    public static final int UNKNOWN = 0;
                }
                
                public static final class StepStateChange extends ExtendableMessageNano<StepStateChange> implements Cloneable
                {
                    private static volatile StepStateChange[] _emptyArray;
                    public Integer newStepState;
                    public Integer previousStepState;
                    public Integer step;
                    
                    public StepStateChange() {
                        this.clear();
                    }
                    
                    public static StepStateChange[] emptyArray() {
                        if (StepStateChange._emptyArray == null) {
                            while (true) {
                                while (true) {
                                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                                        if (StepStateChange._emptyArray != null) {
                                            break;
                                        }
                                    }
                                    StepStateChange._emptyArray = new StepStateChange[0];
                                    continue;
                                }
                            }
                        }
                        return StepStateChange._emptyArray;
                    }
                    
                    public static StepStateChange parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new StepStateChange().mergeFrom(codedInputByteBufferNano);
                    }
                    
                    public static StepStateChange parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                        return MessageNano.mergeFrom(new StepStateChange(), array);
                    }
                    
                    public final StepStateChange clear() {
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }
                    
                    @Override
                    public final StepStateChange clone() {
                        try {
                            return super.clone();
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                    }
                    
                    @Override
                    protected final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.step != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.step);
                        }
                        if (this.previousStepState != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.previousStepState);
                        }
                        if (this.newStepState != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.newStepState);
                        }
                        return computeSerializedSize;
                    }
                    
                    @Override
                    public final StepStateChange mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        while (true) {
                            final int tag = codedInputByteBufferNano.readTag();
                            switch (tag) {
                                default: {
                                    if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                case 0: {
                                    return this;
                                }
                                case 8: {
                                    final int int32 = codedInputByteBufferNano.readInt32();
                                    switch (int32) {
                                        default: {
                                            continue;
                                        }
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8: {
                                            this.step = int32;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                                case 16: {
                                    final int int33 = codedInputByteBufferNano.readInt32();
                                    switch (int33) {
                                        default: {
                                            continue;
                                        }
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4: {
                                            this.previousStepState = int33;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                                case 24: {
                                    final int int34 = codedInputByteBufferNano.readInt32();
                                    switch (int34) {
                                        default: {
                                            continue;
                                        }
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4: {
                                            this.newStepState = int34;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    
                    @Override
                    public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.step != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.step);
                        }
                        if (this.previousStepState != null) {
                            codedOutputByteBufferNano.writeInt32(2, this.previousStepState);
                        }
                        if (this.newStepState != null) {
                            codedOutputByteBufferNano.writeInt32(3, this.newStepState);
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                    
                    public interface StepState
                    {
                        public static final int COMPLETE = 3;
                        public static final int ERROR = 1;
                        public static final int LOADING = 2;
                        public static final int SHOW = 4;
                        public static final int UNKNOWN = 0;
                    }
                }
                
                public static final class View extends ExtendableMessageNano<View> implements Cloneable
                {
                    private static volatile View[] _emptyArray;
                    public Integer page;
                    public Integer step;
                    
                    public View() {
                        this.clear();
                    }
                    
                    public static View[] emptyArray() {
                        if (View._emptyArray == null) {
                            while (true) {
                                while (true) {
                                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                                        if (View._emptyArray != null) {
                                            break;
                                        }
                                    }
                                    View._emptyArray = new View[0];
                                    continue;
                                }
                            }
                        }
                        return View._emptyArray;
                    }
                    
                    public static View parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new View().mergeFrom(codedInputByteBufferNano);
                    }
                    
                    public static View parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
                        return MessageNano.mergeFrom(new View(), array);
                    }
                    
                    public final View clear() {
                        this.page = null;
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }
                    
                    @Override
                    public final View clone() {
                        try {
                            return super.clone();
                        }
                        catch (final CloneNotSupportedException detailMessage) {
                            throw new AssertionError((Object)detailMessage);
                        }
                    }
                    
                    @Override
                    protected final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.step != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.step);
                        }
                        if (this.page != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.page);
                        }
                        return computeSerializedSize;
                    }
                    
                    @Override
                    public final View mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        while (true) {
                            final int tag = codedInputByteBufferNano.readTag();
                            switch (tag) {
                                default: {
                                    if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                case 0: {
                                    return this;
                                }
                                case 8: {
                                    final int int32 = codedInputByteBufferNano.readInt32();
                                    switch (int32) {
                                        default: {
                                            continue;
                                        }
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8: {
                                            this.step = int32;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                                case 16: {
                                    this.page = codedInputByteBufferNano.readInt32();
                                    continue;
                                }
                            }
                        }
                    }
                    
                    @Override
                    public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.step != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.step);
                        }
                        if (this.page != null) {
                            codedOutputByteBufferNano.writeInt32(2, this.page);
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                }
            }
        }
    }
}
