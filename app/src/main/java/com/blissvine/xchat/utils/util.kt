package com.blissvine.xchat.utils

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.blissvine.xchat.DestinationScreen
import com.blissvine.xchat.R
import com.blissvine.xchat.XchatViewModel
import com.blissvine.xchat.ui.theme.primaryColor
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop

fun navigateTo(
    navController: NavController,
    route: String,
    popUpToRoute: String? = null,
    inclusive: Boolean = false
) {
    navController.navigate(route) {
        popUpToRoute?.let {
            popUpTo(it) { this.inclusive = inclusive }
        }
        launchSingleTop = true
    }
}




@Composable
fun commonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(primaryColor)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}





@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun ToastHandler(viewModel: XchatViewModel) {
    val context = LocalContext.current
    val event = viewModel.eventMutableState.value

    // Observe the event and show the toast
    event?.getcontentOrNull()?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

@Composable
fun GlideImage(imageUrl: String, modifier: Modifier) {
    val context = LocalContext.current
    AndroidView(
        factory = { ImageView(context) },
        modifier = modifier,  // Apply the modifier here to ensure size and layout changes
        update = { imageView ->
            Glide.with(context)
                .load(imageUrl)
                .transform(CenterCrop())
                .into(imageView)
        }
    )
}

@Composable
fun GlideImageStatus(imageUrl: String, modifier: Modifier) {
    val context = LocalContext.current
    AndroidView(
        factory = { ImageView(context) },
        modifier = modifier,  // Apply the modifier here to ensure size and layout changes
        update = { imageView ->
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER // Set the content scale to fit
            Glide.with(context)
                .load(imageUrl)
                .into(imageView)
        }
    )
}



@Composable
fun checkSignedIn(vm: XchatViewModel, navController: NavController) {
    val isAlreadySignedIn = remember { mutableStateOf(false) }
    val signIn = vm.signIn.value
    if (signIn && !isAlreadySignedIn.value) {
        isAlreadySignedIn.value = true
        navigateTo(
            navController = navController,
            route = DestinationScreen.ChatList.route,
            popUpToRoute = DestinationScreen.Login.route,
            inclusive = true
        )

    }
}

@Composable
fun CommonRow(
    imageUrl: String?,
    name: String?,
    fontSize: TextUnit = 16.sp,
    onItemClick: () -> Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(start = 5.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageUrl != null) {
            GlideImage(
                imageUrl = imageUrl,
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .clip(shape = CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
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

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = name ?: "Unknown",
            fontSize = fontSize,  // Use the fontSize parameter here
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun CommonRowStatusScreen(imageUrls: List<String>?, name: String?, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(start = 5.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the first image as a profile image
        if (!imageUrls.isNullOrEmpty()) {
            GlideImage(
                imageUrl = imageUrls.first(), // Display the first image in the list
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .clip(shape = CircleShape)
            )
        } else {
            // Fallback for users without any status images
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
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

        Spacer(modifier = Modifier.width(8.dp))

        // User's name
        Text(
            text = name ?: "Unknown",
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Show count of additional images (e.g., "+2" for more images)
        if (!imageUrls.isNullOrEmpty() && imageUrls.size > 1) {
            Text(
                text = "+${imageUrls.size - 1}", // Show count of additional images
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}


