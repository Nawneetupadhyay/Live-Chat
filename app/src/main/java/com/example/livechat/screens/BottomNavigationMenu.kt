package com.example.livechat.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.livechat.DestinationScreens
import com.example.livechat.R

enum class BottomNavigationItem(val icon: Int, val destinationScreens: DestinationScreens) {
    CHATLIST(R.drawable.chat, DestinationScreens.ChatList),
    STATUSLIST(R.drawable.status, DestinationScreens.StatusList),
    PROFILE(R.drawable.profile, DestinationScreens.Profile)

}
@Composable
fun BottomNavigationMenu(selected: BottomNavigationItem, navController: NavController) {
    Row {
        
    }
}