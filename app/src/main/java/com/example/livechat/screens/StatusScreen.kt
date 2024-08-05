package com.example.livechat.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.CommonProgressBar
import com.example.livechat.LCViewModel
import com.example.livechat.TitleText

@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {
    val inProcess = vm.inProgression.value
    if(inProcess)
    {
        CommonProgressBar()
    }
    else
    {
        val statuses = vm.status.value
        val userData = vm.userData.value


        Scaffold(floatingActionButton = {
            FAB {
                {}
            }
        }, content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                TitleText(title = "Status")
                if(statuses.isEmpty()){
                    Column(modifier = Modifier.) {

                    }
                }

            }
        })
    BottomNavigationMenu(selected = BottomNavigationItem.STATUSLIST, navController = navController)}

}

@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(40.dp)
    ) {
Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Add Status", tint =  Color.White)
    }
}