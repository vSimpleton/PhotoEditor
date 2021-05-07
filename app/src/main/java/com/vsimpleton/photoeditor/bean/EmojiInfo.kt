package com.vsimpleton.photoeditor.bean

import android.graphics.Bitmap
import com.vsimpleton.photoeditor.bean.ViewInfo

data class EmojiInfo(
    var emoji: String = "",
    override var centerX: Float = 0f,
    override var centerY: Float = 0f,
    override var angle: Float = 0f,
    override var scale: Float = 1f,
    override var bitmap: Bitmap? = null
) : ViewInfo(centerX, centerY, angle, scale, bitmap)