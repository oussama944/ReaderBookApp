package com.projet.appreader.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.projet.appreader.R
import com.projet.appreader.components.InputField
import com.projet.appreader.components.PasswordInput
import com.projet.appreader.components.ReaderLogo
import com.projet.appreader.components.emailInput
import com.projet.appreader.navigation.ReaderScreens

@Composable
fun ReaderLoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){

    val showLoginForm = rememberSaveable { mutableStateOf(true) }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
            ReaderLogo()
            if(showLoginForm.value)
                UserForm(loading = false,isCreateAccount = false){email,password->

                    viewModel.signInUserWithEmailAndPassword(email= email,passw=password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            else
                UserForm(loading = false,isCreateAccount = true){email,password->
                    viewModel.createUserWithEmailAndPassword(email= email,password=password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                val text = if (showLoginForm.value) "S'enregistrer" else " Se connecter"
                Text(text = "Nouvelle utilisateur?")
                Text(text = text,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondaryVariant
                )

            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(
    loading : Boolean = false,
    isCreateAccount: Boolean = false,
    onDone:(String,String)->Unit={email,pwd ->}
){
    val email = rememberSaveable{ mutableStateOf("") }
    val password = rememberSaveable{ mutableStateOf("") }
    val passordVisibility = rememberSaveable{ mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value,password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .height(250.dp)
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        if(isCreateAccount)
            Text(
                text = stringResource(id = R.string.Create_acount),
                modifier=Modifier.padding(4.dp))
        
        emailInput(
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions{
                passwordFocusRequest.requestFocus()
            })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passordVisibility,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onDone(email.value.trim(),password.value.trim())
            })
        SubmitBtn(
            textId = if(isCreateAccount)"Creer un compte" else "Login",
            loading=loading,
            validInputs= valid
        ){
            onDone(email.value.trim(),password.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitBtn(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick:()-> Unit){
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(3.dp)
                .fillMaxWidth(),
            enabled = !loading && validInputs,
            shape = CircleShape) {
            if(loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
            else Text(text=textId, modifier = Modifier.padding(5.dp))}
}





