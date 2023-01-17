package com.projet.appreader.screens.update

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.appreader.R
import com.projet.appreader.components.InputField
import com.projet.appreader.components.RatingBar
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.components.RoundedBtn
import com.projet.appreader.data.DataOrException
import com.projet.appreader.model.MBook
import com.projet.appreader.model.SearchInfo
import com.projet.appreader.navigation.ReaderScreens
import com.projet.appreader.screens.home.HomeViewModel
import com.projet.appreader.utils.formatDate

@Composable
fun ReaderUpdateScreen(
    navController: NavController,
    bookId : String,
    viewModel: HomeViewModel = hiltViewModel()
){

    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController){
            navController.popBackStack()
        }
    }) {

        val bookInfo = produceState<DataOrException<List<MBook>,
                Boolean,
                Exception>>(initialValue = DataOrException(data = emptyList(),
            true, Exception(""))){
            value = viewModel.data.value
        }.value

        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {
                Log.d("INFO", "BookUpdateScreen: ${viewModel.data.value.data.toString()}")
                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                    bookInfo.loading = false

                }else {
                    Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                        shape = CircleShape,
                        elevation = 4.dp) {
                        ShowBookUpdate(bookInfo = viewModel.data.value,
                            bookItemId = bookId)

                    }

                    ShowSimpleForm(book = viewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookId
                    }!!, navController)

                }


            }
        }

    }

}


@Composable
fun ShowSimpleForm(book: MBook,
                   navController: NavController
) {
    val context = LocalContext.current

    val notesText = remember {
        mutableStateOf("")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)

    }
    val ratingVal = remember {
        mutableStateOf(0)
    }
    SimpleForm(defaultValue = if (book.notes.toString().isNotEmpty()) book.notes.toString()
    else "Pas d'avis valid."){ note ->
        notesText.value = note


    }

    Row(modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        TextButton(onClick = { isStartedReading.value = true},
            enabled = book.startedReading == null) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Commencer à lire")
                } else {
                    Text(
                        text = "Lecture commencer!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }

            }else{
                Text("Commencer le : ${formatDate(book.startedReading!!)}")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { isFinishedReading.value = true },
            enabled = book.finishedReading == null) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Terminer de lire")
                }else {
                    Text(text = "Lecture terminer")
                }
            }else {
                Text(text = "Finis le: ${formatDate(book.finishedReading!!)}")
            }

        }

    }
    Text(text = "Notes", modifier = Modifier.padding(bottom = 3.dp))
    book.rating?.toInt().let {
        RatingBar(rating = it!!){ rating->
            ratingVal.value = rating
            Log.d("TAG", "ShowSimpleForm: ${ratingVal.value}")
        }

    }

    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row {

        val changedNotes = book.notes != notesText.value
        val context = LocalContext.current
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book.startedReading

        val bookUpdate = changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value).toMap()

        RoundedBtn(label = "Mise à jour"){
            if (bookUpdate) {
                FirebaseFirestore
                    .getInstance()
                    .collection("books")


                FirebaseFirestore
                    .getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        Toast.makeText(context, "Book Updated Successfully!",Toast.LENGTH_SHORT).show()
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)

                        // Log.d("Update", "ShowSimpleForm: ${task.result.toString()}")

                    }.addOnFailureListener{
                        Log.w("Error", "Error updating document" , it)
                    }
            }




        }
        Spacer(modifier = Modifier.width(100.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(message = stringResource(id = R.string.sure) + "\n" +
                    stringResource(id = R.string.action), openDialog){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false
                            /*
                             Don't popBackStack() if we want the immediate recomposition
                             of the MainScreen UI, instead navigate to the mainScreen!
                            */

                            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                        }
                    }
            }
        }

        RoundedBtn("Supprimer"){
            openDialog.value = true
        }

    }




}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false},
            title = { Text(text = "Delete Book")},
            text = { Text(text = message)},
            buttons = {
                Row(modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center) {
                    TextButton(onClick = { onYesPressed.invoke() }) {
                        Text(text = "Oui")

                    }
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "Non")

                    }

                }
            })
    }
}










@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
){
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) { textFieldValue.value.trim().isNotEmpty() }

        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelId = "Enter Your thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid)return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })

    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>,
        Boolean, Exception>, bookItemId: String) {
    Row() {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first{mBook ->
                    mBook.googleBookId == bookItemId

                }, onPressDetails = {})
            }
        }
    }
}



@Composable
fun CardListItem(book: MBook,
                 onPressDetails: () -> Unit) {
    Card(modifier = Modifier
        .padding(
            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp
        )
        .clip(RoundedCornerShape(20.dp))
        .clickable { },
        elevation = 8.dp) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = null ,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        )
                    ))
            Column {
                Text(text = book.title.toString(),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                Text(text = book.authors.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp))

                Text(text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp))

            }

        }




    }

}