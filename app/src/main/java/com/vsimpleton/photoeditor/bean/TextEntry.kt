package com.vsimpleton.photoeditor.bean

import android.graphics.Bitmap

data class TextEntry(
    var text: String = "",
    override var centerX: Float = 0f,
    override var centerY: Float = 0f,
    override var angle: Float = 0f,
    override var scale: Float = 1f,
    override var bitmap: Bitmap? = null
) : BaseEntry()
