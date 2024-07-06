package com.techyourchance.android.screens.benchmarks.sharedprefs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.benchmarks.shared_prefs.SharedPrefsWriteBenchmarkUseCase
import com.techyourchance.android.common.random.RandomStringsGenerator
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedPrefsBenchmarkFragment : BaseFragment(), SharedPrefsBenchmarkViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var sharedPrefsWriteBenchmarkUseCase: SharedPrefsWriteBenchmarkUseCase
    @Inject lateinit var randomStringsGenerator: RandomStringsGenerator

    private lateinit var viewMvc: SharedPrefsBenchmarkViewMvc

    private var startBenchmark = false

    private var benchmarkJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newSharedPrefsBenchmarkViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        viewMvc.showBenchmarkStopped()
        if (startBenchmark) {
            startBenchmark()
        }
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        benchmarkJob?.cancel()
    }

    override fun onToggleBenchmarkClicked() {
        benchmarkJob?.let {
            if (it.isActive) {
                it.cancel()
            } else {
                startBenchmark()
            }
        } ?: startBenchmark()
    }

    private fun startBenchmark() {
        benchmarkJob = coroutineScope.launch {
            try {
                viewMvc.showBenchmarkStarted()
                val valueToWrite = randomStringsGenerator.getRandomAlphanumericString(PREF_VALUE_LENGTH)
                val result = sharedPrefsWriteBenchmarkUseCase.runBenchmark(valueToWrite)
                viewMvc.bindBenchmarkResults(
                    PREF_VALUE_LENGTH,
                    result.resultWithCommit,
                    result.resultWithApply
                )
            } finally {
                viewMvc.showBenchmarkStopped()
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        private const val PREF_VALUE_LENGTH = 100

        fun newInstance(): SharedPrefsBenchmarkFragment {
            val args = Bundle(3)
            val fragment = SharedPrefsBenchmarkFragment()
            fragment.arguments = args
            return fragment
        }
    }
}