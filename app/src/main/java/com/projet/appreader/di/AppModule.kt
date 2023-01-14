package com.projet.appreader.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.appreader.network.BooksApi
import com.projet.appreader.repository.BookRepository
import com.projet.appreader.repository.FireRepository
import com.projet.appreader.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesBookRepository(api: BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun providesBookApi(): BooksApi{
        return Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    @Provides
    fun proviteFireBookRepository()
    =FireRepository(queryBook = FirebaseFirestore
        .getInstance()
        .collection("books")
        .whereEqualTo("user_id",
            FirebaseAuth.getInstance().currentUser?.uid.toString())
    )








    /*
    @Singleton
    @Provides
    fun proviteFireBookRepositoryBackup()
            =FireRepository(queryBook = FirebaseFirestore
        .getInstance()
        .collection("books")
    )
*/

}