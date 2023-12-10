package com.example.attractions.presentation.uiphotogalleryfragment


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.attractions.R
import com.example.attractions.databinding.FragmentPhotoGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhotoGalleryFragment : Fragment() {
    private var launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
       isGranted ->
                    Toast.makeText(context, "permission to Notify $isGranted", Toast.LENGTH_SHORT).show()
        }


    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var galleryViewModelFactory: PhotoGalleryViewModelFactory
    private val viewModel: PhotoGalleryViewModel by viewModels { galleryViewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.getPhoto().collect {
                    binding.recycler.adapter = GalleryAdapter(it)
                }

            }
        }

        binding.makePhotoButton.setOnClickListener {

            findNavController().navigate(R.id.action_photoGalleryFragment_to_makePhotoFragment)
        }

        binding.toMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_photoGalleryFragment_to_mapFragment)
        }

        binding.toFirebaseButton.setOnClickListener {

            findNavController().navigate(R.id.action_photoGalleryFragment_to_firebaseFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


       private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "permission is Granted", Toast.LENGTH_SHORT).show()

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}