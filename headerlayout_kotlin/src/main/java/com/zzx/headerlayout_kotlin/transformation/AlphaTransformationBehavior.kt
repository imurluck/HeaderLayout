package com.zzx.headerlayout_kotlin.transformation

import android.util.Log
import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class AlphaTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateNormalProcess(target: View, parent: HeaderLayout, percent: Float) {
        Log.e(TAG, "onStateNormalProcess -> $percent")
        target.alpha = percent
    }

    companion object {
        private const val TAG = "AlphaTransformation"
    }
}