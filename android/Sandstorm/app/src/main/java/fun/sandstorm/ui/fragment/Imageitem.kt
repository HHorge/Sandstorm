package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.gallery_image.view.*

//TODO: Rename ImageItem to ImageItemViewHolder
//TODO: Pass the data to the constructor as an instance of the model
class ImageItem(val resourceName: String, val imgResource: Int) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.gallery_image
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.gallery_image.setImageResource(imgResource)
    }
}