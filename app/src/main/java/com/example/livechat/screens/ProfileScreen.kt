package com.example.livechat.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.livechat.LCViewModel

@Composable
fun ProfileScreen(navController: NavHostController, vm: LCViewModel) {
    BottomNavigationMenu(selected = BottomNavigationItem.PROFILE, navController = navController)

}