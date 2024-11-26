package com.blissvine.xchat.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.utils.CommonRow
import com.blissvine.xchat.utils.TitleText
import com.blissvine.xchat.utils.ToastHandler
import com.blissvine.xchat.utils.commonProgressBar
import com.blissvine.xchat.utils.navigateTo
import kotlinx.coroutines.time.delay

@Composable
fun ChatListScreen(navController: NavController, vm: XchatViewModel) {
    ToastHandler(vm)

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.CHATLIST,
                navController = navController
            )
        }, floatingActionButton = {
            val showDialog = remember { mutableStateOf(false) }
            val onFabClick: () -> Unit = { showDialog.value = true }
            val onFabDismiss: () -> Unit = { showDialog.value = false }
            val onAddChat: (String) -> Unit = {
                vm.onAddChat(it)
                showDialog.value = false
            }

            FAB(
                modifier = Modifier
                    .padding(16.dp),
                showDialog = showDialog.value,
                onFabclick = onFabClick,
                onDismiss = onFabDismiss,
                onAddChat = onAddChat
            )
        }
    ) { innerPadding ->

        val inProgress = vm.inProcess

        if (inProgress.value) {
            commonProgressBar()

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TitleText(
                        text = "Chats",
                    )

                    val chats = vm.chats.value
                    val userData = vm.userData.value


                    Spacer(modifier = Modifier.height(15.dp))

                    if (chats.isEmpty()) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f), // Centers "No Chats Available" vertically
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Chats Available", fontSize = 19.sp)
                        }
                    } else {

                        Log.d("chatwith", chats[0].user1.name!!)
                        Log.d("chatwith", chats[0].user2.name!!)
                        /*
                        Logic:
                    Each chat contains two users (user1 and user2).
                    Check if the current user's userId matches user1.userId.
                    If true: The other participant is user2.
                    If false: The other participant is user1.
                         */
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(chats) { chat -> // this is a single chat from list of chats
                                val chatUser = if (chat.user1.userId == userData?.userId) {
                                    chat.user2
                                } else {
                                    chat.user1
                                }

                                CommonRow(
                                    imageUrl = chatUser.imageUrl,
                                    name = chatUser.name,
                                    fontSize = 19.sp
                                ) {
                                    chat.chatId?.let {
                                        navigateTo(
                                            navController,
                                            DestinationScreen.SingleChat.createRoute(id = it)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}


@Composable
fun FAB(
    modifier: Modifier,
    showDialog: Boolean,
    onFabclick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {

    val addChatNumber = remember {
        mutableStateOf("")
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        }, confirmButton = {
            Button(onClick = { onAddChat(addChatNumber.value) }) {
                Text(text = "Add Chat")

            }
        },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

        )

    }


    FloatingActionButton(
        onClick = { onFabclick.invoke() },
        containerColor = primaryColor,
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }

}


@Composable
@Preview
fun ChatList() {
    val vm = hiltViewModel<XchatViewModel>()
    ChatListScreen(navController = rememberNavController(), vm = vm)
}