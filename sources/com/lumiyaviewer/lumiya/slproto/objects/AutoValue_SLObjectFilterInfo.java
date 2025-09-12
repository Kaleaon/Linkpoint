// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectFilterInfo

final class AutoValue_SLObjectFilterInfo extends SLObjectFilterInfo
{

    private final String filterText;
    private final float range;
    private final boolean showAttachments;
    private final boolean showNonDescriptive;
    private final boolean showNonTouchable;

    AutoValue_SLObjectFilterInfo(String s, boolean flag, boolean flag1, boolean flag2, float f)
    {
        if (s == null)
        {
            throw new NullPointerException("Null filterText");
        } else
        {
            filterText = s;
            showAttachments = flag;
            showNonDescriptive = flag1;
            showNonTouchable = flag2;
            range = f;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SLObjectFilterInfo)
        {
            obj = (SLObjectFilterInfo)obj;
            if (filterText.equals(((SLObjectFilterInfo) (obj)).filterText()) && showAttachments == ((SLObjectFilterInfo) (obj)).showAttachments() && showNonDescriptive == ((SLObjectFilterInfo) (obj)).showNonDescriptive() && showNonTouchable == ((SLObjectFilterInfo) (obj)).showNonTouchable())
            {
                return Float.floatToIntBits(range) == Float.floatToIntBits(((SLObjectFilterInfo) (obj)).range());
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public String filterText()
    {
        return filterText;
    }

    public int hashCode()
    {
        char c2 = '\u04CF';
        int i = filterText.hashCode();
        char c;
        char c1;
        if (showAttachments)
        {
            c = '\u04CF';
        } else
        {
            c = '\u04D5';
        }
        if (showNonDescriptive)
        {
            c1 = '\u04CF';
        } else
        {
            c1 = '\u04D5';
        }
        if (!showNonTouchable)
        {
            c2 = '\u04D5';
        }
        return ((c1 ^ (c ^ (i ^ 0xf4243) * 0xf4243) * 0xf4243) * 0xf4243 ^ c2) * 0xf4243 ^ Float.floatToIntBits(range);
    }

    public float range()
    {
        return range;
    }

    public boolean showAttachments()
    {
        return showAttachments;
    }

    public boolean showNonDescriptive()
    {
        return showNonDescriptive;
    }

    public boolean showNonTouchable()
    {
        return showNonTouchable;
    }

    public String toString()
    {
        return (new StringBuilder()).append("SLObjectFilterInfo{filterText=").append(filterText).append(", ").append("showAttachments=").append(showAttachments).append(", ").append("showNonDescriptive=").append(showNonDescriptive).append(", ").append("showNonTouchable=").append(showNonTouchable).append(", ").append("range=").append(range).append("}").toString();
    }
}
