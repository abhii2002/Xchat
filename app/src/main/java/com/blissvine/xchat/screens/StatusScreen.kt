package com.blissvine.xchat.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.models.Status
import com.blissvine.xchat.models.UserData
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.utils.CommonRow
import com.blissvine.xchat.utils.TitleText
import com.blissvine.xchat.utils.commonProgressBar
import com.blissvine.xchat.utils.navigateTo

@Composable
fun StatusScreen(navController: NavController, vm: XchatViewModel) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri->
        uri?.let {
            vm.uploadStatus(uri)
        }

    }
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.CHATLIST,
                navController = navController
            )
        },
        floatingActionButton = {

            StatusFAB(modifier = Modifier.padding(16.dp)) {
                launcher.launch("image/*")
            }
        }
    ) { innerPadding ->
        StatusContent(
            inProcess = vm.inProcessStatus.value,
            status = vm.status.value,
            userData = vm.userData.value,
            innerPadding = innerPadding,
            navController = navController
        )
    }
}

@Composable
fun StatusContent(
    inProcess: Boolean,
    status: List<Status>,
    userData: UserData?,
    innerPadding: PaddingValues,
    navController: NavController,

) {
    if (inProcess) {
        commonProgressBar()
    } else {

        val myStatus = status.filter {
            it.user.userId == userData?.userId
        }

        val othersStatus = status.filter {
            it.user.userId != userData?.userId
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TitleText(text = "Status")

            Spacer(modifier = Modifier.height(15.dp))

            if (status.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No status available.", fontSize = 19.sp )
                }
            } else {
                if (myStatus.isNotEmpty()) {
                    CommonRow(imageUrl = myStatus[0].imageUrl, name = myStatus[0].user.name) {
                        navigateTo(
                            navController = navController,
                            DestinationScreen.SingleStatus.createRoute(myStatus[0].user.userId!!)
                        )
                    }
                }

                val uniqueUsers = othersStatus.map { it.user }.toSet().toList()

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uniqueUsers) { user ->
                        CommonRow(imageUrl = user.imageUrl, name = user.name) {
                            navigateTo(
                                navController,
                                DestinationScreen.SingleStatus.createRoute(user.userId!!)
                            )

                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StatusFAB(
    modifier: Modifier,
    onFabclick: () -> Unit,

    ) {

    FloatingActionButton(
        onClick = { onFabclick.invoke() },
        containerColor = primaryColor,
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Rounded.Camera, contentDescription = null, tint = Color.White)
    }
}
