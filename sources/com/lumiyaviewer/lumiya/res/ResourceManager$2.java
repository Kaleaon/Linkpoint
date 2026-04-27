// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.CacheLoader;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceManager, ResourceRequest

class this._cls0 extends CacheLoader
{

    final ResourceManager this$0;

    public ResourceRequest load(Object obj)
        throws Exception
    {
        return CreateNewRequest(obj, ResourceManager.this);
    }

    public volatile Object load(Object obj)
        throws Exception
    {
        return load(obj);
    }

    ()
    {
        this$0 = ResourceManager.this;
        super();
    }
}
