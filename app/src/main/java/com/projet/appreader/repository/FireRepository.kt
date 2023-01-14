package com.projet.appreader.repository


import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.projet.appreader.data.DataOrException
import com.projet.appreader.model.MBook
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private val queryBook : Query) {

    suspend fun getAllBooksFromDatabase():
            DataOrException<List<MBook>,Boolean,Exception>{
        val dataOrException = DataOrException<List<MBook>,Boolean,Exception>()

        try {
            dataOrException.loading =true
            dataOrException.data = queryBook.get().await().documents.map {
                it.toObject(MBook::class.java)!!
            }
            if (!dataOrException.data.isNullOrEmpty())
                dataOrException.loading = false
        }catch (e : FirebaseFirestoreException){
            dataOrException.e = e
        }
        return dataOrException
    }

/*    suspend fun getOneBooksFromDatabase():
            DataOrException<MBook,Boolean,Exception>{
        val dataOrException = DataOrException<MBook,Boolean,Exception>()

        try {
            dataOrException.loading =true
            dataOrException.data = queryBook.get().await().documents.map {
                it.toObject(MBook::class.java)!!
            }
            if (!dataOrException.data.isNullOrEmpty())
                dataOrException.loading = false
        }catch (e : FirebaseFirestoreException){
            dataOrException.e = e
        }
        return dataOrException
    }

 */
}