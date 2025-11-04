// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;
import android.content.Context;
import java.util.ArrayList;

public class AccountList
{
    private ArrayList<AccountInfo> accounts;
    private Context context;
    
    public AccountList(final Context context) {
        this.context = context;
        this.accounts = new ArrayList<AccountInfo>();
        this.loadAccounts();
    }
    
    public void addNewAccount(final AccountInfo e) {
        this.accounts.add(e);
    }
    
    public void deleteAccount(final AccountInfo o) {
        this.accounts.remove(o);
    }
    
    public AccountInfo findAccount(final String anObject, final UUID obj) {
        for (final AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(anObject) && accountInfo.getGridUUID().equals(obj)) {
                return accountInfo;
            }
        }
        return null;
    }
    
    public AccountInfo findOrAddAccount(final String anObject, final String passwordHash, final UUID obj) {
        for (final AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(anObject) && accountInfo.getGridUUID().equals(obj)) {
                accountInfo.setPasswordHash(passwordHash);
                this.savePreferences();
                return accountInfo;
            }
        }
        final AccountInfo e = new AccountInfo(anObject, passwordHash, obj);
        this.accounts.add(e);
        this.savePreferences();
        return e;
    }
    
    public List<AccountInfo> getAccountList() {
        return this.accounts;
    }
    
    public List<AccountInfo> getAccountList(final List<AccountInfo> list) {
        list.clear();
        list.addAll(this.accounts);
        return list;
    }
    
    public void loadAccounts() {
        int i = 0;
        this.accounts.clear();
        for (SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()); i < defaultSharedPreferences.getInt("accounts_count", 0); ++i) {
            this.accounts.add(new AccountInfo(defaultSharedPreferences, "account_" + i));
        }
    }
    
    public void savePreferences() {
        final SharedPreferences$Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit();
        edit.putInt("accounts_count", this.accounts.size());
        for (int i = 0; i < this.accounts.size(); ++i) {
            this.accounts.get(i).saveToPreferences(edit, "account_" + i);
        }
        edit.commit();
    }
    
    public static class AccountInfo implements Parcelable
    {
        public static final Parcelable$Creator<AccountInfo> CREATOR;
        private UUID GridUUID;
        private String LoginName;
        private String PasswordHash;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<AccountInfo>() {
                public AccountInfo createFromParcel(final Parcel parcel) {
                    return new AccountInfo(parcel, null);
                }
                
                public AccountInfo[] newArray(final int n) {
                    return new AccountInfo[n];
                }
            };
        }
        
        public AccountInfo(final SharedPreferences sharedPreferences, final String str) {
            this.LoginName = sharedPreferences.getString(str + "_login_name", "");
            this.PasswordHash = sharedPreferences.getString(str + "_pwd_hash", "");
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""));
        }
        
        private AccountInfo(final Parcel parcel) {
            this.LoginName = parcel.readString();
            this.PasswordHash = parcel.readString();
            final String string = parcel.readString();
            if (!string.equals("")) {
                this.GridUUID = UUID.fromString(string);
            }
            else {
                this.GridUUID = null;
            }
        }
        
        public AccountInfo(final String loginName, final String passwordHash, final UUID gridUUID) {
            this.LoginName = loginName;
            this.PasswordHash = passwordHash;
            this.GridUUID = gridUUID;
        }
        
        public int describeContents() {
            return 0;
        }
        
        public UUID getGridUUID() {
            return this.GridUUID;
        }
        
        public String getLoginName() {
            return this.LoginName;
        }
        
        public String getPasswordHash() {
            return this.PasswordHash;
        }
        
        public void saveToPreferences(final SharedPreferences$Editor sharedPreferences$Editor, final String str) {
            sharedPreferences$Editor.putString(str + "_login_name", this.LoginName);
            sharedPreferences$Editor.putString(str + "_pwd_hash", this.PasswordHash);
            sharedPreferences$Editor.putString(str + "_grid", this.GridUUID.toString());
        }
        
        public void setGridUUID(final UUID gridUUID) {
            this.GridUUID = gridUUID;
        }
        
        public void setLoginName(final String loginName) {
            this.LoginName = loginName;
        }
        
        public void setPasswordHash(final String passwordHash) {
            this.PasswordHash = passwordHash;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeString(this.LoginName);
            parcel.writeString(this.PasswordHash);
            if (this.GridUUID != null) {
                parcel.writeString(this.GridUUID.toString());
            }
            else {
                parcel.writeString("");
            }
        }
    }
}
