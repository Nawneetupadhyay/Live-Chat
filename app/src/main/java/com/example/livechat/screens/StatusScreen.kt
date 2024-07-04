package com.example.livechat.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.livechat.LCViewModel

@Composable
fun StatusScreen (navController: NavController, vm: LCViewModel) {
    BottomNavigationMenu(selected = BottomNavigationItem.STATUSLIST, navController = navController)

}