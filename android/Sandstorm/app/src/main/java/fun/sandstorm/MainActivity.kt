package `fun`.sandstorm

import `fun`.sandstorm.ui.fragment.ImageEditFragment
import `fun`.sandstorm.ui.fragment.ImageGalleryFragment
import `fun`.sandstorm.ui.fragment.ImageItem
import `fun`.sandstorm.ui.gallery.Templates
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.amplitude.api.Amplitude
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        var randomized = mutableListOf<ImageItem>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (image in Templates().imageTemplates) {
            randomized.add(ImageItem(image.key, image.value))
        }
        randomized.shuffle()

        // Init Firebase Analytics?
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Init Amplitude Analytics
        Amplitude.getInstance().initialize(
            this,
            "071b5d9b489f6cbac40563fb821431c8"
        ).enableForegroundTracking(application)

        setContentView(R.layout.activity_main)

        //Checking if It is the first time the user opens the app in order to show the tutorial
        val sharedPref: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstTime = sharedPref.getBoolean("firstStart", true)

        if (firstTime) {
            val intent = Intent(this@MainActivity, OpeningTutorial::class.java)
            startActivity(intent)
        }

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()


        val mImageGalleryFragment = ImageGalleryFragment()

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, mImageGalleryFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.commit()

    }

    override fun onResume() {
        super.onResume()
        if (handleIntent(intent)) {
            handleSendImage(intent)
        }
    }

    private fun handleIntent(intent: Intent?): Boolean {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    return true
                }
            }
            else -> {
                return false
            }
        }
        return false
    }

    private fun handleSendImage(intent: Intent?) {
        (intent!!.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {


            val bundle = Bundle()
            bundle.putParcelable("imageUri", it)
            val nextFrag = ImageEditFragment()
            nextFrag.arguments = bundle
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, nextFrag)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

}