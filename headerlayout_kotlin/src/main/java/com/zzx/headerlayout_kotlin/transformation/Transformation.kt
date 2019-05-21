package com.zzx.headerlayout_kotlin.transformation

import android.view.View
import com.zzx.headerlayout_kotlin.HeaderLayout

interface Transformation<in V: View> {
    /**
     * @see [HeaderLayout.scrollState]为STATE_MIN_HEIGHT, 这个方法回调表示[HeaderLayout]的Bottom已经收缩到了最小高度
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     */
    fun onStateMinHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_NORMAL_PROCESS, 在STATE_MIN_HEIGHT和STATE_MAX_HEIGHT之间
     * 这个方法回调表示[HeaderLayout]的Bottom正在最小高度与最大高度之间
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param percent 0<percent<1, 值为([HeaderLayout.getBottom] - [HeaderLayout.minHeight]) / ([HeaderLayout.maxHeight] - [HeaderLayout.minHeight]])
     * 且值不会为0或者1, 为0相当于是回调了[onStateMinHeight], 为1相当于回调了[onStateMaxHeight], 由于值不会为0或1，
     * 所以在回调[onStateMinHeight]和[onStateMaxHeight]时会有一个未消耗的dy
     * @param dy 滑动的距离
     */
    fun onStateNormalProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_MAX_HEIGHT
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight]
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     */
    fun onStateMaxHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_EXTEND_PROCESS, 在STATE_MAX_HEIGHT和STATE_EXTEND_MAX_END之间
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight] 和 [HeaderLayout.maxHeight] + [HeaderLayout.extendHeight]之间
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param percent 0<percent<1, 值为([HeaderLayout.getBottom] - [HeaderLayout.maxHeight]) / [HeaderLayout.extendHeight]
     * 且值不会为0或者1, 为0相当于是回调了[onStateMaxHeight], 为1相当于回调了[onStateExtendMaxEnd], 由于值不会为0或1，
     * 所以在回调[onStateMaxHeight]和[onStateExtendMaxEnd]时会有一个未消耗的dy
     */
    fun onStateExtendProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_EXTEND_MAX_END,
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight] + [HeaderLayout.extendHeight]
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     *
     */
    fun onStateExtendMaxEnd(child: V, parent: HeaderLayout, unConsumedDy: Int)
}