// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class AccountList
{
    public static class AccountInfo
        implements Parcelable
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public AccountInfo createFromParcel(Parcel parcel)
            {
                return new AccountInfo(parcel, null);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public AccountInfo[] newArray(int i)
            {
                return new AccountInfo[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };
        private UUID GridUUID;
        private String LoginName;
        private String PasswordHash;

        public int describeContents()
        {
            return 0;
        }

        public UUID getGridUUID()
        {
            return GridUUID;
        }

        public String getLoginName()
        {
            return LoginName;
        }

        public String getPasswordHash()
        {
            return PasswordHash;
        }

        public void saveToPreferences(android.content.SharedPreferences.Editor editor, String s)
        {
            editor.putString((new StringBuilder()).append(s).append("_login_name").toString(), LoginName);
            editor.putString((new StringBuilder()).append(s).append("_pwd_hash").toString(), PasswordHash);
            editor.putString((new StringBuilder()).append(s).append("_grid").toString(), GridUUID.toString());
        }

        public void setGridUUID(UUID uuid)
        {
            GridUUID = uuid;
        }

        public void setLoginName(String s)
        {
            LoginName = s;
        }

        public void setPasswordHash(String s)
        {
            PasswordHash = s;
        }

        public void writeToParcel(Parcel parcel, int i)
        {
            parcel.writeString(LoginName);
            parcel.writeString(PasswordHash);
            if (GridUUID != null)
            {
                parcel.writeString(GridUUID.toString());
                return;
            } else
            {
                parcel.writeString("");
                return;
            }
        }


        public AccountInfo(SharedPreferences sharedpreferences, String s)
        {
            LoginName = sharedpreferences.getString((new StringBuilder()).append(s).append("_login_name").toString(), "");
            PasswordHash = sharedpreferences.getString((new StringBuilder()).append(s).append("_pwd_hash").toString(), "");
            GridUUID = UUID.fromString(sharedpreferences.getString((new StringBuilder()).append(s).append("_grid").toString(), ""));
        }

        private AccountInfo(Parcel parcel)
        {
            LoginName = parcel.readString();
            PasswordHash = parcel.readString();
            parcel = parcel.readString();
            if (!parcel.equals(""))
            {
                GridUUID = UUID.fromString(parcel);
                return;
            } else
            {
                GridUUID = null;
                return;
            }
        }

        AccountInfo(Parcel parcel, AccountInfo accountinfo)
        {
            this(parcel);
        }

        public AccountInfo(String s, String s1, UUID uuid)
        {
            LoginName = s;
            PasswordHash = s1;
            GridUUID = uuid;
        }
    }


    private ArrayList accounts;
    private Context context;

    public AccountList(Context context1)
    {
        context = context1;
        accounts = new ArrayList();
        loadAccounts();
    }

    public void addNewAccount(AccountInfo accountinfo)
    {
        accounts.add(accountinfo);
    }

    public void deleteAccount(AccountInfo accountinfo)
    {
        accounts.remove(accountinfo);
    }

    public AccountInfo findAccount(String s, UUID uuid)
    {
        for (Iterator iterator = accounts.iterator(); iterator.hasNext();)
        {
            AccountInfo accountinfo = (AccountInfo)iterator.next();
            if (accountinfo.getLoginName().equals(s) && accountinfo.getGridUUID().equals(uuid))
            {
                return accountinfo;
            }
        }

        return null;
    }

    public AccountInfo findOrAddAccount(String s, String s1, UUID uuid)
    {
        for (Iterator iterator = accounts.iterator(); iterator.hasNext();)
        {
            AccountInfo accountinfo = (AccountInfo)iterator.next();
            if (accountinfo.getLoginName().equals(s) && accountinfo.getGridUUID().equals(uuid))
            {
                accountinfo.setPasswordHash(s1);
                savePreferences();
                return accountinfo;
            }
        }

        s = new AccountInfo(s, s1, uuid);
        accounts.add(s);
        savePreferences();
        return s;
    }

    public List getAccountList()
    {
        return accounts;
    }

    public List getAccountList(List list)
    {
        list.clear();
        list.addAll(accounts);
        return list;
    }

    public void loadAccounts()
    {
        int i = 0;
        accounts.clear();
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        for (int j = sharedpreferences.getInt("accounts_count", 0); i < j; i++)
        {
            accounts.add(new AccountInfo(sharedpreferences, (new StringBuilder()).append("account_").append(i).toString()));
        }

    }

    public void savePreferences()
    {
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        editor.putInt("accounts_count", accounts.size());
        for (int i = 0; i < accounts.size(); i++)
        {
            ((AccountInfo)accounts.get(i)).saveToPreferences(editor, (new StringBuilder()).append("account_").append(i).toString());
        }

        editor.commit();
    }
}
