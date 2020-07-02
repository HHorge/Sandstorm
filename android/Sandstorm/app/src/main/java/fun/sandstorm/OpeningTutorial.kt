package `fun`.sandstorm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_opening_tutorial.*

class OpeningTutorial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening_tutorial)

        verify_button.setOnClickListener {
            val intent = Intent(this@OpeningTutorial, MainActivity::class.java)
            startActivity(intent)
        }
    }


}