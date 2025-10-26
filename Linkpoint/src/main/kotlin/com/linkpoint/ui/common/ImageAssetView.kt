package com.linkpoint.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class ImageAssetView : View() {
    private Boolean alignTop = false
    private UUID assetID
    private val Rect bitmapDestRect = Rect()
    private val Paint bitmapPaint = Paint()
    private val Rect bitmapSrcRect = Rect()
    /* access modifiers changed from: private */
    public Bitmap imageBitmap
    /* access modifiers changed from: private */
    public LoadAssetImageTask loadTask
    private val Paint textPaint = Paint()
    /* access modifiers changed from: private */
    public Boolean verticalFit = false

    private class LoadAssetImageTask : AsyncTask()<UUID, Void, Bitmap> : ResourceConsumer {
        private volatile OpenJPEG texture
        private val Object textureReady

        private LoadAssetImageTask() {
            this.textureReady = Object()
        }

        /* synthetic */ LoadAssetImageTask(ImageAssetView imageAssetView, LoadAssetImageTask loadAssetImageTask) {
            this()
        }

        fun OnResourceReady(obj: Object, z: Boolean) {
            if (obj instanceof OpenJPEG) {
                this.texture = (OpenJPEG) obj
            }
            synchronized (this.textureReady) {
                this.textureReady.notify()
            }
        }

        /* access modifiers changed from: protected */
         public override fun doInBackground(vararg uuidArr: UUID): Bitmap {
            Debug.Printf("loading asset ID %s", uuidArr[0].toString())
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuidArr[0], TextureClass.Asset), this)
            synchronized (this.textureReady) {
                if (this.texture == null) {
                    Debug.Printf("asset ID %s is not available, waiting", uuidArr[0].toString())
                    try {
                        this.textureReady.wait()
                        Debug.Printf("done waiting for asset ID %s", uuidArr[0].toString())
                    } catch (InterruptedException e) {
                        Debug.Printf("interrupted while waiting for asset ID %s", uuidArr[0].toString())
                        return null
                    }
                } else {
                    Debug.Printf("asset ID %s is already available", uuidArr[0].toString())
                }
            }
            if (this.texture != null) {
                return this.texture.getAsBitmap()
            }
            return null
        }

        /* access modifiers changed from: protected */
        override fun onPostExecute(bitmap: Bitmap) {
            val unused: Bitmap = ImageAssetView.this.imageBitmap = bitmap
            if (ImageAssetView.this.verticalFit) {
                ImageAssetView.this.requestLayout()
            }
            ImageAssetView.this.invalidate()
            val unused2: LoadAssetImageTask = ImageAssetView.this.loadTask = null
        }
    }

    public ImageAssetView(Context context) {
        super(context)
    }

    public ImageAssetView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    public ImageAssetView(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    /* access modifiers changed from: protected */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val displayMetrics: DisplayMetrics = getResources().getDisplayMetrics()
        val typedValue: TypedValue = TypedValue()
        getContext().getTheme().resolveAttribute(R.attr.chatBubbleText, typedValue, true)
        val i: Int = typedValue.data
        this.textPaint.setStyle(Paint.Style.STROKE)
        this.textPaint.setColor(i)
        this.textPaint.setTextAlign(Paint.Align.CENTER)
        this.textPaint.setAntiAlias(true)
        this.textPaint.setTextSize(TypedValue.applyDimension(2, 14.0f, displayMetrics))
    }

    /* access modifiers changed from: protected */
    override fun onDraw(canvas: Canvas) {
        val width: Int = getWidth()
        val height: Int = getHeight()
        this.bitmapPaint.setStyle(Paint.Style.STROKE)
        this.bitmapPaint.setARGB(255, 192, 192, 192)
        this.bitmapPaint.setTextAlign(Paint.Align.CENTER)
        if (this.imageBitmap == null || width == 0 || height == 0) {
            canvas.drawARGB(50, 0, 0, 0)
            val str: String = (this.assetID == null || UUIDPool.ZeroUUID.equals(this.assetID)) ? "No image" : this.loadTask == null ? "Failed to load" : this.loadTask.getStatus() == AsyncTask.Status.FINISHED ? "Failed to load" : "Loading..."
            this.textPaint.getTextBounds(str, 0, str.length(), this.bitmapSrcRect)
            canvas.drawText(str, ((Float) width) / 2.0f, (((Float) height) / 2.0f) + (((Float) this.bitmapSrcRect.height()) / 2.0f), this.textPaint)
            return
        }
        val width2: Int = this.imageBitmap.getWidth()
        val height2: Int = this.imageBitmap.getHeight()
        val max: Float = Math.max(((Float) width2) / ((Float) width), ((Float) height2) / ((Float) height))
        val round: Int = Math.round(((Float) width2) / max)
        val round2: Int = Math.round(((Float) height2) / max)
        val i: Int = (width / 2) - (round / 2)
        val i2: Int = this.alignTop ? 0 : (height / 2) - (round2 / 2)
        this.bitmapDestRect.left = i + 1
        this.bitmapDestRect.top = i2 + 1
        this.bitmapDestRect.right = (round + i) - 1
        this.bitmapDestRect.bottom = (i2 + round2) - 1
        if (this.bitmapDestRect.left < 1) {
            this.bitmapDestRect.left = 1
        }
        if (this.bitmapDestRect.top < 1) {
            this.bitmapDestRect.top = 1
        }
        if (this.bitmapDestRect.right > width - 1) {
            this.bitmapDestRect.right = width - 1
        }
        if (this.bitmapDestRect.bottom > height - 1) {
            this.bitmapDestRect.bottom = height - 1
        }
        this.bitmapSrcRect.left = 0
        this.bitmapSrcRect.top = 0
        this.bitmapSrcRect.right = width2
        this.bitmapSrcRect.bottom = height2
        canvas.drawBitmap(this.imageBitmap, this.bitmapSrcRect, this.bitmapDestRect, this.bitmapPaint)
        val rect: Rect = this.bitmapDestRect
        rect.left--
        val rect2: Rect = this.bitmapDestRect
        rect2.top--
        canvas.drawRect(this.bitmapDestRect, this.bitmapPaint)
    }

    /* access modifiers changed from: protected */
    override fun onMeasure(i: Int, i2: Int) {
        if (View.MeasureSpec.getMode(i) == 0 && View.MeasureSpec.getMode(i2) == 0) {
            super.onMeasure(i, i2)
            return
        }
        val min: Int = Math.min(View.MeasureSpec.getMode(i2) != 0 ? View.MeasureSpec.getSize(i2) : Integer.MAX_VALUE, View.MeasureSpec.getMode(i) != 0 ? View.MeasureSpec.getSize(i) : Integer.MAX_VALUE)
        val size: Int = View.MeasureSpec.getMode(i) == 1073741824 ? View.MeasureSpec.getSize(i) : min
        if (View.MeasureSpec.getMode(i2) == 1073741824) {
            min = View.MeasureSpec.getSize(i2)
        }
        if (this.verticalFit && this.imageBitmap != null && size != Integer.MAX_VALUE && size > 0) {
            min = (this.imageBitmap.getHeight() * size) / this.imageBitmap.getWidth()
        }
        setMeasuredDimension(size, min)
    }

    fun setAlignTop(z: Boolean) {
        this.alignTop = z
        invalidate()
    }

    fun setAssetID(uuid: UUID) {
        val objArr: Array<Any> = Object[1]
        objArr[0] = uuid != null ? uuid.toString() : null
        Debug.Printf("asset ID: %s", objArr)
        if (uuid != null && uuid.equals(UUIDPool.ZeroUUID)) {
            uuid = null
        }
        if (!Objects.equal(this.assetID, uuid)) {
            if (this.loadTask != null) {
                this.loadTask.cancel(true)
                this.loadTask = null
            }
            this.assetID = uuid
            if (this.imageBitmap != null) {
                this.imageBitmap.recycle()
            }
            this.imageBitmap = null
            if (this.assetID != null) {
                Debug.Printf("requested to view asset ID %s", uuid)
                this.loadTask = LoadAssetImageTask(this, (LoadAssetImageTask) null)
                this.loadTask.execute(Array<UUID>{uuid})
            }
            invalidate()
        }
    }

    fun setVerticalFit(z: Boolean) {
        this.verticalFit = z
        requestLayout()
    }
}
