package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
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
        for (int i = 0; i < avatarAppearance.AppearanceData_Fields.size(); i++) {
            Debug.Printf("appData[%d]: appVer %d, cofVer %d, flags 0x%x", Integer.valueOf(i), Integer.valueOf(avatarAppearance.AppearanceData_Fields.get(i).AppearanceVersion), Integer.valueOf(avatarAppearance.AppearanceData_Fields.get(i).CofVersion), Integer.valueOf(avatarAppearance.AppearanceData_Fields.get(i).Flags));
        }
        int[] iArr = new int[218];
        for (int i2 = 0; i2 < 218; i2++) {
            if (i2 < avatarAppearance.VisualParam_Fields.size()) {
                iArr[i2] = avatarAppearance.VisualParam_Fields.get(i2).ParamValue;
            } else {
                iArr[i2] = avatarShapeParams != null ? avatarShapeParams.visualParamValues[i2] : 0;
            }
        }
        return new AvatarShapeParams(iArr);
    }

    @Nonnull
    public static AvatarShapeParams create(@Nullable AvatarShapeParams avatarShapeParams, int[] iArr) {
        if (iArr.length != 218) {
            int[] iArr2 = new int[218];
            System.arraycopy(iArr, 0, iArr2, 0, Math.min(iArr.length, 218));
            if (iArr.length >= 218 || avatarShapeParams == null) {
                iArr = iArr2;
            } else {
                System.arraycopy(avatarShapeParams.visualParamValues, iArr.length, iArr2, iArr.length, 218 - iArr.length);
                iArr = iArr2;
            }
        }
        return new AvatarShapeParams(iArr);
    }

    public boolean equals(Object obj) {
        if (obj instanceof AvatarShapeParams) {
            return Arrays.equals(this.visualParamValues, ((AvatarShapeParams) obj).visualParamValues);
        }
        return false;
    }

    public int getParamCount() {
        return 218;
    }

    public int getParamValue(int i) {
        if (i < 0 || i >= 218) {
            return 0;
        }
        return this.visualParamValues[i];
    }

    public int hashCode() {
        return Arrays.hashCode(this.visualParamValues);
    }
}
