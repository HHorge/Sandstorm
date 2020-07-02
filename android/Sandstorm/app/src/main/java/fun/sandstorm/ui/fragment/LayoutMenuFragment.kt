package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import `fun`.sandstorm.ui.gallery.Layouts
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import kotlinx.android.synthetic.main.menu_fragment.*
import org.json.JSONObject


class LayoutMenuFragment : Fragment() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        val RESULT_LOAD_IMAGE = 1
        private var imgs = 0
        val bundle = Bundle()
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
        browse_menu_button.setOnClickListener {
            browse()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (image in Layouts().layoutTemplates) {
            adapter.add(ImageItem(image.key, image.value))
        }

        adapter.setOnItemClickListener { item, view ->
            val imageItem = item as ImageItem
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
                bundle.putInt("imageID1", imageItem.imgResource)
            }
            if (imgs == 2) {
                bundle.putInt("imageID2", imageItem.imgResource)
                val nextFrag = ImageEditFragment()
                nextFrag.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, nextFrag, "ImageEditFragment")
                    .addToBackStack(null)
                    .commit()
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

    private fun browse() {
        val nextFrag = ImageGalleryFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "imageGalleryFragment")
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
        val nextFrag = MenuFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu_container, nextFrag, "MenuFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }


}