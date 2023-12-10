package com.example.attractions.presentation.uifirebasefragment

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import com.example.attractions.App
import com.example.attractions.R
import com.example.attractions.databinding.FragmentFirebaseBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.RuntimeException
import kotlin.random.Random

class FirebaseFragment : Fragment() {

    private var _binding: FragmentFirebaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirebaseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationButton.setOnClickListener {
            createNotification()
        }

        binding.makeCriticalErrorButton.setOnClickListener {

            FirebaseCrashlytics.getInstance().log("Test log message")

            throw Exception("My test exception")
        }

        binding.makeNonCriticalErrorButton.setOnClickListener {

            try {
                throw RuntimeException("Test")
            } catch (e: RuntimeException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        binding.backToGalleryButton.setOnClickListener {
            findNavController().navigate(R.id.action_firebaseFragment_to_photoGalleryFragment)
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("registration token", it.result)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun createNotification() {
        val intent = Intent(requireContext(), FirebaseFragment::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        else PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(requireContext(), App.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle("My Notification")
            .setContentText("Description of my notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(requireContext()).notify(Random.nextInt(100,1000),notification)

        }
        /*  NotificationManagerCompat.from(requireContext()).notify(Random.nextInt(100,1000),notification)
  */

    }

}