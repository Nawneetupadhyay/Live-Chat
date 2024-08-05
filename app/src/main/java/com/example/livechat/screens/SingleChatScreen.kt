package com.example.livechat.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable


import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.livechat.CommonDivider
import com.example.livechat.LCViewModel

@Composable
fun SingleChatScreen(navController: NavHostController, vm: LCViewModel, it1: String) {

    var reply  = rememberSaveable {
        mutableStateOf("")

    }


    ReplyBox(reply = reply.value, onReplyChange = {reply.value = it}) {

    }
}

@Composable
fun ReplyBox(reply:String,onReplyChange:(String)->Unit,onSendReply:()->Unit)
{
Column(modifier = Modifier.fillMaxWidth()) {
    CommonDivider()
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
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