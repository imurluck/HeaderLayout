package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

open class TransformationBehaviorAdapter<in V: View>: TransformationBehavior<V> {

    override fun onStateMinHeight(target: V, parent: HeaderLayout) {
    }

    override fun onStateNormalProcess(target: V, parent: HeaderLayout, percent: Float) {
    }

    override fun onStateMaxHeight(target: V, parent: HeaderLayout) {
    }

    override fun onStateExtendProcess(target: V, parent: HeaderLayout, percent: Float) {
    }

    override fun onStateExtendMaxEnd(target: V, parent: HeaderLayout) {
    }
}