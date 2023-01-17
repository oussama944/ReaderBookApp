package com.projet.appreader.screens.home



import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.projet.appreader.components.FABContent
import com.projet.appreader.components.ListCard
import com.projet.appreader.components.ReaderAppBar
import com.projet.appreader.components.TitleSection
import com.projet.appreader.model.MBook
import com.projet.appreader.navigation.ReaderScreens

@Composable
fun Home(
    navController: NavController,
    vieWmodel: HomeViewModel = hiltViewModel()
){
    LaunchedEffect(Unit){
        vieWmodel.getAllBooksFromFireBase()
    }


    Scaffold(
        topBar = {
                 ReaderAppBar(title = "A. Home", navController = navController)
        },
        floatingActionButton = {
            FABContent(){
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            
            HomeContent(navController = navController, viewModel = vieWmodel)
        }
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel
) {
    
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks = viewModel.data.value.data!!.toList()
    }


    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty())
                                email?.split('@')
                                    ?.get(0)
                        else
                            "Pas d'utilisateur"
    Column(
        modifier = Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row (
            modifier = Modifier.align(alignment = Alignment.Start)
            ){
            Text(text = "")
                TitleSection(label = "Vos lectures ")

                Spacer(modifier = Modifier.fillMaxWidth(0.6f))

                Column{
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile utilisateur",
                        modifier = Modifier
                            .clickable {
                                navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                            }
                            .size(45.dp),
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                    Text(
                        text = currentUserName,
                        modifier = Modifier.padding(2.dp),
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                    Divider()
            }
        }
        ReadingRightNowArea(listOfBooks = listOfBooks,navController = navController)
        
        TitleSection(label = "Liste de lecture")

        BookListArea(listOfBooks = listOfBooks,navController = navController)
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {

    val addesBooks = listOfBooks.filter{ mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(addesBooks){
        navController.navigate(ReaderScreens.UpdateScreen.name+"/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    viewModel: HomeViewModel = hiltViewModel(),
    onCardPress: (String)->Unit = {}
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if(viewModel.data.value.loading == true){
            LinearProgressIndicator()
        }else{
            if (listOfBooks.isNullOrEmpty()){
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(
                        text = "Pas de livre. Ajoute un livre",
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }else{
                for ( book in listOfBooks){
                    ListCard(book){
                        onCardPress(book.googleBookId.toString())
                    }
                }

            }
        }

    }
}


@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController:NavController
){
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(readingNowList){
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }

}


