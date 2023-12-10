package com.example.attractions.presentation.uimapfragment

import android.content.pm.PackageManager
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
import com.example.attractions.data.placesApi.Feature
import com.example.attractions.data.placesApi.InfoDTO.PlaceInfoDTO
import com.example.attractions.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MapFragment : Fragment(), CameraListener {


    private lateinit var mapView: MapView
    private lateinit var mapKit: MapKit
    private lateinit var imageProvider: ImageProvider

    private var startPoint = Point(59.935881, 30.324298)
    private var mapPoint = startPoint
    private var zoom = 16.0F
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var places = listOf<Feature>()

    @Inject
    lateinit var mapFragmentViewModelFactory: MapFragmentViewModelFactory
    private val viewModel: MapFragmentViewModel by viewModels { mapFragmentViewModelFactory }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { allGranted ->
            if (allGranted.values.all { it }) {
                startMap()
            } else {
                Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater)
        mapView = binding.map
        mapKit = MapKitFactory.getInstance()
        imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.metka)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapKitFactory.initialize(requireContext())

        checkPermissions()

        val map = mapView.mapWindow.map
        val placeMarkCollection = map.mapObjects.addCollection()
        val points = mutableListOf<Point>()
        val location = mapKit.createUserLocationLayer(mapView.mapWindow)
        location.isVisible = true
        map.addCameraListener(this)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.state.collect { state ->
                    when (state) {
                        State.IsMoving -> {
                            placeMarkCollection.clear()
                            points.removeAll(points)
                        }

                        State.IsStop -> {
                            places = viewModel.getPlaces(mapPoint.longitude, mapPoint.latitude)
                            places.forEach {
                                points.add(
                                    Point(
                                        it.geometry.coordinates[1],
                                        it.geometry.coordinates[0]
                                    )
                                )
                                points.forEach {
                                    val place = placeMarkCollection.addPlacemark()
                                    place.geometry = it
                                    place.setIcon(imageProvider)
                                    place.addTapListener { _, point ->
                                        showPlaceInfo(points.indexOf(it))
                                        true
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        binding.makePhotoOnMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_makePhotoFragment)
        }

        binding.backToGalleryButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_photoGalleryFragment)
        }

        binding.zoomInButton.setOnClickListener {
            changeZoom(1f)
        }
        binding.zoomOutButton.setOnClickListener {
            changeZoom(-1f)
        }


        binding.currentLocation.setOnClickListener {

            val locationPoint = location.cameraPosition()!!.target
            mapPoint = startPoint
            mapView.mapWindow.map.move(
                CameraPosition(locationPoint, zoom, 0.0F, 0.0F),
                Animation(Animation.Type.SMOOTH, 2f), null
            )
        }

        binding.closeButton.setOnClickListener {
            binding.describeCard.visibility = View.GONE
        }


    }

    private fun showPlaceInfo(index: Int) {
        val place = places[index]
        var placeInfo: PlaceInfoDTO
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                placeInfo = viewModel.getPlaceInfo(place.properties.xid)
                binding.nameOnCard.text = getString(R.string.name_on_card, placeInfo.name)
                binding.addressOnCard.text = getString(
                    R.string.address_on_card,
                    placeInfo.address.city,
                    placeInfo.address.road,
                    placeInfo.address.houseNumber
                )
                binding.typeOnCard.text = getString(R.string.type_on_card, placeInfo.kinds)
                binding.coordinatesOnCard.text = getString(
                    R.string.coordinates_on_card,
                    placeInfo.point.lat.toString(),
                    placeInfo.point.lon.toString()
                )
                binding.describeCard.visibility = View.VISIBLE
            }
        }


    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun startMap() {
        mapView.mapWindow.map.move(
            CameraPosition(mapPoint, zoom, 0.0F, 0.0F),
            Animation(Animation.Type.SMOOTH, 5f), null
        )

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
            startMap()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }


    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        }.toTypedArray()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        updateReason: CameraUpdateReason,
        isStop: Boolean
    ) {
        mapPoint = cameraPosition.target
        viewModel.mapIsStopped(isStop)
    }

    private fun changeZoom(step: Float) {
        when (step) {
            -1f -> if (zoom > 0) {
                zoom -= 1
                mapView.mapWindow.map.move(
                    CameraPosition(mapPoint, zoom, 0.0F, 0.0F),
                    Animation(Animation.Type.SMOOTH, 2f), null
                )
            }

            1f -> if (zoom < 21) {
                zoom += 1
                mapView.mapWindow.map.move(
                    CameraPosition(mapPoint, zoom, 0.0F, 0.0F),
                    Animation(Animation.Type.SMOOTH, 2f), null
                )
            }
        }
    }

}