package com.projet.appreader.screens.update

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.Timestamp
import com.projet.appreader.components.InputField
import com.projet.appreader.components.RatingBar
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.components.RoundedBtn
import com.projet.appreader.data.DataOrException
import com.projet.appreader.model.MBook
import com.projet.appreader.model.SearchInfo
import com.projet.appreader.screens.home.HomeViewModel

@Composable
fun ReaderUpdateScreen(
    navController: NavController,
    bookId : String,
    viewModel: HomeViewModel = hiltViewModel()
){

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Mise à jour",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController){
                navController.popBackStack()
            }
        }
    ) {
        val bookInfo = produceState<DataOrException<List<MBook>,
                Boolean,
                Exception>>(initialValue = DataOrException(data = emptyList(),
            true, Exception(""))){
            value = viewModel.data.value
        }.value
        
        Surface(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
        ) {
            Log.d("INFO", "BookUpdateScreen: ${viewModel.data.value.data.toString()}")

            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true){
                    CircularProgressIndicator(

                    )
                    bookInfo.loading = false
                }else{
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        elevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo = viewModel.data.value, bookItemId = bookId)
                    }
                    ShowSimpleForm(book = viewModel.data.value.data?.first{mbook ->
                        mbook.googleBookId == bookId
                    }!!,navController)
                }

            }
            
        }
    }
}

@Composable
fun ShowSimpleForm(
    book: MBook,
    navController: NavController) {
    val notesText = remember { mutableStateOf("") }
    val isStartedReading = remember { mutableStateOf(false) }
    val isFinishReading = remember { mutableStateOf(false) }
    val ratingVal = remember { mutableStateOf(0) }
    SimpleForm(
        defaultValue = if (book.notes.toString().isNotEmpty()) book.notes.toString()
                        else "Pas de notes pour l'instant"
            ){note ->
        notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(
            onClick = { isStartedReading.value = true },
            enabled = book.startedReading == null
        ) {
            if(book.startedReading == null){

                if(!isStartedReading.value){
                    Text(
                        text = "Commencer La lecture",
                        color = Color.Red
                    )
                }else{
                    Text(
                        text = "Commencer La lecture",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }

            }else{
                Text("Commencé le: ${book.startedReading}")
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = { isFinishReading.value = true},
            enabled = book.finishedReading == null
        ) {
            if (book.finishedReading == null){
                if(!isFinishReading.value){
                    Text(
                        text = "Finir La lecture",
                        color = Color.Red
                    )
                }else{
                    Text(
                        text = "Lecture terminer",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            }else{
                Text("Finis le: ${book.finishedReading}")
            }

        }

    }

    Text(text = "Note", modifier = Modifier.padding(bottom = 3.dp))

    book.rating?.toInt().let {
        RatingBar(rating = it!!){rating ->
            ratingVal.value = rating
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 15.dp))

    Row{
        RoundedBtn(label = "Mise à jour "){
            val changedNotes = book.notes != notesText.value
            val changedRating = book.rating?.toInt() != ratingVal.value
            val isFinishedTimeStamp = if(isFinishReading.value) Timestamp.now() else book.finishedReading
            val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book.startedReading

            val bookUpdate = changedNotes || changedRating || isStartedReading.value || isFinishReading.value

            val bookToUpdate = hashMapOf(
                                "finished_reading_at" to isFinishedTimeStamp,
                                "started_reading_at" to isStartedTimeStamp,
                                "rating" to ratingVal.value,
                                "notes" to notesText.value
                                )
        }

        Spacer(modifier = Modifier.width(100.dp))

        RoundedBtn(label = "Supprimer "){

        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Incroyable livre !",
    onSearch: (String) -> Unit
){
    Column {
        val texFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(texFieldValue.value){texFieldValue.value.trim().isNotEmpty()}
        
        InputField(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
            ,
            valueState = texFieldValue,
            labelId = "Entrer votre avis",
            enabled = true,
            onAction = KeyboardActions{
                if (!valid)return@KeyboardActions
                onSearch(texFieldValue.value.trim())
                keyboardController?.hide()
            }
        )
    }

}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>, Boolean, Exception>,
    bookItemId: String
) {
    Row(){
        Spacer(modifier = Modifier.width(43.dp))

        if(bookInfo.data != null){
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {

                CardListItem(book = bookInfo.data!!.first{mBook ->
                    mBook.googleBookId == bookItemId
                },onPressDetails= {})

            }
        }
    }

}

@Composable
fun CardListItem(
    book: MBook,
    onPressDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { },
        elevation = 8.dp
    ){
        Row(
            horizontalArrangement = Arrangement.Start
        ) {

            Image(painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 70.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = 8.dp,end=8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,end=8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,end=8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
