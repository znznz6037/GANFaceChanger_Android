package com.example.pal_grad.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pal_grad.R
import com.example.pal_grad.api.*
import kotlinx.android.synthetic.main.selfie2anime.*
import kotlinx.android.synthetic.main.selfie2anime.view.*
import kotlinx.android.synthetic.main.selfie2anime.view.animeImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class Selfie2Anime : Fragment() {
    private val openGallery = 1
    private var uriPath : String?  = ""
    lateinit var base64: String

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, openGallery)
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = activity?.managedQuery(uri, projection, null, null, null)
        val columnIndex = cursor
            ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return columnIndex?.let { cursor?.getString(it) }
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater!!.inflate(R.layout.selfie2anime, container, false)
        view.animeImageView.setOnClickListener {
            openGalleryForImage()
        }
        view.exec_UGATIT_button.setOnClickListener {
            uploadImage()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == openGallery){
            animeImageView.setImageURI(data?.data)
            val selectedImageUri = data!!.data
            uriPath = getRealPathFromURI(selectedImageUri)
        }
    }

    private fun uploadImage(){
        var file = File(uriPath)
        val requestBody : RequestBody = RequestBody.create(MediaType.parse("CONTENT_TYPE"), file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val call = UgatitAPI().instance().upload(body)

        val view = layoutInflater.inflate(R.layout.ugatit_loading, null)

        val loadingDialog = AlertDialog.Builder(context!!)
            .create()

        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setView(view)
        loadingDialog.show()

        call.enqueue(object : retrofit2.Callback<UgatitPost>{
            override fun onFailure(call: retrofit2.Call<UgatitPost>?, t: Throwable?) {
                loadingDialog.dismiss()

                val errorView = layoutInflater.inflate(R.layout.ugatit_error, null)

                val errorDialog = AlertDialog.Builder(context!!)
                    .create()

                val exitBtn = errorView.findViewById<Button>(R.id.ugatit_error_exit_button)
                exitBtn.setOnClickListener {
                    errorDialog.dismiss()
                }
                errorDialog.setView(errorView)
                errorDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded)
                errorDialog.show()
            }

            override fun onResponse(call: retrofit2.Call<UgatitPost>?, response: Response<UgatitPost>?) {
                getBase64()
                loadingDialog.dismiss()
            }
        })
    }

    fun getBase64(){
        val call = UgatitAPI().instance().getResult()
        call.enqueue(object : retrofit2.Callback<UgatitResult> {

            override fun onResponse(
                call: retrofit2.Call<UgatitResult>,
                response: Response<UgatitResult>
            ) {
                Log.d("결과:", "성공 : ${response.body().toString()}")
                var base64_All = response?.body().toString()
                base64 = base64_All.replace("UgatitResult(img=", "")

                val view = layoutInflater.inflate(R.layout.ugatit_popup, null)
                val alertDialog = AlertDialog.Builder(context!!)
                    .create()

                val img = view.findViewById<ImageView>(R.id.ugatit_popup_image)
                img.setImageBitmap(convertString64ToImage(resizeBase64Image(base64)))
                val exitBtn = view.findViewById<Button>(R.id.ugatit_exit_button)
                exitBtn.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.setView(view)
                alertDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded)
                alertDialog.show()
            }
            override fun onFailure(call: retrofit2.Call<UgatitResult>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })
    }

    fun resizeBase64Image(base64image: String): String {
        val encodeByte: ByteArray = Base64.decode(base64image.toByteArray(), Base64.DEFAULT)
        val options = BitmapFactory.Options()
        var image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size, options)
        image = Bitmap.createScaledBitmap(image, 1024, 600, false)
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        System.gc()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    private fun convertString64ToImage(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    companion object {
        fun create(): Selfie2Anime {
            return Selfie2Anime()
        }
    }
}