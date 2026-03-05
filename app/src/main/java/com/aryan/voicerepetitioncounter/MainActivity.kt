package com.aryan.voicerepetitioncounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aryan.voicerepetitioncounter.ui.theme.VoiceRepetitionCounterTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aryan.voicerepetition.ui.JapaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceRepetitionCounterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CounterScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {

    val viewModel: JapaViewModel = viewModel()

    val count by viewModel.currentCount.collectAsState()

    Text(
        text = "Repetitions: $count",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun CounterPreview() {
    VoiceRepetitionCounterTheme {
        CounterScreen()
    }
}