// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Optional;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectProfileData, PayInfo

final class AutoValue_SLObjectProfileData extends SLObjectProfileData
{

    private final Optional description;
    private final Optional floatingText;
    private final boolean isCopyable;
    private final boolean isDead;
    private final boolean isModifiable;
    private final boolean isPayable;
    private final boolean isTouchable;
    private final Optional name;
    private final UUID objectUUID;
    private final UUID ownerUUID;
    private final PayInfo payInfo;
    private final int salePrice;
    private final byte saleType;
    private final String touchName;

    AutoValue_SLObjectProfileData(UUID uuid, Optional optional, Optional optional1, UUID uuid1, boolean flag, String s, boolean flag1, 
            byte byte0, int i, boolean flag2, boolean flag3, Optional optional2, PayInfo payinfo, boolean flag4)
    {
        objectUUID = uuid;
        if (optional == null)
        {
            throw new NullPointerException("Null name");
        }
        name = optional;
        if (optional1 == null)
        {
            throw new NullPointerException("Null description");
        }
        description = optional1;
        ownerUUID = uuid1;
        isTouchable = flag;
        touchName = s;
        isPayable = flag1;
        saleType = byte0;
        salePrice = i;
        isCopyable = flag2;
        isDead = flag3;
        if (optional2 == null)
        {
            throw new NullPointerException("Null floatingText");
        } else
        {
            floatingText = optional2;
            payInfo = payinfo;
            isModifiable = flag4;
            return;
        }
    }

    public Optional description()
    {
        return description;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SLObjectProfileData)
        {
            obj = (SLObjectProfileData)obj;
            if ((objectUUID != null ? objectUUID.equals(((SLObjectProfileData) (obj)).objectUUID()) : ((SLObjectProfileData) (obj)).objectUUID() == null) && (name.equals(((SLObjectProfileData) (obj)).name()) && description.equals(((SLObjectProfileData) (obj)).description())) && (ownerUUID != null ? ownerUUID.equals(((SLObjectProfileData) (obj)).ownerUUID()) : ((SLObjectProfileData) (obj)).ownerUUID() == null) && isTouchable == ((SLObjectProfileData) (obj)).isTouchable() && (touchName != null ? touchName.equals(((SLObjectProfileData) (obj)).touchName()) : ((SLObjectProfileData) (obj)).touchName() == null) && (isPayable == ((SLObjectProfileData) (obj)).isPayable() && saleType == ((SLObjectProfileData) (obj)).saleType() && salePrice == ((SLObjectProfileData) (obj)).salePrice() && isCopyable == ((SLObjectProfileData) (obj)).isCopyable() && isDead == ((SLObjectProfileData) (obj)).isDead() && floatingText.equals(((SLObjectProfileData) (obj)).floatingText())) && (payInfo != null ? payInfo.equals(((SLObjectProfileData) (obj)).payInfo()) : ((SLObjectProfileData) (obj)).payInfo() == null))
            {
                return isModifiable == ((SLObjectProfileData) (obj)).isModifiable();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public Optional floatingText()
    {
        return floatingText;
    }

    public int hashCode()
    {
        int l = 0;
        char c4 = '\u04CF';
        int i;
        int j;
        char c;
        int k;
        char c1;
        char c2;
        char c3;
        int i1;
        int j1;
        byte byte0;
        int k1;
        int l1;
        if (objectUUID == null)
        {
            i = 0;
        } else
        {
            i = objectUUID.hashCode();
        }
        i1 = name.hashCode();
        j1 = description.hashCode();
        if (ownerUUID == null)
        {
            j = 0;
        } else
        {
            j = ownerUUID.hashCode();
        }
        if (isTouchable)
        {
            c = '\u04CF';
        } else
        {
            c = '\u04D5';
        }
        if (touchName == null)
        {
            k = 0;
        } else
        {
            k = touchName.hashCode();
        }
        if (isPayable)
        {
            c1 = '\u04CF';
        } else
        {
            c1 = '\u04D5';
        }
        byte0 = saleType;
        k1 = salePrice;
        if (isCopyable)
        {
            c2 = '\u04CF';
        } else
        {
            c2 = '\u04D5';
        }
        if (isDead)
        {
            c3 = '\u04CF';
        } else
        {
            c3 = '\u04D5';
        }
        l1 = floatingText.hashCode();
        if (payInfo != null)
        {
            l = payInfo.hashCode();
        }
        if (!isModifiable)
        {
            c4 = '\u04D5';
        }
        return (((c3 ^ (c2 ^ (((c1 ^ (k ^ (c ^ (j ^ (((i ^ 0xf4243) * 0xf4243 ^ i1) * 0xf4243 ^ j1) * 0xf4243) * 0xf4243) * 0xf4243) * 0xf4243) * 0xf4243 ^ byte0) * 0xf4243 ^ k1) * 0xf4243) * 0xf4243) * 0xf4243 ^ l1) * 0xf4243 ^ l) * 0xf4243 ^ c4;
    }

    public boolean isCopyable()
    {
        return isCopyable;
    }

    public boolean isDead()
    {
        return isDead;
    }

    public boolean isModifiable()
    {
        return isModifiable;
    }

    public boolean isPayable()
    {
        return isPayable;
    }

    public boolean isTouchable()
    {
        return isTouchable;
    }

    public Optional name()
    {
        return name;
    }

    public UUID objectUUID()
    {
        return objectUUID;
    }

    public UUID ownerUUID()
    {
        return ownerUUID;
    }

    public PayInfo payInfo()
    {
        return payInfo;
    }

    public int salePrice()
    {
        return salePrice;
    }

    public byte saleType()
    {
        return saleType;
    }

    public String toString()
    {
        return (new StringBuilder()).append("SLObjectProfileData{objectUUID=").append(objectUUID).append(", ").append("name=").append(name).append(", ").append("description=").append(description).append(", ").append("ownerUUID=").append(ownerUUID).append(", ").append("isTouchable=").append(isTouchable).append(", ").append("touchName=").append(touchName).append(", ").append("isPayable=").append(isPayable).append(", ").append("saleType=").append(saleType).append(", ").append("salePrice=").append(salePrice).append(", ").append("isCopyable=").append(isCopyable).append(", ").append("isDead=").append(isDead).append(", ").append("floatingText=").append(floatingText).append(", ").append("payInfo=").append(payInfo).append(", ").append("isModifiable=").append(isModifiable).append("}").toString();
    }

    public String touchName()
    {
        return touchName;
    }
}
