package io.github.andyradionov.himageeditor.ui.editor

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
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import io.github.andyradionov.himageeditor.App
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.ui.common.ImagesAdapter
import io.github.andyradionov.himageeditor.model.utils.BitmapUtils
import io.github.andyradionov.himageeditor.presentation.editor.EditorContract
import io.github.andyradionov.himageeditor.ui.history.HistoryActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val IMG_HEIGHT = 140f
private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_STORAGE_PERMISSION = 1
private const val FILE_PROVIDER_AUTHORITY = "io.github.andyradionov.himageeditor.fileprovider"

class MainActivity : AppCompatActivity(), EditorContract.View {

    private lateinit var presenter: EditorContract.Presenter
    private var takenPhotoPath: String = ""
    private lateinit var imagesAdapter: ImagesAdapter
    private val imageClickListener = object : ImagesAdapter.ImageClickListener {
        override fun onClick(picture: Picture) {
            presenter.setPicture(picture)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onPictureChanged(picture: Picture) {
        runOnUiThread {
            ivPicture.setImageURI(Uri.parse(picture.smallPath))
        }
    }

    override fun onTempPicturesChanged() {
        runOnUiThread {
            imagesAdapter.notifyDataSetChanged()
        }
    }

    override fun initState(viewState: Pair<Picture?, ArrayList<Picture>>) {
        viewState.first?.let { onPictureChanged(it) }
        setupRecycler(viewState.second)
        initListeners()
        showProgress(false)
    }

    override fun showMsg(msgId: Int) {
        runOnUiThread {
            Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    prepareCamera()
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
            presenter.preparePicture(takenPhotoPath, IMG_HEIGHT)
        } else {
            // Otherwise, delete the temporary image file
            presenter.removeTempPicture(takenPhotoPath)
            //BitmapUtils.deleteImageFile(photoPath)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.actionHistory) {
            startActivity(Intent(this, HistoryActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun launchCamera(file: File) {
        runOnUiThread {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Get the path of the temporary file
            takenPhotoPath = file.absolutePath

            // Get the content URI for the image file
            val photoURI = FileProvider.getUriForFile(this,
                    FILE_PROVIDER_AUTHORITY,
                    file)

            // Add the URI so the camera can store the image
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            // Launch the camera activity
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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
                prepareCamera()
            }
        }

        btnSaveImg.setOnClickListener {
            presenter.savePicture()
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
//        val bitmap = BitmapUtils.resamplePic(this, photoPath)
//        val processedBitmap = func(bitmap)
//        val path = BitmapUtils.saveTempBitmap(this, processedBitmap)
//        images.add(0, path)
//        imagesAdapter.notifyDataSetChanged()
    }

    private fun prepareCamera() {

        // Create the capture image intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the temporary File where the photo should go
            presenter.prepareCamera()
        }
    }

    private fun initPresenter() {
        showProgress()
        presenter = App.editorPresenter
        presenter.attachView(this)
    }

    private fun setupRecycler(pictures: List<Picture>) {
        imagesAdapter = ImagesAdapter(imageClickListener, pictures)

        val layoutManager = LinearLayoutManager(this)
        recycler.adapter = imagesAdapter
        recycler.layoutManager = layoutManager
    }

    private fun showProgress(showProgress: Boolean = true) {
        btnInvert.isEnabled = !showProgress
        btnRotate.isEnabled = !showProgress
        btnMirror.isEnabled = !showProgress
        btnSaveImg.isEnabled = !showProgress
        btnTakePic.isEnabled = !showProgress
        pbLoading.visibility = if (showProgress) View.VISIBLE else View.INVISIBLE
    }
}
