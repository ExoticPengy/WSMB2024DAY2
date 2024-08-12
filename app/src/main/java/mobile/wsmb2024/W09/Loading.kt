package mobile.wsmb2024.W09

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Dialog(onDismissRequest = { }) {
            Text("Loading...")
        }
    }
}