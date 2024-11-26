package com.blissvine.xchat.screens

import android.content.res.Resources.Theme
import android.provider.CalendarContract.Colors
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.R
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.Purple40
import com.blissvine.xchat.ui.theme.XchatTheme
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.ui.theme.primaryWhiteColor
import com.blissvine.xchat.utils.checkSignedIn
import com.blissvine.xchat.utils.commonProgressBar
import com.blissvine.xchat.utils.navigateTo

@Composable
fun SignUpScreen(navController: NavController, vm: XchatViewModel) {
    checkSignedIn(vm = vm, navController = navController)

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                NavigationBarWithBack(
                    title = "Back"
                ) {

                    navController.navigateUp()
                }
            }
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues) // Apply scaffold padding
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding(), // Automatically adjust for keyboard
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val emailState = remember { mutableStateOf(TextFieldValue()) }
                    val passwordState = remember { mutableStateOf(TextFieldValue()) }
                    val nameState = remember { mutableStateOf(TextFieldValue()) }
                    val numberState = remember { mutableStateOf(TextFieldValue()) }
                    var passwordVisibleState by remember { mutableStateOf(false) }

                    Text(
                        text = "Sign Up",
                        fontSize = 30.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, bottom = 20.dp)
                            .padding(8.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.W500,
                        color = primaryColor,
                        textAlign = TextAlign.Left,
                    )

                    // Name TextField
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
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

                    // Number TextField
                    TextField(
                        value = numberState.value,
                        onValueChange = { numberState.value = it },
                        label = { Text(text = "Number") },
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
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "Phone Icon",
                                tint = Purple40
                            )
                        },
                        singleLine = true
                    )

                    // Email TextField
                    TextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
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
                        singleLine = true
                    )

                    // Password TextField
                    TextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text(text = "Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            focusedIndicatorColor = primaryColor,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        ),
                        visualTransformation = if (passwordVisibleState) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Pass Icon",
                                tint = Purple40
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibleState = !passwordVisibleState }) {
                                Icon(
                                    painter = if (passwordVisibleState) {
                                        painterResource(id = R.drawable.visible_svgrepo_com)
                                    } else {
                                        painterResource(id = R.drawable.not_visible_svgrepo_com)
                                    },
                                    contentDescription = if (passwordVisibleState) "Visible" else "Not Visible",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        },
                        singleLine = true,
                    )

                   

                    // Sign Up Button
                    GradientButtonSignUp(
                        text = "Sign Up",
                        onClick = {
                            vm.signUp(
                                nameState.value.text,
                                numberState.value.text,
                                emailState.value.text,
                                passwordState.value.text
                            )
                        },
                    )

                    // Sign In Link
                    Text(
                        text = "Already have an account ? Sign In ",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 20.dp)
                            .clickable {
                                navigateTo(
                                    navController = navController,
                                    route = DestinationScreen.Login.route,
                                    popUpToRoute = DestinationScreen.Login.route,
                                    inclusive = true
                                )
                            },
                        color = primaryColor,
                        fontWeight = FontWeight.W400,
                        fontSize = 15.sp
                    )
                }
            }
        }
    )

    if (vm.inProcess.value) {
        commonProgressBar()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarWithBack(
    title: String,
    onBackClick: () -> Unit
) {


    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
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
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun GradientButtonSignUp(
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

