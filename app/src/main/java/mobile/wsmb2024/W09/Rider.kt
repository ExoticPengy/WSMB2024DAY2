package mobile.wsmb2024.W09

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.Locale

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

            OutlinedTextField(
                value = riderViewModel.search,
                onValueChange = { riderViewModel.search = it },
                label = { Text("Search") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null ) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.width(280.dp).padding(top = 5.dp, bottom = 10.dp)
            )

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                items(rides) { ride ->
                    if (ride.origin.lowercase().contains(riderViewModel.search.lowercase()) ||
                        ride.destination.lowercase().contains(riderViewModel.search.lowercase()) ||
                        riderViewModel.search.isBlank()) {
                        val driver = riderViewModel.getDriver(ride)
                        var showDetails by remember { mutableStateOf(true) }

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
                                IconButton(onClick = { showDetails = !showDetails }) {
                                    Icon(imageVector = Icons.Default.Info, null)
                                }
                            }
                            Divider(thickness = 1.dp, modifier = Modifier.padding(5.dp))
                            if (showDetails) {
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
                                        if (!riderViewModel.checkJoined(
                                                ride,
                                                authViewModel.getUid()
                                            ) && ride.passengers < driver.capacity
                                        ) {
                                            riderViewModel.joinRide(ride, authViewModel.getUid())
                                        } else if (riderViewModel.checkJoined(
                                                ride,
                                                authViewModel.getUid()
                                            )
                                        ) {
                                            riderViewModel.cancelRide(ride, authViewModel.getUid())
                                        } else {
                                            Unit
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                        if (riderViewModel.checkJoined(
                                                ride,
                                                authViewModel.getUid()
                                            ) || ride.passengers >= driver.capacity
                                        ) {
                                            Color.Red
                                        } else {
                                            toColor("#52aef1")
                                        }
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text =
                                        if (riderViewModel.checkJoined(
                                                ride,
                                                authViewModel.getUid()
                                            )
                                        ) {
                                            "Cancel"
                                        } else if (ride.passengers >= driver.capacity) {
                                            "Full ${ride.passengers}/${driver.capacity}"
                                        } else {
                                            "Join ${ride.passengers}/${driver.capacity}"
                                        }
                                    )
                                }
                            } else {
                                Text(
                                    text = "Driver Phone No: ${driver.phone}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                Text(
                                    text = "Driver Gender: ${driver.gender}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                Text(
                                    text = "Car Model: ${driver.model}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                Text(
                                    text = "Car Capacity: ${driver.capacity}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                Text(
                                    text = "Special Features: ${driver.features}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                            Spacer(Modifier.height(5.dp))
                        }
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
                        .fillMaxWidth()
                        .height(520.dp)
                ) {
                    items(trips) { trip ->
                        if (trip.status == "Active") {
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
                                            } else {
                                                riderViewModel.setActive(trip)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = toColor(
                                                "#52aef1"
                                            )
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text =
                                            if (trip.status == "Active") {
                                                "Set Inactive"
                                            } else {
                                                "Set Active"
                                            }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                    }
                    items(trips) { trip ->
                        if (trip.status == "Inactive") {
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
                                            } else {
                                                riderViewModel.setActive(trip)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = toColor(
                                                "#52aef1"
                                            )
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text =
                                            if (trip.status == "Active") {
                                                "Set Inactive"
                                            } else {
                                                "Set Active"
                                            }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                    }
                    items(trips) { trip ->
                        if (trip.status == "Cancelled") {
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
                                            } else {
                                                riderViewModel.setActive(trip)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = toColor(
                                                "#52aef1"
                                            )
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text =
                                            if (trip.status == "Active") {
                                                "Set Inactive"
                                            } else {
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

                Text(
                    text = "Total fare: ${toRm(riderViewModel.calculateTotal(trips))}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 15.dp)
                )
            }
        }
    }
}