package com.vsimpleton.photoeditor.view

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.vsimpleton.photoeditor.R
import com.vsimpleton.photoeditor.RatingView
import com.vsimpleton.photoeditor.utlis.BitmapManager
import com.vsimpleton.photoeditor.databinding.ActivityResultBinding
import java.io.File
import kotlin.math.min

class ResultActivity : BaseActivity<ActivityResultBinding>() {

    private val bitmap: Bitmap? by lazy { BitmapManager.bitmap }
    private var scale = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initListener()
    }

    private fun initListener() {
        mBinding.ivBack.setOnClickListener { finish() }
        mBinding.tvSave.setOnClickListener {
            saveImage()
        }
        val widthRatio = ScreenUtils.getAppScreenWidth() * 1.0f / (bitmap?.width ?: 1)
        val heightRatio = ScreenUtils.getAppScreenWidth() * 1.0f / (bitmap?.height ?: 1)
        scale = min(scale, min(widthRatio, heightRatio))
        mBinding.ivImage.setImageBitmap(ImageUtils.scale(bitmap, scale, scale))
        mBinding.ivFacebook.setOnClickListener {
            share("com.facebook.katana")
        }
        mBinding.ivWhatsapp.setOnClickListener {
            share("com.whatsapp")
        }
        mBinding.ivInstagram.setOnClickListener {
            share("com.instagram.android")
        }
        mBinding.ivShare.setOnClickListener {
            share()
        }
        rate()
    }

    private fun rate() {
        if (!SPUtils.getInstance().getBoolean("enableRate", true)) {
            return
        }
        showStarDialog {
            SPUtils.getInstance().put("enableRate", false)
            if (it >= 3) {
                ActivityUtils.startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("market://details?id=$packageName")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
            } else {
                ToastUtils.showShort("Success!")
            }
        }
    }

    private fun showStarDialog(listener: ((Int) -> Unit)) {
        val mBuilder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        val mDialog = mBuilder.create()
        val mWindow = mDialog.window
        mDialog.show()

        val layoutParams = mWindow?.attributes
        layoutParams?.gravity = Gravity.CENTER
        layoutParams?.width = dp2px(318.toFloat())
        layoutParams?.height = dp2px(314.toFloat())
        mWindow?.decorView?.setPadding(0, 0, 0, 0)
        mWindow?.attributes = layoutParams

        mDialog.setContentView(R.layout.dialog_star)

        val ratingView = mWindow?.findViewById<RatingView>(R.id.ratingView)
        ratingView?.mOnTouchListener = {
            mDialog.dismiss()
            listener.invoke(it)
        }

        mWindow?.findViewById<ImageView>(R.id.ivClose)
            ?.setOnClickListener { mDialog.dismiss() }

        if (!mDialog.isShowing) {
            ratingView?.cancelAll()
        }

    }

    private fun share(packageName: String? = null) {
        saveImage {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(File(it)))
            intent.type = "image/*"
            intent.setPackage(packageName)
            ActivityUtils.startActivity(intent)
        }
    }

    private fun saveImage(onSuccess: ((String) -> Unit)? = null) {
        if (PermissionUtils.isGranted(PermissionConstants.CAMERA)) {
            saveResultImage(onSuccess)
        } else {
            PermissionUtils.permission(PermissionConstants.CAMERA)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        saveResultImage(onSuccess)
                    }

                    override fun onDenied() {

                    }
                }).request()
        }
//        PermissionHelper.requestPermission(PermissionConstants.STORAGE) {
//            val path =
//                "${PathUtils.getExternalPicturesPath()}/$packageName/${bitmap.hashCode()}.jpeg"
//            if (FileUtils.isFileExists(path)) {
//                checkSaveStatus(path, true, onSuccess)
//                return@requestPermission
//            }
//            val dialog = ProgressDialog.show(this, "", "Saving...", false, false)
//            val result = FileIOUtils.writeFileFromBytesByStream(
//                File(path),
//                ImageUtils.bitmap2Bytes(bitmap)
//            )
//            checkSaveStatus(path, result, onSuccess)
//            dialog.cancel()
//        }
    }

    private fun saveResultImage(onSuccess: ((String) -> Unit)? = null) {
        val path =
            "${PathUtils.getExternalPicturesPath()}/$packageName/${bitmap.hashCode()}.jpeg"
        if (FileUtils.isFileExists(path)) {
            checkSaveStatus(path, true, onSuccess)
            return
        }
        val dialog = ProgressDialog.show(this, "", "Saving...", false, false)
        val result = FileIOUtils.writeFileFromBytesByStream(
            File(path),
            ImageUtils.bitmap2Bytes(bitmap)
        )
        checkSaveStatus(path, result, onSuccess)
        dialog.cancel()
    }

    private fun checkSaveStatus(
        path: String,
        result: Boolean?,
        onSuccess: ((String) -> Unit)? = null
    ) {
        if (result == true && onSuccess == null) {
            ToastUtils.showShort("Image has saved to $path")
        } else if (result == true) {
            onSuccess?.invoke(path)
        }
    }
}