package mobile.wsmb2024.W09

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class LoginViewModel: ViewModel() {

    val db = Firebase.firestore
    val ridersRef = db.collection("riders")
    var rider by mutableStateOf(RegisterViewModel.UserDetails())

    var ic by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)

    fun getRider(authViewModel: AuthViewModel) {
        loading = true

        ridersRef.whereEqualTo("ic", ic).get()
            .addOnSuccessListener {
                for (doc in it) {
                    rider = doc.toObject()
                }
                authViewModel.signIn(rider.email, password)
            }
    }
}