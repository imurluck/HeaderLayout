package com.zzx.headerlayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.library.BaseAdapter
import com.zzx.headerlayout.utils.ListProvider
import com.zzx.headerlayout.wangyiyun.WangYiYunActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        headerLayout.setOnClickListener {
            startActivity(Intent(this@MainActivity, WangYiYunActivity::class.java))
        }

        Log.e(TAG, "onCreate -> $headerLayout")
    }

    private fun setupRecyclerView() {
        adapter = BaseAdapter.Builder()
            .setDataList(ListProvider.stringEntityList())
            .build()
        recyclerView.layoutManager = LinearLayoutManager(this, VERTICAL, false)
        recyclerView.adapter = adapter
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
