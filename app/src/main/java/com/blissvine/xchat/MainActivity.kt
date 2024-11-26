package com.blissvine.xchat


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blissvine.xchat.screens.BottomNavigationItem
import com.blissvine.xchat.screens.BottomNavigationMenu
import com.blissvine.xchat.screens.ChatListScreen
import com.blissvine.xchat.screens.LoginScreen
import com.blissvine.xchat.screens.ProfileScreen
import com.blissvine.xchat.screens.SignUpScreen
import com.blissvine.xchat.screens.SingleChatScreen
import com.blissvine.xchat.screens.SingleStatusScreen
import com.blissvine.xchat.screens.StatusScreen

import com.blissvine.xchat.ui.theme.XchatTheme
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route: String){
    object SignUp: DestinationScreen("signup")
    object Login: DestinationScreen("login")
    object Profile: DestinationScreen("profile")
    object ChatList: DestinationScreen("chatList")
    object SingleChat: DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }

    object StatusList: DestinationScreen("StatusList")

    object SingleStatus: DestinationScreen("singleStatus/{userId}") {
        fun createRoute(userId: String) = "singleStatus/$userId"
    }


}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        setContent {
            XchatTheme {
              Surface (modifier = Modifier.fillMaxSize(), color =  MaterialTheme.colorScheme.background){
                  ChatAppNavigation()

              }
            }
        }
    }
}


//@Composable
//fun ChatAppNavigation() {
//    val navController = rememberNavController()
//    val vm = hiltViewModel<XchatViewModel>()
//
//    NavHost(
//        navController = navController,
//        startDestination = DestinationScreen.Login.route
//    ) {
//        // Login Screen
//        composable(
//            route = DestinationScreen.Login.route,
//            enterTransition = { fadeIn(animationSpec = tween(400)) },
//            exitTransition = { fadeOut(animationSpec = tween(400)) }
//        ) {
//            LoginScreen(navController, vm)
//        }
//
//        // SignUp Screen
//        composable(
//            route = DestinationScreen.SignUp.route,
//            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(700)) },
//            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(700)) }
//        ) {
//            SignUpScreen(navController, vm)
//        }
//    }
//}


//@Composable
//fun ChatAppNavigation() {
//    val navController = rememberNavController()
//    var vm = hiltViewModel<XchatViewModel>()
//    var selectedItem by remember { mutableStateOf(BottomNavigationItem.CHATLIST) }
//    Scaffold(
//        bottomBar = {
//            BottomNavigationMenu(selectedItem = selectedItem, navController = navController)
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = DestinationScreen.Login.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(DestinationScreen.Login.route) {
//                LoginScreen(navController, vm)
//            }
//
//            composable(DestinationScreen.SignUp.route) {
//                SignUpScreen(navController, vm)
//            }
//
//            composable(DestinationScreen.ChatList.route) {
//                // Update selected item when navigating to the ChatList
//                selectedItem = BottomNavigationItem.CHATLIST
//                ChatListScreen(navController, vm)
//            }
//
//            composable(DestinationScreen.StatusList.route) {
//                // Update selected item when navigating to the StatusList
//                selectedItem = BottomNavigationItem.STATUSLIST
//                StatusScreen()
//            }
//
//            composable(DestinationScreen.Profile.route) {
//                // Update selected item when navigating to the Profile screen
//                selectedItem = BottomNavigationItem.PROFILELIST
//                ProfileScreen(navController = navController, vm)
//            }
//        }
//    }
//}




@Composable
fun ChatAppNavigation(){
    val navController = rememberNavController()
    var vm = hiltViewModel<XchatViewModel>()
    NavHost(navController = navController, startDestination = DestinationScreen.Login.route) {
        composable(DestinationScreen.SignUp.route) {
            SignUpScreen(navController, vm)
        }

        composable(DestinationScreen.Login.route) {
            LoginScreen(navController, vm)
        }

        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(navController, vm)
        }

        composable(DestinationScreen.StatusList.route) {
            StatusScreen(navController, vm)
        }

        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, vm)
        }

        composable(DestinationScreen.SingleChat.route) { it ->
            val chatId = it.arguments?.getString("chatId")
            chatId?.let {
                SingleChatScreen(navController = navController, vm = vm, chatId = chatId)
            }

        }

        composable(DestinationScreen.SingleStatus.route) { it ->
            val userId = it.arguments?.getString("userId")
            userId?.let {
                SingleStatusScreen(navController, vm, it)
            }

        }
    }
}


