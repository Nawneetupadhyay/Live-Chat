package com.example.livechat.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.CommonDivider
import com.example.livechat.CommonProgressBar
import com.example.livechat.CommonRow
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.TitleText
import com.example.livechat.navigateTo

@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {
    val inProcess = vm.inProgression.value
    if (inProcess) {
        CommonProgressBar()
    } else {
        val statuses = vm.status.value
        val userData = vm.userData.value

        val myStatuses = statuses.filter { it.user.userId == userData?.userId }

        val otherStatuses = statuses.filter { it.user.userId != userData?.userId }






        Scaffold(floatingActionButton = {
            FAB {
                {}
            }
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                TitleText(title = "Status")
                if (statuses.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Status Available")
                    }
                } else {
                    if (myStatuses.isNotEmpty()) {
                        CommonRow(
                            imageUrl = myStatuses[0].user.imageUrl,
                            name = myStatuses[0].user.name
                        ) {
                            navigateTo(
                                navController,
                                DestinationScreens.SingleStatus.createRoute(myStatuses[0].user.userId!!)
                            )
                        }

                        CommonDivider()
                        val UniqueUsers = otherStatuses.map { it.user }.toSet().toList()
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(UniqueUsers) { user ->
                                CommonRow(imageUrl = user.imageUrl, name = user.name) {
                                    navigateTo(
                                        navController,
                                        DestinationScreens.SingleStatus.createRoute(user.userId!!)
                                    )

                                }

                            }

                        }
                    }
                    BottomNavigationMenu(
                        selected = BottomNavigationItem.STATUSLIST,
                        navController = navController
                    )
                }

            }
        })

    }

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
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}