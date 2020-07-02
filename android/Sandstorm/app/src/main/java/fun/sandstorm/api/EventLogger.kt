package `fun`.sandstorm.api

import `fun`.sandstorm.model.event.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface EventLoggerService {

    @POST("event.php")
    fun sendEvent(@Body event: Event): Call<String>
}

object EventLogger : Callback<String> {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.sandstorm.fun/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    var eventLogger = retrofit.create(
        EventLoggerService::class.java
    )

    fun sendEvent(event: Event) {
        eventLogger.sendEvent(event).enqueue(this)
    }

    override fun onFailure(call: Call<String>, t: Throwable) {

    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

    }
}