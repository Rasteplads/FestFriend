package com.rasteplads.festfriend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rasteplads.festfriend.model.GroupID
import com.rasteplads.festfriend.repository.Repository
import com.rasteplads.festfriend.ui.theme.FestFriendTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.createGroup("beans")
        viewModel.MessageResponse.observe(this, Observer { response ->
            if(response.isSuccessful){
                response.body()?.let {
                    Log.d("Response", it.groupID)
                }
            }
            else {
                Log.d("Response", response.errorBody().toString())
                Log.d("Response", response.code().toString())
            }
        })
        val joinedGroup = viewModel.MessageResponse.value?.body()

        if (joinedGroup != null) {
            viewModel.joinGroup(joinedGroup.groupID, "Tom", "beans")
            viewModel.GroupResponse.observe(this, Observer { response ->
                if(response.isSuccessful){
                    response.body()?.let {
                        Log.d("Response", it.message)
                    }
                }
                else {
                    Log.d("Response", response.errorBody().toString())
                    Log.d("Response", response.code().toString())
                }
            })
        }





        setContent {
            FestFriendTheme (dynamicColor = false){
                MyApp("Menis", Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(name: String, modifier: Modifier = Modifier) {
    Surface(modifier) {
        LandingPage()
    }
}

@Composable
fun LandingPage(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(0.8f)
        ){
            Text(text = "Create Group")
        }
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth(0.8f)
        ){
            Text(text = "Join Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CreateGroupPage(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Username") },
            modifier = Modifier.padding(bottom = 15.dp, top = 15.dp).fillMaxWidth(0.8f)
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Group Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.padding(bottom = 15.dp, top = 15.dp).fillMaxWidth(0.8f)
        )
        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Create Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun JoinGroupPage(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Username") },
            modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth(0.8f)
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Group ID") },
            modifier = Modifier.padding(bottom = 15.dp, top = 15.dp).fillMaxWidth(0.8f)
        )
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Group Password") },
            modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth(0.8f)
        )
        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Join Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MapPage(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ID",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
    }
}


@Preview
@Composable
fun MyAppPreview() {
    FestFriendTheme (dynamicColor = false) {
        MyApp("prev", Modifier.fillMaxSize())
    }
}

