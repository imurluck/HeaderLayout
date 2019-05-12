package com.zzx.headerlayout_kotlin.transformation

import android.util.Log
import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class AlphaTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMinHeight(target: View, parent: HeaderLayout) {

        target.alpha = 0.0f
        super.onStateMinHeight(target, parent)
    }
    
    override fun onStateNormalProcess(target: View, parent: HeaderLayout, percent: Float) {
        target.alpha = percent
    }

    override fun onStateMaxHeight(target: View, parent: HeaderLayout) {
        Log.e(TAG, "onStateMaxHeight -> bottom=${parent.bottom}")
        target.alpha = 1.0f
        super.onStateMaxHeight(target, parent)
    }


    companion object {
        private const val TAG = "AlphaTransformation"
    }
}