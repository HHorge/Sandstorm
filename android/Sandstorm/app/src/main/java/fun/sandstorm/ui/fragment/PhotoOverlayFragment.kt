package `fun`.sandstorm.ui.fragment


import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import `fun`.sandstorm.ui.fragment.SelectEditFragment.Companion.bottomSheet
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.photo_overlay_fragment.*


class PhotoOverlayFragment : Fragment() {

    var cameraUri: Uri? = null
    var imageUri: Uri? = null

    companion object {
        private val RESULT_LOAD_IMAGE = 1
        private val RESULT_CAMERA = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.photo_overlay_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_add_photo.setOnClickListener {
            getImageFromAlbum()
        }

        btn_take_photo.setOnClickListener {
            openCamera()
        }
    }

    private fun getImageFromAlbum() {
        try {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(i, RESULT_LOAD_IMAGE)
        } catch (exp: Exception) {
            Log.d("Error", exp.toString())
        }
    }

    override fun onActivityResult(reqCode: Int, resCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resCode, data)
        val scaling = 400
        if (reqCode == RESULT_CAMERA && resCode == Activity.RESULT_OK) {

            val bm = MediaStore.Images.Media.getBitmap(context!!.contentResolver, cameraUri)

            val width = bm.width.toFloat()
            val height = bm.height.toFloat()

            val ratio: Float = (height / width)
            val destHeight: Float = scaling * ratio

            val scaled = Bitmap.createScaledBitmap(bm, scaling, destHeight.toInt(), false)
            photoEditor!!.addImage(scaled)
        }

        if (reqCode == RESULT_LOAD_IMAGE && resCode == Activity.RESULT_OK && data != null) {
            val contentResolver: ContentResolver = context!!.contentResolver
            imageUri = data.data
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            val width = bm.width.toFloat()
            val height = bm.height.toFloat()

            val ratio: Float = (height / width)
            val destHeight: Float = scaling * ratio

            val scaled = Bitmap.createScaledBitmap(bm, scaling, destHeight.toInt(), false)
            photoEditor!!.addImage(scaled)

        }
        bottomSheet.dismiss()
    }

    private fun openCamera() {
        val contentResolver: ContentResolver = context!!.contentResolver
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        cameraUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(cameraIntent, RESULT_CAMERA)
    }
}
