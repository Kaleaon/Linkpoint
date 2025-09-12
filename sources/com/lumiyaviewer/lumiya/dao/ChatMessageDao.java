// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            ChatMessage, DaoSession

public class ChatMessageDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Accepted = new Property(22, java/lang/Boolean, "accepted", false, "ACCEPTED");
        public static final Property AssetType = new Property(17, java/lang/Integer, "assetType", false, "ASSET_TYPE");
        public static final Property ChatChannel = new Property(20, java/lang/Integer, "chatChannel", false, "CHAT_CHANNEL");
        public static final Property ChatterID;
        public static final Property DialogButtons = new Property(26, [B, "dialogButtons", false, "DIALOG_BUTTONS");
        public static final Property DialogIgnored = new Property(21, java/lang/Boolean, "dialogIgnored", false, "DIALOG_IGNORED");
        public static final Property DialogSelectedOption = new Property(27, java/lang/String, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION");
        public static final Property EventState = new Property(12, java/lang/Integer, "eventState", false, "EVENT_STATE");
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property IsOffline = new Property(5, java/lang/Boolean, "isOffline", false, "IS_OFFLINE");
        public static final Property ItemID = new Property(15, java/util/UUID, "itemID", false, "ITEM_ID");
        public static final Property ItemName = new Property(16, java/lang/String, "itemName", false, "ITEM_NAME");
        public static final Property MessageText = new Property(10, java/lang/String, "messageText", false, "MESSAGE_TEXT");
        public static final Property MessageType;
        public static final Property NewBalance = new Property(19, java/lang/Integer, "newBalance", false, "NEW_BALANCE");
        public static final Property ObjectName = new Property(24, java/lang/String, "objectName", false, "OBJECT_NAME");
        public static final Property OrigIMType = new Property(13, java/lang/Integer, "origIMType", false, "ORIG_IMTYPE");
        public static final Property OrigTimestamp = new Property(4, java/util/Date, "origTimestamp", false, "ORIG_TIMESTAMP");
        public static final Property QuestionMask = new Property(25, java/lang/Integer, "questionMask", false, "QUESTION_MASK");
        public static final Property SenderLegacyName = new Property(9, java/lang/String, "senderLegacyName", false, "SENDER_LEGACY_NAME");
        public static final Property SenderName = new Property(8, java/lang/String, "senderName", false, "SENDER_NAME");
        public static final Property SenderType = new Property(7, java/lang/Integer, "senderType", false, "SENDER_TYPE");
        public static final Property SenderUUID = new Property(6, java/util/UUID, "senderUUID", false, "SENDER_UUID");
        public static final Property SessionID = new Property(14, java/util/UUID, "sessionID", false, "SESSION_ID");
        public static final Property SyncedToGoogleDrive;
        public static final Property TextBoxButtonIndex = new Property(28, java/lang/Integer, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX");
        public static final Property Timestamp = new Property(2, java/util/Date, "timestamp", false, "TIMESTAMP");
        public static final Property TransactionAmount = new Property(18, java/lang/Integer, "transactionAmount", false, "TRANSACTION_AMOUNT");
        public static final Property UserID = new Property(23, java/util/UUID, "userID", false, "USER_ID");
        public static final Property ViewType;

        static 
        {
            ChatterID = new Property(1, Long.TYPE, "chatterID", false, "CHATTER_ID");
            ViewType = new Property(3, Integer.TYPE, "viewType", false, "VIEW_TYPE");
            MessageType = new Property(11, Integer.TYPE, "messageType", false, "MESSAGE_TYPE");
            SyncedToGoogleDrive = new Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "CHAT_MESSAGE";

    public ChatMessageDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public ChatMessageDao(DaoConfig daoconfig, DaoSession daosession)
    {
        super(daoconfig, daosession);
    }

    public static void createTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        String s;
        if (flag)
        {
            s = "IF NOT EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CHAT_MESSAGE' (").append("'_id' INTEGER PRIMARY KEY ,").append("'CHATTER_ID' INTEGER NOT NULL ,").append("'TIMESTAMP' INTEGER NOT NULL ,").append("'VIEW_TYPE' INTEGER NOT NULL ,").append("'ORIG_TIMESTAMP' INTEGER,").append("'IS_OFFLINE' INTEGER,").append("'SENDER_UUID' TEXT,").append("'SENDER_TYPE' INTEGER,").append("'SENDER_NAME' TEXT,").append("'SENDER_LEGACY_NAME' TEXT,").append("'MESSAGE_TEXT' TEXT,").append("'MESSAGE_TYPE' INTEGER NOT NULL ,").append("'EVENT_STATE' INTEGER,").append("'ORIG_IMTYPE' INTEGER,").append("'SESSION_ID' TEXT,").append("'ITEM_ID' TEXT,").append("'ITEM_NAME' TEXT,").append("'ASSET_TYPE' INTEGER,").append("'TRANSACTION_AMOUNT' INTEGER,").append("'NEW_BALANCE' INTEGER,").append("'CHAT_CHANNEL' INTEGER,").append("'DIALOG_IGNORED' INTEGER,").append("'ACCEPTED' INTEGER,").append("'USER_ID' TEXT,").append("'OBJECT_NAME' TEXT,").append("'QUESTION_MASK' INTEGER,").append("'DIALOG_BUTTONS' BLOB,").append("'DIALOG_SELECTED_OPTION' TEXT,").append("'TEXT_BOX_BUTTON_INDEX' INTEGER,").append("'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE").append(" (CHATTER_ID);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE").append(" (_id,SYNCED_TO_GOOGLE_DRIVE);").toString());
    }

    public static void dropTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        StringBuilder stringbuilder = (new StringBuilder()).append("DROP TABLE ");
        String s;
        if (flag)
        {
            s = "IF EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CHAT_MESSAGE'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, ChatMessage chatmessage)
    {
        long l1 = 1L;
        sqlitestatement.clearBindings();
        Object obj = chatmessage.getId();
        if (obj != null)
        {
            sqlitestatement.bindLong(1, ((Long) (obj)).longValue());
        }
        sqlitestatement.bindLong(2, chatmessage.getChatterID());
        sqlitestatement.bindLong(3, chatmessage.getTimestamp().getTime());
        sqlitestatement.bindLong(4, chatmessage.getViewType());
        obj = chatmessage.getOrigTimestamp();
        if (obj != null)
        {
            sqlitestatement.bindLong(5, ((Date) (obj)).getTime());
        }
        obj = chatmessage.getIsOffline();
        long l;
        if (obj != null)
        {
            byte abyte0[];
            if (((Boolean) (obj)).booleanValue())
            {
                l = 1L;
            } else
            {
                l = 0L;
            }
            sqlitestatement.bindLong(6, l);
        }
        obj = chatmessage.getSenderUUID();
        if (obj != null)
        {
            sqlitestatement.bindString(7, ((UUID) (obj)).toString());
        }
        obj = chatmessage.getSenderType();
        if (obj != null)
        {
            sqlitestatement.bindLong(8, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getSenderName();
        if (obj != null)
        {
            sqlitestatement.bindString(9, ((String) (obj)));
        }
        obj = chatmessage.getSenderLegacyName();
        if (obj != null)
        {
            sqlitestatement.bindString(10, ((String) (obj)));
        }
        obj = chatmessage.getMessageText();
        if (obj != null)
        {
            sqlitestatement.bindString(11, ((String) (obj)));
        }
        sqlitestatement.bindLong(12, chatmessage.getMessageType());
        obj = chatmessage.getEventState();
        if (obj != null)
        {
            sqlitestatement.bindLong(13, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getOrigIMType();
        if (obj != null)
        {
            sqlitestatement.bindLong(14, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getSessionID();
        if (obj != null)
        {
            sqlitestatement.bindString(15, ((UUID) (obj)).toString());
        }
        obj = chatmessage.getItemID();
        if (obj != null)
        {
            sqlitestatement.bindString(16, ((UUID) (obj)).toString());
        }
        obj = chatmessage.getItemName();
        if (obj != null)
        {
            sqlitestatement.bindString(17, ((String) (obj)));
        }
        obj = chatmessage.getAssetType();
        if (obj != null)
        {
            sqlitestatement.bindLong(18, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getTransactionAmount();
        if (obj != null)
        {
            sqlitestatement.bindLong(19, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getNewBalance();
        if (obj != null)
        {
            sqlitestatement.bindLong(20, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getChatChannel();
        if (obj != null)
        {
            sqlitestatement.bindLong(21, ((Integer) (obj)).intValue());
        }
        obj = chatmessage.getDialogIgnored();
        if (obj != null)
        {
            if (((Boolean) (obj)).booleanValue())
            {
                l = 1L;
            } else
            {
                l = 0L;
            }
            sqlitestatement.bindLong(22, l);
        }
        obj = chatmessage.getAccepted();
        if (obj != null)
        {
            if (((Boolean) (obj)).booleanValue())
            {
                l = 1L;
            } else
            {
                l = 0L;
            }
            sqlitestatement.bindLong(23, l);
        }
        obj = chatmessage.getUserID();
        if (obj != null)
        {
            sqlitestatement.bindString(24, ((UUID) (obj)).toString());
        }
        obj = chatmessage.getObjectName();
        if (obj != null)
        {
            sqlitestatement.bindString(25, ((String) (obj)));
        }
        obj = chatmessage.getQuestionMask();
        if (obj != null)
        {
            sqlitestatement.bindLong(26, ((Integer) (obj)).intValue());
        }
        abyte0 = chatmessage.getDialogButtons();
        if (abyte0 != null)
        {
            sqlitestatement.bindBlob(27, abyte0);
        }
        abyte0 = chatmessage.getDialogSelectedOption();
        if (abyte0 != null)
        {
            sqlitestatement.bindString(28, abyte0);
        }
        abyte0 = chatmessage.getTextBoxButtonIndex();
        if (abyte0 != null)
        {
            sqlitestatement.bindLong(29, abyte0.intValue());
        }
        if (chatmessage.getSyncedToGoogleDrive())
        {
            l = l1;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(30, l);
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (ChatMessage)obj);
    }

    public Long getKey(ChatMessage chatmessage)
    {
        if (chatmessage != null)
        {
            return chatmessage.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((ChatMessage)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public ChatMessage readEntity(Cursor cursor, int i)
    {
        Long long1;
        Date date;
        Boolean boolean1;
        UUID uuid;
        Integer integer;
        String s;
        String s1;
        String s2;
        Integer integer1;
        Integer integer2;
        UUID uuid1;
        UUID uuid2;
        String s3;
        Integer integer3;
        Integer integer4;
        Integer integer5;
        Integer integer6;
        Boolean boolean2;
        Boolean boolean3;
        UUID uuid3;
        String s4;
        Integer integer7;
        byte abyte0[];
        String s5;
        Integer integer8;
        Date date1;
        int j;
        int k;
        long l;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        l = cursor.getLong(i + 1);
        date1 = new Date(cursor.getLong(i + 2));
        j = cursor.getInt(i + 3);
        if (cursor.isNull(i + 4))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 4));
        }
        if (cursor.isNull(i + 5))
        {
            boolean1 = null;
        } else
        {
            if (cursor.getShort(i + 5) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean1 = Boolean.valueOf(flag);
        }
        if (cursor.isNull(i + 6))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 6));
        }
        if (cursor.isNull(i + 7))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 7));
        }
        if (cursor.isNull(i + 8))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 8);
        }
        if (cursor.isNull(i + 9))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 9);
        }
        if (cursor.isNull(i + 10))
        {
            s2 = null;
        } else
        {
            s2 = cursor.getString(i + 10);
        }
        k = cursor.getInt(i + 11);
        if (cursor.isNull(i + 12))
        {
            integer1 = null;
        } else
        {
            integer1 = Integer.valueOf(cursor.getInt(i + 12));
        }
        if (cursor.isNull(i + 13))
        {
            integer2 = null;
        } else
        {
            integer2 = Integer.valueOf(cursor.getInt(i + 13));
        }
        if (cursor.isNull(i + 14))
        {
            uuid1 = null;
        } else
        {
            uuid1 = UUID.fromString(cursor.getString(i + 14));
        }
        if (cursor.isNull(i + 15))
        {
            uuid2 = null;
        } else
        {
            uuid2 = UUID.fromString(cursor.getString(i + 15));
        }
        if (cursor.isNull(i + 16))
        {
            s3 = null;
        } else
        {
            s3 = cursor.getString(i + 16);
        }
        if (cursor.isNull(i + 17))
        {
            integer3 = null;
        } else
        {
            integer3 = Integer.valueOf(cursor.getInt(i + 17));
        }
        if (cursor.isNull(i + 18))
        {
            integer4 = null;
        } else
        {
            integer4 = Integer.valueOf(cursor.getInt(i + 18));
        }
        if (cursor.isNull(i + 19))
        {
            integer5 = null;
        } else
        {
            integer5 = Integer.valueOf(cursor.getInt(i + 19));
        }
        if (cursor.isNull(i + 20))
        {
            integer6 = null;
        } else
        {
            integer6 = Integer.valueOf(cursor.getInt(i + 20));
        }
        if (cursor.isNull(i + 21))
        {
            boolean2 = null;
        } else
        {
            if (cursor.getShort(i + 21) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean2 = Boolean.valueOf(flag);
        }
        if (cursor.isNull(i + 22))
        {
            boolean3 = null;
        } else
        {
            if (cursor.getShort(i + 22) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean3 = Boolean.valueOf(flag);
        }
        if (cursor.isNull(i + 23))
        {
            uuid3 = null;
        } else
        {
            uuid3 = UUID.fromString(cursor.getString(i + 23));
        }
        if (cursor.isNull(i + 24))
        {
            s4 = null;
        } else
        {
            s4 = cursor.getString(i + 24);
        }
        if (cursor.isNull(i + 25))
        {
            integer7 = null;
        } else
        {
            integer7 = Integer.valueOf(cursor.getInt(i + 25));
        }
        if (cursor.isNull(i + 26))
        {
            abyte0 = null;
        } else
        {
            abyte0 = cursor.getBlob(i + 26);
        }
        if (cursor.isNull(i + 27))
        {
            s5 = null;
        } else
        {
            s5 = cursor.getString(i + 27);
        }
        if (cursor.isNull(i + 28))
        {
            integer8 = null;
        } else
        {
            integer8 = Integer.valueOf(cursor.getInt(i + 28));
        }
        if (cursor.getShort(i + 29) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        return new ChatMessage(long1, l, date1, j, date, boolean1, uuid, integer, s, s1, s2, k, integer1, integer2, uuid1, uuid2, s3, integer3, integer4, integer5, integer6, boolean2, boolean3, uuid3, s4, integer7, abyte0, s5, integer8, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, ChatMessage chatmessage, int i)
    {
        boolean flag1 = true;
        Object obj1 = null;
        Object obj;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            obj = null;
        } else
        {
            obj = Long.valueOf(cursor.getLong(i + 0));
        }
        chatmessage.setId(((Long) (obj)));
        chatmessage.setChatterID(cursor.getLong(i + 1));
        chatmessage.setTimestamp(new Date(cursor.getLong(i + 2)));
        chatmessage.setViewType(cursor.getInt(i + 3));
        if (cursor.isNull(i + 4))
        {
            obj = null;
        } else
        {
            obj = new Date(cursor.getLong(i + 4));
        }
        chatmessage.setOrigTimestamp(((Date) (obj)));
        if (cursor.isNull(i + 5))
        {
            obj = null;
        } else
        {
            if (cursor.getShort(i + 5) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            obj = Boolean.valueOf(flag);
        }
        chatmessage.setIsOffline(((Boolean) (obj)));
        if (cursor.isNull(i + 6))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 6));
        }
        chatmessage.setSenderUUID(((UUID) (obj)));
        if (cursor.isNull(i + 7))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 7));
        }
        chatmessage.setSenderType(((Integer) (obj)));
        if (cursor.isNull(i + 8))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 8);
        }
        chatmessage.setSenderName(((String) (obj)));
        if (cursor.isNull(i + 9))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 9);
        }
        chatmessage.setSenderLegacyName(((String) (obj)));
        if (cursor.isNull(i + 10))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 10);
        }
        chatmessage.setMessageText(((String) (obj)));
        chatmessage.setMessageType(cursor.getInt(i + 11));
        if (cursor.isNull(i + 12))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 12));
        }
        chatmessage.setEventState(((Integer) (obj)));
        if (cursor.isNull(i + 13))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 13));
        }
        chatmessage.setOrigIMType(((Integer) (obj)));
        if (cursor.isNull(i + 14))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 14));
        }
        chatmessage.setSessionID(((UUID) (obj)));
        if (cursor.isNull(i + 15))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 15));
        }
        chatmessage.setItemID(((UUID) (obj)));
        if (cursor.isNull(i + 16))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 16);
        }
        chatmessage.setItemName(((String) (obj)));
        if (cursor.isNull(i + 17))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 17));
        }
        chatmessage.setAssetType(((Integer) (obj)));
        if (cursor.isNull(i + 18))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 18));
        }
        chatmessage.setTransactionAmount(((Integer) (obj)));
        if (cursor.isNull(i + 19))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 19));
        }
        chatmessage.setNewBalance(((Integer) (obj)));
        if (cursor.isNull(i + 20))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 20));
        }
        chatmessage.setChatChannel(((Integer) (obj)));
        if (cursor.isNull(i + 21))
        {
            obj = null;
        } else
        {
            if (cursor.getShort(i + 21) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            obj = Boolean.valueOf(flag);
        }
        chatmessage.setDialogIgnored(((Boolean) (obj)));
        if (cursor.isNull(i + 22))
        {
            obj = null;
        } else
        {
            if (cursor.getShort(i + 22) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            obj = Boolean.valueOf(flag);
        }
        chatmessage.setAccepted(((Boolean) (obj)));
        if (cursor.isNull(i + 23))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 23));
        }
        chatmessage.setUserID(((UUID) (obj)));
        if (cursor.isNull(i + 24))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 24);
        }
        chatmessage.setObjectName(((String) (obj)));
        if (cursor.isNull(i + 25))
        {
            obj = null;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 25));
        }
        chatmessage.setQuestionMask(((Integer) (obj)));
        if (cursor.isNull(i + 26))
        {
            obj = null;
        } else
        {
            obj = cursor.getBlob(i + 26);
        }
        chatmessage.setDialogButtons(((byte []) (obj)));
        if (cursor.isNull(i + 27))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 27);
        }
        chatmessage.setDialogSelectedOption(((String) (obj)));
        if (cursor.isNull(i + 28))
        {
            obj = obj1;
        } else
        {
            obj = Integer.valueOf(cursor.getInt(i + 28));
        }
        chatmessage.setTextBoxButtonIndex(((Integer) (obj)));
        if (cursor.getShort(i + 29) != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        chatmessage.setSyncedToGoogleDrive(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (ChatMessage)obj, i);
    }

    public Long readKey(Cursor cursor, int i)
    {
        if (cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return Long.valueOf(cursor.getLong(i + 0));
        }
    }

    public volatile Object readKey(Cursor cursor, int i)
    {
        return readKey(cursor, i);
    }

    protected Long updateKeyAfterInsert(ChatMessage chatmessage, long l)
    {
        chatmessage.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((ChatMessage)obj, l);
    }
}
