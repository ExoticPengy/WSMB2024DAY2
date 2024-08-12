package mobile.wsmb2024.W09

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel: ViewModel() {

    data class AuthUiState(
        val authState: String = "",
        val message: String = ""
    )

    private val _authUiState = MutableStateFlow(AuthUiState())
    var authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun updateUiState(authState: String, message: String = "") {
        _authUiState.value = AuthUiState(
            authState = authState,
            message = message
        )
    }

    var userId by mutableStateOf("")
    var userPassword by mutableStateOf("")

    val auth = Firebase.auth

    init {
        signOut()
    }

    fun getUid(): String {
        return auth.currentUser?.uid?:""
    }

    fun signUp(email: String, password: String) {
        updateUiState("Loading")
        if (email.isBlank() || password.isBlank()) {
            updateUiState("Empty", "Email or Password is Empty!")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    userId = auth.currentUser!!.uid
                    updateUiState("Success")
                    auth.signOut()
                } else {
                    updateUiState("Failure", result.exception?.message ?: "Failed to Sign Up")
                }
            }
    }

    fun signIn(email: String, password: String) {
        updateUiState("Loading")
        if (email.isBlank() || password.isBlank()) {
            updateUiState("Empty", "Email or Password is Wrong!")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { result ->
                if (result.isSuccessful) {
                    userPassword = password
                    userId = auth.currentUser!!.uid
                    updateUiState("Authenticated")
                }
                else {
                    updateUiState("Wrong", result.exception?.message?:"Email or Password is Wrong!")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        updateUiState("Unauthenticated")
    }
}