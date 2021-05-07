package com.vsimpleton.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.ConvertUtils.dp2px

@SuppressLint("Recycle")
class RatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val mAnimations = mutableListOf<Animation>()
    var mOnTouchListener: ((Int) -> Unit)? = null

    private var mStarNum = 5
    private var mStepSize = dp2px(16f)
    private var mStarSize = dp2px(25f)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RatingView)
        val starNum = ta.getInteger(R.styleable.RatingView_starNum, 0)
        val stepSize = ta.getDimension(R.styleable.RatingView_stepSize, 0f)
        val starSize = ta.getDimension(R.styleable.RatingView_starSize, 0f)

        if (starNum != 0) {
            mStarNum = starNum
        }

        if (stepSize != 0f) {
            mStepSize = stepSize.toInt()
        }

        if (starSize != 0f) {
            mStarSize = starSize.toInt()
        }

        addImageView()
        initAnimation()
        initTouchListener()

    }

    private fun addImageView() {
        for (i in 0 until mStarNum) {
            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.ic_star_pressed)
            imageView.visibility = View.INVISIBLE
            val layoutParams = LayoutParams(mStarSize, mStarSize)
            if (i != 0) {
                layoutParams.leftMargin = mStepSize
            }
            addView(imageView, layoutParams)
        }
    }

    private fun initAnimation() {
        for (i in 0 until mStarNum) {
            val scaleAnimation = ScaleAnimation(
                0f,
                1f,
                0f,
                1f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            mAnimations.add(scaleAnimation)
            scaleAnimation.duration = 500
            scaleAnimation.startOffset = 100L * i

            val imageView = getChildAt(i)
            imageView.visibility = View.VISIBLE

            imageView.startAnimation(scaleAnimation)

            if (i == mStarNum - 1) {
                scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        for (j in 0 until mStarNum) {
                            val view = getChildAt(j) as ImageView
                            view.setImageResource(R.drawable.ic_star_normal)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {

                    }
                })
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchListener() {
        for (i in 0 until mStarNum) {
            val view = getChildAt(i)
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    showScaleAnimation(v, 1.5f, i)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    showScaleAnimation(v, 1f, i)
                }
                true
            }
        }
    }

    private fun showScaleAnimation(view: View, scale: Float, position: Int) {
        val scaleAnimation = ScaleAnimation(
            1f,
            scale,
            1f,
            scale,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mAnimations.add(scaleAnimation)

        scaleAnimation.duration = 200
        scaleAnimation.fillAfter = true
        view.startAnimation(scaleAnimation)

        if (scale == 1f) {
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    for (j in 0 until mStarNum) {
                        if (j <= position) {
                            (getChildAt(j) as ImageView).setImageResource(R.drawable.ic_star_pressed)
                        }
                    }
                    mOnTouchListener?.invoke(position)
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
        }
    }

    fun cancelAll() {
        for (animation in mAnimations) {
            animation.cancel()
        }
    }
}