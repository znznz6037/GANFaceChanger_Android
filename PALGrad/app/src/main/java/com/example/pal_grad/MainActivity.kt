package com.example.pal_grad

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import com.example.pal_grad.fragment.ResourceStore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        setViewPager()
        setTab()
        this.window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.WHITE
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setViewPager() {
        viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceStore.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceStore.tabList.size
            }
        }
    }

    private fun setTab() {
        TabLayoutMediator(tab_layout, viewpager) { tab : TabLayout.Tab, position ->
            tab.text = ResourceStore.tabList[position]
            main_title.setOnClickListener(){
                viewpager.setCurrentItem(TabLayout.Tab.INVALID_POSITION, true)
            }
        }.attach()
    }
}