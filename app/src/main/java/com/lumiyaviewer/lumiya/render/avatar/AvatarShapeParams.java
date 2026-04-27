package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.AppearanceData;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.VisualParam;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AvatarShapeParams {
    @Nonnull
    private final int[] visualParamValues;

    private AvatarShapeParams(@Nonnull int[] iArr) {
        this.visualParamValues = iArr;
    }

    @Nonnull
    public static AvatarShapeParams create(@Nullable AvatarShapeParams avatarShapeParams, AvatarAppearance avatarAppearance) {
        Debug.Log("DrawableAvatar: new appearance for avatar " + avatarAppearance.Sender_Field.ID + ", numParams = " + avatarAppearance.VisualParam_Fields.size() + ", appData = " + avatarAppearance.AppearanceData_Fields.size());
        for (i = 0; i < avatarAppearance.AppearanceData_Fields.size(); i++) {
            Debug.Printf("appData[%d]: appVer %d, cofVer %d, flags 0x%x", Integer.valueOf(i), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).AppearanceVersion), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).CofVersion), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).Flags));
        }
        int[] iArr = new int[218];
        i = 0;
        while (i < 218) {
            if (i < avatarAppearance.VisualParam_Fields.size()) {
                iArr[i] = ((VisualParam) avatarAppearance.VisualParam_Fields.get(i)).ParamValue;
            } else {
                iArr[i] = avatarShapeParams != null ? avatarShapeParams.visualParamValues[i] : 0;
            }
            i++;
        }
        return new AvatarShapeParams(iArr);
    }

    @Nonnull
    public static AvatarShapeParams create(@Nullable AvatarShapeParams avatarShapeParams, int[] iArr) {
        if (iArr.length != 218) {
            Object obj = new int[218];
            System.arraycopy(iArr, 0, obj, 0, Math.min(iArr.length, 218));
            Object iArr2;
            if (iArr2.length >= 218 || avatarShapeParams == null) {
                iArr2 = obj;
            } else {
                System.arraycopy(avatarShapeParams.visualParamValues, iArr2.length, obj, iArr2.length, 218 - iArr2.length);
                iArr2 = obj;
            }
        }
        return new AvatarShapeParams(iArr2);
    }

    public boolean equals(Object obj) {
        return obj instanceof AvatarShapeParams ? Arrays.equals(this.visualParamValues, ((AvatarShapeParams) obj).visualParamValues) : false;
    }

    public int getParamCount() {
        return 218;
    }

    public int getParamValue(int i) {
        return (i < 0 || i >= 218) ? 0 : this.visualParamValues[i];
    }

    public int hashCode() {
        return Arrays.hashCode(this.visualParamValues);
    }
}
