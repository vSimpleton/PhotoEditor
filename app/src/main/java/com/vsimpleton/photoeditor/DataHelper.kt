package com.vsimpleton.photoeditor

import android.content.Context

object DataHelper {

    fun getEmojiLists(context: Context): List<String> {
        val list = mutableListOf<String>()
        context.assets.list("emojis")?.forEach {
            list.add("emojis/$it")
        }
        return list
    }

    fun getStickerLists(context: Context): List<String> {
        val list = mutableListOf<String>()
        context.assets.list("stickers")?.forEach {
            list.add("stickers/$it")
        }
        return list
    }

    fun getFilterLists(context: Context):List<String> {
        val list = mutableListOf<String>()
        context.assets.list("filter")?.forEach {
            list.add("filter/$it")
        }
        return list
    }

    fun getFontLists(context: Context): List<String> {
        val list = mutableListOf<String>()
        context.assets.list("font")?.forEach {
            list.add("font/$it")
        }
        return list
    }

    fun getColorLists(): List<String> {
        return listOf(
            "#00000000",
            "#FFFFFF",
            "#000000",
            "#EF9A9A",
            "#EF5350",
            "#D32F2F",
            "#B71C1C",
            "#FF5252",
            "#FF1744",
            "#D1C4E9",
            "#B39DDB",
            "#9575CD",
            "#7E57C2",
            "#673AB7",
            "#5E35B1",
            "#4527A0",
            "#311B92",
            "#CE93D8",
            "#B388FF",
            "#BA68C8",
            "#AB47BC",
            "#B2DFDB",
            "#4DB6AC",
            "#A5D6A7",
            "#66BB6A",
            "#C5E1A5",
            "#689F38",
            "#E6EE9C",
            "#C0CA33",
            "#FFF176",
            "#FFE082",
            "#FF8F00",
            "#FFA726",
            "#F57C00",
            "#795548",
            "#A1887F",
            "#6D4C41",
            "#78909C"
        )
    }

}