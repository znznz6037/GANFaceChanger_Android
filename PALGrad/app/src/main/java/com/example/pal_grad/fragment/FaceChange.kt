package com.example.pal_grad.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pal_grad.R
import kotlinx.android.synthetic.main.face_change_fragment.*
import kotlinx.android.synthetic.main.face_change_fragment.view.*

class FaceChange : Fragment() {
    private val OPEN_GALLERY = 1

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater!!.inflate(R.layout.face_change_fragment, container, false)
        view.face_upload_button.setOnClickListener { view->
            openGalleryForImage()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY){
            faceImageView.setImageURI(data?.data) // handle chosen image
        }
    }


    companion object {
        fun create(): FaceChange {
            return FaceChange()
        }
    }
}