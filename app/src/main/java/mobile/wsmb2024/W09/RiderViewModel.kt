package mobile.wsmb2024.W09

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

class RiderViewModel: ViewModel() {
    data class RiderUiState(
        val rides: List<RideDetails> = listOf(),
        val drivers: List<DriverDetails> = listOf(),
        val trips: List<TripDetails> = listOf()
    )

    data class TripDetails(
        var status: String = "",
        var rideId: Int = 0,
        var rideUid: String = "",
        var riderUid: String = ""
    )

    data class RideDetails(
        var date: String = "",
        var time: String = "",
        var origin: String = "",
        var destination: String = "",
        var fare: Double = 0.0,
        var uid: String = "",
        var rideId: Int = 0,
        var passengers: Int = 0
    )

    data class DriverDetails(
        var uid: String = "",
        var name: String = "",
        var email: String = "",
        var ic: String = "",
        var gender: String = "",
        var phone: String = "",
        var address: String = "",
        var photoUrl: String = "",
        var model: String = "",
        var capacity: Int = 0,
        var features: String = ""
    )

    private val _riderUiState = MutableStateFlow(RiderUiState())
    var riderUiState: StateFlow<RiderUiState> = _riderUiState.asStateFlow()

    fun updateUiState(rides: List<RideDetails>, drivers: List<DriverDetails>, trips: List<TripDetails>) {
        _riderUiState.value = RiderUiState(
            rides, drivers, trips
        )
    }

    val db = Firebase.firestore
    val driversRef = db.collection("drivers")
    val ridesRef = db.collection("rides")
    val tripsRef = db.collection("trips")

    var loading by mutableStateOf(false)
    var showRecords by mutableStateOf(false)

    fun getDetails(uid: String) {
        loading = true
        var ridesList = mutableListOf<RideDetails>()
        var driversList = mutableListOf<DriverDetails>()
        var tripsList = mutableListOf<TripDetails>()

        ridesRef.get().addOnSuccessListener {
            for (doc in it) {
                ridesList.add(doc.toObject())
            }
            updateUiState(ridesList, _riderUiState.value.drivers, _riderUiState.value.trips)
            loading = false
        }

        driversRef.get().addOnSuccessListener {
            for (doc in it) {
                driversList.add(doc.toObject())
            }
            updateUiState(_riderUiState.value.rides, driversList, _riderUiState.value.trips)
            loading = false
        }

        tripsRef.whereEqualTo("riderUid", uid)
            .get().addOnSuccessListener {
            for (doc in it) {
                tripsList.add(doc.toObject())
            }
            updateUiState(_riderUiState.value.rides, _riderUiState.value.drivers, tripsList)
            loading = false
        }
    }

    fun getDriver(ride: RideDetails): DriverDetails {
        val drivers = _riderUiState.value.drivers

        for (driver in drivers) {
            if (driver.uid == ride.uid)
            return driver
        }

        return DriverDetails()
    }

    fun checkJoined(ride: RideDetails, uid: String): Boolean {
        val trips = _riderUiState.value.trips

        for (trip in trips) {
            if(
                ride.uid == trip.rideUid &&
                ride.rideId == trip.rideId &&
                uid == trip.riderUid &&
                trip.status != "Cancelled"
                )
                return true
        }

        return false
    }

    fun checkExists(ride: RideDetails, uid: String): Boolean {
        val trips = _riderUiState.value.trips

        for (trip in trips) {
            if(
                ride.uid == trip.rideUid &&
                ride.rideId == trip.rideId &&
                uid == trip.riderUid
                )
                return true
        }

        return false
    }

    fun joinRide(ride: RideDetails, uid: String) {
        loading = true
        if (checkExists(ride, uid)) {
            tripsRef.whereEqualTo("riderUid", uid).whereEqualTo("rideUid", ride.uid).whereEqualTo("rideId", ride.rideId)
                .get().addOnSuccessListener {
                    for (tripDoc in it) {
                        tripsRef.document(tripDoc.id).set(tripDoc.toObject<TripDetails>().copy(status = "Active"))
                            .addOnSuccessListener {
                                ridesRef.whereEqualTo("uid", ride.uid).whereEqualTo("rideId", ride.rideId)
                                    .get().addOnSuccessListener { result ->
                                        for (rideDoc in result) {
                                            ridesRef.document(rideDoc.id).set(ride.copy(passengers = (ride.passengers + 1))).addOnSuccessListener {
                                                loading = false
                                                getDetails(uid)
                                            }
                                        }
                                    }
                            }
                    }
                }
        }
        else {
            tripsRef.add(
                TripDetails(
                    status = "Active",
                    rideId = ride.rideId,
                    rideUid = ride.uid,
                    riderUid = uid,
                )
            ).addOnSuccessListener {
                ridesRef.whereEqualTo("uid", ride.uid).whereEqualTo("rideId", ride.rideId)
                    .get().addOnSuccessListener {
                        for (doc in it) {
                            ridesRef.document(doc.id).set(ride.copy(passengers = (ride.passengers + 1))).addOnSuccessListener {
                                loading = false
                                getDetails(uid)
                            }
                        }
                    }
            }
        }
    }

    fun cancelRide(ride: RideDetails, uid: String) {
        loading = true
        tripsRef.whereEqualTo("riderUid", uid).whereEqualTo("rideUid", ride.uid).whereEqualTo("rideId", ride.rideId)
            .get().addOnSuccessListener {
                for (doc in it) {
                    tripsRef.document(doc.id).set(doc.toObject<TripDetails>().copy(status = "Cancelled"))
                        .addOnSuccessListener {
                            loading = false
                            getDetails(uid)
                        }
                }
            }
        ridesRef.whereEqualTo("uid", ride.uid).whereEqualTo("rideId", ride.rideId)
            .get().addOnSuccessListener {
                for (doc in it) {
                    ridesRef.document(doc.id).set(ride.copy(passengers = (ride.passengers - 1))).addOnSuccessListener {
                        loading = false
                        getDetails(uid)
                    }
                }
            }
    }

    fun setActive(trip: TripDetails) {
        loading = true
        tripsRef.whereEqualTo("riderUid", trip.riderUid).whereEqualTo("rideUid", trip.rideUid).whereEqualTo("rideId", trip.rideId)
            .get().addOnSuccessListener {
                for (doc in it) {
                    tripsRef.document(doc.id).set(doc.toObject<TripDetails>().copy(status = "Active"))
                        .addOnSuccessListener {
                            loading = false
                            getDetails(trip.riderUid)
                        }
                }
            }
    }

    fun setInactive(trip: TripDetails) {
        loading = true
        tripsRef.whereEqualTo("riderUid", trip.riderUid).whereEqualTo("rideUid", trip.rideUid).whereEqualTo("rideId", trip.rideId)
            .get().addOnSuccessListener {
                for (doc in it) {
                    tripsRef.document(doc.id).set(doc.toObject<TripDetails>().copy(status = "Inactive"))
                        .addOnSuccessListener {
                            loading = false
                            getDetails(trip.riderUid)
                        }
                }
            }
    }

    fun getRide(trip: TripDetails, uid: String): RideDetails {
        val rides = _riderUiState.value.rides
        for (ride in rides) {
            if(
                ride.uid == trip.rideUid &&
                ride.rideId == trip.rideId &&
                uid == trip.riderUid
            )
            return ride
        }

        return RideDetails()
    }

    fun calculateTotal(trips: List<TripDetails>): Double {
        var total: Double

        for (trip in trips) {
            for (ride in _riderUiState.value.rides)
            if (
                ride.uid == trip.rideUid &&
                ride.rideId == trip.rideId
                ) {

            }
        }

        return 0.0
    }
}