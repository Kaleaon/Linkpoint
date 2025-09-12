package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class ImageAssetView extends View {
    private boolean alignTop = false;
    private UUID assetID;
    private final Rect bitmapDestRect = new Rect();
    private final Paint bitmapPaint = new Paint();
    private final Rect bitmapSrcRect = new Rect();
    /* access modifiers changed from: private */
    public Bitmap imageBitmap;
    /* access modifiers changed from: private */
    public LoadAssetImageTask loadTask;
    private final Paint textPaint = new Paint();
    /* access modifiers changed from: private */
    public boolean verticalFit = false;

    private class LoadAssetImageTask extends AsyncTask<UUID, Void, Bitmap> implements ResourceConsumer {
        private volatile OpenJPEG texture;
        private final Object textureReady;

        private LoadAssetImageTask() {
            this.textureReady = new Object();
        }

        /* synthetic */ LoadAssetImageTask(ImageAssetView imageAssetView, LoadAssetImageTask loadAssetImageTask) {
            this();
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof OpenJPEG) {
                this.texture = (OpenJPEG) obj;
            }
            synchronized (this.textureReady) {
                this.textureReady.notify();
            }
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(UUID... uuidArr) {
            Debug.Printf("loading asset ID %s", uuidArr[0].toString());
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuidArr[0], TextureClass.Asset), this);
            synchronized (this.textureReady) {
                if (this.texture == null) {
                    Debug.Printf("asset ID %s is not available, waiting", uuidArr[0].toString());
                    try {
                        this.textureReady.wait();
                        Debug.Printf("done waiting for asset ID %s", uuidArr[0].toString());
                    } catch (InterruptedException e) {
                        Debug.Printf("interrupted while waiting for asset ID %s", uuidArr[0].toString());
                        return null;
                    }
                } else {
                    Debug.Printf("asset ID %s is already available", uuidArr[0].toString());
                }
            }
            if (this.texture != null) {
                return this.texture.getAsBitmap();
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            Bitmap unused = ImageAssetView.this.imageBitmap = bitmap;
            if (ImageAssetView.this.verticalFit) {
                ImageAssetView.this.requestLayout();
            }
            ImageAssetView.this.invalidate();
            LoadAssetImageTask unused2 = ImageAssetView.this.loadTask = null;
        }
    }

    public ImageAssetView(Context context) {
        super(context);
    }

    public ImageAssetView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImageAssetView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.chatBubbleText, typedValue, true);
        int i = typedValue.data;
        this.textPaint.setStyle(Paint.Style.STROKE);
        this.textPaint.setColor(i);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(TypedValue.applyDimension(2, 14.0f, displayMetrics));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        this.bitmapPaint.setStyle(Paint.Style.STROKE);
        this.bitmapPaint.setARGB(255, 192, 192, 192);
        this.bitmapPaint.setTextAlign(Paint.Align.CENTER);
        if (this.imageBitmap == null || width == 0 || height == 0) {
            canvas.drawARGB(50, 0, 0, 0);
            String str = (this.assetID == null || UUIDPool.ZeroUUID.equals(this.assetID)) ? "No image" : this.loadTask == null ? "Failed to load" : this.loadTask.getStatus() == AsyncTask.Status.FINISHED ? "Failed to load" : "Loading...";
            this.textPaint.getTextBounds(str, 0, str.length(), this.bitmapSrcRect);
            canvas.drawText(str, ((float) width) / 2.0f, (((float) height) / 2.0f) + (((float) this.bitmapSrcRect.height()) / 2.0f), this.textPaint);
            return;
        }
        int width2 = this.imageBitmap.getWidth();
        int height2 = this.imageBitmap.getHeight();
        float max = Math.max(((float) width2) / ((float) width), ((float) height2) / ((float) height));
        int round = Math.round(((float) width2) / max);
        int round2 = Math.round(((float) height2) / max);
        int i = (width / 2) - (round / 2);
        int i2 = this.alignTop ? 0 : (height / 2) - (round2 / 2);
        this.bitmapDestRect.left = i + 1;
        this.bitmapDestRect.top = i2 + 1;
        this.bitmapDestRect.right = (round + i) - 1;
        this.bitmapDestRect.bottom = (i2 + round2) - 1;
        if (this.bitmapDestRect.left < 1) {
            this.bitmapDestRect.left = 1;
        }
        if (this.bitmapDestRect.top < 1) {
            this.bitmapDestRect.top = 1;
        }
        if (this.bitmapDestRect.right > width - 1) {
            this.bitmapDestRect.right = width - 1;
        }
        if (this.bitmapDestRect.bottom > height - 1) {
            this.bitmapDestRect.bottom = height - 1;
        }
        this.bitmapSrcRect.left = 0;
        this.bitmapSrcRect.top = 0;
        this.bitmapSrcRect.right = width2;
        this.bitmapSrcRect.bottom = height2;
        canvas.drawBitmap(this.imageBitmap, this.bitmapSrcRect, this.bitmapDestRect, this.bitmapPaint);
        Rect rect = this.bitmapDestRect;
        rect.left--;
        Rect rect2 = this.bitmapDestRect;
        rect2.top--;
        canvas.drawRect(this.bitmapDestRect, this.bitmapPaint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (View.MeasureSpec.getMode(i) == 0 && View.MeasureSpec.getMode(i2) == 0) {
            super.onMeasure(i, i2);
            return;
        }
        int min = Math.min(View.MeasureSpec.getMode(i2) != 0 ? View.MeasureSpec.getSize(i2) : Integer.MAX_VALUE, View.MeasureSpec.getMode(i) != 0 ? View.MeasureSpec.getSize(i) : Integer.MAX_VALUE);
        int size = View.MeasureSpec.getMode(i) == 1073741824 ? View.MeasureSpec.getSize(i) : min;
        if (View.MeasureSpec.getMode(i2) == 1073741824) {
            min = View.MeasureSpec.getSize(i2);
        }
        if (this.verticalFit && this.imageBitmap != null && size != Integer.MAX_VALUE && size > 0) {
            min = (this.imageBitmap.getHeight() * size) / this.imageBitmap.getWidth();
        }
        setMeasuredDimension(size, min);
    }

    public void setAlignTop(boolean z) {
        this.alignTop = z;
        invalidate();
    }

    public void setAssetID(UUID uuid) {
        Object[] objArr = new Object[1];
        objArr[0] = uuid != null ? uuid.toString() : null;
        Debug.Printf("new asset ID: %s", objArr);
        if (uuid != null && uuid.equals(UUIDPool.ZeroUUID)) {
            uuid = null;
        }
        if (!Objects.equal(this.assetID, uuid)) {
            if (this.loadTask != null) {
                this.loadTask.cancel(true);
                this.loadTask = null;
            }
            this.assetID = uuid;
            if (this.imageBitmap != null) {
                this.imageBitmap.recycle();
            }
            this.imageBitmap = null;
            if (this.assetID != null) {
                Debug.Printf("requested to view asset ID %s", uuid);
                this.loadTask = new LoadAssetImageTask(this, (LoadAssetImageTask) null);
                this.loadTask.execute(new UUID[]{uuid});
            }
            invalidate();
        }
    }

    public void setVerticalFit(boolean z) {
        this.verticalFit = z;
        requestLayout();
    }
}
