package com.zzx.headerlayout.wangyiyun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.library.BaseAdapter
import com.zzx.headerlayout.R
import com.zzx.headerlayout.utils.ListProvider
import kotlinx.android.synthetic.main.fragment_hot_single.*

class HotSingleFragment : Fragment() {

    private lateinit var adapter: BaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        layoutInflater.inflate(R.layout.fragment_hot_single, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = BaseAdapter.Builder()
            .setDataList(ListProvider.stringEntityList())
            .build()
        recyclerView.layoutManager = LinearLayoutManager(activity, VERTICAL, false)
        recyclerView.adapter = adapter
    }
}