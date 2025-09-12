// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.FriendManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLConnection, SLParcelInfo, GridConnectionManager, SLCircuitInfo, 
//            SLAgentCircuit, SLTempCircuit

public class SLGridConnection extends SLConnection
{
    public static final class ConnectionState extends Enum
    {

        private static final ConnectionState $VALUES[];
        public static final ConnectionState Connected;
        public static final ConnectionState Connecting;
        public static final ConnectionState Idle;

        public static ConnectionState valueOf(String s)
        {
            return (ConnectionState)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/SLGridConnection$ConnectionState, s);
        }

        public static ConnectionState[] values()
        {
            return $VALUES;
        }

        static 
        {
            Idle = new ConnectionState("Idle", 0);
            Connecting = new ConnectionState("Connecting", 1);
            Connected = new ConnectionState("Connected", 2);
            $VALUES = (new ConnectionState[] {
                Idle, Connecting, Connected
            });
        }

        private ConnectionState(String s, int i)
        {
            super(s, i);
        }
    }

    public static class NotConnectedException extends Exception
    {

        private static final long serialVersionUID = 0x1e0880fec7da93a6L;

        public NotConnectedException()
        {
            super("Grid not connected");
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues[];
    private static final String DEFAULT_SYSTEM_ACCOUNT = "Second Life";
    private static boolean autoresponseEnabled = false;
    private static String autoresponseText = "";
    private UUID activeAgentUUID;
    private SLAgentCircuit agentCircuit;
    private SLAuthParams authParams;
    public SLAuthReply authReply;
    public SLCapEventQueue capEventQueue;
    private ConnectionState connectionState;
    private final EventBus eventBus = EventBus.getInstance();
    private volatile boolean firstConnect;
    private volatile boolean hadConnected;
    private volatile boolean isReconnecting;
    private volatile Thread loginThread;
    private SLModules modules;
    public final SLParcelInfo parcelInfo = new SLParcelInfo();
    private volatile int reconnectAttempts;
    private Map tempCircuits;
    private UserManager userManager;
    private volatile boolean userWantsConnected;

    static SLAuthParams _2D_get0(SLGridConnection slgridconnection)
    {
        return slgridconnection.authParams;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues;
        }
        int ai[] = new int[ConnectionState.values().length];
        try
        {
            ai[ConnectionState.Connected.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ConnectionState.Connecting.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ConnectionState.Idle.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues = ai;
        return ai;
    }

    static Thread _2D_set0(SLGridConnection slgridconnection, Thread thread)
    {
        slgridconnection.loginThread = thread;
        return thread;
    }

    static void _2D_wrap0(SLGridConnection slgridconnection, SLAuthParams slauthparams, String s)
    {
        slgridconnection.DoConnect(slauthparams, s);
    }

    public SLGridConnection()
    {
        connectionState = ConnectionState.Idle;
        firstConnect = true;
        userWantsConnected = false;
        hadConnected = false;
        isReconnecting = false;
        reconnectAttempts = 0;
        loginThread = null;
        tempCircuits = Collections.synchronizedMap(new HashMap());
    }

    private void DoConnect(SLAuthParams slauthparams, String s)
    {
        SLAuth slauth = new SLAuth();
        try
        {
            slauthparams = slauth.Login(slauthparams.withLocation(s));
        }
        // Misplaced declaration of an exception variable
        catch (SLAuthParams slauthparams)
        {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, "Failed to connect to login server.");
            return;
        }
        if (!((SLAuthReply) (slauthparams)).success)
        {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, ((SLAuthReply) (slauthparams)).message);
            return;
        }
        this;
        JVM INSTR monitorenter ;
        ConnectionState connectionstate;
        s = connectionState;
        connectionstate = ConnectionState.Idle;
        if (s != connectionstate)
        {
            break MISSING_BLOCK_LABEL_79;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        authReply = slauthparams;
        activeAgentUUID = authReply.agentID;
        userManager = UserManager.getUserManager(activeAgentUUID);
        if (userManager != null)
        {
            userManager.getChatterList().getFriendManager().updateFriendList(authReply.friends);
        }
        parcelInfo.reset(userManager);
        startCircuit(slauthparams, null);
        this;
        JVM INSTR monitorexit ;
        return;
        slauthparams;
        throw slauthparams;
    }

    private boolean Reconnect()
    {
        this;
        JVM INSTR monitorenter ;
        if (!userWantsConnected || !hadConnected || !GlobalOptions.getInstance().getAutoReconnect() || reconnectAttempts >= GlobalOptions.getInstance().getMaxReconnectAttempts())
        {
            break MISSING_BLOCK_LABEL_99;
        }
        if (connectionState == ConnectionState.Idle && authParams != null)
        {
            reconnectAttempts = reconnectAttempts + 1;
            isReconnecting = true;
            eventBus.publish(new SLReconnectingEvent(reconnectAttempts));
            startConnecting(true, "last");
        }
        this;
        JVM INSTR monitorexit ;
        return true;
        isReconnecting = false;
        this;
        JVM INSTR monitorexit ;
        return false;
        Exception exception;
        exception;
        throw exception;
    }

    public static String getAutoresponse()
    {
        if (!autoresponseEnabled)
        {
            return null;
        } else
        {
            return autoresponseText;
        }
    }

    private void reconnectOrDrop(boolean flag, boolean flag1, String s)
    {
label0:
        {
            if (!Reconnect())
            {
                if (activeAgentUUID != null)
                {
                    GridConnectionManager.removeConnection(activeAgentUUID, this);
                }
                if (!flag)
                {
                    break label0;
                }
                eventBus.publish(new SLLoginResultEvent(false, s, activeAgentUUID));
            }
            return;
        }
        eventBus.publish(new SLDisconnectEvent(flag1, s));
    }

    public static void setAutoresponseInfo(boolean flag, String s)
    {
        autoresponseEnabled = flag;
        autoresponseText = s;
    }

    private void setConnectionState(ConnectionState connectionstate)
    {
        if (connectionState != connectionstate)
        {
            connectionState = connectionstate;
            eventBus.publish(new SLConnectionStateChangedEvent(connectionstate));
        }
    }

    private void startCircuit(SLAuthReply slauthreply, SLTempCircuit sltempcircuit)
    {
        Debug.Log((new StringBuilder()).append("login reply: ip = ").append(slauthreply.simAddress.toString()).append(", port = ").append(slauthreply.simPort).append(", ccode = ").append(slauthreply.circuitCode).toString());
        SLCaps slcaps;
        SLCircuitInfo slcircuitinfo;
        if (slauthreply.inventoryRoot != null)
        {
            Debug.Log((new StringBuilder()).append("inventory root: ").append(slauthreply.inventoryRoot.toString()).toString());
        } else
        {
            Debug.Log("inventory root is null");
        }
        slcaps = new SLCaps();
        slcaps.GetCapabilites(authReply.loginURL, authReply.seedCapability);
        slcircuitinfo = new SLCircuitInfo(slauthreply);
        try
        {
            agentCircuit = new SLAgentCircuit(this, slcircuitinfo, slauthreply, slcaps, sltempcircuit);
        }
        // Misplaced declaration of an exception variable
        catch (SLAuthReply slauthreply)
        {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, "Failed to connect to the simulator.");
            return;
        }
        modules = agentCircuit.getModules();
        try
        {
            capEventQueue = new SLCapEventQueue(slcaps.getCapabilityOrThrow(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.EventQueueGet), agentCircuit);
        }
        // Misplaced declaration of an exception variable
        catch (SLAuthReply slauthreply)
        {
            slauthreply.printStackTrace();
        }
        parcelInfo.reset(userManager);
        TextureCache.getInstance().setFetcher(modules.textureFetcher);
        AddCircuit(agentCircuit);
        agentCircuit.SendUseCode();
        firstConnect = false;
    }

    private void startConnecting(final boolean pauseBeforeConnecting, final String location)
    {
        loginThread = new Thread(new Runnable() {

            final SLGridConnection this$0;
            final String val$location;
            final boolean val$pauseBeforeConnecting;

            public void run()
            {
                if (pauseBeforeConnecting)
                {
                    try
                    {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        interruptedexception.printStackTrace();
                    }
                }
                SLGridConnection._2D_wrap0(SLGridConnection.this, SLGridConnection._2D_get0(SLGridConnection.this), location);
                SLGridConnection._2D_set0(SLGridConnection.this, null);
            }

            
            {
                this$0 = SLGridConnection.this;
                pauseBeforeConnecting = flag;
                location = s;
                super();
            }
        });
        setConnectionState(ConnectionState.Connecting);
        loginThread.start();
    }

    public void CancelConnect()
    {
        this;
        JVM INSTR monitorenter ;
        userWantsConnected = false;
        isReconnecting = false;
        hadConnected = false;
        closeConnectionObjects();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void Connect(SLAuthParams slauthparams)
    {
        this;
        JVM INSTR monitorenter ;
        if (connectionState == ConnectionState.Idle)
        {
            authParams = slauthparams;
            userWantsConnected = true;
            reconnectAttempts = 0;
            isReconnecting = false;
            hadConnected = false;
            firstConnect = true;
            startConnecting(false, slauthparams.startLocation);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        slauthparams;
        throw slauthparams;
    }

    public void Disconnect()
    {
        this;
        JVM INSTR monitorenter ;
        userWantsConnected = false;
        isReconnecting = false;
        hadConnected = false;
        if (agentCircuit == null) goto _L2; else goto _L1
_L1:
        agentCircuit.SendLogoutRequest();
_L4:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        processDisconnect(true, "Logged out");
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        throw exception;
    }

    public void HandleTeleportFinish(SLAuthReply slauthreply)
    {
        this;
        JVM INSTR monitorenter ;
        if (agentCircuit != null)
        {
            agentCircuit.CloseCircuit();
            agentCircuit = null;
        }
        if (capEventQueue != null)
        {
            capEventQueue.stopQueue();
            capEventQueue = null;
        }
        authReply = slauthreply;
        startCircuit(slauthreply, (SLTempCircuit)tempCircuits.remove(authReply));
        this;
        JVM INSTR monitorexit ;
        return;
        slauthreply;
        throw slauthreply;
    }

    void addTempCircuit(SLAuthReply slauthreply)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = tempCircuits.containsKey(slauthreply);
        if (flag)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        SLTempCircuit sltempcircuit = new SLTempCircuit(this, new SLCircuitInfo(slauthreply), slauthreply);
        tempCircuits.put(slauthreply, sltempcircuit);
        AddCircuit(sltempcircuit);
        sltempcircuit.SendUseCode();
_L2:
        this;
        JVM INSTR monitorexit ;
        return;
        slauthreply;
        slauthreply.printStackTrace();
        if (true) goto _L2; else goto _L1
_L1:
        slauthreply;
        throw slauthreply;
    }

    public void closeConnectionObjects()
    {
        this;
        JVM INSTR monitorenter ;
        Thread thread = loginThread;
        if (thread == null)
        {
            break MISSING_BLOCK_LABEL_15;
        }
        thread.interrupt();
        loginThread = null;
        modules = null;
        if (agentCircuit != null)
        {
            agentCircuit.CloseCircuit();
            agentCircuit = null;
        }
        if (capEventQueue != null)
        {
            capEventQueue.stopQueue();
            capEventQueue = null;
        }
        TextureCache.getInstance().setFetcher(null);
        for (Iterator iterator = tempCircuits.values().iterator(); iterator.hasNext(); ((SLTempCircuit)iterator.next()).CloseCircuit()) { }
        break MISSING_BLOCK_LABEL_114;
        Exception exception;
        exception;
        throw exception;
        tempCircuits.clear();
        setConnectionState(ConnectionState.Idle);
        this;
        JVM INSTR monitorexit ;
    }

    public void forceDisconnect(boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        userWantsConnected = false;
        isReconnecting = false;
        hadConnected = false;
        StringBuilder stringbuilder = (new StringBuilder()).append("GridConnection: forceDisconnect() called, fromLogoutRequest = ");
        String s;
        int i;
        if (flag)
        {
            s = "true";
        } else
        {
            s = "false";
        }
        Debug.Log(stringbuilder.append(s).toString());
        i = _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues()[connectionState.ordinal()];
        i;
        JVM INSTR tableswitch 1 3: default 96
    //                   1 127
    //                   2 106
    //                   3 96;
           goto _L1 _L2 _L3 _L1
_L1:
        this;
        JVM INSTR monitorexit ;
        return;
_L3:
        closeConnectionObjects();
        reconnectOrDrop(true, flag, "Network connection lost.");
          goto _L1
        Exception exception;
        exception;
        throw exception;
_L2:
        closeConnectionObjects();
        reconnectOrDrop(false, flag, "Network connection lost.");
          goto _L1
    }

    public UUID getActiveAgentUUID()
    {
        return activeAgentUUID;
    }

    public SLAgentCircuit getAgentCircuit()
        throws NotConnectedException
    {
        if (agentCircuit != null)
        {
            return agentCircuit;
        } else
        {
            throw new NotConnectedException();
        }
    }

    public ConnectionState getConnectionState()
    {
        this;
        JVM INSTR monitorenter ;
        ConnectionState connectionstate = connectionState;
        this;
        JVM INSTR monitorexit ;
        return connectionstate;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getIsReconnecting()
    {
        return isReconnecting;
    }

    public SLModules getModules()
        throws NotConnectedException
    {
        this;
        JVM INSTR monitorenter ;
        if (modules == null)
        {
            throw new NotConnectedException();
        }
        break MISSING_BLOCK_LABEL_22;
        Exception exception;
        exception;
        this;
        JVM INSTR monitorexit ;
        throw exception;
        SLModules slmodules = modules;
        this;
        JVM INSTR monitorexit ;
        return slmodules;
    }

    public int getReconnectAttempt()
    {
        return reconnectAttempts;
    }

    public boolean isFirstConnect()
    {
        return firstConnect;
    }

    public void notifyLoginError(String s)
    {
        this;
        JVM INSTR monitorenter ;
        closeConnectionObjects();
        reconnectOrDrop(true, false, s);
        this;
        JVM INSTR monitorexit ;
        return;
        s;
        throw s;
    }

    public void notifyLoginSuccess()
    {
        this;
        JVM INSTR monitorenter ;
        hadConnected = true;
        reconnectAttempts = 0;
        isReconnecting = false;
        setConnectionState(ConnectionState.Connected);
        if (activeAgentUUID != null)
        {
            GridConnectionManager.setConnection(activeAgentUUID, this);
        }
        eventBus.publish(new SLLoginResultEvent(true, null, activeAgentUUID));
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void processDisconnect(boolean flag, String s)
    {
        this;
        JVM INSTR monitorenter ;
        if (connectionState != ConnectionState.Idle)
        {
            closeConnectionObjects();
            reconnectOrDrop(false, flag, s);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        s;
        throw s;
    }

    void removeTempCircuit(SLTempCircuit sltempcircuit)
    {
        this;
        JVM INSTR monitorenter ;
        Iterator iterator = tempCircuits.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            if (((java.util.Map.Entry)iterator.next()).getValue() == sltempcircuit)
            {
                iterator.remove();
            }
        } while (true);
        break MISSING_BLOCK_LABEL_58;
        sltempcircuit;
        throw sltempcircuit;
        sltempcircuit.CloseCircuit();
        this;
        JVM INSTR monitorexit ;
    }

}
