package `fun`.sandstorm.ui.fragment

import com.google.common.truth.Truth.assertThat
import androidx.fragment.app.testing.FragmentScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextEditFragmentTest {

    @Test
    fun testShouldLogCancelEvent() {
        // TextEditFragment should set the shouldLogCancelEvent state to true after being resumed
        with(launch(TextEditFragment::class.java)) {
            onFragment { fragment ->
                fragment.shouldLogCancelEvent = false
                fragment.onPause()
                fragment.onResume()
                assertThat(fragment.shouldLogCancelEvent).isTrue()
            }
        }
    }
}