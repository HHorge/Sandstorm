package `fun`.sandstorm.ui.components

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat

class RichEditText : androidx.appcompat.widget.AppCompatEditText {

    interface OnContentSelectedListener {
        fun onContentSelected(contentUrl: Uri)
    }

    var listener: OnContentSelectedListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
        val ic: InputConnection = super.onCreateInputConnection(editorInfo)
        EditorInfoCompat.setContentMimeTypes(editorInfo, arrayOf("image/png"))

        val callback =
            InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, opts ->
                val lacksPermission = (flags and
                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0
                // read and display inputContentInfo asynchronously
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && lacksPermission) {
                    try {
                        inputContentInfo.requestPermission()
                    } catch (e: Exception) {
                        return@OnCommitContentListener false // return false if failed
                    }
                }

                // read and display inputContentInfo asynchronously.
                // call inputContentInfo.releasePermission() as needed.

                listener?.onContentSelected(inputContentInfo.contentUri)

                true  // return true if succeeded
            }
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback)
    }

    fun setOnContentSelectedListener(listener: OnContentSelectedListener) {
        this.listener = listener
    }
}