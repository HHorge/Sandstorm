package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.MainActivity
import `fun`.sandstorm.R
import `fun`.sandstorm.ui.gallery.Layouts
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.amplitude.api.Amplitude
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.image_gallery_fragment.*
import org.json.JSONObject


class ImageGalleryFragment : Fragment() {
    var randomized = MainActivity.randomized
    var listen = mutableListOf<ImageItem>()
    var initialStart = true

    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        val RESULT_LOAD_IMAGE = 1
        private var imgs = 0
        val imgbundle = Bundle()
        val gallerybundle = Bundle()
        val menubundle = Bundle()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.image_gallery_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageContainer.layoutManager = GridLayoutManager(context, 2)
        imageContainer.adapter = adapter
        showMenuFragment()
        search_bar.setOnClickListener {
            searchMemes()
        }
        menubundle.putBoolean("newlayout", false)
        imgs = 0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chooseDisplayed()

        Log.d("item", listen.toString())
        Log.d("item", initialStart.toString())

        adapter.setOnItemClickListener { item, view ->
            val imageItem = item as ImageItem
            if (layoutActive()) {
                selectLayout(imageItem)
            } else if (noLayout()) {
                normalImage(imageItem)
            } else
                if (arguments!!.getInt("imgnumb") == 1) {
                    oneImage(imageItem)
                } else if (arguments!!.getInt("imgnumb") == 2) {
                    twoImage(imageItem)
                }
        }
    }

    private fun searchMemes() {
        val nextFrag = SearchFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "SearchFragment")
            .addToBackStack(null)
            .commit()
    }


    /*
    Helper function
     */
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

    /*
    Opens the photoEditorView with the given Uri
     */
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

    private fun showMenuFragment() {
        var galleryID = 0
        try {
            galleryID = arguments!!.getInt("galleryID")
        } catch (e: NullPointerException) {
        }
        if (!noLayout()) {
            var imgNumb = arguments!!.getInt("imgnumb")
            menubundle.putInt("imgNumb", imgNumb)
        } else {
            menubundle.putInt("imgNumb", 0)
        }

        menubundle.putInt("MenuID", galleryID)
        val nextFrag = MenuFragment()
        nextFrag.arguments = menubundle
        childFragmentManager
        nextFrag.arguments = menubundle
        childFragmentManager
            .beginTransaction()
            .replace(R.id.menu_container, nextFrag, "MenuFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    /*
    displays layouts or images depending on input from menufragment
   */
    private fun chooseDisplayed() {
        var galleryID = 0
        try {
            galleryID = arguments!!.getInt("galleryID")
        } catch (e: NullPointerException) {

        }
        if (galleryID == 0) {
            if (initialStart) {
                for (item in randomized) {
                    adapter.add(item)
                }

            } else {

                for (item in listen) {
                    adapter.add(item)
                }
            }
        } else {
            for (image in Layouts().layoutTemplates) {
                adapter.add(ImageItem(image.key, image.value))
            }
        }
    }

    /*
        Set the layout choosen
    */
    private fun selectLayout(imageItem: ImageItem) {
        if (imageItem.resourceName == "top_bottom_left" || imageItem.resourceName == "top_bottom_right") {
            gallerybundle.putInt("imgnumb", 2)
        } else {
            gallerybundle.putInt("imgnumb", 1)
        }
        gallerybundle.putString("layout_name", imageItem.resourceName)
        gallerybundle.putInt("galleryID", 0)
        val nextFrag = ImageGalleryFragment()
        nextFrag.arguments = gallerybundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageGalleryFragment")
            .addToBackStack(null)
            .commit()

    }

    private fun normalImage(imageItem: ImageItem) {
        val eventProperties = JSONObject()
        eventProperties.put("From", "Gallery")
        eventProperties.put("ID", imageItem.resourceName)
        Amplitude.getInstance().logEvent("ClickAddImage", eventProperties)

        val bundle = Bundle()
        bundle.putInt("imageID", imageItem.imgResource)
        val nextFrag = ImageEditFragment()
        nextFrag.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageEditFragment")
            .addToBackStack(null)
            .commit()

    }

    /*
    if only one image is needed for the layout run this code
   */
    private fun oneImage(imageItem: ImageItem) {

        // AA event log
        val eventProperties = JSONObject()
        eventProperties.put("From", "Gallery")
        eventProperties.put("ID", imageItem.resourceName)
        Amplitude.getInstance().logEvent("ClickAddImage", eventProperties)

        imgbundle.putInt("imageID1", imageItem.imgResource)
        try {
            imgbundle.putString("layout_name", arguments!!.getString("layout_name"))
        } catch (e: NullPointerException) {

        }
        val nextFrag = ImageEditFragment()
        nextFrag.arguments = imgbundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "ImageEditFragment")
            .addToBackStack(null)
            .commit()
    }

    /*
      if two images is needed it runs this code
   */
    private fun twoImage(imageItem: ImageItem) {

        if (imgs >= 2) {
            imgs = 1
        } else {
            imgs += 1
        }
        // AA event log
        val eventProperties = JSONObject()
        eventProperties.put("From", "Gallery")
        eventProperties.put("ID", imageItem.resourceName)
        Amplitude.getInstance().logEvent("ClickAddImage", eventProperties)

        if (imgs == 1) {
            imgbundle.putInt("imageID1", imageItem.imgResource)
            menubundle.putBoolean("newlayout", true)
            menubundle.putInt("imageID1", imageItem.imgResource)
            val nextFrag = MenuFragment()
            nextFrag.arguments = menubundle
            childFragmentManager
                .beginTransaction()
                .replace(R.id.menu_container, nextFrag, "MenuFragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
        if (imgs == 2) {
            imgbundle.putInt("imageID2", imageItem.imgResource)
            try {
                imgbundle.putString("layout_name", arguments!!.getString("layout_name"))
            } catch (e: NullPointerException) {
            }
            menubundle.putBoolean("newlayout", false)
            val nextFrag = ImageEditFragment()
            nextFrag.arguments = imgbundle
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFrag, "ImageEditFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    private fun noLayout(): Boolean {
        try {
            arguments!!.containsKey("layout_name")
            if (arguments!!.getString("layout_name").toString() == "null") {
                return true
            }
        } catch (e: NullPointerException) {
            return true
        }
        return false
    }

    private fun layoutActive(): Boolean {
        try {
            var test = arguments!!.getInt("galleryID")
            if (test == 1) {
                return true
            }
        } catch (e: NullPointerException) {
            return false
        }
        return false
    }


}
