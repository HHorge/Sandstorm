package `fun`.sandstorm.ui.fragment

import `fun`.sandstorm.MainActivity
import `fun`.sandstorm.R
import `fun`.sandstorm.ui.fragment.utils.CustomMatchers.withImageDrawable
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ImageEditFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val INTENT = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @Before
    fun init() {
        activityTestRule.launchActivity(INTENT);
        val fragment = ImageEditFragment()
        val bundle = Bundle()
        bundle.putInt("imageID", R.drawable.alfredo)
        fragment.arguments = bundle
        activityTestRule.activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, "imageEditFragment")
            .commit()
    }

    @Test
    fun elements_displayed() {
        // Logo
        onView(withId(R.id.header2)).check(matches(isDisplayed()))
        // Edit Buttons
        onView(withId(R.id.edit_fragment_container)).check(matches(isDisplayed()))
        // Undo/Redo Buttons
        onView(withId(R.id.undo_redo_container)).check(matches(isDisplayed()))
        // "X"/Close Button
        onView(withId(R.id.button_close_edit)).check(matches(isDisplayed()))
        // PhotoEditor
        onView(withId(R.id.photoEditorView)).check(matches(isDisplayed()))
        onView(withImageDrawable(R.drawable.alfredo)).check(matches(isDisplayed()))
    }

    @Test
    fun fragmentContainer_exists() {
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
    }

    @Test
    fun transition_to_addOverlay() {
        //onView(withId(R.id.button_add_sticker)).perform(click())
        //onView(withId(R.id.photoEditorView)).check(doesNotExist())
        //onView(withId(R.id.recyclerview_sticker_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun transition_to_AddText() {
        //onView(withId(R.id.button_add_text)).perform(click())
        //onView(withId(R.id.photoEditorView)).check(doesNotExist())
        //onView(withId(R.id.text_view_add_text)).check(matches(isDisplayed()))
    }

    @Test
    fun transition_to_AddDraw() {
        //onView(withId(R.id.button_add_draw)).perform(click())
        //onView(withId(R.id.photoEditorView)).check(doesNotExist())
        //onView(withId(R.id.button_draw_done)).check(matches(isDisplayed()))
    }

    @Test
    fun transition_to_Browse () {
        //onView(withId(R.id.button_close_edit)).perform(click())
        //onView(withId(R.id.photoEditorView)).check(doesNotExist())
        //onView(withId(R.id.imageContainer)).check(matches(isDisplayed()))
    }

}