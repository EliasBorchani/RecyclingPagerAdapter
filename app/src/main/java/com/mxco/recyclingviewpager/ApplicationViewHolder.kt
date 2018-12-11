package com.mxco.recyclingviewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ApplicationViewHolder(activityLifecycle: Lifecycle) : RecyclingPagerAdapter.ViewHolder<ApplicationViewModel>(activityLifecycle) {

    private val ratingAdapter = RatingAdapter(listOf())
    private lateinit var headerBackgroundView: View
    private lateinit var headerImage: ImageView
    private lateinit var iconImage: ImageView
    private lateinit var appRatingTextView: TextView
    private lateinit var appTitleTextView: TextView
    private lateinit var authorDescriptionTextView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(container: ViewGroup): View {
        return LayoutInflater.from(container.context).inflate(R.layout.viewholder_application, container, false).also {
            headerBackgroundView = it.findViewById(R.id.headerBackground)
            headerImage = it.findViewById(R.id.headerImage)
            iconImage = it.findViewById(R.id.iconImage)
            appRatingTextView = it.findViewById(R.id.appRating)
            appTitleTextView = it.findViewById(R.id.appTitle)
            authorDescriptionTextView = it.findViewById(R.id.authorDescription)
            recyclerView = it.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(it.context)
            recyclerView.adapter = ratingAdapter
        }
    }

    override fun onBindView(viewModel: ApplicationViewModel) {
        viewModel.headerBackgroundColorRes.observe(this, Observer { colorRes ->
            colorRes?.let { headerBackgroundView.setBackgroundResource(it) }
        })
        viewModel.headerImageRes.observe(this, Observer { imageRes ->
            imageRes?.let { Glide.with(headerImage).load(it).into(headerImage) }
        })
        viewModel.iconImageResource.observe(this, Observer { imageRes ->
            imageRes?.let { Glide.with(iconImage).load(it).into(iconImage) }
        })
        viewModel.applicationTitleResource.observe(this, Observer { titleRes ->
            titleRes?.let { appTitleTextView.setText(it) }
        })
        viewModel.applicationDescriptionResource.observe(this, Observer { descriptionRes ->
            descriptionRes?.let { authorDescriptionTextView.setText(it) }
        })
        viewModel.applicationRating.observe(this, Observer { value ->
            value?.let { appRatingTextView.text = "$it" }
        })
        viewModel.ratings.observe(this, Observer { ratings ->
            ratings?.let { ratingAdapter.ratings = ratings }
        })
    }
}

private class RatingAdapter(ratings: List<Rating>) : RecyclerView.Adapter<RatingViewHolder>() {

    var ratings: List<Rating> = ratings
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        return RatingViewHolder.createViewHolder(parent)
    }

    override fun getItemCount(): Int = ratings.size

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(ratings[position])
    }
}

private class RatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun createViewHolder(parent: ViewGroup): RatingViewHolder {
            return RatingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_application_rating, parent, false))
        }
    }

    private val image: ImageView = view.findViewById(R.id.raterImage)
    private val text: TextView = view.findViewById(R.id.ratingText)

    fun bind(rating: Rating) {
        text.text = rating.text
        Glide.with(image)
                .load(rating.profileImageResource ?: 0)
                .apply(RequestOptions.circleCropTransform())
                .into(image)
    }
}

class ApplicationViewModel(application: Application) : ViewModel() {
    val headerBackgroundColorRes = MutableLiveData<Int>().apply { value = application.headerBackgroundColorRes }
    val headerImageRes = MutableLiveData<Int>().apply { value = application.headerImageRes }
    val iconImageResource = MutableLiveData<Int>().apply { value = application.iconImageResource }
    val applicationTitleResource = MutableLiveData<Int>().apply { value = application.applicationTitleResource }
    val applicationDescriptionResource = MutableLiveData<Int>().apply { value = application.applicationDescriptionResource }
    val applicationRating = MutableLiveData<Float>().apply { value = application.applicationRating }
    val ratings = MutableLiveData<List<Rating>>().apply { value = application.ratings }
}

data class Application(
        val headerBackgroundColorRes: Int,
        val headerImageRes: Int,
        val iconImageResource: Int,
        val applicationTitleResource: Int,
        val applicationDescriptionResource: Int,
        val applicationRating: Float,
        val ratings: List<Rating>
)

data class Rating(
        val profileImageResource: Int?,
        val text: String
)
