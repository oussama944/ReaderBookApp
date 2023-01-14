package com.projet.appreader.screens.details

import androidx.lifecycle.ViewModel
import com.projet.appreader.data.Resource
import com.projet.appreader.model.Item
import com.projet.appreader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: BookRepository)
    : ViewModel(){

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}