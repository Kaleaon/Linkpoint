// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectInfo, HoverText, AutoValue_SLObjectProfileData, PayInfo

public abstract class SLObjectProfileData
{

    public SLObjectProfileData()
    {
    }

    public static SLObjectProfileData create(SLObjectInfo slobjectinfo)
    {
        Object obj = slobjectinfo.getHoverText();
        byte byte0;
        Optional optional;
        UUID uuid;
        Optional optional1;
        UUID uuid1;
        String s;
        PayInfo payinfo;
        int i;
        boolean flag;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        if (obj != null)
        {
            obj = Strings.emptyToNull(((HoverText) (obj)).text());
        } else
        {
            obj = null;
        }
        uuid = slobjectinfo.getId();
        if (slobjectinfo.nameKnown)
        {
            optional = Optional.of(Strings.nullToEmpty(slobjectinfo.name));
        } else
        {
            optional = Optional.absent();
        }
        optional1 = Optional.fromNullable(slobjectinfo.getDescription());
        uuid1 = slobjectinfo.getOwnerUUID();
        flag2 = slobjectinfo.isTouchable();
        s = slobjectinfo.getTouchName();
        flag3 = slobjectinfo.isPayable();
        byte0 = slobjectinfo.saleType;
        i = slobjectinfo.salePrice;
        if ((slobjectinfo.UpdateFlags & 8) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        flag4 = slobjectinfo.isDead;
        obj = Optional.fromNullable(obj);
        payinfo = slobjectinfo.getPayInfo();
        if ((slobjectinfo.UpdateFlags & 4) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        return new AutoValue_SLObjectProfileData(uuid, optional, optional1, uuid1, flag2, s, flag3, byte0, i, flag, flag4, ((Optional) (obj)), payinfo, flag1);
    }

    public abstract Optional description();

    public abstract Optional floatingText();

    public abstract boolean isCopyable();

    public abstract boolean isDead();

    public abstract boolean isModifiable();

    public abstract boolean isPayable();

    public abstract boolean isTouchable();

    public abstract Optional name();

    public abstract UUID objectUUID();

    public abstract UUID ownerUUID();

    public abstract PayInfo payInfo();

    public abstract int salePrice();

    public abstract byte saleType();

    public abstract String touchName();
}
