package `fun`.sandstorm.ui.fragment


import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.undo_redo_fragment.*


class UndoRedoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.undo_redo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        button_undo.setOnClickListener {
            photoEditor?.undo()
        }

        button_redo.setOnClickListener {
            photoEditor?.redo()

        }
    }
}