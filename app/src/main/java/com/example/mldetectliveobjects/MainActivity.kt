package com.example.mldetectliveobjects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.mldetectliveobjects.databinding.ActivityMainBinding
import com.example.mldetectliveobjects.utils.Draw
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var objectDetect: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider=cameraProvider)
        }, ContextCompat.getMainExecutor(this))


        val localMode = LocalModel.Builder()
           .setAssetFilePath("object_labeler.tflite")
            .build()

        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localMode)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()

        objectDetect = ObjectDetection.getClient(customObjectDetectorOptions)
    }


    @OptIn(ExperimentalGetImage::class)
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
          //  .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image
            if (image != null) {
                val processImage = InputImage.fromMediaImage(image, rotationDegrees)

                objectDetect
                    .process(processImage)
                    .addOnSuccessListener {objects->
                        for(i in objects){
                            Log.d("TAGS", "bindPreview: 1: "+i.labels)
                            Log.d("TAGS", "bindPreview: : "+i.boundingBox)
                            if(binding.parentLayouts.childCount>1) binding.parentLayouts.removeViewAt(1)
                            val element=Draw(
                                context = this,
                                text = i.labels.firstOrNull()?.text?:"Undefined",
                                rect = i.boundingBox)
                            binding.parentLayouts.addView(element)
                        }

                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        Log.d("TAGS", "bindPreview: error: "+it.message)
                        imageProxy.close()
                    }
            }

        }
        cameraProvider.bindToLifecycle(this as LifecycleOwner,cameraSelector,imageAnalysis,preview)

    }
}