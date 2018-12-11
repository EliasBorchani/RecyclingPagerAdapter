package com.mxco.recyclingviewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = RecyclingPagerAdapter(ContentProvider())
    }

    private inner class ContentProvider : RecyclingPagerAdapter.ContentProvider {

        val content = listOf(
                ImageViewModel(R.drawable.abstracts),
                ImageViewModel(R.drawable.campaign),
                ApplicationViewModel(Application(
                        R.color.theme0,
                        R.drawable.logo0,
                        R.drawable.logo0,
                        R.string.app_name_0,
                        R.string.app_description_0,
                        3.2f,
                        listOf(
                                Rating(R.drawable.user1, "Very nice app !"),
                                Rating(R.drawable.user3, "Bouhhhh when is the update ??"),
                                Rating(R.drawable.user2, "What's the point...")
                        )
                )),
                ImageViewModel(R.drawable.sincap),
                ApplicationViewModel(Application(
                        R.color.theme1,
                        R.drawable.logo1,
                        R.drawable.logo1,
                        R.string.app_name_1,
                        R.string.app_description_1,
                        1.8f,
                        listOf(
                                Rating(R.drawable.user0, "Am I the only user ??")
                        )
                )),
                ApplicationViewModel(Application(
                        R.color.theme2,
                        R.drawable.logo2,
                        R.drawable.logo2,
                        R.string.app_name_2,
                        R.string.app_description_2,
                        4.5f,
                        listOf(
                                Rating(R.drawable.user0, "Best App Ever"),
                                Rating(R.drawable.user2, "No it's not !"),
                                Rating(R.drawable.user1, "Yes it is !")
                        )
                )),
                ImageViewModel(R.drawable.tilki)
        )

        override fun getViewModel(position: Int): ViewModel {
            return content[position]
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                content[position] is ImageViewModel -> 0
                else -> 1
            }
        }

        override fun getViewHolderForType(viewType: Int): RecyclingPagerAdapter.ViewHolder<out ViewModel> {
            return when (viewType) {
                0 -> ImageViewHolder(this@MainActivity.lifecycle)
                else -> ApplicationViewHolder(this@MainActivity.lifecycle)
            }
        }

        override fun getCount() = content.size

        override fun getTitle(position: Int): String {
            return "$position"
        }
    }
}
