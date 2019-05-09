package com.zzx.headerlayout_kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

class ScrollingViewBehavior<V : View>(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout.Behavior<V>(context, attrs) {

    private var originTopMargin = 0

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return dependency is HeaderLayout
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        return layoutChild(parent, child, layoutDirection)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        offsetChildAsNeed(child, dependency)
        return false
    }

    private fun offsetChildAsNeed(child: V, dependency: View) {
        val dependencyBehavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (dependencyBehavior is HeaderLayout.HeaderLayoutBehavior) {
            val dependencyParams = dependency.layoutParams as CoordinatorLayout.LayoutParams
            val offset = (dependency.bottom + dependencyParams.bottomMargin +
                    originTopMargin) - child.top
            ViewCompat.offsetTopAndBottom(child, -offset)
        }
    }

    private fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val headerLayout = findFirstDependency(parent.getDependencies(child))
        return if (headerLayout != null) {
            val headerLayoutParams =  headerLayout.layoutParams as CoordinatorLayout.LayoutParams
            val childLayoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            headerLayoutParams.apply {
                originTopMargin = childLayoutParams.topMargin
                childLayoutParams.topMargin += topMargin + height + bottomMargin
            }
            false
        } else {
            super.onLayoutChild(parent, child, layoutDirection)
        }
    }



    private fun findFirstDependency(dependencies: List<View>): HeaderLayout? {
        for (dependency in dependencies) {
            if (dependency is HeaderLayout) {
                return dependency
            }
        }
        return null
    }
}