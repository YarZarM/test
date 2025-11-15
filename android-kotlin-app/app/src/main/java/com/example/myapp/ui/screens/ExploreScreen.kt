package com.example.myapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.ui.components.CollapsibleSection
import com.example.myapp.ui.components.ExternalLink

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            // Header with icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 8.dp),
                    tint = Color(0xFF808080)
                )
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "This app includes example code to help you get started.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            CollapsibleSection(title = "File-based routing") {
                Text(
                    text = buildAnnotatedString {
                        append("This app has two screens: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("app/(tabs)/index.tsx")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("app/(tabs)/explore.tsx")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append("The layout file in ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("app/(tabs)/_layout.tsx")
                        }
                        append(" sets up the tab navigator.")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExternalLink(
                    url = "https://docs.expo.dev/router/introduction",
                    text = "Learn more"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CollapsibleSection(title = "Android, iOS, and web support") {
                Text(
                    text = buildAnnotatedString {
                        append("You can open this project on Android, iOS, and the web. To open the web version, press ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("w")
                        }
                        append(" in the terminal running this project.")
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CollapsibleSection(title = "Images") {
                Text(
                    text = buildAnnotatedString {
                        append("For static images, you can use the ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("@2x")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("@3x")
                        }
                        append(" suffixes to provide files for different screen densities")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExternalLink(
                    url = "https://reactnative.dev/docs/images",
                    text = "Learn more"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CollapsibleSection(title = "Light and dark mode components") {
                Text(
                    text = buildAnnotatedString {
                        append("This template has light and dark mode support. The ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("useColorScheme()")
                        }
                        append(" hook lets you inspect what the user's current color scheme is, and so you can adjust UI colors accordingly.")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExternalLink(
                    url = "https://docs.expo.dev/develop/user-interface/color-themes/",
                    text = "Learn more"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CollapsibleSection(title = "Animations") {
                Text(
                    text = buildAnnotatedString {
                        append("This template includes an example of an animated component. The ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("components/HelloWave.tsx")
                        }
                        append(" component uses the powerful ")
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )) {
                            append("react-native-reanimated")
                        }
                        append(" library to create a waving hand animation.")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

