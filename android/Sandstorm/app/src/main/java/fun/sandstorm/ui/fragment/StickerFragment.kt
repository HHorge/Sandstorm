package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.ImageEditFragment.Companion.photoEditor
import `fun`.sandstorm.ui.gallery.Overlays
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.sticker_fragment.*
import org.json.JSONObject

class StickerFragment : BottomSheetDialogFragment() {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { setupBottomSheet(it) }
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.apply {
            addAll(Overlays().drawableFromName.toRecyclerListItem())
        }

        adapter.setOnItemClickListener { item, view ->
            val stickerItem = item as StickerItem

            // AA event log
            val eventProperties = JSONObject()
            eventProperties.put("From", "Stickers")
            eventProperties.put("ID", stickerItem.resourceName)
            Amplitude.getInstance().logEvent("ClickAddSticker", eventProperties)


            val bm = BitmapFactory.decodeResource(resources, stickerItem.resourceId)

            val width = bm.width.toFloat()
            val height = bm.height.toFloat()

            val ratio: Float = (height / width)
            val destHeight: Float = 200 * ratio

            val scaled = Bitmap.createScaledBitmap(bm, 200, destHeight.toInt(), false)
            photoEditor!!.addImage(scaled)
            this.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview_sticker_fragment.layoutManager = GridLayoutManager(context, 3)
        recyclerview_sticker_fragment.adapter = adapter
        showPhotoOverlayFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sticker_fragment, container, false)
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
            ?: return

        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

    }

    private fun Map<String, Int>.toRecyclerListItem(): List<StickerItem> {
        return this.map {
            StickerItem(it.key, it.value)
        }
    }

    private fun showPhotoOverlayFragment() {
        val nextFrag = PhotoOverlayFragment()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.photo_overlay_container, nextFrag, "PhotoOverlayFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}


