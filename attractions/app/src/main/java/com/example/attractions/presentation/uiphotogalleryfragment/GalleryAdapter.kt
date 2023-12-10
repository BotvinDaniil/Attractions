package com.example.attractions.presentation.uiphotogalleryfragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.attractions.data.PhotoInfo
import com.example.attractions.databinding.PhotoViewBinding

class GalleryAdapter(private val photos : List<PhotoInfo>) : RecyclerView.Adapter<GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = PhotoViewBinding.inflate(LayoutInflater.from(parent.context))
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val photo = photos[position]
        holder.binding.photoData.text = photo.photoDate
        Glide.with(holder.binding.photoView)
            .load(Uri.parse(photo.photoUri))
            .into(holder.binding.photo)
    }
}
class GalleryViewHolder(val binding: PhotoViewBinding) : ViewHolder(binding.root)