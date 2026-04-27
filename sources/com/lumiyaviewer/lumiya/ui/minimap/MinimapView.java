// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MinimapView extends View
{
    static interface OnUserClickListener
    {

        public abstract void onUserClick(UUID uuid);
    }


    private static final float USER_MARK_TOUCH_SLACK = 50F;
    private int activePointerId;
    private float actualZoomFactor;
    private final Rect bitmapDstRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    private final Point displaySize;
    private Rect lastDrawRect;
    private float mapOffsetX;
    private float mapOffsetY;
    private Bitmap minimapBitmap;
    private OnUserClickListener onUserClickListener;
    private float prevTouchX;
    private float prevTouchY;
    private final ScaleGestureDetector scaleGestureDetector;
    private final android.view.ScaleGestureDetector.OnScaleGestureListener scaleGestureListener;
    private UUID selectedUser;
    private com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocations userLocations;
    private final Paint userMarkPaint;

    static float _2D_get0(MinimapView minimapview)
    {
        return minimapview.actualZoomFactor;
    }

    static float _2D_set0(MinimapView minimapview, float f)
    {
        minimapview.actualZoomFactor = f;
        return f;
    }

    public MinimapView(Context context)
    {
        super(context);
        onUserClickListener = null;
        actualZoomFactor = 1.0F;
        prevTouchX = 0.0F;
        prevTouchY = 0.0F;
        activePointerId = -1;
        mapOffsetX = 0.0F;
        mapOffsetY = 0.0F;
        selectedUser = null;
        lastDrawRect = null;
        displaySize = new Point();
        userMarkPaint = new Paint();
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDstRect = new Rect();
        scaleGestureListener = new android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {

            final MinimapView this$0;

            public boolean onScale(ScaleGestureDetector scalegesturedetector)
            {
                MinimapView._2D_set0(MinimapView.this, Math.min(Math.max(MinimapView._2D_get0(MinimapView.this) * scalegesturedetector.getScaleFactor(), 1.0F), 5F));
                invalidate();
                return true;
            }

            
            {
                this$0 = MinimapView.this;
                super();
            }
        };
        scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
    }

    public MinimapView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        onUserClickListener = null;
        actualZoomFactor = 1.0F;
        prevTouchX = 0.0F;
        prevTouchY = 0.0F;
        activePointerId = -1;
        mapOffsetX = 0.0F;
        mapOffsetY = 0.0F;
        selectedUser = null;
        lastDrawRect = null;
        displaySize = new Point();
        userMarkPaint = new Paint();
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDstRect = new Rect();
        scaleGestureListener = new _cls1();
        scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
    }

    public MinimapView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        onUserClickListener = null;
        actualZoomFactor = 1.0F;
        prevTouchX = 0.0F;
        prevTouchY = 0.0F;
        activePointerId = -1;
        mapOffsetX = 0.0F;
        mapOffsetY = 0.0F;
        selectedUser = null;
        lastDrawRect = null;
        displaySize = new Point();
        userMarkPaint = new Paint();
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDstRect = new Rect();
        scaleGestureListener = new _cls1();
        scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
    }

    private void drawUserMark(ImmutableVector immutablevector, Canvas canvas, Paint paint, Rect rect, boolean flag, float f, boolean flag1)
    {
        float f1 = immutablevector.getX();
        float f2 = immutablevector.getY();
        f1 /= 256F;
        float f3 = rect.width();
        f1 = (float)rect.left + f1 * f3;
        f2 = (256F - f2) / 256F;
        f3 = rect.width();
        f2 = (float)rect.top + f2 * f3;
        if (!flag)
        {
            paint.setARGB(255, 0, 255, 0);
        } else
        {
            paint.setARGB(255, 255, 255, 0);
        }
        paint.setStrokeWidth(0.0F);
        paint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(f1, f2, 5F, paint);
        paint.setARGB(255, 128, 255, 128);
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        canvas.drawCircle(f1, f2, 5F, paint);
        if (flag && Float.isNaN(f) ^ true)
        {
            float f4 = (float)(Math.cos(f) * 20D);
            float f5 = (float)(Math.sin(f) * 20D);
            float f6 = (float)(Math.cos(f) * 15D - Math.sin(f) * -5D);
            float f7 = (float)(Math.cos(f) * -5D + Math.sin(f) * 15D);
            float f8 = (float)(Math.cos(f) * 15D - Math.sin(f) * 5D);
            f = (float)(Math.cos(f) * 5D + Math.sin(f) * 15D);
            paint.setStrokeWidth(3F);
            canvas.drawLine(f1, f2, f1 + f4, f2 - f5, paint);
            canvas.drawLine(f1 + f4, f2 - f5, f6 + f1, f2 - f7, paint);
            canvas.drawLine(f1 + f4, f2 - f5, f1 + f8, f2 - f, paint);
        }
        if (flag1)
        {
            paint.setStrokeWidth(2.0F);
            paint.setARGB(255, 255, 255, 0);
            canvas.drawCircle(f1, f2, 10F, paint);
        }
    }

    private void handleTouch(float f, float f1)
    {
        if (userLocations != null && lastDrawRect != null)
        {
            float f4 = TypedValue.applyDimension(1, 50F, getResources().getDisplayMetrics());
            Iterator iterator = userLocations.userPositions.entrySet().iterator();
            UUID uuid = null;
            float f2 = 0.0F;
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                ImmutableVector immutablevector = ((com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation)entry.getValue()).location;
                float f7 = immutablevector.getX() / 256F;
                float f8 = lastDrawRect.width();
                float f9 = lastDrawRect.left;
                float f3 = (256F - immutablevector.getY()) / 256F;
                float f5 = lastDrawRect.width();
                float f6 = lastDrawRect.top;
                f7 = Math.abs((f7 * f8 + f9) - f);
                f3 = Math.abs((f3 * f5 + f6) - f1);
                f3 = (float)Math.sqrt(f3 * f3 + f7 * f7);
                if (f3 < f4)
                {
                    if (uuid == null)
                    {
                        uuid = (UUID)entry.getKey();
                        f2 = f3;
                    } else
                    if (f3 < f2)
                    {
                        uuid = (UUID)entry.getKey();
                        f2 = f3;
                    }
                }
            } while (true);
            setSelectedUser(uuid);
            if (onUserClickListener != null)
            {
                onUserClickListener.onUserClick(uuid);
            }
        }
    }

    protected void onDraw(Canvas canvas)
    {
label0:
        {
            if (minimapBitmap != null)
            {
                int k = getWidth();
                int i1 = getHeight();
                int l = Math.round((float)Math.min(k, i1) * actualZoomFactor);
                int k1 = k / 2;
                int j1 = i1 / 2;
                if (l <= k)
                {
                    mapOffsetX = 0.0F;
                }
                if (l <= i1)
                {
                    mapOffsetY = 0.0F;
                }
                int j = (k1 - l / 2) + (int)mapOffsetX;
                int i = j;
                if (j > 0)
                {
                    i = j;
                    if (l > k)
                    {
                        mapOffsetX = l / 2 - k1;
                        i = (k1 - l / 2) + (int)mapOffsetX;
                    }
                }
                j = i;
                if (i + l <= k)
                {
                    j = i;
                    if (l > k)
                    {
                        mapOffsetX = (k - l - k1) + l / 2;
                        j = (k1 - l / 2) + (int)mapOffsetX;
                    }
                }
                k = (j1 - l / 2) + (int)mapOffsetY;
                i = k;
                if (k > 0)
                {
                    i = k;
                    if (l > i1)
                    {
                        mapOffsetY = l / 2 - j1;
                        i = (j1 - l / 2) + (int)mapOffsetY;
                    }
                }
                k = i;
                if (i + l <= i1)
                {
                    k = i;
                    if (l > i1)
                    {
                        mapOffsetY = (i1 - l - j1) + l / 2;
                        k = (j1 - l / 2) + (int)mapOffsetY;
                    }
                }
                bitmapDstRect.set(j, k, j + l, l + k);
                bitmapSrcRect.set(0, 0, minimapBitmap.getWidth(), minimapBitmap.getHeight());
                canvas.drawBitmap(minimapBitmap, bitmapSrcRect, bitmapDstRect, bitmapPaint);
                if (userLocations != null)
                {
                    java.util.Map.Entry entry;
                    for (Iterator iterator = userLocations.userPositions.entrySet().iterator(); iterator.hasNext(); drawUserMark(((com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation)entry.getValue()).location, canvas, userMarkPaint, bitmapDstRect, false, (0.0F / 0.0F), Objects.equal(selectedUser, entry.getKey())))
                    {
                        entry = (java.util.Map.Entry)iterator.next();
                    }

                    ImmutableVector immutablevector = userLocations.myAvatarPosition;
                    if (immutablevector != null)
                    {
                        drawUserMark(immutablevector, canvas, userMarkPaint, bitmapDstRect, true, userLocations.myAvatarHeading, false);
                    }
                }
                if (lastDrawRect != null)
                {
                    break label0;
                }
                lastDrawRect = new Rect(bitmapDstRect);
            }
            return;
        }
        lastDrawRect.set(bitmapDstRect);
    }

    protected void onMeasure(int i, int j)
    {
        Display display = ((WindowManager)getContext().getSystemService("window")).getDefaultDisplay();
        int k;
        int l;
        if (android.os.Build.VERSION.SDK_INT >= 13)
        {
            display.getSize(displaySize);
        } else
        {
            displaySize.set(display.getWidth(), display.getHeight());
        }
        l = Math.min(displaySize.x, displaySize.y);
        k = l;
        if (android.view.View.MeasureSpec.getMode(i) != 0)
        {
            k = Math.min(l, android.view.View.MeasureSpec.getSize(i));
        }
        i = k;
        if (android.view.View.MeasureSpec.getMode(j) != 0)
        {
            i = Math.min(k, android.view.View.MeasureSpec.getSize(j));
        }
        setMeasuredDimension(i, i);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        int i;
        i = 0;
        scaleGestureDetector.onTouchEvent(motionevent);
        motionevent.getActionMasked();
        JVM INSTR tableswitch 0 6: default 60
    //                   0 62
    //                   1 189
    //                   2 101
    //                   3 196
    //                   4 60
    //                   5 60
    //                   6 203;
           goto _L1 _L2 _L3 _L4 _L5 _L1 _L1 _L6
_L1:
        return true;
_L2:
        activePointerId = motionevent.getPointerId(0);
        prevTouchX = motionevent.getX();
        prevTouchY = motionevent.getY();
        handleTouch(prevTouchX, prevTouchY);
        return true;
_L4:
        i = motionevent.findPointerIndex(activePointerId);
        float f = motionevent.getX(i);
        float f1 = motionevent.getY(i);
        if (!scaleGestureDetector.isInProgress())
        {
            float f2 = prevTouchX;
            float f3 = prevTouchY;
            mapOffsetX = (f - f2) + mapOffsetX;
            mapOffsetY = mapOffsetY + (f1 - f3);
            invalidate();
        }
        prevTouchX = f;
        prevTouchY = f1;
        return true;
_L3:
        activePointerId = -1;
        return true;
_L5:
        activePointerId = -1;
        return true;
_L6:
        int j = motionevent.getActionIndex();
        if (motionevent.getPointerId(j) == activePointerId)
        {
            if (j == 0)
            {
                i = 1;
            }
            prevTouchX = motionevent.getX(i);
            prevTouchY = motionevent.getY(i);
            activePointerId = motionevent.getPointerId(i);
            return true;
        }
        if (true) goto _L1; else goto _L7
_L7:
    }

    void setMinimapBitmap(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.MinimapBitmap minimapbitmap)
    {
        if (minimapbitmap == null)
        {
            if (minimapBitmap != null)
            {
                minimapBitmap.recycle();
                minimapBitmap = null;
            }
        } else
        if (minimapBitmap == null)
        {
            minimapBitmap = minimapbitmap.makeBitmap();
        } else
        {
            minimapbitmap.updateBitmap(minimapBitmap);
        }
        invalidate();
    }

    void setOnUserClickListener(OnUserClickListener onuserclicklistener)
    {
        onUserClickListener = onuserclicklistener;
    }

    void setSelectedUser(UUID uuid)
    {
        if (!Objects.equal(uuid, selectedUser))
        {
            selectedUser = uuid;
            invalidate();
        }
    }

    void setUserLocations(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocations userlocations)
    {
        userLocations = userlocations;
        invalidate();
    }
}
