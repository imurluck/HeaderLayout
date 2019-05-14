package com.zzx.headerlayout_kotlin

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.view.*
import com.zzx.headerlayout_kotlin.transformation.AlphaTransformationBehavior
import com.zzx.headerlayout_kotlin.transformation.ExtendScaleTransformationBehavior
import com.zzx.headerlayout_kotlin.transformation.TransformationBehavior

@DefaultBehavior(HeaderLayout.HeaderLayoutBehavior::class)
class HeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    var maxHeight = 600

    var minHeight = 100

    var extendHeight = 200

    private var scrollState = ScrollState.STATE_MAX_HEIGHT

    enum class ScrollState {
        /**
         * 收缩到了最小高度
         */
        STATE_MIN_HEIGHT,
        /**
         * 在最小高度与最大高度之间
         */
        STATE_NORMAL_PROCESS,
        /**
         * 伸展到了最大高度
         */
        STATE_MAX_HEIGHT,
        /**
         * 拓高，在最大高度与拓展最大高度之间
         */
        STATE_EXTEND_PROCESS,
        /**
         * 到了拓展最大高度
         */
        STATE_EXTEND_MAX_END
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        maxHeight = measuredHeight

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = LayoutParams(context, attrs)
    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        LayoutParams(super.generateLayoutParams(lp))


    override fun getBehavior(): Behavior<*> = HeaderLayoutBehavior(context)

    private fun dispatchTransformationBehaviors(scrollState: ScrollState, percent: Float = 1.0f) {
        for (child in children) {
            val layoutParams = child.layoutParams as LayoutParams
            if (layoutParams.transformationBehaviors != null && layoutParams.transformationBehaviors!!.size > 0) {
                for (behavior in layoutParams.transformationBehaviors!!) {
                    when (scrollState) {
                        ScrollState.STATE_MIN_HEIGHT -> behavior.onStateMinHeight(child, this)
                        ScrollState.STATE_NORMAL_PROCESS -> behavior.onStateNormalProcess(child, this, percent)
                        ScrollState.STATE_MAX_HEIGHT -> behavior.onStateMaxHeight(child, this)
                        ScrollState.STATE_EXTEND_PROCESS -> behavior.onStateExtendProcess(child, this, percent)
                        ScrollState.STATE_EXTEND_MAX_END -> behavior.onStateExtendMaxEnd(child, this)
                    }
                }
            }
        }
    }

    open class HeaderLayoutBehavior(
        context: Context? = null,
        attrs: AttributeSet? = null
    ) : Behavior<HeaderLayout>(context, attrs) {

        private var childUnConsumedDy = 0

        private var isBackAnimationDo = false

        override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
        ): Boolean {
            childUnConsumedDy = 0
            isBackAnimationDo = false
            return ViewCompat.SCROLL_AXIS_VERTICAL and axes != 0
        }

        override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
        ) {
            if (dy > 0 && child.scrollState > ScrollState.STATE_MIN_HEIGHT) {
                //手指上滑时，HeaderLayout的scrollState大于最小高度状态才有效果
                consumed[1] = preScrollUp(child, dy)
            } else if (dy < 0 && childUnConsumedDy < 0 && canScrollDown()) {
                //手指下滑时，需要等target view滑到顶部且还有未消耗完的dy,才将滑动交给HeaderLayout处理
                //且分为两种情况，如果是fling产生的滑动，由于每次分发的dy都是剩下未滑动完的位移，需要特殊处理
                if (type == ViewCompat.TYPE_NON_TOUCH) {
                    consumed[1] = preScrollDown(child, dy, true)
                } else {
                    consumed[1] = preScrollDown(child, dy, false)
                }
            }
        }

        private fun canScrollDown(): Boolean {
            return !isBackAnimationDo
        }

        /**
         * 手指下滑
         */
        private fun preScrollDown(headerLayout: HeaderLayout, dy: Int, fling: Boolean): Int {
            if (headerLayout.scrollState == ScrollState.STATE_EXTEND_MAX_END) {
                return 0
            }
            if (fling && headerLayout.scrollState >= ScrollState.STATE_MAX_HEIGHT) {
                return 0
            }
            var consumedDy = 0
            headerLayout.apply {
                //上滑到了最大拓展高度
                if (bottom - dy >= maxHeight + extendHeight) {
                    if (fling && Math.abs(dy) >= (maxHeight + extendHeight - minHeight)) {
                        bottom = maxHeight
                        consumedDy = bottom - minHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        dispatchTransformationBehaviors(scrollState)
                    } else {
                        consumedDy = bottom - (maxHeight + extendHeight)
                        bottom = maxHeight + extendHeight
                        scrollState = ScrollState.STATE_EXTEND_MAX_END
                        dispatchTransformationBehaviors(scrollState)
                    }
                } else {
                    var unConsumedDy = dy
                    if (bottom < maxHeight && bottom - dy < maxHeight) {
                        consumedDy = dy
                        bottom -= dy
                        scrollState = ScrollState.STATE_NORMAL_PROCESS
                        val percent = (bottom - minHeight).toFloat() / (maxHeight - minHeight).toFloat()
                        dispatchTransformationBehaviors(scrollState, percent)
                    }
                    if (bottom < maxHeight && bottom - dy >= maxHeight) {
                        consumedDy = bottom - maxHeight
                        unConsumedDy = dy - consumedDy
                        bottom = maxHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        dispatchTransformationBehaviors(scrollState)
                    }
                    if (bottom >= maxHeight && bottom - unConsumedDy > maxHeight) {
                        if (fling) {
                            return@apply
                        }
                        consumedDy += unConsumedDy
                        bottom -= unConsumedDy
                        scrollState = ScrollState.STATE_EXTEND_PROCESS
                        val percent = (bottom - maxHeight).toFloat() / extendHeight
                        dispatchTransformationBehaviors(scrollState, percent)
                    }
                }
            }
            return consumedDy
        }

        /**
         * 手指上滑
         */
        private fun preScrollUp(headerLayout: HeaderLayout, dy: Int): Int {
            if (headerLayout.scrollState == ScrollState.STATE_MIN_HEIGHT) {
                return 0
            }
            var consumedDy = 0
            headerLayout.apply {
                //上滑之后到了最小高度
                if (bottom - dy <= minHeight) {
                    consumedDy = bottom - minHeight
                    bottom = minHeight
                    scrollState = ScrollState.STATE_MIN_HEIGHT
                    dispatchTransformationBehaviors(scrollState)
                } else {
                    consumedDy = dy
                    var unConsumedDy = dy
                    //之前在最大伸展高度与拓展的高度之间，上滑之后还在此区间
                    if (bottom > maxHeight && bottom - dy > maxHeight) {
                        bottom -= dy
                        val percent = (bottom - maxHeight).toFloat() / extendHeight.toFloat()
                        scrollState = ScrollState.STATE_EXTEND_PROCESS
                        dispatchTransformationBehaviors(scrollState, percent)
                    }
                    //之前在最大伸展高度与拓展的高度之间, 上滑之后小于了最大高度
                    if (bottom > maxHeight && bottom - dy <= maxHeight) {
                        unConsumedDy = dy - (bottom - maxHeight)
                        bottom = maxHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        dispatchTransformationBehaviors(scrollState)
                    }
                    //之前在最小高度和最大高度之间
                    if (bottom <= maxHeight && bottom - unConsumedDy < maxHeight) {
                        bottom -= unConsumedDy
                        val percent = (bottom - minHeight).toFloat() / (maxHeight - minHeight).toFloat()
                        scrollState = ScrollState.STATE_NORMAL_PROCESS
                        dispatchTransformationBehaviors(scrollState, percent)
                    }
                }

            }
            return consumedDy
        }

        private fun backToMaxHeight(headerLayout: HeaderLayout) {
            if (headerLayout.scrollState <= ScrollState.STATE_MAX_HEIGHT) {
                return
            }
            isBackAnimationDo = true
            ValueAnimator.ofFloat(headerLayout.bottom.toFloat(), headerLayout.maxHeight.toFloat()).apply {
                addUpdateListener { animator ->
                    Log.e(TAG, "backToMaxHeight -> animatedValue = ${animator.animatedValue} bottom=${headerLayout.bottom}")
                    val dy = (headerLayout.bottom - animator.animatedValue as Float).toInt()
                    Log.e(TAG, "backToMaxHeight -> dy=$dy")
                    preScrollUp(headerLayout, dy)
                }
                duration = 300L
            }.start()
        }

        override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int
        ) {
            childUnConsumedDy = dyUnconsumed
        }

        override fun onStopNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            target: View,
            type: Int
        ) {
            backToMaxHeight(child)
        }
    }

    class LayoutParams : FrameLayout.LayoutParams {

        private var transformationFlags = 0x00

        var transformationBehaviors: MutableList<TransformationBehavior<View>>? = null

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
        constructor(source: FrameLayout.LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderLayout_Transformation)
            transformationFlags = a.getInt(R.styleable.HeaderLayout_Transformation_transformation_behavior, 0x00)
            parseTransformationBehaviors(transformationFlags)
            a.recycle()
        }

        private fun parseTransformationBehaviors(transformationFlags: Int) {
            if (transformationFlags and TRANSFORMATION_NOTHING != 0) {
                return
            }
            transformationBehaviors = mutableListOf<TransformationBehavior<View>>().apply {
                if (transformationFlags and TRANSFORMATION_ALPHA != 0) {
                    add(AlphaTransformationBehavior())
                }
                if (transformationFlags and TRANSFORMATION_EXTEND_SCALE != 0) {
                    add(ExtendScaleTransformationBehavior())
                }
            }
        }

        companion object {

            private const val TRANSFORMATION_NOTHING = 0x00
            private const val TRANSFORMATION_SCROLL = 0x01
            private const val TRANSFORMATION_ALPHA = 0x02
            private const val TRANSFORMATION_EXTEND_SCALE = 0x04
        }


    }

    companion object {
        private const val TAG = "HeaderLayout"
    }

}