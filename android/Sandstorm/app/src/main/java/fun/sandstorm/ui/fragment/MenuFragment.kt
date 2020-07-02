package `fun`.sandstorm.ui.fragment


import `fun`.sandstorm.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amplitude.api.Amplitude
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.menu_fragment_img1.button_close_edit
import kotlinx.android.synthetic.main.menu_fragment_img2.*
import org.json.JSONObject

val layoutbundle = Bundle()

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val menuID = checkMenu()
        val imgNumb = checkImgNumb()


        if (menuID == 1) {
            return inflater.inflate(R.layout.menu_fragment_layout, container, false)
        } else if (imgNumb == 2) {
            return inflater.inflate(R.layout.menu_fragment_img2, container, false)
        } else if (imgNumb == 1) {
            return inflater.inflate(R.layout.menu_fragment_img1, container, false)
        } else {
            return inflater.inflate(R.layout.menu_fragment, container, false)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkImgNumb() == 0) {
            gallery_menu_button.setOnClickListener {
                getImageFromAlbum()
            }
            layout_menu_button.setOnClickListener {
                layouts()
            }
            browse_menu_button.setOnClickListener {
                browse()
            }
        } else {
            button_close_edit.setOnClickListener {
                closeImageSelect()
            }
        }
        if (checkNewLayout()) {
            select_image1.setImageResource(arguments!!.getInt("imageID1"))
            select_image2.setImageResource(R.drawable.active_select)
        }

    }

    private fun getImageFromAlbum() {
        try {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("From", "Phone")
            Amplitude.getInstance().logEvent("ClickAddImage", eventProperties)

            startActivityForResult(i, ImageGalleryFragment.RESULT_LOAD_IMAGE)
        } catch (exp: Exception) {
            Log.d("Error", exp.toString())
        }
    }

    private fun replaceImage(image: Uri) {
        val bundle = Bundle()
        bundle.putParcelable("imageUri", image)
        val nextFrag = ImageEditFragment()
        nextFrag.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageEditFragment")
            .addToBackStack(null)
            .commit()
    }

    override fun onActivityResult(reqCode: Int, resCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resCode, data)
        if (resCode == Activity.RESULT_OK && data != null) {
            val realPath: Uri? = data.data
            // SDK < API11
            /*
            realPath = if (Build.VERSION.SDK_INT < 11) {
                RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.data)
            } else if (Build.VERSION.SDK_INT < 19) {
                RealPathUtil.getRealPathFromURI_API11to18(this, data.data)
            } else {
                RealPathUtil.getRealPathFromURI_API19(this, data.data)
            }
             */
            replaceImage(realPath as Uri)
        }
    }

    private fun browse() {
        layoutbundle.putInt("galleryID", 0)
        val nextFrag = ImageGalleryFragment()
        nextFrag.arguments = layoutbundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageGalleryFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun layouts() {
        layoutbundle.putInt("galleryID", 1)
        val nextFrag = ImageGalleryFragment()
        nextFrag.arguments = layoutbundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageGalleryFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun closeImageSelect() {
        val nextFrag = ImageGalleryFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "imageGalleryFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun checkMenu(): Int {
        var MenuID = 0
        try {
            MenuID = arguments!!.getInt("MenuID")
        } catch (e: NullPointerException) {
        }
        return MenuID
    }

    private fun checkImgNumb(): Int {
        var imgNumb = 0
        try {
            imgNumb = arguments!!.getInt("imgNumb")
        } catch (e: NullPointerException) {
        }
        return imgNumb
    }

    private fun checkNewLayout(): Boolean {
        try {
            if (arguments!!.getBoolean("newlayout")) {
                return true
            }
        } catch (e: NullPointerException) {

        }
        return false
    }


}
