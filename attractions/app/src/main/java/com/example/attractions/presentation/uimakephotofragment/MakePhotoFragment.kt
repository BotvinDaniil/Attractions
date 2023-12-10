package com.example.attractions.presentation.uimakephotofragment

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.attractions.R
import com.example.attractions.databinding.FragmentMakePhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import javax.inject.Inject

const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
@AndroidEntryPoint
class MakePhotoFragment : Fragment() {
    @Inject
    lateinit var makePhotoViewModelFactory: MakePhotoViewModelFactory
    private val viewModel: MakePhotoFragmentViewModel by viewModels { makePhotoViewModelFactory }
    private var _binding: FragmentMakePhotoBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor


    private var launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { allGranted ->
            if (allGranted.values.all { it }) {
                startCamera()
            } else {
                Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMakePhotoBinding.inflate(inflater)
        executor = ContextCompat.getMainExecutor(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        binding.makePhotoButton.setOnClickListener {
            makePhoto()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_makePhotoFragment_to_photoGalleryFragment)
        }

        binding.toMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_makePhotoFragment_to_mapFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkPermissions() {
        val allGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            Toast.makeText(context, "permission is Granted", Toast.LENGTH_SHORT).show()
            startCamera()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()

                preview.setSurfaceProvider(binding.photoDisplay.surfaceProvider)
                imageCapture = ImageCapture.Builder().build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )

            }, executor
        )
    }

    private fun makePhoto() {

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        context,
                        "Photo is saved on ${outputFileResults.savedUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                        viewModel.savePhoto(outputFileResults.savedUri.toString(), name)}
                    }


                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Photo Failed ${exception.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}