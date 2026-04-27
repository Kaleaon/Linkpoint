package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Timer;

public class SLConnection implements Runnable {
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private Selector selector;
    private volatile Timer timer;
    private Thread workingThread;

    public SLConnection() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.workingThread = null;
        this.timer = null;
    }

    public void AddCircuit(SLCircuit sLCircuit) {
        synchronized (this) {
            if (this.workingThread == null) {
                this.workingThread = new Thread(this, "SLConnection");
                this.workingThread.start();
            }
        }
    }

    public Selector getSelector() {
        return this.selector;
    }

    public Timer getTimer() {
        Timer timer;
        synchronized (this) {
            if (this.timer == null) {
                this.timer = new Timer("SLConnectionTimer", true);
            }
            timer = this.timer;
        }
        return timer;
    }

    public void run() {
        Debug.Log("working thread started");
        while (!this.selector.keys().isEmpty()) {
            SLCircuit sLCircuit;
            SelectionKey selectionKey;
            int i = 1000;
            for (SelectionKey selectionKey2 : this.selector.keys()) {
                try {
                    int idleInterval;
                    sLCircuit = (SLCircuit) selectionKey2.attachment();
                    if (sLCircuit != null) {
                        if (selectionKey2.isValid()) {
                            sLCircuit.ProcessWakeup();
                            idleInterval = sLCircuit.getIdleInterval();
                            if (idleInterval < i) {
                            }
                        }
                        idleInterval = i;
                    } else {
                        idleInterval = i;
                    }
                    i = idleInterval;
                } catch (NoSuchElementException e) {
                } catch (ConcurrentModificationException e2) {
                }
            }
            Iterator it = this.selector.selectedKeys().iterator();
            while (it.hasNext()) {
                try {
                    selectionKey2 = (SelectionKey) it.next();
                    it.remove();
                    sLCircuit = (SLCircuit) selectionKey2.attachment();
                    if (sLCircuit != null) {
                        if (selectionKey2.isValid() && selectionKey2.isReadable()) {
                            sLCircuit.ProcessReceive();
                        }
                        if (selectionKey2.isValid() && selectionKey2.isWritable()) {
                            sLCircuit.ProcessTransmit();
                        }
                        if (selectionKey2.isValid()) {
                            sLCircuit.UpdateSelectorOps();
                            sLCircuit.TryProcessIdle();
                        }
                    }
                } catch (CancelledKeyException e3) {
                } catch (NoSuchElementException e4) {
                } catch (ClosedSelectorException e5) {
                } catch (ConcurrentModificationException e6) {
                }
            }
            try {
                this.selector.select((long) i);
            } catch (IOException e7) {
                e7.printStackTrace();
                for (SelectionKey selectionKey22 : this.selector.keys()) {
                    try {
                        sLCircuit = (SLCircuit) selectionKey22.attachment();
                        if (sLCircuit != null && selectionKey22.isValid()) {
                            sLCircuit.ProcessNetworkError();
                        }
                    } catch (NoSuchElementException e8) {
                    } catch (CancelledKeyException e9) {
                    } catch (ClosedSelectorException e10) {
                    } catch (ConcurrentModificationException e11) {
                    }
                }
            }
        }
        Debug.Log("working thread exiting");
        synchronized (this) {
            this.workingThread = null;
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
        }
    }
}
