package com.linkpoint.ui.minimap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Display
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.WindowManager
import com.google.common.base.Objects
import com.linkpoint.slproto.modules.SLMinimap
import com.linkpoint.slproto.types.ImmutableVector
import java.util.Iterator
import java.util.Map
import java.util.UUID
import javax.annotation.Nullable

class MinimapView : View() {
    private const val USER_MARK_TOUCH_SLACK: Float = 50.0f
    private Int activePointerId = -1
    /* access modifiers changed from: private */
    public Float actualZoomFactor = 1.0f
    private val Rect bitmapDstRect = Rect()
    private val Paint bitmapPaint = Paint()
    private val Rect bitmapSrcRect = Rect()
    private val Point displaySize = Point()
    private Rect lastDrawRect = null
    private Float mapOffsetX = 0.0f
    private Float mapOffsetY = 0.0f
    private Bitmap minimapBitmap
    private OnUserClickListener onUserClickListener = null
    private Float prevTouchX = 0.0f
    private Float prevTouchY = 0.0f
    private val ScaleGestureDetector scaleGestureDetector
    private val ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = ScaleGestureDetector.SimpleOnScaleGestureListener() {
        public Boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            Float unused = MinimapView.this.actualZoomFactor = Math.min(Math.max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f)
            MinimapView.this.invalidate()
            return true
        }
    }
    private UUID selectedUser = null
    private SLMinimap.UserLocations userLocations
    private val Paint userMarkPaint = Paint()

    interface OnUserClickListener {
        Unit onUserClick(UUID uuid)
    }

    public MinimapView(Context context) {
        super(context)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    public MinimapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    public MinimapView(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    private Unit drawUserMark(ImmutableVector immutableVector, Canvas canvas, Paint paint, Rect rect, Boolean z, Float f, Boolean z2) {
        Float x = immutableVector.getX()
        Float y = immutableVector.getY()
        Float width = ((Float) rect.left) + ((x / 256.0f) * ((Float) rect.width()))
        Float width2 = ((Float) rect.top) + (((256.0f - y) / 256.0f) * ((Float) rect.width()))
        if (!z) {
            paint.setARGB(255, 0, 255, 0)
        } else {
            paint.setARGB(255, 255, 255, 0)
        }
        paint.setStrokeWidth(0.0f)
        paint.setStyle(Paint.Style.FILL_AND_STROKE)
        canvas.drawCircle(width, width2, 5.0f, paint)
        paint.setARGB(255, 128, 255, 128)
        paint.setStyle(Paint.Style.STROKE)
        canvas.drawCircle(width, width2, 5.0f, paint)
        if (z && (!Float.isNaN(f))) {
            Float cos = (Float) (Math.cos((Double) f) * 20.0d)
            Float sin = (Float) (Math.sin((Double) f) * 20.0d)
            Float cos2 = (Float) ((Math.cos((Double) f) * 15.0d) - (Math.sin((Double) f) * -5.0d))
            Float cos3 = (Float) ((Math.cos((Double) f) * -5.0d) + (Math.sin((Double) f) * 15.0d))
            Float cos4 = (Float) ((Math.cos((Double) f) * 15.0d) - (Math.sin((Double) f) * 5.0d))
            Float cos5 = (Float) ((Math.cos((Double) f) * 5.0d) + (Math.sin((Double) f) * 15.0d))
            paint.setStrokeWidth(3.0f)
            canvas.drawLine(width, width2, width + cos, width2 - sin, paint)
            canvas.drawLine(width + cos, width2 - sin, cos2 + width, width2 - cos3, paint)
            canvas.drawLine(width + cos, width2 - sin, width + cos4, width2 - cos5, paint)
        }
        if (z2) {
            paint.setStrokeWidth(2.0f)
            paint.setARGB(255, 255, 255, 0)
            canvas.drawCircle(width, width2, 10.0f, paint)
        }
    }

    private Unit handleTouch(Float f, Float f2) {
        UUID uuid
        UUID uuid2 = null
        if (this.userLocations != null && this.lastDrawRect != null) {
            Float applyDimension = TypedValue.applyDimension(1, USER_MARK_TOUCH_SLACK, getResources().getDisplayMetrics())
            Float f3 = 0.0f
            Iterator<T> it = this.userLocations.userPositions.entrySet().iterator()
            while (true) {
                uuid = uuid2
                Float f4 = f3
                if (!it.hasNext()) {
                    break
                }
                Map.Entry entry = (Map.Entry) it.next()
                ImmutableVector immutableVector = ((SLMinimap.UserLocation) entry.getValue()).location
                Float abs = Math.abs((((immutableVector.getX() / 256.0f) * ((Float) this.lastDrawRect.width())) + ((Float) this.lastDrawRect.left)) - f)
                Float abs2 = Math.abs(((((256.0f - immutableVector.getY()) / 256.0f) * ((Float) this.lastDrawRect.width())) + ((Float) this.lastDrawRect.top)) - f2)
                f3 = (Float) Math.sqrt((Double) ((abs2 * abs2) + (abs * abs)))
                if (f3 < applyDimension) {
                    if (uuid == null) {
                        uuid2 = (UUID) entry.getKey()
                    } else if (f3 < f4) {
                        uuid2 = (UUID) entry.getKey()
                    }
                }
                uuid2 = uuid
                f3 = f4
            }
            setSelectedUser(uuid)
            if (this.onUserClickListener != null) {
                this.onUserClickListener.onUserClick(uuid)
            }
        }
    }

    /* access modifiers changed from: protected */
    fun onDraw(Canvas canvas) {
        if (this.minimapBitmap != null) {
            Int width = getWidth()
            Int height = getHeight()
            Int round = Math.round(((Float) Math.min(width, height)) * this.actualZoomFactor)
            Int i = width / 2
            Int i2 = height / 2
            if (round <= width) {
                this.mapOffsetX = 0.0f
            }
            if (round <= height) {
                this.mapOffsetY = 0.0f
            }
            Int i3 = (i - (round / 2)) + ((Int) this.mapOffsetX)
            if (i3 > 0 && round > width) {
                this.mapOffsetX = (Float) ((round / 2) - i)
                i3 = (i - (round / 2)) + ((Int) this.mapOffsetX)
            }
            if (i3 + round <= width && round > width) {
                this.mapOffsetX = (Float) (((width - round) - i) + (round / 2))
                i3 = (i - (round / 2)) + ((Int) this.mapOffsetX)
            }
            Int i4 = (i2 - (round / 2)) + ((Int) this.mapOffsetY)
            if (i4 > 0 && round > height) {
                this.mapOffsetY = (Float) ((round / 2) - i2)
                i4 = (i2 - (round / 2)) + ((Int) this.mapOffsetY)
            }
            if (i4 + round <= height && round > height) {
                this.mapOffsetY = (Float) (((height - round) - i2) + (round / 2))
                i4 = (i2 - (round / 2)) + ((Int) this.mapOffsetY)
            }
            this.bitmapDstRect.set(i3, i4, i3 + round, round + i4)
            this.bitmapSrcRect.set(0, 0, this.minimapBitmap.getWidth(), this.minimapBitmap.getHeight())
            canvas.drawBitmap(this.minimapBitmap, this.bitmapSrcRect, this.bitmapDstRect, this.bitmapPaint)
            if (this.userLocations != null) {
                for (Map.Entry entry : this.userLocations.userPositions.entrySet()) {
                    drawUserMark(((SLMinimap.UserLocation) entry.getValue()).location, canvas, this.userMarkPaint, this.bitmapDstRect, false, Float.NaN, Objects.equal(this.selectedUser, entry.getKey()))
                }
                ImmutableVector immutableVector = this.userLocations.myAvatarPosition
                if (immutableVector != null) {
                    drawUserMark(immutableVector, canvas, this.userMarkPaint, this.bitmapDstRect, true, this.userLocations.myAvatarHeading, false)
                }
            }
            if (this.lastDrawRect == null) {
                this.lastDrawRect = Rect(this.bitmapDstRect)
            } else {
                this.lastDrawRect.set(this.bitmapDstRect)
            }
        }
    }

    /* access modifiers changed from: protected */
    fun onMeasure(Int i, Int i2) {
        Display defaultDisplay = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay()
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(this.displaySize)
        } else {
            this.displaySize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight())
        }
        Int min = Math.min(this.displaySize.x, this.displaySize.y)
        if (View.MeasureSpec.getMode(i) != 0) {
            min = Math.min(min, View.MeasureSpec.getSize(i))
        }
        if (View.MeasureSpec.getMode(i2) != 0) {
            min = Math.min(min, View.MeasureSpec.getSize(i2))
        }
        setMeasuredDimension(min, min)
    }

    public Boolean onTouchEvent(MotionEvent motionEvent) {
        Int i = 0
        this.scaleGestureDetector.onTouchEvent(motionEvent)
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.activePointerId = motionEvent.getPointerId(0)
                this.prevTouchX = motionEvent.getX()
                this.prevTouchY = motionEvent.getY()
                handleTouch(this.prevTouchX, this.prevTouchY)
                break
            case 1:
                this.activePointerId = -1
                break
            case 2:
                Int findPointerIndex = motionEvent.findPointerIndex(this.activePointerId)
                Float x = motionEvent.getX(findPointerIndex)
                Float y = motionEvent.getY(findPointerIndex)
                if (!this.scaleGestureDetector.isInProgress()) {
                    this.mapOffsetX = (x - this.prevTouchX) + this.mapOffsetX
                    this.mapOffsetY += y - this.prevTouchY
                    invalidate()
                }
                this.prevTouchX = x
                this.prevTouchY = y
                break
            case 3:
                this.activePointerId = -1
                break
            case 6:
                Int actionIndex = motionEvent.getActionIndex()
                if (motionEvent.getPointerId(actionIndex) == this.activePointerId) {
                    if (actionIndex == 0) {
                        i = 1
                    }
                    this.prevTouchX = motionEvent.getX(i)
                    this.prevTouchY = motionEvent.getY(i)
                    this.activePointerId = motionEvent.getPointerId(i)
                    break
                }
                break
        }
        return true
    }

    /* access modifiers changed from: package-private */
    fun setMinimapBitmap(SLMinimap.MinimapBitmap minimapBitmap2) {
        if (minimapBitmap2 == null) {
            if (this.minimapBitmap != null) {
                this.minimapBitmap.recycle()
                this.minimapBitmap = null
            }
        } else if (this.minimapBitmap == null) {
            this.minimapBitmap = minimapBitmap2.makeBitmap()
        } else {
            minimapBitmap2.updateBitmap(this.minimapBitmap)
        }
        invalidate()
    }

    /* access modifiers changed from: package-private */
    fun setOnUserClickListener(OnUserClickListener onUserClickListener2) {
        this.onUserClickListener = onUserClickListener2
    }

    /* access modifiers changed from: package-private */
    fun setSelectedUser(UUID uuid) {
        if (!Objects.equal(uuid, this.selectedUser)) {
            this.selectedUser = uuid
            invalidate()
        }
    }

    /* access modifiers changed from: package-private */
    fun setUserLocations(SLMinimap.UserLocations userLocations2) {
        this.userLocations = userLocations2
        invalidate()
    }
}
