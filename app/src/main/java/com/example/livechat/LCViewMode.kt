package com.example.livechat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.rememberTextMeasurer
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
    var inProgression = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    var signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.let {
            getUserData(it.uid)
        }

    }

fun logIn(email: String, password: String)
{
    if(email.isEmpty() || password.isEmpty())
    {
        handelException(Exception("Please fill all the fields"), "log in failed")
        return
    }
    else{
        inProgression.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() {
            if (it.isSuccessful) {
                signedIn.value = true
                inProgression.value = false
                auth.currentUser?.uid?.let {
                    getUserData(it)
                }
            } else {
                handelException(it.exception!!, "log in failed")
    }
}}}

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgression.value = true
        if(name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            handelException(Exception("Please fill all the fields"), "sign up failed")
            return
        }
        inProgression.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
             if (it.isEmpty)
             {
                 auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {

                     if (it.isSuccessful) {
                         signedIn.value = true
                         createOrUpdatProfile(name, number)
                     } else {
                         handelException(it.exception!!, "sign up failed")
                     }
                 }
             }
            else
             {
                 handelException(Exception("Number already exists"), "sign up failed")
                 inProgression.value = false
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