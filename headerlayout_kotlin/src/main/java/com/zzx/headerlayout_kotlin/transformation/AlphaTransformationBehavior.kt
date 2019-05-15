package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class AlphaTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMinHeight(
        child: View,
        parent: HeaderLayout,
        unConsumedDy: Int
    ) {
        child.alpha = 0.0f
        super.onStateMinHeight(child, parent, unConsumedDy)
    }
    
    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        child.alpha = percent
    }

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        child.alpha = 1.0f
        super.onStateMaxHeight(child, parent, unConsumedDy)
    }

}