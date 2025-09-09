package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Date;
import java.util.UUID;

public class ChatMessageDao extends AbstractDao<ChatMessage, Long> {
    public static final String TABLENAME = "CHAT_MESSAGE";

    public static class Properties {
        public static final Property Accepted = new Property(22, Boolean.class, "accepted", false, "ACCEPTED");
        public static final Property AssetType = new Property(17, Integer.class, "assetType", false, "ASSET_TYPE");
        public static final Property ChatChannel = new Property(20, Integer.class, "chatChannel", false, "CHAT_CHANNEL");
        public static final Property ChatterID = new Property(1, Long.TYPE, ChatterFragment.CHATTER_ID_KEY, false, "CHATTER_ID");
        public static final Property DialogButtons = new Property(26, byte[].class, "dialogButtons", false, "DIALOG_BUTTONS");
        public static final Property DialogIgnored = new Property(21, Boolean.class, "dialogIgnored", false, "DIALOG_IGNORED");
        public static final Property DialogSelectedOption = new Property(27, String.class, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION");
        public static final Property EventState = new Property(12, Integer.class, "eventState", false, "EVENT_STATE");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property IsOffline = new Property(5, Boolean.class, "isOffline", false, "IS_OFFLINE");
        public static final Property ItemID = new Property(15, UUID.class, "itemID", false, "ITEM_ID");
        public static final Property ItemName = new Property(16, String.class, "itemName", false, "ITEM_NAME");
        public static final Property MessageText = new Property(10, String.class, "messageText", false, "MESSAGE_TEXT");
        public static final Property MessageType = new Property(11, Integer.TYPE, "messageType", false, "MESSAGE_TYPE");
        public static final Property NewBalance = new Property(19, Integer.class, "newBalance", false, "NEW_BALANCE");
        public static final Property ObjectName = new Property(24, String.class, "objectName", false, "OBJECT_NAME");
        public static final Property OrigIMType = new Property(13, Integer.class, "origIMType", false, "ORIG_IMTYPE");
        public static final Property OrigTimestamp = new Property(4, Date.class, "origTimestamp", false, "ORIG_TIMESTAMP");
        public static final Property QuestionMask = new Property(25, Integer.class, "questionMask", false, "QUESTION_MASK");
        public static final Property SenderLegacyName = new Property(9, String.class, "senderLegacyName", false, "SENDER_LEGACY_NAME");
        public static final Property SenderName = new Property(8, String.class, "senderName", false, "SENDER_NAME");
        public static final Property SenderType = new Property(7, Integer.class, "senderType", false, "SENDER_TYPE");
        public static final Property SenderUUID = new Property(6, UUID.class, "senderUUID", false, "SENDER_UUID");
        public static final Property SessionID = new Property(14, UUID.class, "sessionID", false, "SESSION_ID");
        public static final Property SyncedToGoogleDrive = new Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE");
        public static final Property TextBoxButtonIndex = new Property(28, Integer.class, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX");
        public static final Property Timestamp = new Property(2, Date.class, "timestamp", false, "TIMESTAMP");
        public static final Property TransactionAmount = new Property(18, Integer.class, "transactionAmount", false, "TRANSACTION_AMOUNT");
        public static final Property UserID = new Property(23, UUID.class, "userID", false, "USER_ID");
        public static final Property ViewType = new Property(3, Integer.TYPE, "viewType", false, "VIEW_TYPE");
    }

    public ChatMessageDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public ChatMessageDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHAT_MESSAGE' (" + "'_id' INTEGER PRIMARY KEY ," + "'CHATTER_ID' INTEGER NOT NULL ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'VIEW_TYPE' INTEGER NOT NULL ," + "'ORIG_TIMESTAMP' INTEGER," + "'IS_OFFLINE' INTEGER," + "'SENDER_UUID' TEXT," + "'SENDER_TYPE' INTEGER," + "'SENDER_NAME' TEXT," + "'SENDER_LEGACY_NAME' TEXT," + "'MESSAGE_TEXT' TEXT," + "'MESSAGE_TYPE' INTEGER NOT NULL ," + "'EVENT_STATE' INTEGER," + "'ORIG_IMTYPE' INTEGER," + "'SESSION_ID' TEXT," + "'ITEM_ID' TEXT," + "'ITEM_NAME' TEXT," + "'ASSET_TYPE' INTEGER," + "'TRANSACTION_AMOUNT' INTEGER," + "'NEW_BALANCE' INTEGER," + "'CHAT_CHANNEL' INTEGER," + "'DIALOG_IGNORED' INTEGER," + "'ACCEPTED' INTEGER," + "'USER_ID' TEXT," + "'OBJECT_NAME' TEXT," + "'QUESTION_MASK' INTEGER," + "'DIALOG_BUTTONS' BLOB," + "'DIALOG_SELECTED_OPTION' TEXT," + "'TEXT_BOX_BUTTON_INDEX' INTEGER," + "'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE" + " (CHATTER_ID);");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE" + " (_id,SYNCED_TO_GOOGLE_DRIVE);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHAT_MESSAGE'");
    }

    protected void bindValues(SQLiteStatement sQLiteStatement, ChatMessage chatMessage) {
        long j = 1;
        sQLiteStatement.clearBindings();
        Long id = chatMessage.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        sQLiteStatement.bindLong(2, chatMessage.getChatterID());
        sQLiteStatement.bindLong(3, chatMessage.getTimestamp().getTime());
        sQLiteStatement.bindLong(4, (long) chatMessage.getViewType());
        Date origTimestamp = chatMessage.getOrigTimestamp();
        if (origTimestamp != null) {
            sQLiteStatement.bindLong(5, origTimestamp.getTime());
        }
        Boolean isOffline = chatMessage.getIsOffline();
        if (isOffline != null) {
            sQLiteStatement.bindLong(6, isOffline.booleanValue() ? 1 : 0);
        }
        UUID senderUUID = chatMessage.getSenderUUID();
        if (senderUUID != null) {
            sQLiteStatement.bindString(7, senderUUID.toString());
        }
        Integer senderType = chatMessage.getSenderType();
        if (senderType != null) {
            sQLiteStatement.bindLong(8, (long) senderType.intValue());
        }
        String senderName = chatMessage.getSenderName();
        if (senderName != null) {
            sQLiteStatement.bindString(9, senderName);
        }
        senderName = chatMessage.getSenderLegacyName();
        if (senderName != null) {
            sQLiteStatement.bindString(10, senderName);
        }
        senderName = chatMessage.getMessageText();
        if (senderName != null) {
            sQLiteStatement.bindString(11, senderName);
        }
        sQLiteStatement.bindLong(12, (long) chatMessage.getMessageType());
        senderType = chatMessage.getEventState();
        if (senderType != null) {
            sQLiteStatement.bindLong(13, (long) senderType.intValue());
        }
        senderType = chatMessage.getOrigIMType();
        if (senderType != null) {
            sQLiteStatement.bindLong(14, (long) senderType.intValue());
        }
        senderUUID = chatMessage.getSessionID();
        if (senderUUID != null) {
            sQLiteStatement.bindString(15, senderUUID.toString());
        }
        senderUUID = chatMessage.getItemID();
        if (senderUUID != null) {
            sQLiteStatement.bindString(16, senderUUID.toString());
        }
        senderName = chatMessage.getItemName();
        if (senderName != null) {
            sQLiteStatement.bindString(17, senderName);
        }
        senderType = chatMessage.getAssetType();
        if (senderType != null) {
            sQLiteStatement.bindLong(18, (long) senderType.intValue());
        }
        senderType = chatMessage.getTransactionAmount();
        if (senderType != null) {
            sQLiteStatement.bindLong(19, (long) senderType.intValue());
        }
        senderType = chatMessage.getNewBalance();
        if (senderType != null) {
            sQLiteStatement.bindLong(20, (long) senderType.intValue());
        }
        senderType = chatMessage.getChatChannel();
        if (senderType != null) {
            sQLiteStatement.bindLong(21, (long) senderType.intValue());
        }
        isOffline = chatMessage.getDialogIgnored();
        if (isOffline != null) {
            sQLiteStatement.bindLong(22, isOffline.booleanValue() ? 1 : 0);
        }
        isOffline = chatMessage.getAccepted();
        if (isOffline != null) {
            sQLiteStatement.bindLong(23, isOffline.booleanValue() ? 1 : 0);
        }
        senderUUID = chatMessage.getUserID();
        if (senderUUID != null) {
            sQLiteStatement.bindString(24, senderUUID.toString());
        }
        senderName = chatMessage.getObjectName();
        if (senderName != null) {
            sQLiteStatement.bindString(25, senderName);
        }
        senderType = chatMessage.getQuestionMask();
        if (senderType != null) {
            sQLiteStatement.bindLong(26, (long) senderType.intValue());
        }
        byte[] dialogButtons = chatMessage.getDialogButtons();
        if (dialogButtons != null) {
            sQLiteStatement.bindBlob(27, dialogButtons);
        }
        senderName = chatMessage.getDialogSelectedOption();
        if (senderName != null) {
            sQLiteStatement.bindString(28, senderName);
        }
        senderType = chatMessage.getTextBoxButtonIndex();
        if (senderType != null) {
            sQLiteStatement.bindLong(29, (long) senderType.intValue());
        }
        if (!chatMessage.getSyncedToGoogleDrive()) {
            j = 0;
        }
        sQLiteStatement.bindLong(30, j);
    }

    public Long getKey(ChatMessage chatMessage) {
        return chatMessage != null ? chatMessage.getId() : null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public ChatMessage readEntity(Cursor cursor, int i) {
        Boolean bool;
        Boolean bool2;
        Boolean bool3;
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        long j = cursor.getLong(i + 1);
        Date date = new Date(cursor.getLong(i + 2));
        int i2 = cursor.getInt(i + 3);
        Date date2 = cursor.isNull(i + 4) ? null : new Date(cursor.getLong(i + 4));
        if (cursor.isNull(i + 5)) {
            bool = null;
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 5) != (short) 0);
        }
        UUID fromString = cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6));
        Integer valueOf2 = cursor.isNull(i + 7) ? null : Integer.valueOf(cursor.getInt(i + 7));
        String string = cursor.isNull(i + 8) ? null : cursor.getString(i + 8);
        String string2 = cursor.isNull(i + 9) ? null : cursor.getString(i + 9);
        String string3 = cursor.isNull(i + 10) ? null : cursor.getString(i + 10);
        int i3 = cursor.getInt(i + 11);
        Integer valueOf3 = cursor.isNull(i + 12) ? null : Integer.valueOf(cursor.getInt(i + 12));
        Integer valueOf4 = cursor.isNull(i + 13) ? null : Integer.valueOf(cursor.getInt(i + 13));
        UUID fromString2 = cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14));
        UUID fromString3 = cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15));
        String string4 = cursor.isNull(i + 16) ? null : cursor.getString(i + 16);
        Integer valueOf5 = cursor.isNull(i + 17) ? null : Integer.valueOf(cursor.getInt(i + 17));
        Integer valueOf6 = cursor.isNull(i + 18) ? null : Integer.valueOf(cursor.getInt(i + 18));
        Integer valueOf7 = cursor.isNull(i + 19) ? null : Integer.valueOf(cursor.getInt(i + 19));
        Integer valueOf8 = cursor.isNull(i + 20) ? null : Integer.valueOf(cursor.getInt(i + 20));
        if (cursor.isNull(i + 21)) {
            bool2 = null;
        } else {
            bool2 = Boolean.valueOf(cursor.getShort(i + 21) != (short) 0);
        }
        if (cursor.isNull(i + 22)) {
            bool3 = null;
        } else {
            bool3 = Boolean.valueOf(cursor.getShort(i + 22) != (short) 0);
        }
        return new ChatMessage(valueOf, j, date, i2, date2, bool, fromString, valueOf2, string, string2, string3, i3, valueOf3, valueOf4, fromString2, fromString3, string4, valueOf5, valueOf6, valueOf7, valueOf8, bool2, bool3, cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)), cursor.isNull(i + 24) ? null : cursor.getString(i + 24), cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)), cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26), cursor.isNull(i + 27) ? null : cursor.getString(i + 27), cursor.isNull(i + 28) ? null : Integer.valueOf(cursor.getInt(i + 28)), cursor.getShort(i + 29) != (short) 0);
    }

    public void readEntity(Cursor cursor, ChatMessage chatMessage, int i) {
        Boolean bool;
        boolean z = true;
        Integer num = null;
        chatMessage.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        chatMessage.setChatterID(cursor.getLong(i + 1));
        chatMessage.setTimestamp(new Date(cursor.getLong(i + 2)));
        chatMessage.setViewType(cursor.getInt(i + 3));
        chatMessage.setOrigTimestamp(cursor.isNull(i + 4) ? null : new Date(cursor.getLong(i + 4)));
        if (cursor.isNull(i + 5)) {
            bool = null;
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 5) != (short) 0);
        }
        chatMessage.setIsOffline(bool);
        chatMessage.setSenderUUID(cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6)));
        chatMessage.setSenderType(cursor.isNull(i + 7) ? null : Integer.valueOf(cursor.getInt(i + 7)));
        chatMessage.setSenderName(cursor.isNull(i + 8) ? null : cursor.getString(i + 8));
        chatMessage.setSenderLegacyName(cursor.isNull(i + 9) ? null : cursor.getString(i + 9));
        chatMessage.setMessageText(cursor.isNull(i + 10) ? null : cursor.getString(i + 10));
        chatMessage.setMessageType(cursor.getInt(i + 11));
        chatMessage.setEventState(cursor.isNull(i + 12) ? null : Integer.valueOf(cursor.getInt(i + 12)));
        chatMessage.setOrigIMType(cursor.isNull(i + 13) ? null : Integer.valueOf(cursor.getInt(i + 13)));
        chatMessage.setSessionID(cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14)));
        chatMessage.setItemID(cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15)));
        chatMessage.setItemName(cursor.isNull(i + 16) ? null : cursor.getString(i + 16));
        chatMessage.setAssetType(cursor.isNull(i + 17) ? null : Integer.valueOf(cursor.getInt(i + 17)));
        chatMessage.setTransactionAmount(cursor.isNull(i + 18) ? null : Integer.valueOf(cursor.getInt(i + 18)));
        chatMessage.setNewBalance(cursor.isNull(i + 19) ? null : Integer.valueOf(cursor.getInt(i + 19)));
        chatMessage.setChatChannel(cursor.isNull(i + 20) ? null : Integer.valueOf(cursor.getInt(i + 20)));
        if (cursor.isNull(i + 21)) {
            bool = null;
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 21) != (short) 0);
        }
        chatMessage.setDialogIgnored(bool);
        if (cursor.isNull(i + 22)) {
            bool = null;
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 22) != (short) 0);
        }
        chatMessage.setAccepted(bool);
        chatMessage.setUserID(cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)));
        chatMessage.setObjectName(cursor.isNull(i + 24) ? null : cursor.getString(i + 24));
        chatMessage.setQuestionMask(cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)));
        chatMessage.setDialogButtons(cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26));
        chatMessage.setDialogSelectedOption(cursor.isNull(i + 27) ? null : cursor.getString(i + 27));
        if (!cursor.isNull(i + 28)) {
            num = Integer.valueOf(cursor.getInt(i + 28));
        }
        chatMessage.setTextBoxButtonIndex(num);
        if (cursor.getShort(i + 29) == (short) 0) {
            z = false;
        }
        chatMessage.setSyncedToGoogleDrive(z);
    }

    public Long readKey(Cursor cursor, int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
    }

    protected Long updateKeyAfterInsert(ChatMessage chatMessage, long j) {
        chatMessage.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
