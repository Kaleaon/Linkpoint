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
        Timer timer2;
        synchronized (this) {
            if (this.timer == null) {
                this.timer = new Timer("SLConnectionTimer", true);
            }
            timer2 = this.timer;
        }
        return timer2;
    }

    public void run() {
        int i;
        Debug.Log("working thread started");
        while (!this.selector.keys().isEmpty()) {
            int i2 = 1000;
            for (SelectionKey next : this.selector.keys()) {
                try {
                    SLCircuit sLCircuit = (SLCircuit) next.attachment();
                    if (sLCircuit != null) {
                        if (next.isValid()) {
                            sLCircuit.ProcessWakeup();
                            i = sLCircuit.getIdleInterval();
                            if (i < i2) {
                            }
                        }
                        i = i2;
                    } else {
                        i = i2;
                    }
                    i2 = i;
                } catch (ConcurrentModificationException | NoSuchElementException e) {
                }
            }
            Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
            while (it.hasNext()) {
                try {
                    SelectionKey next2 = it.next();
                    it.remove();
                    SLCircuit sLCircuit2 = (SLCircuit) next2.attachment();
                    if (sLCircuit2 != null) {
                        if (next2.isValid() && next2.isReadable()) {
                            sLCircuit2.ProcessReceive();
                        }
                        if (next2.isValid() && next2.isWritable()) {
                            sLCircuit2.ProcessTransmit();
                        }
                        if (next2.isValid()) {
                            sLCircuit2.UpdateSelectorOps();
                            sLCircuit2.TryProcessIdle();
                        }
                    }
                } catch (CancelledKeyException | ClosedSelectorException | ConcurrentModificationException | NoSuchElementException e2) {
                }
            }
            try {
                this.selector.select((long) i2);
            } catch (IOException e3) {
                e3.printStackTrace();
                for (SelectionKey next3 : this.selector.keys()) {
                    try {
                        SLCircuit sLCircuit3 = (SLCircuit) next3.attachment();
                        if (sLCircuit3 != null && next3.isValid()) {
                            sLCircuit3.ProcessNetworkError();
                        }
                    } catch (CancelledKeyException | ClosedSelectorException | ConcurrentModificationException | NoSuchElementException e4) {
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
