package com.example.livechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.livechat.ui.theme.LiveChatTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.livechat.screens.ChatScreen
import com.example.livechat.screens.LoginScreen
import com.example.livechat.screens.ProfileScreen
import com.example.livechat.screens.SignUpScreen
import com.example.livechat.screens.StatusScreen
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreens(var route: String) {
    object SignUp : DestinationScreens("signup")
    object Login : DestinationScreens("login")
    object Profile : DestinationScreens("chat")
    object ChatList : DestinationScreens("Chatlist")
    object StatusList : DestinationScreens("StatusList")
    object SingleChat : DestinationScreens("singlechat/{chatId}")
    {
        fun createRoute(chatId: String) = "singlechat/$chatId"

    }

    object SingleStatus : DestinationScreens("singlechat/{chatId}")
    {
        fun createRoute(userId: String) = "singlechat/$userId"

    }


}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiveChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    ChatAppNavigation()

                }
            }
        }
    }


    @Composable
    fun ChatAppNavigation() {
val navController = rememberNavController()
        val vm = hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreens.SignUp.route){
            composable(DestinationScreens.SignUp.route){
                SignUpScreen(navController,vm)
            }
            composable(DestinationScreens.Login.route){
                LoginScreen(vm,navController)
            }
            composable(DestinationScreens.ChatList.route){
                ChatScreen()
            }
            composable(DestinationScreens.StatusList.route){
                StatusScreen()
            }
            composable(DestinationScreens.Profile.route){
                ProfileScreen()
            }
        }
    }
}


