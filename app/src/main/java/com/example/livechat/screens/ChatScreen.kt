package com.example.livechat.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.CommonProgressBar
import com.example.livechat.CommonRow
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.TitleText
import com.example.livechat.navigateTo

@Composable
fun ChatScreen(navController: NavController, vm: LCViewModel) {
    val inprogrss = vm.inProcessChat.value
    if (inprogrss) {
        CommonProgressBar()
    } else {
        val chats = vm.chats.value
        val usrData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = {
            showDialog.value = true
        }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            vm.addchat(it)
            showDialog.value = false

        }

        Scaffold(floatingActionButton = {
            Fab(
                showDialog = showDialog.value,
                onDismiss = { onDismiss },
                onFabClick = { onFabClick },
                onAddChat = { onAddChat })
        }, content = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(it)) {
                TitleText(title = "Chats")

                if(chats.isEmpty())
                {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
 Text(text = "No chats Available")
                    }
                }
                else
                {
                    LazyColumn(modifier = Modifier.weight(1f)){
                        items(chats){
                            chat ->
                            val chatUser = if(chat.user1.userId == usrData?.userId) chat.user2 else chat.user1
                            chat.chatId?.let{
                                CommonRow(imageUrl = chatUser.imageUrl, name = chatUser.name) {

                                    navigateTo(
                                        navController,
                                        DestinationScreens.SingleChat.createRoute(chatId = it)
                                    )

                                }
                            }
                        }

                    }
                }
                BottomNavigationMenu(
                    selected = BottomNavigationItem.CHATLIST,
                    navController = navController
                )
            }
        })
    }


}

@Composable
fun Fab(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    onAddChat: (String) -> Unit // Modified to accept a String parameter
) {

    var addChatNumber = remember { mutableStateOf("") } // Use 'by' for state delegation

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                addChatNumber.value = ""
            }, // Removed unnecessary addChatMember.value
            title = { Text("Add Chat Member") }, // Added a title for clarity
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAddChat(addChatNumber.value) // Pass the member name to onAdd
                }) {
                    Text("Add")
                }
            },

            )

        FloatingActionButton(
            onClick = { onFabClick() },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {

            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}