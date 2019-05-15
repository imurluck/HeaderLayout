package com.zzx.headerlayout_kotlin.transformation

import android.util.Log
import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class ScrollTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMinHeight(
        child: View,
        parent: HeaderLayout,
        unConsumedDy: Int
    ) {
        Log.e(TAG, "onStateMinHeight -> unConsumedDy=$unConsumedDy, childBottom=${child.bottom}")
        parent.offsetChild(child, unConsumedDy)
        Log.e(TAG, "onStateMinHeight -> childBottom=${child.bottom}, parentBottom=${parent.bottom}")
    }

    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        Log.e(TAG, "onStateNormalProcess -> dy=$dy, childBottom=${child.bottom}")
        parent.offsetChild(child, dy)
        Log.e(TAG, "onStateNormalProcess -> childBottom=${child.bottom}, parentBottom=${parent.bottom}")
    }

    override fun onStateExtendProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
        Log.e(TAG, "onStateExtendProcess -> dy=$dy, childBottom=${child.bottom}")
        parent.offsetChild(child, dy)
        Log.e(TAG, "onStateExtendProcess -> childBottom=${child.bottom}, parentBottom=${parent.bottom}")
    }

    override fun onStateExtendMaxEnd(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        Log.e(TAG, "onStateExtendMaxEnd -> unConsumedDy=$unConsumedDy, childBottom=${child.bottom}")
        parent.offsetChild(child, unConsumedDy)
        Log.e(TAG, "onStateExtendMaxEnd -> childBottom=${child.bottom}, parentBottom=${parent.bottom}")
    }

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
        Log.e(TAG, "onStateMaxHeight -> unConsumedDy=$unConsumedDy, childBottom=${child.bottom}")
        parent.offsetChild(child, unConsumedDy)
        Log.e(TAG, "onStateMaxHeight -> childBottom=${child.bottom}, parentBottom=${parent.bottom}")
    }

    companion object {
        private const val TAG = "ScrollTransformation"
    }
}