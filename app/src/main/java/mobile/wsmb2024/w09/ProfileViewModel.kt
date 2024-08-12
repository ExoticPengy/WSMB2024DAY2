package mobile.wsmb2024.w09

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel: ViewModel() {

    data class ProfileUiState(
        val rider: UserDetails = UserDetails()
    )

    data class UserDetails(
        var uid: String = "",
        var name: String = "",
        var email: String = "",
        var ic: String = "",
        var gender: String = "",
        var phone: String = "",
        var address: String = "",
        var photoUrl: String = ""
    )

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    var profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    var loading by mutableStateOf(false)
    var password by mutableStateOf("")
    var userPassword by mutableStateOf("")

    fun updateUiState(user: UserDetails) {
        _profileUiState.value = ProfileUiState(
            user
        )
    }

    val db = Firebase.firestore
    val ridersRef = db.collection("riders")

    fun getUser(uid: String) {
        loading = true

        ridersRef.whereEqualTo("uid", uid)
            .get().addOnSuccessListener {
                for (doc in it) {
                    updateUiState(user = doc.toObject())
                }
                loading = false
        }
    }
}