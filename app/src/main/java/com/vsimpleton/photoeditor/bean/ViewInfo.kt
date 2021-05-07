package com.vsimpleton.photoeditor.bean

import android.graphics.Bitmap

open class ViewInfo(
    open var centerX: Float = 0f,
    open var centerY: Float = 0f,
    open var angle: Float = 0f,
    open var scale: Float = 1f,
    open var bitmap: Bitmap? = null
)