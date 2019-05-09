package com.zzx.headerlayout

import android.widget.TextView
import com.example.library.BaseAdapter
import com.example.library.IEntity

class StringEntity(private val text: String): IEntity<StringEntity> {

    override fun bindView(
        baseAdapter: BaseAdapter,
        holder: BaseAdapter.ViewHolder,
        data: StringEntity,
        position: Int
    ) {
        holder.itemView.findViewById<TextView>(R.id.tv).text = text
    }

    override fun getLayoutId(): Int = R.layout.item_string_list


}