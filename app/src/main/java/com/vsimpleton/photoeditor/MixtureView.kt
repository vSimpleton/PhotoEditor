package com.vsimpleton.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.*
import com.vsimpleton.filter.IImageFilter
import com.vsimpleton.filter.Image
import com.vsimpleton.photoeditor.bean.EmojiInfo
import com.vsimpleton.photoeditor.bean.StickerInfo
import com.vsimpleton.photoeditor.bean.TextInfo
import com.vsimpleton.photoeditor.bean.ViewInfo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MixtureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0
    private var mHeight = 0
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mScale = 1f
    private var mDownX = 0f
    private var mDownY = 0f

    private var mBitmap: Bitmap? = null
    private var mBackgroundBitmap: Bitmap? = null
    private val mElementBitmap by lazy {
        Bitmap.createBitmap(
            mWidth,
            mHeight,
            Bitmap.Config.ARGB_8888
        )
    }

    private var mElementCanvas: Canvas? = null

    private var mViewInfoLists = mutableListOf<ViewInfo>()
    private var mCurrentInfo: ViewInfo = ViewInfo()
    private var mTextInfo: TextInfo = TextInfo()
    private var mEmojiInfo: EmojiInfo = EmojiInfo()
    private var mStickerInfo: StickerInfo = StickerInfo()

    private var isTextTouch = false
    private var isEmojiTouch = false
    private var isStickerTouch = false

    private var preTextAngle = 0f
    private var preEmojiAngle = 0f
    private var preStickerAngle = 0f

    fun setImageUri(uri: Uri) {
        val bitmap = ImageUtils.getBitmap(
            UriUtils.uri2File(uri),
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight()
        )
        val widthRatio = ScreenUtils.getAppScreenWidth() * 1.0f / bitmap.width
        val heightRatio =
            (ScreenUtils.getAppScreenHeight() - ConvertUtils.dp2px(198f)) * 1.0f / bitmap.height
        mScale = min(mScale, min(widthRatio, heightRatio))

        mWidth = (bitmap.width * mScale).toInt()
        mHeight = (bitmap.height * mScale).toInt()

        mCenterX = mWidth * 0.5f
        mCenterY = mHeight * 0.5f

        mBitmap = bitmap
        mBackgroundBitmap = bitmap
        mElementBitmap?.let {
            mElementCanvas = Canvas(it)
        }
        measure(mWidth, mHeight)
        invalidate()
    }

    fun addText(textView: TextView) {
        mTextInfo = TextInfo(
            textView.text.toString(), mCenterX,
            mCenterY, 0f, 1f, ImageUtils.view2Bitmap(textView)
        )

        mViewInfoLists.add(mTextInfo)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBackgroundBitmap?.let { bitmap ->
            canvas.save()
            canvas.drawBitmap(
                ImageUtils.scale(bitmap, mScale, mScale),
                mCenterX - mWidth / 2,
                mCenterY - mHeight / 2,
                null
            )
            canvas.restore()

            mElementCanvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
            mViewInfoLists.forEach {
                mElementCanvas?.save()
                mElementCanvas?.rotate(it.angle, it.centerX, it.centerY)
                mElementCanvas?.scale(it.scale, it.scale, it.centerX, it.centerY)
                it.bitmap?.apply {
                    mElementCanvas?.drawBitmap(
                        this,
                        it.centerX - this.width * 0.5f,
                        it.centerY - this.height * 0.5f,
                        null
                    )
                }
                mElementCanvas?.restore()
            }

            mElementBitmap?.let {
                canvas.drawBitmap(
                    it,
                    mCenterX - mWidth / 2,
                    mCenterY - mHeight / 2,
                    null
                )
            }
        }
    }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean = true

            override fun onLongPress(e: MotionEvent?) {
                if (isTextTouch || isEmojiTouch || isStickerTouch) {
                    mViewInfoLists.remove(mCurrentInfo)
                }
                super.onLongPress(e)
            }
        })
    }
    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            var scale = 1.0f
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scale *= detector.scaleFactor
                mCurrentInfo.scale = scale
                invalidate()
                return true
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)

        if (event.pointerCount == 1) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.x
                    mDownY = event.y

                    isTextTouch = false
                    isEmojiTouch = false
                    isStickerTouch = false

                    whatTouch(mDownX, mDownY)
                    invalidate()
                }

                MotionEvent.ACTION_MOVE -> {
                    val moveX = event.x - mDownX
                    val moveY = event.y - mDownY

                    when {
                        isTextTouch -> {
                            mTextInfo.centerX = mTextInfo.centerX + moveX
                            mTextInfo.centerY = mTextInfo.centerY + moveY
                        }
                        isEmojiTouch -> {
                            mEmojiInfo.centerX = mEmojiInfo.centerX + moveX
                            mEmojiInfo.centerY = mEmojiInfo.centerY + moveY
                        }
                        isStickerTouch -> {
                            mStickerInfo.centerX = mStickerInfo.centerX + moveX
                            mStickerInfo.centerY = mStickerInfo.centerY + moveY
                        }
                    }

                    mDownX = event.x
                    mDownY = event.y
                    invalidate()
                }

                MotionEvent.ACTION_UP -> {

                }
            }
        } else if (event.pointerCount == 2) {
            when (event.actionMasked) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    when {
                        isTextTouch -> {
                            preTextAngle = rotateAngle(
                                mTextInfo.centerX,
                                mTextInfo.centerY,
                                event.getX(1),
                                event.getY(1)
                            )
                        }
                        isEmojiTouch -> {
                            preEmojiAngle = rotateAngle(
                                mEmojiInfo.centerX,
                                mEmojiInfo.centerY,
                                event.getX(1),
                                event.getY(1)
                            )
                        }
                        isStickerTouch -> {
                            preStickerAngle = rotateAngle(
                                mStickerInfo.centerX,
                                mStickerInfo.centerY,
                                event.getX(1),
                                event.getY(1)
                            )
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    when {
                        isTextTouch -> {
                            setTextAngle(event.getX(1), event.getY(1))
                        }
                        isEmojiTouch -> {
                            setEmojiAngle(event.getX(1), event.getY(1))
                        }
                        isStickerTouch -> {
                            setStickerAngle(event.getX(1), event.getY(1))
                        }
                    }
                    invalidate()
                }
            }
        }
        return true
    }

    fun addEmoji(emoji: String) {
        mEmojiInfo = EmojiInfo(
            emoji, mCenterX, mCenterY,
            0f, 1f, ImageUtils.getBitmap(context.assets.open(emoji))
        )
        mViewInfoLists.add(mEmojiInfo)
        invalidate()
    }

    fun addSticker(sticker: String) {
        mStickerInfo = StickerInfo(
            sticker, mCenterX, mCenterY,
            0f, 1f, BitmapFactory.decodeStream(context.assets.open(sticker))
        )
        mViewInfoLists.add(mStickerInfo)
        invalidate()
    }

    fun setFilter(iImageFilter: IImageFilter?) {
        if (iImageFilter == null) {
            mBackgroundBitmap = mBitmap
            invalidate()
            return
        }
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<Bitmap>() {
            override fun doInBackground(): Bitmap {
                var image = Image(mBitmap)
                image = iImageFilter.process(image)
                image.copyPixelsFromBuffer()
                return image.getImage()
            }

            override fun onSuccess(result: Bitmap?) {
                mBackgroundBitmap = result
                invalidate()
            }
        })
    }

    private fun isInfoTouch(x: Float, y: Float, info: ViewInfo): Boolean {
        val cos = cos(info.angle * 0.017453292519943295f)
        val sin = sin(info.angle * 0.017453292519943295f)
        val cX: Float = info.centerX + (x - info.centerX) * cos - (y - info.centerY) * sin
        val cY: Float = info.centerY + sin * (x - info.centerX) + cos * (y - info.centerY)
        val width = info.bitmap?.width ?: 0
        val height = info.bitmap?.height ?: 0
        return info.centerX - (width / 2 * info.scale + 20f) + mCenterX - mWidth / 2 < cX
                && cX < info.centerX + width / 2 * info.scale + 20f + mCenterX - mWidth / 2
                && info.centerY - (height / 2 * info.scale + 20f) + mCenterY - mHeight / 2 < cY
                && cY < info.centerY + height / 2 * info.scale + 20f + mCenterY - mHeight / 2
    }

    private fun whatTouch(x: Float, y: Float) {
        mViewInfoLists.reversed().forEach {
            if (isTextTouch || isEmojiTouch || isStickerTouch) {
                return@forEach
            }
            when (it) {
                is TextInfo -> {
                    mTextInfo = it
                    isTextTouch = isInfoTouch(x, y, it)
                }
                is EmojiInfo -> {
                    mEmojiInfo = it
                    isEmojiTouch = isInfoTouch(x, y, it)
                }
                is StickerInfo -> {
                    mStickerInfo = it
                    isStickerTouch = isInfoTouch(x, y, it)
                }
            }
            if (isTextTouch || isEmojiTouch || isStickerTouch) {
                mCurrentInfo = it
                mViewInfoLists.remove(it)
                mViewInfoLists.add(it)
            }
        }
    }

    private fun rotateAngle(centerX: Float, centerY: Float, x: Float, y: Float): Float {
        return Math.toDegrees(atan2(centerY - y, centerX - x).toDouble()).toFloat()
    }

    private fun setTextAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mTextInfo.centerX, mTextInfo.centerY, x, y)
        mTextInfo.angle += rotateAngle - preTextAngle
        preTextAngle = rotateAngle
    }

    private fun setEmojiAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mEmojiInfo.centerX, mEmojiInfo.centerY, x, y)
        mEmojiInfo.angle += rotateAngle - preEmojiAngle
        preEmojiAngle = rotateAngle
    }

    private fun setStickerAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mStickerInfo.centerX, mStickerInfo.centerY, x, y)
        mStickerInfo.angle += rotateAngle - preStickerAngle
        preStickerAngle = rotateAngle
    }

    fun getBitmap(): Bitmap? {
        mBackgroundBitmap?.let {
            val createBitmap =
                Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(createBitmap)
            canvas.save()
            canvas.drawBitmap(it, 0f, 0f, null)
            mElementBitmap?.let { fore ->
                canvas.drawBitmap(ImageUtils.scale(fore, 1 / mScale, 1 / mScale), 0f, 0f, null)
            }
            canvas.restore()
            return createBitmap
        }
        return null
    }
}