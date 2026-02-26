package com.aryan.voicerepetition.ui

import androidx.lifecycle.ViewModel
import com.aryan.voicerepetition.utils.PhraseMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JapaViewModel : ViewModel() {

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount

    var targetPhrase: String = ""
    var targetCount: Int = 108

    private var lastMatchTime = 0L
    private val minInterval = 1000L

    fun processRecognizedText(text: String) {
        val similarity = PhraseMatcher.similarity(text, targetPhrase)

        if (similarity > 0.85) {
            val now = System.currentTimeMillis()
            if (now - lastMatchTime > minInterval) {
                _currentCount.value++
                lastMatchTime = now
            }
        }
    }

    fun reset() {
        _currentCount.value = 0
        lastMatchTime = 0
    }
}