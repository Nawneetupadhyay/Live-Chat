package com.example.livechat.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.CommonProgressBar
import com.example.livechat.LCViewModel

@Composable
fun ChatScreen(navController: NavController,vm: LCViewModel) {
    val inprogrss = vm.inProgression.value
    if(inprogrss)
    {
        CommonProgressBar()
    }
    else

    BottomNavigationMenu(selected = BottomNavigationItem.CHATLIST, navController =navController )
}