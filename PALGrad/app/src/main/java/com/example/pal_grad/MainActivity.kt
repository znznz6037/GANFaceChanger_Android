package com.example.pal_grad

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.exit_popup, null)
        val textView: TextView = view.findViewById(R.id.alertContent)
        textView.text = "정말 종료하시겠습니까?"

        val alertDialog = AlertDialog.Builder(this)
            .create()

        val cancelBtn = view.findViewById<Button>(R.id.app_exit_cancel_button)
        cancelBtn.setOnClickListener{
            alertDialog.dismiss()
        }

        val exitBtn = view.findViewById<Button>(R.id.app_exit_button)
        exitBtn.setOnClickListener{
            finish()
        }

        alertDialog.setView(view)
        alertDialog.show()
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