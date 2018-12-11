package com.mxco.recyclingviewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide

class ImageViewHolder(activityLifecycle: Lifecycle) : RecyclingPagerAdapter.ViewHolder<ImageViewModel>(activityLifecycle) {

    private lateinit var image: ImageView

    override fun onCreateView(container: ViewGroup): View {
        return LayoutInflater.from(container.context).inflate(R.layout.viewholder_image, container, false).also {
            image = it.findViewById(R.id.fullscreenImage)
        }
    }

    override fun onBindView(viewModel: ImageViewModel) {
        viewModel.imageRes.observe(this, Observer { imageRes ->
            imageRes?.let { Glide.with(image).load(it).into(image) }
        })
    }
}

class ImageViewModel(imageRes: Int): ViewModel() {
    val imageRes = MutableLiveData<Int>().apply { value = imageRes }
}
