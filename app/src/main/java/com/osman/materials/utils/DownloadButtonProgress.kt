package com.osman.materials.utils

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.osman.materials.R
import java.util.*
import kotlin.math.min

class DownloadButtonProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), View.OnClickListener {

    private var mIdleIcon: Drawable? = null
    private var mCancelIcon: Drawable? = null
    private var mFinishIcon: Drawable? = null
    private var mCancelable = DEF_CANCELABLE
    private var mIdleIconWidth = 0
    private var mIdleIconHeight = 0
    private var mCancelIconWidth = 0
    private var mCancelIconHeight = 0
    private var mFinishIconWidth = 0
    private var mFinishIconHeight = 0
    var currState = STATE_IDLE
        private set
    private var mMaxProgress = 100
    private var mCurrProgress = 0
    private var mIdleBgColor = DEF_BG_COLOR
    private var mFinishBgColor = DEF_BG_COLOR
    private var mIndeterminateBgColor = DEF_BG_COLOR
    private var mDeterminateBgColor = DEF_BG_COLOR
    private var mIdleBgDrawable: Drawable? = null
    private var mFinishBgDrawable: Drawable? = null
    private var mIndeterminateBgDrawable: Drawable? = null
    private var mDeterminateBgDrawable: Drawable? = null
    private var mIndeterminateAnimator: ValueAnimator? = null
    private var mCurrIndeterminateBarPos = 0
    private var mProgressIndeterminateSweepAngle = DEF_PROGRESS_INDETERMINATE_WIDTH
    private var mProgressDeterminateColor = DEF_DETERMINATE_COLOR
    private var mProgressIndeterminateColor = DEF_INDETERMINATE_COLOR
    private var mProgressMargin = DEF_PROGRESS_MARGIN
    private val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBgRect = RectF()
    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressRect = RectF()
    private val mClickListeners: MutableList<OnClickListener> = ArrayList()
    private val mOnStateChangedListeners: MutableList<OnStateChangedListener> = ArrayList()

    private fun initBackgroundDrawableFromAttribs(context: Context, attrs: TypedArray) {
        val idleResId =
            attrs.getResourceId(R.styleable.DownloadButtonProgress_idleBackgroundDrawable, -1)
        val finishResId =
            attrs.getResourceId(R.styleable.DownloadButtonProgress_finishBackgroundDrawable, -1)
        val indeterminateResId = attrs.getResourceId(
            R.styleable.DownloadButtonProgress_indeterminateBackgroundDrawable,
            -1
        )
        val determinateResId = attrs.getResourceId(
            R.styleable.DownloadButtonProgress_determinateBackgroundDrawable,
            -1
        )
        if (idleResId != -1) mIdleBgDrawable = ContextCompat.getDrawable(context, idleResId)
        if (finishResId != -1) mFinishBgDrawable = ContextCompat.getDrawable(context, finishResId)
        if (indeterminateResId != -1) mIndeterminateBgDrawable =
            ContextCompat.getDrawable(context, indeterminateResId)
        if (determinateResId != -1) mDeterminateBgDrawable =
            ContextCompat.getDrawable(context, determinateResId)
        mIdleBgColor =
            attrs.getColor(R.styleable.DownloadButtonProgress_idleBackgroundColor, DEF_BG_COLOR)
        mFinishBgColor =
            attrs.getColor(R.styleable.DownloadButtonProgress_finishBackgroundColor, DEF_BG_COLOR)
        mIndeterminateBgColor = attrs.getColor(
            R.styleable.DownloadButtonProgress_indeterminateBackgroundColor,
            DEF_BG_COLOR
        )
        mDeterminateBgColor = attrs.getColor(
            R.styleable.DownloadButtonProgress_determinateBackgroundColor,
            DEF_BG_COLOR
        )
    }

    var maxProgress: Int
        get() = mMaxProgress
        set(maxProgress) {
            mMaxProgress = maxProgress
            invalidate()
        }
    var currentProgress: Int
        get() = mCurrProgress
        set(progress) {
            if (currState != STATE_DETERMINATE) return
            mCurrProgress = min(progress, mMaxProgress)
            invalidate()
        }
    var idleIcon: Drawable?
        get() = mIdleIcon
        set(idleIcon) {
            mIdleIcon = idleIcon
            invalidate()
        }
    var cancelIcon: Drawable?
        get() = mCancelIcon
        set(cancelIcon) {
            mCancelIcon = cancelIcon
            invalidate()
        }
    var finishIcon: Drawable?
        get() = mFinishIcon
        set(finishIcon) {
            mFinishIcon = finishIcon
            invalidate()
        }
    var isCancelable: Boolean
        get() = mCancelable
        set(cancelable) {
            mCancelable = cancelable
            invalidate()
        }
    var idleIconWidth: Int
        get() = mIdleIconWidth
        set(idleIconWidth) {
            mIdleIconWidth = idleIconWidth
            invalidate()
        }
    var idleIconHeight: Int
        get() = mIdleIconHeight
        set(idleIconHeight) {
            mIdleIconHeight = idleIconHeight
            invalidate()
        }
    var cancelIconWidth: Int
        get() = mCancelIconWidth
        set(cancelIconWidth) {
            mCancelIconWidth = cancelIconWidth
            invalidate()
        }
    var cancelIconHeight: Int
        get() = mCancelIconHeight
        set(cancelIconHeight) {
            mCancelIconHeight = cancelIconHeight
            invalidate()
        }
    var finishIconWidth: Int
        get() = mFinishIconWidth
        set(finishIconWidth) {
            mFinishIconWidth = finishIconWidth
            invalidate()
        }
    var finishIconHeight: Int
        get() = mFinishIconHeight
        set(finishIconHeight) {
            mFinishIconHeight = finishIconHeight
            invalidate()
        }
    var idleBgColor: Int
        get() = mIdleBgColor
        set(idleBgColor) {
            mIdleBgColor = idleBgColor
            invalidate()
        }
    var finishBgColor: Int
        get() = mFinishBgColor
        set(finishBgColor) {
            mFinishBgColor = finishBgColor
            invalidate()
        }
    var indeterminateBgColor: Int
        get() = mIndeterminateBgColor
        set(indeterminateBgColor) {
            mIndeterminateBgColor = indeterminateBgColor
            invalidate()
        }
    var determinateBgColor: Int
        get() = mDeterminateBgColor
        set(determinateBgColor) {
            mDeterminateBgColor = determinateBgColor
            invalidate()
        }
    var idleBgDrawable: Drawable?
        get() = mIdleBgDrawable
        set(idleBgDrawable) {
            mIdleBgDrawable = idleBgDrawable
            invalidate()
        }
    var finishBgDrawable: Drawable?
        get() = mFinishBgDrawable
        set(finishBgDrawable) {
            mFinishBgDrawable = finishBgDrawable
            invalidate()
        }
    var indeterminateBgDrawable: Drawable?
        get() = mIndeterminateBgDrawable
        set(indeterminateBgDrawable) {
            mIndeterminateBgDrawable = indeterminateBgDrawable
            invalidate()
        }
    var determinateBgDrawable: Drawable?
        get() = mDeterminateBgDrawable
        set(determinateBgDrawable) {
            mDeterminateBgDrawable = determinateBgDrawable
            invalidate()
        }
    var progressDeterminateColor: Int
        get() = mProgressDeterminateColor
        set(progressDeterminateColor) {
            mProgressDeterminateColor = progressDeterminateColor
            invalidate()
        }
    var progressIndeterminateColor: Int
        get() = mProgressIndeterminateColor
        set(progressIndeterminateColor) {
            mProgressIndeterminateColor = progressIndeterminateColor
            invalidate()
        }
    var progressMargin: Int
        get() = mProgressMargin
        set(progressMargin) {
            mProgressMargin = progressMargin
            invalidate()
        }
    var progressIndeterminateSweepAngle: Int
        get() = mProgressIndeterminateSweepAngle
        set(progressIndeterminateSweepAngle) {
            mProgressIndeterminateSweepAngle = progressIndeterminateSweepAngle
            invalidate()
        }

    fun setIdle() {
        currState = STATE_IDLE
        callStateChangedListener(currState)
        invalidate()
    }

    fun setIndeterminate() {
        mCurrIndeterminateBarPos = BASE_START_ANGLE
        currState = STATE_INDETERMINATE
        callStateChangedListener(currState)
        invalidate()
        mIndeterminateAnimator!!.start()
    }

    fun setDeterminate() {
        mIndeterminateAnimator!!.end()
        mCurrProgress = 0
        currState = STATE_DETERMINATE
        callStateChangedListener(currState)
        invalidate()
    }

    fun setFinish() {
        mCurrProgress = 0
        currState = STATE_FINISHED
        callStateChangedListener(currState)
        invalidate()
    }

    fun addOnClickListener(listener: OnClickListener) {
        if (!mClickListeners.contains(listener)) mClickListeners.add(listener)
    }

    fun removeOnClickListener(listener: OnClickListener) {
        mClickListeners.remove(listener)
    }

    fun addOnStateChangedListeners(listener: OnStateChangedListener) {
        if (!mOnStateChangedListeners.contains(listener)) mOnStateChangedListeners.add(listener)
    }

    fun removeOnStateChangedListener(listener: OnStateChangedListener) {
        mOnStateChangedListeners.remove(listener)
    }

    private fun callStateChangedListener(newState: Int) {
        for (listener in mOnStateChangedListeners) listener.onStateChanged(newState)
    }

    override fun onClick(v: View) {
        if (!mCancelable && (currState == STATE_INDETERMINATE || currState == STATE_DETERMINATE)) return
        if (currState == STATE_IDLE) {
            for (listener in mClickListeners) listener.onIdleButtonClick(v)
        } else if (currState == STATE_INDETERMINATE || currState == STATE_DETERMINATE) {
            for (listener in mClickListeners) listener.onCancelButtonClick(v)
        } else if (currState == STATE_FINISHED) {
            for (listener in mClickListeners) listener.onFinishButtonClick(v)
        }
    }

    private fun drawIdleState(canvas: Canvas) {
        if (mIdleBgDrawable != null) {
            mIdleBgDrawable!!.setBounds(0, 0, width, height)
            mIdleBgDrawable!!.draw(canvas)
        } else {
            mBgRect[0f, 0f, width.toFloat()] = height.toFloat()
            mBgPaint.color = mIdleBgColor
            canvas.drawOval(mBgRect, mBgPaint)
        }
        drawDrawableInCenter(mIdleIcon, canvas, mIdleIconWidth, mIdleIconHeight)
    }

    private fun drawFinishState(canvas: Canvas) {
        if (mFinishBgDrawable != null) {
            mFinishBgDrawable!!.setBounds(0, 0, width, height)
            mFinishBgDrawable!!.draw(canvas)
        } else {
            mBgRect[0f, 0f, width.toFloat()] = height.toFloat()
            mBgPaint.color = mFinishBgColor
            canvas.drawOval(mBgRect, mBgPaint)
        }
        drawDrawableInCenter(mFinishIcon, canvas, mFinishIconWidth, mFinishIconHeight)
    }

    private fun drawIndeterminateState(canvas: Canvas) {
        if (mIndeterminateBgDrawable != null) {
            mIndeterminateBgDrawable!!.setBounds(0, 0, width, height)
            mIndeterminateBgDrawable!!.draw(canvas)
        } else {
            mBgRect[0f, 0f, width.toFloat()] = height.toFloat()
            mBgPaint.color = mIndeterminateBgColor
            canvas.drawOval(mBgRect, mBgPaint)
        }
        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight)
        }
        setProgressRectBounds()
        mProgressPaint.color = mProgressIndeterminateColor
        canvas.drawArc(
            mProgressRect,
            mCurrIndeterminateBarPos.toFloat(),
            mProgressIndeterminateSweepAngle.toFloat(),
            false,
            mProgressPaint
        )
    }

    private fun drawDeterminateState(canvas: Canvas) {
        if (mDeterminateBgDrawable != null) {
            mDeterminateBgDrawable!!.setBounds(0, 0, width, height)
            mDeterminateBgDrawable!!.draw(canvas)
        } else {
            mBgRect[0f, 0f, width.toFloat()] = height.toFloat()
            mBgPaint.color = mDeterminateBgColor
            canvas.drawOval(mBgRect, mBgPaint)
        }
        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight)
        }
        setProgressRectBounds()
        mProgressPaint.color = mProgressDeterminateColor
        canvas.drawArc(mProgressRect, BASE_START_ANGLE.toFloat(), degrees, false, mProgressPaint)
    }

    override fun onDraw(canvas: Canvas) {
        if (currState == STATE_IDLE) {
            drawIdleState(canvas)
        } else if (currState == STATE_INDETERMINATE) {
            drawIndeterminateState(canvas)
        } else if (currState == STATE_DETERMINATE) {
            drawDeterminateState(canvas)
        } else if (currState == STATE_FINISHED) {
            drawFinishState(canvas)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_MAX_PROGRESS, maxProgress)
        bundle.putInt(INSTANCE_CURRENT_PROGRESS, currentProgress)
        bundle.putInt(INSTANCE_CURRENT_STATE, currState)
        bundle.putBoolean(INSTANCE_CANCELABLE, isCancelable)
        bundle.putInt(INSTANCE_IDLE_WIDTH, idleIconWidth)
        bundle.putInt(INSTANCE_IDLE_HEIGHT, idleIconHeight)
        bundle.putInt(INSTANCE_CANCEL_WIDTH, cancelIconWidth)
        bundle.putInt(INSTANCE_CANCEL_HEIGHT, cancelIconHeight)
        bundle.putInt(INSTANCE_FINISH_WIDTH, finishIconWidth)
        bundle.putInt(INSTANCE_FINISH_HEIGHT, finishIconHeight)
        bundle.putInt(INSTANCE_IDLE_BG_COLOR, idleBgColor)
        bundle.putInt(INSTANCE_FINISH_BG_COLOR, finishBgColor)
        bundle.putInt(INSTANCE_INDETERMINATE_BG_COLOR, indeterminateBgColor)
        bundle.putInt(INSTANCE_DETERMINATE_BG_COLOR, determinateBgColor)
        bundle.putInt(INSTANCE_PROGRESS_DETERMINATE_COLOR, progressDeterminateColor)
        bundle.putInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR, progressIndeterminateColor)
        bundle.putInt(INSTANCE_PROGRESS_MARGIN, progressMargin)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            mMaxProgress = state.getInt(INSTANCE_MAX_PROGRESS)
            mCurrProgress = state.getInt(INSTANCE_CURRENT_PROGRESS)
            currState = state.getInt(INSTANCE_CURRENT_STATE)
            mCancelable = state.getBoolean(INSTANCE_CANCELABLE)
            mIdleIconWidth = state.getInt(INSTANCE_IDLE_WIDTH)
            mIdleIconHeight = state.getInt(INSTANCE_IDLE_HEIGHT)
            mCancelIconWidth = state.getInt(INSTANCE_CANCEL_WIDTH)
            mCancelIconHeight = state.getInt(INSTANCE_CANCEL_HEIGHT)
            mFinishIconWidth = state.getInt(INSTANCE_FINISH_WIDTH)
            mFinishIconHeight = state.getInt(INSTANCE_FINISH_HEIGHT)
            mIdleBgColor = state.getInt(INSTANCE_IDLE_BG_COLOR)
            mFinishBgColor = state.getInt(INSTANCE_FINISH_BG_COLOR)
            mIndeterminateBgColor = state.getInt(INSTANCE_INDETERMINATE_BG_COLOR)
            mDeterminateBgColor = state.getInt(INSTANCE_DETERMINATE_BG_COLOR)
            mProgressDeterminateColor = state.getInt(INSTANCE_PROGRESS_DETERMINATE_COLOR)
            mProgressIndeterminateColor = state.getInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR)
            mProgressMargin = state.getInt(INSTANCE_PROGRESS_MARGIN)
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
            if (currState == STATE_INDETERMINATE) mIndeterminateAnimator!!.start()
            return
        }
        super.onRestoreInstanceState(state)
    }

    private fun setProgressRectBounds() {
        val halfStroke = mProgressPaint.strokeWidth / 2.0f
        val totalMargin = mProgressMargin + halfStroke
        mProgressRect[totalMargin, totalMargin, width - totalMargin] = height - totalMargin
    }

    private fun initIndeterminateAnimator() {
        mIndeterminateAnimator = ValueAnimator.ofInt(0, 360)
        mIndeterminateAnimator!!.interpolator = LinearInterpolator()
        mIndeterminateAnimator!!.duration = 1000
        mIndeterminateAnimator!!.repeatCount = ValueAnimator.INFINITE
        mIndeterminateAnimator!!.repeatMode = ValueAnimator.RESTART
        mIndeterminateAnimator!!.addUpdateListener(AnimatorUpdateListener { animation ->
            val value = animation.animatedValue as Int
            mCurrIndeterminateBarPos = value - BASE_START_ANGLE
            invalidate()
        })
    }

    private val degrees: Float
        get() = mCurrProgress.toFloat() / mMaxProgress.toFloat() * 360

    private fun drawDrawableInCenter(drawable: Drawable?, canvas: Canvas, width: Int, height: Int) {
        val left = getWidth() / 2 - width / 2
        val top = getHeight() / 2 - height / 2
        drawable!!.setBounds(left, top, left + width, top + height)
        drawable.draw(canvas)
    }

    interface OnClickListener {
        fun onIdleButtonClick(view: View?)
        fun onCancelButtonClick(view: View?)
        fun onFinishButtonClick(view: View?)
    }

    interface OnStateChangedListener {
        fun onStateChanged(newState: Int)
    }

    companion object {
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_MAX_PROGRESS = "max_progress"
        private const val INSTANCE_CURRENT_PROGRESS = "current_progress"
        private const val INSTANCE_CURRENT_STATE = "current_state"
        private const val INSTANCE_CANCELABLE = "cancelable"
        private const val INSTANCE_IDLE_WIDTH = "idle_width"
        private const val INSTANCE_IDLE_HEIGHT = "idle_height"
        private const val INSTANCE_CANCEL_WIDTH = "cancel_width"
        private const val INSTANCE_CANCEL_HEIGHT = "cancel_height"
        private const val INSTANCE_FINISH_WIDTH = "finish_width"
        private const val INSTANCE_FINISH_HEIGHT = "finish_height"
        private const val INSTANCE_IDLE_BG_COLOR = "idle_bg_color"
        private const val INSTANCE_FINISH_BG_COLOR = "finish_bg_color"
        private const val INSTANCE_INDETERMINATE_BG_COLOR = "indeterminate_bg_color"
        private const val INSTANCE_DETERMINATE_BG_COLOR = "determinate_bg_color"
        private const val INSTANCE_PROGRESS_DETERMINATE_COLOR = "prog_det_color"
        private const val INSTANCE_PROGRESS_INDETERMINATE_COLOR = "prog_indet_color"
        private const val INSTANCE_PROGRESS_MARGIN = "prog_margin"
        const val STATE_IDLE = 1
        const val STATE_INDETERMINATE = 2
        const val STATE_DETERMINATE = 3
        const val STATE_FINISHED = 4
        private const val BASE_START_ANGLE = -90
        private const val DEF_BG_COLOR = -0x4c000000
        private const val DEF_CANCELABLE = true
        private const val DEF_DETERMINATE_COLOR = Color.GREEN
        private const val DEF_INDETERMINATE_COLOR = Color.WHITE
        private const val DEF_PROGRESS_WIDTH = 8
        private const val DEF_PROGRESS_MARGIN = 5
        private const val DEF_PROGRESS_INDETERMINATE_WIDTH = 90
    }

    init {
        super.setOnClickListener(this)
        initIndeterminateAnimator()
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.isDither = true
        mProgressPaint.strokeJoin = Paint.Join.ROUND
        mProgressPaint.strokeCap = Paint.Cap.ROUND
        mProgressPaint.pathEffect = CornerPathEffect(50f)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DownloadButtonProgress, 0, 0)
            initBackgroundDrawableFromAttribs(context, a)
            currState = a.getInt(R.styleable.DownloadButtonProgress_state, STATE_IDLE)
            mCancelable =
                a.getBoolean(R.styleable.DownloadButtonProgress_cancelable, DEF_CANCELABLE)
            mProgressIndeterminateSweepAngle = a.getInteger(
                R.styleable.DownloadButtonProgress_progressIndeterminateSweepAngle,
                DEF_PROGRESS_INDETERMINATE_WIDTH
            )
            mProgressDeterminateColor = a.getColor(
                R.styleable.DownloadButtonProgress_progressDeterminateColor,
                DEF_DETERMINATE_COLOR
            )
            mProgressIndeterminateColor = a.getColor(
                R.styleable.DownloadButtonProgress_progressIndeterminateColor,
                DEF_INDETERMINATE_COLOR
            )
            mProgressPaint.strokeWidth = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_progressWidth,
                DEF_PROGRESS_WIDTH
            )
                .toFloat()
            mProgressMargin = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_progressMargin,
                DEF_PROGRESS_MARGIN
            )
            mCurrProgress = a.getInteger(R.styleable.DownloadButtonProgress_currentProgress, 0)
            mMaxProgress = a.getInteger(R.styleable.DownloadButtonProgress_maxProgress, 100)
            val icIdleDrawableId = a.getResourceId(
                R.styleable.DownloadButtonProgress_idleIconDrawable,
                R.drawable.ic_download
            )
            mIdleIcon = ContextCompat.getDrawable(context, icIdleDrawableId)
            mIdleIconWidth = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_idleIconWidth,
                mIdleIcon!!.minimumWidth
            )
            mIdleIconHeight = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_idleIconHeight,
                mIdleIcon!!.minimumHeight
            )
            val icCancelDrawableId = a.getResourceId(
                R.styleable.DownloadButtonProgress_cancelIconDrawable,
                R.drawable.ic_cancel
            )
            mCancelIcon = ContextCompat.getDrawable(context, icCancelDrawableId)
            mCancelIconWidth = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_cancelIconWidth,
                mCancelIcon!!.minimumWidth
            )
            mCancelIconHeight = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_cancelIconHeight,
                mCancelIcon!!.minimumHeight
            )
            val icFinishDrawableId = a.getResourceId(
                R.styleable.DownloadButtonProgress_finishIconDrawable,
                R.drawable.ic_finish
            )
            mFinishIcon = ContextCompat.getDrawable(context, icFinishDrawableId)
            mFinishIconWidth = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_finishIconWidth,
                mFinishIcon!!.minimumWidth
            )
            mFinishIconHeight = a.getDimensionPixelSize(
                R.styleable.DownloadButtonProgress_finishIconHeight,
                mFinishIcon!!.minimumHeight
            )
            a.recycle()
        } else {
            mProgressPaint.strokeWidth = DEF_PROGRESS_WIDTH.toFloat()
            mIdleIcon = ContextCompat.getDrawable(context, R.drawable.ic_download)
            mIdleIconWidth = mIdleIcon!!.minimumWidth
            mIdleIconHeight = mIdleIcon!!.minimumHeight
            mCancelIcon = ContextCompat.getDrawable(context, R.drawable.ic_cancel)
            mCancelIconWidth = mCancelIcon!!.minimumWidth
            mCancelIconHeight = mCancelIcon!!.minimumHeight
            mFinishIcon = ContextCompat.getDrawable(context, R.drawable.ic_finish)
            mFinishIconWidth = mFinishIcon!!.minimumWidth
            mFinishIconHeight = mFinishIcon!!.minimumHeight
        }
        if (currState == STATE_INDETERMINATE) setIndeterminate()
    }
}