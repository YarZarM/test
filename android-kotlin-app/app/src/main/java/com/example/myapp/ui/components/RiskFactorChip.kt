package com.example.myapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RiskFactorChip(
    feature: String,
    featureName: String,
    emoji: String,
    score: Double,
    scoreColor: Long,
    arrow: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        Color(0xFF67E8F9)
    } else {
        Color(0xFF2A2A2A)
    }
    
    val backgroundColor = if (isSelected) {
        Color(0xFF67E8F9).copy(alpha = 0.09f)
    } else {
        Color(0xFF111111)
    }
    
    val importance = (score * 100).toInt()
    
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(999.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append("$emoji ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(featureName)
                }
                append(" ")
                withStyle(style = SpanStyle(color = Color(scoreColor))) {
                    append("$arrow $importance%")
                }
            },
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

