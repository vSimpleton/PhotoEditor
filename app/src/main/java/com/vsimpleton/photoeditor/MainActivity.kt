package com.vsimpleton.photoeditor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.UriUtils
import com.vsimpleton.photoeditor.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        private const val REQUEST_PERMISSION_WRITE = 0
        private const val REQUEST_PERMISSION_CAMERA = 1
        const val EXTRA_URI = "uri"
    }

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        mBinding.ivAlbum.setOnClickListener {
            openAlbum()
        }

        mBinding.ivCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun openAlbum() {
        PermissionHelper.requestPermission(PermissionConstants.STORAGE) {
            ActivityUtils.startActivityForResult(
                this,
                Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_PERMISSION_WRITE
            )
        }
    }

    private fun openCamera() {
        PermissionHelper.requestPermission(PermissionConstants.CAMERA) {
            val file = File(cacheDir, "${System.currentTimeMillis()}.jpg")
            if (FileUtils.createOrExistsFile(file)) {
                uri = UriUtils.file2Uri(file)
                ActivityUtils.startActivityForResult(
                    this, Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    },
                    REQUEST_PERMISSION_CAMERA
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PERMISSION_WRITE -> {
                    data?.data?.let {
                        UCrop.of(it, Uri.fromFile(File(cacheDir, "temp")))
                            .withOptions(UCrop.Options().apply {
                                setToolbarColor(resources.getColor(R.color.main_color))
                                setStatusBarColor(resources.getColor(R.color.main_color))
                            }).start(this)
                    }
                }
                REQUEST_PERMISSION_CAMERA -> {
                    uri?.let {
                        UCrop.of(it, Uri.fromFile(File(cacheDir, "temp")))
                            .withOptions(UCrop.Options().apply {
                                setToolbarColor(resources.getColor(R.color.main_color))
                                setStatusBarColor(resources.getColor(R.color.main_color))
                            }).start(this)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    data?.let {
                        ActivityUtils.startActivity(
                            bundleOf(EXTRA_URI to UCrop.getOutput(it)),
                            EditActivity::class.java
                        )
                    }
                }
            }
        }
    }

}