package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

interface TransformationBehavior<in V: View> {

    fun onStateMinHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    fun onStateNormalProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    fun onStateMaxHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    fun onStateExtendProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    fun onStateExtendMaxEnd(child: V, parent: HeaderLayout, unConsumedDy: Int)
}