package com.blissvine.xchat.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.R
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.Purple40
import com.blissvine.xchat.ui.theme.primaryColor
import com.blissvine.xchat.ui.theme.primaryWhiteColor
import com.blissvine.xchat.utils.ToastHandler
import com.blissvine.xchat.utils.checkSignedIn
import com.blissvine.xchat.utils.commonProgressBar
import com.blissvine.xchat.utils.navigateTo

@Composable
fun LoginScreen(navController: NavController, vm: XchatViewModel) {
    checkSignedIn(vm = vm, navController = navController)
    ToastHandler(vm)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }

            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }

            var passwordVisibleState by remember { mutableStateOf(false) }

            Image(
                painter = painterResource(id = R.drawable.chat_2_svgrepo_com),
                contentDescription = "",
                modifier = Modifier
                    .width(125.dp)
                    .padding(top = 16.dp)


            )

            Text(
                text = "Xchat",
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(2.dp),
                color = primaryColor
            )

            Text(
                text = "MESSENGER",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(1.dp),
                color = primaryColor,
            )

            Text(
                text = "Sign In",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(8.dp),
                fontWeight = FontWeight.W500,
                color = primaryColor
            )

            TextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },
                label = { Text(text = "Email@example.com") },
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
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        tint = Purple40

                    )
                },
                singleLine = true

            )

            TextField(

                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                },
                label = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
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

            Text(
                text = "Forgot Password ?",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp, end = 30.dp, top = 10.dp
                    ),
                color = primaryColor,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )

            GradientButton(
                text = "LOGIN",
                onClick = {

                    vm.login(emailState.value.text, passwordState.value.text) }
            )

            Text(
                text = "New user? Signup",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp, end = 30.dp, top = 20.dp
                    )
                    .clickable {
                        navigateTo(navController = navController, DestinationScreen.SignUp.route)
                    },
                color = primaryColor,
                fontWeight = FontWeight.W400,
                fontSize = 15.sp
            )

        }

        if (vm.inProcess.value){
            commonProgressBar()
        }
    }



}


@Composable
fun GradientButton(
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

//@Composable
//@Preview(showBackground = true)
//fun previewLoginScreen() {
//
//    LoginScreen(navController = rememberNavController(), vm = XchatViewModel())
//}