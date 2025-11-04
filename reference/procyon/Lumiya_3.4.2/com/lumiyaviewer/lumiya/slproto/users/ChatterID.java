// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import android.os.Parcelable$Creator;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.GridConnectionManager;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.dao.Chatter;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.os.Parcel;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.os.Parcelable;

public abstract class ChatterID implements Parcelable, Comparable<ChatterID>
{
    @Nonnull
    public final UUID agentUUID;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues() {
        if (ChatterID.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues != null) {
            return ChatterID.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = new int[ChatterType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterType.Local.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterType.User.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    private ChatterID(final Parcel parcel) {
        this.agentUUID = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
    }
    
    private ChatterID(@Nonnull final UUID agentUUID) {
        this.agentUUID = agentUUID;
    }
    
    public static ChatterID fromBundle(final Bundle bundle) {
        final UUID fromString = UUID.fromString(bundle.getString("chatterAgentUUID"));
        switch (-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[bundle.getInt("chatterType", 0)].ordinal()]) {
            default: {
                return null;
            }
            case 2: {
                return getLocalChatterID(fromString);
            }
            case 3: {
                return getUserChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")));
            }
            case 1: {
                return getGroupChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")));
            }
        }
    }
    
    public static ChatterID fromDatabaseObject(final UUID uuid, final Chatter chatter) {
        switch (-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[chatter.getType()].ordinal()]) {
            default: {
                return null;
            }
            case 2: {
                return getLocalChatterID(uuid);
            }
            case 3: {
                return getUserChatterID(uuid, chatter.getUuid());
            }
            case 1: {
                return getGroupChatterID(uuid, chatter.getUuid());
            }
        }
    }
    
    @Nonnull
    public static ChatterID getGroupChatterID(@Nonnull final UUID uuid, @Nonnull final UUID uuid2) {
        return new ChatterIDGroup(uuid, uuid2, (ChatterIDGroup)null);
    }
    
    @Nonnull
    public static ChatterID getLocalChatterID(@Nonnull final UUID uuid) {
        return new ChatterIDLocal(uuid, null);
    }
    
    @Nonnull
    public static ChatterIDUser getUserChatterID(@Nonnull final UUID uuid, @Nonnull final UUID uuid2) {
        return new ChatterIDUser(uuid, uuid2, (ChatterIDUser)null);
    }
    
    public int compareTo(@Nonnull final ChatterID chatterID) {
        final int compareTo = this.agentUUID.compareTo(chatterID.agentUUID);
        if (compareTo != 0) {
            return compareTo;
        }
        return this.getChatterType().compareTo(chatterID.getChatterType());
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ChatterID && ((ChatterID)o).agentUUID.equals(this.agentUUID);
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
    
    public Subscription getPictureID(@Nonnull final UserManager userManager, @Nullable final Executor executor, @Nonnull final OnChatterPictureIDListener onChatterPictureIDListener) {
        return null;
    }
    
    @Nullable
    public UserManager getUserManager() {
        return UserManager.getUserManager(this.agentUUID);
    }
    
    @Override
    public int hashCode() {
        return this.agentUUID.hashCode() + 1 + this.getChatterType().ordinal();
    }
    
    public boolean isValidUUID() {
        return false;
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("chatterType", this.getChatterType().ordinal());
        bundle.putString("chatterAgentUUID", this.agentUUID.toString());
        return bundle;
    }
    
    public void toDatabaseObject(final Chatter chatter) {
        chatter.setType(this.getChatterType().ordinal());
        chatter.setUuid(this.getOptionalChatterUUID());
    }
    
    @Override
    public String toString() {
        return "Chatter:" + this.getChatterType().toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeLong(this.agentUUID.getMostSignificantBits());
        parcel.writeLong(this.agentUUID.getLeastSignificantBits());
    }
    
    public static class ChatterIDGroup extends ChatterIDWithUUID
    {
        public static final Parcelable$Creator<ChatterIDGroup> CREATOR;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<ChatterIDGroup>() {
                public ChatterIDGroup createFromParcel(final Parcel parcel) {
                    return new ChatterIDGroup(parcel, (ChatterIDGroup)null);
                }
                
                public ChatterIDGroup[] newArray(final int n) {
                    return new ChatterIDGroup[n];
                }
            };
        }
        
        private ChatterIDGroup(final Parcel parcel) {
            super(parcel, null);
        }
        
        private ChatterIDGroup(@Nonnull final UUID uuid, @Nonnull final UUID uuid2) {
            super(uuid, uuid2, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof ChatterIDGroup && super.equals(o);
        }
        
        @Nonnull
        @Override
        public ChatterType getChatterType() {
            return ChatterType.Group;
        }
        
        @Override
        public Subscription getPictureID(@Nonnull final UserManager userManager, @Nullable final Executor executor, @Nonnull final OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getCachedGroupProfiles().getPool().subscribe(this.uuid, UIThreadExecutor.getInstance(), new _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(onChatterPictureIDListener));
        }
    }
    
    private abstract static class ChatterIDWithUUID extends ChatterID
    {
        @Nonnull
        protected final UUID uuid;
        
        private ChatterIDWithUUID(final Parcel parcel) {
            super(parcel, null);
            this.uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
        }
        
        private ChatterIDWithUUID(@Nonnull final UUID uuid, @Nonnull UUID zeroUUID) {
            super(uuid, null);
            if (zeroUUID == null) {
                zeroUUID = UUIDPool.ZeroUUID;
            }
            this.uuid = zeroUUID;
        }
        
        @Override
        public int compareTo(@Nonnull final ChatterID chatterID) {
            final int compareTo = super.compareTo(chatterID);
            if (compareTo != 0) {
                return compareTo;
            }
            if (chatterID instanceof ChatterIDWithUUID) {
                return this.uuid.compareTo(((ChatterIDWithUUID)chatterID).uuid);
            }
            return 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            return super.equals(o) && o instanceof ChatterIDWithUUID && Objects.equal(this.uuid, ((ChatterIDWithUUID)o).uuid);
        }
        
        @Nonnull
        public UUID getChatterUUID() {
            return this.uuid;
        }
        
        @Nullable
        @Override
        public UUID getOptionalChatterUUID() {
            return this.uuid;
        }
        
        @Override
        public int hashCode() {
            final int hashCode = super.hashCode();
            int hashCode2;
            if (this.uuid != null) {
                hashCode2 = this.uuid.hashCode();
            }
            else {
                hashCode2 = 0;
            }
            return hashCode2 + hashCode;
        }
        
        @Override
        public boolean isValidUUID() {
            return this.uuid != null && (UUIDPool.ZeroUUID.equals(this.uuid) ^ true);
        }
        
        @Override
        public Bundle toBundle() {
            final Bundle bundle = super.toBundle();
            String s;
            if (this.uuid != null) {
                s = this.uuid.toString();
            }
            else {
                s = UUIDPool.ZeroUUID.toString();
            }
            bundle.putString("chatterUUID", s);
            return bundle;
        }
        
        @Override
        public String toString() {
            final StringBuilder append = new StringBuilder().append(super.toString()).append(":");
            String string;
            if (this.uuid != null) {
                string = this.uuid.toString();
            }
            else {
                string = "null";
            }
            return append.append(string).toString();
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            if (this.uuid != null) {
                parcel.writeLong(this.uuid.getMostSignificantBits());
                parcel.writeLong(this.uuid.getLeastSignificantBits());
            }
            else {
                parcel.writeLong(0L);
                parcel.writeLong(0L);
            }
        }
    }
    
    public static class ChatterIDLocal extends ChatterID
    {
        public static final Parcelable$Creator<ChatterIDLocal> CREATOR;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<ChatterIDLocal>() {
                public ChatterIDLocal createFromParcel(final Parcel parcel) {
                    return new ChatterIDLocal(parcel, null);
                }
                
                public ChatterIDLocal[] newArray(final int n) {
                    return new ChatterIDLocal[n];
                }
            };
        }
        
        private ChatterIDLocal(final Parcel parcel) {
            super(parcel, null);
        }
        
        private ChatterIDLocal(@Nonnull final UUID uuid) {
            super(uuid, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof ChatterIDLocal && super.equals(o);
        }
        
        @Nonnull
        @Override
        public ChatterType getChatterType() {
            return ChatterType.Local;
        }
    }
    
    public static class ChatterIDUser extends ChatterIDWithUUID
    {
        public static final Parcelable$Creator<ChatterIDUser> CREATOR;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<ChatterIDUser>() {
                public ChatterIDUser createFromParcel(final Parcel parcel) {
                    return new ChatterIDUser(parcel, (ChatterIDUser)null);
                }
                
                public ChatterIDUser[] newArray(final int n) {
                    return new ChatterIDUser[n];
                }
            };
        }
        
        private ChatterIDUser(final Parcel parcel) {
            super(parcel, null);
        }
        
        private ChatterIDUser(@Nonnull final UUID uuid, @Nonnull final UUID uuid2) {
            super(uuid, uuid2, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof ChatterIDUser && super.equals(o);
        }
        
        @Nonnull
        @Override
        public ChatterType getChatterType() {
            return ChatterType.User;
        }
        
        @Override
        public Subscription getPictureID(@Nonnull final UserManager userManager, @Nullable final Executor executor, @Nonnull final OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getAvatarProperties().getPool().subscribe(this.uuid, executor, new _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE$1(onChatterPictureIDListener));
        }
    }
    
    public enum ChatterType
    {
        Group("Group", 2, NotificationType.Group), 
        Local("Local", 0, NotificationType.LocalChat), 
        User("User", 1, NotificationType.Private);
        
        public static final ChatterType[] VALUES;
        @Nonnull
        private final NotificationType notificationType;
        
        static {
            VALUES = values();
        }
        
        private ChatterType(final String name, final int ordinal, @Nonnull final NotificationType notificationType) {
            this.notificationType = notificationType;
        }
        
        @Nonnull
        public NotificationType getNotificationType() {
            return this.notificationType;
        }
    }
    
    public interface OnChatterPictureIDListener
    {
        void onChatterPictureID(final UUID p0);
    }
}
