// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class ImageAssetView extends View
{
    private class LoadAssetImageTask extends AsyncTask
        implements ResourceConsumer
    {

        private volatile OpenJPEG texture;
        private final Object textureReady;
        final ImageAssetView this$0;

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj instanceof OpenJPEG)
            {
                texture = (OpenJPEG)obj;
            }
            obj = textureReady;
            obj;
            JVM INSTR monitorenter ;
            textureReady.notify();
            obj;
            JVM INSTR monitorexit ;
            return;
            Exception exception;
            exception;
            throw exception;
        }

        protected transient Bitmap doInBackground(UUID auuid[])
        {
            Debug.Printf("loading asset ID %s", new Object[] {
                auuid[0].toString()
            });
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(auuid[0], TextureClass.Asset), this);
            Object obj = textureReady;
            obj;
            JVM INSTR monitorenter ;
            if (texture != null) goto _L2; else goto _L1
_L1:
            Debug.Printf("asset ID %s is not available, waiting", new Object[] {
                auuid[0].toString()
            });
            textureReady.wait();
            Debug.Printf("done waiting for asset ID %s", new Object[] {
                auuid[0].toString()
            });
_L4:
            obj;
            JVM INSTR monitorexit ;
            InterruptedException interruptedexception;
            if (texture != null)
            {
                return texture.getAsBitmap();
            } else
            {
                return null;
            }
            interruptedexception;
            Debug.Printf("interrupted while waiting for asset ID %s", new Object[] {
                auuid[0].toString()
            });
            obj;
            JVM INSTR monitorexit ;
            return null;
_L2:
            Debug.Printf("asset ID %s is already available", new Object[] {
                auuid[0].toString()
            });
            if (true) goto _L4; else goto _L3
_L3:
            auuid;
            throw auuid;
        }

        protected volatile Object doInBackground(Object aobj[])
        {
            return doInBackground((UUID[])aobj);
        }

        protected void onPostExecute(Bitmap bitmap)
        {
            ImageAssetView._2D_set0(ImageAssetView.this, bitmap);
            if (ImageAssetView._2D_get0(ImageAssetView.this))
            {
                requestLayout();
            }
            invalidate();
            ImageAssetView._2D_set1(ImageAssetView.this, null);
        }

        protected volatile void onPostExecute(Object obj)
        {
            onPostExecute((Bitmap)obj);
        }

        private LoadAssetImageTask()
        {
            this$0 = ImageAssetView.this;
            super();
            textureReady = new Object();
        }

        LoadAssetImageTask(LoadAssetImageTask loadassetimagetask)
        {
            this();
        }
    }


    private boolean alignTop;
    private UUID assetID;
    private final Rect bitmapDestRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    private Bitmap imageBitmap;
    private LoadAssetImageTask loadTask;
    private final Paint textPaint;
    private boolean verticalFit;

    static boolean _2D_get0(ImageAssetView imageassetview)
    {
        return imageassetview.verticalFit;
    }

    static Bitmap _2D_set0(ImageAssetView imageassetview, Bitmap bitmap)
    {
        imageassetview.imageBitmap = bitmap;
        return bitmap;
    }

    static LoadAssetImageTask _2D_set1(ImageAssetView imageassetview, LoadAssetImageTask loadassetimagetask)
    {
        imageassetview.loadTask = loadassetimagetask;
        return loadassetimagetask;
    }

    public ImageAssetView(Context context)
    {
        super(context);
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
        textPaint = new Paint();
        alignTop = false;
        verticalFit = false;
    }

    public ImageAssetView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
        textPaint = new Paint();
        alignTop = false;
        verticalFit = false;
    }

    public ImageAssetView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
        textPaint = new Paint();
        alignTop = false;
        verticalFit = false;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        android.util.DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        TypedValue typedvalue = new TypedValue();
        getContext().getTheme().resolveAttribute(0x7f01005c, typedvalue, true);
        int i = typedvalue.data;
        textPaint.setStyle(android.graphics.Paint.Style.STROKE);
        textPaint.setColor(i);
        textPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(TypedValue.applyDimension(2, 14F, displaymetrics));
    }

    protected void onDraw(Canvas canvas)
    {
        int j = getWidth();
        int k = getHeight();
        bitmapPaint.setStyle(android.graphics.Paint.Style.STROKE);
        bitmapPaint.setARGB(255, 192, 192, 192);
        bitmapPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        if (imageBitmap != null && j != 0 && k != 0)
        {
            int l = imageBitmap.getWidth();
            int i1 = imageBitmap.getHeight();
            float f = Math.max((float)l / (float)j, (float)i1 / (float)k);
            int j1 = Math.round((float)l / f);
            int k1 = Math.round((float)i1 / f);
            int l1 = j / 2;
            int i = k / 2;
            l1 -= j1 / 2;
            Rect rect;
            if (alignTop)
            {
                i = 0;
            } else
            {
                i -= k1 / 2;
            }
            bitmapDestRect.left = l1 + 1;
            bitmapDestRect.top = i + 1;
            bitmapDestRect.right = (j1 + l1) - 1;
            bitmapDestRect.bottom = (i + k1) - 1;
            if (bitmapDestRect.left < 1)
            {
                bitmapDestRect.left = 1;
            }
            if (bitmapDestRect.top < 1)
            {
                bitmapDestRect.top = 1;
            }
            if (bitmapDestRect.right > j - 1)
            {
                bitmapDestRect.right = j - 1;
            }
            if (bitmapDestRect.bottom > k - 1)
            {
                bitmapDestRect.bottom = k - 1;
            }
            bitmapSrcRect.left = 0;
            bitmapSrcRect.top = 0;
            bitmapSrcRect.right = l;
            bitmapSrcRect.bottom = i1;
            canvas.drawBitmap(imageBitmap, bitmapSrcRect, bitmapDestRect, bitmapPaint);
            rect = bitmapDestRect;
            rect.left = rect.left - 1;
            rect = bitmapDestRect;
            rect.top = rect.top - 1;
            canvas.drawRect(bitmapDestRect, bitmapPaint);
            return;
        }
        canvas.drawARGB(50, 0, 0, 0);
        String s;
        if (assetID == null || UUIDPool.ZeroUUID.equals(assetID))
        {
            s = "No image";
        } else
        if (loadTask == null)
        {
            s = "Failed to load";
        } else
        if (loadTask.getStatus() == android.os.AsyncTask.Status.FINISHED)
        {
            s = "Failed to load";
        } else
        {
            s = "Loading...";
        }
        textPaint.getTextBounds(s, 0, s.length(), bitmapSrcRect);
        canvas.drawText(s, (float)j / 2.0F, (float)k / 2.0F + (float)bitmapSrcRect.height() / 2.0F, textPaint);
    }

    protected void onMeasure(int i, int j)
    {
        if (android.view.View.MeasureSpec.getMode(i) == 0 && android.view.View.MeasureSpec.getMode(j) == 0)
        {
            super.onMeasure(i, j);
            return;
        }
        int k;
        int l;
        if (android.view.View.MeasureSpec.getMode(i) != 0)
        {
            k = android.view.View.MeasureSpec.getSize(i);
        } else
        {
            k = 0x7fffffff;
        }
        if (android.view.View.MeasureSpec.getMode(j) != 0)
        {
            l = android.view.View.MeasureSpec.getSize(j);
        } else
        {
            l = 0x7fffffff;
        }
        l = Math.min(l, k);
        if (android.view.View.MeasureSpec.getMode(i) == 0x40000000)
        {
            k = android.view.View.MeasureSpec.getSize(i);
        } else
        {
            k = l;
        }
        i = l;
        if (android.view.View.MeasureSpec.getMode(j) == 0x40000000)
        {
            i = android.view.View.MeasureSpec.getSize(j);
        }
        j = i;
        if (verticalFit)
        {
            j = i;
            if (imageBitmap != null)
            {
                j = i;
                if (k != 0x7fffffff)
                {
                    j = i;
                    if (k > 0)
                    {
                        j = (imageBitmap.getHeight() * k) / imageBitmap.getWidth();
                    }
                }
            }
        }
        setMeasuredDimension(k, j);
    }

    public void setAlignTop(boolean flag)
    {
        alignTop = flag;
        invalidate();
    }

    public void setAssetID(UUID uuid)
    {
        Object obj;
        if (uuid != null)
        {
            obj = uuid.toString();
        } else
        {
            obj = null;
        }
        Debug.Printf("new asset ID: %s", new Object[] {
            obj
        });
        obj = uuid;
        if (uuid != null)
        {
            obj = uuid;
            if (uuid.equals(UUIDPool.ZeroUUID))
            {
                obj = null;
            }
        }
        if (!Objects.equal(assetID, obj))
        {
            if (loadTask != null)
            {
                loadTask.cancel(true);
                loadTask = null;
            }
            assetID = ((UUID) (obj));
            if (imageBitmap != null)
            {
                imageBitmap.recycle();
            }
            imageBitmap = null;
            if (assetID != null)
            {
                Debug.Printf("requested to view asset ID %s", new Object[] {
                    obj
                });
                loadTask = new LoadAssetImageTask(null);
                loadTask.execute(new UUID[] {
                    obj
                });
            }
            invalidate();
        }
    }

    public void setVerticalFit(boolean flag)
    {
        verticalFit = flag;
        requestLayout();
    }
}
