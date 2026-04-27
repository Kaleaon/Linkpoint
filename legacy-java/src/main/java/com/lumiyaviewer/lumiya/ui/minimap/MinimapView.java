package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class MinimapView extends View {
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
    private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener;

    @Nullable
    private UUID selectedUser;

    @Nullable
    private SLMinimap.UserLocations userLocations;
    private final Paint userMarkPaint;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnUserClickListener {
        void onUserClick(UUID uuid);
    }

    public MinimapView(Context context) {
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
        this.scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.lumiyaviewer.lumiya.ui.minimap.MinimapView.1
            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }

    public MinimapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
        this.scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.lumiyaviewer.lumiya.ui.minimap.MinimapView.1
            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }

    public MinimapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
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
        this.scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.lumiyaviewer.lumiya.ui.minimap.MinimapView.1
            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f);
                MinimapView.this.invalidate();
                return true;
            }
        };
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.scaleGestureListener);
    }

    private void drawUserMark(ImmutableVector immutableVector, Canvas canvas, Paint paint, Rect rect, boolean z, float f, boolean z2) {
        float x = immutableVector.getX();
        float width = rect.left + ((x / 256.0f) * rect.width());
        float y = rect.top + (((256.0f - immutableVector.getY()) / 256.0f) * rect.width());
        if (z) {
            paint.setARGB(255, 255, 255, 0);
        } else {
            paint.setARGB(255, 0, 255, 0);
        }
        paint.setStrokeWidth(0.0f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(width, y, 5.0f, paint);
        paint.setARGB(255, 128, 255, 128);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(width, y, 5.0f, paint);
        if (z && (!Float.isNaN(f))) {
            float cos = (float) (Math.cos(f) * 20.0d);
            float sin = (float) (Math.sin(f) * 20.0d);
            float cos2 = (float) ((Math.cos(f) * 15.0d) - (Math.sin(f) * (-5.0d)));
            float cos3 = (float) ((Math.cos(f) * (-5.0d)) + (Math.sin(f) * 15.0d));
            float cos4 = (float) ((Math.cos(f) * 15.0d) - (Math.sin(f) * 5.0d));
            float cos5 = (float) ((Math.cos(f) * 5.0d) + (Math.sin(f) * 15.0d));
            paint.setStrokeWidth(3.0f);
            canvas.drawLine(width, y, width + cos, y - sin, paint);
            canvas.drawLine(width + cos, y - sin, cos2 + width, y - cos3, paint);
            canvas.drawLine(width + cos, y - sin, width + cos4, y - cos5, paint);
        }
        if (z2) {
            paint.setStrokeWidth(2.0f);
            paint.setARGB(255, 255, 255, 0);
            canvas.drawCircle(width, y, 10.0f, paint);
        }
    }

    private void handleTouch(float f, float f2) {
        UUID uuid;
        UUID uuid2 = null;
        if (this.userLocations == null || this.lastDrawRect == null) {
            return;
        }
        float applyDimension = TypedValue.applyDimension(1, USER_MARK_TOUCH_SLACK, getResources().getDisplayMetrics());
        float f3 = 0.0f;
        Iterator<T> it = this.userLocations.userPositions.entrySet().iterator();
        while (true) {
            uuid = uuid2;
            float f4 = f3;
            if (!it.hasNext()) {
                break;
            }
            Map.Entry entry = (Map.Entry) it.next();
            ImmutableVector immutableVector = ((SLMinimap.UserLocation) entry.getValue()).location;
            float x = ((immutableVector.getX() / 256.0f) * this.lastDrawRect.width()) + this.lastDrawRect.left;
            float y = (((256.0f - immutableVector.getY()) / 256.0f) * this.lastDrawRect.width()) + this.lastDrawRect.top;
            float abs = Math.abs(x - f);
            float abs2 = Math.abs(y - f2);
            f3 = (float) Math.sqrt((abs2 * abs2) + (abs * abs));
            if (f3 < applyDimension) {
                if (uuid == null) {
                    uuid2 = (UUID) entry.getKey();
                } else if (f3 < f4) {
                    uuid2 = (UUID) entry.getKey();
                }
            }
            uuid2 = uuid;
            f3 = f4;
        }
        setSelectedUser(uuid);
        if (this.onUserClickListener != null) {
            this.onUserClickListener.onUserClick(uuid);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.minimapBitmap != null) {
            int width = getWidth();
            int height = getHeight();
            int round = Math.round(Math.min(width, height) * this.actualZoomFactor);
            int i = width / 2;
            int i2 = height / 2;
            if (round <= width) {
                this.mapOffsetX = 0.0f;
            }
            if (round <= height) {
                this.mapOffsetY = 0.0f;
            }
            int i3 = (i - (round / 2)) + ((int) this.mapOffsetX);
            if (i3 > 0 && round > width) {
                this.mapOffsetX = (round / 2) - i;
                i3 = (i - (round / 2)) + ((int) this.mapOffsetX);
            }
            if (i3 + round <= width && round > width) {
                this.mapOffsetX = ((width - round) - i) + (round / 2);
                i3 = (i - (round / 2)) + ((int) this.mapOffsetX);
            }
            int i4 = (i2 - (round / 2)) + ((int) this.mapOffsetY);
            if (i4 > 0 && round > height) {
                this.mapOffsetY = (round / 2) - i2;
                i4 = (i2 - (round / 2)) + ((int) this.mapOffsetY);
            }
            if (i4 + round <= height && round > height) {
                this.mapOffsetY = ((height - round) - i2) + (round / 2);
                i4 = (i2 - (round / 2)) + ((int) this.mapOffsetY);
            }
            this.bitmapDstRect.set(i3, i4, i3 + round, round + i4);
            this.bitmapSrcRect.set(0, 0, this.minimapBitmap.getWidth(), this.minimapBitmap.getHeight());
            canvas.drawBitmap(this.minimapBitmap, this.bitmapSrcRect, this.bitmapDstRect, this.bitmapPaint);
            if (this.userLocations != null) {
                Iterator<T> it = this.userLocations.userPositions.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    drawUserMark(((SLMinimap.UserLocation) entry.getValue()).location, canvas, this.userMarkPaint, this.bitmapDstRect, false, Float.NaN, Objects.equal(this.selectedUser, entry.getKey()));
                }
                ImmutableVector immutableVector = this.userLocations.myAvatarPosition;
                if (immutableVector != null) {
                    drawUserMark(immutableVector, canvas, this.userMarkPaint, this.bitmapDstRect, true, this.userLocations.myAvatarHeading, false);
                }
            }
            if (this.lastDrawRect == null) {
                this.lastDrawRect = new Rect(this.bitmapDstRect);
            } else {
                this.lastDrawRect.set(this.bitmapDstRect);
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        Display defaultDisplay = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(this.displaySize);
        } else {
            this.displaySize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        }
        int min = Math.min(this.displaySize.x, this.displaySize.y);
        if (View.MeasureSpec.getMode(i) != 0) {
            min = Math.min(min, View.MeasureSpec.getSize(i));
        }
        if (View.MeasureSpec.getMode(i2) != 0) {
            min = Math.min(min, View.MeasureSpec.getSize(i2));
        }
        setMeasuredDimension(min, min);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x000f, code lost:
    
        return true;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            r1 = 1
            r3 = -1
            r0 = 0
            android.view.ScaleGestureDetector r2 = r6.scaleGestureDetector
            r2.onTouchEvent(r7)
            int r2 = r7.getActionMasked()
            switch(r2) {
                case 0: goto L10;
                case 1: goto L5a;
                case 2: goto L2a;
                case 3: goto L5d;
                case 4: goto Lf;
                case 5: goto Lf;
                case 6: goto L60;
                default: goto Lf;
            }
        Lf:
            return r1
        L10:
            int r0 = r7.getPointerId(r0)
            r6.activePointerId = r0
            float r0 = r7.getX()
            r6.prevTouchX = r0
            float r0 = r7.getY()
            r6.prevTouchY = r0
            float r0 = r6.prevTouchX
            float r2 = r6.prevTouchY
            r6.handleTouch(r0, r2)
            goto Lf
        L2a:
            int r0 = r6.activePointerId
            int r0 = r7.findPointerIndex(r0)
            float r2 = r7.getX(r0)
            float r0 = r7.getY(r0)
            android.view.ScaleGestureDetector r3 = r6.scaleGestureDetector
            boolean r3 = r3.isInProgress()
            if (r3 != 0) goto L55
            float r3 = r6.prevTouchX
            float r3 = r2 - r3
            float r4 = r6.prevTouchY
            float r4 = r0 - r4
            float r5 = r6.mapOffsetX
            float r3 = r3 + r5
            r6.mapOffsetX = r3
            float r3 = r6.mapOffsetY
            float r3 = r3 + r4
            r6.mapOffsetY = r3
            r6.invalidate()
        L55:
            r6.prevTouchX = r2
            r6.prevTouchY = r0
            goto Lf
        L5a:
            r6.activePointerId = r3
            goto Lf
        L5d:
            r6.activePointerId = r3
            goto Lf
        L60:
            int r2 = r7.getActionIndex()
            int r3 = r7.getPointerId(r2)
            int r4 = r6.activePointerId
            if (r3 != r4) goto Lf
            if (r2 != 0) goto L6f
            r0 = r1
        L6f:
            float r2 = r7.getX(r0)
            r6.prevTouchX = r2
            float r2 = r7.getY(r0)
            r6.prevTouchY = r2
            int r0 = r7.getPointerId(r0)
            r6.activePointerId = r0
            goto Lf
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.minimap.MinimapView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMinimapBitmap(@Nullable SLMinimap.MinimapBitmap minimapBitmap) {
        if (minimapBitmap == null) {
            if (this.minimapBitmap != null) {
                this.minimapBitmap.recycle();
                this.minimapBitmap = null;
            }
        } else if (this.minimapBitmap == null) {
            this.minimapBitmap = minimapBitmap.makeBitmap();
        } else {
            minimapBitmap.updateBitmap(this.minimapBitmap);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSelectedUser(@Nullable UUID uuid) {
        if (Objects.equal(uuid, this.selectedUser)) {
            return;
        }
        this.selectedUser = uuid;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserLocations(@Nullable SLMinimap.UserLocations userLocations) {
        this.userLocations = userLocations;
        invalidate();
    }
}
