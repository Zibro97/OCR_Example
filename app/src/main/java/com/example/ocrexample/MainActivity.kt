package com.example.ocrexample

import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SizeF
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.example.ocr_module.Camera
import com.example.ocr_module.recognition.FaceAnalyzerListener
import com.example.ocrexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FaceAnalyzerListener {
    private lateinit var binding : ActivityMainBinding
    private val camera = Camera(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setProgressText("시작하기를 눌러주세요.")
            camera.initCamera(cameraLayout, this@MainActivity)

            startDetectButton.setOnClickListener {
                it.isVisible = false
                camera.startFaceDetect()
                setProgressText("얼굴을 보여주세요.")
            }
        }
        camera.initCamera(binding.cameraLayout, this)
    }

    override fun detect() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun stopDetect() {
        camera.stopFaceDetect()
        reset()
    }

    override fun notDetect() {
    }

    override fun detectProgress(progress: Float, message: String) {
        setProgressText(message)
    }

    override fun faceSize(rectF: RectF, sizeF: SizeF, pointF: PointF) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun reset() {
        binding.startDetectButton.isVisible = true
    }

    private fun setProgressText(text : String) {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.progressTextView.text = text
    }
}