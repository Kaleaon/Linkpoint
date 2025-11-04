// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteException;
import java.util.UUID;
import com.lumiyaviewer.lumiya.orm.DBHandle;
import com.lumiyaviewer.lumiya.orm.DBObject;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject;

public class SLInventoryEntry extends InventoryEntryDBObject implements Parcelable
{
    public static final Parcelable$Creator<SLInventoryEntry> CREATOR;
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
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<SLInventoryEntry>() {
            public SLInventoryEntry createFromParcel(final Parcel parcel) {
                return new SLInventoryEntry(parcel, null);
            }
            
            public SLInventoryEntry[] newArray(final int n) {
                return new SLInventoryEntry[n];
            }
        };
    }
    
    public SLInventoryEntry() {
    }
    
    public SLInventoryEntry(final Cursor cursor) {
        super(cursor);
    }
    
    public SLInventoryEntry(final SQLiteDatabase sqLiteDatabase, final long n) throws DatabaseBindingException {
        super(sqLiteDatabase, n);
    }
    
    private SLInventoryEntry(final Parcel parcel) {
        super(parcel);
    }
    
    public SLInventoryEntry(final DBHandle dbHandle, final long n) throws DatabaseBindingException {
        SQLiteDatabase db = null;
        if (dbHandle != null) {
            db = dbHandle.getDB();
        }
        super(db, n);
    }
    
    public static SLInventoryEntry find(final SQLiteDatabase sqLiteDatabase, final UUID uuid) {
        Cursor query;
        try {
            query = sqLiteDatabase.query("Entries", SLInventoryEntry.fieldNames, "uuid_low = ? AND uuid_high = ?", new String[] { Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits()) }, (String)null, (String)null, (String)null);
            if (query.moveToFirst()) {
                final SLInventoryEntry slInventoryEntry = new SLInventoryEntry(query);
                query.close();
                return slInventoryEntry;
            }
        }
        catch (final SQLiteException ex) {
            ex.printStackTrace();
            return null;
        }
        query.close();
        return null;
    }
    
    public static SLInventoryEntry findOrCreate(final SQLiteDatabase sqLiteDatabase, final UUID uuid) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(SLInventoryEntry.class, "database is null");
        }
        if (uuid == null) {
            throw new DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null");
        }
        final Cursor query = sqLiteDatabase.query("Entries", SLInventoryEntry.fieldNames, "uuid_low = ? AND uuid_high = ?", new String[] { Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits()) }, (String)null, (String)null, (String)null);
        if (query.moveToFirst()) {
            final SLInventoryEntry slInventoryEntry = new SLInventoryEntry(query);
            query.close();
            return slInventoryEntry;
        }
        query.close();
        final SLInventoryEntry slInventoryEntry2 = new SLInventoryEntry();
        slInventoryEntry2.uuid = uuid;
        return slInventoryEntry2;
    }
    
    public static SLInventoryEntry findOrCreateForUpdate(final SQLiteDatabase sqLiteDatabase, final UUID uuid) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(SLInventoryEntry.class, "database is null");
        }
        if (uuid == null) {
            throw new DatabaseBindingException(SLInventoryEntry.class, "folderUUID is null");
        }
        final Cursor query = sqLiteDatabase.query("Entries", new String[] { "_id" }, "uuid_low = ? AND uuid_high = ?", new String[] { Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits()) }, (String)null, (String)null, (String)null);
        if (query.moveToFirst()) {
            final SLInventoryEntry slInventoryEntry = new SLInventoryEntry();
            slInventoryEntry._id = query.getLong(0);
            slInventoryEntry.uuid = uuid;
            query.close();
            return slInventoryEntry;
        }
        query.close();
        final SLInventoryEntry slInventoryEntry2 = new SLInventoryEntry();
        slInventoryEntry2.uuid = uuid;
        return slInventoryEntry2;
    }
    
    private static int getDrawableResourceForType(final int n) {
        switch (n) {
            default: {
                return -1;
            }
            case 3: {
                return 2130837699;
            }
            case 0:
            case 12:
            case 15: {
                return 2130837698;
            }
            case 1: {
                return 2130837706;
            }
            case 2: {
                return 2130837709;
            }
            case 4:
            case 10:
            case 11: {
                return 2130837704;
            }
            case 5:
            case 17:
            case 18: {
                return 2130837695;
            }
            case 6: {
                return 2130837702;
            }
            case 7: {
                return 2130837701;
            }
            case 8:
            case 9: {
                return 2130837696;
            }
            case 13: {
                return 2130837697;
            }
            case 14: {
                return 2130837707;
            }
            case 16: {
                return 2130837703;
            }
            case 19: {
                return 2130837693;
            }
            case 20: {
                return 2130837705;
            }
        }
    }
    
    public static SQLiteStatement getInsertStatement(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException("Database is closed");
        }
        if (!sqLiteDatabase.isOpen()) {
            throw new DatabaseBindingException("Database is closed");
        }
        try {
            return sqLiteDatabase.compileStatement("INSERT INTO Entries (parent_id,uuid_high,uuid_low,parentUUID_high,parentUUID_low,name,isFolder,typeDefault,version,sessionID_high,sessionID_low,fetchFailed,description,flags,invType,assetType,creationDate,_blobField) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        }
        catch (final SQLiteException cause) {
            final DatabaseBindingException ex = new DatabaseBindingException(cause.getMessage());
            ex.initCause((Throwable)cause);
            throw ex;
        }
    }
    
    public static SQLiteStatement getUpdateStatement(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException("Database is closed");
        }
        if (!sqLiteDatabase.isOpen()) {
            throw new DatabaseBindingException("Database is closed");
        }
        try {
            return sqLiteDatabase.compileStatement("UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=? WHERE uuid_high = ? AND uuid_low = ?");
        }
        catch (final SQLiteException cause) {
            final DatabaseBindingException ex = new DatabaseBindingException(cause.getMessage());
            ex.initCause((Throwable)cause);
            throw ex;
        }
    }
    
    private static void parsePermissions(final SimpleStringParser simpleStringParser, final SLInventoryEntry slInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", "\n");
        while (true) {
            final String nextToken = simpleStringParser.nextToken(" \t\n");
            if (nextToken.equals("}")) {
                break;
            }
            if (nextToken.equals("base_mask")) {
                slInventoryEntry.baseMask = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("owner_mask")) {
                slInventoryEntry.ownerMask = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("group_mask")) {
                slInventoryEntry.groupMask = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("everyone_mask")) {
                slInventoryEntry.everyoneMask = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("next_owner_mask")) {
                slInventoryEntry.nextOwnerMask = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("creator_id")) {
                slInventoryEntry.creatorUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("owner_id")) {
                slInventoryEntry.ownerUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("last_owner_id")) {
                slInventoryEntry.lastOwnerUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("group_id")) {
                slInventoryEntry.groupUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else {
                simpleStringParser.nextToken("\n");
            }
        }
    }
    
    private static void parseSaleInfo(final SimpleStringParser simpleStringParser, final SLInventoryEntry slInventoryEntry) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", "\n");
        while (true) {
            final String nextToken = simpleStringParser.nextToken(" \t\n");
            if (nextToken.equals("}")) {
                break;
            }
            if (nextToken.equals("sale_type")) {
                slInventoryEntry.saleType = SLSaleType.getByString(simpleStringParser.nextToken("\n")).getTypeCode();
            }
            else if (nextToken.equals("sale_price")) {
                slInventoryEntry.salePrice = simpleStringParser.getIntToken("\n");
            }
            else {
                simpleStringParser.nextToken("\n");
            }
        }
    }
    
    public static SLInventoryEntry parseString(final SimpleStringParser simpleStringParser) throws SimpleStringParser.StringParsingException {
        final SLInventoryEntry slInventoryEntry = new SLInventoryEntry();
        simpleStringParser.expectToken("{", "\n");
        while (true) {
            final String nextToken = simpleStringParser.nextToken(" \t\n");
            if (nextToken.equals("}")) {
                break;
            }
            if (nextToken.equals("item_id")) {
                slInventoryEntry.uuid = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("parent_id")) {
                slInventoryEntry.parentUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("asset_id")) {
                slInventoryEntry.assetUUID = UUID.fromString(simpleStringParser.nextToken("\n"));
            }
            else if (nextToken.equals("type")) {
                slInventoryEntry.assetType = SLAssetType.getByString(simpleStringParser.nextToken("\n")).getTypeCode();
            }
            else if (nextToken.equals("inv_type")) {
                slInventoryEntry.invType = SLInventoryType.getByString(simpleStringParser.nextToken("\n")).getTypeCode();
            }
            else if (nextToken.equals("flags")) {
                slInventoryEntry.flags = simpleStringParser.getHexToken("\n");
            }
            else if (nextToken.equals("name")) {
                slInventoryEntry.name = simpleStringParser.getPipeTerminatedString("\n");
            }
            else if (nextToken.equals("desc")) {
                slInventoryEntry.description = simpleStringParser.getPipeTerminatedString("\n");
            }
            else if (nextToken.equals("creation_date")) {
                slInventoryEntry.creationDate = simpleStringParser.getIntToken("\n");
            }
            else if (nextToken.equals("permissions")) {
                simpleStringParser.nextToken("\n");
                parsePermissions(simpleStringParser, slInventoryEntry);
            }
            else if (nextToken.equals("sale_info")) {
                simpleStringParser.nextToken("\n");
                parseSaleInfo(simpleStringParser, slInventoryEntry);
            }
            else {
                simpleStringParser.nextToken("\n");
            }
        }
        return slInventoryEntry;
    }
    
    public static Cursor query(final SQLiteDatabase sqLiteDatabase, final String s, final String[] array, final String s2) {
        if (sqLiteDatabase == null) {
            return null;
        }
        try {
            return InventoryEntryDBObject.query(sqLiteDatabase, s, array, s2);
        }
        catch (final DatabaseBindingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Cursor query(final DBHandle dbHandle, final String s, final String[] array, final String s2) {
        if (dbHandle == null) {
            return null;
        }
        try {
            return InventoryEntryDBObject.query(dbHandle, s, array, s2);
        }
        catch (final DatabaseBindingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public int getActionDescriptionResId() {
        final SLAssetType byType = SLAssetType.getByType(this.assetType);
        if (byType != null) {
            return byType.getActionDescription();
        }
        switch (this.invType) {
            default: {
                return -1;
            }
            case 3: {
                return 2131296335;
            }
            case 7: {
                return 2131296333;
            }
            case 10: {
                return 2131296333;
            }
            case 0:
            case 15: {
                if (this.assetType == SLAssetType.AT_TEXTURE.getTypeCode()) {
                    return 2131296336;
                }
                return 2131296334;
            }
            case 6: {
                return 2131296334;
            }
        }
    }
    
    public int getDrawableResource() {
        if (this.isFolder) {
            return 2130837696;
        }
        if (!this.isLink()) {
            final SLAssetType byType = SLAssetType.getByType(this.assetType);
            if (byType != null) {
                return byType.getDrawableResource();
            }
        }
        return getDrawableResourceForType(this.invType);
    }
    
    public String getReadableTextForLink() {
        return SLInventoryType.getByType(this.invType).getReadableName() + ": " + this.name;
    }
    
    public int getSubtypeDrawableResource() {
        if (this.isFolder) {
            switch (this.typeDefault) {
                case 0:
                case 15: {
                    return 2130837698;
                }
                case 1: {
                    return 2130837706;
                }
                case 2: {
                    return 2130837709;
                }
                case 3:
                case 23: {
                    return 2130837699;
                }
                case 5:
                case 46:
                case 47:
                case 48: {
                    return 2130837695;
                }
                case 7: {
                    return 2130837701;
                }
                case 6:
                case 49: {
                    return 2130837702;
                }
                case 10: {
                    return 2130837704;
                }
                case 13: {
                    return 2130837697;
                }
                case 14: {
                    return 2130837707;
                }
                case 16: {
                    return 2130837703;
                }
                case 20: {
                    return 2130837693;
                }
                case 21: {
                    return 2130837705;
                }
            }
        }
        else if (this.assetType == SLAssetType.AT_LINK.getTypeCode() || this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
            return 2130837700;
        }
        return -1;
    }
    
    public int getTypeDescriptionResId() {
        final SLAssetType byType = SLAssetType.getByType(this.assetType);
        if (byType != null) {
            return byType.getTypeDescription();
        }
        int n = 2131296376;
        switch (this.invType) {
            case 0: {
                n = 2131296373;
                break;
            }
            case 1: {
                n = 2131296372;
                break;
            }
            case 2: {
                n = 2131296357;
                break;
            }
            case 3: {
                n = 2131296361;
                break;
            }
            case 4: {
                n = 2131296369;
                break;
            }
            case 5: {
                n = 2131296358;
                break;
            }
            case 6: {
                n = 2131296367;
                break;
            }
            case 7: {
                n = 2131296366;
                break;
            }
            case 8: {
                n = 2131296359;
                break;
            }
            case 9: {
                n = 2131296368;
                break;
            }
            case 10: {
                n = 2131296364;
                break;
            }
            case 11: {
                n = 2131296365;
                break;
            }
            case 12: {
                n = 2131296374;
                break;
            }
            case 13: {
                n = 2131296356;
                break;
            }
            case 14: {
                n = 2131296375;
                break;
            }
            case 15: {
                n = 2131296371;
                break;
            }
            case 16: {
                n = 2131296363;
                break;
            }
            case 17: {
                n = 2131296355;
                break;
            }
            case 18: {
                n = 2131296377;
                break;
            }
            case 19: {
                n = 2131296354;
                break;
            }
            case 20: {
                n = 2131296360;
                break;
            }
        }
        return n;
    }
    
    public boolean isAnimation() {
        return this.assetType == SLAssetType.AT_ANIMATION.getTypeCode() || (this.assetType == SLAssetType.AT_LINK.getTypeCode() && this.invType == 19);
    }
    
    public boolean isCopyable() {
        boolean b = false;
        if ((this.ownerMask & this.baseMask & 0x8000) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isFolderOrFolderLink() {
        boolean b = true;
        if (!this.isFolder) {
            b = (this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode() && b);
        }
        return b;
    }
    
    public boolean isLink() {
        boolean b = true;
        if (this.assetType != SLAssetType.AT_LINK.getTypeCode()) {
            b = (this.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode() && b);
        }
        return b;
    }
    
    public boolean isWearable() {
        boolean b2;
        final boolean b = b2 = true;
        if (this.assetType != SLAssetType.AT_BODYPART.getTypeCode()) {
            if (this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
                b2 = b;
            }
            else {
                if (this.assetType == SLAssetType.AT_LINK.getTypeCode()) {
                    b2 = b;
                    if (this.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                        return b2;
                    }
                }
                b2 = false;
            }
        }
        return b2;
    }
    
    public void updateOrInsert(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        super.updateOrInsert(sqLiteDatabase, "uuid_low = ? AND uuid_high = ?", new String[] { Long.toString(this.uuid.getLeastSignificantBits()), Long.toString(this.uuid.getMostSignificantBits()) });
    }
    
    public void updateOrInsert(final SQLiteStatement sqLiteStatement, final SQLiteStatement sqLiteStatement2) throws DatabaseBindingException {
        sqLiteStatement.bindLong(19, this.uuid.getMostSignificantBits());
        sqLiteStatement.bindLong(20, this.uuid.getLeastSignificantBits());
        super.updateOrInsert(sqLiteStatement, sqLiteStatement2);
    }
    
    public Object whatIsItemWornOn(@Nullable final ImmutableMap<UUID, String> immutableMap, @Nullable final Table<SLWearableType, UUID, SLWearable> table, final boolean b) {
        if (this.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (this.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                if (table != null) {
                    for (final Table.Cell<SLWearableType, C, V> cell : table.cellSet()) {
                        final SLWearableType slWearableType = cell.getRowKey();
                        final UUID uuid = (UUID)cell.getColumnKey();
                        if (slWearableType != null && uuid != null && (!b || slWearableType.isBodyPart())) {
                            if (uuid.equals(this.assetUUID)) {
                                return slWearableType;
                            }
                            final SLWearable slWearable = (SLWearable)cell.getValue();
                            if (slWearable != null && slWearable.itemID.equals(this.assetUUID)) {
                                return slWearableType;
                            }
                            continue;
                        }
                    }
                }
            }
            else if (immutableMap != null && !b && immutableMap.containsKey(this.assetUUID)) {
                return Boolean.TRUE;
            }
        }
        else if (this.assetType == SLAssetType.AT_BODYPART.getTypeCode() || this.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            final SLWearableType byCode = SLWearableType.getByCode(this.flags & 0xFF);
            if (byCode != null && table != null) {
                if (b && (byCode.isBodyPart() ^ true)) {
                    return null;
                }
                if (table.contains(byCode, this.assetUUID)) {
                    return byCode;
                }
            }
        }
        else if (immutableMap != null && !b && (immutableMap.containsKey(this.uuid) || immutableMap.containsKey(this.assetUUID))) {
            return Boolean.TRUE;
        }
        return null;
    }
}
