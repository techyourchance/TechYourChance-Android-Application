package com.techyourchance.android.common.restart

import android.app.Activity
import android.content.Intent
import com.techyourchance.android.screens.MainActivity
import com.techyourchance.android.screens.common.ScreenSpec
import javax.inject.Inject
import kotlin.system.exitProcess

class RestartAppUseCase @Inject constructor(private val activity: Activity) {

    fun restartApp() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        val pendingIntentId = 101
//        val pendingIntent = PendingIntent.getActivity(activity, pendingIntentId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
//        val alarmManager = (activity.getSystemService(Context.ALARM_SERVICE)) as AlarmManager
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
        activity.startActivity(intent)
        exitProcess(0)
    }

    fun restartAppOnScreen(screenSpec: ScreenSpec) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent)
        exitProcess(0)
    }

}