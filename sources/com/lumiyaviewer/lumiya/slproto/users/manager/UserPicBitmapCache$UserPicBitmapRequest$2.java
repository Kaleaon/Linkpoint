// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserPicBitmapCache, UserManager

class this._cls1
    implements Runnable
{

    final mpleteRequest this$1;

    public void run()
    {
        try
        {
            Bitmap bitmap = (new OpenJPEG(et0(this._cls1.this), 128, 128, false)).getAsBitmap();
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            bitmap.compress(android.graphics.Cache.UserPicBitmapRequest, 100, bytearrayoutputstream);
            byte abyte0[] = bytearrayoutputstream.toByteArray();
            Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", new Object[] {
                rap0(this._cls1.this), Integer.valueOf(abyte0.length)
            });
            UserPicBitmapCache._2D_get0(_fld0).setUserPic((UUID)rap0(this._cls1.this), abyte0);
            mpleteRequest(bitmap);
            return;
        }
        catch (IOException ioexception)
        {
            mpleteRequest(null);
        }
    }

    ()
    {
        this$1 = this._cls1.this;
        super();
    }
}
