// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.login;

import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import com.lumiyaviewer.lumiya.ui.accounts.ManageAccountsActivity;
import android.view.Menu;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView$OnItemSelectedListener;
import android.widget.SpinnerAdapter;
import android.widget.TextView$BufferType;
import android.text.style.URLSpan;
import com.lumiyaviewer.lumiya.LumiyaApp;
import android.text.SpannableStringBuilder;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import android.support.v7.app.AlertDialog;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import android.text.Editable;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import android.widget.Spinner;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import android.content.SharedPreferences$Editor;
import android.preference.PreferenceManager;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import android.view.MenuItem;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import java.util.List;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.grids.GridEditDialog;
import android.text.TextWatcher;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class LoginActivity extends ThemedActivity implements View$OnClickListener, TextWatcher, OnGridEditResultListener
{
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SAVE_PASSWORD = "save_password";
    private static final String KEY_SELECTED_GRID = "selected_grid";
    private static final String KEY_TOS_ACCEPTED = "tos_accepted";
    private AccountList accountList;
    private boolean enableAutoClear;
    private GridList.GridArrayAdapter gridDisplayAdapter;
    private List<GridList.GridInfo> gridDisplayList;
    private GridList gridList;
    private int lastSelectedGrid;
    private UUID lastSelectedGridUUID;
    private boolean loggingIn;
    private ImmutableList<MenuItem> menuItems;
    
    public LoginActivity() {
        this.loggingIn = false;
        this.enableAutoClear = false;
        this.lastSelectedGrid = 0;
        this.gridList = null;
        this.accountList = null;
        this.gridDisplayList = new ArrayList<GridList.GridInfo>();
        this.gridDisplayAdapter = null;
        this.menuItems = ImmutableList.of();
    }
    
    private void CheckTOSAndLogin() {
        final View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager)this.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        final SharedPreferences preferences = this.getPreferences(0);
        final GridList.GridInfo selectedGrid = this.getSelectedGrid();
        if (preferences.getBoolean("tos_accepted", false) || (selectedGrid.isLindenGrid() ^ true)) {
            this.DoLogin();
        }
        else {
            this.startActivityForResult(new Intent((Context)this, (Class)TOSActivity.class), 5);
        }
    }
    
    private void DoLogin() {
        final SharedPreferences preferences = this.getPreferences(0);
        final String string = this.findViewById(2131755465).getText().toString();
        String s = this.findViewById(2131755466).getText().toString();
        final GridList.GridInfo selectedGrid = this.getSelectedGrid();
        final boolean checked = this.findViewById(2131755468).isChecked();
        String str = "";
        boolean b;
        if (s.equals(this.getString(2131296994))) {
            str = preferences.getString("password", "");
            b = true;
        }
        else {
            b = false;
        }
        SLURL slurl;
        Object loginStartLocation;
        Object string2;
        AccountList.AccountInfo account;
        String s2 = null;
        int n = 0;
        String string3 = null;
        boolean saveUserName = false;
        SharedPreferences$Editor edit = null;
        AccountList accountList = null;
        Label_0373_Outer:Label_0669_Outer:
        while (true) {
            Label_0360: {
                while (true) {
                    Label_0657_Outer:Label_0663_Outer:
                    while (true) {
                        Label_0291: {
                            Label_0281: {
                                while (true) {
                                    Label_0240: {
                                        while (true) {
                                            Label_0226: {
                                            Label_0619_Outer:
                                                while (true) {
                                                    Label_0176: {
                                                        while (true) {
                                                            Label_0137: {
                                                                if (!b) {
                                                                    str = SLAuth.getPasswordHash(s);
                                                                    Debug.Log("Login: not using saved hash, password = " + s + ", new hash: " + str);
                                                                    break Label_0137;
                                                                }
                                                                Label_0545: {
                                                                    break Label_0545;
                                                                Label_0399_Outer:
                                                                    while (true) {
                                                                        while (true) {
                                                                        Label_0684:
                                                                            while (true) {
                                                                                try {
                                                                                    slurl = new SLURL(this.getIntent());
                                                                                    if (slurl != null) {
                                                                                        loginStartLocation = slurl.getLoginStartLocation();
                                                                                        Debug.Log("Start location (LoginActivity): " + (String)loginStartLocation);
                                                                                        this.loggingIn = true;
                                                                                        string2 = new Intent((Context)this, (Class)GridConnectionService.class);
                                                                                        ((Intent)string2).setAction("com.lumiyaviewer.lumiya.ACTION_LOGIN");
                                                                                        ((Intent)string2).putExtra("login", string);
                                                                                        ((Intent)string2).putExtra("password", str);
                                                                                        ((Intent)string2).putExtra("client_id", s);
                                                                                        ((Intent)string2).putExtra("start_location", (String)loginStartLocation);
                                                                                        ((Intent)string2).putExtra("login_url", selectedGrid.getLoginURL());
                                                                                        ((Intent)string2).putExtra("grid_name", selectedGrid.getGridName());
                                                                                        this.startService((Intent)string2);
                                                                                        this.showProgressView(true);
                                                                                        this.findViewById(2131755473).setText(2131297053);
                                                                                        return;
                                                                                    }
                                                                                    break Label_0684;
                                                                                    Block_14: {
                                                                                        while (true) {
                                                                                            s = str;
                                                                                            iftrue(Label_0590:)(account.getPasswordHash().equals(""));
                                                                                            break Block_14;
                                                                                            s = "";
                                                                                            break Label_0240;
                                                                                            s = "";
                                                                                            break Label_0281;
                                                                                            s2 = "";
                                                                                            break Label_0360;
                                                                                            n = 0;
                                                                                            break Label_0226;
                                                                                            account = this.accountList.findAccount(string, selectedGrid.getGridUUID());
                                                                                            s = str;
                                                                                            iftrue(Label_0590:)(account == null);
                                                                                            continue Label_0657_Outer;
                                                                                        }
                                                                                        this.findViewById(2131755466).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
                                                                                        this.findViewById(2131755466).setText((CharSequence)"");
                                                                                        break Label_0176;
                                                                                    }
                                                                                    s = account.getPasswordHash();
                                                                                    Label_0590: {
                                                                                        Debug.Log("Login: using saved hash, hash = " + s);
                                                                                    }
                                                                                    str = s;
                                                                                    break;
                                                                                }
                                                                                catch (final Exception ex) {
                                                                                    slurl = null;
                                                                                    continue Label_0399_Outer;
                                                                                }
                                                                                break;
                                                                            }
                                                                            loginStartLocation = string2;
                                                                            continue Label_0657_Outer;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            this.enableAutoClear = false;
                                                            if (!checked) {
                                                                continue;
                                                            }
                                                            break;
                                                        }
                                                        this.findViewById(2131755466).setTransformationMethod((TransformationMethod)SingleLineTransformationMethod.getInstance());
                                                        this.findViewById(2131755466).setText(2131296994);
                                                    }
                                                    this.enableAutoClear = true;
                                                    string3 = preferences.getString("client_id", "");
                                                    string2 = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext()).getString("start_location", "last");
                                                    saveUserName = this.getSaveUserName();
                                                    if (!checked) {
                                                        continue Label_0619_Outer;
                                                    }
                                                    break;
                                                }
                                                n = (saveUserName ? 1 : 0);
                                            }
                                            edit = preferences.edit();
                                            if (!saveUserName) {
                                                continue Label_0663_Outer;
                                            }
                                            break;
                                        }
                                        s = string;
                                    }
                                    edit.putString("login", s);
                                    edit.putBoolean("save_password", checked);
                                    if (b && (n ^ 0x1) == 0x0) {
                                        break Label_0291;
                                    }
                                    if (n == 0) {
                                        continue Label_0669_Outer;
                                    }
                                    break;
                                }
                                s = str;
                            }
                            edit.putString("password", s);
                        }
                        if (string3.equals("")) {
                            s = UUID.randomUUID().toString();
                            edit.putString("client_id", s);
                        }
                        else {
                            s = string3;
                        }
                        edit.putString("selected_grid", selectedGrid.getGridUUID().toString());
                        edit.apply();
                        if (!saveUserName) {
                            continue Label_0657_Outer;
                        }
                        break;
                    }
                    accountList = this.accountList;
                    if (n == 0) {
                        continue;
                    }
                    break;
                }
                s2 = str;
            }
            accountList.findOrAddAccount(string, s2, selectedGrid.getGridUUID());
            continue Label_0373_Outer;
        }
    }
    
    private void checkIfGridAvailable() {
        Debug.Log("LoginActivity: checking if grid is available");
        final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
        if (gridConnection != null) {
            final SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
            final UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
            Debug.Log("LoginActivity: connectionState = " + connectionState.toString());
            if (connectionState == SLGridConnection.ConnectionState.Connected && activeAgentUUID != null) {
                Debug.Log("LoginActivity: grid available and connected");
                this.startChatActivity(activeAgentUUID);
                this.finish();
                return;
            }
        }
        this.updateConnectingStatus();
    }
    
    private boolean getSaveUserName() {
        return PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("noSaveUserName", false) ^ true;
    }
    
    private GridList.GridInfo getSelectedGrid() {
        final Object selectedItem = this.findViewById(2131755189).getSelectedItem();
        Object defaultGrid;
        if (selectedItem instanceof GridList.GridInfo) {
            defaultGrid = selectedItem;
        }
        else {
            defaultGrid = this.gridList.getDefaultGrid();
        }
        return (GridList.GridInfo)defaultGrid;
    }
    
    private void loadSavedLogin() {
        final SharedPreferences preferences = this.getPreferences(0);
        if (this.getSaveUserName()) {
            final String string = preferences.getString("password", "");
            this.findViewById(2131755465).setText((CharSequence)preferences.getString("login", ""));
            this.findViewById(2131755468).setChecked(preferences.getBoolean("save_password", true));
            if (!string.equals("")) {
                this.findViewById(2131755466).setTransformationMethod((TransformationMethod)SingleLineTransformationMethod.getInstance());
                this.findViewById(2131755466).setText(2131296994);
            }
            else {
                this.findViewById(2131755466).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
                this.findViewById(2131755466).setText((CharSequence)"");
            }
        }
        else {
            this.findViewById(2131755465).setText((CharSequence)"");
            this.findViewById(2131755466).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            this.findViewById(2131755466).setText((CharSequence)"");
        }
        this.enableAutoClear = true;
    }
    
    private boolean progressViewVisible() {
        final boolean b = false;
        final View viewById = this.findViewById(2131755475);
        boolean b2 = b;
        if (viewById != null) {
            b2 = b;
            if (viewById.getVisibility() == 0) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private void setSelectedGrid() {
        try {
            final String string = this.getPreferences(0).getString("selected_grid", "");
            if (!string.equals("")) {
                final int gridIndex = this.gridList.getGridIndex(UUID.fromString(string));
                this.findViewById(2131755189).setSelection(gridIndex);
                this.lastSelectedGrid = gridIndex;
                final Object selectedItem = this.findViewById(2131755189).getSelectedItem();
                if (selectedItem instanceof GridList.GridInfo) {
                    this.lastSelectedGridUUID = ((GridList.GridInfo)selectedItem).getGridUUID();
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    private void showProgressView(final boolean b) {
        final int n = 8;
        final View viewById = this.findViewById(2131755475);
        final View viewById2 = this.findViewById(2131755462);
        if (viewById != null && viewById2 != null) {
            final View viewById3 = this.findViewById(2131755475);
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById3.setVisibility(visibility);
            final View viewById4 = this.findViewById(2131755462);
            int visibility2;
            if (b) {
                visibility2 = n;
            }
            else {
                visibility2 = 0;
            }
            viewById4.setVisibility(visibility2);
        }
        this.updateMenuItems();
    }
    
    private void startChatActivity(final UUID uuid) {
        final Intent intent = new Intent((Context)this, (Class)ChatNewActivity.class);
        intent.addFlags(67108864);
        intent.putExtra("activeAgentUUID", uuid.toString());
        this.startActivity(intent);
    }
    
    private void updateConnectingStatus() {
        int loggingIn;
        final boolean b = (loggingIn = (this.loggingIn ? 1 : 0)) != 0;
        if (!b) {
            final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
            loggingIn = (b ? 1 : 0);
            if (gridConnection != null) {
                loggingIn = (b ? 1 : 0);
                if (gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Connecting) {
                    this.showProgressView(true);
                    if (gridConnection.getIsReconnecting()) {
                        this.findViewById(2131755473).setText((CharSequence)this.getString(2131297054, new Object[] { gridConnection.getReconnectAttempt() }));
                        loggingIn = 1;
                    }
                    else {
                        this.findViewById(2131755473).setText(2131297053);
                        loggingIn = 1;
                    }
                }
            }
        }
        if (loggingIn == 0) {
            this.showProgressView(false);
        }
    }
    
    private void updateMenuItems() {
        final boolean progressViewVisible = this.progressViewVisible();
        final Iterator<Object> iterator = this.menuItems.iterator();
        while (iterator.hasNext()) {
            iterator.next().setVisible(progressViewVisible ^ true);
        }
    }
    
    public void afterTextChanged(final Editable editable) {
    }
    
    public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
        if (this.enableAutoClear) {
            final EditText editText = this.findViewById(2131755466);
            if (editText.getText().toString().equals(this.getString(2131296994))) {
                this.enableAutoClear = false;
                editText.setText((CharSequence)"");
                editText.setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
                this.enableAutoClear = true;
            }
        }
    }
    
    public SharedPreferences getPreferences(final int n) {
        return this.getSharedPreferences("LoginActivity", n);
    }
    
    @EventHandler
    public void handleLoginResult(final SLLoginResultEvent slLoginResultEvent) {
        this.loggingIn = false;
        Debug.Printf("LoginProgressActivity: result.success = %b", slLoginResultEvent.success);
        if (slLoginResultEvent.success) {
            this.startChatActivity(slLoginResultEvent.activeAgentUUID);
            this.finish();
        }
        else {
            if (!this.isFinishing() && this.progressViewVisible()) {
                String message = "Login to Second Life has failed.";
                if (!Strings.isNullOrEmpty(slLoginResultEvent.message)) {
                    message = slLoginResultEvent.message;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
                builder.setTitle("Login failed");
                builder.setMessage(message);
                builder.setCancelable(true);
                builder.create().show();
            }
            this.showProgressView(false);
        }
    }
    
    @EventHandler
    public void handleReconnectingEvent(final SLReconnectingEvent slReconnectingEvent) {
        this.updateConnectingStatus();
    }
    
    protected void onActivityResult(int gridIndex, final int i, final Intent intent) {
        Debug.Log("LoginActivity: onActivityResult: requestCode = " + gridIndex + ", resultCode = " + i);
        if (intent != null) {
            Debug.Log("LoginActivity: onActivityResult: data = " + intent.getDataString() + ", " + intent.toString());
        }
        else {
            Debug.Log("LoginActivity: onActivityResult: data = null");
        }
        switch (gridIndex) {
            case 5: {
                if (i == -1) {
                    final SharedPreferences$Editor edit = this.getPreferences(0).edit();
                    edit.putBoolean("tos_accepted", true);
                    edit.apply();
                    this.DoLogin();
                    break;
                }
                break;
            }
            case 3: {
                if (i != -1 || intent == null) {
                    break;
                }
                final AccountList.AccountInfo accountInfo = (AccountList.AccountInfo)intent.getParcelableExtra("selected_account");
                if (accountInfo != null) {
                    final String passwordHash = accountInfo.getPasswordHash();
                    this.findViewById(2131755465).setText((CharSequence)accountInfo.getLoginName());
                    this.findViewById(2131755468).setChecked(passwordHash.equals("") ^ true);
                    this.enableAutoClear = false;
                    if (!passwordHash.equals("")) {
                        this.findViewById(2131755466).setTransformationMethod((TransformationMethod)SingleLineTransformationMethod.getInstance());
                        this.findViewById(2131755466).setText(2131296994);
                    }
                    else {
                        this.findViewById(2131755466).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
                        this.findViewById(2131755466).setText((CharSequence)"");
                    }
                    this.enableAutoClear = true;
                    if (accountInfo.getGridUUID() != null) {
                        gridIndex = this.gridList.getGridIndex(accountInfo.getGridUUID());
                        this.findViewById(2131755189).setSelection(gridIndex);
                        this.lastSelectedGrid = gridIndex;
                        final Object selectedItem = this.findViewById(2131755189).getSelectedItem();
                        if (selectedItem instanceof GridList.GridInfo) {
                            this.lastSelectedGridUUID = ((GridList.GridInfo)selectedItem).getGridUUID();
                        }
                    }
                    final SharedPreferences$Editor edit2 = this.getPreferences(0).edit();
                    edit2.putString("login", accountInfo.getLoginName());
                    edit2.putBoolean("save_password", passwordHash.equals("") ^ true);
                    edit2.putString("password", passwordHash);
                    if (accountInfo.getGridUUID() != null) {
                        edit2.putString("selected_grid", accountInfo.getGridUUID().toString());
                    }
                    edit2.apply();
                    break;
                }
                break;
            }
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755469: {
                this.CheckTOSAndLogin();
                break;
            }
            case 2131755463: {
                this.startActivity(new Intent((Context)this, (Class)WhatsNewActivity.class));
                break;
            }
            case 2131755474: {
                this.loggingIn = false;
                final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
                if (gridConnection != null) {
                    gridConnection.CancelConnect();
                }
                this.showProgressView(false);
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
        if (gridConnection != null) {
            final SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
            final UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
            Debug.Log("LoginActivity: connectionState = " + connectionState.toString());
            if (connectionState == SLGridConnection.ConnectionState.Connected && activeAgentUUID != null) {
                this.startChatActivity(activeAgentUUID);
                this.finish();
                return;
            }
        }
        this.setContentView(2130968664);
        Debug.Log("LoginActivity: created.");
        this.gridList = new GridList((Context)this);
        this.accountList = new AccountList((Context)this);
        this.gridList.getGridList(this.gridDisplayList, true);
        this.enableAutoClear = false;
        this.findViewById(2131755469).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755466).addTextChangedListener((TextWatcher)this);
        this.loadSavedLogin();
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence)this.getString(2131297162, new Object[] { LumiyaApp.getAppVersion() }));
        spannableStringBuilder.setSpan((Object)new URLSpan(""), 0, spannableStringBuilder.length(), 33);
        this.findViewById(2131755463).setText((CharSequence)spannableStringBuilder, TextView$BufferType.SPANNABLE);
        this.findViewById(2131755463).setClickable(true);
        this.findViewById(2131755463).setOnClickListener((View$OnClickListener)this);
        this.gridDisplayAdapter = new GridList.GridArrayAdapter((Context)this, this.gridDisplayList);
        this.findViewById(2131755189).setAdapter((SpinnerAdapter)this.gridDisplayAdapter);
        this.setSelectedGrid();
        this.findViewById(2131755189).setOnItemSelectedListener((AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                if (n != LoginActivity.this.lastSelectedGrid) {
                    final Object item = adapterView.getAdapter().getItem(n);
                    if (item instanceof GridList.GridInfo) {
                        final GridList.GridInfo gridInfo = (GridList.GridInfo)item;
                        if (gridInfo.getLoginURL() == null) {
                            final GridEditDialog gridEditDialog = new GridEditDialog((Context)LoginActivity.this, LoginActivity.this.gridList, null);
                            gridEditDialog.setOnGridEditResultListener((GridEditDialog.OnGridEditResultListener)LoginActivity.this);
                            gridEditDialog.show();
                        }
                        else {
                            LoginActivity.this.lastSelectedGrid = n;
                            LoginActivity.this.lastSelectedGridUUID = gridInfo.getGridUUID();
                        }
                    }
                }
            }
            
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        this.findViewById(2131755463).getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new _$Lambda$U_ZFuxgsYW8weMauiDTqAtaKePI(this));
        this.checkIfGridAvailable();
        this.findViewById(2131755474).setOnClickListener((View$OnClickListener)this);
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(2131886095, menu);
        final ImmutableList.Builder<MenuItem> builder = ImmutableList.builder();
        builder.add(menu.findItem(2131755820));
        builder.add(menu.findItem(2131755787));
        builder.add(menu.findItem(2131755821));
        builder.add(menu.findItem(2131755822));
        this.menuItems = builder.build();
        return true;
    }
    
    public void onGridAdded(final GridList.GridInfo gridInfo, final boolean b) {
        if (b) {
            this.gridList.addNewGrid(gridInfo);
        }
        this.gridList.getGridList(this.gridDisplayList, true);
        this.gridDisplayAdapter.notifyDataSetChanged();
        final int count = this.findViewById(2131755189).getAdapter().getCount();
        if (count > 1) {
            this.findViewById(2131755189).setSelection(count - 2);
            this.lastSelectedGrid = count - 2;
            final Object selectedItem = this.findViewById(2131755189).getSelectedItem();
            if (selectedItem instanceof GridList.GridInfo) {
                this.lastSelectedGridUUID = ((GridList.GridInfo)selectedItem).getGridUUID();
            }
        }
    }
    
    public void onGridDeleted(final GridList.GridInfo gridInfo) {
    }
    
    public void onGridEditCancelled() {
        this.findViewById(2131755189).setSelection(this.lastSelectedGrid);
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755820: {
                this.startActivityForResult(new Intent((Context)this, (Class)ManageAccountsActivity.class), 3);
                return true;
            }
            case 2131755822: {
                final EditText editText = this.findViewById(2131755466);
                editText.setTransformationMethod((TransformationMethod)SingleLineTransformationMethod.getInstance());
                editText.setInputType(145);
                return true;
            }
            case 2131755821: {
                this.startActivity(new Intent((Context)this, (Class)ManageGridsActivity.class));
                return true;
            }
            case 2131755787: {
                this.startActivity(new Intent((Context)this, (Class)SettingsActivity.class));
                return true;
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Debug.Printf("LoginActivity: Resumed.", new Object[0]);
        this.checkIfGridAvailable();
        this.gridList.loadGrids();
        this.gridList.getGridList(this.gridDisplayList, true);
        this.gridDisplayAdapter.notifyDataSetChanged();
        if (this.lastSelectedGridUUID != null) {
            this.findViewById(2131755189).setSelection(this.gridList.getGridIndex(this.lastSelectedGridUUID));
        }
        this.accountList.loadAccounts();
        if (this.getSaveUserName()) {
            this.findViewById(2131755468).setEnabled(true);
        }
        else {
            this.findViewById(2131755465).setText((CharSequence)"");
            this.findViewById(2131755466).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            this.findViewById(2131755466).setText((CharSequence)"");
            this.findViewById(2131755468).setEnabled(false);
            this.findViewById(2131755468).setChecked(false);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.checkIfGridAvailable();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
}
