package com.projet.appreader.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.projet.appreader.model.MUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel:ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInUserWithEmailAndPassword(email: String, passw: String,home : ()-> Unit)
    = viewModelScope.launch{
        try {
            auth.signInWithEmailAndPassword(email,passw)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        //TODO("take them home")
                        Log.d("FB","signInWithEmailAndPassword yessss  ${task.result.toString()}")
                        home()
                    }
                    else{
                        Log.d("FB", "${task.exception.toString()}")
                    }
                }

        }catch (ex:Exception){
            Log.d("auth","${ex.toString()}")
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //me
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        home()
                    }else {
                        //Log.d("FB", "createUserWithEmailAndPassword: ${task.result.toString()}")
                        Log.d("FB", "${task.exception.toString()}")
                    }
                    _loading.value = false


                }
        }


    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid

        val user = MUser(
            id = null,
            userId = userId.toString(),
            displajName = displayName.toString(),
            avatarUrl = "",
            quote="La vie est injuste",
            profession = "Developpeur pro"
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)



    }

}