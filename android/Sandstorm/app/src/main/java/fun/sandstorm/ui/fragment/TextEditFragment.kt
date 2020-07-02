package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import `fun`.sandstorm.ui.components.RichEditText
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amplitude.api.Amplitude
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.text_edit_fragment.*
import org.json.JSONObject

class TextEditFragment(val fromButton: Boolean = true) : Fragment(),
    RichEditText.OnContentSelectedListener {


    var shouldLogCancelEvent: Boolean = true

    companion object {
        private var memeTextColor: Int = Color.rgb(255, 255, 255)
        private val textStyleBuilder: TextStyleBuilder = TextStyleBuilder()
        private var seekbarVal: Int = 30
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.text_edit_fragment, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekbar_text_edit.progress = seekbarVal
        textStyleBuilder.withTextSize(seekbarVal.toFloat())

        seekbar_text_edit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textStyleBuilder.withTextSize(progress.toFloat())
                seekbarVal = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        button_change_color.setOnClickListener {
            // AA event log
            Amplitude.getInstance().logEvent("ClickColorPicker")

            showColorPicker()
        }

        button_text_done.setOnClickListener {
            addTextToImage()
            hideKeyboard()
        }

        text_view_add_text.setOnContentSelectedListener(this)

        //Add text on enter key press
        text_view_add_text.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addTextToImage()
                showKeyboard(view)

                return@OnKeyListener true
            }
            false
        })


        showKeyboard(text_view_add_text)

        photoEditor?.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
                if (text_view_add_text == null) return
                val memeText = text_view_add_text.text.toString().toUpperCase()
                photoEditor?.editText(rootView, memeText, textStyleBuilder)
            }

            override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: ViewType) {}
            override fun onStopViewChangeListener(viewType: ViewType) {}
        })
    }

    override fun onResume() {
        super.onResume()
        shouldLogCancelEvent = true
    }

    override fun onPause() {
        if (shouldLogCancelEvent) {
            val eventProperties = JSONObject()
            eventProperties.put("TextLength", text_view_add_text.text?.length)
            eventProperties.put("FromButton", fromButton)
            Amplitude.getInstance().logEvent("StopEditText", eventProperties)
        }
        super.onPause()
    }

    private fun showColorPicker() {
        /* TODO: Need to change / make our own color picker? */
        ColorPickerDialogBuilder
            .with(context)
            .showAlphaSlider(false)
            .setTitle("Choose color")
            .initialColor(memeTextColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(3)
            .setPositiveButton("ok", ColorPickerClickListener { d, lastSelectedColor, allColors ->
                textStyleBuilder.withTextColor(lastSelectedColor)
                memeTextColor = lastSelectedColor
            })
            .build()
            .show()
    }


    /* Add the text from textView to the image and change the fragment in the bottom container
       back to SelectEditFragment */
    private fun addTextToImage() {
        photoEditor?.setBrushDrawingMode(false)
        val text = text_view_add_text.text.toString().toUpperCase()
        if (text == "") return backToSelectEdit()


        val typeface = ResourcesCompat.getFont(activity!!, R.font.impact)

        textStyleBuilder.withTextAppearance(R.style.text_shadow)
        textStyleBuilder.withTextColor(memeTextColor)
        textStyleBuilder.withTextFont(typeface!!)

        val eventProperties = JSONObject()
        eventProperties.put("TextLength", text.length)
        Amplitude.getInstance().logEvent("AddTextToImage")
        photoEditor?.addText(text, textStyleBuilder)
        text_view_add_text.text!!.clear()

        shouldLogCancelEvent = false
        backToSelectEdit()
    }

    private fun showKeyboard(v: View) {
        if (v.requestFocus()) {
            val imm =
                view!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun hideKeyboard() {
        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity!!.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun backToSelectEdit() {
        val fragment = SelectEditFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.edit_fragment_container, fragment, "SelectEditFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

    override fun onContentSelected(contentUrl: Uri) {
        Glide.with(this)
            .asBitmap()
            .load(contentUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    bm: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    photoEditor!!.addImage(bm)
                    hideKeyboard()
                    backToSelectEdit()
                }
            })
    }
}