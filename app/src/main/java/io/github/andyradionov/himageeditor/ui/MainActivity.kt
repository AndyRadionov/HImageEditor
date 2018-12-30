package io.github.andyradionov.himageeditor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.utils.BitmapUtils
import io.github.andyradionov.himageeditor.utils.HistoryHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_STORAGE_PERMISSION = 1
private const val FILE_PROVIDER_AUTHORITY = "io.github.andyradionov.himageeditor.fileprovider"

class MainActivity : AppCompatActivity() {

    private var photoPath: String? = null
    private val images = ArrayList<String>()
    private lateinit var imagesAdapter: ImagesAdapter
    private val imageClickListener = object: ImagesAdapter.ImageClickListener {
        override fun onClick(imagePath: String) {
            photoPath = imagePath
            processAndSetImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecycler()
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
            BitmapUtils.deleteImageFile(photoPath)
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

            // Save the image
            val saveBitmap = BitmapUtils.resamplePic(this, photoPath)
            BitmapUtils.deleteImageFile(photoPath)
            BitmapUtils.deleteTempFiles(images)
            images.clear()
            imagesAdapter.notifyDataSetChanged()
            BitmapUtils.saveImage(this, saveBitmap)
            val path = MediaStore.Images.Media.insertImage(contentResolver, saveBitmap, "HImageEditor", "HImageEditor")
            HistoryHelper.updateHistoryList(this, path)
        }

        btnRotate.setOnClickListener {
            processClick { bitmap -> BitmapUtils.rotate(bitmap) }
        }

        btnMirror.setOnClickListener {
            processClick { bitmap -> BitmapUtils.flip(bitmap) }
        }

        btnInvert.setOnClickListener {
            processClick { bitmap -> BitmapUtils.invertColors(bitmap) }
        }
    }

    private fun processClick(func: (bitmap: Bitmap) -> Bitmap) {
        val bitmap = BitmapUtils.resamplePic(this, photoPath)
        val processedBitmap = func(bitmap)
        val path = BitmapUtils.saveTempBitmap(this, processedBitmap)
        images.add(0, path)
        imagesAdapter.notifyDataSetChanged()
    }

    /**
     * Method for processing the captured image and setting it to the ImageView.
     */
    private fun processAndSetImage() {

        // Toggle Visibility of the views
        btnInvert.isEnabled = true
        btnRotate.isEnabled = true
        btnMirror.isEnabled = true
        btnSaveImg.isEnabled = true

        // Resample the saved image to fit the ImageView
        val bitmap = BitmapUtils.scalePic(this, photoPath, 160f)


        // Set the new bitmap to the ImageView
        ivPicture.setImageBitmap(bitmap)
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
                photoPath = photoFile.absolutePath

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

    private fun setupRecycler() {
        imagesAdapter = ImagesAdapter(imageClickListener, images)

        val layoutManager = LinearLayoutManager(this)
        recycler.adapter = imagesAdapter
        recycler.layoutManager = layoutManager
    }
}
