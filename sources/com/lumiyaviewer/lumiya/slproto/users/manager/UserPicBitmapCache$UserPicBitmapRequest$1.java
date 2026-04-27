// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.graphics.BitmapFactory;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserPicBitmapCache, UserManager

class this._cls1
    implements Runnable
{

    final this._cls1 this$1;

    public void run()
    {
        byte abyte0[] = UserPicBitmapCache._2D_get0(_fld0).getUserPic((UUID)rap0(this._cls1.this));
        Object obj1 = rap0(this._cls1.this);
        Object obj;
        if (abyte0 != null)
        {
            obj = Integer.toString(abyte0.length);
        } else
        {
            obj = "null";
        }
        Debug.Printf("UserPic: bitmap ID %s: got bitmap data %s", new Object[] {
            obj1, obj
        });
        if (abyte0 != null)
        {
            obj = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
            mpleteRequest(obj);
            return;
        } else
        {
            TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID)rap0(this._cls1.this), TextureClass.Asset), this._cls1.this);
            return;
        }
    }

    ()
    {
        this$1 = this._cls1.this;
        super();
    }
}
