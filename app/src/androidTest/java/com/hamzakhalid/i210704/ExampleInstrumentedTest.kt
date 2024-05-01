package com.hamzakhalid.i210704

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.hamzakhalid.integration.R
import org.junit.Before
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
/*
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.hamzakhalid.integration", appContext.packageName)
    }
}*/
class ExampleInstrumentedTest {
        private lateinit var scenario: ActivityScenario<Screen10Activity>

        @Before
        fun setup() {
            scenario = ActivityScenario.launch(Screen10Activity::class.java)
            scenario.moveToState(Lifecycle.State.RESUMED)
        }
        @Test
        fun testBookSessionBtnClick() {
            // Launch the Screen7Activity
            val scenario = ActivityScenario.launch(Screen10Activity::class.java)

            // Perform click action on the NotificationsIcon ImageView
            Espresso.onView(ViewMatchers.withId(R.id.BookSessBtn))
                .perform(ViewActions.click())

            // Check if Screen13Activity is opened
            // You can verify this by checking for any view or component of Screen13Activity
            Espresso.onView(ViewMatchers.withId(R.id.test11))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }
