package com.techyourchance.android.screens.composeui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.activities.BaseActivity
import java.io.Serializable

class ComposeActivity : BaseActivity() {

    override fun getActivityName() = ActivityName.COMPOSE

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val TAG = "ComposeActivity"

        fun start(context: Context, screenSpec: ScreenSpec) {
            MyLogger.i(TAG, "start() $screenSpec")
            val intent = Intent(context, ComposeActivity::class.java)
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec as Serializable)
            context.startActivity(intent)
        }

    }

}