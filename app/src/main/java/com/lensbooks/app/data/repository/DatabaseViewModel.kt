package com.lensbooks.app.data.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lensbooks.app.data.models.Booking
import com.lensbooks.app.data.models.Client
import com.lensbooks.app.data.models.PortfolioPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DatabaseViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private var clientsListener: ListenerRegistration? = null
    private var bookingsListener: ListenerRegistration? = null

    private val _photos = MutableStateFlow<List<PortfolioPhoto>>(getInitialPhotos())
    val photos: StateFlow<List<PortfolioPhoto>> = _photos.asStateFlow()

    private val _likedPhotos = MutableStateFlow<Set<String>>(emptySet())
    val likedPhotos: StateFlow<Set<String>> = _likedPhotos.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<String>>>(
        mapOf(
            "1" to listOf("Stunning tones!", "Love the mood of rain-slicked streets."),
            "2" to listOf("Majestic view!", "Incredible sharpness on the peaks."),
            "3" to listOf("Flawless light control.", "Beautiful golden hues.")
        )
    )
    val comments: StateFlow<Map<String, List<String>>> = _comments.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
            }
        }
    }

    private fun startListening(userId: String) {
        stopListening()
        try {
            clientsListener = firestore.collection("clients")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("DatabaseViewModel", "Clients snapshot listener failed", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val clientList = snapshot.documents.map { doc ->
                            Client(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                email = doc.getString("email") ?: "",
                                phone = doc.getString("phone") ?: "",
                                company = doc.getString("company") ?: "",
                                userId = doc.getString("userId") ?: ""
                            )
                        }
                        _clients.value = clientList
                    }
                }

            bookingsListener = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("DatabaseViewModel", "Bookings snapshot listener failed", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val bookingList = snapshot.documents.map { doc ->
                            Booking(
                                id = doc.id,
                                clientName = doc.getString("clientName") ?: "",
                                date = doc.getString("date") ?: "",
                                time = doc.getString("time") ?: "",
                                packageType = doc.getString("packageType") ?: "",
                                status = doc.getString("status") ?: "Confirmed",
                                userId = doc.getString("userId") ?: ""
                            )
                        }
                        _bookings.value = bookingList
                    }
                }
        } catch (e: Exception) {
            Log.e("DatabaseViewModel", "Failed to register Firestore listeners", e)
        }
    }

    private fun stopListening() {
        clientsListener?.remove()
        clientsListener = null
        bookingsListener?.remove()
        bookingsListener = null
        _clients.value = emptyList()
        _bookings.value = emptyList()
    }

    fun addClient(name: String, email: String, phone: String, company: String) {
        val currentUser = auth.currentUser ?: return
        val clientData = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone.ifEmpty { "N/A" },
            "company" to company.ifEmpty { "Independent" },
            "userId" to currentUser.uid
        )
        viewModelScope.launch {
            try {
                firestore.collection("clients").add(clientData)
            } catch (e: Exception) {
                Log.e("DatabaseViewModel", "Failed to add client", e)
            }
        }
    }

    fun deleteClient(clientId: String) {
        if (clientId.isEmpty()) return
        viewModelScope.launch {
            try {
                firestore.collection("clients").document(clientId).delete()
            } catch (e: Exception) {
                Log.e("DatabaseViewModel", "Failed to delete client", e)
            }
        }
    }

    fun addBooking(clientName: String, date: String, time: String, packageType: String) {
        val currentUser = auth.currentUser ?: return
        val bookingData = hashMapOf(
            "clientName" to clientName,
            "date" to date,
            "time" to time,
            "packageType" to packageType,
            "status" to "Confirmed",
            "userId" to currentUser.uid
        )
        viewModelScope.launch {
            try {
                firestore.collection("bookings").add(bookingData)
            } catch (e: Exception) {
                Log.e("DatabaseViewModel", "Failed to add booking", e)
            }
        }
    }

    fun deleteBooking(bookingId: String) {
        if (bookingId.isEmpty()) return
        viewModelScope.launch {
            try {
                firestore.collection("bookings").document(bookingId).delete()
            } catch (e: Exception) {
                Log.e("DatabaseViewModel", "Failed to delete booking", e)
            }
        }
    }

    fun toggleLikePhoto(photoId: String) {
        val currentLikes = _likedPhotos.value
        if (currentLikes.contains(photoId)) {
            _likedPhotos.value = currentLikes - photoId
        } else {
            _likedPhotos.value = currentLikes + photoId
        }
    }

    fun addCommentToPhoto(photoId: String, commentText: String) {
        if (commentText.isBlank()) return
        val currentCommentsMap = _comments.value.toMutableMap()
        val currentPhotoComments = currentCommentsMap[photoId]?.toMutableList() ?: mutableListOf()
        currentPhotoComments.add(commentText)
        currentCommentsMap[photoId] = currentPhotoComments
        _comments.value = currentCommentsMap
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    private fun getInitialPhotos(): List<PortfolioPhoto> {
        return listOf(
            PortfolioPhoto(
                id = "1",
                title = "Rain-slicked Neon",
                artistName = "Hiroshi Sato",
                artistHandle = "@hiroshi_s",
                url = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&q=80&w=800",
                category = "Street",
                camera = "Fujifilm X-T5",
                lens = "Fujinon 35mm f/1.4 R",
                aperture = "f/1.4",
                shutterSpeed = "1/160s",
                iso = "640",
                location = "Shinjuku, Tokyo",
                likes = 1420
            ),
            PortfolioPhoto(
                id = "2",
                title = "Ethereal Crests",
                artistName = "Elena Rostova",
                artistHandle = "@elena_rost",
                url = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=800",
                category = "Landscape",
                camera = "Sony Alpha 7R V",
                lens = "FE 24-70mm f/2.8 GM II",
                aperture = "f/8.0",
                shutterSpeed = "1/80s",
                iso = "100",
                location = "Dolomites, Italy",
                likes = 985
            ),
            PortfolioPhoto(
                id = "3",
                title = "Golden Chiaroscuro",
                artistName = "Marcus Vance",
                artistHandle = "@mv_art",
                url = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=800",
                category = "Portrait",
                camera = "Leica M11-P",
                lens = "Summilux-M 50mm f/1.4 ASPH",
                aperture = "f/1.4",
                shutterSpeed = "1/250s",
                iso = "200",
                location = "Paris Studio, France",
                likes = 2304
            )
        )
    }
}