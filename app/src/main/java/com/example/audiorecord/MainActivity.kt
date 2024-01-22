package com.example.audiorecord

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.audiorecord.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var granted = false
    private var permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private lateinit var recorder : MediaRecorder
    private var path = ""
    private var fileName = ""
    private var isRecord = false
    private var isPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        granted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        if (!granted){
            ActivityCompat.requestPermissions(this, permission, 1111)
        }

        binding.start.setOnClickListener {
            when{
                isRecord -> resumeRecording()
                isPause -> pauseRecording()
                else -> startRecording()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun resumeRecording() {
        recorder.resume()
        isPause = false
        binding.start.text = "resume"
    }

    @SuppressLint("NewApi")
    private fun pauseRecording() {
        recorder.pause()
        isPause = true
        binding.start.text = "pause"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1111){
            granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    fun startRecording(){
        if (!granted){
            ActivityCompat.requestPermissions(this, permission, 1111)
            return
        }
        // start
        recorder = MediaRecorder()

        path = "${externalCacheDir?.absolutePath}/"
        var simpleDate = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDate.format(Date())
        fileName = "audio_${date}"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$path$fileName.mp3")
            try {
                prepare()
            } catch (e: IOException){
                start()
            }
        }
        binding.start.text = "pause"
        isRecord = true
        isPause = false
    }
}