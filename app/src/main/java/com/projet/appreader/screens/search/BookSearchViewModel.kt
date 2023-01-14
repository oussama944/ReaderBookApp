package com.projet.appreader.screens.search


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projet.appreader.data.Resource
import com.projet.appreader.model.Item
import com.projet.appreader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val repository: BookRepository) :ViewModel() {


    var list: List<Item> by mutableStateOf(listOf())

    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks (){
        searchBooks("flutter")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty())
                return@launch
            try{
                when(val response = repository.getBooks(query)){
                    is Resource.Success ->{
                        list = response.data!!
                        if (list.isNotEmpty()) isLoading =false
                    }
                    is Resource.Error ->{
                        Log.d("Resource TAG"," searchBooks : Failed gett")
                        isLoading =false
                    }
                    else ->{isLoading =false}
                }
            }catch (e:Exception){
                isLoading =false
                Log.d("Api TAG"," ${e.message.toString()}")
            }
        }
    }

}