package mobile.wsmb2024.W09

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Login(
    loginViewModel: LoginViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navRegister: () -> Unit,
    navDriver: () -> Unit
) {
    val authUiState by authViewModel.authUiState.collectAsState()
    val authState = authUiState.authState
    val message = authUiState.message

    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            "Authenticated" -> {
                loginViewModel.loading = false
                Toast.makeText(context, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                navDriver()
            }
            "Loading" -> {
                loginViewModel.loading = true
            }
            "Wrong" -> {
                loginViewModel.loading = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            "Empty" -> {
                loginViewModel.loading = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            if (loginViewModel.loading) {
                LoadingDialog()
            }

            Text(
                "Kongsi Kereta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = null)

            OutlinedTextField(
                value = loginViewModel.ic,
                onValueChange = { loginViewModel.ic = it },
                label = { Text("Ic No.") },
                placeholder = { Text("111111223333") },
                isError = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.width(280.dp)
            )

            var showPassword by remember { mutableStateOf(false) }
            val passwordVisualTransformation = remember { PasswordVisualTransformation() }

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.password = it },
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
            Spacer(Modifier.height(10.dp))
            ElevatedButton(
                onClick = { loginViewModel.getRider(authViewModel) },
                enabled = true,
                colors = ButtonDefaults.buttonColors(containerColor = toColor("#52aef1"))
            ) {
                Text("Login")
            }

            Button(
                onClick = { navRegister() },
                enabled = true,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Don't have an account? Register here!", color = Color.Blue)
            }
        }
    }
}


