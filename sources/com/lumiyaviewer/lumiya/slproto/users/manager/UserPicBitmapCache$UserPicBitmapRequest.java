// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserPicBitmapCache, UserManager

private class compressedFile extends ResourceRequest
    implements ResourceConsumer
{

    private volatile File compressedFile;
    private final Runnable decompressRunnable = new Runnable() {

        final UserPicBitmapCache.UserPicBitmapRequest this$1;

        public void run()
        {
            try
            {
                Bitmap bitmap = (new OpenJPEG(UserPicBitmapCache.UserPicBitmapRequest._2D_get0(UserPicBitmapCache.UserPicBitmapRequest.this), 128, 128, false)).getAsBitmap();
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                byte abyte0[] = bytearrayoutputstream.toByteArray();
                Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", new Object[] {
                    UserPicBitmapCache.UserPicBitmapRequest._2D_wrap0(UserPicBitmapCache.UserPicBitmapRequest.this), Integer.valueOf(abyte0.length)
                });
                UserPicBitmapCache._2D_get0(this$0).setUserPic((UUID)UserPicBitmapCache.UserPicBitmapRequest._2D_wrap0(UserPicBitmapCache.UserPicBitmapRequest.this), abyte0);
                completeRequest(bitmap);
                return;
            }
            catch (IOException ioexception)
            {
                completeRequest(null);
            }
        }

            
            {
                this$1 = UserPicBitmapCache.UserPicBitmapRequest.this;
                super();
            }
    };
    private volatile Future decompressorFuture;
    private final Runnable loadRunnable = new Runnable() {

        final UserPicBitmapCache.UserPicBitmapRequest this$1;

        public void run()
        {
            byte abyte0[] = UserPicBitmapCache._2D_get0(this$0).getUserPic((UUID)UserPicBitmapCache.UserPicBitmapRequest._2D_wrap0(UserPicBitmapCache.UserPicBitmapRequest.this));
            Object obj1 = UserPicBitmapCache.UserPicBitmapRequest._2D_wrap0(UserPicBitmapCache.UserPicBitmapRequest.this);
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
                completeRequest(obj);
                return;
            } else
            {
                TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID)UserPicBitmapCache.UserPicBitmapRequest._2D_wrap0(UserPicBitmapCache.UserPicBitmapRequest.this), TextureClass.Asset), UserPicBitmapCache.UserPicBitmapRequest.this);
                return;
            }
        }

            
            {
                this$1 = UserPicBitmapCache.UserPicBitmapRequest.this;
                super();
            }
    };
    private volatile Future loaderFuture;
    final UserPicBitmapCache this$0;

    static File _2D_get0(compressedFile compressedfile)
    {
        return compressedfile.compressedFile;
    }

    static Object _2D_wrap0(compressedFile compressedfile)
    {
        return compressedfile.getParams();
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        Object obj1 = getParams();
        String s;
        if (obj != null)
        {
            s = obj.toString();
        } else
        {
            s = "null";
        }
        Debug.Printf("UserPic: bitmap ID %s: got resource %s", new Object[] {
            obj1, s
        });
        if (obj instanceof File)
        {
            compressedFile = (File)obj;
            decompressorFuture = TextureCache.getInstance().getDecompressorExecutor().submit(decompressRunnable);
        } else
        if (obj == null)
        {
            completeRequest(null);
            return;
        }
    }

    public void cancelRequest()
    {
        Debug.Printf("DecompressRequest: cancelled (%s)", new Object[] {
            ((UUID)getParams()).toString()
        });
        Future future = decompressorFuture;
        if (future != null)
        {
            future.cancel(false);
        }
        future = loaderFuture;
        if (future != null)
        {
            future.cancel(false);
        }
        TextureCache.getInstance().getTextureCompressedCache().CancelRequest(this);
        super.cancelRequest();
    }

    public void execute()
    {
        Debug.Printf("UserPic: Requesting load for %s", new Object[] {
            getParams()
        });
        loaderFuture = LoaderExecutor.getInstance().submit(loadRunnable);
    }

    public _cls2.this._cls1(UUID uuid, ResourceManager resourcemanager)
    {
        this$0 = UserPicBitmapCache.this;
        super(uuid, resourcemanager);
        compressedFile = null;
    }
}
