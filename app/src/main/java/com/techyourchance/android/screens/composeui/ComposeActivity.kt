package com.techyourchance.android.screens.composeui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techyourchance.android.R
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.activities.BaseActivity
import com.techyourchance.android.screens.common.composables.MyTheme
import kotlinx.coroutines.flow.map
import java.io.Serializable

class ComposeActivity : BaseActivity() {

    override fun getActivityName() = ActivityName.COMPOSE

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {

        val navController = rememberNavController()

        val isRootRoute = navController.currentBackStackEntryFlow.map { backStackEntry ->
            val isHomeRoute = backStackEntry.destination.route == Route.HomeRoot.route
            val isRootInTab = listOf("0", null).contains(backStackEntry.arguments?.getString("num"))
            isHomeRoute && isRootInTab
        }.collectAsState(initial = true)

        Scaffold(
            topBar = {
                MyTopAppBar(
                    isRootRoute = isRootRoute.value,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            },
            bottomBar = {
                BottomAppBar(modifier = Modifier) {
                    MyBottomAppBar(
                        navController = navController,
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(
                    PaddingValues(
                        0.dp,
                        0.dp,
                        0.dp,
                        innerPadding.calculateBottomPadding()
                    )
                )
            ) {
                MainScreenContent(navController = navController)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyTopAppBar(
        isRootRoute: Boolean,
        onBackClicked: () -> Unit,
    ) {
        CenterAlignedTopAppBar(
            title = {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                if (!isRootRoute) {
                    IconButton(
                        onClick = onBackClicked
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "back"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        )
    }

    @Composable
    fun MyBottomAppBar(
        navController: NavController,
    ) {
        val currentRoute = navController.currentBackStackEntryFlow.map { backStackEntry ->
            backStackEntry.destination.route
        }.collectAsState(initial = Route.HomeRoot.route)

        val items = listOf(
            BottomTab.Home,
            BottomTab.Settings
        )

        var selectedItem by remember { mutableIntStateOf(0) }

        items.forEachIndexed { index, navigationItem ->
            if (navigationItem.rootRoute.route == currentRoute.value) {
                selectedItem = index
            }
        }

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = { Icon(item.icon!!, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        navController.navigate(item.rootRoute.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun MainScreenContent(navController: NavHostController) {
        val navigateToNextScreen: (String) -> Unit =  { destinationRoute ->
            val currentScreenNum = navController.currentBackStackEntry?.arguments?.getString("num") ?: "0"
            val nextScreenNum = currentScreenNum.toInt() + 1
            navController.navigate(destinationRoute.replace("{num}", "$nextScreenNum"))
        }
        NavHost(navController, startDestination = Route.HomeRoot.route) {
            composable(Route.HomeRoot.route) {
                SimpleScreen(
                    title = Route.HomeRoot.title,
                    onNavigateToNextScreenClicked = { navigateToNextScreen(Route.HomeChild.route) }
                )
            }
            composable(Route.HomeChild.route) { backStackEntry ->
                val screenNum = backStackEntry.arguments?.getString("num") ?: "0"
                SimpleScreen(
                    title = "${Route.HomeChild.title} $screenNum",
                    onNavigateToNextScreenClicked = { navigateToNextScreen(Route.HomeChild.route) }
                )
            }
            composable(Route.SettingsRoot.route) {
                SimpleScreen(
                    title = Route.SettingsRoot.title,
                    onNavigateToNextScreenClicked = { navigateToNextScreen(Route.SettingsChild.route) }
                )
            }
            composable(Route.SettingsChild.route) { backStackEntry ->
                val screenNum = backStackEntry.arguments?.getString("num") ?: "0"
                SimpleScreen(
                    title = "${Route.SettingsChild.title} $screenNum",
                    onNavigateToNextScreenClicked = { navigateToNextScreen(Route.SettingsChild.route) }
                )
            }
        }
    }

    @Composable
    fun SimpleScreen(
        title: String,
        onNavigateToNextScreenClicked: ()-> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNavigateToNextScreenClicked) {
                Text(
                    text = "Add screen",
                    fontSize = 20.sp
                )
            }
        }
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