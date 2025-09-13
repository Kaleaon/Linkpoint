package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.orm.DBHandle;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.util.UUID;
import javax.annotation.Nullable;

public class SLInventoryEntry extends InventoryEntryDBObject implements Parcelable {
    public static final Parcelable.Creator<SLInventoryEntry> CREATOR = new Parcelable.Creator<SLInventoryEntry>() {
        public SLInventoryEntry createFromParcel(Parcel parcel) {
            return new SLInventoryEntry(parcel, (SLInventoryEntry) null);
        }

        public SLInventoryEntry[] newArray(int i) {
            return new SLInventoryEntry[i];
        }
    };
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    public static final int FT_ANIMATION = 20;
    public static final int FT_BASIC_ROOT = 52;
    public static final int FT_BODYPART = 13;
    public static final int FT_CALLINGCARD = 2;
    public static final int FT_CLOTHING = 5;
    public static final int FT_CURRENT_OUTFIT = 46;
    public static final int FT_ENSEMBLE_END = 45;
    public static final int FT_ENSEMBLE_START = 26;
    public static final int FT_FAVORITE = 23;
    public static final int FT_GESTURE = 21;
    public static final int FT_INBOX = 50;
    public static final int FT_LANDMARK = 3;
    public static final int FT_LOST_AND_FOUND = 16;
    public static final int FT_LSL_TEXT = 10;
    public static final int FT_MESH = 49;
    public static final int FT_MY_OUTFITS = 48;
    public static final int FT_NOTECARD = 7;
    public static final int FT_OBJECT = 6;
    public static final int FT_OUTBOX = 51;
    public static final int FT_OUTFIT = 47;
    public static final int FT_ROOT_INVENTORY = 8;
    public static final int FT_SNAPSHOT_CATEGORY = 15;
    public static final int FT_SOUND = 1;
    public static final int FT_TEXTURE = 0;
    public static final int FT_TRASH = 14;
    public static final int II_FLAGS_WEARABLES_MASK = 255;
    public static final int IT_ANIMATION = 19;
    public static final int IT_ATTACHMENT = 17;
    public static final int IT_BODYPART = 13;
    public static final int IT_CALLINGCARD = 2;
    public static final int IT_CATEGORY = 8;
    public static final int IT_CLOTHING = 5;
    public static final int IT_COUNT = 21;
    public static final int IT_GESTURE = 20;
    public static final int IT_LANDMARK = 3;
    public static final int IT_LOST_AND_FOUND = 16;
    public static final int IT_LSL = 10;
    public static final int IT_LSL_BYTECODE = 11;
    public static final int IT_NOTECARD = 7;
    public static final int IT_OBJECT = 6;
    public static final int IT_ROOT_CATEGORY = 9;
    public static final int IT_SCRIPT = 4;
    public static final int IT_SNAPSHOT = 15;
    public static final int IT_SOUND = 1;
    public static final int IT_TEXTURE = 0;
    public static final int IT_TEXTURE_TGA = 12;
    public static final int IT_TRASH = 14;
    public static final int IT_WEARABLE = 18;
    public static final int PERM_COPY = 32768;
    public static final int PERM_FULL = Integer.MAX_VALUE;
    public static final int PERM_MODIFY = 16384;
    public static final int PERM_TRANSFER = 8192;

    public SLInventoryEntry() {
    }

    public SLInventoryEntry(Cursor cursor) {
        super(cursor);
    }

    public SLInventoryEntry(SQLiteDatabase sQLiteDatabase, long j) throws DBObject.DatabaseBindingException {
        super(sQLiteDatabase, j);
    }

    private SLInventoryEntry(Parcel parcel) {
        super(parcel);
    }

    /* synthetic */ SLInventoryEntry(Parcel parcel, SLInventoryEntry sLInventoryEntry) {
        this(parcel);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLInventoryEntry(DBHandle dBHandle, long j) throws DBObject.DatabaseBindingException {
        super(dBHandle != null ? dBHandle.getDB() : null, j);
    }

    public static SLInventoryEntry find(SQLiteDatabase sQLiteDatabase, UUID uuid) {
        try {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, fieldNames, "uuid_low = ? AND uuid_high = ?", new String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null);
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = new SLInventoryEntry(query);
                query.close();
                return sLInventoryEntry;
            }
            query.close();
            return null;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SLInventoryEntry findOrCreate(SQLiteDatabase sQLiteDatabase, UUID uuid) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DBObject.DatabaseBindingException(SLInventoryEntry.class, "database is null");
        } else if (uuid == null) {
            throw new DBObject.DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null");
        } else {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, fieldNames, "uuid_low = ? AND uuid_high = ?", new String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null);
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = new SLInventoryEntry(query);
                query.close();
                return sLInventoryEntry;
            }
            query.close();
            SLInventoryEntry sLInventoryEntry2 = new SLInventoryEntry();
            sLInventoryEntry2.uuid = uuid;
            return sLInventoryEntry2;
        }
    }

    public static SLInventoryEntry findOrCreateForUpdate(SQLiteDatabase sQLiteDatabase, UUID uuid) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DBObject.DatabaseBindingException(SLInventoryEntry.class, "database is null");
        } else if (uuid == null) {
            throw new DBObject.DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null");
        } else {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, new String[]{"_id"}, "uuid_low = ? AND uuid_high = ?", new String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null);
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = new SLInventoryEntry();
                sLInventoryEntry._id = query.getLong(0);
                sLInventoryEntry.uuid = uuid;
                query.close();
                return sLInventoryEntry;
            }
            query.close();
            SLInventoryEntry sLInventoryEntry2 = new SLInventoryEntry();
            sLInventoryEntry2.uuid = uuid;
            return sLInventoryEntry2;
        }
    }

    private static int getDrawableResourceForType(int i) {
        switch (i) {
            case 0:
            case 12:
            case 15:
                return R.drawable.inv_image;
            case 1:
                return R.drawable.inv_sound;
            case 2:
                return R.drawable.inv_vcard;
            case 3:
                return R.drawable.inv_landmark;
            case 4:
            case 10:
            case 11:
                return R.drawable.inv_script;
            case 5:
            case 17:
            case 18:
                return R.drawable.inv_clothes;
            case 6:
                return R.drawable.inv_object;
            case 7:
                return R.drawable.inv_notecard;
            case 8:
            case 9:
                return R.drawable.inv_folder;
            case 13:
                return R.drawable.inv_human;
            case 14:
                return R.drawable.inv_trash;
            case 16:
                return R.drawable.inv_recycle;
            case 19:
                return R.drawable.inv_animation;
            case 20:
                return R.drawable.inv_smile;
            default:
                return -1;
        }
    }

    public static SQLiteStatement getInsertStatement(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DBObject.DatabaseBindingException("Database is closed");
        } else if (!sQLiteDatabase.isOpen()) {
            throw new DBObject.DatabaseBindingException("Database is closed");
        } else {
            try {
                return sQLiteDatabase.compileStatement(InventoryEntryDBObject.insertQuery);
            } catch (SQLiteException e) {
                DBObject.DatabaseBindingException databaseBindingException = new DBObject.DatabaseBindingException(e.getMessage());
                databaseBindingException.initCause(e);
                throw databaseBindingException;
            }
        }
    }

    public static SQLiteStatement getUpdateStatement(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DBObject.DatabaseBindingException("Database is closed");
        } else if (!sQLiteDatabase.isOpen()) {
            throw new DBObject.DatabaseBindingException("Database is closed");
        } else {
            try {
                return sQLiteDatabase.compileStatement("UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=? WHERE uuid_high = ? AND uuid_low = ?");
            } catch (SQLiteException e) {
                DBObject.DatabaseBindingException databaseBindingException = new DBObject.DatabaseBindingException(e.getMessage());
                databaseBindingException.initCause(e);
                throw databaseBindingException;
            }
        }
    }

    private static void parsePermissions(SimpleStringParser simpleStringParser, SLInventoryEntry sLInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", DELIM_EOL);
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY);
            if (!nextToken.equals("}")) {
                if (nextToken.equals("base_mask")) {
                    sLInventoryEntry.baseMask = simpleStringParser.getHexToken(DELIM_EOL);
                } else if (nextToken.equals("owner_mask")) {
                    sLInventoryEntry.ownerMask = simpleStringParser.getHexToken(DELIM_EOL);
                } else if (nextToken.equals("group_mask")) {
                    sLInventoryEntry.groupMask = simpleStringParser.getHexToken(DELIM_EOL);
                } else if (nextToken.equals("everyone_mask")) {
                    sLInventoryEntry.everyoneMask = simpleStringParser.getHexToken(DELIM_EOL);
                } else if (nextToken.equals("next_owner_mask")) {
                    sLInventoryEntry.nextOwnerMask = simpleStringParser.getHexToken(DELIM_EOL);
                } else if (nextToken.equals("creator_id")) {
                    sLInventoryEntry.creatorUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
                } else if (nextToken.equals("owner_id")) {
                    sLInventoryEntry.ownerUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
                } else if (nextToken.equals("last_owner_id")) {
                    sLInventoryEntry.lastOwnerUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
                } else if (nextToken.equals("group_id")) {
                    sLInventoryEntry.groupUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
                } else {
                    simpleStringParser.nextToken(DELIM_EOL);
                }
            } else {
                return;
            }
        }
    }

    private static void parseSaleInfo(SimpleStringParser simpleStringParser, SLInventoryEntry sLInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", DELIM_EOL);
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY);
            if (!nextToken.equals("}")) {
                if (nextToken.equals("sale_type")) {
                    sLInventoryEntry.saleType = SLSaleType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode();
                } else if (nextToken.equals("sale_price")) {
                    sLInventoryEntry.salePrice = simpleStringParser.getIntToken(DELIM_EOL);
                } else {
                    simpleStringParser.nextToken(DELIM_EOL);
                }
            } else {
                return;
            }
        }
    }

    public static SLInventoryEntry parseString(SimpleStringParser simpleStringParser) throws SimpleStringParser.StringParsingException {
        SLInventoryEntry sLInventoryEntry = new SLInventoryEntry();
        simpleStringParser.expectToken("{", DELIM_EOL);
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY);
            if (nextToken.equals("}")) {
                return sLInventoryEntry;
            }
            if (nextToken.equals("item_id")) {
                sLInventoryEntry.uuid = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
            } else if (nextToken.equals("parent_id")) {
                sLInventoryEntry.parentUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
            } else if (nextToken.equals("asset_id")) {
                sLInventoryEntry.assetUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL));
            } else if (nextToken.equals("type")) {
                sLInventoryEntry.assetType = SLAssetType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode();
            } else if (nextToken.equals("inv_type")) {
                sLInventoryEntry.invType = SLInventoryType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode();
            } else if (nextToken.equals("flags")) {
                sLInventoryEntry.flags = simpleStringParser.getHexToken(DELIM_EOL);
            } else if (nextToken.equals("name")) {
                sLInventoryEntry.name = simpleStringParser.getPipeTerminatedString(DELIM_EOL);
            } else if (nextToken.equals("desc")) {
                sLInventoryEntry.description = simpleStringParser.getPipeTerminatedString(DELIM_EOL);
            } else if (nextToken.equals("creation_date")) {
                sLInventoryEntry.creationDate = simpleStringParser.getIntToken(DELIM_EOL);
            } else if (nextToken.equals("permissions")) {
                simpleStringParser.nextToken(DELIM_EOL);
                parsePermissions(simpleStringParser, sLInventoryEntry);
            } else if (nextToken.equals("sale_info")) {
                simpleStringParser.nextToken(DELIM_EOL);
                parseSaleInfo(simpleStringParser, sLInventoryEntry);
            } else {
                simpleStringParser.nextToken(DELIM_EOL);
            }
        }
    }

    public static Cursor query(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, String str2) {
        if (sQLiteDatabase == null) {
            return null;
        }
        try {
            return InventoryEntryDBObject.query(sQLiteDatabase, str, strArr, str2);
        } catch (DBObject.DatabaseBindingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Cursor query(DBHandle dBHandle, String str, String[] strArr, String str2) {
        if (dBHandle == null) {
            return null;
        }
        try {
            return InventoryEntryDBObject.query(dBHandle, str, strArr, str2);
        } catch (DBObject.DatabaseBindingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getActionDescriptionResId() {
        SLAssetType byType = SLAssetType.getByType(this.assetType);
        if (byType != null) {
            return byType.getActionDescription();
        }
        switch (this.invType) {
            case 0:
            case 15:
                return this.assetType == SLAssetType.AT_TEXTURE.getTypeCode() ? R.string.asset_action_view : R.string.asset_action_rez;
            case 3:
                return R.string.asset_action_teleport;
            case 6:
                return R.string.asset_action_rez;
            case 7:
                return R.string.asset_action_edit;
            case 10:
                return R.string.asset_action_edit;
            default:
                return -1;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType.getByType(r1.assetType);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getDrawableResource() {
        /*
            r1 = this;
            boolean r0 = r1.isFolder
            if (r0 == 0) goto L_0x0008
            r0 = 2130837696(0x7f0200c0, float:1.7280353E38)
            return r0
        L_0x0008:
            boolean r0 = r1.isLink()
            if (r0 != 0) goto L_0x001b
            int r0 = r1.assetType
            com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType.getByType(r0)
            if (r0 == 0) goto L_0x001b
            int r0 = r0.getDrawableResource()
            return r0
        L_0x001b:
            int r0 = r1.invType
            int r0 = getDrawableResourceForType(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry.getDrawableResource():int");
    }

    public String getReadableTextForLink() {
        return SLInventoryType.getByType(this.invType).getReadableName() + ": " + this.name;
    }

    public int getSubtypeDrawableResource() {
        if (this.isFolder) {
            switch (this.typeDefault) {
                case 0:
                case 15:
                    return R.drawable.inv_image;
                case 1:
                    return R.drawable.inv_sound;
                case 2:
                    return R.drawable.inv_vcard;
                case 3:
                case 23:
                    return R.drawable.inv_landmark;
                case 5:
                case 46:
                case 47:
                case 48:
                    return R.drawable.inv_clothes;
                case 6:
                case 49:
                    return R.drawable.inv_object;
                case 7:
                    return R.drawable.inv_notecard;
                case 10:
                    return R.drawable.inv_script;
                case 13:
                    return R.drawable.inv_human;
                case 14:
                    return R.drawable.inv_trash;
                case 16:
                    return R.drawable.inv_recycle;
                case 20:
                    return R.drawable.inv_animation;
                case 21:
                    return R.drawable.inv_smile;
                default:
                    return -1;
            }
        } else if (this.assetType == SLAssetType.AT_LINK.getTypeCode() || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
            return R.drawable.inv_link;
        } else {
            return -1;
        }
    }

    public int getTypeDescriptionResId() {
        SLAssetType byType = SLAssetType.getByType(this.assetType);
        if (byType != null) {
            return byType.getTypeDescription();
        }
        switch (this.invType) {
            case 0:
                return R.string.asset_type_texture;
            case 1:
                return R.string.asset_type_sound;
            case 2:
                return R.string.asset_type_calling_card;
            case 3:
                return R.string.asset_type_landmark;
            case 4:
                return R.string.asset_type_script;
            case 5:
                return R.string.asset_type_clothing;
            case 6:
                return R.string.asset_type_object;
            case 7:
                return R.string.asset_type_notecard;
            case 8:
                return R.string.asset_type_folder;
            case 9:
                return R.string.asset_type_root_folder;
            case 10:
                return R.string.asset_type_lsl;
            case 11:
                return R.string.asset_type_lsl_bytecode;
            case 12:
                return R.string.asset_type_texture_tga;
            case 13:
                return R.string.asset_type_bodypart;
            case 14:
                return R.string.asset_type_trash;
            case 15:
                return R.string.asset_type_snapshot;
            case 16:
                return R.string.asset_type_lost_and_found;
            case 17:
                return R.string.asset_type_attachment;
            case 18:
                return R.string.asset_type_wearable;
            case 19:
                return R.string.asset_type_animation;
            case 20:
                return R.string.asset_type_gesture;
            default:
                return R.string.asset_type_unknown;
        }
    }

    public boolean isAnimation() {
        if (this.assetType == SLAssetType.AT_ANIMATION.getTypeCode()) {
            return true;
        }
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() && this.invType == 19;
    }

    public boolean isCopyable() {
        return ((this.ownerMask & this.baseMask) & 32768) != 0;
    }

    public boolean isFolderOrFolderLink() {
        return this.isFolder || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode();
    }

    public boolean isLink() {
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode();
    }

    public boolean isWearable() {
        if (this.assetType == SLAssetType.AT_BODYPART.getTypeCode() || this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            return true;
        }
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() && this.invType == SLInventoryType.IT_WEARABLE.getTypeCode();
    }

    public void updateOrInsert(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        super.updateOrInsert(sQLiteDatabase, "uuid_low = ? AND uuid_high = ?", new String[]{Long.toString(this.uuid.getLeastSignificantBits()), Long.toString(this.uuid.getMostSignificantBits())});
    }

    public void updateOrInsert(SQLiteStatement sQLiteStatement, SQLiteStatement sQLiteStatement2) throws DBObject.DatabaseBindingException {
        sQLiteStatement.bindLong(19, this.uuid.getMostSignificantBits());
        sQLiteStatement.bindLong(20, this.uuid.getLeastSignificantBits());
        super.updateOrInsert(sQLiteStatement, sQLiteStatement2);
    }

    public Object whatIsItemWornOn(@Nullable ImmutableMap<UUID, String> immutableMap, @Nullable Table<SLWearableType, UUID, SLWearable> table, boolean z) {
        if (this.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (this.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                if (table != null) {
                    for (Table.Cell cell : table.cellSet()) {
                        SLWearableType sLWearableType = (SLWearableType) cell.getRowKey();
                        UUID uuid = (UUID) cell.getColumnKey();
                        if (!(sLWearableType == null || uuid == null)) {
                            if (!z || sLWearableType.isBodyPart()) {
                                if (uuid.equals(this.assetUUID)) {
                                    return sLWearableType;
                                }
                                SLWearable sLWearable = (SLWearable) cell.getValue();
                                if (sLWearable != null && sLWearable.itemID.equals(this.assetUUID)) {
                                    return sLWearableType;
                                }
                            }
                        }
                    }
                }
            } else if (immutableMap != null && !z && immutableMap.containsKey(this.assetUUID)) {
                return Boolean.TRUE;
            }
        } else if (this.assetType == SLAssetType.AT_BODYPART.getTypeCode() || this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            SLWearableType byCode = SLWearableType.getByCode(this.flags & 255);
            if (byCode == null || table == null || ((z && (!byCode.isBodyPart())) || !table.contains(byCode, this.assetUUID))) {
                return null;
            }
            return byCode;
        } else if (immutableMap != null && !z && (immutableMap.containsKey(this.uuid) || immutableMap.containsKey(this.assetUUID))) {
            return Boolean.TRUE;
        }
        return null;
    }
}
