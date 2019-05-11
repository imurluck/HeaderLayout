package com.zzx.headerlayout_kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.view.OneShotPreDrawListener.add
import androidx.core.view.children
import com.zzx.headerlayout_kotlin.transformation.AlphaTransformationBehavior
import com.zzx.headerlayout_kotlin.transformation.TransformationBehavior

@DefaultBehavior(HeaderLayout.HeaderLayoutBehavior::class)
class HeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    private val maxHeight = 300

    private var minHeight = 100

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

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = LayoutParams(context, attrs)
    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        LayoutParams(super.generateLayoutParams(lp))


    override fun getBehavior(): Behavior<*> = HeaderLayoutBehavior()

    private fun dispatchTransformationBehaviors(scrollState: ScrollState, percent: Float) {
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

    inner class HeaderLayoutBehavior(
        context: Context? = null,
        attrs: AttributeSet? = null
    ) : Behavior<HeaderLayout>(context, attrs) {

        override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: HeaderLayout,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
        ): Boolean {
            Log.e(TAG, "onStartNestedScroll -> ")
            return true
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
            Log.e(TAG, "onNestedPreScroll -> ")
            var consumedDy: Int
            child.apply {
                consumedDy = when {
                    bottom - dy >= maxHeight -> {
                        bottom = maxHeight
                        scrollState = ScrollState.STATE_MAX_HEIGHT
                        bottom - maxHeight
                    }
                    bottom - dy <= minHeight -> {
                        bottom = minHeight
                        scrollState = ScrollState.STATE_MIN_HEIGHT
                        bottom - minHeight
                    }
                    else -> {
                        bottom -= dy
                        scrollState = ScrollState.STATE_NORMAL_PROCESS
                        val percent = (bottom - minHeight).toFloat() / (maxHeight - minHeight).toFloat()
                        dispatchTransformationBehaviors(scrollState, percent)
                        dy
                    }
                }
            }
            consumed[1] = consumedDy
        }
    }

    class LayoutParams: FrameLayout.LayoutParams {

        private var transformationFlags = 0x00

        var transformationBehaviors: MutableList<TransformationBehavior<View>>? = null

        constructor(width: Int, height: Int): super(width, height)
        constructor(width: Int, height: Int, gravity: Int): super(width, height, gravity)
        constructor(source: ViewGroup.LayoutParams): super(source)
        constructor(source: ViewGroup.MarginLayoutParams): super(source)
        constructor(source: FrameLayout.LayoutParams): super(source)

        constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
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
            }
        }

        companion object {

            private const val TRANSFORMATION_NOTHING = 0x00
            private const val TRANSFORMATION_SCROLL = 0x01
            private const val TRANSFORMATION_ALPHA = 0x02
        }


    }
    
    companion object {
        private const val TAG = "HeaderLayout"
    }
    
}