package com.vsimpleton.photoeditor.utlis

import android.content.Context
import com.vsimpleton.filter.*
import com.vsimpleton.filter.Textures.*

object DataManager {

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

    fun getFilters(): List<IImageFilter?> {
        return listOf(
            null,
            HslModifyFilter(230f),
            HslModifyFilter(200f),
            HslModifyFilter(180f),
            HslModifyFilter(160f),
            HslModifyFilter(100f),
            HslModifyFilter(80f),
            HslModifyFilter(50f),
            HslModifyFilter(320f),
            HslModifyFilter(260f),
            MirrorFilter(true),
            MirrorFilter(false),
            TexturerFilter(CloudsTexture(), 0.8, 0.8),
            TexturerFilter(LabyrinthTexture(), 0.8, 0.8),
            TexturerFilter(MarbleTexture(), 1.8, 0.8),
            TexturerFilter(WoodTexture(), 0.8, 0.8),
            TexturerFilter(TextileTexture(), 0.8, 0.8),
            TileReflectionFilter(20, 8, 45, 1),
            TileReflectionFilter(20, 8, 45, 2),
            VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_TRIPED),
            VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_3X3),
            VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_DOTS),
            YCBCrLinearFilter(YCBCrLinearFilter.Range(-0.3f, 0.3f)),
            YCBCrLinearFilter(YCBCrLinearFilter.Range(-0.276f, 0.163f), YCBCrLinearFilter.Range(-0.202f, 0.5f)),
        )
    }

}