package mobile.wsmb2024.w09

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun Rider(
    riderViewModel: RiderViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navBack: () -> Unit,
    navProfile: () -> Unit
) {
    val authUiState by authViewModel.authUiState.collectAsState()
    val authState = authUiState.authState
    val riderUiState by riderViewModel.riderUiState.collectAsState()
    val rides = riderUiState.rides

    LaunchedEffect(authState) {
        when (authState) {
            "Authenticated" -> {
                riderViewModel.getDetails(authViewModel.getUid())
            }
            "Unauthenticated" -> {
                navBack()
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (riderViewModel.loading) {
                LoadingDialog()
            }
            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                IconButton(
                    onClick = {
                        authViewModel.signOut()
                    },
                    modifier = Modifier.background(Color.Red, RoundedCornerShape(50))
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, null, tint = Color.White)
                }
                Text("Riders Ride Management")
                IconButton(
                    onClick = {
                        navProfile()
                    },
                    modifier = Modifier.background(Color.Black, RoundedCornerShape(50))
                ) {
                    Icon(imageVector = Icons.Default.Person, null, tint = Color.White)
                }
            }

            Divider(thickness = 2.dp)

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                items(rides) { ride ->
                    val driver = riderViewModel.getDriver(ride)

                    Spacer(Modifier.height(30.dp))
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                    ) {
                        Spacer(Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = driver.photoUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(50))
                                    .border(1.dp, Color.Black, RoundedCornerShape(50))
                            )
                            Text(driver.name)
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Default.Info, null)
                            }
                        }
                        Divider(thickness = 1.dp, modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Date: ${ride.date}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Text(
                            text = "Time: ${ride.time}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Text(
                            text = "Origin: ${ride.origin}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Text(
                            text = "Destination: ${ride.destination}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Text(
                            text = "Fare: ${toRm(ride.fare)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Button(
                            onClick = {
                                if (riderViewModel.checkJoined(ride, authViewModel.getUid())) {
                                    riderViewModel.cancelRide(ride, authViewModel.getUid())
                                }
                                else {
                                    riderViewModel.joinRide(ride, authViewModel.getUid())
                                }
                                      },
                            colors = ButtonDefaults.buttonColors(containerColor =
                                if (riderViewModel.checkJoined(ride, authViewModel.getUid())) {
                                    Color.Red
                                }
                                else {
                                    toColor("#52aef1")
                                }
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text =
                                if (riderViewModel.checkJoined(ride, authViewModel.getUid())) {
                                    "Cancel"
                                }
                                else {
                                    "Join ${ride.passengers}/${driver.capacity}"
                                }
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = { riderViewModel.showRecords = true },
                modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)
            ) {
                Text("View Ride Records")
                Spacer(Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }

            if (riderViewModel.showRecords) {
                RecordsDialog(
                    riderViewModel = riderViewModel,
                    onDismiss = { riderViewModel.showRecords = false },
                    uid = authViewModel.getUid()
                )
            }
        }
    }
}

@Composable
fun RecordsDialog(
    riderViewModel: RiderViewModel,
    onDismiss: () -> Unit,
    uid: String,
) {
    val riderUiState by riderViewModel.riderUiState.collectAsState()
    val trips = riderUiState.trips

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = RoundedCornerShape(10),
            modifier = Modifier
                .width(300.dp)
                .height(600.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(50.dp))
                    Text("Ride Records")
                    IconButton(onClick = { onDismiss() }) {
                        Icon(imageVector = Icons.Default.Close, null)
                    }
                }

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(trips) { trip ->
                        val ride = riderViewModel.getRide(trip, uid)

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier
                                .width(280.dp)
                        ) {
                            Spacer(Modifier.height(5.dp))
                            Text(
                                text = "Status: ${trip.status}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = "Date: ${ride.date}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = "Time: ${ride.time}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = "Origin: ${ride.origin}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = "Destination: ${ride.destination}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = "Fare: ${toRm(ride.fare)}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            if (trip.status != "Cancelled") {
                                Button(
                                    onClick = {
                                        if (trip.status == "Active") {
                                            riderViewModel.setInactive(trip)
                                        }
                                        else {
                                            riderViewModel.setActive(trip)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = toColor("#52aef1")),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text =
                                        if (trip.status == "Active") {
                                            "Set Inactive"
                                        }
                                        else {
                                            "Set Active"
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
    }
}