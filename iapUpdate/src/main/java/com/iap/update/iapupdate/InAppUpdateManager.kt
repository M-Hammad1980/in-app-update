package com.iap.update.iapupdate

import android.app.Activity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: Activity,
    private val appUpdateType: Int = AppUpdateType.IMMEDIATE
) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private var updateListener: InstallStateUpdatedListener? = null
    private var parentView: View? = null

    // Set the launcher for activity results
    fun checkForUpdate(
        currentVersion :  Int,
        remoteVersion: Int,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        parent: View
    ) {
        parentView = parent
        if (currentVersion < remoteVersion) {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {

                    // Request the update using the ActivityResultLauncher
                    val appUpdateOptions =
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        launcher, appUpdateOptions
                    )

                }
            }
        }
    }

    // Listener for post-download updates
    private fun setupUpdateListener() {
        updateListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                parentView?.let { showSnackBarForCompleteUpdate(it) }
            }
        }
        updateListener?.let {
            appUpdateManager.registerListener(it)
        }
    }

    // Display a snackbar to notify the user of the downloaded update
    private fun showSnackBarForCompleteUpdate(parent: View) {
        Snackbar.make(parent, "Update downloaded", Snackbar.LENGTH_INDEFINITE)
            .setAction("Restart") { appUpdateManager.completeUpdate() }
            .show()
    }

    // Unregister the listener to avoid leaks
    fun unregisterListener() {
        updateListener?.let {
            appUpdateManager.unregisterListener(it)
        }
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                parentView?.let { showSnackBarForCompleteUpdate(it) }
            }
        }
    }
}
