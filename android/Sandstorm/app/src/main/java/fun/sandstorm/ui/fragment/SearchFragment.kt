package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.ContentFetcherImgFlip
import `fun`.sandstorm.R
import `fun`.sandstorm.api.EventLogger
import `fun`.sandstorm.model.event.Event
import `fun`.sandstorm.model.event.Event.Companion.CLICK_SEARCH_RESULT
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : Fragment(),
    ContentFetcherImgFlip.OnContentFetchedListener,
    SearchResultAdapter.OnItemClickListener {

    lateinit var adapter: SearchResultAdapter
    val extraSearches = arrayOf(
        ContentFetcherImgFlip.ContentRecord(
            Uri.parse("https://www.sandstorm.fun/img/neha_dhupia_judging.jpg"),
            "Neha dhupia judging",
            arrayOf("neha", "dhupia", "judging")
        ),
        ContentFetcherImgFlip.ContentRecord(
            Uri.parse("https://www.sandstorm.fun/img/neha_dhupia_pointing.jpg"),
            "Neha dhupia pointing",
            arrayOf("neha", "dhupia", "pointing")
        )
    )

    companion object {
        const val IMGFLIP_SEARCH_URL = "https://imgflip.com/memesearch"
    }

    fun newInstance(): SearchFragment {
        return SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, null)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchResultAdapter(context!!, this, arrayOf())

        imageContainer.layoutManager = GridLayoutManager(context, 2)

        editText.requestFocus()
        showKeyboard(editText)

        button_back_search.setOnClickListener {
            hideKeyboard()
            activity!!.onBackPressed()
        }
        button_search.setOnClickListener {
            searchContent()
            hideKeyboard()
        }

        imageContainer.swapAdapter(adapter, false)
        editText.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    v.clearFocus()
                    val input: InputMethodManager =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    input.hideSoftInputFromWindow(editText.windowToken, 0)
                    searchContent()
                    true
                }
                else -> false
            }
        }

        emojiSearch.children.iterator().forEach { buttonView ->
            buttonView.setOnClickListener {
                hideKeyboard()
                val text = when (it.id) {
                    R.id.angryFaceButton -> {
                        "angry"
                    }
                    R.id.happyFaceButton -> {
                        "happy"
                    }
                    R.id.sadFaceButton -> {
                        "sad"
                    }
                    R.id.virus_button -> {
                        "virus"
                    }
                    else -> "nothing"
                }
                searchContent(text)
            }
        }
    }

    private fun showKeyboard(v: View) {
        if (v.requestFocus()) {
            val imm =
                view!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }


    private fun searchContent() {
        searchContent(editText.text.toString())
    }

    private fun searchContent(text: String) {
        val imgFlipUrl = Uri.parse(IMGFLIP_SEARCH_URL)

        val searchQueryBuilder = Uri.Builder()

        searchQueryBuilder
            .scheme(imgFlipUrl.scheme)
            .path(imgFlipUrl.path)
            .authority(imgFlipUrl.authority)
            .appendQueryParameter("q", text)

        val searchQuery = searchQueryBuilder.build()

        loadContent(searchQuery.toString())
    }

    private fun loadContent(url: String) {
        val asyncFetcher = ContentFetcherImgFlip()
        asyncFetcher.listener = this

        asyncFetcher.execute(url)
    }

    override fun onContentLoaded(contentRecord: List<ContentFetcherImgFlip.ContentRecord>) {
        val mixedSearchResult = contentRecord + getExtraSearches(editText.text.toString())
        val adapter = SearchResultAdapter(context!!, this, mixedSearchResult.toTypedArray())
        imageContainer.swapAdapter(adapter, false)
    }

    private fun getExtraSearches(query: String): List<ContentFetcherImgFlip.ContentRecord> {
        val terms = query.toLowerCase().split(" ")
        return extraSearches.filter { it.keywords.intersect(terms).isNotEmpty() }
    }

    override fun onItemClick(item: ContentFetcherImgFlip.ContentRecord) {
        hideKeyboard()
        val imageUrl = item.imageUrl
        val bundle: Bundle = Bundle()
        bundle.putParcelable("imageUri", imageUrl)
        val nextFrag = ImageEditFragment()
        nextFrag.arguments = bundle
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragment_container, nextFrag)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(null)
        ft.commit()

        EventLogger.sendEvent(Event(CLICK_SEARCH_RESULT, imageUrl.toString()))
    }

    private fun hideKeyboard() {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}