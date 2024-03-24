package com.hamzakhalid.i210704

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.hamzakhalid.integration.R
import org.junit.Before
import org.junit.Test


class Screen7ActivityTest {
    private lateinit var scenario: ActivityScenario<Screen7Activity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(Screen7Activity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
    @Test
    fun testNotificationsIconClick() {
        // Launch the Screen7Activity
        val scenario = ActivityScenario.launch(Screen7Activity::class.java)

        // Perform click action on the NotificationsIcon ImageView
        onView(withId(R.id.NotificationsIcon)).perform(click())

        // Check if Screen24Activity is opened
        // You can verify this by checking for any view or component of Screen24Activity
        onView(withId(R.id.lin1)).check(matches(isDisplayed()))
    }
}

