package com.zzx.headerlayout.wangyiyun

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.dovebookui.widget.IndicatorAdapter
import com.zzx.headerlayout.R
import com.zzx.headerlayout.utils.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_wang_yi_yun.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class WangYiYunActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        QMUIStatusBarHelper.translucent(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wang_yi_yun)
        setupToolbar()

        setupViewPager()
    }

    private fun setupViewPager() {
        indicator.navigator = CommonNavigator(this).apply {
            adapter = IndicatorAdapter().apply {
                titleList = listOf("热门单曲", "专辑", "视频", "艺人信息")
                onItemClickListener = {
                    viewPager.currentItem = it
                }
            }
            isAdjustMode = true
        }
        val fragmentList = listOf<Fragment>(
            HotSingleFragment(),
            HotSingleFragment(),
            HotSingleFragment(),
            HotSingleFragment()
        )
        ViewPagerHelper.bind(indicator, viewPager)
        viewPager.adapter = FragmentAdapter(supportFragmentManager, fragmentList)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        hideTitleView(toolbar)
    }

    private fun hideTitleView(toolbar: Toolbar) {
        val field = toolbar.javaClass.getDeclaredField("mTitleTextView")
        field.isAccessible = true
        val titleTextView = field.get(toolbar)
        if (titleTextView != null) {
            (titleTextView as TextView).alpha = 0.0f
        }
    }
}