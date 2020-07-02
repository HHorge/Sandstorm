package `fun`.sandstorm

import android.net.Uri
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class ContentFetcherImgFlip : AsyncTask<String, Unit, List<ContentFetcherImgFlip.ContentRecord>>() {

    interface OnContentFetchedListener {
        fun onContentLoaded(contentRecord: List<ContentRecord>)
    }

    lateinit var listener: OnContentFetchedListener

    data class ContentRecord(val imageUrl: Uri, val title: String, val keywords: Array<String>)

    override fun doInBackground(vararg params: String?): List<ContentRecord> {
        val result = mutableListOf<ContentRecord>()
        try {
            val document: Document = Jsoup.connect(params[0]).get()
            val mtBoxes = document.select(".mt-box")
            for (mtBox in mtBoxes) {
                val img = mtBox.select(".mt-img-wrap").select("img").first()
                val imgSrc: String = img.absUrl("src")
                val record = ContentRecord(Uri.parse(imgSrc), "", arrayOf())
                result.add(record)
            }
        } catch (e: Exception) {

        }
        return result
    }

    override fun onPostExecute(result: List<ContentRecord>) {
        listener.onContentLoaded(result)
    }
}