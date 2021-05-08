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
import com.vsimpleton.photoeditor.bean.EmojiEntry
import com.vsimpleton.photoeditor.bean.StickerEntry
import com.vsimpleton.photoeditor.bean.TextEntry
import com.vsimpleton.photoeditor.bean.BaseEntry
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

    private var mViewEntryLists = mutableListOf<BaseEntry>()
    private var mCurrentEntry: BaseEntry = BaseEntry()
    private var mTextEntry: TextEntry = TextEntry()
    private var mEmojiEntry: EmojiEntry = EmojiEntry()
    private var mStickerEntry: StickerEntry = StickerEntry()

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
        mTextEntry = TextEntry(
            textView.text.toString(), mCenterX,
            mCenterY, 0f, 1f, ImageUtils.view2Bitmap(textView)
        )

        mViewEntryLists.add(mTextEntry)
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
            mViewEntryLists.forEach {
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
                    mViewEntryLists.remove(mCurrentEntry)
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
                mCurrentEntry.scale = scale
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
                            mTextEntry.centerX = mTextEntry.centerX + moveX
                            mTextEntry.centerY = mTextEntry.centerY + moveY
                        }
                        isEmojiTouch -> {
                            mEmojiEntry.centerX = mEmojiEntry.centerX + moveX
                            mEmojiEntry.centerY = mEmojiEntry.centerY + moveY
                        }
                        isStickerTouch -> {
                            mStickerEntry.centerX = mStickerEntry.centerX + moveX
                            mStickerEntry.centerY = mStickerEntry.centerY + moveY
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
                                mTextEntry.centerX,
                                mTextEntry.centerY,
                                event.getX(1),
                                event.getY(1)
                            )
                        }
                        isEmojiTouch -> {
                            preEmojiAngle = rotateAngle(
                                mEmojiEntry.centerX,
                                mEmojiEntry.centerY,
                                event.getX(1),
                                event.getY(1)
                            )
                        }
                        isStickerTouch -> {
                            preStickerAngle = rotateAngle(
                                mStickerEntry.centerX,
                                mStickerEntry.centerY,
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
        mEmojiEntry = EmojiEntry(
            emoji, mCenterX, mCenterY,
            0f, 1f, ImageUtils.getBitmap(context.assets.open(emoji))
        )
        mViewEntryLists.add(mEmojiEntry)
        invalidate()
    }

    fun addSticker(sticker: String) {
        mStickerEntry = StickerEntry(
            sticker, mCenterX, mCenterY,
            0f, 1f, BitmapFactory.decodeStream(context.assets.open(sticker))
        )
        mViewEntryLists.add(mStickerEntry)
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

    private fun isInfoTouch(x: Float, y: Float, info: BaseEntry): Boolean {
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
        mViewEntryLists.reversed().forEach {
            if (isTextTouch || isEmojiTouch || isStickerTouch) {
                return@forEach
            }
            when (it) {
                is TextEntry -> {
                    mTextEntry = it
                    isTextTouch = isInfoTouch(x, y, it)
                }
                is EmojiEntry -> {
                    mEmojiEntry = it
                    isEmojiTouch = isInfoTouch(x, y, it)
                }
                is StickerEntry -> {
                    mStickerEntry = it
                    isStickerTouch = isInfoTouch(x, y, it)
                }
            }
            if (isTextTouch || isEmojiTouch || isStickerTouch) {
                mCurrentEntry = it
                mViewEntryLists.remove(it)
                mViewEntryLists.add(it)
            }
        }
    }

    private fun rotateAngle(centerX: Float, centerY: Float, x: Float, y: Float): Float {
        return Math.toDegrees(atan2(centerY - y, centerX - x).toDouble()).toFloat()
    }

    private fun setTextAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mTextEntry.centerX, mTextEntry.centerY, x, y)
        mTextEntry.angle += rotateAngle - preTextAngle
        preTextAngle = rotateAngle
    }

    private fun setEmojiAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mEmojiEntry.centerX, mEmojiEntry.centerY, x, y)
        mEmojiEntry.angle += rotateAngle - preEmojiAngle
        preEmojiAngle = rotateAngle
    }

    private fun setStickerAngle(x: Float, y: Float) {
        val rotateAngle = rotateAngle(mStickerEntry.centerX, mStickerEntry.centerY, x, y)
        mStickerEntry.angle += rotateAngle - preStickerAngle
        preStickerAngle = rotateAngle
    }

    fun getBitmap(): Bitmap? {
        mBackgroundBitmap?.let {
            val createBitmap =
                Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(createBitmap)
            canvas.save()
            canvas.drawBitmap(it, 0f, 0f, null)
            mElementBitmap?.let { bitmap ->
                canvas.drawBitmap(ImageUtils.scale(bitmap, 1 / mScale, 1 / mScale), 0f, 0f, null)
            }
            canvas.restore()
            return createBitmap
        }
        return null
    }
}