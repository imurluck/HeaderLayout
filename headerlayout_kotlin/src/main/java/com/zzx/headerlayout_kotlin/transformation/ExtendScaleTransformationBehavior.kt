package com.zzx.headerlayout_kotlin.transformation

import android.util.Log
import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

class ExtendScaleTransformationBehavior: TransformationBehaviorAdapter<View>() {

    private var scale = 1.0f

    override fun onStateMinHeight(target: View, parent: HeaderLayout) {
        Log.e(TAG, "onStateMinHeight -> bottom=${parent.bottom}")
        super.onStateMinHeight(target, parent)
    }

    override fun onStateNormalProcess(target: View, parent: HeaderLayout, percent: Float) {
        Log.e(TAG, "onStateNormalProcess -> bottom=${parent.bottom} percent=$percent")
        super.onStateNormalProcess(target, parent, percent)
    }

    override fun onStateMaxHeight(target: View, parent: HeaderLayout) {
        Log.e(TAG, "onStateMaxHeight -> bottom=${parent.bottom}")
        scale = parent.extendHeight.toFloat() / target.height.toFloat()
        target.scaleX = 1.0f
        target.scaleY = 1.0f
        Log.e(TAG, "onStateMaxHeight -> imageBottom=${target.bottom}")
        super.onStateMaxHeight(target, parent)
    }

    override fun onStateExtendProcess(target: View, parent: HeaderLayout, percent: Float) {
        Log.e(TAG, "onStateExtendProcess -> bottom=${parent.bottom} percent=$percent")
        target.scaleX = percent * scale + 1.0f
        target.scaleY = percent * scale + 1.0f
        Log.e(TAG, "onStateExtendProcess -> imageBottom=${target.bottom}")
        super.onStateExtendProcess(target, parent, percent)
    }

    override fun onStateExtendMaxEnd(target: View, parent: HeaderLayout) {
        Log.e(TAG, "onStateExtendMaxEnd -> bottom=${parent.bottom}")
        super.onStateExtendMaxEnd(target, parent)
    }

    companion object {
        private const val TAG = "ExtendScale"
    }

}