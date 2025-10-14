package com.lumiyaviewer.lumiya.slproto.users.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Future

class UserPicBitmapCache : ResourceMemoryCache<UUID, Bitmap> {
    private Int MAX_USERPIC_HEIGHT = 128
    private Int MAX_USERPIC_WIDTH = 128
    /* access modifiers changed from: private */
    UserManager userManager

    private class UserPicBitmapRequest : ResourceRequest<UUID, Bitmap> : ResourceConsumer {
        /* access modifiers changed from: private */
        volatile File compressedFile = null
        private Runnable decompressRunnable = Runnable() {
            Unit run() {
                try {
                    Bitmap asBitmap = OpenJPEG(UserPicBitmapRequest.this.compressedFile, 128, 128, false).getAsBitmap()
                    ByteArrayOutputStream byteArrayOutputStream = ByteArrayOutputStream()
                    asBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    Byte[] byteArray = byteArrayOutputStream.toByteArray()
                    Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", UserPicBitmapRequest.this.getParams(), Int.valueOf(byteArray.length))
                    UserPicBitmapCache.this.userManager.setUserPic((UUID) UserPicBitmapRequest.this.getParams(), byteArray)
                    UserPicBitmapRequest.this.completeRequest(asBitmap)
                } catch (IOException e) {
                    UserPicBitmapRequest.this.completeRequest(null)
                }
            }
        }
        private volatile Future<?> decompressorFuture
        private Runnable loadRunnable = Runnable() {
            Unit run() {
                Byte[] userPic = UserPicBitmapCache.this.userManager.getUserPic((UUID) UserPicBitmapRequest.this.getParams())
                Any[] objArr = Any[2]
                objArr[0] = UserPicBitmapRequest.this.getParams()
                objArr[1] = userPic != null ? Int.toString(userPic.length) : "null"
                Debug.Printf("UserPic: bitmap ID %s: got bitmap data %s", objArr)
                if (userPic != null) {
                    UserPicBitmapRequest.this.completeRequest(BitmapFactory.decodeByteArray(userPic, 0, userPic.length))
                } else {
                    TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID) UserPicBitmapRequest.this.getParams(), TextureClass.Asset), (ResourceConsumer) UserPicBitmapRequest.this)
                }
            }
        }
        private volatile Future<?> loaderFuture

        UserPicBitmapRequest(UUID uuid, ResourceManager<UUID, Bitmap> resourceManager) {
            super(uuid, resourceManager)
        }

        Unit OnResourceReady(Any obj, Boolean z) {
            Any[] objArr = Any[2]
            objArr[0] = getParams()
            objArr[1] = obj != null ? obj.toString() : "null"
            Debug.Printf("UserPic: bitmap ID %s: got resource %s", objArr)
            if (obj instanceof File) {
                this.compressedFile = (File) obj
                this.decompressorFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this.decompressRunnable)
            } else if (obj == null) {
                completeRequest(null)
            }
        }

        Unit cancelRequest() {
            Debug.Printf("DecompressRequest: cancelled (%s)", ((UUID) getParams()).toString())
            Future<?> future = this.decompressorFuture
            if (future != null) {
                future.cancel(false)
            }
            Future<?> future2 = this.loaderFuture
            if (future2 != null) {
                future2.cancel(false)
            }
            TextureCache.getInstance().getTextureCompressedCache().CancelRequest(this)
            super.cancelRequest()
        }

        Unit execute() {
            Debug.Printf("UserPic: Requesting load for %s", getParams())
            this.loaderFuture = LoaderExecutor.getInstance().submit(this.loadRunnable)
        }
    }

    UserPicBitmapCache(UserManager userManager2) {
        this.userManager = userManager2
    }

    /* access modifiers changed from: protected */
    ResourceRequest<UUID, Bitmap> CreateNewRequest(UUID uuid, ResourceManager<UUID, Bitmap> resourceManager) {
        return UserPicBitmapRequest(uuid, resourceManager)
    }
}
