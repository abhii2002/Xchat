package com.blissvine.xchat.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.R
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.Purple40
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.ui.theme.primaryWhiteColor
import com.blissvine.xchat.utils.GlideImage
import com.blissvine.xchat.utils.commonProgressBar
import com.blissvine.xchat.utils.navigateTo
import javax.annotation.Untainted


@Composable
fun ProfileScreen(navController: NavController, vm: XchatViewModel) {
    val isInProcess = vm.inProcess.value

    Scaffold(
        topBar = {
            TopAppBarWithBackAndMenu(
                onBackPressed = {
                    navigateTo(navController, DestinationScreen.ChatList.route, popUpToRoute = DestinationScreen.Profile.route, inclusive = true)
                },
                onLogout = {
                    vm.logout()
                    navController.navigate(DestinationScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.PROFILELIST,
                navController = navController,

            )
        }
    ) { innerPadding ->
        if (isInProcess) {
            commonProgressBar()
        } else {
            val userData = vm.userData.value
            var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
            var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }
            val email = userData?.email ?: ""

            profileContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                vm = vm,
                name = name,
                number = number,
                email = email,
                onNameChange = { name = it },
                onNumberChange = { number = it },
            )
        }
    }
}


//@Composable
//fun ProfileScreen(navController: NavController, vm: XchatViewModel) {
//    val isInProcess = vm.inProcess.value
//
//    if (isInProcess) {
//        commonProgressBar()
//    } else {
//        val userData = vm.userData.value
//        var name by rememberSaveable{
//            mutableStateOf(userData?.name?:"")
//        }
//        var number by rememberSaveable{
//            mutableStateOf(userData?.number ?:"")
//        }
//         var email = userData?.email ?:""
//
//        Column() {
//            TopAppBarWithBackAndMenu(onBackPressed = {
//                navigateTo(navController = navController, route = DestinationScreen.ChatList.route)
//            }) {
//
//                vm.logout()
//                navigateTo(navController = navController, route = DestinationScreen.Login.route)
//
//            }
//
//            profileContent(
//                modifier = Modifier.weight(1f),
//                vm = vm,
//                name= name,
//                number = number,
//                email = email,
//                onNameChange = {name = it},
//                onNumberChange = {number = it},
//                )
//
//        }
//
//
//    }
//
//
//}

@Composable
fun profileContent(
    modifier: Modifier,
    vm: XchatViewModel,
    name: String,
    number: String,
    email : String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
) {
    val imageUrl = vm.userData.value?.imageUrl
    var passwordVisibleState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ProfileImageWithCameraIcon(imageUrl = imageUrl, onCameraClick = ({/* */ }), vm = vm)


        TextField(
            value = name,
            onValueChange = {
                onNameChange(it)
            },
            label = { Text(text = "Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryColor,
                cursorColor = primaryColor,
                focusedLabelColor = primaryColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "NameField",
                    tint = Purple40

                )
            },
            singleLine = true

        )

        TextField(
            value = email,
            onValueChange = {

            },
            label = { Text(text = "Email@example.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, top = 10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryColor,
                cursorColor = primaryColor,
                focusedLabelColor = primaryColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email Icon",
                    tint = Purple40

                )
            },
            singleLine = true,
            readOnly = true


        )


        TextField(
            value = number,
            onValueChange = {
               onNumberChange(it)
            },
            label = { Text(text = "Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryColor,
                cursorColor = primaryColor,
                focusedLabelColor = primaryColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "NumberField",
                    tint = Purple40

                )
            },
            singleLine = true

        )
        
        GradientButtonSave(text = "Save", onClick = {

            vm.createOrUpdateProfile(name = name, number = number)
        })



        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackAndMenu(
    onBackPressed: () -> Unit,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text("Profile", color = Color.Black) // Title of the screen
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
            }
            // Dropdown menu for logout
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.log_out_04_svgrepo_com),
                            contentDescription = "Logout",
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    onClick = {
                        expanded = false
                        onLogout() // Trigger logout action
                    }
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun ProfileImageWithCameraIcon(
    imageUrl: String? = null, // Provide an image URL or null for placeholder
    onCameraClick: () -> Unit,
    vm: XchatViewModel // Pass the ViewModel for uploading images
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                vm.uploadProfileImage(uri) // Upload the selected image
            }
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {
        // Circle Image Placeholder
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable {
                    launcher.launch("image/*")
                }
        ) {
            if (imageUrl != null) {
                GlideImage(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 1.dp, y = (-1).dp)
                .size(40.dp)
                .background(primaryColor, CircleShape)
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera_svgrepo_com),
                contentDescription = "Camera Icon",
                tint = Color.White, // White color for the icon
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun GradientButtonSave(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, top = 40.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, primaryWhiteColor)
                ),
                shape = RoundedCornerShape(12.dp) // Rounded corners
            )
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 13.dp, bottom = 13.dp)

        )
    }
}

