package com.projet.appreader.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projet.appreader.data.DataOrException
import com.projet.appreader.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.projet.appreader.model.MBook
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FireRepository)
:ViewModel() {

    val data: MutableState<DataOrException<List<MBook>,Boolean,Exception>>
    = mutableStateOf(
        DataOrException(listOf(),true,Exception(""))
    )


    init{
        getAllBooksFromFireBase()
    }

    fun getAllBooksFromFireBase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()

            if(!data.value.data.isNullOrEmpty())
                data.value.loading = false
        }
        Log.d("HomeViewModel","gvet all books ${data.value.data?.toList().toString()}")
    }

}