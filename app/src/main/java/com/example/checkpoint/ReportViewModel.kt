package com.example.checkpoint

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.dto.Delay
import com.example.checkpoint.dto.User
import com.example.checkpoint.services.IReportService
import com.example.checkpoint.services.ReportService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class ReportViewModel (var reportService: IReportService = ReportService()) : ViewModel() {
    var user: User? = null
    val delays: MutableLiveData<List<Delay>> = MutableLiveData<List<Delay>>()
    var selectedDelay by mutableStateOf(Delay())
    val NEW_DELAY = "New Delay"

    private val storageReference = FirebaseStorage.getInstance().getReference()
    private lateinit var firestore: FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    fun saveDelay() {
        user?.let { user ->
            val document = if (selectedDelay.delayID == null || selectedDelay.delayID.isEmpty()) {
                // insert
                firestore.collection("users").document(user.uid).collection("delays").document()
            } else {
                // update
                firestore.collection("users").document(user.uid).collection("delays")
                    .document(selectedDelay.delayID)
            }

            selectedDelay.delayID = document.id
            val handle = document.set(selectedDelay)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document Saved")
            }
            handle.addOnFailureListener { Log.e("Firebase", "Save failed $it  ") }
        }
    }

            fun listenToDelay() {
                user?.let { user ->
                    firestore.collection("users").document(user.uid).collection("delays")
                        .addSnapshotListener { snapshot, error ->
                            // see of we received an error
                            if (error != null) {
                                Log.w("listen failed.", error)
                                return@addSnapshotListener
                            }
                            // if we reached this point, there was not an error, and we have data.
                            snapshot?.let {
                                val allDelays = ArrayList<Delay>()
                                allDelays.add(Delay(NEW_DELAY))
                                val documents = snapshot.documents
                                documents.forEach {
                                    val delay = it.toObject(Delay::class.java)
                                    delay?.let {
                                        allDelays.add(delay)
                                    }
                                }
                                // we have a populated collection of specimens.
                                delays.value = allDelays
                            }
                        }
                }
            }

            fun saveUser() {
                user?.let { user ->
                    val handle = firestore.collection("users").document(user.uid).set(user)
                    handle.addOnSuccessListener { Log.d("Firebase", "User Saved") }
                    handle.addOnFailureListener { Log.e("Firebase", "User save failed $it") }

                }
            }
        }