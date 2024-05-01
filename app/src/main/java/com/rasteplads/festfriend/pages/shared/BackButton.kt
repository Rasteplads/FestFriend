package com.rasteplads.festfriend.pages.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun BackButton(onClick: () -> Unit){
    Row(
        horizontalArrangement = Arrangement.Absolute.Left,
        modifier = Modifier.fillMaxWidth(1f)
    ){
        TextButton(onClick = onClick) {
            Icon(imageVector = Icons.Sharp.ArrowBack, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
