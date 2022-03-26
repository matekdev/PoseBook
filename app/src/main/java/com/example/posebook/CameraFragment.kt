package com.example.posebook

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.example.posebook.databinding.FragmentCameraBinding
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.OutputStream


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
        setupToolBox()

        // Request initial permissions
        requestPermissions(Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)

        if (hasPermissions())
            initCamera()

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

    private fun showPoses() {
        // Disable taking a photo, we are now in pose mode.
        binding.viewFinder.visibility = View.GONE
        binding.pictureButton.visibility = View.GONE
        binding.toolBox.visibility = View.GONE



        // Show the right pose selector button, and the confirm pose button.
        binding.rightPose.visibility = View.VISIBLE
        binding.confirmPose.visibility = View.VISIBLE

        binding.imageView3.visibility = View.VISIBLE
        binding.imageView.visibility = View.VISIBLE
    }

    private fun showToolBox() {
        // Disable taking a photo, we are now in toolbox mode.
        binding.viewFinder.visibility = View.GONE
        binding.pictureButton.visibility = View.GONE
        binding.rightPose.visibility = View.GONE
        binding.confirmPose.visibility = View.GONE
        binding.imageView3.visibility = View.GONE

        binding.toolBox.visibility = View.VISIBLE
    }

    private fun returnToCamera() {
        // Disable all buttons and show the camera view finder.
        binding.rightPose.visibility = View.GONE
        binding.confirmPose.visibility = View.GONE
        binding.imageView3.visibility = View.GONE
        binding.toolBox.visibility = View.GONE
        binding.imageView.visibility = View.GONE

        binding.viewFinder.visibility = View.VISIBLE
        binding.pictureButton.visibility = View.VISIBLE
    }

    private fun setupToolBox() {
        binding.deletePicture.setOnClickListener {
            returnToCamera()
            val toast = Toast.makeText(context, "Photo Deleted", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 0, 200)
            toast.show()
        }

        binding.savePicture.setOnClickListener {

            val result: String = if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                savePhoto()
                "Photo Saved"
            } else {
                "Unable to save photo without storage permissions!"
            }

            val toast = Toast.makeText(context, result, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 0, 200)
            toast.show()

            returnToCamera()
        }

        binding.writeReview.setOnClickListener {
            showReviewPopup()
        }
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
                    showToolBox()
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

    private fun savePhoto() {
        val contentResolver = requireActivity().contentResolver ?: return
        val bitmap = previewImage.drawToBitmap()
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, Constants.TAG)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val outstream: OutputStream? = uri?.let { contentResolver.openOutputStream(it) }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outstream)
        outstream?.close()
    }

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

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
                initCamera()
            }
        }, ContextCompat.getMainExecutor(activity as MainActivity))
    }

    private fun hasPermissions() =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission((activity as MainActivity), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (hasPermissions())
            initCamera()
        else
            Toast.makeText(context, "Unable to initialize camera without permission!", Toast.LENGTH_LONG).show()
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