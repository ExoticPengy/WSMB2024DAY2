package mobile.wsmb2024.W09

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun Profile(
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navBack: () -> Unit
) {
    val authUiState by authViewModel.authUiState.collectAsState()
    val authState = authUiState.authState
    val profileUiState by profileViewModel.profileUiState.collectAsState()
    val rider = profileUiState.rider

    LaunchedEffect(authState) {
        when (authState) {
            "Authenticated" -> {
                profileViewModel.userPassword = authViewModel.userPassword
                profileViewModel.getUser(authViewModel.getUid())
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {

            if (!profileViewModel.showDialog) {
                ElevatedCard(
                    modifier = Modifier
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .width(300.dp)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Account Details",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        AsyncImage(
                            model = rider.photoUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color.Black, RoundedCornerShape(50))
                                .align(Alignment.CenterHorizontally)
                        )
                        Text("IC No: ${rider.ic}")
                        Text("Email: ${rider.email}")

                        Divider(thickness = 1.dp, color = Color.Black)
                        Text(
                            text = "Personal Details",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Text("Name: ${rider.name}")
                        Text("Gender: ${rider.gender}")
                        Text("Phone: ${rider.phone}")
                        Text("Address: ${rider.address}")
                    }
                }
            }

            ElevatedButton(
                onClick = { navBack() },
                enabled = true,
                colors = ButtonDefaults.buttonColors(containerColor = toColor("#52aef1"))
            ) {
                Text("Back")
            }

            if (profileViewModel.showDialog) {
                PasswordDialog(profileViewModel = profileViewModel, navBack = navBack)
            }
        }
    }
}

@Composable
fun PasswordDialog(
    profileViewModel: ProfileViewModel,
    navBack: () -> Unit
) {
    Dialog(onDismissRequest = {  }) {
        Surface(
            shape = RoundedCornerShape(10),
            modifier = Modifier
                .height(150.dp)
                .width(300.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Enter your password")
                OutlinedTextField(
                    value = profileViewModel.password,
                    onValueChange = {
                        profileViewModel.password = it
                            profileViewModel.checkPassword()
                                    },
                    label = { Text("Password") },
                    placeholder = { Text("") },
                    isError = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.width(280.dp)
                )
                ElevatedButton(
                    onClick = {
                        profileViewModel.showDialog = false
                        navBack()
                              },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(containerColor = toColor("#52aef1"))
                ) {
                    Text("Back")
                }
            }
        }
    }
}