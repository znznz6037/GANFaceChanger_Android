package com.example.pal_grad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pal_grad.R
import kotlinx.android.synthetic.main.selfie2anime.view.*

class Selfie2Anime : Fragment() {
    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater!!.inflate(R.layout.selfie2anime, container, false)
        view.animeImageView.setOnClickListener {
        }
        view.exec_UGATIT_button.setOnClickListener {
        }
        return view
    }

    companion object {
        fun create(): Selfie2Anime {
            return Selfie2Anime()
        }
    }
}