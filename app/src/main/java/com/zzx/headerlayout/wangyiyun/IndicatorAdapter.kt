package com.example.dovebookui.widget

import android.content.Context
import androidx.core.content.ContextCompat
import com.zzx.headerlayout.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class IndicatorAdapter: CommonNavigatorAdapter() {

    var onItemClickListener: (index: Int) -> Unit = {_->}

    var titleList: List<String>? = null

    override fun getTitleView(context: Context, index: Int): IPagerTitleView {
        return ColorTransitionPagerTitleView(context).apply {
            normalColor = ContextCompat.getColor(context, android.R.color.black)
            selectedColor = ContextCompat.getColor(context, R.color.colorAccent)
            text = titleList?.get(index)
            setOnClickListener {
                onItemClickListener.invoke(index)
            }
        }
    }

    override fun getCount(): Int = if (titleList != null) titleList!!.size else 0

    override fun getIndicator(context: Context): IPagerIndicator {
        return LinePagerIndicator(context).apply {
            setColors(ContextCompat.getColor(context, R.color.colorAccent))
        }
    }


}