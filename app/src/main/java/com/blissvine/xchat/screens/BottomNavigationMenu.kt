package com.blissvine.xchat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.R
import com.blissvine.xchat.utils.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.chat_line_square_svgrepo_com__1_, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.status_svgrepo_com, DestinationScreen.StatusList),
    PROFILELIST(R.drawable.profile_1336_svgrepo_com, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        for (item in BottomNavigationItem.entries) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                modifier = Modifier
                    .size(35.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(
                            navController = navController, item.navDestination.route, popUpToRoute = item.navDestination.route
                        )
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(color = Color.Black) else ColorFilter.tint(
                    color = Color.Gray
                )
            )
        }
    }

}