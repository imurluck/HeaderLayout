package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class ExtendScaleTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        child.scaleX = 1.0f
        child.scaleY = 1.0f
        super.onStateMaxHeight(child, parent, unConsumedDy)
    }

    override fun onStateExtendProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        child.scaleX = (percent * parent.extendHeight) / child.height * 2.0f + 1.0f
        child.scaleY = (percent * parent.extendHeight) / child.height * 2.0f + 1.0f
        super.onStateExtendProcess(child, parent, percent, dy)
    }

    override fun onStateExtendMaxEnd(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        child.scaleX = parent.extendHeight.toFloat() / child.height.toFloat() * 2.0f + 1.0f
        child.scaleY = parent.extendHeight.toFloat() / child.height.toFloat() * 2.0f + 1.0f
        super.onStateExtendMaxEnd(child, parent, unConsumedDy)
    }

}