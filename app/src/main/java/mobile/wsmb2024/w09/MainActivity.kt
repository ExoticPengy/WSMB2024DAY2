package mobile.wsmb2024.w09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import mobile.wsmb2024.w09.ui.theme.W09_ModuleTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel: AuthViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            W09_ModuleTheme {
                KongsiKeretaNav(authViewModel = authViewModel)
            }
        }
    }
}

fun toColor(color: String): Color {
    return Color(color.toColorInt())
}

fun toRm(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "MY")).format(amount)
}

fun getDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(System.currentTimeMillis())
}