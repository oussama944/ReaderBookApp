package com.projet.appreader.screens.stats

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

@Composable
fun ReaderStatsScreen(
    navController: NavController
){
    //Text("Stats ecran")
    var teste2 = remember{
        mutableStateOf("")
    }
    val db= FirebaseFirestore
        .getInstance()

    db.collection("books")
        .whereEqualTo("user_id", "cBoAJbDp8XSVL3nhVPaco9OnTUB2")
            //FirebaseAuth.getInstance().currentUser.toString())
        .get()
        .addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                Log.d("testeekjkdhjsdh", "${document.id} => ${document.data}")
                teste2.value = teste2.value + ' ' +document.data.toString()
            }
        }
        .addOnFailureListener { exception ->
            Log.w("testeekjkdhjsdh", "Error getting documents: ", exception)
        }

    /*
    db.collection("books")
        .get()
        .addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                Log.d("testeekjkdhjsdh", "${document.id} => ${document.data}")
                teste2.value = teste2.value + ' ' +document.data.toString()
            }
        }
        .addOnFailureListener { exception ->
            Log.w("testeekjkdhjsdh", "Error getting documents: ", exception)
        }
    */
    //Text(text = teste2.value)
    Spacer(modifier = Modifier.width(100.dp))
    Text(text = FirebaseAuth.getInstance().currentUser?.uid.toString())

}

