package com.example.pal_grad.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pal_grad.R
import com.example.pal_grad.api.StarGANAPI
import com.example.pal_grad.api.StarGANPost
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.face_change_fragment.*
import kotlinx.android.synthetic.main.face_change_fragment.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class FaceChange : Fragment() {
    private val OPEN_GALLERY = 1
    private var str : String? = null
    private var uriPath : String?  = ""

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = activity?.managedQuery(uri, projection, null, null, null)
        val column_index = cursor
            ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return column_index?.let { cursor?.getString(it) }
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater!!.inflate(R.layout.face_change_fragment, container, false)
        view.face_upload_button.setOnClickListener {
            openGalleryForImage()
        }
        view.api_test.setOnClickListener {
            apiTest()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY){
            faceImageView.setImageURI(data?.data) // handle chosen image
            //uriPath = data?.data.toString()
            //Log.d("uri", data?.data.toString())
            val selectedImageUri = data!!.data
            uriPath = getRealPathFromURI(selectedImageUri)
            //uriPath = data?.data.toString()
            Log.d("uri", uriPath)
        }
    }

    fun apiTest(){
        val url = "https://psbgrad.duckdns.org:5000"
        val file = File(uriPath)
        Log.d("filename : ", file.toString())
        val requestBody : RequestBody = RequestBody.create(MediaType.parse("CONTENT_TYPE"), file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(StarGANAPI::class.java)

        api.uploadImage("스타일 선택", body).enqueue(object : Callback<StarGANPost> {
            override fun onResponse(
                call: Call<StarGANPost>,
                response: Response<StarGANPost>
            ) {
                Log.d("결과", "성공 : ${response.body().toString()}")
            }

            override fun onFailure(call: Call<StarGANPost>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })
    }



    companion object {
        fun create(): FaceChange {
            return FaceChange()
        }
    }
}