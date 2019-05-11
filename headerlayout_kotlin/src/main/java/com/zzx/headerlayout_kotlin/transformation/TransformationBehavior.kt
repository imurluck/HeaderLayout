package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

interface TransformationBehavior<in V: View> {

    fun onStateMinHeight(target: V, parent: HeaderLayout)

    fun onStateNormalProcess(target: V, parent: HeaderLayout, percent: Float)

    fun onStateMaxHeight(target: V, parent: HeaderLayout)

    fun onStateExtendProcess(target: V, parent: HeaderLayout, percent: Float)

    fun onStateExtendMaxEnd(target: V, parent: HeaderLayout)
}