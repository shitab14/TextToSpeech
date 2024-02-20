package com.shitabmir.texttospeech
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.shitabmir.texttospeech.databinding.ActivityMainBinding
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding.viewModel = viewModel

        setObservers()
        checkPermissionAndSetupSpeechRecognizer()

        binding.startButton.setOnClickListener {
            Log.e("SHITABUGGER", "startButton.setOnClickListener")
            Toast.makeText(this, "Clicked!!!", Toast.LENGTH_LONG).show()
            if (!viewModel.isListening) {
                startListening()
            } else {
                stopListening()
            }
            viewModel.onStartButtonClicked()

        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.e("SHITABUGGER", "onReadyForSpeech")
            }

            override fun onBeginningOfSpeech() {
                Log.e("SHITABUGGER", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.e("SHITABUGGER", "onRmsChanged $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.e("SHITABUGGER", "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.e("SHITABUGGER", "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                Log.e("SHITABUGGER", "onError $error")

                viewModel.onStartButtonClicked() // stopping it
                viewModel.isListening = false
//                startListening() // Restart listening after receiving results

            }

            override fun onResults(results: Bundle){
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    viewModel.updateDisplayedText(matches[0])
                }
                startListening() // Restart listening after receiving results
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.e("SHITABUGGER", "onPartialResults")
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    viewModel.updateDisplayedText(matches[0])
                }
                Log.e("SHITABUGGER", "onPartialResults matches size: ${matches?.size}")

            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.e("SHITABUGGER", "onEvent")

            }
        })
    }

    private fun setObservers() {
        // Observer for displayedText
        val displayedTextObserver = Observer<String> { newText ->
            // Update UI with the new text
            // For example, update a TextView
            binding.textView.text = newText
        }
        viewModel.displayedText.observe(this, displayedTextObserver)

        // Observer for buttonText
        val buttonTextObserver = Observer<String> { newText ->
            // Update the text of the button
            binding.startButton.text = newText
        }
        viewModel.buttonText.observe(this, buttonTextObserver)
    }

    private fun startListening() {
        Log.e("SHITABUGGER", "startListening")

        speechRecognizer.startListening(createRecognizerIntent())
    }

    private fun stopListening() {
        Log.e("SHITABUGGER", "stopListening")

        speechRecognizer.stopListening()
    }

    private fun createRecognizerIntent(): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        return intent
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }


    private val RECORD_AUDIO_PERMISSION_CODE = 1
    private fun checkPermissionAndSetupSpeechRecognizer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
        } else {
            // Permission has already been granted
            setupSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with SpeechRecognizer setup
                    setupSpeechRecognizer()
                } else {
                    // Permission denied, handle accordingly
                    // For example, display a message or disable speech recognition functionality
                }
                return
            }
            // Handle other permission requests if needed
        }
    }

}