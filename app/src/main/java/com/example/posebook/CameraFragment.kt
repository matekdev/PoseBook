package com.example.posebook

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.posebook.databinding.FragmentCameraBinding

interface CameraFragmentDelegate {
    fun showReviewPopup()
}

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private var _binding : FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var delegate: CameraFragmentDelegate

    private var imageCapture : ImageCapture? = null
    private var isInitialPhotoTaken: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasCameraPermissions()) {
            initCamera()
        } else {
            (activity as MainActivity).requestPermissions(Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }

        binding.pictureButton.setOnClickListener {
            takePhoto(isInitialPhotoTaken)
        }

        binding.confirmPose.setOnClickListener {
            isInitialPhotoTaken = true
            returnToCamera()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CameraFragmentDelegate) {
            delegate = context
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (hasCameraPermissions()) {
                initCamera();
            } else {
                // TODO: Handle the case the user doesn't accept permissions.
            }
        }
    }

    private fun showPoses() {
        // Disable taking a photo, we are now in pose mode.
        binding.viewFinder.visibility = View.GONE
        binding.pictureButton.visibility = View.GONE


        // Show the right pose selector button, and the confirm pose button.
        binding.rightPose.visibility = View.VISIBLE
        binding.confirmPose.visibility = View.VISIBLE
        binding.imageView4.visibility = View.VISIBLE

    }

    private fun returnToCamera() {
        // Disable all buttons and show the camera view finder.
        binding.rightPose.visibility = View.GONE
        binding.confirmPose.visibility = View.GONE
        binding.imageView4.visibility=View.GONE

        binding.viewFinder.visibility = View.VISIBLE
        binding.pictureButton.visibility = View.VISIBLE
    }

    private fun takePhoto(isInitialTaken: Boolean) {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(ContextCompat.getMainExecutor(activity as MainActivity),
        object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                if (!isInitialTaken) {
                    showPoses()
                } else {
                    /* This will be replaced by the function that shows the save and delete button.
                    For the time being, I will just call the review popup here
                        */
                    showReviewPopup()
                    isInitialPhotoTaken = false
                }
                binding.previewImage.setImageBitmap(imageProxy.image?.toBitmap()?.rotate(90f))
                imageProxy.close()
            }
            override fun onError(error: ImageCaptureException) {
               // TODO: Handle when we fail to take a photo.
            }
        })
    }

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance((activity as MainActivity))

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                mPreview -> mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle((activity as MainActivity), cameraSelector, preview, imageCapture)
            } catch(e: Exception) {
                // TODO: Handle camera failed to start.
            }
        }, ContextCompat.getMainExecutor(activity as MainActivity))
    }

    private fun hasCameraPermissions() =
    Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                (activity as MainActivity), it
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Convert Image to Bitmap for use within an ImageView.
    private fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // We need to rotate the due to a CameraX bug.
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showReviewPopup() {
        delegate.showReviewPopup()
    }
}