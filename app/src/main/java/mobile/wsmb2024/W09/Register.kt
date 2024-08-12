package mobile.wsmb2024.W09

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun Register(
    registerViewModel: RegisterViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navBack: () -> Unit
) {
    val authUiState by authViewModel.authUiState.collectAsState()
    val authState = authUiState.authState
    val message = authUiState.message

    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            "Success" -> {
                registerViewModel.loading = false
                Toast.makeText(context, "Account successfully created!", Toast.LENGTH_SHORT).show()
                registerViewModel.uploadImage(context, authViewModel.userId)
                navBack()
            }
            "Loading" -> registerViewModel.loading = true
            "Failure" -> {
                registerViewModel.loading = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            val progress by animateFloatAsState(
                targetValue = (registerViewModel.step - 1) / 2.toFloat(),
                animationSpec = spring(stiffness =  Spring.StiffnessLow)
            )

            if  (registerViewModel.loading) {
                LoadingDialog()
            }
            Text("Register as Rider")
            Spacer(Modifier.height(40.dp))
            if (registerViewModel.step < 4) {
                Text("Step ${registerViewModel.step} of 3")
            }
            LinearProgressIndicator(
                progress = progress,
                color = toColor("#52aef1"),
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 20.dp)
            )
            Spacer(Modifier.height(40.dp))
            Rider(registerViewModel, authViewModel, navBack = {navBack()})
        }
    }
}

@Composable
fun Rider(
    registerViewModel: RegisterViewModel,
    authViewModel: AuthViewModel,
    navBack: () -> Unit
) {

    AnimatedContent(
        targetState = registerViewModel.step,
        transitionSpec = {
            if (registerViewModel.isBack) {
                slideIn(
                    tween(500)
                ) { fullSize ->
                    IntOffset(-fullSize.width * 2, 0)
                } togetherWith
                        slideOut(
                            tween(500)
                        ) { fullSize ->
                            IntOffset(fullSize.width * 2, 0)
                        }
            }
            else {
                slideIn(
                    tween(500)
                ) { fullSize ->
                    IntOffset(fullSize.width * 2, 0)
                } togetherWith
                        slideOut(
                            tween(500)
                        ) { fullSize ->
                            IntOffset(-fullSize.width * 2, 0)
                        }
            }
        }
    ) {
        when (it) {
            1 -> Step1(registerViewModel)
            2 -> Step2(registerViewModel)
            3 -> Confirm(registerViewModel = registerViewModel)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(180.dp)
    ) {
        StepButton(
            registerViewModel = registerViewModel,
            buttonText = "Back",
            onClick = {
                registerViewModel.isBack = true
                when (registerViewModel.step) {
                    1 -> {
                        navBack()
                    }
                    2 -> {
                        registerViewModel.step = 1
                    }
                    3 -> {
                        registerViewModel.step = 2
                    }
                } },
            enabled = true,
            color = toColor("#52aef1")
        )

        Spacer(Modifier.height(20.dp))

        StepButton(
            registerViewModel = registerViewModel,
            buttonText = if (registerViewModel.step == 3) "Done" else "Next",
            onClick = {
                registerViewModel.isBack = false
                when (registerViewModel.step) {
                    1 -> {
                        registerViewModel.step = 2
                    }
                    2 -> {
                        registerViewModel.updateUiState(
                            RegisterViewModel.UserDetails(
                                ic = registerViewModel.ic,
                                email = registerViewModel.email,
                                name = registerViewModel.name,
                                gender = registerViewModel.gender,
                                phone = registerViewModel.phone,
                                address = registerViewModel.address
                            )
                        )
                        registerViewModel.step = 3
                    }
                    3 -> {
                        authViewModel.signUp(registerViewModel.email, registerViewModel.password)
                    }
                }
            },
            enabled = true,
            color = toColor("#52aef1")
        )
    }
}

@Composable
fun Step1(
    registerViewModel: RegisterViewModel
) {

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        registerViewModel.selectedImageUri = it
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(150.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(50))
        ) {
            AsyncImage(
                model = registerViewModel.selectedImageUri,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(50))
            )
            IconButton(
                onClick = { photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                ) },
                modifier = Modifier
                    .offset(x = (-5).dp)
                    .background(Color.Black, RoundedCornerShape(50))
                    .align(Alignment.BottomEnd)
                    .size(35.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White)
            }
        }

        UserField(
            value = registerViewModel.ic,
            onChange = {
                registerViewModel.ic = it
                registerViewModel.validateIc()
            },
            label = "IC No.",
            placeholder = "111111223333",
            validation = registerViewModel.icHasErrors,
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
            support = "Must be 12 digits!"
        )

        UserField(
            value = registerViewModel.password,
            onChange = {
                registerViewModel.password = it
                registerViewModel.validatePassword()
            },
            label = "Password",
            placeholder = "",
            validation = registerViewModel.passwordHasErrors,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            support = "At least 6 characters!"
        )

        UserField(
            value = registerViewModel.email,
            onChange = {
                registerViewModel.email = it
                registerViewModel.validateEmail()
            },
            label = "Email",
            placeholder = "example@email.com",
            validation = registerViewModel.emailHasErrors,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            support = "Incorrect email format!"
        )
    }
}

@Composable
fun Step2(
    registerViewModel: RegisterViewModel
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        UserField(
            value = registerViewModel.name,
            onChange = {
                registerViewModel.name = it
            },
            label = "Name",
            placeholder = "My Name",
            validation = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )

        UserField(
            value = registerViewModel.gender,
            onChange = {
                registerViewModel.gender = it
                registerViewModel.validateGender()
            },
            label = "Gender",
            placeholder = "Male or Female",
            validation = registerViewModel.genderHasErrors,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            support = "Male or Female only!"
        )

        UserField(
            value = registerViewModel.phone,
            onChange = {
                registerViewModel.phone = it
                registerViewModel.validatePhone()
            },
            label = "Phone No.",
            placeholder = "60123456789",
            validation = registerViewModel.phoneHasErrors,
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            support = "11 - 12 digits only!"
        )

        UserField(
            value = registerViewModel.address,
            onChange = {
                registerViewModel.address = it
            },
            label = "Address",
            placeholder = "Walk Street, A place to go, 60000 City State",
            validation = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    }
}

@Composable
fun Confirm(
    registerViewModel: RegisterViewModel
) {
    val registerUiState by registerViewModel.registerUiState.collectAsState()
    val userDetails = registerUiState.userDetails

    ElevatedCard(
        modifier = Modifier.padding(bottom = 20.dp)
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
                model = registerViewModel.selectedImageUri,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .size(100.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.Black, RoundedCornerShape(50))
                    .align(Alignment.CenterHorizontally)
            )
            Text("IC No: ${userDetails.ic}")
            Text("Email: ${userDetails.email}")

            Divider(thickness = 1.dp, color =  Color.Black, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            Text(
                text = "Personal Details",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text("Name: ${userDetails.name}")
            Text("Gender: ${userDetails.gender}")
            Text("Phone: ${userDetails.phone}")
            Text("Address: ${userDetails.address}")
        }
    }
}

@Composable
fun UserField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    placeholder: String,
    validation: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    support: String = ""
) {

    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        supportingText =  { if (validation) Text(support) } ,
        isError = validation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = true,
        modifier = Modifier.width(280.dp)
    )
}

@Composable
fun StepButton(
    registerViewModel: RegisterViewModel,
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color
) {
    ElevatedButton(
        onClick = { onClick() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(buttonText)
    }
}