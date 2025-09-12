// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            AutoValue_SLObjectFilterInfo, SLObjectInfo

public abstract class SLObjectFilterInfo
{

    public SLObjectFilterInfo()
    {
    }

    public static SLObjectFilterInfo create()
    {
        return new AutoValue_SLObjectFilterInfo("", false, false, false, 0.0F);
    }

    public static SLObjectFilterInfo create(String s, boolean flag, boolean flag1, boolean flag2, float f)
    {
        return new AutoValue_SLObjectFilterInfo(s, flag, flag1, flag2, f);
    }

    public abstract String filterText();

    public boolean nameMatches(String s)
    {
        if (s != null)
        {
            String s1 = filterText();
            if (s1.length() != 0 && !s.toLowerCase().contains(s1.toLowerCase()))
            {
                return false;
            }
            return showNonDescriptive() || !s.equals("Object") && !s.equals("(loading)") && !s.equals("");
        } else
        {
            return false;
        }
    }

    public boolean objectMatches(SLObjectInfo slobjectinfo, float f, boolean flag)
    {
        if (flag && showAttachments() ^ true)
        {
            return false;
        }
        if (!showNonTouchable() && !slobjectinfo.isTouchable())
        {
            return false;
        }
        if (range() > 0.0F)
        {
            if (Float.isNaN(f))
            {
                return false;
            }
            if (f > range())
            {
                return false;
            }
        }
        return true;
    }

    public abstract float range();

    public abstract boolean showAttachments();

    public abstract boolean showNonDescriptive();

    public abstract boolean showNonTouchable();
}
