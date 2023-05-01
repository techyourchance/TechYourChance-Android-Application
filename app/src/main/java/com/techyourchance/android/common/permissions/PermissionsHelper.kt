package com.techyourchance.android.common.permissions

import com.techyourchance.android.common.Observable
import androidx.annotation.UiThread
import android.app.Activity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.*

@UiThread
class PermissionsHelper(private val activity: Activity) : Observable<PermissionsHelper.Listener>() {

    interface Listener {
        fun onRequestPermissionsResult(requestCode: Int, result: PermissionsResult)
        fun onPermissionsRequestCancelled(requestCode: Int)
    }

    class PermissionsResult(
        val granted: List<MyPermission>,
        val denied: List<MyPermission>,
        val deniedDoNotAskAgain: List<MyPermission>
    )

    fun hasPermission(permission: MyPermission): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission.androidPermission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllPermissions(vararg  permissions: MyPermission): Boolean {
        for (permission in permissions) {
            if (!hasPermission(permission)) {
                return false
            }
        }
        return true
    }

    fun requestPermission(permission: MyPermission, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission.androidPermission), requestCode)
    }

    fun requestAllPermissions(permissions: Array<MyPermission>, requestCode: Int) {
        val androidPermissions = arrayOfNulls<String>(permissions.size)
        for (i in permissions.indices) {
            androidPermissions[i] = permissions[i].androidPermission
        }
        ActivityCompat.requestPermissions(activity, androidPermissions, requestCode)
    }

    fun onRequestPermissionsResult(requestCode: Int, androidPermissions: Array<String>, grantResults: IntArray) {
        if (androidPermissions.isEmpty() || grantResults.isEmpty()) {
            notifyPermissionsRequestCancelled(requestCode)
            return
        }
        val grantedPermissions: MutableList<MyPermission> = LinkedList()
        val deniedPermissions: MutableList<MyPermission> = LinkedList()
        val deniedAndDoNotAskAgainPermissions: MutableList<MyPermission> = LinkedList()
        var androidPermission: String
        var permission: MyPermission
        for (i in androidPermissions.indices) {
            androidPermission = androidPermissions[i]
            permission = MyPermission.fromAndroidPermission(androidPermission)
            when {
                grantResults[i] == PackageManager.PERMISSION_GRANTED -> {
                    grantedPermissions.add(permission)
                }
                ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermission) -> {
                    deniedPermissions.add(permission)
                }
                else -> {
                    deniedAndDoNotAskAgainPermissions.add(permission)
                }
            }
        }
        val result = PermissionsResult(grantedPermissions, deniedPermissions, deniedAndDoNotAskAgainPermissions)
        notifyPermissionsResult(requestCode, result)
    }

    private fun notifyPermissionsResult(requestCode: Int, permissionsResult: PermissionsResult) {
        for (listener in listeners) {
            listener.onRequestPermissionsResult(requestCode, permissionsResult)
        }
    }

    private fun notifyPermissionsRequestCancelled(requestCode: Int) {
        for (listener in listeners) {
            listener.onPermissionsRequestCancelled(requestCode)
        }
    }
}