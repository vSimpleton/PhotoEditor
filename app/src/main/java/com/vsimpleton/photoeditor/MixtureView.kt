package com.vsimpleton.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.UriUtils

class MixtureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0
    private var mHeight = 0
    private var centerX = 0f
    private var centerY = 0f
    private var mBitmap: Bitmap? = null

    fun setImageUri(uri: Uri) {
        val bitmap = ImageUtils.getBitmap(
            UriUtils.uri2File(uri),
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight()
        )

        mBitmap = bitmap

        mWidth = bitmap.width
        mHeight = bitmap.height
        centerX = mWidth * 0.5f
        centerY = mHeight * 0.5f

        measure(mWidth, mHeight)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBitmap?.let {
            canvas.drawBitmap(
                it,
                ScreenUtils.getAppScreenWidth() / 2 - centerX,
                ScreenUtils.getAppScreenHeight() / 2 - centerY,
                null
            )
        }

    }

}