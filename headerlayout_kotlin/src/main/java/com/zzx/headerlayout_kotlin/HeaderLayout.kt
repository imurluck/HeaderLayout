package com.zzx.headerlayout_kotlin

import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.TypedValue.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.zzx.headerlayout_kotlin.HeaderLayout.ScrollState
import com.zzx.headerlayout_kotlin.transformation.*

/**
 * 头部布局，定义了头部布局的五中状态， 状态信息请看[ScrollState],
 * 在滑动过程中会将滑动的一些信息传递给[Transformation], [Transformation]是
 * 子View需要设置的，设置了[Transformation]的子View则会接收到[HeaderLayout]滑动时的一些
 * 信息，如滑动距离dy等。子View则可以根据这些信息来做相应的改变以达到联动的效果
 * @author zzx
 * @createAt 19-5-15
 */
@DefaultBehavior(HeaderLayout.HeaderLayoutBehavior::class)
class HeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    var maxHeight = 0

    var minHeight = 0

    var extendHeight = DEFAULT_EXTEND_HEIGHT

    var extendHeightFraction = 0.0f

    private var scrollState = ScrollState.STATE_MAX_HEIGHT

    var hasLayouted = false

    var onFlingMaxHeight: ((HeaderLayout) -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderLayout)
        val extendHeightValue = TypedValue()
        val hasValue = a.getValue(R.styleable.HeaderLayout_extend_height, extendHeightValue)
        if (hasValue) {
            extendHeightValue.apply {
                when (type) {
                    TYPE_FRACTION -> extendHeightFraction = getFraction(1.0f, 1.0f)
                    TYPE_FLOAT -> extendHeightFraction = float
                    TYPE_DIMENSION -> extendHeight = getDimension(getDisplayMetrics(context)).toInt()
                }
            }
        }
        post {
            hasLayouted = true
        }
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

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
        if (extendHeightFraction != 0.0f) {
            extendHeight = (maxHeight * extendHeightFraction).toInt()
        }
        if (maxHeight == 0) {
            throw IllegalStateException("the height of HeaderLayout can't be 0")
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (hasLayouted) {
            //表示是由requestLayout引发的，不需要重新布局子View
            return
        }
        super.onLayout(changed, left, top, right, bottom)
        minHeight = 0
        for (child in children) {
            (child.layoutParams as LayoutParams).apply {
                if (stickyUntilExit) {
                    minTop = minHeight
                    minHeight += child.height
                }
            }
        }
    }

    fun offsetChild(child: View, dy: Int) {
        (child.layoutParams as LayoutParams).apply {
            if (stickyUntilExit) {
                var unConsumedDy = dy
                if (dy < 0) {
                    if (minTopOffset != 0) {
                        if (dy + minTopOffset < 0) {
                            unConsumedDy = dy + minTopOffset
                            minTopOffset = 0
                        } else {
                            minTopOffset += dy
                            unConsumedDy = 0
                        }
                    }
                    if (unConsumedDy != 0) {
                        ViewCompat.offsetTopAndBottom(child, -unConsumedDy)
                    }
                } else {
                    if (child.top == minTop) {
                        minTopOffset += dy
                        unConsumedDy = 0
                    } else if (child.top - dy < minTop) {
                        unConsumedDy = child.top - minTop
                        minTopOffset = dy - (child.top - minTop)
                    }
                    ViewCompat.offsetTopAndBottom(child, -unConsumedDy)
                }
            } else {
                ViewCompat.offsetTopAndBottom(child, -dy)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = LayoutParams(context, attrs)
    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        LayoutParams(super.generateLayoutParams(lp))


    override fun getBehavior(): Behavior<*> = HeaderLayoutBehavior(context)

    private fun dispatchTransformationBehaviors(scrollState: ScrollState, dy: Int, percent: Float = 1.0f) {
        for (child in children) {
            val layoutParams = child.layoutParams as LayoutParams
            if (layoutParams.transformations != null && layoutParams.transformations!!.size > 0) {
                for (behavior in layoutParams.transformations!!) {
                    when (scrollState) {
                        ScrollState.STATE_MIN_HEIGHT -> behavior.onStateMinHeight(child, this, dy)
                        ScrollState.STATE_NORMAL_PROCESS -> behavior.onStateNormalProcess(child, this, percent, dy)
                        ScrollState.STATE_MAX_HEIGHT -> behavior.onStateMaxHeight(child, this, dy)
                        ScrollState.STATE_EXTEND_PROCESS -> behavior.onStateExtendProcess(child, this, percent, dy)
                        ScrollState.STATE_EXTEND_MAX_END -> behavior.onStateExtendMaxEnd(child, this, dy)
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

        private var backAnimation: ValueAnimator? = null

        private var canAcceptFling = false

        private var canAcceptFlingCallback = false

        private var canAcceptScroll = false

        override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
        ): Boolean {
            if (ViewCompat.SCROLL_AXIS_VERTICAL and axes != 0) {
                childUnConsumedDy = 0
                canAcceptFling = true
                canAcceptScroll = true
                resetBackAnimation()
                return true
            }

            return false
        }

        private fun resetBackAnimation() {
            backAnimation?.apply {
                if (isRunning) {
                    backAnimation!!.cancel()
                    isBackAnimationDo = false
                }
            }
        }

        override fun onLayoutChild(parent: CoordinatorLayout, child: HeaderLayout, layoutDirection: Int): Boolean {
            return if (child.hasLayouted) {
                //第一次布局加载完成之后，因requestLayout造成的再次layout应是上一次的位置，
                // 需要保持滑动后的原样
                child.layout(child.left, child.top, child.right, child.bottom)
                true
            } else {
                super.onLayoutChild(parent, child, layoutDirection)
            }
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
                if (type == ViewCompat.TYPE_NON_TOUCH && canAcceptFling) {
                    consumed[1] = preScrollDown(child, dy, true)
                } else if (type == ViewCompat.TYPE_TOUCH && canAcceptScroll) {
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
            if (headerLayout.scrollState >= ScrollState.STATE_EXTEND_MAX_END) {
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
                        dispatchTransformationBehaviors(scrollState, consumedDy)
                    } else {
                        consumedDy = bottom - (maxHeight + extendHeight)
                        bottom = maxHeight + extendHeight
                        scrollState = ScrollState.STATE_EXTEND_MAX_END
                        dispatchTransformationBehaviors(scrollState, consumedDy)
                    }
                } else {
                    var unConsumedDy = dy
                    if (bottom < maxHeight && bottom - dy < maxHeight) {
                        consumedDy = dy
                        bottom -= dy
                        scrollState = ScrollState.STATE_NORMAL_PROCESS
                        val percent = (bottom - minHeight).toFloat() / (maxHeight - minHeight).toFloat()
                        dispatchTransformationBehaviors(scrollState, consumedDy, percent)
                    }
                    if (bottom < maxHeight && bottom - dy >= maxHeight) {
                        consumedDy = bottom - maxHeight
                        unConsumedDy = dy - consumedDy
                        bottom = maxHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        dispatchTransformationBehaviors(scrollState, consumedDy)
                    }
                    if (bottom >= maxHeight && bottom - unConsumedDy > maxHeight) {
                        if (fling) {
                            //告诉fling监听者，已fling到maxHeight处
                            if (canAcceptFlingCallback && headerLayout.onFlingMaxHeight != null) {
                                headerLayout.onFlingMaxHeight!!.invoke(headerLayout)
                                canAcceptFlingCallback = false
                            }
                            return 0
                            return@apply
                        }
                        consumedDy += unConsumedDy
                        bottom -= unConsumedDy
                        scrollState = ScrollState.STATE_EXTEND_PROCESS
                        val percent = (bottom - maxHeight).toFloat() / extendHeight
                        dispatchTransformationBehaviors(scrollState, unConsumedDy, percent)
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
            var consumedDy: Int = 0
            headerLayout.apply {
                //上滑之后到了最小高度
                if (bottom - dy <= minHeight) {
                    consumedDy = bottom - minHeight
                    bottom = minHeight
                    scrollState = ScrollState.STATE_MIN_HEIGHT
                    dispatchTransformationBehaviors(scrollState, consumedDy)
                } else {
                    var unConsumedDy = dy
                    //之前在最大伸展高度与拓展的高度之间，上滑之后还在此区间
                    if (bottom > maxHeight && bottom - dy > maxHeight) {
                        consumedDy = dy
                        bottom -= dy
                        val percent = (bottom - maxHeight).toFloat() / extendHeight.toFloat()
                        scrollState = ScrollState.STATE_EXTEND_PROCESS
                        dispatchTransformationBehaviors(scrollState, consumedDy, percent)
                    }
                    //在最大伸展高度与拓展的高度之间, 上滑之后小于了最大高度
                    if (bottom > maxHeight && bottom - dy <= maxHeight) {
                        consumedDy = bottom - maxHeight
                        unConsumedDy = dy - consumedDy
                        bottom = maxHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        dispatchTransformationBehaviors(scrollState, consumedDy)
                    }
                    //在最小高度和最大高度之间
                    if (bottom <= maxHeight && bottom - unConsumedDy < maxHeight) {
                        consumedDy += unConsumedDy
                        bottom -= unConsumedDy
                        val percent = (bottom - minHeight).toFloat() / (maxHeight - minHeight).toFloat()
                        scrollState = ScrollState.STATE_NORMAL_PROCESS
                        dispatchTransformationBehaviors(scrollState, unConsumedDy, percent)
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
            canAcceptFling = false
            canAcceptScroll = false
            backAnimation = ValueAnimator.ofFloat(headerLayout.bottom.toFloat(), headerLayout.maxHeight.toFloat()).apply {
                addUpdateListener { animator ->
                    val dy = (headerLayout.bottom - animator.animatedValue as Float).toInt()
                    preScrollUp(headerLayout, dy)
                }
                doOnEnd {
                    isBackAnimationDo = false
                }
                duration = 200L
            }
            backAnimation!!.start()
        }

        override fun onNestedPreFling(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            target: View,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (child.scrollState < ScrollState.STATE_MAX_HEIGHT) {
                canAcceptFlingCallback = true
            }
            return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
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
            if (type == ViewCompat.TYPE_TOUCH) {
                backToMaxHeight(child)
            }
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                canAcceptFlingCallback = false
            }
        }
    }

    class LayoutParams : FrameLayout.LayoutParams {

        private var transformationFlags = 0x00

        var transformations: MutableList<Transformation<View>>? = null

        var minTop = 0

        var minTopOffset = 0

        var stickyUntilExit = false

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
        constructor(source: FrameLayout.LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderLayout_Layout)
            transformationFlags = a.getInt(R.styleable.HeaderLayout_Layout_transformation_behavior, 0x00)
            stickyUntilExit = a.getBoolean(R.styleable.HeaderLayout_Layout_sticky_until_exit, false)
            parseTransformationBehaviors(transformationFlags)
            a.recycle()
        }

        /**
         * 解析在xml中设置的transformation_behavior,解析成[Transformation]存储在[transformations]中，
         * 在behavior分发时会遍历[transformations]进行分发
         */
        private fun parseTransformationBehaviors(transformationFlags: Int) {
            if (transformationFlags and TRANSFORMATION_NOTHING != 0) {
                return
            }
            transformations = mutableListOf<Transformation<View>>().apply {
                if (transformationFlags and TRANSFORMATION_ALPHA != 0) {
                    add(AlphaTransformation())
                }
                if (transformationFlags and TRANSFORMATION_EXTEND_SCALE != 0) {
                    add(ExtendScaleTransformation())
                }
                if (transformationFlags and TRANSFORMATION_ALPHA_CONTRARY != 0) {
                    add(AlphaContraryTransformation())
                }
                if (transformationFlags and TRANSFORMATION_SCROLL != 0) {
                    add(ScrollTransformation())
                }
                if (transformationFlags and TRANSFORMATION_COMMON_TOOLBAR != 0) {
                    add(CommonToolbarTransformation())
                }
            }
        }

        companion object {

            private const val TRANSFORMATION_NOTHING = 0x00
            private const val TRANSFORMATION_SCROLL = 0x01
            private const val TRANSFORMATION_ALPHA = 0x02
            private const val TRANSFORMATION_ALPHA_CONTRARY = 0x04
            private const val TRANSFORMATION_EXTEND_SCALE = 0x08
            private const val TRANSFORMATION_COMMON_TOOLBAR = 0x10
        }


    }

    companion object {

        private const val DEFAULT_EXTEND_HEIGHT = 200
    }

}