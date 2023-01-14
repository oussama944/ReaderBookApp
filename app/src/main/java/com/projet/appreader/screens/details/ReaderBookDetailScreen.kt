package com.projet.appreader.screens.details


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.navigation.ReaderScreens
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.projet.appreader.data.Resource
import com.projet.appreader.model.Item
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.HtmlCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.appreader.components.RoundedBtn
import com.projet.appreader.model.MBook

@Composable
fun ReaderBookDetailScreen(
    navController : NavController,
    bookId : String,
    viewModel: DetailViewModel = hiltViewModel()
){
    
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Détails du livre",
            showProfile = false,
            icon = Icons.Default.ArrowBack,
            navController = navController,
        ){
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {

        Surface(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
        ) {
            val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()){
                value = viewModel.getBookInfo(bookId)
            }.value

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(bookInfo.data == null){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = " Chargement")
                        CircularProgressIndicator()
                    }

                }else{
                    ShowBookDetails(bookInfo,navController)

                }


            }
        }
    }

}

@Composable
fun ShowBookDetails(
    bookInfo: Resource<Item>,
    navController: NavController
) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id

    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape,
        elevation = 10.dp
    ){
        Image(
            painter = rememberImagePainter(data = bookData!!.imageLinks.thumbnail),
            contentDescription=" Image du livre ",
            modifier = Modifier
                .width(90.dp)
                .height(100.dp)
                .padding(1.dp)
        )
    }
    Text(text = bookData?.title.toString(),
        style = MaterialTheme.typography.h6,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19)

    Text(text = "Authors: ${bookData?.authors.toString()}")
    Text(text = "Page Count: ${bookData?.pageCount.toString()}")
    Text(text = "Categories: ${bookData?.categories.toString()}",
        style = MaterialTheme.typography.subtitle1,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis)
    Text(text = "Published: ${bookData?.publishedDate.toString()}",
        style = MaterialTheme.typography.subtitle1)

    
    Spacer(modifier = Modifier.height(5.dp))



    val localDims = LocalContext.current.resources.displayMetrics
    
    
    
    Surface(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .padding(4.dp)
            .clip(RoundedCornerShape(5.dp)),
        shape = RectangleShape,

    ) {

        val cleanDescription = HtmlCompat.fromHtml(bookData!!.description,HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        LazyColumn(
            modifier = Modifier.padding(3.dp)
        ){
            item {
                Text(text = cleanDescription)
            }

        }
    }

    Row(
        modifier = Modifier.padding(3.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        RoundedBtn(label = "Ajouter aux lectures"){
            val book = MBook(
                title = bookData?.title.toString(),
                authors = bookData?.authors.toString(),
                description = bookData?.description.toString(),
                categories = bookData?.categories.toString(),
                notes = "",
                photoUrl = bookData?.imageLinks?.thumbnail.toString(),
                publishedDate = bookData?.publishedDate,
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            Log.d("testttt ultime", "SaveToFirebase:  Error updating doc" )
            saveToFirebase(book,navController = navController)
        }

    }

}

fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")


    if (book.toString().isNotEmpty()){
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.popBackStack()
                        }


                    }.addOnFailureListener {
                        Log.w("Error", "SaveToFirebase:  Error updating doc",it )
                    }

            }


    }

}