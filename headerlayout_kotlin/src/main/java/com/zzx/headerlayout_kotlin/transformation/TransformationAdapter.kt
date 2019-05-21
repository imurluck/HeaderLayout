package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

open class TransformationAdapter<in V: View>: Transformation<V> {

    override fun onStateMinHeight(child: V, parent: HeaderLayout, unConsumedDy: Int) {
    }

    override fun onStateNormalProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int) {
    }

    override fun onStateMaxHeight(child: V, parent: HeaderLayout, unConsumedDy: Int) {
    }

    override fun onStateExtendProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int) {
    }

    override fun onStateExtendMaxEnd(child: V, parent: HeaderLayout, unConsumedDy: Int) {
    }
}