package com.zzx.headerlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.library.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

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
