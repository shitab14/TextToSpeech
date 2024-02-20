package com.shitabmir.texttospeech

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Shitab Mir on 2/8/24.
 * BD (Technology), Daraz Bangladesh Limited, Alibaba Group
 * shitab.islam@daraz.com.bd | shitabmir@gmail.com
 **/
class MainActivityViewModel : ViewModel() {

    private val _displayedText = MutableLiveData<String>("Listening...")
    val displayedText: LiveData<String>
        get() = _displayedText

    var isListening = false
    private val _buttonText = MutableLiveData<String>("Start Listening")
    val buttonText: LiveData<String>
        get() = _buttonText

    fun onStartButtonClicked() {
        Log.e("SHITABUGGER", "VM onStartButtonClicked")
        if (!isListening) {
            startListening()
            _buttonText.value = "Stop Listening"
        } else {
            stopListening()
            _buttonText.value = "Start Listening"
        }
    }

    fun startListening() {
        // Implement logic to start the speech recognition process
        // For example, you can start the SpeechRecognizer here
        isListening = true
    }

    fun stopListening() {
        // Implement logic to stop the speech recognition process
        // For example, you can stop the SpeechRecognizer here
        isListening = false
    }

    fun updateDisplayedText(newText: String) {
        _displayedText.value = newText
    }
}