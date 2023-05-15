package com.techyourchance.android.common.permissions

interface PermissionsHelperDelegate {

    /**
     * This function must be called from containing Activity's onRequestPermissionResult
     */
    fun onRequestPermissionsResult(requestCode: Int, androidPermissions: Array<String>, grantResults: IntArray)

}