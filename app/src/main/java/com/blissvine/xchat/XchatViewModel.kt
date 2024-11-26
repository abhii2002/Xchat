package com.blissvine.xchat

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.blissvine.xchat.data.Event
import com.blissvine.xchat.models.ChatData
import com.blissvine.xchat.models.ChatUser
import com.blissvine.xchat.models.Message
import com.blissvine.xchat.models.Status
import com.blissvine.xchat.models.UserData
import com.blissvine.xchat.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class XchatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    var inProcess = mutableStateOf(false)
    var chatInProcess = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessage = mutableStateOf<List<Message>>(listOf())
    var currentChatMessageListener: ListenerRegistration? = null
    var currentChatId = mutableStateOf<String?>(null)
    val status = mutableStateOf<List<Status>>(listOf())
    val inProcessStatus = mutableStateOf(false)
    val isUserTyping = mutableStateOf(false)



    init {
        auth.currentUser?.uid?.let {
            signIn.value = true
            getUserData(it)
        } ?: run { signIn.value = false }
    }


    fun signUp(name: String, number: String, email: String, password: String) {
        inProcess.value = true

        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields.")
            inProcess.value = false
            return
        }

        db.collection(Constants.USER_NODE).whereEqualTo("number", number).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signIn.value = true
                                createOrUpdateProfile(name = name, number = number, email = email)
                            } else {
                                handleException(task.exception, "Sign-up failed.")
                            }
                            inProcess.value = false
                        }
                        .addOnFailureListener { exception ->
                            handleException(exception, "An error occurred during sign-up.")
                            inProcess.value = false
                        }
                } else {
                    handleException(customMessage = "Number already exists.")
                    inProcess.value = false
                }
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Failed to check number existence.")
                inProcess.value = false
            }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Xchat", "Xchat Exception", exception)
        exception?.printStackTrace()
        eventMutableState.value = Event(exception?.localizedMessage ?: customMessage)
    }




    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        email: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            email = email ?: userData.value?.email,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {
            inProcess.value = true
            db.collection(Constants.USER_NODE).document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        db.collection(Constants.USER_NODE).document(uid).update(
                            "name", userData.name,
                            "email", userData.email,
                            "number", userData.number,
                            "imageUrl", userData.imageUrl
                        ).addOnSuccessListener {
                            inProcess.value = false
                            getUserData(uid)
                        }.addOnFailureListener { exception ->
                            handleException(exception, "Failed to update user data.")
                            inProcess.value = false
                        }
                    } else {
                        db.collection(Constants.USER_NODE).document(uid).set(userData)
                            .addOnSuccessListener {
                                inProcess.value = false
                                getUserData(uid)
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Failed to save new user data.")
                                inProcess.value = false
                            }
                    }
                }.addOnFailureListener { exception ->
                    handleException(exception, "Can not retrieve User.")
                    inProcess.value = false
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields.")
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                inProcess.value = false
                if (task.isSuccessful) {
                    signIn.value = true
                    auth.currentUser?.uid?.let { getUserData(it) }
                } else {
                    handleException(
                        exception = task.exception,
                        customMessage = task.exception?.localizedMessage ?: "Login Failed."
                    )
                }
            }
        }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(Constants.USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Can not retrieve User.")
            }
            value?.toObject<UserData>()?.let { user ->
                userData.value = user
                populateChatsForCurrentUser()
                populateStatus()
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) { downloadUrl ->
            createOrUpdateProfile(imageUrl = downloadUrl)
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                    inProcess.value = false
                }.addOnFailureListener { exception ->
                    handleException(exception, "Failed to fetch download URL.")
                    inProcess.value = false
                }
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Failed to upload image.")
                inProcess.value = false
            }
    }

    fun logout() {
        auth.signOut()
        userData.value = null
        signIn.value = false
        depopulateMessageOldChats()
        eventMutableState.value = Event("Log Out")
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() || !number.isDigitsOnly()) {
            handleException(customMessage = "Number must contain digits only.")
        } else {
            db.collection(Constants.CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(Constants.USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "Number not found.")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(Constants.CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    user1 = ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    user2 = ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )
                                db.collection(Constants.CHATS).document(id).set(chat)
                            }
                        }.addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exists.")
                }
            }
        }
    }

    fun populateChatsForCurrentUser() {
        db.collection(Constants.CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
                inProcess.value = false
            } else {
                value?.documents?.mapNotNull {
                    it.toObject<ChatData>()?.also { chatData ->
                        val partnerId =
                            if (chatData.user1.userId != userData.value?.userId) chatData.user1.userId else chatData.user2.userId
                        partnerId?.let { listenToPartnerProfileUpdates(it, chatData) }
                    }
                }?.let {
                    chats.value = it
                    inProcess.value = false
                }
            }
        }
    }

    fun listenToPartnerProfileUpdates(partnerId: String, chatData: ChatData) {
        db.collection(Constants.USER_NODE).document(partnerId)
            .addSnapshotListener { snapshot, error ->
                snapshot?.toObject<UserData>()?.let { partnerData ->
                    if (chatData.user1.userId == partnerData.userId) {
                        chatData.user1 = ChatUser(
                            partnerData.userId,
                            partnerData.name,
                            partnerData.imageUrl,
                            partnerData.number
                        )
                    } else {
                        chatData.user2 = ChatUser(
                            partnerData.userId,
                            partnerData.name,
                            partnerData.imageUrl,
                            partnerData.number
                        )
                    }
                    chats.value =
                        chats.value.map { if (it.chatId == chatData.chatId) chatData else it }
                }
            }
    }

//    fun onSendReply(chatId: String, message: String) {
//        val time = Calendar.getInstance().time.toString()
//        val message = com.blissvine.xchat.models.Message(userData.value?.userId, message, time)
//        db.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGE).document()
//            .set(message).addOnSuccessListener {
//
//            }
//            .addOnFailureListener { exception ->
//
//                handleException(customMessage = "Failed to send message.")
//            }
//
//    }

    fun populateAllMessages(chatId: String) {
        // Remove the previous listener before adding a new one
        if (currentChatId.value != chatId) {
            depopulateMessageOldChats()
        }

        // Set the current chat ID
        currentChatId.value = chatId

        inProcess.value = true
        currentChatMessageListener = db.collection(Constants.CHATS)
            .document(chatId)
            .collection(Constants.MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                    return@addSnapshotListener
                }

                value?.let {
                    // Only update messages for the active chat
                    if (currentChatId.value == chatId) {
                        chatMessage.value = it.documents.mapNotNull { doc ->
                            doc.toObject<Message>()
                        }.sortedBy { it.timeStamp }
                    }
                }
                inProcess.value = false
            }
    }


    fun depopulateMessageOldChats() {
        chatMessage.value = listOf() // Optional.
        currentChatMessageListener = null
        inProcess.value = false

    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri = uri) {
            createStatus(it)
        }
    }

    fun createStatus(imageUrl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number,
            ),
            imageUrl,
            System.currentTimeMillis()
        )
        db.collection(Constants.STATUS).document().set(newStatus)
    }

    fun populateStatus() {
        val timeDelta = 24L * 60 * 60 * 100
        val timeCalculation = System.currentTimeMillis() - timeDelta

        inProcessStatus.value = true
        db.collection(Constants.CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                // this will get us the current user connections
                /*
                Iterate Through Chats:
                Checks whether the current user is user1 or user2 in each chat.
                 Adds the userId of the other participant (user2 if current user is user1,
                 and vice versa) to the currentConnections list.
                 */
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnections.add(chat.user2.userId)
                    } else {
                        currentConnections.add(chat.user1.userId)
                    }
                }
                db.collection(Constants.STATUS).whereGreaterThan("timeStamp", timeCalculation).whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            handleException(error)
                        }

                        if (value != null) {
                            status.value = value.toObjects()
                            inProcessStatus.value = false
                        }


                    }

            }
        }
    }

    fun onSendReply(chatId: String, message: String) {
        val currentUser = userData.value
        if (currentUser != null && message.isNotBlank()) {
            val newMessage = com.blissvine.xchat.models.Message(
                message = message,
                sentBy = currentUser.userId,
                timeStamp = System.currentTimeMillis().toString() // Store the current time as timestamp
            )

            // Add message to Firestore
            db.collection(Constants.CHATS)
                .document(chatId)
                .collection(Constants.MESSAGE)
                .add(newMessage)
                .addOnSuccessListener {
                    // Successfully sent message
                    Log.d("XchatViewModel", "Message sent")
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e("XchatViewModel", "Error sending message", exception)
                }
        }
    }







    fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        val typingStatusRef = db.collection(Constants.CHATS)
            .document(chatId)
            .collection("typingStatus")
            .document(userId)

        typingStatusRef.set(mapOf("isTyping" to isTyping))
            .addOnFailureListener { exception ->
                handleException(exception, "Failed to update typing status.")
            }
    }


    fun observeTypingStatus(chatId: String, currentUserId: String) {
        val typingStatusRef = db.collection(Constants.CHATS)
            .document(chatId)
            .collection("typingStatus")

        typingStatusRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("XchatViewModel", "Error fetching typing status", exception)
                return@addSnapshotListener
            }

            snapshot?.documents?.forEach { doc ->
                val userId = doc.id
                val isTyping = doc.getBoolean("isTyping") ?: false
                if (userId != currentUserId) { // Ignore the current user's own status
                    if (isTyping) {
                         isUserTyping.value = true
                    } else {
                        isUserTyping.value= false
                    }
                }
            }
        }
    }







}


