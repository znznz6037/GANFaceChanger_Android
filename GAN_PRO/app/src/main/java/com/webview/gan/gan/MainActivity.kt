package com.webview.gan.gan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private var mWebView: WebView? = null
    private var mWebSettings: WebSettings? = null
    private val mCM: String? = null
    private val mUM: ValueCallback<Uri>? = null
    private val mUMA: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf<Uri>(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data!!.getDataString()
                    if (dataString != null) {
                        results = arrayOf<Uri>(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    if (resultCode != Activity.RESULT_OK) {
                        result = null
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = if (data == null) mCapturedImageURI else data!!.getData()
                    }
                } catch (e: Exception) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show()
                }

                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }

    @SuppressLint("SetJavaScriptEnabled", "WrongViewCast")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        mWebView = findViewById<View>(R.id.webView) as WebView
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 1)
        }
        assert(mWebView != null)

        mWebSettings = mWebView!!.getSettings()
        mWebSettings!!.setJavaScriptEnabled(true)
        mWebSettings!!.setSupportMultipleWindows(false)
        mWebSettings!!.setJavaScriptCanOpenWindowsAutomatically(false)
        mWebSettings!!.setDefaultZoom(WebSettings.ZoomDensity.FAR)
        mWebView!!.setWebViewClient(WebViewClient())
        mWebSettings!!.setLoadWithOverviewMode(true)
        mWebSettings!!.setUseWideViewPort(true)
        mWebSettings!!.setSupportZoom(true)
        mWebSettings!!.setDisplayZoomControls(false)
        if (Build.VERSION.SDK_INT >= 21) {
            mWebSettings!!.setMixedContentMode(0)
            mWebView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT < 19) {
            mWebView!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        mWebView!!.setWebViewClient(Callback())
        mWebSettings!!.setBuiltInZoomControls(true)

        mWebView!!.loadUrl("https://znznz6037.github.io/GANFaceChanger/")
        mWebView!!.setWebChromeClient(object : WebChromeClient() {


            @Throws(IOException::class)
        private fun createImageFile(): File {

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)
            val imageFile = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir      /* directory */
            )
            return imageFile

        }

        public override fun onShowFileChooser(view: WebView, filePath: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent!!.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex)
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile!!.getAbsolutePath()
                    takePictureIntent!!.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile))
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.setType("image/*")
            val intentArray: Array<Intent>
            if (takePictureIntent != null) {
                intentArray = arrayOf<Intent>(takePictureIntent)
            } else {
                intentArray = arrayOf<Intent>()
            }
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            mUploadMessage = uploadMsg
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            val imageStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "AndroidExampleFolder")
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs()
            }
            // Create camera captured image file path and name
            val file = File(
                    (File.separator + "IMG_"
                            + (System.currentTimeMillis()).toString()
                            + ".jpg"))
            mCapturedImageURI = Uri.fromFile(file)
            // Camera capture image intent
            val captureIntent = Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.setType("image/*")
            // Create file chooser intent
            val chooserIntent = Intent.createChooser(i, "Image Chooser")
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>,
                            acceptType: String,
                            capture: String) {
            openFileChooser(uploadMsg, acceptType)
        }

    })

    }

    public override fun onBackPressed() {

        val alBuilder = AlertDialog.Builder(this)
        alBuilder.setMessage("종료하시겠습니까?")


        alBuilder.setPositiveButton("예", object : DialogInterface.OnClickListener {
            public override fun onClick(dialog: DialogInterface, which: Int) {
                finish()
            }
        })

        alBuilder.setNegativeButton("아니오", object : DialogInterface.OnClickListener {
            public override fun onClick(dialog: DialogInterface, which: Int) {
                return
            }
        })
        alBuilder.setTitle("프로그램 종료")
        alBuilder.show()
    }

    public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return true
    }

    inner class Callback : WebViewClient() {
        public override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show()
        }
    }


    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()


        if (id == R.id.who) {
            val alBuilder = AlertDialog.Builder(this)
            alBuilder.setMessage(("201524462 박세범\n" +
                    "201524499 안형진\n" +
                    "201624539 이동현"))
            alBuilder.setPositiveButton("닫기", object : DialogInterface.OnClickListener {
                public override fun onClick(dialog: DialogInterface, which: Int) {
                    return

                }
            })
            alBuilder.setTitle("2020 전기 졸업과제")
            alBuilder.show()
            return true
        }


        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val INPUT_FILE_REQUEST_CODE = 1
        private val FILECHOOSER_RESULTCODE = 1
        private val TAG = MainActivity::class.java!!.getSimpleName()
    }
}
