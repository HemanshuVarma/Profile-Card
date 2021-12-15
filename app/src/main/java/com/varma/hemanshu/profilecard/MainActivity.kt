package com.varma.hemanshu.profilecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.varma.hemanshu.profilecard.data.UserProfile
import com.varma.hemanshu.profilecard.data.userProfileList
import com.varma.hemanshu.profilecard.ui.theme.ProfileCardTheme
import com.varma.hemanshu.profilecard.ui.theme.lightGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileCardTheme {
                UserApplication()
            }
        }
    }
}

@Composable
fun UserApplication(userProfiles: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "users_list") {
        composable(route = "users_list") {
            UserListsScreen(userProfiles = userProfiles, navController = navController)
        }
        composable(
            route = "user_details/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { navBackStackEntry ->
            UserDetailScreen(navBackStackEntry.arguments!!.getInt("userId"))
        }
    }
}

@Composable
fun UserListsScreen(userProfiles: List<UserProfile>, navController: NavController?) {
    Scaffold(topBar = { AppBar() }) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                items(userProfiles) { userProfile ->
                    ProfileCard(userProfile = userProfile) {
                        navController?.navigate("user_details/${userProfile.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailScreen(userId: Int) {
    val userProfile = userProfileList.first { userProfile -> userId == userProfile.id }
    Scaffold(topBar = { AppBar() }) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(userProfile.pictureURL, userProfile.onlineStatus, 240.dp)
                ProfileContent(
                    userProfile.userName,
                    userProfile.onlineStatus,
                    Alignment.CenterHorizontally
                )
            }
        }
    }
}

@Composable
fun AppBar() {
    TopAppBar(
        navigationIcon = {
            Icon(
                Icons.Default.Home,
                modifier = Modifier.padding(horizontal = 12.dp),
                contentDescription = "Home icon"
            )
        }, title = { Text(text = "Profile Card") }
    )
}

@Composable
fun ProfileCard(userProfile: UserProfile, clickCallback: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable(onClick = { clickCallback.invoke() }),
        elevation = 8.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(userProfile.pictureURL, userProfile.onlineStatus, 72.dp)
            ProfileContent(userProfile.userName, userProfile.onlineStatus, Alignment.Start)
        }
    }
}

@Composable
fun ProfilePicture(userProfileImageURL: String, userStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (userStatus) MaterialTheme.colors.lightGreen else Color.Red
        ),
        modifier = Modifier.padding(16.dp),
        elevation = 4.dp
    ) {
        Image(
            painter = rememberImagePainter(
                data = userProfileImageURL,
                builder = {
                    // To evenly crop images
                    transformations(CircleCropTransformation())
                }),
            modifier = Modifier.size(imageSize),
            contentDescription = "Profile Image"
        )
    }
}

@Composable
fun ProfileContent(userName: String, online: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        //Controlling TEXT Alpha based on user status
        CompositionLocalProvider(LocalContentAlpha provides if (online) 1f else ContentAlpha.medium) {
            Text(
                text = userName,
                style = MaterialTheme.typography.h5
            )
        }

        //Controlling STATUS Alpha based on user status
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = if (online) "Active Now" else "Offline",
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListsScreenPreview() {
    ProfileCardTheme {
        UserListsScreen(userProfiles = userProfileList, null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailScreenPreview() {
    ProfileCardTheme {
        UserDetailScreen(userId = 0)
    }
}