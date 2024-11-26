package com.blissvine.xchat

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/*
It starts with fun, which indicates that it is a method.
This method returns a FirebaseAuth instance, which is provided via the Firebase.auth singleton.
 */

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideAuthentication(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun provideStorage(): FirebaseStorage {
        return Firebase.storage
    }

}