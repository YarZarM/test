package com.example.myapp.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExternalLink(
    url: String,
    text: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    TextButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        modifier = modifier
    ) {
        Text(text = text)
    }
}

