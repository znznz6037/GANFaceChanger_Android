package com.example.pal_grad.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pal_grad.R
import com.example.pal_grad.api.StarGANAPI
import com.example.pal_grad.api.StarGANPost
import com.example.pal_grad.api.StarGANResult
import kotlinx.android.synthetic.main.face_change_fragment.*
import kotlinx.android.synthetic.main.face_change_fragment.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.*
import java.util.*

class FaceChange : Fragment() {
    private val openGallery = 1
    private var uriPath : String?  = ""
    lateinit var base64: String
    lateinit var style: String


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
        val view:View = inflater!!.inflate(R.layout.face_change_fragment, container, false)
        //spinner
        val items = resources.getStringArray(R.array.styleList)
        val myAdapter = ArrayAdapter(activity as Context, R.layout.spinner_font, items)
        view.spin_style.adapter = myAdapter
        view.spin_style.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2) {
                    0 -> {
                        style = "스타일 선택"
                    }
                    1 -> {
                        style = "하얀 피부"
                    }
                    2 -> {
                        style = "염소 수염"
                    }
                    3 -> {
                        style = "안경"
                    }
                    4 -> {
                        style = "미소"
                    }
                    5 -> {
                        style = "화장"
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        view.faceImageView.setOnClickListener {
            openGalleryForImage()

        }
        view.exec_StarGAN_button.setOnClickListener {
            uploadImage()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == openGallery){
            faceImageView.setImageURI(data?.data)
            val selectedImageUri = data!!.data
            uriPath = getRealPathFromURI(selectedImageUri)
        }
    }

    private fun uploadImage(){
        var file = File(uriPath)
        val requestBody : RequestBody = RequestBody.create(MediaType.parse("CONTENT_TYPE"), file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val requestBody2 : RequestBody = RequestBody.create(okhttp3.MultipartBody.FORM, style);


        val call = StarGANAPI().instance().upload(requestBody2, body)

        val view = layoutInflater.inflate(R.layout.stargan_loading, null)

        val loadingDialog = AlertDialog.Builder(context!!)
            .create()

        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setView(view)
        loadingDialog.show()

        call.enqueue(object : retrofit2.Callback<StarGANPost>{
            // handling request saat fail
            override fun onFailure(call: retrofit2.Call<StarGANPost>?, t: Throwable?) {
                loadingDialog.dismiss()

                val errorView = layoutInflater.inflate(R.layout.ugatit_error, null)

                val errorDialog = androidx.appcompat.app.AlertDialog.Builder(context!!)
                    .create()

                val exitBtn = errorView.findViewById<Button>(R.id.ugatit_error_exit_button)
                exitBtn.setOnClickListener {
                    errorDialog.dismiss()
                }
                errorDialog.setView(errorView)
                errorDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded)
                errorDialog.show()
            }
            // handling request saat response.
            override fun onResponse(call: retrofit2.Call<StarGANPost>?, response: Response<StarGANPost>?) {
                // menampilkan pesan yang diambil dari response.
                getBase64()
                loadingDialog.dismiss()
            }
        })
    }

    fun getBase64(){
        val call = StarGANAPI().instance().getResult()
        call.enqueue(object : retrofit2.Callback<StarGANResult> {

            override fun onResponse(
                call: retrofit2.Call<StarGANResult>,
                response: Response<StarGANResult>
            ) {
                Log.d("결과:", "성공 : ${response.body().toString()}")
                var base64_All = response?.body().toString()
                base64 = base64_All.replace("StarGANResult(img=", "")
                val view = layoutInflater.inflate(R.layout.stargan_popup, null)
                val alertDialog = AlertDialog.Builder(context!!)
                    .create()

                val img = view.findViewById<ImageView>(R.id.stargan_popup_image)
                img.setImageBitmap(convertString64ToImage(resizeBase64Image(base64)))
                val exitBtn = view.findViewById<Button>(R.id.stargan_exit_button)
                exitBtn.setOnClickListener {
                    alertDialog.dismiss()
                }
                val saveBtn = view.findViewById<Button>(R.id.stargan_save_button)
                saveBtn.setOnClickListener{
                    galleryAddpic(convertString64ToImage(resizeBase64Image(base64)))
                }

                alertDialog.setView(view)
                alertDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded)
                alertDialog.show()
            }
            override fun onFailure(call: retrofit2.Call<StarGANResult>, t: Throwable) {
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

    private fun galleryAddpic(bitmap: Bitmap) {
        val path = Environment.getExternalStorageDirectory().toString() + "/Pictures/얼체"
        val folder = File(path)
        if(folder.exists()) {

        } else {
            folder.mkdir()
        }
        val file = File(path, "${UUID.randomUUID()}.jpeg")
        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)
            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // Flush the output stream
            stream.flush()
            // Close the output stream
            stream.close()
            Toast.makeText(activity,"이미지저장성공", Toast.LENGTH_SHORT).show()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
            Toast.makeText(activity,"이미지저장실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertString64ToImage(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    companion object {
        fun create(): FaceChange {
            return FaceChange()
        }
    }
}