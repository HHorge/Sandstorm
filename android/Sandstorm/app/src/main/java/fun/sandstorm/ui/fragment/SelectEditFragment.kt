package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amplitude.api.Amplitude
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.SaveSettings.Builder
import kotlinx.android.synthetic.main.select_edit_fragment.*
import org.json.JSONObject
import java.io.File
import java.util.*

class SelectEditFragment : Fragment(), ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener {

    // TODO: Comment on these variables
    var checkIfSharing = true
    var lastClickTime = 0L


    companion object {
        fun newInstance(): SelectEditFragment {
            return SelectEditFragment()
        }

        val bottomSheet = StickerFragment()
        const val DEFAULT_FILENAME = "sandstorm_fun.png"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_draw_image.setOnClickListener {
            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("From", "addDrawButton")
            Amplitude.getInstance().logEvent("ClickAddDraw", eventProperties)

            startDrawFragment()
        }

        button_add_text.setOnClickListener {
            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("From", "addTextButton")
            Amplitude.getInstance().logEvent("ClickAddText", eventProperties)

            startTextEditFragment()
        }

        button_add_sticker.setOnClickListener {
            // AA event log
            Amplitude.getInstance().logEvent("ClickAddStickerButton")


            bottomSheet.show(activity!!.supportFragmentManager, "BottomSheet")
        }

        button_share_image.setOnClickListener {
            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("ContentType", "Image")
            Amplitude.getInstance().logEvent("ClickShareButton", eventProperties)

            checkIfSharing = true
            val externalDirectory =
                context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
            val imagePath = "${externalDirectory}${File.separator}${DEFAULT_FILENAME}"

            val saveSettingsBuilder = Builder()
            val saveSettings: SaveSettings = saveSettingsBuilder
                .setClearViewsEnabled(false)
                .build()

            photoEditor?.saveAsFile(imagePath, saveSettings, this)
        }

        button_save_image.setOnClickListener {
            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("ContentType", "Image")
            Amplitude.getInstance().logEvent("ClickSaveButton", eventProperties)

            checkIfSharing = false

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                Log.d("err", "You have to wait one second before you can save again")
            } else {
                var tid = SystemClock.elapsedRealtime() - lastClickTime
                lastClickTime = SystemClock.elapsedRealtime()

                val toast =
                    Toast.makeText(context, "Image saved successfully! :)", Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.BOTTOM, 0, 375)
                toast.show()


                val externalDirectory =
                    context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
                val imagePath = "${externalDirectory}${File.separator}${DEFAULT_FILENAME}"

                val saveSettingsBuilder = Builder()
                val saveSettings: SaveSettings = saveSettingsBuilder
                    .setClearViewsEnabled(false)
                    .build()

                photoEditor?.saveAsFile(imagePath, saveSettings, this)
            }
        }
    }

    private fun startDrawFragment() {
        val fragment = DrawFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.edit_fragment_container, fragment, "DrawFragment")
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun startTextEditFragment() {
        val fragment = TextEditFragment(true)
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.edit_fragment_container, fragment, "TextEditFragment")
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onSuccess(imagePath: String) {
        val time = Date().time.toString()
        val title = time + "Sandstorm"

        val bmpUri =
            FileProvider.getUriForFile(context!!, "fun.sandstorm.fileprovider", File(imagePath))
        val shareIntent = Intent(Intent.ACTION_SEND)
        if (checkIfSharing) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.type = "image/png"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share a meme..."))
        } else {
            val bitmap: Bitmap = BitmapFactory.decodeFile(imagePath)
            val contentResolver: ContentResolver = context!!.contentResolver
            MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                title,
                null
            )
        }

    }

    override fun onFailure(exception: java.lang.Exception) {
        Log.e("error", exception.toString())
    }
}
