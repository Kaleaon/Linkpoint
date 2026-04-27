// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.view.MotionEvent;
import android.view.Display;
import android.view.View$MeasureSpec;
import android.os.Build$VERSION;
import android.view.WindowManager;
import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Map;
import android.util.TypedValue;
import android.graphics.Paint$Style;
import android.graphics.Canvas;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector$SimpleOnScaleGestureListener;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import java.util.UUID;
import android.view.ScaleGestureDetector$OnScaleGestureListener;
import android.view.ScaleGestureDetector;
import javax.annotation.Nullable;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class MinimapView extends View
{
    private static final float USER_MARK_TOUCH_SLACK = 50.0f;
    private int activePointerId;
    private float actualZoomFactor;
    private final Rect bitmapDstRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    private final Point displaySize;
    private Rect lastDrawRect;
    private float mapOffsetX;
    private float mapOffsetY;
    @Nullable
    private Bitmap minimapBitmap;
    private OnUserClickListener onUserClickListener;
    private float prevTouchX;
    private float prevTouchY;
    private final ScaleGestureDetector scaleGestureDetector;
    private final ScaleGestureDetector$OnScaleGestureListener scaleGestureListener;
    @Nullable
    private UUID selectedUser;
    @Nullable
    private SLMinimap.UserLocations userLocations;
    private final Paint userMarkPaint;
    
    public MinimapView(final Context context) {
        super(context);
        this.onUserClickListener = null;
        this.actualZoomFactor = 1.0f;
        this.prevTouchX = 0.0f;
        this.prevTouchY = 0.0f;
        this.activePointerId = -1;
        this.mapOffsetX = 0.0f;
        this.mapOffsetY = 0.0f;
        this.selectedUser = null;
        this.lastDrawRect = null;
        this.displaySize = new Point();
        this.userMarkPaint = new Paint();
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDstRect = new Rect();
        this.scaleGestureListener = (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }
    
    public MinimapView(final Context context, final AttributeSet set) {
        super(context, set);
        this.onUserClickListener = null;
        this.actualZoomFactor = 1.0f;
        this.prevTouchX = 0.0f;
        this.prevTouchY = 0.0f;
        this.activePointerId = -1;
        this.mapOffsetX = 0.0f;
        this.mapOffsetY = 0.0f;
        this.selectedUser = null;
        this.lastDrawRect = null;
        this.displaySize = new Point();
        this.userMarkPaint = new Paint();
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDstRect = new Rect();
        this.scaleGestureListener = (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }
    
    public MinimapView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.onUserClickListener = null;
        this.actualZoomFactor = 1.0f;
        this.prevTouchX = 0.0f;
        this.prevTouchY = 0.0f;
        this.activePointerId = -1;
        this.mapOffsetX = 0.0f;
        this.mapOffsetY = 0.0f;
        this.selectedUser = null;
        this.lastDrawRect = null;
        this.displaySize = new Point();
        this.userMarkPaint = new Paint();
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDstRect = new Rect();
        this.scaleGestureListener = (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }
    
    private void drawUserMark(final ImmutableVector immutableVector, final Canvas canvas, final Paint paint, final Rect rect, final boolean b, float v, final boolean b2) {
        final float x = immutableVector.getX();
        final float y = immutableVector.getY();
        final float n = rect.left + x / 256.0f * rect.width();
        final float n2 = rect.top + (256.0f - y) / 256.0f * rect.width();
        if (!b) {
            paint.setARGB(255, 0, 255, 0);
        }
        else {
            paint.setARGB(255, 255, 255, 0);
        }
        paint.setStrokeWidth(0.0f);
        paint.setStyle(Paint$Style.FILL_AND_STROKE);
        canvas.drawCircle(n, n2, 5.0f, paint);
        paint.setARGB(255, 128, 255, 128);
        paint.setStyle(Paint$Style.STROKE);
        canvas.drawCircle(n, n2, 5.0f, paint);
        if (b && (Float.isNaN(v) ^ true)) {
            final float n3 = (float)(Math.cos(v) * 20.0);
            final float n4 = (float)(Math.sin(v) * 20.0);
            final float n5 = (float)(Math.cos(v) * 15.0 - Math.sin(v) * -5.0);
            final float n6 = (float)(Math.cos(v) * -5.0 + Math.sin(v) * 15.0);
            final float n7 = (float)(Math.cos(v) * 15.0 - Math.sin(v) * 5.0);
            v = (float)(Math.cos(v) * 5.0 + Math.sin(v) * 15.0);
            paint.setStrokeWidth(3.0f);
            canvas.drawLine(n, n2, n + n3, n2 - n4, paint);
            canvas.drawLine(n + n3, n2 - n4, n5 + n, n2 - n6, paint);
            canvas.drawLine(n + n3, n2 - n4, n + n7, n2 - v, paint);
        }
        if (b2) {
            paint.setStrokeWidth(2.0f);
            paint.setARGB(255, 255, 255, 0);
            canvas.drawCircle(n, n2, 10.0f, paint);
        }
    }
    
    private void handleTouch(final float n, final float n2) {
        if (this.userLocations != null && this.lastDrawRect != null) {
            final float applyDimension = TypedValue.applyDimension(1, 50.0f, this.getResources().getDisplayMetrics());
            final Iterator<Object> iterator = this.userLocations.userPositions.entrySet().iterator();
            UUID selectedUser = null;
            float n3 = 0.0f;
        Label_0229_Outer:
            while (iterator.hasNext()) {
                final Map.Entry<K, SLMinimap.UserLocation> entry = iterator.next();
                final ImmutableVector location = entry.getValue().location;
                final float n4 = location.getX() / 256.0f;
                final float n5 = (float)this.lastDrawRect.width();
                final float n6 = (float)this.lastDrawRect.left;
                final float n7 = (256.0f - location.getY()) / 256.0f;
                final float n8 = (float)this.lastDrawRect.width();
                final float n9 = (float)this.lastDrawRect.top;
                final float abs = Math.abs(n4 * n5 + n6 - n);
                final float abs2 = Math.abs(n7 * n8 + n9 - n2);
                final float n10 = (float)Math.sqrt(abs2 * abs2 + abs * abs);
                if (n10 >= applyDimension) {
                    continue Label_0229_Outer;
                }
                if (selectedUser == null) {
                    selectedUser = (UUID)entry.getKey();
                    n3 = n10;
                }
                else {
                    if (n10 >= n3) {
                        continue Label_0229_Outer;
                    }
                    selectedUser = (UUID)entry.getKey();
                    n3 = n10;
                }
                while (true) {
                    continue Label_0229_Outer;
                    continue;
                }
            }
            this.setSelectedUser(selectedUser);
            if (this.onUserClickListener != null) {
                this.onUserClickListener.onUserClick(selectedUser);
            }
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.minimapBitmap != null) {
            final int width = this.getWidth();
            final int height = this.getHeight();
            final int round = Math.round(Math.min(width, height) * this.actualZoomFactor);
            final int n = width / 2;
            final int n2 = height / 2;
            if (round <= width) {
                this.mapOffsetX = 0.0f;
            }
            if (round <= height) {
                this.mapOffsetY = 0.0f;
            }
            final int n3 = n - round / 2 + (int)this.mapOffsetX;
            int n4;
            if ((n4 = n3) > 0) {
                n4 = n3;
                if (round > width) {
                    this.mapOffsetX = (float)(round / 2 - n);
                    n4 = n - round / 2 + (int)this.mapOffsetX;
                }
            }
            int n5 = n4;
            if (n4 + round <= width) {
                n5 = n4;
                if (round > width) {
                    this.mapOffsetX = (float)(width - round - n + round / 2);
                    n5 = n - round / 2 + (int)this.mapOffsetX;
                }
            }
            final int n6 = n2 - round / 2 + (int)this.mapOffsetY;
            int n7;
            if ((n7 = n6) > 0) {
                n7 = n6;
                if (round > height) {
                    this.mapOffsetY = (float)(round / 2 - n2);
                    n7 = n2 - round / 2 + (int)this.mapOffsetY;
                }
            }
            int n8 = n7;
            if (n7 + round <= height) {
                n8 = n7;
                if (round > height) {
                    this.mapOffsetY = (float)(height - round - n2 + round / 2);
                    n8 = n2 - round / 2 + (int)this.mapOffsetY;
                }
            }
            this.bitmapDstRect.set(n5, n8, n5 + round, round + n8);
            this.bitmapSrcRect.set(0, 0, this.minimapBitmap.getWidth(), this.minimapBitmap.getHeight());
            canvas.drawBitmap(this.minimapBitmap, this.bitmapSrcRect, this.bitmapDstRect, this.bitmapPaint);
            if (this.userLocations != null) {
                for (final Map.Entry<K, SLMinimap.UserLocation> entry : this.userLocations.userPositions.entrySet()) {
                    this.drawUserMark(entry.getValue().location, canvas, this.userMarkPaint, this.bitmapDstRect, false, Float.NaN, Objects.equal(this.selectedUser, entry.getKey()));
                }
                final ImmutableVector myAvatarPosition = this.userLocations.myAvatarPosition;
                if (myAvatarPosition != null) {
                    this.drawUserMark(myAvatarPosition, canvas, this.userMarkPaint, this.bitmapDstRect, true, this.userLocations.myAvatarHeading, false);
                }
            }
            if (this.lastDrawRect == null) {
                this.lastDrawRect = new Rect(this.bitmapDstRect);
            }
            else {
                this.lastDrawRect.set(this.bitmapDstRect);
            }
        }
    }
    
    protected void onMeasure(int min, final int n) {
        final Display defaultDisplay = ((WindowManager)this.getContext().getSystemService("window")).getDefaultDisplay();
        if (Build$VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(this.displaySize);
        }
        else {
            this.displaySize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        }
        int a2;
        final int a = a2 = Math.min(this.displaySize.x, this.displaySize.y);
        if (View$MeasureSpec.getMode(min) != 0) {
            a2 = Math.min(a, View$MeasureSpec.getSize(min));
        }
        min = a2;
        if (View$MeasureSpec.getMode(n) != 0) {
            min = Math.min(a2, View$MeasureSpec.getSize(n));
        }
        this.setMeasuredDimension(min, min);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        int n = 0;
        this.scaleGestureDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case 0: {
                this.activePointerId = motionEvent.getPointerId(0);
                this.prevTouchX = motionEvent.getX();
                this.prevTouchY = motionEvent.getY();
                this.handleTouch(this.prevTouchX, this.prevTouchY);
                break;
            }
            case 2: {
                final int pointerIndex = motionEvent.findPointerIndex(this.activePointerId);
                final float x = motionEvent.getX(pointerIndex);
                final float y = motionEvent.getY(pointerIndex);
                if (!this.scaleGestureDetector.isInProgress()) {
                    final float prevTouchX = this.prevTouchX;
                    final float prevTouchY = this.prevTouchY;
                    this.mapOffsetX += x - prevTouchX;
                    this.mapOffsetY += y - prevTouchY;
                    this.invalidate();
                }
                this.prevTouchX = x;
                this.prevTouchY = y;
                break;
            }
            case 1: {
                this.activePointerId = -1;
                break;
            }
            case 3: {
                this.activePointerId = -1;
                break;
            }
            case 6: {
                final int actionIndex = motionEvent.getActionIndex();
                if (motionEvent.getPointerId(actionIndex) == this.activePointerId) {
                    if (actionIndex == 0) {
                        n = 1;
                    }
                    this.prevTouchX = motionEvent.getX(n);
                    this.prevTouchY = motionEvent.getY(n);
                    this.activePointerId = motionEvent.getPointerId(n);
                    break;
                }
                break;
            }
        }
        return true;
    }
    
    void setMinimapBitmap(@Nullable final SLMinimap.MinimapBitmap minimapBitmap) {
        if (minimapBitmap == null) {
            if (this.minimapBitmap != null) {
                this.minimapBitmap.recycle();
                this.minimapBitmap = null;
            }
        }
        else if (this.minimapBitmap == null) {
            this.minimapBitmap = minimapBitmap.makeBitmap();
        }
        else {
            minimapBitmap.updateBitmap(this.minimapBitmap);
        }
        this.invalidate();
    }
    
    void setOnUserClickListener(final OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }
    
    void setSelectedUser(@Nullable final UUID selectedUser) {
        if (!Objects.equal(selectedUser, this.selectedUser)) {
            this.selectedUser = selectedUser;
            this.invalidate();
        }
    }
    
    void setUserLocations(@Nullable final SLMinimap.UserLocations userLocations) {
        this.userLocations = userLocations;
        this.invalidate();
    }
    
    interface OnUserClickListener
    {
        void onUserClick(final UUID p0);
    }
}
