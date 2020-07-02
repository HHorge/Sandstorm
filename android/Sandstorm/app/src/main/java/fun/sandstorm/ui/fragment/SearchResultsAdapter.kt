package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.ContentFetcherImgFlip
import `fun`.sandstorm.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.search_result_image.view.*

class SearchResultAdapter(
    val context: Context,
    val listener: OnItemClickListener,
    private val dataset: Array<ContentFetcherImgFlip.ContentRecord>
) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: ContentFetcherImgFlip.ContentRecord)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class SearchResultViewHolder(val container: ConstraintLayout) :
        RecyclerView.ViewHolder(container)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultViewHolder {
        // create a new view
        val container = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_image, parent, false) as ConstraintLayout
        // set the view's size, margins, paddings and layout parameters

        return SearchResultViewHolder(container)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val item = dataset[position]

        holder.container.gallery_image.setOnClickListener {
            listener.onItemClick(dataset[position])
        }
        Glide.with(context).load(item.imageUrl).into(holder.container.gallery_image)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}