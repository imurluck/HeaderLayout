package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class AlphaContraryTransformation: TransformationAdapter<View>() {

    override fun onStateMinHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        child.alpha = 1.0f
    }

    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        child.alpha = 1.0f - percent
    }

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        child.alpha = 0.0f
    }

}