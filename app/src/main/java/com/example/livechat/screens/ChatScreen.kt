package com.example.livechat.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.livechat.LCViewModel

@Composable
fun ChatScreen(navController: NavController,vm: LCViewModel) {
    BottomNavigationMenu(selected = BottomNavigationItem.CHATLIST, navController = navController)

}