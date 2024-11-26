package com.blissvine.xchat.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blissvine.xchat.R
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.greyColor
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.ui.theme.primaryColorLight
import com.blissvine.xchat.utils.GlideImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SingleChatScreen(navController: NavController, vm: XchatViewModel, chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    // lambda function it dosent have any parameters but have a body with two statements
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    // Observe typing status changes

    /*
    Each Chat object contains information about two users (user1 and user2).
    If the currentUser's userId matches currentChat.user1.userId, it means the current user is user1, so the partner is user2.
    Otherwise, the current user is user2, so the partner is user1.
     */

    val currentUser = vm.userData.value
    var currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser = if (currentUser?.userId == currentChat.user1.userId) {
        currentChat.user2
    } else {
        currentChat.user1
    }

    var chatMessages = vm.chatMessage

    LaunchedEffect(key1 = Unit) {
        vm.populateAllMessages(chatId)
        vm.observeTypingStatus(chatId, currentUserId = currentUser?.userId!!)
    }
    BackHandler {
        vm.depopulateMessageOldChats()
        navController.popBackStack()

    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)

        ) {
            TopBarSingleChatScreen(
                name = chatUser.name ?: "",
                imageUrl = chatUser.imageUrl ?: "",
                onBackClick = {
                    navController.popBackStack()
                    vm.depopulateMessageOldChats()
                }, vm
            )

            chatBoxLayout(
                modifier = Modifier.weight(1f).padding(top = 20.dp),
                chatMessages = chatMessages.value,
                currentUserId = currentUser?.userId ?: ""
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {

                ReplyBox(
                    reply = reply,
                    onReplyChange = { reply = it },
                    onMessageSend = { onSendReply() },
                    vm = vm,
                    chatId = chatId,
                    currentUserId = currentUser?.userId
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSingleChatScreen(
    name: String,
    imageUrl: String,
    onBackClick: () -> Unit,
    vm: XchatViewModel
) {
    TopAppBar(
        modifier = Modifier.shadow(elevation = 1.dp),
        title = {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!imageUrl.isNullOrEmpty()) {
                    GlideImage(
                        imageUrl = imageUrl,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(shape = CircleShape)
                    )
                } else {

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(shape = CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user_place_holder),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }


                Spacer(modifier = Modifier.width(9.dp))

                Column() {
                    Text(
                        text = name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.W400,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )

                    if (vm.isUserTyping.value) {
                        Text(
                            text = "typing...",
                            fontSize = 12.sp,
                            color = Color.Black,
                            lineHeight = 16.sp,
                            modifier = Modifier.heightIn(0.dp)
                        )
                    }
                }



            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White
        ),


        )
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onMessageSend: () -> Unit,
    vm: XchatViewModel,
    chatId: String,
    currentUserId: String?
) {
    var lastTypedTime by remember { mutableStateOf(System.currentTimeMillis()) }


    LaunchedEffect(reply) {
        if (reply.isNotBlank() && currentUserId != null) {
            vm.setTypingStatus(chatId, currentUserId, true)
            lastTypedTime = System.currentTimeMillis()

        }

        if (reply.isBlank() || currentUserId != null) {
            kotlinx.coroutines.delay(3000) // 3 seconds delay
            if (System.currentTimeMillis() - lastTypedTime >= 3000) {
                vm.setTypingStatus(chatId, currentUserId!!, false)
            }
        }

    }

    DisposableEffect(key1 = chatId) {
        onDispose {
            if (currentUserId != null) {
                vm.setTypingStatus(chatId, currentUserId, false)
            }
        }
    }


    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input box
        BasicTextField(
            value = reply,
            onValueChange = {
                onReplyChange(it)
                lastTypedTime = System.currentTimeMillis()
            },
            modifier = Modifier
                .weight(1f)
                .background(
                    Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (reply.isNotBlank()) {
                        onMessageSend()
                    }
                }
            ),
            decorationBox = { innerTextField ->
                if (reply.isEmpty()) {
                    Text("Type a message...", color = Color.Gray, fontSize = 16.sp)
                }
                innerTextField()
            },
            maxLines = 4,
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Action button (Voice or Send)
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(primaryColor)
                .size(45.dp)
                .padding(6.dp),
            onClick = {
                if (reply.isNotBlank()) {
                    onMessageSend()
                } else {
                    // onVoiceRecord()
                }
            }
        ) {
            Icon(
                imageVector = if (reply.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                contentDescription = if (reply.isNotBlank()) "Send" else "Voice",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}


@Composable
fun chatBoxLayout(
    modifier: Modifier,
    chatMessages: List<com.blissvine.xchat.models.Message>,
    currentUserId: String
) {
    val listState = rememberLazyListState()  // Track scroll position

    // Scroll to the last message whenever the chatMessages change, but only if there are messages
    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(chatMessages) { msg ->
            // Determine the alignment based on whether the message is from the current user
            val alignment = if (msg.sentBy == currentUserId) {
                Alignment.End
            } else {
                Alignment.Start
            }

            val color = if (msg.sentBy == currentUserId) {
                primaryColorLight
            } else {
                greyColor
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalAlignment = alignment
            ) {
                // Box for the chat message with width restrictions
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color)
                        .padding(
                            top = 6.dp,
                            bottom = 6.dp,
                            start = if (alignment == Alignment.End) 0.dp else 0.dp,
                            end = if (alignment == Alignment.Start) 40.dp else 40.dp
                        )
                        .widthIn(0.dp, 300.dp)
                        .wrapContentWidth()
                ) {

                    Text(
                        text = msg.message ?: "",
                        textAlign = TextAlign.Start,
                        color = Color.Black,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }


                val formattedTime = formatTimestamp(msg.timeStamp ?: "")


                Text(
                    text = formattedTime,
                    textAlign = TextAlign.Start,
                    color = Color.Gray,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 0.dp)
                )
            }
        }
    }
}


fun formatTimestamp(timestamp: String): String {
    return try {
        val timeInMillis = timestamp.toLong()
        val date = Date(timeInMillis)

        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        timestamp
    }
}


