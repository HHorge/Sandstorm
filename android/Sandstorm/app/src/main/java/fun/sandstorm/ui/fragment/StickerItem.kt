package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.sticker_item.view.*

//TODO: Rename StickerItem to StickerItemViewHolder
//TODO: Pass the data to the constructor as an instance of the model
class StickerItem(val resourceName: String, val resourceId: Int) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.sticker_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.sticker_image.setImageResource(resourceId)
    }

}

