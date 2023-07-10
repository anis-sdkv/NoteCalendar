package com.example.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    singleLine: Boolean = false,
    textColor: Color = Color.Black,
    hint: String? = null
) {
    Box {
        if (value.isEmpty() && hint != null)
            Text(
                text = hint,
                style = textStyle,
                color = textColor.copy(alpha = 0.5f),
                modifier = modifier
            )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            textStyle = textStyle.copy(color = textColor),
            singleLine = singleLine
        )
    }
}

