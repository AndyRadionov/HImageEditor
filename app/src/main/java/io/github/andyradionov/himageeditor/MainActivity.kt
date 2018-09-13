package io.github.andyradionov.himageeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_STORAGE_PERMISSION = 1
    private val FILE_PROVIDER_AUTHORITY = "io.github.andyradionov.himageeditor.fileprovider"

    private var tempPhotoPath: String? = null
    private var initialBitmap: Bitmap? = null
    private var processedBitmap: Bitmap? = null
    private var publicURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera()
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Process the image and set it to the TextView
            btnTakePic.visibility = View.INVISIBLE
            processAndSetImage()
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, tempPhotoPath)
        }
    }

    private fun initListeners() {

        btnTakePic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // If you do not have permission, request it
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_STORAGE_PERMISSION)
            } else {
                // Launch the camera if the permission exists
                launchCamera()
            }
        }

        btnSaveImg.setOnClickListener {
            BitmapUtils.deleteImageFile(this, tempPhotoPath)

            // Save the image
            val saveBitmap = if (processedBitmap != null) processedBitmap else initialBitmap
            BitmapUtils.saveImage(this, saveBitmap)
            val path = MediaStore.Images.Media.insertImage(contentResolver, saveBitmap, "HImageEditor", "HImageEditor")
            publicURI = Uri.parse(path)
        }

        btnRotate.setOnClickListener {
            val bitmapToProcess = if (processedBitmap != null) processedBitmap else initialBitmap
            processedBitmap = BitmapUtils.rotate(bitmapToProcess)
            ivResultImage.setImageBitmap(processedBitmap)
        }

        btnMirror.setOnClickListener {
            val bitmapToProcess = if (processedBitmap != null) processedBitmap else initialBitmap
            processedBitmap = BitmapUtils.flip(bitmapToProcess)
            ivResultImage.setImageBitmap(processedBitmap)
        }

        btnInvert.setOnClickListener {
            val bitmapToProcess = if (processedBitmap != null) processedBitmap else initialBitmap
            processedBitmap = BitmapUtils.invertColors(bitmapToProcess)
            ivResultImage.setImageBitmap(processedBitmap)
        }
    }

    /**
     * Method for processing the captured image and setting it to the TextView.
     */
    private fun processAndSetImage() {

        // Toggle Visibility of the views
        btnInvert.isEnabled = true
        btnRotate.isEnabled = true
        btnMirror.isEnabled = true
        btnSaveImg.isEnabled = true

        // Resample the saved image to fit the ImageView
        initialBitmap = BitmapUtils.resamplePic(this, tempPhotoPath)


        // Set the new bitmap to the ImageView
        ivPicture.setImageBitmap(initialBitmap)
    }


    private fun launchCamera() {

        // Create the capture image intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the temporary File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = BitmapUtils.createTempImageFile(this)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                tempPhotoPath = photoFile.absolutePath

                // Get the content URI for the image file
                val photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile)

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}
