package com.example.audiorecord

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.audiorecord.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity(), Timer.OnTimeTickListener {

    private lateinit var binding: ActivityMainBinding
    private var granted = false
    private var permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private lateinit var recorder: MediaRecorder
    private var path = ""
    private var fileName = ""
    private var isRecording = false
    private var isPause = false
    private lateinit var timer: Timer
    private lateinit var vibrator: Vibrator

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        granted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            ActivityCompat.requestPermissions(this, permission, 1111)
        }

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.start.setOnClickListener {
            when {
                isPause -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        binding.list.setOnClickListener{

        }
        binding.close.setOnClickListener {
            stopRecord()
            File("$path$fileName.mp3")
        }
    }

    @SuppressLint("NewApi")
    private fun pauseRecording() {
        recorder.pause()
        isPause = true
        binding.start.text = "pause"
        timer.pause()
    }

    @SuppressLint("NewApi")
    private fun resumeRecording() {
        recorder.resume()
        isPause = false
        binding.start.text = "resume"
        timer.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1111) {
            granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("NewApi")
    private fun startRecording() {
        if (!granted) {
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
            } catch (e: IOException) {
            }
            start()
        }
        binding.start.text = "pause"
        isRecording = true
        isPause = false

        timer.start()
    }

    private fun stopRecord() {
        timer.stop()
        recorder.apply {
            stop()
            release()
        }

        isPause = false
        isRecording = false

    }

    override fun onTimeTick(duration: String) {
        binding.text.text = duration
        binding.wave.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}