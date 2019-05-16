package com.zzx.headerlayout.utils

import com.zzx.headerlayout.StringEntity

object ListProvider {

    private val fruitList = arrayOf(
        "Apple", "Apricot",
        "Arbutus", "Banana", "Bennett", "Barbados", "Casaba",
        "Gooseberry", "Grapefruit", "Kernel", "Tangerine",
        "Walnut", "Watermelon"
    )

    fun stringEntityList(): List<StringEntity> = mutableListOf<StringEntity>().apply {
        for (fruit in fruitList) {
            add(StringEntity(fruit))
        }
        for (fruit in fruitList) {
            add(StringEntity(fruit))
        }
    }
}