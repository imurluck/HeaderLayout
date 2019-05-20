package com.zzx.headerlayout_kotlin.transformation

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.zzx.headerlayout_kotlin.HeaderLayout

class CommonToolbarTransformationBehavior: TransformationBehaviorAdapter<View>() {

    private var titleTextView: TextView? = null

    private var subtitleTextView: TextView? = null

    private var toolbar: Toolbar? = null

    private var hide = true

    private fun getToolbar(view: View): Toolbar? {
        if (toolbar != null) {
            return toolbar
        }
        toolbar = searchToolbar(view)
        return toolbar
    }

    private fun searchToolbar(view: View): Toolbar? {
        if (view is Toolbar) {
            return view
        } else if (view is ViewGroup) {
            for (child in view.children) {
                val toolbar = getToolbar(child)
                if (toolbar != null) {
                    return toolbar
                }
            }
        }
        return null
    }

    private fun getTitleTextView(child: View): TextView? {
        if (titleTextView != null) {
            return titleTextView
        }
        getToolbar(child)?.apply {
            val field = this.javaClass.getDeclaredField("mTitleTextView")
            field.isAccessible = true
            val title = field.get(this)
            if (title != null) {
                titleTextView = title as TextView
                return titleTextView
            }
            return null
        }
        return null
    }

    private fun getSubtitleTextView(child: View): TextView? {
        if (subtitleTextView != null) {
            return subtitleTextView
        }
        getToolbar(child)?.apply {
            val field = this.javaClass.getDeclaredField("mSubtitleTextView")
            field.isAccessible = true
            val subtitle = field.get(this)
            if (subtitle != null) {
                subtitleTextView = subtitle as TextView
                return subtitleTextView
            }
            return null
        }
        return null
    }

    override fun onStateMinHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        getTitleTextView(child)?.alpha = 1.0f
        getSubtitleTextView(child)?.alpha = 1.0f
        hide = false
    }

    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        if (!hide) {
            getTitleTextView(child)?.alpha = 0.0f
            getSubtitleTextView(child)?.alpha = 0.0f
            hide = true
        }
    }
}

