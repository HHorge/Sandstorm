package `fun`.sandstorm.ui.fragment


import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amplitude.api.Amplitude
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.draw_fragment.*


class DrawFragment : Fragment() {

    companion object {
        private var drawColor = Color.rgb(255, 255, 255)
        private var seekbarVal: Int = 30
    }

    var eraserOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    photoEditor!!.setBrushDrawingMode(false)
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.draw_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoEditor?.setBrushDrawingMode(true)

        button_draw_color.setOnClickListener {
            // AA event log
            Amplitude.getInstance().logEvent("ClickColorPicker")

            showColorPicker()
        }

        seekbar_draw.progress = seekbarVal

        seekbar_draw.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (!eraserOn) {
                    photoEditor?.brushSize = seekbarVal.toFloat()

                } else {
                    photoEditor?.setBrushEraserSize(seekbarVal.toFloat())
                    photoEditor?.brushEraser()

                }

                seekbarVal = progress


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })


        btn_eraser.setOnClickListener {
            if (!eraserOn) {
                photoEditor?.setBrushEraserSize(seekbarVal.toFloat())
                photoEditor?.brushEraser()
                btn_eraser.setBackgroundResource(R.drawable.ic_eraser_highlight)
            } else {
                photoEditor?.setBrushDrawingMode(true)
                photoEditor?.brushSize = seekbarVal.toFloat()
                btn_eraser.setBackgroundResource(R.drawable.ic_eraser)
            }
            eraserOn = !eraserOn
        }

        button_draw_done.setOnClickListener {
            photoEditor?.setBrushDrawingMode(false)
            val fragment = SelectEditFragment()
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.edit_fragment_container, fragment, "SelectEditFragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showColorPicker() {
        btn_eraser.setBackgroundResource(R.drawable.ic_eraser)
        eraserOn = false
        ColorPickerDialogBuilder
            .with(context)
            .showAlphaSlider(false)
            .setTitle("Choose color")
            .initialColor(drawColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(3)
            .setPositiveButton("ok", ColorPickerClickListener { d, lastSelectedColor, allColors ->
                drawColor = lastSelectedColor
                photoEditor?.brushColor = lastSelectedColor
            })
            .build()
            .show()
    }

}
