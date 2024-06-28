package com.example.livechat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.livechat.data.Events
import com.example.livechat.data.USER_NODE
import com.example.livechat.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore
) : ViewModel() {

    init {

    }

    var inProgression = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    var signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgression.value = true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {

            if (it.isSuccessful) {
                signedIn.value = true
                createOrUpdatProfile(name, number)
            } else {
                handelException(it.exception!!, "sign up failed")
            }
        }
    }

    private fun createOrUpdatProfile(
        name: String? = null,
        number: String? = null,
        image: String? = null
    ) {
        val uid = auth.uid
        val userData = UserData(
            uid,
            name ?: userData.value?.name,
            number ?: userData.value?.number,
            image ?: userData.value?.imageUrl
        )

        uid?.let {
            inProgression.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {


                } else {
                    db.collection(USER_NODE).document(uid).set(userData)

                    inProgression.value = false
                    getUserData(uid)

                }


            }.addOnFailureListener {
                handelException(exception = it, "Cannot retrieve user data")

            }
        }
    }

    private fun getUserData(uid: String) {
        inProgression.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handelException(error, "Cannot retrieve user data")
                return@addSnapshotListener
            }
            if (value != null) {
                val user = value.toObject(UserData::class.java)
                userData.value = user
                inProgression.value = false
            }
        }
    }

    fun handelException(exception: Exception, customMsg: String = "") {
        Log.e("live chat", "live chat exception", exception)
        exception.printStackTrace()
        val errorMsg = exception.localizedMessage
        val msg = if (customMsg.isNullOrEmpty()) errorMsg else customMsg
        eventMutableState.value = Events(msg)
        inProgression.value = false
    }


}