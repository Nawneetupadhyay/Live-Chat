package com.example.livechat.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.livechat.CommonDivider
import com.example.livechat.CommonImage
import com.example.livechat.LCViewModel
import com.example.livechat.data.Message

@Composable
fun SingleChatScreen(navController: NavHostController, vm: LCViewModel, chatId: String) {

    var reply = rememberSaveable {
        mutableStateOf("")

    }

    var chatMessages = vm.chatMessages

    val onSendReply = {
        vm.onSendReply(chatId, reply.value)
        reply.value = ""
    }

    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }

    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }
    BackHandler {
        vm.depopulateMessages()

    }

    Column {

        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            onBackClicked = { navController.popBackStack() })
        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessages.value,
            currentUserId = myUser?.userId ?: ""
        )
        ReplyBox(
            reply = reply.value,
            onReplyChange = { reply.value = it },
            onSendReply = onSendReply
        )
    }
}


@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {

    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->

            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)

            Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = alignment) {
                Text(text = msg.message?: "", color = Color.White,modifier = Modifier.clip(
                    RoundedCornerShape(8.dp)).background(color).padding(12.dp), fontWeight = FontWeight.Bold
                )

            }


        }
    }
}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .clickable { onBackClicked() }
                .padding(8.dp)
        )
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))


    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,
                maxLines = 3,
            )

            Button(onClick = onSendReply) {
                Text("Send")
            }

        }

    }
}