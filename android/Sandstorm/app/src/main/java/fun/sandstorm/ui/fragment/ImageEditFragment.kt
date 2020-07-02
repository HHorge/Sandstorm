package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import `fun`.sandstorm.photoeditor.PhotoEditor
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_edit_fragment.*
import kotlinx.android.synthetic.main.two_by_two_mosaic_layout.*


class ImageEditFragment : Fragment() {

    private val textEditFragment = TextEditFragment(false)
    private var drawableList = mutableListOf(
        R.drawable.batman,
        R.drawable.that_smile,
        R.drawable.babyjoda,
        R.drawable.skeletons
    )

    companion object {
        var photoEditor: PhotoEditor? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_edit_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUndoRedoFragment()

        showSelectEditFragment()

        initPhotoEditor()

        button_close_edit.setOnClickListener {
            hideKeyboard()
            showSelectImageFragment()
        }

        photoEditorView.setOnClickListener {
            backToSelectEdit()
            hideKeyboard()
        }

    }

    private fun showSelectEditFragment() {
        val fragment = SelectEditFragment()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.edit_fragment_container, fragment, "selectEditFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun backToSelectEdit() {
        photoEditor?.setBrushDrawingMode(false)
        val fragment = SelectEditFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.edit_fragment_container, fragment, "SelectEditFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initPhotoEditor() {
        if (arguments!!.containsKey("imageUri")) {
            val url = arguments!!.getParcelable<Uri>("imageUri")!!
            Glide.with(this).load(url).into(photoEditorView.source)
        } else if (arguments!!.containsKey("imageID")) {
            photoEditorView.source.setImageResource(arguments!!.getInt("imageID"))
        } else {
            setDrawableList()
            addLayout()
        }

        photoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .build()
        photoEditor?.setBrushDrawingMode(false)
    }

    private fun setDrawableList() {
        if (arguments!!.containsKey("imageID2")) {
            drawableList =
                mutableListOf(arguments!!.getInt("imageID1"), arguments!!.getInt("imageID2"))
        } else {
            drawableList = mutableListOf(arguments!!.getInt("imageID1"))
        }
    }

    private fun addLayout() {
        var layout_name = arguments!!.getString("layout_name")
        when (layout_name) {
            "top_bottom_left" -> arguments!!.putInt("layoutId", R.layout.top_bottom_left_layout)
            "top_bottom_right" -> arguments!!.putInt("layoutId", R.layout.top_bottom_right_layout)
            "toptext_bottomtext" -> arguments!!.putInt(
                "layoutId",
                R.layout.toptext_bottomtext_layout
            )
            "polaroid" -> arguments!!.putInt("layoutId", R.layout.polaroid_layout)
            "toptext" -> arguments!!.putInt("layoutId", R.layout.toptext_layout)
            "bottomtext" -> arguments!!.putInt("layoutId", R.layout.bottomtext_layout)
            "revers_polaroid" -> arguments!!.putInt("layoutId", R.layout.revers_polaroid_layout)
        }
        if (arguments!!.containsKey("layoutId")) {
            val layoutId = arguments!!.getInt("layoutId")
            val viewLayout = LayoutInflater.from(context).inflate(layoutId, photoEditorView)
            photoEditorView.source.setImageResource(R.drawable.babyjoda)

            addLayoutClickListeners(layoutId)
        }
    }

    private fun addLayoutClickListeners(layoutId: Int) {
        when (layoutId) {
            R.layout.top_bottom_left_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
                image2.setImageResource(arguments!!.getInt("imageID2"))
            }
            R.layout.top_bottom_right_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
                image2.setImageResource(arguments!!.getInt("imageID2"))
            }
            R.layout.toptext_bottomtext_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
            }
            R.layout.polaroid_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
            }
            R.layout.toptext_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
            }
            R.layout.bottomtext_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
            }
            R.layout.revers_polaroid_layout -> {
                image1.setImageResource(arguments!!.getInt("imageID1"))
            }
        }
    }

    private fun showUndoRedoFragment() {
        val nextFrag = UndoRedoFragment()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.undo_redo_container, nextFrag, "UndoRedoFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun showSelectImageFragment() {
        val nextFrag = ImageGalleryFragment()
        nextFrag.initialStart = false
        Log.d("item", nextFrag.randomized.toString())
        nextFrag.listen = nextFrag.randomized
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, nextFrag, "imageGalleryFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun hideKeyboard() {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}