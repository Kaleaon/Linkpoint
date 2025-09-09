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

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, ChatMessage chatMessage) {
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
        String senderLegacyName = chatMessage.getSenderLegacyName();
        if (senderLegacyName != null) {
            sQLiteStatement.bindString(10, senderLegacyName);
        }
        String messageText = chatMessage.getMessageText();
        if (messageText != null) {
            sQLiteStatement.bindString(11, messageText);
        }
        sQLiteStatement.bindLong(12, (long) chatMessage.getMessageType());
        Integer eventState = chatMessage.getEventState();
        if (eventState != null) {
            sQLiteStatement.bindLong(13, (long) eventState.intValue());
        }
        Integer origIMType = chatMessage.getOrigIMType();
        if (origIMType != null) {
            sQLiteStatement.bindLong(14, (long) origIMType.intValue());
        }
        UUID sessionID = chatMessage.getSessionID();
        if (sessionID != null) {
            sQLiteStatement.bindString(15, sessionID.toString());
        }
        UUID itemID = chatMessage.getItemID();
        if (itemID != null) {
            sQLiteStatement.bindString(16, itemID.toString());
        }
        String itemName = chatMessage.getItemName();
        if (itemName != null) {
            sQLiteStatement.bindString(17, itemName);
        }
        Integer assetType = chatMessage.getAssetType();
        if (assetType != null) {
            sQLiteStatement.bindLong(18, (long) assetType.intValue());
        }
        Integer transactionAmount = chatMessage.getTransactionAmount();
        if (transactionAmount != null) {
            sQLiteStatement.bindLong(19, (long) transactionAmount.intValue());
        }
        Integer newBalance = chatMessage.getNewBalance();
        if (newBalance != null) {
            sQLiteStatement.bindLong(20, (long) newBalance.intValue());
        }
        Integer chatChannel = chatMessage.getChatChannel();
        if (chatChannel != null) {
            sQLiteStatement.bindLong(21, (long) chatChannel.intValue());
        }
        Boolean dialogIgnored = chatMessage.getDialogIgnored();
        if (dialogIgnored != null) {
            sQLiteStatement.bindLong(22, dialogIgnored.booleanValue() ? 1 : 0);
        }
        Boolean accepted = chatMessage.getAccepted();
        if (accepted != null) {
            sQLiteStatement.bindLong(23, accepted.booleanValue() ? 1 : 0);
        }
        UUID userID = chatMessage.getUserID();
        if (userID != null) {
            sQLiteStatement.bindString(24, userID.toString());
        }
        String objectName = chatMessage.getObjectName();
        if (objectName != null) {
            sQLiteStatement.bindString(25, objectName);
        }
        Integer questionMask = chatMessage.getQuestionMask();
        if (questionMask != null) {
            sQLiteStatement.bindLong(26, (long) questionMask.intValue());
        }
        byte[] dialogButtons = chatMessage.getDialogButtons();
        if (dialogButtons != null) {
            sQLiteStatement.bindBlob(27, dialogButtons);
        }
        String dialogSelectedOption = chatMessage.getDialogSelectedOption();
        if (dialogSelectedOption != null) {
            sQLiteStatement.bindString(28, dialogSelectedOption);
        }
        Integer textBoxButtonIndex = chatMessage.getTextBoxButtonIndex();
        if (textBoxButtonIndex != null) {
            sQLiteStatement.bindLong(29, (long) textBoxButtonIndex.intValue());
        }
        if (!chatMessage.getSyncedToGoogleDrive()) {
            j = 0;
        }
        sQLiteStatement.bindLong(30, j);
    }

    public Long getKey(ChatMessage chatMessage) {
        if (chatMessage != null) {
            return chatMessage.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public ChatMessage readEntity(Cursor cursor, int i) {
        Boolean valueOf;
        Boolean valueOf2;
        Boolean valueOf3;
        Long valueOf4 = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        long j = cursor.getLong(i + 1);
        Date date = new Date(cursor.getLong(i + 2));
        int i2 = cursor.getInt(i + 3);
        Date date2 = cursor.isNull(i + 4) ? null : new Date(cursor.getLong(i + 4));
        if (cursor.isNull(i + 5)) {
            valueOf = null;
        } else {
            valueOf = Boolean.valueOf(cursor.getShort(i + 5) != 0);
        }
        UUID fromString = cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6));
        Integer valueOf5 = cursor.isNull(i + 7) ? null : Integer.valueOf(cursor.getInt(i + 7));
        String string = cursor.isNull(i + 8) ? null : cursor.getString(i + 8);
        String string2 = cursor.isNull(i + 9) ? null : cursor.getString(i + 9);
        String string3 = cursor.isNull(i + 10) ? null : cursor.getString(i + 10);
        int i3 = cursor.getInt(i + 11);
        Integer valueOf6 = cursor.isNull(i + 12) ? null : Integer.valueOf(cursor.getInt(i + 12));
        Integer valueOf7 = cursor.isNull(i + 13) ? null : Integer.valueOf(cursor.getInt(i + 13));
        UUID fromString2 = cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14));
        UUID fromString3 = cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15));
        String string4 = cursor.isNull(i + 16) ? null : cursor.getString(i + 16);
        Integer valueOf8 = cursor.isNull(i + 17) ? null : Integer.valueOf(cursor.getInt(i + 17));
        Integer valueOf9 = cursor.isNull(i + 18) ? null : Integer.valueOf(cursor.getInt(i + 18));
        Integer valueOf10 = cursor.isNull(i + 19) ? null : Integer.valueOf(cursor.getInt(i + 19));
        Integer valueOf11 = cursor.isNull(i + 20) ? null : Integer.valueOf(cursor.getInt(i + 20));
        if (cursor.isNull(i + 21)) {
            valueOf2 = null;
        } else {
            valueOf2 = Boolean.valueOf(cursor.getShort(i + 21) != 0);
        }
        if (cursor.isNull(i + 22)) {
            valueOf3 = null;
        } else {
            valueOf3 = Boolean.valueOf(cursor.getShort(i + 22) != 0);
        }
        return new ChatMessage(valueOf4, j, date, i2, date2, valueOf, fromString, valueOf5, string, string2, string3, i3, valueOf6, valueOf7, fromString2, fromString3, string4, valueOf8, valueOf9, valueOf10, valueOf11, valueOf2, valueOf3, cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)), cursor.isNull(i + 24) ? null : cursor.getString(i + 24), cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)), cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26), cursor.isNull(i + 27) ? null : cursor.getString(i + 27), cursor.isNull(i + 28) ? null : Integer.valueOf(cursor.getInt(i + 28)), cursor.getShort(i + 29) != 0);
    }

    public void readEntity(Cursor cursor, ChatMessage chatMessage, int i) {
        Boolean valueOf;
        Boolean valueOf2;
        Boolean valueOf3;
        boolean z = true;
        Integer num = null;
        chatMessage.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        chatMessage.setChatterID(cursor.getLong(i + 1));
        chatMessage.setTimestamp(new Date(cursor.getLong(i + 2)));
        chatMessage.setViewType(cursor.getInt(i + 3));
        chatMessage.setOrigTimestamp(cursor.isNull(i + 4) ? null : new Date(cursor.getLong(i + 4)));
        if (cursor.isNull(i + 5)) {
            valueOf = null;
        } else {
            valueOf = Boolean.valueOf(cursor.getShort(i + 5) != 0);
        }
        chatMessage.setIsOffline(valueOf);
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
            valueOf2 = null;
        } else {
            valueOf2 = Boolean.valueOf(cursor.getShort(i + 21) != 0);
        }
        chatMessage.setDialogIgnored(valueOf2);
        if (cursor.isNull(i + 22)) {
            valueOf3 = null;
        } else {
            valueOf3 = Boolean.valueOf(cursor.getShort(i + 22) != 0);
        }
        chatMessage.setAccepted(valueOf3);
        chatMessage.setUserID(cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)));
        chatMessage.setObjectName(cursor.isNull(i + 24) ? null : cursor.getString(i + 24));
        chatMessage.setQuestionMask(cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)));
        chatMessage.setDialogButtons(cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26));
        chatMessage.setDialogSelectedOption(cursor.isNull(i + 27) ? null : cursor.getString(i + 27));
        if (!cursor.isNull(i + 28)) {
            num = Integer.valueOf(cursor.getInt(i + 28));
        }
        chatMessage.setTextBoxButtonIndex(num);
        if (cursor.getShort(i + 29) == 0) {
            z = false;
        }
        chatMessage.setSyncedToGoogleDrive(z);
    }

    public Long readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i + 0));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(ChatMessage chatMessage, long j) {
        chatMessage.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
