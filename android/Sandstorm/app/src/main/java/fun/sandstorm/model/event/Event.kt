package `fun`.sandstorm.model.event

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Event(
    val type: String,
    val url: String
) {
    companion object {

        //Event types
        const val CLICK_SEARCH_RESULT = "CLICK_SEARCH_RESULT"
    }
}