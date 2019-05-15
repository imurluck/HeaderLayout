package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class ScrollTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMinHeight(
        child: View,
        parent: HeaderLayout,
        unConsumedDy: Int
    ) {
        parent.offsetChild(child, unConsumedDy)
    }

    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        parent.offsetChild(child, dy)
    }

    override fun onStateExtendProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        parent.offsetChild(child, dy)
    }

    override fun onStateExtendMaxEnd(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        parent.offsetChild(child, unConsumedDy)
    }

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        parent.offsetChild(child, unConsumedDy)
    }

}