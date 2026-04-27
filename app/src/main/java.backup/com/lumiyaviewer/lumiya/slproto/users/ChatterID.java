package com.lumiyaviewer.lumiya.slproto.users;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.GridConnectionManager;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ChatterID implements Parcelable, Comparable<ChatterID> {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = null;
    @Nonnull
    public final UUID agentUUID;

    public static class ChatterIDGroup extends ChatterIDWithUUID {
        public static final Parcelable.Creator<ChatterIDGroup> CREATOR = new Parcelable.Creator<ChatterIDGroup>() {
            public ChatterIDGroup createFromParcel(Parcel parcel) {
                return new ChatterIDGroup(parcel, (ChatterIDGroup) null);
            }

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
            super(uuid, uuid2, (ChatterIDWithUUID) null);
        }

        /* synthetic */ ChatterIDGroup(UUID uuid, UUID uuid2, ChatterIDGroup chatterIDGroup) {
            this(uuid, uuid2);
        }

        public /* bridge */ /* synthetic */ int compareTo(@Nonnull ChatterID chatterID) {
            return super.compareTo(chatterID);
        }

        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDGroup) {
                return super.equals(obj);
            }
            return false;
        }

        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.Group;
        }

        @Nonnull
        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID();
        }

        @Nullable
        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID();
        }

        public Subscription getPictureID(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getCachedGroupProfiles().getPool().subscribe(this.uuid, UIThreadExecutor.getInstance(), new $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(onChatterPictureIDListener));
        }

        public /* bridge */ /* synthetic */ int hashCode() {
            return super.hashCode();
        }

        public /* bridge */ /* synthetic */ boolean isValidUUID() {
            return super.isValidUUID();
        }

        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle();
        }

        public /* bridge */ /* synthetic */ String toString() {
            return super.toString();
        }

        public /* bridge */ /* synthetic */ void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
        }
    }

    public static class ChatterIDLocal extends ChatterID {
        public static final Parcelable.Creator<ChatterIDLocal> CREATOR = new Parcelable.Creator<ChatterIDLocal>() {
            public ChatterIDLocal createFromParcel(Parcel parcel) {
                return new ChatterIDLocal(parcel, (ChatterIDLocal) null);
            }

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

        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDLocal) {
                return ChatterID.super.equals(obj);
            }
            return false;
        }

        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.Local;
        }
    }

    public static class ChatterIDUser extends ChatterIDWithUUID {
        public static final Parcelable.Creator<ChatterIDUser> CREATOR = new Parcelable.Creator<ChatterIDUser>() {
            public ChatterIDUser createFromParcel(Parcel parcel) {
                return new ChatterIDUser(parcel, (ChatterIDUser) null);
            }

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
            super(uuid, uuid2, (ChatterIDWithUUID) null);
        }

        /* synthetic */ ChatterIDUser(UUID uuid, UUID uuid2, ChatterIDUser chatterIDUser) {
            this(uuid, uuid2);
        }

        public /* bridge */ /* synthetic */ int compareTo(@Nonnull ChatterID chatterID) {
            return super.compareTo(chatterID);
        }

        public boolean equals(Object obj) {
            if (obj instanceof ChatterIDUser) {
                return super.equals(obj);
            }
            return false;
        }

        @Nonnull
        public ChatterType getChatterType() {
            return ChatterType.User;
        }

        @Nonnull
        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID();
        }

        @Nullable
        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID();
        }

        public Subscription getPictureID(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getAvatarProperties().getPool().subscribe(this.uuid, executor, new Subscription.OnData(onChatterPictureIDListener) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f145$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Object):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Object):void, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

                public final void onData(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.onData(java.lang.Object):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.onData(java.lang.Object):void, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/
            });
        }

        public /* bridge */ /* synthetic */ int hashCode() {
            return super.hashCode();
        }

        public /* bridge */ /* synthetic */ boolean isValidUUID() {
            return super.isValidUUID();
        }

        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle();
        }

        public /* bridge */ /* synthetic */ String toString() {
            return super.toString();
        }

        public /* bridge */ /* synthetic */ void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
        }
    }

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

        private ChatterIDWithUUID(@Nonnull UUID uuid2, @Nonnull UUID uuid3) {
            super(uuid2, (ChatterID) null);
            this.uuid = uuid3 == null ? UUIDPool.ZeroUUID : uuid3;
        }

        /* synthetic */ ChatterIDWithUUID(UUID uuid2, UUID uuid3, ChatterIDWithUUID chatterIDWithUUID) {
            this(uuid2, uuid3);
        }

        public int compareTo(@Nonnull ChatterID chatterID) {
            int compareTo = ChatterID.super.compareTo(chatterID);
            if (compareTo != 0) {
                return compareTo;
            }
            if (chatterID instanceof ChatterIDWithUUID) {
                return this.uuid.compareTo(((ChatterIDWithUUID) chatterID).uuid);
            }
            return 0;
        }

        public boolean equals(Object obj) {
            if (!ChatterID.super.equals(obj) || !(obj instanceof ChatterIDWithUUID)) {
                return false;
            }
            return Objects.equal(this.uuid, ((ChatterIDWithUUID) obj).uuid);
        }

        @Nonnull
        public UUID getChatterUUID() {
            return this.uuid;
        }

        @Nullable
        public UUID getOptionalChatterUUID() {
            return this.uuid;
        }

        public int hashCode() {
            return (this.uuid != null ? this.uuid.hashCode() : 0) + ChatterID.super.hashCode();
        }

        public boolean isValidUUID() {
            if (this.uuid != null) {
                return !UUIDPool.ZeroUUID.equals(this.uuid);
            }
            return false;
        }

        public Bundle toBundle() {
            Bundle bundle = ChatterID.super.toBundle();
            bundle.putString("chatterUUID", this.uuid != null ? this.uuid.toString() : UUIDPool.ZeroUUID.toString());
            return bundle;
        }

        public String toString() {
            return ChatterID.super.toString() + ":" + (this.uuid != null ? this.uuid.toString() : "null");
        }

        public void writeToParcel(Parcel parcel, int i) {
            ChatterID.super.writeToParcel(parcel, i);
            if (this.uuid != null) {
                parcel.writeLong(this.uuid.getMostSignificantBits());
                parcel.writeLong(this.uuid.getLeastSignificantBits());
                return;
            }
            parcel.writeLong(0);
            parcel.writeLong(0);
        }
    }

    public enum ChatterType {
        Local(NotificationType.LocalChat),
        User(NotificationType.Private),
        Group(NotificationType.Group);
        
        public static final ChatterType[] VALUES = null;
        @Nonnull
        private final NotificationType notificationType;

        static {
            VALUES = values();
        }

        private ChatterType(NotificationType notificationType2) {
            this.notificationType = notificationType2;
        }

        @Nonnull
        public NotificationType getNotificationType() {
            return this.notificationType;
        }
    }

    public interface OnChatterPictureIDListener {
        void onChatterPictureID(UUID uuid);
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues() {
        if (f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues != null) {
            return f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues;
        }
        int[] iArr = new int[ChatterType.values().length];
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
        f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = iArr;
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
        switch (m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[bundle.getInt("chatterType", 0)].ordinal()]) {
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
        switch (m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[chatter.getType()].ordinal()]) {
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
        return new ChatterIDGroup(uuid, uuid2, (ChatterIDGroup) null);
    }

    @Nonnull
    public static ChatterID getLocalChatterID(@Nonnull UUID uuid) {
        return new ChatterIDLocal(uuid, (ChatterIDLocal) null);
    }

    @Nonnull
    public static ChatterIDUser getUserChatterID(@Nonnull UUID uuid, @Nonnull UUID uuid2) {
        return new ChatterIDUser(uuid, uuid2, (ChatterIDUser) null);
    }

    public int compareTo(@Nonnull ChatterID chatterID) {
        int compareTo = this.agentUUID.compareTo(chatterID.agentUUID);
        return compareTo != 0 ? compareTo : getChatterType().compareTo(chatterID.getChatterType());
    }

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

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.agentUUID.getMostSignificantBits());
        parcel.writeLong(this.agentUUID.getLeastSignificantBits());
    }
}
