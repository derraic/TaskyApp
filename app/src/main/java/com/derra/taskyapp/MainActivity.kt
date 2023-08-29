package com.derra.taskyapp

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.derra.taskyapp.presentation.agenda.DayTasksScreen
import com.derra.taskyapp.presentation.authentication.LoginScreen
import com.derra.taskyapp.presentation.authentication.RegisterScreen
import com.derra.taskyapp.presentation.authentication.SplashScreen
import com.derra.taskyapp.presentation.event.EventScreen
import com.derra.taskyapp.presentation.reminder.ReminderScreen
import com.derra.taskyapp.presentation.task.TaskScreen
import com.derra.taskyapp.ui.theme.TaskyAppTheme
import com.derra.taskyapp.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            TaskyAppTheme {
                val navController = rememberNavController()
                NavControllerHolder.setNavController(navController)

                NavHost(navController = navController, startDestination = Routes.SPLASH_SCREEN) {
                    composable(Routes.SPLASH_SCREEN)
                    {
                        SplashScreen(onNavigate = {navController.navigate(it.route)}, window = window)
                    }
                    composable(Routes.LOGIN_SCREEN){
                        LoginScreen(onNavigate = {navController.navigate(it.route)}, window = window)
                        
                    }
                    composable(Routes.REGISTER_SCREEN) {
                        RegisterScreen(onPopBackStack = {navController.popBackStack()}, window = window)
                    }
                    composable(Routes.DAY_TASKS_SCREEN) {

                        DayTasksScreen(onNavigate = {navController.navigate(it.route)}, onPopBackStack = {navController.popBackStack()}, window = window)
                    }
                    composable(Routes.EDIT_DETAIL_EVENT_SCREEN + "?eventId={eventId}" + "&isEditable={isEditable}",
                        arguments = listOf(navArgument(name = "eventId") {
                            type = NavType.StringType
                            defaultValue = "NONE"
                        }, navArgument(name = "isEditable") {
                            type = NavType.BoolType
                            defaultValue = false
                        })
                    ) {
                        EventScreen(onPopBackStack = { navController.popBackStack()}, context = this@MainActivity, window = window)
                    }
                    composable(Routes.EDIT_DETAIL_TASK_SCREEN + "?taskId={taskId}" + "&isEditable={isEditable}",
                        arguments = listOf(navArgument(name = "taskId") {
                            type = NavType.StringType
                            defaultValue = "NONE"
                        }, navArgument(name = "isEditable") {
                            type = NavType.BoolType
                            defaultValue = false
                        })
                    ) {

                        TaskScreen(onPopBackStack = { navController.popBackStack()}, window = window)
                    }
                    composable(Routes.EDIT_DETAIL_REMINDER_SCREEN + "?reminderId={reminderId}" + "&isEditable={isEditable}",
                        arguments = listOf(navArgument(name = "reminderId") {
                            type = NavType.StringType
                            defaultValue = "NONE"
                        }, navArgument(name = "isEditable") {
                            type = NavType.BoolType
                            defaultValue = false
                        })
                    ) {

                        ReminderScreen(onPopBackStack = { navController.popBackStack()}, window = window)
                    }
                }

            }
        }
    }

}
