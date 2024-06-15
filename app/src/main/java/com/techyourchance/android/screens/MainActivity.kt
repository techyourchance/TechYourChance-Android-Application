package com.techyourchance.android.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.activities.BaseViewsActivity
import java.io.Serializable

class MainActivity : BaseViewsActivity() {

    override fun getActivityName() = ActivityName.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        MyLogger.i("onStart()")
        super.onStart()
    }

    override fun onStop() {
        MyLogger.i("onStop()")
        super.onStop()
    }

    override fun onResume() {
        MyLogger.i("onResume()")
        super.onResume()
    }

    override fun onPause() {
        MyLogger.i("onPause()")
        super.onPause()
    }


    companion object {
        private const val TAG = "MainActivity"

        fun start(context: Context, screenSpec: ScreenSpec) {
            MyLogger.i(TAG, "start() $screenSpec")
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec as Serializable)
            context.startActivity(intent)
        }

        fun startClearTask(context: Context, screenSpec: ScreenSpec) {
            MyLogger.i(TAG, "start() $screenSpec")
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec as Serializable)
            context.startActivity(intent)
        }
    }

}