package mobile.wsmb2024.W09

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel: ViewModel() {
    data class RegisterUiState(
        val userDetails: UserDetails = UserDetails()
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

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    var registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun updateUiState(userDetails: UserDetails) {
        _registerUiState.value = RegisterUiState(
            userDetails
        )
    }

    val db = Firebase.firestore
    val driversRef = db.collection("riders")
    val storageRef = Firebase.storage.reference
    val folderRef = storageRef.child("userProfiles")

    var step by mutableIntStateOf(1)
    var loading by mutableStateOf(false)
    var isBack by mutableStateOf(false)
    var notSaved by mutableStateOf(true)
    var selectedImageUri by mutableStateOf<Uri?>(null)


    var ic by mutableStateOf("")
    var password by mutableStateOf("")
    var email by mutableStateOf("")
    var name by mutableStateOf("")
    var gender by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var profileUrl by mutableStateOf("")

    fun uploadImage(context: Context, uid: String) {
        val imageRef = folderRef.child("$ic.png")

        val contentResolver = context.contentResolver


        val byteStream = contentResolver.openInputStream(selectedImageUri!!)

        val imageBytes = byteStream?.readBytes()

        imageRef.putBytes(imageBytes!!)
            .addOnSuccessListener { success ->
                success.storage.downloadUrl.addOnSuccessListener { uri ->
                    profileUrl = uri.toString()
                    uploadDriverDetails(uid)
                }
            }
    }

    fun uploadDriverDetails(uid: String) {
        if (notSaved) {
            notSaved = false
            val userDetails = _registerUiState.value.userDetails
            val newUserDetails = UserDetails(
                ic = userDetails.ic,
                email = userDetails.email,
                name = userDetails.name,
                gender = userDetails.gender,
                phone = userDetails.phone,
                address = userDetails.address,
                photoUrl = profileUrl,
                uid = uid,
            )

            driversRef.add(newUserDetails)
        }
    }

    var icHasErrors by mutableStateOf(false)

    fun validateIc() {
        if (ic.length == 12 || ic.length == 0) {
            icHasErrors = false
        } else icHasErrors = true
    }

    var passwordHasErrors by mutableStateOf(false)

    fun validatePassword() {
        if (password.length >= 6 || password.length == 0) {
            passwordHasErrors = false
        } else passwordHasErrors = true
    }

    var emailHasErrors by mutableStateOf(false)

    fun validateEmail() {
        if (email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailHasErrors = false
        } else emailHasErrors = true
    }

    var genderHasErrors by mutableStateOf(false)

    fun validateGender() {
        if (gender == "" || gender == "Male" || gender == "Female") {
            genderHasErrors = false
        } else genderHasErrors = true
    }

    var phoneHasErrors by mutableStateOf(false)

    fun validatePhone() {
        if (phone.length == 11 || phone.length == 12 || phone.length == 0) {
            phoneHasErrors = false
        } else phoneHasErrors = true
    }
}