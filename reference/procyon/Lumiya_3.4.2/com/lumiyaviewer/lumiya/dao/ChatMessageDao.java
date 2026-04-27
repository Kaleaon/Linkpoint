// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import android.database.Cursor;
import java.util.UUID;
import java.util.Date;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class ChatMessageDao extends AbstractDao<ChatMessage, Long>
{
    public static final String TABLENAME = "CHAT_MESSAGE";
    
    public ChatMessageDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public ChatMessageDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String str;
        if (b) {
            str = "IF NOT EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'CHAT_MESSAGE' (" + "'_id' INTEGER PRIMARY KEY ," + "'CHATTER_ID' INTEGER NOT NULL ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'VIEW_TYPE' INTEGER NOT NULL ," + "'ORIG_TIMESTAMP' INTEGER," + "'IS_OFFLINE' INTEGER," + "'SENDER_UUID' TEXT," + "'SENDER_TYPE' INTEGER," + "'SENDER_NAME' TEXT," + "'SENDER_LEGACY_NAME' TEXT," + "'MESSAGE_TEXT' TEXT," + "'MESSAGE_TYPE' INTEGER NOT NULL ," + "'EVENT_STATE' INTEGER," + "'ORIG_IMTYPE' INTEGER," + "'SESSION_ID' TEXT," + "'ITEM_ID' TEXT," + "'ITEM_NAME' TEXT," + "'ASSET_TYPE' INTEGER," + "'TRANSACTION_AMOUNT' INTEGER," + "'NEW_BALANCE' INTEGER," + "'CHAT_CHANNEL' INTEGER," + "'DIALOG_IGNORED' INTEGER," + "'ACCEPTED' INTEGER," + "'USER_ID' TEXT," + "'OBJECT_NAME' TEXT," + "'QUESTION_MASK' INTEGER," + "'DIALOG_BUTTONS' BLOB," + "'DIALOG_SELECTED_OPTION' TEXT," + "'TEXT_BOX_BUTTON_INDEX' INTEGER," + "'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );");
        sqLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE" + " (CHATTER_ID);");
        sqLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE" + " (_id,SYNCED_TO_GOOGLE_DRIVE);");
    }
    
    public static void dropTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        final StringBuilder append = new StringBuilder().append("DROP TABLE ");
        String str;
        if (b) {
            str = "IF EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL(append.append(str).append("'CHAT_MESSAGE'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final ChatMessage chatMessage) {
        final long n = 1L;
        sqLiteStatement.clearBindings();
        final Long id = chatMessage.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        sqLiteStatement.bindLong(2, chatMessage.getChatterID());
        sqLiteStatement.bindLong(3, chatMessage.getTimestamp().getTime());
        sqLiteStatement.bindLong(4, (long)chatMessage.getViewType());
        final Date origTimestamp = chatMessage.getOrigTimestamp();
        if (origTimestamp != null) {
            sqLiteStatement.bindLong(5, origTimestamp.getTime());
        }
        final Boolean isOffline = chatMessage.getIsOffline();
        if (isOffline != null) {
            long n2;
            if (isOffline) {
                n2 = 1L;
            }
            else {
                n2 = 0L;
            }
            sqLiteStatement.bindLong(6, n2);
        }
        final UUID senderUUID = chatMessage.getSenderUUID();
        if (senderUUID != null) {
            sqLiteStatement.bindString(7, senderUUID.toString());
        }
        final Integer senderType = chatMessage.getSenderType();
        if (senderType != null) {
            sqLiteStatement.bindLong(8, (long)senderType);
        }
        final String senderName = chatMessage.getSenderName();
        if (senderName != null) {
            sqLiteStatement.bindString(9, senderName);
        }
        final String senderLegacyName = chatMessage.getSenderLegacyName();
        if (senderLegacyName != null) {
            sqLiteStatement.bindString(10, senderLegacyName);
        }
        final String messageText = chatMessage.getMessageText();
        if (messageText != null) {
            sqLiteStatement.bindString(11, messageText);
        }
        sqLiteStatement.bindLong(12, (long)chatMessage.getMessageType());
        final Integer eventState = chatMessage.getEventState();
        if (eventState != null) {
            sqLiteStatement.bindLong(13, (long)eventState);
        }
        final Integer origIMType = chatMessage.getOrigIMType();
        if (origIMType != null) {
            sqLiteStatement.bindLong(14, (long)origIMType);
        }
        final UUID sessionID = chatMessage.getSessionID();
        if (sessionID != null) {
            sqLiteStatement.bindString(15, sessionID.toString());
        }
        final UUID itemID = chatMessage.getItemID();
        if (itemID != null) {
            sqLiteStatement.bindString(16, itemID.toString());
        }
        final String itemName = chatMessage.getItemName();
        if (itemName != null) {
            sqLiteStatement.bindString(17, itemName);
        }
        final Integer assetType = chatMessage.getAssetType();
        if (assetType != null) {
            sqLiteStatement.bindLong(18, (long)assetType);
        }
        final Integer transactionAmount = chatMessage.getTransactionAmount();
        if (transactionAmount != null) {
            sqLiteStatement.bindLong(19, (long)transactionAmount);
        }
        final Integer newBalance = chatMessage.getNewBalance();
        if (newBalance != null) {
            sqLiteStatement.bindLong(20, (long)newBalance);
        }
        final Integer chatChannel = chatMessage.getChatChannel();
        if (chatChannel != null) {
            sqLiteStatement.bindLong(21, (long)chatChannel);
        }
        final Boolean dialogIgnored = chatMessage.getDialogIgnored();
        if (dialogIgnored != null) {
            long n3;
            if (dialogIgnored) {
                n3 = 1L;
            }
            else {
                n3 = 0L;
            }
            sqLiteStatement.bindLong(22, n3);
        }
        final Boolean accepted = chatMessage.getAccepted();
        if (accepted != null) {
            long n4;
            if (accepted) {
                n4 = 1L;
            }
            else {
                n4 = 0L;
            }
            sqLiteStatement.bindLong(23, n4);
        }
        final UUID userID = chatMessage.getUserID();
        if (userID != null) {
            sqLiteStatement.bindString(24, userID.toString());
        }
        final String objectName = chatMessage.getObjectName();
        if (objectName != null) {
            sqLiteStatement.bindString(25, objectName);
        }
        final Integer questionMask = chatMessage.getQuestionMask();
        if (questionMask != null) {
            sqLiteStatement.bindLong(26, (long)questionMask);
        }
        final byte[] dialogButtons = chatMessage.getDialogButtons();
        if (dialogButtons != null) {
            sqLiteStatement.bindBlob(27, dialogButtons);
        }
        final String dialogSelectedOption = chatMessage.getDialogSelectedOption();
        if (dialogSelectedOption != null) {
            sqLiteStatement.bindString(28, dialogSelectedOption);
        }
        final Integer textBoxButtonIndex = chatMessage.getTextBoxButtonIndex();
        if (textBoxButtonIndex != null) {
            sqLiteStatement.bindLong(29, (long)textBoxButtonIndex);
        }
        long n5;
        if (chatMessage.getSyncedToGoogleDrive()) {
            n5 = n;
        }
        else {
            n5 = 0L;
        }
        sqLiteStatement.bindLong(30, n5);
    }
    
    public Long getKey(final ChatMessage chatMessage) {
        if (chatMessage != null) {
            return chatMessage.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public ChatMessage readEntity(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        final long long1 = cursor.getLong(n + 1);
        final Date date = new Date(cursor.getLong(n + 2));
        final int int1 = cursor.getInt(n + 3);
        Date date2;
        if (cursor.isNull(n + 4)) {
            date2 = null;
        }
        else {
            date2 = new Date(cursor.getLong(n + 4));
        }
        Boolean value2;
        if (cursor.isNull(n + 5)) {
            value2 = null;
        }
        else {
            value2 = (cursor.getShort(n + 5) != 0);
        }
        UUID fromString;
        if (cursor.isNull(n + 6)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 6));
        }
        Integer value3;
        if (cursor.isNull(n + 7)) {
            value3 = null;
        }
        else {
            value3 = cursor.getInt(n + 7);
        }
        String string;
        if (cursor.isNull(n + 8)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 8);
        }
        String string2;
        if (cursor.isNull(n + 9)) {
            string2 = null;
        }
        else {
            string2 = cursor.getString(n + 9);
        }
        String string3;
        if (cursor.isNull(n + 10)) {
            string3 = null;
        }
        else {
            string3 = cursor.getString(n + 10);
        }
        final int int2 = cursor.getInt(n + 11);
        Integer value4;
        if (cursor.isNull(n + 12)) {
            value4 = null;
        }
        else {
            value4 = cursor.getInt(n + 12);
        }
        Integer value5;
        if (cursor.isNull(n + 13)) {
            value5 = null;
        }
        else {
            value5 = cursor.getInt(n + 13);
        }
        UUID fromString2;
        if (cursor.isNull(n + 14)) {
            fromString2 = null;
        }
        else {
            fromString2 = UUID.fromString(cursor.getString(n + 14));
        }
        UUID fromString3;
        if (cursor.isNull(n + 15)) {
            fromString3 = null;
        }
        else {
            fromString3 = UUID.fromString(cursor.getString(n + 15));
        }
        String string4;
        if (cursor.isNull(n + 16)) {
            string4 = null;
        }
        else {
            string4 = cursor.getString(n + 16);
        }
        Integer value6;
        if (cursor.isNull(n + 17)) {
            value6 = null;
        }
        else {
            value6 = cursor.getInt(n + 17);
        }
        Integer value7;
        if (cursor.isNull(n + 18)) {
            value7 = null;
        }
        else {
            value7 = cursor.getInt(n + 18);
        }
        Integer value8;
        if (cursor.isNull(n + 19)) {
            value8 = null;
        }
        else {
            value8 = cursor.getInt(n + 19);
        }
        Integer value9;
        if (cursor.isNull(n + 20)) {
            value9 = null;
        }
        else {
            value9 = cursor.getInt(n + 20);
        }
        Boolean value10;
        if (cursor.isNull(n + 21)) {
            value10 = null;
        }
        else {
            value10 = (cursor.getShort(n + 21) != 0);
        }
        Boolean value11;
        if (cursor.isNull(n + 22)) {
            value11 = null;
        }
        else {
            value11 = (cursor.getShort(n + 22) != 0);
        }
        UUID fromString4;
        if (cursor.isNull(n + 23)) {
            fromString4 = null;
        }
        else {
            fromString4 = UUID.fromString(cursor.getString(n + 23));
        }
        String string5;
        if (cursor.isNull(n + 24)) {
            string5 = null;
        }
        else {
            string5 = cursor.getString(n + 24);
        }
        Integer value12;
        if (cursor.isNull(n + 25)) {
            value12 = null;
        }
        else {
            value12 = cursor.getInt(n + 25);
        }
        byte[] blob;
        if (cursor.isNull(n + 26)) {
            blob = null;
        }
        else {
            blob = cursor.getBlob(n + 26);
        }
        String string6;
        if (cursor.isNull(n + 27)) {
            string6 = null;
        }
        else {
            string6 = cursor.getString(n + 27);
        }
        Integer value13;
        if (cursor.isNull(n + 28)) {
            value13 = null;
        }
        else {
            value13 = cursor.getInt(n + 28);
        }
        return new ChatMessage(value, long1, date, int1, date2, value2, fromString, value3, string, string2, string3, int2, value4, value5, fromString2, fromString3, string4, value6, value7, value8, value9, value10, value11, fromString4, string5, value12, blob, string6, value13, cursor.getShort(n + 29) != 0);
    }
    
    public void readEntity(final Cursor cursor, final ChatMessage chatMessage, final int n) {
        final boolean b = true;
        final Integer n2 = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        chatMessage.setId(value);
        chatMessage.setChatterID(cursor.getLong(n + 1));
        chatMessage.setTimestamp(new Date(cursor.getLong(n + 2)));
        chatMessage.setViewType(cursor.getInt(n + 3));
        Date origTimestamp;
        if (cursor.isNull(n + 4)) {
            origTimestamp = null;
        }
        else {
            origTimestamp = new Date(cursor.getLong(n + 4));
        }
        chatMessage.setOrigTimestamp(origTimestamp);
        Boolean value2;
        if (cursor.isNull(n + 5)) {
            value2 = null;
        }
        else {
            value2 = (cursor.getShort(n + 5) != 0);
        }
        chatMessage.setIsOffline(value2);
        UUID fromString;
        if (cursor.isNull(n + 6)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 6));
        }
        chatMessage.setSenderUUID(fromString);
        Integer value3;
        if (cursor.isNull(n + 7)) {
            value3 = null;
        }
        else {
            value3 = cursor.getInt(n + 7);
        }
        chatMessage.setSenderType(value3);
        String string;
        if (cursor.isNull(n + 8)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 8);
        }
        chatMessage.setSenderName(string);
        String string2;
        if (cursor.isNull(n + 9)) {
            string2 = null;
        }
        else {
            string2 = cursor.getString(n + 9);
        }
        chatMessage.setSenderLegacyName(string2);
        String string3;
        if (cursor.isNull(n + 10)) {
            string3 = null;
        }
        else {
            string3 = cursor.getString(n + 10);
        }
        chatMessage.setMessageText(string3);
        chatMessage.setMessageType(cursor.getInt(n + 11));
        Integer value4;
        if (cursor.isNull(n + 12)) {
            value4 = null;
        }
        else {
            value4 = cursor.getInt(n + 12);
        }
        chatMessage.setEventState(value4);
        Integer value5;
        if (cursor.isNull(n + 13)) {
            value5 = null;
        }
        else {
            value5 = cursor.getInt(n + 13);
        }
        chatMessage.setOrigIMType(value5);
        UUID fromString2;
        if (cursor.isNull(n + 14)) {
            fromString2 = null;
        }
        else {
            fromString2 = UUID.fromString(cursor.getString(n + 14));
        }
        chatMessage.setSessionID(fromString2);
        UUID fromString3;
        if (cursor.isNull(n + 15)) {
            fromString3 = null;
        }
        else {
            fromString3 = UUID.fromString(cursor.getString(n + 15));
        }
        chatMessage.setItemID(fromString3);
        String string4;
        if (cursor.isNull(n + 16)) {
            string4 = null;
        }
        else {
            string4 = cursor.getString(n + 16);
        }
        chatMessage.setItemName(string4);
        Integer value6;
        if (cursor.isNull(n + 17)) {
            value6 = null;
        }
        else {
            value6 = cursor.getInt(n + 17);
        }
        chatMessage.setAssetType(value6);
        Integer value7;
        if (cursor.isNull(n + 18)) {
            value7 = null;
        }
        else {
            value7 = cursor.getInt(n + 18);
        }
        chatMessage.setTransactionAmount(value7);
        Integer value8;
        if (cursor.isNull(n + 19)) {
            value8 = null;
        }
        else {
            value8 = cursor.getInt(n + 19);
        }
        chatMessage.setNewBalance(value8);
        Integer value9;
        if (cursor.isNull(n + 20)) {
            value9 = null;
        }
        else {
            value9 = cursor.getInt(n + 20);
        }
        chatMessage.setChatChannel(value9);
        Boolean value10;
        if (cursor.isNull(n + 21)) {
            value10 = null;
        }
        else {
            value10 = (cursor.getShort(n + 21) != 0);
        }
        chatMessage.setDialogIgnored(value10);
        Boolean value11;
        if (cursor.isNull(n + 22)) {
            value11 = null;
        }
        else {
            value11 = (cursor.getShort(n + 22) != 0);
        }
        chatMessage.setAccepted(value11);
        UUID fromString4;
        if (cursor.isNull(n + 23)) {
            fromString4 = null;
        }
        else {
            fromString4 = UUID.fromString(cursor.getString(n + 23));
        }
        chatMessage.setUserID(fromString4);
        String string5;
        if (cursor.isNull(n + 24)) {
            string5 = null;
        }
        else {
            string5 = cursor.getString(n + 24);
        }
        chatMessage.setObjectName(string5);
        Integer value12;
        if (cursor.isNull(n + 25)) {
            value12 = null;
        }
        else {
            value12 = cursor.getInt(n + 25);
        }
        chatMessage.setQuestionMask(value12);
        byte[] blob;
        if (cursor.isNull(n + 26)) {
            blob = null;
        }
        else {
            blob = cursor.getBlob(n + 26);
        }
        chatMessage.setDialogButtons(blob);
        String string6;
        if (cursor.isNull(n + 27)) {
            string6 = null;
        }
        else {
            string6 = cursor.getString(n + 27);
        }
        chatMessage.setDialogSelectedOption(string6);
        Integer value13;
        if (cursor.isNull(n + 28)) {
            value13 = n2;
        }
        else {
            value13 = cursor.getInt(n + 28);
        }
        chatMessage.setTextBoxButtonIndex(value13);
        chatMessage.setSyncedToGoogleDrive(cursor.getShort(n + 29) != 0 && b);
    }
    
    public Long readKey(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return value;
    }
    
    @Override
    protected Long updateKeyAfterInsert(final ChatMessage chatMessage, final long n) {
        chatMessage.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property Accepted;
        public static final Property AssetType;
        public static final Property ChatChannel;
        public static final Property ChatterID;
        public static final Property DialogButtons;
        public static final Property DialogIgnored;
        public static final Property DialogSelectedOption;
        public static final Property EventState;
        public static final Property Id;
        public static final Property IsOffline;
        public static final Property ItemID;
        public static final Property ItemName;
        public static final Property MessageText;
        public static final Property MessageType;
        public static final Property NewBalance;
        public static final Property ObjectName;
        public static final Property OrigIMType;
        public static final Property OrigTimestamp;
        public static final Property QuestionMask;
        public static final Property SenderLegacyName;
        public static final Property SenderName;
        public static final Property SenderType;
        public static final Property SenderUUID;
        public static final Property SessionID;
        public static final Property SyncedToGoogleDrive;
        public static final Property TextBoxButtonIndex;
        public static final Property Timestamp;
        public static final Property TransactionAmount;
        public static final Property UserID;
        public static final Property ViewType;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            ChatterID = new Property(1, Long.TYPE, "chatterID", false, "CHATTER_ID");
            Timestamp = new Property(2, Date.class, "timestamp", false, "TIMESTAMP");
            ViewType = new Property(3, Integer.TYPE, "viewType", false, "VIEW_TYPE");
            OrigTimestamp = new Property(4, Date.class, "origTimestamp", false, "ORIG_TIMESTAMP");
            IsOffline = new Property(5, Boolean.class, "isOffline", false, "IS_OFFLINE");
            SenderUUID = new Property(6, UUID.class, "senderUUID", false, "SENDER_UUID");
            SenderType = new Property(7, Integer.class, "senderType", false, "SENDER_TYPE");
            SenderName = new Property(8, String.class, "senderName", false, "SENDER_NAME");
            SenderLegacyName = new Property(9, String.class, "senderLegacyName", false, "SENDER_LEGACY_NAME");
            MessageText = new Property(10, String.class, "messageText", false, "MESSAGE_TEXT");
            MessageType = new Property(11, Integer.TYPE, "messageType", false, "MESSAGE_TYPE");
            EventState = new Property(12, Integer.class, "eventState", false, "EVENT_STATE");
            OrigIMType = new Property(13, Integer.class, "origIMType", false, "ORIG_IMTYPE");
            SessionID = new Property(14, UUID.class, "sessionID", false, "SESSION_ID");
            ItemID = new Property(15, UUID.class, "itemID", false, "ITEM_ID");
            ItemName = new Property(16, String.class, "itemName", false, "ITEM_NAME");
            AssetType = new Property(17, Integer.class, "assetType", false, "ASSET_TYPE");
            TransactionAmount = new Property(18, Integer.class, "transactionAmount", false, "TRANSACTION_AMOUNT");
            NewBalance = new Property(19, Integer.class, "newBalance", false, "NEW_BALANCE");
            ChatChannel = new Property(20, Integer.class, "chatChannel", false, "CHAT_CHANNEL");
            DialogIgnored = new Property(21, Boolean.class, "dialogIgnored", false, "DIALOG_IGNORED");
            Accepted = new Property(22, Boolean.class, "accepted", false, "ACCEPTED");
            UserID = new Property(23, UUID.class, "userID", false, "USER_ID");
            ObjectName = new Property(24, String.class, "objectName", false, "OBJECT_NAME");
            QuestionMask = new Property(25, Integer.class, "questionMask", false, "QUESTION_MASK");
            DialogButtons = new Property(26, byte[].class, "dialogButtons", false, "DIALOG_BUTTONS");
            DialogSelectedOption = new Property(27, String.class, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION");
            TextBoxButtonIndex = new Property(28, Integer.class, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX");
            SyncedToGoogleDrive = new Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE");
        }
    }
}
