package com.techyourchance.android.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.activities.BaseActivity
import java.io.Serializable

class MainActivity : BaseActivity() {

    override fun getActivityName() = ActivityName.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
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