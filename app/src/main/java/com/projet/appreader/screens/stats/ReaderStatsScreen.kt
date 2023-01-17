package com.projet.appreader.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.model.MBook
import com.projet.appreader.screens.home.HomeViewModel
import com.projet.appreader.utils.formatDate
import java.util.*

@Composable
fun ReaderStatsScreen(
    navController: NavController,
    homeViewmodel : HomeViewModel = hiltViewModel()
){
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    //homeViewmodel.getAllBooksFromFireBase()

    Scaffold(
        topBar = {
            ReaderAppBar(title = "MAJ Livre",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController){
                navController.popBackStack()
            }
        }
    ) {
        if (homeViewmodel.data.value.loading == true){
            LinearProgressIndicator()
        }else
        {
            Surface {
                books = if (!homeViewmodel.data.value.data.isNullOrEmpty()){
                    homeViewmodel.data.value.data!!
                }else{
                    emptyList()
                }
                Column {
                    Row{
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector= Icons.Sharp.Person,
                                contentDescription = "Icon",
                            )
                        }
                        Text("Bonjour, ${currentUser?.email.toString().split('@')[0].uppercase(Locale.getDefault())} ")
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        shape = CircleShape,
                        elevation = 5.dp
                    ){
                        val readdBooksList: List<MBook> = if (!homeViewmodel.data.value.data.isNullOrEmpty()){
                            books.filter {
                                it.finishedReading != null
                            }
                        }else {
                            emptyList()
                        }

                        val readingBooks = if (!homeViewmodel.data.value.data.isNullOrEmpty()){
                            books.filter {
                                (it.finishedReading == null && it.startedReading !=null)
                            }
                        }else {
                            emptyList()
                        }

                        Column(
                            modifier = Modifier.padding(start = 25.dp, top =4.dp, bottom = 4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "Vos stats", style = MaterialTheme.typography.h5)
                            Divider()
                            Text(text ="Vous lisez : ${readingBooks.size} livres")
                            Text(text ="Vous avez lu : ${readdBooksList.size} livres")
                        }
                    }

                    if (homeViewmodel.data.value.loading == true){
                        LinearProgressIndicator()
                    }else{
                        Divider()

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentPadding = PaddingValues(16.dp)
                        ){
                            val readingBooks:List<MBook> = if (!homeViewmodel.data.value.data.isNullOrEmpty()){
                                books.filter {
                                    (it.finishedReading != null)
                                }
                            }else {
                                emptyList()
                            }

                            items( readingBooks){ book ->
                                BookRow(book)
                            }


                        }
                    }


                }

            }

        }
    }
}


    @Composable
    fun BookRow(
        book: MBook,
    ){
        Card(
            modifier = Modifier
                .height(100.dp)
                .padding(3.dp)
                .fillMaxWidth()
                .clickable {
                    //navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
                },
            shape = RectangleShape,
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier.padding(5.dp)
            ){
                var imageUrl =  "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
                if (book.photoUrl.toString().isNotEmpty())
                        imageUrl= book.photoUrl.toString()


                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = "Image du livre",
                    modifier = Modifier
                        .height(140.dp)
                        .width(100.dp)
                        .padding(end = 4.dp)
                )

                Column {

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = book.title!!,
                            style = MaterialTheme.typography.subtitle1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if(book.rating !! >= 4){
                            Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Vous avez aimé ce livre",
                                tint = Color.Green.copy(alpha = 0.5f)
                            )
                        }else
                            Box{}
                    }


                    Text(
                        text = "L'auteur "+book.authors.toString(),
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Clip
                    )

                    Text(
                        text = "Commencer : " + formatDate(book.startedReading!!),
                        softWrap = true,
                        style = MaterialTheme.typography.caption,
                        fontStyle = FontStyle.Italic,
                        overflow = TextOverflow.Clip
                    )

                    Text(
                        text = "Finis : " + formatDate(book.finishedReading!!),
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Clip,
                        fontStyle = FontStyle.Normal,
                    )
                }

            }
        }
    }
    


