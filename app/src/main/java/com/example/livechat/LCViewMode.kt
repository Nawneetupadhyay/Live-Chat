package com.example.livechat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.livechat.data.Events
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth
) : ViewModel() {

    init {

    }

    var inProgression = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    val signedIn = mutableStateOf(false)

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgression.value= true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {

            if (it.isSuccessful){
                signedIn.value = true
                createOrUpdatProfile(name, number)
            }
            else
            {
                handelException(it.exception!!,"sign up failed")
            }
        }
    }

    private fun createOrUpdatProfile(name: String? = null, number: String? = null, image: String? = null) {

    }

    fun handelException(exception: Exception,customMsg : String = ""){
        Log.e("live chat", "live chat exception", exception)
        exception.printStackTrace()
        val errorMsg = exception.localizedMessage
        val msg = if(customMsg.isNullOrEmpty())errorMsg else customMsg
        eventMutableState.value =Events(msg)
        inProgression.value  =false
    }


}