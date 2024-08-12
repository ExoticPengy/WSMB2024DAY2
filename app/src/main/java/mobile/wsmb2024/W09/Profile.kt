package mobile.wsmb2024.W09

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
                    modifier = Modifier.padding(bottom = 10.dp)
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
                                .padding(top = 10.dp, bottom = 10.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color.Black, RoundedCornerShape(50))
                                .align(Alignment.CenterHorizontally)
                        )
                        Text("IC No: ${rider.ic}")
                        Text("Email: ${rider.email}")

                        Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
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
    var showPassword by remember { mutableStateOf(false) }
    val passwordVisualTransformation = remember { PasswordVisualTransformation() }

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
                    visualTransformation =
                    if (showPassword) {
                        VisualTransformation.None
                    } else
                    {
                        passwordVisualTransformation
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    trailingIcon = { Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showPassword = !showPassword
                        }
                    ) },
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