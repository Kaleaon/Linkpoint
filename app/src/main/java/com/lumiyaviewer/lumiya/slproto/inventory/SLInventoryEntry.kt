package com.lumiyaviewer.lumiya.slproto.inventory

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.os.Parcel
import android.os.Parcelable
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.orm.DBHandle
import com.lumiyaviewer.lumiya.orm.DBObject
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.utils.SimpleStringParser
import java.util.UUID
import javax.annotation.Nullable

class SLInventoryEntry : InventoryEntryDBObject : Parcelable {
    Parcelable.Creator<SLInventoryEntry> CREATOR = Parcelable.Creator<SLInventoryEntry>() {
        SLInventoryEntry createFromParcel(Parcel parcel) {
            return SLInventoryEntry(parcel, (SLInventoryEntry) null)
        }

        SLInventoryEntry[] newArray(Int i) {
            return SLInventoryEntry[i]
        }
    }
    private String DELIM_ANY = " \t\n"
    private String DELIM_EOL = "\n"
    Int FT_ANIMATION = 20
    Int FT_BASIC_ROOT = 52
    Int FT_BODYPART = 13
    Int FT_CALLINGCARD = 2
    Int FT_CLOTHING = 5
    Int FT_CURRENT_OUTFIT = 46
    Int FT_ENSEMBLE_END = 45
    Int FT_ENSEMBLE_START = 26
    Int FT_FAVORITE = 23
    Int FT_GESTURE = 21
    Int FT_INBOX = 50
    Int FT_LANDMARK = 3
    Int FT_LOST_AND_FOUND = 16
    Int FT_LSL_TEXT = 10
    Int FT_MESH = 49
    Int FT_MY_OUTFITS = 48
    Int FT_NOTECARD = 7
    Int FT_OBJECT = 6
    Int FT_OUTBOX = 51
    Int FT_OUTFIT = 47
    Int FT_ROOT_INVENTORY = 8
    Int FT_SNAPSHOT_CATEGORY = 15
    Int FT_SOUND = 1
    Int FT_TEXTURE = 0
    Int FT_TRASH = 14
    Int II_FLAGS_WEARABLES_MASK = 255
    Int IT_ANIMATION = 19
    Int IT_ATTACHMENT = 17
    Int IT_BODYPART = 13
    Int IT_CALLINGCARD = 2
    Int IT_CATEGORY = 8
    Int IT_CLOTHING = 5
    Int IT_COUNT = 21
    Int IT_GESTURE = 20
    Int IT_LANDMARK = 3
    Int IT_LOST_AND_FOUND = 16
    Int IT_LSL = 10
    Int IT_LSL_BYTECODE = 11
    Int IT_NOTECARD = 7
    Int IT_OBJECT = 6
    Int IT_ROOT_CATEGORY = 9
    Int IT_SCRIPT = 4
    Int IT_SNAPSHOT = 15
    Int IT_SOUND = 1
    Int IT_TEXTURE = 0
    Int IT_TEXTURE_TGA = 12
    Int IT_TRASH = 14
    Int IT_WEARABLE = 18
    Int PERM_COPY = 32768
    Int PERM_FULL = Integer.MAX_VALUE
    Int PERM_MODIFY = 16384
    Int PERM_TRANSFER = 8192

    SLInventoryEntry() {
    }

    SLInventoryEntry(Cursor cursor) {
        super(cursor)
    }

    SLInventoryEntry(SQLiteDatabase sQLiteDatabase, Long j) throws DBObject.DatabaseBindingException {
        super(sQLiteDatabase, j)
    }

    private SLInventoryEntry(Parcel parcel) {
        super(parcel)
    }

    /* synthetic */ SLInventoryEntry(Parcel parcel, SLInventoryEntry sLInventoryEntry) {
        this(parcel)
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLInventoryEntry(DBHandle dBHandle, Long j) throws DBObject.DatabaseBindingException {
        super(dBHandle != null ? dBHandle.getDB() : null, j)
    }

    SLInventoryEntry find(SQLiteDatabase sQLiteDatabase, UUID uuid) {
        try {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, fieldNames, "uuid_low = ? AND uuid_high = ?", String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null)
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = SLInventoryEntry(query)
                query.close()
                return sLInventoryEntry
            }
            query.close()
            return null
        } catch (SQLiteException e) {
            e.printStackTrace()
            return null
        }
    }

    SLInventoryEntry findOrCreate(SQLiteDatabase sQLiteDatabase, UUID uuid) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DBObject.DatabaseBindingException(SLInventoryEntry.class, "database is null")
        } else if (uuid == null) {
            throw DBObject.DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null")
        } else {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, fieldNames, "uuid_low = ? AND uuid_high = ?", String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null)
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = SLInventoryEntry(query)
                query.close()
                return sLInventoryEntry
            }
            query.close()
            SLInventoryEntry sLInventoryEntry2 = SLInventoryEntry()
            sLInventoryEntry2.uuid = uuid
            return sLInventoryEntry2
        }
    }

    SLInventoryEntry findOrCreateForUpdate(SQLiteDatabase sQLiteDatabase, UUID uuid) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DBObject.DatabaseBindingException(SLInventoryEntry.class, "database is null")
        } else if (uuid == null) {
            throw DBObject.DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null")
        } else {
            Cursor query = sQLiteDatabase.query(InventoryEntryDBObject.tableName, String[]{"_id"}, "uuid_low = ? AND uuid_high = ?", String[]{Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())}, (String) null, (String) null, (String) null)
            if (query.moveToFirst()) {
                SLInventoryEntry sLInventoryEntry = SLInventoryEntry()
                sLInventoryEntry._id = query.getLong(0)
                sLInventoryEntry.uuid = uuid
                query.close()
                return sLInventoryEntry
            }
            query.close()
            SLInventoryEntry sLInventoryEntry2 = SLInventoryEntry()
            sLInventoryEntry2.uuid = uuid
            return sLInventoryEntry2
        }
    }

    private Int getDrawableResourceForType(Int i) {
        switch (i) {
            case 0:
            case 12:
            case 15:
                return R.drawable.inv_image
            case 1:
                return R.drawable.inv_sound
            case 2:
                return R.drawable.inv_vcard
            case 3:
                return R.drawable.inv_landmark
            case 4:
            case 10:
            case 11:
                return R.drawable.inv_script
            case 5:
            case 17:
            case 18:
                return R.drawable.inv_clothes
            case 6:
                return R.drawable.inv_object
            case 7:
                return R.drawable.inv_notecard
            case 8:
            case 9:
                return R.drawable.inv_folder
            case 13:
                return R.drawable.inv_human
            case 14:
                return R.drawable.inv_trash
            case 16:
                return R.drawable.inv_recycle
            case 19:
                return R.drawable.inv_animation
            case 20:
                return R.drawable.inv_smile
            default:
                return -1
        }
    }

    SQLiteStatement getInsertStatement(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DBObject.DatabaseBindingException("Database is closed")
        } else if (!sQLiteDatabase.isOpen()) {
            throw DBObject.DatabaseBindingException("Database is closed")
        } else {
            try {
                return sQLiteDatabase.compileStatement(InventoryEntryDBObject.insertQuery)
            } catch (SQLiteException e) {
                DBObject.DatabaseBindingException databaseBindingException = DBObject.DatabaseBindingException(e.getMessage())
                databaseBindingException.initCause(e)
                throw databaseBindingException
            }
        }
    }

    SQLiteStatement getUpdateStatement(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DBObject.DatabaseBindingException("Database is closed")
        } else if (!sQLiteDatabase.isOpen()) {
            throw DBObject.DatabaseBindingException("Database is closed")
        } else {
            try {
                return sQLiteDatabase.compileStatement("UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=? WHERE uuid_high = ? AND uuid_low = ?")
            } catch (SQLiteException e) {
                DBObject.DatabaseBindingException databaseBindingException = DBObject.DatabaseBindingException(e.getMessage())
                databaseBindingException.initCause(e)
                throw databaseBindingException
            }
        }
    }

    private Unit parsePermissions(SimpleStringParser simpleStringParser, SLInventoryEntry sLInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", DELIM_EOL)
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY)
            if (!nextToken.equals("}")) {
                if (nextToken.equals("base_mask")) {
                    sLInventoryEntry.baseMask = simpleStringParser.getHexToken(DELIM_EOL)
                } else if (nextToken.equals("owner_mask")) {
                    sLInventoryEntry.ownerMask = simpleStringParser.getHexToken(DELIM_EOL)
                } else if (nextToken.equals("group_mask")) {
                    sLInventoryEntry.groupMask = simpleStringParser.getHexToken(DELIM_EOL)
                } else if (nextToken.equals("everyone_mask")) {
                    sLInventoryEntry.everyoneMask = simpleStringParser.getHexToken(DELIM_EOL)
                } else if (nextToken.equals("next_owner_mask")) {
                    sLInventoryEntry.nextOwnerMask = simpleStringParser.getHexToken(DELIM_EOL)
                } else if (nextToken.equals("creator_id")) {
                    sLInventoryEntry.creatorUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
                } else if (nextToken.equals("owner_id")) {
                    sLInventoryEntry.ownerUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
                } else if (nextToken.equals("last_owner_id")) {
                    sLInventoryEntry.lastOwnerUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
                } else if (nextToken.equals("group_id")) {
                    sLInventoryEntry.groupUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
                } else {
                    simpleStringParser.nextToken(DELIM_EOL)
                }
            } else {
                return
            }
        }
    }

    private Unit parseSaleInfo(SimpleStringParser simpleStringParser, SLInventoryEntry sLInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", DELIM_EOL)
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY)
            if (!nextToken.equals("}")) {
                if (nextToken.equals("sale_type")) {
                    sLInventoryEntry.saleType = SLSaleType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode()
                } else if (nextToken.equals("sale_price")) {
                    sLInventoryEntry.salePrice = simpleStringParser.getIntToken(DELIM_EOL)
                } else {
                    simpleStringParser.nextToken(DELIM_EOL)
                }
            } else {
                return
            }
        }
    }

    SLInventoryEntry parseString(SimpleStringParser simpleStringParser) throws SimpleStringParser.StringParsingException {
        SLInventoryEntry sLInventoryEntry = SLInventoryEntry()
        simpleStringParser.expectToken("{", DELIM_EOL)
        while (true) {
            String nextToken = simpleStringParser.nextToken(DELIM_ANY)
            if (nextToken.equals("}")) {
                return sLInventoryEntry
            }
            if (nextToken.equals("item_id")) {
                sLInventoryEntry.uuid = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
            } else if (nextToken.equals("parent_id")) {
                sLInventoryEntry.parentUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
            } else if (nextToken.equals("asset_id")) {
                sLInventoryEntry.assetUUID = UUID.fromString(simpleStringParser.nextToken(DELIM_EOL))
            } else if (nextToken.equals("type")) {
                sLInventoryEntry.assetType = SLAssetType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode()
            } else if (nextToken.equals("inv_type")) {
                sLInventoryEntry.invType = SLInventoryType.getByString(simpleStringParser.nextToken(DELIM_EOL)).getTypeCode()
            } else if (nextToken.equals("flags")) {
                sLInventoryEntry.flags = simpleStringParser.getHexToken(DELIM_EOL)
            } else if (nextToken.equals("name")) {
                sLInventoryEntry.name = simpleStringParser.getPipeTerminatedString(DELIM_EOL)
            } else if (nextToken.equals("desc")) {
                sLInventoryEntry.description = simpleStringParser.getPipeTerminatedString(DELIM_EOL)
            } else if (nextToken.equals("creation_date")) {
                sLInventoryEntry.creationDate = simpleStringParser.getIntToken(DELIM_EOL)
            } else if (nextToken.equals("permissions")) {
                simpleStringParser.nextToken(DELIM_EOL)
                parsePermissions(simpleStringParser, sLInventoryEntry)
            } else if (nextToken.equals("sale_info")) {
                simpleStringParser.nextToken(DELIM_EOL)
                parseSaleInfo(simpleStringParser, sLInventoryEntry)
            } else {
                simpleStringParser.nextToken(DELIM_EOL)
            }
        }
    }

    Cursor query(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, String str2) {
        if (sQLiteDatabase == null) {
            return null
        }
        try {
            return InventoryEntryDBObject.query(sQLiteDatabase, str, strArr, str2)
        } catch (DBObject.DatabaseBindingException e) {
            e.printStackTrace()
            return null
        }
    }

    Cursor query(DBHandle dBHandle, String str, String[] strArr, String str2) {
        if (dBHandle == null) {
            return null
        }
        try {
            return InventoryEntryDBObject.query(dBHandle, str, strArr, str2)
        } catch (DBObject.DatabaseBindingException e) {
            e.printStackTrace()
            return null
        }
    }

    Int getActionDescriptionResId() {
        SLAssetType byType = SLAssetType.getByType(this.assetType)
        if (byType != null) {
            return byType.getActionDescription()
        }
        switch (this.invType) {
            case 0:
            case 15:
                return this.assetType == SLAssetType.AT_TEXTURE.getTypeCode() ? R.string.asset_action_view : R.string.asset_action_rez
            case 3:
                return R.string.asset_action_teleport
            case 6:
                return R.string.asset_action_rez
            case 7:
                return R.string.asset_action_edit
            case 10:
                return R.string.asset_action_edit
            default:
                return -1
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType.getByType(r1.assetType)
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    Int getDrawableResource() {
        /*
            r1 = this
            Boolean r0 = r1.isFolder
            if (r0 == 0) goto L_0x0008
            r0 = 2130837696(0x7f0200c0, float:1.7280353E38)
            return r0
        L_0x0008:
            Boolean r0 = r1.isLink()
            if (r0 != 0) goto L_0x001b
            Int r0 = r1.assetType
            com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType r0 = com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType.getByType(r0)
            if (r0 == 0) goto L_0x001b
            Int r0 = r0.getDrawableResource()
            return r0
        L_0x001b:
            Int r0 = r1.invType
            Int r0 = getDrawableResourceForType(r0)
            return r0
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry.getDrawableResource():Int")
    }

    String getReadableTextForLink() {
        return SLInventoryType.getByType(this.invType).getReadableName() + ": " + this.name
    }

    Int getSubtypeDrawableResource() {
        if (this.isFolder) {
            switch (this.typeDefault) {
                case 0:
                case 15:
                    return R.drawable.inv_image
                case 1:
                    return R.drawable.inv_sound
                case 2:
                    return R.drawable.inv_vcard
                case 3:
                case 23:
                    return R.drawable.inv_landmark
                case 5:
                case 46:
                case 47:
                case 48:
                    return R.drawable.inv_clothes
                case 6:
                case 49:
                    return R.drawable.inv_object
                case 7:
                    return R.drawable.inv_notecard
                case 10:
                    return R.drawable.inv_script
                case 13:
                    return R.drawable.inv_human
                case 14:
                    return R.drawable.inv_trash
                case 16:
                    return R.drawable.inv_recycle
                case 20:
                    return R.drawable.inv_animation
                case 21:
                    return R.drawable.inv_smile
                default:
                    return -1
            }
        } else if (this.assetType == SLAssetType.AT_LINK.getTypeCode() || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
            return R.drawable.inv_link
        } else {
            return -1
        }
    }

    Int getTypeDescriptionResId() {
        SLAssetType byType = SLAssetType.getByType(this.assetType)
        if (byType != null) {
            return byType.getTypeDescription()
        }
        switch (this.invType) {
            case 0:
                return R.string.asset_type_texture
            case 1:
                return R.string.asset_type_sound
            case 2:
                return R.string.asset_type_calling_card
            case 3:
                return R.string.asset_type_landmark
            case 4:
                return R.string.asset_type_script
            case 5:
                return R.string.asset_type_clothing
            case 6:
                return R.string.asset_type_object
            case 7:
                return R.string.asset_type_notecard
            case 8:
                return R.string.asset_type_folder
            case 9:
                return R.string.asset_type_root_folder
            case 10:
                return R.string.asset_type_lsl
            case 11:
                return R.string.asset_type_lsl_bytecode
            case 12:
                return R.string.asset_type_texture_tga
            case 13:
                return R.string.asset_type_bodypart
            case 14:
                return R.string.asset_type_trash
            case 15:
                return R.string.asset_type_snapshot
            case 16:
                return R.string.asset_type_lost_and_found
            case 17:
                return R.string.asset_type_attachment
            case 18:
                return R.string.asset_type_wearable
            case 19:
                return R.string.asset_type_animation
            case 20:
                return R.string.asset_type_gesture
            default:
                return R.string.asset_type_unknown
        }
    }

    Boolean isAnimation() {
        if (this.assetType == SLAssetType.AT_ANIMATION.getTypeCode()) {
            return true
        }
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() && this.invType == 19
    }

    Boolean isCopyable() {
        return ((this.ownerMask & this.baseMask) & 32768) != 0
    }

    Boolean isFolderOrFolderLink() {
        return this.isFolder || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()
    }

    Boolean isLink() {
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()
    }

    Boolean isWearable() {
        if (this.assetType == SLAssetType.AT_BODYPART.getTypeCode() || this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            return true
        }
        return this.assetType == SLAssetType.AT_LINK.getTypeCode() && this.invType == SLInventoryType.IT_WEARABLE.getTypeCode()
    }

    Unit updateOrInsert(SQLiteDatabase sQLiteDatabase) throws DBObject.DatabaseBindingException {
        super.updateOrInsert(sQLiteDatabase, "uuid_low = ? AND uuid_high = ?", String[]{Long.toString(this.uuid.getLeastSignificantBits()), Long.toString(this.uuid.getMostSignificantBits())})
    }

    Unit updateOrInsert(SQLiteStatement sQLiteStatement, SQLiteStatement sQLiteStatement2) throws DBObject.DatabaseBindingException {
        sQLiteStatement.bindLong(19, this.uuid.getMostSignificantBits())
        sQLiteStatement.bindLong(20, this.uuid.getLeastSignificantBits())
        super.updateOrInsert(sQLiteStatement, sQLiteStatement2)
    }

    Object whatIsItemWornOn(@Nullable ImmutableMap<UUID, String> immutableMap, @Nullable Table<SLWearableType, UUID, SLWearable> table, Boolean z) {
        if (this.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (this.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                if (table != null) {
                    for (Table.Cell cell : table.cellSet()) {
                        SLWearableType sLWearableType = (SLWearableType) cell.getRowKey()
                        UUID uuid = (UUID) cell.getColumnKey()
                        if (!(sLWearableType == null || uuid == null)) {
                            if (!z || sLWearableType.isBodyPart()) {
                                if (uuid.equals(this.assetUUID)) {
                                    return sLWearableType
                                }
                                SLWearable sLWearable = (SLWearable) cell.getValue()
                                if (sLWearable != null && sLWearable.itemID.equals(this.assetUUID)) {
                                    return sLWearableType
                                }
                            }
                        }
                    }
                }
            } else if (immutableMap != null && !z && immutableMap.containsKey(this.assetUUID)) {
                return Boolean.TRUE
            }
        } else if (this.assetType == SLAssetType.AT_BODYPART.getTypeCode() || this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            SLWearableType byCode = SLWearableType.getByCode(this.flags & 255)
            if (byCode == null || table == null || ((z && (!byCode.isBodyPart())) || !table.contains(byCode, this.assetUUID))) {
                return null
            }
            return byCode
        } else if (immutableMap != null && !z && (immutableMap.containsKey(this.uuid) || immutableMap.containsKey(this.assetUUID))) {
            return Boolean.TRUE
        }
        return null
    }
}
