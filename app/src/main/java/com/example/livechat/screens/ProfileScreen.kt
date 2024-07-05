package com.example.livechat.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.livechat.CommonDivider
import com.example.livechat.CommonImage
import com.example.livechat.CommonProgressBar
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.navigateTo

@Composable
fun ProfileScreen(navController: NavHostController, vm: LCViewModel) {


    val inProgress = vm.inProgression.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val userData = vm.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }
        Column {
            ProfileContent(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(8.dp),
                vm = vm,
                name =  name,
                number =  number,
                onBackClicked = {
                                navigateTo(navController,DestinationScreens.ChatList.route)
                },
                onSaveClicked = {
                                vm.createOrUpdateProfile(name,number)
                },
                onNameChanged = {name = it},
                onNumberChanged = {number = it},
                onLogoutClicked = {
                    vm.logout()
                    navigateTo(navController,DestinationScreens.Login.route)

                }
            )


            BottomNavigationMenu(
                selected = BottomNavigationItem.CHATLIST,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    onBackClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
    vm: LCViewModel,
    name: String,
    number: String,
    onNameChanged: (String) -> Unit = {},
    onNumberChanged: (String) -> Unit = {},
    onLogoutClicked: () -> Unit = {}
) {
    val imageUrl = vm.userData.value?.imageUrl
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "back", Modifier.clickable {
                onBackClicked()
            })
            Text(text = "save", Modifier.clickable {
                onSaveClicked()
            })
            CommonDivider()
            ProfileImage(imageUrl = imageUrl, vm = vm)
            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Name", modifier = Modifier.width(100.dp))
                TextField(
                    value = name,
                    onValueChange = onNameChanged,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = Color.Black,
                        containerColor = Color.Transparent,
                    )
                )


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Number", modifier = Modifier.width(100.dp))
                TextField(
                    value = number,
                    onValueChange = onNumberChanged,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = Color.Black,
                        containerColor = Color.Transparent,
                    )
                )


            }

            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Logout", Modifier.clickable {
                    onLogoutClicked()
                })

            }


        }
    }

}

@Composable
fun ProfileImage(imageUrl: String?, vm: LCViewModel) {

    val launchers = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                vm.uploadProfileImage(it)
            }
        })

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min))
    {
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launchers.launch("image/*")

                }, horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {

                CommonImage(data = imageUrl)

            }

            Text(text = "Change Profile Picture")


        }
        if (vm.inProgression.value) {
            CommonProgressBar()
        }
    }

}
