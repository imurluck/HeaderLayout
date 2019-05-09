package com.zzx.headerlayout_kotlin

import android.content.Context
import android.preference.PreferenceActivity
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior

@DefaultBehavior(HeaderLayout.HeaderLayoutBehavior::class)
class HeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    override fun getBehavior(): Behavior<*> = HeaderLayoutBehavior()

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
//            return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
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
            child.apply {
                bottom -= dy
            }
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
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
            super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type
            )
        }
    }
    
    companion object {
        private const val TAG = "HeaderLayout"
    }
    
}