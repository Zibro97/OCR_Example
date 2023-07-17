package com.example.ocr_module

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.ocr_module.recognition.FaceAnalyzer
import com.example.ocr_module.recognition.FaceAnalyzerListener
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors

class Camera(private val context : Context) : ActivityCompat.OnRequestPermissionsResultCallback {
    private val preview by lazy {
        Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
    }

    private val cameraSelector by lazy {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
    }

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private lateinit var previewView : PreviewView

    private var cameraExecutor = Executors.newSingleThreadExecutor()

    private var listener : FaceAnalyzerListener? = null

    fun initCamera(layout : ViewGroup,listener: FaceAnalyzerListener) {
        this.listener = listener
        previewView = PreviewView(context)
        layout.addView(previewView)
        permissionCheck(context)
    }

    private fun permissionCheck(context:Context){
        val permissionList = listOf(Manifest.permission.CAMERA)

        if(!PermissionUtil.checkPermissions(context,permissionList)){
            PermissionUtil.requestPermission(context as Activity, permissionList)
        } else {
            openPreview()
        }
    }

    private fun openPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            .also { providerFuture ->
                providerFuture.addListener({
                                           startPreview(context)
                }, ContextCompat.getMainExecutor(context))
            }
    }

    //메인액티비티에서 얼굴 인식 호출하는 함수
    fun startFaceDetect() {
        val cameraProvider = cameraProviderFuture.get()
        val faceAnalyzer = FaceAnalyzer((context as ComponentActivity).lifecycle, previewView, listener)

        val analysisUseCase = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, faceAnalyzer)
            }

        try {
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                analysisUseCase
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopFaceDetect() {
        try {
          cameraProviderFuture.get().unbindAll()
          previewView.releasePointerCapture()
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun startPreview(context : Context) {
        val cameraProvider = cameraProviderFuture.get()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var flag = true
        if(grantResults.isNotEmpty()){
            for((i,_) in permissions.withIndex()) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    flag = false
                }
            }
            if(flag){
                openPreview()
            } else {
                Toast.makeText(context, "권한이 필요합니다.",Toast.LENGTH_SHORT).show()
                (context as Activity).finish()
            }
        }
    }
}