package com.example.kmp_template.shared_client

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kmp_template.shared_client.home.presentation.HomeScreen
import com.example.kmp_template.shared_client.home.presentation.HomeState
import com.example.kmp_template.shared_client.theme.AppTheme

@Composable
private fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    AppTheme(darkTheme = true, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                content.invoke()
            }
        }
    }
}

@Preview(device = "id:pixel_7_pro")
@Composable
private fun Home_Preview() {
    PreviewContainer {
        val state = HomeState()

        HomeScreen(
            state,
            onAction = {},
        )
    }
}
