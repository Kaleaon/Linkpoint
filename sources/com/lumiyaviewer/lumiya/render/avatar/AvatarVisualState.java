package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AvatarVisualState {
    private static final ImmutableSet<UUID> basicAnimations = new ImmutableSet.Builder().add((Object) UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")).add((Object) UUID.fromString("15468e00-3400-bb66-cecc-646d7c14458e")).add((Object) UUID.fromString("370f3a20-6ca6-9971-848c-9a01bc42ae3c")).add((Object) UUID.fromString("42b46214-4b44-79ae-deb8-0df61424ff4b")).add((Object) UUID.fromString("f22fed8b-a5ed-2c93-64d5-bdd8b93c889f")).add((Object) UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c")).add((Object) UUID.fromString("47f5f6fb-22e5-ae44-f871-73aaaf4a6022")).add((Object) UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")).add((Object) UUID.fromString("2b5a38b2-5e00-3a97-a495-4c826bc443e6")).add((Object) UUID.fromString("4ae8016b-31b9-03bb-c401-b1ea941db41d")).add((Object) UUID.fromString("20f063ea-8306-2562-0b07-5c853b37b31e")).add((Object) UUID.fromString("62c5de58-cb33-5743-3d07-9e4cd4352864")).add((Object) UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")).add((Object) UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")).add((Object) UUID.fromString("f5fc7433-043d-e819-8298-f519a119b688")).build();
    private static final UUID defaultStandingAnimation = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
    private final UUID agentUUID;
    private final Map<UUID, AnimationSequenceInfo> animations = new ConcurrentHashMap();
    private final SLObjectAvatarInfo avatarObject;
    private volatile AvatarShapeParams avatarShapeParams;
    private final UUID avatarUUID;
    private final AvatarTextures textures = new AvatarTextures();

    public AvatarVisualState(UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo, UUID uuid2) {
        this.agentUUID = uuid;
        this.avatarObject = sLObjectAvatarInfo;
        this.avatarUUID = uuid2;
    }

    private void startAnimation(UUID uuid, int i, long j, DrawableAvatar drawableAvatar) {
        AnimationSequenceInfo animationSequenceInfo;
        boolean z;
        AnimationSequenceInfo animationSequenceInfo2 = this.animations.get(uuid);
        if (animationSequenceInfo2 == null) {
            Debug.Printf("Anim: Starting new animation %s seqID %d", uuid.toString(), Integer.valueOf(i));
            AnimationSequenceInfo newSequence = AnimationSequenceInfo.newSequence(uuid, j, i);
            this.animations.put(uuid, newSequence);
            animationSequenceInfo = newSequence;
            z = true;
        } else if (i != animationSequenceInfo2.sequenceID) {
            AnimationSequenceInfo restartSequence = AnimationSequenceInfo.restartSequence(j, i, animationSequenceInfo2);
            this.animations.put(uuid, restartSequence);
            animationSequenceInfo = restartSequence;
            z = true;
        } else {
            animationSequenceInfo = animationSequenceInfo2;
            z = false;
        }
        if (z && drawableAvatar != null) {
            drawableAvatar.AnimationUpdate(animationSequenceInfo);
        }
    }

    private synchronized void updateAvatarShape() {
        DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
        if (drawableAvatar != null) {
            drawableAvatar.UpdateShapeParams(this.avatarShapeParams);
        }
    }

    private synchronized void updateTextures() {
        DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
        if (drawableAvatar != null) {
            drawableAvatar.UpdateTextures(this.textures);
        }
    }

    public synchronized void ApplyAvatarAnimation(AvatarAnimation avatarAnimation) {
        boolean z;
        AnimationSequenceInfo animationSequenceInfo;
        boolean z2;
        long currentTimeMillis = System.currentTimeMillis();
        HashSet hashSet = new HashSet();
        HashSet<UUID> hashSet2 = new HashSet<>();
        hashSet2.addAll(this.animations.keySet());
        DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
        for (AvatarAnimation.AnimationList animationList : avatarAnimation.AnimationList_Fields) {
            UUID uuid = animationList.AnimID;
            hashSet.add(uuid);
            hashSet2.remove(uuid);
            startAnimation(uuid, animationList.AnimSequenceID, currentTimeMillis, drawableAvatar);
        }
        if (Collections.disjoint(hashSet, basicAnimations)) {
            hashSet2.remove(defaultStandingAnimation);
            startAnimation(defaultStandingAnimation, 1, currentTimeMillis, drawableAvatar);
        }
        for (UUID uuid2 : hashSet2) {
            AnimationSequenceInfo animationSequenceInfo2 = this.animations.get(uuid2);
            if (animationSequenceInfo2 != null) {
                if (animationSequenceInfo2.sequenceID != 0) {
                    AnimationSequenceInfo stopSequence = AnimationSequenceInfo.stopSequence(currentTimeMillis, animationSequenceInfo2);
                    if (stopSequence != null) {
                        this.animations.put(uuid2, stopSequence);
                        z = true;
                        animationSequenceInfo = stopSequence;
                        z2 = false;
                    } else {
                        z = true;
                        animationSequenceInfo = stopSequence;
                        z2 = true;
                    }
                } else {
                    z = false;
                    animationSequenceInfo = animationSequenceInfo2;
                    z2 = false;
                }
                if (animationSequenceInfo != null) {
                    z2 |= animationSequenceInfo.hasStopped(currentTimeMillis);
                }
                if (drawableAvatar != null) {
                    if (z && (!z2) && animationSequenceInfo != null) {
                        drawableAvatar.AnimationUpdate(animationSequenceInfo);
                    }
                    z2 |= drawableAvatar.IsAnimationStopped(uuid2);
                }
                if (z2) {
                    Debug.Printf("Anim: Stopping animation %s", uuid2.toString());
                    this.animations.remove(uuid2);
                    if (drawableAvatar != null) {
                        drawableAvatar.AnimationRemove(uuid2);
                    }
                }
            }
        }
    }

    public synchronized void ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        AvatarShapeParams avatarShapeParams2 = this.avatarShapeParams;
        this.avatarShapeParams = AvatarShapeParams.create(avatarShapeParams2, avatarAppearance);
        if (!this.avatarShapeParams.equals(avatarShapeParams2)) {
            updateAvatarShape();
        }
        if (this.textures.ApplyAvatarAppearance(avatarAppearance)) {
            updateTextures();
        }
    }

    public synchronized void ApplyTextures(SLTextureEntry sLTextureEntry, boolean z) {
        if (this.textures.ApplyTextures(sLTextureEntry, z)) {
            updateTextures();
        }
    }

    public synchronized void ApplyVisualParams(int[] iArr) {
        AvatarShapeParams avatarShapeParams2 = this.avatarShapeParams;
        this.avatarShapeParams = AvatarShapeParams.create(avatarShapeParams2, iArr);
        if (!this.avatarShapeParams.equals(avatarShapeParams2)) {
            updateAvatarShape();
        }
    }

    public synchronized DrawableAvatar createDrawableAvatar(DrawableStore drawableStore) {
        DrawableAvatar drawableAvatar;
        drawableAvatar = new DrawableAvatar(drawableStore, this.agentUUID, this.avatarObject, this.avatarUUID, this.animations);
        drawableAvatar.UpdateShapeParams(this.avatarShapeParams);
        drawableAvatar.UpdateTextures(this.textures);
        return drawableAvatar;
    }

    public synchronized DrawableAvatarStub createDrawableAvatarStub(DrawableStore drawableStore) {
        return new DrawableAvatarStub(drawableStore, this.agentUUID, this.avatarObject);
    }

    public synchronized Set<UUID> getRunningAnimations() {
        return this.animations.keySet();
    }
}
