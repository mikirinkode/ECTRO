package id.ac.unila.ee.himatro.ectro.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): EctroPreferences {
        return EctroPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFirebase(): Firebase {
        return Firebase
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(firebase: Firebase): FirebaseAuth {
        return firebase.auth
    }


    @Provides
    @Singleton
    fun provideFirestore(firebase: Firebase): FirebaseFirestore {
        return firebase.firestore
    }

    @Provides
    @Singleton
    fun provideStorage(firebase: Firebase): FirebaseStorage {
        return firebase.storage
    }
}