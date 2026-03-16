package com.aryan.voicerepetitioncounter.ui

import androidx.lifecycle.ViewModel
import com.aryan.voicerepetitioncounter.utils.PhraseMatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

class JapaViewModel : ViewModel() {

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount

    private val _countEvent = MutableSharedFlow<Boolean>() // true if target reached, false for normal count
    val countEvent: SharedFlow<Boolean> = _countEvent.asSharedFlow()

    var targetPhrase: String = "Japa"
    var targetCount: Int = 10

    private var lastMatchTime = 0L
    private val minInterval = 200L // Reduced significantly for rapid chanting

    private var lastProcessedText = ""

    fun processRecognizedText(text: String, isFinal: Boolean) {
        val normalizedText = PhraseMatcher.normalize(text)
        val normalizedTarget = PhraseMatcher.normalize(targetPhrase)
        
        if (normalizedText == lastProcessedText || normalizedText.isEmpty()) {
            if (isFinal) lastProcessedText = ""
            return
        }
        
        // Count how many times the target phrase appears in the current recognized stream
        val currentOccurrences = countFuzzyOccurrences(normalizedText, normalizedTarget)
        val previousOccurrences = countFuzzyOccurrences(lastProcessedText, normalizedTarget)
        
        // If we found more occurrences than before in this session
        if (currentOccurrences > previousOccurrences) {
            val newMatches = currentOccurrences - previousOccurrences
            repeat(newMatches) {
                val now = System.currentTimeMillis()
                if (now - lastMatchTime > minInterval) {
                    _currentCount.value++
                    lastMatchTime = now
                    
                    val isDone = _currentCount.value >= targetCount
                    _countEvent.tryEmit(isDone)
                }
            }
        }
        
        if (isFinal) {
            lastProcessedText = ""
        } else {
            lastProcessedText = normalizedText
        }
    }
    
    private fun countFuzzyOccurrences(text: String, target: String): Int {
        if (text.isEmpty() || target.isEmpty()) return 0
        
        // Split text into words for more granular counting
        val words = text.split(" ")
        var count = 0
        
        // If the target is multiple words, we need to handle that, 
        // but for japa it's usually 1-3 words.
        val targetWords = target.split(" ")
        
        if (targetWords.size == 1) {
            for (word in words) {
                if (PhraseMatcher.similarity(word, target) > 0.6) { // Lower threshold for sensitivity
                    count++
                }
            }
        } else {
            // Simple windowed check for multi-word phrases
            for (i in 0..words.size - targetWords.size) {
                val subPhrase = words.subList(i, i + targetWords.size).joinToString(" ")
                if (PhraseMatcher.similarity(subPhrase, target) > 0.6) {
                    count++
                }
            }
        }
        return count
    }

    fun reset() {
        _currentCount.value = 0
        lastMatchTime = 0
        lastProcessedText = ""
    }
}
