package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class ExtendScaleTransformationBehavior: TransformationBehaviorAdapter<View>() {

    override fun onStateMinHeight(
        child: View,
        parent: HeaderLayout,
        dy: Int
    ) {
//        Log.e(TAG, "onStateMinHeight -> bottom=${parent.bottom}")
        super.onStateMinHeight(child, parent, dy)
    }

    override fun onStateNormalProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
//        Log.e(TAG, "onStateNormalProcess -> bottom=${parent.bottom} percent=$percent")
        super.onStateNormalProcess(child, parent, percent, dy)
    }

    override fun onStateMaxHeight(child: View, parent: HeaderLayout, unConsumedDy: Int) {
//        Log.e(TAG, "onStateMaxHeight -> bottom=${parent.bottom}")
        child.scaleX = 1.0f
        child.scaleY = 1.0f
//        Log.e(TAG, "onStateMaxHeight -> imageBottom=${target.bottom}")
        super.onStateMaxHeight(child, parent, unConsumedDy)
    }

    override fun onStateExtendProcess(child: View, parent: HeaderLayout, percent: Float, dy: Int) {
//        Log.e(TAG, "onStateExtendProcess -> bottom=${parent.bottom} percent=$percent")
        child.scaleX = (percent * parent.extendHeight) / child.height * 2.0f + 1.0f
        child.scaleY = (percent * parent.extendHeight) / child.height * 2.0f + 1.0f
//        Log.e(TAG, "onStateExtendProcess -> imageBottom=${target.bottom}")
        super.onStateExtendProcess(child, parent, percent, dy)
    }

    override fun onStateExtendMaxEnd(child: View, parent: HeaderLayout, unConsumedDy: Int) {
//        Log.e(TAG, "onStateExtendMaxEnd -> bottom=${parent.bottom}")
        child.scaleX = parent.extendHeight.toFloat() / child.height.toFloat() * 2.0f + 1.0f
        child.scaleY = parent.extendHeight.toFloat() / child.height.toFloat() * 2.0f + 1.0f
        super.onStateExtendMaxEnd(child, parent, unConsumedDy)
    }

    companion object {
        private const val TAG = "ExtendScale"
    }

}