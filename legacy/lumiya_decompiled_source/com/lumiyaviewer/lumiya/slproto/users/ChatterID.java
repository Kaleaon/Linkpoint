package com.lumiyaviewer.lumiya.slproto.users;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.GridConnectionManager;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class ChatterID implements Parcelable, Comparable<ChatterID> {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f150xb1d54699 = null;
    @Nonnull
    public final UUID agentUUID;

    /* loaded from: classes.dex */
    public static class ChatterIDGroup extends ChatterIDWithUUID {
        public static final Parcelable.Creator<ChatterIDGroup> CREATOR = new Parcelable.Creator<ChatterIDGroup>() { // from class: com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDGroup createFromParcel(Parcel parcel) {
                return new ChatterIDGroup(parcel, (ChatterIDGroup) null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDGroup[] newArray(int i) {
                return new ChatterIDGroup[i];
            }
        };

        private ChatterIDGroup(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null);
        }

        /* synthetic */ ChatterIDGroup(Parcel parcel, ChatterIDGroup chatterIDGroup) {
            this(parcel);
        }

        private ChatterIDGroup(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
            super(uuid, uuid2, null);
        }

        /* synthetic */ ChatterIDGroup(UUID uuid, UUID uuid2, ChatterIDGroup chatterIDGroup) {
            this(uuid, uuid2);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ int compareTo(@Nonnull ChatterID chatterID) {
            return super.compareTo(chatterID);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDGroup) {
                return super.equals(obj);
            }
            return false;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.Group;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID
        @Nonnull
        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nullable
        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public Subscription getPictureID(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull final OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getCachedGroupProfiles().getPool().subscribe((Subscribable) this.uuid, UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE
                private final /* synthetic */ void $m$0(Object obj) {
                    ((ChatterID.OnChatterPictureIDListener) onChatterPictureIDListener).onChatterPictureID(((GroupProfileReply) obj).GroupData_Field.InsigniaID);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ int hashCode() {
            return super.hashCode();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ boolean isValidUUID() {
            return super.isValidUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ String toString() {
            return super.toString();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID, android.os.Parcelable
        public /* bridge */ /* synthetic */ void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
        }
    }

    /* loaded from: classes.dex */
    public static class ChatterIDLocal extends ChatterID {
        public static final Parcelable.Creator<ChatterIDLocal> CREATOR = new Parcelable.Creator<ChatterIDLocal>() { // from class: com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDLocal.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDLocal createFromParcel(Parcel parcel) {
                return new ChatterIDLocal(parcel, (ChatterIDLocal) null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDLocal[] newArray(int i) {
                return new ChatterIDLocal[i];
            }
        };

        private ChatterIDLocal(Parcel parcel) {
            super(parcel, (ChatterID) null);
        }

        /* synthetic */ ChatterIDLocal(Parcel parcel, ChatterIDLocal chatterIDLocal) {
            this(parcel);
        }

        private ChatterIDLocal(@Nonnull UUID uuid) {
            super(uuid, (ChatterID) null);
        }

        /* synthetic */ ChatterIDLocal(UUID uuid, ChatterIDLocal chatterIDLocal) {
            this(uuid);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDLocal) {
                return super.equals(obj);
            }
            return false;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.Local;
        }
    }

    /* loaded from: classes.dex */
    public static class ChatterIDUser extends ChatterIDWithUUID {
        public static final Parcelable.Creator<ChatterIDUser> CREATOR = new Parcelable.Creator<ChatterIDUser>() { // from class: com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDUser createFromParcel(Parcel parcel) {
                return new ChatterIDUser(parcel, (ChatterIDUser) null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ChatterIDUser[] newArray(int i) {
                return new ChatterIDUser[i];
            }
        };

        private ChatterIDUser(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null);
        }

        /* synthetic */ ChatterIDUser(Parcel parcel, ChatterIDUser chatterIDUser) {
            this(parcel);
        }

        private ChatterIDUser(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
            super(uuid, uuid2, null);
        }

        /* synthetic */ ChatterIDUser(UUID uuid, UUID uuid2, ChatterIDUser chatterIDUser) {
            this(uuid, uuid2);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ int compareTo(@Nonnull ChatterID chatterID) {
            return super.compareTo(chatterID);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDUser) {
                return super.equals(obj);
            }
            return false;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.User;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID
        @Nonnull
        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nullable
        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public Subscription getPictureID(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull final OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getAvatarProperties().getPool().subscribe((Subscribable) this.uuid, executor, new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1
                private final /* synthetic */ void $m$0(Object obj) {
                    ((ChatterID.OnChatterPictureIDListener) onChatterPictureIDListener).onChatterPictureID(((AvatarPropertiesReply) obj).PropertiesData_Field.ImageID);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ int hashCode() {
            return super.hashCode();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ boolean isValidUUID() {
            return super.isValidUUID();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public /* bridge */ /* synthetic */ String toString() {
            return super.toString();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDWithUUID, com.lumiyaviewer.lumiya.slproto.users.ChatterID, android.os.Parcelable
        public /* bridge */ /* synthetic */ void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
        }
    }

    /* loaded from: classes.dex */
    private static abstract class ChatterIDWithUUID extends ChatterID {
        @Nonnull
        protected final UUID uuid;

        private ChatterIDWithUUID(Parcel parcel) {
            super(parcel, (ChatterID) null);
            this.uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
        }

        /* synthetic */ ChatterIDWithUUID(Parcel parcel, ChatterIDWithUUID chatterIDWithUUID) {
            this(parcel);
        }

        private ChatterIDWithUUID(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
            super(uuid, (ChatterID) null);
            this.uuid = uuid2 == null ? UUIDPool.ZeroUUID : uuid2;
        }

        /* synthetic */ ChatterIDWithUUID(UUID uuid, UUID uuid2, ChatterIDWithUUID chatterIDWithUUID) {
            this(uuid, uuid2);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public int compareTo(@Nonnull ChatterID chatterID) {
            int compareTo = super.compareTo(chatterID);
            if (compareTo != 0) {
                return compareTo;
            }
            if (chatterID instanceof ChatterIDWithUUID) {
                return this.uuid.compareTo(((ChatterIDWithUUID) chatterID).uuid);
            }
            return 0;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public boolean equals(Object obj) {
            if (super.equals(obj) && (obj instanceof ChatterIDWithUUID)) {
                return Objects.equal(this.uuid, ((ChatterIDWithUUID) obj).uuid);
            }
            return false;
        }

        @Nonnull
        public UUID getChatterUUID() {
            return this.uuid;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        @Nullable
        public UUID getOptionalChatterUUID() {
            return this.uuid;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public int hashCode() {
            return (this.uuid != null ? this.uuid.hashCode() : 0) + super.hashCode();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public boolean isValidUUID() {
            if (this.uuid != null) {
                return !UUIDPool.ZeroUUID.equals(this.uuid);
            }
            return false;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            bundle.putString("chatterUUID", this.uuid != null ? this.uuid.toString() : UUIDPool.ZeroUUID.toString());
            return bundle;
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID
        public String toString() {
            return super.toString() + ":" + (this.uuid != null ? this.uuid.toString() : "null");
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            if (this.uuid != null) {
                parcel.writeLong(this.uuid.getMostSignificantBits());
                parcel.writeLong(this.uuid.getLeastSignificantBits());
                return;
            }
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
    }

    /* loaded from: classes.dex */
    public enum ChatterType {
        Local(NotificationType.LocalChat),
        User(NotificationType.Private),
        Group(NotificationType.Group);
        
        public static final ChatterType[] VALUES = valuesCustom();
        @Nonnull
        private final NotificationType notificationType;

        ChatterType(NotificationType notificationType) {
            this.notificationType = notificationType;
        }

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static ChatterType[] valuesCustom() {
            return values();
        }

        @Nonnull
        public NotificationType getNotificationType() {
            return this.notificationType;
        }
    }

    /* loaded from: classes.dex */
    public interface OnChatterPictureIDListener {
        void onChatterPictureID(UUID uuid);
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m270x2680ba3d() {
        if (f150xb1d54699 != null) {
            return f150xb1d54699;
        }
        int[] iArr = new int[ChatterType.valuesCustom().length];
        try {
            iArr[ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterType.Local.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterType.User.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f150xb1d54699 = iArr;
        return iArr;
    }

    private ChatterID(Parcel parcel) {
        this.agentUUID = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
    }

    /* synthetic */ ChatterID(Parcel parcel, ChatterID chatterID) {
        this(parcel);
    }

    private ChatterID(@Nonnull UUID uuid) {
        this.agentUUID = uuid;
    }

    /* synthetic */ ChatterID(UUID uuid, ChatterID chatterID) {
        this(uuid);
    }

    public static ChatterID fromBundle(Bundle bundle) {
        UUID fromString = UUID.fromString(bundle.getString("chatterAgentUUID"));
        switch (m270x2680ba3d()[ChatterType.VALUES[bundle.getInt("chatterType", 0)].ordinal()]) {
            case 1:
                return getGroupChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")));
            case 2:
                return getLocalChatterID(fromString);
            case 3:
                return getUserChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")));
            default:
                return null;
        }
    }

    public static ChatterID fromDatabaseObject(UUID uuid, Chatter chatter) {
        switch (m270x2680ba3d()[ChatterType.VALUES[chatter.getType()].ordinal()]) {
            case 1:
                return getGroupChatterID(uuid, chatter.getUuid());
            case 2:
                return getLocalChatterID(uuid);
            case 3:
                return getUserChatterID(uuid, chatter.getUuid());
            default:
                return null;
        }
    }

    @Nonnull
    public static ChatterID getGroupChatterID(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
        return new ChatterIDGroup(uuid, uuid2, null);
    }

    @Nonnull
    public static ChatterID getLocalChatterID(@Nonnull UUID uuid) {
        return new ChatterIDLocal(uuid, (ChatterIDLocal) null);
    }

    @Nonnull
    public static ChatterIDUser getUserChatterID(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
        return new ChatterIDUser(uuid, uuid2, null);
    }

    @Override // java.lang.Comparable
    public int compareTo(@Nonnull ChatterID chatterID) {
        int compareTo = this.agentUUID.compareTo(chatterID.agentUUID);
        return compareTo != 0 ? compareTo : getChatterType().compareTo(chatterID.getChatterType());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ChatterID) {
            return ((ChatterID) obj).agentUUID.equals(this.agentUUID);
        }
        return false;
    }

    @Nonnull
    public abstract ChatterType getChatterType();

    @Nullable
    public SLGridConnection getConnection() {
        return GridConnectionManager.getConnection(this.agentUUID);
    }

    @Nullable
    public UUID getOptionalChatterUUID() {
        return null;
    }

    public Subscription getPictureID(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull OnChatterPictureIDListener onChatterPictureIDListener) {
        return null;
    }

    @Nullable
    public UserManager getUserManager() {
        return UserManager.getUserManager(this.agentUUID);
    }

    public int hashCode() {
        return this.agentUUID.hashCode() + 1 + getChatterType().ordinal();
    }

    public boolean isValidUUID() {
        return false;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("chatterType", getChatterType().ordinal());
        bundle.putString("chatterAgentUUID", this.agentUUID.toString());
        return bundle;
    }

    public void toDatabaseObject(Chatter chatter) {
        chatter.setType(getChatterType().ordinal());
        chatter.setUuid(getOptionalChatterUUID());
    }

    public String toString() {
        return "Chatter:" + getChatterType().toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.agentUUID.getMostSignificantBits());
        parcel.writeLong(this.agentUUID.getLeastSignificantBits());
    }
}
