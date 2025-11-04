// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import java.util.Collections;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import java.util.UUID;

public class SLGridConnection extends SLConnection
{
    private static final String DEFAULT_SYSTEM_ACCOUNT = "Second Life";
    private static boolean autoresponseEnabled;
    private static String autoresponseText;
    private UUID activeAgentUUID;
    private SLAgentCircuit agentCircuit;
    private SLAuthParams authParams;
    public SLAuthReply authReply;
    public SLCapEventQueue capEventQueue;
    private ConnectionState connectionState;
    private final EventBus eventBus;
    private volatile boolean firstConnect;
    private volatile boolean hadConnected;
    private volatile boolean isReconnecting;
    private volatile Thread loginThread;
    private SLModules modules;
    public final SLParcelInfo parcelInfo;
    private volatile int reconnectAttempts;
    private Map<SLAuthReply, SLTempCircuit> tempCircuits;
    private UserManager userManager;
    private volatile boolean userWantsConnected;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues() {
        if (SLGridConnection.-com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues != null) {
            return SLGridConnection.-com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues = new int[ConnectionState.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[ConnectionState.Connected.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[ConnectionState.Connecting.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[ConnectionState.Idle.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues = -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    static {
        SLGridConnection.autoresponseEnabled = false;
        SLGridConnection.autoresponseText = "";
    }
    
    public SLGridConnection() {
        this.connectionState = ConnectionState.Idle;
        this.eventBus = EventBus.getInstance();
        this.firstConnect = true;
        this.userWantsConnected = false;
        this.hadConnected = false;
        this.isReconnecting = false;
        this.reconnectAttempts = 0;
        this.loginThread = null;
        this.tempCircuits = Collections.synchronizedMap(new HashMap<SLAuthReply, SLTempCircuit>());
        this.parcelInfo = new SLParcelInfo();
    }
    
    private void DoConnect(final SLAuthParams slAuthParams, final String s) {
        final SLAuth slAuth = new SLAuth();
        SLAuthReply login;
        try {
            login = slAuth.Login(slAuthParams.withLocation(s));
            if (!login.success) {
                this.setConnectionState(ConnectionState.Idle);
                this.reconnectOrDrop(true, false, login.message);
                return;
            }
        }
        catch (final Exception ex) {
            this.setConnectionState(ConnectionState.Idle);
            this.reconnectOrDrop(true, false, "Failed to connect to login server.");
            return;
        }
        synchronized (this) {
            if (this.connectionState == ConnectionState.Idle) {
                return;
            }
            this.authReply = login;
            this.activeAgentUUID = this.authReply.agentID;
            this.userManager = UserManager.getUserManager(this.activeAgentUUID);
            if (this.userManager != null) {
                this.userManager.getChatterList().getFriendManager().updateFriendList(this.authReply.friends);
            }
            this.parcelInfo.reset(this.userManager);
            this.startCircuit(login, null);
        }
    }
    
    private boolean Reconnect() {
        synchronized (this) {
            if (this.userWantsConnected && this.hadConnected && GlobalOptions.getInstance().getAutoReconnect() && this.reconnectAttempts < GlobalOptions.getInstance().getMaxReconnectAttempts()) {
                if (this.connectionState == ConnectionState.Idle && this.authParams != null) {
                    ++this.reconnectAttempts;
                    this.isReconnecting = true;
                    this.eventBus.publish(new SLReconnectingEvent(this.reconnectAttempts));
                    this.startConnecting(true, "last");
                }
                return true;
            }
            return this.isReconnecting = false;
        }
    }
    
    public static String getAutoresponse() {
        if (!SLGridConnection.autoresponseEnabled) {
            return null;
        }
        return SLGridConnection.autoresponseText;
    }
    
    private void reconnectOrDrop(final boolean b, final boolean b2, final String s) {
        if (!this.Reconnect()) {
            if (this.activeAgentUUID != null) {
                GridConnectionManager.removeConnection(this.activeAgentUUID, this);
            }
            if (b) {
                this.eventBus.publish(new SLLoginResultEvent(false, s, this.activeAgentUUID));
            }
            else {
                this.eventBus.publish(new SLDisconnectEvent(b2, s));
            }
        }
    }
    
    public static void setAutoresponseInfo(final boolean autoresponseEnabled, final String autoresponseText) {
        SLGridConnection.autoresponseEnabled = autoresponseEnabled;
        SLGridConnection.autoresponseText = autoresponseText;
    }
    
    private void setConnectionState(final ConnectionState connectionState) {
        if (this.connectionState != connectionState) {
            this.connectionState = connectionState;
            this.eventBus.publish(new SLConnectionStateChangedEvent(connectionState));
        }
    }
    
    private void startCircuit(final SLAuthReply p0, final SLTempCircuit p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/lang/StringBuilder.<init>:()V
        //     7: ldc_w           "login reply: ip = "
        //    10: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    13: aload_1        
        //    14: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.simAddress:Ljava/lang/String;
        //    17: invokevirtual   java/lang/String.toString:()Ljava/lang/String;
        //    20: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    23: ldc_w           ", port = "
        //    26: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    29: aload_1        
        //    30: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.simPort:I
        //    33: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    36: ldc_w           ", ccode = "
        //    39: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    42: aload_1        
        //    43: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.circuitCode:I
        //    46: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    49: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    52: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    55: aload_1        
        //    56: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.inventoryRoot:Ljava/util/UUID;
        //    59: ifnull          229
        //    62: new             Ljava/lang/StringBuilder;
        //    65: dup            
        //    66: invokespecial   java/lang/StringBuilder.<init>:()V
        //    69: ldc_w           "inventory root: "
        //    72: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    75: aload_1        
        //    76: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.inventoryRoot:Ljava/util/UUID;
        //    79: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
        //    82: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    85: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    88: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    91: new             Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps;
        //    94: dup            
        //    95: invokespecial   com/lumiyaviewer/lumiya/slproto/caps/SLCaps.<init>:()V
        //    98: astore_3       
        //    99: aload_3        
        //   100: aload_0        
        //   101: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.authReply:Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //   104: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.loginURL:Ljava/lang/String;
        //   107: aload_0        
        //   108: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.authReply:Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //   111: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.seedCapability:Ljava/lang/String;
        //   114: invokevirtual   com/lumiyaviewer/lumiya/slproto/caps/SLCaps.GetCapabilites:(Ljava/lang/String;Ljava/lang/String;)V
        //   117: new             Lcom/lumiyaviewer/lumiya/slproto/SLCircuitInfo;
        //   120: dup            
        //   121: aload_1        
        //   122: invokespecial   com/lumiyaviewer/lumiya/slproto/SLCircuitInfo.<init>:(Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;)V
        //   125: astore          4
        //   127: new             Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   130: astore          5
        //   132: aload           5
        //   134: aload_0        
        //   135: aload           4
        //   137: aload_1        
        //   138: aload_3        
        //   139: aload_2        
        //   140: invokespecial   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.<init>:(Lcom/lumiyaviewer/lumiya/slproto/SLGridConnection;Lcom/lumiyaviewer/lumiya/slproto/SLCircuitInfo;Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps;Lcom/lumiyaviewer/lumiya/slproto/SLTempCircuit;)V
        //   143: aload_0        
        //   144: aload           5
        //   146: putfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.agentCircuit:Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   149: aload_0        
        //   150: aload_0        
        //   151: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.agentCircuit:Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   154: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.getModules:()Lcom/lumiyaviewer/lumiya/slproto/modules/SLModules;
        //   157: putfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.modules:Lcom/lumiyaviewer/lumiya/slproto/modules/SLModules;
        //   160: new             Lcom/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue;
        //   163: astore_1       
        //   164: aload_1        
        //   165: aload_3        
        //   166: getstatic       com/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability.EventQueueGet:Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability;
        //   169: invokevirtual   com/lumiyaviewer/lumiya/slproto/caps/SLCaps.getCapabilityOrThrow:(Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability;)Ljava/lang/String;
        //   172: aload_0        
        //   173: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.agentCircuit:Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   176: invokespecial   com/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue.<init>:(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue$ICapsEventHandler;)V
        //   179: aload_0        
        //   180: aload_1        
        //   181: putfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.capEventQueue:Lcom/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue;
        //   184: aload_0        
        //   185: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.parcelInfo:Lcom/lumiyaviewer/lumiya/slproto/SLParcelInfo;
        //   188: aload_0        
        //   189: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   192: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLParcelInfo.reset:(Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;)V
        //   195: invokestatic    com/lumiyaviewer/lumiya/res/textures/TextureCache.getInstance:()Lcom/lumiyaviewer/lumiya/res/textures/TextureCache;
        //   198: aload_0        
        //   199: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.modules:Lcom/lumiyaviewer/lumiya/slproto/modules/SLModules;
        //   202: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLModules.textureFetcher:Lcom/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher;
        //   205: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCache.setFetcher:(Lcom/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher;)V
        //   208: aload_0        
        //   209: aload_0        
        //   210: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.agentCircuit:Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   213: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLGridConnection.AddCircuit:(Lcom/lumiyaviewer/lumiya/slproto/SLCircuit;)V
        //   216: aload_0        
        //   217: getfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.agentCircuit:Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   220: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.SendUseCode:()V
        //   223: aload_0        
        //   224: iconst_0       
        //   225: putfield        com/lumiyaviewer/lumiya/slproto/SLGridConnection.firstConnect:Z
        //   228: return         
        //   229: ldc_w           "inventory root is null"
        //   232: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   235: goto            91
        //   238: astore_1       
        //   239: aload_0        
        //   240: getstatic       com/lumiyaviewer/lumiya/slproto/SLGridConnection$ConnectionState.Idle:Lcom/lumiyaviewer/lumiya/slproto/SLGridConnection$ConnectionState;
        //   243: invokespecial   com/lumiyaviewer/lumiya/slproto/SLGridConnection.setConnectionState:(Lcom/lumiyaviewer/lumiya/slproto/SLGridConnection$ConnectionState;)V
        //   246: aload_0        
        //   247: iconst_1       
        //   248: iconst_0       
        //   249: ldc_w           "Failed to connect to the simulator."
        //   252: invokespecial   com/lumiyaviewer/lumiya/slproto/SLGridConnection.reconnectOrDrop:(ZZLjava/lang/String;)V
        //   255: return         
        //   256: astore_1       
        //   257: aload_1        
        //   258: invokevirtual   com/lumiyaviewer/lumiya/slproto/caps/SLCaps$NoSuchCapabilityException.printStackTrace:()V
        //   261: goto            184
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                   
        //  -----  -----  -----  -----  -----------------------------------------------------------------------
        //  127    149    238    256    Ljava/io/IOException;
        //  160    184    256    264    Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps$NoSuchCapabilityException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0184:
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
    
    private void startConnecting(final boolean b, final String s) {
        this.loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!b) {
                        break Label_0013;
                    }
                    try {
                        Thread.sleep(3000L);
                        SLGridConnection.this.DoConnect(SLGridConnection.this.authParams, s);
                        SLGridConnection.this.loginThread = null;
                    }
                    catch (final InterruptedException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                    break;
                }
            }
        });
        this.setConnectionState(ConnectionState.Connecting);
        this.loginThread.start();
    }
    
    public void CancelConnect() {
        synchronized (this) {
            this.userWantsConnected = false;
            this.isReconnecting = false;
            this.hadConnected = false;
            this.closeConnectionObjects();
        }
    }
    
    public void Connect(final SLAuthParams authParams) {
        synchronized (this) {
            if (this.connectionState == ConnectionState.Idle) {
                this.authParams = authParams;
                this.userWantsConnected = true;
                this.reconnectAttempts = 0;
                this.isReconnecting = false;
                this.hadConnected = false;
                this.firstConnect = true;
                this.startConnecting(false, authParams.startLocation);
            }
        }
    }
    
    public void Disconnect() {
        synchronized (this) {
            this.userWantsConnected = false;
            this.isReconnecting = false;
            this.hadConnected = false;
            if (this.agentCircuit != null) {
                this.agentCircuit.SendLogoutRequest();
            }
            else {
                this.processDisconnect(true, "Logged out");
            }
        }
    }
    
    public void HandleTeleportFinish(final SLAuthReply authReply) {
        synchronized (this) {
            if (this.agentCircuit != null) {
                this.agentCircuit.CloseCircuit();
                this.agentCircuit = null;
            }
            if (this.capEventQueue != null) {
                this.capEventQueue.stopQueue();
                this.capEventQueue = null;
            }
            this.startCircuit(this.authReply = authReply, this.tempCircuits.remove(this.authReply));
        }
    }
    
    void addTempCircuit(final SLAuthReply slAuthReply) {
        synchronized (this) {
            if (this.tempCircuits.containsKey(slAuthReply)) {
                return;
            }
            try {
                final SLTempCircuit slTempCircuit = new SLTempCircuit(this, new SLCircuitInfo(slAuthReply), slAuthReply);
                this.tempCircuits.put(slAuthReply, slTempCircuit);
                this.AddCircuit(slTempCircuit);
                slTempCircuit.SendUseCode();
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void closeConnectionObjects() {
        synchronized (this) {
            final Thread loginThread = this.loginThread;
            if (loginThread != null) {
                loginThread.interrupt();
            }
            this.loginThread = null;
            this.modules = null;
            if (this.agentCircuit != null) {
                this.agentCircuit.CloseCircuit();
                this.agentCircuit = null;
            }
            if (this.capEventQueue != null) {
                this.capEventQueue.stopQueue();
                this.capEventQueue = null;
            }
            TextureCache.getInstance().setFetcher(null);
            final Iterator<Object> iterator = this.tempCircuits.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().CloseCircuit();
            }
        }
        this.tempCircuits.clear();
        this.setConnectionState(ConnectionState.Idle);
        monitorexit(this);
    }
    
    public void forceDisconnect(final boolean b) {
        monitorenter(this);
        Label_0021: {
            if (!b) {
                break Label_0021;
            }
            while (true) {
                Label_0127: {
                    try {
                        this.userWantsConnected = false;
                        this.isReconnecting = false;
                        this.hadConnected = false;
                        final StringBuilder append = new StringBuilder().append("GridConnection: forceDisconnect() called, fromLogoutRequest = ");
                        String str;
                        if (b) {
                            str = "true";
                        }
                        else {
                            str = "false";
                        }
                        Debug.Log(append.append(str).toString());
                        switch (-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues()[this.connectionState.ordinal()]) {
                            case 2: {
                                this.closeConnectionObjects();
                                this.reconnectOrDrop(true, b, "Network connection lost.");
                                break;
                            }
                            case 1: {
                                break Label_0127;
                            }
                        }
                        return;
                    }
                    finally {
                        monitorexit(this);
                    }
                }
                this.closeConnectionObjects();
                this.reconnectOrDrop(false, b, "Network connection lost.");
            }
        }
    }
    
    public UUID getActiveAgentUUID() {
        return this.activeAgentUUID;
    }
    
    public SLAgentCircuit getAgentCircuit() throws NotConnectedException {
        if (this.agentCircuit != null) {
            return this.agentCircuit;
        }
        throw new NotConnectedException();
    }
    
    public ConnectionState getConnectionState() {
        synchronized (this) {
            return this.connectionState;
        }
    }
    
    public boolean getIsReconnecting() {
        return this.isReconnecting;
    }
    
    public SLModules getModules() throws NotConnectedException {
        synchronized (this) {
            if (this.modules == null) {
                throw new NotConnectedException();
            }
        }
        final SLModules modules = this.modules;
        monitorexit(this);
        return modules;
    }
    
    public int getReconnectAttempt() {
        return this.reconnectAttempts;
    }
    
    public boolean isFirstConnect() {
        return this.firstConnect;
    }
    
    public void notifyLoginError(final String s) {
        synchronized (this) {
            this.closeConnectionObjects();
            this.reconnectOrDrop(true, false, s);
        }
    }
    
    public void notifyLoginSuccess() {
        synchronized (this) {
            this.hadConnected = true;
            this.reconnectAttempts = 0;
            this.isReconnecting = false;
            this.setConnectionState(ConnectionState.Connected);
            if (this.activeAgentUUID != null) {
                GridConnectionManager.setConnection(this.activeAgentUUID, this);
            }
            this.eventBus.publish(new SLLoginResultEvent(true, null, this.activeAgentUUID));
        }
    }
    
    public void processDisconnect(final boolean b, final String s) {
        synchronized (this) {
            if (this.connectionState != ConnectionState.Idle) {
                this.closeConnectionObjects();
                this.reconnectOrDrop(false, b, s);
            }
        }
    }
    
    void removeTempCircuit(final SLTempCircuit slTempCircuit) {
        synchronized (this) {
            final Iterator<Map.Entry<SLAuthReply, SLTempCircuit>> iterator = this.tempCircuits.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue() == slTempCircuit) {
                    iterator.remove();
                }
            }
        }
        final SLCircuit slCircuit;
        slCircuit.CloseCircuit();
        monitorexit(this);
    }
    
    public enum ConnectionState
    {
        Connected("Connected", 2), 
        Connecting("Connecting", 1), 
        Idle("Idle", 0);
        
        private ConnectionState(final String name, final int ordinal) {
        }
    }
    
    public static class NotConnectedException extends Exception
    {
        private static final long serialVersionUID = 2164121452714562470L;
        
        public NotConnectedException() {
            super("Grid not connected");
        }
    }
}
