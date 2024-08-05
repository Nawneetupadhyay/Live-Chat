package com.example.livechat

import android.net.Uri
import android.util.Log
import androidx.collection.emptyLongSet
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.livechat.data.CHATS
import com.example.livechat.data.ChatData
import com.example.livechat.data.ChatUser
import com.example.livechat.data.Events
import com.example.livechat.data.MESSAGE
import com.example.livechat.data.Message
import com.example.livechat.data.Status
import com.example.livechat.data.USER_NODE
import com.example.livechat.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {
    var inProgression = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    var signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())

    val inProgressChatMessages = mutableStateOf(false)

    var currentChatMessageListener : ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.let {
            getUserData(it.uid)
        }

    }

    fun logIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handelException(Exception("Please fill all the fields"), "log in failed")
            return
        } else {
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
            }
        }
    }

    fun onSendReply(message: String, chatId: String)
    {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(message)
    }


    fun populateMessages(chatId: String)
    {
        inProgressChatMessages.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE).addSnapshotListener{value, error ->

            if(error!= null){
                handelException(error)


            }

            if(value != null)
            {
                chatMessages.value = value.documents.mapNotNull {
                    it.toObject<Message>()
                }
            }
        }

    }

    fun depopulateMessages(){
        currentChatMessageListener?.remove()
        chatMessages.value = listOf()
    }
    fun populateChats()
    {
        inProcessChat.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

        )).addSnapshotListener{
            value, error ->
            if(error!= null)
            {
               handelException(error)
            }

            if(value!= null)
            {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChat.value = false
            }

        }
    }


    fun signUp(name: String, number: String, email: String, password: String) {
        inProgression.value = true
        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handelException(Exception("Please fill all the fields"), "sign up failed")
            return
        }
        inProgression.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {

                    if (it.isSuccessful) {
                        signedIn.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handelException(it.exception!!, "sign up failed")
                    }
                }
            } else {
                handelException(Exception("Number already exists"), "sign up failed")
                inProgression.value = false
            }

        }

    }

    fun createOrUpdateProfile(
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
                populateChats()
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

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(image = it.toString())
        }

    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgression.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
                ?.addOnFailureListener {
                    handelException(it, "Cannot upload image")
                }
        }


    }

    fun logout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        eventMutableState.value = Events("Logged out")
        depopulateMessages()
        currentChatMessageListener?.remove()
    }

    fun addchat(number: String) {
        if (number.isEmpty() || !number.isDigitsOnly()) {
            handelException(Exception("Please enter a valid number"))
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2,number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2,number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handelException(Exception("nuber not found"))
                            } else {
                                var chatPartners = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ), ChatUser(
                                        chatPartners.userId,
                                        chatPartners.name,
                                        chatPartners.imageUrl,
                                        chatPartners.number
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)

                            }
                        }.addOnFailureListener {
                            handelException(it)
                        }

                } else {
                    handelException(Exception("Chat already exists"))
                }
            }


        }

    }
}