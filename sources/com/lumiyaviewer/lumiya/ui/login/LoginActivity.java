// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.accounts.ManageAccountsActivity;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.grids.GridEditDialog;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.login:
//            TOSActivity, WhatsNewActivity

public class LoginActivity extends ThemedActivity
    implements android.view.View.OnClickListener, TextWatcher, com.lumiyaviewer.lumiya.ui.grids.GridEditDialog.OnGridEditResultListener
{

    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SAVE_PASSWORD = "save_password";
    private static final String KEY_SELECTED_GRID = "selected_grid";
    private static final String KEY_TOS_ACCEPTED = "tos_accepted";
    private AccountList accountList;
    private boolean enableAutoClear;
    private com.lumiyaviewer.lumiya.ui.grids.GridList.GridArrayAdapter gridDisplayAdapter;
    private List gridDisplayList;
    private GridList gridList;
    private int lastSelectedGrid;
    private UUID lastSelectedGridUUID;
    private boolean loggingIn;
    private ImmutableList menuItems;

    static GridList _2D_get0(LoginActivity loginactivity)
    {
        return loginactivity.gridList;
    }

    static int _2D_get1(LoginActivity loginactivity)
    {
        return loginactivity.lastSelectedGrid;
    }

    static int _2D_set0(LoginActivity loginactivity, int i)
    {
        loginactivity.lastSelectedGrid = i;
        return i;
    }

    static UUID _2D_set1(LoginActivity loginactivity, UUID uuid)
    {
        loginactivity.lastSelectedGridUUID = uuid;
        return uuid;
    }

    public LoginActivity()
    {
        loggingIn = false;
        enableAutoClear = false;
        lastSelectedGrid = 0;
        gridList = null;
        accountList = null;
        gridDisplayList = new ArrayList();
        gridDisplayAdapter = null;
        menuItems = ImmutableList.of();
    }

    private void CheckTOSAndLogin()
    {
        Object obj = getCurrentFocus();
        if (obj != null)
        {
            ((InputMethodManager)getSystemService("input_method")).hideSoftInputFromWindow(((View) (obj)).getWindowToken(), 0);
        }
        obj = getPreferences(0);
        com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo gridinfo = getSelectedGrid();
        if (((SharedPreferences) (obj)).getBoolean("tos_accepted", false) || gridinfo.isLindenGrid() ^ true)
        {
            DoLogin();
            return;
        } else
        {
            startActivityForResult(new Intent(this, com/lumiyaviewer/lumiya/ui/login/TOSActivity), 5);
            return;
        }
    }

    private void DoLogin()
    {
        Object obj3 = getPreferences(0);
        String s1 = ((EditText)findViewById(0x7f1001c9)).getText().toString();
        Object obj = ((EditText)findViewById(0x7f1001ca)).getText().toString();
        com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo gridinfo = getSelectedGrid();
        boolean flag3 = ((CheckBox)findViewById(0x7f1001cc)).isChecked();
        String s = "";
        Object obj1;
        Object obj2;
        boolean flag;
        boolean flag1;
        boolean flag2;
        if (((String) (obj)).equals(getString(0x7f0902e2)))
        {
            s = ((SharedPreferences) (obj3)).getString("password", "");
            flag = true;
        } else
        {
            flag = false;
        }
        if (!flag)
        {
            s = SLAuth.getPasswordHash(((String) (obj)));
            Debug.Log((new StringBuilder()).append("Login: not using saved hash, password = ").append(((String) (obj))).append(", new hash: ").append(s).toString());
        } else
        {
            obj1 = accountList.findAccount(s1, gridinfo.getGridUUID());
            obj = s;
            if (obj1 != null)
            {
                obj = s;
                if (!((com.lumiyaviewer.lumiya.ui.accounts.AccountList.AccountInfo) (obj1)).getPasswordHash().equals(""))
                {
                    obj = ((com.lumiyaviewer.lumiya.ui.accounts.AccountList.AccountInfo) (obj1)).getPasswordHash();
                }
            }
            Debug.Log((new StringBuilder()).append("Login: using saved hash, hash = ").append(((String) (obj))).toString());
            s = ((String) (obj));
        }
        enableAutoClear = false;
        if (flag3)
        {
            ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
            ((EditText)findViewById(0x7f1001ca)).setText(0x7f0902e2);
        } else
        {
            ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((EditText)findViewById(0x7f1001ca)).setText("");
        }
        enableAutoClear = true;
        obj1 = ((SharedPreferences) (obj3)).getString("client_id", "");
        obj2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("start_location", "last");
        flag2 = getSaveUserName();
        if (flag3)
        {
            flag1 = flag2;
        } else
        {
            flag1 = false;
        }
        obj3 = ((SharedPreferences) (obj3)).edit();
        if (flag2)
        {
            obj = s1;
        } else
        {
            obj = "";
        }
        ((android.content.SharedPreferences.Editor) (obj3)).putString("login", ((String) (obj)));
        ((android.content.SharedPreferences.Editor) (obj3)).putBoolean("save_password", flag3);
        if (!flag || flag1 ^ true)
        {
            if (flag1)
            {
                obj = s;
            } else
            {
                obj = "";
            }
            ((android.content.SharedPreferences.Editor) (obj3)).putString("password", ((String) (obj)));
        }
        if (((String) (obj1)).equals(""))
        {
            obj = UUID.randomUUID().toString();
            ((android.content.SharedPreferences.Editor) (obj3)).putString("client_id", ((String) (obj)));
        } else
        {
            obj = obj1;
        }
        ((android.content.SharedPreferences.Editor) (obj3)).putString("selected_grid", gridinfo.getGridUUID().toString());
        ((android.content.SharedPreferences.Editor) (obj3)).apply();
        if (flag2)
        {
            obj3 = accountList;
            if (flag1)
            {
                obj1 = s;
            } else
            {
                obj1 = "";
            }
            ((AccountList) (obj3)).findOrAddAccount(s1, ((String) (obj1)), gridinfo.getGridUUID());
        }
        try
        {
            obj1 = new SLURL(getIntent());
        }
        // Misplaced declaration of an exception variable
        catch (Object obj1)
        {
            obj1 = null;
        }
        if (obj1 != null)
        {
            obj1 = ((SLURL) (obj1)).getLoginStartLocation();
        } else
        {
            obj1 = obj2;
        }
        Debug.Log((new StringBuilder()).append("Start location (LoginActivity): ").append(((String) (obj1))).toString());
        loggingIn = true;
        obj2 = new Intent(this, com/lumiyaviewer/lumiya/GridConnectionService);
        ((Intent) (obj2)).setAction("com.lumiyaviewer.lumiya.ACTION_LOGIN");
        ((Intent) (obj2)).putExtra("login", s1);
        ((Intent) (obj2)).putExtra("password", s);
        ((Intent) (obj2)).putExtra("client_id", ((String) (obj)));
        ((Intent) (obj2)).putExtra("start_location", ((String) (obj1)));
        ((Intent) (obj2)).putExtra("login_url", gridinfo.getLoginURL());
        ((Intent) (obj2)).putExtra("grid_name", gridinfo.getGridName());
        startService(((Intent) (obj2)));
        showProgressView(true);
        ((TextView)findViewById(0x7f1001d1)).setText(0x7f09031d);
    }

    private void checkIfGridAvailable()
    {
        Debug.Log("LoginActivity: checking if grid is available");
        Object obj = GridConnectionService.getGridConnection();
        if (obj != null)
        {
            com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState connectionstate = ((SLGridConnection) (obj)).getConnectionState();
            obj = ((SLGridConnection) (obj)).getActiveAgentUUID();
            Debug.Log((new StringBuilder()).append("LoginActivity: connectionState = ").append(connectionstate.toString()).toString());
            if (connectionstate == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected && obj != null)
            {
                Debug.Log("LoginActivity: grid available and connected");
                startChatActivity(((UUID) (obj)));
                finish();
                return;
            }
        }
        updateConnectingStatus();
    }

    private boolean getSaveUserName()
    {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("noSaveUserName", false) ^ true;
    }

    private com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo getSelectedGrid()
    {
        Object obj = ((Spinner)findViewById(0x7f1000b5)).getSelectedItem();
        if (obj instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
        {
            return (com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)obj;
        } else
        {
            return gridList.getDefaultGrid();
        }
    }

    private void loadSavedLogin()
    {
        SharedPreferences sharedpreferences = getPreferences(0);
        if (getSaveUserName())
        {
            String s = sharedpreferences.getString("password", "");
            ((EditText)findViewById(0x7f1001c9)).setText(sharedpreferences.getString("login", ""));
            ((CheckBox)findViewById(0x7f1001cc)).setChecked(sharedpreferences.getBoolean("save_password", true));
            if (!s.equals(""))
            {
                ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                ((EditText)findViewById(0x7f1001ca)).setText(0x7f0902e2);
            } else
            {
                ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                ((EditText)findViewById(0x7f1001ca)).setText("");
            }
        } else
        {
            ((EditText)findViewById(0x7f1001c9)).setText("");
            ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((EditText)findViewById(0x7f1001ca)).setText("");
        }
        enableAutoClear = true;
    }

    private boolean progressViewVisible()
    {
        boolean flag1 = false;
        View view = findViewById(0x7f1001d3);
        boolean flag = flag1;
        if (view != null)
        {
            flag = flag1;
            if (view.getVisibility() == 0)
            {
                flag = true;
            }
        }
        return flag;
    }

    private void setSelectedGrid()
    {
        try
        {
            Object obj = getPreferences(0).getString("selected_grid", "");
            if (!((String) (obj)).equals(""))
            {
                int i = gridList.getGridIndex(UUID.fromString(((String) (obj))));
                ((Spinner)findViewById(0x7f1000b5)).setSelection(i);
                lastSelectedGrid = i;
                obj = ((Spinner)findViewById(0x7f1000b5)).getSelectedItem();
                if (obj instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
                {
                    lastSelectedGridUUID = ((com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)obj).getGridUUID();
                }
            }
            return;
        }
        catch (Exception exception)
        {
            return;
        }
    }

    private void showProgressView(boolean flag)
    {
        byte byte0 = 8;
        View view = findViewById(0x7f1001d3);
        View view2 = findViewById(0x7f1001c6);
        if (view != null && view2 != null)
        {
            View view1 = findViewById(0x7f1001d3);
            int i;
            if (flag)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            view1.setVisibility(i);
            view1 = findViewById(0x7f1001c6);
            if (flag)
            {
                i = byte0;
            } else
            {
                i = 0;
            }
            view1.setVisibility(i);
        }
        updateMenuItems();
    }

    private void startChatActivity(UUID uuid)
    {
        Intent intent = new Intent(this, com/lumiyaviewer/lumiya/ui/chat/ChatNewActivity);
        intent.addFlags(0x4000000);
        intent.putExtra("activeAgentUUID", uuid.toString());
        startActivity(intent);
    }

    private void updateConnectingStatus()
    {
        boolean flag1 = loggingIn;
        boolean flag = flag1;
        if (!flag1)
        {
            SLGridConnection slgridconnection = GridConnectionService.getGridConnection();
            flag = flag1;
            if (slgridconnection != null)
            {
                flag = flag1;
                if (slgridconnection.getConnectionState() == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connecting)
                {
                    showProgressView(true);
                    if (slgridconnection.getIsReconnecting())
                    {
                        ((TextView)findViewById(0x7f1001d1)).setText(getString(0x7f09031e, new Object[] {
                            Integer.valueOf(slgridconnection.getReconnectAttempt())
                        }));
                        flag = true;
                    } else
                    {
                        ((TextView)findViewById(0x7f1001d1)).setText(0x7f09031d);
                        flag = true;
                    }
                }
            }
        }
        if (!flag)
        {
            showProgressView(false);
        }
    }

    private void updateMenuItems()
    {
        boolean flag = progressViewVisible();
        for (Iterator iterator = menuItems.iterator(); iterator.hasNext(); ((MenuItem)iterator.next()).setVisible(flag ^ true)) { }
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        if (enableAutoClear)
        {
            charsequence = (EditText)findViewById(0x7f1001ca);
            if (charsequence.getText().toString().equals(getString(0x7f0902e2)))
            {
                enableAutoClear = false;
                charsequence.setText("");
                charsequence.setTransformationMethod(PasswordTransformationMethod.getInstance());
                enableAutoClear = true;
            }
        }
    }

    public SharedPreferences getPreferences(int i)
    {
        return getSharedPreferences("LoginActivity", i);
    }

    public void handleLoginResult(SLLoginResultEvent slloginresultevent)
    {
        loggingIn = false;
        Debug.Printf("LoginProgressActivity: result.success = %b", new Object[] {
            Boolean.valueOf(slloginresultevent.success)
        });
        if (slloginresultevent.success)
        {
            startChatActivity(slloginresultevent.activeAgentUUID);
            finish();
            return;
        }
        if (!isFinishing() && progressViewVisible())
        {
            String s = "Login to Second Life has failed.";
            if (!Strings.isNullOrEmpty(slloginresultevent.message))
            {
                s = slloginresultevent.message;
            }
            slloginresultevent = new android.support.v7.app.AlertDialog.Builder(this);
            slloginresultevent.setTitle("Login failed");
            slloginresultevent.setMessage(s);
            slloginresultevent.setCancelable(true);
            slloginresultevent.create().show();
        }
        showProgressView(false);
    }

    public void handleReconnectingEvent(SLReconnectingEvent slreconnectingevent)
    {
        updateConnectingStatus();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_login_LoginActivity_5985()
    {
        if (findViewById(0x7f1001ce).getHeight() < 2 && findViewById(0x7f1001c7).getVisibility() != 8)
        {
            findViewById(0x7f1001c7).setVisibility(8);
        }
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        Debug.Log((new StringBuilder()).append("LoginActivity: onActivityResult: requestCode = ").append(i).append(", resultCode = ").append(j).toString());
        if (intent != null)
        {
            Debug.Log((new StringBuilder()).append("LoginActivity: onActivityResult: data = ").append(intent.getDataString()).append(", ").append(intent.toString()).toString());
        } else
        {
            Debug.Log("LoginActivity: onActivityResult: data = null");
        }
        i;
        JVM INSTR tableswitch 3 5: default 104
    //                   3 151
    //                   4 104
    //                   5 114;
           goto _L1 _L2 _L1 _L3
_L1:
        return;
_L3:
        if (j == -1)
        {
            intent = getPreferences(0).edit();
            intent.putBoolean("tos_accepted", true);
            intent.apply();
            DoLogin();
            return;
        }
        continue; /* Loop/switch isn't completed */
_L2:
        if (j == -1 && intent != null && (intent = (com.lumiyaviewer.lumiya.ui.accounts.AccountList.AccountInfo)intent.getParcelableExtra("selected_account")) != null)
        {
            String s = intent.getPasswordHash();
            ((EditText)findViewById(0x7f1001c9)).setText(intent.getLoginName());
            ((CheckBox)findViewById(0x7f1001cc)).setChecked(s.equals("") ^ true);
            enableAutoClear = false;
            android.content.SharedPreferences.Editor editor;
            if (!s.equals(""))
            {
                ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                ((EditText)findViewById(0x7f1001ca)).setText(0x7f0902e2);
            } else
            {
                ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                ((EditText)findViewById(0x7f1001ca)).setText("");
            }
            enableAutoClear = true;
            if (intent.getGridUUID() != null)
            {
                i = gridList.getGridIndex(intent.getGridUUID());
                ((Spinner)findViewById(0x7f1000b5)).setSelection(i);
                lastSelectedGrid = i;
                Object obj = ((Spinner)findViewById(0x7f1000b5)).getSelectedItem();
                if (obj instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
                {
                    lastSelectedGridUUID = ((com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)obj).getGridUUID();
                }
            }
            editor = getPreferences(0).edit();
            editor.putString("login", intent.getLoginName());
            editor.putBoolean("save_password", s.equals("") ^ true);
            editor.putString("password", s);
            if (intent.getGridUUID() != null)
            {
                editor.putString("selected_grid", intent.getGridUUID().toString());
            }
            editor.apply();
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case 2131755469: 
            CheckTOSAndLogin();
            return;

        case 2131755463: 
            startActivity(new Intent(this, com/lumiyaviewer/lumiya/ui/login/WhatsNewActivity));
            return;

        case 2131755474: 
            loggingIn = false;
            break;
        }
        view = GridConnectionService.getGridConnection();
        if (view != null)
        {
            view.CancelConnect();
        }
        showProgressView(false);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Object obj = GridConnectionService.getGridConnection();
        if (obj != null)
        {
            bundle = ((SLGridConnection) (obj)).getConnectionState();
            obj = ((SLGridConnection) (obj)).getActiveAgentUUID();
            Debug.Log((new StringBuilder()).append("LoginActivity: connectionState = ").append(bundle.toString()).toString());
            if (bundle == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected && obj != null)
            {
                startChatActivity(((UUID) (obj)));
                finish();
                return;
            }
        }
        setContentView(0x7f040058);
        Debug.Log("LoginActivity: created.");
        gridList = new GridList(this);
        accountList = new AccountList(this);
        gridList.getGridList(gridDisplayList, true);
        enableAutoClear = false;
        findViewById(0x7f1001cd).setOnClickListener(this);
        ((EditText)findViewById(0x7f1001ca)).addTextChangedListener(this);
        loadSavedLogin();
        bundle = new SpannableStringBuilder();
        bundle.append(getString(0x7f09038a, new Object[] {
            LumiyaApp.getAppVersion()
        }));
        bundle.setSpan(new URLSpan(""), 0, bundle.length(), 33);
        ((TextView)findViewById(0x7f1001c7)).setText(bundle, android.widget.TextView.BufferType.SPANNABLE);
        findViewById(0x7f1001c7).setClickable(true);
        findViewById(0x7f1001c7).setOnClickListener(this);
        gridDisplayAdapter = new com.lumiyaviewer.lumiya.ui.grids.GridList.GridArrayAdapter(this, gridDisplayList);
        ((Spinner)findViewById(0x7f1000b5)).setAdapter(gridDisplayAdapter);
        setSelectedGrid();
        ((Spinner)findViewById(0x7f1000b5)).setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

            final LoginActivity this$0;

            public void onItemSelected(AdapterView adapterview, View view, int i, long l)
            {
label0:
                {
                    if (i != LoginActivity._2D_get1(LoginActivity.this))
                    {
                        adapterview = ((AdapterView) (adapterview.getAdapter().getItem(i)));
                        if (adapterview instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
                        {
                            adapterview = (com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)adapterview;
                            if (adapterview.getLoginURL() != null)
                            {
                                break label0;
                            }
                            adapterview = new GridEditDialog(LoginActivity.this, LoginActivity._2D_get0(LoginActivity.this), null);
                            adapterview.setOnGridEditResultListener(LoginActivity.this);
                            adapterview.show();
                        }
                    }
                    return;
                }
                LoginActivity._2D_set0(LoginActivity.this, i);
                LoginActivity._2D_set1(LoginActivity.this, adapterview.getGridUUID());
            }

            public void onNothingSelected(AdapterView adapterview)
            {
            }

            
            {
                this$0 = LoginActivity.this;
                super();
            }
        });
        bundle = new _2D_.Lambda.U_ZFuxgsYW8weMauiDTqAtaKePI(this);
        findViewById(0x7f1001c7).getViewTreeObserver().addOnGlobalLayoutListener(bundle);
        checkIfGridAvailable();
        findViewById(0x7f1001d2).setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f12000f, menu);
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(menu.findItem(0x7f10032c));
        builder.add(menu.findItem(0x7f10030b));
        builder.add(menu.findItem(0x7f10032d));
        builder.add(menu.findItem(0x7f10032e));
        menuItems = builder.build();
        return true;
    }

    public void onGridAdded(com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo gridinfo, boolean flag)
    {
        if (flag)
        {
            gridList.addNewGrid(gridinfo);
        }
        gridList.getGridList(gridDisplayList, true);
        gridDisplayAdapter.notifyDataSetChanged();
        int i = ((Spinner)findViewById(0x7f1000b5)).getAdapter().getCount();
        if (i > 1)
        {
            ((Spinner)findViewById(0x7f1000b5)).setSelection(i - 2);
            lastSelectedGrid = i - 2;
            gridinfo = ((com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo) (((Spinner)findViewById(0x7f1000b5)).getSelectedItem()));
            if (gridinfo instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
            {
                lastSelectedGridUUID = ((com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)gridinfo).getGridUUID();
            }
        }
    }

    public void onGridDeleted(com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo gridinfo)
    {
    }

    public void onGridEditCancelled()
    {
        ((Spinner)findViewById(0x7f1000b5)).setSelection(lastSelectedGrid);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755820: 
            startActivityForResult(new Intent(this, com/lumiyaviewer/lumiya/ui/accounts/ManageAccountsActivity), 3);
            return true;

        case 2131755822: 
            menuitem = (EditText)findViewById(0x7f1001ca);
            menuitem.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            menuitem.setInputType(145);
            return true;

        case 2131755821: 
            startActivity(new Intent(this, com/lumiyaviewer/lumiya/ui/grids/ManageGridsActivity));
            return true;

        case 2131755787: 
            startActivity(new Intent(this, com/lumiyaviewer/lumiya/ui/settings/SettingsActivity));
            return true;
        }
    }

    protected void onResume()
    {
        super.onResume();
        Debug.Printf("LoginActivity: Resumed.", new Object[0]);
        checkIfGridAvailable();
        gridList.loadGrids();
        gridList.getGridList(gridDisplayList, true);
        gridDisplayAdapter.notifyDataSetChanged();
        if (lastSelectedGridUUID != null)
        {
            int i = gridList.getGridIndex(lastSelectedGridUUID);
            ((Spinner)findViewById(0x7f1000b5)).setSelection(i);
        }
        accountList.loadAccounts();
        if (getSaveUserName())
        {
            findViewById(0x7f1001cc).setEnabled(true);
            return;
        } else
        {
            ((EditText)findViewById(0x7f1001c9)).setText("");
            ((EditText)findViewById(0x7f1001ca)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((EditText)findViewById(0x7f1001ca)).setText("");
            findViewById(0x7f1001cc).setEnabled(false);
            ((CheckBox)findViewById(0x7f1001cc)).setChecked(false);
            return;
        }
    }

    protected void onStart()
    {
        super.onStart();
        checkIfGridAvailable();
    }

    protected void onStop()
    {
        super.onStop();
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }
}
