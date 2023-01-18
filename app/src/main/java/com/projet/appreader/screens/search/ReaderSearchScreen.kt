package com.projet.appreader.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.projet.appreader.components.InputField
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.model.Item
import com.projet.appreader.navigation.ReaderScreens

@Composable
fun ReaderSearchScreen(
    navController: NavController,
    viewModel: BookSearchViewModel = hiltViewModel()
){
    Scaffold(
        topBar = {
        ReaderAppBar(
            title = "Rechercher un livre",
            icon =Icons.Default.ArrowBack,
            navController = navController,
            showProfile = false
        ){
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
            }
        }
    )
    {
        Surface(){
            Column{
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel = viewModel,
                    navController = navController
                ){ query ->
                    viewModel.searchBooks(query)
                }

                Spacer(Modifier.height(13.dp))

                ContentSearch(navController,viewModel)


            }
        }
    }

}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "Search",
    navController: NavController,
    onSearch: (String) -> Unit = {}
){
    Column {
        val searchQuerieState = rememberSaveable { mutableStateOf("") }

        val keyboardController = LocalSoftwareKeyboardController.current

        val valid = remember(searchQuerieState.value) {
            searchQuerieState.value.trim().isNotEmpty()
        }

        InputField(
            valueState = searchQuerieState,
            labelId = "Recherche",
            enabled = true,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(searchQuerieState.value.trim())
                searchQuerieState.value=""
                keyboardController?.hide()
            }
        )



    }
}


@Composable
fun ContentSearch(
    navController: NavController,
    viewModel: BookSearchViewModel
){

    val listOfBooks = viewModel.list

//    if(listOfBooks.isEmpty() == true )
//        Log.d("boo","recherche")
//        CircularProgressIndicator(
//        )


   //val listTemp = listOf<MBook>(MBook("","testeeeeee"), MBook("","kfkjdkfjdjhjfjo"))
    if (viewModel.isLoading){
        LinearProgressIndicator()
    }
    else{
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(10.dp)
        ){
            items(listOfBooks){book ->
                CardItemSearch(book,navController)
            }

        }
    }
}



@Composable
fun CardItemSearch(
    book:Item,
    navController: NavController
){
    Card(
        modifier = Modifier
            .height(100.dp)
            .padding(3.dp)
            .fillMaxWidth()
            .clickable {
                   navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
            },
        shape = RectangleShape,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ){
            var imageUrl =  "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            if (book.volumeInfo.imageLinks.smallThumbnail.isNotEmpty())
                imageUrl= book.volumeInfo.imageLinks.smallThumbnail


            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "Image du livre",
                modifier = Modifier
                    .height(140.dp)
                    .width(100.dp)
                    .padding(end = 4.dp)
            )

            Column {
                Text(
                    text = book.volumeInfo.title,
                    style = MaterialTheme.typography.subtitle1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "L'auteur "+book.volumeInfo.authors.toString(),
                    style = MaterialTheme.typography.caption,
                    overflow = TextOverflow.Clip
                )

                Text(
                    text = "Date : " + book.volumeInfo.publishedDate,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Clip
                )

                Text(
                    text = "La catégorie : " + book.volumeInfo.categories,
                    style = MaterialTheme.typography.caption,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Normal,
                )
            }

        }
    }
}