/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.nilearning.ai.pisces.feature.DrawerHeader
import com.nilearning.ai.pisces.feature.MultimodalRoute
import com.nilearning.ai.pisces.feature.PromptsRoute
import com.nilearning.ai.pisces.feature.chat.ChatRoute
import com.nilearning.ai.pisces.ui.theme.GenerativeAISample
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    sealed class Screen(val route: String,
                        @StringRes val resourceId: Int,
                        @DrawableRes val iconLine: Int,
                        @DrawableRes val iconFill: Int
    ) {
        data object Chat : Screen("chat",
            R.string.chat_tab,
            R.drawable.ic_robot,
            R.drawable.ic_robot_fill
        )
        data object Summarize : Screen("summarize",
            R.string.summarize_tab,
            R.drawable.ic_summarize,
            R.drawable.ic_summarize_fill
        )
        data object Photo : Screen("reasoning",
            R.string.reason_tab,
            R.drawable.ic_images,
            R.drawable.ic_images_fill
        )
    }

    private val items = listOf(
        Screen.Chat,
        Screen.Summarize,
        Screen.Photo
    )

    object ThemeState {
        var darkModeState : MutableState<Boolean> = mutableStateOf(false)
    }

    private lateinit var appUpdateManager: AppUpdateManager

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        // handle callback
        if (result.resultCode != RESULT_OK) {
            Log.i("Update", "Update flow failed! Result code: " + result.resultCode);
            // If the update is canceled or fails,
            // you can request to start the update again.
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        setContent {
            GenerativeAISample {
                // A surface container using the 'background' color from the theme
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val scope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }
                    val isDark = isSystemInDarkTheme()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    // Returns an intent object that you use to check for an update.
                    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                    // Checks that the platform will allow the specified type of update.
                    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.updatePriority() >= 4 /* high priority */
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        ) {
                            // Request the update.
                            appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // an activity result launcher registered via registerForActivityResult
                                activityResultLauncher,
                                // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for flexible updates.
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                        } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= 3 // DAYS_FOR_FLEXIBLE_UPDATE
                            && appUpdateInfo.updatePriority() >= 2 /* high priority */
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                        ) {
                            // Request the update.
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                activityResultLauncher,
                                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier
                                    .width(250.dp)
                                    .fillMaxHeight()
                                    .background(color = Color.Transparent),
                                drawerContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                DrawerHeader()
                                HorizontalDivider(thickness = 16.dp, color = MaterialTheme.colorScheme.onTertiary)
                                NavigationDrawerItem(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                                    label = { Text(text = stringResource(id = R.string.write_review), modifier = Modifier.padding(start = 4.dp), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer) },
                                    badge = { Icon(imageVector =Icons.Outlined.ThumbUp, tint = MaterialTheme.colorScheme.onSecondaryContainer, contentDescription = null ) },
                                    selected = false,
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                                        startActivity(intent)
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                                NavigationDrawerItem(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    label = { Text(text = stringResource(id = R.string.check_pro), modifier = Modifier.padding(start = 4.dp), fontSize = 16.sp, color = MaterialTheme.colorScheme.onTertiaryContainer) },
                                    badge = { Icon(imageVector =Icons.Outlined.ShoppingCart , tint = MaterialTheme.colorScheme.onTertiaryContainer, contentDescription = null ) },
                                    selected = false,
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            snackbarHostState.showSnackbar(message = getString(R.string.check_pro_message), duration = SnackbarDuration.Short)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                                    text = stringResource(id = R.string.by_company),
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary)
                                // ...other drawer items
                            }
                        },
                        gesturesEnabled = true
                    ) {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(hostState = snackbarHostState)
                            },
                            topBar = {
                                CenterAlignedTopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    title = {
                                        Text(
                                            text = stringResource(id = R.string.app_name),
                                            fontSize = 24.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch {
                                                if (drawerState.isOpen) {
                                                    drawerState.close()
                                                } else {
                                                    drawerState.open()
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                tint = MaterialTheme.colorScheme.primary,
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = {
                                            val theme = when(isDark) {
                                                true -> AppCompatDelegate.MODE_NIGHT_NO
                                                false -> AppCompatDelegate.MODE_NIGHT_YES
                                            }
                                            AppCompatDelegate.setDefaultNightMode(theme)
                                            ThemeState.darkModeState.value = !isDark
                                        }) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(
                                                    id = if (isDark) R.drawable.ic_light_mode_fill else R.drawable.ic_dark_mode_fill),
                                                tint = MaterialTheme.colorScheme.primary,
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    },
                                    scrollBehavior = scrollBehavior,
                                )
                            },
                            bottomBar = {
                                BottomNavigation (
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentDestination = navBackStackEntry?.destination
                                    items.forEach { screen ->
                                        val isSelected = currentDestination?.hierarchy?.any {  it.route == screen.route } == true
                                        BottomNavigationItem(
                                            icon = { Icon(ImageVector.vectorResource(id = if (isSelected) screen.iconFill else screen.iconLine),
                                                tint = MaterialTheme.colorScheme.primary, contentDescription = null) },
                                            label = { Text(stringResource(screen.resourceId), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary) },
                                            selected = isSelected,
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    // Pop up to the start destination of the graph to
                                                    // avoid building up a large stack of destinations
                                                    // on the back stack as users select items
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    // Avoid multiple copies of the same destination when
                                                    // re-selecting the same item
                                                    launchSingleTop = true
                                                    // Restore state when re-selecting a previously selected item
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            NavHost(navController, startDestination = Screen.Chat.route, Modifier.padding(innerPadding)) {
                                composable(Screen.Chat.route) { ChatRoute() }
                                composable(Screen.Summarize.route) { PromptsRoute(rememberNavController()) }
                                composable(Screen.Photo.route) { MultimodalRoute(rememberNavController()) }
                            }
                        }
                    }
                }
            }
        }
    }

    // Checks that the update is not stalled during 'onResume()'.
    // However, you should execute this check at all entry points into the app.
    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                }
            }
    }

//    {
//            "releases": [{
//            "versionCodes": ["88"],
//            "inAppUpdatePriority": 5,
//            "status": "completed"
//        }]
//    }
}
