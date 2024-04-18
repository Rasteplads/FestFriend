package com.rasteplads.festfriend.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rasteplads.festfriend.AppState
import com.rasteplads.festfriend.pages.shared.BackButton

@Composable
fun JoinGroupPage(
    appState: AppState,
    onUsernameChange: (String) -> Unit,
    onGroupIDChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onJoinButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onClick = onBackButtonClick)
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        TextField(
            value = appState.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
            isError = appState.usernameError.isError,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        )
        if (appState.usernameError.isError)
            Text(text = appState.usernameError.msg, color = MaterialTheme.colorScheme.error)

        Divider(Modifier.fillMaxWidth(0.8f).padding(top = 15.dp) )
        TextField(
            value = appState.groupID,
            onValueChange = onGroupIDChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Group ID") },
            singleLine = true,
            isError = appState.groupIDError.isError,
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth(0.8f)
        )
        if (appState.groupIDError.isError)
            Text(text = appState.groupIDError.msg, color = MaterialTheme.colorScheme.error)

        TextField(
            value =  appState.password,
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            label = { Text("Group Password") },
            singleLine = true,
            isError = appState.passwordError.isError,
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth(0.8f)
        )
        if (appState.passwordError.isError)
            Text(text = appState.passwordError.msg, color = MaterialTheme.colorScheme.error)

        Button(
            onClick = onJoinButtonClick,
            modifier = Modifier.fillMaxWidth(0.8f).padding(top = 15.dp)) {
            Text(text = "Join Group")
        }

        if (appState.genericError.isError)
            Text(text = appState.genericError.msg, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.weight(1f))
    }
}