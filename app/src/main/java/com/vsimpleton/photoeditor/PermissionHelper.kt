package com.vsimpleton.photoeditor

import com.blankj.utilcode.util.PermissionUtils

object PermissionHelper {

    fun requestPermission(permission: String, onSuccess: () -> Unit) {
        if (PermissionUtils.isGranted(permission)) {
            onSuccess.invoke()
        } else {
            PermissionUtils.permission(permission)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        onSuccess.invoke()
                    }

                    override fun onDenied() {
                    }
                }).request()
        }
    }
}