// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsActionProvider;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsFragment;
import com.lumiyaviewer.lumiya.ui.objpopup.SingleObjectPopupFragment;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ThemedActivity, ActivityUtils, NavDrawerActivityHelper

public class ConnectedActivity extends ThemedActivity
    implements com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsActionProvider.ObjectPopupsClickListener, com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener
{

    public static final String OBJECT_POPUP_NOTIFICATION = "objectPopupNotification";
    private NavDrawerActivityHelper navDrawerHelper;
    private ObjectPopupsActionProvider objectPopupsActionProvider;
    private boolean objectPopupsDisplayed;
    private final android.view.View.OnClickListener reconnectButtonListener = new _2D_.Lambda.Zi2fvFRNZlQXFOmQ50cSiiV_3Qw(this);
    private boolean singleObjectPopupsDisplayed;
    private boolean wantedShowObjectPopups;

    public ConnectedActivity()
    {
        objectPopupsDisplayed = false;
        singleObjectPopupsDisplayed = false;
        wantedShowObjectPopups = false;
    }

    private void displayObjectPopups()
    {
        Object obj = getSupportFragmentManager();
        java.util.UUID uuid = ActivityUtils.getActiveAgentID(getIntent());
        if (uuid != null)
        {
            Object obj1 = UserManager.getUserManager(uuid);
            if (obj1 != null)
            {
                ((UserManager) (obj1)).getObjectPopupsManager().dismissDisplayedObjectPopup(null);
            }
            singleObjectPopupsDisplayed = false;
            objectPopupsDisplayed = true;
            obj1 = getCurrentFocus();
            if (obj1 != null)
            {
                ((View) (obj1)).clearFocus();
                ((InputMethodManager)getSystemService("input_method")).hideSoftInputFromWindow(((View) (obj1)).getWindowToken(), 0);
            }
            obj = ((FragmentManager) (obj)).beginTransaction();
            ((FragmentTransaction) (obj)).setTransition(4097);
            ((FragmentTransaction) (obj)).replace(0x7f100287, ObjectPopupsFragment.create(uuid));
            ((FragmentTransaction) (obj)).commit();
        }
    }

    private void hideSingleObjectPopup()
    {
        if (singleObjectPopupsDisplayed)
        {
            singleObjectPopupsDisplayed = false;
            Object obj = getSupportFragmentManager();
            android.support.v4.app.Fragment fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100287);
            if (fragment instanceof SingleObjectPopupFragment)
            {
                obj = ((FragmentManager) (obj)).beginTransaction();
                ((FragmentTransaction) (obj)).setCustomAnimations(0, 0x7f050011);
                ((FragmentTransaction) (obj)).remove(fragment);
                ((FragmentTransaction) (obj)).commit();
            }
        }
    }

    private boolean removeObjectPopupsFragment()
    {
        if (objectPopupsDisplayed || singleObjectPopupsDisplayed)
        {
            objectPopupsDisplayed = false;
            singleObjectPopupsDisplayed = false;
            Object obj = getSupportFragmentManager();
            android.support.v4.app.Fragment fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100287);
            if (fragment != null)
            {
                obj = ((FragmentManager) (obj)).beginTransaction();
                ((FragmentTransaction) (obj)).setTransition(8194);
                ((FragmentTransaction) (obj)).remove(fragment);
                ((FragmentTransaction) (obj)).commit();
            }
            return true;
        } else
        {
            return false;
        }
    }

    private void updateConnectionStatus()
    {
        if (handleConnectionEvents() && !isFinishing())
        {
            View view = findViewById(0x7f100242);
            if (view instanceof ViewGroup)
            {
                SLGridConnection slgridconnection = GridConnectionService.getGridConnection();
                com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState connectionstate = slgridconnection.getConnectionState();
                if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected)
                {
                    view.setVisibility(8);
                } else
                {
                    if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connecting)
                    {
                        view.setVisibility(0);
                        if (slgridconnection.getIsReconnecting())
                        {
                            int i = slgridconnection.getReconnectAttempt();
                            ((TextView)view.findViewById(0x7f100244)).setText(getString(0x7f09029e, new Object[] {
                                Integer.valueOf(i)
                            }));
                        } else
                        {
                            ((TextView)view.findViewById(0x7f100244)).setText(0x7f0900cb);
                        }
                        ((Button)view.findViewById(0x7f100245)).setText(0x7f0900a8);
                        view.findViewById(0x7f100243).setVisibility(0);
                        return;
                    }
                    if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle)
                    {
                        view.setVisibility(0);
                        ((TextView)view.findViewById(0x7f100244)).setText(0x7f090102);
                        ((Button)view.findViewById(0x7f100245)).setText(0x7f090250);
                        view.findViewById(0x7f100243).setVisibility(8);
                        return;
                    }
                }
            }
        }
    }

    public void dismissSingleObjectPopup()
    {
        hideSingleObjectPopup();
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null)
        {
            usermanager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
        }
    }

    protected boolean handleBackPressed()
    {
        return false;
    }

    protected boolean handleConnectionEvents()
    {
        return true;
    }

    public void handleConnectionStateChangedEvent(SLConnectionStateChangedEvent slconnectionstatechangedevent)
    {
        updateConnectionStatus();
    }

    public void handleDisconnectEvent(SLDisconnectEvent sldisconnectevent)
    {
label0:
        {
            if (handleConnectionEvents())
            {
                Debug.Printf("ConnectedActivity: disconnect event, normalDisconnect %b", new Object[] {
                    Boolean.valueOf(sldisconnectevent.normalDisconnect)
                });
                if (!sldisconnectevent.normalDisconnect)
                {
                    break label0;
                }
                Debug.Printf("ConnectedActivity: starting login activity", new Object[0]);
                ActivityCompat.finishAffinity(this);
                startActivity((new Intent(this, com/lumiyaviewer/lumiya/ui/login/LoginActivity)).setFlags(0x14008000));
            }
            return;
        }
        updateConnectionStatus();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108(View view)
    {
        view = GridConnectionService.getGridConnection();
        com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState connectionstate = view.getConnectionState();
        if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connecting)
        {
            view.Disconnect();
        } else
        if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle)
        {
            EventBus.getInstance().publish(new SLDisconnectEvent(true, null));
            ActivityCompat.finishAffinity(this);
            startActivity((new Intent(this, com/lumiyaviewer/lumiya/ui/login/LoginActivity)).setFlags(0x14008000));
            return;
        }
    }

    public void onBackPressed()
    {
        if (navDrawerHelper.onBackPressed())
        {
            return;
        }
        if (handleConnectionEvents() && removeObjectPopupsFragment())
        {
            return;
        }
        if (!handleBackPressed())
        {
            super.onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        navDrawerHelper.onConfigurationChanged(configuration);
    }

    protected void onCreate(Bundle bundle)
    {
label0:
        {
            super.onCreate(bundle);
            if (handleConnectionEvents())
            {
                if (bundle == null)
                {
                    break label0;
                }
                objectPopupsDisplayed = bundle.getBoolean("objectPopupsDisplayed");
                singleObjectPopupsDisplayed = bundle.getBoolean("singleObjectPopupsDisplayed");
                wantedShowObjectPopups = bundle.getBoolean("wantedShowObjectPopups");
            }
            return;
        }
        wantedShowObjectPopups = getIntent().getBooleanExtra("objectPopupNotification", false);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        Debug.Printf("ObjectPopup: createOptionsMenu", new Object[0]);
        if (handleConnectionEvents())
        {
            getMenuInflater().inflate(0x7f120016, menu);
            menu = MenuItemCompat.getActionProvider(menu.findItem(0x7f100337));
            if (menu instanceof ObjectPopupsActionProvider)
            {
                objectPopupsActionProvider = (ObjectPopupsActionProvider)menu;
                objectPopupsActionProvider.setObjectPopupsClickListener(this);
                menu = ActivityUtils.getUserManager(getIntent());
                if (menu != null && objectPopupsActionProvider != null)
                {
                    onObjectPopupCountChanged(menu.getObjectPopupsManager().getObjectPopupCount());
                }
            } else
            {
                objectPopupsActionProvider = null;
            }
            return true;
        } else
        {
            return super.onCreateOptionsMenu(menu);
        }
    }

    protected void onNewIntent(Intent intent)
    {
label0:
        {
            super.onNewIntent(intent);
            if (handleConnectionEvents())
            {
                if (!intent.getBooleanExtra("objectPopupNotification", false))
                {
                    break label0;
                }
                wantedShowObjectPopups = true;
            }
            return;
        }
        intent = ActivityUtils.getUserManager(getIntent());
        if (intent != null)
        {
            intent.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
        }
        removeObjectPopupsFragment();
    }

    public void onNewObjectPopup(SLChatEvent slchatevent)
    {
        if (findViewById(0x7f100287) != null)
        {
            java.util.UUID uuid = ActivityUtils.getActiveAgentID(getIntent());
            if (uuid != null)
            {
                FragmentManager fragmentmanager = getSupportFragmentManager();
                if (objectPopupsDisplayed)
                {
                    UserManager usermanager = UserManager.getUserManager(uuid);
                    if (usermanager != null)
                    {
                        usermanager.getObjectPopupsManager().dismissDisplayedObjectPopup(slchatevent);
                        return;
                    }
                } else
                if (singleObjectPopupsDisplayed && slchatevent == null)
                {
                    singleObjectPopupsDisplayed = false;
                    android.support.v4.app.Fragment fragment = fragmentmanager.findFragmentById(0x7f100287);
                    if (fragment != null)
                    {
                        FragmentTransaction fragmenttransaction = fragmentmanager.beginTransaction();
                        fragmenttransaction.setTransition(8194);
                        fragmenttransaction.remove(fragment);
                        fragmenttransaction.commit();
                    }
                }
                if (slchatevent != null)
                {
                    singleObjectPopupsDisplayed = true;
                    objectPopupsDisplayed = false;
                    slchatevent = fragmentmanager.beginTransaction();
                    slchatevent.setTransition(4097);
                    slchatevent.replace(0x7f100287, SingleObjectPopupFragment.create(uuid));
                    slchatevent.commit();
                }
            }
        }
    }

    public void onObjectPopupCountChanged(int i)
    {
        if (objectPopupsActionProvider != null)
        {
            objectPopupsActionProvider.setObjectPopupCount(i);
        }
        if (i == 0 && objectPopupsDisplayed)
        {
            objectPopupsDisplayed = false;
            Object obj = getSupportFragmentManager();
            android.support.v4.app.Fragment fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100287);
            if (fragment instanceof ObjectPopupsFragment)
            {
                obj = ((FragmentManager) (obj)).beginTransaction();
                ((FragmentTransaction) (obj)).setTransition(8194);
                ((FragmentTransaction) (obj)).remove(fragment);
                ((FragmentTransaction) (obj)).commit();
            }
        }
    }

    public void onObjectPopupsClicked()
    {
label0:
        {
            if (findViewById(0x7f100287) != null)
            {
                Object obj = getSupportFragmentManager();
                if (!objectPopupsDisplayed)
                {
                    break label0;
                }
                objectPopupsDisplayed = false;
                android.support.v4.app.Fragment fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100287);
                if (fragment != null)
                {
                    obj = ((FragmentManager) (obj)).beginTransaction();
                    ((FragmentTransaction) (obj)).setTransition(8194);
                    ((FragmentTransaction) (obj)).remove(fragment);
                    ((FragmentTransaction) (obj)).commit();
                }
            }
            return;
        }
        displayObjectPopups();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if (!navDrawerHelper.onOptionsItemSelected(menuitem))
        {
            return super.onOptionsItemSelected(menuitem);
        } else
        {
            return true;
        }
    }

    protected void onPause()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null && handleConnectionEvents())
        {
            usermanager.getObjectPopupsManager().removeObjectPopupListener(this);
            usermanager.getObjectPopupsManager().removePopupWatcher(this);
        }
        super.onPause();
    }

    protected void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        if (handleConnectionEvents())
        {
            bundle = findViewById(0x7f100242);
            if (bundle instanceof ViewGroup)
            {
                bundle.findViewById(0x7f100245).setOnClickListener(reconnectButtonListener);
            }
        }
        navDrawerHelper = new NavDrawerActivityHelper(this);
        navDrawerHelper.syncState();
    }

    protected void onResume()
    {
        super.onResume();
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null && handleConnectionEvents())
        {
            int i = usermanager.getObjectPopupsManager().getObjectPopupCount();
            if (objectPopupsActionProvider != null)
            {
                onObjectPopupCountChanged(i);
            }
            usermanager.getObjectPopupsManager().addPopupWatcher(this);
            usermanager.getObjectPopupsManager().setObjectPopupListener(this, UIThreadExecutor.getInstance());
            if (wantedShowObjectPopups)
            {
                wantedShowObjectPopups = false;
                if (i != 0 && objectPopupsDisplayed ^ true)
                {
                    displayObjectPopups();
                }
            }
        }
        updateConnectionStatus();
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if (handleConnectionEvents())
        {
            bundle.putBoolean("objectPopupsDisplayed", objectPopupsDisplayed);
            bundle.putBoolean("singleObjectPopupsDisplayed", singleObjectPopupsDisplayed);
            bundle.putBoolean("wantedShowObjectPopups", wantedShowObjectPopups);
        }
    }
}
