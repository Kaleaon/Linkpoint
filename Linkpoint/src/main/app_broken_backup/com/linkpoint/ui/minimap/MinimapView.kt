package com.linkpoint.ui.minimap

import kotlin.math.*

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
import androidx.annotation.Nullable

class MinimapView : View {
    private val USER_MARK_TOUCH_SLACK: Float = 50.0f
    private Int activePointerId = -1
    /* access modifiers changed from: private */
    var actualZoomFactor: Float = 1.0f
    private Rect bitmapDstRect = Rect()
    private Paint bitmapPaint = Paint()
    private Rect bitmapSrcRect = Rect()
    private Point displaySize = Point()
    private Rect lastDrawRect = null
    private Float mapOffsetX = 0.0f
    private Float mapOffsetY = 0.0f
    @Nullable
    private Bitmap minimapBitmap
    private OnUserClickListener onUserClickListener = null
    private Float prevTouchX = 0.0f
    private Float prevTouchY = 0.0f
    private ScaleGestureDetector scaleGestureDetector
    private ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = ScaleGestureDetector.SimpleOnScaleGestureListener() {
        fun onScale(ScaleGestureDetector scaleGestureDetector): Boolean {
            var unused: Float = MinimapView.this.actualZoomFactor = min(max(MinimapView.this.actualZoomFactor * scaleGestureDetector.getScaleFactor(), 1.0f), 5.0f)
            MinimapView.this.invalidate()
            return true
        }
    }
    @Nullable
    private val selectedUser: UUID = null
    @Nullable
    private SLMinimap.UserLocations userLocations
    private Paint userMarkPaint = Paint()

    interface OnUserClickListener {
        fun onUserClick(UUID uuid)
    }

    MinimapView(Context context) {
        super(context)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    MinimapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    MinimapView(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        this.scaleGestureDetector = ScaleGestureDetector(context, this.scaleGestureListener)
    }

    private Unit drawUserMark(ImmutableVector immutableVector, Canvas canvas, Paint paint, Rect rect, Boolean z, Float f, Boolean z2) {
        var x: Float = immutableVector.getX()
        var y: Float = immutableVector.getY()
        var width: Float = (rect.toFloat().left) + ((x / 256.0f) * (rect.toFloat().width()))
        var width2: Float = (rect.toFloat().top) + (((256.0f - y) / 256.0f) * (rect.toFloat().width()))
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
            var cos: Float = (Float) (cos(f.toDouble()) * 20.0d)
            var sin: Float = (Float) (sin(f.toDouble()) * 20.0d)
            var cos2: Float = (Float) ((cos(f.toDouble()) * 15.0d) - (sin(f.toDouble()) * -5.0d))
            var cos3: Float = (Float) ((cos(f.toDouble()) * -5.0d) + (sin(f.toDouble()) * 15.0d))
            var cos4: Float = (Float) ((cos(f.toDouble()) * 15.0d) - (sin(f.toDouble()) * 5.0d))
            var cos5: Float = (Float) ((cos(f.toDouble()) * 5.0d) + (sin(f.toDouble()) * 15.0d))
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
            var applyDimension: Float = TypedValue.applyDimension(1, USER_MARK_TOUCH_SLACK, getResources().getDisplayMetrics())
            var f3: Float = 0.0f
            Iterator<T> it = this.userLocations.userPositions.entrySet().iterator()
            while (true) {
                uuid = uuid2
                var f4: Float = f3
                if (!it.hasNext()) {
                    break
                }
                Map.Entry entry = (Map.Entry) it.next()
                ImmutableVector immutableVector = ((SLMinimap.UserLocation) entry.getValue()).location
                var abs: Float = abs((((immutableVector.getX() / 256.0f) * (this.toFloat().lastDrawRect.width())) + (this.toFloat().lastDrawRect.left)) - f)
                var abs2: Float = abs(((((256.0f - immutableVector.getY()) / 256.0f) * (this.toFloat().lastDrawRect.width())) + (this.toFloat().lastDrawRect.top)) - f2)
                f3 = sqrt(((abs2 * abs2.toDouble()).toFloat() + (abs * abs)))
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
    fun onDraw(Canvas canvas)  {
        if (this.minimapBitmap != null) {
            var width: Int = getWidth()
            var height: Int = getHeight()
            var round: Int = Math.round((Math.toFloat().min(width, height)) * this.actualZoomFactor)
            var i: Int = width / 2
            var i2: Int = height / 2
            if (round <= width) {
                this.mapOffsetX = 0.0f
            }
            if (round <= height) {
                this.mapOffsetY = 0.0f
            }
            var i3: Int = (i - (round / 2)) + (this.toInt().mapOffsetX)
            if (i3 > 0 && round > width) {
                this.mapOffsetX = (Float) ((round / 2) - i)
                i3 = (i - (round / 2)) + (this.toInt().mapOffsetX)
            }
            if (i3 + round <= width && round > width) {
                this.mapOffsetX = (Float) (((width - round) - i) + (round / 2))
                i3 = (i - (round / 2)) + (this.toInt().mapOffsetX)
            }
            var i4: Int = (i2 - (round / 2)) + (this.toInt().mapOffsetY)
            if (i4 > 0 && round > height) {
                this.mapOffsetY = (Float) ((round / 2) - i2)
                i4 = (i2 - (round / 2)) + (this.toInt().mapOffsetY)
            }
            if (i4 + round <= height && round > height) {
                this.mapOffsetY = (Float) (((height - round) - i2) + (round / 2))
                i4 = (i2 - (round / 2)) + (this.toInt().mapOffsetY)
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
    fun onMeasure(Int i, Int i2)  {
        Display defaultDisplay = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay()
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(this.displaySize)
        } else {
            this.displaySize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight())
        }
        var min: Int = min(this.displaySize.x, this.displaySize.y)
        if (View.MeasureSpec.getMode(i) != 0) {
            min = min(min, View.MeasureSpec.getSize(i))
        }
        if (View.MeasureSpec.getMode(i2) != 0) {
            min = min(min, View.MeasureSpec.getSize(i2))
        }
        setMeasuredDimension(min, min)
    }

    fun onTouchEvent(MotionEvent motionEvent): Boolean {
        var i: Int = 0
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
                var findPointerIndex: Int = motionEvent.findPointerIndex(this.activePointerId)
                var x: Float = motionEvent.getX(findPointerIndex)
                var y: Float = motionEvent.getY(findPointerIndex)
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
                var actionIndex: Int = motionEvent.getActionIndex()
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
    fun setMinimapBitmap(@Nullable SLMinimap.MinimapBitmap minimapBitmap2)  {
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
    fun setOnUserClickListener(OnUserClickListener onUserClickListener2)  {
        this.onUserClickListener = onUserClickListener2
    }

    /* access modifiers changed from: package-private */
    fun setSelectedUser(@Nullable UUID uuid)  {
        if (!Objects.equal(uuid, this.selectedUser)) {
            this.selectedUser = uuid
            invalidate()
        }
    }

    /* access modifiers changed from: package-private */
    fun setUserLocations(@Nullable SLMinimap.UserLocations userLocations2)  {
        this.userLocations = userLocations2
        invalidate()
    }
}
